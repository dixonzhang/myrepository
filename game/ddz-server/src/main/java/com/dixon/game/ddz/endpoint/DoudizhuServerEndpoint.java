package com.dixon.game.ddz.endpoint;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value="/websocket/ddz")
public class DoudizhuServerEndpoint {
    
    @OnOpen
    public void onOpen(Session session) {
    	System.out.println("onOpen " + session.getId());
    	try {
			session.getBasicRemote().sendText("u logined");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    @OnMessage
    public void onMessage(Session session, String message) {
    	System.out.println("onMessage　" + session.getId() + ", " + message);
    }
    @OnClose
    public void onClose(Session peer) {
    	System.out.println("onClose");
    } 
}
