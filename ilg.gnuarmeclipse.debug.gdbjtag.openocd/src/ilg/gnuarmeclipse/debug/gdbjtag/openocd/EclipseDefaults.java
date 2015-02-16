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

package ilg.gnuarmeclipse.debug.gdbjtag.openocd;

import org.eclipse.core.runtime.preferences.DefaultScope;

public class EclipseDefaults {

	// ------------------------------------------------------------------------

	// These values are deprecated. Use the definitions in PersistentValues.
	private static final String GDB_SERVER_EXECUTABLE = "gdb.server.executable.default";
	private static final String GDB_CLIENT_EXECUTABLE = "gdb.client.executable.default";

	private static final String OPENOCD_CONFIG = "openocd.config.default";

	private static final String OPENOCD_EXECUTABLE = "openocd_executable.default";
	private static final String OPENOCD_PATH = "openocd_path.default";

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

	// ------------------------------------------------------------------------

	public static String getGdbServerExecutable(String defValue) {
		String value = getString(PersistentValues.GDB_SERVER_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return getString(GDB_SERVER_EXECUTABLE, defValue);
	}

	public static String getGdbClientExecutable(String defValue) {
		String value = getString(PersistentValues.GDB_CLIENT_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return getString(GDB_CLIENT_EXECUTABLE, defValue);
	}

	public static String getOpenocdConfig(String defValue) {
		String value = getString(PersistentValues.GDB_SERVER_OTHER_OPTIONS,
				null);
		if (value != null) {
			return value;
		}
		return getString(OPENOCD_CONFIG, defValue);
	}

	// ------------------------------------------------------------------------

	public static boolean getTabMainCheckProgram() {
		return getBoolean(PersistentValues.TAB_MAIN_CHECK_PROGRAM,
				PersistentValues.TAB_MAIN_CHECK_PROGRAM_DEFAULT);
	}

	// ------------------------------------------------------------------------

	public static String getOpenocdExecutable() {
		String value = getString(PersistentValues.OPENOCD_EXECUTABLE, null);
		if (value != null) {
			return value;
		}
		return getString(OPENOCD_EXECUTABLE, null);
	}

	public static String getOpenocdPath() {
		String value = getString(PersistentValues.OPENOCD_PATH, null);
		if (value != null) {
			return value;
		}
		return getString(OPENOCD_PATH, null);
	}

	// ------------------------------------------------------------------------
}
