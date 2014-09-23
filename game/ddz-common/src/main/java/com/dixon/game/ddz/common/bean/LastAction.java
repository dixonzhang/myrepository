package com.dixon.game.ddz.common.bean;

import java.util.List;

import com.dixon.game.ddz.common.enu.ActionType;

public class LastAction {
	private ActionType actionType;
	private String actionDesc;
	private List<Poker> pokers;
	public ActionType getActionType() {
		return actionType;
	}
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
	public String getActionDesc() {
		return actionDesc;
	}
	public void setActionDesc(String actionDesc) {
		this.actionDesc = actionDesc;
	}
	public List<Poker> getPokers() {
		return pokers;
	}
	public void setPokers(List<Poker> pokers) {
		this.pokers = pokers;
	}
	
}
