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

package ilg.gnuarmeclipse.debug.gdbjtag.openocd.dsf;

import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmLaunch;
import ilg.gnuarmeclipse.debug.gdbjtag.openocd.Activator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import org.eclipse.cdt.dsf.concurrent.DefaultDsfExecutor;
import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.IGdbDebugConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.service.IGDBBackend;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.command.AbstractCLIProcess;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;

@SuppressWarnings("restriction")
public class Launch extends GnuArmLaunch {

	ILaunchConfiguration fConfig = null;
	private DsfSession fSession;
	private DsfServicesTracker fTracker;
	private DefaultDsfExecutor fExecutor;

	public Launch(ILaunchConfiguration launchConfiguration, String mode,
			ISourceLocator locator) {

		super(launchConfiguration, mode, locator);

		fConfig = launchConfiguration;
		fExecutor = (DefaultDsfExecutor) getDsfExecutor();
		fSession = getSession();
	}

	public void initialize() {

		Runnable initRunnable = new DsfRunnable() {
			@Override
			public void run() {
				fTracker = new DsfServicesTracker(GdbPlugin.getBundleContext(),
						fSession.getId());
				// fSession.addServiceEventListener(GdbLaunch.this, null);

				// fInitialized = true;
				// fireChanged();
			}
		};

		// Invoke the execution code and block waiting for the result.
		try {
			fExecutor.submit(initRunnable).get();
		} catch (InterruptedException e) {
			new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					IDsfStatusConstants.INTERNAL_ERROR,
					"Error initializing launch", e); //$NON-NLS-1$
		} catch (ExecutionException e) {
			new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					IDsfStatusConstants.INTERNAL_ERROR,
					"Error initializing launch", e); //$NON-NLS-1$
		}

		super.initialize();
	}

	public void shutdownSession(final RequestMonitor rm) {

		super.shutdownSession(rm);

	}

	public IProcess addServerProcess(String label) throws CoreException {
		IProcess newProcess = null;
		try {
			// Add the server process object to the launch.
			Process serverProc = getDsfExecutor().submit(
					new Callable<Process>() {
						@Override
						public Process call() throws CoreException {
							Backend backend = (Backend) fTracker
									.getService(IGDBBackend.class);
							if (backend != null) {
								return backend.getServerProcess();
							}
							return null;
						}
					}).get();

			// Need to go through DebugPlugin.newProcess so that we can use
			// the overrideable process factory to allow others to override.
			// First set attribute to specify we want to create the gdb process.
			// Bug 210366
			Map<String, String> attributes = new HashMap<String, String>();
			if (true) {
				attributes.put(IGdbDebugConstants.PROCESS_TYPE_CREATION_ATTR,
						IGdbDebugConstants.GDB_PROCESS_CREATION_VALUE);
			}
			newProcess = DebugPlugin.newProcess(this, serverProc, label,
					attributes);
		} catch (InterruptedException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, 0,
					"Interrupted while waiting for get process callable.", e)); //$NON-NLS-1$
		} catch (ExecutionException e) {
			throw (CoreException) e.getCause();
		} catch (RejectedExecutionException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, 0,
					"Debugger shut down before launch was completed.", e)); //$NON-NLS-1$
		}

		return newProcess;
	}

	public IProcess ____addClientProcess(String label) throws CoreException {
		IProcess newProcess = null;
		try {
			// Add the CLI process object to the launch.
			AbstractCLIProcess cliProc = getDsfExecutor().submit(
					new Callable<AbstractCLIProcess>() {
						@Override
						public AbstractCLIProcess call() throws CoreException {
							IGDBControl gdb = fTracker
									.getService(IGDBControl.class);
							if (gdb != null) {
								return gdb.getCLIProcess();
							}
							return null;
						}
					}).get();

			// Need to go through DebugPlugin.newProcess so that we can use
			// the overrideable process factory to allow others to override.
			// First set attribute to specify we want to create the gdb process.
			// Bug 210366
			Map<String, String> attributes = new HashMap<String, String>();
			if (true) {
				attributes.put(IGdbDebugConstants.PROCESS_TYPE_CREATION_ATTR,
						IGdbDebugConstants.GDB_PROCESS_CREATION_VALUE);
			}
			newProcess = DebugPlugin.newProcess(this, cliProc, label,
					attributes);
		} catch (InterruptedException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, 0,
					"Interrupted while waiting for get process callable.", e)); //$NON-NLS-1$
		} catch (ExecutionException e) {
			throw (CoreException) e.getCause();
		} catch (RejectedExecutionException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, 0,
					"Debugger shut down before launch was completed.", e)); //$NON-NLS-1$
		}

		return newProcess;
	}

	public IProcess addSemihostingProcess(String label) throws CoreException {
		IProcess newProcess = null;
		try {
			// Add the server process object to the launch.
			Process serverProc = getDsfExecutor().submit(
					new Callable<Process>() {
						@Override
						public Process call() throws CoreException {
							Backend backend = (Backend) fTracker
									.getService(IGDBBackend.class);
							if (backend != null) {
								return backend.getSemihostingProcess();
							}
							return null;
						}
					}).get();

			// Need to go through DebugPlugin.newProcess so that we can use
			// the overrideable process factory to allow others to override.
			// First set attribute to specify we want to create the gdb process.
			// Bug 210366
			Map<String, String> attributes = new HashMap<String, String>();

			// Not necessary, to simplify process factory
			// attributes.put(IGdbDebugConstants.PROCESS_TYPE_CREATION_ATTR,
			// IGdbDebugConstants.GDB_PROCESS_CREATION_VALUE);

			newProcess = DebugPlugin.newProcess(this, serverProc, label,
					attributes);
		} catch (InterruptedException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, 0,
					"Interrupted while waiting for get process callable.", e)); //$NON-NLS-1$
		} catch (ExecutionException e) {
			throw (CoreException) e.getCause();
		} catch (RejectedExecutionException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, 0,
					"Debugger shut down before launch was completed.", e)); //$NON-NLS-1$
		}

		return newProcess;
	}
}
