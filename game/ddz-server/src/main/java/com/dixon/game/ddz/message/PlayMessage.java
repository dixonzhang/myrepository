package com.dixon.game.ddz.message;

import java.util.List;

import com.dixon.game.ddz.bean.Poker;

public class PlayMessage extends Message {
	private List<Poker> pokerList;

	public List<Poker> getPokerList() {
		return pokerList;
	}

	public void setPokerList(List<Poker> pokerList) {
		this.pokerList = pokerList;
	}

	
}
