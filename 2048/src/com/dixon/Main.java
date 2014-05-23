package com.dixon;

import java.awt.Color;
import java.util.Random;
import java.util.Scanner;

public class Main {
	private Item[][] items;
	private Item[][] preItems = new Item[4][4];;
	
	private int[] randomValue = {2,2,2,2,2,2,2,2,4,4};
	
	
	public Main() {
		// 初始化数组
		items = new Item[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Item item = new Item();
				items[i][j] = item;
			}
		}
		
		//随机生成两个初始值
		Random random = new Random();
		for(int i = 0; i < 2; i++){
			items[random.nextInt(4)][random.nextInt(4)].setValue(randomValue[random.nextInt(10)]);
		}
		
		print(items);
		
		Scanner scanner = new Scanner(System.in);
		System.out.println("1=左， 2=下， 3=右， 4=上");
		
		while (scanner.hasNext()) {

			//移动合并
			String input = scanner.next();
			
			copy();
			
			if ("1".equals(input)) {
				for(int i = 0; i < 4; i++){
					//当前元素与后一个元素相加，如果两元素其中一个等于0或者两元素相等，说明可合并，则将两元素的和设置为当前元素的值，而后一元素置0
					/*for(int t = 0; t < 3; t++){
						for(int j = 0; j < 3; j++){
							int current = items[i][j].getValue();
							int after = items[i][j+1].getValue();
							
							if(current == 0 || after == 0 || current == after){
								items[i][j].setValue(current + after);
								items[i][j+1].setValue(0);
							}
							
						}
					}*/
					
					for(int j = 0; j < 4; j++){
						//如果当前元素为0，则往后找不0的元素，并交换值
						if(items[i][j].getValue() == 0){
							for(int k = j + 1; k < 4; k++){
								if(items[i][k].getValue() != 0){
									items[i][j].setValue(items[i][k].getValue());
									items[i][k].setValue(0);
									break;
								}
							}
						}
						
						//如果当前元素还等于0，则结束循环
						if(items[i][j].getValue() == 0)
							break;
						
						
						for(int k = j + 1; k < 4; k++){
							if(items[i][k].getValue() == 0)
								continue;
							
							if(items[i][j].getValue() == items[i][k].getValue()){
								items[i][j].setValue(2*items[i][j].getValue());
								items[i][k].setValue(0);
								break;
							}
							if(items[i][j].getValue() != items[i][k].getValue())
								break;
						}
					}
				}
			}
			else if ("2".equals(input)) {
				for(int i = 0; i < 4; i++){
					/*//当前元素与后一个元素相加，如果两元素其中一个等于0或者两元素相等，说明可合并，则将两元素的和设置为当前元素的值，而后一元素置0
					for(int t = 0; t < 3; t++){
						for(int j = 3; j > 0; j--){
							int current = items[j][i].getValue();
							int after = items[j-1][i].getValue();
							
							if(current == 0 || after == 0 || current == after){
								items[j][i].setValue(current + after);
								items[j-1][i].setValue(0);
							}
						}
					}*/
					
					for(int j = 3; j >= 0; j--){
						//如果当前元素为0，则往后找不0的元素，并交换值
						if(items[j][i].getValue() == 0){
							for(int k = j - 1; k >= 0; k--){
								if(items[k][i].getValue() != 0){
									items[j][i].setValue(items[k][i].getValue());
									items[k][i].setValue(0);
									break;
								}
							}
						}
						
						//如果当前元素还等于0，则结束循环
						if(items[j][i].getValue() == 0)
							break;
						
						
						for(int k = j - 1; k >= 0; k--){
							if(items[k][i].getValue() == 0)
								continue;
							
							if(items[j][i].getValue() == items[k][i].getValue()){
								items[j][i].setValue(2*items[j][i].getValue());
								items[k][i].setValue(0);
								break;
							}
							
							if(items[j][i].getValue() != items[k][i].getValue())
								break;
						}
					}
				}
			}
			else if ("3".equals(input)) {
				for(int i = 0; i < 4; i++){
					/*//当前元素与后一个元素相加，如果两元素其中一个等于0或者两元素相等，说明可合并，则将两元素的和设置为当前元素的值，而后一元素置0
					for(int t = 0; t < 3; t++){
						for(int j = 3; j > 0; j--){
							int current = items[i][j].getValue();
							int after = items[i][j-1].getValue();
							
							if(current == 0 || after == 0 || current == after){
								items[i][j].setValue(current + after);
								items[i][j-1].setValue(0);
							}
						}
					}*/
					
					for(int j = 3; j >= 0; j--){
						//如果当前元素为0，则往后找不0的元素，并交换值
						if(items[i][j].getValue() == 0){
							for(int k = j - 1; k >= 0; k--){
								if(items[i][k].getValue() != 0){
									items[i][j].setValue(items[i][k].getValue());
									items[i][k].setValue(0);
									break;
								}
							}
						}
						
						//如果当前元素还等于0，则结束循环
						if(items[i][j].getValue() == 0)
							break;
						
						
						for(int k = j - 1; k >= 0; k--){
							if(items[i][k].getValue() == 0)
								continue;
							
							if(items[i][j].getValue() == items[i][k].getValue()){
								items[i][j].setValue(2*items[i][j].getValue());
								items[i][k].setValue(0);
								break;
							}
							
							if(items[i][j].getValue() != items[i][k].getValue())
								break;
						}
					}
				}
			}
			else if ("4".equals(input)) {
				for(int i = 0; i < 4; i++){
					/*//当前元素与后一个元素相加，如果两元素其中一个等于0或者两元素相等，说明可合并，则将两元素的和设置为当前元素的值，而后一元素置0
					for(int t = 0; t < 3; t++){
						
						for(int j = 0; j < 3; j++){
							int current = items[j][i].getValue();
							int after = items[j+1][i].getValue();
							
							if(current == 0 || after == 0 || current == after){
								items[j][i].setValue(current + after);
								items[j+1][i].setValue(0);
							}
						}
					}*/
					
					for(int j = 0; j < 4; j++){
						//如果当前元素为0，则往后找不0的元素，并交换值
						if(items[j][i].getValue() == 0){
							for(int k = j + 1; k < 4; k++){
								if(items[k][i].getValue() != 0){
									items[j][i].setValue(items[k][i].getValue());
									items[k][i].setValue(0);
									break;
								}
							}
						}
						
						//如果当前元素还等于0，则结束循环
						if(items[j][i].getValue() == 0)
							break;
						
						
						for(int k = j + 1; k < 4; k++){
							if(items[k][i].getValue() == 0)
								continue;
							
							if(items[j][i].getValue() == items[k][i].getValue()){
								items[j][i].setValue(2*items[j][i].getValue());
								items[k][i].setValue(0);
								break;
							}
							
							if(items[j][i].getValue() != items[k][i].getValue())
								break;
						}
					}
				}
				
			}
			else{
				System.out.println("无效操作");
				continue;
			}
			
			
			if(doHaveMove() == false){
				print(preItems);
				print(items);
				continue;
			}
			
			//判断是否游戏结束
			boolean gameOver = true;
			for (int i = 0; i < 4; i++) {
				boolean isBreak = false;
				
				for (int j = 0; j < 4; j++) {
					if(items[i][j].getValue() == 0){
//						items[i][j].setValue(randomValue[random.nextInt(10)]);
						gameOver = false;
						isBreak = true;
						break;
					}
				}
				
				if(isBreak)
					break;
			}
			
			if(gameOver){
				System.out.println("game over!!!!");
				break;
			}
			
			//随机将value=0的元素置值
			while(true){
				int i = random.nextInt(4);
				int j = random.nextInt(4);
				if(items[i][j].getValue() == 0){
					items[i][j].setValue(randomValue[random.nextInt(10)]);
					break;
				}
			}
			
			print(items);
		}

