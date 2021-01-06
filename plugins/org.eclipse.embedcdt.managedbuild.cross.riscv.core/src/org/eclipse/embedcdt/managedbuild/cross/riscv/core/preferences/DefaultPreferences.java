/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
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

package org.eclipse.embedcdt.managedbuild.cross.riscv.core.preferences;

import org.eclipse.embedcdt.core.preferences.Discoverer;
import org.eclipse.embedcdt.managedbuild.cross.riscv.core.ToolchainDefinition;

public class DefaultPreferences extends org.eclipse.embedcdt.managedbuild.cross.core.preferences.DefaultPreferences {

	// ------------------------------------------------------------------------

	private static final String REG_SUBKEY = "\\GNU MCU Eclipse\\RISC-V Embedded GCC";

	// Standard Microsoft recommendation.
	private static final String REG_NAME = "InstallFolder";

	// ------------------------------------------------------------------------

	public DefaultPreferences(String pluginId) {
		super(pluginId);
	}

	// ------------------------------------------------------------------------

	@Override
	protected String getRegistryToolchainInstallFolder(String toolchainName, String subPath, String executableName) {

		String path = null;
		if (ToolchainDefinition.RISC_V_GCC_NEWLIB.equals(toolchainName)) {
			path = Discoverer.getRegistryInstallFolder(executableName, subPath, REG_SUBKEY, REG_NAME);
		}
		return path;
	}

	// ------------------------------------------------------------------------
}
