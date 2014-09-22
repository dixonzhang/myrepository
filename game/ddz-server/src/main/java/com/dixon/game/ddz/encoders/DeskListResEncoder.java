package com.dixon.game.ddz.encoders;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import net.sf.json.JSONObject;

import com.dixon.game.ddz.common.resp.DeskListRes;


public class DeskListResEncoder implements Encoder.Text<DeskListRes> {

	@Override
	public void init(EndpointConfig config) {

	}

	@Override
	public void destroy() {

	}

	@Override
	public String encode(DeskListRes object) throws EncodeException {
		return JSONObject.fromObject(object).toString();
	}
}
