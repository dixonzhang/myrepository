package com.dixon.game.ddz.endpoint;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONObject;

import com.dixon.game.ddz.bean.Message;
import com.dixon.game.ddz.enu.ChatType;
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
    	System.out.println("onMessage　" + session.getId() + ", " + message);
    	
    	JSONObject json = JSONObject.fromObject(message);
    	
    	Message msg = (Message)JSONObject.toBean(json, Message.class);
    	
    	if(ChatType.valueOf(msg.getChatType()) == ChatType.login){
    		try {
				allocator.login(session, msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	else{
    		allocator.allocate(msg);
    	}
    }
    @OnClose
    public void onClose(Session peer) {
    	System.out.println("onClose");
    }
    
    public static void main(String[] args) {
    	Message msgBean = new Message();
    	msgBean.setUserId("111111");
    	msgBean.setChatType("login");
    	
    	
    	
    	JSONObject json = JSONObject.fromObject(JSONObject.fromObject(msgBean).toString());
    	
    	Message msg = (Message)JSONObject.toBean(json, Message.class);
    	
    	if(ChatType.valueOf(msg.getChatType()) == ChatType.login){
    		System.out.println("ddd");
    	}
	}
    
}
