package rs2;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import rs2.listeners.impl.MyKeyListener;
import rs2.swing.RSFrame;

@SuppressWarnings("serial")
public class MyApplet extends Applet
	implements Runnable, MouseListener, MouseMotionListener, FocusListener, WindowListener, MouseWheelListener
{

	final void createClientFrame(int w, int h) {
		isApplet = false;
		myWidth = w;
		myHeight = h;
		mainFrame = new RSFrame(this, myWidth, myHeight);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		graphics = getGameComponent().getGraphics();
		startRunnable(this, 1);
	}

	final void initClientFrame(int w, int h) {
		isApplet = true;
		myWidth = w;
		myHeight = h;
		graphics = getGameComponent().getGraphics();
		startRunnable(this, 1);
	}

	public void run() {
		getGameComponent().addMouseListener(this);
		getGameComponent().addMouseMotionListener(this);
		getGameComponent().addMouseWheelListener(this);
		getGameComponent().addKeyListener(new MyKeyListener());
		getGameComponent().addFocusListener(this);
		if(mainFrame != null) {
			mainFrame.addWindowListener(this);
		}
		displayProgress(0, "Loading...");
		initialize();
		int opos = 0;
		int ratio = 256;
		int delay = 1;
		int count = 0;
		int intex = 0;
		for(int index = 0; index < 10; index++) {
			times[index] = System.currentTimeMillis();
		}
		do {
			if(timeRunning < 0) {
				break;
			}
			if(timeRunning > 0) {
				timeRunning--;
				if(timeRunning == 0) {
					exit();
					return;
				}
			}
			int k1 = ratio;
			int i2 = delay;
			ratio = 300;
			delay = 1;
			long systemTime = System.currentTimeMillis();
			if(times[opos] == 0L) {
				ratio = k1;
				delay = i2;
			} else if(systemTime > times[opos]) {
				ratio = (int)((long)(2560 * delayTime) / (systemTime - times[opos]));
			}
			if(ratio < 25) {
				ratio = 25;
			}
			if(ratio > 256) {
				ratio = 256;
				delay = (int)((long)delayTime - (systemTime - times[opos]) / 10L);
			}
			if(delay > delayTime) {
				delay = delayTime;
			}
			times[opos] = systemTime;
			opos = (opos + 1) % 10;
			if(delay > 1) {
				for(int index = 0; index < 10; index++) {
					if(times[index] != 0L) {
						times[index] += delay;
					}
				}
			}
			if(delay < minDelay) {
				delay = minDelay;
			}
			try {
				Thread.sleep(delay);
			} catch(InterruptedException e) {
				intex++;
			}
			for(; count < 256; count += ratio) {
				saveClickX = clickX;
				saveClickY = clickY;
				aLong29 = clickTime;
				process();
				readIndex = writeIndex;
			}
			count &= 0xff;
			if(delayTime > 0) {
				fps = (1000 * ratio) / (delayTime * 256);
			}
			processDrawing();
			if(shouldDebug) {
				System.out.println("ntime:" + systemTime);
				for(int index = 0; index < 10; index++) {
					int otim = ((opos - index - 1) + 20) % 10;
					System.out.println("otim" + otim + ":" + times[otim]);
				}
				System.out.println("fps:" + fps + " ratio:" + ratio + " count:" + count);
				System.out.println("del:" + delay + " deltime:" + delayTime + " mindel:" + minDelay);
				System.out.println("intex:" + intex + " opos:" + opos);
				shouldDebug = false;
				intex = 0;
			}
		} while(true);
		if(timeRunning == -1) {
			exit();
		}
	}

	private void exit() {
		timeRunning = -2;
		cleanUpForQuit();
		if(mainFrame != null) {
			try {
				Thread.sleep(1000L);
			} catch(Exception e) {
			}
			try {
				System.exit(0);
			} catch(Throwable throwable) {
			}
		}
	}

	final void setDelayTime(int time) {
		delayTime = 1000 / time;
	}

	public final void start() {
		if(timeRunning >= 0) {
			timeRunning = 0;
		}
	}

	public final void stop() {
		if(timeRunning >= 0) {
			timeRunning = 4000 / delayTime;
		}
	}

	public final void destroy() {
		timeRunning = -1;
		try {
			Thread.sleep(5000L);
		} catch(Exception e) {
		}
		if(timeRunning == -1) {
			exit();
		}
	}

	public final void update(Graphics g) {
		if(graphics == null) {
			graphics = g;
		}
		shouldClearScreen = true;
	}

	public final void paint(Graphics g) {
		if(graphics == null) {
			graphics = g;
		}
		shouldClearScreen = true;
	}
	
	public void mouseWheelMoved(MouseWheelEvent event) {
		int rotation = event.getWheelRotation();
		if (event.isControlDown()) {
			Main.getInstance().zoom -= rotation * 30;
			Main.scaledX = 0;
			Main.scaledY = 0;
		} else {
			if (Main.horizontalScale >= 1 && Main.horizontalScale <= 20) {
				Main.horizontalScale -= rotation;
			}
			if (Main.horizontalScale < 1) {
				Main.horizontalScale = 1;
			}
			if (Main.horizontalScale > 20) {
				Main.horizontalScale = 20;
			}
			if (Main.verticalScale >= 1 && Main.verticalScale <= 20) {
				Main.verticalScale -= rotation;
			}
			if (Main.verticalScale < 1) {
				Main.verticalScale = 1;
			}
			if (Main.verticalScale > 20) {
				Main.verticalScale = 20;
			}
		}
	}
	
	public void mousePressed(MouseEvent e) {
		if (clickType == CTRL_DRAG) {
			return;
		}
		int x = e.getX();
		int y = e.getY();
		if(mainFrame != null) {
			Insets insets = mainFrame.getInsets();
			x -= insets.left;
			y -= insets.top;
		}
		idleTime = 0;
		clickX = x;
		clickY = y;
		long oldTime = clickTime;
		clickTime = System.currentTimeMillis();
		previousType = clickType;
		if (e.isControlDown()) {
			if (SwingUtilities.isRightMouseButton(e)) {
				clickType = CTRL_RIGHT;
				return;
			} else {
				clickType = CTRL_LEFT;
				return;
			}
		}
		if (clickTime - oldTime < 250) {
			clickType = DOUBLE;
			return;
		}
		if(e.isMetaDown()) {
			clickType = RIGHT;
		} else {
			clickType = LEFT;
		}
	}

	public void mouseReleased(MouseEvent e) {
		idleTime = 0;
		clickX = -1;
		clickY = -1;
		clickType = RELEASED;
	}

	public final void mouseClicked(MouseEvent mouseevent) {
	}

	public final void mouseEntered(MouseEvent mouseevent) {
	}

	public final void mouseExited(MouseEvent mouseevent) {
		idleTime = 0;
		mouseX = -1;
		mouseY = -1;
	}

	public final void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if(mainFrame != null) {
			Insets insets = mainFrame.getInsets();
			x -= insets.left;
			y -= insets.top;
		}
		idleTime = 0;
		mouseX = x;
		mouseY = y;
		previousType = clickType;
		if (e.isControlDown()) {
			clickType = CTRL_DRAG;
		} else {
			clickType = DRAG;
		}
	}

	public final void mouseMoved(MouseEvent mouseevent) {
		int x = mouseevent.getX();
		int y = mouseevent.getY();
		if(mainFrame != null) {
			Insets insets = mainFrame.getInsets();
			x -= insets.left;
			y -= insets.top;
		}
		idleTime = 0;
		mouseX = x;
		mouseY = y;
	}

	public final int readCharacter() {
		int charId = -1;
		if(writeIndex != readIndex) {
			charId = charQueue[readIndex];
			readIndex = readIndex + 1 & 0x7f;
		}
		return charId;
	}

	public final void focusGained(FocusEvent event) {
		awtFocus = true;
		shouldClearScreen = true;
	}

	public final void focusLost(FocusEvent event) {
		awtFocus = false;
		for(int index = 0; index < 128; index++) {
			keyArray[index] = 0;
		}
	}

	public final void windowActivated(WindowEvent event) {}

	public final void windowClosed(WindowEvent event) {}

	public final void windowClosing(WindowEvent event) {
		destroy();
	}

	public final void windowDeactivated(WindowEvent event) {}

	public final void windowDeiconified(WindowEvent event) {}

	public final void windowIconified(WindowEvent event) {}

	public final void windowOpened(WindowEvent event){}

	void initialize() {}

	void process() {}

	void cleanUpForQuit() {}

	void processDrawing() {}

	public Component getGameComponent() {
		if(mainFrame != null && !isApplet) {
			return mainFrame;
		} else {
			return this;
		}
	}

	public void startRunnable(Runnable runnable, int priority) {
		Thread thread = new Thread(runnable);
		thread.start();
		thread.setPriority(priority);
	}

	void displayProgress(int percentage, String loadingText) {
		while(graphics == null) {
			graphics = (isApplet ? this : mainFrame).getGraphics();
			try {
				getGameComponent().repaint();
			} catch(Exception exception) { }
			try {
				Thread.sleep(1000L);
			} catch(Exception exception1) { }
		}
		Font font = new Font("Arial", 0, 12);
		FontMetrics fontmetrics = getGameComponent().getFontMetrics(font);
		Font font1 = new Font("Arial", 0, 12);
		FontMetrics fontmetrics1 = getGameComponent().getFontMetrics(font1);
		if(shouldClearScreen) {
			graphics.setColor(new Color(51, 51, 51));
			graphics.fillRect(0, 0, myWidth, myHeight);
			shouldClearScreen = false;
		}
		int width = 300;
		int height = 30;
		int y = myHeight / 2 - (height / 2);
		graphics.setColor(new Color(18, 18, 18));
		graphics.fillRect(myWidth / 2 - (width / 2), y, width, height);
		graphics.setColor(Color.WHITE);
		graphics.drawRect(myWidth / 2 - (width / 2), y, width, height);
		graphics.setFont(font);
		graphics.drawString(loadingText,(myWidth - fontmetrics.stringWidth(loadingText)) / 2, y + 22);
		graphics.drawString(titleText, (myWidth - fontmetrics1.stringWidth(titleText)) / 2, y - 8);
	}

	protected MyApplet() {
		delayTime = 20;
		minDelay = 1;
		shouldDebug = false;
		shouldClearScreen = true;
		awtFocus = true;
	}

	public String titleText = "";
	public static int hotKey = 508;
	private int timeRunning;
	private int delayTime;
	int minDelay;
	private final long times[] = new long[10];
	int fps;
	boolean shouldDebug;
	int myWidth;
	int myHeight;
	Graphics graphics;
	public Insets insets = new Insets(30, 5, 5, 5);
	public RSFrame mainFrame;
	private boolean shouldClearScreen;
	public boolean isApplet;
	boolean awtFocus;
	int idleTime;
	public int mouseX;
	public int mouseY;
	protected int clickX;
	protected int clickY;
	private long clickTime;
	int saveClickX;
	int saveClickY;
	long aLong29;
	protected final int keyArray[] = new int[128];
	protected final int charQueue[] = new int[128];
	private int readIndex;
	protected int writeIndex;
	public static int anInt34;

	public int clickType;
	public int previousType;
	public final int LEFT = 0;
	public final int RIGHT = 1;
	public final int DRAG = 2;
	public final int RELEASED = 3;
	public final int MOVE = 4;
	public final int CTRL_LEFT = 5;
	public final int DOUBLE = 6;
	public final int CTRL_DRAG = 7;
	public final int CTRL_RIGHT = 8;
	
}