/*******************************************************************************
 * Copyright (c) 2015-2020 Chris Reed.
 * Copyright (c) 2016 John Cortell.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Chris Reed - initial version
 *     John Cortell - cleanup and fixes
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.pyocd.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DefaultDsfExecutor;
import org.eclipse.cdt.dsf.concurrent.DsfRunnable;
import org.eclipse.cdt.dsf.concurrent.IDsfStatusConstants;
import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.embedcdt.core.StringUtils;
import org.eclipse.embedcdt.core.SystemJob;
import org.eclipse.embedcdt.internal.debug.gdbjtag.pyocd.core.Activator;
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
	public static final String PROBES_KEY = "boards"; // Yes, this is correct!
	public static final String TARGETS_KEY = "targets";

	public static final String PROBE_INFO_KEY = "info";
	public static final String PROBE_BOARD_NAME_KEY = "board_name";
	public static final String PROBE_VENDOR_NAME_KEY = "vendor_name";
	public static final String PROBE_PRODUCT_NAME_KEY = "product_name";
	public static final String PROBE_TARGET_KEY = "target";
	public static final String PROBE_UNIQUE_ID_KEY = "unique_id";

	public static final String TARGET_NAME_KEY = "name";
	public static final String TARGET_VENDOR_KEY = "vendor";
	public static final String TARGET_FAMILIES_KEY = "part_families";
	public static final String TARGET_PART_NUMBER_KEY = "part_number";
	public static final String TARGET_SVD_PATH_KEY = "svd_path";

	// 60 second timeout for calling pyocd.
	public static final long PYOCD_TIMEOUT_MS = 60000;

	public static final class Errors {
		public static final int ERROR_PARSING_OUTPUT = 1;
		public static final int ERROR_RUNNING_PYOCD = 2;
		public static final int ERROR_TIMEOUT = 3;
		public static final int ERROR_INVALID_JSON_FORMAT = 4;
	}

	private static PyOCD fInstance;
	private DefaultDsfExecutor fExecutor;

	static {
		fInstance = new PyOCD();
	}

	public static PyOCD getInstance() {
		return fInstance;
	}

	/**
	 * Info about an available probe.
	 *
	 */
	public static class Probe {
		public final String fBoardName;
		public final String fVendorName;
		public final String fProductName;
		public final String fTargetName;
		public final String fUniqueId;

		//! The board name pyocd returns if the probe doesn't report that it is associated with a board.
		public static final String GENERIC_BOARD_NAME = "generic";

		//! The board name pyocd returns if the probe reports a board ID but pyocd doesn't recognize it.
		public static final String UNKNOWN_BOARD_NAME = "Unknown Board";

		public static final Comparator DESCRIPTION_COMPARATOR = new Comparator();

		Probe(String board, String vendor, String product, String target, String uid) {
			fBoardName = board;
			fVendorName = vendor;
			fProductName = product;
			fTargetName = target;
			fUniqueId = uid;
		}

		/**
		 * Return the combined vendor and product name.
		 *
		 * If the product name includes the vendor name as a prefix, as is the case with some probes,
		 * then only the product name is returned so there is not a double vendor name.
		 */
		public String getName() {
			if (fProductName.startsWith(fVendorName)) {
				return fProductName;
			} else {
				return fVendorName + " " + fProductName;
			}
		}

		/**
		 * Return a description of the probe.
		 */
		public String getDescription() {
			// PyOCD will set the board name to "generic" if the probe doesn't report that
			// it is associated with a board.
			if (fBoardName.equalsIgnoreCase(GENERIC_BOARD_NAME) || fBoardName.equalsIgnoreCase(UNKNOWN_BOARD_NAME)) {
				return String.format("%s (%s)", getName(), fUniqueId);
			} else {
				return String.format("%s – %s (%s)", fBoardName, getName(), fUniqueId);
			}
		}

		/**
		 * Comparator to sort boards by description.
		 */
		private static class Comparator implements java.util.Comparator<Probe> {
			@Override
			public int compare(Probe o1, Probe o2) {
				return o1.getDescription().compareTo(o2.getDescription());
			}
		}

		@Override
		public String toString() {
			return String.format("<Probe: %s [%s] %s>", getDescription(), fTargetName, fUniqueId);
		}
	}

	/**
	 * Info about a target supported by pyOCD.
	 *
	 */
	public static class Target {
		public final String fName;
		public final String fVendor;
		public final String fPartNumber;
		public final String fFamilies[];

		public static final NameComparator NAME_COMPARATOR = new NameComparator();
		public static final PartNumberComparator PART_NUMBER_COMPARATOR = new PartNumberComparator();

		Target(String name, String vendor, String part, String[] families) {
			fName = name;
			fVendor = vendor;
			fPartNumber = part;
			fFamilies = families;
		}

		public String getFullPartName() {
			if (fVendor != null) {
				return fVendor + " " + fPartNumber;
			} else {
				return fPartNumber;
			}
		}

		/**
		 * Comparator to sort targets by name.
		 */
		public static class NameComparator implements java.util.Comparator<Target> {
			@Override
			public int compare(Target o1, Target o2) {
				return o1.fName.compareTo(o2.fName);
			}
		}

		/**
		 * Comparator to sort targets by part number.
		 */
		public static class PartNumberComparator implements java.util.Comparator<Target> {
			@Override
			public int compare(Target o1, Target o2) {
				return o1.getFullPartName().compareTo(o2.getFullPartName());
			}
		}

		@Override
		public String toString() {
			return String.format("<Target: %s %s [%s]>", fVendor, fPartNumber, fName);
		}
	}

	/**
	 * pyOCD version number.
	 *
	 */
	public static class Version {
		public final int fMajor;
		public final int fMinor;
		public final int fMicro;

		Version(int major, int minor, int micro) {
			fMajor = major;
			fMinor = minor;
			fMicro = micro;
		}

		/**
		 * Parse the given version string, returning a Version object or null.
		 */
		public static Version fromString(String versionString) {
			if (versionString.isEmpty()) {
				return null;
			}
			// remove initial 'v' if present
			if (versionString.startsWith("v")) {
				versionString = versionString.substring(1);
			}
			String[] pieces = versionString.split("\\.", 4);

			if (pieces.length == 0) {
				return null;
			}

			int major = 0;
			int minor = 0;
			int micro = 0;
			try {
				major = Integer.parseUnsignedInt(pieces[0]);
			} catch (NumberFormatException e) {
				if (Activator.getInstance().isDebugging()) {
					System.out.printf("failed to parse pyocd major version:" + String.join(".", pieces) + "\n");
				}
			}
			try {
				minor = Integer.parseUnsignedInt(pieces[1]);
			} catch (NumberFormatException e) {
				if (Activator.getInstance().isDebugging()) {
					System.out.printf("failed to parse pyocd minor version:" + String.join(".", pieces) + "\n");
				}
			}
			try {
				micro = Integer.parseUnsignedInt(pieces[2]);
			} catch (NumberFormatException e) {
				if (Activator.getInstance().isDebugging()) {
					System.out.printf("failed to parse pyocd micro version:" + String.join(".", pieces) + "\n");
				}
			}

			return new Version(major, minor, micro);
		}

		@Override
		public String toString() {
			return String.format("<Version: %d.%d.%d>", fMajor, fMinor, fMicro);
		}
	}

	public PyOCD() {
		fExecutor = new DefaultDsfExecutor();
	}

	public void getProbes(ILaunchConfiguration configuration, final DataRequestMonitor<List<Probe>> rm) {

		String pyOCDPath = Configuration.getGdbServerCommand(configuration, null);
		if (pyOCDPath == null) {
			rm.setStatus(
					new Status(IStatus.ERROR, PyOCD.class, IDsfStatusConstants.REQUEST_FAILED, "no pyocd path", null));
			rm.done();
		} else {
			getProbes(pyOCDPath, rm);
		}
	}

	public void getTargets(ILaunchConfiguration configuration, final DataRequestMonitor<List<Target>> rm) {

		String pyOCDPath = Configuration.getGdbServerCommand(configuration, null);
		if (pyOCDPath == null) {
			rm.setStatus(
					new Status(IStatus.ERROR, PyOCD.class, IDsfStatusConstants.REQUEST_FAILED, "no pyocd path", null));
			rm.done();
		} else {
			getTargets(pyOCDPath, rm);
		}
	}

	private boolean checkOutput(JSONObject output) {
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

	public void getProbes(String pyOCDPath, final DataRequestMonitor<List<Probe>> rm) {

		getJsonOutput(pyOCDPath, "--probes", new DataRequestMonitor<JSONObject>(fExecutor, rm) {
			@Override
			protected void handleSuccess() {
				JSONObject output = getData();

				if (!(checkOutput(output) && output.containsKey(PROBES_KEY))) {
					rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Errors.ERROR_INVALID_JSON_FORMAT,
							"invalid output", null));
					rm.done();
					return;
				}

				Object probesObj = output.get(PROBES_KEY);
				if (!(probesObj instanceof JSONArray)) {
					rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Errors.ERROR_INVALID_JSON_FORMAT,
							"invalid probes key type", null));
					rm.done();
					return;
				}

				JSONArray probes = (JSONArray) probesObj;

				ArrayList<Probe> result = new ArrayList<>();
				for (Object b : probes) {
					try {
						JSONObject bobj = (JSONObject) b;

						Probe probeInfo = new Probe((String) bobj.get(PROBE_BOARD_NAME_KEY),
								(String) bobj.get(PROBE_VENDOR_NAME_KEY), (String) bobj.get(PROBE_PRODUCT_NAME_KEY),
								(String) bobj.get(PROBE_TARGET_KEY), (String) bobj.get(PROBE_UNIQUE_ID_KEY));

						result.add(probeInfo);
					} catch (Exception e) {
						if (Activator.getInstance().isDebugging()) {
							System.out.printf("Exception extracting probe info: %s\n", e);
						}
						continue;
					}
				}

				rm.done(result);
			}
		});
	}

	public void getTargets(String pyOCDPath, final DataRequestMonitor<List<Target>> rm) {

		getJsonOutput(pyOCDPath, "--targets", new DataRequestMonitor<JSONObject>(fExecutor, rm) {
			@Override
			protected void handleSuccess() {
				JSONObject output = getData();

				if (!(checkOutput(output) && output.containsKey(TARGETS_KEY))) {
					rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Errors.ERROR_INVALID_JSON_FORMAT,
							"invalid output", null));
					rm.done();
					return;
				}

				Object targetsObj = output.get(TARGETS_KEY);
				if (!(targetsObj instanceof JSONArray)) {
					rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Errors.ERROR_INVALID_JSON_FORMAT,
							"invalid targets key type", null));
					rm.done();
					return;
				}

				JSONArray targets = (JSONArray) targetsObj;

				ArrayList<Target> result = new ArrayList<>();
				for (Object t : targets) {
					try {
						JSONObject tobj = (JSONObject) t;

						String families[];
						if (tobj.containsKey(TARGET_FAMILIES_KEY)) {
							JSONArray familiesJsonArray = (JSONArray) tobj.get(TARGET_FAMILIES_KEY);
							ArrayList<String> familiesArrayList = new ArrayList<>();
							for (Object f : familiesJsonArray) {
								familiesArrayList.add((String) f);
							}
							families = familiesArrayList.toArray(new String[familiesJsonArray.size()]);
						} else {
							families = new String[] {};
						}

						Target targetInfo = new Target((String) tobj.get(TARGET_NAME_KEY),
								(String) tobj.get(TARGET_VENDOR_KEY), (String) tobj.get(TARGET_PART_NUMBER_KEY),
								families);

						result.add(targetInfo);
					} catch (Exception e) {
						if (Activator.getInstance().isDebugging()) {
							System.out.printf("Exception extracting target info: %s\n", e);
						}
						continue;
					}
				}

				rm.done(result);
			}
		});
	}

	private void getJsonOutput(final String pyOCDPath, String listArg, final DataRequestMonitor<JSONObject> rm) {
		fExecutor.execute(new DsfRunnable() {
			@Override
			public void run() {
				try {
					String[] cmdArray = new String[3];
					cmdArray[0] = pyOCDPath;
					cmdArray[1] = "json";
					cmdArray[2] = listArg;

					String result = getOutput(cmdArray);
					JSONParser parser = new JSONParser();
					JSONObject obj = (JSONObject) parser.parse(result);
					rm.done(obj);
				} catch (ParseException e) {
					if (Activator.getInstance().isDebugging()) {
						System.out.printf("Parse exception: %s\n", e);
					}
					rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Errors.ERROR_PARSING_OUTPUT,
							"error parsing pyocd JSON output", e));
					rm.done();
				} catch (CoreException e) {
					if (Activator.getInstance().isDebugging()) {
						System.out.printf("Core exception: %s\n", e);
					}
					rm.setStatus(e.getStatus());
					rm.done();
				}
			}
		});
	}

	public Version getVersion(final String pyOCDPath) {
		try {
			String[] args = new String[2];
			args[0] = pyOCDPath;
			args[1] = "--version";

			String output = getOutput(args).trim();
			return Version.fromString(output);
		} catch (CoreException e) {
			if (Activator.getInstance().isDebugging()) {
				System.out.printf("Core exception while reading pyocd version: %s\n", e);
			}
			return null;
		}
	}

	public String getOutput(final String[] args) throws CoreException {
		final Process process;
		try {
			process = ProcessFactory.getFactory().exec(args);
		} catch (IOException e) {
			throw new DebugException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Errors.ERROR_RUNNING_PYOCD,
					"Error while launching pyOCD: " + StringUtils.join(args, " "), e.getCause()));//$NON-NLS-2$
		}

		// Start a timeout job to make sure we don't get stuck waiting for
		// an answer from a pyocd that is hanging
		// Bug 376203
		final class PyOCDTimeoutJob extends SystemJob { //$NON-NLS-1$
			private boolean f_didTimeout = false;

			public PyOCDTimeoutJob() {
				super("pyOCD output timeout job");
			}

			public boolean didTimeout() {
				return f_didTimeout;
			}

			@Override
			protected IStatus run(IProgressMonitor arg) {
				// Took too long. Kill the pyocd process and
				// let things clean up.
				process.destroy();
				f_didTimeout = true;
				return Status.OK_STATUS;
			}
		}
		PyOCDTimeoutJob timeoutJob = new PyOCDTimeoutJob();
		timeoutJob.schedule(PYOCD_TIMEOUT_MS);

		String cmdOutput = null;
		try {
			cmdOutput = readStream(process.getInputStream());
		} catch (IOException e) {
			throw new DebugException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Errors.ERROR_RUNNING_PYOCD,
					"Error reading pyOCD stdout: " + StringUtils.join(args, " "), e.getCause()));// $NON-NLS-1$
		} finally {
			if (timeoutJob.didTimeout()) {
				throw new DebugException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Errors.ERROR_TIMEOUT,
						"pyOCD timed out: " + StringUtils.join(args, " "), null));// $NON-NLS-1$
			} else {
				// If we get here we are obviously not stuck so we can cancel the
				// timeout job.
				// Note that it may already have executed, but that is not a
				// problem.
				timeoutJob.cancel();

				process.destroy();

				// Check process exit value.
				if (process.exitValue() != 0) {
					throw new DebugException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, Errors.ERROR_RUNNING_PYOCD,
							String.format("pyOCD returned exit value (%d): %s", process.exitValue(),
									StringUtils.join(args, " ")),
							null));// $NON-NLS-1$
				}
			}
		}

		return cmdOutput;
	}

	/**
	 * Read from the specified stream and return what was read.
	 *
	 * @param stream The input stream to be used to read the data.  This method will close the stream.
	 * @return The data read from the stream
	 * @throws IOException If an IOException happens when reading the stream
	 */
	public String readStream(InputStream stream) throws IOException {
		StringBuilder cmdOutput = new StringBuilder(200);
		try {
			Reader r = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(r);

			String line;
			while ((line = reader.readLine()) != null) {
				cmdOutput.append(line);
				cmdOutput.append('\n');
			}
			return cmdOutput.toString();
		} finally {
			// Cleanup to avoid leaking pipes
			// Bug 345164
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
