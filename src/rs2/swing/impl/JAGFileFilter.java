package rs2.swing.impl;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * A file filter that filters out anything that isn't a RSInterface file.
 * @author Galkon
 *
 */
public class JAGFileFilter extends FileFilter {

	/**
	 * Tells us whether or not the file is acceptable for the filter.
	 */
	public boolean accept(File file) {
		if (file.getName().toLowerCase().endsWith(".jag")) {
			return true;
		} else if (file.isDirectory()) {
			return true;
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "RSInterface files";
	}

}