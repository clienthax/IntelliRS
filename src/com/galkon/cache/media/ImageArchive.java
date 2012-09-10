package com.galkon.cache.media;

import java.io.IOException;
import java.util.ArrayList;

import com.galkon.cache.Archive;
import com.galkon.io.RSByteArrayOutputStream;
import com.galkon.util.DataUtils;


public class ImageArchive {

	private static final String[] knownExceptions = { "index.dat", "title.dat" };
	private Archive archive;
	private ArrayList<ImageGroup> images = new ArrayList<ImageGroup>();

	public ImageArchive(Archive jagArchive) {
		this.archive = jagArchive;
		byte[] indexData = jagArchive.getFile("index.dat");
		for (int index = 0; index < jagArchive.getFileCount(); index++) {
			int hash = jagArchive.getHashAt(index);
			if (isValid(hash)) {
				this.images.add(new ImageGroup(indexData, jagArchive.getFileAt(index), true));
			}
		}
	}

	public boolean isValid(int hash) {
		for (String s : knownExceptions) {
			if (hash == DataUtils.getHash(s)) {
				return false;
			}
		}
		return true;
	}

	public ImageGroup getImage(int i) {
		return (ImageGroup) this.images.get(i);
	}

	public void removeImage(int i) {
		this.images.remove(i);
		this.archive.removeFile(i);
	}

	public void addImage(int hash, ImageGroup g) {
		this.images.add(0, g);
		this.archive.addFileAt(0, hash, new byte[0]);
	}

	public int countImages() {
		return this.images.size();
	}

	public byte[] repackArchive() throws IOException {
		int x = 0;
		RSByteArrayOutputStream indexBuf = new RSByteArrayOutputStream();
		for (int i = 0; i < this.archive.getFileCount(); i++) {
			int hash = this.archive.getHashAt(i);
			if (isValid(hash)) {
				((ImageGroup)this.images.get(x)).appendThisIndex(indexBuf);
				this.archive.updateFile(i, ((ImageGroup)this.images.get(x++)).packData());
			}
		}
		indexBuf.close();
		this.archive.updateFile(this.archive.indexOf("index.dat"), indexBuf.toByteArray());
		return this.archive.recompile();
	}
}