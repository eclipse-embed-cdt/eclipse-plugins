/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
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

package org.eclipse.embedcdt.debug.gdbjtag.core.dsf;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.embedcdt.debug.gdbjtag.core.ILaunchConfigurationProvider;

public class GnuMcuControl_7_4 extends GnuMcuControl_7_2 implements ILaunchConfigurationProvider {

	// ------------------------------------------------------------------------

	private ILaunchConfiguration fConfig;

	// ------------------------------------------------------------------------

	public GnuMcuControl_7_4(DsfSession session, ILaunchConfiguration config, CommandFactory factory, String mode) {
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
