/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.core.gdbjtag.viewmodel.peripheral;

import ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel.SvdDMNode;
import ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel.SvdRegisterDMNode;
import ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel.SvdUtils;

import java.math.BigInteger;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;

/**
 * PeripheralRegister objects are used to represent peripheral registers or
 * fields in the rendering view. Since SVD hierarchies are simple, registers are
 * direct descendants of another register group and register children can be
 * only fields.
 */
public class PeripheralRegisterVMNode extends PeripheralTreeVMNode {

	// ------------------------------------------------------------------------

	protected BigInteger fBitMask;
	protected PeripheralValue fValue;

	// ------------------------------------------------------------------------

	public PeripheralRegisterVMNode(PeripheralTreeVMNode parent,
			SvdDMNode dmNode) {

		super(parent, dmNode);

		fBitMask = null;

		fValue = new PeripheralValue();
		fValue.setDisplayFormatForBitWidth(getWidthBits());
	}

	/**
	 * Clear all internal references.
	 */
	@Override
	public void dispose() {

		fValue = null;
		super.dispose();
	}

	// ------------------------------------------------------------------------

	@Override
	public IValue getValue() throws DebugException {
		return fValue;
	}

	@Override
	public boolean supportsValueModification() {
		return super.supportsValueModification();
	}

	public PeripheralValue getPeripheralValue() {
		return fValue;
	}

	public String getValueString() {

		IValue value = null;
		try {
			value = getValue();
		} catch (DebugException e1) {
		}

		if (value instanceof PeripheralValue) {

			try {
				return value.getValueString();
			} catch (DebugException e) {
			}

		}
		return "";
	}

	@Override
	public void setValue(String value) throws DebugException {

		System.out.println("setValue(" + value + ")");
	}

	@Override
	public void setValue(IValue value) throws DebugException {

		System.out.println("setValue(" + value + ")");

		if ((value instanceof PeripheralValue)) {
			fValue = ((PeripheralValue) value);
		} else {
			setValue(value.getValueString());
		}
		fHasChanged = true;
	}

	@Override
	public boolean verifyValue(String expression) throws DebugException {

		// System.out.println("verifyValue(" + expression + ")");
		return PeripheralValue.isNumeric(expression);
	}

	@Override
	public boolean verifyValue(IValue value) throws DebugException {
		return verifyValue(value.getValueString());
	}

	// ------------------------------------------------------------------------

	public boolean setNumericValue(String value) {

		fHasChanged = false;
		try {
			if (!verifyValue(value)) {
				return false;
			}

			BigInteger bigValue;
			bigValue = SvdUtils.parseNonNegativeBigInteger(value);
			// Strip unused bits.
			bigValue = bigValue.and(getBitMask());

			// Update value and hasChanged flag
			update(bigValue);

		} catch (NumberFormatException e) {
			// Illegal values are ineffective, leave value unchanged.
		} catch (DebugException e) {
		}

		// Always refresh, to be sure fields are updated
		return true;
	}

	/**
	 * Update the current value and related children values.
	 * <p>
	 * If the new value is different from the previous value, the dirty flag is
	 * set.
	 * 
	 * @param newValue
	 *            the new value.
	 * @return true if the value changed.
	 */
	protected void update(BigInteger newValue) {

		boolean sameValue = getPeripheralValue().getBigValue().equals(newValue);

		if (!sameValue) {

			// Write register.
			getPeripheral().writeRegisterValue(getLongAddressOffset(),
					getWidthBytes(), newValue);

			// Read back.
			BigInteger actualValue = getPeripheral().readRegisterValue(
					getLongAddressOffset(), getWidthBytes());

			// Update with actual value.
			setValue(actualValue);
		}
	}

	public void setValue(BigInteger newValue) {

		// Update with actual value.
		fHasChanged = getPeripheralValue().update(newValue);

		Object[] children = getChildren();
		for (int i = 0; i < children.length; ++i) {
			if (children[i] instanceof PeripheralRegisterFieldVMNode) {
				((PeripheralRegisterFieldVMNode) children[i])
						.updateFieldValueFromParent();
			}
		}
	}

	/**
	 * Get the bit mask corresponding to the current register or field width.
	 * <p>
	 * The bit mask is computed as ((1&lt;&lt;width)-1) and it looks like 0x1,
	 * 0x3, 0x7...
	 * 
	 * @return a BigInteger with some right side bits set.
	 */
	protected BigInteger getBitMask() {

		if (fBitMask == null) {
			fBitMask = BigInteger.ONE;
			fBitMask = fBitMask.shiftLeft(getWidthBits());
			fBitMask = fBitMask.subtract(BigInteger.ONE);
		}
		return fBitMask;
	}

	/**
	 * Compute the absolute address of the register, by adding the register
	 * offset to the peripheral base.
	 * 
	 * @return a big integer with the absolute address.
	 */
	@Override
	public BigInteger getBigAbsoluteAddress() {

		BigInteger base;
		try {
			base = ((PeripheralGroupVMNode) getRegisterGroup())
					.getBigAbsoluteAddress();
		} catch (DebugException e) {
			base = BigInteger.ZERO;
		}
		BigInteger offset;
		PeripheralRegisterVMNode peripheralRegister = this;
		if (isField()) {
			peripheralRegister = (PeripheralRegisterVMNode) getParent();
		}
		offset = peripheralRegister.fDMNode.getBigAddressOffset();

		return base.add(offset);
	}

	@Override
	public String getDisplayNodeType() {
		return "Register";
	}

	@Override
	public String getImageName() {
		return "register_obj";
	}

	@Override
	public String getDisplaySize() {

		int width;
		width = getWidthBits();

		if (width == 1) {
			return "1 bit";
		} else if (width > 1) {
			return String.format("%d bits", width);
		}

		return null;
	}

	@Override
	public String getDisplayValue() {

		if (isWriteOnly()) {
			return "(write only)";
		}

		String readAction = getReadAction();
		if (!readAction.isEmpty()) {
			return "(read " + readAction + ")";
		}

		String value = getValueString();

		if (!value.isEmpty()) {
			return value;
		}

		return null;
	}

	// ------------------------------------------------------------------------

	/**
	 * Perform an explicit read of the register, usually when the register has
	 * read side effects and is not automatically fetched.
	 */
	public void forceReadRegister() {

		if (isField())
			return;

		// TODO: implement forceReadRegister()
		System.out.println("unimplemented forceReadRegister()");
	}

	public int getOffsetBits() {
		return 0;
	}

	public int getWidthBits() {
		return ((SvdRegisterDMNode) fDMNode).getWidthBits();
	}

	public int getWidthBytes() {
		return (getWidthBits() + 7) / 8;
	}

	// ------------------------------------------------------------------------
}
