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

package ilg.gnuarmeclipse.debug.gdbjtag.ui;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String MESSAGES = Activator.PLUGIN_ID + ".ui.messages"; //$NON-NLS-1$

	public static String PeripheralsView_NameColumn_text;
	public static String PeripheralsView_AddressColumn_text;
	public static String PeripheralsView_DescriptionColumn_text;

	public static String PeripheralRegister_Msg_Unknown_expression;
	public static String PeripheralRegister_Msg_Not_a_number;

	public static String PeripheralsPreferencePage_description;
	public static String PeripheralsPreferencePage_readOnlyColor_label;
	public static String PeripheralsPreferencePage_writeOnlyColor_label;
	public static String PeripheralsPreferencePage_changedSaturateColor_label;
	public static String PeripheralsPreferencePage_changedMediumColor_label;
	public static String PeripheralsPreferencePage_changedLightColor_label;

	public static String PeripheralsPreferencePage_useFadingBackground_label;

	public static String AddMemoryBlockAction_title;
	public static String AddMemoryBlockAction_noMemoryBlock;
	public static String AddMemoryBlockAction_failed;
	public static String AddMemoryBlockAction_input_invalid;

	static {
		// initialise above static strings
		NLS.initializeMessages(MESSAGES, Messages.class);
	}

	private Messages() {
	}

}
