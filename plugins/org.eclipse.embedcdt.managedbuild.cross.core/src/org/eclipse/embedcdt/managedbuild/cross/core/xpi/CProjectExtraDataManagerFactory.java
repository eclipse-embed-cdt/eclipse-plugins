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

import org.eclipse.embedcdt.debug.core.data.ICProjectExtraDataManager;
import org.eclipse.embedcdt.debug.core.data.ICProjectExtraDataManagerFactory;

public class CProjectExtraDataManagerFactory implements ICProjectExtraDataManagerFactory {

	// ------------------------------------------------------------------------

	public CProjectExtraDataManagerFactory() {

	}

	// ------------------------------------------------------------------------

	// Create a DataManager object
	@Override
	public ICProjectExtraDataManager create() {

		return CProjectExtraDataManager.getInstance();
	}

	@Override
	public void dispose() {

	}

	// ------------------------------------------------------------------------
}
