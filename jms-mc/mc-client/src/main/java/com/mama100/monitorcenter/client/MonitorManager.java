package com.mama100.monitorcenter.client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.Connection;

import com.sun.messaging.ConnectionConfiguration;
import com.sun.messaging.ConnectionFactory;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

public class MonitorManager {
	Logger logger = Logger.getLogger(getClass().getName());
	
	private static String DEFAULT_ENV = "dev";
	
	private static MonitorManager monitor = new MonitorManager();
	private Connection conn;
	private Session session;
	private Queue myQueue;
	private MessageProducer producer;
	
	private MonitorManager() {
		
		ResourceBundle bundleParam = ResourceBundle.getBundle(getRuntimeEnv().concat("/monitor-client-jms"));
		
		String imqAddressList = bundleParam.getString("mq.imqAddressList");
		String queueName = bundleParam.getString("mq.queue");
		
		ConnectionFactory connFactory = new ConnectionFactory();
		try {
			connFactory.setProperty(ConnectionConfiguration.imqAddressList, imqAddressList);
			connFactory.setProperty(ConnectionConfiguration.imqReconnectEnabled, "true");
			
			conn = connFactory.createConnection();
			session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			myQueue = new com.sun.messaging.Queue(queueName);
			producer = session.createProducer(myQueue);

			conn.start();
		} catch (JMSException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	public synchronized static MonitorManager getInstance(){
		return monitor;
	}
	
	public void close() {
		try {
			if(producer != null)
				producer.close();
			if (session != null)
				session.close();
			if (conn != null)
				conn.close();
		} catch (JMSException e) {
//			e.printStackTrace();
		}

	}
	
	/**
	 * 将
	 * @param appName 		应用名称
	 * @param target		
	 * @param monitorMap
	 * @throws JMSException
	 */
	public void addMonitor(String appName, String target, MonitorType monitorType, Map<String, String> monitorMap) throws JMSException{
		addMonitor(appName, target, null, monitorType, monitorMap);
	}
	
	public void addMonitor(String appName, String target, String appAddr, MonitorType monitorType, Map<String, String> monitorMap) throws JMSException{

		String ip = "--";
		try {
			InetAddress addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress();// 获得本机IP
		} catch (UnknownHostException e) {
			// logger.log(Level.SEVERE, e.getMessage(), e);
		}

		MapMessage mm = session.createMapMessage();

		mm.setString("appName", appName);
		mm.setString("target", target);
		mm.setString("appAddr", appAddr == null ? ip : appAddr);
		mm.setString("msgType", monitorType.toString());
		
		if(null != monitorMap){
			for(Iterator<Entry<String, String>> it = monitorMap.entrySet().iterator(); it.hasNext(); ){
				Entry<String, String> entry = it.next();
				mm.setString(entry.getKey(), entry.getValue());
			}
		}

		producer.send(mm);
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
		generateException();
//		generatePoolStatus();
	}

	private static void generatePoolStatus() {
		String appName = "妈妈100";
		String[] targets = {"redis", "mysql"};
		
		Random random = new Random();
		Map<String, String> mm = new HashMap<String, String>(3);
		
		int count = 1000;
		
		try {
			while(true){
				if(count == 0)
					break;
				
				mm.put("numActive", "2" + random.nextInt(10));
				mm.put("maxActive", "30");
				mm.put("maxWait", "20");
				try {
					MonitorManager.getInstance().addMonitor(appName, targets[0], MonitorType.poolStatus, mm);
					
					mm.put("numActive", "2" + random.nextInt(10));
					MonitorManager.getInstance().addMonitor(appName, targets[1], MonitorType.poolStatus, mm);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					count--;
				}
				
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		} finally {
			MonitorManager.getInstance().close();
			System.out.println("----------------");
			System.exit(0);
		}
	}
	
	
	private static void generateException(){
		String appName = "妈妈100";
		
		Map<String, String> mm = new HashMap<String, String>(3);
		
		String type = NullPointerException.class.getName();
		String msg = LogUtil.getStackTrace(new NullPointerException("aaabadfad"));
		
		mm.put("type", type);
		mm.put("msg", msg);
		
		
		Map<String, String> mm2 = new HashMap<String, String>(3);
		
		String type2 = IllegalAccessException.class.getName();
		String msg2 = LogUtil.getStackTrace(new IllegalAccessException("e"));
		
		mm2.put("type", type2);
		mm2.put("msg", msg2);
		
		try {
			MonitorManager.getInstance().addMonitor(appName, null, MonitorType.exception, mm);
			MonitorManager.getInstance().addMonitor(appName, null, MonitorType.exception, mm2);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			MonitorManager.getInstance().close();
			System.out.println("----------------");
			System.exit(0);
		}
	}
}
