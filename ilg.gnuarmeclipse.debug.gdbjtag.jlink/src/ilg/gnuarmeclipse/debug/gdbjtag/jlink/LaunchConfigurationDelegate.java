package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFLaunchConfigurationDelegate;
import org.eclipse.cdt.dsf.debug.service.IDsfDebugServicesFactory;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ISourceLocator;

public class LaunchConfigurationDelegate extends
		GDBJtagDSFLaunchConfigurationDelegate {

	ILaunchConfiguration fConfig = null;

	@Override
	protected IDsfDebugServicesFactory newServiceFactory(
			ILaunchConfiguration config, String version) {

		return new ServicesFactory(version);
		// return new GdbJtagDebugServicesFactory(version);
	}

	protected GdbLaunch createGdbLaunch(ILaunchConfiguration configuration,
			String mode, ISourceLocator locator) throws CoreException {

		if (configuration.getAttribute(
				ConfigurationAttributes.DO_START_GDB_SERVER,
				ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT)) {

			return new Launch(configuration, mode, locator);
		} else {
			return new GdbLaunch(configuration, mode, locator);
		}
	}

}
