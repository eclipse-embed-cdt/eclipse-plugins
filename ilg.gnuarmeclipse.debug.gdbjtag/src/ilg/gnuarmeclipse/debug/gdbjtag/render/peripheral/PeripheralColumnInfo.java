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

package ilg.gnuarmeclipse.debug.gdbjtag.render.peripheral;

public class PeripheralColumnInfo {

	// ------------------------------------------------------------------------

	public static enum ColumnType {
		ADDRESS, _DESCRIPTION, _IMAGE, _OFFSET, REGISTER, _SIZE, _TYPE, VALUE;
	}

	// ------------------------------------------------------------------------

	public String header;
	public ColumnType type;
	public int weight;
	public boolean sortable;

	// ------------------------------------------------------------------------

	public PeripheralColumnInfo(String header, int weight, ColumnType type) {
		this(header, weight, type, false);
	}

	public PeripheralColumnInfo(String header, int weight, ColumnType type, boolean sortable) {

		this.header = header;
		this.weight = weight;
		this.type = type;
		this.sortable = sortable;
	}

	// ------------------------------------------------------------------------
}
