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

package ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.datamodel.SvdDMNode;

import java.math.BigInteger;
import java.util.ArrayList;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IRegister;
import org.eclipse.debug.core.model.IRegisterGroup;

/**
 * Register groups are a structuring method to group related registers in the
 * rendering view. SVD defines two grouping levels, the peripheral and the
 * cluster, so there are two different implementations, SvdPeripheral and
 * SvdCluster. Groups are descendants of other groups and group children can be
 * groups or registers.
 */
public class PeripheralGroupVMNode extends PeripheralTreeVMNode implements IRegisterGroup {

	// ------------------------------------------------------------------------

	/**
	 * Local list of register children (the parent list includes groups too).
	 */
	private ArrayList<PeripheralRegisterVMNode> fRegisters;

	// ------------------------------------------------------------------------

	public PeripheralGroupVMNode(PeripheralTreeVMNode parent, SvdDMNode dmNode) {

		super(parent, dmNode);
	}

	// ------------------------------------------------------------------------

	@Override
	protected void addChild(PeripheralTreeVMNode child) {

		super.addChild(child);

		if (child instanceof PeripheralRegisterVMNode) {

			if (fRegisters == null)
				fRegisters = new ArrayList<PeripheralRegisterVMNode>();

			// Update list of child registers.
			fRegisters.add((PeripheralRegisterVMNode) child);
		}
	}

	// ------------------------------------------------------------------------

	public void update() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("update() unimplemented");
		}
	}

	public void update(String str) {
		;
	}

	// ------------------------------------------------------------------------

	@Override
	public String getDisplayNodeType() {
		return "Group";
	}

	@Override
	public String getImageName() {
		return "registergroup_obj";
	}

	@Override
	public String getDisplaySize() {

		BigInteger bigSize = getBigSize();
		if (bigSize != null) {
			long size = bigSize.longValue();
			if (size == 1) {
				return "1 byte";
			} else if (size > 1) {
				if (size < 0x10000) {
					return String.format("0x%04x bytes", size);
				} else {
					return String.format("0x%08x bytes", size);
				}
			}
		}
		return null;
	}

	// ------------------------------------------------------------------------
	// Contributed by IRegisterGroup

	@Override
	public IRegister[] getRegisters() throws DebugException {
		return fRegisters.toArray(new PeripheralRegisterVMNode[fRegisters.size()]);
	}

	@Override
	public boolean hasRegisters() throws DebugException {
		return (fRegisters != null) && !fRegisters.isEmpty();
	}

	// ------------------------------------------------------------------------
}
