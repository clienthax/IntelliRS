package com.galkon.util;

import java.awt.Color;

public class Utils {

	/**
	 * Returns a hexadecimal value as an RGB color.
	 * @param hex
	 * @return
	 */
	public static Color getColor(int hex) {
		if (hex != -1) {
			int r = (hex & 0xFF0000) >> 16;
			int g = (hex & 0xFF00) >> 8;
			int b = (hex & 0xFF);
			return new Color(r, g, b);
		}
		return null;
	}

	/**
	 * Returns a color as a hexadecimal integer.
	 * @param color
	 * @return
	 */
	public static int getHex(Color color) {
		if (color == null) {
			return 0;
		}
		String hex = Integer.toHexString(color.getRGB() & 0x00ffffff);
		return Integer.parseInt(hex, 16);
	}

}
