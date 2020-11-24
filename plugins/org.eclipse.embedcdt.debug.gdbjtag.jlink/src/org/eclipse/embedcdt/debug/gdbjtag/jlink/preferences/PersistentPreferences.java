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

package org.eclipse.embedcdt.debug.gdbjtag.jlink.preferences;

/**
 * Manage a workspace preference file stored in:
 * 
 * <pre>
 * workspace/.metadata/.plugins/org.eclipse.core.runtime/.settings/<plug-in-id>.prefs
 * </pre>
 *
 * Some of the values may be retrieved from the EclipseDefaults.
 */
public class PersistentPreferences extends org.eclipse.embedcdt.debug.gdbjtag.core.preferences.PersistentPreferences {

	// ------------------------------------------------------------------------

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

	// ------------------------------------------------------------------------

	private DefaultPreferences fDefaultPreferences;

	// ------------------------------------------------------------------------

	public PersistentPreferences(String pluginId) {
		super(pluginId);

		fDefaultPreferences = new DefaultPreferences(pluginId);
	}

	// ----- Install folder -------------------------------------------
	public String getInstallFolder() {

		return getString(INSTALL_FOLDER, INSTALL_FOLDER_DEFAULT);
	}

	// ----- Executable name ------------------------------------------
	public String getExecutableName() {

		return getString(EXECUTABLE_NAME, EXECUTABLE_NAME_DEFAULT);
	}

