/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross.ui;

import org.eclipse.cdt.managedbuilder.core.IManagedBuildInfo;
import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IProject;

/**
 * Property tester for the Tools Paths page.
 * 
 * The project must be managed and the type must be one of the GNU ARM defined
 * type.
 */
public class GnuArmPropertyTester extends PropertyTester {

	private static final String TYPE_PREFIX = "ilg.gnuarmeclipse.managedbuild.cross.target.";

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		if ("isGnuArm".equals(property)) {
			if (receiver instanceof IProject) {

				IProject project = (IProject) receiver;
				IManagedBuildInfo info = ManagedBuildManager
						.getBuildInfo(project);
				if (info == null) {
					return false; // Not managed build
				}

				IManagedProject managedProject = info.getManagedProject();
				IProjectType projectType = managedProject.getProjectType();

				if (projectType != null
						&& projectType.getId().startsWith(TYPE_PREFIX)) {
					return true;
				}
			}
		}
		return false;
	}

}
