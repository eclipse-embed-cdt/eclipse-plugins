/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral;

import java.math.BigInteger;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.SvdDMNode;
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.SvdPeripheralDMNode;
import ilg.gnuarmeclipse.debug.gdbjtag.memory.PeripheralMemoryBlockExtension;

public class PeripheralTopVMNode extends PeripheralGroupVMNode {

	// ------------------------------------------------------------------------

	private PeripheralMemoryBlockExtension fMemoryBlock;

	// ------------------------------------------------------------------------

	public PeripheralTopVMNode(PeripheralTreeVMNode parent, SvdDMNode dmNode,
			PeripheralMemoryBlockExtension memoryBlock) {

		super(parent, dmNode);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralTopVMNode() " + dmNode.getName());
		}
		fMemoryBlock = memoryBlock;
	}

	@Override
	public void dispose() {

		fMemoryBlock = null;
		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralTopVMNode.dispose()");
		}
		super.dispose();
	}

	// ------------------------------------------------------------------------

	public PeripheralMemoryBlockExtension getMemoryBlock() {
		return fMemoryBlock;
	}

	/**
	 * Register groups are peripherals or clusters, return the address of the
	 * peripheral.
	 * 
	 * @return a big integer with the start address.
	 */
	@Override
	public BigInteger getBigAbsoluteAddress() {
		return fDMNode.getBigAbsoluteAddress();
	}

	@Override
	public String getDisplayNodeType() {
		return "Peripheral";
	}

	@Override
	public String getImageName() {
		return "peripheral";
	}

	// ------------------------------------------------------------------------

	public String getDisplayGroupName() {
		return ((SvdPeripheralDMNode) fDMNode).getGroupName();
	}

	public String getDisplayVersion() {
		return ((SvdPeripheralDMNode) fDMNode).getVersion();
	}

	// ------------------------------------------------------------------------
}
