package com.galkon.cache.media;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import com.galkon.io.JagexBuffer;
import com.galkon.io.RSByteArrayOutputStream;
import com.galkon.util.Quantize;


public final class ImageGroup {
	public int groupMaxWidth;
	public int groupMaxHeight;
	private int[] colourMap;
	private ArrayList<ImageBean> imageBeans = new ArrayList<ImageBean>();
	private int colourCount;
	private JagexBuffer indexStream;
	private JagexBuffer dataStream;
	private int indexOffset = 0;
	private int packType = 0;

	public ImageGroup() {
		this.groupMaxWidth = 0;
		this.groupMaxHeight = 0;
		this.colourCount = 2;
		this.colourMap = new int[] { 0, 1 };
	}

	public ImageGroup(byte[] index, byte[] data, boolean unpack) {
		this.indexStream = new JagexBuffer(index);
		this.dataStream = new JagexBuffer(data);

		this.indexOffset = (this.indexStream.offset = this.dataStream.getUnsignedShort());
		this.groupMaxWidth = this.indexStream.getUnsignedShort();
		this.groupMaxHeight = this.indexStream.getUnsignedShort();
		this.colourCount = this.indexStream.getUnsignedByte();
		this.colourMap = new int[this.colourCount];

		for (int x = 0; x < this.colourCount - 1; x++) {
			this.colourMap[(x + 1)] = this.indexStream.get3Bytes();
			if (this.colourMap[(x + 1)] == 0) {
				this.colourMap[(x + 1)] = 1;
			}
		}
		if (unpack)
			unpackImages();
	}

	public int countImages() {
		return this.imageBeans.size();
	}

	public void appendThisIndex(RSByteArrayOutputStream out) throws IOException {
		this.indexOffset = out.size();
		out.putShort(this.groupMaxWidth);
		out.putShort(this.groupMaxHeight);
		out.write(this.colourCount);
		for (int x = 1; x < this.colourCount; x++) {
			out.put3Bytes(this.colourMap[x]);
		}
		for (ImageBean i : this.imageBeans) {
			out.write(i.getDrawOffsetX());
			out.write(i.getDrawOffsetY());
			out.putShort(i.getWidth());
			out.putShort(i.getHeight());
			out.write(this.packType);
		}
	}

	public byte[] packData() throws IOException {
		RSByteArrayOutputStream out = new RSByteArrayOutputStream();
		out.putShort(this.indexOffset);
		for (ImageBean i : this.imageBeans) {
			int[] pixels = i.getPixels();
			if (this.packType == 0) {
				for (int x = 0; x < pixels.length; x++)
					out.write(findPosInMap(pixels[x]));
			}
			else if (this.packType == 1) {
				for (int x = 0; x < i.getWidth(); x++) {
					for (int y = 0; y < i.getHeight(); y++) {
						out.write(findPosInMap(pixels[(x + y * i.getWidth())]));
					}
				}
			}
		}
		byte[] ret = out.toByteArray();
		out.close();
		return ret;
	}

	public int findPosInMap(int colour) {
		for (int x = 0; x < this.colourMap.length; x++) {
			if (colour == this.colourMap[x]) {
				return x;
			}
		}
		return 0;
	}

	private void rebuildColourMap() {
		ArrayList<Integer> tempMap = new ArrayList<Integer>();
		tempMap.add(Integer.valueOf(0));
		for (ImageBean i : this.imageBeans) {
			int[] pixels = i.getPixels();
			for (int x : pixels) {
				if (!tempMap.contains(Integer.valueOf(x))) {
					tempMap.add(Integer.valueOf(x));
				}
			}
		}
		this.colourCount = tempMap.size();
		this.colourMap = new int[this.colourCount];
		for (int i = 0; i < this.colourCount; i++)
			this.colourMap[i] = ((Integer)tempMap.get(i)).intValue();
	}

