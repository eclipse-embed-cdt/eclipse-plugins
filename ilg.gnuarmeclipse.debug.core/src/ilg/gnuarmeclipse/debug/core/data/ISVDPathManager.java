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

import org.eclipse.core.runtime.IPath;

/**
 * The interface of the manager used to handle SVD paths.
 */
public interface ISVDPathManager {

	// ------------------------------------------------------------------------

	public static final String EXTENSION_POINT_NAME = "svdPath";
	public static final String EXTENSION_POINT_ID = ilg.gnuarmeclipse.debug.core.Activator.PLUGIN_ID + "."
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
