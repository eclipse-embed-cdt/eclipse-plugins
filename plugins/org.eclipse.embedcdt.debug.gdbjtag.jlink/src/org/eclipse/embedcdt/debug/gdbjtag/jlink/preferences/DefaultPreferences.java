/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
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

import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.core.preferences.Discoverer;
import org.eclipse.embedcdt.debug.gdbjtag.jlink.Activator;
import org.osgi.service.prefs.BackingStoreException;

public class DefaultPreferences extends org.eclipse.embedcdt.debug.gdbjtag.core.preferences.DefaultPreferences {

	// ------------------------------------------------------------------------

	// Constants.
	public static final String INTERFACE_SWD = "swd";
	public static final String INTERFACE_JTAG = "jtag";

	public static final String ENDIANNESS_LITTLE = "little";
	public static final String ENDIANNESS_BIG = "big";

	public static final String GDB_SERVER_CONNECTION_USB = "usb"; //$NON-NLS-1$
	public static final String GDB_SERVER_CONNECTION_IP = "ip"; //$NON-NLS-1$

	public static final String INTERFACE_SPEED_AUTO = "auto";
	public static final String INTERFACE_SPEED_ADAPTIVE = "adaptive";
	public static final String INTERFACE_SPEED_AUTO_COMMAND = "monitor speed auto";
	public static final String INTERFACE_SPEED_ADAPTIVE_COMMAND = "monitor speed adaptive";
	public static final String INTERFACE_SPEED_FIXED_COMMAND = "monitor speed ";

	// ------------------------------------------------------------------------

	// Normally loaded from defaults, but also used in
	// DefaultPreferenceInitializer.
	protected static final boolean TAB_MAIN_CHECK_PROGRAM_DEFAULT = false;

	protected static final String GDB_SERVER_EXECUTABLE_DEFAULT = "${jlink_path}/${jlink_gdbserver}";
	protected static final String GDB_CLIENT_EXECUTABLE_DEFAULT = "${cross_prefix}gdb${cross_suffix}";

	protected static final String SERVER_OTHER_OPTIONS_DEFAULT = "-singlerun -strict -timeout 0 -nogui"; //$NON-NLS-1$
	protected static final String SERVER_INTERFACE_DEFAULT = INTERFACE_SWD;
	protected static final boolean ENABLE_SWO_DEFAULT = true;
	protected static final boolean ENABLE_SEMIHOSTING_DEFAULT = true;
	protected static final boolean SERVER_DO_START_DEFAULT = true;
	protected static final String SERVER_ENDIANNESS_DEFAULT = ENDIANNESS_LITTLE;
	protected static final String SERVER_CONNECTION_DEFAULT = GDB_SERVER_CONNECTION_USB;
	protected static final String SERVER_CONNECTION_ADDRESS_DEFAULT = "";
	protected static final String SERVER_INITIAL_SPEED_DEFAULT = "1000"; // kHz
	protected static final String CLIENT_OTHER_OPTIONS_DEFAULT = "";
	protected static final String CLIENT_COMMANDS_DEFAULT = "set mem inaccessible-by-default off\n";
	protected static final boolean DO_INITIAL_RESET_DEFAULT = true;
	protected static final String INITIAL_RESET_TYPE_DEFAULT = "";
	protected static final int INITIAL_RESET_SPEED_DEFAULT = 1000;
	protected static final String JLINK_SPEED_DEFAULT = INTERFACE_SPEED_AUTO;
	protected static final boolean ENABLE_FLASH_BREAKPOINTS_DEFAULT = true;
	protected static final boolean SEMIHOSTING_TELNET_DEFAULT = true;
	protected static final boolean SEMIHOSTING_CLIENT_DEFAULT = false;
	protected static final int SWO_ENABLE_TARGET_CPU_FREQ_DEFAULT = 0;
	protected static final int SWO_ENABLE_TARGET_SWO_FREQ_DEFAULT = 0;
	protected static final String SWO_ENABLE_TARGET_PORT_MASK_DEFAULT = "0x1";
	protected static final String INIT_OTHER_DEFAULT = "";
	protected static final boolean DO_DEBUG_IN_RAM_DEFAULT = false;
	protected static final boolean DO_PRERUN_RESET_DEFAULT = true;
	protected static final String PRERUN_RESET_TYPE_DEFAULT = "";
	protected static final String PRERUN_OTHER_DEFAULT = "";

