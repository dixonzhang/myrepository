package com.mama100.monitorcenter.client;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.jms.JMSException;

public class BatchTest {
	public static void main(String[] args) {
		new BatchTest().test();
	}

	private void test(){
		int tcount = 3000;
		
		Thread[] ts = new Thread[tcount];
		for(int i = 0; i < tcount; i++){
			Thread t = new Thread(new Task(i));
			ts[i] = t;
		}
		
		for(Thread t : ts){
			t.start();
		}
		
		
		
		try {
			ts[tcount-1].join();
			
			Thread.sleep(10000);
			System.out.println("------------");
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.exit(1);
		}
	}
	
	private class Task implements Runnable{
		
		private int i;
		String appName = "test";
		String target = "test";
		
		Map<String, String> mm = new HashMap<String, String>(3);
		
		public Task(int i){
			this.i = i;
		}
		
		public void run() {
			mm.put("key", i+"");
			
			try {
				MonitorManager.getInstance().addMonitor(appName, target, MonitorType.poolStatus, mm);
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
