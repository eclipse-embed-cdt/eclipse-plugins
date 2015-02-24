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

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.widgets.Composite;

public class DirectoryNotStrictFieldEditor extends DirectoryFieldEditor {

	private boolean fIsStrict;

	public DirectoryNotStrictFieldEditor(String buildToolsPathKey,
			String toolsPaths_label, Composite fieldEditorParent,
			boolean isStrict) {
		super(buildToolsPathKey, toolsPaths_label, fieldEditorParent);
	}

	@Override
	protected boolean doCheckState() {
		if (fIsStrict) {
			return super.doCheckState();
		} else {
			return true;
		}
	}
}
