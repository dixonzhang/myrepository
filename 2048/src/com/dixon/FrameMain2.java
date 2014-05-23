package com.dixon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

public class FrameMain2 extends JFrame {
	private static final long serialVersionUID = 6851192131927721483L;
	
	//undo
	private Stack<Item[][]> undoStack = new Stack<>();
	private JButton undoButton = null;
	private JLabel gameover = new JLabel("game over!");
	
	private int gou = 0, god = 0, gol = 0, gor = 0; 
	
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
	
	private JPanel panel = null;
	private ColorTableModel tableModel = null;
	
	private JFrame f = null;
	
	private int w = 250;
	private int h = 250;
	
	private Random random = new Random();
	
	public FrameMain2(){
		setTitle("2048");
		setSize(w, h);
		f = this;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(1);
			}
		});
		
		addKeyListener(new KeyListerner());
		
		panel = new ShowPanel();
		
		JPanel undoPanel = new controlPanel();
		
		add(panel, BorderLayout.CENTER);
		add(undoPanel, BorderLayout.NORTH);
		
		pack();
		
		setVisible(true);
		this.requestFocus();//不加，key没反应
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
		undoButton.setEnabled(undoStack.size() > 0);
			
		tableModel.fireTableDataChanged();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//随机将value=0的元素置值
				while(true){
					int i = random.nextInt(4);
					int j = random.nextInt(4);
					if(items[i][j].getValue() == 0){
						items[i][j].setValue(randomValue[random.nextInt(10)]);
						
						final int row = i;
						final int column = j;
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								try {
									Thread.sleep(200);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								tableModel.fireTableCellUpdated(row, column);
							}
						});
						break;
					}
				}
				
			}
		});
		
	}
	
	public static void main(String[] args) {
		new FrameMain2();
	}
	private class Item implements Cloneable{
		private int value = 0;
		
		public Item(){
		}
		public Item(int value){
			this.value = value;
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
	
	private class ShowPanel extends JPanel{
		public ShowPanel(){
			tableModel = new ColorTableModel();
			JTable table = new JTable(tableModel);
			table.setIntercellSpacing(new Dimension(6, 6));
			table.setBackground(Color.GRAY);
			table.setShowGrid(false);
			table.setRowHeight(70);
			table.setDefaultRenderer(Object.class, new ColorRenderer());
			add(table);
			setBackground(Color.GRAY);
		}
	}
	
	private class controlPanel extends JPanel{
		public controlPanel(){
			undoButton = new JButton(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					undoButton.setEnabled(undoStack.size() > 1);
					if(undoStack.size() > 0){
						items = undoStack.pop();
						tableModel.fireTableDataChanged();
					}
					
					f.requestFocusInWindow();
				}
			});
			
			undoButton.setText("undo");
			undoButton.setEnabled(false);
			
			JButton restartButton = new JButton(new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					undoStack.clear();
					gameover.setVisible(false);
					
					initData();
					tableModel.fireTableDataChanged();
					f.requestFocusInWindow();
				}
			});
			restartButton.setText("restart");
			
			gameover.setForeground(Color.RED);
			gameover.setVisible(false);
			
			add(undoButton);
			add(restartButton);
			add(gameover);
		}
	}
	
	private void copy(){
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Item item = new Item(items[i][j].getValue());
				
				preItems[i][j] = item;
			}
		}
		
		if(gou+god+gol+gor == 0)//游戏没结束
			undoStack.push(clone(preItems));
	}
	
	private Item[][] clone(Item[][] items){
		Item[][] is = new Item[4][4];
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				Item item = new Item(items[i][j].getValue());
				
				is[i][j] = item;
			}
		}
		
		return is;
	}
	
	private int tt(double a, int n){
		if(a == 2)
			return n;
		else{
			n++;
			
			return tt(a/2, n);
		}
		
	}
	
	private class KeyListerner extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			copy();
			// 左
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
//				System.out.println("左");
				
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
//				System.out.println("下");
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
//				System.out.println("右");
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
//				System.out.println("上");
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
				if(e.getKeyCode() == KeyEvent.VK_UP)
					gou = 1;
				if(e.getKeyCode() == KeyEvent.VK_DOWN)
					god = 1;
				if(e.getKeyCode() == KeyEvent.VK_LEFT)
					gol = 1;
				if(e.getKeyCode() == KeyEvent.VK_RIGHT)
					gor = 1;
				
				if(gou + god + gol + gor >= 4){
					gameover.setVisible(true);
				}
				
				return;
			}
			
			gou = god = gol = gor = 0;
			
			/*//判断是否游戏结束
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
				gameover.setVisible(true);
				return;
			}*/
			
			print();
		}
	}
	
	private class ColorTableModel extends AbstractTableModel {

		public ColorTableModel() {
			initData();
		}

		public int getRowCount() {
			return items.length;
		}

		public int getColumnCount() {
			return items.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return items[rowIndex][columnIndex];
		}
		
	}
	
	private void initData() {
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
		
		undoStack.push(clone(items));
	}

	private class ColorRenderer implements TableCellRenderer {
		private JLabel label;

		public ColorRenderer() {
			label = new JLabel();
			label.setOpaque(true);
//			label.setHorizontalTextPosition(SwingConstants.RIGHT);
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setPreferredSize(new Dimension(50, 50));
			label.setFont(new Font("宋体", Font.BOLD, 30));
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			
			Item item = (Item)value;
			label.setBackground(item.getColor());
			label.setText(item.getValue() == 0 ? "" : ""+item.getValue());
			if(item.getValue()/1000 > 0){
				label.setFont(new Font("宋体", Font.BOLD, 20));
			}
			
			return label;
		}
	}
}
