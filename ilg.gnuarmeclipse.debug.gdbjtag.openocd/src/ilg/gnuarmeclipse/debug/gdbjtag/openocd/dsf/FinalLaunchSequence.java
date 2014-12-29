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

package ilg.gnuarmeclipse.debug.gdbjtag.openocd.dsf;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.openocd.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.openocd.ConfigurationAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFFinalLaunchSequence;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.core.Messages;
import org.eclipse.cdt.debug.gdbjtag.core.jtagdevice.IGDBJtagDevice;
import org.eclipse.cdt.dsf.concurrent.CountingRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.service.IGDBBackend;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.IMIProcesses;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

@SuppressWarnings("restriction")
public class FinalLaunchSequence extends GDBJtagDSFFinalLaunchSequence {

	// ------------------------------------------------------------------------

	private Map<String, Object> fAttributes;
	private DsfSession fSession;

	private DsfServicesTracker fTracker;
	private IGDBBackend fGDBBackend;
	private IGDBControl fCommandControl;
	private IMIProcesses fProcService;
	private IGDBJtagDevice fGdbJtagDevice;

	// ------------------------------------------------------------------------

	public FinalLaunchSequence(DsfSession session,
			Map<String, Object> attributes, RequestMonitorWithProgress rm) {
		super(session, attributes, rm);
		fAttributes = attributes;
		fSession = session;
	}

	// ------------------------------------------------------------------------

	// This function is used to capture the private objects
	@Execute
	public void stepInitializeFinalLaunchSequence(RequestMonitor requestMonitor) {
		fTracker = new DsfServicesTracker(GdbPlugin.getBundleContext(),
				fSession.getId());
		fGDBBackend = fTracker.getService(IGDBBackend.class);
		if (fGDBBackend == null) {
			requestMonitor.setStatus(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, -1,
					"Cannot obtain GDBBackend service", null)); //$NON-NLS-1$
			requestMonitor.done();
			return;
		}

