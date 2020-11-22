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
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.ui.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * Custom preferences store that does not use the defaults scope.
 *
 */
public class ScopedPreferenceStoreWithoutDefaults extends ScopedPreferenceStore {

	// ------------------------------------------------------------------------

	public ScopedPreferenceStoreWithoutDefaults(IScopeContext context, String qualifier) {
		super(context, qualifier);
	}

	// ------------------------------------------------------------------------

	/**
	 * An internal routine to get values from scopes, except defaults. The
	 * difference is in the false in getPreferenceNodes().
	 * 
	 * @param key
	 * @return a string value or null if not found.
	 */
	protected String internalGet(String key) {
		return Platform.getPreferencesService().get(key, null, getPreferenceNodes(false));
	}

	/**
	 * Redefined because the internalGet() is protected in the parent class.
	 */
	@Override
	public String getString(String name) {
		String value = internalGet(name);
		return value == null ? STRING_DEFAULT_DEFAULT : value;
	}

	/**
	 * Make sure the default string is always empty.
	 */
	@Override
	public String getDefaultString(String name) {
		return "";
	}

	// ------------------------------------------------------------------------
}
