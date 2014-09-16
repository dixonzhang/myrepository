package com.dixon.game.ddz.bean;

import java.util.Map;

public class Message {
	private String playerId;
	private String chatType;
	private Map<String, String> data;
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String userId) {
		this.playerId = userId;
	}
	public String getChatType() {
		return chatType;
	}
	public void setChatType(String chatType) {
		this.chatType = chatType;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
	
}
