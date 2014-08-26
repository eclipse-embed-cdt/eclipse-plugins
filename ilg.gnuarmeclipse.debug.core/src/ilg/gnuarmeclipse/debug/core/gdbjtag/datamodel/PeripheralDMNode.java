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

/**
 * Peripheral data model class.
 */

public class PeripheralDMNode {

	private String fName;
	private String fAddress;
	private long fLength;
	private String fDescription;

	public PeripheralDMNode() {
		fName = "";
		fAddress = "";
		fLength = 0;
		fDescription = "";
	}

	public PeripheralDMNode(String name, String address, long length,
			String description) {
		fName = name;
		fAddress = address;
		fLength = length;
		fDescription = description;
	}

	public String getName() {
		return fName;
	}

	public void setName(String name) {
		fName = name;
	}

	public String getAddress() {
		return fAddress;
	}

	public void setAddress(String address) {
		fAddress = address;
	}

	public long getLength() {
		return fLength;
	}

	public void setLength(long length) {
		fLength = length;
	}

	public String getDescription() {
		return fDescription;
	}

	public void setDescription(String description) {
		fDescription = description;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof PeripheralDMNode) {

			PeripheralDMNode comp = (PeripheralDMNode) obj;

			// Nodes are equal when all members are equal. Might be debated,
			// since names must be unique.
			return fName.equals(comp.fName) && fAddress.equals(comp.fAddress)
					&& (fLength == comp.fLength)
					&& fDescription.equals(comp.fDescription);
		}
		return false;
	}

	@Override
	public String toString() {
		return fName + ", " + fAddress + ", " + fLength + ", \"" + fDescription
				+ "\"";

	}
}
