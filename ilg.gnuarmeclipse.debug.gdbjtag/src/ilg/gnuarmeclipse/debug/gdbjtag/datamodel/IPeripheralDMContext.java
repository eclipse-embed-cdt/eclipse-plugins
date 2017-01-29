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

package ilg.gnuarmeclipse.debug.gdbjtag.datamodel;

import java.math.BigInteger;

import org.eclipse.cdt.dsf.datamodel.IDMContext;

/**
 * Data model interface for peripheral blocks.
 */
public abstract interface IPeripheralDMContext extends IDMContext, Comparable<IPeripheralDMContext> {

	// ------------------------------------------------------------------------

	public abstract String getId();

	/**
	 * Get the peripheral name.
	 * 
	 * @return a short string, generally upper case.
	 */
	public abstract String getName();

	/**
	 * Get the peripheral start address, as given in the configuration file.
	 * 
	 * @return a string hex representation of the peripheral address.
	 */
	public abstract String getRawAddress();

	/**
	 * Get the numerical converted value of the peripheral start address.
	 * 
	 * @return a big integer value with the address.
	 */
	public abstract BigInteger getBigAddress();

	/**
	 * Get the hex representation of the numerical value.
	 * 
	 * @return a string formatted with 0x%08x
	 */
	public abstract String getHexAddress();

	/**
	 * Get the peripheral block length.
	 * 
	 * @return the length of the peripheral block, in bytes.
	 */
	public abstract BigInteger getBigLength();

	/**
	 * Get the peripheral description.
	 * 
	 * @return a short string describing the peripheral.
	 */
	public abstract String getDescription();

	/**
	 * Check if the peripheral is located in the Cortex-M system address range.
	 * 
	 * @return true if system peripheral.
	 */
	public abstract boolean isSystem();

	/**
	 * Check if the peripheral check box is checked.
	 * 
	 * @return true if peripheral is checked.
	 */
	public abstract boolean isChecked();

	/**
	 * Set the checked flag.
	 * 
	 * @param flag
	 *            a boolean value to set the checked flag.
	 */
	public abstract void setChecked(boolean flag);

	/**
	 * Check if the peripheral is shown in the detail pane.
	 * 
	 * @return true if the peripheral is shown.
	 */
	public abstract boolean hasMemoryMonitor();

	public abstract int compareTo(IPeripheralDMContext obj);

	// ------------------------------------------------------------------------
}