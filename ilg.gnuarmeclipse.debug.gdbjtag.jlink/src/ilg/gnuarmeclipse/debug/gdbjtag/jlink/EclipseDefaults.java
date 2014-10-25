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

import org.eclipse.core.runtime.Platform;

public class EclipseDefaults {

	// ------------------------------------------------------------------------

	private static final String GDB_SERVER_EXECUTABLE = "gdb.server.executable.default";
	private static final String GDB_CLIENT_EXECUTABLE = "gdb.client.executable.default";

	// ------------------------------------------------------------------------

	private static String getProperty(String name, String defValue) {

		return Platform.getPreferencesService().getString(Activator.PLUGIN_ID,
				name, defValue, null);
	}

	public static String getGdbServerExecutable(String defValue) {
		return getProperty(GDB_SERVER_EXECUTABLE, defValue);
	}

	public static String getGdbClientExecutable(String defValue) {
		return getProperty(GDB_CLIENT_EXECUTABLE, defValue);
	}

	// ------------------------------------------------------------------------
}
