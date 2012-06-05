package rs2.cache;

import java.io.IOException;
import java.util.ArrayList;

import rs2.io.ExtendedByteArrayOutputStream;
import rs2.io.JagexBuffer;
import rs2.io.bzip2.BZip2Decompressor;
import rs2.util.ByteArray;
import rs2.util.DataUtils;

/**
 * @author tom
 */
public class Archive {

	private ArrayList<ArchiveFile> files = new ArrayList<ArchiveFile>();
	byte[] finalBuffer;
	private int totalFiles;
	boolean decompressed;

	/**
	 * Reads and initializes the archive.
	 * @param compressedData
	 */
	public Archive(byte compressedData[]) {
		JagexBuffer buffer = new JagexBuffer(compressedData);
		int decompressedSize = buffer.get3Bytes();
		int compressedSize = buffer.get3Bytes();
		if (compressedSize != decompressedSize) {
			byte decompressedData[] = new byte[decompressedSize];
			BZip2Decompressor.decompressBuffer(decompressedData, decompressedSize, compressedData, compressedSize, 6);
			finalBuffer = decompressedData;
			buffer = new JagexBuffer(finalBuffer);
			decompressed = true;
		} else {
			finalBuffer = compressedData;
			decompressed = false;
		}
		totalFiles = buffer.getUnsignedShort();
		int offset = buffer.offset + totalFiles * 10;
		for (int index = 0; index < totalFiles; index++) {
			int hash = buffer.getInt();
			int size = buffer.get3Bytes();
			int cSize = buffer.get3Bytes();
			files.add(new ArchiveFile(hash, size, cSize, offset, new ByteArray(finalBuffer), decompressed));
			offset += cSize;
		}
	}

	/**
	 * Recompiles and returns the archive data as a byte array.
	 * @return
	 * @throws IOException
	 */
	public byte[] recompile() throws IOException {
		byte[] compressedWhole = compileUncompressed();
		int compressedWholeDecompressedSize = compressedWhole.length;
		compressedWhole = DataUtils.compressBZip2(compressedWhole);
		int compressedWholeSize = compressedWhole.length;
		byte[] compressedIndividually = compileCompressed();
		int compressedIndividuallySize = compressedIndividually.length;
		boolean compressedAsWhole = false;
		if (compressedWholeSize < compressedIndividuallySize) {
			compressedAsWhole = true;
		}
		ExtendedByteArrayOutputStream finalBuf = new ExtendedByteArrayOutputStream();
		if (compressedAsWhole) {
			finalBuf.put3Bytes(compressedWholeDecompressedSize);
			finalBuf.put3Bytes(compressedWholeSize);
			finalBuf.write(compressedWhole);
		} else {
			finalBuf.put3Bytes(compressedIndividuallySize);
			finalBuf.put3Bytes(compressedIndividuallySize);
			finalBuf.write(compressedIndividually);
		}
		finalBuf.close();
		return finalBuf.toByteArray();
	}

	/**
	 * Compiles and returns the archive's uncompressed data.
	 * @return
	 * @throws IOException
	 */
	private byte[] compileUncompressed() throws IOException {
		ExtendedByteArrayOutputStream fileData = new ExtendedByteArrayOutputStream();
		for (int i = 0; i < totalFiles; i++) {
			files.get(i).setSize(files.get(i).getData().length);
			files.get(i).setCompressedSize(files.get(i).getData().length);
			fileData.write(files.get(i).getData());
		}
		fileData.close();
		ExtendedByteArrayOutputStream fileInfo = new ExtendedByteArrayOutputStream();
		fileInfo.putShort(totalFiles);
		for (int i = 0; i < totalFiles; i++) {
			fileInfo.putInt(files.get(i).getHash());
			fileInfo.put3Bytes(files.get(i).getSize());
			fileInfo.put3Bytes(files.get(i).getCompressedSize());
		}
		fileInfo.close();
		ExtendedByteArrayOutputStream finalBuffer = new ExtendedByteArrayOutputStream();
		finalBuffer.write(fileInfo.toByteArray());
		finalBuffer.write(fileData.toByteArray());
		finalBuffer.close();
		return finalBuffer.toByteArray();
	}

