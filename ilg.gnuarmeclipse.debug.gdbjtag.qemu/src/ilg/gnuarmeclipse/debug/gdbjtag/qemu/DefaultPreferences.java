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

package ilg.gnuarmeclipse.debug.gdbjtag.qemu;

import ilg.gnuarmeclipse.core.EclipseUtils;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class DefaultPreferences {

	// ------------------------------------------------------------------------

	protected static final boolean SERVER_DO_START_DEFAULT = true;
	public static final boolean DO_START_GDB_SERVER_DEFAULT = true;
	public static final String SERVER_EXECUTABLE_DEFAULT = "${qemu_path}/${qemu_executable}";
	protected static final String CLIENT_EXECUTABLE_DEFAULT = "${cross_prefix}gdb${cross_suffix}";

	public static final String SERVER_EXECUTABLE_DEFAULT_NAME = "qemu-system-gnuarmeclipse";

	public static final String QEMU_BOARD_NAME_DEFAULT = "?";
	public static final String QEMU_DEVICE_NAME_DEFAULT = "?";

	public static final int SERVER_GDB_PORT_NUMBER_DEFAULT = 1234;
	public static final String SERVER_OTHER_OPTIONS_DEFAULT = "-d unimp,guest_errors"; //$NON-NLS-1$

	public static final boolean DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT = true;

	public static final boolean QEMU_IS_VERBOSE_DEFAULT = false;

	public static final String CLIENT_OTHER_OPTIONS_DEFAULT = "";

	public static final String CLIENT_COMMANDS_DEFAULT = "set mem inaccessible-by-default off\n";

	public static final boolean USE_REMOTE_TARGET_DEFAULT = true;
	public static final String REMOTE_IP_ADDRESS_LOCALHOST = "localhost"; //$NON-NLS-1$
	public static final String REMOTE_IP_ADDRESS_DEFAULT = REMOTE_IP_ADDRESS_LOCALHOST; // $NON-NLS-1$
	public static final int REMOTE_PORT_NUMBER_DEFAULT = SERVER_GDB_PORT_NUMBER_DEFAULT;

	public static final boolean DO_INITIAL_RESET_DEFAULT = false;
	public static final String DO_INITIAL_RESET_COMMAND = "monitor system_reset ";

	public static final String HALT_COMMAND = ""; // "monitor stop";

	public static final boolean ENABLE_SEMIHOSTING_DEFAULT = true;

	public static final String ENABLE_SEMIHOSTING_OPTION = "-semihosting-config enable=on,target=native";

	public static final String INIT_OTHER_DEFAULT = "";

	public static final boolean DO_DEBUG_IN_RAM_DEFAULT = false;

	public static final boolean DO_PRERUN_RESET_DEFAULT = true;
	public static final String DO_PRERUN_RESET_COMMAND = DO_INITIAL_RESET_COMMAND;

	public static final boolean DO_STOP_AT_DEFAULT = true;
	public static final String STOP_AT_NAME_DEFAULT = "main";

	public static final String PRERUN_OTHER_DEFAULT = "";

	public static final boolean DO_CONTINUE_DEFAULT = true;

	public static final String DO_CONTINUE_COMMAND = "continue";
	public static final boolean DISABLE_GRAPHICS_DEFAULT = false;

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

	public static int getInt(String name, int defValue) {

		return getPreferences().getInt(name, defValue);
	}

	public static void putString(String key, String value) {
		getPreferences().put(key, value);
	}

	public static void putInt(String key, int value) {
		getPreferences().putInt(key, value);
	}

	public static void putBoolean(String key, boolean value) {
		getPreferences().putBoolean(key, value);
	}

	// ------------------------------------------------------------------------

	public static String getGdbServerExecutable() {
		return getString(PersistentPreferences.GDB_SERVER_EXECUTABLE, SERVER_EXECUTABLE_DEFAULT);
	}

	public static String getGdbClientExecutable() {
		return getString(PersistentPreferences.GDB_CLIENT_EXECUTABLE, CLIENT_EXECUTABLE_DEFAULT);
	}

	// ------------------------------------------------------------------------

	public static String getExecutableName() {

		String key = PersistentPreferences.EXECUTABLE_NAME;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("getExecutableName()=\"" + value + "\"");
		}
		return value;
	}

	public static String getExecutableNameOs() {

		String key = EclipseUtils.getKeyOs(PersistentPreferences.EXECUTABLE_NAME_OS);

		String value = getString(key, "");
		if (Activator.getInstance().isDebugging()) {
			System.out.println("getExecutableNameOs()=\"" + value + "\" (" + key + ")");
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
		String value = getString(key, "");

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

		String key = EclipseUtils.getKeyOs(PersistentPreferences.SEARCH_PATH_OS);
		String value = getString(key, "");
		if (Activator.getInstance().isDebugging()) {
			System.out.println("getSearchPathOs()=\"" + value + "\" (" + key + ")");
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

	public static boolean getQemuEnableSemihosting() {

		return getBoolean(PersistentPreferences.GDB_QEMU_ENABLE_SEMIHOSTING, ENABLE_SEMIHOSTING_DEFAULT);
	}

	// ------------------------------------------------------------------------

	public static boolean getTabMainCheckProgram() {
		return getBoolean(PersistentPreferences.TAB_MAIN_CHECK_PROGRAM,
				PersistentPreferences.TAB_MAIN_CHECK_PROGRAM_DEFAULT);
	}

	// ------------------------------------------------------------------------

	public static boolean getGdbServerDoStart() {
		return getBoolean(PersistentPreferences.GDB_SERVER_DO_START, SERVER_DO_START_DEFAULT);
	}

	public static String getGdbServerOtherOptions() {
		return getString(PersistentPreferences.GDB_SERVER_OTHER_OPTIONS, SERVER_OTHER_OPTIONS_DEFAULT);
	}

	public static String getGdbClientOtherOptions() {
		return getString(PersistentPreferences.GDB_CLIENT_OTHER_OPTIONS, CLIENT_OTHER_OPTIONS_DEFAULT);
	}

	public static String getGdbClientCommands() {
		return getString(PersistentPreferences.GDB_CLIENT_COMMANDS, CLIENT_COMMANDS_DEFAULT);
	}

	// ------------------------------------------------------------------------

	public static boolean getQemuDebugInRam() {
		return getBoolean(PersistentPreferences.GDB_QEMU_DO_DEBUG_IN_RAM, DO_DEBUG_IN_RAM_DEFAULT);
	}

	public static boolean getQemuDoInitialReset() {
		return getBoolean(PersistentPreferences.GDB_QEMU_DO_INITIAL_RESET, DO_INITIAL_RESET_DEFAULT);
	}

	public static String getQemuInitOther() {
		return getString(PersistentPreferences.GDB_QEMU_INIT_OTHER, INIT_OTHER_DEFAULT);
	}

	public static boolean getQemuDoPreRunReset() {
		return getBoolean(PersistentPreferences.GDB_QEMU_DO_PRERUN_RESET, DO_PRERUN_RESET_DEFAULT);
	}

	public static String getQemuPreRunOther() {
		return getString(PersistentPreferences.GDB_QEMU_PRERUN_OTHER, PRERUN_OTHER_DEFAULT);
	}

	public static boolean getQemuDisableGraphics() {
		return getBoolean(PersistentPreferences.GDB_QEMU_DISABLE_GRAPHICS, DISABLE_GRAPHICS_DEFAULT);
	}

	// ------------------------------------------------------------------------
}
