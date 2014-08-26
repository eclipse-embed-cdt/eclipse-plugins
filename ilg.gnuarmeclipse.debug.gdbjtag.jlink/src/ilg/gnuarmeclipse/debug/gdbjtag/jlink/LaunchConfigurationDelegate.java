package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import ilg.gnuarmeclipse.debug.core.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui.TabDebugger;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFLaunchConfigurationDelegate;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.ImmediateExecutor;
import org.eclipse.cdt.dsf.concurrent.Query;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.debug.service.IDsfDebugServicesFactory;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.cdt.dsf.gdb.launching.LaunchUtils;
import org.eclipse.cdt.dsf.gdb.launching.ServicesLaunchSequence;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;

@SuppressWarnings("restriction")
public class LaunchConfigurationDelegate extends
		GDBJtagDSFLaunchConfigurationDelegate {

	private final static String NON_STOP_FIRST_VERSION = "6.8.50"; //$NON-NLS-1$

	ILaunchConfiguration fConfig = null;

	// private GdbLaunch fGdbLaunch;

	@Override
	protected IDsfDebugServicesFactory newServiceFactory(
			ILaunchConfiguration config, String version) {

		return new ServicesFactory(version);
		// return new GdbJtagDebugServicesFactory(version);
	}

	protected GdbLaunch createGdbLaunch(ILaunchConfiguration configuration,
			String mode, ISourceLocator locator) throws CoreException {
		// return new GdbLaunch(configuration, mode, locator);

		System.out.println("createGdbLaunch() " + this);

		return new Launch(configuration, mode, locator);
	}

	protected String getGDBVersion(ILaunchConfiguration config)
			throws CoreException {

		String gdbClientCommand = TabDebugger.getGdbClientCommand(config);
		String version = DebugUtils.getGDBVersion(config, gdbClientCommand);
		System.out.println("GDB version=" + version);
		return version;
	}

	@Override
	public void launch(ILaunchConfiguration config, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		System.out.println("launch() " + this);

		org.eclipse.cdt.launch.LaunchUtils.enableActivity(
				"org.eclipse.cdt.debug.dsfgdbActivity", true); //$NON-NLS-1$
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			launchDebugger(config, launch, monitor);
		}
	}

	private void launchDebugger(ILaunchConfiguration config, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {

		int totalWork = 10;
		monitor.beginTask(LaunchMessages.getString("GdbLaunchDelegate.0"), 10); //$NON-NLS-1$
		if (monitor.isCanceled()) {
			cleanupLaunch();
			return;
		}

		try {
			launchDebugSession(config, launch, monitor);
		} finally {
			monitor.done();
		}
	}

	/** @since 4.1 */
	protected void launchDebugSession(final ILaunchConfiguration config,
			ILaunch l, IProgressMonitor monitor) throws CoreException {

		if (monitor.isCanceled()) {
			cleanupLaunch();
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

		String gdbVersion = getGDBVersion(config);

		// First make sure non-stop is supported, if the user want to use this
		// mode
		if (LaunchUtils.getIsNonStopMode(config)
				&& !isNonStopSupportedInGdbVersion(gdbVersion)) {
			cleanupLaunch();
			throw new DebugException(
					new Status(
							IStatus.ERROR,
							Activator.PLUGIN_ID,
							DebugException.REQUEST_FAILED,
							"Non-stop mode is not supported for GDB " + gdbVersion + ", GDB " + NON_STOP_FIRST_VERSION + " or higher is required.", null)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$        	
		}

		if (LaunchUtils.getIsPostMortemTracing(config)
				&& !isPostMortemTracingSupportedInGdbVersion(gdbVersion)) {
			cleanupLaunch();
			throw new DebugException(
					new Status(
							IStatus.ERROR,
							Activator.PLUGIN_ID,
							DebugException.REQUEST_FAILED,
							"Post-mortem tracing is not supported for GDB " + gdbVersion + ", GDB " + NON_STOP_FIRST_VERSION + " or higher is required.", null)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$        	
		}

		launch.setServiceFactory(newServiceFactory(config, gdbVersion));

		// Create and invoke the launch sequence to create the debug control and
		// services
		IProgressMonitor subMon1 = new SubProgressMonitor(monitor, 4,
				SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
		final ServicesLaunchSequence servicesLaunchSequence = new ServicesLaunchSequence(
				launch.getSession(), launch, subMon1);

		launch.getSession().getExecutor().execute(servicesLaunchSequence);
		boolean succeed = false;
		try {
			servicesLaunchSequence.get();
			succeed = true;
		} catch (InterruptedException e1) {
			throw new DebugException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, DebugException.INTERNAL_ERROR,
					"Interrupted Exception in dispatch thread", e1)); //$NON-NLS-1$
		} catch (ExecutionException e1) {
			throw new DebugException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Error in services launch sequence", e1.getCause())); //$NON-NLS-1$
		} catch (CancellationException e1) {
			// Launch aborted, so exit cleanly
			return;
		} finally {
			if (!succeed) {
				cleanupLaunch();
			}
		}

		if (monitor.isCanceled()) {
			cleanupLaunch();
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

		IProcess newProcess;
		boolean doAddServerConsole = config.getAttribute(
				ConfigurationAttributes.DO_START_GDB_SERVER,
				ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT)
				&& config
						.getAttribute(
								ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
								ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);

		if (doAddServerConsole) {

			// Add the GDB server process to the launch tree
			newProcess = ((Launch) launch)
					.addServerProcess(getServerCommandName(config));
			newProcess.setAttribute(IProcess.ATTR_CMDLINE,
					TabDebugger.getGdbServerCommandLine(config));

			monitor.worked(1);
		}

		{
			// Add the GDB client process to the launch tree.
			newProcess = ((Launch) launch)
					.addClientProcess(getClientCommandName(config)); //$NON-NLS-1$

			newProcess.setAttribute(IProcess.ATTR_CMDLINE,
					TabDebugger.getGdbClientCommandLine(config));

			monitor.worked(1);
		}

		boolean doAddSemihostingConsole = config.getAttribute(
				ConfigurationAttributes.DO_START_GDB_SERVER,
				ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT)
				&& config
						.getAttribute(
								ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
								ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT);

		if (doAddSemihostingConsole) {

			// Add the special semihosting and SWV process to the launch tree
			newProcess = ((Launch) launch)
					.addSemihostingProcess("Semihosting and SWV");

			monitor.worked(1);
		}

		// Create and invoke the final launch sequence to setup GDB
		final IProgressMonitor subMon2 = new SubProgressMonitor(monitor, 4,
				SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);

		Query<Object> completeLaunchQuery = new Query<Object>() {
			@Override
			protected void execute(final DataRequestMonitor<Object> rm) {
				DsfServicesTracker tracker = new DsfServicesTracker(
						GdbPlugin.getBundleContext(), launch.getSession()
								.getId());
				IGDBControl control = tracker.getService(IGDBControl.class);
				tracker.dispose();
				control.completeInitialization(new RequestMonitorWithProgress(
						ImmediateExecutor.getInstance(), subMon2) {
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
			throw new DebugException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, DebugException.INTERNAL_ERROR,
					"Interrupted Exception in dispatch thread", e1)); //$NON-NLS-1$
		} catch (ExecutionException e1) {
			throw new DebugException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Error in final launch sequence", e1.getCause())); //$NON-NLS-1$
		} catch (CancellationException e1) {
			// Launch aborted, so exit cleanly
			return;
		} finally {
			if (!succeed) {
				// finalLaunchSequence failed. Shutdown the session so that all
				// started
				// services including any GDB process are shutdown. (bug 251486)
				cleanupLaunch();
			}
		}
	}

	private String getServerCommandName(ILaunchConfiguration config) {
		String fullCommand = TabDebugger.getGdbServerCommand(config);
		if (fullCommand == null)
			return null;

		String parts[] = fullCommand.trim().split("" + Path.SEPARATOR);
		return parts[parts.length - 1];
	}

	private String getClientCommandName(ILaunchConfiguration config) {
		String fullCommand = TabDebugger.getGdbClientCommand(config);
		if (fullCommand == null)
			return null;

		String parts[] = fullCommand.trim().split("" + Path.SEPARATOR);
		return parts[parts.length - 1];
	}
}
