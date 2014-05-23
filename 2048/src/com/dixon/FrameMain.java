package com.dixon;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class FrameMain extends JFrame{
	private static final long serialVersionUID = 6851192131927721483L;
	
	private Item[][] items;
	private Item[][] preItems = new Item[4][4];
	private Color[] colors = new Color[]{
			new Color(250, 255,240),
			new Color(255, 248,220),
			new Color(250, 240, 230),
			new Color(255, 222, 173),
			new Color(255, 99, 71),
			new Color(255, 127, 80),
			new Color(160, 102, 211),
			new Color(160, 32, 240),
			new Color(221, 160, 221),
			new Color(218, 112, 214),
			new Color(255, 0, 0),
			new Color(176, 23, 31),
			new Color(31, 139, 34),
			new Color(8, 46,84)
	};
	
	
	private int[] randomValue = {2,2,2,2,2,2,2,2,4,4};
	
	private JPanel panel = new JPanel();
	private int w = 250;
	private int h = 250;
	
	private Random random = new Random();
	
	public FrameMain(){
		setSize(w, h);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				copy();
				
				// 左
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					System.out.println("左");
					
					for(int i = 0; i < 4; i++){
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
				//下
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					System.out.println("下");
					for(int i = 0; i < 4; i++){
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
				//右
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					System.out.println("右");
					for(int i = 0; i < 4; i++){
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
				//上
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					System.out.println("上");
					for(int i = 0; i < 4; i++){
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
				
				if(doHaveMove() == false){
					/*print(preItems);
					print(items);
					continue;*/
					return;
				}
				
				//判断是否游戏结束
				boolean gameOver = true;
				for (int i = 0; i < 4; i++) {
					boolean isBreak = false;
					
					for (int j = 0; j < 4; j++) {
						if(items[i][j].getValue() == 0){
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
					return;
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
				
				print();
			}
		});
		
		// 初始化数组
		items = new Item[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Item item = new Item();
				items[i][j] = item;
			}
		}
		
		//随机生成两个初始值
		for(int i = 0; i < 2; i++){
			items[random.nextInt(4)][random.nextInt(4)].setValue(randomValue[random.nextInt(10)]);
		}
		
		panel = new showPanel();
		add(panel);
		
		setVisible(true);
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
	
	private void print(){
		panel.repaint();
	}
	
	public static void main(String[] args) {
		new FrameMain();
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
			if(value == 0)
				return new Color(220, 220, 220);
			else
				return colors[tt(value, 0)];
		}
	}
	
	private class showPanel extends JPanel{
		@Override
		public void paint(Graphics g) {
			this.setBackground(Color.GRAY);
			
			
			super.paint(g);
			
			Graphics2D g2 = (Graphics2D)g;
			
			int itemWith = w/5;
			int itemHeight = h/5;
			
			int gap = itemWith / 5;
			
			for (int i = 0; i < 4; i++) {
				StringBuffer printLine = new StringBuffer(20);
				for (int j = 0; j < 4; j++) {
					printLine.append(items[i][j].getValue()).append("\t");
				}
				
				System.out.println(printLine);
				System.out.println();
			}
			System.out.println("--------------------------------------");
			
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					g2.setColor(items[i][j].getColor());
					g2.fillRect(itemWith*j+(j+1)*gap, itemHeight*i+(i+1)*gap, itemWith, itemHeight);
					
					g2.setFont(new Font("宋体",Font.BOLD, 20));
					if(items[i][j].getValue()/1000 > 0)
						g2.setFont(new Font("宋体",Font.BOLD, 10));
					else if(items[i][j].getValue()/100 > 0)
						g2.setFont(new Font("宋体",Font.BOLD, 15));
					
					g2.setColor(Color.BLACK);
					g2.drawString(items[i][j].getValue()==0? "" : items[i][j].getValue()+"", itemWith*j+(j+1)*gap + itemWith/2-5, itemHeight*i+(i+1)*gap+5 + itemHeight/2);
				}
			}
		}
	}
	
	private void copy(){
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Item item = new Item(items[i][j].getValue());
				
				preItems[i][j] = item;
			}
		}
	}
	
	private int tt(double a, int n){
		if(a == 2)
			return n;
		else{
			n++;
			
			return tt(a/2, n);
		}
		
	}
}
