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

public class ProxyTest4 {

	public static void main(String[] args) {
//		String proxyWebSite = "http://www.cnproxy.com/proxy{0}.html";

//		int page = random.nextInt(10) + 1;
//		String proxyUrl = MessageFormat.format(proxyWebSite, page);
		
		String proxyUrl = "http://yaoaii.taobao.com";

		if (proxyUrl.isEmpty() == false) {
			System.out.println("开始解析代理ip地址：" + proxyUrl);

			try {
				HttpClient client = getHttpClient(/*"23.94.44.10"*/"211.138.121.37", /*7808*/83);

				GetMethod get = new GetMethod(proxyUrl);

				client.executeMethod(get);
				if (get.getStatusCode() == 200) {
					InputStream is = get.getResponseBodyAsStream();
					InputStreamReader isr = new InputStreamReader(is, "GB2312");
					BufferedReader bufreader = new BufferedReader(isr);

					String str = null;
					while ((str = bufreader.readLine()) != null) {
//						System.out.println(str);
						/*Pattern p = Pattern
								.compile("<tr><td>(\\d+\\.\\d+\\.\\d+\\.\\d+).+document\\.write\\(\":\"(.+)\\)");
						Matcher m = p.matcher(str);
						if (m.find()) {
						}*/
						
						if(str.indexOf("spm") > 0)
							System.out.println(str);
					}

					// urlSet.addAll(temp);
				}
			} catch (HttpException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	private static HttpClient getHttpClient(String ip, int port) {
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

}
