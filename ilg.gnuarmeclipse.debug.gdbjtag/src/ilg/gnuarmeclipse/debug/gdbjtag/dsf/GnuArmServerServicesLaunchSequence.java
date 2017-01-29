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

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IGdbServerBackendService;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Define the step of creating the GdbServerBackend.
 */
public class GnuArmServerServicesLaunchSequence extends Sequence {

	// ------------------------------------------------------------------------

	private DsfSession fSession;
	private GdbLaunch fLaunch;

	private Step[] fSteps = new Step[] { new Step() {
		@Override
		public void execute(RequestMonitor requestMonitor) {
			fLaunch.getServiceFactory()
					.createService(IGdbServerBackendService.class, fSession, fLaunch.getLaunchConfiguration())
					.initialize(requestMonitor);
		}
	} };

	// ------------------------------------------------------------------------

	public GnuArmServerServicesLaunchSequence(DsfSession session, GdbLaunch launch, IProgressMonitor progressMonitor) {
		super(session.getExecutor(), progressMonitor, "Start Server", "Start Server Rollback");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmServerServicesLaunchSequence()");
		}

		fSession = session;
		fLaunch = launch;
	}

	// ------------------------------------------------------------------------

	@Override
	public Step[] getSteps() {
		// System.out.println("GnuArmServerServicesLaunchSequence.getSteps()");
		return fSteps;
	}

	// ------------------------------------------------------------------------
}
