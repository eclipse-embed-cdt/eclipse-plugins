/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
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

package org.eclipse.embedcdt.debug.gdbjtag.openocd.preferences;

public class PersistentPreferences extends org.eclipse.embedcdt.debug.gdbjtag.preferences.PersistentPreferences {

	// ------------------------------------------------------------------------

	// Tab Debugger
	// GDB Server Setup
	public static final String GDB_SERVER = "gdb.server.";

	public static final String GDB_SERVER_DO_START = GDB_SERVER + "doStart";

	public static final String GDB_SERVER_EXECUTABLE = GDB_SERVER + "executable";

	public static final String GDB_SERVER_OTHER_OPTIONS = GDB_SERVER + "other";

	// GDB Client Setup
	public static final String GDB_CLIENT = "gdb.client.";

	public static final String GDB_CLIENT_DO_START = GDB_CLIENT + "doStart";

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

	// ------------------------------------------------------------------------

	private DefaultPreferences fDefaultPreferences;

	// ------------------------------------------------------------------------

	public PersistentPreferences(String pluginId) {
		super(pluginId);

		fDefaultPreferences = new DefaultPreferences(pluginId);
	}

	// ----- gdb server doStart -----------------------------------------------
	public boolean getGdbServerDoStart() {

		return Boolean.valueOf(
				getString(GDB_SERVER_DO_START, Boolean.toString(DefaultPreferences.DO_START_GDB_SERVER_DEFAULT)));
	}

	public void putGdbServerDoStart(boolean value) {

		putWorkspaceString(GDB_SERVER_DO_START, Boolean.toString(value));
	}

