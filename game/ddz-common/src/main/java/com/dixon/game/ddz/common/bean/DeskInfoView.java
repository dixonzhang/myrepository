package com.dixon.game.ddz.common.bean;

import java.util.List;

import com.dixon.game.ddz.common.enu.DeskType;
import com.dixon.game.ddz.common.enu.PlayerType;

public class DeskInfoView {
	private int num;
	
	private DeskType deskType;
	private PlayerType playerType;
	
	private List<Player> playerList;
	
	private int currentIndex;
	private List<Poker> dizhuPoker;
	
	private LastAction lastAction;

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

	public PlayerType getPlayerType() {
		return playerType;
	}

	public void setPlayerType(PlayerType playerType) {
		this.playerType = playerType;
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

	public LastAction getLastAction() {
		return lastAction;
	}

	public void setLastAction(LastAction lastAction) {
		this.lastAction = lastAction;
	}

	public List<Poker> getDizhuPoker() {
		return dizhuPoker;
	}

	public void setDizhuPoker(List<Poker> dizhuPoker) {
		this.dizhuPoker = dizhuPoker;
	}

}
