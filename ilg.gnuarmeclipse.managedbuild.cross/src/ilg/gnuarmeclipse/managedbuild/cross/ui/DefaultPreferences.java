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

package ilg.gnuarmeclipse.managedbuild.cross.ui;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.managedbuild.cross.Activator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.cdt.utils.WindowsRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;

public class DefaultPreferences {

	// ------------------------------------------------------------------------

	public static final String TOOLCHAIN = "toolchains.prefs";
	public static final String DEFAULT_NAME = "default.name";
	public static final String DEFAULT_PATH = "default.path";

	private static Properties ms_toolchainProperties;

	// LOCAL_MACHINE
	private static final String REG_SUBKEY = "SOFTWARE\\GNU ARM Eclipse\\Build Tools";
	private static final String REG_NAME = "InstallFolder";

	// ------------------------------------------------------------------------

	private static String getString(String name, String defValue) {

		return DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID).get(name,
				defValue);
	}

	@SuppressWarnings("unused")
	private static boolean getBoolean(String name, boolean defValue) {

		return DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID).getBoolean(
				name, defValue);
	}

	// ------------------------------------------------------------------------

	public static String getToolchainName() {

		try {
			Properties prop = getToolchainProperties();
			return prop.getProperty(DEFAULT_NAME, "").trim();
		} catch (IOException e) {
			return "";
		}
	}

	public static String getToolchainPath(String toolchainName) {

		int hash = Math.abs(toolchainName.trim().hashCode());
		String property = DEFAULT_PATH + "." + String.valueOf(hash);
		try {
			Properties prop = getToolchainProperties();
			return prop.getProperty(property, "").trim();
		} catch (IOException e) {
			return "";
		}
	}

	private static Properties getToolchainProperties() throws IOException {

		if (ms_toolchainProperties == null) {

			URL url = Platform.getInstallLocation().getURL();

			IPath path = new Path(url.getPath());
			File file = path.append("configuration")
					.append(Activator.PLUGIN_ID).append(TOOLCHAIN).toFile();
			InputStream is = new FileInputStream(file);

			Properties prop = new Properties();
			prop.load(is);

			ms_toolchainProperties = prop;
		}

		return ms_toolchainProperties;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the default value for the build tools path.
	 * 
	 * @return a string, possibly empty.
	 */
	public static String getBuildToolsPath() {

		return getString(PersistentPreferences.BUILD_TOOLS_PATH, "");
	}

	public static String discoverBuildToolsPath() {

		String value = null;
		if (EclipseUtils.isWindows()) {

			WindowsRegistry registry = WindowsRegistry.getRegistry();
			if (registry != null) {
				value = registry.getLocalMachineValue(REG_SUBKEY, REG_NAME);

				if (value != null && !value.endsWith("\\bin")) {
					value += "\\bin";
				}
			}
		}

		if (value != null) {
			value = value.trim();
		} else {
			value = "";
		}

		System.out.println("DefaultPreferences.discoverBuildToolsPath()=\""
				+ value + "\"");

		return value;
	}
	// ------------------------------------------------------------------------
}
