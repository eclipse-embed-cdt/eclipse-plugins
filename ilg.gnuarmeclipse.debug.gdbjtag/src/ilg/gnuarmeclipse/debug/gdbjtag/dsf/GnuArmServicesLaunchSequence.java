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

import ilg.gnuarmeclipse.debug.gdbjtag.services.IGdbServerBackendService;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.launching.ServicesLaunchSequence;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IProgressMonitor;

public class GnuArmServicesLaunchSequence extends ServicesLaunchSequence {

	// ------------------------------------------------------------------------

    DsfSession fSession;
    GdbLaunch fLaunch;

	Step[] fSteps = new Step[] { new Step() {
		@Override
		public void execute(RequestMonitor requestMonitor) {
			fLaunch.getServiceFactory()
					.createService(IGdbServerBackendService.class, fSession,
							fLaunch.getLaunchConfiguration())
					.initialize(requestMonitor);
		}
	} };

	// ------------------------------------------------------------------------

	public GnuArmServicesLaunchSequence(DsfSession session, GdbLaunch launch,
			IProgressMonitor pm) {
		super(session, launch, pm);

        fSession = session;
        fLaunch = launch;
	}

	// ------------------------------------------------------------------------

    @Override
    public Step[] getSteps() {
		Step[] superSteps = super.getSteps();
    	
    	Step[] steps = new Step[fSteps.length+superSteps.length];
    	System.arraycopy(fSteps, 0, steps, 0, fSteps.length);
    	System.arraycopy(superSteps, 0, steps, fSteps.length,   superSteps.length  );
    	
        return steps;
    }

	// ------------------------------------------------------------------------
}
