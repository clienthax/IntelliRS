package rs2.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import rs2.util.io.bzip.CBZip2OutputStream;

public class DataUtils {

	public static byte[] gzDecompress(byte[] b) throws IOException {
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

	public static byte[] gzCompress(byte[] b) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gzo = new GZIPOutputStream(bos);
		gzo.write(b);
		gzo.close();
		bos.close();
		return bos.toByteArray();
	}

	public static int getHash(String s) {
		int identifier = 0;
		s = s.toUpperCase();
		for (int j = 0; j < s.length(); j++) {
			 identifier = identifier * 61 + s.charAt(j) - 32;
		}
		return identifier;
	}

	public static byte[] bz2Compress(byte[] b) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		CBZip2OutputStream bzo = new CBZip2OutputStream(bos, 1);
		bzo.write(b);
		bzo.close();
		return bos.toByteArray();
	}

	public static void writeFile(byte[] data, String fileName) {
		try {
			if (data != null) {
				OutputStream out = new FileOutputStream(fileName);
				out.write(data);
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] readFile(File f) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(f, "r");
		byte[] data = new byte[(int)raf.length()];
		raf.readFully(data);
		raf.close();
		return data;
	}

	public static void writeCompressedFile(byte[] data, String name) {
		try {
			GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(name));
			out.write(data);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int readJAGHash(String string) {
		int id = 0;
		string = string.toUpperCase();
		for (int j = 0; j < string.length(); j++) {
			id = (id * 61 + string.charAt(j)) - 32;
		}
		return id;
	}

	public static int getCRC(byte[] data) {
		CRC32 crc = new CRC32();
		crc.update(data);
		return (int) crc.getValue();
	}

	public static byte[] gZipDecompress(byte[] b) throws IOException {
		GZIPInputStream gzi = new GZIPInputStream(new ByteArrayInputStream(b));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len;
		try {
			while ((len = gzi.read(buf, 0, buf.length)) > 0) {
				out.write(buf, 0, len);
			}
		} finally {
			out.close();
		}
		return out.toByteArray();
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

	public static byte[] unzip(byte[] data) throws IOException {
		InputStream in = new ByteArrayInputStream(data);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			in = new GZIPInputStream(in);
			byte[] buffer = new byte[65536];
			int noRead;
			while ((noRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, noRead);
			}
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
		return out.toByteArray();
	}

	public static byte[] readFile(String name) {
		try {
			RandomAccessFile raf = new RandomAccessFile(name, "r");
			ByteBuffer buf = raf.getChannel().map(
					FileChannel.MapMode.READ_ONLY, 0, raf.length());
			try {
				if (buf.hasArray()) {
					return buf.array();
				} else {
					byte[] array = new byte[buf.remaining()];
					buf.get(array);
					return array;
				}
			} finally {
				raf.close();
			}
		} catch (Exception e) {
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

	/**
	 * Writes a string in the form of bytes that can be read by Jagex buffer methods.
	 * @param dat output stream.
	 * @param input input string.
	 * @throws IOException
	 */
	public static void writeString(DataOutputStream dat, String input) throws IOException {
		dat.write(input.getBytes());
		dat.writeByte(10);
	}

	/**
	 * Writes 3 bytes(24 bits) to the output stream that can be read by Jagex buffer methods.
	 * @param dat output stream.
	 * @param i input value.
	 * @throws IOException
	 */
	public static void write3Bytes(DataOutputStream dat, int i) throws IOException {
		dat.write((byte) (i >> 16));
		dat.write((byte) (i >> 8));
		dat.write((byte) i);
	}
}
