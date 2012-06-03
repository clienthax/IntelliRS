package rs2.cache;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import rs2.constants.Constants;
import rs2.util.DataUtils;
import rs2.util.io.ByteArray;

public final class ResourceCache {

	ArrayList<ByteArray> files = new ArrayList<ByteArray>();
	private static final byte[] buffer = new byte[520];
	private final RandomAccessFile data;
	private final RandomAccessFile index;
	private final int id;

	/**
	 * Creates a cache hook, to put and get files from the cache.
	 * @param data The data file.
	 * @param index The index file.
	 * @param id The id of the cache index.
	 */
	public ResourceCache(RandomAccessFile data, RandomAccessFile index, int id) throws IOException {
		this.id = id;
		this.data = data;
		this.index = index;
		try {
			for (int file = 0; file < index.length() / 6L; file++) {
				byte[] fileData = null;
				fileData = get(file);
				if (fileData != null) {
					this.files.add(new ByteArray(fileData));
				} else {
					this.files.add(null);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the number of files in the cache index.
	 * @return
	 */
	public long getFileCount() {
		return files.size();
	}

	/**
	 * Dumps the files from the cache index.
	 */
	public void dump() {
		File directory = new File(Constants.getCacheDirectory() + "index" + id);
		if (!directory.exists()) {
			if (!directory.mkdir()) {
				System.out.println("Failed to create directory: " + directory.getAbsolutePath());
				return;
			}
		}
		for (int index = 0; index < getFileCount(); index++) {
			byte[] data = get(index);
			DataUtils.writeFile(data, directory.getAbsolutePath() + System.getProperty("file.separator") + index + ".dat");
		}
	}

	/**
	 * Gets some data from the cache.
	 * @param requestedFileID The file ID to retrieve.
	 * @return The data requested, or null if an error occured.
	 */
	public synchronized byte[] get(int requestFileId) {
		try {
			seek(index, requestFileId * 6);
			for (int offset = 0, numBytesRead; offset < 6; offset += numBytesRead) {
				numBytesRead = index.read(buffer, offset, 6 - offset);
				if (numBytesRead == -1) {
					return null;
				}
			}

			int fileLength = ((buffer[0] & 0xff) << 16) + ((buffer[1] & 0xff) << 8) + (buffer[2] & 0xff);
			int fileIndex = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);
			if (fileIndex <= 0 || (long) fileIndex > data.length() / 520L) {
				return null;
			}
			byte fileData[] = new byte[fileLength];
			int bytesRead = 0;
			for (int index1 = 0; bytesRead < fileLength; index1++) {
				if (fileIndex == 0) {
					return null;
				}
				seek(data, fileIndex * 520);
				int dataLeft = fileLength - bytesRead;
				if (dataLeft > 512) {
					dataLeft = 512;
				}
				for (int numBytesRead = 0, offset; numBytesRead < dataLeft + 8; numBytesRead += offset) {
					offset = data.read(buffer, numBytesRead, (dataLeft + 8) - numBytesRead);
					if (offset == -1) {
						return null;
					}
				}
				int fileId = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
				int l2 = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
				int dataFileIndex = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);
				int version = buffer[7] & 0xff;
				if (fileId != requestFileId || l2 != index1 || version != this.id) {
					return null;
				}
				if (dataFileIndex < 0 || (long) dataFileIndex > data.length() / 520L) {
					return null;
				}
				for (int index2 = 0; index2 < dataLeft; index2++) {
					fileData[bytesRead++] = buffer[index2 + 8];
				}
				fileIndex = dataFileIndex;
			}
			return fileData;
		} catch (IOException _ex) {
			return null;
		}
	}

	/**
	 * Essentially this creates the entry if it doesn't exist already,
	 * while performing a "put" operation and adding the data to the cache.
	 * @param fileIndex The file index.
	 * @param fileData The file data.
	 * @param requestedFileID The file ID.
	 * @return Whether the put operation was succesful or not.
	 */
	public synchronized boolean put(int requestedFileId, int fileIndex, byte fileData[]) {
		boolean exists = put(requestedFileId, fileIndex, fileData, true);
		if (!exists) {
			exists = put(requestedFileId, fileIndex, fileData, false);
		}
		return exists;
	}

	/**
	 * Puts a file into the cache.
	 * @param fileIndex The file index.
	 * @param fileData The file data.
	 * @param exists Whether or not the file exists.
	 * @param requestedFileID The file id.
	 * @return Whether the put operation was succesful or not.
	 */
	public synchronized boolean put(int requestedFileId, int fileIndex, byte fileData[], boolean exists) {
		try {
			int fileLength;
			if (exists) {
				seek(index, requestedFileId * 6);
				for (int offset = 0, numBytesRead; offset < 6; offset += numBytesRead) {
					numBytesRead = index.read(buffer, offset, 6 - offset);
					if (numBytesRead == -1) {
						return false;
					}
				}

				fileLength = ((buffer[3] & 0xff) << 16) + ((buffer[4] & 0xff) << 8) + (buffer[5] & 0xff);
				if (fileLength <= 0 || (long) fileLength > data.length() / 520L) {
					return false;
				}
			} else {
				fileLength = (int) ((data.length() + 519L) / 520L);
				if (fileLength == 0) {
					fileLength = 1;
				}
			}
			buffer[0] = (byte) (fileIndex >> 16);
			buffer[1] = (byte) (fileIndex >> 8);
			buffer[2] = (byte) fileIndex;
			buffer[3] = (byte) (fileLength >> 16);
			buffer[4] = (byte) (fileLength >> 8);
			buffer[5] = (byte) fileLength;
			seek(index, requestedFileId * 6);
			index.write(buffer, 0, 6);
			for (int index1 = 0, writeOffset = 0; writeOffset < fileIndex; index1++) {
				int dataFileIndex = 0;
				if (exists) {
					seek(data, fileLength * 520);
					int offset = 0;
					for (int numBytesRead = 0; offset < 8; offset += numBytesRead) {
						numBytesRead = data.read(buffer, offset, 8 - offset);
						if (numBytesRead == -1) {
							break;
						}
					}
					if (offset == 8) {
						int fileId = ((buffer[0] & 0xff) << 8) + (buffer[1] & 0xff);
						int j3 = ((buffer[2] & 0xff) << 8) + (buffer[3] & 0xff);
						dataFileIndex = ((buffer[4] & 0xff) << 16) + ((buffer[5] & 0xff) << 8) + (buffer[6] & 0xff);
						int version = buffer[7] & 0xff;
						if (fileId != requestedFileId || j3 != index1 || version != this.id) {
							return false;
						}
						if (dataFileIndex < 0 || (long) dataFileIndex > data.length() / 520L) {
							return false;
						}
					}
				}
				if (dataFileIndex == 0) {
					exists = false;
					dataFileIndex = (int) ((data.length() + 519L) / 520L);
					if (dataFileIndex == 0) {
						dataFileIndex++;
					}
					if (dataFileIndex == fileLength) {
						dataFileIndex++;
					}
				}
				if (fileIndex - writeOffset <= 512) {
					dataFileIndex = 0;
				}
				buffer[0] = (byte) (requestedFileId >> 8);
				buffer[1] = (byte) requestedFileId;
				buffer[2] = (byte) (index1 >> 8);
				buffer[3] = (byte) index1;
				buffer[4] = (byte) (dataFileIndex >> 16);
				buffer[5] = (byte) (dataFileIndex >> 8);
				buffer[6] = (byte) dataFileIndex;
				buffer[7] = (byte) id;
				seek(data, fileLength * 520);
				data.write(buffer, 0, 8);
				int dataLeft = fileIndex - writeOffset;
				if (dataLeft > 512) {
					dataLeft = 512;
				}
				data.write(fileData, writeOffset, dataLeft);
				writeOffset += dataLeft;
				fileLength = dataFileIndex;
			}

			return true;
		} catch (IOException _ex) {
			return false;
		}
	}

	/**
	 * Seeks the specified file, and ensures the position is within expected parameters.
	 * @param file The random access file to seek on. This is either the index or the data file.
	 * @param position The position to seek to.
	 * @throws IOException If the method was unable to seek on the RAF.
	 */
	private synchronized void seek(RandomAccessFile file, int position) throws IOException {
		if (Constants.LIMIT_SIZE) {
			if (position < 0 || position > 0x3c00000) {
				System.out.println("(Badseek) position:" + position + " length:" + file.length());
				position = 0x3c00000;
				try {
					Thread.sleep(1000L);
				} catch (Exception _ex) {
				}
			}
		}
		file.seek(position);
	}

	public ArrayList<ByteArray> getFiles() {
		return this.files;
	}

	public void updateFile(int id, byte[] data) {
		this.files.set(id, new ByteArray(data));
	}

	public void addFile(byte[] data) {
		this.files.add(new ByteArray(data));
	}

	public void removeFile(int id) {
		this.files.remove(id);
	}

}
