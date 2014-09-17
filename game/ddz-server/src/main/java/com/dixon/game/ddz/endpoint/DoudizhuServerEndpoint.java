package com.dixon.game.ddz.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.dixon.game.ddz.bean.Poker;
import com.dixon.game.ddz.decoders.MessageTextDecoder;
import com.dixon.game.ddz.encoders.BaseResEncoder;
import com.dixon.game.ddz.encoders.DeskInfoResEncoder;
import com.dixon.game.ddz.encoders.DeskListResEncoder;
import com.dixon.game.ddz.encoders.PlayResEncoder;
import com.dixon.game.ddz.enu.ChatType;
import com.dixon.game.ddz.enu.ColourType;
import com.dixon.game.ddz.enu.RespType;
import com.dixon.game.ddz.message.Message;
import com.dixon.game.ddz.resp.BaseRes;
import com.dixon.game.ddz.service.Allocator;

@ServerEndpoint(
	value = "/websocket/ddz",
	decoders = {MessageTextDecoder.class },
	encoders = {BaseResEncoder.class, DeskInfoResEncoder.class, DeskListResEncoder.class, PlayResEncoder.class}
)
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
    public void onMessage(Session session, Message message) {
    	try {
	    	if(ChatType.valueOf(message.getChatType()) == ChatType.login){
				allocator.login(session, message);
	    	}
	    	else{
	    		allocator.allocate(message);
	    	}
    	} catch (Exception e) {
    		try {
				session.getBasicRemote().sendObject(new BaseRes(RespType.error.toString(), "系统错误"));
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (EncodeException e1) {
				e1.printStackTrace();
			}
    	}
    }
    @OnClose
    public void onClose(Session peer) {
    	allocator.logout(peer);
    }
    
    public static void main(String[] args) {
//    	Message msgBean = new Message();
//    	msgBean.setPlayerId("111111");
//    	msgBean.setChatType("login");
//    	
//    	Map<String, String> data = new HashMap<String, String>(2);
//    	data.put("111", "111");
//    	data.put("222", "2");
//
//    	msgBean.setData(data);
//    	
//    	JSONObject json = JSONObject.fromObject(JSONObject.fromObject(msgBean).toString());
//    	
//    	Message msg = (Message)JSONObject.toBean(json, Message.class);
//    	
//    	if(ChatType.valueOf(msg.getChatType()) == ChatType.login){
//    		System.out.println("ddd");
//    	}
    	
    	
    	List<Poker> list = new ArrayList<Poker>();
    	
    	list.add(new Poker(ColourType.dawang, 0));
    	list.add(new Poker(ColourType.fangzhuan, 2));
    	list.add(new Poker(ColourType.heitao, 5));
    	
    	JSONArray json = JSONArray.fromObject(list);
    	System.out.println(json.toString());
    	
    	List<Poker> l = (List)JSONArray.toCollection(json, Poker.class);
    	
    	for(Poker p : l){
    		System.out.println(p.getNum());
    	}
	}
    
}
