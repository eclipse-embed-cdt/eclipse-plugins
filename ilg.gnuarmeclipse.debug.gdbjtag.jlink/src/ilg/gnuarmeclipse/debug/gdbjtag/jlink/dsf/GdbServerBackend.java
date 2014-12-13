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

package ilg.gnuarmeclipse.debug.gdbjtag.jlink.dsf;

import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmGdbServerBackend;
import ilg.gnuarmeclipse.debug.gdbjtag.jlink.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.jlink.ConfigurationAttributes;
import ilg.gnuarmeclipse.debug.gdbjtag.jlink.SemihostingProcess;
import ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui.TabDebugger;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl.InitializationShutdownStep;
import org.eclipse.cdt.dsf.mi.service.IMIBackend;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.osgi.framework.BundleContext;

public class GdbServerBackend extends GnuArmGdbServerBackend {

	// ------------------------------------------------------------------------

	private final ILaunchConfiguration fLaunchConfiguration;
	private boolean doStartGdbServer = false;
	private boolean doStartSemihostingConsole = false;
	private Process fSemihostingProcess;
	private SemihostingMonitorJob fSemihostingMonitorJob;

	private State fSemihostingBackendState = State.NOT_INITIALIZED;

	@SuppressWarnings("unused")
	private int fSemihostingExitValue = 0;

	private int fGdbServerLaunchTimeout = 30;

	// ------------------------------------------------------------------------

	public GdbServerBackend(DsfSession session, ILaunchConfiguration lc) {
		super(session, lc);
		fLaunchConfiguration = lc;

		System.out.println("GdbServerBackend(" + session + "," + lc.getName()
				+ ")");

	}

	// ------------------------------------------------------------------------

	@Override
	public void initialize(final RequestMonitor rm) {

		System.out.println("GdbServerBackend.initialize()");

		super.initialize(new RequestMonitor(getExecutor(), rm) {

			protected void handleSuccess() {
				doInitialize(rm);
			}
		});
	}

	// @SuppressWarnings("rawtypes")
	private void doInitialize(RequestMonitor rm) {
		// register(new String[] { IGdbServerBackendService.class.getName() },
		// new Hashtable());

		try {
			doStartGdbServer = fLaunchConfiguration.getAttribute(
					ConfigurationAttributes.DO_START_GDB_SERVER,
					ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT);

			doStartSemihostingConsole = doStartGdbServer
					&& fLaunchConfiguration
							.getAttribute(
									ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
									ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT);

		} catch (CoreException e) {
		}

		if (doStartGdbServer) {

			final Sequence.Step[] initializeSteps = new Sequence.Step[] {

					new GdbServerStep(
							InitializationShutdownStep.Direction.INITIALIZING),
					new GdbServerMonitorStep(
							InitializationShutdownStep.Direction.INITIALIZING),

					new SemihostingStep(
							InitializationShutdownStep.Direction.INITIALIZING),
					new SemihostingMonitorStep(
							InitializationShutdownStep.Direction.INITIALIZING), };

			// the sequence completion code will handle the client startup
			Sequence startupSequence = new Sequence(getExecutor(), rm) {
				@Override
				public Step[] getSteps() {
					return initializeSteps;
				}
			};
			getExecutor().execute(startupSequence);

		} else {
			rm.done();
		}
	}

	@Override
	public void shutdown(RequestMonitor rm) {

		System.out.println("GdbServerBackend.shutdown()");

		// Remove this service from DSF.
		// unregister();

		super.shutdown(rm);
	}

