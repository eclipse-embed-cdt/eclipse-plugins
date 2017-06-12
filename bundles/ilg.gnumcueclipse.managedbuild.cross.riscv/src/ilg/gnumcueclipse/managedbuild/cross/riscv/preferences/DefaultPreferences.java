/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.managedbuild.cross.riscv.preferences;

import ilg.gnumcueclipse.core.preferences.Discoverer;
import ilg.gnumcueclipse.managedbuild.cross.riscv.ToolchainDefinition;

public class DefaultPreferences extends ilg.gnumcueclipse.managedbuild.cross.preferences.DefaultPreferences {

	// ------------------------------------------------------------------------

	private static final String REG_SUBKEY = "\\GNU MCU Eclipse\\Build Tools for RISC-V Embedded Processors";

	// Standard Microsoft recommendation.
	private static final String REG_NAME = "InstallFolder";

	// ------------------------------------------------------------------------

	public DefaultPreferences(String pluginId) {
		super(pluginId);
	}

	// ------------------------------------------------------------------------

	protected String getRegistryToolchainInstallFolder(String toolchainName, String subPath, String executableName) {

		String path = null;
		if (ToolchainDefinition.RISC_V_GCC_NEWLIB.equals(toolchainName)) {
			path = Discoverer.getRegistryInstallFolder(executableName, subPath, REG_SUBKEY, REG_NAME);
		}
		return path;
	}

	// ------------------------------------------------------------------------
}
