package ilg.gnuarmeclipse.core;

public class StringUtils {

	public static String join(String[] strArray, String joiner) {

		StringBuffer sb = new StringBuffer();
		for (String item : strArray) {
			sb.append(item);
			sb.append(joiner);
		}
		return sb.toString().trim();
	}

	public static long convertHexLong(String hex) {

		boolean isNegative = false;
		if (hex.startsWith("+")) {
			hex = hex.substring(1);
		} else if (hex.startsWith("-")) {
			hex = hex.substring(1);
			isNegative = true;
		}

		if (hex.startsWith("0x") || hex.startsWith("0X")) {
			hex = hex.substring(2);
		}

		if (hex.startsWith("A") || hex.startsWith("E")) {
			System.out.println();
		}

		long value = Long.valueOf("0" + hex, 16);
		if (isNegative)
			value = -value;

		return value;
	}

	public static String capitalize(String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}

	public static String cosmetiseUrl(String url) {
		if (url.endsWith("/")) {
			return url;
		} else {
			return url + "/";
		}
	}

	public static String convertSizeToString(int size) {

		String sizeString;
		if (size < 1024) {
			sizeString = String.valueOf(size) + "B";
		} else if (size < 1024 * 1024) {
			sizeString = String.valueOf((size + (1024 / 2)) / 1024) + "kB";
		} else {
			sizeString = String.valueOf((size + ((1024 * 1024) / 2))
					/ (1024 * 1024))
					+ "MB";
		}
		return sizeString;
	}



}
