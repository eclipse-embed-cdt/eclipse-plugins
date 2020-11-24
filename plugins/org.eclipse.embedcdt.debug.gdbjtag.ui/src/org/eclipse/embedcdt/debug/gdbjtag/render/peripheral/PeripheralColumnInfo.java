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
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.render.peripheral;

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
