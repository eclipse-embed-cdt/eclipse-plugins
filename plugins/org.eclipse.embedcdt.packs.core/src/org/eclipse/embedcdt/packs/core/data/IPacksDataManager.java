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

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.core.runtime.IPath;
import org.eclipse.embedcdt.packs.core.tree.Leaf;
import org.eclipse.embedcdt.packs.core.tree.Node;

public interface IPacksDataManager {

	// ------------------------------------------------------------------------

	// TODO: add all functions when the interface is final.

	/**
	 * Provide access to the summary data.
	 * <p>
	 * If the data is not in memory, load it from the disk cache (the .content_*.xml
	 * files, one per repository)
	 *
	 * @return a tree of repositories/packages/versions/...
	 */
	public Node getRepositoriesTree();

	public Node getInstalledObjectsForBuild(IConfiguration config);

	public Node getCmsisCoreFiles(String deviceName, String compiler);

	public Node getRegisterDetailsForDebug(String deviceName);

	/**
	 * 
	 * @param node
	 * @return
	 */
	public String getCmsisDestinationFolder(Leaf node);

	/**
	 * Find a device in the tree of installed boards & devices. Also use the local
	 * project definitions.
	 * 
	 * @param packType
	 *            a string with the package type
	 * @param deviceSupplierId
	 *            a string with the supplier id
	 * @param deviceId
	 *            a string with the device id
	 * @param config
	 *            project configuration; may be null;
	 * @return the device node or null if not found.
	 */
	public Leaf findInstalledDevice(String packType, String deviceSupplierId, String deviceId, IConfiguration config);

	/**
	 * Find a board in the tree of installed boards & devices. Also use the local
	 * project definitions.
	 * 
	 * @param packType
	 *            a string with the package type
	 * @param boardSupplierId
	 *            a string with the supplier id; may be null;
	 * @param boardSupplierName
	 *            a string with the supplier name, if the supplier id is null;
	 * @param boardId
	 *            a string with the board id
	 * @param config
	 *            project configuration
	 * @return the board node or null, if not found
	 */
	public Leaf findInstalledBoard(String packType, String boardSupplierId, String boardSupplierName, String boardId,
			IConfiguration config);

	/**
	 * Get the absolute path of a SVD file associated with the given device.
	 * <p>
	 * For unsupported devices, this should return null.
	 * 
	 * @param packType
	 *            a string with the package type
	 * @param deviceSupplierId
	 *            a string with the CMSIS device vendor id.
	 * @param deviceId
	 *            a string with the CMSIS device id.
	 * @param config
	 *            project configuration
	 * @return the absolute path to the SVD file, or null.
	 */
	public IPath getSVDAbsolutePath(String packType, String deviceSupplierId, String deviceId, IConfiguration config);

	// ------------------------------------------------------------------------
}
