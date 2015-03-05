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

package ilg.gnuarmeclipse.debug.gdbjtag.openocd;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.core.StringUtils;

import org.eclipse.cdt.utils.WindowsRegistry;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

public class DefaultPreferences {

	// ------------------------------------------------------------------------

	// These values are deprecated. Use the definitions in PersistentValues.
	private static final String GDB_SERVER_EXECUTABLE_DEPRECATED = "gdb.server.executable.default";
	private static final String GDB_CLIENT_EXECUTABLE_DEPRECATED = "gdb.client.executable.default";

	private static final String OPENOCD_CONFIG_DEPRECATED = "openocd.config.default";

	private static final String OPENOCD_EXECUTABLE_DEPRECATED = "openocd_executable.default";
	private static final String OPENOCD_PATH_DEPRECATED = "openocd_path.default";

	// private static final String TAB_MAIN_CHECK_PROGRAM =
	// "tab.main.checkProgram";
	// private static final boolean TAB_MAIN_CHECK_PROGRAM_DEFAULT = false;

	// ------------------------------------------------------------------------

	// LOCAL_MACHINE
	private static final String SPECIFIC_SUBKEY = "\\GNU ARM Eclipse\\OpenOCD";
	private static final String REG_SUBKEY = "SOFTWARE" + SPECIFIC_SUBKEY;
	private static final String REG32_SUBKEY = "SOFTWARE\\Wow6432Node"
			+ SPECIFIC_SUBKEY;
	private static final String REG_NAME = "InstallFolder";

	// ------------------------------------------------------------------------

	/**
	 * The DefaultScope preference store.
	 */
	private static IEclipsePreferences fgPreferences;

	// ------------------------------------------------------------------------

