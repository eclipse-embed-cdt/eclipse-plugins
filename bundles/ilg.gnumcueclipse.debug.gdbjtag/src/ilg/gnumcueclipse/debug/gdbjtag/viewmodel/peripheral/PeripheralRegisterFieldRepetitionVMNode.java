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

import ilg.gnumcueclipse.debug.gdbjtag.datamodel.SvdDMNode;
import ilg.gnumcueclipse.debug.gdbjtag.datamodel.SvdFieldDMNode;

public class PeripheralRegisterFieldRepetitionVMNode extends PeripheralRegisterFieldVMNode {

	// ------------------------------------------------------------------------

	private int fBitOffset;

	// ------------------------------------------------------------------------

	public PeripheralRegisterFieldRepetitionVMNode(PeripheralTreeVMNode parent, SvdDMNode dmNode, String subst,
			int offset) {

		super(parent, dmNode);

		// The node name must have a %s, substitute it with the actual substitution
		// value.
		substituteRepetition(subst);
		fBitOffset = ((SvdFieldDMNode) fDMNode).getOffset() + offset;
	}

	@Override
	public int getOffsetBits() {
		return fBitOffset;
	}

	// ------------------------------------------------------------------------
}
