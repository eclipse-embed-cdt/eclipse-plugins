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

package org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui;

import org.eclipse.embedcdt.debug.gdbjtag.qemu.core.preferences.DefaultPreferences;
import org.eclipse.embedcdt.debug.gdbjtag.qemu.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.ui.FieldEditorPropertyPage;
import org.eclipse.embedcdt.ui.XpackDirectoryNotStrictFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;

public class McuPage extends FieldEditorPropertyPage {

	// ------------------------------------------------------------------------

	private PersistentPreferences fPersistentPreferences;
	private DefaultPreferences fDefaultPreferences;

	// ------------------------------------------------------------------------

	public McuPage() {
		super(GRID);

		fPersistentPreferences = Activator.getInstance().getPersistentPreferences();
		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
	}

	// ------------------------------------------------------------------------

	/**
	 * Creates the field editors. Field editors are abstractions of the common GUI
	 * blocks needed to manipulate various types of preferences. Each field editor
	 * knows how to save and restore itself.
	 */
	@Override
	protected void createFieldEditors() {

		boolean isStrict = fPersistentPreferences.getFolderStrict();

		for (String prefix : PersistentPreferences.architectures) {
			String prefixDot = prefix + ".";
			FieldEditor executable = new StringFieldEditor(prefixDot + PersistentPreferences.EXECUTABLE_NAME,
					prefix + " executable:", getFieldEditorParent());
			addField(executable);

			String[] xpackNames = fDefaultPreferences.getXpackNames(prefixDot);

			FieldEditor folder = new XpackDirectoryNotStrictFieldEditor(xpackNames,
					prefixDot + PersistentPreferences.INSTALL_FOLDER, prefix + " folder:", getFieldEditorParent(),
					isStrict);
			addField(folder);
		}
	}

	// ------------------------------------------------------------------------
}
