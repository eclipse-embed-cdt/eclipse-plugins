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

package ilg.gnumcueclipse.debug.core.data;

/**
 * Factory to create a manager to handle the SVD path.
 */
public interface ISVDPathManagerFactory {

	// ------------------------------------------------------------------------

	/**
	 * Create the path manager object.
	 * 
	 * @return the data manager object.
	 */
	public ISVDPathManager create();

	/**
	 * Free any resources used by the data manager. (currently not used).
	 */
	public void dispose();

	// ------------------------------------------------------------------------
}
