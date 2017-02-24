/*******************************************************************************
 * Copyright (c) 2006, 2012 Wind River Systems, Nokia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Nokia              - initial API and implementation with some code moved from GDBControl.
 *     Wind River System
 *     Ericsson
 *     Marc Khouzam (Ericsson) - Use the new IMIBackend2 interface (Bug 350837)
 *     Mark Bozeman (Mentor Graphics) - Report GDB start failures (Bug 376203)
 *     Iulia Vasii (Freescale Semiconductor) - Separate GDB command from its arguments (Bug 445360)
 *******************************************************************************/

/* ----------------------------------------------------------------------------
 *
 * Copied here because we need the new version, but still remain compatible 
 * with Kepler. When dependency to Kepler will be removed, this file will 
 * no longer be necessary.
 * 
 * It is identical to the newer GDBBackend, with the following changes:
 * - package ilg.gnuarmeclipse.debug.gdbjtag.jlink.dsf;
 * - some imports
 * - @SuppressWarnings("restriction")
 *
 * ------------------------------------------------------------------------- */

package ilg.gnuarmeclipse.debug.gdbjtag.dsf;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.parser.util.StringUtil;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGdbDebugPreferenceConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.launching.LaunchUtils;
import org.eclipse.cdt.dsf.gdb.service.IGDBBackend;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl.InitializationShutdownStep;
import org.eclipse.cdt.dsf.mi.service.IMIBackend;
import org.eclipse.cdt.dsf.mi.service.IMIBackend2;
import org.eclipse.cdt.dsf.mi.service.command.events.MIStoppedEvent;
import org.eclipse.cdt.dsf.service.AbstractDsfService;
import org.eclipse.cdt.dsf.service.DsfServiceEventHandler;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.utils.spawner.Spawner;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.osgi.framework.BundleContext;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmProcesses_7_2_1.ProcessStateChangedEvent;

/**
 * Implementation of {@link IGDBBackend} for the common case where GDB is
 * launched in local file system on host PC where Eclipse runs. This also
 * manages some GDB parameters from a given launch configuration.<br>
 * <br>
 * You can subclass for you special needs.
 * 
 * @since 1.1
 */
@SuppressWarnings("restriction")
public class GnuArmGdbBackend extends AbstractDsfService implements IGDBBackend, IMIBackend2 {

	private final ILaunchConfiguration fLaunchConfiguration;

	/*
	 * Parameters for launching GDB.
	 */
	private IPath fProgramPath;
	private IPath fGDBWorkingDirectory;
	private String fGDBInitFile;
	private List<String> fSharedLibPaths;
	private String fProgramArguments;

	private Properties fEnvVariables;
	private SessionType fSessionType;
	private Boolean fAttach;
	private State fBackendState = State.NOT_INITIALIZED;

	/**
	 * Unique ID of this service instance.
	 */
	private final String fBackendId;
	private static int fgInstanceCounter = 0;

	/*
	 * Service state parameters.
	 */
	private MonitorJob fMonitorJob;
	private Process fProcess;
	private int fGDBExitValue;
	private int fGDBLaunchTimeout = 30;

	/**
	 * A Job that will set a failed status in the proper request monitor, if the
	 * interrupt did not succeed after a certain time.
	 */
	private MonitorInterruptJob fInterruptFailedJob;

	public GnuArmGdbBackend(DsfSession session, ILaunchConfiguration lc) {
		super(session);
		fBackendId = "gdb[" + Integer.toString(fgInstanceCounter++) + "]"; //$NON-NLS-1$//$NON-NLS-2$
		fLaunchConfiguration = lc;

		try {
			// Don't call verifyCProject, because the JUnit tests are not
			// setting a project
			ICProject cproject = LaunchUtils.getCProject(lc);
			fProgramPath = LaunchUtils.verifyProgramPath(lc, cproject);
		} catch (CoreException e) {
			fProgramPath = new Path(""); //$NON-NLS-1$
		}
	}

