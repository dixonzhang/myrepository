package com.dixon.game.ddz.android;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectMapper;

import com.dixon.game.ddz.android.MainActivity.MyHandler;
import com.dixon.game.ddz.android.service.WebSocketService;
import com.dixon.game.ddz.android.service.WebSocketService.MyBinder;
import com.dixon.game.ddz.android.views.PlayView;
import com.dixon.game.ddz.common.bean.DeskInfoView;
import com.dixon.game.ddz.common.enu.ChatType;
import com.dixon.game.ddz.common.enu.RespType;
import com.dixon.game.ddz.common.message.JoinMessage;
import com.dixon.game.ddz.common.resp.BaseRes;
import com.dixon.game.ddz.common.resp.DeskInfoRes;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class PlayActivity extends Activity{
	private static final String TAG = "de.tavendo.test1";
	
	private Handler myHandler = new MyHandler();
	private PlayView playView;
	
	private boolean isJoin = false;
	private int oncreatetimes = 0;
	
	WebSocketService.MyBinder binder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG, "playactivity 创建次数： " + (++oncreatetimes));
		
		playView = new PlayView(this);
		setContentView(playView);
		
//		getWindow().setFormat(PixelFormat.RGBA_8888);
//		// 锁定横屏
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		binder = (MyBinder) getIntent().getExtras().getSerializable("binder");
		//给service调用，更新UI
		binder.addHandler(PlayActivity.class.getName(), myHandler);
		binder.setName(PlayActivity.class.getName());
		//加入桌
		if(isJoin == false)
			join();
	}

	private void join() {
		Bundle bundle = getIntent().getExtras();
		Log.d(TAG, " deskId= " + bundle.getInt("deskId"));
		
//		Log.d(TAG, "get playerid ----------");
		String playerId = getSharedPreferences("userInfo", Activity.MODE_PRIVATE).getString("playerId", "");
		Log.d(TAG, "get playerid ----------" + playerId);
		
		JoinMessage msg = new JoinMessage();
		msg.setChatType(ChatType.join.toString());
		msg.setPlayerId(playerId);
		msg.setDeskId(bundle.getInt("deskId"));
		
		try {
			binder.sendMessage(new ObjectMapper().writeValueAsString(msg));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		isJoin = true;
	}
	
	class MyHandler extends Handler{
		public void handleMessage(android.os.Message msg) {
			Log.d(TAG, msg.getData().toString());
			
			Bundle bundle = msg.getData();
			RespType respType = (RespType)bundle.get("respType");
			String payload = bundle.getString("payload");
			
			if(null != respType){
				ObjectMapper om = new ObjectMapper();
				
				try {
					if(RespType.error == respType || RespType.notify == respType){
						BaseRes baseRes = om.readValue(payload, BaseRes.class);
						Toast.makeText(PlayActivity.this, baseRes.getRespDesc(), Toast.LENGTH_SHORT).show();
					}
					else if(RespType.deskInfo == respType){
						DeskInfoRes deskInfoRes = om.readValue(payload, DeskInfoRes.class);
						DeskInfoView deskInfo = deskInfoRes.getDeskInfo();
						
						//绘制
						Log.d(TAG, "桌号： " + deskInfo.getNum());
						Log.d(TAG, "当前序号： " + deskInfo.getCurrentIndex());
//						Log.d(TAG, deskInfo.getLastAction().toString());
						Log.d(TAG, deskInfo.getPlayerList().toString());
						
						playView.updateDeskInfo(deskInfo);
					}
					else if(RespType.playFirst == respType){
					}
					else if(RespType.playFollow == respType){
					}
					else if(RespType.win == respType){
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		};
	}
}
