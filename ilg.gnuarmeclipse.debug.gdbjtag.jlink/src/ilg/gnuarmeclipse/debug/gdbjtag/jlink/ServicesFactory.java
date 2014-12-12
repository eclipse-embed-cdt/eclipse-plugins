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

package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmCommandFactory;
import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmServicesFactory;

import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControl;
import org.eclipse.cdt.dsf.mi.service.IMIBackend;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ServicesFactory extends GnuArmServicesFactory {

	private final String fVersion;

	public ServicesFactory(String version) {
		super(version);

		System.out.println("ServicesFactory(" + version + ") " + this);
		fVersion = version;
	}

	protected ICommandControl createCommandControl(DsfSession session,
			ILaunchConfiguration config) {

		System.out.println("ServicesFactory.createCommandControl(" + session
				+ "," + config.getName() + ") " + this);

		if (GDB_7_4_VERSION.compareTo(getVersion()) <= 0) {
			return new Control_7_4(session, config, new GnuArmCommandFactory());
		}

		return super.createCommandControl(session, config);
	}

	protected IMIBackend createBackendGDBService(DsfSession session,
			ILaunchConfiguration lc) {

		System.out.println("ServicesFactory.createBackendGDBService(" + session
				+ "," + lc.getName() + ") " + this);

		// return new GDBBackend(session, lc);
		return new Backend(session, lc);
	}

	@Override
	protected IProcesses createProcessesService(DsfSession session) {

		System.out.println("ServicesFactory.createBackendGDBService(" + session
				+ ") " + this);

		if (GDB_7_2_1_VERSION.compareTo(fVersion) <= 0) {
			return new Processes_7_2_1(session);
		}

		return super.createProcessesService(session);
	}

}
