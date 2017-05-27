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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.core.preferences.Discoverer;
import ilg.gnumcueclipse.managedbuild.cross.Activator;

public class DefaultPreferences {

	// ------------------------------------------------------------------------

	// HKCU & HKLM LOCAL_MACHINE
	private static final String REG_SUBKEY = "\\GNU MCU Eclipse\\Build Tools";
	private static final String REG_SUBKEY_DEPRECATED = "\\GNU ARM Eclipse\\Build Tools";
	// Standard Microsoft recommendation.
	private static final String REG_NAME = "InstallLocation";
	// Custom name, used before reading the standard.
	private static final String REG_NAME_DEPRECATED = "InstallFolder";

	private static final String EXECUTABLE_NAME = "make.exe";

	// ------------------------------------------------------------------------

	private String fPluginId;

	/**
	 * The DefaultScope preference store.
	 */
	private IEclipsePreferences fPreferences;

	// ------------------------------------------------------------------------

	public DefaultPreferences(String pluginId) {
		fPluginId = pluginId;
		fPreferences = DefaultScope.INSTANCE.getNode(fPluginId);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get a string preference value, or the default.
	 * 
	 * @param key
	 *            a string with the key to search.
	 * @param defaultValue
	 *            a string with the default, possibly null.
	 * @return a trimmed string, or a null default.
	 */
	public String getString(String key, String defaultValue) {

		String value;
		value = fPreferences.get(key, defaultValue);

		if (value != null) {
			value = value.trim();
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getString(" + key + ", \"" + defaultValue + "\") "
					+ fPreferences.name() + " = \"" + value + "\"");
		}

		return value;
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		boolean value = fPreferences.getBoolean(key, defaultValue);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getString(" + key + ", \"" + defaultValue + "\") "
					+ fPreferences.name() + " = " + value);
		}
		return value;
	}

	private void putString(String key, String value) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putString(" + key + ", \"" + value + "\") " + fPreferences.name());
		}
		fPreferences.put(key, value);
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

	/**
	 * Find where the build tools might have been installed. The returned folder
	 * is known to be an existing folder.
	 * 
	 * @return a trimmed string, possibly empty.
	 */
	public static String discoverBuildToolsPath() {

		String value = null;
		if (EclipseUtils.isWindows()) {

			value = Discoverer.getRegistryInstallFolder(EXECUTABLE_NAME, "bin", REG_SUBKEY, REG_NAME);
			if (value == null) {
				value = Discoverer.getRegistryInstallFolder(EXECUTABLE_NAME, "bin", REG_SUBKEY_DEPRECATED, REG_NAME);
			}
			if (value == null) {
				value = Discoverer.getRegistryInstallFolder(EXECUTABLE_NAME, "bin", REG_SUBKEY_DEPRECATED,
						REG_NAME_DEPRECATED);
			}

		} else if (EclipseUtils.isMacOSX()) {
			// value = "/opt/local/bin";
		}

		if (value != null) {
			value = value.trim();

			// Validate registry path. If folder does not exist, ignore.
			File file = new File(value);
			if (!file.isDirectory()) {
				value = "";
			}
		} else {
			value = "";
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.discoverBuildToolsPath() = \"" + value + "\"");
		}

		return value;
	}

	/**
	 * Return the last (in lexicographical order) folder that contain
	 * "bin/executable".
	 * 
	 * @param folder
	 * @param executableName
	 * @return a String with the folder absolute path, or null if not found.
	 */
	private static String getLastToolchain(String folder, final String executableName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getLastToolchain(\"" + folder + "\", \"" + executableName + "\") ");
		}
		File local = new File(folder);
		if (!local.isDirectory()) {
			// System.out.println(folder + " not a folder");
			return null;
		}

		File[] files = local.listFiles(new FilenameFilter() {

			/**
			 * Filter to select only
			 */
			@Override
			public boolean accept(File dir, String name) {
				IPath path = (new Path(dir.getAbsolutePath())).append(name).append("bin").append(executableName);

				if (path.toFile().isFile()) {
					return true;
				}
				return false;
			}
		});

		if (files == null || files.length == 0) {
			return null;
		}

		IPath latestPath = null;
		long latestDate = 0;

		for (int i = 0; i < files.length; ++i) {
			IPath path = (new Path(files[i].getAbsolutePath())).append("bin").append(executableName);
			long date = path.toFile().lastModified();
			if (date > latestDate) {
				latestPath = (new Path(files[i].getAbsolutePath())).append("bin");
				latestDate = date;
			}
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getLastToolchain(\"" + folder + "\", \"" + executableName
					+ "\") = \"" + latestPath.toString() + "\"");
		}

		return latestPath.toString();
	}

	/**
	 * Try to find a possible match for the toolchain, in the given path.
	 * 
	 * @param toolchainName
	 * @param searchPath
	 *            a string with a sequence of folders.
	 * @return a String with the absolute folder path, or null if not found.
	 */
	public static String discoverToolchainPath(String toolchainName, String searchPath, String executableName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.discoverToolchainPath(\"" + toolchainName + "\", \"" + searchPath
					+ "\", " + executableName + ") ");
		}

		if (searchPath == null || searchPath.isEmpty()) {
			return null;
		}

		// Resolve ${user.home}
		String resolvedPath = searchPath;
		if (resolvedPath.indexOf("${user.home}") >= 0) {
			resolvedPath = resolvedPath.replaceAll("\\$\\{user.home\\}", System.getProperty("user.home"));

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

		// Try paths in order; return the first.
		for (int i = 0; i < paths.length; ++i) {
			String value = getLastToolchain(paths[i], executableName);
			if (value != null && !value.isEmpty()) {
				return value;
			}
		}

		return null;
	}

	// ------------------------------------------------------------------------

	public static final String TOOLCHAIN = "toolchains.prefs";

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
