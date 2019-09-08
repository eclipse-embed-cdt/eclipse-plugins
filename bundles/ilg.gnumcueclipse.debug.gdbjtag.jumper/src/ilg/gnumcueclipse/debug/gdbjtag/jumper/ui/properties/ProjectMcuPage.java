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

package ilg.gnumcueclipse.debug.gdbjtag.jumper.ui.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;

import ilg.gnumcueclipse.core.preferences.ScopedPreferenceStoreWithoutDefaults;
import ilg.gnumcueclipse.core.ui.FieldEditorPropertyPage;
import ilg.gnumcueclipse.core.ui.XpackDirectoryNotStrictFieldEditor;
import ilg.gnumcueclipse.debug.gdbjtag.jumper.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.jumper.preferences.DefaultPreferences;
import ilg.gnumcueclipse.debug.gdbjtag.jumper.preferences.PersistentPreferences;
import ilg.gnumcueclipse.debug.gdbjtag.jumper.ui.Messages;

public class ProjectMcuPage extends FieldEditorPropertyPage {

	// ------------------------------------------------------------------------

	public static final String ID = "ilg.gnumcueclipse.debug.gdbjtag.jumper.projectPropertiesPage";

	// ------------------------------------------------------------------------

	private PersistentPreferences fPersistentPreferences;
	private DefaultPreferences fDefaultPreferences;

	// ------------------------------------------------------------------------

	public ProjectMcuPage() {
		super(GRID);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jumper.ProjectMcuPage()");
		}

		fPersistentPreferences = Activator.getInstance().getPersistentPreferences();
		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();

		setDescription(Messages.ProjectMcuPagePropertyPage_description);
	}

	// ------------------------------------------------------------------------

	protected IPreferenceStore doGetPreferenceStore() {

		Object element = getElement();
		if (element instanceof IProject) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("jumper.ProjectMcuPage.doGetPreferenceStore() project store");
			}
			return new ScopedPreferenceStoreWithoutDefaults(new ProjectScope((IProject) element), Activator.PLUGIN_ID);
		}
		return null;
	}

	@Override
	protected void createFieldEditors() {

		FieldEditor executable = new StringFieldEditor(PersistentPreferences.EXECUTABLE_NAME,
				Messages.McuPage_executable_label, getFieldEditorParent());
		addField(executable);

		boolean isStrict = fPersistentPreferences.getFolderStrict();

		String[] xpackNames = fDefaultPreferences.getXpackNames();

		FieldEditor folder = new XpackDirectoryNotStrictFieldEditor(xpackNames, PersistentPreferences.INSTALL_FOLDER,
				Messages.McuPage_executable_folder, getFieldEditorParent(), isStrict);
		addField(folder);
	}

	// ------------------------------------------------------------------------
}