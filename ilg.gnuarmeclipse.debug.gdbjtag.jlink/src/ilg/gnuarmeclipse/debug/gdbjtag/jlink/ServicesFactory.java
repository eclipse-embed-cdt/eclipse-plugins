package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.cdt.debug.gdbjtag.core.dsf.gdb.service.GDBJtagControl_7_2;
import org.eclipse.cdt.debug.gdbjtag.core.dsf.gdb.service.GDBJtagControl_7_4;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControl;
import org.eclipse.cdt.dsf.gdb.service.GdbDebugServicesFactory;
import org.eclipse.cdt.dsf.gdb.service.command.CommandFactory_6_8;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ServicesFactory extends GdbDebugServicesFactory {

	public ServicesFactory(String version) {
		super(version);
	}

	@Override
	protected ICommandControl createCommandControl(DsfSession session,
			ILaunchConfiguration config) {
		if (GDB_7_4_VERSION.compareTo(getVersion()) <= 0) {
			return new Control_7_4(session, config, new CommandFactory_6_8());
		}
		if (GDB_7_2_VERSION.compareTo(getVersion()) <= 0) {
			return new Control_7_2(session, config, new CommandFactory_6_8());
		}
		return new Control(session, config, new CommandFactory());
	}

}
