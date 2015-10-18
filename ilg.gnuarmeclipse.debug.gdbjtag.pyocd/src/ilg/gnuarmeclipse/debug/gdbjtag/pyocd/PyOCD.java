/*******************************************************************************
 * Copyright (c) 2015 Chris Reed.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Chris Reed - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.pyocd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.cdt.dsf.gdb.launching.LaunchUtils;
import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;

import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;

/**
 * Utilities for managing pyOCD.
 *
 */
public class PyOCD {
	
	/**
	 * Info about an available board.
	 *
	 */
	public class Board {
		public String fName;
		public String fTargetName;
		public String fDescription;
		public String fUniqueId;
	}
	
	/**
	 * Info about a target supported by pyOCD.
	 *
	 */
	public class Target {
		public String fName; 
		public String fPartNumber;
		public String fSvdPath;
	}
	
	public static Board[] getBoards(ILaunchConfiguration configuration)
			throws CoreException {
		
		String pyOCDPath = Configuration.getGdbServerCommand(configuration);
		if (pyOCDPath == null) {
			return null;
		}
		return getBoards(pyOCDPath);
	}
	
	public static Target[] getTargets(ILaunchConfiguration configuration)
			throws CoreException {
		
		String pyOCDPath = Configuration.getGdbServerCommand(configuration);
		if (pyOCDPath == null) {
			return null;
		}
		return getTargets(pyOCDPath);
	}
	
	public static Board[] getBoards(String pyOCDPath)
			throws CoreException {
		
		String output = getOutput(pyOCDPath, "--list");
		System.out.printf("pyOCD boards = %s\n", output);
		
		return null;
	}
	
	public static Target[] getTargets(String pyOCDPath)
			throws CoreException {
		
		String output = getOutput(pyOCDPath, "--list-targets");
		System.out.printf("pyOCD targets = %s\n", output);
		
		return null;
	}
	
	private static String getOutput(final String pyOCDPath, String listArg)
			throws CoreException {

		String[] cmdArray = new String[3];
		cmdArray[0] = pyOCDPath;
		cmdArray[1] = "--json";
		cmdArray[2] = listArg;

		final Process process;
		try {
			process = ProcessFactory.getFactory().exec(cmdArray); //,
//					DebugUtils.getLaunchEnvironment(configuration));
		} catch (IOException e) {
			throw new DebugException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Error while launching command: "
							+ StringUtils.join(cmdArray, " "), e.getCause()));//$NON-NLS-1$
		}

		// Start a timeout job to make sure we don't get stuck waiting for
		// an answer from a gdb that is hanging
		// Bug 376203
		Job timeoutJob = new Job("pyOCD output timeout job") { //$NON-NLS-1$
			{
				setSystem(true);
			}

			@Override
			protected IStatus run(IProgressMonitor arg) {
				// Took too long. Kill the gdb process and
				// let things clean up.
				process.destroy();
				return Status.OK_STATUS;
			}
		};
		timeoutJob.schedule(10000);

		InputStream stream = null;
		StringBuilder cmdOutput = new StringBuilder(200);
		try {
			stream = process.getInputStream();
			Reader r = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(r);

			String line;
			while ((line = reader.readLine()) != null) {
				cmdOutput.append(line);
				cmdOutput.append('\n');
			}
		} catch (IOException e) {
			throw new DebugException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Error reading pyOCD stdout after sending: "
							+ StringUtils.join(cmdArray, " ") + ", response: "
							+ cmdOutput, e.getCause()));//$NON-NLS-1$
		} finally {
			// If we get here we are obviously not stuck so we can cancel the
			// timeout job.
			// Note that it may already have executed, but that is not a
			// problem.
			timeoutJob.cancel();

			// Cleanup to avoid leaking pipes
			// Close the stream we used, and then destroy the process
			// Bug 345164
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
			process.destroy();
		}
		
		return cmdOutput.toString();

//		String gdbVersion = LaunchUtils.getGDBVersionFromText(cmdOutput
//				.toString());
//		if (gdbVersion == null || gdbVersion.isEmpty()) {
//			throw new DebugException(new Status(IStatus.ERROR,
//					Activator.PLUGIN_ID, DebugException.REQUEST_FAILED,
//					"Could not determine GDB version after sending: "
//							+ StringUtils.join(cmdArray, " ") + ", response: "
//							+ cmdOutput, null));//$NON-NLS-1$
//		}
//		return gdbVersion;
	}


}
