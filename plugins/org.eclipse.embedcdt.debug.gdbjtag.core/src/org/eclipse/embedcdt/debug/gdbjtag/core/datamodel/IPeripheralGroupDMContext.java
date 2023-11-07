package org.eclipse.embedcdt.debug.gdbjtag.core.datamodel;

import org.eclipse.cdt.dsf.datamodel.IDMContext;

public abstract interface IPeripheralGroupDMContext extends IDMContext, Comparable<IPeripheralGroupDMContext> {

	/**
	 * Get the peripheral group name.
	 *
	 * @return a short string, generally upper case.
	 */
	public abstract String getName();
}
