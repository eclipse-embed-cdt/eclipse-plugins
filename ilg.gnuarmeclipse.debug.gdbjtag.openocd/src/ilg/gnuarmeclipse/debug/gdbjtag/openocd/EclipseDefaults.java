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

import org.eclipse.core.runtime.Platform;

public class EclipseDefaults {

	// ------------------------------------------------------------------------

	private static final String GDB_SERVER_EXECUTABLE = "gdb.server.executable.default";
	private static final String GDB_CLIENT_EXECUTABLE = "gdb.client.executable.default";

	private static final String OPENOCD_CONFIG = "openocd.config.default";

	private static final String OPENOCD_EXECUTABLE = "openocd_executable.default";
	private static final String OPENOCD_PATH = "openocd_path.default";

	private static final String TAB_MAIN_CHECK_PROGRAM = "tab.main.checkProgram";
	private static final boolean TAB_MAIN_CHECK_PROGRAM_DEFAULT = false;

	// ------------------------------------------------------------------------

	private static String getProperty(String name, String defValue) {

		return Platform.getPreferencesService().getString(Activator.PLUGIN_ID,
				name, defValue, null);
	}

	private static boolean getProperty(String name, boolean defValue) {

		return Platform.getPreferencesService().getBoolean(Activator.PLUGIN_ID,
				name, defValue, null);
	}

	public static String getGdbServerExecutable(String defValue) {
		return getProperty(GDB_SERVER_EXECUTABLE, defValue);
	}

	public static String getGdbClientExecutable(String defValue) {
		return getProperty(GDB_CLIENT_EXECUTABLE, defValue);
	}

	public static String getOpenocdConfig(String defValue) {
		return getProperty(OPENOCD_CONFIG, defValue);
	}

	public static boolean getTabMainCheckProgram(boolean defValue) {
		return getProperty(TAB_MAIN_CHECK_PROGRAM, defValue);
	}

	public static boolean getTabMainCheckProgram() {
		return getProperty(TAB_MAIN_CHECK_PROGRAM,
				TAB_MAIN_CHECK_PROGRAM_DEFAULT);
	}

	public static String getOpenocdExecutable() {
		return getProperty(OPENOCD_EXECUTABLE, null);
	}

	public static String getOpenocdPath() {
		return getProperty(OPENOCD_PATH, null);
	}

	// ------------------------------------------------------------------------
}
