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
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package org.eclipse.embedcdt.managedbuild.cross.arm.ui.preferences;

import org.eclipse.embedcdt.managedbuild.cross.arm.core.ToolchainDefinition;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class ToolchainsFieldEditor extends ComboFieldEditor {

	static String[][] populateCombo() {
		int len = ToolchainDefinition.getList().size();
		String[][] a = new String[len + 1][2];
		a[0][0] = "--- Global default ---";
		a[0][1] = "";

		int i = 1;
		for (ToolchainDefinition t : ToolchainDefinition.getList()) {
			a[i][0] = t.getName();
			a[i][1] = t.getName();
			++i;
		}
		return a;
	}

	public ToolchainsFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, populateCombo(), parent);
	}

}
