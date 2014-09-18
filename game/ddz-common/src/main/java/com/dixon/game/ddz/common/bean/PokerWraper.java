package com.dixon.game.ddz.common.bean;

import java.util.List;

import com.dixon.game.ddz.common.enu.PokerType;
import com.dixon.game.ddz.common.exceptions.NotSamePokerTypeException;

public class PokerWraper {
	private List<Poker> pokerList;
	private int pokerLeaderValue;//主导牌大小
	private PokerType pokerType;
	private int amount;//牌数量
	
	@Override
	public String toString() {
		return "pokerType=" + pokerType + ",lv=" + pokerLeaderValue + ",amount=" + amount;
	}
	
	/**
	 * 跟上， 上一手牌
	 * @param pw 上一手牌
	 * @return
	 * @throws NotSamePokerTypeException 
	 */
	public boolean follow(PokerWraper pw) throws NotSamePokerTypeException{
		//相同的牌型，相同的牌数量才能跟上
		if(pokerType == pw.getPokerType() && amount == pw.getAmount()){
			return pokerLeaderValue > pw.getPokerLeaderValue();
		}
		else
			throw new NotSamePokerTypeException("not same");
	}
	
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
