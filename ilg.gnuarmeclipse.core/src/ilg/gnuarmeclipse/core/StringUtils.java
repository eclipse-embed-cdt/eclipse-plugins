package ilg.gnuarmeclipse.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * A collection of utilities used for string processing.
 */
public class StringUtils {

	// ------------------------------------------------------------------------

	/**
	 * Join an array of string.
	 * 
	 * @param strArray
	 *            array of strings.
	 * @param joiner
	 *            a string inserted between elements.
	 * @return a string.
	 */
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

	/**
	 * Convert hex to long. Considers +/-, ignores 0x and 0X.
	 * 
	 * @param hex
	 *            a string.
	 * @return a long.
	 */
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

	/**
	 * Capitalise first letter of a string.
	 * 
	 * @param str
	 *            a string.
	 * @return a string.
	 */
	public static String capitalizeFirst(String str) {

		if (str.isEmpty()) {
			return str;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * Cosmetise the URL tail to always have a slash, to simplify appending more
	 * path elements.
	 * 
	 * @param url
	 *            a string.
	 * @return a string.
	 */
	public static String cosmetiseUrl(String url) {

		if (url.endsWith("/")) {
			return url;
		} else {
			return url + "/";
		}
	}

	/**
	 * Convert an integer to B/kB/MB.
	 * 
	 * @param size
	 *            an integer size.
	 * @return a string.
	 */
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

	/**
	 * Duplicate single backslashes, i.e. not part of a double backslash group.
	 * 
	 * @param str
	 *            a string.
	 * @return a string.
	 */
	public static String duplicateBackslashes(String str) {

		if (str.indexOf('\\') < 0) {
			return str;
		}

		String sa[] = str.split("\\\\\\\\");
		for (int i = 0; i < sa.length; ++i) {
			// System.out.println(sa[i]);
			sa[i] = sa[i].replaceAll("\\\\", "\\\\\\\\");
			// System.out.println(sa[i]);
		}

		str = StringUtils.join(sa, "\\\\");
		// System.out.println(str);
		return str;
	}

	/**
	 * Split the path into segments and return the name.
	 * 
	 * @param str
	 *            a string with the full path.
	 * @return a string with the name, or the full path if error.
	 */
	public static String extractNameFromPath(String str) {

		if (str == null) {
			return null;
		}

		IPath path = new Path(str);

		String ret = path.lastSegment();
		if (ret != null) {
			return ret;
		}

		return str;
	}

	// ------------------------------------------------------------------------

	private static enum SplitState {
		None, InOption, InString
	};

	/**
	 * Split a string containing command line option separated by white spaces
	 * into substrings. Content of quoted options is not parsed, but preserved
	 * as a single substring.
	 * 
	 * @param str
	 * @return array of strings.
	 */
	public static List<String> splitCommandLineOptions(String str) {

		List<String> lst = new ArrayList<String>();
		SplitState state = SplitState.None;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < str.length(); ++i) {
			char ch = str.charAt(i);

			// a small state machine to split a string in substrings,
			// preserving quoted parts
			switch (state) {
			case None:

				if (ch == '"') {
					sb.setLength(0);

					state = SplitState.InString;
				} else if (ch != ' ' && ch != '\n' && ch != '\r') {
					sb.setLength(0);
					sb.append(ch);

					state = SplitState.InOption;
				}
				break;

			case InOption:

				if (ch != ' ' && ch != '\n' && ch != '\r') {
					sb.append(ch);
				} else {
					lst.add(sb.toString());

					state = SplitState.None;
				}

				break;

			case InString:

				if (ch != '"') {
					sb.append(ch);
				} else {
					lst.add(sb.toString());

					state = SplitState.None;
				}

				break;
			}

		}

		if (state == SplitState.InOption || state == SplitState.InString) {
			lst.add(sb.toString());
		}
		return lst;

	}

	// ------------------------------------------------------------------------
}
