package com.dixon.proxyaccess.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dixon.simpleaccess.job.SimpleAccessJob;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring-conf/spring-context.xml" }, inheritLocations = true)
public class SimpleAccessJobTest {
	@Autowired
	private SimpleAccessJob job;
	
	@Test
	public void doJob(){
//		new SimpleAccessJob().doJob();
		job.doJob();
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
