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

package ilg.gnuarmeclipse.packs.ui.preferences;

import ilg.gnuarmeclipse.packs.Activator;

import java.io.File;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialise default preference values.
 */
public class FolderInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
	 * initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {

		// System.out
		// .println("PreferenceInitializer.initializeDefaultPreferences()");

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();

		// Get workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		// Compute workspace/../Packages
		IPath workspacePath = workspace.getRoot().getLocation();
		IPath workspaceParentPath = workspacePath.removeLastSegments(1);
		IPath packagesPath = workspaceParentPath.append("Packages");
		// System.out.println(packagesPath.toOSString());

		// Create folder if needed
		File packagesFolder = new File(packagesPath.toOSString());
		if (!packagesFolder.exists()) {
			packagesFolder.mkdir();
			// System.out.println(packagesFolder + " created");
		}

		store.setDefault(FolderConstants.P_FOLDER_PATH,
				packagesPath.toOSString());

		store.setDefault(FolderConstants.P_MACRO_NAME,
				FolderConstants.DEFAULT_MACRO_NAME);
	}

}
