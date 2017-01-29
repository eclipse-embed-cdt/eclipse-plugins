/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.dsf;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IGdbServerBackendService;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IGdbServerBackendService.ServerBackendStateChangedEvent;

import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.datamodel.AbstractDMEvent;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl_7_2;
import org.eclipse.cdt.dsf.mi.service.IMIBackend;
import org.eclipse.cdt.dsf.mi.service.IMIBackend.BackendStateChangedEvent;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.service.DsfServiceEventHandler;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

public class GnuArmControl_7_2 extends GDBControl_7_2 {

	// ========================================================================

	/**
	 * Event indicating that the CommandControl has terminated. There is not
	 * much custom functionality, the trick is to be a instance of
	 * ICommandControlShutdownDMEvent, this is used by GDBLaunch to hunt for
	 * this event.
	 */
	private static class GnuArmCommandControlShutdownDMEvent extends AbstractDMEvent<ICommandControlDMContext>
			implements ICommandControlShutdownDMEvent {

		public GnuArmCommandControlShutdownDMEvent(ICommandControlDMContext context) {
			super(context);
		}
	}

	// ------------------------------------------------------------------------

	private IGdbServerBackendService fServerBackend;
	private String fMode;

	// ------------------------------------------------------------------------

	public GnuArmControl_7_2(DsfSession session, ILaunchConfiguration config, CommandFactory factory, String mode) {
		super(session, config, factory);

		fMode = mode;
	}

	// ------------------------------------------------------------------------

	@Override
	public void initialize(final RequestMonitor rm) {
		super.initialize(new ImmediateRequestMonitor(rm) {
			@Override
			protected void handleSuccess() {
				doInitialize(rm);
			}
		});
	}

	private void doInitialize(final RequestMonitor rm) {

		fServerBackend = getServicesTracker().getService(IGdbServerBackendService.class);
		rm.done();
	}

	// ------------------------------------------------------------------------

	@Override
	protected Sequence getCompleteInitializationSequence(Map<String, Object> attributes,
			RequestMonitorWithProgress rm) {
		return new GnuArmFinalLaunchSequence_7_2(getSession(), attributes, fMode, rm);
	}

	/**
	 * Be sure it is present and does nothing, otherwise the parent
	 * implementation will fire and dispatch the event.
	 */
	@DsfServiceEventHandler
	public void eventDispatched(BackendStateChangedEvent e) {
		;
	}

	/**
	 * Handle "GDB Exited" event, just relay to following event.
	 */
	@DsfServiceEventHandler
	public void eventDispatched(ServerBackendStateChangedEvent e) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmControl_7_2.eventDispatched() " + e);
		}

		if (e.getState() == IMIBackend.State.TERMINATED && e.getSessionId().equals(getSession().getId())
				&& e.getBackendId().equals(fServerBackend.getId())) {

			// Will be captured by GdbLaunch (waiting for
			// ICommandControlShutdownDMEvent), which will trigger
			// sessionShutdown.
			getSession().dispatchEvent(new GnuArmCommandControlShutdownDMEvent(getContext()), getProperties());
		}
	}

	// ------------------------------------------------------------------------
}
