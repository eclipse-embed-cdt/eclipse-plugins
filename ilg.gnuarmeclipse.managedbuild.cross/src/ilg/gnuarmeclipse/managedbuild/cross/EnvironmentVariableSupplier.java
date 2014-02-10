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

import ilg.gnuarmeclipse.managedbuild.cross.ui.SharedStorage;

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
		if (PathEnvironmentVariable.isVar(variableName))
			return PathEnvironmentVariable.create(configuration);
		else
			return null;
	}

	public IBuildEnvironmentVariable[] getVariables(
			IConfiguration configuration, IEnvironmentVariableProvider provider) {
		IBuildEnvironmentVariable path = PathEnvironmentVariable
				.create(configuration);
		return path != null ? new IBuildEnvironmentVariable[] { path }
				: new IBuildEnvironmentVariable[0];
	}

	private static class PathEnvironmentVariable implements
			IBuildEnvironmentVariable {

		public static String name = "PATH"; //$NON-NLS-1$

		private File path;

		private PathEnvironmentVariable(File path) {
			// System.out.println("cpath=" + path);
			this.path = path;
		}

		public static PathEnvironmentVariable create(
				IConfiguration configuration) {
			IToolChain toolchain = configuration.getToolChain();

			String path;

			IOption option;
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_DO_PREFER_GLOBAL_PATH); //$NON-NLS-1$

			Boolean doPreferGlobal = (Boolean) option.getValue();

			if (doPreferGlobal != null && doPreferGlobal) {
				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME); //$NON-NLS-1$
				String toolchainName = (String)option.getValue();
				
				path = SharedStorage.getToolchainPath(toolchainName);
			} else {
				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_PATH); //$NON-NLS-1$
				path = (String) option.getValue();
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
					// System.out.println("path=" + sysroot + " opt=" + path +
					// " cfg="
					// + configuration + " prj="
					// +
					// configuration.getManagedProject().getOwner().getName());
					return new PathEnvironmentVariable(sysroot);
				}
			}
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
