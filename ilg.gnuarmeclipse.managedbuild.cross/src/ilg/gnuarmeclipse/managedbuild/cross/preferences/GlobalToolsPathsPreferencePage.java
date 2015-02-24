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

package ilg.gnuarmeclipse.managedbuild.cross.preferences;

import ilg.gnuarmeclipse.managedbuild.cross.Activator;
import ilg.gnuarmeclipse.managedbuild.cross.ui.DefaultPreferences;
import ilg.gnuarmeclipse.managedbuild.cross.ui.Messages;
import ilg.gnuarmeclipse.managedbuild.cross.ui.PersistentPreferences;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

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

public class GlobalToolsPathsPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	// ------------------------------------------------------------------------

	public GlobalToolsPathsPreferencePage() {
		super(GRID);

		setPreferenceStore(new ScopedPreferenceStore(
				ConfigurationScope.INSTANCE, Activator.PLUGIN_ID));

		String toolchainName = PersistentPreferences.getToolchainName();
		setDescription(String.format(
				Messages.GlobalToolsPathsPropertyPage_description,
				toolchainName));
	}

	// ------------------------------------------------------------------------

	// Contributed by IWorkbenchPreferencePage
	@Override
	public void init(IWorkbench workbench) {
		System.out.println("GlobalToolsPathsPage.init()");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */

	@Override
	protected void createFieldEditors() {

		boolean isStrict;
		isStrict = DefaultPreferences.getBoolean(
				PersistentPreferences.GLOBAL_BUILDTOOLS_PATH_STRICT, true);
		FieldEditor buildToolsPathField;
		buildToolsPathField = new DirectoryNotStrictFieldEditor(
				PersistentPreferences.BUILD_TOOLS_PATH_KEY,
				Messages.ToolsPaths_label, getFieldEditorParent(), isStrict);

		addField(buildToolsPathField);

		FieldEditor toolchainNameField = new ToolchainsFieldEditor(
				PersistentPreferences.TOOLCHAIN_NAME_KEY,
				Messages.ToolchainName_label, getFieldEditorParent());
		addField(toolchainNameField);

		String toolchainName = PersistentPreferences.getToolchainName();
		String key = PersistentPreferences.getToolchainKey(toolchainName);

		isStrict = DefaultPreferences.getBoolean(
				PersistentPreferences.GLOBAL_TOOLCHAIN_PATH_STRICT, true);

		FieldEditor toolchainPathField;
		toolchainPathField = new DirectoryNotStrictFieldEditor(key,
				Messages.ToolchainPaths_label, getFieldEditorParent(), isStrict);

		addField(toolchainPathField);
	}

	// ------------------------------------------------------------------------
}
