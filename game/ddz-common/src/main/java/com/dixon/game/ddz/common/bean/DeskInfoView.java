package com.dixon.game.ddz.common.bean;

import java.util.List;

import com.dixon.game.ddz.common.enu.DeskType;

public class DeskInfoView {
	private int num;
	private DeskType deskType;
	
	private List<Player> playerList;
	
	private int currentIndex;
	
	private List<Poker> lastPokerList;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public DeskType getDeskType() {
		return deskType;
	}

	public void setDeskType(DeskType deskType) {
		this.deskType = deskType;
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
