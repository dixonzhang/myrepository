package com.dixon.game.ddz.resp;

public class PlayRes extends BaseRes {
	
	private boolean lastPlayerFollow;//上一家是否管上？

	public PlayRes(String respType, String respDesc) {
		super(respType, respDesc);
	}

	public boolean isLastPlayerFollow() {
		return lastPlayerFollow;
	}

	public void setLastPlayerFollow(boolean lastPlayerFollow) {
		this.lastPlayerFollow = lastPlayerFollow;
	}
	
	
}
