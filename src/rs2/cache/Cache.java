package rs2.cache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

import rs2.constants.Constants;
import rs2.util.io.ByteArray;

public class Cache {

	private ArrayList<ResourceCache> indices;
	private String[] indexFiles;
	private String dataFile;

	/**
	 * Loads and reads the cache data and cache indices.
	 */
	public Cache() {
		try {
			File[] files = new File(Constants.getCacheDirectory()).listFiles();
			dataFile = findDataFile(files);
			indexFiles = findIndexFiles(files);
			Arrays.sort(indexFiles);
			if (dataFile == null) {
				throw new IOException("Unable to locate cache data file!\nCorrect cache loaded?");
			}
			if (indexFiles.length == 0) {
				throw new IOException("Unable to locate cache index files!\nCorrect cache loaded?");
			}
			indices = new ArrayList<ResourceCache>();
			RandomAccessFile data = new RandomAccessFile(dataFile, "rw");
			for (int i = 0; i < indexFiles.length; i++) {
				ResourceCache indice = new ResourceCache(data, new RandomAccessFile(indexFiles[i], "rw"), i + 1);
				indices.add(indice);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Rebuilds the cache.
	 * @throws IOException
	 */
	public void rebuildCache() throws IOException {
		ResourceCache index;
		int currentFile;
		for (int i = 0; i < indexFiles.length; i++) {
			index = (ResourceCache) indices.get(i);
			currentFile = 0;
			for (ByteArray data : index.getFiles()) {
				int file = currentFile++;
				if (data != null) {
					index.put(file, data.length, data.getBytes(), false);
				}
			}
		}
	}

	public ResourceCache getIndice(int i) {
		return (ResourceCache) indices.get(i);
	}

	public ArrayList<ResourceCache> getIndices() {
		return indices;
	}

	public String getIndexFile(int i) {
		return indexFiles[i];
	}

	public String[] getIndexFiles() {
		return indexFiles;
	}

	public String getDataFile() {
		return dataFile;
	}

	public String findDataFile(File[] files) {
		for (File file : files) {
			String path = file.getAbsolutePath();
			if ((path.endsWith(".dat")) && (path.contains("file_cache"))) {
				return path;
			}
		}
		return null;
	}

	public String[] findIndexFiles(File[] files) {
		ArrayList<String> indices = new ArrayList<String>();
		for (File file : files) {
			String path = file.getAbsolutePath();
			if ((!path.contains(".idx")) || (!path.contains("cache"))) {
				continue;
			}
			indices.add(path);
		}
		String[] fileNames = new String[indices.size()];
		for (int index = 0; index < indices.size(); index++) {
			fileNames[index] = ((String)indices.get(index));
		}
		return fileNames;
	}
}