/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.openocd;

import org.eclipse.core.resources.IProject;

import ilg.gnumcueclipse.core.EclipseUtils;

// Resolves variables from persistent preferences.

public class DynamicVariableResolver {

	public static final String VARIABLE_OPENOCD_EXECUTABLE = "openocd_executable";
	public static final String VARIABLE_OPENOCD_PATH = "openocd_path";

	public static String resolveAll(String input, IProject project) {

		String macros[] = { VARIABLE_OPENOCD_EXECUTABLE, VARIABLE_OPENOCD_PATH };
		String preferences[] = { PersistentPreferences.EXECUTABLE_NAME, PersistentPreferences.INSTALL_FOLDER };
		String defaults[] = { PersistentPreferences.EXECUTABLE_NAME_DEFAULT,
				PersistentPreferences.INSTALL_FOLDER_DEFAULT };

		String output = input;
		if (input.indexOf("${") >= 0) {
			for (int i = 0; i < macros.length; i++) {
				if (input.indexOf("${" + macros[i] + "}") >= 0) {
					String tmp = EclipseUtils.getPreferenceValueForId(Activator.PLUGIN_ID, preferences[i], defaults[i],
							project);

					output = output.replaceAll("\\$\\{" + macros[i] + "\\}", tmp);
				}
			}
		}
		return output;
	}

}
