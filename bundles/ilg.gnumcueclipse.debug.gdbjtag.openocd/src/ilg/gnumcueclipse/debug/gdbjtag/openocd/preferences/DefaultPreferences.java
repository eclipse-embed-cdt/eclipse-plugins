/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.openocd.preferences;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.core.preferences.Discoverer;
import ilg.gnumcueclipse.debug.gdbjtag.openocd.Activator;

public class DefaultPreferences extends ilg.gnumcueclipse.debug.gdbjtag.preferences.DefaultPreferences {

	// ------------------------------------------------------------------------

	// Constants
	public static final String REMOTE_IP_ADDRESS_LOCALHOST = "localhost"; //$NON-NLS-1$

	// ------------------------------------------------------------------------

	// Preferences
	protected static final boolean TAB_MAIN_CHECK_PROGRAM_DEFAULT = false;

	public static final String GDB_SERVER_EXECUTABLE_DEFAULT = "${openocd_path}/${openocd_executable}";

	public static final String GDB_SERVER_EXECUTABLE_DEFAULT_NAME = "openocd";
	protected static final String GDB_CLIENT_EXECUTABLE_DEFAULT = "${cross_prefix}gdb${cross_suffix}";

	// ------------------------------------------------------------------------

	// Not yet preferences
	public static final boolean DO_START_GDB_SERVER_DEFAULT = true;
	public static final String GDB_SERVER_CONNECTION_ADDRESS_DEFAULT = "";
	public static final int GDB_SERVER_GDB_PORT_NUMBER_DEFAULT = 3333;
	public static final int GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT = 4444;
	public static final String GDB_SERVER_TCL_PORT_NUMBER_DEFAULT = "6666";
	public static final String GDB_SERVER_LOG_DEFAULT = ""; //$NON-NLS-1$
	public static final String GDB_SERVER_OTHER_DEFAULT = ""; //$NON-NLS-1$
	public static final boolean DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT = true;
	public static final boolean DO_GDB_SERVER_ALLOCATE_TELNET_CONSOLE_DEFAULT = false;

	public static final boolean DO_START_GDB_CLIENT_DEFAULT = true;
	public static final String GDB_CLIENT_OTHER_OPTIONS_DEFAULT = "";

	public static final boolean USE_REMOTE_TARGET_DEFAULT = true;
	public static final String REMOTE_IP_ADDRESS_DEFAULT = REMOTE_IP_ADDRESS_LOCALHOST; // $NON-NLS-1$
	public static final int REMOTE_PORT_NUMBER_DEFAULT = GDB_SERVER_GDB_PORT_NUMBER_DEFAULT;

	public static final boolean UPDATE_THREAD_LIST_DEFAULT = false;

	public static final boolean DO_FIRST_RESET_DEFAULT = true;
	public static final String FIRST_RESET_TYPE_DEFAULT = "init";

	public static final boolean ENABLE_SEMIHOSTING_DEFAULT = true;

	public static final boolean DO_DEBUG_IN_RAM_DEFAULT = false;

	public static final boolean DO_SECOND_RESET_DEFAULT = true;

	public static final String SECOND_RESET_TYPE_DEFAULT = "halt";

	public static final boolean DO_STOP_AT_DEFAULT = true;
	public static final String STOP_AT_NAME_DEFAULT = "main";

	public static final boolean DO_CONTINUE_DEFAULT = true;

	// ------------------------------------------------------------------------

	// Debugger commands
	public static final String GDB_CLIENT_OTHER_COMMANDS_DEFAULT = "set mem inaccessible-by-default off\n";
	public static final String DO_FIRST_RESET_COMMAND = "monitor reset ";
	public static final String HALT_COMMAND = "monitor halt";
	public static final String ENABLE_SEMIHOSTING_COMMAND = "monitor arm semihosting enable";
	public static final String DO_SECOND_RESET_COMMAND = "monitor reset ";
	public static final String DO_CONTINUE_COMMAND = "continue";
	public static final String OTHER_INIT_COMMANDS_DEFAULT = "";
	public static final String OTHER_RUN_COMMANDS_DEFAULT = "";

	// ------------------------------------------------------------------------

	// HKCU & HKLM LOCAL_MACHINE
	private static final String REG_SUBKEY = "\\GNU ARM Eclipse\\OpenOCD";
	// Standard Microsoft recommendation.
	private static final String REG_NAME = "InstallLocation";

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

	public String getOpenocdConfig() {
		String value = getString(PersistentPreferences.GDB_SERVER_OTHER_OPTIONS,
				DefaultPreferences.GDB_SERVER_OTHER_DEFAULT);
		return value;
	}

	// ------------------------------------------------------------------------

	public boolean getTabMainCheckProgram() {
		return getBoolean(PersistentPreferences.TAB_MAIN_CHECK_PROGRAM,
				PersistentPreferences.TAB_MAIN_CHECK_PROGRAM_DEFAULT);
	}

	// ------------------------------------------------------------------------

	public String getExecutableName() {

		String key = PersistentPreferences.EXECUTABLE_NAME;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.DefaultPreferences.getExecutableName() = \"" + value + "\"");
		}
		return value;
	}

	public String getExecutableNameOs() {

		String key = EclipseUtils.getKeyOs(PersistentPreferences.EXECUTABLE_NAME_OS);
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.DefaultPreferences.getExecutableNameOs() = \"" + value + "\" (" + key + ")");
		}
		return value;
	}

	public void putExecutableName(String value) {

		String key = PersistentPreferences.EXECUTABLE_NAME;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.DefaultPreferences.putExecutableName(\"" + value + "\")");
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	public String getInstallFolder() {

		String key = PersistentPreferences.INSTALL_FOLDER;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.DefaultPreferences.getInstallFolder() = \"" + value + "\"");
		}
		return value;
	}

	public void putInstallFolder(String value) {

		String key = PersistentPreferences.INSTALL_FOLDER;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.DefaultPreferences.putInstallFolder(\"" + value + "\")");
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	public String getSearchPath() {

		String key = PersistentPreferences.SEARCH_PATH;
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.DefaultPreferences.getSearchPath() = \"" + value + "\"");
		}
		return value;
	}

	public String getSearchPathOs() {

		String key = EclipseUtils.getKeyOs(PersistentPreferences.SEARCH_PATH_OS);
		String value = getString(key, "");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.DefaultPreferences.getSearchPathOs() = \"" + value + "\"");
		}
		return value;
	}

	public void putSearchPath(String value) {

		String key = PersistentPreferences.SEARCH_PATH;

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.DefaultPreferences.putSearchPath(\"" + value + "\")");
		}
		putString(key, value);
	}

	// ------------------------------------------------------------------------

	protected String getRegistryInstallFolder(String subFolder, String executableName) {

		String path = Discoverer.getRegistryInstallFolder(executableName, subFolder, REG_SUBKEY, REG_NAME);
		return path;
	}

	// ------------------------------------------------------------------------
}
