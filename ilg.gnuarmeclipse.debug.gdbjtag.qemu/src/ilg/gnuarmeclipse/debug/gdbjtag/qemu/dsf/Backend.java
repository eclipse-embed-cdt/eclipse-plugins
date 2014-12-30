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

package ilg.gnuarmeclipse.debug.gdbjtag.qemu.dsf;

import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmGdbBackend;
import ilg.gnuarmeclipse.debug.gdbjtag.qemu.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.qemu.Configuration;

import java.io.File;
import java.io.IOException;

import org.eclipse.cdt.core.parser.util.StringUtil;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * The Kepler CDT GDBBackend does not allow such a simple customisation, we had
 * to copy a newer version locally and use it.
 */

public class Backend extends GnuArmGdbBackend {

	// ------------------------------------------------------------------------

	private final ILaunchConfiguration fLaunchConfiguration;

	// ------------------------------------------------------------------------

	public Backend(DsfSession session, ILaunchConfiguration lc) {

		super(session, lc);

		System.out.println("Backend() " + this);
		fLaunchConfiguration = lc;
	}

	// ------------------------------------------------------------------------

	@Override
	public void initialize(final RequestMonitor rm) {

		System.out.println("Backend.initialize() " + Thread.currentThread());

		super.initialize(rm);
	}

	@Override
	public void destroy() {

		System.out.println("Backend.destroy() " + Thread.currentThread());
		super.destroy();
	}

	@Override
	public void shutdown(final RequestMonitor rm) {

		System.out.println("Backend.shutdown() " + Thread.currentThread());
		super.shutdown(rm);
	}

	// ------------------------------------------------------------------------

	/**
	 * Overridden to get the full command line, including all options, from the
	 * JLink configuration.
	 */
	protected String[] getGDBCommandLineArray() {
		String[] commandLineArray = Configuration
				.getGdbClientCommandLineArray(fLaunchConfiguration);

		return commandLineArray;
	}

	/**
	 * Overridden to use our own launch environment and to add the working
	 * directory to exec(), although it is anyway set in a separate step
	 * (stepSetEnvironmentDirector in FinalLaunchSequence).
	 */
	@Override
	protected Process launchGDBProcess(String[] commandLine)
			throws CoreException {
		Process proc = null;
		File dir = null;
		IPath path = getGDBWorkingDirectory();
		if (path != null) {
			dir = new File(path.toOSString());
		}

		System.out.println("exec " + StringUtils.join(commandLine, " "));
		System.out.println("dir " + dir);
		try {
			proc = ProcessFactory.getFactory().exec(commandLine,
					DebugUtils.getLaunchEnvironment(fLaunchConfiguration), dir);
		} catch (IOException e) {
			String message = "Error while launching command: "
					+ StringUtil.join(commandLine, " "); //$NON-NLS-1$ //$NON-NLS-2$
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, -1, message, e));
		}

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

		System.out.println("getGDBWorkingDirectory() " + path);
		return path;
	}

	// ------------------------------------------------------------------------
}
