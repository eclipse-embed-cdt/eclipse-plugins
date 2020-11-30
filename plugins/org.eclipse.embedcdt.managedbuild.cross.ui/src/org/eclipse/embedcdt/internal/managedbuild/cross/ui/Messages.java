/*******************************************************************************
 * Copyright (c) 2011, 2013 Marc-Andre Laperle and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Marc-Andre Laperle - initial API and implementation
 *     Liviu Ionescu - MCU version
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.internal.managedbuild.cross.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.embedcdt.managedbuild.cross.ui.Activator;
import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	// ------------------------------------------------------------------------

	private static final String MESSAGES = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$

	public static String McuPropertiesPage_description;
	public static String BuildToolsPaths_label;

	public static String ProjectBuildToolsPathsPropertiesPage_description;
	public static String WorkspaceBuildToolsPathsPreferencesPage_description;
	public static String GlobalBuildToolsPathsPreferencesPage_description;

	// ------------------------------------------------------------------------

	static {
		// Initialize resource bundle
		NLS.initializeMessages(MESSAGES, Messages.class);
	}

	private static ResourceBundle RESOURCE_BUNDLE;
	static {
		try {
			RESOURCE_BUNDLE = ResourceBundle.getBundle(MESSAGES);
		} catch (MissingResourceException e) {
			Activator.log(e);
		}
	}

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	// ------------------------------------------------------------------------
}
