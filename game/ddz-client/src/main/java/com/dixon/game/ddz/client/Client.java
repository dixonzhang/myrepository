package com.dixon.game.ddz.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.websocket.ClientEndpoint;
import javax.websocket.EncodeException;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;

import com.dixon.game.ddz.common.bean.DeskInfoView;
import com.dixon.game.ddz.common.bean.DeskListView;
import com.dixon.game.ddz.common.enu.ChatType;
import com.dixon.game.ddz.common.enu.RespType;
import com.dixon.game.ddz.common.message.Message;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@ClientEndpoint  
public class Client {
	private Session session;
	private Date lastActiveTime;
	private boolean isServerActive = true;
	
	private boolean isTimeout(){
		return (System.currentTimeMillis() - lastActiveTime.getTime() > 30 * 1000);
	}
	
	@OnOpen
	public void onOpen(final Session session) {
		this.session = session;
		System.out
				.println("Connected to endpoint: " + session.getBasicRemote());
		lastActiveTime = new Date();
//		try {
//			session.getBasicRemote().sendText("Hello");
//		} catch (IOException ex) {
//		}
		
		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable() {
			
			public void run() {
				if(isServerActive == false){
					try {
						if(session != null && session.isOpen()){
							System.out.println("关闭session");
							session.close();
							System.exit(1);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else{
					System.out.println(isTimeout());
					if(isTimeout()){
						isServerActive = false;
						System.out.println("sending heartpear--------------");
						Message msg = new Message();
						msg.setChatType(ChatType.ping.toString());
						try {
							session.getAsyncRemote().sendText("{\"chatType\":\"ping\",\"playerId\":\"111111\"}");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, 0, 30, TimeUnit.SECONDS);
	}

	@SuppressWarnings("unchecked")
	@OnMessage
	public void onMessage(String message) {
		System.out.println(message);
		lastActiveTime = new Date();
		System.out.println(lastActiveTime);
		
		try{
			JSONObject json = JSONObject.fromObject(message);
			
			RespType respType = RespType.valueOf(json.getString("respType"));
			
			if(RespType.error == respType || RespType.notify == respType){
				System.out.println(json.getString("respDesc"));
			}
			else if(RespType.ping == respType){
				System.out.println("received ping message");
				byte b = 1;
				session.getBasicRemote().sendPong(ByteBuffer.allocate(1).put(b));
			}
			else if(RespType.deskList == respType){
				String deskListStr = json.getString("desks");
				
				List<DeskListView> viewList = (List<DeskListView>) JSONArray.toCollection(JSONArray.fromObject(deskListStr), DeskListView.class);
				System.out.println("桌列表信息");
				for(DeskListView view : viewList){
					System.out.println(view.getNum() + " (" + view.getPulyerCount() + ")");
				}
			}
			else if(RespType.deskFull == respType){
				System.out.println(message);
			}
			else if(RespType.deskInfo == respType){
				DeskInfoView deskInfo = (DeskInfoView)JSONObject.toBean(JSONObject.fromObject(json.getString("deskInfo")), DeskInfoView.class);
				System.out.println("桌号： " + deskInfo.getNum());
				System.out.println("当前序号： " + deskInfo.getCurrentIndex());
				System.out.println(deskInfo.getLastAction());
				System.out.println(deskInfo.getPlayerList());
			}
			else if(RespType.playFirst == respType){
				System.out.println(message);
			}
			else if(RespType.playFollow == respType){
				System.out.println(message);
			}
			else if(RespType.win == respType){
				System.out.println(message);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		
	}
	
	@OnMessage
	public void onPong(PongMessage pongMessage){
		System.out.println("isServerActive = true");
		isServerActive = true;
		lastActiveTime = new Date();
	}
	

	@OnError
	public void onError(Throwable t) {
		t.printStackTrace();
	}
	
}
