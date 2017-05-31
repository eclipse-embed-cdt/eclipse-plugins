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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;

// Resolves variables from persistent preferences.

public class DynamicVariableResolver implements IDynamicVariableResolver {

	public static final String VARIABLE_OPENOCD_EXECUTABLE = "openocd_executable";
	public static final String VARIABLE_OPENOCD_PATH = "openocd_path";

	@Override
	public String resolveValue(IDynamicVariable variable, String argument) throws CoreException {

		String value = null;
		if (VARIABLE_OPENOCD_EXECUTABLE.equals(variable.getName())) {
			value = PersistentPreferences.getExecutableName();
		} else if (VARIABLE_OPENOCD_PATH.equals(variable.getName())) {
			value = PersistentPreferences.getInstallFolder();
		}
		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.DynamicVariableResolver.resolveValue(\"" + variable.getName() + "\", \""
					+ argument + "\") = \"" + value + "\"");
		}
		return value;
	}

}
