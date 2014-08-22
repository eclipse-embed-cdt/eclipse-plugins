package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import ilg.gnuarmeclipse.debug.core.DebugUtils;

import java.util.ArrayList;
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
import org.eclipse.cdt.dsf.concurrent.ReflectionSequence.Execute;
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

public class FinalLaunchSequence extends GDBJtagDSFFinalLaunchSequence {

	private Map<String, Object> fAttributes;
	private DsfSession fSession;

	private DsfServicesTracker fTracker;
	private IGDBBackend fGDBBackend;
	private IGDBControl fCommandControl;
	private IMIProcesses fProcService;
	private IGDBJtagDevice fGdbJtagDevice;

	// public FinalLaunchSequence(DsfExecutor executor, GdbLaunch launch,
	// SessionType sessionType, boolean attach,
	// RequestMonitorWithProgress rm) {
	// super(executor, launch, sessionType, attach, rm);
	// }

	public FinalLaunchSequence(DsfSession session,
			Map<String, Object> attributes, RequestMonitorWithProgress rm) {
		super(session, attributes, rm);
		fAttributes = attributes;
		fSession = session;
	}

	/** utility method; cuts down on clutter */
	private void queueCommands(List<String> commands, RequestMonitor rm) {
		if (!commands.isEmpty()) {
			fCommandControl.queueCommand(
					new CLICommand<MIInfo>(fCommandControl.getContext(),
							DebugUtils.composeCommandWithLf(commands)),
					new DataRequestMonitor<MIInfo>(getExecutor(), rm));
		} else {
			rm.done();
		}
	}

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

