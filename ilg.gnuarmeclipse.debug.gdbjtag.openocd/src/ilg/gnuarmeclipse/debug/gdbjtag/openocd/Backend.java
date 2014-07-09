package ilg.gnuarmeclipse.debug.gdbjtag.openocd;

import ilg.gnuarmeclipse.debug.gdbjtag.openocd.ui.TabDebugger;

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

	private Process fServerProcess;
	private Process fSemihostingProcess;

	private State fServerBackendState = State.NOT_INITIALIZED;
	private State fSemihostingBackendState = State.NOT_INITIALIZED;

	private int fGdbServerLaunchTimeout = 30;

	private ServerMonitorJob fServerMonitorJob;
	private SemihostingMonitorJob fSemihostingMonitorJob;

	private int fGdbServerExitValue = 0;
	private int fSemihostingExitValue = 0;

	// private Process fTelnetProcess;

	private boolean doStartGdbServer = false;
	private boolean doStartSemihostingConsole = false;

	public Backend(DsfSession session, ILaunchConfiguration lc) {

		super(session, lc);
		fLaunchConfiguration = lc;

		try {
			doStartGdbServer = fLaunchConfiguration.getAttribute(
					ConfigurationAttributes.DO_START_GDB_SERVER,
					ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT);

			doStartSemihostingConsole = doStartGdbServer
					&& fLaunchConfiguration
							.getAttribute(
									ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_TELNET_CONSOLE,
									ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_TELNET_CONSOLE_DEFAULT);

		} catch (CoreException e) {
		}
	}

	public Process getServerProcess() {
		return fServerProcess;
	}

	public Process getSemihostingProcess() {
		return fSemihostingProcess;
	}

	public State getServerState() {
		return fServerBackendState;
	}

	protected Process launchSemihostingProcess(String host, int port)
			throws CoreException {

		SemihostingProcess proc = new SemihostingProcess(host, port);

		// proc.submit(getExecutor());
		proc.submit();

		System.out.println("launchSemihostingProcess() return " + proc);
		return proc;
	}

	@Override
	public void destroy() {
		// Don't close the streams ourselves as it may be too early.
		// Wait for the actual user of the streams to close it.
		// Bug 339379

		// kill client first
		System.out.println("Backend.destroy() " + Thread.currentThread());
		super.destroy();

		// then semihosting
		if (fSemihostingBackendState == State.STARTED) {
			System.out
					.println("Backend.destroy() before fSemihostingProcess.destroy()");
			fSemihostingProcess.destroy();
		}

		// destroy() should be supported even if it's not spawner.
		if (fServerBackendState == State.STARTED) {
			System.out
					.println("Backend.destroy() before fServerProcess.destroy()");
			fServerProcess.destroy();
		}
	}

	@Override
	public void initialize(final RequestMonitor requestMonitor) {

		System.out.println("Backend.initialize() " + Thread.currentThread());
		if (doStartGdbServer) {

			final Sequence.Step[] initializeSteps = new Sequence.Step[] {

					new GdbServerStep(
							InitializationShutdownStep.Direction.INITIALIZING),
					new ServerMonitorJobStep(
							InitializationShutdownStep.Direction.INITIALIZING),

					new SemihostingConsoleStep(
							InitializationShutdownStep.Direction.INITIALIZING),
					new SemihostingMonitorJobStep(
							InitializationShutdownStep.Direction.INITIALIZING), };

			// the sequence completion code will handle the client startup
			Sequence startupSequence = new Sequence(getExecutor(),
					new RequestMonitor(getExecutor(), requestMonitor) {
						@Override
						protected void handleCompleted() {
							if (isSuccess()) {
								Backend.super.initialize(requestMonitor);
							} else {
								requestMonitor.setStatus(getStatus());
								requestMonitor.done();
							}
						}
					}) {
				@Override
				public Step[] getSteps() {
					return initializeSteps;
				}
			};
			getExecutor().execute(startupSequence);

		} else {
			super.initialize(requestMonitor);
		}
	}

	@Override
	public void shutdown(final RequestMonitor requestMonitor) {

		System.out.println("Backend.shutdown() " + Thread.currentThread());
		if (doStartGdbServer) {

			// first shutdown client, then the server
			super.shutdown(new ImmediateRequestMonitor(requestMonitor) {
				@Override
				protected void handleSuccess() {
					doShutdown(requestMonitor);
				}
			});

		} else {
			super.shutdown(requestMonitor);
		}
		System.out.println("Backend.shutdown() return");
	}

	private void doShutdown(final RequestMonitor requestMonitor) {

		final Sequence.Step[] shutdownSteps = new Sequence.Step[] {

				new SemihostingMonitorJobStep(
						InitializationShutdownStep.Direction.SHUTTING_DOWN),
				new SemihostingConsoleStep(
						InitializationShutdownStep.Direction.SHUTTING_DOWN),

				new ServerMonitorJobStep(
						InitializationShutdownStep.Direction.SHUTTING_DOWN),
				new GdbServerStep(
						InitializationShutdownStep.Direction.SHUTTING_DOWN), };

		Sequence shutdownSequence = new Sequence(getExecutor(), requestMonitor) {
			@Override
			public Step[] getSteps() {
				return shutdownSteps;
			}
		};
		getExecutor().execute(shutdownSequence);
		System.out.println("Backend.doShutdown() return");
	}

	// ------------------------------------------------------------------------
	// start the Gdb server
	protected class GdbServerStep extends InitializationShutdownStep {

		GdbServerStep(Direction direction) {
			super(direction);
		}

		@Override
		public void initialize(final RequestMonitor requestMonitor) {

			class ServerLaunchMonitor {
				boolean fLaunched = false;
				boolean fTimedOut = false;
			}

			final ServerLaunchMonitor fServerLaunchMonitor = new ServerLaunchMonitor();

			final RequestMonitor fTmpLaunchRequestMonitor = new RequestMonitor(
					getExecutor(), requestMonitor) {

				@Override
				protected void handleCompleted() {
					System.out
							.println("GdbServerStep initalise() handleCompleted()");
					if (!fServerLaunchMonitor.fTimedOut) {
						fServerLaunchMonitor.fLaunched = true;
						if (!isSuccess()) {
							requestMonitor.setStatus(getStatus());
						}
						requestMonitor.done();
					}
				}
			};

			final Job startGdbServerJob = new Job("Start GDB Server Job") { //$NON-NLS-1$
				{
					setSystem(true);
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {

					if (fTmpLaunchRequestMonitor.isCanceled()) {

						System.out.println("startGdbServerJob run cancel");
						fTmpLaunchRequestMonitor.setStatus(new Status(
								IStatus.CANCEL, Activator.PLUGIN_ID, -1,
								"Canceled starting GDB", null)); //$NON-NLS-1$
						fTmpLaunchRequestMonitor.done();
						return Status.OK_STATUS;
					}

					String serverOther = "";
					try {
						serverOther = fLaunchConfiguration
								.getAttribute(
										ConfigurationAttributes.GDB_SERVER_OTHER,
										ConfigurationAttributes.GDB_SERVER_OTHER_DEFAULT)
								.trim();
					} catch (CoreException e1) {
					}

					if (serverOther.length() == 0) {
						fTmpLaunchRequestMonitor
								.setStatus(new Status(
										IStatus.ERROR,
										Activator.PLUGIN_ID,
										-1,
										"OpenOCD requires some -c or -f configuration options to start.", null)); //$NON-NLS-1$
						fTmpLaunchRequestMonitor.done();
						return Status.OK_STATUS;
					}

					String[] commandLineArray = TabDebugger
							.getGdbServerCommandLineArray(fLaunchConfiguration);

					try {
						String projectName = fLaunchConfiguration.getAttribute(
								"org.eclipse.cdt.launch.PROJECT_ATTR", "");
						File dir = Utils.getProjectOsPath(projectName);

						fServerProcess = launchGDBProcess(commandLineArray, dir);
						// Need to do this on the executor for thread-safety
						getExecutor().submit(new DsfRunnable() {
							@Override
							public void run() {
								System.out
										.println("startGdbServerJob run() State.STARTED");
								fServerBackendState = State.STARTED;
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
					System.out.println("startGdbServerJob run completed");
					return Status.OK_STATUS;
				}
			};
			startGdbServerJob.schedule();

			System.out.println("GdbServerStep initalise() after job schedule");

			getExecutor().schedule(new Runnable() {

				@Override
				public void run() {

					// Only process the event if we have not finished yet (hit
					// the breakpoint).
					if (!fServerLaunchMonitor.fLaunched) {
						fServerLaunchMonitor.fTimedOut = true;
						Thread jobThread = startGdbServerJob.getThread();
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

			System.out.println("GdbServerStep initalise() return");
		}

		@Override
		protected void shutdown(final RequestMonitor requestMonitor) {

			if (fServerBackendState != State.STARTED) {
				// gdb not started yet or already killed, don't bother starting
				// a job to kill it
				requestMonitor.done();
				return;
			}

			new Job("Terminating GDB Server process.") { //$NON-NLS-1$
				{
					setSystem(true);
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						// Need to do this on the executor for thread-safety
						// And we should wait for it to complete since we then
						// check if the killing of GDB worked.
						System.out.println("GdbServerStep shutdown() run()");
						getExecutor().submit(new DsfRunnable() {
							@Override
							public void run() {
								System.out
										.println("GdbServerStep shutdown() run() run()");
								destroy();

								if (fServerMonitorJob.fMonitorExited) {
									// Now that we have destroyed the process,
									// and that the monitoring thread was
									// killed,
									// we need to set our state and send the
									// event
									System.out
											.println("GdbServerStep shutdown() run() run() State.TERMINATED");
									fServerBackendState = State.TERMINATED;
									getSession().dispatchEvent(
											new BackendStateChangedEvent(
													getSession().getId(),
													getId(), State.TERMINATED),
											getProperties());
								}
							}
						}).get();
					} catch (InterruptedException e1) {
					} catch (ExecutionException e1) {
					}

					System.out
							.println("GdbServerStep shutdown() run() before getting exitValue");
					int attempts = 0;
					while (attempts < 10) {
						try {
							// Don't know if we really need the exit value...
							// but what the heck.
							// throws exception if process not exited
							fGdbServerExitValue = fServerProcess.exitValue();

							System.out
									.println("GdbServerStep shutdown() run() return");
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
							.println("GdbServerStep shutdown() run() REQUEST_FAILED");
					requestMonitor.setStatus(new Status(IStatus.ERROR,
							Activator.PLUGIN_ID,
							IDsfStatusConstants.REQUEST_FAILED,
							"GDB Server terminate failed", null)); //$NON-NLS-1$
					requestMonitor.done();
					return Status.OK_STATUS;
				}
			}.schedule();
			System.out.println("GdbServerStep shutdown() return");
		}
	}

	/**
	 * Monitors a system process, waiting for it to terminate, and then notifies
	 * the associated runtime process.
	 */
	private class ServerMonitorJob extends Job {

		boolean fMonitorExited = false;
		DsfRunnable fMonitorStarted;
		Process fProcess;

		ServerMonitorJob(Process process, DsfRunnable monitorStarted) {
			super("GDB Server process monitor job."); //$NON-NLS-1$
			fProcess = process;
			fMonitorStarted = monitorStarted;
			setSystem(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			synchronized (fProcess) {
				System.out.println("ServerMonitorJob.run() submit "
						+ fMonitorStarted + " thread " + getThread());
				getExecutor().submit(fMonitorStarted);
				try {
					fProcess.waitFor();
					fGdbServerExitValue = fProcess.exitValue();

					// Need to do this on the executor for thread-safety
					getExecutor().submit(new DsfRunnable() {
						@Override
						public void run() {
							System.out
									.println("ServerMonitorJob.run() run() thread "
											+ getThread());
							destroy();
							System.out
									.println("ServerMonitorJob.run() run() State.TERMINATED");
							fServerBackendState = State.TERMINATED;
							getSession()
									.dispatchEvent(
											new BackendStateChangedEvent(
													getSession().getId(),
													getId(), State.TERMINATED),
											getProperties());
						}
					});
				} catch (InterruptedException ie) {
					// clear interrupted state
					Thread.interrupted();
				}

				System.out
						.println("ServerMonitorJob.run() fMonitorExited = true thread "
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
						System.out.println("ServerMonitorJob.kill() interrupt "
								+ thread.toString());
						getThread().interrupt();
					} else {
						System.out.println("null thread");
					}
				}
			}
		}
	}

	protected class ServerMonitorJobStep extends InitializationShutdownStep {

		ServerMonitorJobStep(Direction direction) {
			super(direction);
		}

		@Override
		public void initialize(final RequestMonitor requestMonitor) {

			fServerMonitorJob = new ServerMonitorJob(fServerProcess,
					new DsfRunnable() {
						@Override
						public void run() {
							requestMonitor.done();
						}
					});
			fServerMonitorJob.schedule();
		}

		@Override
		protected void shutdown(RequestMonitor requestMonitor) {

			System.out.println("ServerMonitorJobStep.shutdown()");
			if (fServerMonitorJob != null) {
				fServerMonitorJob.kill();
			}
			requestMonitor.done();
			System.out.println("ServerMonitorJobStep.shutdown() return");
		}
	}

	// ------------------------------------------------------------------------
	// start the Gdb semihosting console
	protected class SemihostingConsoleStep extends InitializationShutdownStep {

		SemihostingConsoleStep(Direction direction) {
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
							.println("SemihostingConsoleStep initalise() handleCompleted()");
					if (!fSemihostingLaunchMonitor.fTimedOut) {
						fSemihostingLaunchMonitor.fLaunched = true;
						if (!isSuccess()) {
							requestMonitor.setStatus(getStatus());
						}
						requestMonitor.done();
					}
				}
			};

			final Job startSemihostingJob = new Job("Start GDB Semihosting Job") { //$NON-NLS-1$
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
					.println("SemihostingConsoleStep initalise() after job schedule");

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

			System.out.println("SemihostingConsoleStep initalise() return");
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

			new Job("Terminating GDB semihosting process.") { //$NON-NLS-1$
				{
					setSystem(true);
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						// Need to do this on the executor for thread-safety
						// And we should wait for it to complete since we then
						// check if the killing of GDB worked.
						System.out
								.println("SemihostingConsoleStep shutdown() run()");
						getExecutor().submit(new DsfRunnable() {
							@Override
							public void run() {
								System.out
										.println("SemihostingConsoleStep shutdown() run() run()");
								destroy();

								if (fSemihostingMonitorJob.fMonitorExited) {
									// Now that we have destroyed the process,
									// and that the monitoring thread was
									// killed,
									// we need to set our state and send the
									// event
									System.out
											.println("SemihostingConsoleStep shutdown() run() run() State.TERMINATED");
									fSemihostingBackendState = State.TERMINATED;
									getSession().dispatchEvent(
											new BackendStateChangedEvent(
													getSession().getId(),
													getId(), State.TERMINATED),
											getProperties());
								}
							}
						}).get();
					} catch (InterruptedException e1) {
					} catch (ExecutionException e1) {
					}

					System.out
							.println("SemihostingConsoleStep shutdown() run() before getting exitValue");
					int attempts = 0;
					while (attempts < 10) {
						try {
							// Don't know if we really need the exit value...
							// but what the heck.
							// throws exception if process not exited
							fSemihostingExitValue = fSemihostingProcess
									.exitValue();

							System.out
									.println("SemihostingConsoleStep shutdown() run() return");
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
							.println("SemihostingConsoleStep shutdown() run() REQUEST_FAILED");
					requestMonitor.setStatus(new Status(IStatus.ERROR,
							Activator.PLUGIN_ID,
							IDsfStatusConstants.REQUEST_FAILED,
							"GDB semihosting terminate failed", null)); //$NON-NLS-1$
					requestMonitor.done();
					return Status.OK_STATUS;
				}
			}.schedule();
			System.out.println("SemihostingConsoleStep shutdown() return");
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
							getSession()
									.dispatchEvent(
											new BackendStateChangedEvent(
													getSession().getId(),
													getId(), State.TERMINATED),
											getProperties());
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

	// ----- SemihostingMonitorJobStep -----
	protected class SemihostingMonitorJobStep extends
			InitializationShutdownStep {

		SemihostingMonitorJobStep(Direction direction) {
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

			System.out.println("SemihostingMonitorJobStep.shutdown()");
			if (fSemihostingMonitorJob != null) {
				fSemihostingMonitorJob.kill();
			}
			requestMonitor.done();
			System.out.println("SemihostingMonitorJobStep.shutdown() return");
		}
	}

	// ------------------------------------------------------------------------
}
