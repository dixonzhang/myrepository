package com.dixon.proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class ProxyTest3 {
	private Set<IpPort> ipportSet = new HashSet<>(1000);
	private List<String> urlList = new ArrayList<>(50);

	private Random random = new Random();
	
	private ThreadPoolExecutor executor2 = new ThreadPoolExecutor(10, 30, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(400));
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(30, 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(200));

	public ProxyTest3() {
		readProxyFromInternet();
//		readIpPortFromTxt();
		readUrls();
		play();
	}

	public static void main(String[] args) {
		new ProxyTest3();
	}

	private void play() {
		//可用的代理服务器ip
		int avaibel = 200;
//		Vector<HttpClient> availableClients = new Vector<>(avaibel);
		Vector<IpPort> availableIpp = new Vector<>(avaibel);
		
		CountDownLatch latch = new CountDownLatch(ipportSet.size());
		
		int tryCount = 1;
		for(IpPort ipPort : ipportSet){
			try {
				executor2.execute(new searchAvailbleProxyTask(tryCount, availableIpp, ipPort, latch));
//				new Thread(new searchAvailbleProxyTask(availableIpp, ipPort, latch)).start();
				tryCount++;
			} catch (RejectedExecutionException e) {
				latch.countDown();//队列满塞不进去的时候，减1
			}
		}

		try {
			latch.await(5, TimeUnit.MINUTES);// 等待1分钟
			
			writeAvailableProxyIp(availableIpp);
		} catch (InterruptedException e) {
			System.out.println("======================================================");
			e.printStackTrace();
		}
		
		System.out.println("可用的代理服务器数：" + availableIpp.size());

		
		CountDownLatch l = new CountDownLatch(availableIpp.size());
		
		int accessCount = 0;
		
		for (IpPort ipPort : availableIpp) {
			// 从url中随机取出十个地址访问
			Set<String> urlSet = new HashSet<>(10);
			if (urlList.size() <= 10)
				urlSet.addAll(urlList);
			else {
				int count = 0;
				while (urlSet.size() < 10 && count < 1000) {
					urlSet.add(urlList.get(random.nextInt(urlList.size())));
					count++;
				}
			}

			System.out.println("will call "+ urlSet.size() +" :" + urlSet);

			try {
				executor.execute(new Task(urlSet, ipPort, l));
			} catch (RejectedExecutionException e) {
				l.countDown();//队列满塞不进去的时候，减1
			}
			
			accessCount += urlSet.size();
		}

		try {
			l.await(5, TimeUnit.MINUTES);// 等待5分钟
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("共访问：" + accessCount);
		System.exit(0);
	}
	
	private void writeAvailableProxyIp(Vector<IpPort> availableIpp){
		BufferedWriter bw = null;
		try{
			File file = new File("d:/availableproxyip.txt");
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			
			for(IpPort ipp : availableIpp){
				bw.write(ipp.getIp() + ":" + ipp.getPort());
				bw.newLine();
			}
			
		} catch(Exception e){
			System.out.println("写可用代理服务器ip失败");
		} finally {
			if(bw != null){
				try {
					bw.close();
				} catch (IOException e) {
				}
			}
		}
		
	}

	private class searchAvailbleProxyTask implements Runnable {
//		private Vector<HttpClient> availabelClients;
		private int tryCount;
		private Vector<IpPort> availableIpp;
		private IpPort ipPort;
		private CountDownLatch latch;

		public searchAvailbleProxyTask(int trCount, Vector<IpPort> availableIpp, IpPort ipPort, CountDownLatch latch) {
//			this.availabelClients = availbelClients;
			this.tryCount = trCount;
			this.availableIpp = availableIpp;
			this.ipPort = ipPort;
			this.latch = latch;
		}

		@Override
		public void run() {
			StringBuffer sb = new StringBuffer(500).append("尝试连接：").append(tryCount).append("\t");
			
			HttpClient httpClient = getHttpClient(ipPort.getIp(), ipPort.getPort());
			
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

				shutdown(httpClient);
				
				System.out.println(sb.toString());
			}
		}
	}
	
	private void shutdown(HttpClient httpClient){
		((SimpleHttpConnectionManager)httpClient.getHttpConnectionManager()).shutdown();
	}

	private HttpClient getHttpClient(String ip, int port) {
		HttpClient httpClient = new HttpClient();
		httpClient.getHostConfiguration().setProxy(ip, port);
		// httpclient.getParams().setAuthenticationPreemptive(true);//重要！！！告诉httpclient，使用抢先认证，否则你会收到“你没有资格”的恶果
		// 如果没有其他要设置的到这里就已经ok了
		// 需要验证
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
				"", "");

		httpClient.getState().setProxyCredentials(AuthScope.ANY, creds);
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout( 5 * 1000);	//连接超时
		httpClient.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5 * 1000);			//读取数据超时
		return httpClient;
	}
	
	private HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();

		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout( 5 * 1000);	//连接超时
		httpClient.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30 * 1000);		//读取数据超时
		return httpClient;
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
			HttpClient httpClient = getHttpClient(ipp.getIp(), ipp.getPort());

			try {
				StringBuffer sb = new StringBuffer(1000);
				sb.append("\n>>>>>>\n");
				
				for (Iterator<String> it = urlSet.iterator(); it.hasNext();) {
					String url = it.next();
					
					GetMethod gm = new GetMethod(url);
					httpClient.executeMethod(gm);
					sb.append(gm.getStatusCode()).append(":").append(url).append("\n");
				}
				
				System.out.println(sb.toString());
			} catch (HttpException e) {
//				e.printStackTrace();
			} catch (IOException e) {
//				e.printStackTrace();
			} finally {
				shutdown(httpClient);
				latch.countDown();
			}
		}
	}

	private void readIpPortFromTxt() {
		File file = new File("d:/availableproxyip.txt");
		if(file.exists() == false){
			file = new File("d:/proxyip.txt");
		}

		FileReader fr = null;
		BufferedReader br = null;

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line = null;
			while ((line = br.readLine()) != null) {
				if (null != line && line.length() > 0) {
					int index = line.indexOf(":");
					if (index > 0) {
						Pattern p = Pattern
								.compile("(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)");
						Matcher m = p.matcher(line);

						if (m.find()) {
							// System.out.println(m.group(1) + "  "
							// +m.group(2));
							String ip = m.group(1);
							String port = m.group(2);
							if (ip != null && ip.length() > 0 && port != null
									&& port.length() > 0) {
								IpPort ipp = new IpPort(ip,
										Integer.parseInt(port));
								ipportSet.add(ipp);
							}
						}
					}
				}
			}

			System.out.println("读取代理ip地址端口共 ：" + ipportSet.size());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void readProxyFromInternet() {
		String proxyWebSite = "http://www.cnproxy.com/proxy{0}.html";

		int page = random.nextInt(10) + 1;
		String proxyUrl = MessageFormat.format(proxyWebSite, page);

		if (proxyUrl.isEmpty() == false) {
			System.out.println("开始解析代理ip地址：" + proxyUrl);

			try {
				HttpClient client = getHttpClient();

				GetMethod get = new GetMethod(proxyUrl);

				client.executeMethod(get);
				if (get.getStatusCode() == 200) {
					InputStream is = get.getResponseBodyAsStream();
					InputStreamReader isr = new InputStreamReader(is, "GB2312");
					BufferedReader bufreader = new BufferedReader(isr);

					String str = null;
					while ((str = bufreader.readLine()) != null) {
//						System.out.println(str);
						Pattern p = Pattern
								.compile("<tr><td>(\\d+\\.\\d+\\.\\d+\\.\\d+).+document\\.write\\(\":\"(.+)\\)");
						Matcher m = p.matcher(str);
						if (m.find()) {
							String ip = m.group(1);

							String portS = m.group(2);
							portS = portS.replaceAll("\\+", "_");
//							System.out.println(portS);

							String[] s = portS.split("_");

							String port = getPort(s);

							IpPort ipp = new IpPort(ip, Integer.parseInt(port));

							ipportSet.add(ipp);
						}
					}

					// urlSet.addAll(temp);
					System.out.println("找到" + ipportSet.size() + "个ip与端口");
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private String getPort(String[] s) {
		String port = "";
		for(int i = 0; i < s.length; i++){
			String t = s[i];
			if(t != null && t.length() > 0){
				switch (t) {
				case "v":
					port += "3";
					break;
				case "m":
					port += "4";
					break;
				case "a":
					port += "2";
					break;
				case "l":
					port += "9";
					break;
				case "q":
					port += "0";
					break;
				case "b":
					port += "5";
					break;
				case "i":
					port += "7";
					break;
				case "w":
					port += "6";
					break;
				case "r":
					port += "8";
					break;
				case "c":
					port += "1";
					break;

				default:
					break;
				}
				//v("3"), m("4"), a("2"), l("9"), q("0"), b("5"), i("7"), w("6"), r("8"), c("1");
			}
		}
		return port;
	}

	private void readUrls() {
		File file = new File("d:/taobaourls.txt");

		FileReader fr = null;
		BufferedReader br = null;
		
		List<String> taobaoSearchUrlList = new ArrayList<>(5);

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line = null;
			while ((line = br.readLine()) != null) {
				if (null != line && line.length() > 0) {
					taobaoSearchUrlList.add(line);
				}
			}

			System.out.println("读取商品列表网址共：" + taobaoSearchUrlList.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(taobaoSearchUrlList.size() > 0){
			Set<String> urlSet = new HashSet<>(100);
			
			for(String searchUrl : taobaoSearchUrlList){
				System.out.println("读取：" + searchUrl);
				
				try {
					HttpClient client = getHttpClient();
					
					GetMethod get = new GetMethod(searchUrl);
					
					client.executeMethod(get);
					if(get.getStatusCode()==200){
						InputStream is = get.getResponseBodyAsStream();
						InputStreamReader isr = new InputStreamReader(is, "GB2312");
						BufferedReader bufreader = new BufferedReader(isr);
						
						String str = null;
						Set<String> temp = new HashSet<>(30);
						while((str = bufreader.readLine()) != null){
//							System.out.println(str);
							Pattern p = Pattern.compile("\"(http://item.taobao.com/item.htm\\?id=\\d+)\"");
							Matcher m = p.matcher(str);
							if(m.find()){
								temp.add(m.group(1));
							}
						}
						
						urlSet.addAll(temp);
						System.out.println("有" + temp.size() + "个地址");
					}
				} catch (HttpException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			System.out.println("添加主页。。。");
			for(String searchUrl : taobaoSearchUrlList){
				Pattern p = Pattern.compile("http://.+\\.taobao\\.com");
				Matcher m = p.matcher(searchUrl);
				
				if(m.find()){
					urlSet.add(m.group());
				}
			}
			
			System.out.println("共找到可访问的地址：" + urlSet.size());
			
			urlList.addAll(urlSet);
		}
	}

	private class IpPort {
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
	
	private enum PortCode {
		v("3"), m("4"), a("2"), l("9"), q("0"), b("5"), i("7"), w("6"), r("8"), c("1");
		
		
		private final String code;

		public String getCode() {
			return code;
		}

		PortCode(String code) {
			this.code = code;
		}
	}
}
