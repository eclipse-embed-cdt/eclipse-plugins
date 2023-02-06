/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
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

package org.eclipse.embedcdt.managedbuild.cross.core.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.internal.managedbuild.cross.core.Activator;

public class PersistentPreferences extends org.eclipse.embedcdt.core.preferences.PersistentPreferences {

	// ------------------------------------------------------------------------

	public static final String BUILD_TOOLS_PATH_KEY = "buildTools.path";
	public static final String GLOBAL_BUILDTOOLS_PATH_STRICT = "global.buildTools.path.strict";
	public static final String WORKSPACE_BUILDTOOLS_PATH_STRICT = "workspace.buildTools.path.strict";
	public static final String PROJECT_BUILDTOOLS_PATH_STRICT = "project.buildTools.path.strict";

	public static final String BUILD_TOOLS_SEARCH_PATH_KEY = "buildTools.search.path";
	public static final String BUILD_TOOLS_SEARCH_PATH_OS_KEY = "buildTools.search.path.%s";

	public static final String BUILD_TOOLS_XPACK_NAMES = "buildTools.xpack.names";

	public static final String TOOLCHAIN_NAME_KEY = "toolchain.name";
	public static final String TOOLCHAIN_ID_KEY = "toolchain.id";
	private static final String TOOLCHAIN_PATH_KEY = "toolchain.path.%d";
	private static final String TOOLCHAIN_SEARCH_PATH_KEY = "toolchain.search.path.%d";
	private static final String TOOLCHAIN_SEARCH_PATH_OS_KEY = "toolchain.search.path.%s.%d";

	private static final String TOOLCHAIN_XPACK_NAMES_KEY = "toolchain.xpack.names.%d";

	public static final String GLOBAL_TOOLCHAIN_PATH_STRICT = "global.toolchain.path.strict";
	public static final String WORKSPACE_TOOLCHAIN_PATH_STRICT = "workspace.toolchain.path.strict";
	public static final String PROJECT_TOOLCHAIN_PATH_STRICT = "project.toolchain.path.strict";

	// ------------------------------------------------------------------------

	public PersistentPreferences(String pluginId) {
		super(pluginId);
	}

	// ------------------------------------------------------------------------

