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

import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.SvdDMNode;

import java.math.BigInteger;

public class PeripheralClusterArrayElementVMNode extends PeripheralClusterVMNode {

	// ------------------------------------------------------------------------

	protected BigInteger fBigArrayAddressOffset;

	// ------------------------------------------------------------------------

	public PeripheralClusterArrayElementVMNode(PeripheralTreeVMNode parent, SvdDMNode dmNode, String index,
			BigInteger offset) {

		super(parent, dmNode);

		// The node name must have a %s, substitute it with the actual index
		substituteIndex(index);
		fBigArrayAddressOffset = offset;
	}

	/**
	 * Get the local offset to the array base.
	 */
	@Override
	public BigInteger getThisBigAddressOffset() {
		return fBigArrayAddressOffset;
	}

}
