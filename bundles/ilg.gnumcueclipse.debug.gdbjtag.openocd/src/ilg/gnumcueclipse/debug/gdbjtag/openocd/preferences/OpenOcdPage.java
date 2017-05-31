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

package ilg.gnumcueclipse.debug.gdbjtag.openocd.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import ilg.gnumcueclipse.core.preferences.DirectoryNotStrictGlobalFieldEditor;
import ilg.gnumcueclipse.core.preferences.StringGlobalFieldEditor;
import ilg.gnumcueclipse.debug.gdbjtag.openocd.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.openocd.PersistentPreferences;
import ilg.gnumcueclipse.debug.gdbjtag.openocd.ui.Messages;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page uses special filed editors, that get the default values from the
 * preferences store, but the values are from the variables store.
 */
public class OpenOcdPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	// ------------------------------------------------------------------------

	public static final String ID = "ilg.gnumcueclipse.debug.gdbjtag.openocd.preferencePage";

	// ------------------------------------------------------------------------

	public OpenOcdPage() {
		super(GRID);

		// Explicit use of the workspace preferences.
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID));

		setDescription(Messages.OpenOCDPagePropertyPage_description);
	}

	// ------------------------------------------------------------------------

	// Contributed by IWorkbenchPreferencePage
	@Override
	public void init(IWorkbench workbench) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.OpenOcdPage.init()");
		}
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	protected void createFieldEditors() {

		FieldEditor executable;
		executable = new StringGlobalFieldEditor(PersistentPreferences.EXECUTABLE_NAME, Activator.PLUGIN_ID,
				Messages.Variable_executable_description, getFieldEditorParent());
		addField(executable);

		boolean isStrict;
		isStrict = PersistentPreferences.getFolderStrict();

		FieldEditor folder;
		folder = new DirectoryNotStrictGlobalFieldEditor(PersistentPreferences.INSTALL_FOLDER, Activator.PLUGIN_ID,
				Messages.Variable_path_description, getFieldEditorParent(), isStrict);
		addField(folder);
	}

	// ------------------------------------------------------------------------
}
