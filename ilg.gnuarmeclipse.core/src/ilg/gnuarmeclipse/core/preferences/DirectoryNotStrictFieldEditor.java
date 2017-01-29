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

package ilg.gnuarmeclipse.core.preferences;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class DirectoryNotStrictFieldEditor extends DirectoryFieldEditor {

	// ------------------------------------------------------------------------

	private boolean fIsStrict;

	// ------------------------------------------------------------------------

	public DirectoryNotStrictFieldEditor(String buildToolsPathKey, String toolsPaths_label, Composite fieldEditorParent,
			boolean isStrict) {
		super(buildToolsPathKey, toolsPaths_label, fieldEditorParent);
		fIsStrict = isStrict;
	}

	// ------------------------------------------------------------------------

	@Override
	protected boolean doCheckState() {

		if (fIsStrict) {
			String fileName = getTextControl().getText();
			fileName = fileName.trim();
			if (fileName.length() == 0 && isEmptyStringAllowed()) {
				// Empty fields are accepted.
				return true;
			}

			IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
			String substitutedFileName;
			try {
				substitutedFileName = manager.performStringSubstitution(fileName);
			} catch (CoreException e) {
				// It's apparently invalid
				return false;
			}
			File file = new File(substitutedFileName);
			return file.isDirectory();
		} else {
			return true;
		}
	}

	// ------------------------------------------------------------------------
}
