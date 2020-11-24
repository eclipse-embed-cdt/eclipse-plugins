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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.debug.core.DebugException;
import org.eclipse.embedcdt.debug.gdbjtag.Activator;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.PeripheralValue;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.SvdDMNode;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.SvdEnumeratedValueDMNode;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.SvdEnumerationDMNode;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.SvdFieldDMNode;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.SvdObjectDMNode;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.SvdUtils;
import org.eclipse.embedcdt.packs.core.tree.Leaf;

public class PeripheralRegisterFieldVMNode extends PeripheralRegisterVMNode {

	// ------------------------------------------------------------------------

	private SvdEnumeratedValueDMNode fEnumeratedValueDMNode;

	// ------------------------------------------------------------------------

	public PeripheralRegisterFieldVMNode(PeripheralTreeVMNode parent, SvdDMNode dmNode) {

		super(parent, dmNode);

		fValue.setDisplayFormat(PeripheralValue.FORMAT_HEX);

		fEnumeratedValueDMNode = null;
	}

	// ------------------------------------------------------------------------

	public SvdEnumeratedValueDMNode getEnumeratedValueDMNode() {
		if (fEnumeratedValueDMNode == null) {
			try {
				fEnumeratedValueDMNode = ((SvdFieldDMNode) fDMNode).findEnumeratedValue((PeripheralValue) getValue());
			} catch (DebugException e) {
				Activator.log(e);
			}
		}
		return fEnumeratedValueDMNode;
	}

	public void clearEnumeratedValueDMNode() {
		fEnumeratedValueDMNode = null;
	}

	public String[] getEnumerationComboItems() {
		SvdEnumerationDMNode enumeration = ((SvdFieldDMNode) fDMNode).getWriteEnumerationDMNode();

		List<String> list = new LinkedList<String>();
		if (enumeration != null) {
			SvdObjectDMNode children[] = enumeration.getChildren();
			for (int i = 0; i < children.length; ++i) {
				SvdEnumeratedValueDMNode child = (SvdEnumeratedValueDMNode) children[i];
				String name = child.getDisplayName();
				BigInteger bigValue = SvdUtils.parseScaledNonNegativeBigInteger(child.getValue());
				// Use the same format as for displaying, "value: name"
				list.add(String.format("0x%X: %s", bigValue, name));
			}
		}
		// System.out.println("Combo has " + list.size());
		return list.toArray(new String[list.size()]);
	}

	/**
	 * Try to find the current field value in the list of possible enumeration combo
	 * selections.
	 * 
	 * @return an Integer with the index, or null if not found.
	 */
	public Integer getEnumerationComboIndex() {

		try {
			return ((SvdFieldDMNode) fDMNode).findEnumeratedComboIndex((PeripheralValue) getValue());

		} catch (DebugException e) {
			;
		}

		// Match not found.
		return null;
	}

	@Override
	public String getDisplayValue() {

		String readAction = getReadAction();
		if (!readAction.isEmpty()) {
			return "(read " + readAction + ")";
		}

		String value = getValueString();

		if (value.isEmpty()) {
			return null;
		}

		if (!isEnumeration()) {
			return value;
		}

		// Process enumerations, if any
		SvdEnumeratedValueDMNode node = getEnumeratedValueDMNode();
		if (node == null) {
			return value;
		}

		// Append the enumeration name to the existing value.
		String enumerationName = "";
		if (node.getNode().getPackType() == Leaf.PACK_TYPE_CMSIS) {
			enumerationName = node.getName();
		} else if (node.getNode().getPackType() == Leaf.PACK_TYPE_XPACK) {
			enumerationName = node.getDisplayName();
		}
		if (!enumerationName.isEmpty()) {
			value += ": " + enumerationName;
		}
		return value;
	}

	@Override
	public boolean supportsValueModification() {

		// Prevent write only fields to be modified
		return !isReadOnly() && isReadAllowed();
	}

