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

package ilg.gnuarmeclipse.managedbuild.cross.ui;

import ilg.gnuarmeclipse.managedbuild.cross.Activator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class DefaultPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		System.out
				.println("DefaultPreferenceInitializer.initializeDefaultPreferences()");

		IPreferenceStore store = Activator.getInstance().getPreferenceStore();

		String value = DefaultPreferences.discoverBuildToolsPath();
		if (!value.isEmpty()) {
			System.out.println(PersistentPreferences.BUILD_TOOLS_PATH + "="
					+ value);
			store.setDefault(PersistentPreferences.BUILD_TOOLS_PATH, value);
		}
	}

}
