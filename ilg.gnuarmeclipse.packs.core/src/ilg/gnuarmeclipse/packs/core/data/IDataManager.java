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

import ilg.gnuarmeclipse.packs.core.tree.Node;

public interface IDataManager {

	/**
	 * Get the devices from all installed packs, to be used in the device
	 * selection properties page in project settings page.
	 * 
	 * @return A tree of nodes, with Devices/Boards, Vendors, Family, Subfamily,
	 *         Device, Variant.
	 */
	public Node getInstalledDevicesForBuild();

	/**
	 * Get the list of files referring to CMSIS Core (ARM headers and vendor
	 * headers and source files), for the given device and compiler.
	 * 
	 * @param deviceName
	 * @param compiler
	 * @return A tree of nodes, CMSIS/Vendor, files.
	 */
	public Node getCmsisCoreFiles(String deviceName, String compiler);

	/**
	 * Get the register details (address and bit fields) for display/modify in
	 * the debug perspective.
	 * 
	 * @param deviceName
	 * @return (to be defined)
	 */
	public Node getRegisterDetailsForDebug(String deviceName);

	/**
	 * Get the documentation associated with the device and/or board in use.
	 * 
	 * @param deviceName
	 * @param boardName
	 * @return
	 */
	public Node getBooks(String deviceName, String boardName);

}
