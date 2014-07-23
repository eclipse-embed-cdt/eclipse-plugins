/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.core;

import org.eclipse.jface.preference.IPreferenceStore;

public class Preferences {

	public static IPreferenceStore getPreferenceStore() {
		return Activator.getInstance().getPreferenceStore();
	}

	public static final String PACKS_FOLDER_PATH = "packs.folder.path";
	public static final String PACKS_MACRO_NAME = "packs.macro.name";

	public static final String DEFAULT_MACRO_NAME = "packs_path";

}
