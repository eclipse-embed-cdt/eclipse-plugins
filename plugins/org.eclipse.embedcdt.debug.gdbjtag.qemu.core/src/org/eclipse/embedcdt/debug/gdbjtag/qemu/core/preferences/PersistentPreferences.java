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

package org.eclipse.embedcdt.debug.gdbjtag.qemu.core.preferences;

public class PersistentPreferences extends org.eclipse.embedcdt.debug.gdbjtag.core.preferences.PersistentPreferences {

	// ------------------------------------------------------------------------

	public static final String architectures[] = { "arm", "aarch64", "riscv32", "riscv64" };

	// Legacy gnuarmeclipse uses these names directly; the new ones are prefixed.

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
	public static final String GDB_QEMU = "gdb.qemu.";

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
	@Deprecated
	public boolean getGdbServerDoStart() {
		return Boolean
				.valueOf(getString(GDB_SERVER_DO_START, Boolean.toString(DefaultPreferences.SERVER_DO_START_DEFAULT)));
	}

	public boolean getGdbServerDoStart(String prefix) {
		return Boolean.valueOf(
				getString(prefix + GDB_SERVER_DO_START, Boolean.toString(DefaultPreferences.SERVER_DO_START_DEFAULT)));
	}

	@Deprecated
	public void putGdbServerDoStart(boolean value) {
		putWorkspaceString(GDB_SERVER_DO_START, Boolean.toString(value));
	}

	public void putGdbServerDoStart(String prefix, boolean value) {
		putWorkspaceString(prefix + GDB_SERVER_DO_START, Boolean.toString(value));
	}

