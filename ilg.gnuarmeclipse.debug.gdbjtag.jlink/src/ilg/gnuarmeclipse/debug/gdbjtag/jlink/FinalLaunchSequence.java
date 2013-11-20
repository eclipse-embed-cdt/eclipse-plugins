package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.gdbjtag.core.Activator;
import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFFinalLaunchSequence;
import org.eclipse.cdt.dsf.concurrent.CountingRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
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
							composeCommandWithLf(commands)),
					new DataRequestMonitor<MIInfo>(getExecutor(), rm));
		} else {
			rm.done();
		}
	}

	private String composeCommandWithLf(Collection<String> commands) {
		if (commands.isEmpty())
			return null;
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = commands.iterator();
		while (it.hasNext()) {
			sb.append(it.next().trim());
			if (it.hasNext()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	@Execute
	public void stepInitializeJTAGFinalLaunchSequence(RequestMonitor rm) {
		fTracker = new DsfServicesTracker(Activator.getBundleContext(),
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
	public void stepResetBoard(final RequestMonitor rm) {
		rm.done();
	}

	@Execute
	public void stepUserInitCommands(final RequestMonitor rm) {
		try {
			List<String> commandsList = new ArrayList<String>();

			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.DO_FIRST_RESET,
					ConfigurationAttributes.DO_FIRST_RESET_DEFAULT)) {
				String commandStr = ConfigurationAttributes.DO_FIRST_RESET_COMMAND;
				String resetType = CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.FIRST_RESET_TYPE,
						ConfigurationAttributes.FIRST_RESET_TYPE_DEFAULT);
				commandsList.add(commandStr + resetType);
			}

			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.ENABLE_SEMIHOSTING,
					ConfigurationAttributes.ENABLE_SEMIHOSTING_DEFAULT)) {
				String commandStr = ConfigurationAttributes.ENABLE_SEMIHOSTING_COMMAND;
				commandsList.add(commandStr);
			}

			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.ENABLE_SWO,
					ConfigurationAttributes.ENABLE_SWO_DEFAULT)) {
				String commandStr = ConfigurationAttributes.ENABLE_SWO_COMMAND;
				commandsList.add(commandStr);
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
				// for (int i = 0; i < commands.length; ++i) {
				// fCommandControl.queueCommand(
				// new CLICommand<MIInfo>(fCommandControl.getContext(),
				// commands[i]),
				// new DataRequestMonitor<MIInfo>(getExecutor(), crm));
				// }
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

}