	public void unpackImages()
	{
		int origIndexOffset = this.indexStream.offset;
		int origDataOffset = this.dataStream.offset;
		while (this.dataStream.offset < this.dataStream.payload.length) {
			int drawOffsetX = this.indexStream.getSignedByte();
			int drawOffsetY = this.indexStream.getSignedByte();
			int width = this.indexStream.getUnsignedShort();
			int height = this.indexStream.getUnsignedShort();

			this.packType = this.indexStream.getSignedByte();
			int numPixels = width * height;

			int[] pixels = new int[numPixels];
			if (this.packType == 0)
				for (int x = 0; x < numPixels; x++) {
					int i = this.dataStream.getUnsignedByte();
					pixels[x] = this.colourMap[i];
				}
			else if (this.packType == 1) {
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						int i = this.dataStream.getUnsignedByte();
						pixels[(x + y * width)] = this.colourMap[i];
					}
				}
			}
			this.imageBeans.add(new ImageBean(drawOffsetX, drawOffsetY, width, height, pixels));
		}
		this.indexStream.offset = origIndexOffset;
		this.dataStream.offset = origDataOffset;
	}

	public void replaceImage(int i, byte[] imageData, Component c) throws InterruptedException {
		ImageBean img = (ImageBean)this.imageBeans.get(i);
		img.replaceFromImage(imageData, c);
		if (img.getWidth() > this.groupMaxWidth) this.groupMaxWidth = img.getWidth();
		if (img.getHeight() > this.groupMaxHeight) this.groupMaxHeight = img.getHeight();
		quantizeImage(img);
		rebuildColourMap();
	}

	public void quantizeImage(ImageBean img)
	{
		int[][] newPix = new int[img.getWidth()][img.getHeight()];
		for (int x = img.getWidth(); x-- > 0; )
			for (int y = img.getHeight(); y-- > 0; )
				newPix[x][y] = img.getPixels()[(y * img.getWidth() + x)];
		int[] newColMap = Quantize.quantizeImage(newPix, 254);
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				img.pixels[(y * img.getWidth() + x)] = newColMap[newPix[x][y]];
			}
		}
		for (int i = 0; i < img.pixels.length; i++) {
			int[] rgb = unpack(img.pixels[i]);
			if ((rgb[0] == 255) && (rgb[1] == 0) && (rgb[2] == 255))
				img.pixels[i] = 0;
		}
	}

	public void addSprite(byte[] imageData, Component c) throws InterruptedException
	{
		ImageBean i = new ImageBean(0, 0, 1, 1, new int[] { 0, 0, 0, 0 });
		i.replaceFromImage(imageData, c);
		if (i.getWidth() > this.groupMaxWidth) this.groupMaxWidth = i.getWidth();
		if (i.getHeight() > this.groupMaxHeight) this.groupMaxHeight = i.getHeight();
		this.imageBeans.add(i);
		rebuildColourMap();
	}

	public void removeSprite(int image) {
		this.imageBeans.remove(image);
		rebuildColourMap();
	}

	public ImageBean getImageBean(int image) {
		return (ImageBean)this.imageBeans.get(image);
	}

	private int[] unpack(int rgb) {
		int[] val = new int[3];
		val[0] = (rgb >> 16 & 0xFF);
		val[1] = (rgb >> 8 & 0xFF);
		val[2] = (rgb & 0xFF);
		return val;
	}

	public void writeImageGroup() {
		for (int index = 0; index < this.imageBeans.size(); index++) {
			
		}
	}

	public int getImageBeanCount() {
		return this.imageBeans.size();
	}

	public BufferedImage getImage(int indice) {
		return toImage((ImageBean) this.imageBeans.get(indice));
	}

	public BufferedImage toImage(ImageBean i) {
		BufferedImage image = new BufferedImage(i.getWidth(), i.getHeight(), 1);
		int[] pixels = i.getPixels();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				int rgb = pixels[(x + y * image.getWidth())];
				if (rgb == 0) {
					rgb = 16711935;
				}
				image.setRGB(x, y, rgb);
			}
		}
		return image;
	}
}