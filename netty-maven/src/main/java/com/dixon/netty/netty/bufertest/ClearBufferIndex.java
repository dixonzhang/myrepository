package com.dixon.netty.netty.bufertest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ClearBufferIndex {

	public static void main(String[] args) {
		ByteBuf buffer = Unpooled.wrappedBuffer("abcde".getBytes());
		for (int i = 0; i < 2; i++) {
			byte b = buffer.readByte();
			System.out.println((char)b);
		}
		System.out.println("bufore clear : readerindex=" + buffer.readerIndex() + " writerIndex=" + buffer.writerIndex()
				+ " readableBytes=" + buffer.readableBytes());
		
		System.out.println(buffer.array().length);//原有的字节数组不会清空
		
		buffer.clear();
		
		System.out.println("after clear : readerindex=" + buffer.readerIndex() + " writerIndex=" + buffer.writerIndex()
				+ " readableBytes=" + buffer.readableBytes());
		
		System.out.println(buffer.array().length);
	}

}
