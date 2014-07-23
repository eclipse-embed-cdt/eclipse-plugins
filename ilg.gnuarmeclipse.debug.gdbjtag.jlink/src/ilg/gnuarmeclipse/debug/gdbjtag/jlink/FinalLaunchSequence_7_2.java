package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitorWithProgress;
import org.eclipse.cdt.dsf.gdb.service.IGDBProcesses;
import org.eclipse.cdt.dsf.gdb.service.command.IGDBControl;
import org.eclipse.cdt.dsf.service.DsfServicesTracker;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class FinalLaunchSequence_7_2 extends FinalLaunchSequence {

	private DsfSession fSession;

	public FinalLaunchSequence_7_2(DsfSession session,
			Map<String, Object> attributes, RequestMonitorWithProgress rm) {
		super(session, attributes, rm);
		fSession = session;
	}

	@Override
	protected String[] getExecutionOrder(String group) {
		if (GROUP_JTAG.equals(group)) {
			// Initialize the list with the base class' steps
			// We need to create a list that we can modify, which is why we
			// create our own ArrayList.
			List<String> orderList = new ArrayList<String>(Arrays.asList(super
					.getExecutionOrder(GROUP_JTAG)));

			// Now insert our steps right after the initialization of the base
			// class.
			orderList
					.add(orderList
							.indexOf("stepInitializeJTAGFinalLaunchSequence") + 1, "stepInitializeJTAGSequence_7_2"); //$NON-NLS-1$ //$NON-NLS-2$

			return orderList.toArray(new String[orderList.size()]);
		}

		return super.getExecutionOrder(group);
	}

	/**
	 * Initialize the members of the DebugNewProcessSequence_7_2 class. This
	 * step is mandatory for the rest of the sequence to complete.
	 */
	@Execute
	public void stepInitializeJTAGSequence_7_2(RequestMonitor rm) {
		DsfServicesTracker tracker = new DsfServicesTracker(Activator
				.getInstance().getBundle().getBundleContext(), fSession.getId());
		IGDBControl gdbControl = tracker.getService(IGDBControl.class);
		IGDBProcesses procService = tracker.getService(IGDBProcesses.class);
		tracker.dispose();

		if (gdbControl == null || procService == null) {
			rm.setStatus(new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
					"Cannot obtain service", null)); //$NON-NLS-1$
			rm.done();
			return;
		}
		setContainerContext(procService.createContainerContextFromGroupId(
				gdbControl.getContext(), "i1")); //$NON-NLS-1$
		rm.done();
	}

}
