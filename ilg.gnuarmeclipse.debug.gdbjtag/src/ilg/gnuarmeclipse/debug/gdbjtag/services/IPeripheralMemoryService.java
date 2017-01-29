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

package ilg.gnuarmeclipse.debug.gdbjtag.services;

import org.eclipse.cdt.core.IAddress;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.debug.service.IMemory.IMemoryDMContext;
import org.eclipse.cdt.dsf.service.IDsfService;
import org.eclipse.debug.core.model.MemoryByte;

public interface IPeripheralMemoryService extends IDsfService {

	// ------------------------------------------------------------------------

	public abstract void initializeMemoryData(final IMemoryDMContext memContext, RequestMonitor rm);

	public abstract boolean isBigEndian(IMemoryDMContext context);

	public abstract int getAddressSize(IMemoryDMContext context);

	public abstract void getMemory(IMemoryDMContext memoryDMC, IAddress address, long offset, int word_size,
			int word_count, DataRequestMonitor<MemoryByte[]> drm);

	public abstract void setMemory(IMemoryDMContext memoryDMC, IAddress address, long offset, int word_size,
			int word_count, byte[] buffer, RequestMonitor rm);

	// ------------------------------------------------------------------------
}
