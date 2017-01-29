/*******************************************************************************
 * Copyright (c) 2011 Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Marc Khouzam (Ericsson) - initial API and implementation
 *     Liviu Ionescu - ARM version
 *******************************************************************************/
package ilg.gnuarmeclipse.debug.gdbjtag.dsf;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;

import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DsfExecutor;
import org.eclipse.cdt.dsf.concurrent.ImmediateDataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Immutable;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_2_1;
import org.eclipse.cdt.dsf.gdb.service.IGDBBackend;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.IMIBackend.State;
import org.eclipse.cdt.dsf.mi.service.IMICommandControl;
import org.eclipse.cdt.dsf.mi.service.IMIContainerDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIProcessDMContext;
import org.eclipse.cdt.dsf.mi.service.IMIRunControl;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

// Used to redefine the ProcessSequence, where the reset happens
public class GnuArmProcesses_7_2_1 extends GDBProcesses_7_2_1 {

	// ========================================================================

	/**
	 * Event indicating that the server back end process has started or
	 * terminated.
	 */
	@Immutable
	public static class ProcessStateChangedEvent {

		// --------------------------------------------------------------------

		final private String fSessionId;
		final private State fState;

		// --------------------------------------------------------------------

		public ProcessStateChangedEvent(String sessionId, State state) {
			fSessionId = sessionId;
			fState = state;
		}

		// --------------------------------------------------------------------

		public String getSessionId() {
			return fSessionId;
		}

		public State getState() {
			return fState;
		}

		// --------------------------------------------------------------------
	}

	// ------------------------------------------------------------------------

	private IGDBControl fCommandControl;
	private IGDBBackend fBackend;
	private CommandFactory fCommandFactory;

	// ------------------------------------------------------------------------

	public GnuArmProcesses_7_2_1(DsfSession session) {
		super(session);
	}

	// ------------------------------------------------------------------------

	@Override
	public void initialize(final RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmProcesses_7_2_1.initialize()");
		}
		super.initialize(new ImmediateRequestMonitor(rm) {
			@Override
			protected void handleSuccess() {
				doInitialize(rm);
			}
		});
	}

	private void doInitialize(RequestMonitor rm) {

		fCommandControl = getServicesTracker().getService(IGDBControl.class);
		fBackend = getServicesTracker().getService(IGDBBackend.class);
		fCommandFactory = getServicesTracker().getService(IMICommandControl.class).getCommandFactory();
		rm.done();

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmProcesses_7_2_1.initialize() done");
		}
	}

	// ------------------------------------------------------------------------

	@Override
	protected Sequence getStartOrRestartProcessSequence(DsfExecutor executor, IContainerDMContext containerDmc,
			Map<String, Object> attributes, boolean restart, DataRequestMonitor<IContainerDMContext> rm) {

		if (restart) {
			return new GnuArmRestartProcessSequence(executor, containerDmc, attributes, restart, rm);
		}

		return super.getStartOrRestartProcessSequence(executor, containerDmc, attributes, restart, rm);
	}

	@Override
	public void canDetachDebuggerFromProcess(IDMContext dmc, DataRequestMonitor<Boolean> rm) {
		rm.setData(false);
		rm.done();
	}

	/**
	 * Process termination.
	 */
	@Override
	public void terminate(IThreadDMContext thread, final RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmProcesses_7_2_1.terminate()");
		}

		// For a core session, there is no concept of killing the inferior,
		// so lets kill GDB
		if (thread instanceof IMIProcessDMContext) {
			getDebuggingContext(thread, new ImmediateDataRequestMonitor<IDMContext>(rm) {
				@SuppressWarnings("unused")
				@Override
				protected void handleSuccess() {
					if (getData() instanceof IMIContainerDMContext) {

						if (true) {
							IMIRunControl runControl = getServicesTracker().getService(IMIRunControl.class);
							if (runControl != null && !runControl.isTargetAcceptingCommands()) {
								if (Activator.getInstance().isDebugging()) {
									System.out.println("GnuArmProcesses_7_2_1.terminate() interrupt");
								}
								fBackend.interrupt();
							}

							// Does nothing on terminate, just exit.
							fCommandControl.queueCommand(
									fCommandFactory
											// .createMIGDBExit
											.createMIInterpreterExecConsoleKill((IMIContainerDMContext) getData()),
									new ImmediateDataRequestMonitor<MIInfo>(rm) {
										@Override
										protected void handleSuccess() {
											if (Activator.getInstance().isDebugging()) {
												System.out.println(
														"GnuArmProcesses_7_2_1.terminate() dispatchEvent(ProcessStateChangedEvent, TERMINATED)");
											}

											getSession().dispatchEvent(new ProcessStateChangedEvent(
													getSession().getId(), State.TERMINATED), getProperties());

											if (Activator.getInstance().isDebugging()) {
												System.out.println("GnuArmProcesses_7_2_1.terminate() done");
											}

											rm.done();
										}
									});
						} else {
							if (Activator.getInstance().isDebugging()) {
								System.out.println("GnuArmProcesses_7_2_1.terminate() done");
							}
							rm.done();
						}
					} else {
						rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, INTERNAL_ERROR,
								"Invalid process context.", null)); //$NON-NLS-1$
						rm.done();
					}
				}
			});
		} else {
			super.terminate(thread, rm);
		}
	}
	// ------------------------------------------------------------------------
}
