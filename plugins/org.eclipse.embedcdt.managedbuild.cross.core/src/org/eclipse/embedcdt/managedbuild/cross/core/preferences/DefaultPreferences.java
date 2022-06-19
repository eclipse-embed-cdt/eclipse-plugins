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

package org.eclipse.embedcdt.managedbuild.cross.core.preferences;

import java.io.File;

import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.core.preferences.Discoverer;
import org.eclipse.embedcdt.internal.managedbuild.cross.core.Activator;

public class DefaultPreferences extends org.eclipse.embedcdt.core.preferences.DefaultPreferences {

	// ------------------------------------------------------------------------

	// HKCU & HKLM LOCAL_MACHINE
	private static final String REG_SUBKEY = "\\GNU MCU Eclipse\\Build Tools";
	private static final String REG_SUBKEY_DEPRECATED = "\\GNU ARM Eclipse\\Build Tools";
	// Standard Microsoft recommendation.
	private static final String REG_NAME = "InstallLocation";

	// private static final String EXECUTABLE_NAME = "make.exe";

	// ------------------------------------------------------------------------

	public DefaultPreferences(String pluginId) {
		super(pluginId);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the default toolchain name.
	 *
	 * @return a trimmed string, possibly empty.
	 */
	public String getToolchainName() {

		String key = PersistentPreferences.TOOLCHAIN_NAME_KEY;
		String value = getString(key, null);
		if (value == null) {
			value = "";
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getToolchainName() = \"" + value + "\"");
		}
		return value;
	}

