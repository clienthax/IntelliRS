package com.galkon.util;

import com.galkon.graphics.font.RSFont;

public final class TextUtils {

	/**
	 * Cuts a string into more than one line if it exceeds the specified max width.
	 * @param font
	 * @param string
	 * @param maxWidth
	 * @return
	 */
	public static String[] splitString(RSFont font, String prefix, String string, int maxWidth, boolean ranked) {
		maxWidth -= font.getTextWidth(prefix) + (ranked ? 14 : 0);
		if (font.getTextWidth(prefix + string) + (ranked ? 14 : 0) <= maxWidth) {
			return new String[]{ string };
		}
		String line = "";
		String[] cut = new String[2];
		boolean split = false;
		char[] characters = string.toCharArray();
		int space = -1;
		for (int index = 0; index < characters.length; index++) {
			char c = characters[index];
			line += c;
			if (c == ' ') {
				space = index;
			}
			if (!split) {
				if (font.getTextWidth(line) + 10 > maxWidth) {
					if (space != -1 && characters[index - 1] != ' ') {
						cut[0] = line.substring(0, space);
						line = line.substring(space);
					} else {
						cut[0] = line;
						line = "";
					}
					split = true;
				}
			}
		}
		if (line.length() > 0) {
			cut[1] = line;
		}
		return cut;
	}

	/**
	 * Converts a string to a long.
	 * @param string
	 * @return
	 */
	public static long longForName(String string) {
		long name = 0L;
		for (int character = 0; character < string.length() && character < 12; character++) {
			char c = string.charAt(character);
			name *= 37L;
			if (c >= 'A' && c <= 'Z') {
				name += (1 + c) - 65;
			} else if (c >= 'a' && c <= 'z') {
				name += (1 + c) - 97;
			} else if (c >= '0' && c <= '9') {
				name += (27 + c) - 48;
			}
		}
		for (; name % 37L == 0L && name != 0L; name /= 37L);
		return name;
	}

	/**
	 * Converts a long to a string.
	 * @param name
	 * @return
	 */
	public static String nameForLong(long name) {
		try {
			if (name <= 0L || name >= 0x5b5b57f8a98a5dd1L) {
				return "invalid_name";
			}
			if (name % 37L == 0L) {
				return "invalid_name";
			}
			int index = 0;
			char characters[] = new char[12];
			while (name != 0L) {
				long l = name;
				name /= 37L;
				characters[11 - index++] = validChars[(int) (l - name * 37L)];
			}
			return new String(characters, 12 - index, index);
		} catch (RuntimeException e) {
			System.out.println("81570, " + name + ", " + (byte) -99 + ", " + e.toString());
		}
		throw new RuntimeException();
	}

	public static long stringToLong(String string) {
		string = string.toUpperCase();
		long l = 0L;
		for (int i = 0; i < string.length(); i++) {
			l = (l * 61L + (long) string.charAt(i)) - 32L;
			l = l + (l >> 56) & 0xffffffffffffffL;
		}
		return l;
	}

	public static String method586(int i) {
		return (i >> 24 & 0xff) + "." + (i >> 16 & 0xff) + "." + (i >> 8 & 0xff) + "." + (i & 0xff);
	}

	public static String fixName(String name) {
		if (name.length() > 0) {
			char characters[] = name.toCharArray();
			for (int index = 0; index < characters.length; index++)
				if (characters[index] == '_') {
					characters[index] = ' ';
					if (index + 1 < characters.length && characters[index + 1] >= 'a' && characters[index + 1] <= 'z') {
						characters[index + 1] = (char) ((characters[index + 1] + 65) - 97);
					}
				}
			if (characters[0] >= 'a' && characters[0] <= 'z') {
				characters[0] = (char) ((characters[0] + 65) - 97);
			}
			return capitalize(new String(characters));
		} else {
			return capitalize(name);
		}
	}

	public static String capitalize(String string) {
		for (int index = 0; index < string.length(); index++) {
			if (index == 0) {
				string = String.format("%s%s", Character.toUpperCase(string.charAt(0)), string.substring(1));
			}
			if (!Character.isLetterOrDigit(string.charAt(index))) {
				if (index + 1 < string.length()) {
					string = String.format("%s%s%s", string.subSequence(0, index + 1), Character.toUpperCase(string.charAt(index + 1)), string.substring(index + 2));
				}
			}
		}
		return string;
	}

	public static boolean isAlphanumeric(String string) {
		if(!string.matches("[A-Za-z0-9 ]+")) {
			return false;
		}
		return true;
	}

	public static boolean isValidName(String name) {
		if(name.contains("  ") || name.contains("   ") || name.contains("    ") || name.contains("     ") || name.contains("      ") || name.contains("       ") || name.contains("        ") || name.contains("         ") || name.contains("          ") || name.contains("           ") || name.contains("            ")) {
			return false;
		}
		if(!name.matches("[A-Za-z0-9 ]+")) {
			return false;
		}
		return true;
	}

	public static boolean isValidEmail(String email) {
		if (email.matches(".+@.+\\.[a-z]+")) {
			return true;
		}
		return false;
	}

	public static String mask(String password) {
		StringBuffer stringbuffer = new StringBuffer();
		for (int index = 0; index < password.length(); index++) {
			stringbuffer.append("*");
		}
		return stringbuffer.toString();
	}

	private static final char[] validChars = {
		'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 
		'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
		'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
	};

}
