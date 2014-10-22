/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class EclipseDefaults {

	// ------------------------------------------------------------------------

	private static final String GDB_SERVER_EXECUTABLE = "gdb.server.executable";
	private static final String GDB_CLIENT_EXECUTABLE = "gdb.client.executable";

	private static final String DEFAULTS_PREFS = "defaults.prefs";

	// ------------------------------------------------------------------------

	private static Properties fgProp = null;

	// ------------------------------------------------------------------------

	private static String getProperty(String name, String defValue) {

		if (fgProp == null) {

			URL url = Platform.getInstallLocation().getURL();

			IPath path = new Path(url.getPath());
			File file = path.append("configuration")
					.append(Activator.PLUGIN_ID).append(DEFAULTS_PREFS)
					.toFile();
			InputStream is;
			Properties prop = new Properties();
			try {
				is = new FileInputStream(file);

				prop.load(is);
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
				Activator.log(e);
			}

			// Always non-null, but possibly empty
			fgProp = prop;
		}

		return fgProp.getProperty(name, defValue).trim();
	}

	public static String getGdbServerExecutable(String defValue) {
		return getProperty(GDB_SERVER_EXECUTABLE, defValue);
	}

	public static String getGdbClientExecutable(String defValue) {
		return getProperty(GDB_CLIENT_EXECUTABLE, defValue);
	}

	// ------------------------------------------------------------------------
}
