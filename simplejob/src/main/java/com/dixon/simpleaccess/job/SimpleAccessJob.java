package com.dixon.simpleaccess.job;

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
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.stereotype.Service;

import com.dixon.utils.HttpClientUtil;

@Service
public class SimpleAccessJob {
	@Resource
	private Executor accessExecutor;
	
	public void doJob() {
		Random random = new Random();
		
		List<String> urlList = readUrls();
		
		int index = 0;
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
			
			accessExecutor.execute(new Task(accessUrlSet, index));
			index++;
		}
		
	}
	
	private List<String> readUrls() {
		File file = new File("d:/taobaospmurls.txt");

		FileReader fr = null;
		BufferedReader br = null;
		
		List<String> urlList = new ArrayList<String>(100);

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
	
	private class Task implements Runnable {
		private Set<String> urlSet;
		private int index;

		private Task(Set<String> urlSet, int index) {
			this.urlSet = urlSet;
			this.index = index;
		}

		@Override
		public void run() {
			HttpClient httpClient = HttpClientUtil.getHttpClient();

			try {
				StringBuffer sb = new StringBuffer(1000);
				sb.append("\n>>>>>>访问第" + index + "\n");
				
				for (Iterator<String> it = urlSet.iterator(); it.hasNext();) {
					String url = it.next();
					
					GetMethod gm = new GetMethod(url);
					httpClient.executeMethod(gm);
					sb.append(gm.getStatusCode()).append(":").append(url).append("\n");
				}
				
				System.out.println(sb.toString());
			} catch (HttpException e) {
			} catch (IOException e) {
			} finally {
				HttpClientUtil.shutdown(httpClient);
			}
		}
	}
}
