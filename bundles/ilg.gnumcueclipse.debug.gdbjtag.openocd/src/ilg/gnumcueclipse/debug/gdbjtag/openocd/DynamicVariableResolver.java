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

	public static final String VARIABLE_EXECUTABLE = "openocd_executable";
	public static final String VARIABLE_PATH = "openocd_path";

	static String macros[] = { VARIABLE_EXECUTABLE, VARIABLE_PATH };
	static String preferences[] = { PersistentPreferences.EXECUTABLE_NAME, PersistentPreferences.INSTALL_FOLDER };
	static String defaults[] = { "none", "/undefined_path" };

	public static String resolveAll(String input, IProject project) {

		String output = input;
		if (input.indexOf("${") >= 0) {
			for (int i = 0; i < macros.length; i++) {
				if (input.indexOf("${" + macros[i] + "}") >= 0) {
					String tmp = EclipseUtils.getPreferenceValueForId(Activator.PLUGIN_ID, preferences[i], defaults[i],
							project);

					output = output.replaceAll("[$][{]" + macros[i] + "[}]", tmp);
				}
			}
		}
		return output;
	}

}
