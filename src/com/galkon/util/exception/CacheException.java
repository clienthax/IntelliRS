package com.galkon.util.exception;

import java.io.IOException;

@SuppressWarnings("serial")
public class CacheException extends IOException {

	public CacheException() {
	}

	public CacheException(String message, Throwable cause) {
		super(message, cause);
	}

	public CacheException(Throwable cause) {
		super(cause);
	}

	public CacheException(String message) {
		super(message);
	}

}