	// ----- gdb server executable --------------------------------------------
	public String getGdbServerExecutable() {

		String value = getString(GDB_SERVER_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return fDefaultPreferences.getGdbServerExecutable();
	}

	public void putGdbServerExecutable(String value) {

		putWorkspaceString(GDB_SERVER_EXECUTABLE, value);
	}

	// ----- gdb server other options -----------------------------------------
	public String getGdbServerOtherOptions() {

		String value = getString(GDB_SERVER_OTHER_OPTIONS, null);
		if (value != null) {
			return value;
		}
		return fDefaultPreferences.getOpenocdConfig();
	}

	public void putGdbServerOtherOptions(String value) {

		putWorkspaceString(GDB_SERVER_OTHER_OPTIONS, value);
	}

	// ----- gdb client doStart -----------------------------------------------
	public boolean getGdbClientDoStart() {

		return Boolean.valueOf(
				getString(GDB_CLIENT_DO_START, Boolean.toString(DefaultPreferences.DO_START_GDB_CLIENT_DEFAULT)));
	}

	public void putGdbClientDoStart(boolean value) {

		putWorkspaceString(GDB_CLIENT_DO_START, Boolean.toString(value));
	}

	// ----- gdb client executable --------------------------------------------
	public String getGdbClientExecutable() {

		String value = getString(GDB_CLIENT_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return fDefaultPreferences.getGdbClientExecutable();
	}

	public void putGdbClientExecutable(String value) {

		putWorkspaceString(GDB_CLIENT_EXECUTABLE, value);
	}

	// ----- gdb client other options -----------------------------------------
	public String getGdbClientOtherOptions() {

		return getString(GDB_CLIENT_OTHER_OPTIONS, DefaultPreferences.GDB_CLIENT_OTHER_OPTIONS_DEFAULT);
	}

	public void putGdbClientOtherOptions(String value) {

		putWorkspaceString(GDB_CLIENT_OTHER_OPTIONS, value);
	}

	// ----- gdb client commands ----------------------------------------------
	public String getGdbClientCommands() {

		return getString(GDB_CLIENT_COMMANDS, DefaultPreferences.GDB_CLIENT_OTHER_COMMANDS_DEFAULT);
	}

	public void putGdbClientCommands(String value) {

		putWorkspaceString(GDB_CLIENT_COMMANDS, value);
	}

	// ----- OpenOCD do initial reset -----------------------------------------
	public boolean getOpenOCDDoInitialReset() {

		return Boolean.valueOf(
				getString(GDB_OPENOCD_DO_INITIAL_RESET, Boolean.toString(DefaultPreferences.DO_FIRST_RESET_DEFAULT)));
	}

	public void putOpenOCDDoInitialReset(boolean value) {

		putWorkspaceString(GDB_OPENOCD_DO_INITIAL_RESET, Boolean.toString(value));
	}

	// ----- OpenOCD initial reset type ---------------------------------------
	public String getOpenOCDInitialResetType() {

		return getString(GDB_OPENOCD_INITIAL_RESET_TYPE, DefaultPreferences.FIRST_RESET_TYPE_DEFAULT);
	}

	public void putOpenOCDInitialResetType(String value) {

		putWorkspaceString(GDB_OPENOCD_INITIAL_RESET_TYPE, value);
	}

	// ----- OpenOCD enable semihosting ---------------------------------------
	public boolean getOpenOCDEnableSemihosting() {

		return Boolean.valueOf(getString(GDB_OPENOCD_ENABLE_SEMIHOSTING,
				Boolean.toString(DefaultPreferences.ENABLE_SEMIHOSTING_DEFAULT)));
	}

	public void putOpenOCDEnableSemihosting(boolean value) {

		putWorkspaceString(GDB_OPENOCD_ENABLE_SEMIHOSTING, Boolean.toString(value));
	}

	// ----- OpenOCD init other -----------------------------------------------
	public String getOpenOCDInitOther() {

		return getString(GDB_OPENOCD_INIT_OTHER, DefaultPreferences.OTHER_INIT_COMMANDS_DEFAULT);
	}

	public void putOpenOCDInitOther(String value) {

		putWorkspaceString(GDB_OPENOCD_INIT_OTHER, value);
	}

	// ----- OpenOCD debug in ram ---------------------------------------------
	public boolean getOpenOCDDebugInRam() {

		return Boolean.valueOf(
				getString(GDB_OPENOCD_DO_DEBUG_IN_RAM, Boolean.toString(DefaultPreferences.DO_DEBUG_IN_RAM_DEFAULT)));
	}

	public void putOpenOCDDebugInRam(boolean value) {

		putWorkspaceString(GDB_OPENOCD_DO_DEBUG_IN_RAM, Boolean.toString(value));
	}

	// ----- OpenOCD do prerun reset ------------------------------------------
	public boolean getOpenOCDDoPreRunReset() {

		return Boolean.valueOf(
				getString(GDB_OPENOCD_DO_PRERUN_RESET, Boolean.toString(DefaultPreferences.DO_SECOND_RESET_DEFAULT)));
	}

	public void putOpenOCDDoPreRunReset(boolean value) {

		putWorkspaceString(GDB_OPENOCD_DO_PRERUN_RESET, Boolean.toString(value));
	}

	// ----- OpenOCD prerun reset type ----------------------------------------
	public String getOpenOCDPreRunResetType() {

		return getString(GDB_OPENOCD_PRERUN_RESET_TYPE, DefaultPreferences.SECOND_RESET_TYPE_DEFAULT);
	}

	public void putOpenOCDPreRunResetType(String value) {

		putWorkspaceString(GDB_OPENOCD_PRERUN_RESET_TYPE, value);
	}

	// ----- OpenOCD init other -----------------------------------------------
	public String getOpenOCDPreRunOther() {

		return getString(GDB_OPENOCD_PRERUN_OTHER, DefaultPreferences.OTHER_RUN_COMMANDS_DEFAULT);
	}

	public void putOpenOCDPreRunOther(String value) {

		putWorkspaceString(GDB_OPENOCD_PRERUN_OTHER, value);
	}

	// ------------------------------------------------------------------------
}
