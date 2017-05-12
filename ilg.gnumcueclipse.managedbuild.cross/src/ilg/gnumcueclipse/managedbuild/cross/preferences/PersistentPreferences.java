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

package ilg.gnumcueclipse.managedbuild.cross.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import ilg.gnuarmeclipse.core.EclipseUtils;

public class PersistentPreferences {

	// ------------------------------------------------------------------------

	public static final String BUILD_TOOLS_PATH_KEY = "buildTools.path";
	public static final String GLOBAL_BUILDTOOLS_PATH_STRICT = "global.buildTools.path.strict";
	public static final String WORKSPACE_BUILDTOOLS_PATH_STRICT = "workspace.buildTools.path.strict";
	public static final String PROJECT_BUILDTOOLS_PATH_STRICT = "project.buildTools.path.strict";

	public static final String TOOLCHAIN_NAME_KEY = "toolchain.name";
	private static final String TOOLCHAIN_PATH_KEY = "toolchain.path.%d";
	private static final String TOOLCHAIN_SEARCH_PATH_KEY = "toolchain.search.path.%d";
	private static final String TOOLCHAIN_SEARCH_PATH_OS_KEY = "toolchain.search.path.%s.%d";

	public static final String GLOBAL_TOOLCHAIN_PATH_STRICT = "global.toolchain.path.strict";
	public static final String WORKSPACE_TOOLCHAIN_PATH_STRICT = "workspace.toolchain.path.strict";
	public static final String PROJECT_TOOLCHAIN_PATH_STRICT = "project.toolchain.path.strict";

	// ------------------------------------------------------------------------

	private String fPluginId;

	// ------------------------------------------------------------------------

	public PersistentPreferences(String pluginId) {
		fPluginId = pluginId;
	}

	// ----- Getters ----------------------------------------------------------
	private String getString(String key, String defaultValue, IProject project) {

		String value = EclipseUtils.getPreferenceValueForId(fPluginId, key, null, project);
		if (value != null && !value.isEmpty()) {
			return value;
		}

		return defaultValue;
	}

	@SuppressWarnings("unused")
	private String getEclipseString(String key, String defaultValue) {

		// Access the Eclipse scope
		Preferences preferences = ConfigurationScope.INSTANCE.getNode(fPluginId);

		String value = preferences.get(key, defaultValue);
		return value;
	}

	private String getWorkspaceString(String key, String defaultValue) {

		// Access the Eclipse scope
		Preferences preferences = InstanceScope.INSTANCE.getNode(fPluginId);

		String value = preferences.get(key, defaultValue);
		return value;
	}

	// ----- Setters ----------------------------------------------------------

	private void putString(String key, String value) {

		String oldValue = getWorkspaceString(key, null);
		if (oldValue != null) {
			putWorkspaceString(key, value);
		} else {
			putEclipseString(key, value);
		}
	}

	private void putEclipseString(String key, String value) {

		value = value.trim();

		// Access the Eclipse scope
		Preferences preferences = ConfigurationScope.INSTANCE.getNode(fPluginId);
		preferences.put(key, value);
	}

	private void putWorkspaceString(String key, String value) {

		value = value.trim();

		// Access the Workspace scope
		Preferences preferences = InstanceScope.INSTANCE.getNode(fPluginId);
		preferences.put(key, value);
	}

	public void flush() {

		try {
			ConfigurationScope.INSTANCE.getNode(fPluginId).flush();
			InstanceScope.INSTANCE.getNode(fPluginId).flush();
		} catch (BackingStoreException e) {
			;
		}
	}

	private void putProjectString(String key, String value, IProject project) {

		value = value.trim();

		// Access the Eclipse scope
		Preferences preferences = new ProjectScope(project).getNode(fPluginId);
		preferences.put(key, value);
	}

	public void flush(IProject project) {

		try {
			new ProjectScope(project).getNode(fPluginId).flush();
		} catch (BackingStoreException e) {
			;
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the last used toolchain name.
	 * 
	 * @return a trimmed string, possibly empty.
	 */
	public String getToolchainName() {

		String toolchainName = getString(TOOLCHAIN_NAME_KEY, null, null);
		if (toolchainName != null && !toolchainName.isEmpty()) {
			return toolchainName;
		}

		return "";
	}

	/**
	 * Store the toolchain name in the Workspace/Eclipse scope. Used in the
	 * project wizard, to maintain global persistency.
	 * 
	 * @param toolchainName
	 *            a string.
	 */
	public void putToolchainName(String toolchainName) {
		putString(TOOLCHAIN_NAME_KEY, toolchainName);
	}

	// ------------------------------------------------------------------------

	public static String getToolchainKey(String toolchainName) {

		int hash = Math.abs(toolchainName.trim().hashCode());
		String key = String.format(TOOLCHAIN_PATH_KEY, hash);
		return key;
	}

	/**
	 * Get the toolchain path for a given toolchain name.
	 * 
	 * @param toolchainName
	 * @return a string, possibly empty.
	 */
	public String getToolchainPath(String toolchainName, IProject project) {

		String value = getString(getToolchainKey(toolchainName), null, project);
		if (value != null && !value.isEmpty()) {
			return value;
		}

		return "";
	}

	/**
	 * Store the toolchain path in the Workspace/Eclipse scope. Used in the
	 * project wizard, to maintain global persistency.
	 * 
	 * @param toolchainName
	 * @param path
	 */
	public void putToolchainPath(String toolchainName, String path) {

		putString(getToolchainKey(toolchainName), path);
	}

	/**
	 * Store the toolchain path in the Project scope. Used in
	 * EnvironmentVariableSupplier to copy path from old storage to new storage.
	 * 
	 * @param toolchainName
	 * @param path
	 * @param project
	 */
	public void putToolchainPath(String toolchainName, String path, IProject project) {

		putProjectString(getToolchainKey(toolchainName), path, project);
	}

	// ------------------------------------------------------------------------

	public static String getToolchainSearchKey(String toolchainName) {

		int hash = Math.abs(toolchainName.trim().hashCode());
		String key = String.format(TOOLCHAIN_SEARCH_PATH_KEY, hash);
		// System.out.println(key);
		return key;
	}

	public static String getToolchainSearchOsKey(String toolchainName) {

		int hash = Math.abs(toolchainName.trim().hashCode());
		String os = EclipseUtils.getOsFamily();
		String key = String.format(TOOLCHAIN_SEARCH_PATH_OS_KEY, os, hash);
		// System.out.println(key);
		return key;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the build tools path. Search all possible scopes.
	 * 
	 * @return a string, possibly empty.
	 */
	public String getBuildToolsPath(IProject project) {

		return getString(BUILD_TOOLS_PATH_KEY, "", project);
	}

	// ------------------------------------------------------------------------
}
