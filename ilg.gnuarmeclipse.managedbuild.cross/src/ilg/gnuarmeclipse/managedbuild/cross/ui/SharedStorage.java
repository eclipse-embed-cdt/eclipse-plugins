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

package ilg.gnuarmeclipse.managedbuild.cross.ui;

import ilg.gnuarmeclipse.managedbuild.cross.Activator;

import org.eclipse.cdt.core.templateengine.SharedDefaults;

public class SharedStorage {

	// Note: The shared defaults keys don't have "cross" in them because we want
	// to keep
	// compatibility with defaults that were saved when it used to be a template
	static final String SHARED_CROSS_TOOLCHAIN_NAME = Activator.getIdPrefix()
			+ "." + SetCrossCommandWizardPage.CROSS_TOOLCHAIN_NAME;
	static final String SHARED_CROSS_TOOLCHAIN_PATH = Activator.getIdPrefix()
			+ "." + SetCrossCommandWizardPage.CROSS_TOOLCHAIN_PATH;

	public static String getToolchainName() {

		String toolchainName = SharedDefaults.getInstance()
				.getSharedDefaultsMap().get(SHARED_CROSS_TOOLCHAIN_NAME);

		if (toolchainName == null)
			toolchainName = Preferences.getToolchainName();

		return toolchainName.trim();
	}

	public static void putToolchainName(String toolchainName) {

		SharedDefaults.getInstance().getSharedDefaultsMap()
				.put(SHARED_CROSS_TOOLCHAIN_NAME, toolchainName);
	}

	public static String getToolchainPath(String toolchainName) {

		String name = toolchainName.trim();
		String pathKey = SHARED_CROSS_TOOLCHAIN_PATH + "."
				+ Math.abs(name.hashCode());
		String sPath = SharedDefaults.getInstance().getSharedDefaultsMap()
				.get(pathKey);

		if (sPath == null) {
			sPath = Preferences.getToolchainPath(name);
		}

		return sPath.trim();
	}

	public static void putToolchainPath(String toolchainName, String path) {

		String pathKey = SHARED_CROSS_TOOLCHAIN_PATH + "."
				+ Math.abs(toolchainName.trim().hashCode());
		SharedDefaults.getInstance().getSharedDefaultsMap()
				.put(pathKey, path.trim());
	}

	public static void update() {

		SharedDefaults.getInstance().updateShareDefaultsMap(
				SharedDefaults.getInstance().getSharedDefaultsMap());
	}
}
