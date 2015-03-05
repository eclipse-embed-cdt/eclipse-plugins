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

package ilg.gnuarmeclipse.managedbuild.cross.ui;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.managedbuild.cross.Activator;
import ilg.gnuarmeclipse.managedbuild.cross.ToolchainDefinition;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.cdt.utils.WindowsRegistry;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

public class DefaultPreferences {

	// ------------------------------------------------------------------------

	// LOCAL_MACHINE
	private static final String REG_SUBKEY = "SOFTWARE\\GNU ARM Eclipse\\Build Tools";
	private static final String REG32_SUBKEY = "SOFTWARE\\Wow6432Node\\GNU ARM Eclipse\\Build Tools";
	private static final String REG_NAME = "InstallFolder";

	// ------------------------------------------------------------------------

	/**
	 * The DefaultScope preference store.
	 */
	private static IEclipsePreferences fgPreferences;

	// ------------------------------------------------------------------------

	private static IEclipsePreferences getPreferences() {

		if (fgPreferences == null) {
			fgPreferences = DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		}

		return fgPreferences;
	}

	/**
	 * Get a string preference value, or the default.
	 * 
	 * @param key
	 *            a string with the key to search.
	 * @param defaulValue
	 *            a string with the default, possibly null.
	 * @return a trimmed string, or a null default.
	 */
	public static String getString(String key, String defaulValue) {

		String value;
		value = getPreferences().get(key, defaulValue);

		if (value != null) {
			value = value.trim();
		}

		return value;
	}

	public static boolean getBoolean(String key, boolean defaultValue) {

		return getPreferences().getBoolean(key, defaultValue);
	}

