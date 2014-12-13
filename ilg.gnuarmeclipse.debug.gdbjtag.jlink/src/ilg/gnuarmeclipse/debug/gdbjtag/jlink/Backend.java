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

package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui.TabDebugger;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl.InitializationShutdownStep;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;

public class Backend extends Backend0 {

	private final ILaunchConfiguration fLaunchConfiguration;

	// private Process fServerProcess;
	// private Process fSemihostingProcess;
	//
	// private State fServerBackendState = State.NOT_INITIALIZED;
	// private State fSemihostingBackendState = State.NOT_INITIALIZED;
	//
	// private int fGdbServerLaunchTimeout = 30;

	// private ServerMonitorJob fServerMonitorJob;
	// private SemihostingMonitorJob fSemihostingMonitorJob;

	@SuppressWarnings("unused")
	private int fGdbServerExitValue = 0;
	@SuppressWarnings("unused")
	private int fSemihostingExitValue = 0;

	// private Process fTelnetProcess;

	private boolean doStartGdbServer = false;
	private boolean doStartSemihostingConsole = false;

	public Backend(DsfSession session, ILaunchConfiguration lc) {

		super(session, lc);
		fLaunchConfiguration = lc;

		// try {
		// doStartGdbServer = fLaunchConfiguration.getAttribute(
		// ConfigurationAttributes.DO_START_GDB_SERVER,
		// ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT);
		//
		// doStartSemihostingConsole = doStartGdbServer
		// && fLaunchConfiguration
		// .getAttribute(
		// ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
		// ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT);
		//
		// } catch (CoreException e) {
		// }
	}

	// public Process getServerProcess() {
	// return fServerProcess;
	// }
	//
	// public Process getSemihostingProcess() {
	// return fSemihostingProcess;
	// }

	// protected Process launchSemihostingProcess(String host, int port)
	// throws CoreException {
	//
	// SemihostingProcess proc = new SemihostingProcess(host, port);
	//
	// // proc.submit(getExecutor());
	// proc.submit();
	//
	// System.out.println("launchSemihostingProcess() return " + proc);
	// return proc;
	// }

	@Override
	public void destroy() {
		// Don't close the streams ourselves as it may be too early.
		// Wait for the actual user of the streams to close it.
		// Bug 339379

		// kill client first
		System.out.println("Backend.destroy() " + Thread.currentThread());
		super.destroy();

		// then semihosting
		// if (fSemihostingBackendState == State.STARTED) {
		// System.out
		// .println("Backend.destroy() before fSemihostingProcess.destroy()");
		// fSemihostingProcess.destroy();
		// }
		//
		// // destroy() should be supported even if it's not spawner.
		// if (fServerBackendState == State.STARTED) {
		// System.out
		// .println("Backend.destroy() before fServerProcess.destroy()");
		// fServerProcess.destroy();
		// }
	}

	@Override
	public void initialize(final RequestMonitor requestMonitor) {

		System.out.println("Backend.initialize() " + Thread.currentThread());
		if (false && doStartGdbServer) {

			// final Sequence.Step[] initializeSteps = new Sequence.Step[] {
			//
			// new GdbServerStep(
			// InitializationShutdownStep.Direction.INITIALIZING),
			// new ServerMonitorJobStep(
			// InitializationShutdownStep.Direction.INITIALIZING),
			//
			// new SemihostingConsoleStep(
			// InitializationShutdownStep.Direction.INITIALIZING),
			// new SemihostingMonitorJobStep(
			// InitializationShutdownStep.Direction.INITIALIZING), };
			//
			// // the sequence completion code will handle the client startup
			// Sequence startupSequence = new Sequence(getExecutor(),
			// new RequestMonitor(getExecutor(), requestMonitor) {
			// @Override
			// protected void handleCompleted() {
			// if (isSuccess()) {
			// Backend.super.initialize(requestMonitor);
			// } else {
			// requestMonitor.setStatus(getStatus());
			// requestMonitor.done();
			// }
			// }
			// }) {
			// @Override
			// public Step[] getSteps() {
			// return initializeSteps;
			// }
			// };
			// getExecutor().execute(startupSequence);

		} else {
			super.initialize(requestMonitor);
		}
	}

	@Override
	public void shutdown(final RequestMonitor requestMonitor) {

		System.out.println("Backend.shutdown() " + Thread.currentThread());
		if (false && doStartGdbServer) {

			// // first shutdown client, then the server
			// super.shutdown(new ImmediateRequestMonitor(requestMonitor) {
			// @Override
			// protected void handleSuccess() {
			// doShutdown(requestMonitor);
			// }
			// });

		} else {
			super.shutdown(requestMonitor);
		}
		System.out.println("Backend.shutdown() return");
	}

	// private void doShutdown(final RequestMonitor requestMonitor) {
	//
	// final Sequence.Step[] shutdownSteps = new Sequence.Step[] {
	//
	// new SemihostingMonitorJobStep(
	// InitializationShutdownStep.Direction.SHUTTING_DOWN),
	// new SemihostingConsoleStep(
	// InitializationShutdownStep.Direction.SHUTTING_DOWN),
	//
	// new ServerMonitorJobStep(
	// InitializationShutdownStep.Direction.SHUTTING_DOWN),
	// new GdbServerStep(
	// InitializationShutdownStep.Direction.SHUTTING_DOWN), };
	//
	// Sequence shutdownSequence = new Sequence(getExecutor(), requestMonitor) {
	// @Override
	// public Step[] getSteps() {
	// return shutdownSteps;
	// }
	// };
	// getExecutor().execute(shutdownSequence);
	// System.out.println("Backend.doShutdown() return");
	// }

}
