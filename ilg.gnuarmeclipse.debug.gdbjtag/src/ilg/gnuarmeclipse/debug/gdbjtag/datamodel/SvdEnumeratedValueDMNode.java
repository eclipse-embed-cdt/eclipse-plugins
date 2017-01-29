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

package ilg.gnuarmeclipse.debug.gdbjtag.datamodel;

import java.math.BigInteger;

import ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralValue;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;

public class SvdEnumeratedValueDMNode extends SvdObjectDMNode {

	// ------------------------------------------------------------------------

	public SvdEnumeratedValueDMNode(Leaf node) {
		super(node);
	}

	public String getValue() {
		return getNode().getProperty("value");

	}

	public boolean isDefault() {

		String isDefault = getNode().getProperty("isDefault");
		if (isDefault.isEmpty()) {
			return false;
		}

		return "true".equalsIgnoreCase(isDefault);
	}

	public boolean isMatchForValue(PeripheralValue value) {

		BigInteger bigEnumerationValue = null;
		try {
			// TODO: The value tag in enumeratedValue accepts do not care bits
			// represented by "x".

			bigEnumerationValue = SvdUtils.parseScaledNonNegativeBigInteger(getValue());

			if (bigEnumerationValue.equals(value.getBigValue())) {
				return true;
			}
		} catch (NumberFormatException e) {
			;
		}

		return false;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {

		String value = getValue();
		if (isDefault()) {
			value = "default";
		}

		return "[" + getClass().getSimpleName() + ": " + getName() + ", " + value + ", \"" + getDescription() + "\"]";
	}

	// ------------------------------------------------------------------------

}
