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

package ilg.gnuarmeclipse.debug.gdbjtag.dsf;

import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IGdbServerBackendService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.concurrent.Sequence.Step;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl.InitializationShutdownStep;
import org.eclipse.cdt.dsf.mi.service.IMIBackend2;
import org.eclipse.cdt.dsf.service.AbstractDsfService;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.osgi.framework.BundleContext;

public abstract class GnuArmGdbServerBackend extends AbstractDsfService
		implements IGdbServerBackendService, IMIBackend2 {

	// ------------------------------------------------------------------------

	private final ILaunchConfiguration fLaunchConfiguration;

	protected Process fServerProcess;
	protected GdbServerMonitorJob fServerMonitorJob;
	protected State fServerBackendState = State.NOT_INITIALIZED;
	protected boolean fDoStartGdbServer = true;

	private int fGdbServerExitValue = 0;

	/**
	 * Unique ID of this service instance.
	 */
	private final String fBackendId;
	private static int fgInstanceCounter = 0;

	// ------------------------------------------------------------------------

	public GnuArmGdbServerBackend(DsfSession session, ILaunchConfiguration lc) {
		super(session);
		fLaunchConfiguration = lc;
		fBackendId = "gdbServer[" + Integer.toString(fgInstanceCounter++) + "]"; //$NON-NLS-1$//$NON-NLS-2$

		System.out.println("GnuArmGdbServerBackend(" + session + ","
				+ lc.getName() + ")");
	}

	// ------------------------------------------------------------------------

	@Override
	public void initialize(final RequestMonitor rm) {

		System.out.println("GnuArmGdbServerBackend.initialize()");

		// Initialise parent, and, when ready, initialise this class.
		super.initialize(new RequestMonitor(getExecutor(), rm) {

			protected void handleSuccess() {
				doInitialize(rm);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private void doInitialize(RequestMonitor rm) {

		// Register the service, by interface and by actual name;
		// it is searched later, to shut it down or to get processes.
		register(new String[] { IGdbServerBackendService.class.getName(),
				getClass().getName() }, new Hashtable());

		// Currently not used.
		// getSession().addServiceEventListener(GnuArmGdbServerBackend.this,
		// null);

		if (fDoStartGdbServer) {

			final Sequence.Step[] initializeSteps = new Sequence.Step[] {

					new GdbServerStep(
							InitializationShutdownStep.Direction.INITIALIZING),
					new GdbServerMonitorStep(
							InitializationShutdownStep.Direction.INITIALIZING), };

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

		System.out.println("GnuArmGdbServerBackend.shutdown()");

		// Remove this service from DSF.
		unregister();
		// Currently not used.
		// getSession().removeServiceEventListener(GnuArmGdbServerBackend.this);

		// We're done here, shutdown parent.
		super.shutdown(rm);
	}

	// ------------------------------------------------------------------------

	public abstract String[] getCommandLineArray();

	public abstract int getServerLaunchTimeoutSeconds();

	public abstract String getStartingJobName();

	public abstract String getTerminatingJobName();

	// ------------------------------------------------------------------------

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getInstance().getBundle().getBundleContext();
	}

	public Process getServerProcess() {
		return fServerProcess;
	}

	@Override
	public void destroy() {
		System.out.println("GnuArmGdbServerBackend.destroy() "
				+ Thread.currentThread());

		if (fServerProcess != null && fServerBackendState == State.STARTED) {
			fServerProcess.destroy();
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public String getId() {
		return fBackendId;
	}

	@Override
	public State getState() {
		return fServerBackendState;
	}

	@Override
	public int getExitCode() {
		return fGdbServerExitValue;
	}

	@Override
	public InputStream getMIInputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public OutputStream getMIOutputStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getMIErrorStream() {
		// TODO Auto-generated method stub
		return null;
	}

	// ------------------------------------------------------------------------

	protected Process launchGDBProcess(String[] commandLineArray, File dir)
			throws CoreException {

		Process proc = null;

		System.out.println("exec " + StringUtils.join(commandLineArray, " "));
		System.out.println("dir " + dir);

		try {
			proc = ProcessFactory.getFactory().exec(commandLineArray,
					DebugUtils.getLaunchEnvironment(fLaunchConfiguration), dir);
		} catch (IOException e) {
			String message = "Error while launching command "
					+ StringUtils.join(commandLineArray, " "); //$NON-NLS-1$
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, -1, message, e));
		}

		return proc;
	}

	// ------------------------------------------------------------------------

	// Start/stop the GDB server.
	protected class GdbServerStep extends InitializationShutdownStep {

		public GdbServerStep(Direction direction) {
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

			final Job startGdbServerJob = new Job(getStartingJobName()) { //$NON-NLS-1$
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

					String[] commandLineArray = getCommandLineArray();
					if (commandLineArray == null) {
						fTmpLaunchRequestMonitor
								.setStatus(new Status(
										IStatus.ERROR,
										Activator.PLUGIN_ID,
										-1,
										"Cannot resolve macros in GDB server command line",
										null));
						fTmpLaunchRequestMonitor.done();
						return Status.OK_STATUS;
					}
					try {
						File dir = DebugUtils
								.getProjectOsDir(fLaunchConfiguration);

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
			}, getServerLaunchTimeoutSeconds(), TimeUnit.SECONDS);

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

			new Job(getTerminatingJobName()) { //$NON-NLS-1$
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
	protected class GdbServerMonitorJob extends Job {

		boolean fMonitorExited = false;
		DsfRunnable fMonitorStarted;
		Process fProcess;

		public GdbServerMonitorJob(Process process, DsfRunnable monitorStarted) {
			super("GDB Server process monitor job."); //$NON-NLS-1$
			fProcess = process;
			fMonitorStarted = monitorStarted;
			setSystem(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			synchronized (fProcess) {
				System.out.println("GdbServerMonitorJob.run() submit "
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
									.println("GdbServerMonitorJob.run() run() thread "
											+ getThread());
							destroy();
							System.out
									.println("GdbServerMonitorJob.run() run() State.TERMINATED");
							fServerBackendState = State.TERMINATED;
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
						.println("GdbServerMonitorJob.run() fMonitorExited = true thread "
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
								.println("GdbServerMonitorJob.kill() interrupt "
										+ thread.toString());
						thread.interrupt();
					} else {
						System.out.println("null thread");
					}
				}
			}
		}
	}

	protected class GdbServerMonitorStep extends InitializationShutdownStep {

		public GdbServerMonitorStep(Direction direction) {
			super(direction);
		}

		@Override
		public void initialize(final RequestMonitor requestMonitor) {

			fServerMonitorJob = new GdbServerMonitorJob(fServerProcess,
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

			System.out.println("GdbServerMonitorStep.shutdown()");
			if (fServerMonitorJob != null) {
				fServerMonitorJob.kill();
			}
			requestMonitor.done();
			System.out.println("GdbServerMonitorStep.shutdown() return");
		}
	}

	// ------------------------------------------------------------------------
}
