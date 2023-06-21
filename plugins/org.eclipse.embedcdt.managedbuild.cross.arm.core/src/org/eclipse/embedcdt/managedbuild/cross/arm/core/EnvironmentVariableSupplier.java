/*******************************************************************************
 * Copyright (c) 2009, 2023 Wind River Systems, Inc. and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Doug Schaefer - initial API and implementation
 *     Liviu Ionescu - Arm version
 *******************************************************************************/

package org.eclipse.embedcdt.managedbuild.cross.arm.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.embedcdt.internal.managedbuild.cross.arm.core.Activator;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.PersistentPreferences;

public class EnvironmentVariableSupplier implements IConfigurationEnvironmentVariableSupplier {

	// ------------------------------------------------------------------------

	private static boolean DEBUG_PATH = true;

	// ------------------------------------------------------------------------

	@Override
	public IBuildEnvironmentVariable getVariable(String variableName, IConfiguration configuration,
			IEnvironmentVariableProvider provider) {
		if (PathEnvironmentVariable.isVar(variableName)) {
			return PathEnvironmentVariable.create(configuration);
		} else {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("arm.EnvironmentVariableSupplier.getVariable(" + variableName + ","
						+ configuration.getName() + ") returns null");
			}
			return null;
		}
	}

	@Override
	public IBuildEnvironmentVariable[] getVariables(IConfiguration configuration,
			IEnvironmentVariableProvider provider) {
		IBuildEnvironmentVariable path = PathEnvironmentVariable.create(configuration);
		if (path != null) {
			return new IBuildEnvironmentVariable[] { path };
		} else {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("arm.EnvironmentVariableSupplier.getVariables(" + configuration.getName()
						+ ") returns empty array");
			}
			return new IBuildEnvironmentVariable[0];
		}
	}

	private static class PathEnvironmentVariable implements IBuildEnvironmentVariable {

		public static String name = "PATH"; //$NON-NLS-1$

		private String path;

		private PathEnvironmentVariable(String path) {
			// System.out.println("cpath=" + path);
			this.path = path;
		}

		public static PathEnvironmentVariable create(IConfiguration configuration) {
			IToolChain toolchain = configuration.getToolChain();

			IProject project = (IProject) configuration.getManagedProject().getOwner();

			Boolean preferXpacksBin = Option.getOptionBooleanValue(configuration, Option.OPTION_PREFER_XPACKS_BIN);

			List<String> path = new ArrayList<>();
			if (preferXpacksBin) {
				IPath projectPath = project.getWorkspace().getRoot().getLocation().append(project.getFullPath());
				IPath xpackBinPath = projectPath.append("xpacks").append(".bin");

				path.add(xpackBinPath.toOSString());
			}

			// Get the build tools path from the common store.
			PersistentPreferences commonPersistentPreferences = org.eclipse.embedcdt.internal.managedbuild.cross.core.Activator
					.getInstance().getPersistentPreferences();

			PersistentPreferences deprecatedPersistentPreferences = new PersistentPreferences(
					"ilg.gnuarmeclipse.managedbuild.cross");

			// Get the build tools path from the common store.
			String buildToolsPath = commonPersistentPreferences.getBuildToolsPath(project);
			if (buildToolsPath.isEmpty()) {
				buildToolsPath = deprecatedPersistentPreferences.getBuildToolsPath(project);
			}

			if (!buildToolsPath.isEmpty()) {
				path.add(buildToolsPath);
			}

			IOption optionId;
			optionId = toolchain.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_ID); // $NON-NLS-1$
			String toolchainId = (String) optionId.getValue();

			IOption optionName;
			optionName = toolchain.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME); // $NON-NLS-1$
			String toolchainName = (String) optionName.getValue();

			String toolchainPath = "";

			// Get the toolchain path from this plug-in store.
			PersistentPreferences persistentPreferences = Activator.getInstance().getPersistentPreferences();
			// Get the most specific toolchain path (project, workspace,
			// Eclipse, defaults).
			toolchainPath = persistentPreferences.getToolchainPath(toolchainId, toolchainName, project);
			if (toolchainPath.isEmpty()) {
				// Try to get from original gnuarmeclipse store.
				toolchainPath = deprecatedPersistentPreferences.getToolchainPath(toolchainId, toolchainName, project);
			}

			if (!toolchainPath.isEmpty()) {
				path.add(toolchainPath);
			}

			if (!path.isEmpty()) {
				// if present, substitute macros
				Stream<String> macrosResolved = path.stream().map(p -> {
					if (p.indexOf("${") >= 0) {
						p = resolveMacros(p, configuration);
					}
					return p;
				});
				// filter out empty entries and convert string to absolute paths
				String pathVar = macrosResolved.filter(Predicate.not(String::isBlank)).map(File::new)
						.map(File::getAbsolutePath).collect(Collectors.joining(File.pathSeparator));

				if (DEBUG_PATH) {
					if (Activator.getInstance().isDebugging()) {
						System.out.println("arm.PathEnvironmentVariable.create() PATH=\"" + pathVar + "\" cfg="
								+ configuration + " prj=" + configuration.getManagedProject().getOwner().getName());
					}
				}
				return new PathEnvironmentVariable(pathVar);
			}

			if (Activator.getInstance().isDebugging()) {
				System.out.println("arm.PathEnvironmentVariable.create(" + configuration.getName() + ") returns null");
			}
			return null;
		}

		private static String resolveMacros(String str, IConfiguration configuration) {

			String result = str;
			try {
				result = ManagedBuildManager.getBuildMacroProvider().resolveValue(str, "", " ", //$NON-NLS-1$ //$NON-NLS-2$
						IBuildMacroProvider.CONTEXT_CONFIGURATION, configuration);
			} catch (CdtVariableException e) {
				Activator.log("arm.PathEnvironmentVariable.resolveMacros " + e.getMessage());
			}

			if (Activator.getInstance().isDebugging()) {
				Activator.log("arm.PathEnvironmentVariable.resolveMacros(\"" + str + "\", \"" + configuration.getName()
						+ "\") = \"" + "\"");
			}
			return result;

		}

		public static boolean isVar(String name) {
			// Windows has case insensitive env var names
			return Platform.getOS().equals(Platform.OS_WIN32) ? name.equalsIgnoreCase(PathEnvironmentVariable.name)
					: name.equals(PathEnvironmentVariable.name);
		}

		@Override
		public String getDelimiter() {
			return Platform.getOS().equals(Platform.OS_WIN32) ? ";" : ":"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public int getOperation() {
			return IBuildEnvironmentVariable.ENVVAR_PREPEND;
		}

		@Override
		public String getValue() {
			return path;
		}

	}

	// ------------------------------------------------------------------------
}
