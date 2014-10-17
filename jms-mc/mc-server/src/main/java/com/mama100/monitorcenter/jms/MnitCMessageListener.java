package com.mama100.monitorcenter.jms;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.mama100.monitorcenter.constants.EHCacheConstants;
import com.mama100.monitorcenter.enu.MsgType;
import com.mama100.monitorcenter.message.ExceptionMessage;
import com.mama100.monitorcenter.message.PoolStatusMessage;
import com.mama100.monitorcenter.utils.CacheUtil;

/**
 * 消息中心的入口处理器
 * 
 * @author Raphael
 * 
 */
public class MnitCMessageListener implements MessageListener {
	Logger logger = Logger.getLogger(getClass().getName());
	org.apache.log4j.Logger receiveLog = org.apache.log4j.Logger.getLogger("RECEIVE");
	
	@Autowired
	private CacheUtil cacheUtil;
	
	private String APP_NAME = "appName";
	private String TARGET = "target";
	private String APP_ADDR = "appAddr";
	private String msgType = "msgType";
	
	@Override
    public void onMessage(Message message) {
    	if(message instanceof MapMessage){
    		MapMessage mm = (MapMessage) message;
    		try {
				if(StringUtils.isNotBlank(mm.getString(APP_NAME))/* && StringUtils.isNotBlank(mm.getString(TARGET))
						&& StringUtils.isNotBlank(mm.getString(APP_ADDR))*/ && StringUtils.isNotBlank(mm.getString(msgType))){
//					set2MonitorMap(mm);
					wrapMsg(mm);
				}
				else
					logger.info("Unuseful MapMessage, because it is not contains key 'appName' and 'msgType'.");
					
			} catch (JMSException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
			}
    	}
    }
	
	private void wrapMsg(MapMessage mm) throws JMSException{
		try {
			MsgType mt = MsgType.valueOf(mm.getString(msgType));
			
			if(mt == MsgType.poolStatus){
				wrapPoolStatusMsgAndAddToList(mm);
			}
			else if(mt == MsgType.exception){
				wrapExceptionMsgAndAddToList(mm);
			}
			else{
				logger.info("Invalid message type ["+ mm.getString(msgType) +"]");
			}
		} catch (Exception e) {
//			e.printStackTrace();
			logger.info("Invalid message type ["+ mm.getString(msgType) +"]");
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void wrapPoolStatusMsgAndAddToList(MapMessage mm) throws JMSException{
		
		String appName = mm.getString(APP_NAME);
    	String target = mm.getString(TARGET);
    	String appAddr = mm.getString(APP_ADDR);
		
		PoolStatusMessage psm = new PoolStatusMessage();
		psm.setAppName(appName);
		psm.setTarget(target);
		psm.setAppAddr(appAddr);
		
		psm.setCreatedTime(new Date());
		
		StringBuffer logInfo = new StringBuffer(100);
    	logInfo.append(">>received ")
    	.append(psm.getFullName())
    	.append(" [ ");
    	
    	Vector<PoolStatusMessage> vector = (Vector<PoolStatusMessage>) cacheUtil.getElementValue(EHCacheConstants.CACHE_NAME_POOL_STATUS_MAP, psm.getKey());
    	
    	if(vector == null){
    		vector = new Vector<PoolStatusMessage>();
    		cacheUtil.putElement(EHCacheConstants.CACHE_NAME_POOL_STATUS_MAP, psm.getKey(), vector);
    	}
    	
    	Map<String, String> statusMap = new HashMap<String, String>(5);
    	
    	Enumeration names = mm.getMapNames();
    	while(names.hasMoreElements()){
    		String name = (String)names.nextElement();
    		if(APP_NAME.equals(name) || APP_ADDR.equals(name) || TARGET.equals(name))
    			continue;
    		
    		
    		String value = mm.getString(name);
    		if(StringUtils.isNotBlank(value) && StringUtils.isNumeric(value))
    			statusMap.put(name, value);
    		
    		logInfo.append(name)
    		.append("=")
    		.append(value)
    		.append(" ");
    	}
    	
    	logInfo.append("] ");

    	psm.setStatusMap(statusMap);
    	vector.add(psm);
    	
    	receiveLog.info(logInfo.toString());
	}
	
	/**
	 * 包装异常消息
	 * @param mm
	 * @throws JMSException
	 */
	@SuppressWarnings("unchecked")
	private void wrapExceptionMsgAndAddToList(MapMessage mm) throws JMSException{
		
		String appName = mm.getString(APP_NAME);
    	String target = mm.getString(TARGET);
    	String appAddr = mm.getString(APP_ADDR);
		
		ExceptionMessage psm = new ExceptionMessage();
		psm.setAppName(appName);
		psm.setTarget(target);
		psm.setAppAddr(appAddr);
		
		psm.setCreatedTime(new Date());
		
		StringBuffer logInfo = new StringBuffer(100);
    	logInfo.append(">>received ")
    	.append(psm.getFullName())
    	.append(" [ ");
    	
    	Vector<ExceptionMessage> vector = (Vector<ExceptionMessage>) cacheUtil.getElementValue(EHCacheConstants.CACHE_NAME_EXCEPTION_MAP, psm.getKey());
    	if(vector == null){
    		vector = new Vector<ExceptionMessage>();
    		cacheUtil.putElement(EHCacheConstants.CACHE_NAME_EXCEPTION_MAP, psm.getKey(), vector);
    	}
    	
    	psm.setType(mm.getString("type"));
    	psm.setMsg(mm.getString("msg"));
    	
    	logInfo.append("type=").append(psm.getType())
    	.append(" msg=").append(psm.getMsg().substring(0, Math.min(20, psm.getMsg().length())));
    	
    	logInfo.append("] ");

    	vector.add(psm);
    	
    	receiveLog.info(logInfo.toString());
	}
	
	
}
