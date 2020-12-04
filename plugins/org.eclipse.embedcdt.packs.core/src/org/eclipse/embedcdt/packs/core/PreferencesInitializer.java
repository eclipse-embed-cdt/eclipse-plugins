/*******************************************************************************
 * Copyright (c) 2014, 2020 Liviu Ionescu and others.
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
 *     Alexander Fedorov (ArSysOp) - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.core;

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.core.XpackUtils;

/**
 * Class used to initialise default preference values.
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PreferenceInitializer.initializeDefaultPreferences()");
		}

		// Get workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IPath packagesPath = null;

		Map<String, String> env = System.getenv();
		String envFolder = env.get("CMSIS_PACKS_FOLDER");
		if (envFolder != null) {
			IPath path = new Path(envFolder);
			if (path.toFile().isDirectory()) {
				packagesPath = path;
			} else {
				Activator.log("\"" + envFolder + "\" not a folder, ignored.");
			}
		}

		// For compatibility reasons, if workspace/../Packages exists, use it.
		if (packagesPath == null) {
			IPath workspacePath = workspace.getRoot().getLocation();
			IPath workspaceParentPath = workspacePath.removeLastSegments(1);
			IPath path = workspaceParentPath.append("Packages");
			// System.out.println(packagesPath.toOSString());
			File packagesFolder = new File(path.toOSString());
			if (packagesFolder.isDirectory()) {
				packagesPath = path;
			}
		}

		if (packagesPath == null) {
			IPath[] paths = XpackUtils.getRepoBasePaths();
			assert (paths != null);

			for (IPath path : paths) {
				if (EclipseUtils.isLinux()) {
					path = path.append("cmsis-packs");
				} else {
					path = path.append("CMSIS-Packs");
				}
				if (path.toFile().isDirectory()) {
					packagesPath = path;
					break;
				}
			}
			if (packagesPath == null) {
				packagesPath = paths[0];
				if (EclipseUtils.isLinux()) {
					packagesPath = packagesPath.append("cmsis-packs");
				} else {
					packagesPath = packagesPath.append("CMSIS-Packs");
				}
			}
		}

		assert (packagesPath != null);
		IEclipsePreferences node = DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		node.put(Preferences.PACKS_CMSIS_FOLDER_PATH, packagesPath.toOSString());
		node.put(Preferences.PACKS_CMSIS_MACRO_NAME, Preferences.DEFAULT_CMSIS_MACRO_NAME);

		// Read back the actual value.
		String folderPath = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, Preferences.PACKS_CMSIS_FOLDER_PATH, packagesPath.toOSString(), null);

		File packagesFolder = new File(folderPath);
		if (!packagesFolder.exists()) {
			packagesFolder.mkdir();
			System.out.println("Folder \"" + packagesFolder + "\" created");
		}
		if (!packagesFolder.isDirectory()) {
			Activator.log("\"" + folderPath + "\" present, but not a folder; not good.");
		}
	}
}
