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

package ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel;

import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;

import java.math.BigInteger;

/**
 * Wrapper over the tree node parsed from the SVD file. All details of a
 * peripheral are available from this point. Some of them are cached, for faster
 * access.
 * 
 */
public class PeripheralDetails {

	private Leaf fNode;

	private BigInteger fNumericAddress;

	private String fDescription;

	private Boolean fIsSystem;

	/**
	 * The address where the Cortex-M system registers start.
	 */
	private static final BigInteger fSystemLimit = new BigInteger(
			"E0000000", 16);

	public PeripheralDetails(Leaf node) {

		fNode = node;

		fNumericAddress = null;
		fDescription = null;
		fIsSystem = null;
	}

	public void dispose() {
	}

	public Leaf getNode() {
		return fNode;
	}

	public String getName() {
		return fNode.getName();
	}

	public String getAddress() {
		return fNode.getProperty("address", "");
	}

	public long getLength() {

		// TODO: compute it
		return 0x123;
	}

	public String getDescription() {

		if (fDescription == null) {

			// Process multiple lines descriptions
			fDescription = StringUtils.joinMultiLine(fNode.getDescription());
		}
		return fDescription;
	}

	public BigInteger getNumericAddress() {

		if (fNumericAddress == null) {
			String str = getAddress();
			int base = 10;
			if ((str.startsWith("0x")) || (str.startsWith("0X"))) {
				base = 16;
				str = str.substring(2);
			} else if (str.startsWith("0")) {
				base = 8;
			}
			fNumericAddress = new BigInteger(str, base);
		}
		return fNumericAddress;
	}

	public boolean isSystem() {

		if (fIsSystem == null) {

			BigInteger addr = getNumericAddress();
			if (addr.compareTo(fSystemLimit) >= 0) {
				fIsSystem = new Boolean(true);
			} else {
				fIsSystem = new Boolean(false);
			}
		}
		return fIsSystem.booleanValue();
	}

	@Override
	public String toString() {
		
		return "[" + getName() + ", " + getAddress() + ", " + getLength()
				+ ", \"" + getDescription() + "\"]";
	}

}
