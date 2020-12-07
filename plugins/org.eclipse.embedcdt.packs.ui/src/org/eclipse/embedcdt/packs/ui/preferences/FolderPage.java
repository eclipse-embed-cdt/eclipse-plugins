/*******************************************************************************
 * Copyright (c) 2014, 2020 Liviu Ionescu and others.
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
 *     Alexander Fedorov (ArSysOp) - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.ui.preferences;

import org.eclipse.embedcdt.internal.packs.ui.Activator;
import org.eclipse.embedcdt.packs.core.Preferences;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
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

public class FolderPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public FolderPage() {
		super(GRID);
		setPreferenceStore(Activator.getInstance().getCorePreferenceStore());
		setDescription("The location where packages are stored locally.");
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public void createFieldEditors() {
		addField(new DirectoryFieldEditor(Preferences.PACKS_CMSIS_FOLDER_PATH, "&CMSIS Packs folder:", getFieldEditorParent()));
		addField(new StringFieldEditor(Preferences.PACKS_CMSIS_MACRO_NAME, "&Macro name:", getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
		// System.out.println("PreferencePage.init()");
	}

}