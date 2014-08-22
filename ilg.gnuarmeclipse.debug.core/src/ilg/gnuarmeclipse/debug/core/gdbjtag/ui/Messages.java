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

package ilg.gnuarmeclipse.debug.core.gdbjtag.ui;

import ilg.gnuarmeclipse.debug.core.Activator;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String MESSAGES = Activator.PLUGIN_ID
			+ ".gdbjtag.ui.messages"; //$NON-NLS-1$

	public static String PeripheralsView_NameColumn_text;
	public static String PeripheralsView_AddressColumn_text;
	public static String PeripheralsView_DescriptionColumn_text;

	static {
		// initialise above static strings
		NLS.initializeMessages(MESSAGES, Messages.class);
	}

	private Messages() {
	}

}
