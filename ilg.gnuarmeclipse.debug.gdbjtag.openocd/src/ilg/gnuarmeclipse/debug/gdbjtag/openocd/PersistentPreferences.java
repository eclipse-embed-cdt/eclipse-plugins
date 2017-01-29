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

package ilg.gnuarmeclipse.debug.gdbjtag.openocd;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class PersistentPreferences {

	// ------------------------------------------------------------------------

	// Tab Debugger
	// GDB Server Setup
	public static final String GDB_SERVER = "gdb.server.";

	public static final String GDB_SERVER_DO_START = GDB_SERVER + "doStart";

	public static final String GDB_SERVER_EXECUTABLE = GDB_SERVER + "executable";

	public static final String GDB_SERVER_OTHER_OPTIONS = GDB_SERVER + "other";

	// GDB Client Setup
	public static final String GDB_CLIENT = "gdb.client.";

	public static final String GDB_CLIENT_EXECUTABLE = GDB_CLIENT + "executable";

	public static final String GDB_CLIENT_OTHER_OPTIONS = GDB_CLIENT + "other";

	public static final String GDB_CLIENT_COMMANDS = GDB_CLIENT + "commands";

	// Tab Startup
	// Initialisation Commands
	public static final String GDB_OPENOCD = "gdb.openocd.";

	public static final String GDB_OPENOCD_DO_INITIAL_RESET = GDB_OPENOCD + "doInitialReset";
	public static final String GDB_OPENOCD_INITIAL_RESET_TYPE = GDB_OPENOCD + "initialReset.type";
	public static final String GDB_OPENOCD_INIT_OTHER = GDB_OPENOCD + "init.other";

	public static final String GDB_OPENOCD_ENABLE_SEMIHOSTING = GDB_OPENOCD + "enableSemihosting";

	public static final String GDB_OPENOCD_DO_DEBUG_IN_RAM = GDB_OPENOCD + "doDebugInRam";

	// Run Commands
	public static final String GDB_OPENOCD_DO_PRERUN_RESET = GDB_OPENOCD + "doPreRunReset";
	public static final String GDB_OPENOCD_PRERUN_RESET_TYPE = GDB_OPENOCD + "preRunReset.type";

	public static final String GDB_OPENOCD_PRERUN_OTHER = GDB_OPENOCD + "preRun.other";

	// ----- Defaults ---------------------------------------------------------

	public static final String EXECUTABLE_NAME = "executable.name";
	public static final String EXECUTABLE_NAME_OS = EXECUTABLE_NAME + ".%s";
	public static final String INSTALL_FOLDER = "install.folder";
	public static final String SEARCH_PATH = "search.path";
	public static final String SEARCH_PATH_OS = SEARCH_PATH + ".%s";

	public static final String FOLDER_STRICT = "folder.strict";

	public static final String TAB_MAIN_CHECK_PROGRAM = "tab.main.checkProgram";
	public static final boolean TAB_MAIN_CHECK_PROGRAM_DEFAULT = false;

	// TODO: remove DEPRECATED
	public static final String OPENOCD_EXECUTABLE_DEPRECATED = "openocd_executable";
	public static final String OPENOCD_PATH_DEPRECATED = "openocd_path";

	// ----- Getters ----------------------------------------------------------

	private static String getString(String key, String defaultValue) {

		return Platform.getPreferencesService().getString(Activator.PLUGIN_ID, key, defaultValue, null);
	}

	// ----- Setters ----------------------------------------------------------

	private static void putWorkspaceString(String key, String value) {

		value = value.trim();

		// Access the instanceScope
		Preferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		preferences.put(key, value);
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

		return Boolean.valueOf(
				getString(GDB_SERVER_DO_START, Boolean.toString(DefaultPreferences.DO_START_GDB_SERVER_DEFAULT)));
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

	// ----- gdb server other options -----------------------------------------
	public static String getGdbServerOtherOptions() {

		String value = getString(GDB_SERVER_OTHER_OPTIONS, null);
		if (value != null) {
			return value;
		}
		return DefaultPreferences.getOpenocdConfig();
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

		return getString(GDB_CLIENT_OTHER_OPTIONS, DefaultPreferences.GDB_CLIENT_OTHER_OPTIONS_DEFAULT);
	}

	public static void putGdbClientOtherOptions(String value) {

		putWorkspaceString(GDB_CLIENT_OTHER_OPTIONS, value);
	}

	// ----- gdb client commands ----------------------------------------------
	public static String getGdbClientCommands() {

		return getString(GDB_CLIENT_COMMANDS, DefaultPreferences.GDB_CLIENT_OTHER_COMMANDS_DEFAULT);
	}

	public static void putGdbClientCommands(String value) {

		putWorkspaceString(GDB_CLIENT_COMMANDS, value);
	}

	// ----- OpenOCD do initial reset -----------------------------------------
	public static boolean getOpenOCDDoInitialReset() {

		return Boolean.valueOf(
				getString(GDB_OPENOCD_DO_INITIAL_RESET, Boolean.toString(DefaultPreferences.DO_FIRST_RESET_DEFAULT)));
	}

	public static void putOpenOCDDoInitialReset(boolean value) {

		putWorkspaceString(GDB_OPENOCD_DO_INITIAL_RESET, Boolean.toString(value));
	}

	// ----- OpenOCD initial reset type ---------------------------------------
	public static String getOpenOCDInitialResetType() {

		return getString(GDB_OPENOCD_INITIAL_RESET_TYPE, DefaultPreferences.FIRST_RESET_TYPE_DEFAULT);
	}

	public static void putOpenOCDInitialResetType(String value) {

		putWorkspaceString(GDB_OPENOCD_INITIAL_RESET_TYPE, value);
	}

	// ----- OpenOCD enable semihosting ---------------------------------------
	public static boolean getOpenOCDEnableSemihosting() {

		return Boolean.valueOf(getString(GDB_OPENOCD_ENABLE_SEMIHOSTING,
				Boolean.toString(DefaultPreferences.ENABLE_SEMIHOSTING_DEFAULT)));
	}

	public static void putOpenOCDEnableSemihosting(boolean value) {

		putWorkspaceString(GDB_OPENOCD_ENABLE_SEMIHOSTING, Boolean.toString(value));
	}

	// ----- OpenOCD init other -----------------------------------------------
	public static String getOpenOCDInitOther() {

		return getString(GDB_OPENOCD_INIT_OTHER, DefaultPreferences.OTHER_INIT_COMMANDS_DEFAULT);
	}

	public static void putOpenOCDInitOther(String value) {

		putWorkspaceString(GDB_OPENOCD_INIT_OTHER, value);
	}

	// ----- OpenOCD debug in ram ---------------------------------------------
	public static boolean getOpenOCDDebugInRam() {

		return Boolean.valueOf(
				getString(GDB_OPENOCD_DO_DEBUG_IN_RAM, Boolean.toString(DefaultPreferences.DO_DEBUG_IN_RAM_DEFAULT)));
	}

	public static void putOpenOCDDebugInRam(boolean value) {

		putWorkspaceString(GDB_OPENOCD_DO_DEBUG_IN_RAM, Boolean.toString(value));
	}

	// ----- OpenOCD do prerun reset ------------------------------------------
	public static boolean getOpenOCDDoPreRunReset() {

		return Boolean.valueOf(
				getString(GDB_OPENOCD_DO_PRERUN_RESET, Boolean.toString(DefaultPreferences.DO_SECOND_RESET_DEFAULT)));
	}

	public static void putOpenOCDDoPreRunReset(boolean value) {

		putWorkspaceString(GDB_OPENOCD_DO_PRERUN_RESET, Boolean.toString(value));
	}

	// ----- OpenOCD prerun reset type ----------------------------------------
	public static String getOpenOCDPreRunResetType() {

		return getString(GDB_OPENOCD_PRERUN_RESET_TYPE, DefaultPreferences.SECOND_RESET_TYPE_DEFAULT);
	}

	public static void putOpenOCDPreRunResetType(String value) {

		putWorkspaceString(GDB_OPENOCD_PRERUN_RESET_TYPE, value);
	}

	// ----- OpenOCD init other -----------------------------------------------
	public static String getOpenOCDPreRunOther() {

		return getString(GDB_OPENOCD_PRERUN_OTHER, DefaultPreferences.OTHER_RUN_COMMANDS_DEFAULT);
	}

	public static void putOpenOCDPreRunOther(String value) {

		putWorkspaceString(GDB_OPENOCD_PRERUN_OTHER, value);
	}

	// ------------------------------------------------------------------------
}
