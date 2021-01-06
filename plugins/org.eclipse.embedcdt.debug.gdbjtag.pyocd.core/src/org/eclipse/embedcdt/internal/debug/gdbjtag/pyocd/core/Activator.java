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
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.internal.debug.gdbjtag.pyocd.core;

import org.eclipse.embedcdt.debug.gdbjtag.pyocd.core.preferences.DefaultPreferences;
import org.eclipse.embedcdt.debug.gdbjtag.pyocd.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.internal.core.AbstractActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 *
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @noextend This class is not intended to be subclassed by clients.
 * @noreference This class is not intended to be referenced by clients.
 */
public class Activator extends AbstractActivator {

	// ------------------------------------------------------------------------

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.embedcdt.debug.gdbjtag.pyocd.core"; //$NON-NLS-1$

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

	protected DefaultPreferences fDefaultPreferences = null;
	protected PersistentPreferences fPersistentPreferences = null;

	public Activator() {

		super();
		fgInstance = this;

		fPersistentPreferences = new PersistentPreferences(PLUGIN_ID);
	}

	// ------------------------------------------------------------------------

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	// ------------------------------------------------------------------------

	public DefaultPreferences getDefaultPreferences() {

		if (fDefaultPreferences == null) {
			fDefaultPreferences = new DefaultPreferences(PLUGIN_ID);
		}
		return fDefaultPreferences;
	}

	public PersistentPreferences getPersistentPreferences() {

		if (fPersistentPreferences == null) {
			fPersistentPreferences = new PersistentPreferences(PLUGIN_ID);
		}
		return fPersistentPreferences;
	}

	// ------------------------------------------------------------------------
}
