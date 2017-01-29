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

package ilg.gnuarmeclipse.core.preferences;

import ilg.gnuarmeclipse.core.Activator;
import ilg.gnuarmeclipse.core.AltWindowsRegistry;
import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.core.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.cdt.utils.WindowsRegistry;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

/**
 * Helper class used by debuggers.
 * 
 */
public class Discoverer {

	// ------------------------------------------------------------------------

	private static final String REG_PREFIX = "SOFTWARE";
	private static final String REG32_PREFIX = "SOFTWARE\\Wow6432Node";

	// ------------------------------------------------------------------------

	/**
	 * Find where the executable might have been installed. The returned path is
	 * known to be an existing folder.
	 * 
	 * @param executableName
	 * @param searchPath
	 *            a string with a sequence of folders.
	 * @param binFolder
	 *            a String, usually "bin", or null.
	 * @return a String with the absolute folder path, or null if not found.
	 */
	public static String searchInstallFolder(String executableName, String searchPath, String binFolder) {

		String value = null;

		if (searchPath == null || searchPath.isEmpty()) {
			return null;
		}

		// Resolve ${user.home}
		String resolvedPath = searchPath;
		if (resolvedPath.indexOf("${user.home}") >= 0) {
			String userHome = new Path(System.getProperty("user.home")).toString();
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
		} else if (EclipseUtils.isWindows()) {
			resolvedPath = StringUtils.duplicateBackslashes(resolvedPath);
		}

		// Split into multiple paths.
		String[] paths = resolvedPath.split(EclipseUtils.getPathSeparator());
		if (paths.length == 0) {
			return null;
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Discoverer.searchInstallFolder() resolved path " + resolvedPath);
		}

		// Try paths in order; return the first.
		for (int i = 0; i < paths.length; ++i) {
			value = getLastExecutable(paths[i], binFolder, executableName);
			if (value != null && !value.isEmpty()) {
				return value;
			}
		}

		return null;
	}

	/**
	 * Get key value from registry and validate the executable. The returned
	 * path is known to be an existing folder.
	 * 
	 * @param executableName
	 * @param binFolder
	 *            a String, usually "bin", or null.
	 * @param registrySubKey
	 * @param registryName
	 * @return a String with the absolute folder path, or null if not found.
	 */
	public static String getRegistryInstallFolder(String executableName, String binFolder, String registrySubKey,
			String registryName) {

		String value = null;
		if (EclipseUtils.isWindows()) {

			WindowsRegistry registry = WindowsRegistry.getRegistry();

			if (registry != null) {
				value = getRegistryValue(registry, REG_PREFIX, registrySubKey, registryName);
				if (value == null) {
					// If on 64-bit, check the 32-bit registry too.
					value = getRegistryValue(registry, REG32_PREFIX, registrySubKey, registryName);
				}

				if (binFolder != null && value != null && !value.endsWith("\\" + binFolder)) {
					value += "\\" + binFolder;
				}

				if (value != null) {
					IPath path = new Path(value);
					// Make portable
					value = path.toString(); // includes /bin, if it exists
					if (Activator.getInstance().isDebugging()) {
						System.out.println("Discoverer.getRegistryInstallFolder() " + registryName + " " + value);
					}

					File folder = path.append(executableName).toFile();
					if (folder.isFile()) {
						if (Activator.getInstance().isDebugging()) {
							System.out.println("Discoverer.getRegistryInstallFolder()=" + value);
						}
						return value;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Get the value of a registry key. It first tests the current user key,
	 * then the local machine key.
	 * 
	 * @param registry
	 * @param prefix
	 * @param registrySubKey
	 * @param registryName
	 * @return a String, or null if not found.
	 */
	private static String getRegistryValue(WindowsRegistry registry, String prefix, String registrySubKey,
			String registryName) {

		String value;
		// TODO: remove kludge after SEGGER fixes the bug
		if (!registrySubKey.startsWith("\\SEGGER")) {
			value = registry.getCurrentUserValue(prefix + registrySubKey, registryName);
		} else {
			// Kludge to compensate for SEGGER and CDT bug (the value is
			// terminated with lots of zeroes, more than CDT WindowsRegistry
			// class can handle).
			value = AltWindowsRegistry.query("HKEY_CURRENT_USER\\" + prefix + registrySubKey, registryName);
		}
		if (value == null) {
			value = registry.getLocalMachineValue(prefix + registrySubKey, registryName);
		}

		return value;
	}

	/**
	 * Return the last (in lexicographical order) folder that contain
	 * "bin/executable". If not found, the folder itself is checked.
	 * 
	 * The returned path includes the ending /bin.
	 * 
	 * @param folder
	 * @param binFolder
	 *            a String, usually "bin", or null.
	 * @param executableName
	 * @return a String with the folder absolute path, or null if not found.
	 */
	public static String getLastExecutable(String folderName, final String binFolder, final String executableName) {

		IPath folderPath = new Path(folderName);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Discoverer.getLastExecutable(" + folderPath + ", " + executableName + ")");
		}

		List<String> list = new ArrayList<String>();
		File folder = folderPath.toFile();
		if (!folder.isDirectory()) {
			// System.out.println(folder + " not a folder");
			return null;
		}

		File[] files = folder.listFiles(new FilenameFilter() {

			/**
			 * Filter to select only
			 */
			@Override
			public boolean accept(File dir, String name) {
				IPath path = (new Path(dir.getAbsolutePath())).append(name);

				if (binFolder != null) {
					path = path.append(binFolder).append(executableName);
				} else {
					path = path.append(executableName);
				}
				if (path.toFile().isFile()) {
					return true;
				}
				return false;
			}
		});

		if (files != null && files.length > 0) {

			for (int i = 0; i < files.length; ++i) {
				list.add(files[i].getName());
			}

			// The sort criteria is the lexicographical order on folder name.
			Collections.sort(list);

			// Get the last name in ordered list.
			String last = list.get(list.size() - 1);

			// System.out.println(last);
			IPath path = (new Path(folderName)).append(last);
			if (binFolder != null) {
				path = path.append(binFolder);
			}
			return path.toString();
		} else {
			IPath path = (new Path(folderName));
			if (binFolder != null) {
				path = path.append(binFolder).append(executableName);
			} else {
				path = path.append(executableName);
			}
			folder = path.toFile();
			if (folder.isFile()) {
				// System.out.println(folder + " not a folder");
				path = (new Path(folderName));
				if (binFolder != null) {
					path = path.append(binFolder);
				}
				return path.toString();
			}
		}

		return null;
	}

	// ------------------------------------------------------------------------
}
