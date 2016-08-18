/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Liviu Ionescu - initial version
 *     Jonah Graham - fix for Neon
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.jlink.dsf;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.ImmediateExecutor;
import org.eclipse.cdt.dsf.concurrent.Query;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.debug.service.IDsfDebugServicesFactory;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.cdt.dsf.gdb.launching.LaunchUtils;
import org.eclipse.cdt.dsf.gdb.launching.ServicesLaunchSequence;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ISourceLocator;

import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.dsf.AbstractGnuArmLaunchConfigurationDelegate;
import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmServerServicesLaunchSequence;
import ilg.gnuarmeclipse.debug.gdbjtag.jlink.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.jlink.Configuration;

/**
 * This class is referred in the plugin.xml as an
 * "org.eclipse.debug.core.launchDelegates" extension point.
 *
 * It inherits directly from the GDB Hardware Debug plug-in.
 *
 *
 */
@SuppressWarnings("restriction")
public class LaunchConfigurationDelegate extends AbstractGnuArmLaunchConfigurationDelegate {

	// ------------------------------------------------------------------------

	private final static String NON_STOP_FIRST_VERSION = "6.8.50"; //$NON-NLS-1$

	ILaunchConfiguration fConfig = null;
	@SuppressWarnings("unused")
	private boolean fIsNonStopSession = false;
	private boolean fDoStartGdbServer = false;
	private boolean fDoAddSemihostingConsole = false;

	// private GdbLaunch fGdbLaunch;

	// ------------------------------------------------------------------------

