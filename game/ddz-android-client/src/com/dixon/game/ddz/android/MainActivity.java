package com.dixon.game.ddz.android;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import com.dixon.game.ddz.android.service.WebSocketService;
import com.dixon.game.ddz.common.bean.DeskListView;
import com.dixon.game.ddz.common.enu.ChatType;
import com.dixon.game.ddz.common.enu.RespType;
import com.dixon.game.ddz.common.message.Message;
import com.dixon.game.ddz.common.resp.BaseRes;
import com.dixon.game.ddz.common.resp.DeskListRes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = "de.tavendo.test1";

	// 保持所启动的Service的IBinder对象
	WebSocketService.MyBinder binder;
	private Handler myHandler = new MyHandler();
	
	// 定义一个ServiceConnection对象
	private ServiceConnection conn = new ServiceConnection() {
		// 当该Activity与Service连接成功时回调该方法
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "--Service Connected--");
			// 获取Service的onBind方法所返回的MyBinder对象
			binder = (WebSocketService.MyBinder) service;
			//给service调用，更新UI
			binder.addHandler(MainActivity.class.getName(), myHandler);
			binder.setName(MainActivity.class.getName());
		}

		// 当该Activity与Service断开连接时回调该方法
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "--Service Disconnected--");
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 锁定横屏
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_main);
		
		startService();
		
		//登录按钮
		Button loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				login();
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		unbindService(conn);//解绑service，使其销毁
		super.onDestroy();
	}

	private void startService(){
		final Intent intent = new Intent();
		// 为Intent设置Action属性
		intent.setAction("com.dixon.game.ddz.android.service.WEBSOCKET_SERVICE");
		// 绑定指定Serivce
		bindService(intent, conn, Service.BIND_AUTO_CREATE);
		startService(intent);//启动,否则不会调用onstart方法
	}
	

	private void login(){
		EditText nameText = (EditText)findViewById(R.id.playerName);
		if(null != nameText.getText().toString() && !"".equals(nameText.getText().toString())){
			Message loginMsg = new Message();
			loginMsg.setChatType(ChatType.login.toString());
			loginMsg.setPlayerId(nameText.getText().toString());
			
			Log.d(TAG, nameText.getText().toString());
			
			ObjectMapper om = new ObjectMapper();
			try {
				binder.sendMessage(om.writeValueAsString(loginMsg));
				SharedPreferences.Editor editor = getSharedPreferences("userInfo", Activity.MODE_PRIVATE)
						.edit().putString("playerId", nameText.getText().toString());

				editor.commit();//提交,保存玩家信息
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
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
						Toast.makeText(MainActivity.this, baseRes.getRespDesc(), Toast.LENGTH_SHORT).show();
					}
					else if(RespType.deskList == respType){
						DeskListRes deskListRes = om.readValue(payload, DeskListRes.class);
						
						final List<DeskListView> deskList = deskListRes.getDesks();
						String[] items = new String[deskList.size()];
						for(int i = 0; i < deskList.size(); i++){
							DeskListView view = deskList.get(i);
							items[i] = view.getNum() + " (" + view.getPulyerCount() + ")";
						}
						
						new AlertDialog.Builder(MainActivity.this).setTitle("桌列表")
						.setItems(items, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								//TODO 打开新activity
								Log.d(TAG, "计入了桌 " + deskList.get(which).getNum());
								Intent intent = new Intent();
								intent.setClass(MainActivity.this, PlayActivity.class);
								
								Bundle bundle=new Bundle();
								bundle.putInt("deskId", deskList.get(which).getNum());
								bundle.putSerializable("binder", binder);
								intent.putExtras(bundle);

								startActivity(intent);
							}
						})
//						.setNegativeButton("进入", )
						.show();
					}
					else if(RespType.deskFull == respType){
//					System.out.println(message);
						//TODO 重新获取桌列表
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
