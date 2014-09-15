package com.dixon.proxy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

public class ProxyTest2 {
	public static void main(String[] args) {
		DefaultHttpClient httpClient = new DefaultHttpClient();

		try {
			// 设置代理开始。如果代理服务器需要验证的话，可以修改用户名和密码
			// 192.168.1.107为代理地址 808为代理端口
			// UsernamePasswordCredentials后的两个参数为代理的用户名密码
			/*httpClient.getCredentialsProvider().setCredentials(
					new AuthScope("23.94.44.10", 7808),
					new UsernamePasswordCredentials("", ""));
			
			
			HttpHost proxy = new HttpHost("121.22.29.182", 80);
			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);*/
			// 设置代理结束
			/*HttpGet get = new HttpGet("http://shop107006182.taobao.com/search.htm?spm=a1z10.1.w5002-4287991988.1.w3Jjn4&search=y");*/
//			HttpGet get = new HttpGet("http://yaoaii.taobao.com/search.htm?spm=a1z10.1.w5002-1566833049.1.OLPHZW&search=y");
//			HttpGet get = new HttpGet("http://shop70224883.taobao.com/search.htm?spm=a1z10.1.w5002-1570223877.1.eKWShE&search=y");
			HttpGet get = new HttpGet("http://yaoaii.taobao.com");
			
			HttpResponse response = httpClient.execute(get);
			// 打印出状态码
			System.out.println(response.getStatusLine());
			// 获得返回的内容，循环遍历出
			HttpEntity entity = response.getEntity();
			String str = null;
			if (entity != null) {
				InputStream instream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(instream, "GB2312"));
				
				Set<String> set = new HashSet<>();
				
				while ((str = reader.readLine()) != null) {
//					System.out.println(str);
//					"http://item.taobao.com/item.htm?id=38946493285";
					
					/*Pattern p = Pattern.compile("\"(http://item.taobao.com/item.htm\\?id=\\d+)\"");
					Matcher m = p.matcher(str);
					if(m.find()){
//						System.out.println(m.group(1));
						set.add(m.group(1));
					}*/
					if(str.indexOf("spm") > 0)
						System.out.println(str);
					
				}
				instream.close();
				reader.close();
				
				for(Iterator<String> it = set.iterator(); it.hasNext(); ){
					System.out.println(it.next());
				}
				
				System.out.println(set.size());
				
			}
			// 遍历内容结束
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
