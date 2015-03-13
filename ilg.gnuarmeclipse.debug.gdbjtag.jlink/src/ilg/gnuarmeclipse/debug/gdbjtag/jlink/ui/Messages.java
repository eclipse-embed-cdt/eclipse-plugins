/*******************************************************************************
 *  Copyright (c) 2008 QNX Software Systems and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      QNX Software Systems - initial API and implementation
 *      Liviu Ionescu - ARM version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui;

import ilg.gnuarmeclipse.debug.gdbjtag.jlink.Activator;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class Messages {

	// ------------------------------------------------------------------------

	private static final String MESSAGES = Activator.PLUGIN_ID + ".ui.messages"; //$NON-NLS-1$

	public static String JLinkPreferencePage_description;
	public static String JLinkPreferencePage_executable_label;
	public static String JLinkPropertyPage_executable_folder;

	public static String Variable_executable_description;
	public static String Variable_path_description;

	// ------------------------------------------------------------------------

	static {
		// initialise resource bundle
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

	public static ResourceBundle getResourceBundle() {
		return RESOURCE_BUNDLE;
	}

	// ------------------------------------------------------------------------
}
