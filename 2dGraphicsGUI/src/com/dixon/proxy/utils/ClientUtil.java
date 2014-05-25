package com.dixon.proxy.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class ClientUtil {
	public static HttpClient getHttpClient(String ip, int port) {
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
	
	public static HttpClient getHttpClient() {
		HttpClient httpClient = new HttpClient();

		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout( 5 * 1000);	//连接超时
		httpClient.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 30 * 1000);		//读取数据超时
		return httpClient;
	}
	
	public static void shutdown(HttpClient httpClient){
		((SimpleHttpConnectionManager)httpClient.getHttpConnectionManager()).shutdown();
	}
}
