package com.dixon.game.ddz.bean;

import java.util.Vector;

import com.dixon.game.ddz.enu.ColourType;

public class Poker {
	private String name;
	private int num;
	private ColourType type;
	
	public Poker(){}
	
	public Poker(ColourType type, int num){
		this.type = type;
		this.num = num;
	}

	@Override
	public int hashCode() {
		int value = 0;
		
		if(type == ColourType.dawang)
			value = 1;
		else if(type == ColourType.xiaowang)
			value = 2;
		else if(type == ColourType.heitao)
			value = 100;
		else if(type == ColourType.hongxin)
			value = 200;
		else if(type == ColourType.meihua)
			value = 300;
		else if(type == ColourType.fangzhuan)
			value = 400;
		
		value += num;
		
		
		return value;
	}
	
	@Override
	public boolean equals(Object obj) {
		return getName().equals(((Poker)obj).getName());
	}
	
	public String getName() {
		if(type == ColourType.dawang)
			name = "大王";
		else if(type == ColourType.xiaowang)
			name = "小王";
		else{
			if(num == 1)
				name = type + " A";
			else if(num == 11)
				name = type + " J";
			else if(num == 12)
				name = type + " Q";
			else if(num == 13)
				name = type + " K";
			else
				name = type + " " + num;
		}
		
		return name;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int value) {
		this.num = value;
	}

	public ColourType getType() {
		return type;
	}

	public void setType(ColourType type) {
		this.type = type;
	}

	public String getImg() {
		return "";
	}
	
	public static void main(String[] args) {
//		System.out.println(new Poker(ColourType.heitao, 2).getName());
		
		Poker p11 = new Poker(ColourType.dawang, 0);
		Poker p12 = new Poker(ColourType.meihua, 2);
		Poker p13 = new Poker(ColourType.meihua, 3);
		
		Vector<Poker> v1 = new Vector<Poker>();
		v1.add(p11);
		v1.add(p12);
		v1.add(p13);
		
		Poker p21 = new Poker(ColourType.meihua, 2);
		Poker p22 = new Poker(ColourType.meihua, 3);
		
		Vector<Poker> v2 = new Vector<Poker>();
		v2.add(p21);
		v2.add(p22);
		
		v1.removeAll(v2);
		
		System.out.println(v1.size());
		System.out.println(v1.get(0).getName());
	}
}
