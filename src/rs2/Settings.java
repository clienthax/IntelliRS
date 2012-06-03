package rs2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import rs2.constants.Constants;

/**
 * This class handles the reading and writing of the application settings.
 * @author Galkon
 */
public class Settings {

	/**
	 * The shortcuts class.
	 * @author Galkon
	 */
	public class Shortcuts {
		public static final String CTRL = "CTRL";
		public static final String ALT = "ALT";
		public static final String SHIFT = "SHIFT";
	}

	/**
	 * Writes the settings file.
	 */
	public static void write() {
		try {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(getFile()));
			out.writeBoolean(displayGrid);
			out.writeBoolean(displayHover);
			out.writeBoolean(displayData);
			out.writeBoolean(forceEnabled);
			out.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads the settings file.
	 */
	public static void read() {
		if (!getFile().exists()) {
			return;
		}
		try {
			DataInputStream in = new DataInputStream(new FileInputStream(getFile()));
			displayGrid = in.readBoolean();
			displayHover = in.readBoolean();
			displayData = in.readBoolean();
			forceEnabled = in.readBoolean();
			in.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks to see if the working directory exists.
	 * If it doesn't exist, it is created.
	 */
	public static void checkDirectory() {
		File directory = new File(Constants.getWorkingDirectory());
		if (!directory.exists()) {
			if (!directory.mkdir()) {
				System.out.println("The working directory could not be created.");
			}
		}
	}

	/**
	 * Returns the settings file.
	 * @return
	 */
	public static File getFile() {
		return new File(Constants.getWorkingDirectory() + Constants.SETTINGS_FILE_NAME);
	}

	public static boolean displayGrid = true;
	public static boolean displayHover = true;
	public static boolean displayData = true;
	public static boolean forceEnabled = false;

}