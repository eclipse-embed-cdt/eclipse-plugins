/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.packs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ilg.gnuarmeclipse.managedbuild.packs"; //$NON-NLS-1$

	// The shared instance
	private static Activator sfInstance;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return sfInstance;
	}

	/**
	 * The constructor
	 */
	public Activator() {
		;
	}

	public void start(BundleContext context) throws Exception {

		System.out.println("ilg.gnuarmeclipse.managedbuild.packs.start()");

		super.start(context);
		sfInstance = this;
	}

	public void stop(BundleContext context) throws Exception {

		sfInstance = null;
		super.stop(context);
	}

	// ------------------------------------------------------------------------

	/**
	 * Logs the specified status with this plug-in's log.
	 * 
	 * @param status
	 *            status to log
	 */
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	/**
	 * Logs an internal error with the specified throwable
	 * 
	 * @param e
	 *            the exception to be logged
	 */
	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 1, "Internal Error", e)); //$NON-NLS-1$
	}

	// ------------------------------------------------------------------------

}
