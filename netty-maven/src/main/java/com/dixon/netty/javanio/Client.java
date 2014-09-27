package com.dixon.netty.javanio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Client {

	public static void main(String[] args) {
		try {
			final SocketChannel socketChannel = SocketChannel.open();
			Socket socket = socketChannel.socket();
			
			InetSocketAddress address = new InetSocketAddress(/*8008*/8899);
			
			socket.connect(address);
			socketChannel.configureBlocking(false);
			
			Selector selector = Selector.open();
			
			//这里给sessionkey分配一个缓存， key.attachment()可以返回，否者=null， 
			socketChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE, ByteBuffer.allocate(100));
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					for(int i = 0 ; i < 10; i++){
						String hello = "hello , i'm client    ";
						try {
							hello += i;
							ByteBuffer bb = ByteBuffer.allocate(hello.length()).put(hello.getBytes());
							bb.flip();
						
							socketChannel.write(bb);
							bb.clear();
							
							Thread.sleep(1000);
						} catch (IOException | InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
			
			
			
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
						if(key.isWritable()){
//							SocketChannel client = (SocketChannel) key.channel();
//							ByteBuffer output = (ByteBuffer) key.attachment();
//							if(output != null){
//								output.flip();
//								client.write(output);
//								output.compact();
//								
//							}
						}
						
						if(key.isReadable()){
							System.out.println("read");
							SocketChannel client = (SocketChannel) key.channel();
							ByteBuffer bb2 = (ByteBuffer) key.attachment();
							if(client.read(bb2) != -1){
								System.out.println("receive data : " + new String(bb2.array(), "utf-8"));
								bb2.clear();
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
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
}
