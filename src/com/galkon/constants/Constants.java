package com.galkon.constants;

public class Constants {

	/**
	 * The main name.
	 */
	public static String NAME = "IntelliRS";

	/**
	 * Do we debug the mouse?
	 */
	public final static boolean DEBUG_MOUSE = true;

	/**
	 * Limit cache to default font?
	 */
	public final static boolean LIMIT_SIZE = false;

	/**
	 * The default background color.
	 */
	public static final int BACKGROUND_COLOR = 0x333333;

	/**
	 * The color of default text.
	 */
	public static final int TEXT_COLOR = 0xD8D8D8;

	/**
	 * The name of the settings file.
	 */
	public static final String SETTINGS_FILE_NAME = "settings.gcl";

	public static final String INTERFACE_FILE_EXTENSION = ".rsi";

	/**
	 * The maximum memory usage the program can consume.
	 * Most clients should consume no more than 130,000,000 to 230,000,00.
	 */
	public static final long MEMORY_LIMIT = 300000000L;

	/**
	 * Returns the cache directory.
	 * @return
	 */
	public static final String getCacheDirectory() {
		return "cache" + System.getProperty("file.separator");
	}

	/**
	 * Returns the image directory.
	 * @return
	 */
	public static final String getImageDirectory() {
		return "img" + System.getProperty("file.separator");
	}

	/**
	 * Returns the working directory.
	 * @return
	 */
	public static final String getWorkingDirectory() {
		return System.getenv("APPDATA") + System.getProperty("file.separator") + "." + NAME.toLowerCase() + System.getProperty("file.separator");
	}

	/**
	 * Returns the export directory.
	 * @return
	 */
	public static String getExportDirectory() {
		return "export" + System.getProperty("file.separator");
	}
}
