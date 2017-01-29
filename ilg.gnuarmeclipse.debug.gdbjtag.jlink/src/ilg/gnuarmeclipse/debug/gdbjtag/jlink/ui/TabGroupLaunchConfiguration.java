/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;

public class TabGroupLaunchConfiguration extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {

		// Normally the tabs should be defined in the plugin.xml, and
		// here just return an empty array:
		// setTabs(new ILaunchConfigurationTab[0]);
		// But the first attempt to make this work failed, it seems
		// there is something missing in the definitions and
		// the tab extensions are filtered out.

		// To avoid these problems and for a better control,
		// we manually define the tabs here.

		TabStartup tabStartup = new TabStartup();

		ILaunchConfigurationTab tabs[] = new ILaunchConfigurationTab[] { new TabMain(), new TabDebugger(tabStartup),
				tabStartup, new SourceLookupTab(), new CommonTab() };

		setTabs(tabs);

	}

}
