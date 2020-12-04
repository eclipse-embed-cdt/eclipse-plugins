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
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.internal.packs.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	private static final String MESSAGES = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$

	public static String NewSiteDialog_label_title_edit;
	public static String NewSiteDialog_label_title_new;

	public static String NewSiteDialog_label_type;
	public static String NewSiteDialog_label_name;
	public static String NewSiteDialog_label_url;

	public static String PacksView_UpdateAction_text;
	public static String PacksView_UpdateAction_toolTipText;

	public static String PacksView_InstallAction_text;
	public static String PacksView_InstallAction_toolTipText;

	public static String PacksView_RemoveAction_text;
	public static String PacksView_RemoveAction_toolTipText;

	public static String PacksView_ExpandAll_text;
	public static String PacksView_ExpandAll_toolTipText;

	public static String PacksView_CollapseAll_text;
	public static String PacksView_CollapseAll_toolTipText;

	static {
		// Initialise resource bundle.
		NLS.initializeMessages(MESSAGES, Messages.class);
	}

	private Messages() {
	}

}
