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

package ilg.gnuarmeclipse.debug.gdbjtag.render.peripheral;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralRegisterFieldVMNode;
import ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralTreeVMNode;

import org.eclipse.debug.core.DebugException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

/**
 * Comparator used to sort columns in the Peripheral monitor.
 * 
 * Inspired by the code provided by Lars Vogella.
 */
public class PeripheralViewerComparator extends ViewerComparator {

	private int fColumnIndex;
	private static final int DESCENDING = 1;
	private static final int ASCENDING = 0;
	private int fDirection;

	public PeripheralViewerComparator() {
		fColumnIndex = 0;
		fDirection = ASCENDING;
	}

	public int getDirection() {
		return fDirection == DESCENDING ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Column " + column);
		}
		if (column == fColumnIndex) {
			// Same column as last sort; toggle the direction
			fDirection = 1 - fDirection;
		} else {
			// New column; do an ascending sort
			fColumnIndex = column;
			fDirection = ASCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {

		PeripheralTreeVMNode p1 = (PeripheralTreeVMNode) e1;
		PeripheralTreeVMNode p2 = (PeripheralTreeVMNode) e2;

		int comparison = 0;
		switch (fColumnIndex) {
		case 0:
			try {
				comparison = p1.getName().compareTo(p2.getName());
			} catch (DebugException e) {
				;
			}
			break;
		case 1:
			if (e1 instanceof PeripheralRegisterFieldVMNode && e2 instanceof PeripheralRegisterFieldVMNode) {
				// For bit fields, compare the start bit offset
				comparison = ((PeripheralRegisterFieldVMNode) e1).getOffsetBits()
						- ((PeripheralRegisterFieldVMNode) e2).getOffsetBits();
			} else {
				// For all others, compare hex address, as strings
				comparison = p1.getDisplayAddress().compareTo(p2.getDisplayAddress());
			}
			break;
		default:
			comparison = 0;
		}
		// If descending order, flip the direction
		if (fDirection == DESCENDING) {
			comparison = -comparison;
		}
		return comparison;
	}
}