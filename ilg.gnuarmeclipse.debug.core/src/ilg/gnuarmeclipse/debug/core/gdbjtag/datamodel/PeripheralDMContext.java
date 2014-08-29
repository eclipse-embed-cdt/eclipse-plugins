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

import java.math.BigInteger;

import org.eclipse.cdt.dsf.datamodel.AbstractDMContext;
import org.eclipse.cdt.dsf.datamodel.IDMContext;

/**
 * Peripheral data context, represents a handle to a chunk of data in the Data
 * Model.
 * 
 */
public class PeripheralDMContext extends AbstractDMContext implements
		IPeripheralDMContext, Comparable<PeripheralDMContext> {

	private final PeripheralsService fPeripheralsService;

	/**
	 * Reference to the data model peripheral.
	 */
	private PeripheralDMNode fInstance;

	public PeripheralDMContext(PeripheralsService service,
			IDMContext[] parents, PeripheralDMNode instance) {

		super(service.getSession(), parents);

		fPeripheralsService = service;
		fInstance = instance;
	}

	public PeripheralsService getPeripheralsService() {
		return this.fPeripheralsService;
	}

	@Override
	public int compareTo(PeripheralDMContext context) {

		// The peripheral names are (should be!) unique, use them for
		// sorting
		return getName().compareTo(context.getName());
	}

	@Override
	public boolean isSystem() {
		return fInstance.isSystem();
	}

	@Override
	public String getName() {
		return fInstance.getName();
	}

	@Override
	public String getAddress() {
		return fInstance.getAddress();
	}

	public BigInteger getNumericAddress() {
		return fInstance.getNumericAddress();
	}

	@Override
	public long getLength() {
		return fInstance.getLength();
	}

	@Override
	public String getDescription() {
		return fInstance.getDescription();
	}

	@Override
	public boolean isChecked() {
		return fInstance.isChecked();
	}

	@Override
	public void setChecked(boolean flag) {
		fInstance.setChecked(flag);
	}

	@Override
	public boolean isShown() {
		return fInstance.isShown();
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof PeripheralDMContext) {

			PeripheralDMContext comp = (PeripheralDMContext) obj;
			return baseEquals(obj) && fInstance.equals(comp.fInstance);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return baseHashCode() + fInstance.hashCode();
	}

	@Override
	public String toString() {
		return "[" + getSessionId() + ", " + getName() + ", " + getAddress()
				+ ", " + getLength() + ", \"" + getDescription() + "\"" + "]";
	}
}
