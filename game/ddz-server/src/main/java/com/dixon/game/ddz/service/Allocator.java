package com.dixon.game.ddz.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.websocket.EncodeException;
import javax.websocket.Session;

import com.dixon.game.ddz.bean.Desk;
import com.dixon.game.ddz.common.bean.DeskInfoView;
import com.dixon.game.ddz.common.bean.DeskListView;
import com.dixon.game.ddz.common.bean.Player;
import com.dixon.game.ddz.common.bean.Poker;
import com.dixon.game.ddz.common.enu.ChatType;
import com.dixon.game.ddz.common.enu.DeskType;
import com.dixon.game.ddz.common.enu.PlayerType;
import com.dixon.game.ddz.common.enu.RespType;
import com.dixon.game.ddz.common.message.GrabMessage;
import com.dixon.game.ddz.common.message.JoinMessage;
import com.dixon.game.ddz.common.message.Message;
import com.dixon.game.ddz.common.message.PlayMessage;
import com.dixon.game.ddz.common.resp.BaseRes;
import com.dixon.game.ddz.common.resp.DeskInfoRes;
import com.dixon.game.ddz.common.resp.DeskListRes;
import com.dixon.game.ddz.common.resp.PlayRes;
import com.dixon.game.ddz.utils.DizhuShuffler;

public class Allocator {
	//用户与session映射
	private static ConcurrentMap<String, Session> allSessionMap = new ConcurrentHashMap<String, Session>(100);
	//用户与所在桌映射
	private static ConcurrentMap<String, Desk> userDeskMap = new ConcurrentHashMap<String, Desk>(100);
	//桌id与桌的映射
	private static ConcurrentMap<Integer, Desk> deskMap = new ConcurrentHashMap<Integer, Desk>(10);	
	
	public Allocator(){
		for(int i = 1; i <= 10; i++){
			Desk desk = new Desk();
			desk.setDeskNum(i);
			
			deskMap.put(i, desk);
		}
		
		//心跳， 30秒一次
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				//向所有客户端发送ping
				for(Iterator<Session> it = allSessionMap.values().iterator(); it.hasNext(); ){
					ByteBuffer bb = ByteBuffer.allocate(0);
					try {
						it.next().getBasicRemote().sendPing(bb);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}, 30, 30, TimeUnit.SECONDS);
	}
	
	public void allocate(Message msg) throws IOException, EncodeException{
		if(ChatType.valueOf(msg.getChatType()) == ChatType.join)
			join((JoinMessage)msg);
		if(ChatType.valueOf(msg.getChatType()) == ChatType.leave)
//			join((JoinMessage)msg);
		if(ChatType.valueOf(msg.getChatType()) == ChatType.ready)
			ready(msg);
		if(ChatType.valueOf(msg.getChatType()) == ChatType.grab)
			grab((GrabMessage)msg);
		if(ChatType.valueOf(msg.getChatType()) == ChatType.play)
			play((PlayMessage)msg);
		if(ChatType.valueOf(msg.getChatType()) == ChatType.logout)
			logout(msg);
	}
	
	
	public void login(Session session, Message msg) throws IOException, EncodeException{
		if(allSessionMap.get(msg.getPlayerId()) == null && null != msg.getPlayerId() && !"".equals(msg.getPlayerId())){
			allSessionMap.put(msg.getPlayerId(), session);
			
			// 返回桌信息
			List<Desk> list = new ArrayList<Desk>();
			list.addAll(deskMap.values());
			
			Collections.sort(list);//排序
			
			DeskListRes res = new DeskListRes(RespType.deskList.toString(), "获取桌列表成功");
			res.setDesks(convert2DeskList(list));
			
			session.getBasicRemote().sendObject(res);
		}
		else{
			session.getBasicRemote().sendObject(new BaseRes(RespType.notify.toString(), "登录失败，参数不全"));;
		}
	}
	
	public void logout(Session session){
		for(Iterator<Entry<String, Session>> it = allSessionMap.entrySet().iterator(); it.hasNext(); ){
			Entry<String, Session> entry = it.next();
			if(entry.getValue() == session){
				String userId = entry.getKey();
				
				remove(userId);
			}
		}
	}
	
	public void logout(Message msg){
		remove(msg.getPlayerId());
	}
	
