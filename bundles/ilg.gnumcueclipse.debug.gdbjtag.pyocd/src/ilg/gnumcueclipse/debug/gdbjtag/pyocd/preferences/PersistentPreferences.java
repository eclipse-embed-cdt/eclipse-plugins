/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * Copyright (c) 2015-2016 Chris Reed.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Liviu Ionescu - initial version
 *     Chris Reed - pyOCD changes
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.pyocd.preferences;

import ilg.gnumcueclipse.debug.gdbjtag.pyocd.Activator;

public class PersistentPreferences extends ilg.gnumcueclipse.debug.gdbjtag.preferences.PersistentPreferences {

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
	public static final String GDB_PYOCD = "gdb.pyocd.";

	public static final String GDB_PYOCD_DO_INITIAL_RESET = GDB_PYOCD + "doInitialReset";
	public static final String GDB_PYOCD_INITIAL_RESET_TYPE = GDB_PYOCD + "initialReset.type";
	public static final String GDB_PYOCD_INIT_OTHER = GDB_PYOCD + "init.other";

	public static final String GDB_PYOCD_ENABLE_SEMIHOSTING = GDB_PYOCD + "enableSemihosting";

	public static final String GDB_PYOCD_DO_DEBUG_IN_RAM = GDB_PYOCD + "doDebugInRam";

	// Run Commands
	public static final String GDB_PYOCD_DO_PRERUN_RESET = GDB_PYOCD + "doPreRunReset";
	public static final String GDB_PYOCD_PRERUN_RESET_TYPE = GDB_PYOCD + "preRunReset.type";

	public static final String GDB_PYOCD_PRERUN_OTHER = GDB_PYOCD + "preRun.other";

	// ------------------------------------------------------------------------

	private DefaultPreferences fDefaultPreferences;

	// ------------------------------------------------------------------------

	public PersistentPreferences(String pluginId) {
		super(pluginId);

		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
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
		return fDefaultPreferences.getPyocdConfig();
	}

	public void putGdbServerOtherOptions(String value) {

		putWorkspaceString(GDB_SERVER_OTHER_OPTIONS, value);
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

	// ----- pyOCD do initial reset -----------------------------------------
	public boolean getPyOCDDoInitialReset() {

		return Boolean.valueOf(
				getString(GDB_PYOCD_DO_INITIAL_RESET, Boolean.toString(DefaultPreferences.DO_FIRST_RESET_DEFAULT)));
	}

	public void putPyOCDDoInitialReset(boolean value) {

		putWorkspaceString(GDB_PYOCD_DO_INITIAL_RESET, Boolean.toString(value));
	}

	// ----- pyOCD initial reset type ---------------------------------------
	public String getPyOCDInitialResetType() {

		return getString(GDB_PYOCD_INITIAL_RESET_TYPE, DefaultPreferences.FIRST_RESET_TYPE_DEFAULT);
	}

	public void putPyOCDInitialResetType(String value) {

		putWorkspaceString(GDB_PYOCD_INITIAL_RESET_TYPE, value);
	}

	// ----- pyOCD enable semihosting ---------------------------------------
	public boolean getPyOCDEnableSemihosting() {

		return Boolean.valueOf(getString(GDB_PYOCD_ENABLE_SEMIHOSTING,
				Boolean.toString(DefaultPreferences.ENABLE_SEMIHOSTING_DEFAULT)));
	}

	public void putPyOCDEnableSemihosting(boolean value) {

		putWorkspaceString(GDB_PYOCD_ENABLE_SEMIHOSTING, Boolean.toString(value));
	}

	// ----- pyOCD init other -----------------------------------------------
	public String getPyOCDInitOther() {

		return getString(GDB_PYOCD_INIT_OTHER, DefaultPreferences.OTHER_INIT_COMMANDS_DEFAULT);
	}

	public void putPyOCDInitOther(String value) {

		putWorkspaceString(GDB_PYOCD_INIT_OTHER, value);
	}

	// ----- pyOCD debug in ram ---------------------------------------------
	public boolean getPyOCDDebugInRam() {

		return Boolean.valueOf(
				getString(GDB_PYOCD_DO_DEBUG_IN_RAM, Boolean.toString(DefaultPreferences.DO_DEBUG_IN_RAM_DEFAULT)));
	}

	public void putPyOCDDebugInRam(boolean value) {

		putWorkspaceString(GDB_PYOCD_DO_DEBUG_IN_RAM, Boolean.toString(value));
	}

	// ----- pyOCD do prerun reset ------------------------------------------
	public boolean getPyOCDDoPreRunReset() {

		return Boolean.valueOf(
				getString(GDB_PYOCD_DO_PRERUN_RESET, Boolean.toString(DefaultPreferences.DO_SECOND_RESET_DEFAULT)));
	}

	public void putPyOCDDoPreRunReset(boolean value) {

		putWorkspaceString(GDB_PYOCD_DO_PRERUN_RESET, Boolean.toString(value));
	}

	// ----- pyOCD prerun reset type ----------------------------------------
	public String getPyOCDPreRunResetType() {

		return getString(GDB_PYOCD_PRERUN_RESET_TYPE, DefaultPreferences.SECOND_RESET_TYPE_DEFAULT);
	}

	public void putPyOCDPreRunResetType(String value) {

		putWorkspaceString(GDB_PYOCD_PRERUN_RESET_TYPE, value);
	}

	// ----- pyOCD init other -----------------------------------------------
	public String getPyOCDPreRunOther() {

		return getString(GDB_PYOCD_PRERUN_OTHER, DefaultPreferences.OTHER_RUN_COMMANDS_DEFAULT);
	}

	public void putPyOCDPreRunOther(String value) {

		putWorkspaceString(GDB_PYOCD_PRERUN_OTHER, value);
	}

	// ------------------------------------------------------------------------
}
