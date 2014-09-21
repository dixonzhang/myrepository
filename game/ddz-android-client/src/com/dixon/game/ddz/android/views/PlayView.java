package com.dixon.game.ddz.android.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dixon.game.ddz.common.bean.DeskInfoView;
import com.dixon.game.ddz.common.bean.Poker;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PlayView extends SurfaceView implements SurfaceHolder.Callback{
	private static final String TAG = "de.tavendo.test1";
	SurfaceHolder surfaceHolder;
	Canvas canvas;
	
	private Map<String, Bitmap> bitmapMap = new HashMap<String, Bitmap>(60);
	
	public PlayView(Context context) {
		super(context);
		
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);
	}
	
	

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		initBitMap();
//		try {
//			canvas = surfaceHolder.lockCanvas();
//			drawBackground();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(canvas != null)
//				surfaceHolder.unlockCanvasAndPost(canvas);
//		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		bitmapMap.clear();
	}
	
	public void updateDeskInfo(DeskInfoView deskInfo){
		try {
			canvas = surfaceHolder.lockCanvas();
			//画背景
			drawBackground();
			
			//画自己
			
			//画左玩家
			
			//画右玩家
			
			//画地主牌
			
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if(canvas != null)
				surfaceHolder.unlockCanvasAndPost(canvas);
		}
	}
	
	private void initBitMap(){
		if(bitmapMap.isEmpty()){
			List<String> pokerNameList = Poker.getAllName();
			
			ApplicationInfo appInfo = getContext().getApplicationInfo();
			
			for(String pokerName : pokerNameList){
				int id = getResources().getIdentifier(pokerName, "drawable", appInfo.packageName);
				
				bitmapMap.put(pokerName, BitmapFactory.decodeResource(getResources(), id));
			}
			//背景
			bitmapMap.put("bg", BitmapFactory.decodeResource(getResources(), 
					getResources().getIdentifier("bg", "drawable", appInfo.packageName)));
			bitmapMap.put("cardbg", BitmapFactory.decodeResource(getResources(), 
					getResources().getIdentifier("cardbg", "drawable", appInfo.packageName)));
			
			Log.d(TAG, "bitmap size = " + bitmapMap.size());
		}
	}
	
	// 画背景
	public void drawBackground() {
		Bitmap bgBitmap = bitmapMap.get("bg");
		
		Rect src = new Rect(0, 0, bgBitmap.getWidth()*3 / 4,
				2*bgBitmap.getHeight() / 3);
		Rect dst = new Rect(0, 0, getWidth(), getHeight());
		canvas.drawBitmap(bgBitmap, src, dst, null);
	}
}