	/**
	 * 选桌
	 * @param msg
	 * @throws IOException 
	 * @throws EncodeException 
	 */
	public void join(JoinMessage msg) throws IOException, EncodeException{
		
		if(null != msg.getDeskId()){
			Desk desk = deskMap.get(msg.getDeskId());
			desk.setDeskType(DeskType.ready);
			Vector<Player> playerList = desk.getPlayerList();
			if(playerList == null){
				playerList = new Vector<Player>(3);
				desk.setPlayerList(playerList);
			}
			
			if(playerList.size() < 3){
				
				Player p = new Player();
				p.setPlayerId(msg.getPlayerId());
				p.setPlayerType(PlayerType.reading);
				
				playerList.add(p);
				
				// 同一桌的客户端亦返回信息
				notifyDeskInfoToAll(desk, playerList);
			}
			else{
				// 已满，告知客户端
				allSessionMap.get(msg.getPlayerId()).getBasicRemote().sendObject(new BaseRes(RespType.deskFull.toString(), "此桌已满人，请重新选桌"));
			}
		}
		
	}
	
	/**
	 * 准备
	 * @param msg
	 * @throws IOException 
	 * @throws EncodeException 
	 */
	public void ready(Message msg) throws IOException, EncodeException{
		Desk desk = userDeskMap.get(msg.getPlayerId());
		
		if(null != desk){
			List<Player> playerList = desk.getPlayerList();
			for(Player p : playerList){
				if(p.getPlayerId().equals(msg.getPlayerId())){
					p.setPlayerType(PlayerType.readied);
					
					//看是否全部都已准备就绪，是的话就可以发牌了
					if(playerList.size() == 3){
						int allReadyCount = 0;
						for(Player p2 : playerList){
							if(PlayerType.readied == p2.getPlayerType())
								allReadyCount++;
						}
						
						if(3 == allReadyCount){
							// 发牌
							desk.setDeskType(DeskType.deal);
							deal(desk);
						}
						else
							desk.setDeskType(DeskType.ready);
					}
					
					// 通知同桌用户
					notifyDeskInfoToAll(desk, playerList);
					
					break;
				}
			}
		}
	}

	private void notifyDeskInfoToAll(Desk desk, List<Player> playerList)
			throws IOException, EncodeException {
		for(Player p2 : playerList){
			
			DeskInfoRes res = new DeskInfoRes(RespType.deskInfo.toString(), "获取桌信息成功");
			res.setDeskInfo(convert2DeskInfo(desk));
			
			allSessionMap.get(p2.getPlayerId()).getBasicRemote().sendObject(res);;
		}
	}
	
	/**
	 * 抢地主
	 * @param msg
	 * @throws IOException 
	 * @throws EncodeException 
	 */
	public void grab(GrabMessage msg) throws IOException, EncodeException{
		String playerId = msg.getPlayerId();
		
		Desk desk = userDeskMap.get(playerId);
		
		int nextIndex = desk.currentIndexAdd();
		desk.grabTimesAdd();
		
		for(int i = 0; i < desk.getPlayerList().size(); i++){
			if(playerId.equals(desk.getPlayerList().get(i).getPlayerId())){
				if(msg.isGrab()){
					desk.setDizhuIndex(i);
				}
			}
			desk.getPlayerList().get(i).setPlayerType(PlayerType.waiting);
		}
		desk.getPlayerList().get(nextIndex).setPlayerType(PlayerType.grab);
		
		
		//抢完地主
		if(4 == desk.getGrabTimes()){
			for(int i = 0; i < desk.getPlayerList().size(); i++){
				desk.getPlayerList().get(i).setPlayerType(PlayerType.waiting);
			}
			
			// 通知地主出牌
			Player dizhu = desk.getPlayerList().get(desk.getDizhuIndex());
			dizhu.setPlayerType(PlayerType.first);
			desk.setCurrentIndex(index)
		}
		notifyDeskInfoToAll(desk, desk.getPlayerList());
		
	}
	
