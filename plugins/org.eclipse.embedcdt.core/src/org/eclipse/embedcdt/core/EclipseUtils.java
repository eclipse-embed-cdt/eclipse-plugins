/*******************************************************************************
 * Copyright (c) 2014, 2020 Liviu Ionescu and others.
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
 *    Alexander Fedorov (ArSysOp) - extract UI part
 *******************************************************************************/

package org.eclipse.embedcdt.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.utils.spawner.ProcessFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.embedcdt.internal.core.Activator;

/**
 * Various utilities that use Eclipse global classes, grouped together more like
 * a reference.
 * <ul>
 * <li>Platform</li>
 * <li>PlatformUI</li>
 * <li>ManagedBuildManager</li>
 * <li>ImmediateExecutor</li>
 * </ul>
 *
 * Other interesting places to search for utility functions are:
 * <ul>
 * <li>Plugin</li>
 * </ul>
 *
 * For debugging, use
 *
 * <pre>
 * private static final boolean DEBUG_TWO =
 *     ExamplesPlugin.getDefault().isDebugging() &&
 *        "true".equalsIgnoreCase(Platform.getDebugOption(
 *        "org.eclipse.faq.examples/debug/option2"));
 *  ...
 *  if (DEBUG_TWO)
 *     System.out.println("Debug statement two.");
 * </pre>
 *
 * This will test two properties like
 * <ul>
 * <li>org.eclipse.faq.examples/debug=true</li>
 * <li>org.eclipse.faq.examples/debug/option2=true</li>
 * </ul>
 * These properties should be stored in a .option file in the plug-in root, or
 * in a custom file whose name is passed to the Eclipse -debug option.
 * <p>
 * See also the <a href=
 * "https://wiki.eclipse.org/FAQ_How_do_I_use_the_platform_debug_tracing_facility"
 * >Eclipse Wiki</a>.
 *
 */
public class EclipseUtils {

	// ------------------------------------------------------------------------

	private static final String PROPERTY_OS_NAME = "os.name"; //$NON-NLS-1$

	public static boolean isWindows() {

		// Platform.OS_WIN32 might be not appropriate, use shorter prefix
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase().startsWith("win");
	}

	public static boolean isWindowsXP() {
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase().equalsIgnoreCase("Windows XP");
	}

	public static boolean isLinux() {
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase().startsWith(Platform.OS_LINUX);
	}

	public static boolean isMacOSX() {
		// Platform.OS_MACOSX is not appropriate, since the returned value
		// contains spaces "Mac OS X".
		return System.getProperty(PROPERTY_OS_NAME).toLowerCase().startsWith("mac");
	}

	/**
	 * Get a short string to identify the OS Family.
	 *
	 * @return a String, one of "windows", "linux", "osx", "other".
	 */
	public static String getOsFamily() {

		if (isWindows()) {
			return "windows";
		} else if (isLinux()) {
			return "linux";
		} else if (isMacOSX()) {
			return "osx";
		} else {
			return "other";
		}
	}

