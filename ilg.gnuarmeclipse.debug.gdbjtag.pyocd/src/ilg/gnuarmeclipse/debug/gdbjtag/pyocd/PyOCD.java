/*******************************************************************************
 * Copyright (c) 2015-2016 Chris Reed.
 * Copyright (c) 2016 John Cortell.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Chris Reed - initial version
 *     John Cortell - cleanup and fixes
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.pyocd;

import ilg.gnuarmeclipse.core.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Utilities for managing pyOCD.
 *
 */
public class PyOCD {

	/// Latest version of the JSON data format returned by pyOCD.
	public static final long FORMAT_MAJOR_VERSION = 1;

	// Dictionary keys for JSON data.
	public static final String VERSION_KEY = "version";
	public static final String VERSION_MAJOR_KEY = "major";
	public static final String VERSION_MINOR_KEY = "minor";
	public static final String STATUS_KEY = "status";
	public static final String ERROR_KEY = "error";
	public static final String BOARDS_KEY = "boards";
	public static final String TARGETS_KEY = "targets";

	public static final String BOARD_INFO_KEY = "info";
	public static final String BOARD_NAME_KEY = "board_name";
	public static final String BOARD_VENDOR_NAME_KEY = "vendor_name";
	public static final String BOARD_PRODUCT_NAME_KEY = "product_name";
	public static final String BOARD_TARGET_KEY = "target";
	public static final String BOARD_UNIQUE_ID_KEY = "unique_id";

	public static final String TARGET_NAME_KEY = "name";
	public static final String TARGET_PART_NUMBER_KEY = "part_number";
	public static final String TARGET_SVD_PATH_KEY = "svd_path";

	/**
	 * Info about an available board.
	 *
	 */
	public static class Board {
		public String fName;
		public String fVendorName;
		public String fProductName;
		public String fTargetName;
		public String fDescription;
		public String fUniqueId;

		public static final Comparator COMPARATOR = new Comparator();

		/**
		 * Comparator to sort boards by name.
		 */
		private static class Comparator implements java.util.Comparator<Board> {
			public int compare(Board o1, Board o2) {
				return o1.fName.compareTo(o2.fName);
			}
		}

		@Override
		public String toString() {
			return String.format("<Board: %s [%s] %s>", fName, fTargetName, fUniqueId);
		}
	}

	/**
	 * Info about a target supported by pyOCD.
	 *
	 */
	public static class Target {
		public String fName;
		public String fPartNumber;
		public String fSvdPath;

		public static final Comparator COMPARATOR = new Comparator();

		/**
		 * Comparator to sort targets by name.
		 */
		public static class Comparator implements java.util.Comparator<Target> {
			public int compare(Target o1, Target o2) {
				return o1.fName.compareTo(o2.fName);
			}
		}

		@Override
		public String toString() {
			return String.format("<Target: %s [%s]>", fName, fPartNumber);
		}
	}

	public static List<Board> getBoards(ILaunchConfiguration configuration) {

		String pyOCDPath = Configuration.getGdbServerCommand(configuration);
		if (pyOCDPath == null) {
			return null;
		}
		return getBoards(pyOCDPath);
	}

	public static List<Target> getTargets(ILaunchConfiguration configuration) {

		String pyOCDPath = Configuration.getGdbServerCommand(configuration);
		if (pyOCDPath == null) {
			return null;
		}
		return getTargets(pyOCDPath);
	}

	private static boolean checkOutput(JSONObject output) {
		// Make sure we even have valid output.
		if (output == null) {
			return false;
		}

		// Check version
		if (!output.containsKey(VERSION_KEY)) {
			if (Activator.getInstance().isDebugging()) {
				System.out.printf("No data format version from pyOCD\n");
			}
			return false;
		}

		JSONObject version = (JSONObject) output.get(VERSION_KEY);
		if (!version.containsKey(VERSION_MAJOR_KEY)) {
			if (Activator.getInstance().isDebugging()) {
				System.out.printf("No data format major version from pyOCD\n");
			}
			return false;
		}
		if (!version.get(VERSION_MAJOR_KEY).equals(Long.valueOf(FORMAT_MAJOR_VERSION))) {
			if (Activator.getInstance().isDebugging()) {
				System.out.printf("Unsupported version %d of data from pyOCD\n", version.get(VERSION_MAJOR_KEY));
			}
			return false;
		}

		// Check status
		if (!output.containsKey(STATUS_KEY) || !output.get(STATUS_KEY).equals(Long.valueOf(0))) {
			if (Activator.getInstance().isDebugging()) {
				String msg = "unknown error";
				if (output.containsKey(ERROR_KEY)) {
					msg = (String) output.get(ERROR_KEY);
				}
				System.out.printf("Error %d reading from pyOCD: %s\n", output.get(STATUS_KEY), msg);
			}
			return false;
		}

		return true;
	}

