package rs2.util.io.exceptions;

@SuppressWarnings("serial")
public class ConfigException extends Exception
{
	public ConfigException()
	{
	}

	public ConfigException(String message)
	{
		/* 11 */     super(message);
	}

	public ConfigException(String message, Throwable cause) {
		/* 15 */     super(message, cause);
	}

	public ConfigException(Throwable cause) {
		/* 19 */     super(cause);
	}
}

/* Location:           C:\Users\Galkon\Dropbox\RSPS\Tools\Toms Suite\CacheSuite Jarred\Toms Suite.jar
 * Qualified Name:     com.jagex.cache.util.exceptions.ConfigException
 * JD-Core Version:    0.6.0
 */