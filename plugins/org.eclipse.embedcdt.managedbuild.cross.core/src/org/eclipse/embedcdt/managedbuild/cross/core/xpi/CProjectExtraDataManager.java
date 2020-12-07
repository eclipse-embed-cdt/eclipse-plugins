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

package org.eclipse.embedcdt.managedbuild.cross.core.xpi;

import java.util.Map;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.embedcdt.core.CProjectPacksStorage;
import org.eclipse.embedcdt.debug.core.data.ICProjectExtraDataManager;
import org.eclipse.embedcdt.internal.managedbuild.cross.core.Activator;

public class CProjectExtraDataManager implements ICProjectExtraDataManager {

	// ------------------------------------------------------------------------

	private static final CProjectExtraDataManager fgInstance;

	static {
		// Required via static, to simplify synchronisations
		fgInstance = new CProjectExtraDataManager();
	}

	public static CProjectExtraDataManager getInstance() {
		return fgInstance;
	}

	// ------------------------------------------------------------------------

	public CProjectExtraDataManager() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("CProjectExtraDataManager()");
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public Map<String, String> getExtraProperties(IConfiguration config) {

		try {
			CProjectPacksStorage storage = new CProjectPacksStorage(config);
			return storage.getOptions();
		} catch (CoreException e) {
			;
		}
		return null; // No extra properties
	}

	// ------------------------------------------------------------------------
}
