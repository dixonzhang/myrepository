package com.dixon.netty.netty.bufertest;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class BuferTest {
	
	public static void main(String[] args) {
		Person person = new BuferTest().new Person();
		person.setAge(10);
		person.setName("张dixon");
		person.setBirthTimesmtamp(new Date().getTime());
		
		try {
			ByteBuf bb = person.toBinary();
			
			Person p = new BuferTest().new Person();
			p.parse(bb);
			
			System.out.println(p);
			
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	private class Person{
		private int age;
		private String name;
		private long birthTimesmtamp;
		
		private int NAME_LENGTH = 60;
		
		public ByteBuf toBinary() throws UnsupportedEncodingException{
			ByteBuf bb = Unpooled.buffer(100);
			bb.writeInt(age);
			
			for(int i = 0; i < NAME_LENGTH-name.getBytes().length; i++){
				name += " ";
			}
			
			byte[] nameByte = Arrays.copyOf(getName().getBytes("utf-8"), NAME_LENGTH);
			
			bb.writeBytes(nameByte);
			bb.writeLong(birthTimesmtamp);
			
			return bb;
		}

		public void parse(ByteBuf bb) throws UnsupportedEncodingException{
			setAge(bb.readInt());
			byte[] nameByte = new byte[NAME_LENGTH];
			ByteBuf nameByteBuf = bb.readBytes(NAME_LENGTH);
			if(nameByteBuf.hasArray()){
				System.arraycopy(nameByteBuf.array(), nameByteBuf.arrayOffset(), nameByte, 0, NAME_LENGTH);
			}
			
			setName(new String(nameByte, "utf-8").trim());
			setBirthTimesmtamp(bb.readLong());
		}
		
		@Override
		public String toString() {
			return age + "\n" + name + "\n" + birthTimesmtamp;
		}
		
		public int getAge() {
			return age;
		}
		public void setAge(int age) {
			this.age = age;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public long getBirthTimesmtamp() {
			return birthTimesmtamp;
		}
		public void setBirthTimesmtamp(long birthTimesmtamp) {
			this.birthTimesmtamp = birthTimesmtamp;
		}
	}
}
