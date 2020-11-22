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
 *******************************************************************************/

package org.eclipse.embedcdt.ui;

import org.eclipse.embedcdt.core.Activator;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class StringVariableFieldEditor extends StringFieldEditor {

	// ------------------------------------------------------------------------

	protected String fVariableName;
	protected String fVariableDescription;

	// ------------------------------------------------------------------------

	public StringVariableFieldEditor(String name, String variableName, String variableDescription, String labelText,
			Composite parent) {
		super(name, labelText, parent);

		fVariableName = variableName;
		fVariableDescription = variableDescription;
	}

	// ------------------------------------------------------------------------

	/**
	 * Load the value from the VariablesPlugin.
	 */
	@Override
	protected void doLoad() {

		if (getTextControl() != null) {
			String value = EclipseUtils.getVariableValue(fVariableName);
			if (value == null || value.isEmpty()) {
				value = getPreferenceStore().getString(getPreferenceName());
				if (Activator.getInstance().isDebugging()) {
					System.out.println("StringVariableFieldEditor.doLoad() got \"" + value + "\"");
				}
				setPresentsDefaultValue(false);
			}
			getTextControl().setText(value);
			oldValue = value;
		}
	}

	/**
	 * Store the value back to the VariablesPlugin.
	 */
	@Override
	protected void doStore() {

		// Store the value as a variable, to be used during substitutions.
		String value = getTextControl().getText();
		EclipseUtils.setVariableValue(fVariableName, fVariableDescription, value);

		// Also store the value in the persistent store.
		super.doStore();
	}

	// ------------------------------------------------------------------------
}
