/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.jlink;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGdbDebugPreferenceConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.core.StringUtils;
import org.eclipse.embedcdt.debug.gdbjtag.DebugUtils;
import org.eclipse.embedcdt.debug.gdbjtag.jlink.preferences.DefaultPreferences;

@SuppressWarnings("restriction")
public class Configuration {

	// ------------------------------------------------------------------------

	public static String getGdbServerCommand(ILaunchConfiguration configuration, String executable) {

		try {

			if (executable == null) {
				DefaultPreferences fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
				if (!configuration.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
						fDefaultPreferences.getGdbServerDoStart()))
					return null;

				executable = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE,
						fDefaultPreferences.getGdbServerExecutable());
				// executable = Utils.escapeWhitespaces(executable).trim();
			}

			executable = resolveAll(executable, configuration);

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		return executable;
	}

	public static String getGdbServerCommandLine(ILaunchConfiguration configuration) {

		String cmdLineArray[] = getGdbServerCommandLineArray(configuration);

		return StringUtils.join(cmdLineArray, " ");
	}

	public static String[] getGdbServerCommandLineArray(ILaunchConfiguration configuration) {

		List<String> lst = new ArrayList<String>();

		try {
			DefaultPreferences fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
			if (!configuration.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
					fDefaultPreferences.getGdbServerDoStart()))
				return null;

			String executable = getGdbServerCommand(configuration, null);
			if (executable == null || executable.isEmpty())
				return null;

			lst.add(executable);

			String connection = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_CONNECTION,
					fDefaultPreferences.getGdbServerConnection());
			String connectionAddress = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_CONNECTION_ADDRESS,
					fDefaultPreferences.getGdbServerConnectionAddress());

			connectionAddress = DebugUtils.resolveAll(connectionAddress, configuration.getAttributes());
			if (connectionAddress.length() > 0) {
				if (DefaultPreferences.GDB_SERVER_CONNECTION_USB.equals(connection)) {

					lst.add("-select");
					lst.add("usb=" + connectionAddress);
				} else if (DefaultPreferences.GDB_SERVER_CONNECTION_IP.equals(connection)) {
					lst.add("-select");
					lst.add("ip=" + connectionAddress);
				}
			}

			lst.add("-if");
			lst.add(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_DEBUG_INTERFACE,
					fDefaultPreferences.getGdbServerInterface()));

			String defaultName = configuration.getAttribute(ConfigurationAttributes.FLASH_DEVICE_NAME_COMPAT,
					DefaultPreferences.FLASH_DEVICE_NAME_DEFAULT);

			String name = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_NAME, defaultName)
					.trim();
			name = DebugUtils.resolveAll(name, configuration.getAttributes());
			if (name.length() > 0) {
				lst.add("-device");
				lst.add(name);
			}

			lst.add("-endian");
			lst.add(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_ENDIANNESS,
					fDefaultPreferences.getGdbServerEndianness()));

			lst.add("-speed");
			lst.add(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_SPEED,
					fDefaultPreferences.getGdbServerInitialSpeed()));

			lst.add("-port");
			lst.add(Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
					DefaultPreferences.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT)));

			lst.add("-swoport");
			lst.add(Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_SWO_PORT_NUMBER,
					DefaultPreferences.GDB_SERVER_SWO_PORT_NUMBER_DEFAULT)));

			lst.add("-telnetport");
			lst.add(Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
					DefaultPreferences.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT)));

			if (configuration.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_VERIFY_DOWNLOAD,
					DefaultPreferences.DO_GDB_SERVER_VERIFY_DOWNLOAD_DEFAULT)) {
				lst.add("-vd");
			}

			if (configuration.getAttribute(ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
					DefaultPreferences.DO_CONNECT_TO_RUNNING_DEFAULT)) {
				lst.add("-noreset");
				lst.add("-noir");
			} else {
				if (configuration.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_INIT_REGS,
						DefaultPreferences.DO_GDB_SERVER_INIT_REGS_DEFAULT)) {
					lst.add("-ir");
				} else {
					lst.add("-noir");
				}
			}

			lst.add("-localhostonly");
			if (configuration.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_LOCAL_ONLY,
					DefaultPreferences.DO_GDB_SERVER_LOCAL_ONLY_DEFAULT)) {
				lst.add("1");
			} else {
				lst.add("0");
			}

			if (configuration.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_SILENT,
					DefaultPreferences.DO_GDB_SERVER_SILENT_DEFAULT)) {
				lst.add("-silent");
			}

			String logFile = configuration
					.getAttribute(ConfigurationAttributes.GDB_SERVER_LOG, DefaultPreferences.GDB_SERVER_LOG_DEFAULT)
					.trim();

			logFile = DebugUtils.resolveAll(logFile, configuration.getAttributes());
			if (logFile.length() > 0) {
				lst.add("-log");

				// lst.add(Utils.escapeWhitespaces(logFile));
				lst.add(logFile);
			}

			String other = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER,
					fDefaultPreferences.getGdbServerOtherOptions()).trim();
			other = DebugUtils.resolveAll(other, configuration.getAttributes());
			if (other.length() > 0) {
				lst.addAll(StringUtils.splitCommandLineOptions(other));
			}

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		return lst.toArray(new String[0]);
	}

	public static String getGdbServerCommandName(ILaunchConfiguration config) {

		String fullCommand = getGdbServerCommand(config, null);
		return StringUtils.extractNameFromPath(fullCommand);
	}

	public static String getGdbServerDeviceName(ILaunchConfiguration config) throws CoreException {

		return config.getAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_NAME,
				DefaultPreferences.FLASH_DEVICE_NAME_DEFAULT).trim();
	}

	// ------------------------------------------------------------------------

	public static String getGdbClientCommand(ILaunchConfiguration configuration, String executable) {

		try {
			if (executable == null) {
				String defaultGdbCommand = Platform.getPreferencesService().getString(GdbPlugin.PLUGIN_ID,
						IGdbDebugPreferenceConstants.PREF_DEFAULT_GDB_COMMAND,
						IGDBLaunchConfigurationConstants.DEBUGGER_DEBUG_NAME_DEFAULT, null);

				executable = configuration.getAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
						defaultGdbCommand);
			}

			executable = resolveAll(executable, configuration);

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		return executable;
	}

	public static String[] getGdbClientCommandLineArray(ILaunchConfiguration configuration) {

		List<String> lst = new ArrayList<String>();

		String executable = getGdbClientCommand(configuration, null);
		if (executable == null || executable.isEmpty())
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
			DefaultPreferences fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
			other = configuration.getAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS,
					fDefaultPreferences.getGdbClientOtherOptions()).trim();
			other = DebugUtils.resolveAll(other, configuration.getAttributes());
			if (other.length() > 0) {
				lst.addAll(StringUtils.splitCommandLineOptions(other));
			}
		} catch (CoreException e) {
			Activator.log(e);
		}

		return lst.toArray(new String[0]);
	}

	public static String getGdbClientCommandLine(ILaunchConfiguration configuration) {

		String cmdLineArray[] = getGdbClientCommandLineArray(configuration);
		return StringUtils.join(cmdLineArray, " ");
	}

	public static String getGdbClientCommandName(ILaunchConfiguration config) {

		String fullCommand = getGdbClientCommand(config, null);
		return StringUtils.extractNameFromPath(fullCommand);
	}

	public static String resolveAll(String str, ILaunchConfiguration configuration) throws CoreException {
		String value = str;
		value = value.trim();
		if (value.isEmpty())
			return null;

		if (value.indexOf("${") >= 0) {
			IProject project = EclipseUtils.getProjectByLaunchConfiguration(configuration);
			if (project != null) {
				value = DynamicVariableResolver.resolveAll(value, project);
			}
		}

		if (value.indexOf("${") >= 0) {
			// If more macros to process.
			value = DebugUtils.resolveAll(value, configuration.getAttributes());

			ICConfigurationDescription buildConfig = EclipseUtils.getBuildConfigDescription(configuration);
			if (buildConfig != null) {
				value = DebugUtils.resolveAll(value, buildConfig);
			}
		}
		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.resolveAll(\"" + str + "\") = \"" + value + "\"");
		}
		return value;
	}

	// ------------------------------------------------------------------------

	public static boolean getDoStartGdbServer(ILaunchConfiguration config) throws CoreException {

		DefaultPreferences fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
		return config.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
				fDefaultPreferences.getGdbServerDoStart());
	}

	public static boolean getDoAddServerConsole(ILaunchConfiguration config) throws CoreException {

		return getDoStartGdbServer(config)
				&& config.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
						DefaultPreferences.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);
	}

	public static boolean getDoAddSemihostingConsole(ILaunchConfiguration config) throws CoreException {

		return getDoStartGdbServer(config)
				&& config.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
						DefaultPreferences.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT);
	}

	// ------------------------------------------------------------------------
}
