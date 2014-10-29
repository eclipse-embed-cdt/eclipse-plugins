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

import java.math.BigInteger;
import java.util.regex.Pattern;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * Peripheral register values usually are BigInteger but for enumerations can be
 * String.
 */
public class PeripheralValue implements IValue {

	// ------------------------------------------------------------------------

	public static final int FORMAT_NONE = 0;
	public static final int FORMAT_HEX = 1;
	public static final int FORMAT_HEX8 = 2;
	public static final int FORMAT_HEX16 = 3;
	public static final int FORMAT_HEX32 = 4;
	public static final int FORMAT_HEX64 = 5;

	public static boolean isNumeric(String str) {
		String regExp = "(0[xX]|\\+|\\-)?[0-9A-Fa-f]+";
		return Pattern.matches(regExp, str);
	}

	// ------------------------------------------------------------------------

	private Object fValue;
	private int fDisplayFormat;

	// ------------------------------------------------------------------------

	public PeripheralValue() {
		fValue = null;
		fDisplayFormat = FORMAT_NONE;
	}

	// ------------------------------------------------------------------------

	/**
	 * Set the format used when converting the value to string.
	 * 
	 * @param displayFormat
	 *            an integer (FORMAT_NONE, FORMAT_HEX, ...)
	 */
	public void setDisplayFormat(int displayFormat) {
		fDisplayFormat = displayFormat;
	}

	public void setDisplayFormatForBitWidth(int bitWidth) {

		if (bitWidth <= 8) {
			setDisplayFormat(PeripheralValue.FORMAT_HEX8);
		} else if (bitWidth <= 16) {
			setDisplayFormat(PeripheralValue.FORMAT_HEX16);
		} else if (bitWidth <= 32) {
			setDisplayFormat(PeripheralValue.FORMAT_HEX32);
		} else if (bitWidth <= 64) {
			setDisplayFormat(PeripheralValue.FORMAT_HEX64);
		} else {
			setDisplayFormat(PeripheralValue.FORMAT_HEX);
		}

	}

	/**
	 * Update the object value field with possibly a different node.
	 * 
	 * @param value
	 *            the new node.
	 * @return true if the new node is different.
	 */
	public boolean update(Object value) {

		Object previousValue = fValue;
		fValue = value;
		boolean hasChanged;
		if (previousValue != null) {
			hasChanged = !previousValue.equals(fValue);
		} else {
			hasChanged = false; // First time set, not a value change
		}
		return hasChanged;
	}

	public boolean hasValue() {
		return fValue != null;
	}

	/**
	 * Get the numeric value.
	 * 
	 * @return a BigInteger value.
	 */
	public BigInteger getBigValue() {

		if (fValue instanceof BigInteger) {
			return (BigInteger) fValue;
		} else {
			return null;
		}
	}

	public boolean isNumeric() {
		return (fValue instanceof BigInteger);
	}

	// ------------------------------------------------------------------------

	@Override
	public String getModelIdentifier() {
		return null;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return null;
	}

	@Override
	public ILaunch getLaunch() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public String getReferenceTypeName() throws DebugException {
		return null;
	}

	@Override
	public String getValueString() throws DebugException {

		if (isNumeric()) {

			// Numeric values are formatted as upper case hex strings, with
			// various length.
			BigInteger value = getBigValue();

			if (fDisplayFormat == FORMAT_HEX8) {
				return String.format("0x%02X", value);
			} else if (fDisplayFormat == FORMAT_HEX16) {
				return String.format("0x%04X", value);
			} else if (fDisplayFormat == FORMAT_HEX32) {
				return String.format("0x%08X", value);
			} else if (fDisplayFormat == FORMAT_HEX64) {
				return String.format("0x%016X", value);
			} else if (fDisplayFormat == FORMAT_HEX) {
				return String.format("0x%X", value);
			} else {
				// Default is hex 32/64
				if (value.bitCount() <= 32) {
					// 32-bit register value
					return String.format("0x%08X", value);
				} else {
					// 64-bit register value
					return String.format("0x%016X", value);
				}
			}
		} else if (fValue != null) {
			return fValue.toString();
		} else {
			return "";
		}
	}

	@Override
	public boolean isAllocated() throws DebugException {
		return false;
	}

	@Override
	public IVariable[] getVariables() throws DebugException {
		return null;
	}

	@Override
	public boolean hasVariables() throws DebugException {
		return false;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		try {
			return getValueString();
		} catch (DebugException e) {
			return super.toString();
		}
	}

	// ------------------------------------------------------------------------
}
