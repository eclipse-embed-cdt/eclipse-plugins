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

import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;

public interface IDataManager {

	// TODO: add all functions when the interface is final.

	/**
	 * Provide access to the summary data.
	 * <p>
	 * If the data is not in memory, load it from the disk cache (the
	 * .content_*.xml files, one per repository)
	 *
	 * @return a tree of repositories/packages/versions/...
	 */
	public Node getRepositoriesTree();

	public Node getInstalledObjectsForBuild();

	public Node getCmsisCoreFiles(String deviceName, String compiler);

	public Node getRegisterDetailsForDebug(String deviceName);

	/**
	 * Find a device in the tree of installed boards & devices.
	 * 
	 * @param deviceVendorId
	 *            a string with the numeric id of the device vendor.
	 * @param deviceName
	 *            a string with the device name.
	 * @return the device node or null if not found.
	 */
	public Leaf findInstalledDevice(String deviceVendorId, String deviceName);

	/**
	 * Find a board in the tree of installed boards & devices.
	 * 
	 * @param boardVendorName
	 *            a string with the vendor name
	 * @param boardName
	 *            a string with the board name
	 * @return the board node or null, if not found
	 */
	public Leaf findInstalledBoard(String boardVendorName, String boardName);

}
