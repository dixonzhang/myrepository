package com.dixon.netty.netty.bufertest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class ReadableBytes {

	public static void main(String[] args) {
		ByteBuf buffer = Unpooled.wrappedBuffer("abcde".getBytes());
		while (buffer.readableBytes() > 0) {
			byte b = buffer.readByte();
			System.out.println((char)b);
		}
		
		System.out.println(buffer.readableBytes() == 0);
	}

}