	public static List<Board> getBoards(String pyOCDPath) {

		JSONObject output = getJsonOutput(pyOCDPath, "--list");
		// System.out.printf("pyOCD boards = %s\n", output);

		if (!checkOutput(output)) {
			return null;
		}

		if (!output.containsKey(BOARDS_KEY)) {
			return null;
		}

		Object boardsObj = output.get(BOARDS_KEY);
		if (!(boardsObj instanceof JSONArray)) {
			return null;
		}

		JSONArray boards = (JSONArray) boardsObj;

		ArrayList<Board> result = new ArrayList<Board>();
		for (Object b : boards) {
			try {
				JSONObject bobj = (JSONObject) b;

				Board boardInfo = new Board();
				boardInfo.fDescription = (String) bobj.get(BOARD_INFO_KEY);
				boardInfo.fName = (String) bobj.get(BOARD_NAME_KEY);
				boardInfo.fVendorName = (String) bobj.get(BOARD_VENDOR_NAME_KEY);
				boardInfo.fProductName = (String) bobj.get(BOARD_PRODUCT_NAME_KEY);
				boardInfo.fTargetName = (String) bobj.get(BOARD_TARGET_KEY);
				boardInfo.fUniqueId = (String) bobj.get(BOARD_UNIQUE_ID_KEY);

				result.add(boardInfo);
			} catch (Exception e) {
				continue;
			}
		}

		return result;
	}

	public static List<Target> getTargets(String pyOCDPath) {

		JSONObject output = getJsonOutput(pyOCDPath, "--list-targets");
		// System.out.printf("pyOCD targets = %s\n", output);

		if (!checkOutput(output)) {
			return null;
		}

		if (!output.containsKey(TARGETS_KEY)) {
			return null;
		}

		Object targetsObj = output.get(TARGETS_KEY);
		if (!(targetsObj instanceof JSONArray)) {
			return null;
		}

		JSONArray targets = (JSONArray) targetsObj;

		ArrayList<Target> result = new ArrayList<Target>();
		for (Object t : targets) {
			try {
				JSONObject tobj = (JSONObject) t;

				Target targetInfo = new Target();
				targetInfo.fName = (String) tobj.get(TARGET_NAME_KEY);
				targetInfo.fPartNumber = (String) tobj.get(TARGET_PART_NUMBER_KEY);
				targetInfo.fSvdPath = (String) tobj.get(TARGET_SVD_PATH_KEY);

				result.add(targetInfo);
			} catch (Exception e) {
				continue;
			}
		}

		return result;
	}

	private static JSONObject getJsonOutput(final String pyOCDPath, String listArg) {
		try {
			String result = getOutput(pyOCDPath, listArg);
			JSONParser parser = new JSONParser();
			Object obj = parser.parse(result);
			return (JSONObject) obj;
		} catch (ParseException e) {
			if (Activator.getInstance().isDebugging()) {
				System.out.printf("Parse exception: %s\n", e);
			}
			return null;
		} catch (CoreException e) {
			if (Activator.getInstance().isDebugging()) {
				System.out.printf("Core exception: %s\n", e);
			}
			return null;
		}
	}

	private static String getOutput(final String pyOCDPath, String listArg) throws CoreException {

		String[] cmdArray = new String[3];
		cmdArray[0] = pyOCDPath;
		cmdArray[1] = "--json";
		cmdArray[2] = listArg;

		final Process process;
		try {
			process = ProcessFactory.getFactory().exec(cmdArray);
		} catch (IOException e) {
			throw new DebugException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Error while launching command: " + StringUtils.join(cmdArray, " "), e.getCause()));//$NON-NLS-2$
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
			throw new DebugException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Error reading pyOCD stdout after sending: " + StringUtils.join(cmdArray, " ") + ", response: "
							+ cmdOutput,
					e.getCause()));// $NON-NLS-1$
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
	}

}
