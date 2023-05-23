/*******************************************************************************
 * Copyright (c) 2013, 2023 Liviu Ionescu and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Liviu Ionescu - initial version
 *     John Dallaway - provide GNU tool prefix for CDT GNU tool factory (#567)
 *******************************************************************************/

package org.eclipse.embedcdt.managedbuild.cross.riscv.core;

import java.util.ArrayList;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.internal.macros.BuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.macros.IConfigurationBuildMacroSupplier;
import org.eclipse.embedcdt.internal.managedbuild.cross.riscv.core.Activator;

@SuppressWarnings("restriction")
public class ConfigurationBuildMacroSupplier implements IConfigurationBuildMacroSupplier {

	// ------------------------------------------------------------------------

	private String[] fCmds = { "cross_prefix", "cross_suffix", "cross_c", "cross_cpp", "cross_ar", "cross_objcopy",
			"cross_objdump", "cross_size", "cross_make", "cross_rm" };

	private static String CROSS_FLAGS = "cross_toolchain_flags";
	private static String GNU_TOOL_PREFIX = "gnu_tool_prefix"; // TODO: use IGnuToolFactory.GNU_TOOL_PREFIX_VARIABLE (since CDT 11.2)

	// ------------------------------------------------------------------------

	@Override
	public IBuildMacro getMacro(String macroName, IConfiguration configuration, IBuildMacroProvider provider) {

		for (String sCmd : fCmds) {
			if (sCmd.equals(macroName)) {
				IToolChain toolchain = configuration.getToolChain();

				String sId = Option.OPTION_PREFIX + ".command." + sCmd.replace("cross_", "");

				IOption option = toolchain.getOptionBySuperClassId(sId); // $NON-NLS-1$
				if (option != null) {
					String sVal = (String) option.getValue();

					// System.out.println("Macro "
					// + sCmd
					// + "="
					// + sVal
					// + " cfg="
					// + configuration
					// + " prj="
					// + configuration.getManagedProject().getOwner()
					// .getName());
					return new BuildMacro(macroName, BuildMacro.VALUE_TEXT, sVal);
				}

				if (Activator.getInstance().isDebugging()) {
					System.out.println("riscv.ConfigurationBuildMacroSupplier.getMacro() Missing value of " + sId);
				}
				return null;
			}
		}

		if (CROSS_FLAGS.equals(macroName)) {
			String sValue = Option.getToolChainFlags(configuration);
			if (sValue != null && sValue.length() > 0) {
				return new BuildMacro(macroName, BuildMacro.VALUE_TEXT, sValue);
			}
		}

		if (GNU_TOOL_PREFIX.equals(macroName)) {
			String sValue = Option.getOptionStringValue(configuration, Option.OPTION_PREFIX + ".command.prefix");
			if (sValue != null && sValue.length() > 0) {
				return new BuildMacro(GNU_TOOL_PREFIX, IBuildMacro.VALUE_TEXT, sValue);
			}
		}

		// System.out.println("Missing value of " + macroName + " in "
		// + configuration.getName());
		return null;
	}

	// Generate a set of configuration specific macros to pass the
	// toolchain commands (like ${cross_c}) to the make generator.

	@Override
	public IBuildMacro[] getMacros(IConfiguration configuration, IBuildMacroProvider provider) {

		IToolChain toolchain = configuration.getToolChain();
		ArrayList<IBuildMacro> oMacrosList = new ArrayList<>();

		String sValue;
		for (String cmd : fCmds) {
			String sId = Option.OPTION_PREFIX + ".command." + cmd.replace("cross_", "");

			IOption option = toolchain.getOptionBySuperClassId(sId); // $NON-NLS-1$
			if (option != null) {
				sValue = (String) option.getValue();

				oMacrosList.add(new BuildMacro(cmd, BuildMacro.VALUE_TEXT, sValue));
			}
		}

		sValue = Option.getToolChainFlags(configuration);
		if (sValue != null && sValue.length() > 0) {
			oMacrosList.add(new BuildMacro(CROSS_FLAGS, BuildMacro.VALUE_TEXT, sValue));
		}

		IBuildMacro gnuToolPrefixMacro = getMacro(GNU_TOOL_PREFIX, configuration, provider);
		if (gnuToolPrefixMacro != null) {
			oMacrosList.add(gnuToolPrefixMacro);
		}

		return oMacrosList.toArray(new IBuildMacro[0]);
	}

	// ------------------------------------------------------------------------
}
