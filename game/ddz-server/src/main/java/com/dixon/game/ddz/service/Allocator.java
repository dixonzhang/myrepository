package com.dixon.game.ddz.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import java.util.logging.Logger;

import javax.websocket.EncodeException;
import javax.websocket.Session;

import com.dixon.game.ddz.bean.Desk;
import com.dixon.game.ddz.bean.SessionBean;
import com.dixon.game.ddz.common.bean.DeskInfoView;
import com.dixon.game.ddz.common.bean.DeskListView;
import com.dixon.game.ddz.common.bean.LastAction;
import com.dixon.game.ddz.common.bean.Player;
import com.dixon.game.ddz.common.bean.Poker;
import com.dixon.game.ddz.common.enu.ActionType;
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
import com.dixon.game.ddz.utils.DizhuShuffler;

public class Allocator {
	private Logger logger = Logger.getLogger(getClass().getName());
	
	//用户与session映射
	private static ConcurrentMap<String, SessionBean> allSessionMap = new ConcurrentHashMap<String, SessionBean>(100);
	//用户与所在桌映射
	private static ConcurrentMap<String, Desk> userDeskMap = new ConcurrentHashMap<String, Desk>(100);
	//桌id与桌的映射
	private static ConcurrentMap<Integer, Desk> deskMap = new ConcurrentHashMap<Integer, Desk>(10);
	
	private List<SessionBean> heartBearSessionList = new ArrayList<SessionBean>();
	private long timeoutTimeMillis = 60 * 1000;
	
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
				try {
					logger.info("removing heartBearSessionList size is " + heartBearSessionList.size());
					//移除上一次发送ping没响应的客户端
					if(heartBearSessionList != null && !heartBearSessionList.isEmpty()){
						for(SessionBean sb : heartBearSessionList){
							try {
								sb.getSession().close();
								logout(sb.getSession());
								logger.info("removed " + sb);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					
					logger.info("all session client size is " + allSessionMap.size());
					//向所有客户端发送ping
					heartBearSessionList = new ArrayList<SessionBean>(allSessionMap.size()/2);
					for(Iterator<SessionBean> it = allSessionMap.values().iterator(); it.hasNext(); ){
						SessionBean sb = it.next();
						if((System.currentTimeMillis() - sb.getLastActiveTime().getTime()) > timeoutTimeMillis){
							heartBearSessionList.add(sb);
						}
					}
					logger.info("need send heartbear size is " + heartBearSessionList.size());
					for(SessionBean sb : heartBearSessionList){
						try {
							logger.info("sending heartbear " + sb);
							sb.getSession().getAsyncRemote().sendObject(new BaseRes(RespType.ping.toString(), ""));
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, 30, TimeUnit.SECONDS);
	}
	
	/**
	 * 处理客户端的pong响应
	 * @param session
	 */
	public void onPong(Session session){
		for(Iterator<SessionBean> it = heartBearSessionList.iterator(); it.hasNext(); ){
			SessionBean sb = it.next();
			if(sb.getSession().getId().equals(session.getId())){
				logger.info("update heartbear " + sb);
				sb.setLastActiveTime(new Date());
				it.remove();
			}
		}
	}
	
	public void allocate(Message msg) throws IOException, EncodeException{
		//更新session最后活动的时间
		SessionBean sb = allSessionMap.get(msg.getPlayerId());
		sb.setLastActiveTime(new Date());
		
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
			SessionBean sb = new SessionBean();
			sb.setPlayerId(msg.getPlayerId());
			sb.setLastActiveTime(new Date());
			sb.setSession(session);
			
			allSessionMap.put(msg.getPlayerId(), sb);
			
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
		for(Iterator<Entry<String, SessionBean>> it = allSessionMap.entrySet().iterator(); it.hasNext(); ){
			Entry<String, SessionBean> entry = it.next();
			if(entry.getValue().getSession() == session){
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
				
				playerList.add(p);
				
				// 同一桌的客户端亦返回信息
				notifyDeskInfoToAll(desk, playerList, null, null);
			}
			else{
				// 已满，告知客户端
				allSessionMap.get(msg.getPlayerId()).getSession().getBasicRemote().sendObject(new BaseRes(RespType.deskFull.toString(), "此桌已满人，请重新选桌"));
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
					
					break;
				}
			}
			//看是否全部都已准备就绪，是的话就可以发牌了
			if(playerList.size() == 3){
				int allReadyCount = 0;
				for(Player p2 : playerList){
					if(p2.isReady())
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
			notifyDeskInfoToAll(desk, playerList, null, null);
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
		
		LastAction lastAction = new LastAction();
		lastAction.setActionType(ActionType.noGrab);
		
		for(int i = 0; i < desk.getPlayerList().size(); i++){
			if(playerId.equals(desk.getPlayerList().get(i).getPlayerId())){
				if(msg.isGrab()){
					desk.setDizhuIndex(i);
					lastAction.setActionType(ActionType.grab);
				}
			}
		}
		
		desk.grabTimesAdd();
		
		PlayerType playerType = null;
		
		//抢完地主
		if(4 == desk.getGrabTimes()){
			desk.setDeskType(DeskType.play);
			playerType = PlayerType.first;
			//将地主牌
			desk.getPlayerList().get(desk.getDizhuIndex()).getPokerList().addAll(desk.getDealPokerMap().get(3));
		}
		else{
			desk.setDeskType(DeskType.grab);
			desk.currentIndexAdd();
		}
		notifyDeskInfoToAll(desk, desk.getPlayerList(), lastAction, playerType);
		
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
		
		LastAction lastAction = new LastAction();
		lastAction.setPokers(pokerList);
		
		desk.currentIndexAdd();
		
		PlayerType playerType = null;
		
		//不出牌，过
		if(null == pokerList || pokerList.isEmpty()){
			//过
			desk.passTimesAdd();
			
			lastAction.setActionType(ActionType.pass);
			
			//过了两次， 重新出牌
			if(2 == desk.getPassTimes()){
				playerType = PlayerType.first;
			}
			else{
				//下一家跟牌
				playerType = PlayerType.follow;
			}
		}
		else{
			//重置过的次数
			desk.passTimesReset();
			lastAction.setActionType(ActionType.follow);
			
			//下一家跟牌
			playerType = PlayerType.follow;
			
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
					
					allSessionMap.get(p2.getPlayerId()).getSession().getBasicRemote().sendObject(res);
				}
			}
			else{
				
			}
		}
		
		notifyDeskInfoToAll(desk, desk.getPlayerList(), lastAction, playerType);
	}

	private void notifyDeskInfoToAll(Desk desk, List<Player> playerList, LastAction lastAction, PlayerType playerType)
			throws IOException, EncodeException {
		
		DeskInfoView deskView = convert2DeskInfo(desk);
		deskView.setLastAction(lastAction);
		if(desk.getDealPokerMap() != null)
			deskView.setDizhuPoker(desk.getDealPokerMap().get(3));
		deskView.setPlayerType(playerType);
		
		for(Player p2 : playerList){
			DeskInfoRes res = new DeskInfoRes(RespType.deskInfo.toString(), "获取桌信息成功");
			res.setDeskInfo(deskView);
			
			allSessionMap.get(p2.getPlayerId()).getSession().getBasicRemote().sendObject(res);
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
		
		//随机一个序号，开始抢地主
		desk.setCurrentIndex(new Random().nextInt(3));
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
		deskInfo.setDeskType(desk.getDeskType().toString());
		deskInfo.setCurrentIndex(desk.getCurrentIndex());
		deskInfo.setPlayerList(desk.getPlayerList());
		
		return deskInfo;
	}
}