			if (commandsList.size() > 0) {
				CountingRequestMonitor crm = new CountingRequestMonitor(
						getExecutor(), requestMonitor);
				crm.setDoneCount(commandsList.size());

				queueCommands(commandsList, requestMonitor);
			} else {
				requestMonitor.done();
			}
		} catch (CoreException e) {
			requestMonitor.setStatus(new Status(IStatus.ERROR,
					Activator.PLUGIN_ID, -1,
					"Cannot run other gdb client commands", e)); //$NON-NLS-1$
			requestMonitor.done();
		}

		super.stepSourceGDBInitFile(requestMonitor);
	}

	@Execute
	public void stepResetBoard(final RequestMonitor rm) {

		List<String> commandsList = new ArrayList<String>();

		String attr;
		// attr = CDebugUtils.getAttribute(fAttributes,
		// ConfigurationAttributes.INTERFACE,
		// ConfigurationAttributes.INTERFACE_SWD);
		// if (ConfigurationAttributes.INTERFACE_SWD.equals(attr)) {
		// commandsList.add(ConfigurationAttributes.INTERFACE_SWD_COMMAND);
		// } else if (ConfigurationAttributes.INTERFACE_JTAG.equals(attr)) {
		// commandsList.add(ConfigurationAttributes.INTERFACE_JTAG_COMMAND);
		// }

		try {
			attr = String.valueOf(CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.FIRST_RESET_SPEED,
					ConfigurationAttributes.FIRST_RESET_SPEED_DEFAULT));
		} catch (Exception e) {
			attr = CDebugUtils
					.getAttribute(
							fAttributes,
							ConfigurationAttributes.FIRST_RESET_SPEED,
							String.valueOf(ConfigurationAttributes.FIRST_RESET_SPEED_DEFAULT));
		}
		if (attr.length() > 0) {
			commandsList
					.add(ConfigurationAttributes.INTERFACE_SPEED_FIXED_COMMAND
							+ attr);
		}

		// attr = CDebugUtils.getAttribute(fAttributes,
		// ConfigurationAttributes.FLASH_DEVICE_NAME,
		// ConfigurationAttributes.FLASH_DEVICE_NAME_DEFAULT);
		// attr = attr.trim();
		// if (attr.length() > 0) {
		// commandsList.add(ConfigurationAttributes.FLASH_DEVICE_NAME_COMMAND
		// + attr);
		// }
		//
		// attr = CDebugUtils.getAttribute(fAttributes,
		// ConfigurationAttributes.ENDIANNESS,
		// ConfigurationAttributes.ENDIANNESS_LITTLE);
		// if (ConfigurationAttributes.ENDIANNESS_LITTLE.equals(attr)) {
		// commandsList.add(ConfigurationAttributes.ENDIANNESS_LITTLE_COMMAND);
		// } else if (ConfigurationAttributes.ENDIANNESS_BIG.equals(attr)) {
		// commandsList.add(ConfigurationAttributes.ENDIANNESS_BIG_COMMAND);
		// }

		boolean noReset = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);
		if (!noReset) {
			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.DO_FIRST_RESET,
					ConfigurationAttributes.DO_FIRST_RESET_DEFAULT)) {

				String commandStr;

				// Since reset does not clear breakpoints, we do it explicitly
				commandStr = ConfigurationAttributes.CLRBP_COMMAND;
				commandsList.add(commandStr);

				commandStr = ConfigurationAttributes.DO_FIRST_RESET_COMMAND;
				String resetType = CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.FIRST_RESET_TYPE,
						ConfigurationAttributes.FIRST_RESET_TYPE_DEFAULT);
				commandsList.add(commandStr + resetType);

				// Although the manual claims that reset always does a
				// halt, better issue it explicitly
				commandStr = ConfigurationAttributes.HALT_COMMAND;
				commandsList.add(commandStr);

				// Also add a command to see the registers in the
				// location where execution halted
				commandStr = ConfigurationAttributes.REGS_COMMAND;
				commandsList.add(commandStr);

				// Flush registers, GDB should read them again
				commandStr = ConfigurationAttributes.FLUSH_REGISTERS_COMMAND;
				commandsList.add(commandStr);
			}
		}

		attr = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.INTERFACE_SPEED,
				ConfigurationAttributes.INTERFACE_SPEED_AUTO);
		if (ConfigurationAttributes.INTERFACE_SPEED_AUTO.equals(attr)) {
			commandsList
					.add(ConfigurationAttributes.INTERFACE_SPEED_AUTO_COMMAND);
		} else if (ConfigurationAttributes.INTERFACE_SPEED_ADAPTIVE
				.equals(attr)) {
			commandsList
					.add(ConfigurationAttributes.INTERFACE_SPEED_ADAPTIVE_COMMAND);
		} else {
			commandsList
					.add(ConfigurationAttributes.INTERFACE_SPEED_FIXED_COMMAND
							+ attr);
		}

		if (commandsList.size() > 0) {
			CountingRequestMonitor crm = new CountingRequestMonitor(
					getExecutor(), rm);
			crm.setDoneCount(commandsList.size());

			queueCommands(commandsList, rm);
		} else {
			rm.done();
		}
	}

	@Execute
	public void stepResumeScript(final RequestMonitor rm) {

		rm.done();
	}

	@Execute
	public void stepUserInitCommands(final RequestMonitor rm) {
		try {
			List<String> commandsList = new ArrayList<String>();

			String commandStr;
			commandStr = ConfigurationAttributes.ENABLE_FLASH_BREAKPOINTS_COMMAND;
			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.ENABLE_FLASH_BREAKPOINTS,
					ConfigurationAttributes.ENABLE_FLASH_BREAKPOINTS_DEFAULT))
				commandStr += "1";
			else
				commandStr += "0";
			commandsList.add(commandStr);

			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.ENABLE_SEMIHOSTING,
					ConfigurationAttributes.ENABLE_SEMIHOSTING_DEFAULT)) {
				commandStr = ConfigurationAttributes.ENABLE_SEMIHOSTING_COMMAND;
				commandsList.add(commandStr);

				int ioclientMask = 0;
				if (CDebugUtils
						.getAttribute(
								fAttributes,
								ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_TELNET,
								ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_TELNET_DEFAULT)) {
					ioclientMask |= ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_TELNET_MASK;
				}
				if (CDebugUtils
						.getAttribute(
								fAttributes,
								ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT,
								ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT_DEFAULT)) {
					ioclientMask |= ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT_MASK;
				}

				commandStr = ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_COMMAND
						+ String.valueOf(ioclientMask);
				commandsList.add(commandStr);
			}

			String attr;
			attr = CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.GDB_SERVER_DEBUG_INTERFACE,
					ConfigurationAttributes.INTERFACE_SWD);
			if (ConfigurationAttributes.INTERFACE_SWD.equals(attr)) {

				if (CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.ENABLE_SWO,
						ConfigurationAttributes.ENABLE_SWO_DEFAULT)) {
					commandStr = ConfigurationAttributes.ENABLE_SWO_COMMAND;
					commandStr += CDebugUtils
							.getAttribute(
									fAttributes,
									ConfigurationAttributes.SWO_ENABLETARGET_CPUFREQ,
									ConfigurationAttributes.SWO_ENABLETARGET_CPUFREQ_DEFAULT);
					commandStr += " ";
					commandStr += CDebugUtils
							.getAttribute(
									fAttributes,
									ConfigurationAttributes.SWO_ENABLETARGET_SWOFREQ,
									ConfigurationAttributes.SWO_ENABLETARGET_SWOFREQ_DEFAULT);
					commandStr += " ";
					commandStr += CDebugUtils
							.getAttribute(
									fAttributes,
									ConfigurationAttributes.SWO_ENABLETARGET_PORTMASK,
									ConfigurationAttributes.SWO_ENABLETARGET_PORTMASK_DEFAULT);
					commandStr += " 0";

					commandsList.add(commandStr);

					// commandStr =
					// ConfigurationAttributes.ENABLE_SWO_GETSPEEDINFO_COMMAND;
					// commandsList.add(commandStr);
				}
			}

			String otherInits = CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.OTHER_INIT_COMMANDS,
					ConfigurationAttributes.OTHER_INIT_COMMANDS_DEFAULT);

			boolean doConnectToRunning = CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
					ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);

			// String flashDownload = doConnectToRunning ?
			// ConfigurationAttributes.DISABLE_FLASH_DOWNLOAD_COMMAND
			// : ConfigurationAttributes.ENABLE_FLASH_DOWNLOAD_COMMAND;
			//
			// otherInits += "\n" + flashDownload;

			DebugUtils.addMultiLine(otherInits, commandsList);

			if (commandsList.size() > 0) {
				CountingRequestMonitor crm = new CountingRequestMonitor(
						getExecutor(), rm);
				crm.setDoneCount(commandsList.size());

				queueCommands(commandsList, rm);
			} else {
				rm.done();
			}
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

			boolean noReset = CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
					ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);
			if (!noReset) {
				if (CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.DO_SECOND_RESET,
						ConfigurationAttributes.DO_SECOND_RESET_DEFAULT)) {

					String commandStr;

					// Since reset does not clear breakpoints, we do it
					// explicitly
					commandStr = ConfigurationAttributes.CLRBP_COMMAND;
					commandsList.add(commandStr);

					commandStr = ConfigurationAttributes.DO_SECOND_RESET_COMMAND;
					String resetType = CDebugUtils.getAttribute(fAttributes,
							ConfigurationAttributes.SECOND_RESET_TYPE,
							ConfigurationAttributes.SECOND_RESET_TYPE_DEFAULT);
					commandsList.add(commandStr + resetType);

					// Although the manual claims that reset always does a
					// halt, better issue it explicitly
					commandStr = ConfigurationAttributes.HALT_COMMAND;
					commandsList.add(commandStr);

					// Also add a command to see the registers in the
					// location where execution halted
					commandStr = ConfigurationAttributes.REGS_COMMAND;
					commandsList.add(commandStr);

					// Flush registers, GDB should read them again
					commandStr = ConfigurationAttributes.FLUSH_REGISTERS_COMMAND;
					commandsList.add(commandStr);
				}
			}

			String userCmd = CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.OTHER_RUN_COMMANDS,
					ConfigurationAttributes.OTHER_RUN_COMMANDS_DEFAULT);

			DebugUtils.addMultiLine(userCmd, commandsList);

			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.DO_CONTINUE,
					ConfigurationAttributes.DO_CONTINUE_DEFAULT)) {
				commandsList.add(ConfigurationAttributes.DO_CONTINUE_COMMAND);
			}

			if (commandsList.size() > 0) {
				CountingRequestMonitor crm = new CountingRequestMonitor(
						getExecutor(), rm);
				crm.setDoneCount(commandsList.size());
				queueCommands(commandsList, rm);
			} else {
				rm.done();
			}
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
	public void stepLoadImage(final RequestMonitor rm) {

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
					rm.done();
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

		boolean doConnectToRunning = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);

		if (!doConnectToRunning) {
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
						imageFileName = CDebugUtils.getAttribute(
								getAttributes(),
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

					String imageOffset = CDebugUtils.getAttribute(
							getAttributes(),
							IGDBJtagConstants.ATTR_IMAGE_OFFSET,
							IGDBJtagConstants.DEFAULT_IMAGE_OFFSET);
					if (imageOffset.length() > 0) {
						imageOffset = (imageFileName.endsWith(".elf")) ? "" : "0x" + CDebugUtils.getAttribute(getAttributes(), IGDBJtagConstants.ATTR_IMAGE_OFFSET, IGDBJtagConstants.DEFAULT_IMAGE_OFFSET); //$NON-NLS-2$ 
					}
					List<String> commands = new ArrayList<String>();
					fGdbJtagDevice.doLoadImage(imageFileName, imageOffset,
							commands);
					queueCommands(commands, rm);
				} else {
					rm.done();
				}
			} catch (CoreException e) {
				rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
						"Cannot load image", e)); //$NON-NLS-1$
				rm.done();
			}
		}
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

}
