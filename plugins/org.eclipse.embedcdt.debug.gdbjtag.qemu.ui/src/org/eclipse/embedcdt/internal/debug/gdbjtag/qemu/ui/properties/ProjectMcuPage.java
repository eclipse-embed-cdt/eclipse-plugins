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

package org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui.Activator;
import org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui.McuPage;
import org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui.Messages;
import org.eclipse.embedcdt.ui.preferences.ScopedPreferenceStoreWithoutDefaults;
import org.eclipse.jface.preference.IPreferenceStore;

public class ProjectMcuPage extends McuPage {

	// ------------------------------------------------------------------------

	public static final String ID = "org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui.projectPropertiesPage";

	// ------------------------------------------------------------------------

	public ProjectMcuPage() {
		super();

		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.ProjectMcuPage()");
		}

		setDescription(Messages.ProjectMcuPagePropertyPage_description);
	}

	// ------------------------------------------------------------------------

	@Override
	protected IPreferenceStore doGetPreferenceStore() {

		Object element = getElement();
		if (element instanceof IProject) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("qemu.ProjectMcuPage.doGetPreferenceStore() project store");
			}
			return new ScopedPreferenceStoreWithoutDefaults(new ProjectScope((IProject) element),
					Activator.CORE_PLUGIN_ID);
		}
		return null;
	}

	// ------------------------------------------------------------------------
}
