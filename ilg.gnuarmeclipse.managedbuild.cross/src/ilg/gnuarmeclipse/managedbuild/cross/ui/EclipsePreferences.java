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
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class EclipsePreferences {

	// Note: The shared defaults keys don't have "cross" in them because we want
	// to keep
	// compatibility with defaults that were saved when it used to be a template
	static final String SHARED_CROSS_TOOLCHAIN_NAME = SetCrossCommandWizardPage.CROSS_TOOLCHAIN_NAME;
	static final String SHARED_CROSS_TOOLCHAIN_PATH = SetCrossCommandWizardPage.CROSS_TOOLCHAIN_PATH;

	// ----- getter & setter --------------------------------------------------
	private static String getValueForId(String id, String defaultValue) {

		// Access the instanceScope
		Preferences preferences = ConfigurationScope.INSTANCE
				.getNode(Activator.PLUGIN_ID);

		String value;
		// preferences.get(id, defaultValue);
		value = preferences.get(id, null);
		// System.out.println("Value of " + id + " is " + value);

		if (value != null) {
			return value;
		}

		// Keep this for compatibility
		id = Activator.PLUGIN_ID + "." + id;

		value = SharedDefaults.getInstance().getSharedDefaultsMap().get(id);

		if (value == null)
			value = "";

		value = value.trim();
		if (value.length() == 0 && defaultValue != null)
			return defaultValue.trim();

		return value;
	}

	private static void putValueForId(String id, String value) {

		value = value.trim();

		// Access the instanceScope
		Preferences preferences = ConfigurationScope.INSTANCE
				.getNode(Activator.PLUGIN_ID);
		preferences.put(id, value);

	}

	public static String getToolchainName() {

		String toolchainName = getValueForId(SHARED_CROSS_TOOLCHAIN_NAME, "");

		if (toolchainName.length() == 0)
			toolchainName = DefaultPreferences.getToolchainName();

		return toolchainName.trim();
	}

	public static void putToolchainName(String toolchainName) {

		putValueForId(SHARED_CROSS_TOOLCHAIN_NAME, toolchainName);
	}

	public static String getToolchainPath(String toolchainName) {

		String name = toolchainName.trim();
		String pathKey = SHARED_CROSS_TOOLCHAIN_PATH + "."
				+ Math.abs(name.hashCode());
		String sPath = getValueForId(pathKey, "");

		if (sPath.length() == 0) {
			sPath = DefaultPreferences.getToolchainPath(name);
		}

		return sPath.trim();
	}

	public static void putToolchainPath(String toolchainName, String path) {

		String pathKey = SHARED_CROSS_TOOLCHAIN_PATH + "."
				+ Math.abs(toolchainName.trim().hashCode());
		putValueForId(pathKey, path.trim());
	}

	public static void update() {

		try {
			ConfigurationScope.INSTANCE.getNode(Activator.PLUGIN_ID).flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}
}
