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

package ilg.gnuarmeclipse.debug.gdbjtag.qemu.preferences;

import ilg.gnuarmeclipse.core.preferences.DirectoryNotStrictVariableFieldEditor;
import ilg.gnuarmeclipse.core.preferences.StringVariableFieldEditor;
import ilg.gnuarmeclipse.debug.gdbjtag.qemu.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.qemu.DefaultPreferences;
import ilg.gnuarmeclipse.debug.gdbjtag.qemu.PersistentPreferences;
import ilg.gnuarmeclipse.debug.gdbjtag.qemu.VariableInitializer;
import ilg.gnuarmeclipse.debug.gdbjtag.qemu.ui.Messages;

import org.eclipse.core.runtime.preferences.InstanceScope;
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
 * This page uses special filed editors, that get the default values from the
 * preferences store, but the values are from the variables store.
 */
public class QemuPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	// ------------------------------------------------------------------------

	public static final String ID = "ilg.gnuarmeclipse.debug.gdbjtag.qemu.preferencePage";

	// ------------------------------------------------------------------------

	public QemuPage() {
		super(GRID);

		// Not really used, the field editors directly access the variables
		// store.
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID));

		setDescription(Messages.QemuPagePropertyPage_description);
	}

	// ------------------------------------------------------------------------

	// Contributed by IWorkbenchPreferencePage
	@Override
	public void init(IWorkbench workbench) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("OpenOcdPage.init()");
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
		executable = new StringVariableFieldEditor(PersistentPreferences.EXECUTABLE_NAME,
				VariableInitializer.VARIABLE_QEMU_EXECUTABLE, Messages.Variable_executable_description,
				Messages.QemuPagePropertyPage_executable_label, getFieldEditorParent());
		addField(executable);

		boolean isStrict;
		isStrict = DefaultPreferences.getBoolean(PersistentPreferences.FOLDER_STRICT, true);

		FieldEditor folder;
		folder = new DirectoryNotStrictVariableFieldEditor(PersistentPreferences.INSTALL_FOLDER,
				VariableInitializer.VARIABLE_QEMU_PATH, Messages.Variable_path_description,
				Messages.QemuPagePropertyPage_executable_folder, getFieldEditorParent(), isStrict);
		addField(folder);
	}

	// ------------------------------------------------------------------------
}
