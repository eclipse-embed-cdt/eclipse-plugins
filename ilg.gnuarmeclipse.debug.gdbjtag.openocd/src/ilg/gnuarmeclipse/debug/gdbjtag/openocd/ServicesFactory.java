package ilg.gnuarmeclipse.debug.gdbjtag.openocd;

import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControl;
import org.eclipse.cdt.dsf.gdb.service.GDBBackend;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_0;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_1;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_2;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_2_1;
import org.eclipse.cdt.dsf.gdb.service.GdbDebugServicesFactory;
import org.eclipse.cdt.dsf.gdb.service.command.CommandFactory_6_8;
import org.eclipse.cdt.dsf.mi.service.IMIBackend;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ServicesFactory extends GdbDebugServicesFactory {

	private final String fVersion;

	public ServicesFactory(String version) {
		super(version);
		fVersion = version;
	}

	@Override
	protected ICommandControl createCommandControl(DsfSession session,
			ILaunchConfiguration config) {

		if (GDB_7_4_VERSION.compareTo(getVersion()) <= 0) {
			return new Control_7_4(session, config, new CommandFactory_6_8());
		}

		return super.createCommandControl(session, config);
	}

	protected IMIBackend createBackendGDBService(DsfSession session,
			ILaunchConfiguration lc) {
		
		// return new GDBBackend(session, lc);
		return new Backend(session, lc);
	}

	@Override
	protected IProcesses createProcessesService(DsfSession session) {

		if (GDB_7_2_1_VERSION.compareTo(fVersion) <= 0) {
			return new Processes_7_2_1(session);
		}

		return super.createProcessesService(session);
	}

}
