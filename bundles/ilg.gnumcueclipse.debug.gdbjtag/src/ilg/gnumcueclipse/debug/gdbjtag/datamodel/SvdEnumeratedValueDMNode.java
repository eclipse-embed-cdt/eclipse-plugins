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

package ilg.gnumcueclipse.debug.gdbjtag.datamodel;

import java.math.BigInteger;

import ilg.gnumcueclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralValue;
import ilg.gnumcueclipse.packs.core.tree.Leaf;

public class SvdEnumeratedValueDMNode extends SvdObjectDMNode {

	// ------------------------------------------------------------------------

	public SvdEnumeratedValueDMNode(Leaf node) {
		super(node);
	}

	/**
	 * 
	 * @return The enumeration value or an empty string, for default.
	 */
	public String getValue() {
		if (getNode().getPackType() == Leaf.PACK_TYPE_CMSIS) {
			return getNode().getProperty("value");
		} else if (getNode().getPackType() == Leaf.PACK_TYPE_XPACK) {
			String str = getNode().getName();
			if ("*".equals(str)) {
				return "";
			}
			return str;
		}
		return "";
	}

	public boolean isDefault() {

		if (getNode().getPackType() == Leaf.PACK_TYPE_CMSIS) {
			String isDefault = getNode().getProperty("isDefault");
			if (isDefault.isEmpty()) {
				return false;
			}

			return "true".equalsIgnoreCase(isDefault);
		} else if (getNode().getPackType() == Leaf.PACK_TYPE_XPACK) {
			return ("*".equals(getNode().getName()));
		}
		return false;
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
