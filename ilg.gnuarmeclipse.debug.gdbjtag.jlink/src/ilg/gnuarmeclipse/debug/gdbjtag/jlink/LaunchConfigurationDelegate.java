package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.cdt.debug.gdbjtag.core.GDBJtagDSFLaunchConfigurationDelegate;
import org.eclipse.cdt.dsf.debug.service.IDsfDebugServicesFactory;
import org.eclipse.debug.core.ILaunchConfiguration;

public class LaunchConfigurationDelegate extends
		GDBJtagDSFLaunchConfigurationDelegate {

	@Override
	protected IDsfDebugServicesFactory newServiceFactory(ILaunchConfiguration config, String version) {

		return new ServicesFactory(version);
		//return new GdbJtagDebugServicesFactory(version);
	}

}
