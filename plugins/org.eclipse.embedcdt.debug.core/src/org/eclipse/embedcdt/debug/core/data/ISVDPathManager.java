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

package org.eclipse.embedcdt.debug.core.data;

import org.eclipse.core.runtime.IPath;

/**
 * The interface of the manager used to handle SVD paths.
 */
public interface ISVDPathManager {

	// ------------------------------------------------------------------------

	public static final String EXTENSION_POINT_NAME = "svdPath";
	static final String EXTENSION_POINT_ID = "ilg.gnumcueclipse.debug.core."
			+ EXTENSION_POINT_NAME;

	// ------------------------------------------------------------------------

	/**
	 * Get the absolute path of a SVD file associated with the given device.
	 * <p>
	 * For unsupported devices, this should return null.
	 * 
	 * @param deviceVendorId
	 *            a string with the CMSIS device vendor id.
	 * @param deviceName
	 *            a string with the CMSIS device name.
	 * @return the absolute path to the SVD file, or null.
	 */
	public IPath getSVDAbsolutePath(String deviceVendorId, String deviceName);

	// ------------------------------------------------------------------------
}
