/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.core.data;

import ilg.gnuarmeclipse.packs.core.Activator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

public class DataManagerFactoryProxy implements IDataManagerFactory,
		IAvailableSupport {

	private static final String FACTORY_ELEMENT = "factory";
	private static final String CLASS_ATTRIBUTE = "class";

	// ------------------------------------------------------------------------

	private static DataManagerFactoryProxy sfInstance;

	public static DataManagerFactoryProxy getInstance() {

		if (sfInstance == null) {
			sfInstance = new DataManagerFactoryProxy();
		}
		return sfInstance;
	}

	// ------------------------------------------------------------------------

	private IDataManager fDataManager;

	public DataManagerFactoryProxy() {
		fDataManager = null;
	}

	@Override
	public IDataManager createDataManager() {

		if (fDataManager != null) {
			return fDataManager;
		}

		System.out.println("DataManagerFactoryProxy.createDataManager()");

		IExtension[] extensions = Platform.getExtensionRegistry()
				.getExtensionPoint(Activator.PLUGIN_ID, "data").getExtensions();

		if (extensions.length != 1) {
			System.out.println("no single core.data xp");
			return null;
		}

		IExtension extension = extensions[0];
		IConfigurationElement[] configElements = extension
				.getConfigurationElements();
		IConfigurationElement configElement = configElements[0];

		if (!FACTORY_ELEMENT.equals(configElement.getName())) {
			System.out.println("no <factory> element");
			return null;
		}

		IDataManagerFactory factory;
		try {
			Object obj = configElement
					.createExecutableExtension(CLASS_ATTRIBUTE);

			if (obj instanceof IDataManagerFactory) {
				factory = (IDataManagerFactory) obj;
				fDataManager = factory.createDataManager();

				System.out
						.println("DataManagerFactoryProxy.createDataManager() completed");
				return fDataManager;
			} else {
				return null;
			}
		} catch (CoreException e) {
			System.out.println("cannot get factory");
			return null;
		}
	}

	@Override
	public boolean isAvailable() {
		// Return true if the Packs plug-in is available
		return false;
	}

	@Override
	public void dispose() {
	}
}
