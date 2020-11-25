/*******************************************************************************
 * Copyright (c) 2007 - 2010 QNX Software Systems and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     QNX Software Systems - Initial implementation
 *     Liviu Ionescu - Arm version
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.openocd.ui;

import org.eclipse.cdt.launch.ui.CMainTab2;
import org.eclipse.embedcdt.debug.gdbjtag.openocd.core.Activator;

public class TabMain extends CMainTab2 {

	/**
	 * If the preference is set to true, check program and disable Debug button
	 * if not found. The default GDB Hardware Debug plug-in behaviour is to do
	 * not check program, to allow project-less debug sessions.
	 */
	public TabMain() {
		super((Activator.getInstance().getDefaultPreferences().getTabMainCheckProgram() ? 0
				: CMainTab2.DONT_CHECK_PROGRAM) | CMainTab2.INCLUDE_BUILD_SETTINGS);
	}
}
