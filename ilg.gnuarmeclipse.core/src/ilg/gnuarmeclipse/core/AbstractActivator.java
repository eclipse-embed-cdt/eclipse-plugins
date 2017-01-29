/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public abstract class AbstractActivator extends Plugin {

	// ------------------------------------------------------------------------

	private static AbstractActivator fgInstance;

	public static AbstractActivator getInstance() {
		return fgInstance;
	}

	// ------------------------------------------------------------------------

	public abstract String getBundleId();

	// ------------------------------------------------------------------------

	protected boolean fIsDebugging;

	// ------------------------------------------------------------------------

	public AbstractActivator() {

		super();
		fgInstance = this;

		fIsDebugging = "true".equalsIgnoreCase(Platform.getDebugOption(getBundleId() + "/debug"));
	}

	// ------------------------------------------------------------------------

	public void start(BundleContext context) throws Exception {

		if (isDebugging()) {
			System.out.println(getBundleId() + ".start()");
		}
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {

		super.stop(context);
		if (isDebugging()) {
			System.out.println(getBundleId() + ".stop()");
		}
	}

	// ------------------------------------------------------------------------

	public boolean isDebugging() {

		return fIsDebugging;
	}

	// ------------------------------------------------------------------------

	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, getInstance().getBundleId(), 1, "Internal Error", e)); //$NON-NLS-1$
	}

	public static void log(String message) {
		log(new Status(IStatus.ERROR, getInstance().getBundleId(), 1, message, null)); // $NON-NLS-1$
	}

	// ------------------------------------------------------------------------
}
