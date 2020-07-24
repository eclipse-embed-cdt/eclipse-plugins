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

package ilg.gnumcueclipse.debug.gdbjtag.services;

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