	/**
	 * Get a platform specific key, where %s is replaced by the os family.
	 *
	 * @param key
	 * @return
	 */
	public static String getKeyOs(String key) {

		String os = getOsFamily();
		String keyOs = String.format(key, os);
		return keyOs;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the separator used to compose PATHs.
	 *
	 * @return a string.
	 */
	public static String getPathSeparator() {

		if (isWindows()) {
			return ";";
		} else {
			return ":";
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Find the project with the given project name.
	 *
	 * @param name a string with the project name
	 * @return the project or null, if not found
	 */
	public static IProject getProjectByName(String name) {

		return ResourcesPlugin.getWorkspace().getRoot().getProject(name);
	}

	/**
	 * Find the project associated with the given configuration.
	 *
	 * @param configuration
	 * @return the project or null, if not found.
	 */
	public static IProject getProjectByLaunchConfiguration(ILaunchConfiguration configuration) {
		ICConfigurationDescription buildConfig = EclipseUtils.getBuildConfigDescription(configuration);
		if (buildConfig != null) {
			return buildConfig.getProjectDescription().getProject();
		}
		return null;
	}

	// ------------------------------------------------------------------------

	public static IConfiguration getConfigurationFromDescription(ICConfigurationDescription configDescription) {
		return ManagedBuildManager.getConfigurationForDescription(configDescription);
	}

	// ------------------------------------------------------------------------

	/**
	 * Search the given scopes and return the non empty trimmed string or the
	 * default.
	 *
	 * @param pluginId     a string with the plugin id.
	 * @param key          a string with the key to search.
	 * @param defaultValue a string with the default, possibly null.
	 * @param contexts     an array of IScopeContext.
	 * @return a trimmed string or the given default, possibly null.
	 */
	public static String getPreferenceValueForId(String pluginId, String key, String defaultValue,
			IScopeContext[] contexts) {

		String value = null;
		String from = null;
		for (int i = 0; i < contexts.length; ++i) {
			value = contexts[i].getNode(pluginId).get(key, null);

			if (value != null) {
				value = value.trim();

				if (!value.isEmpty()) {
					from = contexts[i].getName();
					break;
				}
			}
		}

		if (value != null) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("EclipseUtils.getPreferenceValueForId(\"" + pluginId + "\", \"" + key + "\", \""
						+ defaultValue + "\") = \"" + value + "\" from " + from);
			}
			return value;
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("EclipseUtils.getPreferenceValueForId(\"" + pluginId + "\", \"" + key + "\", \""
					+ defaultValue + "\") = \"" + defaultValue + "\" default");
		}
		return defaultValue;
	}

	/**
	 * Compute a maximum array of scopes where to search for.
	 *
	 * @param project the IProject reference to the project, possibly null.
	 * @return an array of IScopeContext.
	 */
	public static IScopeContext[] getPreferenceScopeContexts(IProject project) {

		// If the project is known, the contexts searched will include the
		// specific ProjectScope.
		IScopeContext[] contexts;
		if (project != null) {
			contexts = new IScopeContext[] { new ProjectScope(project), InstanceScope.INSTANCE,
					ConfigurationScope.INSTANCE, DefaultScope.INSTANCE };
		} else {
			contexts = new IScopeContext[] { InstanceScope.INSTANCE, ConfigurationScope.INSTANCE,
					DefaultScope.INSTANCE };
		}
		return contexts;
	}

	/**
	 * Search all scopes and return the non empty trimmed string or the default.
	 *
	 * @param pluginId     a string with the plugin id.
	 * @param key          a string with the key to search.
	 * @param defaultValue a string with the default, possibly null.
	 * @param project      the IProject reference to the project, possibly null.
	 * @return a trimmed string or the given default, possibly null.
	 */
	public static String getPreferenceValueForId(String pluginId, String key, String defaultValue, IProject project) {

		IScopeContext[] contexts = getPreferenceScopeContexts(project);
		return getPreferenceValueForId(pluginId, key, defaultValue, contexts);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the variable value. The variables are accessible in the Run/Debug ->
	 * String Substitution preferences page.
	 *
	 * @param name a String with the variable name.
	 * @return a String with the variable value, or null if not found.
	 */
	public static String getVariableValue(String name) {

		IValueVariable variable = VariablesPlugin.getDefault().getStringVariableManager().getValueVariable(name);
		if (variable != null) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("EclipseUtils.getVariableValue(\"" + name + "\") = \"" + variable.getValue() + "\"");
			}
			return variable.getValue();
		} else {
			System.out.println("EclipseUtils.getVariableValue(\"" + name + "\" ) not found");
		}

		return null;
	}