	// ------------------------------------------------------------------------

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getInstance().getBundle().getBundleContext();
	}

	@Override
	public String[] getCommandLineArray() {
		String[] commandLineArray = TabDebugger
				.getGdbServerCommandLineArray(fLaunchConfiguration);

		return commandLineArray;
	}

	@Override
	public int getServerLaunchTimeoutSeconds() {
		return fGdbServerLaunchTimeout;
	}

	@Override
	public String getStartingJobName() {
		return "Starting J-Link GDB Server";
	}

	@Override
	public String getTerminatingJobName() {
		return "Terminating J-Link GDB Server";
	}

	// ------------------------------------------------------------------------

	public Process getSemihostingProcess() {
		return fSemihostingProcess;
	}

	@Override
	public void destroy() {
		System.out.println("GdbServerBackend.destroy() "
				+ Thread.currentThread());

		// Destroy semihosting process
		if (fSemihostingBackendState == State.STARTED) {
			System.out
					.println("GdbServerBackend.destroy() before fSemihostingProcess.destroy()");
			fSemihostingProcess.destroy();
		}

		// Destroy GDB client
		DsfServicesTracker tracker = getServicesTracker();
		if (tracker != null) {
			IMIBackend backend = (IMIBackend) tracker
					.getService(IMIBackend.class);
			if (backend != null) {
				backend.destroy();
			}
		}

		// Destroy GDB server
		super.destroy();

	}

	// ------------------------------------------------------------------------
	// start the Gdb semihosting console
	protected class SemihostingStep extends InitializationShutdownStep {

		SemihostingStep(Direction direction) {
			super(direction);
		}

		@Override
		public void initialize(final RequestMonitor requestMonitor) {

			if (!doStartSemihostingConsole) {
				requestMonitor.done();
				return;
			}

			class SemihostingLaunchMonitor {
				boolean fLaunched = false;
				boolean fTimedOut = false;
			}

			final SemihostingLaunchMonitor fSemihostingLaunchMonitor = new SemihostingLaunchMonitor();

			final RequestMonitor fTmpLaunchRequestMonitor = new RequestMonitor(
					getExecutor(), requestMonitor) {

				@Override
				protected void handleCompleted() {
					System.out
							.println("SemihostingStep initalise() handleCompleted()");
					if (!fSemihostingLaunchMonitor.fTimedOut) {
						fSemihostingLaunchMonitor.fLaunched = true;
						if (!isSuccess()) {
							requestMonitor.setStatus(getStatus());
						}
						requestMonitor.done();
					}
				}
			};

			final Job startSemihostingJob = new Job(
					"Starting GDB Semihosting Process") { //$NON-NLS-1$
				{
					setSystem(true);
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {

					if (fTmpLaunchRequestMonitor.isCanceled()) {

						System.out.println("startSemihostingJob run cancel");
						fTmpLaunchRequestMonitor.setStatus(new Status(
								IStatus.CANCEL, Activator.PLUGIN_ID, -1,
								"Canceled starting GDB semihosting", null)); //$NON-NLS-1$
						fTmpLaunchRequestMonitor.done();
						return Status.OK_STATUS;
					}

					try {
						String host = "localhost";

						int port = fLaunchConfiguration
								.getAttribute(
										ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
										ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT);

						fSemihostingProcess = launchSemihostingProcess(host,
								port);
						// Need to do this on the executor for thread-safety
						getExecutor().submit(new DsfRunnable() {
							@Override
							public void run() {
								System.out
										.println("startSemihostingJob run State.STARTED");
								fSemihostingBackendState = State.STARTED;
							}
						});
						// Don't send the backendStarted event yet. We wait
						// until we have registered this service
						// so that other services can have access to it.
					} catch (CoreException e) {
						fTmpLaunchRequestMonitor.setStatus(new Status(
								IStatus.ERROR, Activator.PLUGIN_ID, -1, e
										.getMessage(), e));
						fTmpLaunchRequestMonitor.done();
						return Status.OK_STATUS;
					}

					// TODO: check if the server started properly

					fTmpLaunchRequestMonitor.done();
					System.out.println("startSemihostingJob run completed");
					return Status.OK_STATUS;
				}
			};
			startSemihostingJob.schedule();

			System.out
					.println("SemihostingStep initalise() after job schedule");

			getExecutor().schedule(new Runnable() {

				@Override
				public void run() {

					// Only process the event if we have not finished yet (hit
					// the breakpoint).
					if (!fSemihostingLaunchMonitor.fLaunched) {
						fSemihostingLaunchMonitor.fTimedOut = true;
						Thread jobThread = startSemihostingJob.getThread();
						if (jobThread != null) {
							System.out.println("interrupt thread " + jobThread);

							jobThread.interrupt();
						}
						requestMonitor
								.setStatus(new Status(
										IStatus.ERROR,
										Activator.PLUGIN_ID,
										DebugException.TARGET_REQUEST_FAILED,
										"Timed out trying to launch GDB Server.", null)); //$NON-NLS-1$
						requestMonitor.done();
					}
				}
			}, fGdbServerLaunchTimeout, TimeUnit.SECONDS);

			System.out.println("SemihostingStep initalise() return");
		}

		@Override
		protected void shutdown(final RequestMonitor requestMonitor) {

			if (!doStartSemihostingConsole) {
				requestMonitor.done();
				return;
			}

			if (fSemihostingBackendState != State.STARTED) {
				// gdb not started yet or already killed, don't bother starting
				// a job to kill it
				requestMonitor.done();
				return;
			}

			new Job("Terminating GDB semihosting Process.") { //$NON-NLS-1$
				{
					setSystem(true);
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						// Need to do this on the executor for thread-safety
						// And we should wait for it to complete since we then
						// check if the killing of GDB worked.
						System.out.println("SemihostingStep shutdown() run()");
						getExecutor().submit(new DsfRunnable() {
							@Override
							public void run() {
								System.out
										.println("SemihostingStep shutdown() run() run()");
								destroy();

								if (fSemihostingMonitorJob.fMonitorExited) {
									// Now that we have destroyed the process,
									// and that the monitoring thread was
									// killed,
									// we need to set our state and send the
									// event
									System.out
											.println("SemihostingStep shutdown() run() run() State.TERMINATED");
									fSemihostingBackendState = State.TERMINATED;
									// getSession().dispatchEvent(
									// new BackendStateChangedEvent(
									// getSession().getId(),
									// getId(), State.TERMINATED),
									// getProperties());
								}
							}
						}).get();
					} catch (InterruptedException e1) {
					} catch (ExecutionException e1) {
					}

					System.out
							.println("SemihostingStep shutdown() run() before getting exitValue");
					int attempts = 0;
					while (attempts < 10) {
						try {
							// Don't know if we really need the exit value...
							// but what the heck.
							// throws exception if process not exited
							fSemihostingExitValue = fSemihostingProcess
									.exitValue();

							System.out
									.println("SemihostingStep shutdown() run() return");
							requestMonitor.done();
							return Status.OK_STATUS;
						} catch (IllegalThreadStateException ie) {
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
						attempts++;
					}
					System.out
							.println("SemihostingStep shutdown() run() REQUEST_FAILED");
					requestMonitor.setStatus(new Status(IStatus.ERROR,
							Activator.PLUGIN_ID,
							IDsfStatusConstants.REQUEST_FAILED,
							"GDB semihosting terminate failed", null)); //$NON-NLS-1$
					requestMonitor.done();
					return Status.OK_STATUS;
				}
			}.schedule();
			System.out.println("SemihostingStep shutdown() return");
		}

		protected Process launchSemihostingProcess(String host, int port)
				throws CoreException {

			SemihostingProcess proc = new SemihostingProcess(host, port);

			// proc.submit(getExecutor());
			proc.submit();

			System.out.println("launchSemihostingProcess() return " + proc);
			return proc;
		}

	}

	/**
	 * Monitors a system process, waiting for it to terminate, and then notifies
	 * the associated runtime process.
	 */
	private class SemihostingMonitorJob extends Job {

		boolean fMonitorExited = false;
		DsfRunnable fMonitorStarted;
		Process fProcess;

		SemihostingMonitorJob(Process process, DsfRunnable monitorStarted) {
			super("Semihosting process monitor job."); //$NON-NLS-1$
			fProcess = process;
			fMonitorStarted = monitorStarted;
			setSystem(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			synchronized (fProcess) {
				System.out.println("SemihostingMonitorJob.run() submit "
						+ fMonitorStarted + " thread " + getThread());
				getExecutor().submit(fMonitorStarted);
				try {
					fProcess.waitFor();
					fSemihostingExitValue = fProcess.exitValue();

					// Need to do this on the executor for thread-safety
					getExecutor().submit(new DsfRunnable() {
						@Override
						public void run() {
							System.out
									.println("SemihostingMonitorJob.run() run() thread "
											+ getThread());
							destroy();
							System.out
									.println("SemihostingMonitorJob.run() run() State.TERMINATED");
							fSemihostingBackendState = State.TERMINATED;
							// getSession()
							// .dispatchEvent(
							// new BackendStateChangedEvent(
							// getSession().getId(),
							// getId(), State.TERMINATED),
							// getProperties());
						}
					});
				} catch (InterruptedException ie) {
					// clear interrupted state
					Thread.interrupted();
				}

				System.out
						.println("SemihostingMonitorJob.run() fMonitorExited = true thread "
								+ getThread());
				fMonitorExited = true;
			}
			return Status.OK_STATUS;
		}

		void kill() {
			synchronized (fProcess) {
				if (!fMonitorExited) {
					Thread thread = getThread();
					if (thread != null) {
						System.out
								.println("SemihostingMonitorJob.kill() interrupt "
										+ thread.toString());
						thread.interrupt();
					} else {
						System.out.println("null thread");
					}
				}
			}
		}
	}

	// ----- SemihostingMonitorStep -----
	protected class SemihostingMonitorStep extends InitializationShutdownStep {

		SemihostingMonitorStep(Direction direction) {
			super(direction);
		}

		@Override
		public void initialize(final RequestMonitor requestMonitor) {

			if (!doStartSemihostingConsole) {
				requestMonitor.done();
				return;
			}

			fSemihostingMonitorJob = new SemihostingMonitorJob(
					fSemihostingProcess, new DsfRunnable() {
						@Override
						public void run() {
							requestMonitor.done();
						}
					});
			fSemihostingMonitorJob.schedule();
		}

		@Override
		protected void shutdown(RequestMonitor requestMonitor) {

			if (!doStartSemihostingConsole) {
				requestMonitor.done();
				return;
			}

			System.out.println("SemihostingMonitorStep.shutdown()");
			if (fSemihostingMonitorJob != null) {
				fSemihostingMonitorJob.kill();
			}
			requestMonitor.done();
			System.out.println("SemihostingMonitorStep.shutdown() return");
		}
	}

	// ------------------------------------------------------------------------

}
