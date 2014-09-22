package com.dixon.game.ddz.common.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.dixon.game.ddz.common.enu.ColourType;

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
	
	/**
	 * 牌大小值
	 * 同一个num的值一样大，出王外
	 * @return
	 */
	public int getValue(){
		int value = 0;
		if(num >= 3 && num <= 13)
			value = num;
		else if(num == 0){//
			value = type == ColourType.dawang ? 17 : 16;
		}
		else if(num == 1){
			value = 14;
		}
		else if(num == 2){
			value = 15;
		}
		
		return value;
	}
	
	public String getName() {
		if(type == ColourType.dawang)
			name = "dawang";
		else if(type == ColourType.xiaowang)
			name = "xiaowang";
		else{
			if(num == 1)
				name = type + "_a";
			else if(num == 11)
				name = type + "_j";
			else if(num == 12)
				name = type + "_q";
			else if(num == 13)
				name = type + "_k";
			else
				name = type + "" + num;
		}
		
		return name;
	}

	/**
	 * 获取所有牌名
	 * @return
	 */
	public static List<String> getAllName(){
		List<String> list = new ArrayList<String>(54);
		for(ColourType ct : ColourType.values()){
			if(ct == ColourType.dawang)
				list.add(ColourType.dawang.toString());
			else if(ct == ColourType.xiaowang)
				list.add(ColourType.xiaowang.toString());
			else{
				
				for(int num = 1; num <=13; num++){
					if(num == 1)
						list.add(ct + "_a");
					else if(num == 11)
						list.add(ct + "_j");
					else if(num == 12)
						list.add(ct + "_q");
					else if(num == 13)
						list.add(ct + "_k");
					else
						list.add(ct + "" + num);
				}
			}
		}
		return list;
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
