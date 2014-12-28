/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.core.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.core.cdtvariables.ICdtVariable;
import org.eclipse.cdt.core.envvar.IEnvironmentVariable;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.concurrent.CountingRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DsfExecutor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGdbDebugPreferenceConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.launching.LaunchUtils;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.command.commands.CLICommand;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;
import org.eclipse.cdt.internal.core.envvar.EnvVarCollector;
import org.eclipse.cdt.internal.core.envvar.EnvVarDescriptor;
import org.eclipse.cdt.internal.core.envvar.EnvironmentVariableManager;
import org.eclipse.cdt.internal.core.envvar.IEnvironmentContextInfo;
import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;

@SuppressWarnings("restriction")
public class DebugUtils {

	// ------------------------------------------------------------------------

	public static String[] getLaunchEnvironment(ILaunchConfiguration config)
			throws CoreException {

		ICConfigurationDescription cfg = getBuildConfigDescription(config);
		if (cfg == null)
			return getLaunchEnvironmentWithoutProject();

		// Environment variables and inherited vars
		HashMap<String, String> envMap = new HashMap<String, String>();
		IEnvironmentVariable[] vars = CCorePlugin.getDefault()
				.getBuildEnvironmentManager().getVariables(cfg, true);
		for (IEnvironmentVariable var : vars)
			envMap.put(var.getName(), var.getValue());

		// Add variables from build info
		ICdtVariable[] build_vars = CCorePlugin.getDefault()
				.getCdtVariableManager().getVariables(cfg);
		for (ICdtVariable var : build_vars) {
			try {
				// The project_classpath variable contributed by JDT is useless
				// for running C/C++
				// binaries, but it can be lethal if it has a very large value
				// that exceeds shell
				// limit. See
				// http://bugs.eclipse.org/bugs/show_bug.cgi?id=408522
				if (!"project_classpath".equals(var.getName())) //$NON-NLS-1$
					envMap.put(var.getName(), var.getStringValue());
			} catch (CdtVariableException e) {
				// Some Eclipse dynamic variables can't be resolved
				// dynamically... we don't care.
			}
		}

		// Turn it into an envp format
		List<String> strings = new ArrayList<String>(envMap.size());
		for (Entry<String, String> entry : envMap.entrySet()) {
			StringBuffer buffer = new StringBuffer(entry.getKey());
			buffer.append('=').append(entry.getValue());
			strings.add(buffer.toString());
		}

		return strings.toArray(new String[strings.size()]);
	}

