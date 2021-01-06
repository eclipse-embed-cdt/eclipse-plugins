/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DsfExecutor;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.concurrent.ReflectionSequence;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.gdb.service.IGDBProcesses;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.IMICommandControl;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.embedcdt.debug.gdbjtag.core.DebugUtils;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.IGnuMcuDebuggerCommandsService;
import org.eclipse.embedcdt.internal.debug.gdbjtag.core.Activator;

public class GnuMcuRestartProcessSequence extends ReflectionSequence {

	// ------------------------------------------------------------------------

	private IGDBControl fCommandControl;
	private CommandFactory fCommandFactory;
	private IGDBProcesses fProcService;
	// private IReverseRunControl fReverseService;
	// private IGDBBackend fBackend;
	// private IGDBJtagDevice fGdbJtagDevice;
	private IGnuMcuDebuggerCommandsService fDebuggerCommands;

	private DsfServicesTracker fTracker;

	// This variable will be used to store the original container context,
	// but once the new process is started (restarted), it will contain the new
	// container context. This new container context has for parent the process
	// context, which holds the new pid.
	private IContainerDMContext fContainerDmc;

	// If the user requested a stop_on_main, this variable will hold the
	// breakpoint
	// private MIBreakpoint fUserBreakpoint;
	// Since the stop_on_main option allows the user to set the breakpoint on
	// any
	// symbol, we use this variable to know if the stop_on_main breakpoint was
	// really
	// on the main() method.
	// private boolean fUserBreakpointIsOnMain;

	// private boolean fReverseEnabled;
	@SuppressWarnings("unused")
	private final Map<String, Object> fAttributes;

	// private final boolean fRestart;
	// private final DataRequestMonitor<IContainerDMContext>
	// fDataRequestMonitor;

	// ------------------------------------------------------------------------

	public GnuMcuRestartProcessSequence(DsfExecutor executor, IContainerDMContext containerDmc,
			Map<String, Object> attributes, boolean restart, DataRequestMonitor<IContainerDMContext> rm) {
		super(executor, rm);

		assert executor != null;
		assert containerDmc != null;
		if (attributes == null) {
			// If no attributes are specified, simply use an empty map.
			attributes = new HashMap<>();
		}

		fContainerDmc = containerDmc;
		fAttributes = attributes;
		// fRestart = restart;
		// fDataRequestMonitor = rm;
	}

	// ------------------------------------------------------------------------

	protected IContainerDMContext getContainerContext() {
		return fContainerDmc;
	}

	private void queueCommands(List<String> commands, RequestMonitor rm) {
		DebugUtils.queueCommands(commands, rm, fCommandControl, getExecutor());
	}

	@Override
	protected String[] getExecutionOrder(String group) {
		if (GROUP_TOP_LEVEL.equals(group)) {
			return new String[] { //
					"stepInitializeBaseSequence", //$NON-NLS-1$
					"stepRestartCommands", //$NON-NLS-1$
			};
		}
		return null;
	}

	/**
	 * Initialise the members of the StartOrRestartProcessSequence_7_0 class.
	 * This step is mandatory for the rest of the sequence to complete.
	 */
	@Execute
	public void stepInitializeBaseSequence(RequestMonitor rm) {

		fTracker = new DsfServicesTracker(Activator.getInstance().getBundle().getBundleContext(),
				fContainerDmc.getSessionId());
		fCommandControl = fTracker.getService(IGDBControl.class);
		fCommandFactory = fTracker.getService(IMICommandControl.class).getCommandFactory();
		fProcService = fTracker.getService(IGDBProcesses.class);
		fDebuggerCommands = fTracker.getService(IGnuMcuDebuggerCommandsService.class);
		if (fCommandControl == null || fCommandFactory == null || fProcService == null || fDebuggerCommands == null) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IDsfStatusConstants.INTERNAL_ERROR,
					"Cannot obtain service", null)); //$NON-NLS-1$
			rm.done();
			return;
		}

		// fBackend = fTracker.getService(IGDBBackend.class);
		// fGdbJtagDevice = DebugUtils.getGDBJtagDevice(fAttributes);
		rm.done();
	}

	@Execute
	public void stepRestartCommands(final RequestMonitor rm) {

		List<String> commandsList = new ArrayList<>();

		IStatus status = fDebuggerCommands.addGnuMcuRestartCommands(commandsList);

		if (!status.isOK()) {
			rm.setStatus(status);
			rm.done();
			return;
		}

		queueCommands(commandsList, rm);
	}

	// ------------------------------------------------------------------------
}
