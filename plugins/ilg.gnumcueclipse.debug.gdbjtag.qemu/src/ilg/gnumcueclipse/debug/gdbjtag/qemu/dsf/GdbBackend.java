/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.qemu.dsf;

import java.io.File;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;

import ilg.gnumcueclipse.debug.gdbjtag.DebugUtils;
import ilg.gnumcueclipse.debug.gdbjtag.dsf.GnuMcuGdbBackend;
import ilg.gnumcueclipse.debug.gdbjtag.qemu.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.qemu.Configuration;

/**
 * The Kepler CDT GDBBackend does not allow such a simple customisation, we had
 * to copy a newer version locally and use it.
 */

public class GdbBackend extends GnuMcuGdbBackend {

	// ------------------------------------------------------------------------

	private final ILaunchConfiguration fLaunchConfiguration;

	// ------------------------------------------------------------------------

	public GdbBackend(DsfSession session, ILaunchConfiguration lc) {

		super(session, lc);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.GdbBackend() " + this);
		}
		fLaunchConfiguration = lc;
	}

	// ------------------------------------------------------------------------

	@Override
	public void initialize(final RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.GdbBackend.initialize() " + Thread.currentThread());
		}
		super.initialize(rm);
	}

	@Override
	public void destroy() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.GdbBackend.destroy() " + Thread.currentThread());
		}
		super.destroy();
	}

	@Override
	public void shutdown(final RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.GdbBackend.shutdown() " + Thread.currentThread());
		}
		super.shutdown(rm);
	}

	// ------------------------------------------------------------------------

	/**
	 * Overridden to get the full command line, including all options, from the
	 * QEMU configuration.
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

		Process proc = DebugUtils.exec(commandLineArray, DebugUtils.getLaunchEnvironment(fLaunchConfiguration), dir);

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
			System.out.println("qemu.GdbBackend.getGDBWorkingDirectory() " + path);
		}
		return path;
	}

	// ------------------------------------------------------------------------
}
