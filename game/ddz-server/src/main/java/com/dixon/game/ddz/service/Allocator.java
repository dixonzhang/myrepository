package com.dixon.game.ddz.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.websocket.Session;

public class Allocator {
	//用户与session映射
	private static ConcurrentMap<String, Session> allSessionMap = new ConcurrentHashMap<String, Session>(100);
	//用户与所在桌映射
	private static ConcurrentMap<String, Integer> userDeskMap = new ConcurrentHashMap<String, Integer>(100);
	
	
	public void allocate(){
		
	}
}
