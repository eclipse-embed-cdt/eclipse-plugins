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

import org.eclipse.cdt.core.templateengine.SharedDefaults;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class WorkspacePreferences {

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

	// Run Commands
	public static final String GDB_OPENOCD_DO_PRERUN_RESET = GDB_OPENOCD
			+ "doPreRunReset";
	public static final String GDB_OPENOCD_PRERUN_RESET_TYPE = GDB_OPENOCD
			+ "preRunReset.type";

	public static final String GDB_OPENOCD_PRERUN_OTHER = GDB_OPENOCD
			+ "preRun.other";

	// ----- getter & setter --------------------------------------------------
	private static String getValueForId(String id, String defaultValue) {

		// Access the instanceScope
		Preferences preferences = InstanceScope.INSTANCE
				.getNode(Activator.PLUGIN_ID);

		String value;
		// preferences.get(id, defaultValue);
		value = preferences.get(id, null);
		// System.out.println("Value of " + id + " is " + value);

		if (value != null) {
			return value;
		}

		// Keep this for compatibility
		id = Activator.PLUGIN_ID + "." + id;

		value = SharedDefaults.getInstance().getSharedDefaultsMap().get(id);

		if (value == null)
			value = "";

		value = value.trim();
		if (value.length() == 0 && defaultValue != null)
			return defaultValue.trim();

		return value;
	}

	private static void putValueForId(String id, String value) {

		value = value.trim();

		// Access the instanceScope
		Preferences preferences = InstanceScope.INSTANCE
				.getNode(Activator.PLUGIN_ID);
		preferences.put(id, value);

		if (false) {
			// Access the shared preferences
			String sharedId = Activator.PLUGIN_ID + "." + id;

			SharedDefaults.getInstance().getSharedDefaultsMap()
					.put(sharedId, value);
		}
	}

	// ----- gdb server doStart -----------------------------------------------
	public static boolean getGdbServerDoStart(boolean defaultValue) {

		return Boolean.valueOf(getValueForId(GDB_SERVER_DO_START,
				Boolean.toString(defaultValue)));
	}

	public static void putGdbServerDoStart(boolean value) {

		putValueForId(GDB_SERVER_DO_START, Boolean.toString(value));
	}

	// ----- gdb server executable --------------------------------------------
	public static String getGdbServerExecutable(String defaultValue) {

		return getValueForId(GDB_SERVER_EXECUTABLE, defaultValue);
	}

	public static void putGdbServerExecutable(String value) {

		putValueForId(GDB_SERVER_EXECUTABLE, value);
	}

	// ----- gdb server other options -----------------------------------------
	public static String getGdbServerOtherOptions(String defaultValue) {

		return getValueForId(GDB_SERVER_OTHER_OPTIONS, defaultValue);
	}

	public static void putGdbServerOtherOptions(String value) {

		putValueForId(GDB_SERVER_OTHER_OPTIONS, value);
	}

	// ----- gdb client executable --------------------------------------------
	public static String getGdbClientExecutable(String defaultValue) {

		return getValueForId(GDB_CLIENT_EXECUTABLE, defaultValue);
	}

	public static void putGdbClientExecutable(String value) {

		putValueForId(GDB_CLIENT_EXECUTABLE, value);
	}

	// ----- gdb client other options -----------------------------------------
	public static String getGdbClientOtherOptions(String defaultValue) {

		return getValueForId(GDB_CLIENT_OTHER_OPTIONS, defaultValue);
	}

	public static void putGdbClientOtherOptions(String value) {

		putValueForId(GDB_CLIENT_OTHER_OPTIONS, value);
	}

	// ----- gdb client commands ----------------------------------------------
	public static String getGdbClientCommands(String defaultValue) {

		return getValueForId(GDB_CLIENT_COMMANDS, defaultValue);
	}

	public static void putGdbClientCommands(String value) {

		putValueForId(GDB_CLIENT_COMMANDS, value);
	}

	// ----- OpenOCD do initial reset -----------------------------------------
	public static boolean getOpenOCDDoInitialReset(boolean defaultValue) {

		return Boolean.valueOf(getValueForId(GDB_OPENOCD_DO_INITIAL_RESET,
				Boolean.toString(defaultValue)));
	}

	public static void putOpenOCDDoInitialReset(boolean value) {

		putValueForId(GDB_OPENOCD_DO_INITIAL_RESET, Boolean.toString(value));
	}

	// ----- OpenOCD initial reset type ---------------------------------------
	public static String getOpenOCDInitialResetType(String defaultValue) {

		return getValueForId(GDB_OPENOCD_INITIAL_RESET_TYPE, defaultValue);
	}

	public static void putOpenOCDInitialResetType(String value) {

		putValueForId(GDB_OPENOCD_INITIAL_RESET_TYPE, value);
	}

	// ----- OpenOCD enable semihosting ---------------------------------------
	public static boolean getOpenOCDEnableSemihosting(boolean defaultValue) {

		return Boolean.valueOf(getValueForId(GDB_OPENOCD_ENABLE_SEMIHOSTING,
				Boolean.toString(defaultValue)));
	}

	public static void putOpenOCDEnableSemihosting(boolean value) {

		putValueForId(GDB_OPENOCD_ENABLE_SEMIHOSTING, Boolean.toString(value));
	}

	// ----- OpenOCD init other -----------------------------------------------
	public static String getOpenOCDInitOther(String defaultValue) {

		return getValueForId(GDB_OPENOCD_INIT_OTHER, defaultValue);
	}

	public static void putOpenOCDInitOther(String value) {

		putValueForId(GDB_OPENOCD_INIT_OTHER, value);
	}

	// ----- OpenOCD do prerun reset ------------------------------------------
	public static boolean getOpenOCDDoPreRunReset(boolean defaultValue) {

		return Boolean.valueOf(getValueForId(GDB_OPENOCD_DO_PRERUN_RESET,
				Boolean.toString(defaultValue)));
	}

	public static void putOpenOCDDoPreRunReset(boolean value) {

		putValueForId(GDB_OPENOCD_DO_PRERUN_RESET, Boolean.toString(value));
	}

	// ----- OpenOCD prerun reset type ----------------------------------------
	public static String getOpenOCDPreRunResetType(String defaultValue) {

		return getValueForId(GDB_OPENOCD_PRERUN_RESET_TYPE, defaultValue);
	}

	public static void putOpenOCDPreRunResetType(String value) {

		putValueForId(GDB_OPENOCD_PRERUN_RESET_TYPE, value);
	}

	// ----- OpenOCD init other -----------------------------------------
	public static String getOpenOCDPreRunOther(String defaultValue) {

		return getValueForId(GDB_OPENOCD_PRERUN_OTHER, defaultValue);
	}

	public static void putOpenOCDPreRunOther(String value) {

		putValueForId(GDB_OPENOCD_PRERUN_OTHER, value);
	}

	// ----- flush -----------------------------------------------------------
	public static void flush() {

		try {
			InstanceScope.INSTANCE
			.getNode(Activator.PLUGIN_ID).flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
		
		if (false) {
			SharedDefaults.getInstance().updateShareDefaultsMap(
					SharedDefaults.getInstance().getSharedDefaultsMap());
		}
	}
}
