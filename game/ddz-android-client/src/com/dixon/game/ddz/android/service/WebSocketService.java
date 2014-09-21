package com.dixon.game.ddz.android.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dixon.game.ddz.common.enu.RespType;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class WebSocketService extends Service{
	private static final String TAG = "de.tavendo.test1";
	private final WebSocketConnection mConnection = new WebSocketConnection();
	private Map<String, Handler> handlerMap = new HashMap<String, Handler>(2);
	
	private String currentActivityName = "";
	
	//是否已连接
	private boolean connected = false;
	
	// 定义onBinder方法所返回的对象
	private MyBinder binder = new MyBinder();

	// 通过继承Binder来实现IBinder类
	public class MyBinder extends Binder implements Serializable{
		private static final long serialVersionUID = 7405918647619411682L;
		public void sendMessage(String message) {
			mConnection.sendTextMessage(message);
		}
		public void addHandler(String activityName, Handler handler){
			handlerMap.put(activityName, handler);
//			handlers.add(handler);
		}
		public void setName(String name){
			currentActivityName = name;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "WebSocketService onCreate");
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "WebSocketService onStart");
		
		if(connected == false)
			connect();
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "WebSocketService onDestroy");
	}
	
	private void connect() {

		final String wsuri = "ws://192.168.1.101:8888/ddz-server/websocket/ddz";

		try {
			mConnection.connect(wsuri, new MyWebSocketHandler(wsuri));
		} catch (WebSocketException e) {
			e.printStackTrace();
			Log.d(TAG, e.toString());
		}
	}
	
	class MyWebSocketHandler extends WebSocketHandler{
		private String wsuri;
		public MyWebSocketHandler(String wsuri) {
			super();
			this.wsuri = wsuri;
		}

		@Override
		public void onOpen() {
			Log.d(TAG, "Status: Connected to " + wsuri);
			
			connected = true;
		}

		@Override
		public void onTextMessage(String payload) {
			Log.d(TAG, "Got echo: " + payload);
			RespType respType = getRespType(payload);
			
			Message message = new Message();
			Bundle bundle = new Bundle();
			bundle.putString("payload", payload);
			bundle.putSerializable("respType", respType);
			message.setData(bundle);
			
			//给activity发送消息通知
			Log.d(TAG, "handler size ="+ handlerMap.size());
//			for(Handler handler : handlers){
//				Log.d(TAG, "handler " + handler.getClass().getName());
//				handler.sendMessage(message);
//			}
			handlerMap.get(currentActivityName).sendMessage(message);
		}

		private RespType getRespType(String payload) {
			Pattern p = Pattern.compile("\"respType\":\"(\\w+)\"");
			Matcher m = p.matcher(payload);
			
			if(m.find()){
				return RespType.valueOf(m.group(1));
			}
			return null;
		}

		@Override
		public void onClose(int code, String reason) {
			Log.d(TAG, "Connection lost.");
			
			connected = false;
		}
	}
}
