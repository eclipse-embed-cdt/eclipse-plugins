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

package ilg.gnuarmeclipse.debug.gdbjtag.render.peripherals;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.ui.Messages;

import org.eclipse.debug.internal.ui.viewers.model.provisional.IColumnPresentation2;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Generic way of defining the content of the peripherals table. The
 * IColumnPresentation2 is used to allow setting the column widths.
 */
@SuppressWarnings("restriction")
public class PeripheralsColumnPresentation implements IColumnPresentation2 {

	// ------------------------------------------------------------------------

	/**
	 * The ColumnPresentation ID, referred internally by the implementation.
	 */
	public static final String ID = Activator.PLUGIN_ID + ".PERIPHERALS_COLUMN_PRESENTATION_ID";

	public static final String COLUMN_PERIPHERAL_ID = "column.peripheral";
	public static final String COLUMN_ADDRESS_ID = "column.address";
	public static final String COLUMN_DESCRIPTION_ID = "column.description";

	private static final int COLUMN_PERIPHERAL_SIZE = 150;
	private static final int COLUMN_ADDRESS_SIZE = 90;
	private static final int COLUMN_DESCRIPTION_SIZE = 300;

	private static final String[] fgAvailableColumns = { COLUMN_PERIPHERAL_ID, COLUMN_ADDRESS_ID,
			COLUMN_DESCRIPTION_ID };

	// ------------------------------------------------------------------------

	@Override
	public void init(IPresentationContext context) {
		;
	}

	@Override
	public void dispose() {
		;
	}

	@Override
	public String[] getAvailableColumns() {
		return fgAvailableColumns;
	}

	@Override
	public String[] getInitialColumns() {
		return fgAvailableColumns; // All available columns are visible.
	}

	@Override
	public String getHeader(String id) {

		if (COLUMN_PERIPHERAL_ID.equals(id)) {
			return Messages.PeripheralsView_NameColumn_text;
		}
		if (COLUMN_ADDRESS_ID.equals(id)) {
			return Messages.PeripheralsView_AddressColumn_text;
		}
		if (COLUMN_DESCRIPTION_ID.equals(id)) {
			return Messages.PeripheralsView_DescriptionColumn_text;
		}

		return null;
	}

	@Override
	public ImageDescriptor getImageDescriptor(String id) {
		return null; // No image used in table column header.
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public boolean isOptional() {
		return true;
	}

	@Override
	public int getInitialColumnWidth(String id, int treeWidgetWidth, String[] visibleColumnIds) {

		if (COLUMN_PERIPHERAL_ID.equals(id)) {
			return COLUMN_PERIPHERAL_SIZE;
		}
		if (COLUMN_ADDRESS_ID.equals(id)) {
			return COLUMN_ADDRESS_SIZE;
		}
		if (COLUMN_DESCRIPTION_ID.equals(id)) {

			// If the table is larger than needed,
			// make the Description column fill the space.
			if (treeWidgetWidth > (COLUMN_PERIPHERAL_SIZE + COLUMN_ADDRESS_SIZE + COLUMN_DESCRIPTION_SIZE)) {
				return treeWidgetWidth - (COLUMN_PERIPHERAL_SIZE + COLUMN_ADDRESS_SIZE);
			} else {
				return COLUMN_DESCRIPTION_SIZE;
			}
		}
		return -1; // Let the caller decide (actually not used).
	}

	// ------------------------------------------------------------------------
}
