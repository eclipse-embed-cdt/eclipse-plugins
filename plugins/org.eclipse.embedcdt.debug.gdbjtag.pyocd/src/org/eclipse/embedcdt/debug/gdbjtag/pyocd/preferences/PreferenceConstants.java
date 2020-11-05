/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * Copyright (c) 2015-2016 Chris Reed.
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
 *     Chris Reed - pyOCD changes
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.pyocd.preferences;

public interface PreferenceConstants {

	public static final String DEFAULT_GDB_COMMAND = "arm-none-eabi-gdb"; //$NON-NLS-1$

	public static final int AUTO_ERASE = 0;
	public static final int CHIP_ERASE = 1;
	public static final int SECTOR_ERASE = 2;
	public static final int FAST_PROGRAM = 3;

}
