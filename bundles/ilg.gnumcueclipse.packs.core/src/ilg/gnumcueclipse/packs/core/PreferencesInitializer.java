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

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialise default preference values.
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {

		// System.out
		// .println("PreferenceInitializer.initializeDefaultPreferences()");

		// Get workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		// Compute workspace/../Packages
		IPath workspacePath = workspace.getRoot().getLocation();
		IPath workspaceParentPath = workspacePath.removeLastSegments(1);
		IPath packagesPath = workspaceParentPath.append("Packages");
		// System.out.println(packagesPath.toOSString());

		IPreferenceStore store = Preferences.getPreferenceStore();
		store.setDefault(Preferences.PACKS_FOLDER_PATH, packagesPath.toOSString());
		store.setDefault(Preferences.PACKS_MACRO_NAME, Preferences.DEFAULT_MACRO_NAME);
		
		// Read back the actual value.
		String folderPath = store.getString(Preferences.PACKS_FOLDER_PATH);

		File packagesFolder = new File(folderPath);
		if (!packagesFolder.exists()) {
			packagesFolder.mkdir();
			if (Activator.getInstance().isDebugging()) {
			  System.out.println(packagesFolder + " created");
			}
		}
	}
}
