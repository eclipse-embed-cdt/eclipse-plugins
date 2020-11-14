/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
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
 *******************************************************************************/

package org.eclipse.embedcdt.managedbuild.cross.riscv;

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;

/**
 * Property tester for the Tools Paths page.
 * 
 * The project must be managed and the type must be one of the GNU RISC-V
 * defined type.
 */
public class GnuRiscvPropertyTester extends PropertyTester {

	public static final String TYPE_PREFIX = "ilg.gnumcueclipse.managedbuild.cross.riscv.target.";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {

		if ("isGnuRiscv".equals(property)) {
			if (receiver instanceof IProject) {

				IProject project = (IProject) receiver;
				IManagedBuildInfo info = ManagedBuildManager.getBuildInfo(project);
				if (info == null) {
					return false; // Not managed build
				}

				IManagedProject managedProject = info.getManagedProject();
				IConfiguration[] cfgs = managedProject.getConfigurations();
				for (int i = 0; i < cfgs.length; ++i) {
					IToolChain toolchain = cfgs[i].getToolChain();
					if (toolchain == null) {
						continue;
					}
					IOption option = toolchain.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME);
					if (option == null) {
						continue;
					}
					try {
						String name = option.getStringValue();
						// Might be empty
						if (name != null) {
							return true;
						}
					} catch (BuildException e) {
						;
					}

				}

				return false;

				// IProjectType projectType = managedProject.getProjectType();
				//
				// if (projectType == null
				// || !projectType.getId().startsWith(TYPE_PREFIX)) {
				// return false;
				// }
				// return true;
			}
		}
		return false;
	}

}
