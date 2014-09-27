package com.dixon.netty.javanio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Server {
	public static void main(String[] args) {
		ServerSocketChannel serverChannel;
		try {
			serverChannel = ServerSocketChannel.open();
			ServerSocket serverSocket = serverChannel.socket();
			
			InetSocketAddress address = new InetSocketAddress(8008);
			serverSocket.bind(address);
			
			serverChannel.configureBlocking(false);
			
			Selector selector = Selector.open();
			
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			
			while(true){
				try {
					selector.select();
					
				} catch (Exception e) {
					break;
				}
				
				for(Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
					SelectionKey key = it.next();
					it.remove();
					
					try{
						if(key.isAcceptable()){
							ServerSocketChannel server =  (ServerSocketChannel) key.channel();
							SocketChannel client = server.accept();
							
							System.out.println("Accepted connection from " +
									client);
							
							client.configureBlocking(false);
							client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, ByteBuffer.allocate(100));
							
//							String hello = "hello";
//							
//							ByteBuffer bb = ByteBuffer.allocate(hello.length()).put(hello.getBytes());
//							bb.flip();
//							
//							client.write(bb);
						}
						
						//监听可写事件
						if(key.isWritable()){
//							SocketChannel client = (SocketChannel) key.channel();
//							ByteBuffer output = (ByteBuffer) key.attachment();
//							output.flip();
//							client.write(output);
//							output.compact();
						}
						
						//可读时处理
						if(key.isReadable()){
							System.out.println("is readable");
							SocketChannel client = (SocketChannel) key.channel();
							ByteBuffer byteb = (ByteBuffer) key.attachment();
							if(client.read(byteb) != -1){
								System.out.println(new String(byteb.array(), "utf-8"));
//									byteb.clear();
								
								byteb.flip();
								client.write(byteb);
								byteb.clear();
							}
						}
						
					}catch(Exception e){
						e.printStackTrace();
						key.cancel();
						try {
							key.channel().close();
						} catch (Exception e2) {
							// TODO: handle exception
						}
					}
					
				}
				
				
			}
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
}
