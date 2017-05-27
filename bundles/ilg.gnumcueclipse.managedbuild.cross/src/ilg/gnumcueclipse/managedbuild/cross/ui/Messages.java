/*******************************************************************************
 * Copyright (c) 2011, 2013 Marc-Andre Laperle and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Marc-Andre Laperle - initial API and implementation
 *     Liviu Ionescu - MCU version
  *******************************************************************************/

package ilg.gnumcueclipse.managedbuild.cross.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import ilg.gnumcueclipse.managedbuild.cross.Activator;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	// ------------------------------------------------------------------------

	private static final String MESSAGES = Activator.PLUGIN_ID + ".ui.messages"; //$NON-NLS-1$

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
