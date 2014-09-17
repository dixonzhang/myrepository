package com.dixon.game.ddz.common.bean;

import java.util.List;

public class DeskInfoView {
	private int num;
	
	private List<Player> playerList;
	
	private int currentIndex;
	
	private List<Poker> lastPokerList;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public List<Player> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<Player> playerList) {
		this.playerList = playerList;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public List<Poker> getLastPokerList() {
		return lastPokerList;
	}

	public void setLastPokerList(List<Poker> lastPokerList) {
		this.lastPokerList = lastPokerList;
	}
}
