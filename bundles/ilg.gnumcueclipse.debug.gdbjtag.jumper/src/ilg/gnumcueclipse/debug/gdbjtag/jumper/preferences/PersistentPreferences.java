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

package ilg.gnumcueclipse.debug.gdbjtag.jumper.preferences;

public class PersistentPreferences extends ilg.gnumcueclipse.debug.gdbjtag.preferences.PersistentPreferences {

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
	public static final String GDB_QEMU = "gdb.jumper.";

	public static final String GDB_QEMU_DO_INITIAL_RESET = GDB_QEMU + "doInitialReset";

	public static final String GDB_QEMU_INIT_OTHER = GDB_QEMU + "init.other";

	public static final String GDB_QEMU_ENABLE_SEMIHOSTING = GDB_QEMU + "enableSemihosting";

	public static final String GDB_QEMU_BOARD_NAME = GDB_QEMU + "boardName";

	public static final String GDB_QEMU_MACHINE_NAME = GDB_QEMU + "machineName";

	public static final String GDB_QEMU_DEVICE_NAME = GDB_QEMU + "deviceName";

	private static final String GDB_QEMU_IS_VERBOSE = GDB_QEMU + "isVerbose";

	public static final String GDB_QEMU_DISABLE_GRAPHICS = GDB_QEMU + "disableGraphics";

	// Run Commands
	public static final String GDB_QEMU_DO_DEBUG_IN_RAM = GDB_QEMU + "doDebugInRam";

	public static final String GDB_QEMU_DO_PRERUN_RESET = GDB_QEMU + "doPreRunReset";

	public static final String GDB_QEMU_PRERUN_OTHER = GDB_QEMU + "preRun.other";

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

	// ----- QEMU do initial reset -----------------------------------------
	public boolean getQemuDoInitialReset() {

		return Boolean.valueOf(
				getString(GDB_QEMU_DO_INITIAL_RESET, Boolean.toString(DefaultPreferences.DO_INITIAL_RESET_DEFAULT)));
	}

	public void putQemuDoInitialReset(boolean value) {

		putWorkspaceString(GDB_QEMU_DO_INITIAL_RESET, Boolean.toString(value));
	}

	// ----- QEMU enable semihosting ---------------------------------------
	public boolean getQemuEnableSemihosting() {

		String value = getString(GDB_QEMU_ENABLE_SEMIHOSTING, null);
		if (value != null) {
			return Boolean.valueOf(value);
		}
		return fDefaultPreferences.getQemuEnableSemihosting();

	}

	public void putQemuEnableSemihosting(boolean value) {

		putWorkspaceString(GDB_QEMU_ENABLE_SEMIHOSTING, Boolean.toString(value));
	}

	// ----- QEMU init other -----------------------------------------------
	public String getQemuInitOther() {

		return getString(GDB_QEMU_INIT_OTHER, DefaultPreferences.INIT_OTHER_DEFAULT);
	}

	public void putQemuInitOther(String value) {

		putWorkspaceString(GDB_QEMU_INIT_OTHER, value);
	}

	// ----- QEMU board name ------------------------------------------------
	public String getQemuBoardName() {

		String value = getString(GDB_QEMU_BOARD_NAME, null);
		if (value != null) {
			return value;
		}
		return getString(GDB_QEMU_MACHINE_NAME, DefaultPreferences.QEMU_BOARD_NAME_DEFAULT);
	}

	public void putQemuBoardName(String value) {

		putWorkspaceString(GDB_QEMU_BOARD_NAME, value);
	}

	// ----- QEMU device name ------------------------------------------------
	public String getQemuDeviceName() {

		return getString(GDB_QEMU_DEVICE_NAME, DefaultPreferences.QEMU_DEVICE_NAME_DEFAULT);
	}

	public void putQemuDeviceName(String value) {

		putWorkspaceString(GDB_QEMU_DEVICE_NAME, value);
	}

	// ----- QEMU is verbose ---------------------------------------------
	public Boolean getQemuIsVerbose() {
		return getBoolean(GDB_QEMU_IS_VERBOSE, DefaultPreferences.QEMU_IS_VERBOSE_DEFAULT);
	}

	public void putQemuIsVerbose(boolean value) {

		putWorkspaceString(GDB_QEMU_IS_VERBOSE, Boolean.toString(value));
	}

	// ----- QEMU debug in ram ---------------------------------------------
	public boolean getQemuDebugInRam() {

		return Boolean.valueOf(
				getString(GDB_QEMU_DO_DEBUG_IN_RAM, Boolean.toString(DefaultPreferences.DO_DEBUG_IN_RAM_DEFAULT)));
	}

	public void putQemuDebugInRam(boolean value) {

		putWorkspaceString(GDB_QEMU_DO_DEBUG_IN_RAM, Boolean.toString(value));
	}

	// ----- QEMU do prerun reset ------------------------------------------
	public boolean getQemuDoPreRunReset() {

		return Boolean.valueOf(
				getString(GDB_QEMU_DO_PRERUN_RESET, Boolean.toString(DefaultPreferences.DO_PRERUN_RESET_DEFAULT)));
	}

	public void putQemuDoPreRunReset(boolean value) {

		putWorkspaceString(GDB_QEMU_DO_PRERUN_RESET, Boolean.toString(value));
	}

	// ----- QEMU init other --------------------------------------------------
	public String getQemuPreRunOther() {

		return getString(GDB_QEMU_PRERUN_OTHER, DefaultPreferences.PRERUN_OTHER_DEFAULT);
	}

	public void putQemuPreRunOther(String value) {

		putWorkspaceString(GDB_QEMU_PRERUN_OTHER, value);
	}

	// ----- QEMU debug in ram ---------------------------------------------
	public boolean getQemuDisableGraphics() {

		return Boolean.valueOf(
				getString(GDB_QEMU_DISABLE_GRAPHICS, Boolean.toString(DefaultPreferences.DISABLE_GRAPHICS_DEFAULT)));
	}

	public void putQemuDisableGraphics(boolean value) {

		putWorkspaceString(GDB_QEMU_DO_DEBUG_IN_RAM, Boolean.toString(value));
	}

	// ------------------------------------------------------------------------
}
