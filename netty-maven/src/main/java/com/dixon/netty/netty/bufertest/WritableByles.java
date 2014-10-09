package com.dixon.netty.netty.bufertest;

import java.util.Random;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class WritableByles {

	public static void main(String[] args) {
		ByteBuf bb = Unpooled.buffer(4*12);
		Random random = new Random();
		while(bb.writableBytes() >= 4){//一个整型=4个字节
			bb.writeInt(random.nextInt(10));
		}
		
		System.out.println(bb.readableBytes() == 48);
		
		while(bb.readableBytes() > 0){
			System.out.println(bb.readInt());
		}
	}

}
