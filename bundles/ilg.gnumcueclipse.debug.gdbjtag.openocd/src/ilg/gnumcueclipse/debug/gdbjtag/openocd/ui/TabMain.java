/*******************************************************************************
 * Copyright (c) 2007 - 2010 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial implementation
 *     Liviu Ionescu - ARM version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.openocd.ui;

import org.eclipse.cdt.dsf.gdb.internal.ui.launching.CMainTab;

import ilg.gnumcueclipse.debug.gdbjtag.openocd.Activator;

/**
 * @since 7.0
 */
@SuppressWarnings({ "restriction", "deprecation" })
public class TabMain extends CMainTab {

	public TabMain() {
		super((Activator.getInstance().getDefaultPreferences().getTabMainCheckProgram() ? 0
				: CMainTab.DONT_CHECK_PROGRAM) | CMainTab.INCLUDE_BUILD_SETTINGS);
	}
}
