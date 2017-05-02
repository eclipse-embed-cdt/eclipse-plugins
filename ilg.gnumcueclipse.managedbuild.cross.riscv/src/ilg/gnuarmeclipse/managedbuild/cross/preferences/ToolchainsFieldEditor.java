/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross.preferences;

import ilg.gnuarmeclipse.managedbuild.cross.ToolchainDefinition;

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
