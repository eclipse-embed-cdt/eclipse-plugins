/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnumcueclipse.core;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.github.zafarkhaja.semver.Version;

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
	
	public static IPath getRepoPath() {

		Map<String, String> env = System.getenv();
		String folder = env.get("XPACKS_REPO_FOLDER");
		
		IPath path;
		if (folder != null) {
			path = new Path(folder);
			if (path.toFile().isDirectory()) {
				return path;
			}
		}

		path = getRepoBasePath();
		if (path != null) {
			return path.append("xPacks");
		}

		return null;
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

	public static String[] getPackVersions(String[] packNames) {

		List<String> list = new LinkedList<String>();

		for (String packName : packNames) {
			IPath packPath = getPackPath(packName);
			File folder = packPath.toFile();
			if (folder.isDirectory()) {
				
				folder.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						IPath path = (new Path(dir.getAbsolutePath())).append(name);
						if (path.toFile().isDirectory()) {
							IPath packagePath = path.append("package.json");
							if (packagePath.toFile().isFile()) {
								if (".link".equals(name)) {
									list.add("current");
								} else {
									list.add(name);
								}
								return true;
							}

						}
						return false;
					}

				});
			}
		}
		
		if (list.isEmpty()) {
			return new String[] {};		
		}


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
