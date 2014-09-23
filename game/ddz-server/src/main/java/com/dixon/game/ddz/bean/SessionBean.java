package com.dixon.game.ddz.bean;

import java.util.Date;

import javax.websocket.Session;

public class SessionBean {
	private String playerId;
	private Date lastActiveTime;
	private Session session;
	
	public SessionBean(){
		lastActiveTime = new Date();
	}
	
	public void updateLastActiveTime(){
		lastActiveTime = new Date();
	}
	
	public boolean isTimeout(){
		return false;
	}
	
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public Date getLastActiveTime() {
		return lastActiveTime;
	}
	public void setLastActiveTime(Date lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	
	@Override
	public String toString() {
		return playerId + " " + session.getId() + " " + lastActiveTime;
	}
}
