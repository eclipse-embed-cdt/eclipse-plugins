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

package org.eclipse.embedcdt.debug.gdbjtag.memory;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.model.MemoryByte;
import org.eclipse.embedcdt.debug.gdbjtag.viewmodel.peripheral.PeripheralRegisterVMNode;

public class PeripheralMemoryRegion implements Comparable<PeripheralMemoryRegion> {

	// ------------------------------------------------------------------------

	private long fAddressOffset;
	private long fSizeBytes;
	private List<PeripheralRegisterVMNode> fVMNodes;

	private MemoryByte[] fBytes;

	// ------------------------------------------------------------------------

	public PeripheralMemoryRegion(long offset, long sizeBytes) {

		fAddressOffset = offset;
		fSizeBytes = sizeBytes;

		fVMNodes = new LinkedList<PeripheralRegisterVMNode>();
	}

	// ------------------------------------------------------------------------

	public long getAddressOffset() {
		return fAddressOffset;
	}

	public long getSizeBytes() {
		return fSizeBytes;
	}

	public void addNode(PeripheralRegisterVMNode node) {
		fVMNodes.add(node);
	}

	public List<PeripheralRegisterVMNode> getNodes() {
		return fVMNodes;
	}

	/**
	 * Check if the parameter region is contiguous or contained in the current
	 * region.
	 * 
	 * @param region
	 *            the region to be compared with the current region.
	 * @return true if contiguous or contained.
	 */
	public boolean isContiguous(PeripheralMemoryRegion region) {
		return (region.fAddressOffset <= (fAddressOffset + fSizeBytes));
	}

	/**
	 * Concatenate to contiguous regions by adjusting the current region size to
	 * fully include the given region and take of region nodes.
	 * 
	 * @param region
	 *            the region to be concatenated with the current region.
	 */
	public void concatenate(PeripheralMemoryRegion region) {

		// Assume the regions are contiguous or contained.
		assert (region.fAddressOffset <= (fAddressOffset + fSizeBytes));

		if ((region.fAddressOffset + region.fSizeBytes) > (fAddressOffset + fSizeBytes)) {
			// Increase the size
			fSizeBytes = (region.fAddressOffset + region.fSizeBytes) - fAddressOffset;
		}

		// Take over nodes from given region.
		fVMNodes.addAll(region.getNodes());
	}

	public void setBytes(MemoryByte[] bytes) {
		fBytes = bytes;
	}

	public MemoryByte[] getBytes() {
		return fBytes;
	}

	// ------------------------------------------------------------------------

	@Override
	public int compareTo(PeripheralMemoryRegion comp) {
		return Long.signum((getAddressOffset() - comp.getAddressOffset()));
	}

	@Override
	public String toString() {
		return String.format("[Region 0x%08X, 0x%X, %d nodes]", fAddressOffset, fSizeBytes, fVMNodes.size());
	}

	// ------------------------------------------------------------------------
}
