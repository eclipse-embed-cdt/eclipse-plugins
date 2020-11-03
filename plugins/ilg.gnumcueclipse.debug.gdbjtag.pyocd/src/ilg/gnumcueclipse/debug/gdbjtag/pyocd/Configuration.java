/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * Copyright (c) 2015-2016 Chris Reed.
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
 *    Chris Reed - pyOCD changes
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.pyocd;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.core.StringUtils;
import ilg.gnumcueclipse.debug.gdbjtag.DebugUtils;
import ilg.gnumcueclipse.debug.gdbjtag.pyocd.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.pyocd.DynamicVariableResolver;
import ilg.gnumcueclipse.debug.gdbjtag.pyocd.preferences.DefaultPreferences;
import ilg.gnumcueclipse.debug.gdbjtag.pyocd.preferences.PreferenceConstants;

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

@SuppressWarnings("restriction")
public class Configuration {

	// ------------------------------------------------------------------------

	public static String getGdbServerCommand(ILaunchConfiguration configuration, String executable) {

		try {
			if (executable == null) {
				if (!configuration.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
						DefaultPreferences.DO_START_GDB_SERVER_DEFAULT))
					return null;

				executable = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE,
						DefaultPreferences.GDB_SERVER_EXECUTABLE_DEFAULT);
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
			if (!configuration.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
					DefaultPreferences.DO_START_GDB_SERVER_DEFAULT))
				return null;

			String executable = getGdbServerCommand(configuration, null);
			if (executable == null || executable.isEmpty())
				return null;

			lst.add(executable);

			// GDB port
			lst.add("--port");
			lst.add(Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
					DefaultPreferences.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT)));

			// Telnet port
			lst.add("--telnet-port");
			lst.add(Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
					DefaultPreferences.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT)));

			// Board ID
			String boardId = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_ID,
					DefaultPreferences.GDB_SERVER_BOARD_ID_DEFAULT);
			if (!boardId.isEmpty()) {
				lst.add("--board");
				lst.add(boardId);
			}

			// Override target
			if (configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_OVERRIDE_TARGET,
					DefaultPreferences.GDB_SERVER_OVERRIDE_TARGET_DEFAULT)) {
				lst.add("--target");
				lst.add(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_TARGET_NAME,
						DefaultPreferences.GDB_SERVER_TARGET_NAME_DEFAULT));
			}

			// Bus speed
			lst.add("--frequency");
			lst.add(Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_BUS_SPEED,
					DefaultPreferences.GDB_SERVER_BUS_SPEED_DEFAULT)));

			// Halt at hard fault
			if (!configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_HALT_AT_HARD_FAULT,
					DefaultPreferences.GDB_SERVER_HALT_AT_HARD_FAULT_DEFAULT)) {
				lst.add("--nobreak");
			}

			// Step into interrupts
			if (configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_STEP_INTO_INTERRUPTS,
					DefaultPreferences.GDB_SERVER_STEP_INTO_INTERRUPTS_DEFAULT)) {
				lst.add("--step-int");
			}

			// Flash mode
			int flashMode = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_FLASH_MODE,
					DefaultPreferences.GDB_SERVER_FLASH_MODE_DEFAULT);
			switch (flashMode) {
			case PreferenceConstants.AUTO_ERASE:
				break;
			case PreferenceConstants.CHIP_ERASE:
				lst.add("--chip_erase");
				break;
			case PreferenceConstants.SECTOR_ERASE:
				lst.add("--sector_erase");
				break;
			}

			if (configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_FLASH_FAST_VERIFY,
					DefaultPreferences.GDB_SERVER_FLASH_FAST_VERIFY_DEFAULT)) {
				lst.add("--fast_program");
			}

			// Semihosting
			if (configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_ENABLE_SEMIHOSTING,
					DefaultPreferences.GDB_SERVER_ENABLE_SEMIHOSTING_DEFAULT)) {
				lst.add("--semihosting");
			}

			if (configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_USE_GDB_SYSCALLS,
					DefaultPreferences.GDB_SERVER_USE_GDB_SYSCALLS_DEFAULT)) {
				lst.add("--gdb-syscall");
			}

			// Other
			String other = configuration
					.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, DefaultPreferences.GDB_SERVER_OTHER_DEFAULT)
					.trim();

			other = DebugUtils.resolveAll(other, configuration.getAttributes());

			if (!other.isEmpty()) {
				lst.addAll(StringUtils.splitCommandLineOptions(other));
			}

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		// Added as a marker, it is displayed if the configuration was processed
		// properly.
		lst.add("-c");
		lst.add("echo \"Started by GNU MCU Eclipse\"");

		return lst.toArray(new String[0]);
	}

	public static String getGdbServerCommandName(ILaunchConfiguration config) {

		String fullCommand = getGdbServerCommand(config, null);
		return StringUtils.extractNameFromPath(fullCommand);
	}

	public static String getGdbServerOtherConfig(ILaunchConfiguration config) throws CoreException {

		return config
				.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, DefaultPreferences.GDB_SERVER_OTHER_DEFAULT)
				.trim();
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
			executable = DebugUtils.resolveAll(executable, configuration.getAttributes());

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
			other = configuration.getAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS,
					DefaultPreferences.GDB_CLIENT_OTHER_OPTIONS_DEFAULT).trim();

			other = DebugUtils.resolveAll(other, configuration.getAttributes());

			if (!other.isEmpty()) {
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
			System.out.println("pyocd.resolveAll(\"" + str + "\") = \"" + value + "\"");
		}
		return value;
	}

	// ------------------------------------------------------------------------

	public static boolean getDoStartGdbServer(ILaunchConfiguration config) throws CoreException {

		return config.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
				DefaultPreferences.DO_START_GDB_SERVER_DEFAULT);
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
