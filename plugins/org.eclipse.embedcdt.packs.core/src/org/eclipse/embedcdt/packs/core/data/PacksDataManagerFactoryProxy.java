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
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.core.data;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.embedcdt.internal.packs.core.Activator;

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

	public static final String EXTENSION_POINT_NAME = "data";
	public static final String EXTENSION_POINT_ID = "org.eclipse.embedcdt.packs.core.data";

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

		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(EXTENSION_POINT_ID);
		if (extensionPoint == null) {
			Activator.log("Extension point " + EXTENSION_POINT_ID + " not found");
			return null;
		}
		IExtension[] extensions = extensionPoint.getExtensions();

		if (extensions.length != 1) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("No single " + EXTENSION_POINT_ID + " extension point");
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
		// Return true if the CMSIS Packs plug-in is available
		return false;
	}

	@Override
	public void dispose() {
	}
}
