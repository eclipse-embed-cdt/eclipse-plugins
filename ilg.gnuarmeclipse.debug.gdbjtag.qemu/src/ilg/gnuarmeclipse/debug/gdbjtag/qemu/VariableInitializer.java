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

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.WindowsRegistry;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

public class VariableInitializer implements IValueVariableInitializer {

	// ------------------------------------------------------------------------

	static final String QEMU_VARIABLE_NAME = "qemu_executable";
	static final String QEMU_PATH = "qemu_path";

	static final String UNDEFINED_PATH = "undefined_path";

	private static final String LOCATION = "HKEY_LOCAL_MACHINE\\SOFTWARE\\GNU ARM Eclipse\\QEMU";
	private static final String KEY = "InstallFolder";

	// ------------------------------------------------------------------------

	@Override
	public void initialize(IValueVariable variable) {

		String value;

		if (QEMU_VARIABLE_NAME.equals(variable.getName())) {

			value = ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_NAME;
			variable.setValue(value);

		} else if (QEMU_PATH.equals(variable.getName())) {

			if (EclipseUtils.isWindows()) {
				value = WindowsRegistry.query(LOCATION, KEY);
				if (value == null) {
					value = UNDEFINED_PATH;
				}
			} else if (EclipseUtils.isLinux()) {
				value = "/usr/bin";
			} else if (EclipseUtils.isMacOSX()) {
				value = "/opt/local/bin";
			} else {
				value = UNDEFINED_PATH;
			}

			variable.setValue(value);
		}
	}

	// ------------------------------------------------------------------------
}
