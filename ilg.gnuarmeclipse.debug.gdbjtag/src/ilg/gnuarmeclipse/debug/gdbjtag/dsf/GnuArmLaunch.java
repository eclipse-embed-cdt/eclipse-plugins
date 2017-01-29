/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Liviu Ionescu - initial version
 *     Jonah Graham - fix for Neon
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.dsf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;

import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.dsf.concurrent.DefaultDsfExecutor;
import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.ThreadSafeAndProhibitedFromDsfExecutor;
import org.eclipse.cdt.dsf.debug.internal.provisional.model.IMemoryBlockRetrievalManager;
import org.eclipse.cdt.dsf.gdb.IGdbDebugConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunchDelegate;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.command.AbstractCLIProcess;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;

/**
 * Common launcher, used to initialise the memory bloc retrieval used by the
 * Peripherals view.
 * <p>
 * To be used as parent by implementation (JLink, OpenOCD) launchers.
 */
@SuppressWarnings("restriction")
public class GnuArmLaunch extends GdbLaunch {

	// ------------------------------------------------------------------------

	// private ILaunchConfiguration fConfig = null;
	private DsfSession fSession;
	private DsfServicesTracker fTracker;
	private DefaultDsfExecutor fExecutor;
	private IMemoryBlockRetrievalManager fMemRetrievalManager;

	// ------------------------------------------------------------------------

	public GnuArmLaunch(ILaunchConfiguration launchConfiguration, String mode, ISourceLocator locator) {

		super(launchConfiguration, mode, locator);

		// fConfig = launchConfiguration;
		fExecutor = (DefaultDsfExecutor) getDsfExecutor();
		fSession = getSession();
	}

	@SuppressWarnings("unused")
	private DsfServicesTracker getTracker() {

		if (fTracker == null)
			fTracker = new DsfServicesTracker(Activator.getInstance().getBundle().getBundleContext(), fSession.getId());

		return fTracker;
	}

	@Override
	public void initialize() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmLaunch.initialize() " + this);
		}

		ILaunchConfigurationWorkingCopy config;
		try {
			config = getLaunchConfiguration().getWorkingCopy();
			provideDefaults(config);
			config.doSave();
		} catch (CoreException e) {
			Activator.log(e);
		}

		try {
			// In CDT 9.0 super.initialize started raising a checked exception,
			// but because we are trying to support both pre and post 9.0 API
			// we can't throw the exception here. So we log it instead. The
			// exception case is that the tracker cannot be created, which
			// is very improbable and the sign that things are very broken.
			super.initialize();

			// Used to get the tracker
			Runnable initRunnable = new DsfRunnable() {
				@Override
				public void run() {
					fTracker = new DsfServicesTracker(Activator.getInstance().getBundle().getBundleContext(),
							fSession.getId());
				}
			};

			// Invoke the execution code and block waiting for the result.
			fExecutor.submit(initRunnable).get();
		} catch (Exception e) {
			Activator.log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, IDsfStatusConstants.INTERNAL_ERROR,
					"Error initializing launch", e)); //$NON-NLS-1$
		}
	}

	/**
	 * Define some of the mandatory CDT attributes with default values, in case
	 * the launch configuration was generated externally, for example by a
	 * wizard.
	 *
	 * @param config
	 *            a writable copy of the launch configuration.
	 * @throws CoreException
	 */
	protected void provideDefaults(ILaunchConfigurationWorkingCopy config) throws CoreException {

		if (!config.hasAttribute(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE)) {
			config.setAttribute(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE, true);
		}
		if (!config.hasAttribute(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS)) {
			config.setAttribute(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS, true);
		}
		if (!config.hasAttribute(IGDBJtagConstants.ATTR_USE_REMOTE_TARGET)) {
			config.setAttribute(IGDBJtagConstants.ATTR_USE_REMOTE_TARGET, true);
		}
	}

	@Override
	public void shutdownSession(final RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmLaunch.shutdownSession() " + this);
		}
		super.shutdownSession(rm);
	}

	/**
	 * Initialise the memory block retrieval required by the Peripherals view.
	 */
	@Override
	public void initializeControl() throws CoreException {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmLaunch.initializeControl()");
		}

		// The parent defines and register GdbMemoryBlockRetrievalManager
		// but we do not want this.
		// super.initializeControl();

		// Create a memory retrieval manager and register it with the session
		// to maintain a mapping of memory contexts to the corresponding memory
		// retrieval in this session.
		// The manager is called to create a memory retrieval on session START.
		try {
			fExecutor.submit(new Callable<Object>() {
				@Override
				public Object call() throws CoreException {
					fMemRetrievalManager = new GdbArmMemoryBlockRetrievalManager(GdbLaunchDelegate.GDB_DEBUG_MODEL_ID,
							getLaunchConfiguration(), fSession);
					fSession.registerModelAdapter(IMemoryBlockRetrievalManager.class, fMemRetrievalManager);
					fSession.addServiceEventListener(fMemRetrievalManager, null);
					return null;
				}
			}).get();
		} catch (InterruptedException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0,
					"Interrupted while waiting for get process callable.", e)); //$NON-NLS-1$
		} catch (ExecutionException e) {
			throw (CoreException) e.getCause();
		} catch (RejectedExecutionException e) {
			throw new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0,
					"Debugger shut down before launch was completed.", e)); //$NON-NLS-1$
		}
	}

	/**
	 * Identical to addCLIProcess(), just that it returns the process, so we can
	 * add properties to it later.
	 *
	 * @param label
	 * @return the IProcess created.
	 * @throws CoreException
	 */
	@ThreadSafeAndProhibitedFromDsfExecutor("getDsfExecutor()")
	public IProcess addClientProcess(String label) throws CoreException {
		IProcess newProcess = null;
		try {
			// Add the CLI process object to the launch.
			AbstractCLIProcess cliProc = getDsfExecutor().submit(new Callable<AbstractCLIProcess>() {
				@Override
				public AbstractCLIProcess call() throws CoreException {
					IGDBControl gdb = fTracker.getService(IGDBControl.class);
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
			attributes.put(IGdbDebugConstants.PROCESS_TYPE_CREATION_ATTR,
					IGdbDebugConstants.GDB_PROCESS_CREATION_VALUE);
			newProcess = DebugPlugin.newProcess(this, cliProc, label, attributes);
		} catch (InterruptedException e) {
			throw new CoreException(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, 0,
					"Interrupted while waiting for get process callable.", e)); //$NON-NLS-1$
		} catch (ExecutionException e) {
			throw (CoreException) e.getCause();
		} catch (RejectedExecutionException e) {
			throw new CoreException(new Status(IStatus.ERROR, GdbPlugin.PLUGIN_ID, 0,
					"Debugger shut down before launch was completed.", e)); //$NON-NLS-1$
		}

		return newProcess;
	}

	// ------------------------------------------------------------------------
}
