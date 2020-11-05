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
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.services;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.debug.service.IRunControl;
import org.eclipse.cdt.dsf.service.IDsfService;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.IPeripheralDMContext;

public interface IPeripheralsService extends IDsfService {

	// ------------------------------------------------------------------------

	public abstract void getPeripherals(IRunControl.IContainerDMContext containerDMContext,
			DataRequestMonitor<IPeripheralDMContext[]> dataRequestMonitor);

	// ------------------------------------------------------------------------
}
