package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import java.util.Map;

import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFFinalLaunchSequence_7_2;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl_7_2;
import org.eclipse.cdt.dsf.mi.service.command.CommandFactory;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

public class Control_7_2 extends GDBControl_7_2 {

	public Control_7_2(DsfSession session, ILaunchConfiguration config, CommandFactory factory) {
		super(session, config, factory);
	}

	@Override
	protected Sequence getCompleteInitializationSequence(Map<String,Object> attributes, RequestMonitorWithProgress rm) {
		return new FinalLaunchSequence_7_2(getSession(), attributes, rm);
	}

}
