/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *     Jonathan Seroussi - Jumper Virtual Lab adjustments
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.jumper;

import java.util.regex.Matcher;

import org.eclipse.core.resources.IProject;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.debug.gdbjtag.jumper.preferences.PersistentPreferences;

// Resolves variables from persistent preferences.

public class DynamicVariableResolver {

	public static final String VARIABLE_EXECUTABLE = "jumper_executable";
	public static final String VARIABLE_PATH = "jumper_path";

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

					// The replacer gives a special meaning to '\' and '$'; to
					// prevent this
					// the string must be quoted.
					tmp = Matcher.quoteReplacement(tmp);
					output = output.replaceAll("[$][{]" + macros[i] + "[}]", tmp);
				}
			}
		}
		return output;
	}

}
