/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
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

package org.eclipse.embedcdt.debug.gdbjtag.core.dsf;

import org.eclipse.cdt.dsf.debug.model.DsfMemoryBlockRetrieval;
import org.eclipse.cdt.dsf.debug.service.IMemory.IMemoryDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IStartedDMEvent;
import org.eclipse.cdt.dsf.gdb.internal.memory.GdbMemoryBlockRetrievalManager;
import org.eclipse.cdt.dsf.service.DsfServiceEventHandler;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IMemoryBlockRetrieval;
import org.eclipse.embedcdt.debug.gdbjtag.core.memory.PeripheralMemoryBlockRetrieval;
import org.eclipse.embedcdt.internal.debug.gdbjtag.core.Activator;

@SuppressWarnings("restriction")
public class GdbMcuMemoryBlockRetrievalManager extends GdbMemoryBlockRetrievalManager {

	public GdbMcuMemoryBlockRetrievalManager(String modelId, ILaunchConfiguration config, DsfSession session) {
		super(modelId, config, session);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.cdt.dsf.debug.internal.provisional.model.
	 * MemoryBlockRetrievalManager#createMemoryBlockRetrieval(java.lang.String,
	 * org.eclipse.debug.core.ILaunchConfiguration,
	 * org.eclipse.cdt.dsf.service.DsfSession)
	 */
	@Override
	protected IMemoryBlockRetrieval createMemoryBlockRetrieval(String model, ILaunchConfiguration config,
			DsfSession session) {
		DsfMemoryBlockRetrieval memRetrieval = null;

		try {
			memRetrieval = new PeripheralMemoryBlockRetrieval(model, config, session);
		} catch (DebugException e) {
			Activator.log(e.getStatus());
		}

		return memRetrieval;
	}

	@Override
	@DsfServiceEventHandler
	public void eventDispatched(IStartedDMEvent event) {

		super.eventDispatched(event);

		// If a new memory context is starting, create its memory retrieval
		// instance
		if (event.getDMContext() instanceof IMemoryDMContext) {
			IMemoryDMContext memDmc = (IMemoryDMContext) event.getDMContext();
			IMemoryBlockRetrieval memRetrieval = getMemoryBlockRetrieval(memDmc);
			if (memRetrieval != null && memRetrieval instanceof PeripheralMemoryBlockRetrieval) {

				DsfSession.getSession(memDmc.getSessionId()).registerModelAdapter(PeripheralMemoryBlockRetrieval.class,
						memRetrieval);
			}
		}
	}

}