	private String getCommonString(String key, String defaultValue, IProject project) {

		String value = getPreferenceValueForId(Activator.PLUGIN_ID, key, null, project);
		if (value != null && !value.isEmpty()) {
			return value;
		}

		value = getPreferenceValueForId("ilg.gnumcueclipse.managedbuild.cross", key, null, project);
		if (value != null && !value.isEmpty()) {
			return value;
		}
		return defaultValue;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the last used toolchain name.
	 *
	 * @return a trimmed string, possibly empty.
	 */
	public String getToolchainName() {

		return getString(TOOLCHAIN_NAME_KEY, "").trim();
	}

	public String getToolchainId() {

		return getString(TOOLCHAIN_ID_KEY, "").trim();
	}

	/**
	 * Store the toolchain name in the Workspace/Eclipse scope. Used in the project
	 * wizard, to maintain global persistence.
	 *
	 * @param toolchainName a string.
	 */
	public void putToolchainName(String toolchainName) {

		putString(TOOLCHAIN_NAME_KEY, toolchainName);
	}

	public void putToolchainId(String toolchainId) {

		putString(TOOLCHAIN_ID_KEY, toolchainId);
	}

	// ------------------------------------------------------------------------

	public static Long getKeyId(String toolchainId, String toolchainName) {

		long keyId;
		try {
			if (toolchainId != null && !toolchainId.trim().isEmpty()) {
				keyId = Long.parseLong(toolchainId.trim());
				return keyId;
			}
		} catch (NumberFormatException ex) {
		}

		keyId = Math.abs(toolchainName.trim().hashCode());
		return keyId;
	}

	@Deprecated
	public static String getToolchainKey(String toolchainName) {

		int hash = Math.abs(toolchainName.trim().hashCode());
		String key = String.format(TOOLCHAIN_PATH_KEY, hash);
		return key;
	}

	public static String getToolchainKey(String toolchainId, String toolchainName) {

		Long keyId = getKeyId(toolchainId, toolchainName);
		return String.format(TOOLCHAIN_PATH_KEY, keyId);
	}

	/**
	 * Get the toolchain path for a given toolchain name.
	 *
	 * @param toolchainName
	 * @return a string, possibly empty.
	 */
	@Deprecated
	public String getToolchainPath(String toolchainName, IProject project) {

		String value;
		if (project != null) {
			value = getString(getToolchainKey(toolchainName), null, project);
		} else {
			value = getString(getToolchainKey(toolchainName), null);
		}
		if (value != null && !value.isEmpty()) {
			return value;
		}

		return "";
	}

	public String getToolchainPath(String toolchainId, String toolchainName, IProject project) {

		String value;
		if (project != null) {
			value = getString(getToolchainKey(toolchainId, toolchainName), null, project);
		} else {
			value = getString(getToolchainKey(toolchainId, toolchainName), null);
		}
		if (value != null && !value.isEmpty()) {
			return value;
		}
		// This could be a project using toolchain id created
		// using old hash mechanism, so just trying to get the
		// toolchain path using just the toolchain name.
		return getToolchainPath(toolchainName, project);
	}

	/**
	 * Store the toolchain path in the Workspace/Eclipse scope. Used in the project
	 * wizard, to maintain global persistency.
	 *
	 * @param toolchainName
	 * @param path
	 */
	@Deprecated
	public void putToolchainPath(String toolchainName, String path) {

		putString(getToolchainKey(toolchainName), path);
	}

	public void putToolchainPath(String toolchainId, String toolchainName, String path) {

		putString(getToolchainKey(toolchainId, toolchainName), path);
	}

	/**
	 * Store the toolchain path in the Project scope. Used in
	 * EnvironmentVariableSupplier to copy path from old storage to new storage.
	 *
	 * @param toolchainName
	 * @param path
	 * @param project
	 */
	@Deprecated
	public void putToolchainPath(String toolchainName, String path, IProject project) {

		putProjectString(getToolchainKey(toolchainName), path, project);
	}

	public void putToolchainPath(String toolchainId, String toolchainName, String path, IProject project) {

		putProjectString(getToolchainKey(toolchainId, toolchainName), path, project);
	}

	// ------------------------------------------------------------------------

	@Deprecated
	public static String getToolchainSearchKey(String toolchainName) {

		int hash = Math.abs(toolchainName.trim().hashCode());
		String key = String.format(TOOLCHAIN_SEARCH_PATH_KEY, hash);
		// System.out.println(key);
		return key;
	}

	@Deprecated
	public static String getToolchainSearchOsKey(String toolchainName) {

		int hash = Math.abs(toolchainName.trim().hashCode());
		String os = EclipseUtils.getOsFamily();
		String key = String.format(TOOLCHAIN_SEARCH_PATH_OS_KEY, os, hash);
		// System.out.println(key);
		return key;
	}

	@Deprecated
	public static String getToolchainXpackKey(String toolchainName) {

		int hash = Math.abs(toolchainName.trim().hashCode());
		String key = String.format(TOOLCHAIN_XPACK_NAMES_KEY, hash);
		// System.out.println(key);
		return key;
	}

	public static String getToolchainSearchKey(String toolchainId, String toolchainName) {

		Long id = getKeyId(toolchainId, toolchainName);
		String key = String.format(TOOLCHAIN_SEARCH_PATH_KEY, id);
		// System.out.println(key);
		return key;
	}

	public static String getToolchainSearchOsKey(String toolchainId, String toolchainName) {

		Long id = getKeyId(toolchainId, toolchainName);
		String os = EclipseUtils.getOsFamily();
		String key = String.format(TOOLCHAIN_SEARCH_PATH_OS_KEY, os, id);
		// System.out.println(key);
		return key;
	}

	public static String getToolchainXpackKey(String toolchainId, String toolchainName) {

		Long id = getKeyId(toolchainId, toolchainName);
		String key = String.format(TOOLCHAIN_XPACK_NAMES_KEY, id);
		// System.out.println(key);
		return key;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the build tools path. Search all possible scopes. The definition is
	 * common to all build plug-ins.
	 *
	 * @return a string, possibly empty.
	 */
	public String getBuildToolsPath(IProject project) {

		String value = getCommonString(BUILD_TOOLS_PATH_KEY, "", project);

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"PersistentPreferences.getBuildToolsPath(\"" + project.getName() + "\") = \"" + value + "\"");
		}
		return value;
	}

	// ------------------------------------------------------------------------
}
