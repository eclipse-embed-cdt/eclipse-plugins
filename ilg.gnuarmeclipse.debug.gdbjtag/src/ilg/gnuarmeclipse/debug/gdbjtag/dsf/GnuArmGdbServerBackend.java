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

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IGdbServerBackendService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl.InitializationShutdownStep;
import org.eclipse.cdt.dsf.mi.service.IMIBackend.BackendStateChangedEvent;
import org.eclipse.cdt.dsf.mi.service.IMIBackend.State;
import org.eclipse.cdt.dsf.mi.service.command.events.MIStoppedEvent;
import org.eclipse.cdt.dsf.service.AbstractDsfService;
import org.eclipse.cdt.dsf.service.DsfServiceEventHandler;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.osgi.framework.BundleContext;

/**
 * This abstract class handles the start/stop of the GDB server. Based on the
 * configuration, it may or it may not actually start the server.
 * 
 * It should be used as base class for an implementation specific GDB server
 * backend.
 */
public abstract class GnuArmGdbServerBackend extends AbstractDsfService implements IGdbServerBackendService {

	// ------------------------------------------------------------------------

	// public static final int ERROR_CHARBUFFER_SIZE = 1024;

	// Allow derived classes to use these variables.

	protected final ILaunchConfiguration fLaunchConfiguration;

	// May be set to false by derived classes.
	protected boolean fDoStartGdbServer = true;

	protected Process fServerProcess;
	protected PushBackProcess fServerPipedProcess;
	protected GdbServerMonitorJob fServerMonitorJob;

	// For synchronisation reasons, set/check this only on the DSF thread.
	protected State fServerBackendState = State.NOT_INITIALIZED;

	private int fGdbServerExitValue = 0;
	protected IStatus fGdbServerExitStatus = null;

	// private StringBuffer fErrorStreamBuffer;
	// private CaptureErrorStreamHandler fCaptureHandler;

	private ScheduledFuture<?> fTimeoutFuture = null;

	private Job fStartGdbServerJob;

	/**
	 * Monotone increasing Unique ID of this service instance.
	 */
	private final String fBackendId;
	private static int fgInstanceCounter = 0;

	// ------------------------------------------------------------------------

	public GnuArmGdbServerBackend(DsfSession session, ILaunchConfiguration lc) {
		super(session);
		fLaunchConfiguration = lc;
		fBackendId = "gdbServer[" + Integer.toString(fgInstanceCounter++) + "]"; //$NON-NLS-1$//$NON-NLS-2$

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmGdbServerBackend(" + session + "," + lc.getName() + ")");
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public void initialize(final RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmGdbServerBackend.initialize()");
		}

		// Initialise parent, and, when ready, initialise this class too.
		super.initialize(new RequestMonitor(getExecutor(), rm) {

			protected void handleSuccess() {
				doInitialize(rm);
			}
		});
	}

	private void doInitialize(RequestMonitor rm) {

		final Sequence.Step[] initializeSteps;
		if (fDoStartGdbServer) {
			initializeSteps = new Sequence.Step[] { new RegisterStep(InitializationShutdownStep.Direction.INITIALIZING),
					new GdbServerStep(InitializationShutdownStep.Direction.INITIALIZING),
					new GdbServerMonitorStep(InitializationShutdownStep.Direction.INITIALIZING), };
		} else {
			// The RegisterStep is needed anyway.
			initializeSteps = new Sequence.Step[] {
					new RegisterStep(InitializationShutdownStep.Direction.INITIALIZING), };
		}
		Sequence startupSequence = new Sequence(getExecutor(), rm) {
			@Override
			public Step[] getSteps() {
				return initializeSteps;
			}
		};
		getExecutor().execute(startupSequence);
	}

	@Override
	public void shutdown(final RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmGdbServerBackend.shutdown()");
		}

