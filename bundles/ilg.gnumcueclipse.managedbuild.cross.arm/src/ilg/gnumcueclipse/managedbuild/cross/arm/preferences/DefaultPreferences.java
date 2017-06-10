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

package ilg.gnumcueclipse.managedbuild.cross.arm.preferences;

import java.io.File;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.core.preferences.Discoverer;
import ilg.gnumcueclipse.managedbuild.cross.Activator;
import ilg.gnumcueclipse.managedbuild.cross.arm.ToolchainDefinition;

public class DefaultPreferences extends ilg.gnumcueclipse.managedbuild.cross.preferences.DefaultPreferences {

	// ------------------------------------------------------------------------

	private static final String REG_SUBKEY = "\\ARM\\Build Tools for ARM Embedded Processors";
	private static final String REG_SUBKEY_v6_q1 = "\\ARM\\GNU Tools for ARM Embedded Processors 6-2017-q1-update";

	// Standard Microsoft recommendation.
	private static final String REG_NAME = "InstallFolder";

	// ------------------------------------------------------------------------

	public DefaultPreferences(String pluginId) {
		super(pluginId);
	}

	// ------------------------------------------------------------------------

	public String discoverToolchainPath(String toolchainName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("arm.DefaultPreferences.discoverToolchainPath(\"" + toolchainName + "\")");
		}

		// If the search path is known, discover toolchain.
		int ix;
		try {
			ix = ToolchainDefinition.findToolchainByName(toolchainName);
		} catch (IndexOutOfBoundsException e) {
			ix = ToolchainDefinition.getDefault();
		}

		String executableName = ToolchainDefinition.getToolchain(ix).getFullCmdC();

		String path = null;
		if (EclipseUtils.isWindows()) {

			if (ToolchainDefinition.GNU_TOOLS_FOR_ARM_EMBEDDED.equals(toolchainName)) {
				path = Discoverer.getRegistryInstallFolder(executableName + ".exe", "bin", REG_SUBKEY, REG_NAME);
				if (path == null) {
					path = Discoverer.getRegistryInstallFolder(executableName + ".exe", "bin", REG_SUBKEY_v6_q1,
							REG_NAME);
				}
			}
		}

		String searchPath = null;
		if (path == null) {

			// Check if the search path is defined in the default
			// preferences.
			searchPath = getToolchainSearchPath(toolchainName);
			if (searchPath.isEmpty()) {

				// If not defined, get the OS Specific default
				// from preferences.ini.
				searchPath = getToolchainSearchPathOs(toolchainName);
				if (!searchPath.isEmpty()) {
					// Store the search path in the preferences
					putToolchainSearchPath(toolchainName, searchPath);
				}
			}

			if (searchPath != null && !searchPath.isEmpty()) {
				path = searchLatestExecutable(searchPath, executableName);
			}

			if (path != null) {
				path = path.trim();

				// Validate registry path. If folder does not exist, ignore.
				File file = new File(path);
				if (!file.isDirectory()) {
					path = "";
				}
			} else {
				path = "";
			}
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"arm.DefaultPreferences.discoverToolchainPath(\"" + toolchainName + "\") = \"" + path + "\"");
		}

		return path;
	}

	// ------------------------------------------------------------------------
}
