/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.BackingStoreException;

public class EclipseDefaults {

	// ------------------------------------------------------------------------

	// These values are deprecated. Use the definitions in PersistentValues.
	private static final String GDB_SERVER_EXECUTABLE = "gdb.server.executable.default";
	private static final String JLINK_INTRFACE = "interface.default";
	private static final String JLINK_ENABLE_SEMIHOSTING = "enableSemihosting.default";
	private static final String JLINK_ENABLE_SWO = "enableSwo.default";

	private static final String JLINK_GDBSERVER = "jlink_gdbserver.default";
	private static final String JLINK_PATH = "jlink_path.default";

	private static final String GDB_CLIENT_EXECUTABLE = "gdb.client.executable.default";

	// private static final String TAB_MAIN_CHECK_PROGRAM =
	// "tab.main.checkProgram";
	// private static final boolean TAB_MAIN_CHECK_PROGRAM_DEFAULT = false;

	// ------------------------------------------------------------------------

	private static String getString(String name, String defValue) {

		return DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID).get(name,
				defValue);
	}

	private static boolean getBoolean(String name, boolean defValue) {

		return DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID).getBoolean(
				name, defValue);
	}

	private static int getInt(String name, int defValue) {

		return DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID).getInt(name,
				defValue);
	}

	// ------------------------------------------------------------------------

	public static String getGdbServerExecutable(String defValue) {
		String value = getString(PersistentValues.GDB_SERVER_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return getString(GDB_SERVER_EXECUTABLE, defValue);
	}

	public static String getGdbServerInterface(String defValue) {
		String value = getString(PersistentValues.GDB_SERVER_INTERFACE, null);
		if (value != null) {
			return value;
		}
		return getString(JLINK_INTRFACE, defValue);
	}

	public static boolean getJLinkEnableSemihosting(boolean defValue) {

		try {
			if (DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID).nodeExists(
					PersistentValues.GDB_JLINK_ENABLE_SEMIHOSTING)) {
				return getBoolean(
						PersistentValues.GDB_JLINK_ENABLE_SEMIHOSTING, defValue);
			}
		} catch (BackingStoreException e) {
			;
		}
		return getBoolean(JLINK_ENABLE_SEMIHOSTING, defValue);
	}

	public static boolean getJLinkEnableSwo(boolean defValue) {
		try {
			if (DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID).nodeExists(
					PersistentValues.GDB_JLINK_ENABLE_SWO)) {
				return getBoolean(PersistentValues.GDB_JLINK_ENABLE_SWO,
						defValue);
			}
		} catch (BackingStoreException e) {
			;
		}
		return getBoolean(JLINK_ENABLE_SWO, defValue);
	}

	public static String getGdbClientExecutable(String defValue) {
		String value = getString(PersistentValues.GDB_CLIENT_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return getString(GDB_CLIENT_EXECUTABLE, defValue);
	}

	// ------------------------------------------------------------------------

	public static boolean getTabMainCheckProgram() {
		return getBoolean(PersistentValues.TAB_MAIN_CHECK_PROGRAM,
				PersistentValues.TAB_MAIN_CHECK_PROGRAM_DEFAULT);
	}

	// ------------------------------------------------------------------------

	public static String getJLinkGdbServer() {
		String value = getString(PersistentValues.JLINK_GDBSERVER, null);
		if (value != null) {
			return value;
		}
		return getString(JLINK_GDBSERVER, null);
	}

	public static String getJLinkPath() {
		String value = getString(PersistentValues.JLINK_PATH, null);
		if (value != null) {
			return value;
		}
		return getString(JLINK_PATH, null);
	}

	// ------------------------------------------------------------------------

	public static boolean getGdbServerDoStart(boolean defaultValue) {
		return getBoolean(PersistentValues.GDB_SERVER_DO_START, defaultValue);
	}

	public static String getGdbServerEndianness(String defaultValue) {
		return getString(PersistentValues.GDB_SERVER_ENDIANNESS, defaultValue);
	}

	public static String getGdbServerConnection(String defaultValue) {
		return getString(PersistentValues.GDB_SERVER_CONNECTION, defaultValue);
	}

	public static String getGdbServerConnectionAddress(String defaultValue) {
		return getString(PersistentValues.GDB_SERVER_CONNECTION_ADDRESS,
				defaultValue);
	}

	public static String getGdbServerInitialSpeed(String defaultValue) {
		return getString(PersistentValues.GDB_SERVER_INITIAL_SPEED,
				defaultValue);
	}

	public static String getGdbServerOtherOptions(String defaultValue) {
		return getString(PersistentValues.GDB_SERVER_OTHER_OPTIONS,
				defaultValue);
	}

	public static String getGdbClientOtherOptions(String defaultValue) {
		return getString(PersistentValues.GDB_CLIENT_OTHER_OPTIONS,
				defaultValue);
	}

	public static String getGdbClientCommands(String defaultValue) {
		return getString(PersistentValues.GDB_CLIENT_COMMANDS, defaultValue);
	}

	public static boolean getJLinkDoInitialReset(boolean defaultValue) {
		return getBoolean(PersistentValues.GDB_JLINK_DO_INITIAL_RESET,
				defaultValue);
	}

	public static String getJLinkInitialResetType(String defaultValue) {
		return getString(PersistentValues.GDB_JLINK_INITIAL_RESET_TYPE,
				defaultValue);
	}

	public static int getJLinkInitialResetSpeed(int defaultValue) {
		return getInt(PersistentValues.GDB_JLINK_INITIAL_RESET_SPEED,
				defaultValue);
	}

	public static String getJLinkSpeed(String defaultValue) {
		return getString(PersistentValues.GDB_JLINK_SPEED, defaultValue);
	}

	public static boolean getJLinkEnableFlashBreakpoints(boolean defaultValue) {
		return getBoolean(PersistentValues.GDB_JLINK_ENABLE_FLASH_BREAKPOINTS,
				defaultValue);
	}

	public static boolean getJLinkSemihostingTelnet(boolean defaultValue) {
		return getBoolean(PersistentValues.GDB_JLINK_SEMIHOSTING_TELNET,
				defaultValue);
	}

	public static boolean getJLinkSemihostingClient(boolean defaultValue) {
		return getBoolean(PersistentValues.GDB_JLINK_SEMIHOSTING_CLIENT,
				defaultValue);
	}

	public static int getJLinkSwoEnableTargetCpuFreq(int defaultValue) {
		return getInt(PersistentValues.GDB_JLINK_SWO_ENABLE_TARGET_CPU_FREQ,
				defaultValue);
	}

	public static int getJLinkSwoEnableTargetSwoFreq(int defaultValue) {
		return getInt(PersistentValues.GDB_JLINK_SWO_ENABLE_TARGET_SWO_FREQ,
				defaultValue);
	}

	public static String getJLinkSwoEnableTargetPortMask(String defaultValue) {
		return getString(
				PersistentValues.GDB_JLINK_SWO_ENABLE_TARGET_PORT_MASK,
				defaultValue);
	}

	public static String getJLinkInitOther(String defaultValue) {
		return getString(PersistentValues.GDB_JLINK_INIT_OTHER, defaultValue);
	}

	public static boolean getJLinkDebugInRam(boolean defaultValue) {
		return getBoolean(PersistentValues.GDB_JLINK_DO_DEBUG_IN_RAM,
				defaultValue);
	}

	public static boolean getJLinkDoPreRunReset(boolean defaultValue) {
		return getBoolean(PersistentValues.GDB_JLINK_DO_PRERUN_RESET,
				defaultValue);
	}

	public static String getJLinkPreRunResetType(String defaultValue) {
		return getString(PersistentValues.GDB_JLINK_PRERUN_RESET_TYPE,
				defaultValue);
	}

	public static String getJLinkPreRunOther(String defaultValue) {
		return getString(PersistentValues.GDB_JLINK_PRERUN_OTHER, defaultValue);
	}

	// ------------------------------------------------------------------------
}
