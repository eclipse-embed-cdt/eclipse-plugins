/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
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
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.core;

import org.eclipse.embedcdt.core.AbstractActivator;
import org.eclipse.embedcdt.debug.gdbjtag.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.packs.core.IConsoleStream;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivator {

	// ------------------------------------------------------------------------

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.embedcdt.debug.gdbjtag.core"; //$NON-NLS-1$

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

	protected PersistentPreferences fPersistentPreferences = null;

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

	
	public IConsoleStream getConsoleOutput() {
		return org.eclipse.embedcdt.packs.core.Activator.getInstance().getConsoleOutput();
	}

	// ------------------------------------------------------------------------


	public PersistentPreferences getPersistentPreferences() {
		if (fPersistentPreferences == null) {
			fPersistentPreferences = new PersistentPreferences(PLUGIN_ID);
		}
		return fPersistentPreferences;
	}

	public IPreferenceStore getCorePreferenceStore() {
		return org.eclipse.embedcdt.packs.core.Activator.getInstance().getCorePreferenceStore();
	}

	// ------------------------------------------------------------------------
}
