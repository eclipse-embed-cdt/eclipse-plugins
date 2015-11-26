/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.templates.aducm36x;

import ilg.gnuarmeclipse.core.AbstractActivator;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivator {

	// ------------------------------------------------------------------------

	// The plug-in ID
	public static final String PLUGIN_ID = "ilg.gnuarmeclipse.templates.ad"; //$NON-NLS-1$

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
}
