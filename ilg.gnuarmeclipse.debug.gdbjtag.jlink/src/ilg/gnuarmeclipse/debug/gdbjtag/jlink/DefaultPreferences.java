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

import ilg.gnuarmeclipse.core.EclipseUtils;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

public class DefaultPreferences {

	// ------------------------------------------------------------------------

	// TODO: remove DEPRECATED
	// These values are deprecated. Use the definitions in PersistentValues.
	private static final String GDB_SERVER_EXECUTABLE_DEPRECATED = "gdb.server.executable.default";
	private static final String GDB_CLIENT_EXECUTABLE_DEPRECATED = "gdb.client.executable.default";

	private static final String JLINK_INTRFACE_DEPRECATED = "interface.default";
	private static final String JLINK_ENABLE_SEMIHOSTING_DEPRECATED = "enableSemihosting.default";
	private static final String JLINK_ENABLE_SWO_DEPRECATED = "enableSwo.default";

	private static final String JLINK_GDBSERVER_DEPRECATED = "jlink_gdbserver.default";
	private static final String JLINK_PATH_DEPRECATED = "jlink_path.default";

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

	private static int getInt(String name, int defValue) {

		return getPreferences().getInt(name, defValue);
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

	public static String getExecutableName() {

		String key = PersistentPreferences.EXECUTABLE_NAME;
		String value = getString(key, null);
		if (value == null) {

			// TODO: remove DEPRECATED
			value = getString(PersistentPreferences.JLINK_GDBSERVER_DEPRECATED,
					null);
			if (value == null) {
				value = getString(JLINK_GDBSERVER_DEPRECATED, "");
			}
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("getExecutableName()=\"" + value + "\"");
		}
		return value;

	}

	public static String getExecutableNameOs() {

		String key = EclipseUtils
				.getKeyOs(PersistentPreferences.EXECUTABLE_NAME_OS);

		String value = getString(key, "");
		if (Activator.getInstance().isDebugging()) {
			System.out.println("getExecutableNameOs()=\"" + value + "\" ("
					+ key + ")");
		}
		return value;
	}

	public static void putExecutableName(String value) {

		String key = PersistentPreferences.EXECUTABLE_NAME;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Default " + key + "=" + value);
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	public static String getInstallFolder() {

		String key = PersistentPreferences.INSTALL_FOLDER;
		String value = getString(key, null);
		if (value == null) {

			// TODO: remove DEPRECATED
			value = getString(PersistentPreferences.JLINK_PATH_DEPRECATED, null);
			if (value == null) {
				value = getString(JLINK_PATH_DEPRECATED, "");
			}
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("getInstallFolder()=\"" + value + "\"");
		}
		return value;
	}

	public static void putInstallFolder(String value) {

		String key = PersistentPreferences.INSTALL_FOLDER;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Default " + key + "=" + value);
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	public static String getSearchPath() {

		String key = PersistentPreferences.SEARCH_PATH;
		String value = getString(key, "");
		if (Activator.getInstance().isDebugging()) {
			System.out.println("getSearchPath()=\"" + value + "\"");
		}
		return value;
	}

	public static String getSearchPathOs() {

		String key = EclipseUtils
				.getKeyOs(PersistentPreferences.SEARCH_PATH_OS);
		String value = getString(key, "");
		if (Activator.getInstance().isDebugging()) {
			System.out.println("getSearchPathOs()=\"" + value + "\" (" + key
					+ ")");
		}
		return value;
	}

	public static void putSearchPath(String value) {

		String key = PersistentPreferences.SEARCH_PATH;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Default " + key + "=" + value);
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	public static String getGdbServerInterface(String defValue) {
		String value = getString(PersistentPreferences.GDB_SERVER_INTERFACE,
				null);
		if (value != null) {
			return value;
		}
		return getString(JLINK_INTRFACE_DEPRECATED, defValue);
	}

	public static boolean getJLinkEnableSemihosting(boolean defValue) {

		try {
			if (getPreferences().nodeExists(
					PersistentPreferences.GDB_JLINK_ENABLE_SEMIHOSTING)) {
				return getBoolean(
						PersistentPreferences.GDB_JLINK_ENABLE_SEMIHOSTING,
						defValue);
			}
		} catch (BackingStoreException e) {
			;
		}
		return getBoolean(JLINK_ENABLE_SEMIHOSTING_DEPRECATED, defValue);
	}

	public static boolean getJLinkEnableSwo(boolean defValue) {
		try {
			if (getPreferences().nodeExists(
					PersistentPreferences.GDB_JLINK_ENABLE_SWO)) {
				return getBoolean(PersistentPreferences.GDB_JLINK_ENABLE_SWO,
						defValue);
			}
		} catch (BackingStoreException e) {
			;
		}
		return getBoolean(JLINK_ENABLE_SWO_DEPRECATED, defValue);
	}

	// ------------------------------------------------------------------------

	public static boolean getTabMainCheckProgram() {
		return getBoolean(PersistentPreferences.TAB_MAIN_CHECK_PROGRAM,
				PersistentPreferences.TAB_MAIN_CHECK_PROGRAM_DEFAULT);
	}

	// ------------------------------------------------------------------------

	public static boolean getGdbServerDoStart(boolean defaultValue) {
		return getBoolean(PersistentPreferences.GDB_SERVER_DO_START,
				defaultValue);
	}

	public static String getGdbServerEndianness(String defaultValue) {
		return getString(PersistentPreferences.GDB_SERVER_ENDIANNESS,
				defaultValue);
	}

	public static String getGdbServerConnection(String defaultValue) {
		return getString(PersistentPreferences.GDB_SERVER_CONNECTION,
				defaultValue);
	}

	public static String getGdbServerConnectionAddress(String defaultValue) {
		return getString(PersistentPreferences.GDB_SERVER_CONNECTION_ADDRESS,
				defaultValue);
	}

	public static String getGdbServerInitialSpeed(String defaultValue) {
		return getString(PersistentPreferences.GDB_SERVER_INITIAL_SPEED,
				defaultValue);
	}

	public static String getGdbServerOtherOptions(String defaultValue) {
		return getString(PersistentPreferences.GDB_SERVER_OTHER_OPTIONS,
				defaultValue);
	}

	public static String getGdbClientOtherOptions(String defaultValue) {
		return getString(PersistentPreferences.GDB_CLIENT_OTHER_OPTIONS,
				defaultValue);
	}

	public static String getGdbClientCommands(String defaultValue) {
		return getString(PersistentPreferences.GDB_CLIENT_COMMANDS,
				defaultValue);
	}

	public static boolean getJLinkDoInitialReset(boolean defaultValue) {
		return getBoolean(PersistentPreferences.GDB_JLINK_DO_INITIAL_RESET,
				defaultValue);
	}

	public static String getJLinkInitialResetType(String defaultValue) {
		return getString(PersistentPreferences.GDB_JLINK_INITIAL_RESET_TYPE,
				defaultValue);
	}

	public static int getJLinkInitialResetSpeed(int defaultValue) {
		return getInt(PersistentPreferences.GDB_JLINK_INITIAL_RESET_SPEED,
				defaultValue);
	}

	public static String getJLinkSpeed(String defaultValue) {
		return getString(PersistentPreferences.GDB_JLINK_SPEED, defaultValue);
	}

	public static boolean getJLinkEnableFlashBreakpoints(boolean defaultValue) {
		return getBoolean(
				PersistentPreferences.GDB_JLINK_ENABLE_FLASH_BREAKPOINTS,
				defaultValue);
	}

	public static boolean getJLinkSemihostingTelnet(boolean defaultValue) {
		return getBoolean(PersistentPreferences.GDB_JLINK_SEMIHOSTING_TELNET,
				defaultValue);
	}

	public static boolean getJLinkSemihostingClient(boolean defaultValue) {
		return getBoolean(PersistentPreferences.GDB_JLINK_SEMIHOSTING_CLIENT,
				defaultValue);
	}

	public static int getJLinkSwoEnableTargetCpuFreq(int defaultValue) {
		return getInt(
				PersistentPreferences.GDB_JLINK_SWO_ENABLE_TARGET_CPU_FREQ,
				defaultValue);
	}

	public static int getJLinkSwoEnableTargetSwoFreq(int defaultValue) {
		return getInt(
				PersistentPreferences.GDB_JLINK_SWO_ENABLE_TARGET_SWO_FREQ,
				defaultValue);
	}

	public static String getJLinkSwoEnableTargetPortMask(String defaultValue) {
		return getString(
				PersistentPreferences.GDB_JLINK_SWO_ENABLE_TARGET_PORT_MASK,
				defaultValue);
	}

	public static String getJLinkInitOther(String defaultValue) {
		return getString(PersistentPreferences.GDB_JLINK_INIT_OTHER,
				defaultValue);
	}

	public static boolean getJLinkDebugInRam(boolean defaultValue) {
		return getBoolean(PersistentPreferences.GDB_JLINK_DO_DEBUG_IN_RAM,
				defaultValue);
	}

	public static boolean getJLinkDoPreRunReset(boolean defaultValue) {
		return getBoolean(PersistentPreferences.GDB_JLINK_DO_PRERUN_RESET,
				defaultValue);
	}

	public static String getJLinkPreRunResetType(String defaultValue) {
		return getString(PersistentPreferences.GDB_JLINK_PRERUN_RESET_TYPE,
				defaultValue);
	}

	public static String getJLinkPreRunOther(String defaultValue) {
		return getString(PersistentPreferences.GDB_JLINK_PRERUN_OTHER,
				defaultValue);
	}

	// ------------------------------------------------------------------------
}
