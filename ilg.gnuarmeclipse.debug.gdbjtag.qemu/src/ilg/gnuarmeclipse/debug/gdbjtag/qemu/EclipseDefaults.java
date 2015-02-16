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

package ilg.gnuarmeclipse.debug.gdbjtag.qemu;

import org.eclipse.core.runtime.preferences.DefaultScope;

public class EclipseDefaults {

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
		return getString(PersistentValues.GDB_SERVER_EXECUTABLE, defValue);
	}

	public static String getGdbClientExecutable(String defValue) {
		return getString(PersistentValues.GDB_CLIENT_EXECUTABLE, defValue);
	}

	public static boolean getTabMainCheckProgram() {
		return getBoolean(PersistentValues.TAB_MAIN_CHECK_PROGRAM,
				PersistentValues.TAB_MAIN_CHECK_PROGRAM_DEFAULT);
	}

	public static String getQemuExecutable() {
		return getString(PersistentValues.QEMU_EXECUTABLE, null);
	}

	public static String getQemuPath() {
		return getString(PersistentValues.QEMU_PATH, null);
	}

	// ------------------------------------------------------------------------
}