	/**
	 * 
	 * @param msg
	 * @throws IOException 
	 * @throws EncodeException 
	 */
	public void play(PlayMessage msg) throws IOException, EncodeException{
		
		List<Poker> pokerList = msg.getPokerList();
		
		Desk desk = userDeskMap.get(msg.getPlayerId());
		//不出牌，过
		if(null == pokerList || pokerList.isEmpty()){
			//过
			desk.passTimesAdd();
			int nextIndex = desk.currentIndexAdd();
			Player nextPlayer = desk.getPlayerList().get(nextIndex);
			
			//过了两次， 让下一家重新出牌
			if(2 == desk.getPassTimes()){
				PlayRes res = new PlayRes(RespType.playFirst.toString(), "请出牌");
				res.setLastPlayerFollow(false);
				
				allSessionMap.get(nextPlayer.getPlayerId()).getBasicRemote().sendObject(res);
			}
			else{
				//下一家跟牌
				PlayRes res = new PlayRes(RespType.playFollow.toString(), "请跟牌");
				res.setLastPlayerFollow(false);
				
				allSessionMap.get(nextPlayer.getPlayerId()).getBasicRemote().sendObject(res);
			}
		}
		else{
			//重置过的次数
			desk.passTimesReset();
			
			Vector<Poker> followPokerList = new Vector<Poker>(pokerList);
			//当前出的牌
			desk.setCurrentPokers(followPokerList);
			
			//移除当前玩家出了的牌
			Player currPlayer = desk.getPlayerList().get(desk.getCurrentIndex());
			currPlayer.getPokerList().removeAll(followPokerList);
			
			//如果当前玩家牌出完了，就赢了
			if(currPlayer.getPokerList().isEmpty()){
				for(Player p2 : desk.getPlayerList()){
					DeskInfoRes res = new DeskInfoRes(RespType.win.toString(), "获胜");
					res.setDeskInfo(convert2DeskInfo(desk));
					
					allSessionMap.get(p2.getPlayerId()).getBasicRemote().sendObject(res);
				}
			}
			else{
				int nextIndex = desk.currentIndexAdd();
				Player nextPlayer = desk.getPlayerList().get(nextIndex);
				
				PlayRes res = new PlayRes(RespType.playFollow.toString(), "请跟牌");
				res.setLastPlayerFollow(true);
				
				allSessionMap.get(nextPlayer.getPlayerId()).getBasicRemote().sendObject(res);
			}
		}
	}

	private void remove(String playerId){
		//移除session
		allSessionMap.remove(playerId);
		
		Desk desk = userDeskMap.get(playerId);
		
		if(desk != null){
			//移除该桌用户
			List<Player> playerList = desk.getPlayerList();
			if(playerList != null && !playerList.isEmpty()){
				for(Iterator<Player> it = playerList.iterator(); it.hasNext(); ){
					Player p = it.next();
					if(playerId.equals(p.getPlayerId())){
						it.remove();
						break;
					}
				}
			}
		}
	}

	/**
	 * 发牌
	 * @param desk
	 */
	private void deal(Desk desk){
		desk.setDealPokerMap(DizhuShuffler.deal());
		
		for(int i = 0; i < desk.getPlayerList().size(); i++){
			Player player = desk.getPlayerList().get(i);
			
			player.setPokerList(desk.getDealPokerMap().get(i));
			player.setPlayerType(PlayerType.waiting);
		}
		
		//随机一个序号，开始抢地主
		int ci = desk.setCurrentIndex(new Random().nextInt(3));
		desk.getPlayerList().get(ci).setPlayerType(PlayerType.call);
	}
	
	
	private List<DeskListView> convert2DeskList(List<Desk> list){
		
		List<DeskListView> viewList = new ArrayList<DeskListView>(list.size());
		for(Desk desk : list){
			DeskListView view = new DeskListView();
			view.setNum(desk.getDeskNum());
			view.setPulyerCount(desk.getPlayerList() == null ? 0 : desk.getPlayerList().size());
			
			viewList.add(view);
		}
		
		return viewList;
	}
	
	private DeskInfoView convert2DeskInfo(Desk desk){
		DeskInfoView deskInfo = new DeskInfoView();
		deskInfo.setNum(desk.getDeskNum());
		deskInfo.setDeskType(desk.getDeskType());
		deskInfo.setCurrentIndex(desk.getCurrentIndex());
		deskInfo.setLastPokerList(desk.getCurrentPokers());
		deskInfo.setPlayerList(desk.getPlayerList());
		
		return deskInfo;
	}
}
