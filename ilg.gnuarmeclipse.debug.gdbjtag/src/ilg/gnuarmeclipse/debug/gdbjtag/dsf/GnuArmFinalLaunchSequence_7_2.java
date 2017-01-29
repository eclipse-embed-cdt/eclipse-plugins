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

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.gdb.service.IGDBProcesses;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class GnuArmFinalLaunchSequence_7_2 extends GnuArmFinalLaunchSequence {

	// ------------------------------------------------------------------------

	private String[] jtagInitSteps = { "stepInitializeJTAGSequence_7_2" };

	// ------------------------------------------------------------------------

	private DsfSession fSession;

	// ------------------------------------------------------------------------

	public GnuArmFinalLaunchSequence_7_2(DsfSession session, Map<String, Object> attributes, String mode,
			RequestMonitorWithProgress rm) {
		super(session, attributes, mode, rm);
		fSession = session;
	}

	// ------------------------------------------------------------------------

	@Override
	protected String[] getExecutionOrder(String group) {

		// Initialise the list with the base class' steps
		// We need to create a list that we can modify, which is why we
		// create our own ArrayList.
		List<String> orderList = new ArrayList<String>(Arrays.asList(super.getExecutionOrder(group)));

		if (GROUP_JTAG.equals(group)) {

			// Insert our steps right after the existing steps.
			orderList.addAll(orderList.indexOf("stepInitializeJTAGFinalLaunchSequence") + 1,
					Arrays.asList(jtagInitSteps));

		}

		return orderList.toArray(new String[orderList.size()]);
	}

	// ------------------------------------------------------------------------

	/**
	 * Initialise the members of the DebugNewProcessSequence_7_2 class. This
	 * step is mandatory for the rest of the sequence to complete.
	 */
	@Execute
	public void stepInitializeJTAGSequence_7_2(RequestMonitor rm) {
		DsfServicesTracker tracker = new DsfServicesTracker(Activator.getInstance().getBundle().getBundleContext(),
				fSession.getId());
		IGDBControl gdbControl = tracker.getService(IGDBControl.class);
		IGDBProcesses procService = tracker.getService(IGDBProcesses.class);
		tracker.dispose();

		if (gdbControl == null || procService == null) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, "Cannot obtain service", null)); //$NON-NLS-1$
			rm.done();
			return;
		}
		setContainerContext(procService.createContainerContextFromGroupId(gdbControl.getContext(), "i1")); //$NON-NLS-1$
		rm.done();
	}

	// ------------------------------------------------------------------------
}
