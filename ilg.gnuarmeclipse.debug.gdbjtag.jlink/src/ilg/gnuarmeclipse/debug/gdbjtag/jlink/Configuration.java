package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

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

			executable = DebugUtils.resolveAll(executable,
					configuration.getAttributes());

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

			String connection = configuration.getAttribute(
					ConfigurationAttributes.GDB_SERVER_CONNECTION,
					ConfigurationAttributes.GDB_SERVER_CONNECTION_DEFAULT);
			String connectionAddress = configuration
					.getAttribute(
							ConfigurationAttributes.GDB_SERVER_CONNECTION_ADDRESS,
							ConfigurationAttributes.GDB_SERVER_CONNECTION_ADDRESS_DEFAULT);

			connectionAddress = DebugUtils.resolveAll(connectionAddress,
					configuration.getAttributes());
			if (connectionAddress.length() > 0) {
				if (ConfigurationAttributes.GDB_SERVER_CONNECTION_USB
						.equals(connection)) {

					lst.add("-select");
					lst.add("usb=" + connectionAddress);
				} else if (ConfigurationAttributes.GDB_SERVER_CONNECTION_IP
						.equals(connection)) {
					lst.add("-select");
					lst.add("ip=" + connectionAddress);
				}
			}

			lst.add("-if");
			lst.add(configuration.getAttribute(
					ConfigurationAttributes.GDB_SERVER_DEBUG_INTERFACE,
					ConfigurationAttributes.INTERFACE_DEFAULT));

			String defaultName = configuration.getAttribute(
					ConfigurationAttributes.FLASH_DEVICE_NAME_COMPAT,
					ConfigurationAttributes.FLASH_DEVICE_NAME_DEFAULT);

			String name = configuration
					.getAttribute(
							ConfigurationAttributes.GDB_SERVER_DEVICE_NAME,
							defaultName).trim();
			name = DebugUtils.resolveAll(name, configuration.getAttributes());
			if (name.length() > 0) {
				lst.add("-device");
				lst.add(name);
			}

			lst.add("-endian");
			lst.add(configuration.getAttribute(
					ConfigurationAttributes.GDB_SERVER_DEVICE_ENDIANNESS,
					ConfigurationAttributes.ENDIANNESS_DEFAULT));

			lst.add("-speed");
			lst.add(configuration.getAttribute(
					ConfigurationAttributes.GDB_SERVER_DEVICE_SPEED,
					ConfigurationAttributes.GDB_SERVER_SPEED_DEFAULT));

			lst.add("-port");
			lst.add(Integer.toString(configuration.getAttribute(
					ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
					ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT)));

			lst.add("-swoport");
			lst.add(Integer.toString(configuration.getAttribute(
					ConfigurationAttributes.GDB_SERVER_SWO_PORT_NUMBER,
					ConfigurationAttributes.GDB_SERVER_SWO_PORT_NUMBER_DEFAULT)));

			lst.add("-telnetport");
			lst.add(Integer.toString(configuration
					.getAttribute(
							ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
							ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT)));

			if (configuration
					.getAttribute(
							ConfigurationAttributes.DO_GDB_SERVER_VERIFY_DOWNLOAD,
							ConfigurationAttributes.DO_GDB_SERVER_VERIFY_DOWNLOAD_DEFAULT)) {
				lst.add("-vd");
			}

			if (configuration.getAttribute(
					ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
					ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT)) {
				lst.add("-noreset");
				lst.add("-noir");
			} else {
				if (configuration
						.getAttribute(
								ConfigurationAttributes.DO_GDB_SERVER_INIT_REGS,
								ConfigurationAttributes.DO_GDB_SERVER_INIT_REGS_DEFAULT)) {
					lst.add("-ir");
				} else {
					lst.add("-noir");
				}
			}

			lst.add("-localhostonly");
			if (configuration.getAttribute(
					ConfigurationAttributes.DO_GDB_SERVER_LOCAL_ONLY,
					ConfigurationAttributes.DO_GDB_SERVER_LOCAL_ONLY_DEFAULT)) {
				lst.add("1");
			} else {
				lst.add("0");
			}

			if (configuration.getAttribute(
					ConfigurationAttributes.DO_GDB_SERVER_SILENT,
					ConfigurationAttributes.DO_GDB_SERVER_SILENT_DEFAULT)) {
				lst.add("-silent");
			}

			String logFile = configuration.getAttribute(
					ConfigurationAttributes.GDB_SERVER_LOG,
					ConfigurationAttributes.GDB_SERVER_LOG_DEFAULT).trim();

			logFile = DebugUtils.resolveAll(logFile,
					configuration.getAttributes());
			if (logFile.length() > 0) {
				lst.add("-log");

				// lst.add(Utils.escapeWhitespaces(logFile));
				lst.add(logFile);
			}

			String other = configuration.getAttribute(
					ConfigurationAttributes.GDB_SERVER_OTHER,
					ConfigurationAttributes.GDB_SERVER_OTHER_DEFAULT).trim();
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
		String fullCommand = getGdbServerCommand(config);
		if (fullCommand == null)
			return null;

		String parts[] = fullCommand.trim().split("" + Path.SEPARATOR);
		return parts[parts.length - 1];
	}

	public static String getGdbServerDeviceName(ILaunchConfiguration config)
			throws CoreException {

		return config.getAttribute(
				ConfigurationAttributes.GDB_SERVER_DEVICE_NAME,
				ConfigurationAttributes.FLASH_DEVICE_NAME_DEFAULT).trim();
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
			executable = DebugUtils.resolveAll(executable,
					configuration.getAttributes());

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
			other = DebugUtils.resolveAll(other, configuration.getAttributes());
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

	public static boolean getDoStartGdbServer(ILaunchConfiguration config)
			throws CoreException {

		return config.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
				ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT);
	}

	public static boolean getDoAddServerConsole(ILaunchConfiguration config)
			throws CoreException {

		return getDoStartGdbServer(config)
				&& config
						.getAttribute(
								ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
								ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);
	}

	public static boolean getDoAddSemihostingConsole(ILaunchConfiguration config)
			throws CoreException {

		return getDoStartGdbServer(config)
				&& config
						.getAttribute(
								ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
								ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT);
	}

	// ------------------------------------------------------------------------
}
