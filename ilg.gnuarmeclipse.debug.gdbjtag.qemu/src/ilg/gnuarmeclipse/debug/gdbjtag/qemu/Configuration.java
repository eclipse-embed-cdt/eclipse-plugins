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

package ilg.gnuarmeclipse.debug.gdbjtag.qemu;

import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGdbDebugPreferenceConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;

@SuppressWarnings("restriction")
public class Configuration {

	// ------------------------------------------------------------------------

	public static String getGdbServerCommand(ILaunchConfiguration configuration) {

		String executable = null;

		try {
			if (!configuration.getAttribute(
					ConfigurationAttributes.DO_START_GDB_SERVER,
					ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT))
				return null;

			executable = configuration.getAttribute(
					ConfigurationAttributes.GDB_SERVER_EXECUTABLE,
					ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT);
			// executable = Utils.escapeWhitespaces(executable).trim();
			executable = executable.trim();
			if (executable.length() == 0)
				return null;

			executable = VariablesPlugin.getDefault()
					.getStringVariableManager()
					.performStringSubstitution(executable, false).trim();

			ICConfigurationDescription buildConfig = DebugUtils
					.getBuildConfigDescription(configuration);
			if (buildConfig != null) {
				executable = DebugUtils.resolveAll(executable, buildConfig);
			}

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		return executable;
	}

	public static String getGdbServerCommandLine(
			ILaunchConfiguration configuration) {

		String cmdLineArray[] = getGdbServerCommandLineArray(configuration);

		return StringUtils.join(cmdLineArray, " ");
	}

	public static String[] getGdbServerCommandLineArray(
			ILaunchConfiguration configuration) {

		List<String> lst = new ArrayList<String>();

		try {
			if (!configuration.getAttribute(
					ConfigurationAttributes.DO_START_GDB_SERVER,
					ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT))
				return null;

			String executable = getGdbServerCommand(configuration);
			if (executable == null || executable.length() == 0)
				return null;

			lst.add(executable);

			// Added always, to get the 'Waiting for connection' message.
			lst.add("-verbose");

			lst.add("-gdb");
			lst.add("tcp::"
					+ Integer.toString(configuration
							.getAttribute(
									ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
									ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT)));

			// lst.add("-c");
			// lst.add("telnet_port "
			// + Integer.toString(configuration
			// .getAttribute(
			// ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
			// ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT)));

			String logFile = configuration.getAttribute(
					ConfigurationAttributes.GDB_SERVER_LOG,
					ConfigurationAttributes.GDB_SERVER_LOG_DEFAULT).trim();

			logFile = VariablesPlugin.getDefault().getStringVariableManager()
					.performStringSubstitution(logFile, false);

			// if (EclipseUtils.isWindows()) {
			// logFile = doubleBackslashes(logFile);
			// }
			if (!logFile.isEmpty()) {
				lst.add("-D");
				lst.add(logFile);
			}

			String other = configuration.getAttribute(
					ConfigurationAttributes.GDB_SERVER_OTHER,
					ConfigurationAttributes.GDB_SERVER_OTHER_DEFAULT).trim();

			other = VariablesPlugin.getDefault().getStringVariableManager()
					.performStringSubstitution(other);

			// if (EclipseUtils.isWindows()) {
			// other = doubleBackslashes(other);
			// }
			if (!other.isEmpty()) {
				lst.addAll(StringUtils.splitCommandLineOptions(other));
			}

			if (configuration.getAttribute(
					ConfigurationAttributes.ENABLE_SEMIHOSTING,
					ConfigurationAttributes.ENABLE_SEMIHOSTING_DEFAULT)) {
				String semihostingOption = ConfigurationAttributes.ENABLE_SEMIHOSTING_OPTION;
				lst.addAll(StringUtils
						.splitCommandLineOptions(semihostingOption));
			}

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		return lst.toArray(new String[0]);
	}

	public static String getServerCommandName(ILaunchConfiguration config) {
		String fullCommand = getGdbServerCommand(config);
		if (fullCommand == null)
			return null;

		String parts[] = fullCommand.trim().split("" + Path.SEPARATOR);
		return parts[parts.length - 1];
	}

	// ------------------------------------------------------------------------

	public static String getGdbClientCommand(ILaunchConfiguration configuration) {

		String executable = null;
		try {
			String defaultGdbCommand = Platform
					.getPreferencesService()
					.getString(
							GdbPlugin.PLUGIN_ID,
							IGdbDebugPreferenceConstants.PREF_DEFAULT_GDB_COMMAND,
							IGDBLaunchConfigurationConstants.DEBUGGER_DEBUG_NAME_DEFAULT,
							null);

			executable = configuration.getAttribute(
					IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
					defaultGdbCommand);
			executable = VariablesPlugin.getDefault()
					.getStringVariableManager()
					.performStringSubstitution(executable, false).trim();

			ICConfigurationDescription buildConfig = DebugUtils
					.getBuildConfigDescription(configuration);
			if (buildConfig != null) {
				executable = DebugUtils.resolveAll(executable, buildConfig);
			}

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		return executable;
	}

	public static String[] getGdbClientCommandLineArray(
			ILaunchConfiguration configuration) {

		List<String> lst = new ArrayList<String>();

		String executable = getGdbClientCommand(configuration);
		if (executable == null || executable.length() == 0)
			return null;

		lst.add(executable);

		// We currently work with MI version 2. Don't use just 'mi' because
		// it points to the latest MI version, while we want mi2 specifically.
		lst.add("--interpreter=mi2");

		// Don't read the gdbinit file here. It is read explicitly in
		// the LaunchSequence to make it easier to customise.
		lst.add("--nx");

		String other;
		try {
			other = configuration.getAttribute(
					ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS,
					ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS_DEFAULT)
					.trim();
			if (other.length() > 0) {
				lst.addAll(StringUtils.splitCommandLineOptions(other));
			}
		} catch (CoreException e) {
			Activator.log(e);
		}

		return lst.toArray(new String[0]);
	}

	public static String getGdbClientCommandLine(
			ILaunchConfiguration configuration) {

		String cmdLineArray[] = getGdbClientCommandLineArray(configuration);

		return StringUtils.join(cmdLineArray, " ");
	}

	public static String getClientCommandName(ILaunchConfiguration config) {
		String fullCommand = getGdbClientCommand(config);
		if (fullCommand == null)
			return null;

		String parts[] = fullCommand.trim().split("" + Path.SEPARATOR);
		return parts[parts.length - 1];
	}

	// ------------------------------------------------------------------------
}
