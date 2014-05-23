package com.dixon.proxyaccess.service;

import java.util.List;
import java.util.Set;

import com.dixon.proxyaccess.bean.IpPort;

public interface IProxyAccessService {
	public Set<IpPort> readProxyFromInternet();
	public List<String> readUrls();
}
