package com.dixon.game.ddz.message;

public class Message {
	private String playerId;
	private String chatType;
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
}
