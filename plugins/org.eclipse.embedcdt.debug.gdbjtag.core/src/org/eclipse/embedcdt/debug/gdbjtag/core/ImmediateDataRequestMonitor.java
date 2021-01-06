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
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.core;

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
