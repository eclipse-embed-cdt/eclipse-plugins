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

package org.eclipse.embedcdt.managedbuild.cross.arm.ui;

import org.eclipse.embedcdt.managedbuild.cross.arm.core.preferences.DefaultPreferences;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.ui.AbstractUIActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIActivator {

	// ------------------------------------------------------------------------

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.embedcdt.managedbuild.cross.arm.ui"; //$NON-NLS-1$
	public static final String CORE_PLUGIN_ID = "org.eclipse.embedcdt.managedbuild.cross.arm.core"; //$NON-NLS-1$

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

	public PersistentPreferences getPersistentPreferences() {
		return org.eclipse.embedcdt.managedbuild.cross.arm.core.Activator.getInstance().getPersistentPreferences();
	}

	public DefaultPreferences getDefaultPreferences() {
		return org.eclipse.embedcdt.managedbuild.cross.arm.core.Activator.getInstance().getDefaultPreferences();
	}

	// ------------------------------------------------------------------------
}
