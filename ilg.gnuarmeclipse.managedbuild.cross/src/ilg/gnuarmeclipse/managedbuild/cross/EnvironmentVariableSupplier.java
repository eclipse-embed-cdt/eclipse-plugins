/*******************************************************************************
 * Copyright (c) 2009, 2013 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Doug Schaefer - initial API and implementation
 *     Liviu Ionescu - ARM version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross;

import ilg.gnuarmeclipse.managedbuild.cross.ui.ProjectStorage;
import ilg.gnuarmeclipse.managedbuild.cross.ui.EclipsePreferences;

import java.io.File;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.envvar.IBuildEnvironmentVariable;
import org.eclipse.cdt.managedbuilder.envvar.IConfigurationEnvironmentVariableSupplier;
import org.eclipse.cdt.managedbuilder.envvar.IEnvironmentVariableProvider;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.core.runtime.Platform;

public class EnvironmentVariableSupplier implements
		IConfigurationEnvironmentVariableSupplier {

	public IBuildEnvironmentVariable getVariable(String variableName,
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		if (PathEnvironmentVariable.isVar(variableName)) {
			return PathEnvironmentVariable.create(configuration);
		} else {
			// System.out.println("getVariable(" + variableName + ","
			// + configuration.getName() + ") returns null");
			return null;
		}
	}

	public IBuildEnvironmentVariable[] getVariables(
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		IBuildEnvironmentVariable path = PathEnvironmentVariable
				.create(configuration);
		if (path != null) {
			return new IBuildEnvironmentVariable[] { path };
		} else {
			// System.out.println("getVariables(" + configuration.getName()
			// + ") returns empty array");
			return new IBuildEnvironmentVariable[0];
		}
	}

	private static class PathEnvironmentVariable implements
			IBuildEnvironmentVariable {

		public static String name = "PATH"; //$NON-NLS-1$

		private File path;

		private PathEnvironmentVariable(File path) {
			// System.out.println("cpath=" + path);
			this.path = path;
		}

		@SuppressWarnings("unused")
		public static PathEnvironmentVariable create(
				IConfiguration configuration) {
			IToolChain toolchain = configuration.getToolChain();

			String path = null;

			boolean isPathPerProject = ProjectStorage
					.isToolchainPathPerProject(configuration);

			if (isPathPerProject) {
				path = ProjectStorage.getToolchainPath(configuration);
			} else {
				IOption option;
				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME); //$NON-NLS-1$
				String toolchainName = (String) option.getValue();

				path = EclipsePreferences.getToolchainPath(toolchainName);
			}

			if (path != null) {
				path = path.trim();

				if (path.length() > 0) {

					// if present, substitute macros
					if (path.indexOf("${") >= 0) {
						path = resolveMacros(path, configuration);
					}

					File sysroot = new File(path);
					File bin = new File(sysroot, "bin"); //$NON-NLS-1$
					if (bin.isDirectory())
						sysroot = bin;
					if (false) {
						System.out.println("PATH="
								+ sysroot
								+ " opt="
								+ path
								+ " cfg="
								+ configuration
								+ " prj="
								+ configuration.getManagedProject().getOwner()
										.getName());
					}
					return new PathEnvironmentVariable(sysroot);
				}
			}
			// System.out.println("create(" + configuration.getName()
			// + ") returns null");
			return null;
		}

		private static String resolveMacros(String str,
				IConfiguration configuration) {

			String result = str;
			try {
				result = ManagedBuildManager
						.getBuildMacroProvider()
						.resolveValue(
								str,
								"", " ", IBuildMacroProvider.CONTEXT_CONFIGURATION, configuration); //$NON-NLS-1$	//$NON-NLS-2$
			} catch (CdtVariableException e) {
				System.out.println("resolveMacros " + e.getMessage());
			}

			return result;

		}

		public static boolean isVar(String name) {
			// Windows has case insensitive env var names
			return Platform.getOS().equals(Platform.OS_WIN32) ? name
					.equalsIgnoreCase(PathEnvironmentVariable.name) : name
					.equals(PathEnvironmentVariable.name);
		}

		public String getDelimiter() {
			return Platform.getOS().equals(Platform.OS_WIN32) ? ";" : ":"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		public String getName() {
			return name;
		}

		public int getOperation() {
			return IBuildEnvironmentVariable.ENVVAR_PREPEND;
		}

		public String getValue() {
			return path.getAbsolutePath();
		}

	}
}
