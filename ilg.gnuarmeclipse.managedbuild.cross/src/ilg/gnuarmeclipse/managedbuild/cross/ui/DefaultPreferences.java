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

	// DEPRECATED, non-standard location
	public static final String DEFAULT_NAME = "default.name";
	public static final String DEFAULT_PATH = "default.path";
	public static final String TOOLCHAIN = "toolchains.prefs";
	private static Properties fgToolchainProperties;

	// LOCAL_MACHINE
	private static final String REG_SUBKEY = "SOFTWARE\\GNU ARM Eclipse\\Build Tools";
	private static final String REG32_SUBKEY = "SOFTWARE\\Wow6432Node\\GNU ARM Eclipse\\Build Tools";
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

		String value = getString(PersistentPreferences.TOOLCHAIN_NAME, null);
		if (value != null) {
			return value;
		}

		value = "";

		{
			// DEPRECATED
			try {
				Properties prop = getToolchainProperties();
				value = prop.getProperty(DEFAULT_NAME, "").trim();
			} catch (IOException e) {
				;
			}
		}

		return value;
	}

	public static String getToolchainPath(String toolchainName) {

		int hash = Math.abs(toolchainName.trim().hashCode());

		String value = getString(PersistentPreferences.TOOLCHAIN_PATH + "."
				+ String.valueOf(hash), null);
		if (value != null) {
			return value;
		}

		value = "";

		{
			// DEPRECATED
			try {
				Properties prop = getToolchainProperties();
				value = prop.getProperty(
						DEFAULT_PATH + "." + String.valueOf(hash), "").trim();
			} catch (IOException e) {
				;
			}
		}

		return value;
	}

	// DEPRECATED, non-standard location:
	// eclipse/configuration/ilg.gnuarmeclipse.managedbuild.cross/toolchain.prefs/name=value

	private static Properties getToolchainProperties() throws IOException {

		if (fgToolchainProperties == null) {

			URL url = Platform.getInstallLocation().getURL();

			IPath path = new Path(url.getPath());
			File file = path.append("configuration")
					.append(Activator.PLUGIN_ID).append(TOOLCHAIN).toFile();
			InputStream is = new FileInputStream(file);

			Properties prop = new Properties();
			prop.load(is);

			fgToolchainProperties = prop;
		}

		return fgToolchainProperties;
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
				if (value == null) {
					value = registry.getLocalMachineValue(REG32_SUBKEY,
							REG_NAME);
				}

				if (value != null && !value.endsWith("\\bin")) {
					value += "\\bin";
				}
			}
		} else if (EclipseUtils.isMacOSX()) {
			// value = "/opt/local/bin";
		}

		if (value != null) {
			value = value.trim();

			// Validate registry path. If folder does not exist, ignore.
			File file = new File(value);
			if (!file.isDirectory()) {
				value = "";
			}
		} else {
			value = "";
		}

		System.out.println("DefaultPreferences.discoverBuildToolsPath()=\""
				+ value + "\"");

		return value;
	}

	// ------------------------------------------------------------------------
}
