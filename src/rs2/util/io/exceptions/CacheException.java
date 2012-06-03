package rs2.util.io.exceptions;

import java.io.IOException;

@SuppressWarnings("serial")
public class CacheException extends IOException
{
	public CacheException()
	{
	}

	public CacheException(String message, Throwable cause)
	{
		/* 14 */     super(message, cause);
	}

	public CacheException(Throwable cause) {
		/* 18 */     super(cause);
	}

	public CacheException(String message) {
		/* 22 */     super(message);
	}
}

/* Location:           C:\Users\Galkon\Dropbox\RSPS\Tools\Toms Suite\CacheSuite Jarred\Toms Suite.jar
 * Qualified Name:     com.jagex.cache.util.exceptions.CacheException
 * JD-Core Version:    0.6.0
 */