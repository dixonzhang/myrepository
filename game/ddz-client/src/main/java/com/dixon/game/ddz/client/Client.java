package com.dixon.game.ddz.client;

import java.io.IOException;
import java.util.List;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import com.dixon.game.ddz.common.bean.DeskInfoView;
import com.dixon.game.ddz.common.bean.DeskListView;
import com.dixon.game.ddz.common.enu.RespType;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@ClientEndpoint  
public class Client {
	@OnOpen
	public void onOpen(Session session) {
		System.out
				.println("Connected to endpoint: " + session.getBasicRemote());
//		try {
//			session.getBasicRemote().sendText("Hello");
//		} catch (IOException ex) {
//		}
	}

	@SuppressWarnings("unchecked")
	@OnMessage
	public void onMessage(String message) {
		System.out.println(message);
		
		try{
			JSONObject json = JSONObject.fromObject(message);
			
			RespType respType = RespType.valueOf(json.getString("respType"));
			
			if(RespType.error == respType || RespType.notify == respType){
				System.out.println(json.getString("respDesc"));
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
				System.out.println(deskInfo.getLastPokerList());
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

	@OnError
	public void onError(Throwable t) {
		t.printStackTrace();
	}
	
	/**
	 * 登录
	 */
	public void login(){
		
	}
	
	
	
}
