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

import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.qemu.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.qemu.ConfigurationAttributes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFFinalLaunchSequence;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.core.Messages;
import org.eclipse.cdt.debug.gdbjtag.core.jtagdevice.GDBJtagDeviceContribution;
import org.eclipse.cdt.debug.gdbjtag.core.jtagdevice.GDBJtagDeviceContributionFactory;
import org.eclipse.cdt.debug.gdbjtag.core.jtagdevice.IGDBJtagDevice;
import org.eclipse.cdt.dsf.concurrent.CountingRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.cdt.dsf.gdb.service.IGDBBackend;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.mi.service.IMIProcesses;
import org.eclipse.cdt.dsf.mi.service.command.commands.CLICommand;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;

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

		if (commands != null && !commands.isEmpty()) {

			CountingRequestMonitor crm = new CountingRequestMonitor(
					getExecutor(), rm);
			crm.setDoneCount(commands.size());

			Iterator<String> it = commands.iterator();
			while (it.hasNext()) {
				String s = it.next().trim();
				if (s.isEmpty() || s.startsWith("#")) {
					crm.done();
					continue; // ignore empty lines and comments
				}
				// System.out.println("queueCommand('" + s + "')");
				fCommandControl.queueCommand(new CLICommand<MIInfo>(
						fCommandControl.getContext(), s),
						new DataRequestMonitor<MIInfo>(getExecutor(), crm));
			}

		} else {
			rm.done();
		}
	}

	// ------------------------------------------------------------------------

	@Execute
	public void stepSourceGDBInitFile(final RequestMonitor requestMonitor) {
		try {

			List<String> commandsList = new ArrayList<String>();

			String otherInits = CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
					ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS_DEFAULT);
			otherInits = VariablesPlugin.getDefault()
					.getStringVariableManager()
					.performStringSubstitution(otherInits);
			if (otherInits.length() > 0) {
				String[] commandsStr = otherInits.split("\\r?\\n"); //$NON-NLS-1$
				for (String str : commandsStr) {
					str = str.trim();
					if (str.length() > 0) {
						commandsList.add(str);
					}
				}
			}

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
		} catch (CoreException e) {
			requestMonitor.setStatus(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, -1,
					"Cannot run other gdb client commands", e)); //$NON-NLS-1$
			requestMonitor.done();
		}
	}

	@Execute
	public void stepResetBoard(final RequestMonitor rm) {

		List<String> commandsList = new ArrayList<String>();

		// boolean noReset = CDebugUtils.getAttribute(fAttributes,
		// ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
		// ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);
		// if (!noReset) {
		if (CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.DO_FIRST_RESET,
				ConfigurationAttributes.DO_FIRST_RESET_DEFAULT)) {
			String commandStr = ConfigurationAttributes.DO_FIRST_RESET_COMMAND;
			// String resetType = CDebugUtils.getAttribute(fAttributes,
			// ConfigurationAttributes.FIRST_RESET_TYPE,
			// ConfigurationAttributes.FIRST_RESET_TYPE_DEFAULT);
			commandsList.add(commandStr /* + resetType */);

			// Although the manual claims that reset always does a
			// halt, better issue it explicitly
			commandStr = ConfigurationAttributes.HALT_COMMAND;
			commandsList.add(commandStr);
		}
		// }

		queueCommands(commandsList, rm);
	}

	@Execute
	public void stepResumeScript(final RequestMonitor rm) {

		rm.done();
	}

	@Execute
	public void stepUserInitCommands(final RequestMonitor rm) {
		try {
			List<String> commandsList = new ArrayList<String>();

			String otherInits = CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.OTHER_INIT_COMMANDS,
					ConfigurationAttributes.OTHER_INIT_COMMANDS_DEFAULT);

			DebugUtils.addMultiLine(otherInits, commandsList);

			// if (CDebugUtils.getAttribute(fAttributes,
			// ConfigurationAttributes.ENABLE_SEMIHOSTING,
			// ConfigurationAttributes.ENABLE_SEMIHOSTING_DEFAULT)) {
			// String commandStr =
			// ConfigurationAttributes.ENABLE_SEMIHOSTING_COMMAND;
			// commandsList.add(commandStr);
			// }

			queueCommands(commandsList, rm);
		} catch (CoreException e) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					"Cannot run user defined init commands", e)); //$NON-NLS-1$
			rm.done();
		}
	}

	@Execute
	public void stepUserDebugCommands(final RequestMonitor rm) {
		try {
			List<String> commandsList = new ArrayList<String>();

			// boolean noReset = CDebugUtils.getAttribute(fAttributes,
			// ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
			// ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);
			// if (!noReset) {
			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.DO_SECOND_RESET,
					ConfigurationAttributes.DO_SECOND_RESET_DEFAULT)) {
				String commandStr = ConfigurationAttributes.DO_SECOND_RESET_COMMAND;
				// String resetType = CDebugUtils.getAttribute(fAttributes,
				// ConfigurationAttributes.SECOND_RESET_TYPE,
				// ConfigurationAttributes.SECOND_RESET_TYPE_DEFAULT);
				commandsList.add(commandStr /* + resetType */);

				// Although the manual claims that reset always does a
				// halt, better issue it explicitly
				commandStr = ConfigurationAttributes.HALT_COMMAND;
				commandsList.add(commandStr);
			}
			// }

			String userCmd = CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.OTHER_RUN_COMMANDS,
					ConfigurationAttributes.OTHER_RUN_COMMANDS_DEFAULT);

			DebugUtils.addMultiLine(userCmd, commandsList);

			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.DO_CONTINUE,
					ConfigurationAttributes.DO_CONTINUE_DEFAULT)) {
				commandsList.add(ConfigurationAttributes.DO_CONTINUE_COMMAND);
			}

			queueCommands(commandsList, rm);
		} catch (CoreException e) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					"Cannot run user defined run commands", e)); //$NON-NLS-1$
			rm.done();
		}
	}

	protected Map<String, Object> getAttributes() {
		return fAttributes;
	}

	private IGDBJtagDevice getGDBJtagDevice() {
		IGDBJtagDevice gdbJtagDevice = null;
		String jtagDeviceName = CDebugUtils.getAttribute(getAttributes(),
				IGDBJtagConstants.ATTR_JTAG_DEVICE,
				IGDBJtagConstants.DEFAULT_JTAG_DEVICE);
		GDBJtagDeviceContribution[] availableDevices = GDBJtagDeviceContributionFactory
				.getInstance().getGDBJtagDeviceContribution();
		for (GDBJtagDeviceContribution availableDevice : availableDevices) {
			if (jtagDeviceName.equals(availableDevice.getDeviceName())) {
				gdbJtagDevice = availableDevice.getDevice();
				break;
			}
		}
		return gdbJtagDevice;
	}

	/*
	 * Retrieve the IGDBJtagDevice instance
	 */
	/** @since 8.2 */
	@Execute
	public void stepRetrieveJTAGDevice(final RequestMonitor rm) {
		Exception exception = null;
		try {
			fGdbJtagDevice = getGDBJtagDevice();
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

		try {
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
					if (symbolsFileName.length() > 0) {
						symbolsFileName = VariablesPlugin.getDefault()
								.getStringVariableManager()
								.performStringSubstitution(symbolsFileName);
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

				// Escape windows path separator characters TWICE, once for Java
				// and once for GDB.
				symbolsFileName = symbolsFileName.replace("\\", "\\\\"); //$NON-NLS-1$ //$NON-NLS-2$

				String symbolsOffset = CDebugUtils.getAttribute(
						getAttributes(), IGDBJtagConstants.ATTR_SYMBOLS_OFFSET,
						IGDBJtagConstants.DEFAULT_SYMBOLS_OFFSET);
				if (symbolsOffset.length() > 0) {
					symbolsOffset = "0x" + symbolsOffset;
				}
				List<String> commands = new ArrayList<String>();
				fGdbJtagDevice.doLoadSymbol(symbolsFileName, symbolsOffset,
						commands);
				queueCommands(commands, rm);

			} else {
				rm.done();
			}
		} catch (CoreException e) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					"Cannot load symbol", e)); //$NON-NLS-1$
			rm.done();
		}

		// boolean doConnectToRunning = CDebugUtils.getAttribute(fAttributes,
		// ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
		// ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);
		//
		// if (!doConnectToRunning) {
		try {
			String imageFileName = null;
			if (CDebugUtils.getAttribute(getAttributes(),
					IGDBJtagConstants.ATTR_LOAD_IMAGE,
					IGDBJtagConstants.DEFAULT_LOAD_IMAGE)) {
				// New setting in Helios. Default is true. Check for
				// existence
				// in order to support older launch configs
				if (getAttributes().containsKey(
						IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE)
						&& CDebugUtils
								.getAttribute(
										getAttributes(),
										IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE,
										IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_IMAGE)) {
					IPath programFile = fGDBBackend.getProgramPath();
					if (programFile != null) {
						imageFileName = programFile.toOSString();
					}
				} else {
					imageFileName = CDebugUtils.getAttribute(getAttributes(),
							IGDBJtagConstants.ATTR_IMAGE_FILE_NAME,
							IGDBJtagConstants.DEFAULT_IMAGE_FILE_NAME);
					if (imageFileName.length() > 0) {
						imageFileName = VariablesPlugin.getDefault()
								.getStringVariableManager()
								.performStringSubstitution(imageFileName);
					} else {
						imageFileName = null;
					}
				}

				if (imageFileName == null) {
					rm.setStatus(new Status(
							IStatus.ERROR,
							Activator.PLUGIN_ID,
							-1,
							Messages.getString("GDBJtagDebugger.err_no_img_file"), null)); //$NON-NLS-1$
					rm.done();
					return;
				}

				// Escape windows path separator characters TWICE, once for
				// Java
				// and once for GDB.
				imageFileName = imageFileName.replace("\\", "\\\\"); //$NON-NLS-1$ //$NON-NLS-2$

				String imageOffset = CDebugUtils.getAttribute(getAttributes(),
						IGDBJtagConstants.ATTR_IMAGE_OFFSET,
						IGDBJtagConstants.DEFAULT_IMAGE_OFFSET);
				if (imageOffset.length() > 0) {
					imageOffset = (imageFileName.endsWith(".elf")) ? "" : "0x" + CDebugUtils.getAttribute(getAttributes(), IGDBJtagConstants.ATTR_IMAGE_OFFSET, IGDBJtagConstants.DEFAULT_IMAGE_OFFSET); //$NON-NLS-2$ 
				}
				List<String> commands = new ArrayList<String>();
				fGdbJtagDevice
						.doLoadImage(imageFileName, imageOffset, commands);
				queueCommands(commands, rm);
			} else {
				rm.done();
			}
		} catch (CoreException e) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					"Cannot load image", e)); //$NON-NLS-1$
			rm.done();
		}
		// }
	}

	@Execute
	public void stepStopScript(final RequestMonitor rm) {

		if (CDebugUtils.getAttribute(getAttributes(),
				IGDBJtagConstants.ATTR_SET_STOP_AT,
				IGDBJtagConstants.DEFAULT_SET_STOP_AT)) {
			String stopAt = CDebugUtils.getAttribute(getAttributes(),
					IGDBJtagConstants.ATTR_STOP_AT,
					IGDBJtagConstants.DEFAULT_STOP_AT);
			List<String> commands = new ArrayList<String>();

			// The tbreak is not optional if we want execution to halt
			fGdbJtagDevice.doStopAt(stopAt, commands);
			queueCommands(commands, rm);
		} else {
			rm.done();
		}
	}

	// ------------------------------------------------------------------------
}
