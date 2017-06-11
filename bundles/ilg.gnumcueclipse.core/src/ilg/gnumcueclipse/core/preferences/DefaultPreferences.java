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

package ilg.gnumcueclipse.core.preferences;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

import ilg.gnumcueclipse.core.Activator;
import ilg.gnumcueclipse.core.EclipseUtils;

public class DefaultPreferences {

	// ------------------------------------------------------------------------

	protected String fPluginId;

	/**
	 * The DefaultScope preference store.
	 */
	protected IEclipsePreferences fPreferences;

	// ------------------------------------------------------------------------

	public DefaultPreferences(String pluginId) {
		fPluginId = pluginId;
		fPreferences = DefaultScope.INSTANCE.getNode(fPluginId);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get a string preference value, or the default.
	 * 
	 * @param key
	 *            a string with the key to search.
	 * @param defaultValue
	 *            a string with the default, possibly null.
	 * @return a trimmed string, or a null default.
	 */
	public String getString(String key, String defaultValue) {

		String value;
		value = fPreferences.get(key, defaultValue);

		if (value != null) {
			value = value.trim();
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getString(\"" + key + "\", \"" + defaultValue + "\") "
					+ fPreferences.name() + " = \"" + value + "\"");
		}

		return value;
	}

	public boolean getBoolean(String key, boolean defaultValue) {

		boolean value = fPreferences.getBoolean(key, defaultValue);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getString(\"" + key + "\", \"" + defaultValue + "\") "
					+ fPreferences.name() + " = " + value);
		}
		return value;
	}

	public int getInt(String key, int defaultValue) {

		int value = fPreferences.getInt(key, defaultValue);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getBoolean(\"" + key + "\", " + defaultValue + ") = " + value);
		}
		return value;
	}

	public void putString(String key, String value) {
		if (Activator.getInstance().isDebugging()) {
			System.out
					.println("DefaultPreferences.putString(\"" + key + "\", \"" + value + "\") " + fPreferences.name());
		}
		fPreferences.put(key, value);
	}

	public void putInt(String key, int value) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putInt(\"" + key + "\", " + value + ")");
		}

		fPreferences.putInt(key, value);
	}

	public void putBoolean(String key, boolean value) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putBoolean(\"" + key + "\", " + value + ")");
		}

		fPreferences.putBoolean(key, value);
	}

	public boolean checkFolderExecutable(String folder, String executableName) {
		
		if (folder == null || folder.isEmpty()) {
			return false;
		}
		if (executableName == null || executableName.isEmpty()) {
			return false;
		}
		
		if (EclipseUtils.isWindows() && !executableName.endsWith(".exe")) {
			executableName += ".exe";
		}

		IPath path = (new Path(folder)).append(executableName);
		if (path.toFile().isFile()) {
			return true;
		}

		return false;
	}

	/**
	 * Search subfolders for an executable and remember date of version folders.
	 * "<searchPath>/<version>/bin/<executable>"
	 * 
	 * @param searchPath
	 * @param executableName
	 * @return
	 */
	public String searchLatestExecutable(String searchPath, String executableName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"DefaultPreferences.searchLatestExecutable(\"" + searchPath + "\", " + executableName + ") ");
		}

		if (searchPath == null || searchPath.isEmpty()) {
			return null;
		}

		// Resolve ${user.home}
		String resolvedPath = searchPath;
		if (resolvedPath.indexOf("${user.home}") >= 0) {
			String userHome = System.getProperty("user.home");
			userHome = Matcher.quoteReplacement(userHome);
			resolvedPath = resolvedPath.replaceAll("\\$\\{user.home\\}", userHome);
		}

		// If more macros remain, use the usual substituter.
		if (resolvedPath.indexOf("${") >= 0) {
			IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
			try {
				resolvedPath = variableManager.performStringSubstitution(resolvedPath, false);
			} catch (CoreException e) {
				resolvedPath = null;
			}
		}

		if (resolvedPath == null || resolvedPath.isEmpty()) {
			return null;
		}

		// Split into multiple paths.
		String[] paths = resolvedPath.split(EclipseUtils.getPathSeparator());
		if (paths.length == 0) {
			return null;
		}

		if (EclipseUtils.isWindows() && !executableName.endsWith(".exe")) {
			executableName += ".exe";
		}

		Map<Long, String> map = new HashMap<Long, String>();
		// Try paths in order; collect dates.
		for (int i = 0; i < paths.length; ++i) {
			searchExecutable(paths[i], executableName, map);
		}

		if (map.isEmpty()) {
			return null;
		}

		Set<Long> keys = map.keySet();
		Long latestKey = new Long(0);
		for (Long key : keys) {
			if (key > latestKey) {
				latestKey = key;
			}
		}

		return map.get(latestKey);
	}

	private void searchExecutable(String folder, final String executableName, Map<Long, String> map) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.searchExecutable(\"" + folder + "\", \"" + executableName + "\") ");
		}
		File local = new File(folder);
		if (!local.isDirectory()) {
			// System.out.println(folder + " not a folder");
			return;
		}

		local.listFiles(new FilenameFilter() {

			/**
			 * Filter to select only
			 */
			@Override
			public boolean accept(File dir, String name) {
				IPath versionPath = (new Path(dir.getAbsolutePath())).append(name);
				IPath basePath = versionPath.append("bin");
				IPath path = basePath.append(executableName);

				File file = path.toFile();
				if (file.isFile()) {
					File versioFolder = versionPath.toFile();
					Long key = new Long(versioFolder.lastModified());
					map.put(key, basePath.toPortableString());

					if (Activator.getInstance().isDebugging()) {
						System.out.println("DefaultPreferences.searchExecutable(\"" + folder + "\", \"" + executableName
								+ "\") = add \"" + basePath.toPortableString() + "\" " + new Date(key.longValue()));
					}
					return true;
				}
				return false;
			}
		});
	}


	// ------------------------------------------------------------------------
}