	/**
	 * Compiles and returns the archive's compressed data.
	 * @return
	 * @throws IOException
	 */
	private byte[] compileCompressed() throws IOException {
		ExtendedByteArrayOutputStream fileData = new ExtendedByteArrayOutputStream();
		for (int i = 0; i < totalFiles; i++) {
			files.get(i).setSize(files.get(i).data.length);
			byte[] compressed = DataUtils.compressBZip2(files.get(i).getData());
			files.get(i).setCompressedSize(compressed.length);
			fileData.write(compressed);
		}
		fileData.close();
		ExtendedByteArrayOutputStream fileInfo = new ExtendedByteArrayOutputStream();
		fileInfo.putShort(totalFiles);
		for (int i = 0; i < totalFiles; i++) {
			fileInfo.putInt(files.get(i).getHash());
			fileInfo.put3Bytes(files.get(i).getSize());
			fileInfo.put3Bytes(files.get(i).getCompressedSize());
		}
		fileInfo.close();
		ExtendedByteArrayOutputStream finalBuffer = new ExtendedByteArrayOutputStream();
		finalBuffer.write(fileInfo.toByteArray());
		finalBuffer.write(fileData.toByteArray());
		finalBuffer.close();
		return finalBuffer.toByteArray();
	}

	/**
	 * Gets the data of the file at the specified index.
	 * @param at
	 * @return
	 */
	public byte[] getFileAt(int at) {
		byte dataBuffer[] = new byte[files.get(at).getSize()];
		if (!decompressed) {
			BZip2Decompressor.decompressBuffer(dataBuffer, files.get(at).getSize(), finalBuffer, files.get(at).getCompressedSize(), files.get(at).offset);
		} else {
			System.arraycopy(finalBuffer, files.get(at).offset, dataBuffer, 0, files.get(at).getSize());
		}
		return dataBuffer;
	}

	/**
	 * Gets the data of the file for the specified hash.
	 * @param hash
	 * @return
	 */
	public byte[] getFile(int hash) {
		for (int index = 0; index < totalFiles; index++) {
			if (files.get(index).getHash() == hash) {
				return getFileAt(index);
			}
		}
		return null;
	}

	/**
	 * Gets the hash at the specified index.
	 * @param index
	 * @return
	 */
	public int getHashAt(int index) {
		return files.get(index).getHash();
	}

	/**
	 * Gets the decompressed size at the specified index.
	 * @param index
	 * @return
	 */
	public int getDecompressedSize(int index) {
		return files.get(index).getSize();
	}

	/**
	 * Gets the total number of files in the archive.
	 * @return
	 */
	public int getFileCount() {
		return totalFiles;
	}

	/**
	 * Gets the data of the file for the specified name.
	 * @param name
	 * @return
	 */
	public byte[] getFile(String name) {
		return getFile(DataUtils.getHash(name));
	}

	/**
	 * Renames the file at the specified index with a new hash.
	 * @param index
	 * @param hash
	 */
	public void renameFile(int index, int hash) {
		files.get(index).setHash(hash);
	}

	/**
	 * Updates the file at the specified index with new data.
	 * @param index
	 * @param data
	 */
	public void updateFile(int index, byte[] data) {
		files.get(index).setData(data);
	}

	/**
	 * Gets the index of the specified name.
	 * @param name
	 * @return
	 */
	public int indexOf(String name) {
		return indexOf(DataUtils.getHash(name));
	}

	/**
	 * Gets the index of the specified hash.
	 * @param hash
	 * @return
	 */
	public int indexOf(int hash) {
		for (int index = 0; index < totalFiles; index++) {
			if (files.get(index).getHash() == hash) {
				return index;
			}
		}
		return 0;
	}

	/**
	 * Removes the file at the specified index.
	 * @param index
	 */
	public void removeFile(int index) {
		files.remove(index);
		totalFiles--;
	}

	/**
	 * Adds a file that is given the specified hash and data.
	 * @param hash
	 * @param data
	 */
	public void addFile(int hash, byte[] data) {
		files.add(new ArchiveFile(hash, data.length, 0, 0, new ByteArray(data), true));
		totalFiles++;
	}

	/**
	 * Adds a file at the specified index that is given the specified hash and data.
	 * @param index
	 * @param hash
	 * @param data
	 */
	public void addFileAt(int index, int hash, byte[] data) {
		files.add(index, new ArchiveFile(hash, data.length, 0, 0, new ByteArray(data), true));
		totalFiles++;
	}
}