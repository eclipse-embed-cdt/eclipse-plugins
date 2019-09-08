/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.preferences;

import ilg.gnumcueclipse.core.Activator;
import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.debug.gdbjtag.preferences.PersistentPreferences;

public class DefaultPreferences extends ilg.gnumcueclipse.core.preferences.DefaultPreferences {

	// ------------------------------------------------------------------------

	public DefaultPreferences(String pluginId) {
		super(pluginId);
	}

	// ------------------------------------------------------------------------

	public String getSearchPath() {

		String key = PersistentPreferences.SEARCH_PATH;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getSearchPath() = \"" + value + "\"");
		}
		return value;
	}

	/**
	 * Get an OS specific path.
	 * 
	 * @return string. May be empty or null.
	 */
	public String getSearchPathOs() {

		String key = EclipseUtils.getKeyOs(PersistentPreferences.SEARCH_PATH_OS);
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getSearchPathOs() = \"" + value + "\" (" + key + ")");
		}
		return value;
	}

	public void putSearchPath(String value) {

		String key = PersistentPreferences.SEARCH_PATH;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putSearchPath(\"" + value + "\")");
		}
		putString(key, value);
	}

	public String[] getXpackNames() {

		String key = PersistentPreferences.XPACK_NAMES;
		String[] values = getStringArray(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getXpackNames() = \"" + String.join(";", values) + "\"");
		}
		return values;
	}

	// ------------------------------------------------------------------------

	// Override it in each plug-in with actual code.
	protected String getRegistryInstallFolder(String subFolder, String executableName) {

		return null;
	}

	/**
	 * 
	 * @param subFolder
	 *            may be null, usually "bin".
	 * @param executableName
	 * @return
	 */
	public String discoverInstallPath(String subFolder, String executableName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("gdbjtag.DefaultPreferences.discoverInstallPath(\"" + executableName + "\")");
		}

		String path = null;

		if (EclipseUtils.isWindows()) {
			// Try Windows registry keys.
			String exe = addExeExtension(executableName);
			path = getRegistryInstallFolder(subFolder, exe);
		}

		String searchPath = null;

		if (path == null) {

			// Check if the search path is defined in the default
			// preferences.
			searchPath = getSearchPath();
			if (searchPath.isEmpty()) {

				// If not defined, get the OS Specific default
				// from preferences.ini.
				searchPath = getSearchPathOs();
				if (!searchPath.isEmpty()) {
					// Store the search path in the preferences.
					putSearchPath(searchPath);
				}
			}

			if (searchPath != null && !searchPath.isEmpty()) {
				String[] xpackNames = getXpackNames();
				path = searchLatestExecutable(xpackNames, searchPath, subFolder, executableName);
			}
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"gdbjtag.DefaultPreferences.discoverInstallPath(\"" + executableName + "\") = \"" + path + "\"");
		}

		return path;
	}

	// ------------------------------------------------------------------------
}
