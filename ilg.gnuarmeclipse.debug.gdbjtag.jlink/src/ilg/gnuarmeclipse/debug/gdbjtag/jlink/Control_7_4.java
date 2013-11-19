package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

public class Control_7_4 extends Control_7_2 {

	public Control_7_4(DsfSession session, ILaunchConfiguration config, CommandFactory factory) {
		super(session, config, factory);
	}
	
	@Override
	public void setPrintPythonErrors(boolean enabled, RequestMonitor rm) {
		// With GDB 7.4, the command 'maintenance set python print-stack' has been replaced by
		// the new command "set python print-stack none|full|message".
		// Bug 367788
		String errorOption = enabled ? "full" : "none"; //$NON-NLS-1$ //$NON-NLS-2$
		queueCommand(
			getCommandFactory().createMIGDBSetPythonPrintStack(getContext(), errorOption),
			new DataRequestMonitor<MIInfo>(getExecutor(), rm));	
	}

}
