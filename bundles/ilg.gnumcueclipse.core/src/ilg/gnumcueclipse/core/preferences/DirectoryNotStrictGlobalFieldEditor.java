/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnumcueclipse.core.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;

import ilg.gnumcueclipse.core.Activator;

public class DirectoryNotStrictGlobalFieldEditor extends DirectoryNotStrictFieldEditor {

	// ------------------------------------------------------------------------

	protected String fPluginId;

	// ------------------------------------------------------------------------

	public DirectoryNotStrictGlobalFieldEditor(String key, String pluginId, String labelText,
			Composite fieldEditorParent, boolean isStrict) {
		super(key, labelText, fieldEditorParent, isStrict);

		fPluginId = pluginId;
	}

	// ------------------------------------------------------------------------

	/**
	 * Load the value from the VariablesPlugin.
	 */
	@Override
	protected void doLoad() {

		if (getTextControl() != null) {
			// Search the value in all preference stores, not only the current
			// one.
			String value = Platform.getPreferencesService().getString(fPluginId, getPreferenceName(), "", null);
			if (Activator.getInstance().isDebugging()) {
				System.out.println("DirectoryNotStrictGlobalFieldEditor.doLoad() got \"" + value + "\"");
			}
			setPresentsDefaultValue(false);
			getTextControl().setText(value);
			oldValue = value;
		}
	}

	// ------------------------------------------------------------------------
}
