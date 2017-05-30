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

package ilg.gnumcueclipse.core.preferences;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;

import ilg.gnumcueclipse.core.Activator;
import ilg.gnumcueclipse.core.EclipseUtils;

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
