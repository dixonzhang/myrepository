package com.dixon.game.ddz.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.websocket.EncodeException;
import javax.websocket.Session;

import net.sf.json.JSONArray;

import com.dixon.game.ddz.bean.Desk;
import com.dixon.game.ddz.bean.Player;
import com.dixon.game.ddz.bean.Poker;
import com.dixon.game.ddz.enu.ChatType;
import com.dixon.game.ddz.enu.RespType;
import com.dixon.game.ddz.message.GrabMessage;
import com.dixon.game.ddz.message.JoinMessage;
import com.dixon.game.ddz.message.Message;
import com.dixon.game.ddz.message.PlayMessage;
import com.dixon.game.ddz.resp.BaseRes;
import com.dixon.game.ddz.resp.DeskInfoRes;
import com.dixon.game.ddz.resp.DeskListRes;
import com.dixon.game.ddz.resp.PlayRes;
import com.dixon.game.ddz.utils.DizhuShuffler;

public class Allocator {
	//用户与session映射
	private static ConcurrentMap<String, Session> allSessionMap = new ConcurrentHashMap<String, Session>(100);
	//用户与所在桌映射
	private static ConcurrentMap<String, Desk> userDeskMap = new ConcurrentHashMap<String, Desk>(100);
	//桌id与桌的映射
	private static ConcurrentMap<Integer, Desk> deskMap = new ConcurrentHashMap<Integer, Desk>(10);	
	
	public Allocator(){
		for(int i = 0; i < 10; i++){
			Desk desk = new Desk();
			desk.setDeskNum(i+1);
			
			deskMap.put(i, desk);
		}
	}
	
	public void allocate(Message msg) throws IOException, EncodeException{
		if(ChatType.valueOf(msg.getChatType()) == ChatType.join)
			join((JoinMessage)msg);
		if(ChatType.valueOf(msg.getChatType()) == ChatType.ready)
			ready(msg);
		if(ChatType.valueOf(msg.getChatType()) == ChatType.play)
			play((PlayMessage)msg);
		if(ChatType.valueOf(msg.getChatType()) == ChatType.logout)
			logout(msg);
		if(ChatType.valueOf(msg.getChatType()) == ChatType.grab)
			grab((GrabMessage)msg);
	}
	
	
	public void login(Session session, Message msg) throws IOException, EncodeException{
		if(allSessionMap.get(msg.getPlayerId()) == null && null != msg.getPlayerId() && !"".equals(msg.getPlayerId())){
			allSessionMap.put(msg.getPlayerId(), session);
			
			// 返回桌信息
			List<Desk> list = new ArrayList<Desk>();
			list.addAll(deskMap.values());
			
			Collections.sort(list);//排序
			
			DeskListRes res = new DeskListRes(RespType.deskList.toString(), "获取桌列表成功");
			res.setDesks(list);
			
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
			Vector<Player> playerList = desk.getPlayerList();
			if(playerList == null)
				playerList = new Vector<Player>();
			
			if(playerList.size() < 3){
				
				Player p = new Player();
				p.setPlayerId(msg.getPlayerId());
				
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
					p.setReady(true);
					
					//看是否全部都已准备就绪，是的话就可以发牌了
					if(playerList.size() == 3){
						int readyCount = 0;
						for(Player p2 : playerList){
							if(p2.isReady())
								readyCount++;
						}
						
						if(3 == readyCount){
							// 发牌
							deal(desk);
						}
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
			
			DeskInfoRes res = new DeskInfoRes(RespType.deskInfo.toString(), "获取桌信息表成功");
			res.setDesk(desk);
			
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
		
		desk.currentIndexAdd();
		desk.grabTimesAdd();
		
		if(msg.isGrab()){//当前用户抢地主
			for(int i = 0; i < desk.getPlayerList().size(); i++){
				if(playerId.equals(desk.getPlayerList().get(i).getPlayerId())){
					desk.setDizhuIndex(i);
					break;
				}
			}
		}
		
		notifyDeskInfoToAll(desk, desk.getPlayerList());
		
		//抢完地主
		if(4 == desk.getGrabTimes()){
			// 通知地主出牌
			Player dizhu = desk.getPlayerList().get(desk.getDizhuIndex());
			
			PlayRes res = new PlayRes(RespType.playFirst.toString(), "请出牌");
			
			allSessionMap.get(dizhu.getPlayerId()).getBasicRemote().sendObject(res);
		}
		
	}
	
	/**
	 * 
	 * @param msg
	 * @throws IOException 
	 * @throws EncodeException 
	 */
	@SuppressWarnings("unchecked")
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
				
				allSessionMap.get(nextPlayer.getPlayerId()).getBasicRemote().sendObject(res);
			}
			else{
				//下一家跟牌
				PlayRes res = new PlayRes(RespType.playFollow.toString(), "请跟牌");
				
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
					res.setDesk(desk);
					
					allSessionMap.get(p2.getPlayerId()).getBasicRemote().sendObject(res);
				}
			}
			else{
				int nextIndex = desk.currentIndexAdd();
				Player nextPlayer = desk.getPlayerList().get(nextIndex);
				
				PlayRes res = new PlayRes(RespType.playFollow.toString(), "请跟牌");
				
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
		}
		
		//客户端判断不为-1，则由该序号的玩家开始叫地主 
		desk.setDizhuIndex(new Random().nextInt(3));
	}
}
