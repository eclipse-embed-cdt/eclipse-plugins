package ilg.gnuarmeclipse.core;

public class StringUtils {

	public static String join(String[] strArray, String joiner) {

		assert strArray != null;
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (String item : strArray) {
			if (i > 0) {
				sb.append(joiner);
			}
			sb.append(item.trim());
			++i;
		}
		return sb.toString();
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

		long value = Long.valueOf("0" + hex, 16);
		if (isNegative)
			value = -value;

		return value;
	}

	public static String capitalizeFirst(String s) {

		if (s.isEmpty()) {
			return s;
		}
		return s.substring(0, 1).toUpperCase() + s.substring(1);
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