	@Override
	public boolean verifyValue(String expression) throws DebugException {

		// System.out.println("verifyValue(" + expression + ")");

		boolean isValid = false;
		if (isEnumeration()) {
			// Enumerations come as normal values.
			isValid = PeripheralValue.isNumeric(expression);
		} else {
			isValid = PeripheralValue.isNumeric(expression);
		}
		return isValid;
	}

	// ------------------------------------------------------------------------

	@Override
	protected void update(BigInteger newValue) {

		boolean sameValue = getPeripheralValue().getBigValue().equals(newValue);

		if (!sameValue) {

			clearEnumeratedValueDMNode();

			// Propagate change to parent.
			PeripheralRegisterVMNode parent = (PeripheralRegisterVMNode) getParent();

			PeripheralValue parentValue = null;
			parentValue = parent.getPeripheralValue();

			BigInteger bigValue;
			if (parentValue.isNumeric()) {
				bigValue = parentValue.getBigValue();
			} else {
				bigValue = BigInteger.ZERO;
			}
			// Mask out existing value, using andNot().
			bigValue = bigValue.andNot(getBitMask().shiftLeft(getOffsetBits()));

			// Mask in new value, using or().
			bigValue = bigValue.or(newValue.and(getBitMask()).shiftLeft(getOffsetBits()));

			// Update the parent with the newly computed value.
			// This in turn will use updateFieldValueFromParent() to update
			// all fields, including this one.
			parent.update(bigValue);
		}
	}

	/**
	 * Update the current field after the parent changed.
	 * 
	 * @return true if the field value changed.
	 */
	public void updateFieldValueFromParent() {

		PeripheralRegisterVMNode parent = (PeripheralRegisterVMNode) getParent();
		PeripheralValue parentValue = parent.getPeripheralValue();

		BigInteger bigValue;
		if (parentValue.isNumeric()) {
			bigValue = parentValue.getBigValue();
			bigValue = bigValue.shiftRight(getOffsetBits()).and(getBitMask());
		} else {
			bigValue = BigInteger.ZERO;
		}

		// Update the current field with the newly computed value.
		setChanged(getPeripheralValue().update(bigValue));
	}

	@Override
	public String getDisplayNodeType() {
		return "Field";
	}

	@Override
	public String getImageName() {
		return "field";
	}

	@Override
	public String getDisplayAddress() {
		return convertFieldToString();
	}

	@Override
	public String getDisplayOffset() {
		return convertFieldToString();
	}

	/**
	 * Get the value to be displayed for the reset value, possibly with mask.
	 * 
	 * @return a string or null if no value is available.
	 */
	public String getDisplayResetValue() {

		if (fDMNode instanceof SvdFieldDMNode) {

			// Only registers have reset definitions
			String resetValue = ((SvdFieldDMNode) fDMNode).getResetValue();
			if (resetValue.isEmpty()) {
				return null;
			}

			String resetMask = ((SvdFieldDMNode) fDMNode).getResetMask();
			if (resetValue.isEmpty()) {
				return resetValue;
			}

			return resetValue + "/" + resetMask;
		}

		return null;
	}

	private String convertFieldToString() {

		int lsb = getOffsetBits();
		int msb = lsb + getWidthBits() - 1;

		if (lsb == msb) {
			return String.format("[%d]", lsb);
		} else {
			return String.format("[%d:%d]", msb, lsb);
		}
	}

	@Override
	public int getOffsetBits() {
		return ((SvdFieldDMNode) fDMNode).getOffset();
	}

	@Override
	public int getWidthBits() {
		return ((SvdFieldDMNode) fDMNode).getWidthBits();

	}

	public boolean isEnumeration() {
		return ((SvdFieldDMNode) fDMNode).isEnumeration();
	}

	@Override
	public BigInteger getThisBigAddressOffset() {
		return BigInteger.ZERO;
	}

	// ------------------------------------------------------------------------
}
