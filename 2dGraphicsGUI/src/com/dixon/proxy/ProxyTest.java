package com.dixon.proxy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

public class ProxyTest {
	/**
	 * 对代理进行初始设置
	 * 
	 * @param host
	 *            代理服务器的IP
	 * @param port
	 *            代理服务器的端口
	 * @param username
	 *            连接代理服务器的用户名
	 * @param password
	 *            连接代理服务器的密码
	 */
	public static void initProxy(String host, int port, final String username,

	final String password) {
		/*// 设置一个默认的验证器
		Authenticator.setDefault(new Authenticator() {

			protected PasswordAuthentication getPasswordAuthentication() {

				return new PasswordAuthentication(username,

				new String(password).toCharArray());

			}

		});*/
		// 设置对HTTP进行代理,key可以写成http.proxyXxxx或proxyXxxx两种形式
		System.setProperty("http.proxyType", "4");

		System.setProperty("http.proxyPort", Integer.toString(port));

		System.setProperty("http.proxyHost", host);

		System.setProperty("http.proxySet", "true");

		// 设置对FTP进行代理
		System.setProperty("ftpProxyPort", Integer.toString(port));

		System.setProperty("ftpProxyHost", host);

		System.setProperty("ftpProxySet", "true");

	}

	public static void main(String[] args) throws IOException {

		// String ftpurl = "ftp://204.2.225.157/favicon.ico";
		String ftpurl = "ftp://204.2.225.157/robots.txt";
		String httpurl = "http://www.baidu.com";

		String proxy = "121.22.29.182";// 代理服务器IP

		int port = 80;// 代理服务器端口

		String username = "footmark";// 连接代理服务器的用户名

		String password = "market5156";// 连接代理服务器的密码

		String temp = "D:/temp";// 存放文件的临时目录

		initProxy(proxy, port, username, password);

//		test(ftpurl, temp);
		test(httpurl, temp);

	}

	/**
	 * 通过代理服务器访问外网HTTP或FTP下载文件
	 * 
	 * @param url
	 *            外网地址
	 * @param dir
	 *            存放下载文件的目录
	 * @throws IOException
	 */
	private static void test(String url, String dir) throws IOException {
		URL server = new URL(url);
//		String file = server.getFile();
//		if (file.equals(""))
//			file = "/temp.txt";
//		String path = dir + file;
		URLConnection connection = server.openConnection();
		connection.connect();
		connection.setReadTimeout(5000);
		
		InputStream io = connection.getInputStream();
		
		byte[] b = new byte[1024];
		
		System.out.println(io.read(b));
		
		System.out.println(new String(b));
		
		io.close();
		
		/*InputStream is = connection.getInputStream();
		OutputStream os = new FileOutputStream(path);
		// 使用流实现对二进制文件的读写,亦可读写文本文件,但速度较慢
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedOutputStream bos = new BufferedOutputStream(os);
		byte[] buf = new byte[is.available()];
		bis.read(buf);
		bos.write(buf);
		bis.close();
		bos.close();*/
		// 实现对文本文件的读写
		// BufferedReader reader = new BufferedReader(new
		// InputStreamReader(is));
		// BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(os));
		// System.out.println(is.available());
		// char[] buf=new char[is.available()];
		// reader.read(buf);
		// writer.write(buf);
		// reader.close();
		// os.flush();
		// writer.close();
	}
}
