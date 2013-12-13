package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui.TabDebugger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.gdb.service.GDBBackend;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl.InitializationShutdownStep;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;

public class Backend extends GDBBackend {

	private final ILaunchConfiguration fLaunchConfiguration;
	private Process fServerProcess;
	private State fServerBackendState = State.NOT_INITIALIZED;
	private int fGdbServerLaunchTimeout = 30;
	private MonitorJob fServerMonitorJob;
	private int fGdbServerExitValue;

	private boolean doStartGdbServer = false;

	public Backend(DsfSession session, ILaunchConfiguration lc) {
		super(session, lc);
		fLaunchConfiguration = lc;

		try {
			doStartGdbServer = fLaunchConfiguration.getAttribute(
					ConfigurationAttributes.DO_START_GDB_SERVER,
					ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT);

		} catch (CoreException e) {
		}
	}

	public Process getServerProcess(){
		return fServerProcess;
	}
	
	@Override
    public void destroy() {
    	// Don't close the streams ourselves as it may be too early.
    	// Wait for the actual user of the streams to close it.
    	// Bug 339379
    	
		// kill client first
		super.destroy(); 
		
    	// destroy() should be supported even if it's not spawner. 
    	if (fServerBackendState == State.STARTED) {
    		fServerProcess.destroy();
    	}
    }

