package com.galkon.cache;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import com.galkon.util.ByteArray;
import com.galkon.util.exception.CacheException;


public class CacheIndice {

	public CacheIndice(RandomAccessFile dataFile, RandomAccessFile indexFile, int id) {
		this.maxFileSize = 99999999;
		this.id = id;
		this.dataFile = dataFile;
		this.indexFile = indexFile;
		try {
			for (int i = 0; i < indexFile.length() / 6L; i++) {
				byte[] data = null;
				data = get(i);
				if (data != null)
					this.files.add(new ByteArray(data));
				else
					this.files.add(null);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getFileCount() {
		return this.files.size();
	}

	public synchronized byte[] get(int requestedId) {
		try {
			seekTo(this.indexFile, requestedId * 6);
			for (int offset = 0, numBytesRead = 0; offset < 6; offset += numBytesRead) {
				numBytesRead = this.indexFile.read(buffer, offset, 6 - offset);
				if (numBytesRead == -1) {
					throw new CacheException("Error reading file");
				}
			}
			int fileLength = ((buffer[0] & 0xFF) << 16) + ((buffer[1] & 0xFF) << 8) + (buffer[2] & 0xFF);
			int fileIndex = ((buffer[3] & 0xFF) << 16) + ((buffer[4] & 0xFF) << 8) + (buffer[5] & 0xFF);
			if ((fileLength < 0) || (fileLength > this.maxFileSize)) {
				throw new CacheException("File size too large");
			}
			if (fileIndex <= 0 || (long) fileIndex > this.dataFile.length() / 520L) {
				if (fileIndex != 0) {
					throw new CacheException("Index " + fileIndex + " extends file bounds");
				}
			}
			byte[] fileData = new byte[fileLength];
			int bytesRead = 0;
			int index = 0;
			while (fileLength > bytesRead) {
				if (fileIndex == 0) {
					throw new CacheException("Invalid index: " + fileIndex);
				}
				seekTo(this.dataFile, fileIndex * 520);
				int dataLeft = fileLength - bytesRead;
				if (dataLeft > 512) {
					dataLeft = 512;
				}
				for (int offset = 0, numBytesRead = 0; offset < dataLeft + 8; offset += numBytesRead) {
					numBytesRead = this.dataFile.read(buffer, offset, dataLeft + 8 - offset);
					if (numBytesRead == -1) {
						throw new CacheException("Error reading file");
					}
				}
				int fileId = ((buffer[0] & 0xFF) << 8) + (buffer[1] & 0xFF);
				int part = ((buffer[2] & 0xFF) << 8) + (buffer[3] & 0xFF);
				int dataFileIndex = ((buffer[4] & 0xFF) << 16) + ((buffer[5] & 0xFF) << 8) + (buffer[6] & 0xFF);
				int id = buffer[7] & 0xFF;
				if (fileId != requestedId) {
					throw new CacheException("Index file id didn't match expected file number");
				}
				if (part != index)
					throw new CacheException("Index file part number didn't match expected file part number");
				if (id != this.id) {
					throw new CacheException("Index ID didn't match expected ID");
				}
				if ((dataFileIndex < 0) || (dataFileIndex > this.dataFile.length() / 520L)) {
					throw new CacheException("Index extends cache bounds!");
				}
				for (int index2 = 0; index2 < dataLeft; index2++) {
					fileData[(bytesRead++)] = buffer[(index2 + 8)];
				}
				fileIndex = dataFileIndex;
				index++;
			}
			return fileData;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized boolean put(int index, int file, byte[] data, boolean update) throws IOException {
		boolean exists = put(index, file, data, true, update);
		if (!exists) {
			exists = put(index, file, data, false, update);
		}
		return exists;
	}

	private synchronized boolean put(int requestedFileId, int fileIndex, byte[] data, boolean exists, boolean update) throws IOException {
		int fileLength;
		if (exists) {
			seekTo(this.indexFile, requestedFileId * 6);
			int fileIndex_;
			int offset;
			for (fileIndex_ = 0; fileIndex_ < 6; fileIndex_ += offset) {
				offset = this.indexFile.read(buffer, fileIndex_, 6 - fileIndex_);
				if (offset == -1) {
					return false;
				}
			}
			fileIndex_ = ((buffer[0] & 0xFF) << 16) + ((buffer[1] & 0xFF) << 8) + (buffer[2] & 0xFF);
			fileLength = ((buffer[3] & 0xFF) << 16) + ((buffer[4] & 0xFF) << 8) + (buffer[5] & 0xFF);
			if (update) {
				updateFile(requestedFileId, data);
			}
			if ((fileLength <= 0) || (fileLength <= this.dataFile.length() / 520L)) {
				
			}
		} else {
			fileLength = (int) ((this.dataFile.length() + 519L) / 520L);
			if (fileLength == 0) {
				fileLength = 1;
			}
			addFile(data);
		}
		buffer[0] = (byte) (fileIndex >> 16);
		buffer[1] = (byte) (fileIndex >> 8);
		buffer[2] = (byte) fileIndex;
		buffer[3] = (byte) (fileLength >> 16);
		buffer[4] = (byte) (fileLength >> 8);
		buffer[5] = (byte) fileLength;
		seekTo(this.indexFile, requestedFileId * 6);
		this.indexFile.write(buffer, 0, 6);
		int writeOffset = 0;
		for (int index = 0; writeOffset < fileIndex; index++) {
			int dataFileIndex = 0;
			if (exists) {
				seekTo(this.dataFile, fileLength * 520);
				int numBytesRead;
				int offset;
				for (offset = 0; offset < 8; offset += numBytesRead) {
					numBytesRead = this.dataFile.read(buffer, offset, 8 - offset);
					if (numBytesRead == -1) {
						break;
					}
				}
				if (offset == 8) {
					int fileId = ((buffer[0] & 0xFF) << 8) + (buffer[1] & 0xFF);
					int part = ((buffer[2] & 0xFF) << 8) + (buffer[3] & 0xFF);
					dataFileIndex = ((buffer[4] & 0xFF) << 16) + ((buffer[5] & 0xFF) << 8) + (buffer[6] & 0xFF);
					int id = buffer[7] & 0xFF;
					if (fileId != requestedFileId) {
						//throw new CacheException("Sector file number didn't match expected file number");
					}
					if (part != index) {
						//throw new CacheException("Sector file part number didn't match expected file part number");
					}
					if (id != this.id) {
						//throw new CacheException("Sector cache number didn't match expected cache number");
					}
					if ((dataFileIndex < 0) || (dataFileIndex > this.dataFile.length() / 520L)) {
						//throw new CacheException("Sector extends cache bounds!");
					}
				}
			}
			if (dataFileIndex == 0) {
				exists = false;
				dataFileIndex = (int) ((this.dataFile.length() + 519L) / 520L);
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
			buffer[2] = (byte) (index >> 8);
			buffer[3] = (byte) index;
			buffer[4] = (byte) (dataFileIndex >> 16);
			buffer[5] = (byte) (dataFileIndex >> 8);
			buffer[6] = (byte) dataFileIndex;
			buffer[7] = (byte) this.id;
			seekTo(this.dataFile, fileLength * 520);
			this.dataFile.write(buffer, 0, 8);
			int dataLeft = fileIndex - writeOffset;
			if (dataLeft > 512) {
				dataLeft = 512;
			}
			this.dataFile.write(data, writeOffset, dataLeft);
			writeOffset += dataLeft;
			fileLength = dataFileIndex;
		}
		return true;
	}

	public synchronized void seekTo(RandomAccessFile file, int id) throws IOException {
		file.seek(id);
	}

	public ArrayList<ByteArray> getFiles() {
		return this.files;
	}

	public void updateFile(int index, byte[] data) {
		this.files.set(index, new ByteArray(data));
	}

	public void addFile(byte[] data) {
		this.files.add(new ByteArray(data));
	}

	public void removeFile(int index) {
		this.files.remove(index);
	}

	ArrayList<ByteArray> files = new ArrayList<ByteArray>();
	static byte[] buffer = new byte[520];
	RandomAccessFile dataFile;
	RandomAccessFile indexFile;
	int id;
	int maxFileSize;
}