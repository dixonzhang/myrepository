package com.mama100.monitorcenter.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.telnet.TelnetClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.mama100.monitorcenter.bean.CountBean;
import com.mama100.monitorcenter.constants.EHCacheConstants;
import com.mama100.monitorcenter.message.ConnectionMessage;
import com.mama100.monitorcenter.utils.CacheUtil;

@Service
public class TelnetService {
	
	@Autowired
	private TaskExecutor taskExecutor;
	
	@Autowired
	private CacheUtil cacheUtil;
	
	private static String DEFAULT_ENV = "dev";
	List<ConnectionMessage> list = null;
	
	
	public TelnetService() {
		load();
	}

	private void load() {
		ResourceBundle bundleParam = ResourceBundle.getBundle(getRuntimeEnv().concat("/ipports"));
		
		list = new ArrayList<ConnectionMessage>(bundleParam.keySet().size());
		
		for(Iterator<String> it = bundleParam.keySet().iterator(); it.hasNext(); ){
			String key = it.next();
			if(StringUtils.isNoneBlank(key)){
				String serverName = key;
				String value = bundleParam.getString(key);
				
				int index = value.indexOf(":");
				if(index == -1)
					continue;
				
				String ip = value.substring(0, index);
				String port = value.substring(index+1);
				
				ConnectionMessage cm = new ConnectionMessage();
				cm.setAppName(serverName);
				cm.setIp(ip);
				cm.setPort(new Integer(port));
				
				list.add(cm);
			}
		}
		
		Collections.sort(list, new Comparator<ConnectionMessage>() {
			@Override
			public int compare(ConnectionMessage o1, ConnectionMessage o2) {
				return o1.getAppName().compareTo(o2.getAppName());
			}
		});
	}
	
	/**
	 * 获取ipports.properties文件配置的所有服务的连通情况
	 * @return
	 */
	public List<ConnectionMessage> getAllServerConnectionStatus(){
		if(null == list || list.isEmpty())
			load();
		
		for(ConnectionMessage cm : list){
			cm.setConnected(testTelnet(cm.getIp(), cm.getPort()));
		}
		
		return list;
	}

	/**
	 * 返回list的数量与产生key
	 * @return
	 */
	public CountBean count(){
		if(null == list || list.isEmpty())
			load();
		
		final String key = UUID.randomUUID().toString();
		
		CountBean cb = new CountBean();
		cb.setCount(list.size());
		
		cb.setKey(key);
		
//		cacheUtil.getConnMap().put(key, new Vector<ConnectionMessage>(5));
		
		cacheUtil.putElement(EHCacheConstants.CACHE_NAME_CONN_MAP, key,  new Vector<ConnectionMessage>(5));
		
		//线程去处理
		taskExecutor.execute(new Runnable() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				Vector<ConnectionMessage> vector = (Vector<ConnectionMessage>) cacheUtil.getElementValue(EHCacheConstants.CACHE_NAME_CONN_MAP, key);
				if(null != vector){
					for(ConnectionMessage cm : list){
						cm.setConnected(testTelnet(cm.getIp(), cm.getPort()));
						vector.add(cm);
					}
				}
			}
		});
		
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				for(ConnectionMessage cm : list){
//					long start = System.currentTimeMillis();
//					cm.setConnected(testTelnet(cm.getIp(), cm.getPort()));
//					System.out.println("..take time " + (System.currentTimeMillis() - start));
//					
//					CONN_MAP.get(key).add(cm);
//				}
//			}
//		}).start();
		
		return cb;
	}
	
	@SuppressWarnings("unchecked")
	public List<ConnectionMessage> takeByKey(String key){
		List<ConnectionMessage> reList = null;
		
		Vector<ConnectionMessage> vector = (Vector<ConnectionMessage>) cacheUtil.getElementValue(EHCacheConstants.CACHE_NAME_CONN_MAP, key);
		if(!vector.isEmpty()){
			reList = new ArrayList<ConnectionMessage>(list.size());
			
			for(Iterator<ConnectionMessage> it = vector.iterator(); it.hasNext(); ){
				reList.add(it.next());
				it.remove();
			}
		}
		
		return reList;
	}
	
	
	/**
	 * telnet ip 端口是否连通
	 * @param ip
	 * @param port
	 * @return
	 */
	public boolean testTelnet(String ip, int port){
		TelnetClient tc = new TelnetClient();
		tc.setDefaultTimeout(2000);//两秒超时
		tc.setConnectTimeout(2000);
		
		try {
			tc.connect(ip, port);
			return tc.isConnected();
		} catch (Exception e) {
			return false;
		}
	}
	
	
	private String getRuntimeEnv() {
		String env = System.getProperty("runtime.env");
		if (env == null || "".equals(env)) {
			System.setProperty("runtime.env", DEFAULT_ENV);
			env = DEFAULT_ENV;
		}
		return env;
	}
	
	public static void main(String[] args) {
//		new TelnetService().getAllServerConnectionStatus();
		
		TelnetService ts = new TelnetService();
		CountBean cb = ts.count();
		
		int amount = 0;
		
		while(amount < cb.getCount()){
			List<ConnectionMessage> list = ts.takeByKey(cb.getKey());
			
			if(null != list){
				amount += list.size();
				for(ConnectionMessage cm : list){
					System.out.println(cm.getAppName() + " " + cm.isConnected());
				}
			}
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.exit(-1);
	}
}