		scanner.close();
		
		/**
		 * 2 2 2 2
		 * 2 2 0 4
		 * 2 0 2 4
		 * 0 2 2 4
		 * 
		 * 
		 */
	}
	
	private void copy(){
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Item item = new Item(items[i][j].getValue());
				
				preItems[i][j] = item;
			}
		}
	}
	
	private boolean doHaveMove(){
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if(preItems[i][j].getValue() != items[i][j].getValue())
					return true;
			}
		}
		
		return false;
	}
	
	private void print(Item[][] items){
		for (int i = 0; i < 4; i++) {
			StringBuffer printLine = new StringBuffer(20);
			for (int j = 0; j < 4; j++) {
				printLine.append(items[i][j].getValue()).append("\t");
			}
			
			System.out.println(printLine);
			System.out.println();
		}
		System.out.println("--------------------------------------");
	}
	
	public static void main(String[] args) {
		new Main();
	}
	
	private class Item{
		private int value = 0;
		private Color color;
		
		public Item(){
		}
		public Item(int value){
			this(value, null);
		}
		public Item(int value, Color color){
			this.value = value;
			this.color = color;
		}
		public int getValue() {
			return value;
		}
		public void setValue(int value) {
			this.value = value;
		}
		public Color getColor() {
			return color;
		}
	}
}