	/**
	 * Get environment from workspace. Useful when project-less launching when
	 * there is no environment available from the configuration.
	 * 
	 * @return String [] of environment variables in variable=value format.
	 * @throws CoreException
	 */
	public static String[] getLaunchEnvironmentWithoutProject()
			throws CoreException {
		String[] retVal = null;
		IEnvironmentContextInfo contextInfo = EnvironmentVariableManager
				.getDefault().getContextInfo(null);
		EnvVarCollector envVarMergedCollection = EnvironmentVariableManager
				.getVariables(contextInfo, true);
		if (null != envVarMergedCollection) {
			EnvVarDescriptor envVars[] = envVarMergedCollection.toArray(false);
			if (envVars != null) {
				List<String> strings = new ArrayList<String>();
				for (int i = 0; i < envVars.length; i++) {
					IEnvironmentVariable resolved = EnvironmentVariableManager
							.getDefault().calculateResolvedVariable(envVars[i],
									contextInfo);
					if (null != resolved) {
						// The project_classpath variable contributed by JDT is
						// useless
						// for running C/C++
						// binaries, but it can be lethal if it has a very large
						// value
						// that exceeds shell
						// limit. See
						// http://bugs.eclipse.org/bugs/show_bug.cgi?id=408522
						if (!"project_classpath".equals(resolved.getName())) {//$NON-NLS-1$
							StringBuffer buffer = new StringBuffer(
									resolved.getName());
							buffer.append('=').append(resolved.getValue());
							strings.add(buffer.toString());
						}
					}
				}
				retVal = strings.toArray(new String[strings.size()]);
			}
		} else {
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID,
					"Error retrieving workspace environment."));
		}
		return retVal;
	}

	/**
	 * Get the build configuration associated with the debug launch
	 * configuration, if defined in the first tab.
	 * 
	 * @param config
	 *            a debug launch configuration.
	 * @return the build configuration, or null if not found or not defined.
	 */
	public static ICConfigurationDescription getBuildConfigDescription(
			ILaunchConfiguration config) {

		ICConfigurationDescription cfg = null;

		// Get the project
		String projectName;
		try {
			projectName = config.getAttribute(
					ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					(String) null);
			if (projectName == null) {
				return null;
			}
			projectName = projectName.trim();
			if (projectName.isEmpty()) {
				return null;
			}
			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);
			if (project == null || !project.isAccessible()) {
				return null;
			}

			ICProjectDescription projDesc = CoreModel.getDefault()
					.getProjectDescription(project, false);

			// Not a CDT project?
			if (projDesc == null) {
				return null;
			}

			String buildConfigID = config
					.getAttribute(
							ICDTLaunchConfigurationConstants.ATTR_PROJECT_BUILD_CONFIG_ID,
							""); //$NON-NLS-1$
			if (buildConfigID.length() != 0) {
				cfg = projDesc.getConfigurationById(buildConfigID);
			}
			// if configuration is null fall-back to active
			if (cfg == null) {
				cfg = projDesc.getActiveConfiguration();
			}
		} catch (CoreException e) {
			Activator.log(e);
		}
		return cfg;
	}

	public static File getProjectOsPath(String projectName) {
		IPath path = null;
		if (projectName.length() > 0) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject project = workspace.getRoot().getProject(projectName);
			path = project.getLocation();
		}
		File dir = new File(path.toOSString());
		return dir;
	}

	/**
	 * Gets working directory from either the location of the project, or if
	 * project-less launching, from the location of the executable.
	 * 
	 * @param configuration
	 * @return Working directory
	 * @throws CoreException
	 */
	public static File getProjectOsDir(ILaunchConfiguration configuration)
			throws CoreException {

		IPath path = getProjectOsPath(configuration);
		File dir = null;
		if (null != path) {
			dir = new File(path.toOSString());
		}
		return dir;
	}

	public static IPath getProjectOsPath(ILaunchConfiguration configuration)
			throws CoreException {

		IPath path = null;
		if (null != configuration) {
			String projectName = configuration.getAttribute(
					"org.eclipse.cdt.launch.PROJECT_ATTR", "");
			if (projectName.length() > 0) {
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IProject project = workspace.getRoot().getProject(projectName);
				path = project.getLocation();
			} else {
				/*
				 * If project-less launch then PROGRAM_NAME will be an absolute
				 * path
				 */
				String executableName = configuration.getAttribute(
						"org.eclipse.cdt.launch.PROGRAM_NAME", "");
				path = new Path(executableName).removeLastSegments(1);
			}
		}
		return path;
	}

	public static IPath getGDBPath(ILaunchConfiguration configuration) {
		String defaultGdbCommand = Platform.getPreferencesService().getString(
				GdbPlugin.PLUGIN_ID,
				IGdbDebugPreferenceConstants.PREF_DEFAULT_GDB_COMMAND,
				IGDBLaunchConfigurationConstants.DEBUGGER_DEBUG_NAME_DEFAULT,
				null);

		IPath retVal = new Path(defaultGdbCommand);
		try {
			String gdb = configuration.getAttribute(
					IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
					defaultGdbCommand);
			gdb = VariablesPlugin.getDefault().getStringVariableManager()
					.performStringSubstitution(gdb, false);

			ICConfigurationDescription buildConfig = getBuildConfigDescription(configuration);
			if (buildConfig != null) {
				gdb = resolveAll(gdb, buildConfig);
			}

			retVal = new Path(gdb);
		} catch (CoreException e) {
			Activator.log(e);
		}
		return retVal;
	}

	public static String resolveAll(String value,
			ICConfigurationDescription cfgDescription) {
		try {
			return CCorePlugin.getDefault().getCdtVariableManager()
					.resolveValue(value, "", " ", cfgDescription); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (CdtVariableException e) {
			Activator.log(e);
		}
		return value;
	}

	/**
	 * Split a string into separate lines and add them to the list.
	 * 
	 * @param multiLine
	 *            a string.
	 * @param commandsList
	 *            a list of strings.
	 * @throws CoreException
	 */
	public static void addMultiLine(String multiLine, List<String> commandsList)
			throws CoreException {

		if (multiLine.length() > 0) {
			multiLine = VariablesPlugin.getDefault().getStringVariableManager()
					.performStringSubstitution(multiLine);
			String[] commandsStr = multiLine.split("\\r?\\n"); //$NON-NLS-1$
			for (String str : commandsStr) {
				str = str.trim();
				if (str.length() > 0) {
					commandsList.add(str);
				}
			}
		}
	}

	// public static String composeCommandWithLf(Collection<String> commands) {
	// if (commands.isEmpty())
	// return null;
	// StringBuffer sb = new StringBuffer();
	// Iterator<String> it = commands.iterator();
	// while (it.hasNext()) {
	// String s = it.next().trim();
	// if (s.isEmpty() || s.startsWith("#"))
	// continue; // ignore empty lines and comment
	//
	// sb.append(s);
	// if (it.hasNext()) {
	// sb.append("\n");
	// }
	// }
	// return sb.toString();
	// }

	static public String escapeWhitespaces(String path) {
		path = path.trim();
		// Escape the spaces in the path/filename if it has any
		String[] segments = path.split("\\s"); //$NON-NLS-1$
		if (segments.length > 1) {
			if (EclipseUtils.isWindows()) {
				return "\"" + path + "\"";
			} else {
				StringBuffer escapedPath = new StringBuffer();
				for (int index = 0; index < segments.length; ++index) {
					escapedPath.append(segments[index]);
					if (index + 1 < segments.length) {
						escapedPath.append("\\ "); //$NON-NLS-1$
					}
				}
				return escapedPath.toString().trim();
			}
		} else {
			return path;
		}
	}

	/**
	 * This method actually launches 'arm-none-eabi-gdb --vesion' to determine
	 * the version of the GDB that is being used. This method should ideally be
	 * called only once and the resulting version string stored for future uses.
	 */
	public static String getGDBVersion(
			final ILaunchConfiguration configuration, String gdbClientCommand)
			throws CoreException {

		String[] cmdArray = new String[2];
		cmdArray[0] = gdbClientCommand;
		cmdArray[1] = "--version";

		final Process process;
		try {
			process = ProcessFactory.getFactory().exec(cmdArray,
					DebugUtils.getLaunchEnvironment(configuration));
		} catch (IOException e) {
			throw new DebugException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Error while launching command: "
							+ StringUtils.join(cmdArray, " "), e.getCause()));//$NON-NLS-1$
		}

		// Start a timeout job to make sure we don't get stuck waiting for
		// an answer from a gdb that is hanging
		// Bug 376203
		Job timeoutJob = new Job("GDB version timeout job") { //$NON-NLS-1$
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
					"Error reading GDB STDOUT after sending: "
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

		String gdbVersion = LaunchUtils.getGDBVersionFromText(cmdOutput
				.toString());
		if (gdbVersion == null || gdbVersion.isEmpty()) {
			throw new DebugException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, DebugException.REQUEST_FAILED,
					"Could not determine GDB version after sending: "
							+ StringUtils.join(cmdArray, " ") + ", response: "
							+ cmdOutput, null));//$NON-NLS-1$
		}
		return gdbVersion;
	}

	/**
	 * Test if the launch configuration is already started. Enumerate all
	 * launches and check by name and non terminated status.
	 * 
	 * @param configuration
	 * @return true if already present.
	 */
	public static boolean isLaunchConfigurationStarted(
			ILaunchConfiguration configuration) {

		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunch[] launches = launchManager.getLaunches();
		for (int i = 0; i < launches.length; ++i) {
			ILaunch launch = launches[i];
			// System.out.println(ls[i].getLaunchConfiguration().getName());
			if (!launch.isTerminated()
					&& configuration.getName().equals(
							launch.getLaunchConfiguration().getName())) {

				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the launch configuration is already started and throw a
	 * CoreException.
	 * 
	 * @param configuration
	 * @throws CoreException
	 */
	public static void checkLaunchConfigurationStarted(
			ILaunchConfiguration configuration) throws CoreException {

		if (isLaunchConfigurationStarted(configuration)) {

			throw new CoreException(
					new Status(
							IStatus.ERROR,
							Activator.PLUGIN_ID,
							"Debug session '"
									+ configuration.getName()
									+ "' already started. Terminate the first one before restarting."));

		}
	}

	/**
	 * Execute an external process.
	 * 
	 * @param commandLineArray
	 *            a String array with the comman line.
	 * @param environ
	 *            a String array with environment variables.
	 * @param dir
	 *            the current working directory for the process.
	 * @return a Process object.
	 * @throws CoreException
	 */
	public static Process exec(String[] commandLineArray, String[] environ,
			File dir) throws CoreException {

		System.out.println("exec " + StringUtils.join(commandLineArray, " "));
		System.out.println("dir " + dir);

		Process proc = null;
		try {
			proc = ProcessFactory.getFactory().exec(commandLineArray, environ,
					dir);
		} catch (IOException e) {
			String message = "Launching command ["
					+ StringUtils.join(commandLineArray, " ") + "] failed."; //$NON-NLS-1$
			throw new CoreException(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, -1, message, e));
		}

		return proc;
	}

	/**
	 * Best effort to get a current working directory.
	 * 
	 * The first part is an exact copy of GDBBackend.getGDBWorkingDirectory().
	 * 
	 * @param launchConfiguration
	 * @return
	 * @throws CoreException
	 */
	public static IPath getGdbWorkingDirectory(
			ILaunchConfiguration launchConfiguration) throws CoreException {

		// First try to use the user-specified working directory for the
		// debugged program.
		// This is fine only with local debug.
		// For remote debug, the working dir of the debugged program will be
		// on remote device
		// and hence not applicable. In such case we may just use debugged
		// program path on host
		// as the working dir for GDB.
		// However, we cannot find a standard/common way to distinguish
		// remote debug from local
		// debug. For instance, a local debug may also use gdbserver+gdb. So
		// it's up to each
		// debugger implementation to make the distinction.
		//
		IPath path = null;
		String location = launchConfiguration.getAttribute(
				ICDTLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY,
				(String) null);

		if (location != null) {
			String expandedLocation = VariablesPlugin.getDefault()
					.getStringVariableManager()
					.performStringSubstitution(location);
			if (expandedLocation.length() > 0) {
				path = new Path(expandedLocation);
			}
		}

		if (path != null) {
			// Some validity check. Should have been done by UI code.
			if (path.isAbsolute()) {
				File dir = new File(path.toPortableString());
				if (!dir.isDirectory())
					path = null;
			} else {
				IResource res = ResourcesPlugin.getWorkspace().getRoot()
						.findMember(path);
				if (res instanceof IContainer && res.exists()) {
					path = res.getLocation();
				} else
					// Relative but not found in workspace.
					path = null;
			}
		}

		if (path == null) {
			// default working dir is the project if this config has a
			// project
			ICProject cp = LaunchUtils.getCProject(launchConfiguration);
			if (cp != null) {
				IProject p = cp.getProject();
				path = p.getLocation();
			} else {
				// no meaningful value found. Just return null.
			}
		}

		// --------------------------------------------------------------------

		if (path == null) {
			path = DebugUtils.getProjectOsPath(launchConfiguration);
		}
		return path;
	}

	/**
	 * Queue a list of string commands to the executor. Ignore empty lines or
	 * comments
	 * 
	 * @param commands
	 *            a list of strings.
	 * @param rm
	 *            a request monitor.
	 * @param control
	 *            an IGDBControl.
	 * @param executor
	 *            an DsfExecutor.
	 */
	public static void queueCommands(List<String> commands, RequestMonitor rm,
			IGDBControl control, DsfExecutor executor) {

		if (commands != null && !commands.isEmpty()) {

			CountingRequestMonitor crm = new CountingRequestMonitor(executor,
					rm);
			crm.setDoneCount(commands.size());

			Iterator<String> it = commands.iterator();
			while (it.hasNext()) {
				String s = it.next().trim();
				if (s.isEmpty() || s.startsWith("#")) {
					crm.done();
					continue; // ignore empty lines and comments
				}
				// System.out.println("queueCommand('" + s + "')");
				control.queueCommand(
						new CLICommand<MIInfo>(control.getContext(), s),
						new DataRequestMonitor<MIInfo>(executor, crm));
			}

		} else {
			rm.done();
		}
	}

	// ------------------------------------------------------------------------
}
