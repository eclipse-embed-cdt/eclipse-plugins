/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.viewmodel.peripheral;

import java.math.BigInteger;

import ilg.gnumcueclipse.debug.gdbjtag.datamodel.SvdDMNode;

public class PeripheralRegisterRepetitionVMNode extends PeripheralRegisterVMNode {

	// ------------------------------------------------------------------------

	protected BigInteger fBigIntegerAddressOffset;

	// ------------------------------------------------------------------------

	public PeripheralRegisterRepetitionVMNode(PeripheralTreeVMNode parent, SvdDMNode dmNode, String subst,
			BigInteger offset) {

		super(parent, dmNode);

		// The node name must have a %s, substitute it with the actual substitution value.
		substituteRepetition(subst);
		fBigIntegerAddressOffset = dmNode.getBigAddressOffset().add(offset);
	}

	@Override
	public BigInteger getThisBigAddressOffset() {
		return fBigIntegerAddressOffset;
	}

	// ------------------------------------------------------------------------
}
