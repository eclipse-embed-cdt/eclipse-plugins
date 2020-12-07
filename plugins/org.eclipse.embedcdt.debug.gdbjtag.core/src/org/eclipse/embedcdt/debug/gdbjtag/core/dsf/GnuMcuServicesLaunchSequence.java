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
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.launching.ServicesLaunchSequence;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.IGdbServerBackendService;
import org.eclipse.embedcdt.internal.debug.gdbjtag.core.Activator;

/**
 * Insert the step of creating the GdbServerBackend before all other steps.
 */
public class GnuMcuServicesLaunchSequence extends ServicesLaunchSequence {

	// ------------------------------------------------------------------------

	private DsfSession fSession;
	private GdbLaunch fLaunch;

	Step[] fOurSteps = new Step[] { new Step() {
		@Override
		public void execute(RequestMonitor requestMonitor) {
			fLaunch.getServiceFactory()
					.createService(IGdbServerBackendService.class, fSession, fLaunch.getLaunchConfiguration())
					.initialize(requestMonitor);
		}
	} };

	private Step[] fSteps = null;

	// ------------------------------------------------------------------------

	public GnuMcuServicesLaunchSequence(DsfSession session, GdbLaunch launch, IProgressMonitor pm) {
		super(session, launch, pm);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuMcuServicesLaunchSequence()");
		}

		fSession = session;
		fLaunch = launch;
	}

	// ------------------------------------------------------------------------

	@Override
	public Step[] getSteps() {
		// System.out.println("GnuMcuServicesLaunchSequence.getSteps()");
		if (fSteps == null) {
			Step[] superSteps = super.getSteps();

			fSteps = new Step[fOurSteps.length + superSteps.length];
			System.arraycopy(fOurSteps, 0, fSteps, 0, fOurSteps.length);
			System.arraycopy(superSteps, 0, fSteps, fOurSteps.length, superSteps.length);
		}
		return fSteps;
	}

	// ------------------------------------------------------------------------
}
