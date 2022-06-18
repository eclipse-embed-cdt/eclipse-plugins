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
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.properties;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.Activator;
import org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.Messages;
import org.eclipse.embedcdt.managedbuild.cross.arm.core.Option;
import org.eclipse.embedcdt.managedbuild.cross.arm.core.ToolchainDefinition;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.DefaultPreferences;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.ui.FieldEditorPropertyPage;
import org.eclipse.embedcdt.ui.LabelFakeFieldEditor;
import org.eclipse.embedcdt.ui.XpackDirectoryNotStrictFieldEditor;
import org.eclipse.embedcdt.ui.preferences.ScopedPreferenceStoreWithoutDefaults;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;

public class ProjectToolchainsPathPropertiesPage extends FieldEditorPropertyPage {

	// ------------------------------------------------------------------------

	public static final String ID = "org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.properties.toolchainsPage";

	// ------------------------------------------------------------------------

	private PersistentPreferences fPersistentPreferences;
	private DefaultPreferences fDefaultPreferences;

	// ------------------------------------------------------------------------

	public ProjectToolchainsPathPropertiesPage() {
		super(GRID);

		fPersistentPreferences = Activator.getInstance().getPersistentPreferences();
		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();

		setDescription(Messages.ProjectToolchainsPathsPropertiesPage_description);
	}

	// ------------------------------------------------------------------------

	@Override
	protected IPreferenceStore doGetPreferenceStore() {

		Object element = getElement();
		if (element instanceof IProject) {
			return new ScopedPreferenceStoreWithoutDefaults(new ProjectScope((IProject) element),
					Activator.CORE_PLUGIN_ID);
		}
		return null;
	}

	@Override
	protected void createFieldEditors() {

		boolean isStrict;

		Set<ToolchainDefinition> toolchains = new HashSet<>();

		Object element = getElement();
		if (element instanceof IProject) {
			// TODO: get project toolchain name. How?
			IProject project = (IProject) element;
			IConfiguration[] configs = EclipseUtils.getConfigurationsForProject(project);
			if (configs != null) {
				for (int i = 0; i < configs.length; ++i) {
					IToolChain toolchain = configs[i].getToolChain();
					if (toolchain == null) {
						continue;
					}
					IOption optionId = toolchain.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_ID);
					if (optionId != null) {
						try {
							String toolchainId = optionId.getStringValue();
							int ix = ToolchainDefinition.findToolchainById(toolchainId);
							toolchains.add(ToolchainDefinition.getToolchain(ix));
							continue;
						} catch (BuildException e) {
						} catch (IndexOutOfBoundsException e) {
						}
					}
					IOption optionName = toolchain.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME);
					if (optionName != null) {
						try {
							String toolchainName = optionName.getStringValue();
							int ix = ToolchainDefinition.findToolchainByName(toolchainName);
							toolchains.add(ToolchainDefinition.getToolchain(ix));
							continue;
						} catch (BuildException e) {
						} catch (IndexOutOfBoundsException e) {
						}
					}
				}
			}
		}

		if (toolchains.isEmpty()) {
			try {
				String toolchainId = fPersistentPreferences.getToolchainId();
				int ix = ToolchainDefinition.findToolchainById(toolchainId);
				toolchains.add(ToolchainDefinition.getToolchain(ix));
			} catch (IndexOutOfBoundsException e) {
			}
		}

		if (toolchains.isEmpty()) {
			try {
				String toolchainName = fPersistentPreferences.getToolchainName();
				int ix = ToolchainDefinition.findToolchainByName(toolchainName);
				toolchains.add(ToolchainDefinition.getToolchain(ix));
			} catch (IndexOutOfBoundsException e) {
			}
		}

		if (toolchains.isEmpty()) {
			int ix = ToolchainDefinition.getDefault();
			toolchains.add(ToolchainDefinition.getToolchain(ix));
		}

		for (ToolchainDefinition toolchain : toolchains) {

			FieldEditor labelField = new LabelFakeFieldEditor(toolchain.getFullName(),
					Messages.ToolsPaths_ToolchainName_label, getFieldEditorParent());
			addField(labelField);

			isStrict = fDefaultPreferences.getBoolean(PersistentPreferences.PROJECT_TOOLCHAIN_PATH_STRICT, true);

			String[] xpackNames = fDefaultPreferences.getToolchainXpackNames(toolchain.getId(), toolchain.getName());

			String key = PersistentPreferences.getToolchainKey(toolchain.getId(), toolchain.getName());
			FieldEditor toolchainPathField = new XpackDirectoryNotStrictFieldEditor(xpackNames, key,
					Messages.ToolchainPaths_label, getFieldEditorParent(), isStrict);

			addField(toolchainPathField);
		}

		Label message = new Label(getFieldEditorParent(), SWT.NONE);
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		message.setLayoutData(layoutData);
		message.setText(Messages.SetCrossCommandWizardPage_text);
	}

	// ------------------------------------------------------------------------
}
