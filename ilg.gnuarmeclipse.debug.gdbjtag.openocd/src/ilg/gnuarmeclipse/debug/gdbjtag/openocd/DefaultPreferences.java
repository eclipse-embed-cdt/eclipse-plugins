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

import ilg.gnuarmeclipse.core.EclipseUtils;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class DefaultPreferences {

	// ------------------------------------------------------------------------

	// These values are deprecated. Use the definitions in PersistentValues.
	private static final String GDB_SERVER_EXECUTABLE_DEPRECATED = "gdb.server.executable.default";
	private static final String GDB_CLIENT_EXECUTABLE_DEPRECATED = "gdb.client.executable.default";

	private static final String OPENOCD_CONFIG_DEPRECATED = "openocd.config.default";

	private static final String OPENOCD_EXECUTABLE_DEPRECATED = "openocd_executable.default";
	private static final String OPENOCD_PATH_DEPRECATED = "openocd_path.default";

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
}
