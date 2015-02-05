/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *     Ningareddy Modase - windows J-Link path
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.WindowsRegistry;

import java.io.File;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

public class VariableInitializer implements IValueVariableInitializer {

	// ------------------------------------------------------------------------

	static final String VARIABLE_JLINK_GDBSERVER = "jlink_gdbserver";
	static final String VARIABLE_JLINK_PATH = "jlink_path";

	static final String KEY_JLINK_GDBSERVER = "jlink_gdbserver.default";
	static final String KEY_JLINK_PATH = "jlink_path.default";

	static final String UNDEFINED_PATH = "undefined_path";

	private static final String LOCATION = "HKEY_CURRENT_USER\\Software\\SEGGER\\J-Link";
	private static final String KEY = "InstallPath";

	// ------------------------------------------------------------------------

	@Override
	public void initialize(IValueVariable variable) {

		String value;

		if (VARIABLE_JLINK_GDBSERVER.equals(variable.getName())) {

			value = EclipseDefaults.getJLinkGdbServer();
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

			value = EclipseDefaults.getJLinkPath();
			if (value == null) {
				if (EclipseUtils.isWindows()) {
					value = WindowsRegistry.query(LOCATION, KEY);
					if (value == null) {
						value = UNDEFINED_PATH;
					}
				} else if (EclipseUtils.isLinux()) {
					value = findMostRecentFolder("/opt/SEGGER/", "JLink",
							UNDEFINED_PATH);
				} else if (EclipseUtils.isMacOSX()) {
					value = findMostRecentFolder("/Applications/SEGGER/",
							"JLink", UNDEFINED_PATH);
				} else {
					value = UNDEFINED_PATH;
				}
			}
			variable.setValue(value);
		}
	}

	private String findMostRecentFolder(String pathBase, String name,
			String defPath) {

		String path = pathBase + name;
		File folder = new File(path);
		if (folder.isDirectory()) {
			return path;
		}

		folder = new File(pathBase);
		if (folder.isDirectory()) {

			long lastModified = 0;
			String lastName = null;
			File files[] = folder.listFiles();
			for (int i = 0; i < files.length; ++i) {
				File file = files[i];
				if (file.isDirectory()) {
					String crtName = file.getName();
					if (file.getName().startsWith(name)) {
						if (file.lastModified() > lastModified) {
							lastName = crtName;
							lastModified = file.lastModified();
						}
					}
				}
			}

			if (lastName != null) {
				return pathBase + lastName;
			}
		}

		return defPath;
	}

	// ------------------------------------------------------------------------
}
