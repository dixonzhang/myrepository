package com.dixon.game.ddz.bean;

import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Desk implements Comparable<Desk>{
	private int deskNum;
	//玩家，关系出牌顺序
	private Vector<Player> playerList;
	
//	//玩家 信息
//	private Map<String, Player> playerMap;
	
	//当前出的牌
	private Vector<Poker> currentPokers;
	
	//当前出牌序号
	private int currentIndex;
	
	//地主序号
	private int dizhuIndex = -1;
	//抢地主次数，当=4时，决定谁是地主
	//派牌时，随机初始化dizhuIndex参数，比如等于1，则由序号为1的玩家先叫，循环一圈 ；抢地主的则改变dizhuIndex,不抢的则不变，每次grebTimes++,最终grebTimes=4时，确定地主
	private int grabTimes = 0;
	
	//序号循环
	public int currentIndexAdd(){
		currentIndex++;
		
		if(currentIndex == 3)
			currentIndex = 0;
		
		return currentIndex;
	}
	
	public int grabTimesAdd(){
		return ++grabTimes;
	}
	
	
	//派牌
	private Map<Integer, List<Poker>> dealPokerMap;

	public int getDeskNum() {
		return deskNum;
	}

	public void setDeskNum(int deskNum) {
		this.deskNum = deskNum;
	}

	public Vector<Player> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(Vector<Player> playerList) {
		this.playerList = playerList;
	}

	public Vector<Poker> getCurrentPokers() {
		return currentPokers;
	}

	public void setCurrentPokers(Vector<Poker> currentPokers) {
		this.currentPokers = currentPokers;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public Map<Integer, List<Poker>> getDealPokerMap() {
		return dealPokerMap;
	}

	public void setDealPokerMap(Map<Integer, List<Poker>> dealPokerMap) {
		this.dealPokerMap = dealPokerMap;
	}

	@Override
	public int compareTo(Desk o) {
		return deskNum - o.getDeskNum();
	}

	public int getDizhuIndex() {
		return dizhuIndex;
	}

	public void setDizhuIndex(int preDizhuIndex) {
		this.dizhuIndex = preDizhuIndex;
	}

	public int getGrabTimes() {
		return grabTimes;
	}
}
