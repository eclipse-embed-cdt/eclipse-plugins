/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package org.eclipse.embedcdt.core;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.embedcdt.core.zafarkhaja.semver.Version;

public class XpackUtils {

	public static boolean hasPackageJson(IProject project) {

		if (project == null) {
			return false;
		}
		IPath projectPath = project.getLocation();
		File packageFile = projectPath.append("package.json").toFile();
		if (!packageFile.exists()) {
			return false;
		}
		if (packageFile.isDirectory()) {
			return false;
		}

		return true;
	}

	// DEPRECATED
	public static IPath getRepoBasePath() {

		Map<String, String> env = System.getenv();

		if (EclipseUtils.isMacOSX()) {
			String homeFolder = env.get("HOME");
			IPath path = new Path(homeFolder);
			return path.append("/Library");
		} else if (EclipseUtils.isLinux()) {
			String homeFolder = env.get("HOME");
			IPath path = new Path(homeFolder);
			return path.append("/opt");
		} else if (EclipseUtils.isWindows()) {
			String homeFolder = env.get("APPDATA");
			IPath path = new Path(homeFolder);
			return path;
		}
		return null;
	}

	public static IPath[] getRepoBasePaths() {

		Map<String, String> env = System.getenv();

		if (EclipseUtils.isMacOSX()) {
			IPath[] paths = new IPath[1];
			String homeFolder = env.get("HOME");
			paths[0] = (new Path(homeFolder)).append("/Library");
			return paths;
		} else if (EclipseUtils.isLinux()) {
			IPath[] paths = new IPath[2];
			String homeFolder = env.get("HOME");
			paths[0] = (new Path(homeFolder)).append("/.local");
			paths[1] = (new Path(homeFolder)).append("/opt");
			return paths;
		} else if (EclipseUtils.isWindows()) {
			IPath[] paths = new IPath[1];
			String homeFolder = env.get("APPDATA");
			paths[0] = (new Path(homeFolder));
			return paths;
		}
		return null;
	}

	// DEPRECATED
	public static IPath getSysRepoBasePath() {

		Map<String, String> env = System.getenv();

		if (EclipseUtils.isMacOSX()) {
			IPath path = new Path("/opt");
			return path;
		} else if (EclipseUtils.isLinux()) {
			IPath path = new Path("/opt");
			return path;
		} else if (EclipseUtils.isWindows()) {
			String progFolder = env.get("ProgramFiles");
			IPath path = new Path(progFolder);
			return path;
		}
		return null;
	}

	public static IPath[] getSysRepoBasePaths() {

		Map<String, String> env = System.getenv();

		if (EclipseUtils.isMacOSX()) {
			IPath[] paths = new IPath[1];
			paths[0] = new Path("/opt");
			return paths;
		} else if (EclipseUtils.isLinux()) {
			IPath[] paths = new IPath[1];
			paths[0] = new Path("/opt");
			return paths;
		} else if (EclipseUtils.isWindows()) {
			IPath[] paths = new IPath[1];
			String progFolder = env.get("ProgramFiles");
			paths[0] = new Path(progFolder);
			return paths;
		}
		return null;
	}

	public static IPath getRepoPath() {

		Map<String, String> env = System.getenv();
		String folder = env.get("XPACKS_REPO_FOLDER");

		IPath repoPath;
		if (folder != null) {
			repoPath = new Path(folder);
			if (repoPath.toFile().isDirectory()) {
				return repoPath;
			}
		}

		IPath[] paths;
		paths = getRepoBasePaths();
		if (paths != null) {
			for (IPath path : paths) {
				repoPath = path.append("xPacks");
				if (repoPath.toFile().isDirectory()) {
					return repoPath;
				}
			}
		}

		return paths[0].append("xPacks");
	}

	public static IPath getSysRepoPath() {

		Map<String, String> env = System.getenv();
		String folder = env.get("XPACKS_SYSTEM_FOLDER");

		IPath repoPath;
		if (folder != null) {
			repoPath = new Path(folder);
			if (repoPath.toFile().isDirectory()) {
				return repoPath;
			}
		}

		IPath[] paths;
		paths = getSysRepoBasePaths();
		if (paths != null) {
			for (IPath path : paths) {
				repoPath = path.append("xPacks");
				if (repoPath.toFile().isDirectory()) {
					return repoPath;
				}
			}
		}

		return paths[0].append("xPacks");
	}

	public static IPath getPackPath(String packName) {
		IPath repoPath = getRepoPath();
		String arr[] = packName.split("[/]", 2);
		if (arr.length == 1) {
			return repoPath.append(arr[0]);
		} else {
			return repoPath.append(arr[0]).append(arr[1]);
		}
	}

	public static IPath getSysPackPath(String packName) {
		IPath repoPath = getSysRepoPath();
		String arr[] = packName.split("[/]", 2);
		if (arr.length == 1) {
			return repoPath.append(arr[0]);
		} else {
			return repoPath.append(arr[0]).append(arr[1]);
		}
	}

	public static String[] getPackVersions(String[] packNames) {

		Set<String> versions = new LinkedHashSet<>();

		FilenameFilter filter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				IPath path = (new Path(dir.getAbsolutePath())).append(name);
				if (path.toFile().isDirectory()) {
					IPath packagePath = path.append("package.json");
					if (packagePath.toFile().isFile()) {
						if (".link".equals(name)) {
							versions.add("current");
						} else {
							versions.add(name);
						}
						return true;
					}

				}
				return false;
			}

		};

		for (String packName : packNames) {
			// Enumerate user home versions.
			IPath packPath = getPackPath(packName);
			File folder = packPath.toFile();
			if (folder.isDirectory()) {
				folder.listFiles(filter);
			}

			// Enumerate system version.
			packPath = getSysPackPath(packName);
			folder = packPath.toFile();
			if (folder.isDirectory()) {
				folder.listFiles(filter);
			}
		}

		if (versions.isEmpty()) {
			return new String[] {};
		}

		List<String> list = new LinkedList<>(versions);

		Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String lhs, String rhs) {
				if (lhs.equals("current")) {
					return -1;
				}
				if (rhs.equals("current")) {
					return 1;
				}

				Version vlhs = Version.valueOf(lhs);
				Version vrhs = Version.valueOf(rhs);

				// Reverse order.
				return vrhs.compareTo(vlhs);
			}
		});
		return list.toArray(new String[list.size()]);
	}
}
