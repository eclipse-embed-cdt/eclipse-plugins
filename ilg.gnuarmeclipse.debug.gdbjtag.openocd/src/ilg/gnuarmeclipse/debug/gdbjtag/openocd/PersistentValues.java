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

public class PersistentValues {

	// Tab Debugger
	// GDB Server Setup
	public static final String GDB_SERVER = "gdb.server.";

	public static final String GDB_SERVER_DO_START = GDB_SERVER + "doStart";

	public static final String GDB_SERVER_EXECUTABLE = GDB_SERVER
			+ "executable";

	public static final String GDB_SERVER_OTHER_OPTIONS = GDB_SERVER + "other";

	// GDB Client Setup
	public static final String GDB_CLIENT = "gdb.client.";

	public static final String GDB_CLIENT_EXECUTABLE = GDB_CLIENT
			+ "executable";

	public static final String GDB_CLIENT_OTHER_OPTIONS = GDB_CLIENT + "other";

	public static final String GDB_CLIENT_COMMANDS = GDB_CLIENT + "commands";

	// Tab Startup
	// Initialisation Commands
	public static final String GDB_OPENOCD = "gdb.openocd.";

	public static final String GDB_OPENOCD_DO_INITIAL_RESET = GDB_OPENOCD
			+ "doInitialReset";
	public static final String GDB_OPENOCD_INITIAL_RESET_TYPE = GDB_OPENOCD
			+ "initialReset.type";
	public static final String GDB_OPENOCD_INIT_OTHER = GDB_OPENOCD
			+ "init.other";

	public static final String GDB_OPENOCD_ENABLE_SEMIHOSTING = GDB_OPENOCD
			+ "enableSemihosting";

	public static final String GDB_OPENOCD_DO_DEBUG_IN_RAM = GDB_OPENOCD
			+ "doDebugInRam";

	// Run Commands
	public static final String GDB_OPENOCD_DO_PRERUN_RESET = GDB_OPENOCD
			+ "doPreRunReset";
	public static final String GDB_OPENOCD_PRERUN_RESET_TYPE = GDB_OPENOCD
			+ "preRunReset.type";

	public static final String GDB_OPENOCD_PRERUN_OTHER = GDB_OPENOCD
			+ "preRun.other";

	// ----- Defaults ---------------------------------------------------------
	public static final String OPENOCD_EXECUTABLE = "openocd_executable";
	public static final String OPENOCD_PATH = "openocd_path";

	public static final String TAB_MAIN_CHECK_PROGRAM = "tab.main.checkProgram";
	public static final boolean TAB_MAIN_CHECK_PROGRAM_DEFAULT = false;

	// ----- getter -----------------------------------------------------------
	private static String getValueForId(String id, String defaultValue) {

		return Platform.getPreferencesService().getString(Activator.PLUGIN_ID,
				id, defaultValue, null);
	}

	// ----- setter -----------------------------------------------------------
	private static void putWorkspaceValueForId(String id, String value) {

		value = value.trim();

		// Access the instanceScope
		Preferences preferences = InstanceScope.INSTANCE
				.getNode(Activator.PLUGIN_ID);
		preferences.put(id, value);
	}