	// ----- gdb server executable --------------------------------------------
	@Deprecated
	public String getGdbServerExecutable() {

		String value = getString(GDB_SERVER_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return fDefaultPreferences.getGdbServerExecutable();
	}

	public String getGdbServerExecutable(String prefix, String architecture) {

		String value = getString(prefix + GDB_SERVER_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return fDefaultPreferences.getGdbServerExecutable(prefix, architecture);
	}

	@Deprecated
	public void putGdbServerExecutable(String value) {
		putWorkspaceString(GDB_SERVER_EXECUTABLE, value);
	}

	public void putGdbServerExecutable(String prefix, String value) {
		putWorkspaceString(prefix + GDB_SERVER_EXECUTABLE, value);
	}

	// ----- gdb server other options -----------------------------------------
	@Deprecated
	public String getGdbServerOtherOptions() {
		return getString(GDB_SERVER_OTHER_OPTIONS, DefaultPreferences.SERVER_OTHER_OPTIONS_DEFAULT);
	}

	public String getGdbServerOtherOptions(String prefix) {
		return getString(prefix + GDB_SERVER_OTHER_OPTIONS, DefaultPreferences.SERVER_OTHER_OPTIONS_DEFAULT);
	}

	@Deprecated
	public void putGdbServerOtherOptions(String value) {
		putWorkspaceString(GDB_SERVER_OTHER_OPTIONS, value);
	}

	public void putGdbServerOtherOptions(String prefix, String value) {
		putWorkspaceString(prefix + GDB_SERVER_OTHER_OPTIONS, value);
	}

	// ----- gdb client executable --------------------------------------------
	@Deprecated
	public String getGdbClientExecutable() {

		String value = getString(GDB_CLIENT_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return fDefaultPreferences.getGdbClientExecutable();
	}

	public String getGdbClientExecutable(String prefix) {

		String value = getString(prefix + GDB_CLIENT_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return fDefaultPreferences.getGdbClientExecutable(prefix);
	}

	@Deprecated
	public void putGdbClientExecutable(String value) {
		putWorkspaceString(GDB_CLIENT_EXECUTABLE, value);
	}

	public void putGdbClientExecutable(String prefix, String value) {
		putWorkspaceString(prefix + GDB_CLIENT_EXECUTABLE, value);
	}

	// ----- gdb client other options -----------------------------------------
	@Deprecated
	public String getGdbClientOtherOptions() {
		return getString(GDB_CLIENT_OTHER_OPTIONS, DefaultPreferences.CLIENT_OTHER_OPTIONS_DEFAULT);
	}

	public String getGdbClientOtherOptions(String prefix) {
		return getString(prefix + GDB_CLIENT_OTHER_OPTIONS, DefaultPreferences.CLIENT_OTHER_OPTIONS_DEFAULT);
	}

	@Deprecated
	public void putGdbClientOtherOptions(String value) {
		putWorkspaceString(GDB_CLIENT_OTHER_OPTIONS, value);
	}

	public void putGdbClientOtherOptions(String prefix, String value) {
		putWorkspaceString(prefix + GDB_CLIENT_OTHER_OPTIONS, value);
	}

	// ----- gdb client commands ----------------------------------------------
	@Deprecated
	public String getGdbClientCommands() {
		return getString(GDB_CLIENT_COMMANDS, DefaultPreferences.CLIENT_COMMANDS_DEFAULT);
	}

	public String getGdbClientCommands(String prefix) {
		return getString(prefix + GDB_CLIENT_COMMANDS, DefaultPreferences.CLIENT_COMMANDS_DEFAULT);
	}

	@Deprecated
	public void putGdbClientCommands(String value) {
		putWorkspaceString(GDB_CLIENT_COMMANDS, value);
	}

	public void putGdbClientCommands(String prefix, String value) {
		putWorkspaceString(prefix + GDB_CLIENT_COMMANDS, value);
	}

	// ----- QEMU do initial reset -----------------------------------------
	@Deprecated
	public boolean getQemuDoInitialReset() {
		return Boolean.valueOf(
				getString(GDB_QEMU_DO_INITIAL_RESET, Boolean.toString(DefaultPreferences.DO_INITIAL_RESET_DEFAULT)));
	}

	public boolean getQemuDoInitialReset(String prefix) {
		return Boolean.valueOf(getString(prefix + GDB_QEMU_DO_INITIAL_RESET,
				Boolean.toString(DefaultPreferences.DO_INITIAL_RESET_DEFAULT)));
	}

	@Deprecated
	public void putQemuDoInitialReset(boolean value) {
		putWorkspaceString(GDB_QEMU_DO_INITIAL_RESET, Boolean.toString(value));
	}

	public void putQemuDoInitialReset(String prefix, boolean value) {
		putWorkspaceString(prefix + GDB_QEMU_DO_INITIAL_RESET, Boolean.toString(value));
	}

	// ----- QEMU enable semihosting ---------------------------------------
	@Deprecated
	public boolean getQemuEnableSemihosting() {

		String value = getString(GDB_QEMU_ENABLE_SEMIHOSTING, null);
		if (value != null) {
			return Boolean.valueOf(value);
		}
		return fDefaultPreferences.getQemuEnableSemihosting();

	}

	public boolean getQemuEnableSemihosting(String prefix) {

		String value = getString(prefix + GDB_QEMU_ENABLE_SEMIHOSTING, null);
		if (value != null) {
			return Boolean.valueOf(value);
		}
		return fDefaultPreferences.getQemuEnableSemihosting(prefix);

	}

	@Deprecated
	public void putQemuEnableSemihosting(boolean value) {
		putWorkspaceString(GDB_QEMU_ENABLE_SEMIHOSTING, Boolean.toString(value));
	}

	public void putQemuEnableSemihosting(String prefix, boolean value) {
		putWorkspaceString(prefix + GDB_QEMU_ENABLE_SEMIHOSTING, Boolean.toString(value));
	}

	// ----- QEMU init other -----------------------------------------------
	@Deprecated
	public String getQemuInitOther() {
		return getString(GDB_QEMU_INIT_OTHER, DefaultPreferences.INIT_OTHER_DEFAULT);
	}

	public String getQemuInitOther(String prefix) {
		return getString(prefix + GDB_QEMU_INIT_OTHER, DefaultPreferences.INIT_OTHER_DEFAULT);
	}

	@Deprecated
	public void putQemuInitOther(String value) {
		putWorkspaceString(GDB_QEMU_INIT_OTHER, value);
	}

	public void putQemuInitOther(String prefix, String value) {
		putWorkspaceString(prefix + GDB_QEMU_INIT_OTHER, value);
	}

	// ----- QEMU board name ------------------------------------------------
	@Deprecated
	public String getQemuBoardName() {

		String value = getString(GDB_QEMU_BOARD_NAME, null);
		if (value != null) {
			return value;
		}
		return getString(GDB_QEMU_MACHINE_NAME, DefaultPreferences.QEMU_BOARD_NAME_DEFAULT);
	}

	public String getQemuBoardName(String prefix) {

		String value = getString(prefix + GDB_QEMU_BOARD_NAME, null);
		if (value != null) {
			return value;
		}
		return getString(prefix + GDB_QEMU_MACHINE_NAME, DefaultPreferences.QEMU_BOARD_NAME_DEFAULT);
	}

	@Deprecated
	public void putQemuBoardName(String value) {
		putWorkspaceString(GDB_QEMU_BOARD_NAME, value);
	}

	public void putQemuBoardName(String prefix, String value) {
		putWorkspaceString(prefix + GDB_QEMU_BOARD_NAME, value);
	}

	// ----- QEMU device name ------------------------------------------------
	@Deprecated
	public String getQemuDeviceName() {
		return getString(GDB_QEMU_DEVICE_NAME, DefaultPreferences.QEMU_DEVICE_NAME_DEFAULT);
	}

	public String getQemuDeviceName(String prefix) {
		return getString(prefix + GDB_QEMU_DEVICE_NAME, DefaultPreferences.QEMU_DEVICE_NAME_DEFAULT);
	}

	@Deprecated
	public void putQemuDeviceName(String value) {
		putWorkspaceString(GDB_QEMU_DEVICE_NAME, value);
	}

	public void putQemuDeviceName(String prefix, String value) {
		putWorkspaceString(prefix + GDB_QEMU_DEVICE_NAME, value);
	}

	// ----- QEMU is verbose ---------------------------------------------
	@Deprecated
	public Boolean getQemuIsVerbose() {
		return getBoolean(GDB_QEMU_IS_VERBOSE, DefaultPreferences.QEMU_IS_VERBOSE_DEFAULT);
	}

	public Boolean getQemuIsVerbose(String prefix) {
		return getBoolean(prefix + GDB_QEMU_IS_VERBOSE, DefaultPreferences.QEMU_IS_VERBOSE_DEFAULT);
	}

	@Deprecated
	public void putQemuIsVerbose(boolean value) {
		putWorkspaceString(GDB_QEMU_IS_VERBOSE, Boolean.toString(value));
	}

	public void putQemuIsVerbose(String prefix, boolean value) {
		putWorkspaceString(prefix + GDB_QEMU_IS_VERBOSE, Boolean.toString(value));
	}

	// ----- QEMU debug in ram ---------------------------------------------
	@Deprecated
	public boolean getQemuDebugInRam() {
		return Boolean.valueOf(
				getString(GDB_QEMU_DO_DEBUG_IN_RAM, Boolean.toString(DefaultPreferences.DO_DEBUG_IN_RAM_DEFAULT)));
	}

	public boolean getQemuDebugInRam(String prefix) {
		return Boolean.valueOf(getString(prefix + GDB_QEMU_DO_DEBUG_IN_RAM,
				Boolean.toString(DefaultPreferences.DO_DEBUG_IN_RAM_DEFAULT)));
	}

	@Deprecated
	public void putQemuDebugInRam(boolean value) {
		putWorkspaceString(GDB_QEMU_DO_DEBUG_IN_RAM, Boolean.toString(value));
	}

	public void putQemuDebugInRam(String prefix, boolean value) {
		putWorkspaceString(prefix + GDB_QEMU_DO_DEBUG_IN_RAM, Boolean.toString(value));
	}

	// ----- QEMU do prerun reset ------------------------------------------
	@Deprecated
	public boolean getQemuDoPreRunReset() {
		return Boolean.valueOf(
				getString(GDB_QEMU_DO_PRERUN_RESET, Boolean.toString(DefaultPreferences.DO_PRERUN_RESET_DEFAULT)));
	}

	public boolean getQemuDoPreRunReset(String prefix) {
		return Boolean.valueOf(getString(prefix + GDB_QEMU_DO_PRERUN_RESET,
				Boolean.toString(DefaultPreferences.DO_PRERUN_RESET_DEFAULT)));
	}

	@Deprecated
	public void putQemuDoPreRunReset(boolean value) {
		putWorkspaceString(GDB_QEMU_DO_PRERUN_RESET, Boolean.toString(value));
	}

	public void putQemuDoPreRunReset(String prefix, boolean value) {
		putWorkspaceString(prefix + GDB_QEMU_DO_PRERUN_RESET, Boolean.toString(value));
	}

	// ----- QEMU init other --------------------------------------------------
	@Deprecated
	public String getQemuPreRunOther() {
		return getString(GDB_QEMU_PRERUN_OTHER, DefaultPreferences.PRERUN_OTHER_DEFAULT);
	}

	public String getQemuPreRunOther(String prefix) {
		return getString(prefix + GDB_QEMU_PRERUN_OTHER, DefaultPreferences.PRERUN_OTHER_DEFAULT);
	}

	@Deprecated
	public void putQemuPreRunOther(String value) {
		putWorkspaceString(GDB_QEMU_PRERUN_OTHER, value);
	}

	public void putQemuPreRunOther(String prefix, String value) {
		putWorkspaceString(prefix + GDB_QEMU_PRERUN_OTHER, value);
	}

	// ----- QEMU debug in ram ---------------------------------------------
	@Deprecated
	public boolean getQemuDisableGraphics() {
		return Boolean.valueOf(
				getString(GDB_QEMU_DISABLE_GRAPHICS, Boolean.toString(DefaultPreferences.DISABLE_GRAPHICS_DEFAULT)));
	}

	public boolean getQemuDisableGraphics(String prefix) {
		if (prefix.isEmpty()) {
			// Legacy interface.
			return Boolean.valueOf(getString(prefix + GDB_QEMU_DISABLE_GRAPHICS,
					Boolean.toString(DefaultPreferences.DISABLE_GRAPHICS_DEFAULT)));
		} else {
			return Boolean.valueOf(getString(prefix + GDB_QEMU_DISABLE_GRAPHICS, Boolean.toString(true)));
		}
	}

	@Deprecated
	public void putQemuDisableGraphics(boolean value) {
		putWorkspaceString(GDB_QEMU_DO_DEBUG_IN_RAM, Boolean.toString(value));
	}

	public void putQemuDisableGraphics(String prefix, boolean value) {
		putWorkspaceString(prefix + GDB_QEMU_DO_DEBUG_IN_RAM, Boolean.toString(value));
	}

	// ------------------------------------------------------------------------
}
