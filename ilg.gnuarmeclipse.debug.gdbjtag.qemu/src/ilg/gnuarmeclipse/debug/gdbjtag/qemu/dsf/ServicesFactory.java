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

package ilg.gnuarmeclipse.debug.gdbjtag.qemu.dsf;

import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmDebuggerCommandsService;
import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmGdbServerBackend;
import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmServicesFactory;

import org.eclipse.cdt.dsf.mi.service.IMIBackend;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ServicesFactory extends GnuArmServicesFactory {

	// ------------------------------------------------------------------------

	@SuppressWarnings("unused")
	private final String fVersion;

	// ------------------------------------------------------------------------

	public ServicesFactory(String version) {
		super(version);

		System.out.println("ServicesFactory(" + version + ") " + this);
		fVersion = version;
	}

	// ------------------------------------------------------------------------

	protected IMIBackend createBackendGDBService(DsfSession session,
			ILaunchConfiguration lc) {
		return new GdbBackend(session, lc);
	}

	@Override
	protected GnuArmGdbServerBackend createGdbServerBackendService(
			DsfSession session, ILaunchConfiguration lc) {
		return new GdbServerBackend(session, lc);
	}

	protected GnuArmDebuggerCommandsService createDebuggerCommandsService(
			DsfSession session, ILaunchConfiguration lc) {
		return new DebuggerCommands(session, lc);
	}

	// ------------------------------------------------------------------------
}
