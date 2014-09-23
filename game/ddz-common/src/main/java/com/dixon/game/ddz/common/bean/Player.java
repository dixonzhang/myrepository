package com.dixon.game.ddz.common.bean;

import java.util.List;

import com.dixon.game.ddz.common.bean.Poker;

public class Player {
	private String playerId;
	//是否准备了
	private boolean ready;
	//用户剩下的牌
	private List<Poker> pokerList;
	
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
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
