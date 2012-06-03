package rs2.cache;

import java.io.IOException;
import java.util.ArrayList;

import rs2.io.BZip2InputStream;
import rs2.io.ExtendedByteArrayOutputStream;
import rs2.io.JagexBuffer;
import rs2.util.DataUtils;
import rs2.util.io.ByteArray;

public class Archive {

	private ArrayList<ByteArray> files = new ArrayList<ByteArray>();
	byte[] finalBuffer;
	private int totalFiles;
	private ArrayList<Integer> identifiers = new ArrayList<Integer>();
	private ArrayList<Integer> decompressedSizes = new ArrayList<Integer>();
	private ArrayList<Integer> compressedSizes = new ArrayList<Integer>();
	private ArrayList<Integer> startOffsets = new ArrayList<Integer>();
	boolean compressedAsWhole;

	public Archive(byte[] data) {
		JagexBuffer buffer = new JagexBuffer(data);
		int decompressedSize = buffer.get3Bytes();
		int compressedSize = buffer.get3Bytes();
		if (compressedSize != decompressedSize) {
			byte[] abyte1 = new byte[decompressedSize];
			BZip2InputStream.resetAndRead(abyte1, decompressedSize, data, compressedSize, 6);
			finalBuffer = abyte1;
			buffer = new JagexBuffer(finalBuffer);
			compressedAsWhole = true;
		} else {
			finalBuffer = data;
			compressedAsWhole = false;
		}
		totalFiles = buffer.getUnsignedShort();
		int offset = buffer.offset + totalFiles * 10;
		for (int l = 0; l < totalFiles; l++) {
			identifiers.add(Integer.valueOf(buffer.getInt()));
			decompressedSizes.add(Integer.valueOf(buffer.get3Bytes()));
			compressedSizes.add(Integer.valueOf(buffer.get3Bytes()));
			startOffsets.add(Integer.valueOf(offset));
			offset += ((Integer)compressedSizes.get(l)).intValue();
			files.add(new ByteArray(getFileAt(l)));
		}
	}

