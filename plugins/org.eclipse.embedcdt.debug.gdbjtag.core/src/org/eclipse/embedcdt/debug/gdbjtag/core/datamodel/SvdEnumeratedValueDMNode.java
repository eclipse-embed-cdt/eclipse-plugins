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

package org.eclipse.embedcdt.debug.gdbjtag.core.datamodel;

import java.math.BigInteger;

import org.eclipse.embedcdt.packs.core.tree.Leaf;

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
			String val = getValue();
			if (val.isEmpty()) {
				return false;
			}
			bigEnumerationValue = SvdUtils.parseScaledNonNegativeBigInteger(val);

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