	@Override
	public void initialize(final RequestMonitor requestMonitor) {

		if (doStartGdbServer) {

			final Sequence.Step[] initializeSteps = new Sequence.Step[] {

					new GdbServerStep(
							InitializationShutdownStep.Direction.INITIALIZING),
					new MonitorJobStep(
							InitializationShutdownStep.Direction.INITIALIZING),
			// new
			// RegisterStep(InitializationShutdownStep.Direction.INITIALIZING),
			};

			// the sequence completion code will handle the client startup
			Sequence startupSequence = new Sequence(getExecutor(),
					new RequestMonitor(getExecutor(), requestMonitor) {
						@Override
						protected void handleCompleted() {
							Backend.super.initialize(requestMonitor);
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
	}

	private void doShutdown(final RequestMonitor requestMonitor) {

		final Sequence.Step[] shutdownSteps = new Sequence.Step[] {

				// new
				// RegisterStep(InitializationShutdownStep.Direction.SHUTTING_DOWN),
				new MonitorJobStep(
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
	}

	// start the Gdb server
	protected class GdbServerStep extends InitializationShutdownStep {

		GdbServerStep(Direction direction) {
			super(direction);
		}

		@Override
		public void initialize(final RequestMonitor requestMonitor) {
			class GdbServerLaunchMonitor {
				boolean fLaunched = false;
				boolean fTimedOut = false;
			}
			final GdbServerLaunchMonitor fGdbServerLaunchMonitor = new GdbServerLaunchMonitor();

			final RequestMonitor gdbLaunchRequestMonitor = new RequestMonitor(
					getExecutor(), requestMonitor) {
				@Override
				protected void handleCompleted() {
					if (!fGdbServerLaunchMonitor.fTimedOut) {
						fGdbServerLaunchMonitor.fLaunched = true;
						if (!isSuccess()) {
							requestMonitor.setStatus(getStatus());
						}
						requestMonitor.done();
					}
				}
			};

			final Job startGdbServerJob = new Job("Start GDB Process Job") { //$NON-NLS-1$
				{
					setSystem(true);
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					if (gdbLaunchRequestMonitor.isCanceled()) {
						gdbLaunchRequestMonitor.setStatus(new Status(
								IStatus.CANCEL, Activator.PLUGIN_ID, -1,
								"Canceled starting GDB", null)); //$NON-NLS-1$
						gdbLaunchRequestMonitor.done();
						return Status.OK_STATUS;
					}

					String commandLine = TabDebugger
							.getGdbServerCommandLine(fLaunchConfiguration);

					try {
						fServerProcess = launchGDBProcess(commandLine);
						// Need to do this on the executor for thread-safety
						getExecutor().submit(new DsfRunnable() {
							@Override
							public void run() {
								fServerBackendState = State.STARTED;
							}
						});
						// Don't send the backendStarted event yet. We wait
						// until we have registered this service
						// so that other services can have access to it.
					} catch (CoreException e) {
						gdbLaunchRequestMonitor.setStatus(new Status(
								IStatus.ERROR, Activator.PLUGIN_ID, -1, e
										.getMessage(), e));
						gdbLaunchRequestMonitor.done();
						return Status.CANCEL_STATUS; //OK_STATUS;
					}

					// TODO: check if the server started properly
					
					gdbLaunchRequestMonitor.done();
					return Status.OK_STATUS;
				}
			};
			startGdbServerJob.schedule();

			getExecutor().schedule(new Runnable() {
				@Override
				public void run() {
					// Only process the event if we have not finished yet (hit
					// the breakpoint).
					if (!fGdbServerLaunchMonitor.fLaunched) {
						fGdbServerLaunchMonitor.fTimedOut = true;
						Thread jobThread = startGdbServerJob.getThread();
						if (jobThread != null) {
							jobThread.interrupt();
						}
						requestMonitor.setStatus(new Status(IStatus.ERROR,
								Activator.PLUGIN_ID,
								DebugException.TARGET_REQUEST_FAILED,
								"Timed out trying to launch GDB Server.", null)); //$NON-NLS-1$
						requestMonitor.done();
					}
				}
			}, fGdbServerLaunchTimeout, TimeUnit.SECONDS);
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
						getExecutor().submit(new DsfRunnable() {
							@Override
							public void run() {
								destroy();

								if (fServerMonitorJob.fMonitorExited) {
									// Now that we have destroyed the process,
									// and that the monitoring thread was
									// killed,
									// we need to set our state and send the
									// event
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

					int attempts = 0;
					while (attempts < 10) {
						try {
							// Don't know if we really need the exit value...
							// but what the heck.
							// throws exception if process not exited
							fGdbServerExitValue = fServerProcess.exitValue();

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
					requestMonitor.setStatus(new Status(IStatus.ERROR,
							Activator.PLUGIN_ID,
							IDsfStatusConstants.REQUEST_FAILED,
							"GDB Server terminate failed", null)); //$NON-NLS-1$
					requestMonitor.done();
					return Status.OK_STATUS;
				}
			}.schedule();
		}
	}

	/**
	 * Monitors a system process, waiting for it to terminate, and then notifies
	 * the associated runtime process.
	 */
	private class MonitorJob extends Job {
		boolean fMonitorExited = false;
		DsfRunnable fMonitorStarted;
		Process fMonProcess;

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			synchronized (fMonProcess) {
				getExecutor().submit(fMonitorStarted);
				try {
					fMonProcess.waitFor();
					fGdbServerExitValue = fMonProcess.exitValue();

					// Need to do this on the executor for thread-safety
					getExecutor().submit(new DsfRunnable() {
						@Override
						public void run() {
							destroy();
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

				fMonitorExited = true;
			}
			return Status.OK_STATUS;
		}

		MonitorJob(Process process, DsfRunnable monitorStarted) {
			super("GDB Server process monitor job."); //$NON-NLS-1$
			fMonProcess = process;
			fMonitorStarted = monitorStarted;
			setSystem(true);
		}

		void kill() {
			synchronized (fMonProcess) {
				if (!fMonitorExited) {
					getThread().interrupt();
				}
			}
		}
	}

	protected class MonitorJobStep extends InitializationShutdownStep {
		MonitorJobStep(Direction direction) {
			super(direction);
		}

		@Override
		public void initialize(final RequestMonitor requestMonitor) {
			fServerMonitorJob = new MonitorJob(fServerProcess, new DsfRunnable() {
				@Override
				public void run() {
					requestMonitor.done();
				}
			});
			fServerMonitorJob.schedule();
		}

		@Override
		protected void shutdown(RequestMonitor requestMonitor) {
			if (fServerMonitorJob != null) {
				fServerMonitorJob.kill();
			}
			requestMonitor.done();
		}
	}

}
