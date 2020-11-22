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

package org.eclipse.embedcdt.managedbuild.cross.ui.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.embedcdt.internal.managedbuild.cross.ui.Messages;
import org.eclipse.embedcdt.managedbuild.cross.Activator;
import org.eclipse.embedcdt.managedbuild.cross.preferences.DefaultPreferences;
import org.eclipse.embedcdt.managedbuild.cross.preferences.PersistentPreferences;
import org.eclipse.embedcdt.ui.FieldEditorPropertyPage;
import org.eclipse.embedcdt.ui.XpackDirectoryNotStrictFieldEditor;
import org.eclipse.embedcdt.ui.preferences.ScopedPreferenceStoreWithoutDefaults;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;

public class BuildToolsProjectPathPropertiesPage extends FieldEditorPropertyPage {

	// ------------------------------------------------------------------------

	public static final String ID = "org.eclipse.embedcdt.managedbuild.cross.ui.properties.toolsPage";

	// ------------------------------------------------------------------------

	private DefaultPreferences fDefaultPreferences;

	// ------------------------------------------------------------------------

	public BuildToolsProjectPathPropertiesPage() {
		super(GRID);

		fDefaultPreferences = new DefaultPreferences(Activator.PLUGIN_ID);

		setDescription(Messages.ProjectBuildToolsPathsPropertiesPage_description);
	}

	// ------------------------------------------------------------------------

	protected IPreferenceStore doGetPreferenceStore() {

		Object element = getElement();
		if (element instanceof IProject) {
			return new ScopedPreferenceStoreWithoutDefaults(new ProjectScope((IProject) element), Activator.PLUGIN_ID);
		}
		return null;
	}

	@Override
	protected void createFieldEditors() {
		boolean isStrict = fDefaultPreferences.getBoolean(PersistentPreferences.PROJECT_BUILDTOOLS_PATH_STRICT, true);

		String[] xpackNames = fDefaultPreferences.getBuildToolsXpackNames();

		FieldEditor buildToolsPathField = new XpackDirectoryNotStrictFieldEditor(xpackNames,
				PersistentPreferences.BUILD_TOOLS_PATH_KEY, Messages.BuildToolsPaths_label, getFieldEditorParent(),
				isStrict);

		addField(buildToolsPathField);
	}

	// ------------------------------------------------------------------------
}