		fCommandControl = fTracker.getService(IGDBControl.class);
		if (fCommandControl == null) {
			requestMonitor.setStatus(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, -1,
					"Cannot obtain control service", null)); //$NON-NLS-1$
			requestMonitor.done();
			return;
		}

		fCommandControl.getCommandFactory();

		fProcService = fTracker.getService(IMIProcesses.class);
		if (fProcService == null) {
			requestMonitor.setStatus(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, -1,
					"Cannot obtain process service", null)); //$NON-NLS-1$
			requestMonitor.done();
			return;
		}

		super.stepInitializeFinalLaunchSequence(requestMonitor);
	}

	@Execute
	public void stepInitializeJTAGFinalLaunchSequence(RequestMonitor rm) {
		fTracker = new DsfServicesTracker(GdbPlugin.getBundleContext(),
				fSession.getId());
		fGDBBackend = fTracker.getService(IGDBBackend.class);
		if (fGDBBackend == null) {
			rm.done(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					"Cannot obtain GDBBackend service", null)); //$NON-NLS-1$
			return;
		}

		fCommandControl = fTracker.getService(IGDBControl.class);
		if (fCommandControl == null) {
			rm.done(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					"Cannot obtain control service", null)); //$NON-NLS-1$
			return;
		}

		fProcService = fTracker.getService(IMIProcesses.class);
		if (fProcService == null) {
			rm.done(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					"Cannot obtain process service", null)); //$NON-NLS-1$
			return;
		}

		super.stepInitializeJTAGFinalLaunchSequence(rm);
	}

	// ------------------------------------------------------------------------

	/** utility method; cuts down on clutter */
	private void queueCommands(List<String> commands, RequestMonitor rm) {
		DebugUtils.queueCommands(commands, rm, fCommandControl, getExecutor());
	}

	// ------------------------------------------------------------------------

	@Execute
	public void stepSourceGDBInitFile(final RequestMonitor requestMonitor) {

		List<String> commandsList = new ArrayList<String>();

		String otherInits = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
				ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS_DEFAULT)
				.trim();

		otherInits = DebugUtils.resolveAll(otherInits, fAttributes);
		DebugUtils.addMultiLine(otherInits, commandsList);

		if (!commandsList.isEmpty()) {
			CountingRequestMonitor crm = new CountingRequestMonitor(
					getExecutor(), requestMonitor);

			// One more for the parent step
			crm.setDoneCount(1 + 1);

			queueCommands(commandsList, crm);

			super.stepSourceGDBInitFile(crm);
		} else {
			super.stepSourceGDBInitFile(requestMonitor);
		}
	}

	@Execute
	public void stepResetBoard(final RequestMonitor rm) {

		List<String> commandsList = new ArrayList<String>();

		boolean noReset = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);
		if (!noReset) {
			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.DO_FIRST_RESET,
					ConfigurationAttributes.DO_FIRST_RESET_DEFAULT)) {
				String commandStr = ConfigurationAttributes.DO_FIRST_RESET_COMMAND;
				String resetType = CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.FIRST_RESET_TYPE,
						ConfigurationAttributes.FIRST_RESET_TYPE_DEFAULT);
				commandsList.add(commandStr + resetType);

				// Although the manual claims that reset always does a
				// halt, better issue it explicitly
				commandStr = ConfigurationAttributes.HALT_COMMAND;
				commandsList.add(commandStr);
			}
		}

		queueCommands(commandsList, rm);
	}

	@Execute
	public void stepResumeScript(final RequestMonitor rm) {

		rm.done();
	}

	@Execute
	public void stepUserInitCommands(final RequestMonitor rm) {

		List<String> commandsList = new ArrayList<String>();

		String otherInits = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.OTHER_INIT_COMMANDS,
				ConfigurationAttributes.OTHER_INIT_COMMANDS_DEFAULT).trim();

		otherInits = DebugUtils.resolveAll(otherInits, fAttributes);
		if (EclipseUtils.isWindows()) {
			otherInits = StringUtils.duplicateBackslashes(otherInits);
		}
		DebugUtils.addMultiLine(otherInits, commandsList);

		if (CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.ENABLE_SEMIHOSTING,
				ConfigurationAttributes.ENABLE_SEMIHOSTING_DEFAULT)) {
			String commandStr = ConfigurationAttributes.ENABLE_SEMIHOSTING_COMMAND;
			commandsList.add(commandStr);
		}

		queueCommands(commandsList, rm);
	}

	@Execute
	public void stepUserDebugCommands(final RequestMonitor rm) {

		List<String> commandsList = new ArrayList<String>();

		boolean doReset = !CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);

		IStatus status = Launch.startRestart(fAttributes, doReset,
				fGDBBackend.getProgramPath(), fGdbJtagDevice, commandsList);

		if (!status.isOK()) {
			rm.setStatus(status); //$NON-NLS-1$
			rm.done();
			return;
		}

		queueCommands(commandsList, rm);
	}

	protected Map<String, Object> getAttributes() {
		return fAttributes;
	}

	/*
	 * Retrieve the IGDBJtagDevice instance
	 */
	/** @since 8.2 */
	@Execute
	public void stepRetrieveJTAGDevice(final RequestMonitor rm) {
		Exception exception = null;
		try {
			fGdbJtagDevice = DebugUtils.getGDBJtagDevice(getAttributes());
		} catch (NullPointerException e) {
			exception = e;
		}
		if (fGdbJtagDevice == null) {
			// Abort the launch
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					"Cannot get Jtag device", exception)); //$NON-NLS-1$
			rm.done();
		} else {
			super.stepRetrieveJTAGDevice(rm);
		}
	}

	/*
	 * Execute symbol loading
	 */
	/** @since 8.2 */
	@Execute
	public void stepLoadSymbols(final RequestMonitor rm) {
		// actions moved inside stepLoadImage(), this is called too early
		rm.done();
	}

	/*
	 * Execute image loading
	 */
	/** @since 8.2 */
	@Execute
	public void stepLoadImage(final RequestMonitor rm0) {

		CountingRequestMonitor rm = new CountingRequestMonitor(getExecutor(),
				rm0);
		rm.setDoneCount(2);

		if (CDebugUtils.getAttribute(getAttributes(),
				IGDBJtagConstants.ATTR_LOAD_SYMBOLS,
				IGDBJtagConstants.DEFAULT_LOAD_SYMBOLS)) {
			String symbolsFileName = null;

			// New setting in Helios. Default is true. Check for existence
			// in order to support older launch configs
			if (getAttributes().containsKey(
					IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS)
					&& CDebugUtils
							.getAttribute(
									getAttributes(),
									IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS,
									IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_SYMBOLS)) {
				IPath programFile = fGDBBackend.getProgramPath();
				if (programFile != null) {
					symbolsFileName = programFile.toOSString();
				}
			} else {
				symbolsFileName = CDebugUtils.getAttribute(getAttributes(),
						IGDBJtagConstants.ATTR_SYMBOLS_FILE_NAME,
						IGDBJtagConstants.DEFAULT_SYMBOLS_FILE_NAME);
				if (!symbolsFileName.isEmpty()) {
					symbolsFileName = DebugUtils.resolveAll(symbolsFileName,
							getAttributes());
				} else {
					symbolsFileName = null;
				}
			}

			if (symbolsFileName == null) {
				rm.setStatus(new Status(
						IStatus.ERROR,
						Activator.PLUGIN_ID,
						-1,
						Messages.getString("GDBJtagDebugger.err_no_img_file"), null)); //$NON-NLS-1$
				rm0.done();
				return;
			}

			if (EclipseUtils.isWindows()) {
				// Escape windows path separator characters TWICE, once for
				// Java and once for GDB.
				symbolsFileName = StringUtils
						.duplicateBackslashes(symbolsFileName); //$NON-NLS-1$ //$NON-NLS-2$
			}

			String symbolsOffset = CDebugUtils.getAttribute(getAttributes(),
					IGDBJtagConstants.ATTR_SYMBOLS_OFFSET,
					IGDBJtagConstants.DEFAULT_SYMBOLS_OFFSET);
			if (!symbolsOffset.isEmpty()) {
				symbolsOffset = "0x" + symbolsOffset;
			}
			List<String> commands = new ArrayList<String>();
			fGdbJtagDevice.doLoadSymbol(symbolsFileName, symbolsOffset,
					commands);
			queueCommands(commands, rm);

		} else {
			rm.done();
		}

		boolean doConnectToRunning = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);

		if (!doConnectToRunning) {
			if (CDebugUtils.getAttribute(getAttributes(),
					IGDBJtagConstants.ATTR_LOAD_IMAGE,
					IGDBJtagConstants.DEFAULT_LOAD_IMAGE)
					&& !CDebugUtils.getAttribute(getAttributes(),
							ConfigurationAttributes.DO_DEBUG_IN_RAM,
							ConfigurationAttributes.DO_DEBUG_IN_RAM_DEFAULT)) {

				List<String> commands = new ArrayList<String>();
				IStatus status = Launch.loadImage(fAttributes,
						fGDBBackend.getProgramPath(), fGdbJtagDevice, commands);

				if (status.isOK()) {
					queueCommands(commands, rm);
				} else {
					rm.setStatus(status); //$NON-NLS-1$
					rm.done();
				}
			} else {
				rm.done();
			}
		}
	}

	@Execute
	public void stepSetProgramCounter(final RequestMonitor rm) {
		
		// set PC moved to user step
		rm.done();
	}

	@Execute
	public void stepStopScript(final RequestMonitor rm) {

		// tbreak moved to user step
		rm.done();
	}

	// ------------------------------------------------------------------------
}
