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
 *     Jonathan Seroussi - Jumper Virtual Lab adjustments
*******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.jumper.preferences;

public class PersistentPreferences extends org.eclipse.embedcdt.debug.gdbjtag.core.preferences.PersistentPreferences {

	// ------------------------------------------------------------------------

	// Tab Debugger
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
	public static final String GDB_JUMPER = "gdb.jumper.";

	public static final String GDB_JUMPER_DO_INITIAL_RESET = GDB_JUMPER + "doInitialReset";

	public static final String GDB_JUMPER_INIT_OTHER = GDB_JUMPER + "init.other";

	public static final String GDB_JUMPER_ENABLE_SEMIHOSTING = GDB_JUMPER + "enableSemihosting";

	public static final String GDB_JUMPER_BOARD_NAME = GDB_JUMPER + "boardName";

	public static final String GDB_JUMPER_MACHINE_NAME = GDB_JUMPER + "machineName";

	public static final String GDB_JUMPER_DEVICE_NAME = GDB_JUMPER + "deviceName";

	private static final String GDB_JUMPER_IS_VERBOSE = GDB_JUMPER + "isVerbose";

	public static final String GDB_JUMPER_DISABLE_GRAPHICS = GDB_JUMPER + "disableGraphics";

	// Run Commands
	public static final String GDB_JUMPER_DO_DEBUG_IN_RAM = GDB_JUMPER + "doDebugInRam";

	public static final String GDB_JUMPER_DO_PRERUN_RESET = GDB_JUMPER + "doPreRunReset";

	public static final String GDB_JUMPER_PRERUN_OTHER = GDB_JUMPER + "preRun.other";

	// ------------------------------------------------------------------------

	private DefaultPreferences fDefaultPreferences;

	// ------------------------------------------------------------------------

	public PersistentPreferences(String pluginId) {
		super(pluginId);

		fDefaultPreferences = new DefaultPreferences(pluginId);
	}

	// ----- gdb server doStart -----------------------------------------------
	public boolean getGdbServerDoStart() {

		return Boolean
				.valueOf(getString(GDB_SERVER_DO_START, Boolean.toString(DefaultPreferences.SERVER_DO_START_DEFAULT)));
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

		return getString(GDB_SERVER_OTHER_OPTIONS, DefaultPreferences.SERVER_OTHER_OPTIONS_DEFAULT);
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

		return getString(GDB_CLIENT_OTHER_OPTIONS, DefaultPreferences.CLIENT_OTHER_OPTIONS_DEFAULT);
	}

	public void putGdbClientOtherOptions(String value) {

		putWorkspaceString(GDB_CLIENT_OTHER_OPTIONS, value);
	}

	// ----- gdb client commands ----------------------------------------------
	public String getGdbClientCommands() {

		return getString(GDB_CLIENT_COMMANDS, DefaultPreferences.CLIENT_COMMANDS_DEFAULT);
	}

	public void putGdbClientCommands(String value) {

		putWorkspaceString(GDB_CLIENT_COMMANDS, value);
	}

	// ----- Jumper do initial reset -----------------------------------------
	public boolean getJumperDoInitialReset() {

		return false;
	}

	public void putJumperDoInitialReset(boolean value) {

		putWorkspaceString(GDB_JUMPER_DO_INITIAL_RESET, Boolean.toString(value));
	}

	// ----- Jumper do initial reset -----------------------------------------
	// ----- Jumper enable semihosting ---------------------------------------
	public boolean getJumperEnableSemihosting() {

		String value = getString(GDB_JUMPER_ENABLE_SEMIHOSTING, null);
		if (value != null) {
			return Boolean.valueOf(value);
		}
		return fDefaultPreferences.getJumperEnableSemihosting();

	}

	public void putJumperEnableSemihosting(boolean value) {

		putWorkspaceString(GDB_JUMPER_ENABLE_SEMIHOSTING, Boolean.toString(value));
	}

	// ----- Jumper init other -----------------------------------------------
	public String getJumperInitOther() {

		return getString(GDB_JUMPER_INIT_OTHER, DefaultPreferences.INIT_OTHER_DEFAULT);
	}

	public void putJumperInitOther(String value) {

		putWorkspaceString(GDB_JUMPER_INIT_OTHER, value);
	}

	// ----- Jumper board name ------------------------------------------------
	public String getJumperBoardName() {

		String value = getString(GDB_JUMPER_BOARD_NAME, null);
		if (value != null) {
			return value;
		}
		return getString(GDB_JUMPER_MACHINE_NAME, DefaultPreferences.JUMPER_BOARD_NAME_DEFAULT);
	}

	public void putJumperBoardName(String value) {

		putWorkspaceString(GDB_JUMPER_BOARD_NAME, value);
	}

	// ----- Jumper device name ------------------------------------------------
	public String getJumperDeviceName() {

		return getString(GDB_JUMPER_DEVICE_NAME, DefaultPreferences.JUMPER_DEVICE_NAME_DEFAULT);
	}

	public void putJumperDeviceName(String value) {

		putWorkspaceString(GDB_JUMPER_DEVICE_NAME, value);
	}

	// ----- Jumper is verbose ---------------------------------------------
	public Boolean getJumperIsVerbose() {
		return getBoolean(GDB_JUMPER_IS_VERBOSE, DefaultPreferences.JUMPER_IS_VERBOSE_DEFAULT);
	}

	public void putJumperIsVerbose(boolean value) {

		putWorkspaceString(GDB_JUMPER_IS_VERBOSE, Boolean.toString(value));
	}

	// ----- Jumper debug in ram ---------------------------------------------
	public boolean getJumperDebugInRam() {

		return Boolean.valueOf(
				getString(GDB_JUMPER_DO_DEBUG_IN_RAM, Boolean.toString(DefaultPreferences.DO_DEBUG_IN_RAM_DEFAULT)));
	}

	public void putJumperDebugInRam(boolean value) {

		putWorkspaceString(GDB_JUMPER_DO_DEBUG_IN_RAM, Boolean.toString(value));
	}

	// ----- Jumper do prerun reset ------------------------------------------
	public boolean getJumperDoPreRunReset() {

		return Boolean.valueOf(
				getString(GDB_JUMPER_DO_PRERUN_RESET, Boolean.toString(DefaultPreferences.DO_PRERUN_RESET_DEFAULT)));
	}

	public void putJumperDoPreRunReset(boolean value) {

		putWorkspaceString(GDB_JUMPER_DO_PRERUN_RESET, Boolean.toString(value));
	}

	// ----- Jumper init other --------------------------------------------------
	public String getJumperPreRunOther() {

		return getString(GDB_JUMPER_PRERUN_OTHER, DefaultPreferences.PRERUN_OTHER_DEFAULT);
	}

	public void putJumperPreRunOther(String value) {

		putWorkspaceString(GDB_JUMPER_PRERUN_OTHER, value);
	}

	// ------------------------------------------------------------------------
}