	public void putToolchainName(String value) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putToolchainName(\"" + value + "\")");
		}

		String key = PersistentPreferences.TOOLCHAIN_NAME_KEY;
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	public String getToolchainId() {

		String key = PersistentPreferences.TOOLCHAIN_ID_KEY;
		String value = getString(key, null);
		if (value == null) {
			value = "";
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getToolchainId() = \"" + value + "\"");
		}
		return value;
	}

	public void putToolchainId(String value) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putToolchainId(\"" + value + "\")");
		}

		String key = PersistentPreferences.TOOLCHAIN_ID_KEY;
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the default toolchain path for a given toolchain name. Toolchains are
	 * identified by their absolute hash code.
	 *
	 * @param toolchainName
	 *            a string.
	 * @return a trimmed string, possibly empty.
	 */
	@Deprecated
	public String getToolchainPath(String toolchainName) {

		String key = PersistentPreferences.getToolchainKey(toolchainName);
		String value = getString(key, null);
		if (value == null) {
			value = "";
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getToolchainPath(\"" + toolchainName + "\") = \"" + value + "\")");
		}
		return value;
	}

	public String getToolchainPath(String toolchainId, String toolchainName) {

		String key = PersistentPreferences.getToolchainKey(toolchainId, toolchainName);
		String value = getString(key, null);
		if (value == null) {
			value = "";
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getToolchainPath(\"" + toolchainId + ", " + toolchainName
					+ "\") = \"" + value + "\")");
		}
		return value;
	}

	@Deprecated
	public void putToolchainPath(String toolchainName, String value) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putToolchainPath(\"" + toolchainName + "\", \"" + value + "\")");
		}
		String key = PersistentPreferences.getToolchainKey(toolchainName);
		putString(key, value);
	}

	public void putToolchainPath(String toolchainId, String toolchainName, String value) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putToolchainPath(\"" + toolchainId + ", " + toolchainName + "\", \""
					+ value + "\")");
		}
		String key = PersistentPreferences.getToolchainKey(toolchainId, toolchainName);
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the default toolchain search path for a given toolchain name. Toolchains
	 * are identified by their absolute hash code.
	 *
	 * @param toolchainName
	 *            a string.
	 * @return a trimmed string, possibly empty.
	 */
	@Deprecated
	public String getToolchainSearchPath(String toolchainName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getToolchainSearchPath(\"" + toolchainName + "\")");
		}
		String key = PersistentPreferences.getToolchainSearchKey(toolchainName);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getToolchainSearchPath(\"" + toolchainName + "\") (" + key + ")");
		}
		String value = getString(key, "");

		return value;
	}

	public String getToolchainSearchPath(String toolchainId, String toolchainName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"DefaultPreferences.getToolchainSearchPath(\"" + toolchainId + ", " + toolchainName + "\")");
		}
		String key = PersistentPreferences.getToolchainSearchKey(toolchainId, toolchainName);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getToolchainSearchPath(\"" + toolchainId + ", " + toolchainName
					+ "\") (" + key + ")");
		}
		String value = getString(key, "");

		return value;
	}

	@Deprecated
	public void putToolchainSearchPath(String toolchainName, String value) {

		String key = PersistentPreferences.getToolchainSearchKey(toolchainName);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putToolchainSearchPath(\"" + toolchainName + "\", \"" + value
					+ "\") (" + key + ")");
		}
		putString(key, value);
	}

	public void putToolchainSearchPath(String toolchainId, String toolchainName, String value) {

		String key = PersistentPreferences.getToolchainSearchKey(toolchainId, toolchainName);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putToolchainSearchPath(\"" + toolchainId + ", " + toolchainName
					+ "\", \"" + value + "\") (" + key + ")");
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the default toolchain search path for a given toolchain name and the
	 * current OS family. Toolchains are identified by their absolute hash code.
	 *
	 * @param toolchainName
	 *            a string.
	 * @return a trimmed string, possibly empty.
	 */
	@Deprecated
	public String getToolchainSearchPathOs(String toolchainName) {

		String value = getString(PersistentPreferences.getToolchainSearchOsKey(toolchainName), "");
		return value;
	}

	public String getToolchainSearchPathOs(String toolchainId, String toolchainName) {

		String value = getString(PersistentPreferences.getToolchainSearchOsKey(toolchainId, toolchainName), "");
		return value;
	}

	@Deprecated
	public String[] getToolchainXpackNames(String toolchainName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getToolchainXpackName(\"" + toolchainName + "\")");
		}
		String key = PersistentPreferences.getToolchainXpackKey(toolchainName);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getToolchainXpackName(\"" + toolchainName + "\") (" + key + ")");
		}
		String[] values = getStringArray(key, "");

		return values;
	}

	public String[] getToolchainXpackNames(String toolchainId, String toolchainName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"DefaultPreferences.getToolchainXpackName(\"" + toolchainId + ", " + toolchainName + "\")");
		}
		String key = PersistentPreferences.getToolchainXpackKey(toolchainId, toolchainName);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getToolchainXpackName(\"" + toolchainId + ", " + toolchainName
					+ "\") (" + key + ")");
		}
		String[] values = getStringArray(key, "");

		// An array of strings, possibly with an empty element.
		return values;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the default value for the build tools path.
	 *
	 * @return a trimmed string, possibly empty.
	 */
	public String getBuildToolsPath() {

		return getString(PersistentPreferences.BUILD_TOOLS_PATH_KEY, "");
	}

	public void putBuildToolsPath(String value) {

		String key = PersistentPreferences.BUILD_TOOLS_PATH_KEY;
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putBuildToolsPath(\"" + value + "\") (" + key + ")");
		}

		putString(key, value);
	}

	public String getBuildToolsSearchPath() {

		String key = PersistentPreferences.BUILD_TOOLS_SEARCH_PATH_KEY;
		return getString(key, "");
	}

	public String getBuildToolsSearchPathOs() {

		String os = EclipseUtils.getOsFamily();
		String key = String.format(PersistentPreferences.BUILD_TOOLS_SEARCH_PATH_OS_KEY, os);

		return getString(key, "");
	}

	public void putBuildToolsSearchPath(String value) {

		String key = PersistentPreferences.BUILD_TOOLS_SEARCH_PATH_KEY;
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putBuildToolsSearchPath(\"" + value + "\") (" + key + ")");
		}
		putString(key, value);
	}

	/**
	 *
	 * @return string, possibly empty.
	 */
	public String[] getBuildToolsXpackNames() {

		String key = PersistentPreferences.BUILD_TOOLS_XPACK_NAMES;
		String[] values = getStringArray(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getBuildToolsXpackNames() = \"" + String.join(";", values) + "\"");
		}
		return values;
	}

	/**
	 * Find where the build tools might have been installed. The returned folder is
	 * known to be an existing folder.
	 *
	 * @return a trimmed string, possibly empty.
	 */
	public String discoverBuildToolsPath() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.discoverBuildToolsPath()");
		}

		String path = null;
		String executableName = "make";
		String subPath = "bin";

		if (EclipseUtils.isWindows()) {

			String exe = addExeExtension(executableName);
			path = Discoverer.getRegistryInstallFolder(exe, subPath, REG_SUBKEY, REG_NAME);

			// If the GNU MCU name is not found, try the GNU ARM key.
			if (path == null) {
				path = Discoverer.getRegistryInstallFolder(exe, subPath, REG_SUBKEY_DEPRECATED, REG_NAME);
			}
		}

		String searchPath = null;
		if (path == null) {

			// Check if the search path is defined in the default
			// preferences.
			searchPath = getBuildToolsSearchPath();
			if (searchPath.isEmpty()) {

				// If not defined, get the OS Specific default
				// from preferences.ini.
				searchPath = getBuildToolsSearchPathOs();
				if (!searchPath.isEmpty()) {
					// Store the search path in the preferences
					putBuildToolsSearchPath(searchPath);
				}
			}
		}

		if (searchPath != null && !searchPath.isEmpty()) {
			String[] xpackNames = getBuildToolsXpackNames();
			path = searchLatestExecutable(xpackNames, searchPath, subPath, executableName);
		}

		if (path != null) {
			path = path.trim();

			// Validate registry path. If folder does not exist, ignore.
			File file = new File(path);
			if (!file.isDirectory()) {
				path = "";
			}
		} else {
			path = "";
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.discoverBuildToolsPath() = \"" + path + "\"");
		}

		return path;
	}

	protected String getRegistryToolchainInstallFolder(String toolchainName, String subPath, String executableName) {

		String path = null;
		return path;
	}

	@Deprecated
	public String discoverToolchainPath(String toolchainName, String executableName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.discoverToolchainPath(\"" + toolchainName + "\")");
		}

		String path = null;
		String subPath = "bin";

		if (EclipseUtils.isWindows()) {
			String exe = addExeExtension(executableName);
			path = getRegistryToolchainInstallFolder(toolchainName, subPath, exe);
		}

		String searchPath = null;

		if (path == null) {

			// Check if the search path is defined in the default
			// preferences.
			searchPath = getToolchainSearchPath(toolchainName);
			if (searchPath.isEmpty()) {

				// If not defined, get the OS Specific default
				// from preferences.ini.
				searchPath = getToolchainSearchPathOs(toolchainName);
				if (!searchPath.isEmpty()) {
					// Store the search path in the preferences.
					putToolchainSearchPath(toolchainName, searchPath);
				}
			}

			if (!searchPath.isEmpty()) {
				String[] xpackNames = getToolchainXpackNames(toolchainName);
				path = searchLatestExecutable(xpackNames, searchPath, subPath, executableName);
			}
		}

		if (Activator.getInstance().isDebugging()) {
			System.out
					.println("DefaultPreferences.discoverToolchainPath(\"" + toolchainName + "\") = \"" + path + "\"");
		}

		return path;
	}

	public String discoverToolchainPath(String toolchainId, String toolchainName, String executableName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"DefaultPreferences.discoverToolchainPath(\"" + toolchainId + ", " + toolchainName + "\")");
		}

		String path = null;
		String subPath = "bin";

		if (EclipseUtils.isWindows()) {
			String exe = addExeExtension(executableName);
			path = getRegistryToolchainInstallFolder(toolchainName, subPath, exe);
		}

		String searchPath = null;

		if (path == null) {

			// Check if the search path is defined in the default
			// preferences.
			searchPath = getToolchainSearchPath(toolchainId, toolchainName);
			if (searchPath.isEmpty()) {

				// If not defined, get the OS Specific default
				// from preferences.ini.
				searchPath = getToolchainSearchPathOs(toolchainId, toolchainName);
				if (!searchPath.isEmpty()) {
					// Store the search path in the preferences.
					putToolchainSearchPath(toolchainId, toolchainName, searchPath);
				}
			}

			if (!searchPath.isEmpty()) {
				String[] xpackNames = getToolchainXpackNames(toolchainId, toolchainName);
				path = searchLatestExecutable(xpackNames, searchPath, subPath, executableName);
			}
		}

		if (Activator.getInstance().isDebugging()) {
			System.out
					.println("DefaultPreferences.discoverToolchainPath(\"" + toolchainName + "\") = \"" + path + "\"");
		}

		return path;
	}

	// ------------------------------------------------------------------------

	// public static final String TOOLCHAIN = "toolchains.prefs";

	// private Properties fToolchainProperties;

	// ------------------------------------------------------------------------

	// TODO: remove DEPRECATED
	// Non-standard location:
	// eclipse/configuration/org.eclipse.embedcdt.managedbuild.cross.riscv/toolchain.prefs/name=value

	// private Properties getToolchainProperties() throws IOException {
	//
	// if (fToolchainProperties == null) {
	//
	// URL url = Platform.getInstallLocation().getURL();
	//
	// IPath path = new Path(url.getPath());
	// File file =
	// path.append("configuration").append(fPluginId).append(TOOLCHAIN).toFile();
	// InputStream is = new FileInputStream(file);
	//
	// Properties prop = new Properties();
	// prop.load(is);
	//
	// fToolchainProperties = prop;
	// }
	//
	// return fToolchainProperties;
	// }

	// ------------------------------------------------------------------------
}
