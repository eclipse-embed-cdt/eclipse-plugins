/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * Copyright (c) 2015-2016 Chris Reed.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Liviu Ionescu - initial version
 *     Chris Reed - pyOCD changes
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.pyocd;

public interface PreferenceConstants {

	public static final String DEFAULT_GDB_COMMAND = "arm-none-eabi-gdb"; //$NON-NLS-1$

	public static final int AUTO_ERASE = 0;
	public static final int CHIP_ERASE = 1;
	public static final int SECTOR_ERASE = 2;
	public static final int FAST_PROGRAM = 3;

}
