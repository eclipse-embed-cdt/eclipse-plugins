/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
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

package org.eclipse.embedcdt.packs.core;

import org.eclipse.jface.preference.IPreferenceStore;

public class Preferences {

	public static IPreferenceStore getPreferenceStore() {
		return Activator.getInstance().getPreferenceStore();
	}

	public static final String PACKS_FOLDER_PATH = "packs.folder.path";
	public static final String PACKS_MACRO_NAME = "packs.macro.name";

	public static final String DEFAULT_MACRO_NAME = "packs_path";

}
