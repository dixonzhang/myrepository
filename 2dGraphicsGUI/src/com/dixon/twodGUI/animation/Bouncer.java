package com.dixon.twodGUI.animation;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Bouncer extends JPanel implements Runnable {
	private boolean trucking = true;

	private long[] previousTimes; // milliseconds

	private int previousIndex;

	private boolean previousFilled;

	private double frameRate; // frames per second

	private Image image;

	public static void main(String[] args) {
		final Bouncer bouncer = new Bouncer();
		Frame f = new AnimationFrame(bouncer);
		f.setFont(new Font("Serif", Font.PLAIN, 12));
		f.setSize(200, 200);
		Panel controls = new Panel();
		controls.add(bouncer.createCheckbox("Anti.", Bouncer.ANTIALIASING));
		controls.add(bouncer.createCheckbox("Trans.", Bouncer.TRANSFORM));
		controls.add(bouncer.createCheckbox("Gradient", Bouncer.GRADIENT));
		controls.add(bouncer.createCheckbox("Outline", Bouncer.OUTLINE));
		controls.add(bouncer.createCheckbox("Dotted", Bouncer.DOTTED));
		controls.add(bouncer.createCheckbox("Axes", Bouncer.AXES));
		controls.add(bouncer.createCheckbox("Clip", Bouncer.CLIP));
		f.add(controls, BorderLayout.NORTH);

		f.setVisible(true);
	}

	// Tweakable variables
	private boolean mAntialiasing, mGradient, mOutline;

	private boolean mTransform, mDotted, mAxes, mClip;

	// ...and the constants that represent them. See setSwitch().
	public static final int ANTIALIASING = 0;

	public static final int GRADIENT = 1;

	public static final int OUTLINE = 2;

	public static final int TRANSFORM = 3;

	public static final int DOTTED = 4;

	public static final int AXES = 5;

	public static final int CLIP = 6;

	private float[] mPoints;

	private float[] mDeltas;

	private float mTheta;

	private int mN;

	private Shape mClipShape;

	public Bouncer() {
		previousTimes = new long[128];
		previousTimes[0] = System.currentTimeMillis();
		previousIndex = 1;
		previousFilled = false;

		mN = 38;
		mPoints = new float[mN];
		mDeltas = new float[mN];
		Random random = new Random();
		for (int i = 0; i < mN; i++) {
			mPoints[i] = random.nextFloat() * 500;
			mDeltas[i] = random.nextFloat() * 3;
		}

		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				Dimension d = getSize();
				for (int i = 0; i < mN; i++) {
					int limit = ((i % 2) == 0) ? d.width : d.height;
					if (mPoints[i] < 0)
						mPoints[i] = 0;
					else if (mPoints[i] >= limit)
						mPoints[i] = limit - 1;
				}
			}
		});
	}

	public void setSwitch(int item, boolean value) {
		switch (item) {
		case ANTIALIASING:
			mAntialiasing = value;
			break;
		case GRADIENT:
			mGradient = value;
			break;
		case OUTLINE:
			mOutline = value;
			break;
		case TRANSFORM:
			mTransform = value;
			break;
		case DOTTED:
			mDotted = value;
			break;
		case AXES:
			mAxes = value;
			break;
		case CLIP:
			mClip = value;
			break;
		default:
			break;
		}
	}

	protected Checkbox createCheckbox(String label, final int item) {
		Checkbox check = new Checkbox(label);
		check.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				setSwitch(item, (ie.getStateChange() == ie.SELECTED));
			}
		});
		return check;
	}

	public void timeStep() {
		Dimension d = getSize();
		for (int i = 0; i < mN; i++) {
			float value = mPoints[i] + mDeltas[i];
			int limit = ((i % 2) == 0) ? d.width : d.height;
			if (value < 0 || value > limit) {
				mDeltas[i] = -mDeltas[i];
				mPoints[i] += mDeltas[i];
			} else
				mPoints[i] = value;
		}
		mTheta += Math.PI / 192;
		if (mTheta > (2 * Math.PI))
			mTheta -= (2 * Math.PI);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		setAntialiasing(g2);
		setClip(g2);
		setTransform(g2);
		Shape shape = createShape();
		setPaint(g2);

		g2.fill(shape);

		if (mOutline) {
			setStroke(g2);
			g2.setPaint(Color.blue);
			g2.draw(shape);
		}
		drawAxes(g2);
	}

	protected void setAntialiasing(Graphics2D g2) {
		if (mAntialiasing == false)
			return;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
	}

	protected void setClip(Graphics2D g2) {
		if (mClip == false)
			return;
		if (mClipShape == null) {
			Dimension d = getSize();
			FontRenderContext frc = g2.getFontRenderContext();
			Font font = new Font("Serif", Font.PLAIN, 144);
			String s = "Java Source and Support!";
			GlyphVector gv = font.createGlyphVector(frc, s);
			Rectangle2D bounds = font.getStringBounds(s, frc);
			mClipShape = gv.getOutline(
					(d.width - (float) bounds.getWidth()) / 2,
					(d.height + (float) bounds.getHeight()) / 2);
		}
		g2.clip(mClipShape);
	}

	protected void setTransform(Graphics2D g2) {
		if (mTransform == false)
			return;
		Dimension d = getSize();
		g2.rotate(mTheta, d.width / 2, d.height / 2);
	}

	protected Shape createShape() {
		GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
				mPoints.length);
		path.moveTo(mPoints[0], mPoints[1]);
		for (int i = 2; i < mN; i += 6)
			path.curveTo(mPoints[i], mPoints[i + 1], mPoints[i + 2],
					mPoints[i + 3], mPoints[i + 4], mPoints[i + 5]);
		path.closePath();
		return path;
	}

	protected void setPaint(Graphics2D g2) {
		if (mGradient) {
			GradientPaint gp = new GradientPaint(0, 0, Color.yellow, 50, 25,
					Color.red, true);
			g2.setPaint(gp);
		} else
			g2.setPaint(Color.orange);
	}

	protected void setStroke(Graphics2D g2) {
		if (mDotted == false)
			return;

		Stroke stroke = new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 10, new float[] { 4, 4 }, 0);
		g2.setStroke(stroke);
	}

	protected void drawAxes(Graphics2D g2) {
		if (mAxes == false)
			return;
		g2.setPaint(getForeground());
		g2.setStroke(new BasicStroke());
		Dimension d = getSize();
		int side = 20;
		int arrow = 4;
		int w = d.width / 2, h = d.height / 2;
		g2.drawLine(w - side, h, w + side, h);
		g2.drawLine(w + side - arrow, h - arrow, w + side, h);
		g2.drawLine(w, h - side, w, h + side);
		g2.drawLine(w + arrow, h + side - arrow, w, h + side);
	}

	public void run() {
		while (trucking) {
			render();
			timeStep();
			calculateFrameRate();
		}
	}

	protected void render() {
		Graphics g = getGraphics();
		if (g != null) {
			Dimension d = getSize();
			if (checkImage(d)) {
				Graphics imageGraphics = image.getGraphics();

				imageGraphics.setColor(getBackground());
				imageGraphics.fillRect(0, 0, d.width, d.height);
				imageGraphics.setColor(getForeground());

				paint(imageGraphics);

				g.drawImage(image, 0, 0, null);

				imageGraphics.dispose();
			}
			g.dispose();
		}
	}

	// Offscreen image.
	protected boolean checkImage(Dimension d) {
		if (d.width == 0 || d.height == 0)
			return false;
		if (image == null || image.getWidth(null) != d.width
				|| image.getHeight(null) != d.height) {
			image = createImage(d.width, d.height);
		}
		return true;
	}

	protected void calculateFrameRate() {
		// Measure the frame rate
		long now = System.currentTimeMillis();
		int numberOfFrames = previousTimes.length;
		double newRate;
		// Use the more stable method if a history is available.
		if (previousFilled)
			newRate = (double) numberOfFrames
					/ (double) (now - previousTimes[previousIndex]) * 1000.0;
		else
			newRate = 1000.0 / (double) (now - previousTimes[numberOfFrames - 1]);
		firePropertyChange("frameRate", frameRate, newRate);
		frameRate = newRate;
		// Update the history.
		previousTimes[previousIndex] = now;
		previousIndex++;
		if (previousIndex >= numberOfFrames) {
			previousIndex = 0;
			previousFilled = true;
		}
	}

	public double getFrameRate() {
		return frameRate;
	}

	// Property change support.
	private transient AnimationFrame mRateListener;

	public void setRateListener(AnimationFrame af) {
		mRateListener = af;
	}

	public void firePropertyChange(String name, double oldValue, double newValue) {
		mRateListener.rateChanged(newValue);
	}

}

class AnimationFrame extends JFrame {
	private Label mStatusLabel;

	private NumberFormat mFormat;

	public AnimationFrame(Bouncer ac) {
		super();
		setLayout(new BorderLayout());
		add(ac, BorderLayout.CENTER);
		add(mStatusLabel = new Label(), BorderLayout.SOUTH);
		// Create a number formatter.
		mFormat = NumberFormat.getInstance();
		mFormat.setMaximumFractionDigits(1);
		// Listen for the frame rate changes.
		ac.setRateListener(this);
		// Kick off the animation.
		Thread t = new Thread(ac);
		t.start();
	}

	public void rateChanged(double frameRate) {
		mStatusLabel.setText(mFormat.format(frameRate) + " fps");
	}
}
