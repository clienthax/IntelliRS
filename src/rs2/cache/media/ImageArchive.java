package rs2.cache.media;

import java.io.IOException;
import java.util.ArrayList;

import rs2.cache.Archive;
import rs2.io.ExtendedByteArrayOutputStream;
import rs2.util.DataUtils;

public class ImageArchive {

	private static final String[] knownExceptions = { "index.dat", "title.dat" };
	private Archive jagArchive;
	private ArrayList<ImageGroup> images = new ArrayList<ImageGroup>();

	public ImageArchive(Archive jagArchive) {
		this.jagArchive = jagArchive;
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
		this.jagArchive.removeFile(i);
	}

	public void addImage(int hash, ImageGroup g) {
		this.images.add(0, g);
		this.jagArchive.addFileAt(0, hash, new byte[0]);
	}

	public int countImages() {
		return this.images.size();
	}

	public byte[] repackArchive() throws IOException {
		int x = 0;
		ExtendedByteArrayOutputStream indexBuf = new ExtendedByteArrayOutputStream();
		for (int i = 0; i < this.jagArchive.getFileCount(); i++) {
			int hash = this.jagArchive.getHashAt(i);
			if (isValid(hash)) {
				((ImageGroup)this.images.get(x)).appendThisIndex(indexBuf);
				this.jagArchive.updateFile(i, ((ImageGroup)this.images.get(x++)).packData());
			}
		}
		indexBuf.close();
		this.jagArchive.updateFile(this.jagArchive.indexOf("index.dat"), indexBuf.toByteArray());
		return this.jagArchive.recompile();
	}
}