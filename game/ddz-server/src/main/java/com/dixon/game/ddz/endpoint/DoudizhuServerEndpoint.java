package com.dixon.game.ddz.endpoint;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONArray;

import com.dixon.game.ddz.common.bean.Poker;
import com.dixon.game.ddz.common.enu.ChatType;
import com.dixon.game.ddz.common.enu.ColourType;
import com.dixon.game.ddz.common.enu.RespType;
import com.dixon.game.ddz.common.message.Message;
import com.dixon.game.ddz.common.resp.BaseRes;
import com.dixon.game.ddz.decoders.MessageTextDecoder;
import com.dixon.game.ddz.encoders.BaseResEncoder;
import com.dixon.game.ddz.encoders.DeskInfoResEncoder;
import com.dixon.game.ddz.encoders.DeskListResEncoder;
import com.dixon.game.ddz.encoders.PlayResEncoder;
import com.dixon.game.ddz.service.Allocator;

@ServerEndpoint(
	value = "/websocket/ddz",
	encoders = {BaseResEncoder.class, DeskInfoResEncoder.class, DeskListResEncoder.class, PlayResEncoder.class},
	decoders = {MessageTextDecoder.class }
)
public class DoudizhuServerEndpoint {
	private Logger logger = Logger.getLogger(getClass().getName());
	
	static Allocator allocator;
	
	static{
		allocator = new Allocator();
	}
    
    @OnOpen
    public void onOpen(Session session) {
    	//登录成功返回桌信息
    	logger.info("onOpen " + session.getId());
    	try {
			session.getBasicRemote().sendObject(new BaseRes(RespType.notify.toString(), "连接成功！"));;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (EncodeException e) {
			e.printStackTrace();
		}
    }
    
//    @OnMessage
//    public void onMessage(Session session, String message) {
//    	logger.info("text:" + message);
//    }
    
    @OnMessage
    public void onMessage(Session session, Message message) {
    	try {
    		logger.info(message.getChatType());
	    	if(ChatType.valueOf(message.getChatType()) == ChatType.login){
				allocator.login(session, message);
	    	}
	    	else if(ChatType.valueOf(message.getChatType()) == ChatType.ping){
	    		byte b = 1;
	    		session.getAsyncRemote().sendPong(ByteBuffer.allocate(1).put(b));
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
    
    /**
     * 服务端对所有客户端发送ping消息，这里处理客户端的pong消息
     * @param session
     * @param pong
     */
    @OnMessage
    public void onPong(Session session, PongMessage pong){
    	logger.info("pong " + session + " " + pong.getApplicationData().toString());
    	allocator.onPong(session);
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
//    		logger.info("ddd");
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
