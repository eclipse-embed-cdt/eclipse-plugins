/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.cdt.core.templateengine.SharedDefaults;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Manage a workspace preference file stored in:
 * 
 * <pre>
 * workspace/.metadata/.plugins/org.eclipse.core.runtime/.settings/<plug-in-id>.prefs
 * </pre>
 *
 * Some of the values may be retrieved from the EclipseDefaults.
 */
public class PersistentPreferences {

	// Tab Debugger
	// GDB Server Setup
	public static final String FLASH_DEVICE_NAME = "flashDeviceName";

	public static final String GDB_SERVER = "gdb.server.";

	public static final String GDB_SERVER_DO_START = GDB_SERVER + "doStart";

	public static final String GDB_SERVER_EXECUTABLE = GDB_SERVER + "executable";

	public static final String GDB_SERVER_ENDIANNESS = GDB_SERVER + "endianness";

	public static final String GDB_SERVER_CONNECTION = GDB_SERVER + "connection";
	public static final String GDB_SERVER_CONNECTION_ADDRESS = GDB_SERVER + "connection.address";

	public static final String GDB_SERVER_INTERFACE = GDB_SERVER + "interface";

	public static final String GDB_SERVER_INITIAL_SPEED = GDB_SERVER + "speed";

	public static final String GDB_SERVER_OTHER_OPTIONS = GDB_SERVER + "other";

	// GDB Client Setup
	public static final String GDB_CLIENT = "gdb.client.";

	public static final String GDB_CLIENT_EXECUTABLE = GDB_CLIENT + "executable";

	public static final String GDB_CLIENT_OTHER_OPTIONS = GDB_CLIENT + "other";

	public static final String GDB_CLIENT_COMMANDS = GDB_CLIENT + "commands";

	// Tab Startup
	// Initialisation Commands
	public static final String GDB_JLINK = "gdb.jlink.";

	public static final String GDB_JLINK_DO_INITIAL_RESET = GDB_JLINK + "doInitialReset";
	public static final String GDB_JLINK_INITIAL_RESET_TYPE = GDB_JLINK + "initialReset.type";
	public static final String GDB_JLINK_INITIAL_RESET_SPEED = GDB_JLINK + "initialReset.speed";

	public static final String GDB_JLINK_SPEED = GDB_JLINK + "speed";

	public static final String GDB_JLINK_ENABLE_FLASH_BREAKPOINTS = GDB_JLINK + "enableFlashBreakpoints";
	public static final String GDB_JLINK_ENABLE_SEMIHOSTING = GDB_JLINK + "enableSemihosting";
	public static final String GDB_JLINK_SEMIHOSTING_TELNET = GDB_JLINK + "semihosting.telnet";
	public static final String GDB_JLINK_SEMIHOSTING_CLIENT = GDB_JLINK + "semihosting.client";

	public static final String GDB_JLINK_ENABLE_SWO = GDB_JLINK + "enableSwo";

	public static final String GDB_JLINK_SWO_ENABLE_TARGET_CPU_FREQ = GDB_JLINK + "swoEnableTarget.cpuFreq";
	public static final String GDB_JLINK_SWO_ENABLE_TARGET_SWO_FREQ = GDB_JLINK + "swoEnableTarget.swoFreq";
	public static final String GDB_JLINK_SWO_ENABLE_TARGET_PORT_MASK = GDB_JLINK + "swoEnableTarget.portMask";

	public static final String GDB_JLINK_INIT_OTHER = GDB_JLINK + "init.other";

	// Run Commands
	public static final String GDB_JLINK_DO_DEBUG_IN_RAM = GDB_JLINK + "doDebugInRam";

	public static final String GDB_JLINK_DO_PRERUN_RESET = GDB_JLINK + "doPreRunReset";
	public static final String GDB_JLINK_PRERUN_RESET_TYPE = GDB_JLINK + "preRunReset.type";

	public static final String GDB_JLINK_PRERUN_OTHER = GDB_JLINK + "preRun.other";

	// ----- Defaults ---------------------------------------------------------

	public static final String EXECUTABLE_NAME = "executable.name";
	public static final String EXECUTABLE_NAME_OS = EXECUTABLE_NAME + ".%s";
	public static final String INSTALL_FOLDER = "install.folder";
	public static final String SEARCH_PATH = "search.path";
	public static final String SEARCH_PATH_OS = SEARCH_PATH + ".%s";

	public static final String FOLDER_STRICT = "folder.strict";

	public static final String TAB_MAIN_CHECK_PROGRAM = "tab.main.checkProgram";

