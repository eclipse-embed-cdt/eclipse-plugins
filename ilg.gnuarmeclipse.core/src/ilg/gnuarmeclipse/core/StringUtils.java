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

}
