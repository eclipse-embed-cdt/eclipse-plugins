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

package ilg.gnumcueclipse.managedbuild.cross.preferences;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.core.preferences.Discoverer;
import ilg.gnumcueclipse.managedbuild.cross.Activator;

public class DefaultPreferences extends ilg.gnumcueclipse.core.DefaultPreferences {

	// ------------------------------------------------------------------------

	// HKCU & HKLM LOCAL_MACHINE
	private static final String REG_SUBKEY = "\\GNU MCU Eclipse\\Build Tools";
	private static final String REG_SUBKEY_DEPRECATED = "\\GNU ARM Eclipse\\Build Tools";
	// Standard Microsoft recommendation.
	private static final String REG_NAME = "InstallLocation";
	// Custom name, used before reading the standard.
	private static final String REG_NAME_DEPRECATED = "InstallFolder";

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

	/**
	 * Get the default toolchain path for a given toolchain name. Toolchains are
	 * identified by their absolute hash code.
	 * 
	 * @param toolchainName
	 *            a string.
	 * @return a trimmed string, possibly empty.
	 */
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

	public void putToolchainPath(String toolchainName, String value) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putToolchainPath(\"" + toolchainName + "\", \"" + value + "\")");
		}
		String key = PersistentPreferences.getToolchainKey(toolchainName);
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the default toolchain search path for a given toolchain name.
	 * Toolchains are identified by their absolute hash code.
	 * 
	 * @param toolchainName
	 *            a string.
	 * @return a trimmed string, possibly empty.
	 */
	public String getToolchainSearchPath(String toolchainName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getToolchainSearchPath(\"" + toolchainName + "\")");
		}
		String key = PersistentPreferences.getToolchainSearchKey(toolchainName);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getToolchainSearchPath(\"" + toolchainName + "\") (" + key + ")");
		}
		String value = getString(PersistentPreferences.getToolchainSearchKey(toolchainName), "");

		return value;
	}

	public void putToolchainSearchPath(String toolchainName, String value) {

		String key = PersistentPreferences.getToolchainSearchKey(toolchainName);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putToolchainSearchPath(\"" + toolchainName + "\", \"" + value
					+ "\") (" + key + ")");
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
	public String getToolchainSearchPathOs(String toolchainName) {

		String value = getString(PersistentPreferences.getToolchainSearchOsKey(toolchainName), "");

		return value;
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
	 * Find where the build tools might have been installed. The returned folder
	 * is known to be an existing folder.
	 * 
	 * @return a trimmed string, possibly empty.
	 */
	public String discoverBuildToolsPath() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.discoverBuildToolsPath()");
		}

		String path = null;
		String executableName = "make";
		if (EclipseUtils.isWindows()) {

			path = Discoverer.getRegistryInstallFolder(executableName + ".exe", "bin", REG_SUBKEY, REG_NAME);

			// If the GNU MCU name is not found, try the GNU ARM key.
			if (path == null) {
				path = Discoverer.getRegistryInstallFolder(executableName + ".exe", "bin", REG_SUBKEY_DEPRECATED,
						REG_NAME);
			}

			// For very old setups, try the old name.
			if (path == null) {
				path = Discoverer.getRegistryInstallFolder(executableName + ".exe", "bin", REG_SUBKEY_DEPRECATED,
						REG_NAME_DEPRECATED);
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
			path = searchLatestExecutable(searchPath, executableName);
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

	/**
	 * Search subfolders for an executable and remember date of version folders.
	 * "<searchPath>/<version>/bin/<executable>"
	 * 
	 * @param searchPath
	 * @param executableName
	 * @return
	 */
	public String searchLatestExecutable(String searchPath, String executableName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"DefaultPreferences.searchLatestExecutable(\"" + searchPath + "\", " + executableName + ") ");
		}

		if (searchPath == null || searchPath.isEmpty()) {
			return null;
		}

		// Resolve ${user.home}
		String resolvedPath = searchPath;
		if (resolvedPath.indexOf("${user.home}") >= 0) {
			String userHome = System.getProperty("user.home");
			userHome = Matcher.quoteReplacement(userHome);
			resolvedPath = resolvedPath.replaceAll("\\$\\{user.home\\}", userHome);
		}

		// If more macros remain, use the usual substituter.
		if (resolvedPath.indexOf("${") >= 0) {
			IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
			try {
				resolvedPath = variableManager.performStringSubstitution(resolvedPath, false);
			} catch (CoreException e) {
				resolvedPath = null;
			}
		}

		if (resolvedPath == null || resolvedPath.isEmpty()) {
			return null;
		}

		// Split into multiple paths.
		String[] paths = resolvedPath.split(EclipseUtils.getPathSeparator());
		if (paths.length == 0) {
			return null;
		}

		if (EclipseUtils.isWindows() && !executableName.endsWith(".exe")) {
			executableName += ".exe";
		}

		Map<Long, String> map = new HashMap<Long, String>();
		// Try paths in order; collect dates.
		for (int i = 0; i < paths.length; ++i) {
			searchExecutable(paths[i], executableName, map);
		}

		if (map.isEmpty()) {
			return null;
		}

		Set<Long> keys = map.keySet();
		Long latestKey = new Long(0);
		for (Long key : keys) {
			if (key > latestKey) {
				latestKey = key;
			}
		}

		return map.get(latestKey);
	}

	private void searchExecutable(String folder, final String executableName, Map<Long, String> map) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.searchExecutable(\"" + folder + "\", \"" + executableName + "\") ");
		}
		File local = new File(folder);
		if (!local.isDirectory()) {
			// System.out.println(folder + " not a folder");
			return;
		}

		local.listFiles(new FilenameFilter() {

			/**
			 * Filter to select only
			 */
			@Override
			public boolean accept(File dir, String name) {
				IPath versionPath = (new Path(dir.getAbsolutePath())).append(name);
				IPath basePath = versionPath.append("bin");
				IPath path = basePath.append(executableName);

				File file = path.toFile();
				if (file.isFile()) {
					File versioFolder = versionPath.toFile();
					Long key = new Long(versioFolder.lastModified());
					map.put(key, basePath.toPortableString());

					if (Activator.getInstance().isDebugging()) {
						System.out.println("DefaultPreferences.searchExecutable(\"" + folder + "\", \"" + executableName
								+ "\") = add \"" + basePath.toPortableString() + "\" " + new Date(key.longValue()));
					}
					return true;
				}
				return false;
			}
		});
	}

	// ------------------------------------------------------------------------

	// public static final String TOOLCHAIN = "toolchains.prefs";

	// private Properties fToolchainProperties;

	// ------------------------------------------------------------------------

	// TODO: remove DEPRECATED
	// Non-standard location:
	// eclipse/configuration/ilg.gnumcueclipse.managedbuild.cross.riscv/toolchain.prefs/name=value

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
