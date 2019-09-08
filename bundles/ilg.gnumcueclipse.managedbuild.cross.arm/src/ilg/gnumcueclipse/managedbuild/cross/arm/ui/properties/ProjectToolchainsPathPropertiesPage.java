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

package ilg.gnumcueclipse.managedbuild.cross.arm.ui.properties;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.core.preferences.ScopedPreferenceStoreWithoutDefaults;
import ilg.gnumcueclipse.core.ui.FieldEditorPropertyPage;
import ilg.gnumcueclipse.core.ui.LabelFakeFieldEditor;
import ilg.gnumcueclipse.core.ui.XpackDirectoryNotStrictFieldEditor;
import ilg.gnumcueclipse.managedbuild.cross.arm.Activator;
import ilg.gnumcueclipse.managedbuild.cross.arm.Option;
import ilg.gnumcueclipse.managedbuild.cross.arm.ui.Messages;
import ilg.gnumcueclipse.managedbuild.cross.preferences.DefaultPreferences;
import ilg.gnumcueclipse.managedbuild.cross.preferences.PersistentPreferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;

public class ProjectToolchainsPathPropertiesPage extends FieldEditorPropertyPage {

	// ------------------------------------------------------------------------

	public static final String ID = "ilg.gnumcueclipse.managedbuild.cross.arm.properties.toolchainsPage";

	// ------------------------------------------------------------------------

	private PersistentPreferences fPersistentPreferences;
	private DefaultPreferences fDefaultPreferences;

	// ------------------------------------------------------------------------

	public ProjectToolchainsPathPropertiesPage() {
		super(GRID);

		fPersistentPreferences = Activator.getInstance().getPersistentPreferences();
		fDefaultPreferences = new DefaultPreferences(Activator.PLUGIN_ID);

		setDescription(Messages.ProjectToolchainsPathsPropertiesPage_description);
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
		boolean isStrict;

		Set<String> toolchainNames = new HashSet<String>();

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

			isStrict = fDefaultPreferences.getBoolean(PersistentPreferences.PROJECT_TOOLCHAIN_PATH_STRICT, true);

			String[] xpackNames = fDefaultPreferences.getToolchainXpackNames(toolchainName);

			String key = PersistentPreferences.getToolchainKey(toolchainName);
			FieldEditor toolchainPathField = new XpackDirectoryNotStrictFieldEditor(xpackNames, key,
					Messages.ToolchainPaths_label, getFieldEditorParent(), isStrict);
			addField(toolchainPathField);
		}
	}

	// ------------------------------------------------------------------------
}
