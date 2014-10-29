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

package ilg.gnuarmeclipse.debug.core.data;

/**
 * Factory to create a manager to handle C project extra data, like properties
 * (CMSIS device name & others.
 */
public interface ICProjectExtraDataManagerFactory {

	// ------------------------------------------------------------------------

	/**
	 * Create a data manager object.
	 * 
	 * @return the data manager object.
	 */
	public ICProjectExtraDataManager createDataManager();

	/**
	 * Free any resources used by the data manager. (currently not used).
	 */
	public void dispose();

	// ------------------------------------------------------------------------
}
