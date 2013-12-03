package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import java.util.Collection;
import java.util.Iterator;

public class Utils {

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

}