	// TODO: remove DEPRECATED
	public static final String JLINK_GDBSERVER_DEPRECATED = "jlink_gdbserver";
	public static final String JLINK_PATH_DEPRECATED = "jlink_path";

	// ----- Getters ----------------------------------------------------------

	private static String getString(String id, String defaultValue) {

		String value;
		value = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, id, null, null);
		// System.out.println("Value of " + id + " is " + value);

		if (value != null) {
			return value;
		}

		// For compatibility reasons, still keep this for a while, on older
		// versions preferences were erroneously saved in the shared defaults.
		id = Activator.PLUGIN_ID + "." + id;

		value = SharedDefaults.getInstance().getSharedDefaultsMap().get(id);

		if (value == null)
			value = "";

		value = value.trim();
		if (!value.isEmpty()) {
			return value;
		}

		return defaultValue;
	}

	// ----- Setters ----------------------------------------------------------

	private static void putWorkspaceString(String id, String value) {

		value = value.trim();

		// Access the instanceScope
		Preferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		preferences.put(id, value);
	}

	public static void flush() {

		try {
			InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).flush();
		} catch (BackingStoreException e) {
			Activator.log(e);
		}
	}

	// ----- gdb server doStart -----------------------------------------------
	public static boolean getGdbServerDoStart() {

		return Boolean
				.valueOf(getString(GDB_SERVER_DO_START, Boolean.toString(DefaultPreferences.SERVER_DO_START_DEFAULT)));
	}

	public static void putGdbServerDoStart(boolean value) {

		putWorkspaceString(GDB_SERVER_DO_START, Boolean.toString(value));
	}

	// ----- gdb server executable --------------------------------------------
	public static String getGdbServerExecutable() {

		String value = getString(GDB_SERVER_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return DefaultPreferences.getGdbServerExecutable();
	}

	public static void putGdbServerExecutable(String value) {

		putWorkspaceString(GDB_SERVER_EXECUTABLE, value);
	}

	// ----- flash device id --------------------------------------------------
	public static String getFlashDeviceName() {

		return getString(FLASH_DEVICE_NAME, DefaultPreferences.FLASH_DEVICE_NAME_DEFAULT);
	}

	public static void putFlashDeviceName(String value) {

		putWorkspaceString(FLASH_DEVICE_NAME, value);
	}

	// ----- gdb server endianness --------------------------------------------
	public static String getGdbServerEndianness() {

		return getString(GDB_SERVER_ENDIANNESS, DefaultPreferences.SERVER_ENDIANNESS_DEFAULT);
	}

	public static void putGdbServerEndianness(String value) {

		putWorkspaceString(GDB_SERVER_ENDIANNESS, value);
	}

	// ----- gdb server connection --------------------------------------------
	public static String getGdbServerConnection() {

		return getString(GDB_SERVER_CONNECTION, DefaultPreferences.SERVER_CONNECTION_DEFAULT);
	}

	public static void putGdbServerConnection(String value) {

		putWorkspaceString(GDB_SERVER_CONNECTION, value);
	}

	// ----- gdb server connection address ------------------------------------
	public static String getGdbServerConnectionAddress() {

		return getString(GDB_SERVER_CONNECTION_ADDRESS, DefaultPreferences.SERVER_CONNECTION_ADDRESS_DEFAULT);
	}

	public static void putGdbServerConnectionAddress(String value) {

		putWorkspaceString(GDB_SERVER_CONNECTION_ADDRESS, value);
	}

	// ----- gdb server interface ---------------------------------------------
	public static String getGdbServerInterface() {

		String value = getString(GDB_SERVER_INTERFACE, null);
		if (value != null) {
			return value;
		}
		return DefaultPreferences.getGdbServerInterface();
	}

	public static void putGdbServerInterface(String value) {

		putWorkspaceString(GDB_SERVER_INTERFACE, value);
	}

	// ----- gdb server initial speed -----------------------------------------
	public static String getGdbServerInitialSpeed() {

		return getString(GDB_SERVER_INITIAL_SPEED, DefaultPreferences.SERVER_INITIAL_SPEED_DEFAULT);
	}

	public static void putGdbServerInitialSpeed(String value) {

		putWorkspaceString(GDB_SERVER_INITIAL_SPEED, value);
	}

	// ----- gdb server other options -----------------------------------------
	public static String getGdbServerOtherOptions() {

		return getString(GDB_SERVER_OTHER_OPTIONS, DefaultPreferences.SERVER_OTHER_OPTIONS_DEFAULT);
	}

	public static void putGdbServerOtherOptions(String value) {

		putWorkspaceString(GDB_SERVER_OTHER_OPTIONS, value);
	}

	// ----- gdb client executable --------------------------------------------
	public static String getGdbClientExecutable() {

		String value = getString(GDB_CLIENT_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return DefaultPreferences.getGdbClientExecutable();
	}

	public static void putGdbClientExecutable(String value) {

		putWorkspaceString(GDB_CLIENT_EXECUTABLE, value);
	}

	// ----- gdb client other options -----------------------------------------
	public static String getGdbClientOtherOptions() {

		return getString(GDB_CLIENT_OTHER_OPTIONS, DefaultPreferences.CLIENT_OTHER_OPTIONS_DEFAULT);
	}

	public static void putGdbClientOtherOptions(String value) {

		putWorkspaceString(GDB_CLIENT_OTHER_OPTIONS, value);
	}

	// ----- gdb client commands ----------------------------------------------
	public static String getGdbClientCommands() {

		return getString(GDB_CLIENT_COMMANDS, DefaultPreferences.CLIENT_COMMANDS_DEFAULT);
	}

	public static void putGdbClientCommands(String value) {

		putWorkspaceString(GDB_CLIENT_COMMANDS, value);
	}

	// ----- jlink do initial reset -------------------------------------------
	public static boolean getJLinkDoInitialReset() {

		return Boolean.valueOf(
				getString(GDB_JLINK_DO_INITIAL_RESET, Boolean.toString(DefaultPreferences.DO_INITIAL_RESET_DEFAULT)));
	}

	public static void putJLinkDoInitialReset(boolean value) {

		putWorkspaceString(GDB_JLINK_DO_INITIAL_RESET, Boolean.toString(value));
	}

	// ----- jlink initial reset type -----------------------------------------
	public static String getJLinkInitialResetType() {

		return getString(GDB_JLINK_INITIAL_RESET_TYPE, DefaultPreferences.INITIAL_RESET_TYPE_DEFAULT);
	}

	public static void putJLinkInitialResetType(String value) {

		putWorkspaceString(GDB_JLINK_INITIAL_RESET_TYPE, value);
	}

	// ----- jlink initial reset speed ----------------------------------------
	public static int getJLinkInitialResetSpeed() {

		return Integer.valueOf(getString(GDB_JLINK_INITIAL_RESET_SPEED,
				Integer.toString(DefaultPreferences.INITIAL_RESET_SPEED_DEFAULT)));
	}

	public static void putJLinkInitialResetSpeed(int value) {

		putWorkspaceString(GDB_JLINK_INITIAL_RESET_SPEED, Integer.toString(value));
	}

	// ----- jlink speed ------------------------------------------------------
	public static String getJLinkSpeed() {

		return getString(GDB_JLINK_SPEED, DefaultPreferences.JLINK_SPEED_DEFAULT);
	}

	public static void putJLinkSpeed(String value) {

		putWorkspaceString(GDB_JLINK_SPEED, value);
	}

	// ----- jlink enable flash breakpoints -----------------------------------
	public static boolean getJLinkEnableFlashBreakpoints() {

		return Boolean.valueOf(getString(GDB_JLINK_ENABLE_FLASH_BREAKPOINTS,
				Boolean.toString(DefaultPreferences.ENABLE_FLASH_BREAKPOINTS_DEFAULT)));
	}

	public static void putJLinkEnableFlashBreakpoints(boolean value) {

		putWorkspaceString(GDB_JLINK_ENABLE_FLASH_BREAKPOINTS, Boolean.toString(value));
	}

	// ----- jlink enable semihosting -----------------------------------------
	public static boolean getJLinkEnableSemihosting() {

		String value = getString(GDB_JLINK_ENABLE_SEMIHOSTING, null);
		if (value != null) {
			return Boolean.valueOf(value);
		}
		return DefaultPreferences.getJLinkEnableSemihosting();
	}

	public static void putJLinkEnableSemihosting(boolean value) {

		putWorkspaceString(GDB_JLINK_ENABLE_SEMIHOSTING, Boolean.toString(value));
	}

	// ----- jlink semihosting telnet -----------------------------------------
	public static boolean getJLinkSemihostingTelnet() {

		return Boolean.valueOf(getString(GDB_JLINK_SEMIHOSTING_TELNET,
				Boolean.toString(DefaultPreferences.ENABLE_SEMIHOSTING_DEFAULT)));
	}

	public static void putJLinkSemihostingTelnet(boolean value) {

		putWorkspaceString(GDB_JLINK_SEMIHOSTING_TELNET, Boolean.toString(value));
	}

	// ----- jlink semihosting client -----------------------------------------
	public static boolean getJLinkSemihostingClient() {

		return Boolean.valueOf(getString(GDB_JLINK_SEMIHOSTING_CLIENT,
				Boolean.toString(DefaultPreferences.SEMIHOSTING_CLIENT_DEFAULT)));
	}

	public static void putJLinkSemihostingClient(boolean value) {

		putWorkspaceString(GDB_JLINK_SEMIHOSTING_CLIENT, Boolean.toString(value));
	}

	// ----- jlink enable swo -------------------------------------------------
	public static boolean getJLinkEnableSwo() {

		String value = getString(GDB_JLINK_ENABLE_SWO, null);
		if (value != null) {
			return Boolean.valueOf(value);
		}
		return DefaultPreferences.getJLinkEnableSwo();
	}

	public static void putJLinkEnableSwo(boolean value) {

		putWorkspaceString(GDB_JLINK_ENABLE_SWO, Boolean.toString(value));
	}

	// ----- jlink swo cpu frequency ------------------------------------------
	public static int getJLinkSwoEnableTargetCpuFreq() {

		return Integer.valueOf(getString(GDB_JLINK_SWO_ENABLE_TARGET_CPU_FREQ,
				Integer.toString(DefaultPreferences.SWO_ENABLE_TARGET_CPU_FREQ_DEFAULT)));
	}

	public static void putJLinkSwoEnableTargetCpuFreq(int value) {

		putWorkspaceString(GDB_JLINK_SWO_ENABLE_TARGET_CPU_FREQ, Integer.toString(value));
	}

	// ----- jlink swo frequency ----------------------------------------------
	public static int getJLinkSwoEnableTargetSwoFreq() {

		return Integer.valueOf(getString(GDB_JLINK_SWO_ENABLE_TARGET_SWO_FREQ,
				Integer.toString(DefaultPreferences.SWO_ENABLE_TARGET_SWO_FREQ_DEFAULT)));
	}

	public static void putJLinkSwoEnableTargetSwoFreq(int value) {

		putWorkspaceString(GDB_JLINK_SWO_ENABLE_TARGET_SWO_FREQ, Integer.toString(value));
	}

	// ----- jlink swo mask ---------------------------------------------------
	public static String getJLinkSwoEnableTargetPortMask() {

		return getString(GDB_JLINK_SWO_ENABLE_TARGET_PORT_MASK, DefaultPreferences.SWO_ENABLE_TARGET_PORT_MASK_DEFAULT);
	}

	public static void putJLinkSwoEnableTargetPortMask(String value) {

		putWorkspaceString(GDB_JLINK_SWO_ENABLE_TARGET_PORT_MASK, value);
	}

	// ----- jlink init other -------------------------------------------------
	public static String getJLinkInitOther() {

		return getString(GDB_JLINK_INIT_OTHER, DefaultPreferences.INIT_OTHER_DEFAULT);
	}

	public static void putJLinkInitOther(String value) {

		putWorkspaceString(GDB_JLINK_INIT_OTHER, value);
	}

	// ----- jlink debug in ram -----------------------------------------------
	public static boolean getJLinkDebugInRam() {

		return Boolean.valueOf(
				getString(GDB_JLINK_DO_DEBUG_IN_RAM, Boolean.toString(DefaultPreferences.DO_DEBUG_IN_RAM_DEFAULT)));
	}

	public static void putJLinkDebugInRam(boolean value) {

		putWorkspaceString(GDB_JLINK_DO_DEBUG_IN_RAM, Boolean.toString(value));
	}

	// ----- jlink do prerun reset -----------------------------------
	public static boolean getJLinkDoPreRunReset() {

		return Boolean.valueOf(
				getString(GDB_JLINK_DO_PRERUN_RESET, Boolean.toString(DefaultPreferences.DO_PRERUN_RESET_DEFAULT)));
	}

	public static void putJLinkDoPreRunReset(boolean value) {

		putWorkspaceString(GDB_JLINK_DO_PRERUN_RESET, Boolean.toString(value));
	}

	// ----- jlink prerun reset type ------------------------------------------
	public static String getJLinkPreRunResetType() {

		return getString(GDB_JLINK_PRERUN_RESET_TYPE, DefaultPreferences.PRERUN_RESET_TYPE_DEFAULT);
	}

	public static void putJLinkPreRunResetType(String value) {

		putWorkspaceString(GDB_JLINK_PRERUN_RESET_TYPE, value);
	}

	// ----- jlink init other -------------------------------------------------
	public static String getJLinkPreRunOther() {

		return getString(GDB_JLINK_PRERUN_OTHER, DefaultPreferences.PRERUN_OTHER_DEFAULT);
	}

	public static void putJLinkPreRunOther(String value) {

		putWorkspaceString(GDB_JLINK_PRERUN_OTHER, value);
	}

	// ------------------------------------------------------------------------
}
