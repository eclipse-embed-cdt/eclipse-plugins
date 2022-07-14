/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.qemu.core;

import java.util.regex.Matcher;

import org.eclipse.core.resources.IProject;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.debug.gdbjtag.qemu.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.core.Activator;

// Resolves variables from persistent preferences.

public class DynamicVariableResolver {

	@Deprecated
	public static final String VARIABLE_EXECUTABLE = "qemu_executable";
	@Deprecated
	public static final String VARIABLE_PATH = "qemu_path";

	static String executable_macros[] = new String[PersistentPreferences.architectures.length + 1];
	static String paths_macros[] = new String[PersistentPreferences.architectures.length + 1];

	static String executable_preferences[] = new String[PersistentPreferences.architectures.length + 1];
	static String paths_preferences[] = new String[PersistentPreferences.architectures.length + 1];

	public static String resolveAll(String input, IProject project) {

		int i;
		for (i = 0; i < PersistentPreferences.architectures.length; ++i) {
			executable_macros[i] = "qemu_" + PersistentPreferences.architectures[i] + "_executable";
			paths_macros[i] = "qemu_" + PersistentPreferences.architectures[i] + "_path";

			executable_preferences[i] = PersistentPreferences.architectures[i] + "." + PersistentPreferences.EXECUTABLE_NAME;
			paths_preferences[i] = PersistentPreferences.architectures[i] + "." + PersistentPreferences.INSTALL_FOLDER;
		}

		// Compatibility definitions.
		executable_macros[i] = "qemu_executable";
		paths_macros[i] = "qemu_path";
		executable_preferences[i] = PersistentPreferences.EXECUTABLE_NAME;
		paths_preferences[i] = PersistentPreferences.INSTALL_FOLDER;

		String output = input;
		if (input.indexOf("${") >= 0) {
			for (int j = 0; j < executable_macros.length; j++) {
				if (input.indexOf("${" + executable_macros[j] + "}") >= 0) {
					String tmp = EclipseUtils.getPreferenceValueForId(Activator.PLUGIN_ID, executable_preferences[j],
							"none", project);

					// The replacer gives a special meaning to '\' and '$'; to
					// prevent this
					// the string must be quoted.
					tmp = Matcher.quoteReplacement(tmp);
					output = output.replaceAll("[$][{]" + executable_macros[j] + "[}]", tmp);
				}
			}
			for (int j = 0; j < paths_macros.length; j++) {
				if (input.indexOf("${" + paths_macros[j] + "}") >= 0) {
					String tmp = EclipseUtils.getPreferenceValueForId(Activator.PLUGIN_ID, paths_preferences[j],
							"/undefined_path", project);

					// The replacer gives a special meaning to '\' and '$'; to
					// prevent this
					// the string must be quoted.
					tmp = Matcher.quoteReplacement(tmp);
					output = output.replaceAll("[$][{]" + paths_macros[j] + "[}]", tmp);
				}
			}
		}
		return output;
	}

}
