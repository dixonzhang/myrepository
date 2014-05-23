package com.dixon.proxyaccess.bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpPort {
	private String ip;
	private int port;

	public IpPort(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}
	
	@Override
	public boolean equals(Object obj) {
		IpPort bean = (IpPort)obj;
		
		return ip.equals(bean.getIp()) && port == bean.getPort();
	}
	
	@Override
	public int hashCode() {
		Pattern p = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)");
		Matcher m = p.matcher(ip);
		
		if(m.find()){
			int i1 = Integer.parseInt(m.group(1));
			int i2 = Integer.parseInt(m.group(2));
			int i3 = Integer.parseInt(m.group(3));
			int i4 = Integer.parseInt(m.group(4));
			
			return i1-i2+i3-i4 + port;
		}
		
		return super.hashCode();
	}
}
