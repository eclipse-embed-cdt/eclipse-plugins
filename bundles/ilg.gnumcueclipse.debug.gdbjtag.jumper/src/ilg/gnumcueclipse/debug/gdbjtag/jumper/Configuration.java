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

package ilg.gnumcueclipse.debug.gdbjtag.jumper;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.core.StringUtils;
import ilg.gnumcueclipse.debug.gdbjtag.DebugUtils;
import ilg.gnumcueclipse.debug.gdbjtag.jumper.preferences.DefaultPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.IGdbDebugPreferenceConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.launching.LaunchUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;

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
	
	private static String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec("whoami");
			p.waitFor();
			BufferedReader reader = 
                           new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}
	
	public static String findExecutableOnPath(String name) {
		String path = System.getenv("PATH") + File.pathSeparator + "/usr/local/bin";
	    for (String dirname : path.split(File.pathSeparator)) {
	        File file = new File(dirname, name);
	        if (file.isFile() && file.canExecute()) {
	            return file.getAbsolutePath();
	        }
	    }
	    
	    throw new AssertionError("Jumper Virtual Lab was not installed. Please head to https://docs.jumper.io for installation instructions.");	    	
	}

	public static String[] getGdbServerCommandLineArray(ILaunchConfiguration configuration) {

		List<String> lst = new ArrayList<String>();

		try {
			DefaultPreferences fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
			if (!configuration.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER,
					fDefaultPreferences.getGdbServerDoStart()))
				return null;

			String executable = getGdbServerCommand(configuration, null);
			if (executable == null || executable.length() == 0)
				return null;
			
			if (EclipseUtils.isWindows()) {
				executable += ".exe";
			}
			
			String pathToExe = findExecutableOnPath(executable);

			lst.add(pathToExe);
			lst.add("run");

			
//			ICProject project = verifyCProject(configuration);
			// Now verify we know the program to debug.
			IPath exePath = LaunchUtils.verifyProgramPath(configuration, null);
			// To allow users to debug with binary parsers turned off, we don't call
			// LaunchUtils.verifyBinary here. Instead we simply rely on the debugger to
			// report any issues with the binary.

			if (exePath != null) {
				lst.add("--fw=" + exePath);
			}
			
			String boardName = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_NAME, "").trim();
			boardName = DebugUtils.resolveAll(boardName, configuration.getAttributes());
			if (!boardName.isEmpty()) {
				lst.add("--platform=" + boardName);
			}

			String deviceName = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_NAME, "").trim();
			deviceName = DebugUtils.resolveAll(deviceName, configuration.getAttributes());
			if (!deviceName.isEmpty()) {
				File file = new File(deviceName);
				
				if ((file.exists()) && (file.getParent() != null)) {
					lst.add("--directory=" + file.getParent());					
				}
			} else {
				File file = new File(exePath.toString());

				lst.add("--directory=" + file.getParent());					
			}

			lst.add("--gdb");
			
			String other = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER,
					DefaultPreferences.SERVER_OTHER_OPTIONS_DEFAULT).trim();

			other = DebugUtils.resolveAll(other, configuration.getAttributes());

			if (!other.isEmpty()) {
				lst.addAll(StringUtils.splitCommandLineOptions(other));
			}

//			boolean nographic = configuration.getAttribute(ConfigurationAttributes.DISABLE_GRAPHICS,
//					DefaultPreferences.DISABLE_GRAPHICS_DEFAULT);
//			if (nographic) {
//				lst.add("--nographic");
//			}

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

	public static String getGdbServerOtherConfig(ILaunchConfiguration config) throws CoreException {

		return config
				.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, DefaultPreferences.SERVER_OTHER_OPTIONS_DEFAULT)
				.trim();
	}

	public static String getJumperBoardName(ILaunchConfiguration config) throws CoreException {

		return config.getAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_NAME, "").trim();
	}

	public static String getJumperDeviceName(ILaunchConfiguration config) throws CoreException {

		return config.getAttribute(ConfigurationAttributes.GDB_SERVER_DEVICE_NAME, "").trim();
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
		if (value.length() == 0)
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
			System.out.println("jumper.resolveAll(\"" + str + "\") = \"" + value + "\"");
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

	// ------------------------------------------------------------------------
}
