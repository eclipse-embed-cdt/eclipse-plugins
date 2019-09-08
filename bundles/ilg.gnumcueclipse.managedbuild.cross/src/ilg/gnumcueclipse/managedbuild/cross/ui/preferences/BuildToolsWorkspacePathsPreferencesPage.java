/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnumcueclipse.managedbuild.cross.ui.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ilg.gnumcueclipse.core.preferences.ScopedPreferenceStoreWithoutDefaults;
import ilg.gnumcueclipse.core.ui.XpackDirectoryNotStrictFieldEditor;
import ilg.gnumcueclipse.managedbuild.cross.Activator;
import ilg.gnumcueclipse.managedbuild.cross.preferences.DefaultPreferences;
import ilg.gnumcueclipse.managedbuild.cross.preferences.PersistentPreferences;
import ilg.gnumcueclipse.managedbuild.cross.ui.Messages;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */
public class BuildToolsWorkspacePathsPreferencesPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	// ------------------------------------------------------------------------

	public static final String ID = "ilg.gnumcueclipse.managedbuild.cross.ui.preferencePage.workspaceToolsPaths";

	// ------------------------------------------------------------------------

	private DefaultPreferences fDefaultPreferences;

	// ------------------------------------------------------------------------

	public BuildToolsWorkspacePathsPreferencesPage() {
		super(GRID);

		fDefaultPreferences = new DefaultPreferences(Activator.PLUGIN_ID);

		setPreferenceStore(new ScopedPreferenceStoreWithoutDefaults(InstanceScope.INSTANCE, Activator.PLUGIN_ID));

		setDescription(Messages.WorkspaceBuildToolsPathsPreferencesPage_description);
	}

	// ------------------------------------------------------------------------

	// Contributed by IWorkbenchPreferencePage
	@Override
	public void init(IWorkbench workbench) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("WorkspaceToolsPathsPreferencePage.init()");
		}
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	protected void createFieldEditors() {

		boolean isStrict;
		isStrict = fDefaultPreferences.getBoolean(PersistentPreferences.WORKSPACE_BUILDTOOLS_PATH_STRICT, true);

		String[] xpackNames = fDefaultPreferences.getBuildToolsXpackNames();

		FieldEditor buildToolsPathField = new XpackDirectoryNotStrictFieldEditor(xpackNames,
				PersistentPreferences.BUILD_TOOLS_PATH_KEY, Messages.BuildToolsPaths_label, getFieldEditorParent(),
				isStrict);

		addField(buildToolsPathField);
	}

	// ------------------------------------------------------------------------
}