	// ------------------------------------------------------------------------

	// Not yet preferences, only constants, but moved here for consistency.
	public static final boolean DO_CONNECT_TO_RUNNING_DEFAULT = false;
	public static final String FLASH_DEVICE_NAME_DEFAULT = "";

	public static final int GDB_SERVER_GDB_PORT_NUMBER_DEFAULT = 2331;
	public static final int GDB_SERVER_SWO_PORT_NUMBER_DEFAULT = 2332;
	public static final int GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT = 2333;
	public static final boolean DO_GDB_SERVER_VERIFY_DOWNLOAD_DEFAULT = true;
	public static final boolean DO_GDB_SERVER_INIT_REGS_DEFAULT = true;
	public static final boolean DO_GDB_SERVER_LOCAL_ONLY_DEFAULT = true;
	public static final boolean DO_GDB_SERVER_SILENT_DEFAULT = false;
	public static final String GDB_SERVER_LOG_DEFAULT = ""; //$NON-NLS-1$
	public static final boolean DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT = true;
	public static final boolean DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT = true;
	public static final boolean USE_REMOTE_TARGET_DEFAULT = true;
	public static final String REMOTE_IP_ADDRESS_LOCALHOST = "localhost"; //$NON-NLS-1$
	public static final String REMOTE_IP_ADDRESS_DEFAULT = REMOTE_IP_ADDRESS_LOCALHOST; // $NON-NLS-1$
	public static final int REMOTE_PORT_NUMBER_DEFAULT = GDB_SERVER_GDB_PORT_NUMBER_DEFAULT;
	public static final boolean UPDATE_THREAD_LIST_DEFAULT = false;

	public static final boolean DO_STOP_AT_DEFAULT = true;
	public static final boolean DO_CONTINUE_DEFAULT = true;

	// Debugger Commands
	public static final String ENABLE_SEMIHOSTING_COMMAND = "monitor semihosting enable";

	public static final String DO_SECOND_RESET_COMMAND = "monitor reset ";
	public static final String STOP_AT_NAME_DEFAULT = "main";

	public static final String DO_FIRST_RESET_COMMAND = "monitor reset ";

	// Usually these commands are issues together
	public static final String CLRBP_COMMAND = "monitor clrbp";
	public static final String HALT_COMMAND = "monitor halt";
	public static final String REGS_COMMAND = "monitor regs";
	public static final String FLUSH_REGISTERS_COMMAND = "flushreg";

	public static final String ENABLE_FLASH_BREAKPOINTS_COMMAND = "monitor flash breakpoints ";

	public static final int ENABLE_SEMIHOSTING_IOCLIENT_TELNET_MASK = 1;
	public static final int ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT_MASK = 2;

	public static final String ENABLE_SEMIHOSTING_IOCLIENT_COMMAND = "monitor semihosting IOClient "; //$NON-NLS-1$

	public static final String DISABLE_SWO_COMMAND = "monitor SWO DisableTarget 0xFFFFFFFF";
	public static final String ENABLE_SWO_COMMAND = "monitor SWO EnableTarget ";

	public static final String DO_CONTINUE_COMMAND = "continue";

	// ------------------------------------------------------------------------

	// Current SEGGER versions use HKEY_CURRENT_USER
	private static final String REG_SUBKEY = "\\SEGGER\\J-Link";
	private static final String REG_NAME = "InstallPath";

	// ------------------------------------------------------------------------

	public DefaultPreferences(String pluginId) {
		super(pluginId);
	}

	// ------------------------------------------------------------------------

	public String getGdbServerExecutable() {
		String value = getString(PersistentPreferences.GDB_SERVER_EXECUTABLE, GDB_SERVER_EXECUTABLE_DEFAULT);
		return value;
	}

	public String getGdbClientExecutable() {
		String value = getString(PersistentPreferences.GDB_CLIENT_EXECUTABLE, GDB_CLIENT_EXECUTABLE_DEFAULT);
		return value;
	}

