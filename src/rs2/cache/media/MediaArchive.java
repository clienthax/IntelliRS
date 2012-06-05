package rs2.cache.media;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashSet;
import javax.imageio.ImageIO;

import rs2.cache.Archive;
import rs2.constants.Constants;
import rs2.graphics.RSImage;
import rs2.util.DataUtils;

/**
 * Handles everything related to the media archive.
 * @author Galkon
 */
public class MediaArchive {

	/**
	 * A list of known image archives.
	 */
	public LinkedHashSet<String> knownArchives;
	public LinkedHashSet<String> foundArchives;

	/**
	 * A list of known hashes.
	 */
	private int[] knownHashes;

	/**
	 * The Archive for media (media.jag).
	 */
	public Archive archive;

	/**
	 * The ImageArchive instance used for the media archive.
	 */
	public ImageArchive imageArchive;

	/**
	 * Returns the ImageArchive instance.
	 * @return
	 */
	public ImageArchive getImageArchive() {
		return imageArchive;
	}

	/**
	 * Initializes the MediaArchive instance.
	 * @param archive
	 */
	public MediaArchive(Archive archive) {
		this.archive = archive;
		updateKnown();
		imageArchive = new ImageArchive(archive);
		log("Archive initialized.");
	}

	/**
	 * Dumps the media archive to the cache folder/rsimg.
	 */
	public void dump() {
		updateKnown();
		File imgdir = new File(Constants.getCacheDirectory() + "rsimg" + System.getProperty("file.separator"));
		if (!imgdir.exists()) {
			imgdir.mkdir();
		}
		int count = imageArchive.countImages();
		int total = count;
		try {
			for (int index = 0; index < count; index++) {
				File directory = new File(Constants.getCacheDirectory() + "rsimg" + System.getProperty("file.separator") + getArchiveList()[index]);
				if (!directory.exists()) {
					directory.mkdir();
				}
				for (int index2 = 0; index2 < imageArchive.getImage(index).getImageBeanCount(); index2++) {
					BufferedImage bi = imageArchive.getImage(index).getImage(index2);
					ImageIO.write(bi, "png", new File(directory + System.getProperty("file.separator") + index2 + ".png"));
					total++;
				}
			}
			log("Dumped " + total + " indices.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets all of the images from the specified archive as an RSImage array.
	 * @param archive
	 * @return
	 */
	public RSImage[] archiveToRSImages(String archive) {
		if (imageArchive == null) {
			return null;
		}
		int archiveIndex = getIndexForName(archive);
		int count = imageArchive.getImage(archiveIndex).getImageBeanCount();
		RSImage[] images = new RSImage[count];
		for (int index = 0; index < count; index++) {
			images[index] = new RSImage(imageArchive.getImage(archiveIndex).getImage(index));
		}
		return images;
	}

	/**
	 * Gets all of the images from the specified archive as an RSImage array.
	 * @param archive
	 * @return
	 */
	public BufferedImage[] archiveToBufferedImages(String archive) {
		if (imageArchive == null) {
			return null;
		}
		int archiveIndex = getIndexForName(archive);
		int count = imageArchive.getImage(archiveIndex).getImageBeanCount();
		BufferedImage[] images = new BufferedImage[count];
		for (int index = 0; index < count; index++) {
			images[index] = imageArchive.getImage(archiveIndex).getImage(index);
		}
		return images;
	}

	/**
	 * Gets all of the images from the specified archive as an RSImage array.
	 * @param archive
	 * @return
	 */
	public Image[] archiveToImages(String archive) {
		if (imageArchive == null) {
			return null;
		}
		int archiveIndex = getIndexForName(archive);
		int count = imageArchive.getImage(archiveIndex).getImageBeanCount();
		BufferedImage[] images = new BufferedImage[count];
		for (int index = 0; index < count; index++) {
			images[index] = imageArchive.getImage(archiveIndex).getImage(index);
		}
		return images;
	}

	/**
	 * Gets the index for the specified archive name.
	 * @param archive
	 * @return
	 */
	public int getIndexForName(String archive) {
		String[] list = getArchiveList();
		for (int index = 0; index < imageArchive.countImages(); index++) {
			if (archive.equalsIgnoreCase(list[index])) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Adds an archive name to the known archives.
	 */
	public void addKnownArchive(String name) {
		if (foundArchives == null) {
			foundArchives = new LinkedHashSet<String>();
		}
		foundArchives.add(name + ".dat");
	}

	/**
	 * Updates the known archives and the known hashes.
	 */
	public void updateKnown() {
		getKnownArchives();
		getKnownHashes();
	}

	/**
	 * Gets all of the known archives for the media archive.
	 */
	public void getKnownArchives() {
		if (archive == null) {
			return;
		}
		int total = archive.getFileCount() - 1;
		for (int index = 0; index < archive.getFileCount() - 1; index++) {
			try {
				BufferedReader in = new BufferedReader(new FileReader("medianames.txt"));
				knownArchives = new LinkedHashSet<String>();
				String s;
				while ((s = in.readLine()) != null) {
					knownArchives.add(s);
				}
				in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (foundArchives != null) {
			log("Found " + knownArchives.size() + " known archives.");
			knownArchives.addAll(foundArchives);
			log("Found " + foundArchives.size() + " interface archives.");
			log("Found " + knownArchives.size() + " archives out of " + total + " archives.");
		}
	}

	/**
	 * Gets all of the known hashes for the media archive.
	 */
	public void getKnownHashes() {
		knownHashes = new int[knownArchives.size()];
		for (int index = 0; index < knownArchives.size(); index++) {
			knownHashes[index] = DataUtils.getHash((String) knownArchives.toArray()[index]);
		}
	}

	/**
	 * Lists all media archive files as a string array.
	 * @return
	 */
	public String[] getArchiveList() {
		int numImages = imageArchive.countImages();
		String[] values = new String[numImages];
		int count = 0;
		for (int index = 0; index < numImages; index++) {
			int identifier = archive.getHashAt(index);
			if (imageArchive.isValid(identifier)) {
				String fileName = String.valueOf(identifier);
				for (int id = 0; id < knownArchives.size(); id++) {
					if (identifier == knownHashes[id]) {
						fileName = (String) knownArchives.toArray()[id];
						break;
					}
				}
				values[count++] = fileName;
			}
		}
		return values;
	}

	/**
	 * Logs the object with the label of media archive.
	 * @param o
	 */
	public void log(Object o) {
		System.out.println("[Media Archive]: " + o);
	}

}
