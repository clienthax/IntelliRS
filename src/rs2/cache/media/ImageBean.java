package rs2.cache.media;

import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.PixelGrabber;

public class ImageBean
{
	private int drawOffsetX;
	private int drawOffsetY;
	private int width;
	private int height;
	public int[] pixels;

	ImageBean(int drawOffsetX, int drawOffsetY, int width, int height, int[] pixels)
	{
		this.drawOffsetX = drawOffsetX;
		this.drawOffsetY = drawOffsetY;
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}

	public int getDrawOffsetX() {
		return this.drawOffsetX;
	}

	public void setDrawOffsetX(int drawOffsetX) {
		this.drawOffsetX = drawOffsetX;
	}

	public int getDrawOffsetY() {
		return this.drawOffsetY;
	}

	public void setDrawOffsetY(int drawOffsetY) {
		this.drawOffsetY = drawOffsetY;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return this.height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int[] getPixels() {
		return this.pixels;
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}

	public void replaceFromImage(byte[] imageData, Component component) throws InterruptedException {
		Image image = Toolkit.getDefaultToolkit().createImage(imageData);
		MediaTracker mediatracker = new MediaTracker(component);
		mediatracker.addImage(image, 0);
		mediatracker.waitForAll();
		setWidth(image.getWidth(component));
		setHeight(image.getHeight(component));
		this.pixels = new int[getWidth() * getHeight()];
		PixelGrabber pixelgrabber = new PixelGrabber(image, 0, 0, getWidth(), getHeight(), this.pixels, 0, getWidth());
		pixelgrabber.grabPixels();
	}
}