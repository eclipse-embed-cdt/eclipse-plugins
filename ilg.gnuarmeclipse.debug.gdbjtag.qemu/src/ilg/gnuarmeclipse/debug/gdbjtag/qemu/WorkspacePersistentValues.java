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

package ilg.gnuarmeclipse.debug.gdbjtag.qemu;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class WorkspacePersistentValues {

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
	public static final String GDB_QEMU = "gdb.qemu.";

	public static final String GDB_QEMU_DO_INITIAL_RESET = GDB_QEMU
			+ "doInitialReset";
	// public static final String GDB_QEMU_INITIAL_RESET_TYPE = GDB_QEMU
	// + "initialReset.type";
	public static final String GDB_QEMU_INIT_OTHER = GDB_QEMU + "init.other";

	public static final String GDB_QEMU_ENABLE_SEMIHOSTING = GDB_QEMU
			+ "enableSemihosting";

	// Run Commands
	public static final String GDB_QEMU_DO_PRERUN_RESET = GDB_QEMU
			+ "doPreRunReset";
	// public static final String GDB_QEMU_PRERUN_RESET_TYPE = GDB_QEMU
	// + "preRunReset.type";

	public static final String GDB_QEMU_PRERUN_OTHER = GDB_QEMU
			+ "preRun.other";

	// ----- getter -----------------------------------------------------------
	private static String getValueForId(String id, String defaultValue) {

		// Access the instanceScope
		Preferences preferences = InstanceScope.INSTANCE
				.getNode(Activator.PLUGIN_ID);

		String value;
		value = preferences.get(id, null);
		// System.out.println("Value of " + id + " is " + value);

		if (value == null) {
			value = "";
		} else {
			value = value.trim();
		}

		if (value.isEmpty() && defaultValue != null)
			return defaultValue.trim();

		return value;
	}

	// ----- setter -----------------------------------------------------------
	private static void putValueForId(String id, String value) {

		value = value.trim();

		// Access the instanceScope
		Preferences preferences = InstanceScope.INSTANCE
				.getNode(Activator.PLUGIN_ID);
		preferences.put(id, value);
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

	// ----- QEMU do initial reset -----------------------------------------
	public static boolean getQemuDoInitialReset(boolean defaultValue) {

		return Boolean.valueOf(getValueForId(GDB_QEMU_DO_INITIAL_RESET,
				Boolean.toString(defaultValue)));
	}

	public static void putQemuDoInitialReset(boolean value) {

		putValueForId(GDB_QEMU_DO_INITIAL_RESET, Boolean.toString(value));
	}

	// ----- QEMU initial reset type ---------------------------------------
	// public static String getQemuInitialResetType(String defaultValue) {
	//
	// return getValueForId(GDB_QEMU_INITIAL_RESET_TYPE, defaultValue);
	// }
	//
	// public static void putQemuInitialResetType(String value) {
	//
	// putValueForId(GDB_QEMU_INITIAL_RESET_TYPE, value);
	// }

	// ----- QEMU enable semihosting ---------------------------------------
	public static boolean getQemuEnableSemihosting(boolean defaultValue) {

		return Boolean.valueOf(getValueForId(GDB_QEMU_ENABLE_SEMIHOSTING,
				Boolean.toString(defaultValue)));
	}

	public static void putQemuEnableSemihosting(boolean value) {

		putValueForId(GDB_QEMU_ENABLE_SEMIHOSTING, Boolean.toString(value));
	}

	// ----- QEMU init other -----------------------------------------------
	public static String getQemuInitOther(String defaultValue) {

		return getValueForId(GDB_QEMU_INIT_OTHER, defaultValue);
	}

	public static void putQemuInitOther(String value) {

		putValueForId(GDB_QEMU_INIT_OTHER, value);
	}

	// ----- QEMU do prerun reset ------------------------------------------
	public static boolean getQemuDoPreRunReset(boolean defaultValue) {

		return Boolean.valueOf(getValueForId(GDB_QEMU_DO_PRERUN_RESET,
				Boolean.toString(defaultValue)));
	}

	public static void putQemuDoPreRunReset(boolean value) {

		putValueForId(GDB_QEMU_DO_PRERUN_RESET, Boolean.toString(value));
	}

	// ----- QEMU prerun reset type ----------------------------------------
	// public static String getQemuPreRunResetType(String defaultValue) {
	//
	// return getValueForId(GDB_QEMU_PRERUN_RESET_TYPE, defaultValue);
	// }
	//
	// public static void putQemuPreRunResetType(String value) {
	//
	// putValueForId(GDB_QEMU_PRERUN_RESET_TYPE, value);
	// }

	// ----- QEMU init other -----------------------------------------
	public static String getQemuPreRunOther(String defaultValue) {

		return getValueForId(GDB_QEMU_PRERUN_OTHER, defaultValue);
	}

	public static void putQemuPreRunOther(String value) {

		putValueForId(GDB_QEMU_PRERUN_OTHER, value);
	}

	// ----- flush -----------------------------------------------------------
	public static void flush() {

		try {
			InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

	}
}
