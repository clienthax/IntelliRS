package rs2.cache;

import rs2.util.ByteArray;

public class ArchiveFile {

	/**
	 * Initializes the archive file.
	 * @param hash
	 * @param size
	 * @param compressedSize
	 * @param offset
	 * @param data
	 * @param decompressed
	 */
	public ArchiveFile(int hash, int size, int compressedSize, int offset, ByteArray data, boolean decompressed) {
		this.hash = hash;
		this.size = size;
		this.compressedSize = compressedSize;
		this.offset = offset;
		this.data = data;
		this.decompressed = decompressed;
	}

	/**
	 * Gets the (uncompressed) size of the archive file.
	 * @return
	 */
	public int getSize() {
		return this.size;
	}

	/**
	 * Sets the (uncompressed) size of the archive file.
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Gets the compressed size of the archive file.
	 * @return
	 */
	public int getCompressedSize() {
		return this.compressedSize;
	}

	/**
	 * Sets the compressed size of the archive file.
	 * @param size
	 */
	public void setCompressedSize(int size) {
		this.compressedSize = size;
	}

	/**
	 * Gets the hash of the archive file.
	 * @return
	 */
	public int getHash() {
		return this.hash;
	}

	/**
	 * Sets the hash of the archive file.
	 * @param hash
	 */
	public void setHash(int hash) {
		this.hash = hash;
	}

	/**
	 * Gets the data of the archive file.
	 * @return
	 */
	public byte[] getData() {
		return this.data.getBytes();
	}

	/**
	 * Sets the data of the archive file.
	 * @param data
	 */
	public void setData(byte[] data) {
		this.data.setBytes(data);
	}

	public ByteArray data;
	public int compressedSize, size, hash, offset;
	public boolean decompressed;

}