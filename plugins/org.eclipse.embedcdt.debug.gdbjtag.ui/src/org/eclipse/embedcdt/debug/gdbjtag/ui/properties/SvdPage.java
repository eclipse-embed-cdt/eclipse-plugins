/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.ui.properties;

import org.eclipse.cdt.ui.newui.AbstractPage;

// Warning: tabs attach to a parent class, not id. Since it is not clear if/where the id
// is used, better make the id match the class name (in plugin.xml).

// DEPRECATED, functionality moved to debugger launch configuration.
public class SvdPage extends AbstractPage {

	@Override
	protected boolean isSingle() {
		// False: create Tabs, True: then single page, no tabs
		return true;
	}

}
