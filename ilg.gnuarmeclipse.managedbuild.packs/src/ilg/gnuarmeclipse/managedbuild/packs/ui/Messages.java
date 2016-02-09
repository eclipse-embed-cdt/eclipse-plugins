/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.packs.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import ilg.gnuarmeclipse.managedbuild.packs.Activator;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String MESSAGES = Activator.PLUGIN_ID + ".ui.messages"; //$NON-NLS-1$

	public static String DevicesTab_DeviceGroup_name;
	public static String DevicesTab_DeviceGroup_architecture_label;
	public static String DevicesTab_MemoryGroup_name;
	public static String DevicesTab_MemoryGroup_editButton_text;

	public static String DevicesTab_MemoryGroup_nameColumn_toolTipText;
	public static String DevicesTab_MemoryGroup_startColumn_toolTipText;
	public static String DevicesTab_MemoryGroup_sizeColumn_toolTipText;
	public static String DevicesTab_MemoryGroup_startupColumn_toolTipText;

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
}
