/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.memory;

import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.PeripheralDMContext;

import org.eclipse.cdt.dsf.datamodel.DMContexts;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.model.DsfMemoryBlockRetrieval;
import org.eclipse.cdt.dsf.debug.service.IMemory;
import org.eclipse.cdt.dsf.gdb.internal.memory.GdbMemoryBlockRetrieval;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IMemoryBlockExtension;

@SuppressWarnings("restriction")
public class PeripheralMemoryBlockRetrieval extends GdbMemoryBlockRetrieval {

	// ------------------------------------------------------------------------

	public PeripheralMemoryBlockRetrieval(String modelId,
			ILaunchConfiguration config, DsfSession session)
			throws DebugException {
		super(modelId, config, session);
	}

	// ------------------------------------------------------------------------

	@Override
	public IMemoryBlockExtension getExtendedMemoryBlock(String addr,
			Object context) throws DebugException {

		System.out.println("getExtendedMemoryBlock(" + addr + "," + context
				+ ")");
		IMemoryBlockExtension memoryBlockExtension = null;
		if (context instanceof PeripheralDMContext) {
			PeripheralDMContext peripheralDMContext = (PeripheralDMContext) context;
			IMemory.IMemoryDMContext memoryDMContext = null;
			IDMContext dmContext = null;
			if (context instanceof IAdaptable
					&& (dmContext = (IDMContext) ((IAdaptable) context)
							.getAdapter((Class<IDMContext>) IDMContext.class)) != null) {

				memoryDMContext = (IMemory.IMemoryDMContext) DMContexts
						.getAncestorOfType(
								(IDMContext) dmContext,
								(Class<IMemory.IMemoryDMContext>) IMemory.IMemoryDMContext.class);
			}
			if (memoryDMContext == null) {
				return null;
			}
			memoryBlockExtension = new PeripheralMemoryBlockExtension(
					(DsfMemoryBlockRetrieval) this, memoryDMContext,
					getModelId(), peripheralDMContext);
		}

		if (memoryBlockExtension == null) {
			System.out.println("getExtendedMemoryBlock(" + addr + "," + context
					+ ") super.getExtendedMemoryBlock()");
			// Needed for regular memory blocks
			memoryBlockExtension = super.getExtendedMemoryBlock(addr, context);
		}
		return memoryBlockExtension;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long addr, long length)
			throws DebugException {

		// Do not return any memory block, use extended memory block above.
		return null;
	}

	@Override
	public boolean supportsStorageRetrieval() {
		return true;
	}

	// ------------------------------------------------------------------------
}
