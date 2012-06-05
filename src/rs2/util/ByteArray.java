package rs2.util;

public class ByteArray {

	private byte[] bytes;
	public int length = 0;

	public ByteArray(byte[] bytes) {
		this.bytes = bytes;
		this.length = bytes.length;
	}

	public ByteArray(byte[] bytes, int length) {
		this.bytes = bytes;
		this.length = length;
	}

	public byte[] getBytes() {
		return this.bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
		this.length = bytes.length;
	}
}