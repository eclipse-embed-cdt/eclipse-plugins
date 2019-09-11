/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnumcueclipse.packs.core;

import java.io.File;
import java.util.Map;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.core.XpackUtils;

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
			packagesPath = XpackUtils.getRepoBasePath();
			if (EclipseUtils.isLinux()) {
				packagesPath = packagesPath.append("cmsis-packs");
			} else {
				packagesPath = packagesPath.append("CMSIS-Packs");
			}
		}
		
		IPreferenceStore store = Preferences.getPreferenceStore();
		store.setDefault(Preferences.PACKS_FOLDER_PATH, packagesPath.toOSString());
		store.setDefault(Preferences.PACKS_MACRO_NAME, Preferences.DEFAULT_MACRO_NAME);

		// Read back the actual value.
		String folderPath = store.getString(Preferences.PACKS_FOLDER_PATH);

		File packagesFolder = new File(folderPath);
		if (!packagesFolder.exists()) {
			packagesFolder.mkdir();
			System.out.println("Folder \"" + packagesFolder + "\" created");
		} if (!packagesFolder.isDirectory()) {
			Activator.log("\"" + folderPath + "\" present, but not a folder; not good.");
		}
	}
}
