package com.dixon.proxy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;

import com.dixon.proxy.utils.ClientUtil;

public class ProxyTest4 {
	
	private static ThreadPoolExecutor executor = new ThreadPoolExecutor(30, 50, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(200));

	public static void main(String[] args) {
		Random random = new Random();
		
		List<String> urlList = readUrls();
		
		for(String url : urlList){
			
			Set<String> accessUrlSet = new HashSet<String>(10);
			accessUrlSet.add(url);
			
			if(url.indexOf("spm") > 0){
				for(int i = 0; i < 9; i++){
					Pattern p = Pattern.compile("a\\d+z\\d+\\.\\d+");
					Matcher m = p.matcher(url);
					
					StringBuffer sb = new StringBuffer(30);
					
					if(m.find()){
						m.appendReplacement(sb, "a" + random.nextInt(10) + "z"
								+ random.nextInt(15) + "." + random.nextInt(10));
					}
					m.appendTail(sb);
					
//					System.out.println(sb);
					accessUrlSet.add(sb.toString());
				}
			}
			
			executor.execute(new Task(accessUrlSet, null));
		}
	}

	
	private static List<String> readUrls() {
		File file = new File("d:/taobaospmurls.txt");

		FileReader fr = null;
		BufferedReader br = null;
		
		List<String> urlList = new ArrayList<>(100);

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line = null;
			while ((line = br.readLine()) != null) {
				if (null != line && line.length() > 0) {
					urlList.add(line);
				}
			}

			System.out.println("读取商品列表网址共：" + urlList.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return urlList;
	}
	
	private static class Task implements Runnable {
		private Set<String> urlSet;
		private CountDownLatch latch;

		private Task(Set<String> urlSet, CountDownLatch latch) {
			this.urlSet = urlSet;
			this.latch = latch;
		}

		@Override
		public void run() {
			HttpClient httpClient = ClientUtil.getHttpClient();

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
				ClientUtil.shutdown(httpClient);
				if(latch != null)
					latch.countDown();
			}
		}
	}
}
