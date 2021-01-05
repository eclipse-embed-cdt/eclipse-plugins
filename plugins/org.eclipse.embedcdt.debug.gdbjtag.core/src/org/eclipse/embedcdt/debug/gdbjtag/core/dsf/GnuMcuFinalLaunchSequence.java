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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFFinalLaunchSequence;
import org.eclipse.cdt.debug.gdbjtag.core.jtagdevice.DefaultGDBJtagDeviceImpl;
import org.eclipse.cdt.debug.gdbjtag.core.jtagdevice.IGDBJtagDevice;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.service.IGDBBackend;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.IMIProcesses;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.embedcdt.debug.gdbjtag.core.DebugUtils;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.IGnuMcuDebuggerCommandsService;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.IPeripheralMemoryService;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.IPeripheralsService;
import org.eclipse.embedcdt.internal.debug.gdbjtag.core.Activator;

public class GnuMcuFinalLaunchSequence extends GDBJtagDSFFinalLaunchSequence {

	// ------------------------------------------------------------------------

	private Map<String, Object> fAttributes;
	private DsfSession fSession;

	private DsfServicesTracker fTracker;
	private IGDBBackend fGdbBackend;
	private IGDBControl fCommandControl;
	private IMIProcesses fProcService;
	private IGDBJtagDevice fGdbJtagDevice;
	private String fMode;

	private IGnuMcuDebuggerCommandsService fDebuggerCommands;

	// ------------------------------------------------------------------------

	private String[] topPreInitSteps = { "stepCreatePeripheralService", "stepCreatePeripheralMemoryService",
			"stepCreateDebuggerCommandsService" };

	private String[] topToRemove = { "stepRemoteConnection", "stepAttachToProcess" };

	private String[] jtagPreInitSteps = {};

	private String[] jtagResetStep = { "stepGnuMcuReset" };
	private String[] jtagStartStep = { "stepGnuMcuStart" };

	private String[] jtagToRemove = { "stepLoadSymbols", "stepResetBoard", "stepDelayStartup", "stepHaltBoard",
			"stepUserInitCommands", "stepLoadImage", "stepSetProgramCounter", "stepStopScript", "stepResumeScript",
			"stepUserDebugCommands" };

	// ------------------------------------------------------------------------

	public GnuMcuFinalLaunchSequence(DsfSession session, Map<String, Object> attributes, String mode,
			RequestMonitorWithProgress rm) {
		super(session, attributes, rm);
		fAttributes = attributes;
		fSession = session;
		fMode = mode;
	}

	// ------------------------------------------------------------------------

