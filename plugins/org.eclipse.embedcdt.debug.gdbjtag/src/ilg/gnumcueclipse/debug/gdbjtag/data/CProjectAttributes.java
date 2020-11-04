/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.data;

import java.util.Map;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.debug.core.ILaunchConfiguration;

import ilg.gnumcueclipse.core.CProjectPacksStorage;
import ilg.gnumcueclipse.core.EclipseUtils;

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

		return getCmsisAttribute(configuration, CProjectPacksStorage.CMSIS_DEVICE_NAME);
	}

	public static String getCmsisBoardName(ILaunchConfiguration configuration) {

		return getCmsisAttribute(configuration, CProjectPacksStorage.CMSIS_BOARD_NAME);
	}

}
