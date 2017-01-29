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

package ilg.gnuarmeclipse.debug.gdbjtag.preferences;

import ilg.gnuarmeclipse.core.preferences.ThemeColorFieldEditor;
import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.PersistentPreferences;
import ilg.gnuarmeclipse.debug.gdbjtag.render.peripheral.PeripheralColumnLabelProvider;
import ilg.gnuarmeclipse.debug.gdbjtag.ui.Messages;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
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

public class PeripheralsPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	// ------------------------------------------------------------------------

	public static final String ID = "ilg.gnuarmeclipse.debug.gdbjtag.preferencePage.peripherals";

	// ------------------------------------------------------------------------

	public PeripheralsPage() {
		super(GRID);

		// Explicit use of the workspace storage.
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Activator.PLUGIN_ID));

		setDescription(Messages.PeripheralsPreferencePage_description);
	}

	// ------------------------------------------------------------------------

	// Contributed by IWorkbenchPreferencePage
	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsPage.init()");
		}
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	@Override
	protected void createFieldEditors() {

		FieldEditor colourReadOnly;
		colourReadOnly = new ThemeColorFieldEditor(PersistentPreferences.PERIPHERALS_COLOR_READONLY,
				PeripheralColumnLabelProvider.COLOR_READONLY, Messages.PeripheralsPreferencePage_readOnlyColor_label,
				getFieldEditorParent());
		addField(colourReadOnly);

		FieldEditor colourWriteOnly;
		colourWriteOnly = new ThemeColorFieldEditor(PersistentPreferences.PERIPHERALS_COLOR_WRITEONLY,
				PeripheralColumnLabelProvider.COLOR_WRITEONLY, Messages.PeripheralsPreferencePage_writeOnlyColor_label,
				getFieldEditorParent());
		addField(colourWriteOnly);

		FieldEditor colourChangedSaturate;
		colourChangedSaturate = new ThemeColorFieldEditor(PersistentPreferences.PERIPHERALS_COLOR_CHANGED,
				PeripheralColumnLabelProvider.COLOR_CHANGED,
				Messages.PeripheralsPreferencePage_changedSaturateColor_label, getFieldEditorParent());
		addField(colourChangedSaturate);

		FieldEditor colourChangedMedium;
		colourChangedMedium = new ThemeColorFieldEditor(PersistentPreferences.PERIPHERALS_COLOR_CHANGED_MEDIUM,
				PeripheralColumnLabelProvider.COLOR_CHANGED_MEDIUM,
				Messages.PeripheralsPreferencePage_changedMediumColor_label, getFieldEditorParent());
		addField(colourChangedMedium);

		FieldEditor colourChangedLight;
		colourChangedLight = new ThemeColorFieldEditor(PersistentPreferences.PERIPHERALS_COLOR_CHANGED_LIGHT,
				PeripheralColumnLabelProvider.COLOR_CHANGED_LIGHT,
				Messages.PeripheralsPreferencePage_changedLightColor_label, getFieldEditorParent());
		addField(colourChangedLight);

		FieldEditor hasFadingBackground;
		hasFadingBackground = new BooleanFieldEditor(PersistentPreferences.PERIPHERALS_CHANGED_USE_FADING_BACKGROUND,
				Messages.PeripheralsPreferencePage_useFadingBackground_label, getFieldEditorParent());
		addField(hasFadingBackground);
	}

	// ------------------------------------------------------------------------
}
