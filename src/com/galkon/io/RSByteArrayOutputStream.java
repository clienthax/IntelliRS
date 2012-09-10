package com.galkon.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author tom
 */
public class RSByteArrayOutputStream extends ByteArrayOutputStream {

    public RSByteArrayOutputStream() {
        super();
    }

    public void putShort(int val) {
        write(val >> 8);
        write(val);
    }
   
	public void putSpaceSaver(int val) {
		write((val >> 8) + 1);
		write(val);
	}

	public void putByte(int val) {
		write(val);
	}

    public void put3Bytes(int val) {
        write(val >> 16);
        write(val >> 8);
        write(val);
    }

    public void putInt(int val) {
        write(val >> 24);
        write(val >> 16);
        write(val >> 8);
        write(val);
    }

    public void putLong(long val) {
        write((int) (val >> 56));
        write((int) (val >> 48));
        write((int) (val >> 40));
        write((int) (val >> 32));
        write((int) (val >> 24));
        write((int) (val >> 16));
        write((int) (val >> 8));
        write((int) val);
    }

    public void putString(String s) throws IOException {
        write(s.getBytes());
        write(10);
    }

	public void putSmart(int val) {
		if(val < 64 && val >= -64) {
			write(val + 64);
		} else {
			putShort(val + 49152);
		}
	}
}
