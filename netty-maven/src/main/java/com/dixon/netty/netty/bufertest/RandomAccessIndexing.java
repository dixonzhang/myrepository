package com.dixon.netty.netty.bufertest;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 通过index访问数据
 *  0 <= index <= capacity-1
 * @author dixon
 */
public class RandomAccessIndexing {
	public static void main(String[] args) {
		ByteBuf buffer = Unpooled.wrappedBuffer("abcde".getBytes());
		
		for (int i = 0; i < buffer.capacity(); i++) {
			byte b = buffer.getByte(i);
			System.out.println((char) b);
		}
		
		System.out.println("使用getxxx(index)方法获取的话， 不会推进readerIndex或者writerIndexx,"
				+ "使用readxxx()方法的话会推进； 可使用readerIndex(index)或writerIndex(index)设置readerIndex或者writerIndexx");
		System.out.println(buffer.readerIndex());//=0
		
		//readxxxx() 推进
		ByteBuf buffer2 = Unpooled.wrappedBuffer("abcde".getBytes());
		
		for (int i = 0; i < 2; i++) {
			byte b = buffer2.readByte();
			System.out.println((char)b);
		}
		System.out.println(buffer2.readerIndex());//推进了 =2
		
		//readerIndex(index)
		ByteBuf buffer3 = Unpooled.wrappedBuffer("abcde".getBytes());
		
		buffer3.readerIndex(3);
		for (int i = buffer3.readerIndex(); i < buffer3.capacity(); i++) {
			byte b = buffer3.getByte(i);
			System.out.println((char) b);
		}
		
		
	}
}
