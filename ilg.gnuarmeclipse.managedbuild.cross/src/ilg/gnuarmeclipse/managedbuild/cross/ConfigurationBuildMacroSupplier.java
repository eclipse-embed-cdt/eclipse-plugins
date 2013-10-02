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

public class ConfigurationBuildMacroSupplier implements
		IConfigurationBuildMacroSupplier {

	private String[] m_asCmds = { "cross_prefix", "cross_suffix", "cross_c",
			"cross_cpp", "cross_ar", "cross_objcopy", "cross_objdump",
			"cross_size", "cross_make", "cross_rm" };

	@Override
	public IBuildMacro getMacro(String macroName, IConfiguration configuration,
			IBuildMacroProvider provider) {

		for (String cmd : m_asCmds) {
			if (cmd.equals(macroName)) {
				IToolChain toolchain = configuration.getToolChain();

				String sId = Activator.getOptionPrefix() + ".command."
						+ cmd.replace("cross_", "");

				IOption option = toolchain.getOptionBySuperClassId(sId); //$NON-NLS-1$
				if (option != null) {
					String sVal = (String) option.getValue();

					return new BuildMacro(cmd, BuildMacro.VALUE_TEXT, sVal);
				}

				System.out.println("Missing value of "+sId);
				return null;
			}
		}
		// TODO Auto-generated method stub
		return null;
	}

	// Generate a set of configuration specific macros to pass the
	// toolchain commands (like ${cross_c}) to the make generator.

	@Override
	public IBuildMacro[] getMacros(IConfiguration configuration,
			IBuildMacroProvider provider) {

		IToolChain toolchain = configuration.getToolChain();
		ArrayList<IBuildMacro> oMacrosList = new ArrayList<IBuildMacro>();

		for (String cmd : m_asCmds) {
			String sId = Activator.getOptionPrefix() + ".command." + cmd.replace("cross_", "");

			IOption option = toolchain.getOptionBySuperClassId(sId); //$NON-NLS-1$
			if (option != null) {
				String sVal = (String) option.getValue();

				oMacrosList.add(new BuildMacro(cmd, BuildMacro.VALUE_TEXT, sVal));
			}
		}

		return oMacrosList.toArray(new IBuildMacro[0]);
	}

}
