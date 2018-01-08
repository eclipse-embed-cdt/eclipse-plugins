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
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

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

	public static IPath getRepoPath() {

		Map<String, String> env = System.getenv();
		String folder = env.get("XPACKS_REPO_FOLDER");
		if (folder != null) {
			IPath path = new Path(folder);
			if (path.toFile().isDirectory()) {
				return path;
			}
		}

		if (EclipseUtils.isMacOSX()) {
			String homeFolder = env.get("HOME");
			IPath path = new Path(homeFolder);
			return path.append("/Library/xPacks");
		} else if (EclipseUtils.isLinux()) {
			String homeFolder = env.get("HOME");
			IPath path = new Path(homeFolder);
			return path.append("/opt/xPacks");
		} else if (EclipseUtils.isWindows()) {
			String homeFolder = env.get("APPDATA");
			IPath path = new Path(homeFolder);
			return path.append("xPacks");
		}
		return null;
	}
}
