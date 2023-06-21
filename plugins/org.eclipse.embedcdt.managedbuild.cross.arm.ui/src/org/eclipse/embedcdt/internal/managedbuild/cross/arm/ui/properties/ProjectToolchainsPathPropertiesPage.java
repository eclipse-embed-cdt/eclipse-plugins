/*******************************************************************************
 * Copyright (c) 2015, 2023 Liviu Ionescu and others.
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

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.Activator;
import org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.Messages;
import org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.preferences.GlobalToolchainsPathsPreferencesPage;
import org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.preferences.WorkspaceToolchainsPathsPreferencesPage;
import org.eclipse.embedcdt.managedbuild.cross.arm.core.Option;
import org.eclipse.embedcdt.managedbuild.cross.arm.core.ToolchainDefinition;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.DefaultPreferences;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.ui.FieldEditorPropertyPage;
import org.eclipse.embedcdt.ui.LabelFakeFieldEditor;
import org.eclipse.embedcdt.ui.XpackDirectoryNotStrictFieldEditor;
import org.eclipse.embedcdt.ui.preferences.ScopedPreferenceStoreWithoutDefaults;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.dialogs.PreferencesUtil;

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

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	protected void createFieldEditors() {
		final Composite parent = getFieldEditorParent();
		final GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		parent.setLayout(layout);

		Link link = new Link(parent, SWT.NONE);
		link.setText(Messages.ProjectToolchainsPathPropertiesPage_GlobalSettings_link);
		link.setLayoutData(GridDataFactory.fillDefaults().create());
		link.addListener(SWT.Selection, e -> {
			PreferencesUtil
					.createPreferenceDialogOn(parent.getShell(), GlobalToolchainsPathsPreferencesPage.ID, new String[] {
							WorkspaceToolchainsPathsPreferencesPage.ID, GlobalToolchainsPathsPreferencesPage.ID }, null)
					.open();
		});
		link = new Link(parent, SWT.NONE);
		link.setText(Messages.ProjectToolchainsPathPropertiesPage_WorkspaceSettings_link);
		link.setLayoutData(GridDataFactory.fillDefaults().create());
		link.addListener(SWT.Selection, e -> {
			PreferencesUtil.createPreferenceDialogOn(parent.getShell(), WorkspaceToolchainsPathsPreferencesPage.ID,
					new String[] { WorkspaceToolchainsPathsPreferencesPage.ID,
							GlobalToolchainsPathsPreferencesPage.ID },
					null).open();
		});

		Map<ToolchainDefinition, Set<String>> toolchains = new TreeMap<>(
				Comparator.comparing(ToolchainDefinition::getName));

		Object element = getElement();
		if (element instanceof IProject) {
			IProject project = (IProject) element;
			IConfiguration[] configs = EclipseUtils.getConfigurationsForProject(project);
			boolean allConfigsUseSameToolchain = true;
			ToolchainDefinition toolchainInUse = null;
			if (configs != null) {
				for (IConfiguration config : configs) {
					IToolChain toolchain = config.getToolChain();
					if (toolchain == null) {
						continue;
					}
					IOption optionId = toolchain.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_ID);
					if (optionId != null) {
						try {
							String toolchainId = optionId.getStringValue();
							int ix = ToolchainDefinition.findToolchainById(toolchainId);

							ToolchainDefinition toolchainDefinition = ToolchainDefinition.getToolchain(ix);
							if (toolchainInUse == null) {
								toolchainInUse = toolchainDefinition;
							} else if (!toolchainInUse.equals(toolchainDefinition)) {
								allConfigsUseSameToolchain = false;
							}
							var buildConfigsSet = toolchains.computeIfAbsent(toolchainDefinition, x -> new TreeSet<>());
							buildConfigsSet.add(config.getName());
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

							ToolchainDefinition toolchainDefinition = ToolchainDefinition.getToolchain(ix);
							if (toolchainInUse == null) {
								toolchainInUse = toolchainDefinition;
							} else if (!toolchainInUse.equals(toolchainDefinition)) {
								allConfigsUseSameToolchain = false;
							}
							var buildConfigsSet = toolchains.computeIfAbsent(toolchainDefinition, x -> new TreeSet<>());
							buildConfigsSet.add(config.getName());
						} catch (BuildException e) {
						} catch (IndexOutOfBoundsException e) {
						}
					}
				}
			}

			if (allConfigsUseSameToolchain && toolchainInUse != null) {
				Set<String> set = toolchains.get(toolchainInUse);
				// If all the configurations use the same toolchain, don't display
				// any of the configurations in the UI
				set.clear();
			}
		}

		if (toolchains.isEmpty()) {
			try {
				String toolchainId = fPersistentPreferences.getToolchainId();
				int ix = ToolchainDefinition.findToolchainById(toolchainId);
				toolchains.computeIfAbsent(ToolchainDefinition.getToolchain(ix), x -> Set.of());
			} catch (IndexOutOfBoundsException e) {
			}
		}

		if (toolchains.isEmpty()) {
			try {
				String toolchainName = fPersistentPreferences.getToolchainName();
				int ix = ToolchainDefinition.findToolchainByName(toolchainName);
				toolchains.computeIfAbsent(ToolchainDefinition.getToolchain(ix), x -> Set.of());
			} catch (IndexOutOfBoundsException e) {
			}
		}

		if (toolchains.isEmpty()) {
			int ix = ToolchainDefinition.getDefault();
			toolchains.computeIfAbsent(ToolchainDefinition.getToolchain(ix), x -> Set.of());
		}

		final Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.GlobalToolchainsPathsPreferencesPage_ToolchainPathGroup_label);
		GridLayout groupLayout = new GridLayout(4, false);
		group.setLayout(groupLayout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		boolean first = true;
		for (var entry : toolchains.entrySet()) {
			if (first) {
				first = false;
			} else {
				Label verticalSpacer = new Label(group, SWT.NONE);
				GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
				verticalSpacer.setLayoutData(layoutData);
				verticalSpacer.setText("");
			}

			ToolchainDefinition toolchain = entry.getKey();
			var configSet = entry.getValue();

			Label message = new Label(group, SWT.WRAP);
			GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
			message.setLayoutData(layoutData);
			if (configSet.isEmpty()) {
				message.setText(Messages.ProjectToolchainsPathPropertiesPage_ToolchainMessageAllConfigs_label);
			} else {
				String collected = String.join(", ", configSet);
				message.setText(NLS.bind(Messages.ProjectToolchainsPathPropertiesPage_ToolchainMessageSomeConfigs_label,
						collected));
			}
			PixelConverter pixelConverter = new PixelConverter(message);
			layoutData.widthHint = pixelConverter
					.convertWidthInCharsToPixels(Math.min(message.getText().length(), 100));

			FieldEditor labelField = new LabelFakeFieldEditor(toolchain.getFullName(),
					Messages.ToolsPaths_ToolchainName_label, group);
			labelField.fillIntoGrid(group, 4);
			addField(labelField);

			boolean isStrict = fDefaultPreferences.getBoolean(PersistentPreferences.PROJECT_TOOLCHAIN_PATH_STRICT,
					true);

			String[] xpackNames = fDefaultPreferences.getToolchainXpackNames(toolchain.getId(), toolchain.getName());

			String key = PersistentPreferences.getToolchainKey(toolchain.getId(), toolchain.getName());
			FieldEditor toolchainPathField = new XpackDirectoryNotStrictFieldEditor(xpackNames, key,
					Messages.ToolchainPaths_label, group, isStrict);
			toolchainPathField.fillIntoGrid(group, 4);
			addField(toolchainPathField);
		}

		Label verticalSpacer = new Label(group, SWT.NONE);
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		verticalSpacer.setLayoutData(layoutData);
		verticalSpacer.setText("");

		Label macOSMessage = new Label(group, SWT.NONE);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		macOSMessage.setLayoutData(layoutData);
		macOSMessage.setText(Messages.SetCrossCommandWizardPage_text);

		// Layout need to be reset after fields are added
		group.setLayout(groupLayout);
	}

	@Override
	protected void adjustGridLayout() {
		// do nothing as we are manually handling the grid layout in createFieldEditors
	}

	// ------------------------------------------------------------------------
}
