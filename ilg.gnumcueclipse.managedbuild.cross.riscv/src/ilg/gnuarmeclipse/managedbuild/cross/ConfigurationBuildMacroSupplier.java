/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross;

import java.util.ArrayList;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.internal.macros.BuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacro;
import org.eclipse.cdt.managedbuilder.macros.IBuildMacroProvider;
import org.eclipse.cdt.managedbuilder.macros.IConfigurationBuildMacroSupplier;

@SuppressWarnings("restriction")
public class ConfigurationBuildMacroSupplier implements IConfigurationBuildMacroSupplier {

	// ------------------------------------------------------------------------

	private String[] fCmds = { "cross_prefix", "cross_suffix", "cross_c", "cross_cpp", "cross_ar", "cross_objcopy",
			"cross_objdump", "cross_size", "cross_make", "cross_rm" };

	private static String CROSS_FLAGS = "cross_toolchain_flags";

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
					System.out.println("Missing value of " + sId);
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
		// System.out.println("Missing value of " + macroName + " in "
		// + configuration.getName());
		return null;
	}

	// Generate a set of configuration specific macros to pass the
	// toolchain commands (like ${cross_c}) to the make generator.

	@Override
	public IBuildMacro[] getMacros(IConfiguration configuration, IBuildMacroProvider provider) {

		IToolChain toolchain = configuration.getToolChain();
		ArrayList<IBuildMacro> oMacrosList = new ArrayList<IBuildMacro>();

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

		return oMacrosList.toArray(new IBuildMacro[0]);
	}

	// ------------------------------------------------------------------------
}
