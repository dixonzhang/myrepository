package com.dixon.game.ddz.client;


import net.sf.json.JSONObject;

import com.dixon.game.ddz.common.enu.ChatType;
import com.dixon.game.ddz.common.message.JoinMessage;
import com.dixon.game.ddz.common.message.Message;

public class JsonCreater {
	public static void main(String[] args) {
		craeteLoing();
		
		craeteJoin();
	}
	
	
	//{"chatType":"login","playerId":"111111"}
	private static void craeteLoing(){
		Message loginMsg = new Message();
		loginMsg.setChatType(ChatType.login.toString());
		loginMsg.setPlayerId("111111");
		
		System.out.println(JSONObject.fromObject(loginMsg).toString());
	}
	
	//{"chatType":"join","deskId":1,"playerId":"111111"}
	private static void craeteJoin(){
		JoinMessage msg = new JoinMessage();
		msg.setChatType(ChatType.join.toString());
		msg.setPlayerId("111111");
		msg.setDeskId(1);
		
		System.out.println(JSONObject.fromObject(msg).toString());
	}
}