	// ----- flush ------------------------------------------------------------
	public static void flush() {

		try {
			InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).flush();
		} catch (BackingStoreException e) {
			Activator.log(e);
		}

	}

	// ----- gdb server doStart -----------------------------------------------
	public static boolean getGdbServerDoStart(boolean defaultValue) {

		return Boolean.valueOf(getValueForId(GDB_SERVER_DO_START,
				Boolean.toString(defaultValue)));
	}

	public static void putGdbServerDoStart(boolean value) {

		putWorkspaceValueForId(GDB_SERVER_DO_START, Boolean.toString(value));
	}

	// ----- gdb server executable --------------------------------------------
	public static String getGdbServerExecutable(String defaultValue) {

		String value = getValueForId(GDB_SERVER_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return EclipseDefaults.getGdbServerExecutable(defaultValue);
	}

	public static void putGdbServerExecutable(String value) {

		putWorkspaceValueForId(GDB_SERVER_EXECUTABLE, value);
	}

	// ----- gdb server other options -----------------------------------------
	public static String getGdbServerOtherOptions(String defaultValue) {

		String value = getValueForId(GDB_SERVER_OTHER_OPTIONS, null);
		if (value != null) {
			return value;
		}
		return EclipseDefaults.getOpenocdConfig(defaultValue);
	}

	public static void putGdbServerOtherOptions(String value) {

		putWorkspaceValueForId(GDB_SERVER_OTHER_OPTIONS, value);
	}

	// ----- gdb client executable --------------------------------------------
	public static String getGdbClientExecutable(String defaultValue) {

		String value = getValueForId(GDB_CLIENT_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return EclipseDefaults.getGdbClientExecutable(defaultValue);
	}

	public static void putGdbClientExecutable(String value) {

		putWorkspaceValueForId(GDB_CLIENT_EXECUTABLE, value);
	}

	// ----- gdb client other options -----------------------------------------
	public static String getGdbClientOtherOptions(String defaultValue) {

		return getValueForId(GDB_CLIENT_OTHER_OPTIONS, defaultValue);
	}

	public static void putGdbClientOtherOptions(String value) {

		putWorkspaceValueForId(GDB_CLIENT_OTHER_OPTIONS, value);
	}

	// ----- gdb client commands ----------------------------------------------
	public static String getGdbClientCommands(String defaultValue) {

		return getValueForId(GDB_CLIENT_COMMANDS, defaultValue);
	}

	public static void putGdbClientCommands(String value) {

		putWorkspaceValueForId(GDB_CLIENT_COMMANDS, value);
	}

	// ----- OpenOCD do initial reset -----------------------------------------
	public static boolean getOpenOCDDoInitialReset(boolean defaultValue) {

		return Boolean.valueOf(getValueForId(GDB_OPENOCD_DO_INITIAL_RESET,
				Boolean.toString(defaultValue)));
	}

	public static void putOpenOCDDoInitialReset(boolean value) {

		putWorkspaceValueForId(GDB_OPENOCD_DO_INITIAL_RESET,
				Boolean.toString(value));
	}

	// ----- OpenOCD initial reset type ---------------------------------------
	public static String getOpenOCDInitialResetType(String defaultValue) {

		return getValueForId(GDB_OPENOCD_INITIAL_RESET_TYPE, defaultValue);
	}

	public static void putOpenOCDInitialResetType(String value) {

		putWorkspaceValueForId(GDB_OPENOCD_INITIAL_RESET_TYPE, value);
	}

	// ----- OpenOCD enable semihosting ---------------------------------------
	public static boolean getOpenOCDEnableSemihosting(boolean defaultValue) {

		return Boolean.valueOf(getValueForId(GDB_OPENOCD_ENABLE_SEMIHOSTING,
				Boolean.toString(defaultValue)));
	}

	public static void putOpenOCDEnableSemihosting(boolean value) {

		putWorkspaceValueForId(GDB_OPENOCD_ENABLE_SEMIHOSTING,
				Boolean.toString(value));
	}

	// ----- OpenOCD init other -----------------------------------------------
	public static String getOpenOCDInitOther(String defaultValue) {

		return getValueForId(GDB_OPENOCD_INIT_OTHER, defaultValue);
	}

	public static void putOpenOCDInitOther(String value) {

		putWorkspaceValueForId(GDB_OPENOCD_INIT_OTHER, value);
	}

	// ----- OpenOCD debug in ram ---------------------------------------------
	public static boolean getOpenOCDDebugInRam(boolean defaultValue) {

		return Boolean.valueOf(getValueForId(GDB_OPENOCD_DO_DEBUG_IN_RAM,
				Boolean.toString(defaultValue)));
	}

	public static void putOpenOCDDebugInRam(boolean value) {

		putWorkspaceValueForId(GDB_OPENOCD_DO_DEBUG_IN_RAM,
				Boolean.toString(value));
	}

	// ----- OpenOCD do prerun reset ------------------------------------------
	public static boolean getOpenOCDDoPreRunReset(boolean defaultValue) {

		return Boolean.valueOf(getValueForId(GDB_OPENOCD_DO_PRERUN_RESET,
				Boolean.toString(defaultValue)));
	}

	public static void putOpenOCDDoPreRunReset(boolean value) {

		putWorkspaceValueForId(GDB_OPENOCD_DO_PRERUN_RESET,
				Boolean.toString(value));
	}

	// ----- OpenOCD prerun reset type ----------------------------------------
	public static String getOpenOCDPreRunResetType(String defaultValue) {

		return getValueForId(GDB_OPENOCD_PRERUN_RESET_TYPE, defaultValue);
	}

	public static void putOpenOCDPreRunResetType(String value) {

		putWorkspaceValueForId(GDB_OPENOCD_PRERUN_RESET_TYPE, value);
	}

	// ----- OpenOCD init other -----------------------------------------------
	public static String getOpenOCDPreRunOther(String defaultValue) {

		return getValueForId(GDB_OPENOCD_PRERUN_OTHER, defaultValue);
	}

	public static void putOpenOCDPreRunOther(String value) {

		putWorkspaceValueForId(GDB_OPENOCD_PRERUN_OTHER, value);
	}

	// ------------------------------------------------------------------------
}
