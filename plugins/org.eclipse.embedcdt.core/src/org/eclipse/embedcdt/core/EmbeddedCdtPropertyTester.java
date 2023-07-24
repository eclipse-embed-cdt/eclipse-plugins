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

package org.eclipse.embedcdt.core;

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
 * The project must be managed and have an options with the toolchain name.
 *
 * This class deprecates org.eclipse.embedcdt.managedbuild.cross.core.GnuMcuPropertyTester.
 */
public class EmbeddedCdtPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {

		if ("isEmbeddedCdt".equals(property)) {
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

					IOption options[] = toolchain.getOptions();
					for (IOption opt : options) {
						String id = opt.getId();
						// System.out.println(opt.getId());
						if (id.indexOf(".managedbuild.cross.") >= 0 && id.indexOf(".toolchain.name.") >= 0) {
							try {
								String name = opt.getStringValue();
								// Might be empty
								if (name != null) {
									return true;
								}
							} catch (BuildException e) {

							}
						}
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
