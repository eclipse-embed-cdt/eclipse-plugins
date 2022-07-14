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

package org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui.preferences;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui.Activator;
import org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui.McuPage;
import org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui.Messages;
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
public class GlobalMcuPage extends McuPage implements IWorkbenchPreferencePage {

	// ------------------------------------------------------------------------

	public static final String ID = "org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui.globalPreferencePage";

	// ------------------------------------------------------------------------

	public GlobalMcuPage() {
		super();

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GlobalMcuPage()");
		}

		setPreferenceStore(new ScopedPreferenceStore(ConfigurationScope.INSTANCE, Activator.CORE_PLUGIN_ID));

		setDescription(Messages.GlobalMcuPagePropertyPage_description);
	}

	// ------------------------------------------------------------------------

	// Contributed by IWorkbenchPreferencePage
	@Override
	public void init(IWorkbench workbench) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.GlobalMcuPage.init()");
		}
	}

	// ------------------------------------------------------------------------
}
