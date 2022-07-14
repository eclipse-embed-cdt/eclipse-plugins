/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
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

import org.eclipse.embedcdt.core.preferences.Discoverer;

public class DefaultPreferences extends org.eclipse.embedcdt.debug.gdbjtag.core.preferences.DefaultPreferences {

	// ------------------------------------------------------------------------

	public static final boolean SERVER_DO_START_DEFAULT = true;
	public static final boolean DO_START_GDB_SERVER_DEFAULT = true;

	public static final String SERVER_EXECUTABLE_DEFAULT = "${qemu_path}/${qemu_executable}";
	public static final String PARAMETRIZED_SERVER_EXECUTABLE_DEFAULT = "${qemu_%s_path}/${qemu_%s_executable}";

	protected static final String CLIENT_EXECUTABLE_DEFAULT = "${cross_prefix}gdb${cross_suffix}";

	@Deprecated
	public static final String SERVER_EXECUTABLE_DEFAULT_NAME = "qemu-system-gnuarmeclipse";

	public static final String QEMU_BOARD_NAME_DEFAULT = "?";
	public static final String QEMU_DEVICE_NAME_DEFAULT = "?";

	public static final int SERVER_GDB_PORT_NUMBER_DEFAULT = 1234;
	public static final String SERVER_OTHER_OPTIONS_DEFAULT = "-d unimp,guest_errors"; //$NON-NLS-1$

	public static final boolean DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT = true;

	public static final int SERVER_GDB_DELAY_SECONDS_DEFAULT = 0;

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

	// HKCU & HKLM LOCAL_MACHINE
	private static final String REG_SUBKEY = "\\GNU ARM Eclipse\\QEMU";
	// Standard Microsoft recommendation.
	private static final String REG_NAME = "InstallLocation";

	// ------------------------------------------------------------------------

	public DefaultPreferences(String pluginId) {
		super(pluginId);
	}

	// ------------------------------------------------------------------------

	@Deprecated
	public String getGdbServerExecutable() {
		return getString(PersistentPreferences.GDB_SERVER_EXECUTABLE, SERVER_EXECUTABLE_DEFAULT);
	}

	/**
	 * Get GDB server executable.
	 *
	 * @param prefix
	 * @param architecture
	 * @return executable name
	 */
	public String getGdbServerExecutable(String prefix, String architecture) {

		if (prefix.isEmpty()) {
			return getString(prefix + PersistentPreferences.GDB_SERVER_EXECUTABLE, SERVER_EXECUTABLE_DEFAULT);
		} else {
			String serverDefault = String.format(PARAMETRIZED_SERVER_EXECUTABLE_DEFAULT, architecture, architecture);
			return getString(prefix + PersistentPreferences.GDB_SERVER_EXECUTABLE, serverDefault);
		}
	}

	@Deprecated
	public String getGdbClientExecutable() {
		return getString(PersistentPreferences.GDB_CLIENT_EXECUTABLE, CLIENT_EXECUTABLE_DEFAULT);
	}

	public String getGdbClientExecutable(String prefix) {
		return getString(prefix + PersistentPreferences.GDB_CLIENT_EXECUTABLE, CLIENT_EXECUTABLE_DEFAULT);
	}

	// ------------------------------------------------------------------------

	@Deprecated
	public boolean getQemuEnableSemihosting() {
		return getBoolean(PersistentPreferences.GDB_QEMU_ENABLE_SEMIHOSTING, ENABLE_SEMIHOSTING_DEFAULT);
	}

	public boolean getQemuEnableSemihosting(String prefix) {
		return getBoolean(prefix + PersistentPreferences.GDB_QEMU_ENABLE_SEMIHOSTING, ENABLE_SEMIHOSTING_DEFAULT);
	}

	// ------------------------------------------------------------------------

	public boolean getTabMainCheckProgram() {
		return getBoolean(PersistentPreferences.TAB_MAIN_CHECK_PROGRAM,
				PersistentPreferences.TAB_MAIN_CHECK_PROGRAM_DEFAULT);
	}

	// ------------------------------------------------------------------------

	@Deprecated
	public boolean getGdbServerDoStart() {
		return getBoolean(PersistentPreferences.GDB_SERVER_DO_START, SERVER_DO_START_DEFAULT);
	}

	public boolean getGdbServerDoStart(String prefix) {
		return getBoolean(prefix + PersistentPreferences.GDB_SERVER_DO_START, SERVER_DO_START_DEFAULT);
	}

