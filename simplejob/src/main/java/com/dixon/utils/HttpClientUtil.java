package com.dixon.utils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.params.HttpMethodParams;

public class HttpClientUtil {
	public static HttpClient getHttpClient(String ip, int port) {
		HttpClient httpClient = new HttpClient();
		httpClient.getHostConfiguration().setProxy(ip, port);
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
