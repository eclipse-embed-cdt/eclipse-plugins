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

import ilg.gnuarmeclipse.core.EclipseUtils;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

public class GdbServerVariableInitializer implements IValueVariableInitializer {

	static final String VARIABLE_JLINK_GDBSERVER = "jlink_gdbserver";
	static final String VARIABLE_JLINK_PATH = "jlink_path";

	static final String KEY_JLINK_GDBSERVER = "jlink_gdbserver.default";
	static final String KEY_JLINK_PATH = "jlink_path.default";

	static final String UNDEFINED_PATH = "undefined_path";

	@Override
	public void initialize(IValueVariable variable) {

		String value;

		if (VARIABLE_JLINK_GDBSERVER.equals(variable.getName())) {

			value = Platform.getPreferencesService().getString(
					Activator.PLUGIN_ID, "jlink_gdbserver.default", null, null);
			if (value == null) {
				if (EclipseUtils.isWindows()) {
					value = ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_NAME_WINDOWS;
				} else if (EclipseUtils.isLinux()) {
					value = ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_NAME_LINUX;
				} else if (EclipseUtils.isMacOSX()) {
					value = ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_NAME_MAC;
				} else {
					value = ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_NAME;
				}
			}
			variable.setValue(value);

		} else if (VARIABLE_JLINK_PATH.equals(variable.getName())) {

			value = Platform.getPreferencesService().getString(
					Activator.PLUGIN_ID, "jlink_path.default", null, null);
			if (value == null) {
				if (EclipseUtils.isWindows()) {
					value = "C:\\Program Files\\SEGGER\\JLinkARM_Vxxx";
				} else if (EclipseUtils.isLinux()) {
					value = "/usr/bin";
				} else if (EclipseUtils.isMacOSX()) {
					value = "/Applications/SEGGER/JLink";
				} else {
					value = UNDEFINED_PATH;
				}
			}
			variable.setValue(value);
		}
	}

}
