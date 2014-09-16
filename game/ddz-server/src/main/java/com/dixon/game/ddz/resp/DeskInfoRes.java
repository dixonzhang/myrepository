package com.dixon.game.ddz.resp;

import com.dixon.game.ddz.bean.Desk;

public class DeskInfoRes extends BaseRes {
	private Desk desk;
	public DeskInfoRes(String respType, String respDesc) {
		super(respType, respDesc);
	}
	public Desk getDesk() {
		return desk;
	}
	public void setDesk(Desk desk) {
		this.desk = desk;
	}
	
}
