package com.dixon.proxyaccess.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.stereotype.Service;

import com.dixon.proxyaccess.bean.IpPort;
import com.dixon.utils.HttpClientUtil;

@Service
public class ProxyAccessService implements IProxyAccessService{
	private Logger logger = Logger.getLogger(getClass().getName());

	@Override
	public Set<IpPort> readProxyFromInternet() {
		String proxyWebSite = "http://www.cnproxy.com/proxy{0}.html";

		Set<IpPort> ipportSet = new HashSet<IpPort>(100);
		Random random = new Random();

		int page = random.nextInt(10) + 1;
		String proxyUrl = MessageFormat.format(proxyWebSite, page);

		logger.info("开始解析代理ip地址：" + proxyUrl);

		try {
			HttpClient client = HttpClientUtil.getHttpClient();

			GetMethod get = new GetMethod(proxyUrl);

			client.executeMethod(get);
			if (get.getStatusCode() == 200) {
				InputStream is = get.getResponseBodyAsStream();
				InputStreamReader isr = new InputStreamReader(is, "GB2312");
				BufferedReader bufreader = new BufferedReader(isr);

				String str = null;
				while ((str = bufreader.readLine()) != null) {
					// logger.info(str);
					Pattern p = Pattern
							.compile("<tr><td>(\\d+\\.\\d+\\.\\d+\\.\\d+).+document\\.write\\(\":\"(.+)\\)");
					Matcher m = p.matcher(str);
					if (m.find()) {
						String ip = m.group(1);

						String portS = m.group(2);
						portS = portS.replaceAll("\\+", "_");
						// logger.info(portS);

						String[] s = portS.split("_");

						String port = getPort(s);

						IpPort ipp = new IpPort(ip, Integer.parseInt(port));

						ipportSet.add(ipp);
					}
				}

				HttpClientUtil.shutdown(client);
				logger.info("找到" + ipportSet.size() + "个ip与端口");
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ipportSet;
	}

	@Override
	public List<String> readUrls() {
		List<String> urlList = new ArrayList<String>(100);
		
		List<String> taobaoSearchUrlList = new ArrayList<String>(3);
		taobaoSearchUrlList.add("http://yaoaii.taobao.com/search.htm?spm=a1z10.1.w5002-1566833049.1.1gKgR0&search=y");
		
		
		if(taobaoSearchUrlList.size() > 0){
			Set<String> urlSet = new HashSet<String>(100);
			
			HttpClient client = HttpClientUtil.getHttpClient();
			for(String searchUrl : taobaoSearchUrlList){
				logger.info("读取：" + searchUrl);
				
				try {
					
					GetMethod get = new GetMethod(searchUrl);
					
					client.executeMethod(get);
					if(get.getStatusCode()==200){
						InputStream is = get.getResponseBodyAsStream();
						InputStreamReader isr = new InputStreamReader(is, "GB2312");
						BufferedReader bufreader = new BufferedReader(isr);
						
						String str = null;
						Set<String> temp = new HashSet<String>(30);
						while((str = bufreader.readLine()) != null){
//							logger.info(str);
							Pattern p = Pattern.compile("\"(http://item.taobao.com/item.htm\\?id=\\d+)\"");
							Matcher m = p.matcher(str);
							if(m.find()){
								temp.add(m.group(1));
							}
						}
						
						urlSet.addAll(temp);
						logger.info("有" + temp.size() + "个地址");
					}
				} catch (HttpException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			HttpClientUtil.shutdown(client);
			
			logger.info("添加主页。。。");
			for(String searchUrl : taobaoSearchUrlList){
				Pattern p = Pattern.compile("http://.+\\.taobao\\.com");
				Matcher m = p.matcher(searchUrl);
				
				if(m.find()){
					urlSet.add(m.group());
				}
			}
			
			logger.info("共找到可访问的地址：" + urlSet.size());
			
			urlList.addAll(urlSet);
		}
		
		return urlList;
	}
	
	private String getPort(String[] s) {
		String port = "";
		for(int i = 0; i < s.length; i++){
			String t = s[i];
			if(t != null && t.length() > 0){
				switch (t.charAt(0)) {
				case 'v':
					port += "3";
					break;
				case 'm':
					port += "4";
					break;
				case 'a':
					port += "2";
					break;
				case 'l':
					port += "9";
					break;
				case 'q':
					port += "0";
					break;
				case 'b':
					port += "5";
					break;
				case 'i':
					port += "7";
					break;
				case 'w':
					port += "6";
					break;
				case 'r':
					port += "8";
					break;
				case 'c':
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
}
