/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.core.gdbjtag.memory;

import ilg.gnuarmeclipse.debug.core.gdbjtag.viewmodel.peripheral.PeripheralRegisterVMNode;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.model.MemoryByte;

public class PeripheralMemoryRegion implements
		Comparable<PeripheralMemoryRegion> {

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

	public boolean isContiguous(PeripheralMemoryRegion region) {
		return ((fAddressOffset + fSizeBytes) == region.fAddressOffset);
	}

	public void concatenate(PeripheralMemoryRegion region) {

		// Assume the regions are contiguous
		assert ((fAddressOffset + fSizeBytes) == region.fAddressOffset);

		// Increase the size
		fSizeBytes += region.getSizeBytes();

		// Transfer nodes
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
		return String.format("[Region 0x%08X, 0x%X, %d nodes]", fAddressOffset,
				fSizeBytes, fVMNodes.size());
	}

	// ------------------------------------------------------------------------
}
