package rs2.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import rs2.io.bzip2.BZip2InputStream;
import rs2.io.bzip2.BZip2OutputStream;

public class DataUtils {

	public static int getHash(String string) {
		int identifier = 0;
		string = string.toUpperCase();
		for (int index = 0; index < string.length(); index++) {
			identifier = (identifier * 61 + string.charAt(index)) - 32;
		}
		return identifier;
	}

	public static byte[] readFile(String file) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(new File(file), "r");
		byte[] data = new byte[(int) raf.length()];
		raf.readFully(data);
		raf.close();
		return data;
	}

	public static void writeFile(String file, byte[] data) {
		try {
			if (data != null) {
				OutputStream out = new FileOutputStream(file);
				out.write(data);
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Compresses all data provided to headerless bz2 format
	 *
	 * @param data the data to compress
	 * @return The compressed data
	 * @throws IOException If there was an error compressing
	 */
	public static byte[] compressBZip2(byte[] data) {
		/*try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			CBZip2OutputStream bzo = new CBZip2OutputStream(bos);
			try {
				bzo.write(data, 0, data.length);
			} finally {
				bzo.close();
				bos.close();
			}
			return bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		return compressBZip2(data, 0, data.length);
	}


	/**
	 * Compresses data provided between off + len to headerless bz2 format
	 *
	 * @param data the data to compress
	 * @param off  offset to compress from
	 * @param len  amount to compress
	 * @return The compressed data
	 * @throws IOException If there was an error compressing
	 */
	public static byte[] compressBZip2(byte[] data, int off, int len) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			BZip2OutputStream bzo = new BZip2OutputStream(bos);
			bzo.write(data, off, len);
			bzo.close();
			bos.close();
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Decompresses all headerless bz2 data provided
	 *
	 * @param data bz2 data to decompress
	 * @return The decompressed data
	 * @throws IOException If there was an error decompressing
	 */
	public static byte[] decompressBZip2(byte[] data) {
		/*try {
			CBZip2InputStream bzi = new CBZip2InputStream(new ByteArrayInputStream(data));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = bzi.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		return decompressBZip2(data, 0, data.length);
	}

	/**
	 * Decompressed data provided between off + len from headerless bz2 format
	 *
	 * @param data the data to decompress
	 * @param off  offset to decompress from
	 * @param len  amount to decompress
	 * @return The decompressed data
	 * @throws IOException If there was an error decompressing
	 */
	public static byte[] decompressBZip2(byte[] data, int off, int len) {
		try {
			byte[] dat = new byte[len];
			System.arraycopy(data, off, dat, 0, len);
			BZip2InputStream bzi = new BZip2InputStream(new ByteArrayInputStream(dat));
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int read;
			while ((read = bzi.read(buf)) > 0) {
				out.write(buf, 0, read);
			}
			out.close();
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] compressGZip(byte[] data, int off, int len) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			GZIPOutputStream gzo = new GZIPOutputStream(bos);
			try {
				gzo.write(data, off, len);
			} finally {
				gzo.close();
				bos.close();
			}
			return bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] decompressGZip(byte[] b) throws IOException {
		GZIPInputStream gzi = new GZIPInputStream(new ByteArrayInputStream(b));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len;
		while ((len = gzi.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		return out.toByteArray();
	}

}
