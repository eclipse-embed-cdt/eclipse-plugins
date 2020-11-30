/*******************************************************************************
 * Copyright (c) 2016 Liviu Ionescu.
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
 *     Jonah Graham - fix for Neon
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.core.dsf;

import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFLaunchConfigurationDelegate;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @since 3.0
 */
public abstract class AbstractGnuMcuLaunchConfigurationDelegate extends GDBJtagDSFLaunchConfigurationDelegate {

	// This is the first method to be called in the launch sequence, even before
	// preLaunchCheck()
	// If we cancel the launch, we need to cleanup what is allocated in this
	// method. The cleanup
	// can be performed by GdbLaunch.shutdownSession()
	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		GdbLaunch launch = createGdbLaunch(configuration, mode, null);
		// Don't initialize the GdbLaunch yet to avoid needing to cleanup.
		// We will initialize the launch once we know it will proceed and
		// that we need to start using it.

		// Need to configure the source locator before returning the launch
		// because once the launch is created and added to the launch manager,
		// the adapters will be created for the whole session, including
		// the source lookup adapter.
		launch.setSourceLocator(getSourceLocator(configuration, launch.getSession()));
		return launch;
	}

}