	@Override
	protected IDsfDebugServicesFactory newServiceFactory(ILaunchConfiguration config, String version) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"LaunchConfigurationDelegate.newServiceFactory(" + config.getName() + "," + version + ") " + this);
		}

		fConfig = config;
		return new ServicesFactory(version, ILaunchManager.DEBUG_MODE);
		// return new GdbJtagDebugServicesFactory(version);
	}

	protected IDsfDebugServicesFactory newServiceFactory(ILaunchConfiguration config, String version, String mode) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("LaunchConfigurationDelegate.newServiceFactory(" + config.getName() + "," + version + ","
					+ mode + ") " + this);
		}

		fConfig = config;
		return new ServicesFactory(version, mode);
		// return new GdbJtagDebugServicesFactory(version);
	}

	/**
	 * This method is called first when starting a debug session.
	 */
	protected GdbLaunch createGdbLaunch(ILaunchConfiguration configuration, String mode, ISourceLocator locator)
			throws CoreException {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("LaunchConfigurationDelegate.createGdbLaunch(" + configuration.getName() + "," + mode
					+ ") " + this);
		}

		fDoStartGdbServer = Configuration.getDoStartGdbServer(configuration);

		fDoAddSemihostingConsole = Configuration.getDoAddSemihostingConsole(configuration);

		DebugUtils.checkLaunchConfigurationStarted(configuration);

		// return new GdbLaunch(configuration, mode, locator);
		return new Launch(configuration, mode, locator);
	}

	protected String getGDBVersion(ILaunchConfiguration config) throws CoreException {

		String gdbClientCommand = Configuration.getGdbClientCommand(config);
		String version = DebugUtils.getGDBVersion(config, gdbClientCommand);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("LaunchConfigurationDelegate.getGDBVersion " + version);
		}
		return version;
	}

	/**
	 * After Launch.initialise(), call here to effectively launch.
	 *
	 * The main reason for this is the custom launchDebugSession().
	 */
	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("LaunchConfigurationDelegate.launch(" + config.getName() + "," + mode + ") " + this);
		}

		org.eclipse.cdt.launch.LaunchUtils.enableActivity("org.eclipse.cdt.debug.dsfgdbActivity", true); //$NON-NLS-1$
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		if (mode.equals(ILaunchManager.DEBUG_MODE) || mode.equals(ILaunchManager.RUN_MODE)) {
			launchDebugger(config, launch, monitor);
		}
	}

	private void launchDebugger(ILaunchConfiguration config, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("LaunchConfigurationDelegate.launchDebugger(" + config.getName() + ") " + this);
		}

		int totalWork = 10;
		// Extra units due to server and semihosting console
		totalWork += 1;
		if (fDoStartGdbServer) {
			if (fDoAddSemihostingConsole) {
				totalWork += 1;
			}
		}

		monitor.beginTask(LaunchMessages.getString("GdbLaunchDelegate.0"), totalWork); //$NON-NLS-1$
		if (monitor.isCanceled()) {
			cleanupLaunchLocal(launch);
			return;
		}

		try {
			launchDebugSession(config, launch, monitor);
		} finally {
			monitor.done();
		}
	}

	/**
	 * This customisation was required to replace the creation of the initial
	 * "gdb" console with three consoles (server, client, semihosting).
	 *
	 */
	protected void launchDebugSession(final ILaunchConfiguration config, ILaunch l, IProgressMonitor monitor)
			throws CoreException {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("LaunchConfigurationDelegate.launchDebugSession(" + config.getName() + ") " + this);
		}

		// From here it is almost identical with the system one, except
		// the console creation, explicitly marked with '+++++'.

		// --------------------------------------------------------------------
		if (monitor.isCanceled()) {
			cleanupLaunchLocal(l);
			return;
		}

		SessionType sessionType = LaunchUtils.getSessionType(config);
		boolean attach = LaunchUtils.getIsAttach(config);

		final GdbLaunch launch = (GdbLaunch) l;

		if (sessionType == SessionType.REMOTE) {
			monitor.subTask(LaunchMessages.getString("GdbLaunchDelegate.1")); //$NON-NLS-1$
		} else if (sessionType == SessionType.CORE) {
			monitor.subTask(LaunchMessages.getString("GdbLaunchDelegate.2")); //$NON-NLS-1$
		} else {
			assert sessionType == SessionType.LOCAL : "Unexpected session type: " + sessionType.toString(); //$NON-NLS-1$
			monitor.subTask(LaunchMessages.getString("GdbLaunchDelegate.3")); //$NON-NLS-1$
		}

		// An attach session does not need to necessarily have an
		// executable specified. This is because:
		// - In remote multi-process attach, there will be more than one
		// executable
		// In this case executables need to be specified differently.
		// The current solution is to use the solib-search-path to specify
		// the path of any executable we can attach to.
		// - In local single process, GDB has the ability to find the executable
		// automatically.
		if (!attach) {
			checkBinaryDetails(config);
		}

		monitor.worked(1);

		// Must set this here for users that call directly the deprecated
		// newServiceFactory(String)
		fIsNonStopSession = LaunchUtils.getIsNonStopMode(config);

		String gdbVersion = getGDBVersion(config);

		// First make sure non-stop is supported, if the user want to use this
		// mode
		if (LaunchUtils.getIsNonStopMode(config) && !isNonStopSupportedInGdbVersion(gdbVersion)) {
			cleanupLaunchLocal(launch);
			throw new DebugException(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Non-stop mode is not supported for GDB " + gdbVersion + ", GDB " + NON_STOP_FIRST_VERSION //$NON-NLS-1$ //$NON-NLS-2$
							+ " or higher is required.", //$NON-NLS-1$
					null));
		}

		if (LaunchUtils.getIsPostMortemTracing(config) && !isPostMortemTracingSupportedInGdbVersion(gdbVersion)) {
			cleanupLaunchLocal(launch);
			throw new DebugException(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Post-mortem tracing is not supported for GDB " + gdbVersion + ", GDB " + NON_STOP_FIRST_VERSION //$NON-NLS-1$ //$NON-NLS-2$
							+ " or higher is required.", //$NON-NLS-1$
					null));
		}

		launch.setServiceFactory(newServiceFactory(config, gdbVersion, launch.getLaunchMode()));

		// Time to start the DSF stuff. First initialize the launch.
		// We do this here to avoid having to cleanup in case
		// the launch is cancelled above.
		// This initialize() call is the first thing that requires cleanup
		// followed by the steps further down which also need cleanup.
		launch.initialize();

		// vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv

		boolean succeed = false;

		// Assign 4 work ticks.
		IProgressMonitor subMonServer = new SubProgressMonitor(monitor, 4,
				SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);

		Sequence serverServicesLaunchSequence = getServerServicesSequence(launch.getSession(), launch, subMonServer);

		try {
			// Execute on DSF thread and wait for it.
			launch.getSession().getExecutor().execute(serverServicesLaunchSequence);
			serverServicesLaunchSequence.get();
			succeed = true;
		} catch (InterruptedException e1) {
			throw new DebugException(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, DebugException.INTERNAL_ERROR,
					"Interrupted Exception in dispatch thread", e1)); //$NON-NLS-1$
		} catch (ExecutionException e1) {
			throw new DebugException(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Error in services launch sequence", e1.getCause())); //$NON-NLS-1$
		} catch (CancellationException e1) {
			// Launch aborted, so exit cleanly
			if (Activator.getInstance().isDebugging()) {
				System.out.println("Launch aborted, so exit cleanly");
			}
			return;
		} finally {
			if (!succeed) {
				cleanupLaunchLocal(launch);
			}
		}

		if (fDoStartGdbServer) {

			// This contributes 1 work units to the monitor
			((Launch) launch).initializeServerConsole(monitor);

			// Wait for the server to be available, or to know it failed.
			IStatus serverStatus;
			try {
				Callable<IStatus> callable = new Callable<IStatus>() {
					@Override
					public IStatus call() throws CoreException {
						DsfServicesTracker tracker = new DsfServicesTracker(GdbPlugin.getBundleContext(),
								launch.getSession().getId());
						GdbServerBackend backend = (GdbServerBackend) tracker.getService(GdbServerBackend.class);
						if (backend != null) {
							return backend.getServerExitStatus();
						} else {
							throw new CoreException(
									new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Could not start GDB server."));
						}
					}
				};

				// Wait to get the server status. Being endless should not be a
				// problem, the timeout will kill it if too long.
				serverStatus = null;
				while (serverStatus == null) {
					if (monitor.isCanceled()) {
						if (Activator.getInstance().isDebugging()) {
							System.out
									.println("LaunchConfigurationDelegate.launchDebugSession() sleep cancelled" + this);
						}
						cleanupLaunchLocal(launch);
						return;
					}
					Thread.sleep(10);
					serverStatus = launch.getSession().getExecutor().submit(callable).get();
					if (Activator.getInstance().isDebugging()) {
						System.out.print('!');
					}
				}

				if (serverStatus != Status.OK_STATUS) {
					if ("TERMINATED".equals(serverStatus.getMessage())) {
						cleanupLaunchLocal(launch);
						return;
					}
					if (Activator.getInstance().isDebugging()) {
						System.out.println(serverStatus);
					}
					throw new CoreException(serverStatus);
				}

			} catch (InterruptedException e) {
				Activator.log(e);
			} catch (ExecutionException e) {
				Activator.log(e);
			}

			if (Activator.getInstance().isDebugging()) {
				System.out.println("launchDebugSession() * Server start confirmed. *");
			}
		}

		// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

		// Create and invoke the launch sequence to create the debug control and
		// services

		// 4 work ticks.
		IProgressMonitor subMon1 = new SubProgressMonitor(monitor, 4, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
		Sequence servicesLaunchSequence = getServicesSequence(launch.getSession(), launch, subMon1);

		launch.getSession().getExecutor().execute(servicesLaunchSequence);
		// boolean succeed = false;
		try {
			servicesLaunchSequence.get();
			succeed = true;
		} catch (InterruptedException e1) {
			throw new DebugException(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, DebugException.INTERNAL_ERROR,
					"Interrupted Exception in dispatch thread", e1)); //$NON-NLS-1$
		} catch (ExecutionException e1) {
			throw new DebugException(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Error in services launch sequence", e1.getCause())); //$NON-NLS-1$
		} catch (CancellationException e1) {
			// Launch aborted, so exit cleanly
			if (Activator.getInstance().isDebugging()) {
				System.out.println("Launch aborted, so exit cleanly");
			}
			return;
		} finally {
			if (!succeed) {
				cleanupLaunchLocal(launch);
			}
		}

		if (monitor.isCanceled()) {
			cleanupLaunchLocal(launch);
			return;
		}

		// The initializeControl method should be called after the
		// ICommandControlService
		// is initialised in the ServicesLaunchSequence above. This is because
		// it is that
		// service that will trigger the launch cleanup (if we need it during
		// this launch)
		// through an ICommandControlShutdownDMEvent
		launch.initializeControl();

		// Add the GDB process object to the launch.

		// vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
		// launch.addCLIProcess("gdb"); //$NON-NLS-1$
		// monitor.worked(1);

		// This contributes one work units for the GDB client console
		// and optionally one for the semihosting console.
		((Launch) launch).initializeConsoles(monitor);
		// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

		// Create and invoke the final launch sequence to setup GDB
		final IProgressMonitor subMon2 = new SubProgressMonitor(monitor, 4,
				SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);

		Query<Object> completeLaunchQuery = new Query<Object>() {
			@Override
			protected void execute(final DataRequestMonitor<Object> rm) {
				DsfServicesTracker tracker = new DsfServicesTracker(GdbPlugin.getBundleContext(),
						launch.getSession().getId());
				IGDBControl control = tracker.getService(IGDBControl.class);
				tracker.dispose();
				control.completeInitialization(
						new RequestMonitorWithProgress(ImmediateExecutor.getInstance(), subMon2) {
							@Override
							protected void handleCompleted() {
								if (isCanceled()) {
									rm.cancel();
								} else {
									rm.setStatus(getStatus());
								}
								rm.done();
							}
						});
			}
		};

		launch.getSession().getExecutor().execute(completeLaunchQuery);
		succeed = false;
		try {
			completeLaunchQuery.get();
			succeed = true;
		} catch (InterruptedException e1) {
			throw new DebugException(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, DebugException.INTERNAL_ERROR,
					"Interrupted Exception in dispatch thread", e1)); //$NON-NLS-1$
		} catch (ExecutionException e1) {
			throw new DebugException(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Error in final launch sequence", e1.getCause())); //$NON-NLS-1$
		} catch (CancellationException e1) {
			// Launch aborted, so exit cleanly
			return;
		} finally {
			if (!succeed) {
				// finalLaunchSequence failed. Shutdown the session so that all
				// started
				// services including any GDB process are shutdown. (bug 251486)
				cleanupLaunchLocal(launch);
			}
		}
		// --------------------------------------------------------------------
	}

	/**
	 * Perform some local validations before starting the debug session.
	 */
	@Override
	protected IPath checkBinaryDetails(final ILaunchConfiguration config) throws CoreException {

		boolean doStartServer = true;
		try {
			doStartServer = Configuration.getDoStartGdbServer(config);
		} catch (CoreException e) {
			;
		}

		if (doStartServer) {
			// If we should start the server, there must be a device name
			// present, otherwise refuse to start.
			String deviceName = "";
			try {
				deviceName = Configuration.getGdbServerDeviceName(config);
			} catch (CoreException e) {
				;
			}

			if (deviceName.isEmpty()) {
				throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Missing mandatory device name. " + "Fill-in the 'Device name:' field in the Debugger tab.")); //$NON-NLS-2$
			}
		}

		IPath path = super.checkBinaryDetails(config);
		return path;
	}

	/**
	 * Get a custom launch sequence, that inserts a GDB server starter.
	 */
	protected Sequence getServicesSequence(DsfSession session, ILaunch launch, IProgressMonitor progressMonitor) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("LaunchConfigurationDelegate.getServicesSequence()");
		}

		return new ServicesLaunchSequence(session, (GdbLaunch) launch, progressMonitor);
	}

	protected Sequence getServerServicesSequence(DsfSession session, ILaunch launch, IProgressMonitor progressMonitor) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("LaunchConfigurationDelegate.getServerServicesSequence()");
		}

		return new GnuArmServerServicesLaunchSequence(session, (GdbLaunch) launch, progressMonitor);
	}

	// ------------------------------------------------------------------------
}
