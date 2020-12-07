/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
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

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.IGdbServerBackendService;
import org.eclipse.embedcdt.internal.debug.gdbjtag.core.Activator;

/**
 * Define the step of creating the GdbServerBackend.
 */
public class GnuMcuServerServicesLaunchSequence extends Sequence {

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

	public GnuMcuServerServicesLaunchSequence(DsfSession session, GdbLaunch launch, IProgressMonitor progressMonitor) {
		super(session.getExecutor(), progressMonitor, "Start Server", "Start Server Rollback");

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuMcuServerServicesLaunchSequence()");
		}

		fSession = session;
		fLaunch = launch;
	}

	// ------------------------------------------------------------------------

	@Override
	public Step[] getSteps() {
		// System.out.println("GnuMcuServerServicesLaunchSequence.getSteps()");
		return fSteps;
	}

	// ------------------------------------------------------------------------
}
