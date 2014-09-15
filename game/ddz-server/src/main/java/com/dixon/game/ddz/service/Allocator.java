package com.dixon.game.ddz.service;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.websocket.Session;

import com.dixon.game.ddz.bean.Message;

public class Allocator {
	//用户与session映射
	private static ConcurrentMap<String, Session> allSessionMap = new ConcurrentHashMap<String, Session>(100);
	//用户与所在桌映射
	private static ConcurrentMap<String, Integer> userDeskMap = new ConcurrentHashMap<String, Integer>(100);
	//桌有几个用户， value=userIds
	private static ConcurrentMap<Integer, String[]> deskUsersMap = new ConcurrentHashMap<Integer, String[]>(100);
	
	public void allocate(Message msg){
		
	
	}
	
	
	public void login(Session session, Message msg) throws IOException{
		if(allSessionMap.get(msg.getUserId()) == null && null != msg.getUserId() && !"".equals(msg.getUserId())){
			allSessionMap.put(msg.getUserId(), session);
			
			//返回桌信息
			
			
			
			session.getBasicRemote().sendText("登录成功");
		}
		else{
			session.getBasicRemote().sendText("登录失败");
		}
	}
}
