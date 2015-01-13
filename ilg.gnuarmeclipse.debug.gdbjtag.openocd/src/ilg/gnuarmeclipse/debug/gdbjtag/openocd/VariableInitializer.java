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

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.WindowsRegistry;

import java.io.File;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

public class VariableInitializer implements IValueVariableInitializer {

	// ------------------------------------------------------------------------

	static final String OPENOCD_VARIABLE_NAME = "openocd_executable";
	static final String OPENOCD_PATH = "openocd_path";

	static final String OSX_APPLICATIONS_PATH = "/Applications/GNU ARM Eclipse/OpenOCD/";
	static final String OSX_MACPORTS_PATH = "/opt/local/bin/";

	static final String LINUX_OPT_PATH = "/opt/gnuarmeclipse/openocd/bin/";
	static final String LINUX_LOCAL_PATH = "/usr/local/bin/";
	static final String LINUX_PATH = "/usr/bin/";

	static final String UNDEFINED_PATH = "undefined_path";

	private static final String LOCATION = "HKEY_LOCAL_MACHINE\\SOFTWARE\\GNU ARM Eclipse\\OpenOCD";
	private static final String KEY = "InstallFolder";

	// ------------------------------------------------------------------------

	@Override
	public void initialize(IValueVariable variable) {

		String value;

		if (OPENOCD_VARIABLE_NAME.equals(variable.getName())) {

			value = ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_NAME;
			variable.setValue(value);

		} else if (OPENOCD_PATH.equals(variable.getName())) {

			if (EclipseUtils.isWindows()) {
				value = WindowsRegistry.query(LOCATION, KEY);
				if (value == null) {
					value = UNDEFINED_PATH;
				}
			} else if (EclipseUtils.isLinux()) {
				if (isExecutablePresent(LINUX_OPT_PATH, "openocd")) {
					value = LINUX_OPT_PATH;
				} else if (isExecutablePresent(LINUX_LOCAL_PATH, "openocd")) {
					value = LINUX_LOCAL_PATH;
				} else if (isExecutablePresent(LINUX_PATH, "openocd")) {
					value = LINUX_PATH;
				} else {
					value = UNDEFINED_PATH;
				}
			} else if (EclipseUtils.isMacOSX()) {
				if (isExecutablePresent(OSX_APPLICATIONS_PATH, "openocd")) {
					value = OSX_APPLICATIONS_PATH;
				} else if (isExecutablePresent(OSX_MACPORTS_PATH, "openocd")) {
					value = OSX_MACPORTS_PATH;
				} else {
					value = UNDEFINED_PATH;
				}
			} else {
				value = UNDEFINED_PATH;
			}

			variable.setValue(value);
		}
	}

	private boolean isExecutablePresent(String pathBase, String name) {

		File folder = new File(pathBase);
		if (!folder.isDirectory()) {
			return false;
		}

		// Assume pathBase ends with separator
		String path = pathBase + name;
		File file = new File(path);
		if (!file.isFile()) {
			return false;
		}

		return true;
	}

	// ------------------------------------------------------------------------
}
