package com.dixon.twodGUI.animation;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageBouncer extends JPanel implements Runnable {
	private boolean trucking = true;
	private long[] previousTimes; // milliseconds

	private int previousIndex;

	private boolean previousFilled;

	private double frameRate; // frames per second

	public static void main(String[] args) {
		String filename = "http://img.nr99.com/attachment/forum/201404/24/232829kggxosozugi4mg4n.jpg";
		if (args.length > 0)
			filename = args[0];

		Image image = null;
		try {
			image = blockingLoad(new URL(filename));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final ImageBouncer bouncer = new ImageBouncer(image);
		Frame f = new AnimationFrame(bouncer);
		f.setFont(new Font("Serif", Font.PLAIN, 12));
		Panel controls = new Panel();
		controls.add(bouncer.createCheckbox("Bilinear", ImageBouncer.BILINEAR));
		controls.add(bouncer
				.createCheckbox("Transform", ImageBouncer.TRANSFORM));
		final Choice typeChoice = new Choice();
		typeChoice.add("TYPE_INT_RGB");
		typeChoice.add("TYPE_INT_ARGB");
		typeChoice.add("TYPE_INT_ARGB_PRE");
		typeChoice.add("TYPE_3BYTE_BGR");
		typeChoice.add("TYPE_BYTE_GRAY");
		typeChoice.add("TYPE_USHORT_GRAY");
		typeChoice.add("TYPE_USHORT_555_RGB");
		typeChoice.add("TYPE_USHORT_565_RGB");
		controls.add(typeChoice);
		f.add(controls, BorderLayout.NORTH);

		typeChoice.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ie) {
				String type = typeChoice.getSelectedItem();
				bouncer.setImageType(type);
			}
		});
		f.setSize(200, 200);
		f.setVisible(true);
	}

	private boolean mBilinear = false;

	private boolean mTransform = false;

	public static final int BILINEAR = 1;

	public static final int TRANSFORM = 3;

	private float mDeltaX, mDeltaY;

	private float mX, mY, mWidth, mHeight;

	private float mTheta;

	private Image mOriginalImage;

	private Image image;

	public ImageBouncer(Image image) {
		previousTimes = new long[128];
		previousTimes[0] = System.currentTimeMillis();
		previousIndex = 1;
		previousFilled = false;

		mOriginalImage = image;
		setImageType("TYPE_INT_RGB");

		Random random = new Random();
		mX = random.nextFloat() * 500;
		mY = random.nextFloat() * 500;
		mWidth = image.getWidth(this);
		mHeight = image.getHeight(this);
		mDeltaX = random.nextFloat() * 3;
		mDeltaY = random.nextFloat() * 3;
		// Make sure points are within range.
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				Dimension d = getSize();
				if (mX < 0)
					mX = 0;
				else if (mX + mWidth >= d.width)
					mX = d.width - mWidth - 1;
				if (mY < 0)
					mY = 0;
				else if (mY + mHeight >= d.height)
					mY = d.height - mHeight - 1;
			}
		});
	}

	public void setSwitch(int item, boolean value) {
		switch (item) {
		case BILINEAR:
			mBilinear = value;
			break;
		case TRANSFORM:
			mTransform = value;
			break;
		default:
			break;
		}
	}

	public void setImageType(String s) {
		int type = BufferedImage.TYPE_CUSTOM;
		if (s.equals("TYPE_INT_RGB"))
			type = BufferedImage.TYPE_INT_RGB;
		else if (s.equals("TYPE_INT_ARGB"))
			type = BufferedImage.TYPE_INT_ARGB;
		else if (s.equals("TYPE_INT_ARGB_PRE"))
			type = BufferedImage.TYPE_INT_ARGB_PRE;
		else if (s.equals("TYPE_3BYTE_BGR"))
			type = BufferedImage.TYPE_3BYTE_BGR;
		else if (s.equals("TYPE_BYTE_GRAY"))
			type = BufferedImage.TYPE_BYTE_GRAY;
		else if (s.equals("TYPE_USHORT_GRAY"))
			type = BufferedImage.TYPE_USHORT_GRAY;
		else if (s.equals("TYPE_USHORT_555_RGB"))
			type = BufferedImage.TYPE_USHORT_565_RGB;
		else if (s.equals("TYPE_USHORT_565_RGB"))
			type = BufferedImage.TYPE_USHORT_565_RGB;
		else {
			System.out.println("Unrecognized type.");
			return;
		}
		image = makeBufferedImage(mOriginalImage, type);
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
		if (mX + mDeltaX < 0)
			mDeltaX = -mDeltaX;
		else if (mX + mWidth + mDeltaX >= d.width)
			mDeltaX = -mDeltaX;
		if (mY + mDeltaY < 0)
			mDeltaY = -mDeltaY;
		else if (mY + mHeight + mDeltaY >= d.height)
			mDeltaY = -mDeltaY;
		mX += mDeltaX;
		mY += mDeltaY;

		mTheta += Math.PI / 192;
		if (mTheta > (2 * Math.PI))
			mTheta -= (2 * Math.PI);
	}

	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		setTransform(g2);
		setBilinear(g2);
		// Draw the image.
		g2.drawImage(image, AffineTransform.getTranslateInstance(mX, mY), null);
	}

	protected void setTransform(Graphics2D g2) {
		if (mTransform == false)
			return;
		float cx = mX + mWidth / 2;
		float cy = mY + mHeight / 2;
		g2.rotate(mTheta, cx, cy);
	}

	protected void setBilinear(Graphics2D g2) {
		if (mBilinear == false)
			return;
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
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
				// Clear the image background.
				imageGraphics.setColor(getBackground());
				imageGraphics.fillRect(0, 0, d.width, d.height);
				imageGraphics.setColor(getForeground());
				// Draw this component offscreen.
				paint(imageGraphics);
				// Now put the offscreen image on the screen.
				g.drawImage(image, 0, 0, null);
				// Clean up.
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

	private static Component sComponent = new Component() {
	};

	private static final MediaTracker sTracker = new MediaTracker(sComponent);

	private static int sID = 0;

	public static boolean waitForImage(Image image) {
		int id;
		synchronized (sComponent) {
			id = sID++;
		}
		sTracker.addImage(image, id);
		try {
			sTracker.waitForID(id);
		} catch (InterruptedException ie) {
			return false;
		}
		if (sTracker.isErrorID(id))
			return false;
		return true;
	}

	public Image blockingLoad(String path) {
		Image image = Toolkit.getDefaultToolkit().getImage(path);
		if (waitForImage(image) == false)
			return null;
		return image;
	}

	public static Image blockingLoad(URL url) {
		Image image = Toolkit.getDefaultToolkit().getImage(url);
		if (waitForImage(image) == false)
			return null;
		return image;
	}

	public BufferedImage makeBufferedImage(Image image) {
		return makeBufferedImage(image, BufferedImage.TYPE_INT_RGB);
	}

	public BufferedImage makeBufferedImage(Image image, int imageType) {
		if (waitForImage(image) == false)
			return null;

		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
				image.getHeight(null), imageType);
		Graphics2D g2 = bufferedImage.createGraphics();
		g2.drawImage(image, null, null);
		return bufferedImage;
	}
	
	static class AnimationFrame extends JFrame {
		private Label mStatusLabel;

		private NumberFormat mFormat;

		public AnimationFrame(ImageBouncer ac) {
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
}