		final Sequence.Step[] shutdownSteps;
		if (fDoStartGdbServer) {
			shutdownSteps = new Sequence.Step[] {
					new GdbServerMonitorStep(InitializationShutdownStep.Direction.SHUTTING_DOWN),
					new GdbServerStep(InitializationShutdownStep.Direction.SHUTTING_DOWN),
					new RegisterStep(InitializationShutdownStep.Direction.SHUTTING_DOWN), };
		} else {
			// The RegisterStep is needed anyway.
			shutdownSteps = new Sequence.Step[] {
					new RegisterStep(InitializationShutdownStep.Direction.SHUTTING_DOWN), };
		}
		Sequence startupSequence = new Sequence(getExecutor(), new ImmediateRequestMonitor(rm) {
			@Override
			protected void handleSuccess() {
				// We're done here, shutdown parent.
				GnuArmGdbServerBackend.super.shutdown(rm);
			}
		}) {
			@Override
			public Step[] getSteps() {
				return shutdownSteps;
			}
		};
		getExecutor().execute(startupSequence);
	}

	/**
	 * Override this in the derived class and call super.destroy().
	 */
	@Override
	public void destroy() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmGdbServerBackend.destroy() " + Thread.currentThread());
		}
		if (fStartGdbServerJob != null && fStartGdbServerJob.getThread() != null) {
			// Try to terminate read loop.
			fStartGdbServerJob.getThread().interrupt();
		}

		if (getServerProcess() != null && getServerState() == State.STARTED) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("GnuArmGdbServerBackend.destroy() fServerProcess " + fServerProcess);
			}
			getServerProcess().destroy();
		} else if (fServerProcess != null) {
			fServerProcess.destroy();
		}

		if (fTimeoutFuture != null) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("GnuArmGdbServerBackend.destroy() cancel timeout");
			}
			fTimeoutFuture.cancel(true);
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * A lot of MIStoppedEvent debug events come here. (like breakpoint hit).
	 * 
	 * Currently not used.
	 * 
	 * @param e
	 */
	@DsfServiceEventHandler
	public void eventDispatched(final MIStoppedEvent e) {

		// System.out.println("GnuArmGdbServerBackend.eventDispatched() " + e);
	}

	/**
	 * Safety net in case the server is not configured to terminate on closing
	 * the client connection.
	 * 
	 * The event is created and fired in the backend monitor, when the client is
	 * confirmed dead.
	 * 
	 * @param e
	 */
	@DsfServiceEventHandler
	public void eventDispatched(final BackendStateChangedEvent e) {
		// Only BackendStateChangedEvent debug events come here.
		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmGdbServerBackend.eventDispatched() " + e);
		}

		if (fDoStartGdbServer) {
			if (e.getState() == State.TERMINATED && e.getSessionId().equals(getSession().getId())
					&& getServerState() == State.STARTED) {

				destroy();
			}
		} else {
			if (Activator.getInstance().isDebugging()) {
				System.out.println(
						"GnuArmGdbServerBackend.eventDispatched() -> dispatchEvent(ServerBackendStateChangedEvent, TERMINATED)");
			}
			getSession().dispatchEvent(
					new ServerBackendStateChangedEvent(getSession().getId(), getId(), State.TERMINATED),
					getProperties());
		}
	}

	// ------------------------------------------------------------------------

	@Override
	protected abstract BundleContext getBundleContext();

	public abstract String[] getServerCommandLineArray();

	public abstract int getServerLaunchTimeoutSeconds();

	public abstract String getServerName();

	public abstract String prepareMessageBoxText(int exitCode);

	public boolean canMatchStdOut() {
		return true;
	}

	public boolean matchStdOutExpectedPattern(String line) {
		return false;
	}

	public boolean canMatchStdErr() {
		return false;
	}

	public boolean matchStdErrExpectedPattern(String line) {
		return false;
	}

	public String getStartingServerJobName() {
		return "Starting " + getServerName();
	}

	public String getTerminatingServerJobName() {
		return "Terminating " + getServerName();
	}

	public String getMonitorServerJobName() {
		return getServerName() + " Monitor";
	}

	// ------------------------------------------------------------------------

	public Process getServerProcess() {
		return fServerPipedProcess;
	}

	public State getServerState() {
		return fServerBackendState;
	}

	public int getServerExitCode() {
		return fGdbServerExitValue;
	}

	public IStatus getServerExitStatus() {
		return fGdbServerExitStatus;
	}

	@Override
	public String getId() {
		return fBackendId;
	}

	// ------------------------------------------------------------------------

	protected Process launchGdbServerProcess(String[] commandLineArray) throws CoreException {

		Process proc = null;

		File dir = null;
		IPath path = DebugUtils.getGdbWorkingDirectory(fLaunchConfiguration);
		if (path != null) {
			dir = new File(path.toOSString());
		}

		proc = DebugUtils.exec(commandLineArray, DebugUtils.getLaunchEnvironment(fLaunchConfiguration), dir);
		return proc;
	}

	/**
	 * A best effort to determine if the GDB server started properly.
	 * 
	 * @param serverLaunchRequestMonitor
	 * @param monitor
	 * @param outBuffer
	 * @param errBuffer
	 * @return true if server started.
	 */
	protected boolean checkServer(RequestMonitor serverLaunchRequestMonitor, IProgressMonitor monitor,
			StringBuffer outBuffer, StringBuffer errBuffer) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmGdbServerBackend.checkServer()");
		}

		// The strategy is to parse the output stream and stop
		// when a certain pattern is matched.
		boolean success = false;

		if (canMatchStdOut()) {

			InputStream inputStream = null;
			try {

				inputStream = fServerProcess.getInputStream();
				byte b[] = new byte[1024];

				// Awfully inefficient
				int count;

				// The input stream will block until the process ends.
				while ((count = inputStream.read(b, 0, b.length)) != -1) {

					String str = new String(b, 0, count, "ascii");
					if (Activator.getInstance().isDebugging()) {
						System.out.print(str);
					}
					outBuffer.append(new String(b, 0, count, "ascii"));
					if (matchStdOutExpectedPattern(outBuffer.toString())) {
						success = true;
						break;
					}

					if (serverLaunchRequestMonitor.isCanceled() || monitor.isCanceled()) {

						if (Activator.getInstance().isDebugging()) {
							System.out.println("startGdbServerJob run canceled read");
						}
						serverLaunchRequestMonitor.setStatus(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, -1,
								getStartingServerJobName() + " cancelled.", null)); //$NON-NLS-1$
						serverLaunchRequestMonitor.done();
						// return Status.OK_STATUS;
						return false;
					}
				}

				if (success) {

					// The expected pattern was identified, the server
					// is ready so we can proceed to the next step.
					try {
						getExecutor().submit(new DsfRunnable() {
							@Override
							public void run() {
								if (Activator.getInstance().isDebugging()) {
									System.out.println("startGdbServerJob run() State.STARTED");
								}
								// Need to do this on the executor for
								// thread-safety
								fServerBackendState = State.STARTED;

								// The launcher will wait for this.
								if (fGdbServerExitStatus == null) {
									fGdbServerExitStatus = Status.OK_STATUS;
								}
							}
						}).get(); // Wait for it to complete.
					} catch (InterruptedException e) {
						;
					} catch (ExecutionException e) {
						Activator.log(e);
					}

				} else {

					// Failure means the sever exited with error,
					// otherwise the server would be still reading.
					if (Activator.getInstance().isDebugging()) {
						System.out.println("startGdbServerJob run() EOF stdout");
					}

					// try {
					// No need to preserve the capture thread, it is
					// already terminated.
					// fCaptureHandler.join();

					// Add errors to the end of the output buffer.
					// outBuffer.append(fErrorStreamBuffer);

					// } catch (InterruptedException e) {
					// ;
					// }

					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							Activator.log(e);
						}
					}
				}

			} catch (IOException e) {
				success = false;
				serverLaunchRequestMonitor.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
						"Error reading " + getServerName() + " stdout", e)); //$NON-NLS-1$
			}
		}

		if (canMatchStdErr()) {

			InputStream errorStream = null;
			try {

				errorStream = fServerProcess.getErrorStream();
				byte b[] = new byte[1024];

				// Awfully inefficient
				int count;

				// The input stream will block until the process ends.
				while ((count = errorStream.read(b, 0, b.length)) != -1) {

					String str = new String(b, 0, count, "ascii");
					if (Activator.getInstance().isDebugging()) {
						System.out.print(str);
					}
					errBuffer.append(new String(b, 0, count, "ascii"));
					if (matchStdErrExpectedPattern(errBuffer.toString())) {
						success = true;
						break;
					}

					if (serverLaunchRequestMonitor.isCanceled() || monitor.isCanceled()) {

						if (Activator.getInstance().isDebugging()) {
							System.out.println("startGdbServerJob run canceled read");
						}
						serverLaunchRequestMonitor.setStatus(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, -1,
								getStartingServerJobName() + " cancelled.", null)); //$NON-NLS-1$
						serverLaunchRequestMonitor.done();
						// return Status.OK_STATUS;
						return false;
					}
				}

				if (success) {

					// The expected pattern was identified, the server
					// is ready so we can proceed to the next step.
					try {
						getExecutor().submit(new DsfRunnable() {
							@Override
							public void run() {
								if (Activator.getInstance().isDebugging()) {
									System.out.println("startGdbServerJob run() State.STARTED");
								}
								// Need to do this on the executor for
								// thread-safety
								fServerBackendState = State.STARTED;

								// The launcher will wait for this.
								if (fGdbServerExitStatus == null) {
									fGdbServerExitStatus = Status.OK_STATUS;
								}
							}
						}).get(); // Wait for it to complete.
					} catch (InterruptedException e) {
						;
					} catch (ExecutionException e) {
						Activator.log(e);
					}

				} else {

					// Failure means the sever exited with error,
					// otherwise the server would be still reading.
					if (Activator.getInstance().isDebugging()) {
						System.out.println("startGdbServerJob run() EOF stderr");
					}

					// try {
					// No need to preserve the capture thread, it is
					// already terminated.
					// fCaptureHandler.join();

					// Add errors to the end of the output buffer.
					// outBuffer.append(fErrorStreamBuffer);

					// } catch (InterruptedException e) {
					// ;
					// }

					if (errorStream != null) {
						try {
							errorStream.close();
						} catch (IOException e) {
							Activator.log(e);
						}
					}
				}

			} catch (IOException e) {
				success = false;
				serverLaunchRequestMonitor.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
						"Error reading " + getServerName() + " stderr", e)); //$NON-NLS-1$
			}
		}

		return success;
	}

	// ========================================================================

	protected class RegisterStep extends InitializationShutdownStep {

		RegisterStep(Direction direction) {
			super(direction);
		}

		@Override
		public void initialize(final RequestMonitor rm) {

			// Register the service, by interface and by actual name;
			// it is searched later, to shut it down or to get processes.
			if (Activator.getInstance().isDebugging()) {
				System.out.println("register " + GnuArmGdbServerBackend.this.getClass().getName());
			}
			register(new String[] { IGdbServerBackendService.class.getName(),
					GnuArmGdbServerBackend.this.getClass().getName() }, new Hashtable<String, String>());

			// Register listener.
			getSession().addServiceEventListener(GnuArmGdbServerBackend.this, null);

			// Notify world that server backend started.
			getSession().dispatchEvent(new ServerBackendStateChangedEvent(getSession().getId(), getId(), State.STARTED),
					getProperties());

			rm.done();
		}

		@Override
		protected void shutdown(RequestMonitor rm) {

			// Remove this service from DSF.
			unregister();

			// Unregister listener.
			getSession().removeServiceEventListener(GnuArmGdbServerBackend.this);

			rm.done();
		}
	}

	// ========================================================================

	/**
	 * A custom process, with push back streams, in case the streams were
	 * already parsed in search of a pattern to confirm the server is ready.
	 */
	protected class PushBackProcess extends Process {

		private Process fProcess;

		private PushbackInputStream fInput;
		private PushbackInputStream fError;

		public PushBackProcess(Process process, StringBuffer outBuffer, StringBuffer errBuffer) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("PushBackProcess(" + process + ")");
			}
			fProcess = process;

			byte[] b;

			if (outBuffer != null && outBuffer.length() > 0) {
				fInput = new PushbackInputStream(fProcess.getInputStream(), outBuffer.length() + 1);
				b = outBuffer.toString().getBytes();
				try {
					fInput.unread(b);
				} catch (IOException e) {
					Activator.log(e);
				}
			}

			if (errBuffer != null && errBuffer.length() > 0) {
				fError = new PushbackInputStream(fProcess.getErrorStream(), errBuffer.length() + 1);
				b = errBuffer.toString().getBytes();
				try {
					fError.unread(b);
				} catch (IOException e) {
					Activator.log(e);
				}
			}
		}

		@Override
		public void destroy() {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("PushBackProcess.destroy()");
			}
			// fInput, fError are closed by parent, do not close them here.

			fProcess.destroy();
		}

		@Override
		public int exitValue() {
			int intExit = fProcess.exitValue();
			if (EclipseUtils.isMacOSX() || EclipseUtils.isLinux()) {
				// On these platforms we know that the exit code is a
				// byte value.
				byte byteExit = (byte) intExit;
				intExit = byteExit;
			}
			return intExit;
		}

		@Override
		public InputStream getErrorStream() {
			if (fError != null) {
				return fError;
			} else {
				return fProcess.getErrorStream();
			}
		}

		@Override
		public InputStream getInputStream() {
			if (fInput != null) {
				return fInput;
			} else {
				return fProcess.getInputStream();
			}
		}

		@Override
		public OutputStream getOutputStream() {
			return fProcess.getOutputStream();
		}

		@Override
		public int waitFor() throws InterruptedException {
			return fProcess.waitFor();
		}

	}

	// ========================================================================

	// Start/stop the GDB server.
	protected class GdbServerStep extends InitializationShutdownStep {

		public GdbServerStep(Direction direction) {
			super(direction);
		}

		/**
		 * The initialise is completed either normally, or with timeout,
		 * notified directly by the timeout job.
		 * 
		 */
		@Override
		public void initialize(final RequestMonitor rm) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("GdbServerStep.initialise()");
			}

			class ServerLaunchMonitor {
				boolean fIsTimeoutEnabled = true;
				boolean fWasTimeout = false;
			}

			final ServerLaunchMonitor fServerLaunchMonitor = new ServerLaunchMonitor();

			// This new request monitor is used by the job defined below.
			final RequestMonitor serverLaunchRequestMonitor = new RequestMonitor(getExecutor(), rm) {

				@Override
				protected void handleCompleted() {
					if (Activator.getInstance().isDebugging()) {
						System.out.println("GdbServerStep.initialise() handleCompleted()");
					}

					// Timeouts are notified directly by the timeout job.
					if (!fServerLaunchMonitor.fWasTimeout) {

						// Might not be entirely true, but this actually means
						// to disable the timeout.
						fServerLaunchMonitor.fIsTimeoutEnabled = false;
						if (!isSuccess()) {
							rm.setStatus(getStatus());
						}
						// Notify the initialise(rm) to proceed to next step.
						rm.done();
					}
				}
			};

			// This job should notify serverLaunchRequestMonitor when done.
			final Job startGdbServerJob = new Job(getStartingServerJobName()) { // $NON-NLS-1$
				{
					setSystem(true);
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {

					if (Activator.getInstance().isDebugging()) {
						System.out.println("GdbServerStep.initialise() Job run()");
					}
					if (serverLaunchRequestMonitor.isCanceled() || monitor.isCanceled()) {
						if (Activator.getInstance().isDebugging()) {
							System.out.println("startGdbServerJob run canceled");
						}
						serverLaunchRequestMonitor.setStatus(new Status(IStatus.CANCEL, Activator.PLUGIN_ID, -1,
								getStartingServerJobName() + " cancelled.", null)); //$NON-NLS-1$
						serverLaunchRequestMonitor.done();
						return Status.OK_STATUS;
					}

					String[] commandLineArray = getServerCommandLineArray();
					if (commandLineArray == null) {
						serverLaunchRequestMonitor.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,

								getStartingServerJobName() + " failed, cannot get commnd line.", null));
						serverLaunchRequestMonitor.done();
						return Status.OK_STATUS;
					}
					try {
						fServerProcess = launchGdbServerProcess(commandLineArray);
					} catch (CoreException e) {

						// The process failed to start.
						serverLaunchRequestMonitor
								.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, e.getMessage(), e));
						serverLaunchRequestMonitor.done();
						return Status.OK_STATUS;
					}

					boolean success = false;

					StringBuffer outBuffer = new StringBuffer();
					StringBuffer errBuffer = new StringBuffer();

					// Check if the server started properly

					success = checkServer(serverLaunchRequestMonitor, monitor, outBuffer, errBuffer);
					if (success || serverLaunchRequestMonitor.isSuccess())
						// Create a wrapper for the original process, to have
						// some
						// control over the I/O stream.
						fServerPipedProcess = new PushBackProcess(fServerProcess, outBuffer, errBuffer);

					// This monitor will further complete the initialise().
					if (!serverLaunchRequestMonitor.isCanceled()) {
						serverLaunchRequestMonitor.done();
					}
					if (Activator.getInstance().isDebugging()) {
						System.out.println("startGdbServerJob run completed");
					}
					return Status.OK_STATUS;
				}
			};

			fStartGdbServerJob = startGdbServerJob;

			// The job will run on a separate thread, usually immediately as
			// initialise() returns.
			startGdbServerJob.schedule();

			if (Activator.getInstance().isDebugging()) {
				System.out.println("GdbServerStep.initialise() after job schedule");
			}

			// Register a timeout task, that should kill everything if the
			// server
			// did not start.
			fTimeoutFuture = getExecutor().schedule(new Runnable() {

				@Override
				public void run() {

					if (Activator.getInstance().isDebugging()) {
						System.out.println("GdbServerStep.initialise() timeout run()");
					}

					if (fServerLaunchMonitor.fIsTimeoutEnabled) {

						// If not yet launched, the start job probably hanged,
						// and there
						// isn't much we can do to save it; interrupt thread and
						// destroy.

						fServerLaunchMonitor.fWasTimeout = true;

						Thread jobThread = startGdbServerJob.getThread();
						if (jobThread != null) {
							if (Activator.getInstance().isDebugging()) {
								System.out.println("GdbServerStep.initialise() timeout interrupt thread " + jobThread);
							}
							// Interrupt thread, in case it was blocked in a
							// read.
							jobThread.interrupt();
						}

						// Destroy the GDB server process.
						if (getServerProcess() != null) {
							getServerProcess().destroy();
						} else if (fServerProcess != null) {
							fServerProcess.destroy();
						}

						// Notify initialise(rm) directly.
						rm.setStatus(
								new Status(IStatus.ERROR, Activator.PLUGIN_ID, DebugException.TARGET_REQUEST_FAILED,
										getStartingServerJobName() + " timed out.", null)); //$NON-NLS-1$
						rm.done();
					}
				}
			}, getServerLaunchTimeoutSeconds(), TimeUnit.SECONDS);

			if (Activator.getInstance().isDebugging()) {
				System.out.println("GdbServerStep.initialise() return");
			}
		}

		@Override
		protected void shutdown(final RequestMonitor rm) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("GdbServerStep.shutdown()");
			}

			if (fServerBackendState == State.TERMINATED) {
				// Already killed, don't bother starting
				// another job to kill it.
				rm.done();
				return;
			}

			new Job(getTerminatingServerJobName()) { // $NON-NLS-1$
				{
					setSystem(true);
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					try {
						// Need to do this on the executor for thread-safety
						// And we should wait for it to complete since we then
						// check if the killing of GDB worked.
						if (Activator.getInstance().isDebugging()) {
							System.out.println("GdbServerStep.shutdown() run()");
						}
						getExecutor().submit(new DsfRunnable() {
							@Override
							public void run() {
								if (Activator.getInstance().isDebugging()) {
									System.out.println("GdbServerStep.shutdown() run() run()");
								}
								destroy();

								if (fServerMonitorJob != null && fServerMonitorJob.fMonitorExited) {
									// Now that we have destroyed the process,
									// and that the monitoring thread was
									// killed,
									// we need to set our state and send the
									// event
									if (Activator.getInstance().isDebugging()) {
										System.out.println("GdbServerStep shutdown() run() run() State.TERMINATED");
									}
									fServerBackendState = State.TERMINATED;

									// Notify world that server backend
									// terminated.
									if (Activator.getInstance().isDebugging()) {
										System.out.println(
												"GdbServerStep.shutdown() run() dispatchEvent(ServerBackendStateChangedEvent, TERMINATED)");
									}
									getSession().dispatchEvent(new ServerBackendStateChangedEvent(getSession().getId(),
											getId(), State.TERMINATED), getProperties());
								}
							}
						}).get();
					} catch (InterruptedException e) {
						;
					} catch (ExecutionException e) {
						;
					}

					if (Activator.getInstance().isDebugging()) {
						System.out.println("GdbServerStep.shutdown() run() before getting exitValue");
					}
					int attempts = 0;
					while (attempts < 10) {
						try {
							// Don't know if we really need the exit value...
							// but what the heck.
							// throws exception if process not exited
							fGdbServerExitValue = fServerProcess.exitValue();

							if (Activator.getInstance().isDebugging()) {
								System.out.println("GdbServerStep shutdown() run() return");
							}
							rm.done();
							return Status.OK_STATUS;
						} catch (IllegalThreadStateException ie) {
						}
						try {
							Thread.sleep(500); // 0.5s
						} catch (InterruptedException e) {
						}
						attempts++;
					}
					if (Activator.getInstance().isDebugging()) {
						System.out.println("GdbServerStep.shutdown() run() REQUEST_FAILED");
					}
					rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IDsfStatusConstants.REQUEST_FAILED,
							getTerminatingServerJobName() + " failed.", null)); //$NON-NLS-1$
					rm.done();
					return Status.OK_STATUS;
				}
			}.schedule();
			if (Activator.getInstance().isDebugging()) {
				System.out.println("GdbServerStep.shutdown() return");
			}
		}
	}

	// ========================================================================

	/**
	 * A dedicated job to monitor a system process, waiting for it to terminate,
	 * and then destroy the entire ServerBackend (destroy is from derived
	 * class).
	 */
	protected class GdbServerMonitorJob extends Job {

		boolean fMonitorExited = false;
		DsfRunnable fMonitorStarted;
		Process fProcess;

		public GdbServerMonitorJob(Process process, DsfRunnable monitorStarted) {
			super(getMonitorServerJobName()); // $NON-NLS-1$
			fProcess = process;
			fMonitorStarted = monitorStarted;
			setSystem(true);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {

			IStatus status = Status.OK_STATUS;

			synchronized (fProcess) {
				if (Activator.getInstance().isDebugging()) {
					System.out
							.println("GdbServerMonitorJob.run() submit " + fMonitorStarted + " thread " + getThread());
				}

				try {
					boolean mustNotify = false;
					try {
						// If the process is already dead, do whatever is needed
						// for burial and delay notifying the parent, to prevent
						// additional useless steps.
						fGdbServerExitValue = fProcess.exitValue();
						mustNotify = true;
					} catch (IllegalThreadStateException e) {
						// Notify the runnable that the job started.
						getExecutor().submit(fMonitorStarted);
						fProcess.waitFor();
						fGdbServerExitValue = fProcess.exitValue();
					}

					if (Activator.getInstance().isDebugging()) {
						System.out.println("GdbServerMonitorJob.run() exitValue() " + fGdbServerExitValue);
					}

					if (fGdbServerExitValue != 0) {

						final String message = prepareMessageBoxText(fGdbServerExitValue);
						try {
							getExecutor().submit(new DsfRunnable() {
								@Override
								public void run() {
									if (Activator.getInstance().isDebugging()) {
										System.out.println("GdbServerMonitorJob.run() failed");
									}
									// Need to do this on the executor for
									// thread-safety

									// The launcher will wait for this.
									fGdbServerExitStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
								}
							}).get(); // Wait for it to complete.
						} catch (ExecutionException e) {
							Activator.log(e);
						}
					}

					getExecutor().submit(new DsfRunnable() {
						@Override
						public void run() {
							if (Activator.getInstance().isDebugging()) {
								System.out.println("GdbServerMonitorJob.run() run() thread " + getThread());
							}

							// Need to do this on the executor for thread-safety

							// Destroy the process
							fProcess.destroy();

							// Destroy the derived ServerBackend
							destroy();
							if (Activator.getInstance().isDebugging()) {
								System.out.println("GdbServerMonitorJob.run() run() State.TERMINATED");
							}

							if (fGdbServerExitStatus == null) {
								fGdbServerExitStatus = new Status(IStatus.OK, Activator.PLUGIN_ID, "TERMINATED"); //$NON-NLS-1$
							}
							fServerBackendState = State.TERMINATED;

							// Notify world that server backend terminated.
							if (Activator.getInstance().isDebugging()) {
								System.out.println(
										"GdbServerMonitorJob.run() run() dispatchEvent(ServerBackendStateChangedEvent, TERMINATED)");
							}
							getSession().dispatchEvent(
									new ServerBackendStateChangedEvent(getSession().getId(), getId(), State.TERMINATED),
									getProperties());
						}
					});

					if (mustNotify) {
						getExecutor().submit(fMonitorStarted);
					}

				} catch (InterruptedException ie) {
					// clear interrupted state
					Thread.interrupted();
				}

				if (Activator.getInstance().isDebugging()) {
					System.out.println("GdbServerMonitorJob.run() fMonitorExited = true thread " + getThread());
				}
				fMonitorExited = true;
			}
			return status;
		}

		void kill() {
			synchronized (fProcess) {
				if (!fMonitorExited) {
					Thread thread = getThread();
					if (thread != null) {
						if (Activator.getInstance().isDebugging()) {
							System.out.println("GdbServerMonitorJob.kill() interrupt " + thread.toString());
						}
						thread.interrupt();
					} else {
						Activator.log("GdbServerMonitorJob.kill() null thread");
					}
				}
			}
		}

	}

	// ========================================================================

	/**
	 * Start/stop the GdbServerMonitorJob.
	 */
	protected class GdbServerMonitorStep extends InitializationShutdownStep {

		public GdbServerMonitorStep(Direction direction) {
			super(direction);
		}

		@Override
		public void initialize(final RequestMonitor rm) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("GdbServerMonitorStep.initialize()");
			}

			// The request monitor is notified when the new job is started.
			fServerMonitorJob = new GdbServerMonitorJob(getServerProcess(), new DsfRunnable() {
				@Override
				public void run() {
					rm.done();
				}
			});

			// The monitor job will run on a separate thread.
			fServerMonitorJob.schedule();
		}

		@Override
		protected void shutdown(RequestMonitor rm) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("GdbServerMonitorStep.shutdown()");
			}
			if (fServerMonitorJob != null) {
				fServerMonitorJob.kill();
			}
			rm.done();
			if (Activator.getInstance().isDebugging()) {
				System.out.println("GdbServerMonitorStep.shutdown() done");
			}
		}
	}

	// ------------------------------------------------------------------------
}