	@Override
	public void initialize(final RequestMonitor requestMonitor) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmGdbBackend.initialize()");
		}

		super.initialize(new ImmediateRequestMonitor(requestMonitor) {
			@Override
			protected void handleSuccess() {
				doInitialize(requestMonitor);
			}
		});
	}

	private void doInitialize(final RequestMonitor requestMonitor) {

		final Sequence.Step[] initializeSteps = new Sequence.Step[] {
				new GDBProcessStep(InitializationShutdownStep.Direction.INITIALIZING),
				new MonitorJobStep(InitializationShutdownStep.Direction.INITIALIZING),
				new RegisterStep(InitializationShutdownStep.Direction.INITIALIZING), };

		Sequence startupSequence = new Sequence(getExecutor(), requestMonitor) {
			@Override
			public Step[] getSteps() {
				return initializeSteps;
			}
		};
		getExecutor().execute(startupSequence);
	}

	@Override
	public void shutdown(final RequestMonitor requestMonitor) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmGdbBackend.shutdown()");
		}

		final Sequence.Step[] shutdownSteps = new Sequence.Step[] {
				new RegisterStep(InitializationShutdownStep.Direction.SHUTTING_DOWN),
				new MonitorJobStep(InitializationShutdownStep.Direction.SHUTTING_DOWN),
				new GDBProcessStep(InitializationShutdownStep.Direction.SHUTTING_DOWN), };
		Sequence shutdownSequence = new Sequence(getExecutor(), new RequestMonitor(getExecutor(), requestMonitor) {
			@Override
			protected void handleCompleted() {
				GnuArmGdbBackend.super.shutdown(requestMonitor);
			}
		}) {
			@Override
			public Step[] getSteps() {
				return shutdownSteps;
			}
		};
		getExecutor().execute(shutdownSequence);
	}

	/** @since 4.0 */
	protected IPath getGDBPath() {
		return LaunchUtils.getGDBPath(fLaunchConfiguration);
	}

	/**
	 * Options for GDB process. Allow subclass to override.
	 * 
	 * @deprecated Use {@link #getGDBCommandLineArray()} instead
	 */
	@Deprecated
	protected String getGDBCommandLine() {
		String cmdArray[] = getGDBCommandLineArray();
		return StringUtil.join(cmdArray, " "); //$NON-NLS-1$
	}

	/**
	 * Options for GDB process. Returns the GDB command and its arguments as an
	 * array. Allow subclass to override.
	 * 
	 * @since 4.6
	 */
	protected String[] getGDBCommandLineArray() {
		// The goal here is to keep options to an absolute minimum.
		// All configuration should be done in the final launch sequence
		// to allow for more flexibility.
		return new String[] { getGDBPath().toOSString(), // This could contain
															// spaces
				"--interpreter", //$NON-NLS-1$
				// We currently work with MI version 2. Don't use just 'mi'
				// because it
				// points to the latest MI version, while we want mi2
				// specifically.
				"mi2", //$NON-NLS-1$
				// Don't read the gdbinit file here. It is read explicitly in
				// the LaunchSequence to make it easier to customize.
				"--nx" }; //$NON-NLS-1$
	}

	@Override
	public String getGDBInitFile() throws CoreException {
		if (fGDBInitFile == null) {
			String defaultGdbInit = Platform.getPreferencesService().getString(GdbPlugin.PLUGIN_ID,
					IGdbDebugPreferenceConstants.PREF_DEFAULT_GDB_INIT,
					IGDBLaunchConfigurationConstants.DEBUGGER_GDB_INIT_DEFAULT, null);

			fGDBInitFile = fLaunchConfiguration.getAttribute(IGDBLaunchConfigurationConstants.ATTR_GDB_INIT,
					defaultGdbInit);
		}

		return fGDBInitFile;
	}

	@Override
	public IPath getGDBWorkingDirectory() throws CoreException {
		if (fGDBWorkingDirectory == null) {

			// First try to use the user-specified working directory for the
			// debugged program.
			// This is fine only with local debug.
			// For remote debug, the working dir of the debugged program will be
			// on remote device
			// and hence not applicable. In such case we may just use debugged
			// program path on host
			// as the working dir for GDB.
			// However, we cannot find a standard/common way to distinguish
			// remote debug from local
			// debug. For instance, a local debug may also use gdbserver+gdb. So
			// it's up to each
			// debugger implementation to make the distinction.
			//
			IPath path = null;
			String location = fLaunchConfiguration.getAttribute(ICDTLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
					(String) null);

			if (location != null) {
				String expandedLocation = VariablesPlugin.getDefault().getStringVariableManager()
						.performStringSubstitution(location);
				if (expandedLocation.length() > 0) {
					path = new Path(expandedLocation);
				}
			}

			if (path != null) {
				// Some validity check. Should have been done by UI code.
				if (path.isAbsolute()) {
					File dir = new File(path.toPortableString());
					if (!dir.isDirectory())
						path = null;
				} else {
					IResource res = ResourcesPlugin.getWorkspace().getRoot().findMember(path);
					if (res instanceof IContainer && res.exists()) {
						path = res.getLocation();
					} else
						// Relative but not found in workspace.
						path = null;
				}
			}

			if (path == null) {
				// default working dir is the project if this config has a
				// project
				ICProject cp = LaunchUtils.getCProject(fLaunchConfiguration);
				if (cp != null) {
					IProject p = cp.getProject();
					path = p.getLocation();
				} else {
					// no meaningful value found. Just return null.
				}
			}

			fGDBWorkingDirectory = path;
		}

		return fGDBWorkingDirectory;
	}

	@Override
	public String getProgramArguments() throws CoreException {
		if (fProgramArguments == null) {
			fProgramArguments = fLaunchConfiguration
					.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, (String) null);

			if (fProgramArguments != null) {
				fProgramArguments = VariablesPlugin.getDefault().getStringVariableManager()
						.performStringSubstitution(fProgramArguments);
			}
		}

		return fProgramArguments;
	}

	@Override
	public IPath getProgramPath() {
		return fProgramPath;
	}

	@Override
	public List<String> getSharedLibraryPaths() throws CoreException {
		if (fSharedLibPaths == null) {
			fSharedLibPaths = fLaunchConfiguration
					.getAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_SOLIB_PATH, new ArrayList<String>(0));
		}

		return fSharedLibPaths;
	}

	/** @since 3.0 */
	@Override
	public Properties getEnvironmentVariables() throws CoreException {
		if (fEnvVariables == null) {
			fEnvVariables = new Properties();

			// if the attribute ATTR_APPEND_ENVIRONMENT_VARIABLES is set,
			// the LaunchManager will return both the new variables and the
			// existing ones.
			// That would force us to delete all the variables in GDB, and then
			// re-create then all
			// that is not very efficient. So, let's fool the LaunchManager into
			// returning just the
			// list of new variables.

			boolean append = fLaunchConfiguration.getAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true);

			String[] properties;
			if (append) {
				ILaunchConfigurationWorkingCopy wc = fLaunchConfiguration.copy(""); //$NON-NLS-1$
				// Don't save this change, it is just temporary, and in just a
				// copy of our launchConfig.
				wc.setAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, false);
				properties = DebugPlugin.getDefault().getLaunchManager().getEnvironment(wc);
			} else {
				// We're getting rid of the environment anyway, so this call
				// will only yield the new variables.
				properties = DebugPlugin.getDefault().getLaunchManager().getEnvironment(fLaunchConfiguration);
			}

			if (properties == null) {
				properties = new String[0];
			}

			for (String property : properties) {
				int idx = property.indexOf('=');
				if (idx != -1) {
					String key = property.substring(0, idx);
					String value = property.substring(idx + 1);
					fEnvVariables.setProperty(key, value);
				} else {
					fEnvVariables.setProperty(property, ""); //$NON-NLS-1$
				}
			}
		}

		return fEnvVariables;
	}

	/** @since 3.0 */
	@Override
	public boolean getClearEnvironment() throws CoreException {
		return !fLaunchConfiguration.getAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
	}

	/** @since 3.0 */
	@Override
	public boolean getUpdateThreadListOnSuspend() throws CoreException {
		return fLaunchConfiguration.getAttribute(
				IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
				IGDBLaunchConfigurationConstants.DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND_DEFAULT);
	}

	protected Process launchGDBProcess() throws CoreException {
		// Keep calling deprecated getGDBCommandLine() in case it was overridden
		String command = getGDBCommandLine();
		// Keep calling deprecated launchGDBProcess(String) in case it was
		// overridden
		return launchGDBProcess(command);
	}

	/**
	 * Launch GDB process. Allow subclass to override.
	 * 
	 * @deprecated Use {@link #launchGDBProcess(String[])} instead
	 */
	@Deprecated
	protected Process launchGDBProcess(String commandLine) throws CoreException {
		// Backwards-compatibility check
		// If the commandLine parameter is not the same as the command line
		// array we provide
		// it implies that the commandLine was modified by an extender and
		// should be used as
		// is. If it is the same, we can use the command line array instead
		// using the more robust
		// non-deprecated call to launchGDBProcess.
		String unmodifiedCmdLine = StringUtil.join(getGDBCommandLineArray(), " ").trim(); //$NON-NLS-1$
		if (unmodifiedCmdLine.equals(commandLine.trim()) == false) {
			Process proc = DebugUtils.exec(commandLine, LaunchUtils.getLaunchEnvironment(fLaunchConfiguration));
			return proc;
		}
		// End of Backwards-compatibility check

		return launchGDBProcess(getGDBCommandLineArray());
	}

	/**
	 * Launch GDB process with command and arguments. Allow subclass to
	 * override.
	 * 
	 * @since 4.6
	 */
	protected Process launchGDBProcess(String[] commandLineArray) throws CoreException {
		Process proc = DebugUtils.exec(commandLineArray,
					LaunchUtils.getLaunchEnvironment(fLaunchConfiguration), null);
		return proc;
	}

	public Process getProcess() {
		return fProcess;
	}

	@Override
	public OutputStream getMIOutputStream() {
		return fProcess.getOutputStream();
	};

	@Override
	public InputStream getMIInputStream() {
		return fProcess.getInputStream();
	};

	/** @since 4.1 */
	@Override
	public InputStream getMIErrorStream() {
		return fProcess.getErrorStream();
	};

	@Override
	public String getId() {
		return fBackendId;
	}

	@Override
	public void interrupt() {
		if (fProcess instanceof Spawner) {
			Spawner gdbSpawner = (Spawner) fProcess;

			// Cygwin gdb 6.8 is capricious when it comes to interrupting the
			// target. The same logic here will work with MinGW, though. And on
			// linux it's irrelevant since interruptCTRLC()==interrupt(). So,
			// one odd size fits all.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=304096#c54
			if (getSessionType() == SessionType.REMOTE) {
				gdbSpawner.interrupt();
			} else {
				gdbSpawner.interruptCTRLC();
			}
		}
	}

	/**
	 * @since 3.0
	 */
	@Override
	public void interruptAndWait(int timeout, RequestMonitor rm) {
		if (fProcess instanceof Spawner) {
			Spawner gdbSpawner = (Spawner) fProcess;

			// Cygwin gdb 6.8 is capricious when it comes to interrupting the
			// target. The same logic here will work with MinGW, though. And on
			// linux it's irrelevant since interruptCTRLC()==interrupt(). So,
			// one odd size fits all.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=304096#c54
			if (getSessionType() == SessionType.REMOTE) {
				gdbSpawner.interrupt();
			} else {
				gdbSpawner.interruptCTRLC();
			}
			fInterruptFailedJob = new MonitorInterruptJob(timeout, rm);
		} else {
			rm.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, IDsfStatusConstants.NOT_SUPPORTED,
					"Cannot interrupt.", null)); //$NON-NLS-1$
			rm.done();
		}
	}

	/**
	 * @since 3.0
	 */
	@Override
	public void interruptInferiorAndWait(long pid, int timeout, RequestMonitor rm) {
		if (fProcess instanceof Spawner) {
			Spawner gdbSpawner = (Spawner) fProcess;
			gdbSpawner.raise((int) pid, gdbSpawner.INT);
			fInterruptFailedJob = new MonitorInterruptJob(timeout, rm);
		} else {
			rm.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, IDsfStatusConstants.NOT_SUPPORTED,
					"Cannot interrupt.", null)); //$NON-NLS-1$
			rm.done();
		}
	}

	@Override
	public void destroy() {
		// Don't close the streams ourselves as it may be too early.
		// Wait for the actual user of the streams to close it.
		// Bug 339379

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmGdbBackend.destroy()");
		}

		// destroy() should be supported even if it's not spawner.
		if (getState() == State.STARTED) {
			fProcess.destroy();
		}
	}

	@Override
	public State getState() {
		return fBackendState;
	}

	@Override
	public int getExitCode() {
		return fGDBExitValue;
	}

	@Override
	public SessionType getSessionType() {
		if (fSessionType == null) {
			fSessionType = LaunchUtils.getSessionType(fLaunchConfiguration);
		}
		return fSessionType;
	}

	@Override
	public boolean getIsAttachSession() {
		if (fAttach == null) {
			fAttach = LaunchUtils.getIsAttach(fLaunchConfiguration);
		}
		return fAttach;
	}

	@Override
	protected BundleContext getBundleContext() {
		return GdbPlugin.getBundleContext();
	}

	protected class GDBProcessStep extends InitializationShutdownStep {
		GDBProcessStep(Direction direction) {
			super(direction);
		}

		@Override
		public void initialize(final RequestMonitor requestMonitor) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("GDBProcessStep.initialise()");
			}

			class GDBLaunchMonitor {
				boolean fLaunched = false;
				boolean fTimedOut = false;
			}
			final GDBLaunchMonitor fGDBLaunchMonitor = new GDBLaunchMonitor();

			final RequestMonitor gdbLaunchRequestMonitor = new RequestMonitor(getExecutor(), requestMonitor) {
				@Override
				protected void handleCompleted() {
					if (Activator.getInstance().isDebugging()) {
						System.out.println("GDBProcessStep.initialise() handleCompleted()");
					}

					if (!fGDBLaunchMonitor.fTimedOut) {
						fGDBLaunchMonitor.fLaunched = true;
						if (!isSuccess()) {
							requestMonitor.setStatus(getStatus());
						}
						requestMonitor.done();
					}
				}
			};

			final Job startGdbJob = new Job("Start GDB Process Job") { //$NON-NLS-1$
				{
					setSystem(true);
				}

				@Override
				protected IStatus run(IProgressMonitor monitor) {

					if (Activator.getInstance().isDebugging()) {
						System.out.println("GDBProcessStep.initialise() Job run()");
					}

					if (gdbLaunchRequestMonitor.isCanceled()) {
						gdbLaunchRequestMonitor.setStatus(
								new Status(IStatus.CANCEL, GdbPlugin.PLUGIN_ID, -1, "Canceled starting GDB", null)); //$NON-NLS-1$
						gdbLaunchRequestMonitor.done();
						return Status.OK_STATUS;
					}

					try {
						fProcess = launchGDBProcess();
						// Need to do this on the executor for thread-safety
						getExecutor().submit(new DsfRunnable() {
							@Override
							public void run() {
								fBackendState = State.STARTED;
							}
						});
						// Don't send the backendStarted event yet. We wait
						// until we have registered this service
						// so that other services can have access to it.
					} catch (CoreException e) {
						gdbLaunchRequestMonitor
								.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, -1, e.getMessage(), e));
						gdbLaunchRequestMonitor.done();
						return Status.OK_STATUS;
					}

					BufferedReader inputReader = null;
					BufferedReader errorReader = null;
					boolean success = false;
					try {
						// Read initial GDB prompt
						inputReader = new BufferedReader(new InputStreamReader(getMIInputStream()));
						String line;
						while ((line = inputReader.readLine()) != null) {
							line = line.trim();
							if (line.endsWith("(gdb)")) { //$NON-NLS-1$
								success = true;
								break;
							}
						}

						// Failed to read initial prompt, check for error
						if (!success) {
							errorReader = new BufferedReader(new InputStreamReader(getMIErrorStream()));
							String errorInfo = errorReader.readLine();
							if (errorInfo == null) {
								errorInfo = "GDB prompt not read"; //$NON-NLS-1$
							}
							gdbLaunchRequestMonitor
									.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, -1, errorInfo, null));
						}
					} catch (IOException e) {
						success = false;
						gdbLaunchRequestMonitor.setStatus(
								new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, -1, "Error reading GDB output", e)); //$NON-NLS-1$
					}

					// In the case of failure, close the MI streams so
					// they are not leaked.
					if (!success) {
						if (inputReader != null) {
							try {
								inputReader.close();
							} catch (IOException e) {
							}
						}
						if (errorReader != null) {
							try {
								errorReader.close();
							} catch (IOException e) {
							}
						}
					}

					gdbLaunchRequestMonitor.done();
					return Status.OK_STATUS;
				}
			};
			startGdbJob.schedule();

			getExecutor().schedule(new Runnable() {
				@Override
				public void run() {
					// Only process the event if we have not finished yet (hit
					// the breakpoint).
					if (!fGDBLaunchMonitor.fLaunched) {
						fGDBLaunchMonitor.fTimedOut = true;
						Thread jobThread = startGdbJob.getThread();
						if (jobThread != null) {
							jobThread.interrupt();
						}
						requestMonitor.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID,
								DebugException.TARGET_REQUEST_FAILED, "Timed out trying to launch GDB.", null)); //$NON-NLS-1$
						requestMonitor.done();
					}
				}
			}, fGDBLaunchTimeout, TimeUnit.SECONDS);
		}

		@Override
		protected void shutdown(final RequestMonitor requestMonitor) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("GDBProcessStep.shutdown()");
			}

			if (getState() != State.STARTED) {
				// gdb not started yet or already killed, don't bother starting
				// a job to kill it
				requestMonitor.done();
				return;
			}

			new Job("Terminating GDB process.") { //$NON-NLS-1$
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
							System.out.println("GDBProcessStep.shutdown() run()");
						}
						getExecutor().submit(new DsfRunnable() {
							@Override
							public void run() {
								destroy();

								if (fMonitorJob.fMonitorExited) {
									// Now that we have destroyed the process,
									// and that the monitoring thread was
									// killed,
									// we need to set our state and send the
									// event
									fBackendState = State.TERMINATED;
									if (Activator.getInstance().isDebugging()) {
										System.out.println(
												"GDBProcessStep.shutdown() run() dispatchEvent(BackendStateChangedEvent, TERMINATED)");
									}
									getSession().dispatchEvent(new BackendStateChangedEvent(getSession().getId(),
											getId(), State.TERMINATED), getProperties());
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
							fGDBExitValue = fProcess.exitValue(); // throws
																	// exception
																	// if
																	// process
																	// not
																	// exited

							if (Activator.getInstance().isDebugging()) {
								System.out.println("GDBProcessStep.shutdown() run() return");
							}
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
					if (Activator.getInstance().isDebugging()) {
						System.out.println("GDBProcessStep.shutdown() run() REQUEST_FAILED");
					}
					requestMonitor.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID,
							IDsfStatusConstants.REQUEST_FAILED, "GDB terminate failed", null)); //$NON-NLS-1$
					requestMonitor.done();
					return Status.OK_STATUS;
				}
			}.schedule();
		}
	}

	protected class MonitorJobStep extends InitializationShutdownStep {
		MonitorJobStep(Direction direction) {
			super(direction);
		}

		@Override
		public void initialize(final RequestMonitor requestMonitor) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("MonitorJobStep.initialize()");
			}
			fMonitorJob = new MonitorJob(fProcess, new DsfRunnable() {
				@Override
				public void run() {
					requestMonitor.done();
				}
			});
			fMonitorJob.schedule();
		}

		@Override
		protected void shutdown(RequestMonitor requestMonitor) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("MonitorJobStep.shutdown()");
			}
			if (fMonitorJob != null) {
				fMonitorJob.kill();
			}
			requestMonitor.done();
			if (Activator.getInstance().isDebugging()) {
				System.out.println("MonitorJobStep.shutdown() done");
			}
		}
	}

	protected class RegisterStep extends InitializationShutdownStep {
		RegisterStep(Direction direction) {
			super(direction);
		}

		@Override
		public void initialize(final RequestMonitor requestMonitor) {
			register(new String[] { IMIBackend.class.getName(), IMIBackend2.class.getName(),
					IGDBBackend.class.getName() }, new Hashtable<String, String>());

			getSession().addServiceEventListener(GnuArmGdbBackend.this, null);

			/*
			 * This event is not consumed by any one at present, instead it's
			 * the GDBControlInitializedDMEvent that's used to indicate that GDB
			 * back end is ready for MI commands. But we still fire the event as
			 * it does no harm and may be needed sometime.... 09/29/2008
			 * 
			 * We send the event in the register step because that is when other
			 * services have access to it.
			 */
			getSession().dispatchEvent(new BackendStateChangedEvent(getSession().getId(), getId(), State.STARTED),
					getProperties());

			requestMonitor.done();
		}

		@Override
		protected void shutdown(RequestMonitor requestMonitor) {
			unregister();
			getSession().removeServiceEventListener(GnuArmGdbBackend.this);
			requestMonitor.done();
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
					fGDBExitValue = fMonProcess.exitValue();

					if (Activator.getInstance().isDebugging()) {
						System.out.println("MonitorJob.run() exitValue() " + fGDBExitValue);
					}
					// Need to do this on the executor for thread-safety
					getExecutor().submit(new DsfRunnable() {
						@Override
						public void run() {

							if (Activator.getInstance().isDebugging()) {
								System.out.println("MonitorJob.run() run() ");
							}

							destroy();
							fBackendState = State.TERMINATED;

							if (Activator.getInstance().isDebugging()) {
								System.out.println(
										"MonitorJob.run() run() dispatchEvent(BackendStateChangedEvent, TERMINATED)");
							}
							getSession().dispatchEvent(
									new BackendStateChangedEvent(getSession().getId(), getId(), State.TERMINATED),
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
			super("GDB process monitor job."); //$NON-NLS-1$
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

	/**
	 * Stores the request monitor that must be dealt with for the result of the
	 * interrupt operation. If the interrupt successfully suspends the backend,
	 * the request monitor can be retrieved and completed successfully, and then
	 * this job should be canceled. If this job is not canceled before the time
	 * is up, it will imply the interrupt did not successfully suspend the
	 * backend, and the current job will indicate this in the request monitor.
	 * 
	 * The specified timeout is used to indicate how many milliseconds this job
	 * should wait for. INTERRUPT_TIMEOUT_DEFAULT indicates to use the default
	 * of 5 seconds. The default is also use if the timeout value is 0 or
	 * negative.
	 * 
	 * @since 3.0
	 */
	protected class MonitorInterruptJob extends Job {
		// Bug 310274. Until we have a preference to configure timeouts,
		// we need a large enough default timeout to accommodate slow
		// remote sessions.
		private final static int TIMEOUT_DEFAULT_VALUE = 5000;
		private final RequestMonitor fRequestMonitor;

		public MonitorInterruptJob(int timeout, RequestMonitor rm) {
			super("Interrupt monitor job."); //$NON-NLS-1$
			setSystem(true);
			fRequestMonitor = rm;

			if (timeout == INTERRUPT_TIMEOUT_DEFAULT || timeout <= 0) {
				timeout = TIMEOUT_DEFAULT_VALUE; // default of 5 seconds
			}

			schedule(timeout);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			getExecutor().submit(new DsfRunnable() {
				@Override
				public void run() {
					fInterruptFailedJob = null;
					fRequestMonitor.setStatus(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID,
							IDsfStatusConstants.REQUEST_FAILED, "Interrupt failed.", null)); //$NON-NLS-1$
					fRequestMonitor.done();
				}
			});
			return Status.OK_STATUS;
		}

		public RequestMonitor getRequestMonitor() {
			return fRequestMonitor;
		}
	}

	/**
	 * We use this handler to determine if the SIGINT we sent to GDB has been
	 * effective. We must listen for an MI event and not a higher-level
	 * ISuspendedEvent. The reason is that some ISuspendedEvent are not sent
	 * when the target stops, in cases where we don't want to views to update.
	 * For example, if we want to interrupt the target to set a breakpoint, this
	 * interruption is done silently; we will receive the MI event though.
	 * 
	 * <p>
	 * Though we send a SIGINT, we may not specifically get an MISignalEvent.
	 * Typically we will, but not always, so wait for an MIStoppedEvent. See
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=305178#c21
	 * 
	 * @since 3.0
	 * 
	 */
	@DsfServiceEventHandler
	public void eventDispatched(final MIStoppedEvent e) {
		if (fInterruptFailedJob != null) {
			if (fInterruptFailedJob.cancel()) {
				fInterruptFailedJob.getRequestMonitor().done();
			}
			fInterruptFailedJob = null;
		}
	}

	/**
	 * Safety net, in case the GDB client does not exit on command.
	 * 
	 * The event is created and triggered by Process.terminate().
	 * 
	 * @param e
	 */
	@DsfServiceEventHandler
	public void eventDispatched(final ProcessStateChangedEvent e) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmGdbBackend.eventDispatched() " + e);
		}

		// When the process is terminated, also terminate the backend.
		if (e.getState() == State.TERMINATED && e.getSessionId().equals(getSession().getId())
				&& getState() == State.STARTED) {

			destroy();
		}
	}

	// ------------------------------------------------------------------------
}
