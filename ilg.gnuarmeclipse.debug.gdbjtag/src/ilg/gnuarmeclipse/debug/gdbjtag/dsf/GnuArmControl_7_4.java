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

package ilg.gnuarmeclipse.debug.gdbjtag.dsf;

import ilg.gnuarmeclipse.debug.gdbjtag.ILaunchConfigurationProvider;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

public class GnuArmControl_7_4 extends GnuArmControl_7_2 implements ILaunchConfigurationProvider {

	// ------------------------------------------------------------------------

	private ILaunchConfiguration fConfig;

	// ------------------------------------------------------------------------

	public GnuArmControl_7_4(DsfSession session, ILaunchConfiguration config, CommandFactory factory, String mode) {
		super(session, config, factory, mode);

		fConfig = config;
	}

	// ------------------------------------------------------------------------

	// Required by ILaunchConfigurationProvider

	@Override
	public ILaunchConfiguration getLaunchConfiguration() {
		return fConfig;
	}

	// ------------------------------------------------------------------------

	@Override
	public void setPrintPythonErrors(boolean enabled, RequestMonitor rm) {
		// With GDB 7.4, the command 'maintenance set python print-stack' has
		// been replaced by the new command
		// "set python print-stack none|full|message".
		// Bug 367788
		String errorOption = enabled ? "full" : "none"; //$NON-NLS-1$ //$NON-NLS-2$
		queueCommand(getCommandFactory().createMIGDBSetPythonPrintStack(getContext(), errorOption),
				new DataRequestMonitor<MIInfo>(getExecutor(), rm));
	}

	// ------------------------------------------------------------------------
}
