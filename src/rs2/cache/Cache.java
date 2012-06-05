package rs2.cache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import rs2.constants.Constants;
import rs2.util.ByteArray;

/**
 * @author tom
 */
public class Cache {

    /**
     * Initializes and reads the cache files.
     */
    public Cache() {
    	try {
	        File[] files = new File(Constants.getCacheDirectory()).listFiles();
	        dataFile = findDataFile(files);
	        indexFiles = findIndexFiles(files);
	        java.util.Arrays.sort(indexFiles);
	        if (dataFile == null) {
	            throw new IOException("Unable to locate cache data file!\nCorrect cache loaded?");
	        }
	        if (indexFiles.length == 0) {
	            throw new IOException("Unable to locate cache index files!\nCorrect cache loaded?");
	        }
	        indices = new ArrayList<CacheIndice>();
	        RandomAccessFile data = new RandomAccessFile(dataFile, "rw");
	        for (int i = 0; i < indexFiles.length; i++) {
	            CacheIndice indice = new CacheIndice(data, new RandomAccessFile(indexFiles[i], "rw"), i+1);
	            indices.add(indice);
	        }
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    /**
     * Rebuilds the cache files.
     */
    public void rebuildCache() {
    	try {
	        for (int i = 0; i < indexFiles.length; i++) {
	            CacheIndice index = indices.get(i);
	            ArrayList<ByteArray> files = index.getFiles();
	            int currentFile = 0;
	            for (ByteArray data : files) {
	                int file = currentFile++;
	                if (data != null) {
	                    index.put(file, data.length, data.getBytes(), false);
	                }
	            }
	        }
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    /**
     * Gets the cache index specified.
     * @param i
     * @return
     */
    public CacheIndice getIndice(int i) {
        return indices.get(i);
    }

    /**
     * Gets the cache indices.
     * @return
     */
    public ArrayList<CacheIndice> getIndices() {
        return indices;
    }

    /**
     * Gets the index file name at the specified index.
     * @param index
     * @return
     */
    public String getIndexFile(int index) {
        return indexFiles[index];
    }

    /**
     * Gets the index file names.
     * @return
     */
    public String[] getIndexFiles() {
        return indexFiles;
    }

    /**
     * Gets the data file name.
     * @return
     */
    public String getDataFile() {
        return dataFile;
    }

    /**
     * Finds the cache data file.
     * @param files
     * @return
     */
    public String findDataFile(File[] files) {
        for (File file : files) {
            String s = file.getAbsolutePath();
            if (s.endsWith(".dat")) {
                if (s.contains("file_cache")) {
                    return s;
                }
            }
        }
        return null;
    }

    /**
     * Finds the cache index files.
     * @param files
     * @return
     */
    public String[] findIndexFiles(File[] files) {
        ArrayList<String> indices = new ArrayList<String>();
        for (File file : files) {
            String s = file.getAbsolutePath();
            if (s.contains(".idx")) {
                if (s.contains("cache")) {
                    indices.add(s);
                }
            }
        }
        String[] s = new String[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            s[i] = indices.get(i);
        }
        return s;
    }

    private ArrayList<CacheIndice> indices;
    private String[] indexFiles;
    private String dataFile;
}
