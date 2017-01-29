/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.data;

import java.util.Map;

import ilg.gnuarmeclipse.debug.core.data.ICProjectExtraDataManager;
import ilg.gnuarmeclipse.debug.core.data.ICProjectExtraDataManagerFactory;
import ilg.gnuarmeclipse.debug.gdbjtag.Activator;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

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

		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID).getExtensions();

		if (extensions.length == 0) {
			Activator.log("no cprojectExtra extension point");
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