	@Override
	protected String[] getExecutionOrder(String group) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuMcuFinalLaunchSequence.getExecutionOrder(" + group + ")");
		}

		// Initialise the list with the base class' steps
		// We need to create a list that we can modify, which is why we
		// create our own ArrayList.
		List<String> orderList = new ArrayList<>(Arrays.asList(super.getExecutionOrder(group)));

		if (GROUP_TOP_LEVEL.equals(group)) {

			for (int i = 0; i < topToRemove.length; ++i) {
				int ix = orderList.indexOf(jtagToRemove[i]);
				if (ix >= 0) {
					orderList.remove(ix);
				}
			}

			// Insert the new steps at he beginning
			orderList.addAll(0, Arrays.asList(topPreInitSteps));

		} else if (GROUP_JTAG.equals(group)) {

			for (int i = 0; i < jtagToRemove.length; ++i) {
				int ix = orderList.indexOf(jtagToRemove[i]);
				if (ix >= 0) {
					orderList.remove(ix);
				}
			}

			// Insert the new steps at he beginning
			orderList.addAll(0, Arrays.asList(jtagPreInitSteps));

			// Insert our steps right after the existing steps.
			orderList.addAll(orderList.indexOf("stepConnectToTarget") + 1, Arrays.asList(jtagResetStep));

			// Insert our steps right before the existing steps.
			orderList.addAll(orderList.indexOf("stepJTAGCleanup"), Arrays.asList(jtagStartStep));

		}

		return orderList.toArray(new String[orderList.size()]);
	}

	// ------------------------------------------------------------------------

	@Execute
	public void stepCreatePeripheralService(RequestMonitor rm) {

		GdbLaunch launch = ((GdbLaunch) this.fSession.getModelAdapter(ILaunch.class));
		IPeripheralsService service = launch.getServiceFactory().createService(IPeripheralsService.class,
				launch.getSession(), new Object[0]);
		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuMcuFinalLaunchSequence.stepCreatePeripheralService() " + service);
		}
		if (service != null) {
			service.initialize(rm);
		} else {
			rm.setStatus(new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to start PeripheralService"));
			rm.done();
		}
	}

	@Execute
	public void stepCreatePeripheralMemoryService(RequestMonitor rm) {

		GdbLaunch launch = ((GdbLaunch) this.fSession.getModelAdapter(ILaunch.class));
		IPeripheralMemoryService service = launch.getServiceFactory().createService(IPeripheralMemoryService.class,
				launch.getSession(), launch.getLaunchConfiguration());
		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuMcuFinalLaunchSequence.stepCreatePeripheralMemoryService() " + service);
		}
		if (service != null) {
			service.initialize(rm);
		} else {
			rm.setStatus(new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to start PeripheralMemoryService"));
			rm.done();
		}
	}

	// ------------------------------------------------------------------------

	@Execute
	public void stepCreateDebuggerCommandsService(RequestMonitor rm) {

		GdbLaunch launch = ((GdbLaunch) this.fSession.getModelAdapter(ILaunch.class));
		GnuMcuDebuggerCommandsService service = (GnuMcuDebuggerCommandsService) launch.getServiceFactory()
				.createService(IGnuMcuDebuggerCommandsService.class, launch.getSession(),
						launch.getLaunchConfiguration());
		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuMcuFinalLaunchSequence.stepCreateDebuggerCommandsService() " + service);
		}
		if (service != null) {
			service.initialize(rm);
		} else {
			rm.setStatus(
					new Status(Status.ERROR, Activator.PLUGIN_ID, "Unable to start GnuMcuDebuggerCommandsService"));
			rm.done();
		}
	}

	// ------------------------------------------------------------------------

	// This function is used to capture the private objects
	@Override
	@Execute
	public void stepInitializeFinalLaunchSequence(RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuMcuFinalLaunchSequence.stepInitializeFinalLaunchSequence()");
		}

		fTracker = new DsfServicesTracker(Activator.getInstance().getBundle().getBundleContext(), fSession.getId());
		fGdbBackend = fTracker.getService(IGDBBackend.class);
		if (fGdbBackend == null) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, "Cannot obtain GDBBackend service", null)); //$NON-NLS-1$
			rm.done();
			return;
		}

		fCommandControl = fTracker.getService(IGDBControl.class);
		if (fCommandControl == null) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, "Cannot obtain control service", null)); //$NON-NLS-1$
			rm.done();
			return;
		}

		fCommandControl.getCommandFactory();

		fProcService = fTracker.getService(IMIProcesses.class);
		if (fProcService == null) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, "Cannot obtain process service", null)); //$NON-NLS-1$
			rm.done();
			return;
		}

		fDebuggerCommands = fTracker.getService(IGnuMcuDebuggerCommandsService.class);
		if (fDebuggerCommands == null) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, "Cannot obtain debugger commands service", //$NON-NLS-1$
					null));
			rm.done();
			return;
		}
		fDebuggerCommands.setAttributes(fAttributes);

		super.stepInitializeFinalLaunchSequence(rm);
	}

	@Override
	@Execute
	public void stepInitializeJTAGFinalLaunchSequence(RequestMonitor rm) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuMcuFinalLaunchSequence.stepInitializeJTAGFinalLaunchSequence()");
		}

		super.stepInitializeJTAGFinalLaunchSequence(rm);
	}

	// ------------------------------------------------------------------------

	private void queueCommands(List<String> commands, RequestMonitor rm) {
		DebugUtils.queueCommands(commands, rm, fCommandControl, getExecutor());
	}

	// ------------------------------------------------------------------------

	/**
	 * These steps are part of the GROUP_TOP_LEVEL.
	 *
	 * [stepInitializeFinalLaunchSequence, stepSetEnvironmentDirectory,
	 * stepSetBreakpointPending, stepEnablePrettyPrinting, stepSetPrintObject,
	 * stepSetCharset, stepSourceGDBInitFile,
	 * stepSetAutoLoadSharedLibrarySymbols, stepSetSharedLibraryPaths,
	 * stepRemoteConnection, stepAttachToProcess, GROUP_JTAG,
	 * stepDataModelInitializationComplete, stepCleanup]
	 */

	@Override
	@Execute
	public void stepSourceGDBInitFile(final RequestMonitor rm) {

		final List<String> commandsList = new ArrayList<>();

		IStatus status = fDebuggerCommands.addGdbInitCommandsCommands(commandsList);
		if (!status.isOK()) {
			rm.setStatus(status);
			rm.done();
			return;
		}

		super.stepSourceGDBInitFile(new RequestMonitor(getExecutor(), rm) {

			@Override
			protected void handleSuccess() {
				queueCommands(commandsList, rm);
			}
		});
	}

	// ------------------------------------------------------------------------

	/**
	 * These steps are part of the GROUP_JTAG.
	 *
	 * [stepInitializeJTAGFinalLaunchSequence, stepRetrieveJTAGDevice,
	 * stepLoadSymbols, stepConnectToTarget, stepResetBoard, stepDelayStartup,
	 * stepHaltBoard, stepUserInitCommands, stepLoadImage, stepUpdateContainer,
	 * stepInitializeMemory, stepSetArguments, stepSetEnvironmentVariables,
	 * stepStartTrackingBreakpoints, stepSetProgramCounter, stepStopScript,
	 * stepResumeScript, stepUserDebugCommands, stepJTAGCleanup]
	 */

	/**
	 * Retrieve the IGDBJtagDevice instance
	 */
	@Override
	@Execute
	public void stepRetrieveJTAGDevice(final RequestMonitor rm) {
		Exception exception = null;
		try {
			// fGdbJtagDevice = getGDBJtagDevice();
			fGdbJtagDevice = new DefaultGDBJtagDeviceImpl();
			// fDebuggerCommands.setJtagDevice(fGdbJtagDevice);
		} catch (NullPointerException e) {
			exception = e;
		}
		if (fGdbJtagDevice == null) {
			// Abort the launch
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, "Cannot get JTAG device", exception)); //$NON-NLS-1$
			rm.done();
		} else {
			super.stepRetrieveJTAGDevice(rm);
		}
	}

	// public IGDBJtagDevice getGDBJtagDevice() {
	//
	// IGDBJtagDevice gdbJtagDevice = null;
	// String jtagDeviceName = DebugUtils.getAttribute(fAttributes,
	// ConfigurationAttributes.ATTR_JTAG_DEVICE,
	// IGDBJtagConstants.DEFAULT_JTAG_DEVICE);
	// GDBJtagDeviceContribution[] availableDevices =
	// GDBJtagDeviceContributionFactory
	// .getInstance().getGDBJtagDeviceContribution();
	// for (GDBJtagDeviceContribution availableDevice : availableDevices) {
	// if (jtagDeviceName.equals(availableDevice.getDeviceName())) {
	// gdbJtagDevice = availableDevice.getDevice();
	// break;
	// }
	// }
	// return gdbJtagDevice;
	// }

	// ------------------------------------------------------------------------

	@Override
	@Execute
	public void stepConnectToTarget(final RequestMonitor rm) {

		List<String> commandsList = new ArrayList<>();

		IStatus status = fDebuggerCommands.addGnuMcuSelectRemoteCommands(commandsList);
		if (!status.isOK()) {
			rm.setStatus(status);
			rm.done();
			return;
		}

		queueCommands(commandsList, rm);
	}

	@Execute
	public void stepGnuMcuReset(RequestMonitor rm) {

		List<String> commandsList = new ArrayList<>();

		IStatus status = fDebuggerCommands.addGnuMcuResetCommands(commandsList);
		if (!status.isOK()) {
			rm.setStatus(status);
			rm.done();
			return;
		}

		queueCommands(commandsList, rm);
	}

	@Execute
	public void stepGnuMcuStart(RequestMonitor rm) {

		List<String> commandsList = new ArrayList<>();

		IStatus status = fDebuggerCommands.addGnuMcuStartCommands(commandsList);
		if (!status.isOK()) {
			rm.setStatus(status);
			rm.done();
			return;
		}

		queueCommands(commandsList, rm);
	}

	// ------------------------------------------------------------------------

	@Override
	@Execute
	public void stepStartTrackingBreakpoints(final RequestMonitor rm) {
		if (fMode.equals(ILaunchManager.DEBUG_MODE)) {
			super.stepStartTrackingBreakpoints(rm);
		} else {
			rm.done();
		}
	}

	// ------------------------------------------------------------------------
}
