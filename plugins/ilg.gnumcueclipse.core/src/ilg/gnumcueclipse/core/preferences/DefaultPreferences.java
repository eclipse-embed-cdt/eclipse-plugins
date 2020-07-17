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

package ilg.gnumcueclipse.core.preferences;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import ilg.gnumcueclipse.core.Activator;
import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.core.XpackUtils;

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
	 * @param key          a string with the key to search.
	 * @param defaultValue a string with the default, possibly null.
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

	/**
	 * Get an array of strings, or the default.
	 * 
	 * @param key           a string with the key to search.
	 * @param defaultValuea string with the default, possibly null.
	 * @return an array of strings, possibly with an empty element
	 */
	public String[] getStringArray(String key, String defaultValue) {

		String value;
		value = fPreferences.get(key, defaultValue);

		if (value != null) {
			value = value.trim();
		} else {
			value = "";
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getStringArray(\"" + key + "\", \"" + defaultValue + "\") "
					+ fPreferences.name() + " = \"" + value + "\"");
		}

		String[] values = value.split(";");
		for (int i = 0; i < values.length; i++)
			values[i] = values[i].trim();

		return values;
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

	// ------------------------------------------------------------------------

	public String addExeExtension(String executableName) {
		if (!executableName.endsWith(".exe")) {
			return executableName + ".exe";
		} else {
			return executableName;
		}
	}

	public boolean checkFolderExecutable(String folder, String executableName) {

		folder = EclipseUtils.performStringSubstitution(folder);
		if (folder == null || folder.isEmpty()) {
			return false;
		}
		executableName = EclipseUtils.performStringSubstitution(executableName);
		if (executableName == null || executableName.isEmpty()) {
			return false;
		}

		if (EclipseUtils.isWindows()) {
			executableName = addExeExtension(executableName);
		}

		IPath path = (new Path(folder)).append(executableName);
		if (path.toFile().isFile()) {
			return true;
		}

		return false;
	}

	/**
	 * Search subfolders for an executable and remember timestamp of version
	 * folders. "<searchPath>/<version>/bin/<executable>"
	 * 
	 * @param searchPath
	 * @param subFolder      may be null; usually "bin".
	 * @param executableName
	 * @return
	 */
	public String searchLatestExecutable(String[] xpackNames, String searchPath, String subFolder,
			String executableName) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.searchLatestExecutable(" + String.join(";", xpackNames) + ", \""
					+ searchPath + "\", " + subFolder + "\", " + executableName + ") ");
		}

		// Each folder in the search path will be searched for versioned subfolders.
		// The names are not relevant, just that the extra folders must exits.
		String newSearchPath = searchPath;

		// Compute the list of unique names in xpackNames (ignore scope).
		Set<String> uniqueNames = new LinkedHashSet<String>();
		// Iterate in reverse order.
		for (int i = xpackNames.length - 1; i >= 0; --i) {
			if (!xpackNames[i].isEmpty()) {
				String name;
				int ix = xpackNames[i].indexOf('/');
				if (ix == -1) {
					name = xpackNames[i];
				} else {
					name = xpackNames[i].substring(ix + 1);
				}
				if (!name.isEmpty()) {
					uniqueNames.add(name);
				}
			}
		}

		
		// Add paths for manually installed packages
		// ${XPACK_REPO_PATH}/<name>
		IPath repoPath = XpackUtils.getRepoPath();
		File repoFolder = repoPath.toFile();
		if (repoFolder.isDirectory()) {

			for (String uniqueName : uniqueNames) {
				IPath path = repoPath.append(uniqueName);

				File p2Folder = path.toFile();
				if (p2Folder.isDirectory()) {
					newSearchPath = path.toPortableString() + EclipseUtils.getPathSeparator() + newSearchPath;
					if (Activator.getInstance().isDebugging()) {
						System.out.println("DefaultPreferences.searchLatestExecutable() prepend \""
								+ path.toPortableString() + "\" to path ");
					}
				}
			}
		}

		// Add actual xPack paths, as installed by xpm.
		// Iterate in reverse order.
		for (int i = xpackNames.length - 1; i >= 0; --i) {
			if (!xpackNames[i].isEmpty()) {
				// Add system xPack path in front of the search path.
				String xpackPath = XpackUtils.getSysPackPath(xpackNames[i]).toPortableString();
				newSearchPath = xpackPath + EclipseUtils.getPathSeparator() + newSearchPath;
				if (Activator.getInstance().isDebugging()) {
					System.out.println(
							"DefaultPreferences.searchLatestExecutable() prepend \"" + xpackPath + "\" to path ");
				}
				
				// Add user xPack path in front of the search path.
				xpackPath = XpackUtils.getPackPath(xpackNames[i]).toPortableString();
				newSearchPath = xpackPath + EclipseUtils.getPathSeparator() + newSearchPath;
				if (Activator.getInstance().isDebugging()) {
					System.out.println(
							"DefaultPreferences.searchLatestExecutable() prepend \"" + xpackPath + "\" to path ");
				}
			}
		}
		// At his point, the actual xPack paths are in front of the list;
		// the selection is still done by date, but in case of identical date,
		// the xPacks are preferred.

		String resolvedPath = EclipseUtils.performStringSubstitution(newSearchPath);
		if (resolvedPath == null || resolvedPath.isEmpty()) {
			return null;
		}

		// Split into multiple paths.
		String[] paths = resolvedPath.split(EclipseUtils.getPathSeparator());
		if (paths.length == 0) {
			return null;
		}

		if (EclipseUtils.isWindows()) {
			executableName = addExeExtension(executableName);
		}

		Map<Long, String> map = new HashMap<Long, String>();
		// Try paths in order; collect dates.
		for (String path : paths) {
			searchExecutable(path, subFolder, executableName, map);
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

	/**
	 * 
	 * @param folder
	 * @param subFolder      may be null; usually "bin".
	 * @param executableName
	 * @param map
	 */
	private void searchExecutable(String folder, String subFolder, final String executableName, Map<Long, String> map) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.searchExecutable(\"" + folder + "\", \"" + subFolder + "\", \""
					+ executableName + "\") ");
		}
		File local = new File(folder);
		if (!local.isDirectory()) {
			// System.out.println(folder + " not a folder");
			return;
		}

		// Enumerate the version folders; in each, check if the executable is present.
		local.listFiles(new FilenameFilter() {

			/**
			 * Filter to select only
			 */
			@Override
			public boolean accept(File dir, String name) {
				IPath versionPath = (new Path(dir.getAbsolutePath())).append(name);
				IPath basePath;
				if (subFolder != null) {
					// If there is a .content subfolder, use it.
					basePath = versionPath.append(".content").append(subFolder);
					if (!basePath.toFile().isDirectory()) {
						basePath = versionPath.append(subFolder);
					}
				} else {
					basePath = versionPath;
				}
				IPath path = basePath.append(executableName);

				File file = path.toFile();
				if (file.isFile()) {
					File versioFolder = versionPath.toFile();
					Long key = new Long(versioFolder.lastModified());
					map.put(key, basePath.toPortableString());

					if (Activator.getInstance().isDebugging()) {
						System.out.println("DefaultPreferences.searchExecutable(\"" + folder + "\", \"" + subFolder
								+ "\", \"" + executableName + "\") = add \"" + basePath.toPortableString() + "\" "
								+ new Date(key.longValue()));
					}
					return true;
				}
				return false;
			}
		});
	}

	// ------------------------------------------------------------------------
}
