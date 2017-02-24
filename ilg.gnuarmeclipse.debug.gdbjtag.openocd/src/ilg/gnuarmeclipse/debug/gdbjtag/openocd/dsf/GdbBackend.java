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

import java.io.File;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;

import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmGdbBackend;
import ilg.gnuarmeclipse.debug.gdbjtag.openocd.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.openocd.Configuration;

/**
 * The Kepler CDT GDBBackend does not allow such a simple customisation, we had
 * to copy a newer version locally and use it.
 */

public class GdbBackend extends GnuArmGdbBackend {

	// ------------------------------------------------------------------------

	private final ILaunchConfiguration fLaunchConfiguration;

	// ------------------------------------------------------------------------

	public GdbBackend(DsfSession session, ILaunchConfiguration lc) {

		super(session, lc);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GdbBackend() " + this);
		}
		fLaunchConfiguration = lc;
	}

	// ------------------------------------------------------------------------

	@Override
	public void initialize(final RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GdbBackend.initialize() " + Thread.currentThread());
		}

		super.initialize(rm);
	}

	@Override
	public void destroy() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GdbBackend.destroy() " + Thread.currentThread());
		}
		super.destroy();
	}

	@Override
	public void shutdown(final RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GdbBackend.shutdown() " + Thread.currentThread());
		}
		super.shutdown(rm);
	}

	// ------------------------------------------------------------------------

	/**
	 * Overridden to get the full command line, including all options, from the
	 * OpenOCD configuration.
	 */
	protected String[] getGDBCommandLineArray() {
		String[] commandLineArray = Configuration.getGdbClientCommandLineArray(fLaunchConfiguration);

		return commandLineArray;
	}

	/**
	 * Overridden to use our own launch environment and to add the working
	 * directory to exec(), although it is anyway set in a separate step
	 * (stepSetEnvironmentDirector in FinalLaunchSequence).
	 */
	@Override
	protected Process launchGDBProcess(String[] commandLineArray) throws CoreException {
		File dir = null;
		IPath path = getGDBWorkingDirectory();
		if (path != null) {
			dir = new File(path.toOSString());
		}

		Process proc = DebugUtils.exec(commandLineArray, DebugUtils.getLaunchEnvironment(fLaunchConfiguration),
				dir);

		return proc;
	}

	/**
	 * Overridden to also try getProjectOsPath(), if getGDBWorkingDirectory() is
	 * not defined.
	 * 
	 * May return null.
	 */
	@Override
	public IPath getGDBWorkingDirectory() throws CoreException {

		IPath path;
		try {
			path = super.getGDBWorkingDirectory();
		} catch (CoreException e) {
			path = null;
		}

		if (path == null) {
			path = DebugUtils.getProjectOsPath(fLaunchConfiguration);
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("getGDBWorkingDirectory() " + path);
		}
		return path;
	}

	// ------------------------------------------------------------------------
}
