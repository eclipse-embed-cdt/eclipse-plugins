package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.gdbjtag.core.Activator;
import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFFinalLaunchSequence;
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
							Utils.composeCommandWithLf(commands)),
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
		attr = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.INTERFACE,
				ConfigurationAttributes.INTERFACE_SWD);
		if (ConfigurationAttributes.INTERFACE_SWD.equals(attr)) {
			commandsList.add(ConfigurationAttributes.INTERFACE_SWD_COMMAND);
		} else if (ConfigurationAttributes.INTERFACE_JTAG.equals(attr)) {
			commandsList.add(ConfigurationAttributes.INTERFACE_JTAG_COMMAND);
		}

		attr = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.FIRST_RESET_SPEED,
				ConfigurationAttributes.FIRST_RESET_SPEED_DEFAULT);
		if (attr.length() > 0) {
			commandsList
					.add(ConfigurationAttributes.INTERFACE_SPEED_FIXED_COMMAND
							+ attr);
		}

		attr = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.FLASH_DEVICE_NAME,
				ConfigurationAttributes.FLASH_DEVICE_NAME_DEFAULT);
		attr = attr.trim();
		if (attr.length() > 0) {
			commandsList.add(ConfigurationAttributes.FLASH_DEVICE_NAME_COMMAND
					+ attr);
		}

		attr = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.ENDIANNESS,
				ConfigurationAttributes.ENDIANNESS_LITTLE);
		if (ConfigurationAttributes.ENDIANNESS_LITTLE.equals(attr)) {
			commandsList.add(ConfigurationAttributes.ENDIANNESS_LITTLE_COMMAND);
		} else if (ConfigurationAttributes.ENDIANNESS_BIG.equals(attr)) {
			commandsList.add(ConfigurationAttributes.ENDIANNESS_BIG_COMMAND);
		}

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

			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.ENABLE_SEMIHOSTING,
					ConfigurationAttributes.ENABLE_SEMIHOSTING_DEFAULT)) {
				String commandStr = ConfigurationAttributes.ENABLE_SEMIHOSTING_COMMAND;
				commandsList.add(commandStr);
				
				int ioclientMask = 0;
				if (CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_TELNET,
						ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_TELNET_DEFAULT)) {
					ioclientMask |= ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_TELNET_MASK;
				}
				if (CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT,
						ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT_DEFAULT)) {
					ioclientMask |= ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT_MASK;
				}
				
				commandStr = ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_COMMAND + String.valueOf(ioclientMask);
				commandsList.add(commandStr);
			}

			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.ENABLE_SWO,
					ConfigurationAttributes.ENABLE_SWO_DEFAULT)) {
				String commandStr = ConfigurationAttributes.ENABLE_SWO_COMMAND;
				commandStr += CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.SWO_ENABLETARGET_CPUFREQ,
						ConfigurationAttributes.SWO_ENABLETARGET_CPUFREQ_DEFAULT);
				commandStr += " ";
				commandStr += CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.SWO_ENABLETARGET_SWOFREQ,
						ConfigurationAttributes.SWO_ENABLETARGET_SWOFREQ_DEFAULT);
				commandStr += " ";
				commandStr += CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.SWO_ENABLETARGET_PORTMASK,
						ConfigurationAttributes.SWO_ENABLETARGET_PORTMASK_DEFAULT);
				commandStr += " 0";
				
				commandsList.add(commandStr);
				
				//commandStr = ConfigurationAttributes.ENABLE_SWO_GETSPEEDINFO_COMMAND;
				//commandsList.add(commandStr);
			}

			String otherInits = CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.OTHER_INIT_COMMANDS,
					ConfigurationAttributes.OTHER_INIT_COMMANDS_DEFAULT);
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
					String commandStr = ConfigurationAttributes.DO_SECOND_RESET_COMMAND;
					String resetType = CDebugUtils.getAttribute(fAttributes,
							ConfigurationAttributes.SECOND_RESET_TYPE,
							ConfigurationAttributes.SECOND_RESET_TYPE_DEFAULT);
					commandsList.add(commandStr + resetType);
				}
			}

			String userCmd = CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.OTHER_RUN_COMMANDS,
					ConfigurationAttributes.OTHER_RUN_COMMANDS_DEFAULT);

			if (userCmd.length() > 0) {
				userCmd = VariablesPlugin.getDefault()
						.getStringVariableManager()
						.performStringSubstitution(userCmd);
				String[] commandsStr = userCmd.split("\\r?\\n"); //$NON-NLS-1$
				for (String str : commandsStr) {
					str = str.trim();
					if (str.length() > 0) {
						commandsList.add(str);
					}
				}
			}

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
	
}