	// ------------------------------------------------------------------------

	public String getExecutableName() {

		String key = PersistentPreferences.EXECUTABLE_NAME;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.DefaultPreferences.getExecutableName() = \"" + value + "\"");
		}
		return value;

	}

	public String getExecutableNameOs() {

		String key = EclipseUtils.getKeyOs(PersistentPreferences.EXECUTABLE_NAME_OS);
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.DefaultPreferences.getExecutableNameOs() = \"" + value + "\" (" + key + ")");
		}
		return value;
	}

	public void putExecutableName(String value) {

		String key = PersistentPreferences.EXECUTABLE_NAME;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.DefaultPreferences.putExecutableName(\"" + value + "\")");
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	public String getInstallFolder() {

		String key = PersistentPreferences.INSTALL_FOLDER;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.DefaultPreferences.getInstallFolder()=\"" + value + "\"");
		}
		return value;
	}

	public void putInstallFolder(String value) {

		String key = PersistentPreferences.INSTALL_FOLDER;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.DefaultPreferences.putInstallFolder(\"" + value + "\")");
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	public String getSearchPath() {

		String key = PersistentPreferences.SEARCH_PATH;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.DefaultPreferences.getSearchPath() = \"" + value + "\"");
		}
		return value;
	}

	public String getSearchPathOs() {

		String key = EclipseUtils.getKeyOs(PersistentPreferences.SEARCH_PATH_OS);
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.DefaultPreferences.getSearchPathOs() = \"" + value + "\" (" + key + ")");
		}
		return value;
	}

	public void putSearchPath(String value) {

		String key = PersistentPreferences.SEARCH_PATH;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.DefaultPreferences.putSearchPath(\"" + value + "\")");
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	public String getGdbServerInterface() {
		String value = getString(PersistentPreferences.GDB_SERVER_INTERFACE, SERVER_INTERFACE_DEFAULT);
		return value;
	}

	public boolean getJLinkEnableSemihosting() {

		try {
			if (fPreferences.nodeExists(PersistentPreferences.GDB_JLINK_ENABLE_SEMIHOSTING)) {
				return getBoolean(PersistentPreferences.GDB_JLINK_ENABLE_SEMIHOSTING, ENABLE_SEMIHOSTING_DEFAULT);
			}
		} catch (BackingStoreException e) {
			;
		}
		return ENABLE_SEMIHOSTING_DEFAULT;
	}

	public boolean getJLinkEnableSwo() {

		try {
			if (fPreferences.nodeExists(PersistentPreferences.GDB_JLINK_ENABLE_SWO)) {
				return getBoolean(PersistentPreferences.GDB_JLINK_ENABLE_SWO, ENABLE_SWO_DEFAULT);
			}
		} catch (BackingStoreException e) {
			;
		}
		return ENABLE_SWO_DEFAULT;
	}

	// ------------------------------------------------------------------------

	public boolean getTabMainCheckProgram() {
		return getBoolean(PersistentPreferences.TAB_MAIN_CHECK_PROGRAM, TAB_MAIN_CHECK_PROGRAM_DEFAULT);
	}

	// ------------------------------------------------------------------------

	public boolean getGdbServerDoStart() {
		return getBoolean(PersistentPreferences.GDB_SERVER_DO_START, SERVER_DO_START_DEFAULT);
	}

	public String getGdbServerEndianness() {
		return getString(PersistentPreferences.GDB_SERVER_ENDIANNESS, SERVER_ENDIANNESS_DEFAULT);
	}

	public String getGdbServerConnection() {
		return getString(PersistentPreferences.GDB_SERVER_CONNECTION, SERVER_CONNECTION_DEFAULT);
	}

	public String getGdbServerConnectionAddress() {
		return getString(PersistentPreferences.GDB_SERVER_CONNECTION_ADDRESS, SERVER_CONNECTION_ADDRESS_DEFAULT);
	}

	public String getGdbServerInitialSpeed() {
		return getString(PersistentPreferences.GDB_SERVER_INITIAL_SPEED, SERVER_INITIAL_SPEED_DEFAULT);
	}

	public String getGdbServerOtherOptions() {
		return getString(PersistentPreferences.GDB_SERVER_OTHER_OPTIONS, SERVER_OTHER_OPTIONS_DEFAULT);
	}

	public String getGdbClientOtherOptions() {
		return getString(PersistentPreferences.GDB_CLIENT_OTHER_OPTIONS, CLIENT_OTHER_OPTIONS_DEFAULT);
	}

	public String getGdbClientCommands() {
		return getString(PersistentPreferences.GDB_CLIENT_COMMANDS, CLIENT_COMMANDS_DEFAULT);
	}

	public boolean getJLinkDoInitialReset() {
		return getBoolean(PersistentPreferences.GDB_JLINK_DO_INITIAL_RESET, DO_INITIAL_RESET_DEFAULT);
	}

	public String getJLinkInitialResetType() {
		return getString(PersistentPreferences.GDB_JLINK_INITIAL_RESET_TYPE, INITIAL_RESET_TYPE_DEFAULT);
	}

	public int getJLinkInitialResetSpeed() {
		return getInt(PersistentPreferences.GDB_JLINK_INITIAL_RESET_SPEED, INITIAL_RESET_SPEED_DEFAULT);
	}

	public String getJLinkSpeed() {
		return getString(PersistentPreferences.GDB_JLINK_SPEED, JLINK_SPEED_DEFAULT);
	}

	public boolean getJLinkEnableFlashBreakpoints() {
		return getBoolean(PersistentPreferences.GDB_JLINK_ENABLE_FLASH_BREAKPOINTS, ENABLE_FLASH_BREAKPOINTS_DEFAULT);
	}

	public boolean getJLinkSemihostingTelnet() {
		return getBoolean(PersistentPreferences.GDB_JLINK_SEMIHOSTING_TELNET, SEMIHOSTING_TELNET_DEFAULT);
	}

	public boolean getJLinkSemihostingClient() {
		return getBoolean(PersistentPreferences.GDB_JLINK_SEMIHOSTING_CLIENT, SEMIHOSTING_CLIENT_DEFAULT);
	}

	public int getJLinkSwoEnableTargetCpuFreq() {
		return getInt(PersistentPreferences.GDB_JLINK_SWO_ENABLE_TARGET_CPU_FREQ, SWO_ENABLE_TARGET_CPU_FREQ_DEFAULT);
	}

	public int getJLinkSwoEnableTargetSwoFreq() {
		return getInt(PersistentPreferences.GDB_JLINK_SWO_ENABLE_TARGET_SWO_FREQ, SWO_ENABLE_TARGET_SWO_FREQ_DEFAULT);
	}

	public String getJLinkSwoEnableTargetPortMask() {
		return getString(PersistentPreferences.GDB_JLINK_SWO_ENABLE_TARGET_PORT_MASK,
				SWO_ENABLE_TARGET_PORT_MASK_DEFAULT);
	}

	public String getJLinkInitOther() {
		return getString(PersistentPreferences.GDB_JLINK_INIT_OTHER, INIT_OTHER_DEFAULT);
	}

	public boolean getJLinkDebugInRam() {
		return getBoolean(PersistentPreferences.GDB_JLINK_DO_DEBUG_IN_RAM, DO_DEBUG_IN_RAM_DEFAULT);
	}

	public boolean getJLinkDoPreRunReset() {
		return getBoolean(PersistentPreferences.GDB_JLINK_DO_PRERUN_RESET, DO_PRERUN_RESET_DEFAULT);
	}

	public String getJLinkPreRunResetType() {
		return getString(PersistentPreferences.GDB_JLINK_PRERUN_RESET_TYPE, PRERUN_RESET_TYPE_DEFAULT);
	}

	public String getJLinkPreRunOther() {
		return getString(PersistentPreferences.GDB_JLINK_PRERUN_OTHER, PRERUN_OTHER_DEFAULT);
	}

	// ------------------------------------------------------------------------

	protected String getRegistryInstallFolder(String subFolder, String executableName) {

		String path = Discoverer.getRegistryInstallFolder(executableName, subFolder, REG_SUBKEY, REG_NAME);
		return path;
	}

	// ------------------------------------------------------------------------
}
