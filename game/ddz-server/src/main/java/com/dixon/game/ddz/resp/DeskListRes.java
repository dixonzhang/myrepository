package com.dixon.game.ddz.resp;

import java.util.List;

import com.dixon.game.ddz.bean.Desk;

public class DeskListRes extends BaseRes {
	private List<Desk> desks;
	public DeskListRes(String respType, String respDesc) {
		super(respType, respDesc);
	}
	public List<Desk> getDesks() {
		return desks;
	}
	public void setDesks(List<Desk> desks) {
		this.desks = desks;
	}
}
