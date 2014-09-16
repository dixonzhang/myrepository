package com.dixon.game.ddz.endpoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONObject;

import com.dixon.game.ddz.bean.Message;
import com.dixon.game.ddz.enu.ChatType;
import com.dixon.game.ddz.enu.RespType;
import com.dixon.game.ddz.resp.BaseRes;
import com.dixon.game.ddz.service.Allocator;

@ServerEndpoint(value="/websocket/ddz")
public class DoudizhuServerEndpoint {
	static Allocator allocator;
	
	static{
		allocator = new Allocator();
	}
    
    @OnOpen
    public void onOpen(Session session) {
    	//登录成功返回桌信息
    	System.out.println("onOpen " + session.getId());
    	try {
			session.getBasicRemote().sendText("u logined");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    @OnMessage
    public void onMessage(Session session, String message) {
    	try {
	    	System.out.println("onMessage　" + session.getId() + ", " + message);
	    	
	    	JSONObject json = JSONObject.fromObject(message);
	    	
	    	Message msg = (Message)JSONObject.toBean(json, Message.class);
	    	
	    	if(ChatType.valueOf(msg.getChatType()) == ChatType.login){
					allocator.login(session, msg);
	    	}
	    	else{
	    		allocator.allocate(msg);
	    	}
    	} catch (IOException e) {
    		try {
    			String resp = JSONObject.fromObject(new BaseRes(RespType.error.toString(), "系统错误")).toString();
				session.getBasicRemote().sendText(resp);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    	}
    }
    @OnClose
    public void onClose(Session peer) {
    	allocator.logout(peer);
    }
    
    public static void main(String[] args) {
    	Message msgBean = new Message();
    	msgBean.setPlayerId("111111");
    	msgBean.setChatType("login");
    	
    	Map<String, String> data = new HashMap<String, String>(2);
    	data.put("111", "111");
    	data.put("222", "2");

    	msgBean.setData(data);
    	
    	JSONObject json = JSONObject.fromObject(JSONObject.fromObject(msgBean).toString());
    	
    	Message msg = (Message)JSONObject.toBean(json, Message.class);
    	
    	if(ChatType.valueOf(msg.getChatType()) == ChatType.login){
    		System.out.println("ddd");
    	}
	}
    
}
