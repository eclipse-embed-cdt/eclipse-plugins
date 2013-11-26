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

package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.cdt.core.templateengine.SharedDefaults;

public class SharedStorage {

	public static final String FLASH_DEVICE_NAME = Activator.PLUGIN_ID
			+ ".flashDeviceName";
	public static final String GDB_CLIENT_EXECUTABLE = Activator.PLUGIN_ID
			+ ".gdbClientExecutable";

	// ----- getter & setter --------------------------------------------------
	private static String getValueForId(String id, String defaultValue) {
		String value = SharedDefaults.getInstance().getSharedDefaultsMap()
				.get(id);

		if (value == null)
			value = "";

		value = value.trim();
		if (value.length() == 0 && defaultValue != null)
			return defaultValue.trim();

		return value;
	}

	private static void putValueForId(String id, String value){

		SharedDefaults.getInstance().getSharedDefaultsMap()
		.put(id, value.trim());
	}
	
	// ----- flash device id --------------------------------------------------
	public static String getFlashDeviceName(String defaultValue) {

		return getValueForId(FLASH_DEVICE_NAME, defaultValue);
	}

	public static void putFlashDeviceName(String value) {

		putValueForId(FLASH_DEVICE_NAME, value);
	}

	// ----- gdb client executable --------------------------------------------
	public static String getGdbClientExecutable(String defaultValue) {

		return getValueForId(GDB_CLIENT_EXECUTABLE, defaultValue);
	}
	
	public static void putGdbClientExecutable(String value){

		putValueForId(GDB_CLIENT_EXECUTABLE, value);
	}

	// ----- update -----------------------------------------------------------
	public static void update() {

		SharedDefaults.getInstance().updateShareDefaultsMap(
				SharedDefaults.getInstance().getSharedDefaultsMap());
	}
}
