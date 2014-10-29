/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.dsf;

import ilg.gnuarmeclipse.debug.gdbjtag.ILaunchConfigurationProvider;

import org.eclipse.cdt.dsf.gdb.service.command.GDBControl_7_4;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

public class GnuArmControl_7_4 extends GDBControl_7_4 implements
		ILaunchConfigurationProvider {

	// ------------------------------------------------------------------------

	private ILaunchConfiguration fConfig;

	// ------------------------------------------------------------------------

	public GnuArmControl_7_4(DsfSession session, ILaunchConfiguration config,
			CommandFactory factory) {
		super(session, config, factory);
		fConfig = config;
	}

	// ------------------------------------------------------------------------

	@Override
	public ILaunchConfiguration getLaunchConfiguration() {
		return fConfig;
	}

	// ------------------------------------------------------------------------
}