	public byte[] recompile() {
		try {
			byte[] compressedWhole = compileUncompressed();
			int compressedWholeDecompressedSize = compressedWhole.length;
			compressedWhole = DataUtils.bz2Compress(compressedWhole);
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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] compileUncompressed() {
		try {
			ExtendedByteArrayOutputStream fileBuf = new ExtendedByteArrayOutputStream();
			for (int i = 0; i < totalFiles; i++) {
				decompressedSizes.set(i, Integer.valueOf(((ByteArray)files.get(i)).length));
				compressedSizes.set(i, Integer.valueOf(((ByteArray)files.get(i)).length));
				fileBuf.write(((ByteArray)files.get(i)).getBytes());
			}
			byte[] filesSection = fileBuf.toByteArray();
			fileBuf.close();
			ExtendedByteArrayOutputStream fileInfo = new ExtendedByteArrayOutputStream();
			fileInfo.putShort(totalFiles);
			for (int i = 0; i < totalFiles; i++) {
				fileInfo.putInt(((Integer)identifiers.get(i)).intValue());
				fileInfo.put3Bytes(((Integer)decompressedSizes.get(i)).intValue());
				fileInfo.put3Bytes(((Integer)compressedSizes.get(i)).intValue());
			}
			byte[] fileInfoSection = fileInfo.toByteArray();
			fileInfo.close();
			ExtendedByteArrayOutputStream finalBuffer = new ExtendedByteArrayOutputStream();
			finalBuffer.write(fileInfoSection);
			finalBuffer.write(filesSection);
			finalBuffer.close();
			return finalBuffer.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private byte[] compileCompressed() throws IOException {
		ExtendedByteArrayOutputStream fileBuf = new ExtendedByteArrayOutputStream();
		for (int i = 0; i < totalFiles; i++) {
			decompressedSizes.set(i, Integer.valueOf(((ByteArray)files.get(i)).length));
			byte[] compressed = DataUtils.bz2Compress(((ByteArray)files.get(i)).getBytes());
			compressedSizes.set(i, Integer.valueOf(compressed.length));
			fileBuf.write(compressed);
		}
		byte[] filesSection = fileBuf.toByteArray();
		fileBuf.close();
		ExtendedByteArrayOutputStream fileInfo = new ExtendedByteArrayOutputStream();
		fileInfo.putShort(totalFiles);
		for (int i = 0; i < totalFiles; i++) {
			fileInfo.putInt(((Integer)identifiers.get(i)).intValue());
			fileInfo.put3Bytes(((Integer)decompressedSizes.get(i)).intValue());
			fileInfo.put3Bytes(((Integer)compressedSizes.get(i)).intValue());
		}
		byte[] fileInfoSection = fileInfo.toByteArray();
		fileInfo.close();
		ExtendedByteArrayOutputStream finalBuffer = new ExtendedByteArrayOutputStream();
		finalBuffer.write(fileInfoSection);
		finalBuffer.write(filesSection);
		finalBuffer.close();
		return finalBuffer.toByteArray();
	}

	public byte[] getFileAt(int at) {
		byte[] dataBuffer = new byte[((Integer)decompressedSizes.get(at)).intValue()];
		if (!compressedAsWhole)
			BZip2InputStream.resetAndRead(dataBuffer, ((Integer)decompressedSizes.get(at)).intValue(), finalBuffer, ((Integer)compressedSizes.get(at)).intValue(), ((Integer)startOffsets.get(at)).intValue());
		else {
			System.arraycopy(finalBuffer, ((Integer)startOffsets.get(at)).intValue(), dataBuffer, 0, ((Integer)decompressedSizes.get(at)).intValue());
		}
		return dataBuffer;
	}

	public byte[] getFile(int identifier) {
		for (int index = 0; index < totalFiles; index++) {
			if (((Integer) identifiers.get(index)).intValue() == identifier) {
				return getFileAt(index);
			}
		}
		return null;
	}

	public int getIdentifierAt(int at) {
		return ((Integer) identifiers.get(at)).intValue();
	}

	public int getDecompressedSize(int at) {
		return ((Integer) decompressedSizes.get(at)).intValue();
	}

	public int getFileCount() {
		return totalFiles;
	}

	public byte[] getFile(String identStr) {
		return getFile(getHash(identStr));
	}

	public static int getHash(String identStr) {
		int identifier = 0;
		identStr = identStr.toUpperCase();
		for (int id = 0; id < identStr.length(); id++) {
			identifier = identifier * 61 + identStr.charAt(id) - 32;
		}
		return identifier;
	}

	public void renameFile(int index, int newName) {
		identifiers.set(index, Integer.valueOf(newName));
	}

	public void updateFile(int index, byte[] data) {
		((ByteArray) files.get(index)).setBytes(data);
	}

	public int indexOf(String name) {
		return indexOf(getHash(name));
	}

	public int indexOf(int hash) {
		return identifiers.indexOf(Integer.valueOf(hash));
	}

	public void removeFile(int index) {
		files.remove(index);
		identifiers.remove(index);
		compressedSizes.remove(index);
		decompressedSizes.remove(index);
		totalFiles -= 1;
	}

	public void addFile(int identifier, byte[] data) {
		identifiers.add(Integer.valueOf(identifier));
		decompressedSizes.add(Integer.valueOf(data.length));
		compressedSizes.add(Integer.valueOf(0));
		files.add(new ByteArray(data));
		totalFiles += 1;
	}

	public void addFileAt(int at, int identifier, byte[] data) {
		identifiers.add(at, Integer.valueOf(identifier));
		decompressedSizes.add(at, Integer.valueOf(data.length));
		compressedSizes.add(at, Integer.valueOf(0));
		files.add(at, new ByteArray(data));
		totalFiles += 1;
	}
}