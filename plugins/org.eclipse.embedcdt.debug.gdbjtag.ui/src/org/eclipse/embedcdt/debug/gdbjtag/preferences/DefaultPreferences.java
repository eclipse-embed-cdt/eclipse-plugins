/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.preferences;

import org.eclipse.embedcdt.core.Activator;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.debug.gdbjtag.core.preferences.PersistentPreferences;

public class DefaultPreferences extends org.eclipse.embedcdt.core.preferences.DefaultPreferences {

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
