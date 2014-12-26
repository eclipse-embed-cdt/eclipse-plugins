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

import ilg.gnuarmeclipse.debug.gdbjtag.qemu.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IPeripheralMemoryService;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IPeripheralsService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.service.IGDBProcesses;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;

public class FinalLaunchSequence_7_2 extends FinalLaunchSequence {

	// ------------------------------------------------------------------------

	private String[] newPreInitSteps = { "stepCreatePeripheralService",
			"stepCreatePeripheralMemoryService", };

	private String[] newInitSteps = { "stepInitializeJTAGSequence_7_2" };

	// ------------------------------------------------------------------------

	private DsfSession fSession;

	// ------------------------------------------------------------------------

	public FinalLaunchSequence_7_2(DsfSession session,
			Map<String, Object> attributes, RequestMonitorWithProgress rm) {
		super(session, attributes, rm);
		fSession = session;
	}

	// ------------------------------------------------------------------------

	@Override
	protected String[] getExecutionOrder(String group) {

		if (GROUP_JTAG.equals(group)) {
			// Initialize the list with the base class' steps
			// We need to create a list that we can modify, which is why we
			// create our own ArrayList.
			List<String> orderList = new ArrayList<String>(Arrays.asList(super
					.getExecutionOrder(GROUP_JTAG)));

			// Insert the peripherals step first
			orderList.addAll(0, Arrays.asList(newPreInitSteps)); //$NON-NLS-1$ //$NON-NLS-2$

			// Now insert our steps right after the initialisation of the base
			// class.
			orderList
					.addAll(orderList
							.indexOf("stepInitializeJTAGFinalLaunchSequence") + 1,
							Arrays.asList(newInitSteps)); //$NON-NLS-1$ //$NON-NLS-2$

			return orderList.toArray(new String[orderList.size()]);
		}

		return super.getExecutionOrder(group);
	}

	/**
	 * Initialize the members of the DebugNewProcessSequence_7_2 class. This
	 * step is mandatory for the rest of the sequence to complete.
	 */
	@Execute
	public void stepInitializeJTAGSequence_7_2(RequestMonitor rm) {
		DsfServicesTracker tracker = new DsfServicesTracker(Activator
				.getInstance().getBundle().getBundleContext(), fSession.getId());
		IGDBControl gdbControl = tracker.getService(IGDBControl.class);
		IGDBProcesses procService = tracker.getService(IGDBProcesses.class);
		tracker.dispose();

		if (gdbControl == null || procService == null) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					"Cannot obtain service", null)); //$NON-NLS-1$
			rm.done();
			return;
		}
		setContainerContext(procService.createContainerContextFromGroupId(
				gdbControl.getContext(), "i1")); //$NON-NLS-1$
		rm.done();
	}

	@Execute
	public void stepCreatePeripheralService(RequestMonitor rm) {

		GdbLaunch launch = ((GdbLaunch) this.fSession
				.getModelAdapter(ILaunch.class));
		IPeripheralsService service = (IPeripheralsService) launch
				.getServiceFactory().createService(IPeripheralsService.class,
						launch.getSession(), new Object[0]);
		System.out.println("stepCreatePeripheralService() " + service);
		if (service != null) {
			service.initialize(rm);
		} else {
			rm.setStatus(new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Unable to start PeripheralService"));
			rm.done();
		}
	}

	@Execute
	public void stepCreatePeripheralMemoryService(RequestMonitor rm) {

		GdbLaunch launch = ((GdbLaunch) this.fSession
				.getModelAdapter(ILaunch.class));
		IPeripheralMemoryService service = (IPeripheralMemoryService) launch
				.getServiceFactory().createService(
						IPeripheralMemoryService.class, launch.getSession(),
						new Object[0]);
		System.out.println("stepCreatePeripheralMemoryService() " + service);
		if (service != null) {
			service.initialize(rm);
		} else {
			rm.setStatus(new Status(Status.ERROR, Activator.PLUGIN_ID,
					"Unable to start PeripheralMemoryService"));
			rm.done();
		}
	}

	// ------------------------------------------------------------------------
}
