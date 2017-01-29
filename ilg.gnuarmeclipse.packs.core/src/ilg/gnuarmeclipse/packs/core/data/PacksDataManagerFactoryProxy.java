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

/**
 * This factory proxy allows plug-ins that do not depend on the packs plug-in to
 * get a packs data manager reference without having to use a hard dependency on
 * it.
 * <p>
 * Usage:
 * 
 * <pre>
 * IPacksDataManager dataManager = PacksDataManagerFactoryProxy.getInstance().createDataManager();
 * </pre>
 */
public class PacksDataManagerFactoryProxy implements IPacksDataManagerFactory, IAvailableSupport {

	private static final String FACTORY_ELEMENT = "factory";
	private static final String CLASS_ATTRIBUTE = "class";

	// ------------------------------------------------------------------------

	private static PacksDataManagerFactoryProxy fgInstance;

	public static PacksDataManagerFactoryProxy getInstance() {

		if (fgInstance == null) {
			fgInstance = new PacksDataManagerFactoryProxy();
		}
		return fgInstance;
	}

	// ------------------------------------------------------------------------

	private IPacksDataManager fDataManager;

	public PacksDataManagerFactoryProxy() {
		fDataManager = null;
	}

	/**
	 * Create a packs data manager using the extension point.
	 * 
	 * @return a reference to the data manager or null.
	 */
	@Override
	public IPacksDataManager createDataManager() {

		if (fDataManager != null) {
			return fDataManager;
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DataManagerFactoryProxy.createDataManager()");
		}

		IExtension[] extensions = Platform.getExtensionRegistry().getExtensionPoint(Activator.PLUGIN_ID, "data")
				.getExtensions();

		if (extensions.length != 1) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("no single core.data xp");
			}
			return null;
		}

		IExtension extension = extensions[0];
		IConfigurationElement[] configElements = extension.getConfigurationElements();
		IConfigurationElement configElement = configElements[0];

		if (!FACTORY_ELEMENT.equals(configElement.getName())) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("no <factory> element");
			}
			return null;
		}

		IPacksDataManagerFactory factory;
		try {
			Object obj = configElement.createExecutableExtension(CLASS_ATTRIBUTE);

			if (obj instanceof IPacksDataManagerFactory) {
				factory = (IPacksDataManagerFactory) obj;
				fDataManager = factory.createDataManager();

				if (Activator.getInstance().isDebugging()) {
					System.out.println("DataManagerFactoryProxy.createDataManager() completed");
				}
				return fDataManager;
			} else {
				return null;
			}
		} catch (CoreException e) {
			Activator.log(e.getStatus());
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
