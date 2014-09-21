package com.dixon.game.ddz.common.resp;

import java.util.List;

import com.dixon.game.ddz.common.bean.DeskListView;

public class DeskListRes extends BaseRes {
	private List<DeskListView> desks;
	
	public DeskListRes(){}
	public DeskListRes(String respType, String respDesc) {
		super(respType, respDesc);
	}
	public List<DeskListView> getDesks() {
		return desks;
	}
	public void setDesks(List<DeskListView> desks) {
		this.desks = desks;
	}
}