	public static IEclipsePreferences getPreferences() {

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
	private static String getString(String key, String defaulValue) {

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

	public static String getGdbServerExecutable(String defValue) {
		String value = getString(PersistentPreferences.GDB_SERVER_EXECUTABLE,
				null);
		if (value != null) {
			return value;
		}
		return getString(GDB_SERVER_EXECUTABLE_DEPRECATED, defValue);
	}

	public static String getGdbClientExecutable(String defValue) {
		String value = getString(PersistentPreferences.GDB_CLIENT_EXECUTABLE,
				null);
		if (value != null) {
			return value;
		}
		return getString(GDB_CLIENT_EXECUTABLE_DEPRECATED, defValue);
	}

	// ------------------------------------------------------------------------

	public static String getOpenocdConfig(String defValue) {
		String value = getString(
				PersistentPreferences.GDB_SERVER_OTHER_OPTIONS, null);
		if (value != null) {
			return value;
		}
		return getString(OPENOCD_CONFIG_DEPRECATED, defValue);
	}

	// ------------------------------------------------------------------------

	public static boolean getTabMainCheckProgram() {
		return getBoolean(PersistentPreferences.TAB_MAIN_CHECK_PROGRAM,
				PersistentPreferences.TAB_MAIN_CHECK_PROGRAM_DEFAULT);
	}

	// ------------------------------------------------------------------------

	public static String getExecutableName() {

		String key = PersistentPreferences.OPENOCD_EXECUTABLE;
		String value = getString(key, null);
		if (value == null) {

			// TODO: remove DEPRECATED
			value = getString(
					PersistentPreferences.OPENOCD_EXECUTABLE_DEPRECATED, null);
			if (value == null) {
				value = getString(OPENOCD_EXECUTABLE_DEPRECATED, "");
			}
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("getExecutableName()=\"" + value + "\"");
		}
		return value;
	}

	public static String getExecutableNameOs() {

		String key = EclipseUtils
				.getKeyOs(PersistentPreferences.OPENOCD_EXECUTABLE_OS);

		String value = getString(key, "");
		if (Activator.getInstance().isDebugging()) {
			System.out.println("getExecutableNameOs()=\"" + value + "\" ("
					+ key + ")");
		}
		return value;
	}

	public static void putExecutableName(String value) {

		String key = PersistentPreferences.OPENOCD_EXECUTABLE;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Default " + key + "=" + value);
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	public static String getInstallFolder() {

		String key = PersistentPreferences.OPENOCD_FOLDER;
		String value = getString(key, null);
		if (value == null) {

			// TODO: remove DEPRECATED
			value = getString(PersistentPreferences.OPENOCD_PATH_DEPRECATED,
					null);
			if (value == null) {
				value = getString(OPENOCD_PATH_DEPRECATED, "");
			}
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("getInstallFolder()=\"" + value + "\"");
		}
		return value;
	}

	public static void putInstallFolder(String value) {

		String key = PersistentPreferences.OPENOCD_FOLDER;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Default " + key + "=" + value);
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	public static String getInstallSearchPath() {

		String key = PersistentPreferences.OPENOCD_SEARCH_PATH;
		String value = getString(key, "");
		if (Activator.getInstance().isDebugging()) {
			System.out.println("getInstallSearchPath()=\"" + value + "\"");
		}
		return value;
	}

	public static String getInstallSearchPathOs() {

		String key = EclipseUtils
				.getKeyOs(PersistentPreferences.OPENOCD_SEARCH_PATH_OS);
		String value = getString(key, "");
		if (Activator.getInstance().isDebugging()) {
			System.out.println("getInstallSearchPathOs()=\"" + value + "\" ("
					+ key + ")");
		}
		return value;
	}

	public static void putInstallSearchPath(String value) {

		String key = PersistentPreferences.OPENOCD_SEARCH_PATH;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Default " + key + "=" + value);
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	/**
	 * Return the last (in lexicographical order) folder that contain
	 * "bin/executable". If not found, the folder itself is checked.
	 * 
	 * The returned path includes the ending /bin.
	 * 
	 * @param folder
	 * @param executableName
	 * @return a String with the folder absolute path, or null if not found.
	 */
	public static String getLastExecutable(String folderName,
			final String executableName) {

		IPath folderPath = new Path(folderName);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("getLastExecutable(" + folderPath + ", "
					+ executableName + ")");
		}

		List<String> list = new ArrayList<String>();
		File folder = folderPath.toFile();
		if (!folder.isDirectory()) {
			// System.out.println(folder + " not a folder");
			return null;
		}

		File[] files = folder.listFiles(new FilenameFilter() {

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

		if (files != null && files.length > 0) {

			for (int i = 0; i < files.length; ++i) {
				list.add(files[i].getName());
			}

			// The sort criteria is the lexicographical order on folder name.
			Collections.sort(list);

			// Get the last name in ordered list.
			String last = list.get(list.size() - 1);

			// System.out.println(last);
			IPath path = (new Path(folderName)).append(last).append("bin");
			return path.toString();
		} else {
			IPath path = (new Path(folderName)).append("bin").append(
					executableName);
			folder = path.toFile();
			if (folder.isFile()) {
				// System.out.println(folder + " not a folder");
				path = (new Path(folderName)).append("bin");
				return path.toString();
			}
		}

		return null;
	}

	/**
	 * Find where the OpenOCD might have been installed. The returned path is
	 * known to be an existing folder.
	 * 
	 * @param searchPath
	 *            a string with a sequence of folders.
	 * @return a String with the absolute folder path, or null if not found.
	 */
	public static String discoverInstallFolder(String searchPath) {

		String executableName = EclipseUtils
				.getVariableValue(VariableInitializer.VARIABLE_OPENOCD_EXECUTABLE);
		if (executableName == null || executableName.isEmpty()) {
			executableName = getExecutableName();
		}
		if (EclipseUtils.isWindows() && !executableName.endsWith(".exe")) {
			executableName += ".exe";
		}

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

				if (value != null) {
					IPath path = new Path(value);
					// Make portable
					value = path.toString(); // includes /bin
					if (Activator.getInstance().isDebugging()) {
						System.out.println("WinReg " + REG_NAME + " " + value);
					}

					File folder = path.append(executableName).toFile();
					if (folder.isFile()) {
						if (Activator.getInstance().isDebugging()) {
							System.out
									.println("DefaultPreferences.discoverInstallFolder() WinReg="
											+ value);
						}
						return value;
					}
				}
			}
		}

		if (searchPath == null || searchPath.isEmpty()) {
			return null;
		}

		// Resolve ${user.home}
		String resolvedPath = searchPath;
		if (resolvedPath.indexOf("${user.home}") >= 0) {
			String userHome = new Path(System.getProperty("user.home"))
					.toString();
			resolvedPath = resolvedPath.replaceAll("\\$\\{user.home\\}",
					userHome);

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
		} else if (EclipseUtils.isWindows()) {
			resolvedPath = StringUtils.duplicateBackslashes(resolvedPath);
		}

		// Split into multiple paths.
		String[] paths = resolvedPath.split(EclipseUtils.getPathSeparator());
		if (paths.length == 0) {
			return null;
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("discoverInstallFolder() resolved path "
					+ resolvedPath);
		}

		// Try paths in order; return the first.
		for (int i = 0; i < paths.length; ++i) {
			value = getLastExecutable(paths[i], executableName);
			if (value != null && !value.isEmpty()) {
				return value;
			}
		}

		return null;
	}

	// ------------------------------------------------------------------------
}
