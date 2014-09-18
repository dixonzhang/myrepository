package com.dixon.game.ddz.common.bean;

import java.util.List;

import com.dixon.game.ddz.common.enu.PokerType;

public class PokerWraper {
	private List<Poker> pokerList;
	private int pokerLeaderValue;//主导牌大小
	private PokerType pokerType;
	private int amount;//牌数量
	public List<Poker> getPokerList() {
		return pokerList;
	}
	public void setPokerList(List<Poker> pokerList) {
		this.pokerList = pokerList;
	}
	public int getPokerLeaderValue() {
		return pokerLeaderValue;
	}
	public void setPokerLeaderValue(int pokerLeaderValue) {
		this.pokerLeaderValue = pokerLeaderValue;
	}
	public PokerType getPokerType() {
		return pokerType;
	}
	public void setPokerType(PokerType pokerType) {
		this.pokerType = pokerType;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	
}
