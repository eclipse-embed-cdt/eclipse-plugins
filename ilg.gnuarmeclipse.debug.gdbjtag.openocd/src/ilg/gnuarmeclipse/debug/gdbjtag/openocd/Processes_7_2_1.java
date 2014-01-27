/*******************************************************************************
 * Copyright (c) 2011 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Marc Khouzam (Ericsson) - initial API and implementation
 *******************************************************************************/
package ilg.gnuarmeclipse.debug.gdbjtag.openocd;

import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DsfExecutor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_2_1;
import org.eclipse.cdt.dsf.gdb.service.StartOrRestartProcessSequence_7_0;
import org.eclipse.cdt.dsf.service.DsfSession;

// Used to redefine the ProcessSequence, where the reset happens
public class Processes_7_2_1 extends GDBProcesses_7_2_1 {

	public Processes_7_2_1(DsfSession session) {
		super(session);
	}

	protected Sequence getStartOrRestartProcessSequence(DsfExecutor executor,
			IContainerDMContext containerDmc, Map<String, Object> attributes,
			boolean restart, DataRequestMonitor<IContainerDMContext> rm) {

		if (restart) {
			return new RestartProcessSequence(executor,
					containerDmc, attributes, restart, rm);
		} else {
			return new StartOrRestartProcessSequence_7_0(executor,
					containerDmc, attributes, restart, rm);
		}
	}

}
