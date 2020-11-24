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

package org.eclipse.embedcdt.debug.gdbjtag.viewmodel.peripheral;

import java.math.BigInteger;

import org.eclipse.embedcdt.debug.gdbjtag.datamodel.SvdDMNode;

public class PeripheralRegisterArrayElementVMNode extends PeripheralRegisterVMNode {

	// ------------------------------------------------------------------------

	protected BigInteger fBigIntegerAddressOffset;

	// ------------------------------------------------------------------------

	public PeripheralRegisterArrayElementVMNode(PeripheralTreeVMNode parent, SvdDMNode dmNode, int index,
			BigInteger offset) {

		super(parent, dmNode);

		// The node name must have a %s, substitute it with the actual index
		substituteRepetition(String.valueOf(index));
		fBigIntegerAddressOffset = offset;
	}

	/**
	 * Get the local offset to the array base.
	 */
	@Override
	public BigInteger getThisBigAddressOffset() {
		return fBigIntegerAddressOffset;
	}

	// ------------------------------------------------------------------------
}
