/*******************************************************************************
 * Copyright (c) 2016 Liviu Ionescu.
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

import java.util.concurrent.ExecutionException;

import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFLaunchConfigurationDelegate;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Query;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;

/**
 * @since 3.0
 */
public abstract class AbstractGnuArmLaunchConfigurationDelegate extends GDBJtagDSFLaunchConfigurationDelegate {

	/**
	 * @deprecated While waiting to drop support for CDT < 9.0 we need this
	 *             method copied from CDT 9.0. Once support for old CDT is
	 *             removed this should become simply
	 *             super.cleanupLaunch(ILaunch).
	 */
	@Deprecated
	protected void cleanupLaunchLocal(ILaunch launch) throws DebugException {
		if (launch instanceof GdbLaunch) {
			final GdbLaunch gdbLaunch = (GdbLaunch) launch;
			Query<Object> launchShutdownQuery = new Query<Object>() {
				@Override
				protected void execute(DataRequestMonitor<Object> rm) {
					gdbLaunch.shutdownSession(rm);
				}
			};

			gdbLaunch.getSession().getExecutor().execute(launchShutdownQuery);

			// wait for the shutdown to finish.
			// The Query.get() method is a synchronous call which blocks until
			// the query completes.
			try {
				launchShutdownQuery.get();
			} catch (InterruptedException e) {
				throw new DebugException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, DebugException.INTERNAL_ERROR,
						"InterruptedException while shutting down debugger launch " + launch, e)); //$NON-NLS-1$
			} catch (ExecutionException e) {
				throw new DebugException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, DebugException.REQUEST_FAILED,
						"Error in shutting down debugger launch " + launch, e)); //$NON-NLS-1$
			}
		}

	}

	// This is the first method to be called in the launch sequence, even before
	// preLaunchCheck()
	// If we cancel the launch, we need to cleanup what is allocated in this
	// method. The cleanup
	// can be performed by GdbLaunch.shutdownSession()
	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		GdbLaunch launch = createGdbLaunch(configuration, mode, null);
		// Don't initialize the GdbLaunch yet to avoid needing to cleanup.
		// We will initialize the launch once we know it will proceed and
		// that we need to start using it.

		// Need to configure the source locator before returning the launch
		// because once the launch is created and added to the launch manager,
		// the adapters will be created for the whole session, including
		// the source lookup adapter.
		launch.setSourceLocator(getSourceLocator(configuration, launch.getSession()));
		return launch;
	}

}
