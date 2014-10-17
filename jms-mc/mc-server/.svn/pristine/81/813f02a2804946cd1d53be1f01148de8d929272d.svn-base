package com.mama100.monitorcenter.message;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.mama100.monitorcenter.utils.Md5Util;
/**
 * 
 * @author dixon
 *
 */
public class Message implements Comparable<Message>{
	private String appName;	//应用名称
	private String target;	//目标，比如redis， oracle。。。
	private String appAddr; //Ip  or  ip port
	private Date createdTime; //创建时间
	
	public String getFullName(){
		String fullName = appName;
		
		if(StringUtils.isNotBlank(target))
			fullName = fullName.concat(".").concat(target);
		if(StringUtils.isNotBlank(appAddr))
			fullName = fullName.concat("(").concat(appAddr).concat(")");
		
		return fullName;
	}
	
	public String getKey(){
		return Md5Util.encryptTo16hex(getFullName());
	}
	
	@Override
	public int compareTo(Message o) {
		return createdTime.before(o.getCreatedTime()) ? 1 : -1;
	}
	
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getAppAddr() {
		return appAddr;
	}
	public void setAppAddr(String appAddr) {
		this.appAddr = appAddr;
	}
	public Date getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	
}
