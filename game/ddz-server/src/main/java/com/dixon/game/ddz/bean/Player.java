package com.dixon.game.ddz.bean;

import java.util.List;

public class Player {
	private String playerId;
	private boolean dizhu;
	//是否准备就绪
	private boolean ready;
	//用户剩下的牌
	private List<Poker> pokerList;
	
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	
	public boolean isDizhu() {
		return dizhu;
	}
	public void setDizhu(boolean dizhu) {
		this.dizhu = dizhu;
	}
	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	public List<Poker> getPokerList() {
		return pokerList;
	}
	public void setPokerList(List<Poker> pokerList) {
		this.pokerList = pokerList;
	}
	
	
}
