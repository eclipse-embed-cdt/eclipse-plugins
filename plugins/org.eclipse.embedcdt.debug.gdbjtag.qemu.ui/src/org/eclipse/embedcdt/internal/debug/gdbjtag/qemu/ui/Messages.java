/*******************************************************************************
 *  Copyright (c) 2008 QNX Software Systems and others.
 *
 *  This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *     QNX Software Systems - initial API and implementation
 *     Liviu Ionescu - Arm version
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class Messages {

	// ------------------------------------------------------------------------

	private static final String MESSAGES = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$

	public static String ProjectMcuPagePropertyPage_description;
	public static String WorkspaceMcuPagePropertyPage_description;
	public static String GlobalMcuPagePropertyPage_description;

	public static String McuPage_executable_label;
	public static String McuPage_executable_folder;

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
