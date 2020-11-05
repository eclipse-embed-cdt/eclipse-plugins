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

package org.eclipse.embedcdt.debug.gdbjtag.datamodel;

import java.math.BigInteger;

import org.eclipse.cdt.dsf.datamodel.AbstractDMContext;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.embedcdt.debug.gdbjtag.memory.MemoryBlockMonitor;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Peripheral data context, represents a handle to a chunk of data in the Data
 * Model.
 * 
 */
public class PeripheralDMContext extends AbstractDMContext
		implements IPeripheralDMContext, Comparable<IPeripheralDMContext> {

	// ------------------------------------------------------------------------

	/**
	 * Reference to the data model peripheral.
	 */
	private PeripheralDMNode fDMNode;

	// ------------------------------------------------------------------------

	public PeripheralDMContext(DsfSession session, IDMContext[] parents, PeripheralDMNode instance) {

		super(session, parents);

		fDMNode = instance;
	}

	// ------------------------------------------------------------------------

	public PeripheralDMNode getPeripheralInstance() {
		return fDMNode;
	}

	@Override
	public int compareTo(IPeripheralDMContext context) {

		// The peripheral names are (should be!) unique, use them for
		// sorting
		return getName().compareTo(context.getName());
	}

	@Override
	public boolean isSystem() {
		return fDMNode.isSystem();
	}

	@Override
	public String getId() {
		return fDMNode.getId();
	}

	@Override
	public String getName() {
		return fDMNode.getName();
	}

	@Override
	public String getRawAddress() {
		return fDMNode.getBaseAddress();
	}

	@Override
	public String getHexAddress() {
		return fDMNode.getHexAddress();
	}

	@Override
	public BigInteger getBigAddress() {
		return fDMNode.getBigAbsoluteAddress();
	}

	@Override
	public BigInteger getBigLength() {
		return fDMNode.getBigSizeBytes();
	}

	@Override
	public String getDescription() {
		return fDMNode.getDescription();
	}

	@Override
	public boolean isChecked() {
		return fDMNode.isChecked();
	}

	@Override
	public void setChecked(boolean flag) {
		fDMNode.setChecked(flag);
	}

	@Override
	public boolean hasMemoryMonitor() {
		return fDMNode.isShown();
	}

	/**
	 * Support function, to forward the action to the service, with more details,
	 * the DMcontext and the flag. Called from UI thread.
	 * 
	 * @param workbenchWindow
	 *            the window where the check widget is displayed.
	 */
	public void displayPeripheralMonitor(IWorkbenchWindow workbenchWindow) {

		MemoryBlockMonitor.getInstance().displayPeripheralMonitor(workbenchWindow, this, isChecked());
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof PeripheralDMContext) {

			PeripheralDMContext comp = (PeripheralDMContext) obj;
			return baseEquals(obj) && fDMNode.equals(comp.fDMNode);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return baseHashCode() + fDMNode.hashCode();
	}

	@Override
	public String toString() {
		return "[" + getSessionId() + ", " + getName() + ", " + getRawAddress() + ", " + getBigLength() + ", \""
				+ getDescription() + "\"" + "]";
	}

	// ------------------------------------------------------------------------
}
