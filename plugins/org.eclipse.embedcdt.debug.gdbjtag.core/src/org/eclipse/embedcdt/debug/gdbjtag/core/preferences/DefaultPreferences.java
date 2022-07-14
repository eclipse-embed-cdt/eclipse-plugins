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
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.core.preferences;

import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.internal.debug.gdbjtag.core.Activator;

public class DefaultPreferences extends org.eclipse.embedcdt.core.preferences.DefaultPreferences {

	// ------------------------------------------------------------------------

	public DefaultPreferences(String pluginId) {
		super(pluginId);
	}

	// ------------------------------------------------------------------------

	@Deprecated
	public String getInstallFolder() {

		String key = PersistentPreferences.INSTALL_FOLDER;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getInstallFolder() = \"" + value + "\"");
		}
		return value;
	}

	public String getInstallFolder(String prefix) {

		String key = prefix + PersistentPreferences.INSTALL_FOLDER;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getInstallFolder(\"" + prefix + "\") = \"" + value + "\"");
		}
		return value;
	}

	@Deprecated
	public void putInstallFolder(String value) {

		String key = PersistentPreferences.INSTALL_FOLDER;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putInstallFolder(\"" + value + "\")");
		}
		putString(key, value);
	}

	public void putInstallFolder(String prefix, String value) {

		String key = prefix + PersistentPreferences.INSTALL_FOLDER;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putInstallFolder(\"" + prefix + "\", \"" + value + "\")");
		}
		putString(key, value);
	}

	@Deprecated
	public String getSearchPath() {

		String key = PersistentPreferences.SEARCH_PATH;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getSearchPath() = \"" + value + "\"");
		}
		return value;
	}

	public String getSearchPath(String prefix) {

		String key = prefix + PersistentPreferences.SEARCH_PATH;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getSearchPath(\"" + prefix + "\") = \"" + value + "\"");
		}
		return value;
	}

	/**
	 * Get an OS specific path.
	 *
	 * @return string. May be empty or null.
	 */
	@Deprecated
	public String getSearchPathOs() {

		String key = EclipseUtils.getKeyOs(PersistentPreferences.SEARCH_PATH_OS);
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getSearchPathOs() = \"" + value + "\" (" + key + ")");
		}
		return value;
	}

	public String getSearchPathOs(String prefix) {

		String key = EclipseUtils.getKeyOs(prefix + PersistentPreferences.SEARCH_PATH_OS);
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"DefaultPreferences.getSearchPathOs(\"" + prefix + "\") = \"" + value + "\" (" + key + ")");
		}
		return value;
	}

	@Deprecated
	public void putSearchPath(String value) {

		String key = PersistentPreferences.SEARCH_PATH;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putSearchPath(\"" + value + "\")");
		}
		putString(key, value);
	}

	public void putSearchPath(String prefix, String value) {

		String key = prefix + PersistentPreferences.SEARCH_PATH;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putSearchPath(\"" + prefix + "\", \"" + value + "\")");
		}
		putString(key, value);
	}

	@Deprecated
	public String[] getXpackNames() {

		String key = PersistentPreferences.XPACK_NAMES;
		String[] values = getStringArray(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getXpackNames() = \"" + String.join(";", values) + "\"");
		}
		return values;
	}

	public String[] getXpackNames(String prefix) {

		String key = prefix + PersistentPreferences.XPACK_NAMES;
		String[] values = getStringArray(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"DefaultPreferences.getXpackNames(\"" + prefix + "\") = \"" + String.join(";", values) + "\"");
		}
		return values;
	}

	// ------------------------------------------------------------------------

	@Deprecated
	public String getExecutableName() {

		String key = PersistentPreferences.EXECUTABLE_NAME;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getExecutableName() = \"" + value + "\"");
		}
		return value;
	}

	public String getExecutableName(String prefix) {

		String key = prefix + PersistentPreferences.EXECUTABLE_NAME;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getExecutableName(\"" + prefix + "\") = \"" + value + "\"");
		}
		return value;
	}

	@Deprecated
	public String getExecutableNameOs() {

		String key = EclipseUtils.getKeyOs(PersistentPreferences.EXECUTABLE_NAME_OS);

		String value = getString(key, "");
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getExecutableNameOs() = \"" + value + "\" (" + key + ")");
		}
		return value;
	}

	public String getExecutableNameOs(String prefix) {

		String key = EclipseUtils.getKeyOs(prefix + PersistentPreferences.EXECUTABLE_NAME_OS);

		String value = getString(key, "");
		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"DefaultPreferences.getExecutableNameOs(\"" + prefix + "\") = \"" + value + "\" (" + key + ")");
		}
		return value;
	}

	@Deprecated
	public void putExecutableName(String value) {

		String key = PersistentPreferences.EXECUTABLE_NAME;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putExecutableName(\"" + value + "\")");
		}
		putString(key, value);
	}

	public void putExecutableName(String prefix, String value) {

		String key = prefix + PersistentPreferences.EXECUTABLE_NAME;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putExecutableName(\"" + prefix + "\", \"" + value + "\")");
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	// Override it in each plug-in with actual code.
	@Deprecated
	protected String getRegistryInstallFolder(String subFolder, String executableName) {

		return null;
	}

	protected String getRegistryInstallFolder(String prefix, String subFolder, String executableName) {

		return null;
	}

	/**
	 *
	 * @param subFolder
	 *            may be null, usually "bin".
	 * @param executableName
	 * @return
	 */
	@Deprecated
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

			if (!searchPath.isEmpty()) {
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

	public String discoverInstallPath(String prefix, String subFolder, String executableName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"gdbjtag.DefaultPreferences.discoverInstallPath(\"" + prefix + "\", \"" + executableName + "\")");
		}

		String path = null;
		String searchPath = null;

		// Check if the search path is defined in the default
		// preferences.
		searchPath = getSearchPath(prefix);
		if (searchPath.isEmpty()) {

			// If not defined, get the OS Specific default
			// from preferences.ini.
			searchPath = getSearchPathOs(prefix);
			if (!searchPath.isEmpty()) {
				// Store the search path in the preferences.
				putSearchPath(prefix, searchPath);
			}
		}

		if (!searchPath.isEmpty()) {
			String[] xpackNames = getXpackNames(prefix);
			path = searchLatestExecutable(xpackNames, searchPath, subFolder, executableName);
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("gdbjtag.DefaultPreferences.discoverInstallPath(\"" + prefix + "\", \"" + executableName
					+ "\") = \"" + path + "\"");
		}

		return path;
	}

	// ------------------------------------------------------------------------
}
