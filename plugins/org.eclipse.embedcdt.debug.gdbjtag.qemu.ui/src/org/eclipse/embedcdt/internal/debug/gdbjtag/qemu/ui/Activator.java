/*******************************************************************************
 * Copyright (c) 2014, 2020 Liviu Ionescu and others.
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
 *     Alexander Fedorov (ArSysOp) - extract UI part
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui;

import org.eclipse.embedcdt.debug.gdbjtag.qemu.core.preferences.DefaultPreferences;
import org.eclipse.embedcdt.debug.gdbjtag.qemu.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.internal.ui.AbstractUIActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 * @noreference This class is not intended to be referenced by clients.
 */
public class Activator extends AbstractUIActivator {

	// ------------------------------------------------------------------------

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.embedcdt.debug.gdbjtag.qemu.ui"; //$NON-NLS-1$
	public static final String CORE_PLUGIN_ID = "org.eclipse.embedcdt.debug.gdbjtag.qemu.core"; //$NON-NLS-1$

	@Override
	public String getBundleId() {
		return PLUGIN_ID;
	}

	// ------------------------------------------------------------------------

	// The shared instance
	private static Activator fgInstance;

	public static Activator getInstance() {
		return fgInstance;
	}

	public Activator() {

		super();
		fgInstance = this;
	}

	// ------------------------------------------------------------------------

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	// ------------------------------------------------------------------------

	public DefaultPreferences getDefaultPreferences() {
		return org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.core.Activator.getInstance().getDefaultPreferences();
	}

	public PersistentPreferences getPersistentPreferences() {
		return org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.core.Activator.getInstance().getPersistentPreferences();
	}

	// ------------------------------------------------------------------------
}
