package com.galkon.graphics;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.util.Hashtable;

import com.galkon.Main;


public final class RSImageProducer {

	public RSImageProducer(int width, int height, Component component) {
		this.width = width;
		this.height = height;
		this.component = component;
		int count = width * height;
		pixels = new int[count];
		scaled = null;
		image = new BufferedImage(COLOR_MODEL, Raster.createWritableRaster(COLOR_MODEL.createCompatibleSampleModel(width, height), new DataBufferInt(pixels, count), null), false, new Hashtable<Object, Object>());
		initDrawingArea();
	}

    public void drawGraphics(int x, int y, Graphics gfx) {
		draw(x, y, gfx);
	}

	public void draw(int x, int y, Graphics gfx) {
		x += Main.scaledX;
		y += Main.scaledY;
		double scale = Main.getInstance().getScale();
		gfx.drawImage(image, x, y, (int) (image.getWidth() * scale), (int) (image.getHeight() * scale), component);
	}

	public void draw(Graphics gfx, int x, int y, int clipX, int clipY, int clipWidth, int clipHeight) {
		Shape tmp = gfx.getClip();
		try {
			clip.x = clipX;
			clip.y = clipY;
			clip.width = clipWidth;
			clip.height = clipHeight;
			gfx.setClip(clip);
			gfx.drawImage(image, x, y, component);
		} finally {
			gfx.setClip(tmp);
		}
	}

	public void initDrawingArea() {
		DrawingArea.initDrawingArea(width, height, pixels);
	}

    public final int[] pixels;
	public final int width;
	public final int height;
	public final BufferedImage image;
	public BufferedImage scaled;
	public final Component component;
	private final Rectangle clip = new Rectangle();
	private static final ColorModel COLOR_MODEL = new DirectColorModel(32, 0xff0000, 0xff00, 0xff);
}