	/**
	 * Set a variable value. The variables are accessible in the Run/Debug -> String
	 * Substitution preferences page.
	 *
	 * @param name  a String with the variable name.
	 * @param value a String with the variable value.
	 */
	public static void setVariableValue(String name, String value) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("EclipseUtils.setVariableValue(\"" + name + "\", \"" + value + "\")");
		}
		IValueVariable variable = VariablesPlugin.getDefault().getStringVariableManager().getValueVariable(name);
		if (variable != null) {
			variable.setValue(value);
		} else {
			System.out.println("EclipseUtils.setVariableValue(\"" + name + "\", \"" + value + "\") not set");
		}
	}

	/**
	 * Set a variable value. The variables are accessible in the Run/Debug -> String
	 * Substitution preferences page.
	 *
	 * If the variable does not exist, it is created. Unfortunately the 'Contributed
	 * By' field is not filled in.
	 *
	 * @param name        a String with the variable name.
	 * @param description a String with the variable description.
	 * @param value       a String with the variable value.
	 */
	public static void setVariableValue(String name, String description, String value) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"EclipseUtils.setVariableValue(\"" + name + "\", \"" + description + "\", \"" + value + "\")");
		}
		IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		IValueVariable variable = manager.getValueVariable(name);
		if (variable == null) {
			variable = manager.newValueVariable(name, description);
			try {
				manager.addVariables(new IValueVariable[] { variable });
			} catch (CoreException e) {
				Activator.log(e);
				variable = null;
			}
		}
		if (variable != null) {
			variable.setValue(value);
		} else {
			System.out.println("EclipseUtils.setVariableValue(\"" + name + "\", \"" + value + "\") not set");
		}
	}

	/**
	 * Get the configuration of a project.
	 *
	 * @param project
	 * @return array of configurations or null.
	 */
	public static IConfiguration[] getConfigurationsForProject(IProject project) {

		ICProjectDescription cProjectDescription = CoreModel.getDefault().getProjectDescription(project);
		if (cProjectDescription == null) {
			return null;
		}
		ICConfigurationDescription[] cfgs = cProjectDescription.getConfigurations();
		if (cfgs == null) {
			return null;
		}

		List<IConfiguration> list = new LinkedList<>();

		for (int i = 0; i < cfgs.length; ++i) {
			// System.out.println(cfgs[i].getName());
			IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
			if (info == null) {
				continue;
			}
			IConfiguration config = info.getManagedProject().getConfiguration(cfgs[i].getId());
			if (config == null) {
				continue;

			}
			list.add(config);
		}

		if (list.size() == 0) {
			return null;
		}

		return list.toArray(new IConfiguration[list.size()]);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the build configuration associated with the debug launch configuration,
	 * if defined in the first tab.
	 *
	 * @param config a debug launch configuration.
	 * @return the build configuration, or null if not found or not defined.
	 */
	public static ICConfigurationDescription getBuildConfigDescription(ILaunchConfiguration config) {

		ICConfigurationDescription cfg = null;

		// Get the project
		String projectName;
		try {
			projectName = config.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
			if (projectName == null) {
				return null;
			}
			projectName = projectName.trim();
			if (projectName.isEmpty()) {
				return null;
			}
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (project == null || !project.isAccessible()) {
				return null;
			}

			ICProjectDescription projDesc = CoreModel.getDefault().getProjectDescription(project, false);

			// Not a CDT project?
			if (projDesc == null) {
				return null;
			}

			String buildConfigID = config.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROJECT_BUILD_CONFIG_ID,
					""); //$NON-NLS-1$
			if (!buildConfigID.isEmpty()) {
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

	// ------------------------------------------------------------------------

	public static String performStringSubstitution(String expression) {

		if (expression == null || expression.isEmpty()) {
			return null;
		}

		// Resolve ${user.home}
		String result = expression;
		if (result.indexOf("${user.home}") >= 0) {
			String userHome = new Path(System.getProperty("user.home")).toString();
			userHome = Matcher.quoteReplacement(userHome);
			result = result.replaceAll("\\$\\{user.home\\}", userHome);
		}

		// If more macros remain, use the usual substituter.
		if (result.indexOf("${") >= 0) {
			IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
			try {
				result = variableManager.performStringSubstitution(result, false);
			} catch (CoreException e) {
				result = null;
			}
		}
		return result;
	}

	// ------------------------------------------------------------------------

	public static IProject getProjectFromConfiguration(IConfiguration config) {
		ICConfigurationDescription configDesc = ManagedBuildManager.getDescriptionForConfiguration(config);
		IProject project = configDesc.getProjectDescription().getProject();
		return project;
	}

	// ------------------------------------------------------------------------

	private static String[] gShellEnvironmentCache = null;

	/**
	 * Get the shell environment, to overcome the different PATH
	 * in graphical environments (like on macOS).
	 *
	 * It assumes that the shell implements the `env` command.
	 */
	public static String[] getShellEnvironment() {

		if (gShellEnvironmentCache != null) {
			return gShellEnvironmentCache;
		}

		List<String> envList = new ArrayList<>();

		String shell = System.getenv("SHELL");
		if (shell == null || shell.trim().isEmpty()) {
			System.out.println("No SHELL in environment");
			return null;
		}

		String cmdArray[] = new String[4];
		cmdArray[0] = shell;
		cmdArray[1] = "-i";
		cmdArray[2] = "-c";
		cmdArray[3] = "env";

		if (Activator.getInstance().isDebugging()) {
			System.out.println("> " + StringUtils.join(cmdArray, " "));
		}

		// Inherit from parent process.
		String envp[] = null;

		List<String> outputLines = new ArrayList<>();
		try {
			BufferedReader reader = null;
			Process process = ProcessFactory.getFactory().exec(cmdArray, envp);
			reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				outputLines.add(line);
			}

			if (Activator.getInstance().isDebugging()) {
				for (String l : outputLines) {
					System.out.println(l);
				}
			}

			process.destroy();
			if (process.exitValue() != 0) {
				if (Activator.getInstance().isDebugging()) {
					System.out.println("exit (" + process.exitValue() + ")");
				}

				return null;
			}
		} catch (IOException e) {
			return null;
		}

		for (String line : outputLines) {
			if (line.contains("=")) {
				envList.add(line);
			}
		}

		gShellEnvironmentCache = envList.toArray(new String[envList.size()]);
		return gShellEnvironmentCache;
	}

	// ------------------------------------------------------------------------
}
