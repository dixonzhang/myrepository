package com.dixon.game.ddz.common.resp;

import com.dixon.game.ddz.common.bean.DeskInfoView;

public class DeskInfoRes extends BaseRes {
	private DeskInfoView deskInfo;
	
	public DeskInfoRes(){}
	public DeskInfoRes(String respType, String respDesc) {
		super(respType, respDesc);
	}
	public DeskInfoView getDeskInfo() {
		return deskInfo;
	}
	public void setDeskInfo(DeskInfoView deskInfo) {
		this.deskInfo = deskInfo;
	}
	
}
