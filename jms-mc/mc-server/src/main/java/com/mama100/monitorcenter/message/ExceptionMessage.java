package com.mama100.monitorcenter.message;

/**
 * 异常消息
 * @author dixon
 *
 */
public class ExceptionMessage extends Message {
	private String type;	//异常类型
	private String msg;	//异常详情

	public String getType() {
		return type;
	}

	public void setType(String exceptionType) {
		this.type = exceptionType;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String exceptionMsg) {
		this.msg = exceptionMsg;
	}
}
