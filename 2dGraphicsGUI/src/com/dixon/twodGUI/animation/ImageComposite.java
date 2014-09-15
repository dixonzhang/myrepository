package com.dixon.twodGUI.animation;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class ImageComposite extends JPanel implements ActionListener {
	Image a = new ImageIcon(this.getClass().getResource("a.png")).getImage();
	Image b = new ImageIcon(this.getClass().getResource("java2slogo.png")).getImage();
	Timer timer = new Timer(800, this);
	float alpha = 1f;

	public ImageComposite() {
		timer.start();
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;

		BufferedImage buffImg = new BufferedImage(200, 100,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gbi = buffImg.createGraphics();

		AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				alpha);

		gbi.drawImage(a, 40, 30, null);
		gbi.setComposite(ac);
		gbi.drawImage(b, 0, 0, null);

		g2d.drawImage(buffImg, 20, 20, null);
	}

	public void actionPerformed(ActionEvent e) {
		alpha -= 0.1;
		if (alpha <= 0) {
			alpha = 0;
			timer.stop();
		}
		repaint();
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.add(new ImageComposite());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 210);
		frame.setVisible(true);
	}

}