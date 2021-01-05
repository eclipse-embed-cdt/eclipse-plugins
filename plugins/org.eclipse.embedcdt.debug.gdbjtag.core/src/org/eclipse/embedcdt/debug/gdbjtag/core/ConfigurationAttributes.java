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

package org.eclipse.embedcdt.debug.gdbjtag.core;

public interface ConfigurationAttributes {

	// ------------------------------------------------------------------------

	// public static final String PREFIX = Activator.PLUGIN_ID;
	public static final String PREFIX = "ilg.gnumcueclipse.debug.gdbjtag";

	// ------------------------------------------------------------------------

	// TabSvd
	public static final String SVD_PATH = PREFIX + ".svdPath"; //$NON-NLS-1$

	// ------------------------------------------------------------------------

	// TabSvd
	public static final String ATTR_JTAG_DEVICE = "org.eclipse.cdt.debug.gdbjtag.core.jtagDevice"; //$NON-NLS-1$

	// ------------------------------------------------------------------------
}
