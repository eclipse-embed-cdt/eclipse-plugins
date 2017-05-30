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

package ilg.gnumcueclipse.core.preferences;

import org.eclipse.swt.widgets.Composite;

import ilg.gnumcueclipse.core.EclipseUtils;

public class DirectoryNotStrictVariableFieldEditor extends DirectoryNotStrictFieldEditor {

	// ------------------------------------------------------------------------

	protected String fVariableName;
	protected String fVariableDescription;

	// ------------------------------------------------------------------------

	public DirectoryNotStrictVariableFieldEditor(String buildToolsPathKey, String variableName,
			String variableDescription, String toolsPaths_label, Composite fieldEditorParent, boolean isStrict) {
		super(buildToolsPathKey, toolsPaths_label, fieldEditorParent, isStrict);

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
			if (value == null) {
				value = getPreferenceStore().getString(getPreferenceName());
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