	@Deprecated
	public String getGdbServerOtherOptions() {
		return getString(PersistentPreferences.GDB_SERVER_OTHER_OPTIONS, SERVER_OTHER_OPTIONS_DEFAULT);
	}

	public String getGdbServerOtherOptions(String prefix) {
		return getString(prefix + PersistentPreferences.GDB_SERVER_OTHER_OPTIONS, SERVER_OTHER_OPTIONS_DEFAULT);
	}

	@Deprecated
	public String getGdbClientOtherOptions() {
		return getString(PersistentPreferences.GDB_CLIENT_OTHER_OPTIONS, CLIENT_OTHER_OPTIONS_DEFAULT);
	}

	public String getGdbClientOtherOptions(String prefix) {
		return getString(prefix + PersistentPreferences.GDB_CLIENT_OTHER_OPTIONS, CLIENT_OTHER_OPTIONS_DEFAULT);
	}

	@Deprecated
	public String getGdbClientCommands() {
		return getString(PersistentPreferences.GDB_CLIENT_COMMANDS, CLIENT_COMMANDS_DEFAULT);
	}

	public String getGdbClientCommands(String prefix) {
		return getString(prefix + PersistentPreferences.GDB_CLIENT_COMMANDS, CLIENT_COMMANDS_DEFAULT);
	}

	// ------------------------------------------------------------------------

	@Deprecated
	public boolean getQemuDebugInRam() {
		return getBoolean(PersistentPreferences.GDB_QEMU_DO_DEBUG_IN_RAM, DO_DEBUG_IN_RAM_DEFAULT);
	}

	public boolean getQemuDebugInRam(String prefix) {
		return getBoolean(prefix + PersistentPreferences.GDB_QEMU_DO_DEBUG_IN_RAM, DO_DEBUG_IN_RAM_DEFAULT);
	}

	@Deprecated
	public boolean getQemuDoInitialReset() {
		return getBoolean(PersistentPreferences.GDB_QEMU_DO_INITIAL_RESET, DO_INITIAL_RESET_DEFAULT);
	}

	public boolean getQemuDoInitialReset(String prefix) {
		return getBoolean(prefix + PersistentPreferences.GDB_QEMU_DO_INITIAL_RESET, DO_INITIAL_RESET_DEFAULT);
	}

	@Deprecated
	public String getQemuInitOther() {
		return getString(PersistentPreferences.GDB_QEMU_INIT_OTHER, INIT_OTHER_DEFAULT);
	}

	public String getQemuInitOther(String prefix) {
		return getString(prefix + PersistentPreferences.GDB_QEMU_INIT_OTHER, INIT_OTHER_DEFAULT);
	}

	@Deprecated
	public boolean getQemuDoPreRunReset() {
		return getBoolean(PersistentPreferences.GDB_QEMU_DO_PRERUN_RESET, DO_PRERUN_RESET_DEFAULT);
	}

	public boolean getQemuDoPreRunReset(String prefix) {
		return getBoolean(prefix + PersistentPreferences.GDB_QEMU_DO_PRERUN_RESET, DO_PRERUN_RESET_DEFAULT);
	}

	@Deprecated
	public String getQemuPreRunOther() {
		return getString(PersistentPreferences.GDB_QEMU_PRERUN_OTHER, PRERUN_OTHER_DEFAULT);
	}

	public String getQemuPreRunOther(String prefix) {
		return getString(prefix + PersistentPreferences.GDB_QEMU_PRERUN_OTHER, PRERUN_OTHER_DEFAULT);
	}

	@Deprecated
	public boolean getQemuDisableGraphics() {
		return getBoolean(PersistentPreferences.GDB_QEMU_DISABLE_GRAPHICS, DISABLE_GRAPHICS_DEFAULT);
	}

	public boolean getQemuDisableGraphics(String prefix) {
		return getBoolean(prefix + PersistentPreferences.GDB_QEMU_DISABLE_GRAPHICS, DISABLE_GRAPHICS_DEFAULT);
	}

	// ------------------------------------------------------------------------

	@Override
	@Deprecated
	protected String getRegistryInstallFolder(String subFolder, String executableName) {

		String path = Discoverer.getRegistryInstallFolder(executableName, subFolder, REG_SUBKEY, REG_NAME);
		return path;
	}

	// ------------------------------------------------------------------------
}
