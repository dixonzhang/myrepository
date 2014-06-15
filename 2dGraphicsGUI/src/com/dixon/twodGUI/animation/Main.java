package com.dixon.twodGUI.animation;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.MediaTracker;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main extends JPanel implements ActionListener {
	ImageIcon images[];
	int totalImages = 30, currentImage = 0, animationDelay = 50;
	Timer animationTimer;

	public Main() {
		images = new ImageIcon[totalImages];
		for (int i = 0; i < images.length; ++i)
			images[i] = new ImageIcon("images/java" + i + ".gif");
		startAnimation();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (images[currentImage].getImageLoadStatus() == MediaTracker.COMPLETE) {
			images[currentImage].paintIcon(this, g, 0, 0);
			currentImage = (currentImage + 1) % totalImages;
		}
	}

	public void actionPerformed(ActionEvent e) {
		repaint();
	}

	public void startAnimation() {
		if (animationTimer == null) {
			currentImage = 0;
			animationTimer = new Timer(animationDelay, this);
			animationTimer.start();
		} else if (!animationTimer.isRunning())
			animationTimer.restart();
	}

	public void stopAnimation() {
		animationTimer.stop();
	}

	public static void main(String args[]) {
		Main anim = new Main();
		JFrame app = new JFrame("Animator test");
		app.add(anim, BorderLayout.CENTER);
		app.setSize(300, 300);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		app.setSize(anim.getPreferredSize().width + 10,
				anim.getPreferredSize().height + 30);
	}
}