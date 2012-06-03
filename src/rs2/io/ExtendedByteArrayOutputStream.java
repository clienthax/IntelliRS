package rs2.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author tom
 */
public class ExtendedByteArrayOutputStream extends ByteArrayOutputStream {

    public ExtendedByteArrayOutputStream() {
        super();
    }

    public void putShort(int s) {
        write(s >> 8);
        write(s);
    }

    public void put3Bytes(int s) {
        write(s >> 16);
        write(s >> 8);
        write(s);
    }

    public void putInt(int s) {
        write(s >> 24);
        write(s >> 16);
        write(s >> 8);
        write(s);
    }

    public void putLong(long l) {
        write((int) (l >> 56));
        write((int) (l >> 48));
        write((int) (l >> 40));
        write((int) (l >> 32));
        write((int) (l >> 24));
        write((int) (l >> 16));
        write((int) (l >> 8));
        write((int) l);
    }

    public void putString(String s) throws IOException {
        write(s.getBytes());
        write(10);
    }

	public void putSmart(int i) {
		if(i < 64 && i >= -64) {
			write(i + 64);
		} else {
			putShort(i + 49152);
		}
	}
}