	private static void putString(String key, String value) {
		getPreferences().put(key, value);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the default toolchain name.
	 * 
	 * @return a trimmed string, possibly empty.
	 */
	public static String getToolchainName() {

		String value = getString(PersistentPreferences.TOOLCHAIN_NAME_KEY, null);
		if (value != null && !value.isEmpty()) {
			return value;
		}

		value = "";
		{
			// TODO: remove DEPRECATED
			try {
				Properties prop = getToolchainProperties();
				value = prop.getProperty(DEFAULT_NAME, "").trim();
			} catch (IOException e) {
				;
			}
		}

		return value;
	}

	public static void putToolchainName(String value) {

		String key = PersistentPreferences.TOOLCHAIN_NAME_KEY;
		if (Activator.getInstance().isDebugging()) {
			System.out.println("Default " + key + "=" + value);
		}
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
	public static String getToolchainPath(String toolchainName) {

		String value = getString(
				PersistentPreferences.getToolchainKey(toolchainName), null);
		if (value != null && !value.isEmpty()) {
			return value;
		}

		value = "";
		{
			// TODO: remove DEPRECATED
			try {
				Properties prop = getToolchainProperties();
				int hash = Math.abs(toolchainName.trim().hashCode());
				value = prop.getProperty(
						DEFAULT_PATH + "." + String.valueOf(hash), "").trim();
			} catch (IOException e) {
				;
			}
		}

		return value;
	}

	public static void putToolchainPath(String toolchainName, String value) {

		String key = PersistentPreferences.getToolchainKey(toolchainName);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("Default " + key + "=" + value);
		}
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
	public static String getToolchainSearchPath(String toolchainName) {

		String key = PersistentPreferences.getToolchainSearchKey(toolchainName);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("Check " + key + " for \"" + toolchainName
					+ "\"");
		}
		String value = getString(
				PersistentPreferences.getToolchainSearchKey(toolchainName), "");

		return value;
	}

	public static void putToolchainSearchPath(String toolchainName, String value) {

		String key = PersistentPreferences.getToolchainSearchKey(toolchainName);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("Default " + key + "=" + value);
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
	public static String getToolchainSearchPathOs(String toolchainName) {

		String value = getString(
				PersistentPreferences.getToolchainSearchOsKey(toolchainName),
				"");

		return value;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the default value for the build tools path.
	 * 
	 * @return a trimmed string, possibly empty.
	 */
	public static String getBuildToolsPath() {

		return getString(PersistentPreferences.BUILD_TOOLS_PATH_KEY, "");
	}

	public static void putBuildToolsPath(String value) {

		String key = PersistentPreferences.BUILD_TOOLS_PATH_KEY;
		if (Activator.getInstance().isDebugging()) {
			System.out.println("Default " + key + "=" + value);
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

			WindowsRegistry registry = WindowsRegistry.getRegistry();
			if (registry != null) {
				value = registry.getLocalMachineValue(REG_SUBKEY, REG_NAME);
				if (value == null) {
					value = registry.getLocalMachineValue(REG32_SUBKEY,
							REG_NAME);
				}

				if (value != null && !value.endsWith("\\bin")) {
					value += "\\bin";
				}
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
			System.out.println("DefaultPreferences.discoverBuildToolsPath()=\""
					+ value + "\"");
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
	private static String getLastToolchain(String folder,
			final String executableName) {

		List<String> list = new ArrayList<String>();
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
				IPath path = (new Path(dir.getAbsolutePath())).append(name)
						.append("bin").append(executableName);

				if (path.toFile().isFile()) {
					return true;
				}
				return false;
			}
		});

		if (files == null || files.length == 0) {
			return null;
		}

		for (int i = 0; i < files.length; ++i) {
			list.add(files[i].getName());
		}

		// The sort criteria is the lexicographical order on folder name.
		Collections.sort(list);

		// Get the last name in ordered list.
		String last = list.get(list.size() - 1);

		// System.out.println(last);
		IPath path = (new Path(folder)).append(last).append("bin");
		return path.toString();
	}

	/**
	 * Try to find a possible match for the toolchain, in the given path.
	 * 
	 * @param toolchainName
	 * @param searchPath
	 *            a string with a sequence of folders.
	 * @return a String with the absolute folder path, or null if not found.
	 */
	public static String discoverToolchainPath(String toolchainName,
			String searchPath) {

		if (searchPath == null || searchPath.isEmpty()) {
			return null;
		}

		// Resolve ${user.home}
		String resolvedPath = searchPath;
		if (resolvedPath.indexOf("${user.home}") >= 0) {
			resolvedPath = resolvedPath.replaceAll("\\$\\{user.home\\}",
					System.getProperty("user.home"));

		}

		// If more macros remain, use the usual substituter.
		if (resolvedPath.indexOf("${") >= 0) {
			IStringVariableManager variableManager = VariablesPlugin
					.getDefault().getStringVariableManager();
			try {
				resolvedPath = variableManager.performStringSubstitution(
						resolvedPath, false);
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

		int ix;
		try {
			ix = ToolchainDefinition.findToolchainByName(toolchainName);
		} catch (IndexOutOfBoundsException e) {
			ix = ToolchainDefinition.getDefault();
		}
		String executableName = ToolchainDefinition.getToolchain(ix)
				.getFullCmdC();
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

	// TODO: remove DEPRECATED
	public static final String DEFAULT_NAME = "default.name";
	public static final String DEFAULT_PATH = "default.path";
	public static final String TOOLCHAIN = "toolchains.prefs";
	private static Properties fgToolchainProperties;

	// ------------------------------------------------------------------------

	// TODO: remove DEPRECATED
	// Non-standard location:
	// eclipse/configuration/ilg.gnuarmeclipse.managedbuild.cross/toolchain.prefs/name=value

	private static Properties getToolchainProperties() throws IOException {

		if (fgToolchainProperties == null) {

			URL url = Platform.getInstallLocation().getURL();

			IPath path = new Path(url.getPath());
			File file = path.append("configuration")
					.append(Activator.PLUGIN_ID).append(TOOLCHAIN).toFile();
			InputStream is = new FileInputStream(file);

			Properties prop = new Properties();
			prop.load(is);

			fgToolchainProperties = prop;
		}

		return fgToolchainProperties;
	}

	// ------------------------------------------------------------------------
}
