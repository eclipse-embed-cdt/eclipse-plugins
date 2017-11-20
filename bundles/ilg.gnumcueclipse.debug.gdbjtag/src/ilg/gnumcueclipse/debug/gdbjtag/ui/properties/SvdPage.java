/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.ui.properties;

import org.eclipse.cdt.ui.newui.AbstractPage;

// Warning: tabs attach to a parent class, not id. Since it is not clear if/where the id
// is used, better make the id match the class name (in plugin.xml).

public class SvdPage extends AbstractPage {

	@Override
	protected boolean isSingle() {
		// False: create Tabs, True: then single page, no tabs
		return true;
	}

}
