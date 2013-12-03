package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.cdt.dsf.concurrent.ImmediateRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.service.GDBBackend;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;

public class Backend extends GDBBackend {

	public Backend(DsfSession session, ILaunchConfiguration lc) {
		super(session, lc);
	}

	@Override
	public void initialize(final RequestMonitor requestMonitor) {
		System.out.println("Backend.initialise()");

		super.initialize(new ImmediateRequestMonitor(requestMonitor) {
			@Override
			protected void handleSuccess() {
				System.out.println("Backend.initialise() successful");
				SourceProvider.sourceProvider.setRestartEnabled(true);
				super.handleSuccess();
			}
		});
	}

	@Override
	public void shutdown(final RequestMonitor requestMonitor) {
		System.out.println("Backend.shutdown()");
		SourceProvider.sourceProvider.setRestartEnabled(false);

		super.shutdown(requestMonitor);
	}

	protected Process launchGDBProcess(String commandLine) throws CoreException {
		System.out.println("Backend.launchGDBProcess(\"" + commandLine + "\")");
		return super.launchGDBProcess(commandLine);
	}

}
