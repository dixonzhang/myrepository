package com.dixon.game.ddz.common.resp;

public class BaseRes {
	private String respType;
	private String respDesc;
	public BaseRes(){}
	public BaseRes(String respType, String respDesc) {
		this.respType = respType;
		this.respDesc = respDesc;
	}
	public String getRespType() {
		return respType;
	}
	public void setRespType(String respType) {
		this.respType = respType;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
}
