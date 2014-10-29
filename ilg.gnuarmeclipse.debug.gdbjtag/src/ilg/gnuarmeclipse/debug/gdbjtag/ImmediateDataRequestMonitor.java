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

package ilg.gnuarmeclipse.debug.gdbjtag;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.ImmediateExecutor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;

/**
 * A convenience request monitor using the immediate executor.
 *
 * @param <V>
 *            the type of the result object.
 */
public class ImmediateDataRequestMonitor<V> extends DataRequestMonitor<V> {

	// ------------------------------------------------------------------------

	/**
	 * Create an immediate data request monitor without a parent.
	 */
	public ImmediateDataRequestMonitor() {
		super(ImmediateExecutor.getInstance(), null);
	}

	/**
	 * Create an immediate data request monitor with a parent request monitor.
	 * 
	 * @param parentRequestMonitor
	 */
	public ImmediateDataRequestMonitor(RequestMonitor parentRequestMonitor) {
		super(ImmediateExecutor.getInstance(), parentRequestMonitor);
	}

	// ------------------------------------------------------------------------
}
