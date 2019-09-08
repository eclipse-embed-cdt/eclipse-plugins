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

package ilg.gnumcueclipse.managedbuild.cross.ui.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;

import ilg.gnumcueclipse.core.preferences.ScopedPreferenceStoreWithoutDefaults;
import ilg.gnumcueclipse.core.ui.FieldEditorPropertyPage;
import ilg.gnumcueclipse.core.ui.XpackDirectoryNotStrictFieldEditor;
import ilg.gnumcueclipse.managedbuild.cross.Activator;
import ilg.gnumcueclipse.managedbuild.cross.preferences.DefaultPreferences;
import ilg.gnumcueclipse.managedbuild.cross.preferences.PersistentPreferences;
import ilg.gnumcueclipse.managedbuild.cross.ui.Messages;

public class BuildToolsProjectPathPropertiesPage extends FieldEditorPropertyPage {

	// ------------------------------------------------------------------------

	public static final String ID = "ilg.gnumcueclipse.managedbuild.cross.ui.properties.toolsPage";

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