	// ----- Is strict ------------------------------------------------
	public boolean getFolderStrict() {

		return getBoolean(FOLDER_STRICT, FOLDER_STRICT_DEFAULT);
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

	// ----- flash device id --------------------------------------------------
	public String getFlashDeviceName() {

		return getString(FLASH_DEVICE_NAME, DefaultPreferences.FLASH_DEVICE_NAME_DEFAULT);
	}

	public void putFlashDeviceName(String value) {

		putWorkspaceString(FLASH_DEVICE_NAME, value);
	}

	// ----- gdb server endianness --------------------------------------------
	public String getGdbServerEndianness() {

		return getString(GDB_SERVER_ENDIANNESS, DefaultPreferences.SERVER_ENDIANNESS_DEFAULT);
	}

	public void putGdbServerEndianness(String value) {

		putWorkspaceString(GDB_SERVER_ENDIANNESS, value);
	}

	// ----- gdb server connection --------------------------------------------
	public String getGdbServerConnection() {

		return getString(GDB_SERVER_CONNECTION, DefaultPreferences.SERVER_CONNECTION_DEFAULT);
	}

	public void putGdbServerConnection(String value) {

		putWorkspaceString(GDB_SERVER_CONNECTION, value);
	}

	// ----- gdb server connection address ------------------------------------
	public String getGdbServerConnectionAddress() {

		return getString(GDB_SERVER_CONNECTION_ADDRESS, DefaultPreferences.SERVER_CONNECTION_ADDRESS_DEFAULT);
	}

	public void putGdbServerConnectionAddress(String value) {

		putWorkspaceString(GDB_SERVER_CONNECTION_ADDRESS, value);
	}

	// ----- gdb server interface ---------------------------------------------
	public String getGdbServerInterface() {

		String value = getString(GDB_SERVER_INTERFACE, null);
		if (value != null) {
			return value;
		}
		return fDefaultPreferences.getGdbServerInterface();
	}

	public void putGdbServerInterface(String value) {

		putWorkspaceString(GDB_SERVER_INTERFACE, value);
	}

	// ----- gdb server initial speed -----------------------------------------
	public String getGdbServerInitialSpeed() {

		return getString(GDB_SERVER_INITIAL_SPEED, DefaultPreferences.SERVER_INITIAL_SPEED_DEFAULT);
	}

	public void putGdbServerInitialSpeed(String value) {

		putWorkspaceString(GDB_SERVER_INITIAL_SPEED, value);
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

	// ----- jlink do initial reset -------------------------------------------
	public boolean getJLinkDoInitialReset() {

		return Boolean.valueOf(
				getString(GDB_JLINK_DO_INITIAL_RESET, Boolean.toString(DefaultPreferences.DO_INITIAL_RESET_DEFAULT)));
	}

	public void putJLinkDoInitialReset(boolean value) {

		putWorkspaceString(GDB_JLINK_DO_INITIAL_RESET, Boolean.toString(value));
	}

	// ----- jlink initial reset type -----------------------------------------
	public String getJLinkInitialResetType() {

		return getString(GDB_JLINK_INITIAL_RESET_TYPE, DefaultPreferences.INITIAL_RESET_TYPE_DEFAULT);
	}

	public void putJLinkInitialResetType(String value) {

		putWorkspaceString(GDB_JLINK_INITIAL_RESET_TYPE, value);
	}

	// ----- jlink initial reset speed ----------------------------------------
	public int getJLinkInitialResetSpeed() {

		return Integer.valueOf(getString(GDB_JLINK_INITIAL_RESET_SPEED,
				Integer.toString(DefaultPreferences.INITIAL_RESET_SPEED_DEFAULT)));
	}

	public void putJLinkInitialResetSpeed(int value) {

		putWorkspaceString(GDB_JLINK_INITIAL_RESET_SPEED, Integer.toString(value));
	}

	// ----- jlink speed ------------------------------------------------------
	public String getJLinkSpeed() {

		return getString(GDB_JLINK_SPEED, DefaultPreferences.JLINK_SPEED_DEFAULT);
	}

	public void putJLinkSpeed(String value) {

		putWorkspaceString(GDB_JLINK_SPEED, value);
	}

	// ----- jlink enable flash breakpoints -----------------------------------
	public boolean getJLinkEnableFlashBreakpoints() {

		return Boolean.valueOf(getString(GDB_JLINK_ENABLE_FLASH_BREAKPOINTS,
				Boolean.toString(DefaultPreferences.ENABLE_FLASH_BREAKPOINTS_DEFAULT)));
	}

	public void putJLinkEnableFlashBreakpoints(boolean value) {

		putWorkspaceString(GDB_JLINK_ENABLE_FLASH_BREAKPOINTS, Boolean.toString(value));
	}

	// ----- jlink enable semihosting -----------------------------------------
	public boolean getJLinkEnableSemihosting() {

		String value = getString(GDB_JLINK_ENABLE_SEMIHOSTING, null);
		if (value != null) {
			return Boolean.valueOf(value);
		}
		return fDefaultPreferences.getJLinkEnableSemihosting();
	}

	public void putJLinkEnableSemihosting(boolean value) {

		putWorkspaceString(GDB_JLINK_ENABLE_SEMIHOSTING, Boolean.toString(value));
	}

	// ----- jlink semihosting telnet -----------------------------------------
	public boolean getJLinkSemihostingTelnet() {

		return Boolean.valueOf(getString(GDB_JLINK_SEMIHOSTING_TELNET,
				Boolean.toString(DefaultPreferences.ENABLE_SEMIHOSTING_DEFAULT)));
	}

	public void putJLinkSemihostingTelnet(boolean value) {

		putWorkspaceString(GDB_JLINK_SEMIHOSTING_TELNET, Boolean.toString(value));
	}

	// ----- jlink semihosting client -----------------------------------------
	public boolean getJLinkSemihostingClient() {

		return Boolean.valueOf(getString(GDB_JLINK_SEMIHOSTING_CLIENT,
				Boolean.toString(DefaultPreferences.SEMIHOSTING_CLIENT_DEFAULT)));
	}

	public void putJLinkSemihostingClient(boolean value) {

		putWorkspaceString(GDB_JLINK_SEMIHOSTING_CLIENT, Boolean.toString(value));
	}

	// ----- jlink enable swo -------------------------------------------------
	public boolean getJLinkEnableSwo() {

		String value = getString(GDB_JLINK_ENABLE_SWO, null);
		if (value != null) {
			return Boolean.valueOf(value);
		}
		return fDefaultPreferences.getJLinkEnableSwo();
	}

	public void putJLinkEnableSwo(boolean value) {

		putWorkspaceString(GDB_JLINK_ENABLE_SWO, Boolean.toString(value));
	}

	// ----- jlink swo cpu frequency ------------------------------------------
	public int getJLinkSwoEnableTargetCpuFreq() {

		return Integer.valueOf(getString(GDB_JLINK_SWO_ENABLE_TARGET_CPU_FREQ,
				Integer.toString(DefaultPreferences.SWO_ENABLE_TARGET_CPU_FREQ_DEFAULT)));
	}

	public void putJLinkSwoEnableTargetCpuFreq(int value) {

		putWorkspaceString(GDB_JLINK_SWO_ENABLE_TARGET_CPU_FREQ, Integer.toString(value));
	}

	// ----- jlink swo frequency ----------------------------------------------
	public int getJLinkSwoEnableTargetSwoFreq() {

		return Integer.valueOf(getString(GDB_JLINK_SWO_ENABLE_TARGET_SWO_FREQ,
				Integer.toString(DefaultPreferences.SWO_ENABLE_TARGET_SWO_FREQ_DEFAULT)));
	}

	public void putJLinkSwoEnableTargetSwoFreq(int value) {

		putWorkspaceString(GDB_JLINK_SWO_ENABLE_TARGET_SWO_FREQ, Integer.toString(value));
	}

	// ----- jlink swo mask ---------------------------------------------------
	public String getJLinkSwoEnableTargetPortMask() {

		return getString(GDB_JLINK_SWO_ENABLE_TARGET_PORT_MASK, DefaultPreferences.SWO_ENABLE_TARGET_PORT_MASK_DEFAULT);
	}

	public void putJLinkSwoEnableTargetPortMask(String value) {

		putWorkspaceString(GDB_JLINK_SWO_ENABLE_TARGET_PORT_MASK, value);
	}

	// ----- jlink init other -------------------------------------------------
	public String getJLinkInitOther() {

		return getString(GDB_JLINK_INIT_OTHER, DefaultPreferences.INIT_OTHER_DEFAULT);
	}

	public void putJLinkInitOther(String value) {

		putWorkspaceString(GDB_JLINK_INIT_OTHER, value);
	}

	// ----- jlink debug in ram -----------------------------------------------
	public boolean getJLinkDebugInRam() {

		return Boolean.valueOf(
				getString(GDB_JLINK_DO_DEBUG_IN_RAM, Boolean.toString(DefaultPreferences.DO_DEBUG_IN_RAM_DEFAULT)));
	}

	public void putJLinkDebugInRam(boolean value) {

		putWorkspaceString(GDB_JLINK_DO_DEBUG_IN_RAM, Boolean.toString(value));
	}

	// ----- jlink do prerun reset -----------------------------------
	public boolean getJLinkDoPreRunReset() {

		return Boolean.valueOf(
				getString(GDB_JLINK_DO_PRERUN_RESET, Boolean.toString(DefaultPreferences.DO_PRERUN_RESET_DEFAULT)));
	}

	public void putJLinkDoPreRunReset(boolean value) {

		putWorkspaceString(GDB_JLINK_DO_PRERUN_RESET, Boolean.toString(value));
	}

	// ----- jlink prerun reset type ------------------------------------------
	public String getJLinkPreRunResetType() {

		return getString(GDB_JLINK_PRERUN_RESET_TYPE, DefaultPreferences.PRERUN_RESET_TYPE_DEFAULT);
	}

	public void putJLinkPreRunResetType(String value) {

		putWorkspaceString(GDB_JLINK_PRERUN_RESET_TYPE, value);
	}

	// ----- jlink init other -------------------------------------------------
	public String getJLinkPreRunOther() {

		return getString(GDB_JLINK_PRERUN_OTHER, DefaultPreferences.PRERUN_OTHER_DEFAULT);
	}

	public void putJLinkPreRunOther(String value) {

		putWorkspaceString(GDB_JLINK_PRERUN_OTHER, value);
	}

	// ------------------------------------------------------------------------
}
