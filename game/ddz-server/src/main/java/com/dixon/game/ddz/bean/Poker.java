package com.dixon.game.ddz.bean;

import com.dixon.game.ddz.enu.ColourType;

public class Poker {
	private String name;
	private int num;
	private ColourType type;
	
	public Poker(ColourType type, int num){
		this.type = type;
		this.num = num;
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
		System.out.println(new Poker(ColourType.heitao, 2).getName());
	}
}
