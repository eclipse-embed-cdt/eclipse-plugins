/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

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
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}

	private Messages() {
	}

}
