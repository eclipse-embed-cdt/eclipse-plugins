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
 *     Liviu Ionescu - initial implementation.
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.managedbuild.cross.riscv.ui.preferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.internal.managedbuild.cross.riscv.ui.Messages;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.DefaultPreferences;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.managedbuild.cross.riscv.core.Option;
import org.eclipse.embedcdt.managedbuild.cross.riscv.ui.Activator;
import org.eclipse.embedcdt.ui.LabelFakeFieldEditor;
import org.eclipse.embedcdt.ui.XpackDirectoryNotStrictFieldEditor;
import org.eclipse.embedcdt.ui.preferences.ScopedPreferenceStoreWithoutDefaults;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

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
public class WorkspaceToolchainsPathsPreferencesPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	// ------------------------------------------------------------------------

	public static final String ID = "org.eclipse.embedcdt.managedbuild.cross.riscv.preferencePage.workspaceToolchainsPaths";

	// ------------------------------------------------------------------------

	private PersistentPreferences fPersistentPreferences;
	private DefaultPreferences fDefaultPreferences;

	// ------------------------------------------------------------------------

	public WorkspaceToolchainsPathsPreferencesPage() {
		super(GRID);

		fPersistentPreferences = Activator.getInstance().getPersistentPreferences();
		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();

		setPreferenceStore(new ScopedPreferenceStoreWithoutDefaults(InstanceScope.INSTANCE, Activator.CORE_PLUGIN_ID));

		setDescription(Messages.WorkspaceToolchainsPathsPreferencesPage_description);
	}

	// ------------------------------------------------------------------------

	// Contributed by IWorkbenchPreferencePage
	@Override
	public void init(IWorkbench workbench) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("riscv.WorkspaceToolchainsPathsPreferencesPage.init()");
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

		FieldEditor toolchainNameField = new ToolchainsFieldEditor(PersistentPreferences.TOOLCHAIN_NAME_KEY,
				Messages.ToolchainName_label, getFieldEditorParent());
		addField(toolchainNameField);

		Set<String> toolchainNames = new HashSet<String>();

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; ++i) {
			IConfiguration[] configs = EclipseUtils.getConfigurationsForProject(projects[i]);
			if (configs != null) {
				for (int j = 0; j < configs.length; ++j) {
					IToolChain toolchain = configs[j].getToolChain();
					if (toolchain == null) {
						continue;
					}
					IOption option = toolchain.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME);
					if (option == null) {
						continue;
					}
					try {
						String name = option.getStringValue();
						if (!name.isEmpty()) {
							toolchainNames.add(name);
						}
					} catch (BuildException e) {
						;
					}
				}
			}
		}

		if (toolchainNames.isEmpty()) {
			toolchainNames.add(fPersistentPreferences.getToolchainName());
		}

		for (String toolchainName : toolchainNames) {

			FieldEditor labelField = new LabelFakeFieldEditor(toolchainName, Messages.ToolsPaths_ToolchainName_label,
					getFieldEditorParent());
			addField(labelField);

			isStrict = fDefaultPreferences.getBoolean(PersistentPreferences.WORKSPACE_TOOLCHAIN_PATH_STRICT, true);

			String[] xpackNames = fDefaultPreferences.getToolchainXpackNames(toolchainName);

			String key = PersistentPreferences.getToolchainKey(toolchainName);
			FieldEditor toolchainPathField = new XpackDirectoryNotStrictFieldEditor(xpackNames, key,
					Messages.ToolchainPaths_label, getFieldEditorParent(), isStrict);

			addField(toolchainPathField);
		}
	}

	// ------------------------------------------------------------------------
}
