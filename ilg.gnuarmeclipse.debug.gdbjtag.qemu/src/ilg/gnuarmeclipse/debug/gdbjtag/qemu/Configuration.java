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

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGdbDebugPreferenceConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;

@SuppressWarnings("restriction")
public class Configuration {

	// ------------------------------------------------------------------------

	public static String getGdbServerCommand(ILaunchConfiguration configuration) {

		String executable = null;

		try {
			if (!configuration.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
					DefaultPreferences.getGdbServerDoStart()))
				return null;

			executable = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE,
					DefaultPreferences.getGdbServerExecutable());
			// executable = Utils.escapeWhitespaces(executable).trim();
			executable = executable.trim();
			if (executable.length() == 0)
				return null;

			executable = DebugUtils.resolveAll(executable, configuration.getAttributes());

			ICConfigurationDescription buildConfig = EclipseUtils.getBuildConfigDescription(configuration);
			if (buildConfig != null) {
				executable = DebugUtils.resolveAll(executable, buildConfig);
			}

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
					DefaultPreferences.getGdbServerDoStart()))
				return null;

			String executable = getGdbServerCommand(configuration);
			if (executable == null || executable.length() == 0)
				return null;

			lst.add(executable);

			// Added always, to get the 'Waiting for connection' message.
			lst.add("--verbose");

			if (configuration.getAttribute(ConfigurationAttributes.IS_GDB_SERVER_VERBOSE,
					DefaultPreferences.QEMU_IS_VERBOSE_DEFAULT)) {
				lst.add("--verbose");
			}

			String boardName = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_NAME, "").trim();
			boardName = DebugUtils.resolveAll(boardName, configuration.getAttributes());
			if (!boardName.isEmpty()) {
				lst.add("--board");
				lst.add(boardName);
			}

			String deviceName = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_NAME, "").trim();
			deviceName = DebugUtils.resolveAll(deviceName, configuration.getAttributes());
			if (!deviceName.isEmpty()) {
				lst.add("--mcu");
				lst.add(deviceName);
			}

			lst.add("--gdb");
			lst.add("tcp::"
					+ Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
							DefaultPreferences.SERVER_GDB_PORT_NUMBER_DEFAULT)));

			String other = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER,
					DefaultPreferences.SERVER_OTHER_OPTIONS_DEFAULT).trim();

			other = DebugUtils.resolveAll(other, configuration.getAttributes());

			if (!other.isEmpty()) {
				lst.addAll(StringUtils.splitCommandLineOptions(other));
			}

			boolean nographic = configuration.getAttribute(ConfigurationAttributes.DISABLE_GRAPHICS,
					DefaultPreferences.DISABLE_GRAPHICS_DEFAULT);
			if (nographic) {
				lst.add("--nographic");
			}

			boolean isSemihosting = configuration.getAttribute(ConfigurationAttributes.ENABLE_SEMIHOSTING,
					DefaultPreferences.ENABLE_SEMIHOSTING_DEFAULT);

			lst.add("--semihosting-config");
			lst.add(isSemihosting ? "enable=on,target=native" : "enable=off,target=native");

			if (isSemihosting) {
				String semihostingCmdline = configuration.getAttribute(ConfigurationAttributes.SEMIHOSTING_CMDLINE, "")
						.trim();

				// This option must be the last one.
				if (!semihostingCmdline.isEmpty()) {
					lst.add("--semihosting-cmdline");
					lst.addAll(StringUtils.splitCommandLineOptions(semihostingCmdline));
				}
			}

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		return lst.toArray(new String[0]);
	}

	public static String getGdbServerCommandName(ILaunchConfiguration config) {

		String fullCommand = getGdbServerCommand(config);
		return StringUtils.extractNameFromPath(fullCommand);
	}

	public static String getGdbServerOtherConfig(ILaunchConfiguration config) throws CoreException {

		return config
				.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, DefaultPreferences.SERVER_OTHER_OPTIONS_DEFAULT)
				.trim();
	}

	public static String getQemuBoardName(ILaunchConfiguration config) throws CoreException {

		return config.getAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_NAME, "").trim();
	}

	public static String getQemuDeviceName(ILaunchConfiguration config) throws CoreException {

		return config.getAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_NAME, "").trim();
	}

	// ------------------------------------------------------------------------

	public static String getGdbClientCommand(ILaunchConfiguration configuration) {

		String executable = null;
		try {
			String defaultGdbCommand = Platform.getPreferencesService().getString(GdbPlugin.PLUGIN_ID,
					IGdbDebugPreferenceConstants.PREF_DEFAULT_GDB_COMMAND,
					IGDBLaunchConfigurationConstants.DEBUGGER_DEBUG_NAME_DEFAULT, null);

			executable = configuration.getAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
					defaultGdbCommand);
			executable = DebugUtils.resolveAll(executable, configuration.getAttributes());

			ICConfigurationDescription buildConfig = EclipseUtils.getBuildConfigDescription(configuration);
			if (buildConfig != null) {
				executable = DebugUtils.resolveAll(executable, buildConfig);
			}

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		return executable;
	}

	public static String[] getGdbClientCommandLineArray(ILaunchConfiguration configuration) {

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
			other = configuration.getAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS,
					DefaultPreferences.getGdbClientOtherOptions()).trim();
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

		String fullCommand = getGdbClientCommand(config);
		return StringUtils.extractNameFromPath(fullCommand);
	}

	// ------------------------------------------------------------------------

	public static boolean getDoStartGdbServer(ILaunchConfiguration config) throws CoreException {

		return config.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
				DefaultPreferences.getGdbServerDoStart());
	}

	public static boolean getDoAddServerConsole(ILaunchConfiguration config) throws CoreException {

		return getDoStartGdbServer(config)
				&& config.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
						DefaultPreferences.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);
	}

	// ------------------------------------------------------------------------
}
