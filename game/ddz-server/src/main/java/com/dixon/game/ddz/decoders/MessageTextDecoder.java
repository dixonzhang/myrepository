package com.dixon.game.ddz.decoders;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.dixon.game.ddz.common.bean.Poker;
import com.dixon.game.ddz.common.enu.ChatType;
import com.dixon.game.ddz.common.enu.ColourType;
import com.dixon.game.ddz.common.message.GrabMessage;
import com.dixon.game.ddz.common.message.JoinMessage;
import com.dixon.game.ddz.common.message.Message;
import com.dixon.game.ddz.common.message.PlayMessage;

public class MessageTextDecoder implements Decoder.Text<Message>{
	Logger logger = Logger.getLogger(getClass().getName());
	
	private JSONObject json;
	@Override
	public void init(EndpointConfig config) {
	}

	@Override
	public void destroy() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Message decode(String s) throws DecodeException {
		Message msg = null;
//		if(willDecode(s)){
			logger.info(json.toString());
			ChatType chatType = ChatType.valueOf(json.getString("chatType"));
			
			if(chatType == ChatType.join){
				msg = (JoinMessage)JSONObject.toBean(json, JoinMessage.class);
			}
			else if(chatType == ChatType.grab){
				msg = (GrabMessage)JSONObject.toBean(json, GrabMessage.class);
			}
			else if(chatType == ChatType.play){
				PlayMessage pmsg = (PlayMessage)JSONObject.toBean(json, PlayMessage.class);
				List<Poker> list = (List<Poker>) JSONArray.toCollection(JSONArray.fromObject(json.get("pokerList")), Poker.class);
				pmsg.setPokerList(list);
				
				msg = pmsg;
			}
			else{
				msg = (Message)JSONObject.toBean(json, Message.class);
			}
//		}
//		else 
//			throw new DecodeException(s, "[Message] Can't decode.");
		
		return msg;
	}

	@Override
	public boolean willDecode(String s) {
		try{
			json = JSONObject.fromObject(s);
			if(json != null){
				String chatType = (String)json.get("chatType");
					ChatType.valueOf(chatType);
					return true;
		}
		}catch (Exception e){
		}
		
		return false;
	}

	public static void main(String[] args) {
		PlayMessage pm = new PlayMessage();
		
		List<Poker> pList = new ArrayList<Poker>(2);
		pList.add(new Poker(ColourType.heitao, 4));
		pList.add(new Poker(ColourType.heitao, 5));
		
		pm.setChatType(ChatType.play.toString());
		pm.setPlayerId("1111");
		pm.setPokerList(pList);
		
		JSONObject json = JSONObject.fromObject(pm);
		
		System.out.println(json);
		
		
		PlayMessage pmsg = (PlayMessage)JSONObject.toBean(json, PlayMessage.class);
		
		System.out.println(pmsg.getChatType());
		System.out.println(pmsg.getPlayerId());
		System.out.println(pmsg.getPokerList());
		
		System.out.println(json.get("pokerList"));
		
		List<Poker> list = (List<Poker>) JSONArray.toCollection(JSONArray.fromObject(json.get("pokerList")), Poker.class);
		pmsg.setPokerList(list);
		
		for(Poker p : pmsg.getPokerList()){
			System.out.println(p.getName());
		}
		
		
		
	}
}
