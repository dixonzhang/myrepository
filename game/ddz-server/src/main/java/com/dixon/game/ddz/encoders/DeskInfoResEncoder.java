package com.dixon.game.ddz.encoders;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import net.sf.json.JSONObject;

import com.dixon.game.ddz.common.resp.DeskInfoRes;


public class DeskInfoResEncoder implements Encoder.Text<DeskInfoRes> {

	@Override
	public void init(EndpointConfig config) {

	}

	@Override
	public void destroy() {

	}

	@Override
	public String encode(DeskInfoRes object) throws EncodeException {
		return JSONObject.fromObject(object).toString();
	}
}
