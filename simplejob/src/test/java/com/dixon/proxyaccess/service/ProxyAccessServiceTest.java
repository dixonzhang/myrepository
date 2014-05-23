package com.dixon.proxyaccess.service;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dixon.proxyaccess.bean.IpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-conf/spring-context.xml" }, inheritLocations = true)
public class ProxyAccessServiceTest {
	
	@Autowired
	private IProxyAccessService service;
	
	@Test
	public void readProxyFromInternet(){
		Set<IpPort> set = service.readProxyFromInternet();
		Assert.assertNotSame(0, set.size());
	}
	
}
