package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ISourceLocator;

public class Launch extends GdbLaunch {

	ILaunchConfiguration fConfig = null;

	public Launch(ILaunchConfiguration launchConfiguration, String mode,
			ISourceLocator locator) {
		super(launchConfiguration, mode, locator);

		fConfig = launchConfiguration;
		// fExecutor = getDsfExecutor();
		// fSession = getSession();
	}

	public void initialize() {
		try {
			if (fConfig.getAttribute(
					ConfigurationAttributes.DO_START_GDB_SERVER,
					ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT)) {

				System.out.println("initialize() server");
			}
		} catch (CoreException e) {
		}

		super.initialize();
	}

	public void shutdownSession(final RequestMonitor rm) {

		super.shutdownSession(rm);

		try {
			if (fConfig.getAttribute(
					ConfigurationAttributes.DO_START_GDB_SERVER,
					ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT)) {

				System.out.println("shutdownSession() server");
			}
		} catch (CoreException e) {
		}
	}
}
