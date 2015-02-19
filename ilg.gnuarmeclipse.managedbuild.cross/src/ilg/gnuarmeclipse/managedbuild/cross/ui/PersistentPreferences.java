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

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.managedbuild.cross.Activator;

import org.eclipse.cdt.core.templateengine.SharedDefaults;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class PersistentPreferences {

	// ------------------------------------------------------------------------

	static final String TOOLCHAIN_NAME = "toolchain.name";
	static final String TOOLCHAIN_PATH = "toolchain.path";

	public static final String BUILD_TOOLS_PATH = "buildTools.path";

	// Note: The shared defaults keys don't have "cross" in them because we want
	// to keep
	// compatibility with defaults that were saved when it used to be a template
	// static final String SHARED_CROSS_TOOLCHAIN_NAME =
	// SetCrossCommandWizardPage.CROSS_TOOLCHAIN_NAME;
	// static final String SHARED_CROSS_TOOLCHAIN_PATH =
	// SetCrossCommandWizardPage.CROSS_TOOLCHAIN_PATH;

	// ----- getter -----------------------------------------------------------
	private static String getValueForId(String id, String defaultValue,
			IProject project) {

		String value = EclipseUtils.getPreferenceValueForId(
				Activator.PLUGIN_ID, id, null, project);
		if (value != null) {
			return value;
		}

		{
			// Keep this a while for compatibility with the first versions
			// which erroneously stored values in the shared storage.
			value = SharedDefaults.getInstance().getSharedDefaultsMap()
					.get(Activator.PLUGIN_ID + "." + id);

			if (value == null)
				value = "";

			value = value.trim();
			if (!value.isEmpty()) {
				return value;
			}
		}

		return defaultValue;
	}

	// ----- setter -----------------------------------------------------------
	private static void putEclipseValueForId(String id, String value) {

		value = value.trim();

		// Access the Eclipse scope
		Preferences preferences = ConfigurationScope.INSTANCE
				.getNode(Activator.PLUGIN_ID);
		preferences.put(id, value);
	}

	public static void flush() {

		try {
			ConfigurationScope.INSTANCE.getNode(Activator.PLUGIN_ID).flush();
		} catch (BackingStoreException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	// ------------------------------------------------------------------------

	public static String getToolchainName(IProject project) {

		String toolchainName = getValueForId(TOOLCHAIN_NAME, "", project);

		if (toolchainName.length() == 0)
			toolchainName = DefaultPreferences.getToolchainName();

		return toolchainName.trim();
	}

	public static void putToolchainName(String toolchainName) {

		putEclipseValueForId(TOOLCHAIN_NAME, toolchainName);
	}

	/**
	 * Get the toolchain path for a given toolchain name.
	 * 
	 * @param toolchainName
	 * @return a string, possibly empty.
	 */
	public static String getToolchainPath(String toolchainName, IProject project) {

		String name = toolchainName.trim();
		String pathKey = TOOLCHAIN_PATH + "." + Math.abs(name.hashCode());
		String sPath = getValueForId(pathKey, "", project);

		if (sPath.length() == 0) {
			sPath = DefaultPreferences.getToolchainPath(name);
		}

		return sPath.trim();
	}

	public static void putToolchainPath(String toolchainName, String path) {

		String pathKey = TOOLCHAIN_PATH + "."
				+ Math.abs(toolchainName.trim().hashCode());
		putEclipseValueForId(pathKey, path.trim());
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the value for the build tools path.
	 * 
	 * @return a string, possibly empty.
	 */
	public static String getBuildToolsPath(IProject project) {

		return getValueForId(BUILD_TOOLS_PATH, "", project);
	}

	// ------------------------------------------------------------------------
}
