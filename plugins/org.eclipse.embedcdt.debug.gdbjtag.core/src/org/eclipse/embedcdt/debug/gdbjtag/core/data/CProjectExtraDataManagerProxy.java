/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.core.data;

import java.util.Map;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.embedcdt.debug.core.data.ICProjectExtraDataManager;
import org.eclipse.embedcdt.debug.core.data.ICProjectExtraDataManagerFactory;
import org.eclipse.embedcdt.debug.gdbjtag.core.Activator;

public class CProjectExtraDataManagerProxy implements ICProjectExtraDataManager {

	// ------------------------------------------------------------------------

	private static final String FACTORY_ELEMENT = "factory";
	private static final String CLASS_ATTRIBUTE = "class";

	// ------------------------------------------------------------------------

	private static CProjectExtraDataManagerProxy fgInstance;

	public static CProjectExtraDataManagerProxy getInstance() {

		if (fgInstance == null) {
			fgInstance = new CProjectExtraDataManagerProxy();
		}
		return fgInstance;
	}

	// ------------------------------------------------------------------------

	private ICProjectExtraDataManager fDataManagers[];

	public CProjectExtraDataManagerProxy() {

		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID);
		if (extensionPoint == null) {
			Activator.log("Extension point " + EXTENSION_POINT_ID + " not found");
			return;
		}
		
		IExtension[] extensions = extensionPoint.getExtensions();
		if (extensions.length == 0) {
			Activator.log("Extension point " + EXTENSION_POINT_ID + " has no extensions");
			return;
		}

		fDataManagers = new ICProjectExtraDataManager[extensions.length];

		for (int i = 0; i < extensions.length; ++i) {

			fDataManagers[i] = null;

			IExtension extension = extensions[i];
			IConfigurationElement[] configElements = extension.getConfigurationElements();
			IConfigurationElement configElement = configElements[0];

			if (FACTORY_ELEMENT.equals(configElement.getName())) {

				ICProjectExtraDataManagerFactory factory;
				try {
					Object obj = configElement.createExecutableExtension(CLASS_ATTRIBUTE);

					if (obj instanceof ICProjectExtraDataManagerFactory) {
						factory = (ICProjectExtraDataManagerFactory) obj;

						// Create the extension point data manager.
						fDataManagers[i] = factory.create();
					} else {
						Activator.log("no ICProjectExtraDataManagerFactory");
					}
				} catch (CoreException e) {
					Activator.log("cannot get factory for " + EXTENSION_POINT_ID);
				}
			} else {
				Activator.log("no <factory> element");
			}

		}
	}

	// ------------------------------------------------------------------------

	@Override
	public Map<String, String> getExtraProperties(IConfiguration config) {

		if (fDataManagers == null) {
			return null;
		}

		// Iterate all managers and return the first value available.
		for (int i = 0; i < fDataManagers.length; ++i) {
			Map<String, String> map = null;
			if (fDataManagers[i] != null) {
				map = fDataManagers[i].getExtraProperties(config);
				if (map != null) {
					return map;
				}
			}
		}

		return null;
	}

	// ------------------------------------------------------------------------
}
