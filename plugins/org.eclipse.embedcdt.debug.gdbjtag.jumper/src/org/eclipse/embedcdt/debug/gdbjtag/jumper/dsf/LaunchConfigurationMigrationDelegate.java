package org.eclipse.embedcdt.debug.gdbjtag.jumper.dsf;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationMigrationDelegate;

// Not called by the launcher.
public class LaunchConfigurationMigrationDelegate implements ILaunchConfigurationMigrationDelegate {

	@Override
	public boolean isCandidate(ILaunchConfiguration candidate) throws CoreException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void migrate(ILaunchConfiguration candidate) throws CoreException {
		// TODO Auto-generated method stub
	}

}
