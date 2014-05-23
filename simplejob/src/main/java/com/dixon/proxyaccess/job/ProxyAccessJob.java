package com.dixon.proxyaccess.job;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.annotation.Resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dixon.proxyaccess.bean.IpPort;
import com.dixon.proxyaccess.service.IProxyAccessService;
import com.dixon.proxyaccess.util.HttpClientUtil;

@Service
public class ProxyAccessJob {
	private Logger logger = Logger.getLogger(getClass().getName());
	
	@Autowired
	private IProxyAccessService service;
	
	@Resource
	private Executor proxyExecutor;
	
	@Resource
	private Executor accessExecutor;
	
	public void doJob() {
		Set<IpPort> ipportSet = service.readProxyFromInternet();
		List<String> urlList = service.readUrls();
		
		Random random = new Random();
		
		// 可用的代理服务器ip
		int avaibel = 200;
		Vector<IpPort> availableIpp = new Vector<IpPort>(avaibel);

		CountDownLatch latch = new CountDownLatch(ipportSet.size());

		int tryCount = 1;
		for (IpPort ipPort : ipportSet) {
			try {
				proxyExecutor.execute(new searchAvailbleProxyTask(tryCount,
						availableIpp, ipPort, latch));
				// new Thread(new searchAvailbleProxyTask(availableIpp, ipPort,
				// latch)).start();
				tryCount++;
			} catch (RejectedExecutionException e) {
				e.printStackTrace();
				latch.countDown();// 队列满塞不进去的时候，减1
			}
		}

		try {
			latch.await(5, TimeUnit.MINUTES);// 等待1分钟

//			writeAvailableProxyIp(availableIpp);
		} catch (InterruptedException e) {
			logger.info("======================================================");
			e.printStackTrace();
		}

		logger.info("可用的代理服务器数：" + availableIpp.size());

		CountDownLatch l = new CountDownLatch(availableIpp.size());

		int accessCount = 0;

		for (IpPort ipPort : availableIpp) {
			// 从url中随机取出十个地址访问
			Set<String> urlSet = new HashSet<String>(10);
			if (urlList.size() <= 10)
				urlSet.addAll(urlList);
			else {
				int count = 0;
				while (urlSet.size() < 10 && count < 1000) {
					urlSet.add(urlList.get(random.nextInt(urlList.size())));
					count++;
				}
			}

			logger.info("will call " + urlSet.size() + " :" + urlSet);

			try {
				accessExecutor.execute(new Task(urlSet, ipPort, l));
			} catch (RejectedExecutionException e) {
				l.countDown();// 队列满塞不进去的时候，减1
			}

			accessCount += urlSet.size();
		}

		try {
			l.await(5, TimeUnit.MINUTES);// 等待5分钟
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("共访问：" + accessCount);

	}
	
	
	private class searchAvailbleProxyTask implements Runnable {
		private int tryCount;
		private Vector<IpPort> availableIpp;
		private IpPort ipPort;
		private CountDownLatch latch;

		public searchAvailbleProxyTask(int trCount, Vector<IpPort> availableIpp, IpPort ipPort, CountDownLatch latch) {
			this.tryCount = trCount;
			this.availableIpp = availableIpp;
			this.ipPort = ipPort;
			this.latch = latch;
		}

		@Override
		public void run() {
			StringBuffer sb = new StringBuffer(500).append("尝试连接：").append(tryCount).append("\t");
			
			HttpClient httpClient = HttpClientUtil.getHttpClient(ipPort.getIp(), ipPort.getPort());
			
			GetMethod get = new GetMethod("http://www.baidu.com");

			try {
				httpClient.executeMethod(get);

				if (200 == get.getStatusCode()) {
//					availabelClients.add(httpClient);
					availableIpp.add(ipPort);
				}
			} catch (HttpException e) {
				sb.append(e.getMessage());
			} catch (IOException e) {
				sb.append(e.getMessage());
			} finally {
				latch.countDown();

				HttpClientUtil.shutdown(httpClient);
				
				logger.info(sb.toString());
			}
		}
	}
	
	private class Task implements Runnable {
		private Set<String> urlSet;
		private IpPort ipp;
		private CountDownLatch latch;

		private Task(Set<String> urlSet, IpPort ipp, CountDownLatch latch) {
			this.urlSet = urlSet;
			this.ipp = ipp;
			this.latch = latch;
		}

		@Override
		public void run() {
			HttpClient httpClient = HttpClientUtil.getHttpClient(ipp.getIp(), ipp.getPort());

			try {
				StringBuffer sb = new StringBuffer(1000);
				sb.append("\n>>>>>>\n");
				
				for (Iterator<String> it = urlSet.iterator(); it.hasNext();) {
					String url = it.next();
					
					GetMethod gm = new GetMethod(url);
					httpClient.executeMethod(gm);
					sb.append(gm.getStatusCode()).append(":").append(url).append("\n");
				}
				
				logger.info(sb.toString());
				HttpClientUtil.shutdown(httpClient);
			} catch (HttpException e) {
//				e.printStackTrace();
			} catch (IOException e) {
//				e.printStackTrace();
			} finally {
				latch.countDown();
			}
		}
	}
}
