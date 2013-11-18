package ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;

public class LaunchConfigurationTabGroup extends
//LaunchConfigurationTabGroupWrapper {
		AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
//		setTabs(new ILaunchConfigurationTab[0]);
		
		setTabs(new ILaunchConfigurationTab[] {
//				new PDAMainTab(),
				new SourceLookupTab(),
				new CommonTab()
		});

	}

}
