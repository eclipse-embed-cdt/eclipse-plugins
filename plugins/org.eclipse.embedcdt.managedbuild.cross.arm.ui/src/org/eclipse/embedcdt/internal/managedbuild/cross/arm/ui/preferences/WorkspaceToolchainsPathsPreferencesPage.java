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
 *     Liviu Ionescu - initial implementation.
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.preferences;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.Activator;
import org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.Messages;
import org.eclipse.embedcdt.managedbuild.cross.arm.core.Option;
import org.eclipse.embedcdt.managedbuild.cross.arm.core.ToolchainDefinition;
import org.eclipse.embedcdt.managedbuild.cross.arm.ui.preferences.ToolchainsFieldEditor;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.DefaultPreferences;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.ui.LabelFakeFieldEditor;
import org.eclipse.embedcdt.ui.XpackDirectoryNotStrictFieldEditor;
import org.eclipse.embedcdt.ui.preferences.ScopedPreferenceStoreWithoutDefaults;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
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

	public static final String ID = "org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.preferencePage.workspaceToolchainsPaths";

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
			System.out.println("arm.WorkspaceToolchainsPathsPreferencesPage.init()");
		}
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

		Map<ToolchainDefinition, Map<String, Set<String>>> toolchains = new TreeMap<>(
				Comparator.comparing(ToolchainDefinition::getName));

		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject project : projects) {
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
							var projectMap = toolchains.computeIfAbsent(toolchainDefinition, x -> new TreeMap<>());
							var buildConfigsSet = projectMap.computeIfAbsent(project.getName(), x -> new TreeSet<>());
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
							var projectMap = toolchains.computeIfAbsent(toolchainDefinition, x -> new TreeMap<>());
							var buildConfigsSet = projectMap.computeIfAbsent(project.getName(), x -> new TreeSet<>());
							buildConfigsSet.add(config.getName());
						} catch (BuildException e) {
						} catch (IndexOutOfBoundsException e) {
						}
					}
				}
			}

			if (allConfigsUseSameToolchain && toolchainInUse != null) {
				Set<String> set = toolchains.get(toolchainInUse).get(project.getName());
				// If all the configurations use the same toolchain, don't display
				// any of the configurations in the UI
				set.clear();
			}
		}

		if (toolchains.isEmpty()) {
			try {
				String toolchainId = fPersistentPreferences.getToolchainId();
				int ix = ToolchainDefinition.findToolchainById(toolchainId);
				toolchains.computeIfAbsent(ToolchainDefinition.getToolchain(ix), x -> Map.of());
			} catch (IndexOutOfBoundsException e) {
			}
		}

		if (toolchains.isEmpty()) {
			try {
				String toolchainName = fPersistentPreferences.getToolchainName();
				int ix = ToolchainDefinition.findToolchainByName(toolchainName);
				toolchains.computeIfAbsent(ToolchainDefinition.getToolchain(ix), x -> Map.of());
			} catch (IndexOutOfBoundsException e) {
			}
		}

		if (toolchains.isEmpty()) {
			int ix = ToolchainDefinition.getDefault();
			toolchains.computeIfAbsent(ToolchainDefinition.getToolchain(ix), x -> Map.of());
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
			var projectToConfigsMap = entry.getValue();

			Label message = new Label(group, SWT.WRAP);
			GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
			message.setLayoutData(layoutData);
			if (projectToConfigsMap.isEmpty()) {
				message.setText(Messages.GlobalToolchainsPathsPreferencesPage_ToolchainMessageDefault_label);
			} else {
				String collected = projectToConfigsMap.entrySet().stream().map(e -> {
					String projectName = e.getKey();
					Set<String> configNames = e.getValue();
					if (configNames.isEmpty()) {
						return projectName;
					} else {
						return projectName + " (" + String.join(", ", configNames) + ")";
					}
				}).collect(Collectors.joining(", "));
				message.setText(NLS.bind(
						Messages.GlobalToolchainsPathsPreferencesPage_ToolchainMessageWithProjects_label, collected));

			}
			PixelConverter pixelConverter = new PixelConverter(message);
			layoutData.widthHint = pixelConverter
					.convertWidthInCharsToPixels(Math.min(message.getText().length(), 100));

			FieldEditor labelField = new LabelFakeFieldEditor(toolchain.getFullName(),
					Messages.ToolsPaths_ToolchainName_label, group);
			labelField.fillIntoGrid(group, 4);
			addField(labelField);

			boolean isStrict = fDefaultPreferences.getBoolean(PersistentPreferences.WORKSPACE_TOOLCHAIN_PATH_STRICT,
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

		final Group group1 = new Group(parent, SWT.NONE);
		group1.setText(Messages.GlobalToolchainsPathsPreferencesPage_ToolchainDefaultGroup_label);
		GridLayout group1Layout = new GridLayout(2, false);
		group1.setLayout(group1Layout);
		group1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		FieldEditor toolchainNameField = new ToolchainsFieldEditor(PersistentPreferences.TOOLCHAIN_ID_KEY,
				Messages.ToolchainName_label, group1);
		toolchainNameField.fillIntoGrid(group1, 2);
		// ComboFieldEditor does not grab excess space (like StringFieldEditor does) therefore
		// we need to specify that here. ComboFieldEditor also doesn't provide an equivalent
		// to StringFieldEditor.getTextControl, hence the need to get control's layout data
		// like this
		((GridData) group1.getChildren()[1].getLayoutData()).grabExcessHorizontalSpace = true;
		addField(toolchainNameField);
		// Layout need to be reset after fields are added
		group1.setLayout(group1Layout);
	}

	@Override
	protected void adjustGridLayout() {
		// do nothing as we are manually handling the grid layout in createFieldEditors
	}

	// ------------------------------------------------------------------------
}
