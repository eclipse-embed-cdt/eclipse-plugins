package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import java.util.Map;

import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFFinalLaunchSequence;
import org.eclipse.cdt.dsf.concurrent.DsfExecutor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.service.DsfSession;

public class FinalLaunchSequence extends GDBJtagDSFFinalLaunchSequence {

	public FinalLaunchSequence(DsfExecutor executor, GdbLaunch launch,
			SessionType sessionType, boolean attach,
			RequestMonitorWithProgress rm) {
		super(executor, launch, sessionType, attach, rm);
	}

	public FinalLaunchSequence(DsfSession session, Map<String, Object> attributes, RequestMonitorWithProgress rm) {
		super(session, attributes, rm);
	}
}
