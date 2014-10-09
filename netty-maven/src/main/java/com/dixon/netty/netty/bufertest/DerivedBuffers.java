package com.dixon.netty.netty.bufertest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

/**
 * 派生出来的buffer
 * @author Administrator
 *
 */
public class DerivedBuffers {

	public static void main(String[] args) {
		//slice
		Charset utf8 = Charset.forName("UTF-8");
		ByteBuf buf = Unpooled.copiedBuffer("Netty in Action rocks!", utf8);
		ByteBuf sliced = buf.slice(0, 14);
		System.out.println(sliced.toString(utf8));
		buf.setByte(0, (byte) 'J');
		System.out.println(buf.getByte(0) == sliced.getByte(0));//共享内存
		
		//比较
		//duplicate() 跟 slice()的唯一区别就是，duplicate保持原buffer的readerIndex跟writerIndex
		ByteBuf buf2 = Unpooled.copiedBuffer("abcde", utf8);
		buf2.readByte();
		System.out.println("buf2 : readerindex=" + buf2.readerIndex() + " writerIndex=" + buf2.writerIndex()
				+ " readableBytes=" + buf2.readableBytes());
		
		ByteBuf sliced2 = buf2.slice();
		ByteBuf duplicated = buf2.duplicate();
		
		System.out.println(sliced2.toString(utf8));
		System.out.println("sliced2 : readerindex=" + sliced2.readerIndex() + " writerIndex=" + sliced2.writerIndex()
				+ " readableBytes=" + sliced2.readableBytes());
		System.out.println((char)sliced2.getByte(0));
		
		
		System.out.println(duplicated.toString(utf8));
		System.out.println("duplicated : readerindex=" + duplicated.readerIndex() + " writerIndex=" + duplicated.writerIndex()
				+ " readableBytes=" + duplicated.readableBytes());
		System.out.println((char)duplicated.getByte(0));
		
		
		//copy
		ByteBuf buf3 = Unpooled.copiedBuffer("abcde", utf8);
		ByteBuf copyed = buf3.copy();
		
		buf3.setByte(0, (byte) 'J');
		System.out.println(buf3.getByte(0) != copyed.getByte(0));
		
		
		
		
	}

}
