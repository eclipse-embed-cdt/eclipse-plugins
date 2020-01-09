/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
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

package ilg.gnumcueclipse.debug.gdbjtag.viewmodel.peripheral;

import java.math.BigInteger;

import ilg.gnumcueclipse.debug.gdbjtag.datamodel.SvdDMNode;

public class PeripheralClusterRepetitionVMNode extends PeripheralClusterVMNode {

	// ------------------------------------------------------------------------

	protected BigInteger fBigIntegerAddressOffset;

	// ------------------------------------------------------------------------

	public PeripheralClusterRepetitionVMNode(PeripheralTreeVMNode parent, SvdDMNode dmNode, String subst,
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

}
