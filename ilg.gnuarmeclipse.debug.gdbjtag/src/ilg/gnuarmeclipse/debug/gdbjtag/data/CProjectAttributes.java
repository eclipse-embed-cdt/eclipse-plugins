/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.data;

import ilg.gnuarmeclipse.core.CProjectPacksStorage;
import ilg.gnuarmeclipse.core.EclipseUtils;

import java.util.Map;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.debug.core.ILaunchConfiguration;

public class CProjectAttributes {

	/**
	 * Get the value of a given attribute.
	 * 
	 * @param configuration
	 * @param attributeName
	 * @return attribute value or null.
	 */
	public static String getCmsisAttribute(ILaunchConfiguration configuration, String attributeName) {

		// Get the build configuration description from the launch configuration
		ICConfigurationDescription cConfigDescription = EclipseUtils.getBuildConfigDescription(configuration);

		String atttributeValue = null;
		if (cConfigDescription != null) {
			// System.out.println(cConfigDescription);

			// The next step is to get the CDT configuration.
			IConfiguration config = EclipseUtils.getConfigurationFromDescription(cConfigDescription);
			// System.out.println(config);

			// The custom storage is specific to the CDT configuration.
			CProjectExtraDataManagerProxy dataManager = CProjectExtraDataManagerProxy.getInstance();
			Map<String, String> propertiesMap = dataManager.getExtraProperties(config);
			if (propertiesMap != null) {
				atttributeValue = propertiesMap.get(attributeName);
			}

			// System.out.println("CMSIS device name: " + cmsisDeviceName
			// + ", config: " + config + "/"
			// + config.getArtifactName() + ", launch: "
			// + configuration);
		}
		return atttributeValue;
	}

	public static String getCmsisDeviceName(ILaunchConfiguration configuration) {

		return getCmsisAttribute(configuration, CProjectPacksStorage.DEVICE_NAME);
	}

	public static String getCmsisBoardName(ILaunchConfiguration configuration) {

		return getCmsisAttribute(configuration, CProjectPacksStorage.BOARD_NAME);
	}

}
