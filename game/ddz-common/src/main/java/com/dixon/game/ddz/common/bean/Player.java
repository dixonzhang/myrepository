package com.dixon.game.ddz.common.bean;

import java.util.List;

import com.dixon.game.ddz.common.bean.Poker;
import com.dixon.game.ddz.common.enu.PlayerType;

public class Player {
	private String playerId;
	private PlayerType playerType;
	//用户剩下的牌
	private List<Poker> pokerList;
	
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	
	public PlayerType getPlayerType() {
		return playerType;
	}
	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
	}
	public List<Poker> getPokerList() {
		return pokerList;
	}
	public void setPokerList(List<Poker> pokerList) {
		this.pokerList = pokerList;
	}
	
	
}
