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

package ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui;

import ilg.gnuarmeclipse.debug.gdbjtag.jlink.DefaultPreferences;

import org.eclipse.cdt.dsf.gdb.internal.ui.launching.CMainTab;

/**
 * @since 7.0
 */
@SuppressWarnings("restriction")
public class TabMain extends CMainTab {

	/**
	 * If the preference is set to true, check program and disable Debug button
	 * if not found. The default GDB Hardware Debug plug-in behaviour is to do
	 * not check program, to allow project-less debug sessions.
	 */
	public TabMain() {
		super((DefaultPreferences.getTabMainCheckProgram() ? 0 : CMainTab.DONT_CHECK_PROGRAM)
				| CMainTab.INCLUDE_BUILD_SETTINGS);
	}
}
