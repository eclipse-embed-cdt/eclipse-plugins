package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import java.util.Collection;
import java.util.Iterator;

public class Utils {

	private static final String PROPERTY_OS_NAME = "os.name"; //$NON-NLS-1$
	public static final String PROPERTY_OS_VALUE_WINDOWS = "windows";//$NON-NLS-1$
	public static final String PROPERTY_OS_VALUE_LINUX = "linux";//$NON-NLS-1$
	public static final String PROPERTY_OS_VALUE_MACOSX = "macosx";//$NON-NLS-1$

	public static String composeCommandWithLf(Collection<String> commands) {
		if (commands.isEmpty())
			return null;
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = commands.iterator();
		while (it.hasNext()) {
			String s = it.next().trim();
			if (s.length() == 0 || s.startsWith("#"))
				continue; // ignore empty lines and comment

			sb.append(s);
			if (it.hasNext()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	static public String escapeWhitespaces(String path) {
		// Escape the spaces in the path/filename if it has any
		String[] segments = path.split("\\s"); //$NON-NLS-1$
		if (segments.length > 1) {
			StringBuffer escapedPath = new StringBuffer();
			for (int index = 0; index < segments.length; ++index) {
				escapedPath.append(segments[index]);
				if (index + 1 < segments.length) {
					escapedPath.append("\\ "); //$NON-NLS-1$
				}
			}
			return escapedPath.toString().trim();
		} else {
			return path;
		}
	}

	static public boolean isWindows(){
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase()
		.startsWith(PROPERTY_OS_VALUE_WINDOWS);
	}
	
	static public boolean isLinux(){
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase()
		.startsWith(PROPERTY_OS_VALUE_LINUX);
	}
	
	static public boolean isMacOSX(){
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase()
		.startsWith(PROPERTY_OS_VALUE_MACOSX);
	}

}
