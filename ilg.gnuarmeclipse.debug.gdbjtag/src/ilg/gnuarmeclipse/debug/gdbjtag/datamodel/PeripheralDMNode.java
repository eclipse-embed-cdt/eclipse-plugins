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

package ilg.gnuarmeclipse.debug.gdbjtag.datamodel;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.model.IMemoryBlockExtension;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;

/**
 * Peripheral data model definition. It is based on the PeripheralDetails class
 * that maps over the SVD tree node.
 */
public class PeripheralDMNode extends SvdPeripheralDMNode implements IAdaptable {

	// ------------------------------------------------------------------------

	private IMemoryBlockExtension fMemoryBlock;
	private boolean fIsChecked;

	// ------------------------------------------------------------------------

	public PeripheralDMNode(Leaf node) {

		super(node);

		fMemoryBlock = null;
		fIsChecked = true;
	}

	public void dispose() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("dispose() " + this);
		}
		if (fMemoryBlock != null) {
			fMemoryBlock = null;
		}

		// Peripheral nodes are special, and cannot be disposed, so DO NOT call
		// super.dispose() here, dispose only their children.
		disposeChildren();
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class clazz) {
		return null;
	}

	// ------------------------------------------------------------------------

	public void setMemoryBlock(IMemoryBlockExtension memoryBlockExtension) {
		fMemoryBlock = memoryBlockExtension;
	}

	public IMemoryBlockExtension getMemoryBlock() {
		return fMemoryBlock;
	}

	public boolean isShown() {
		return fMemoryBlock != null;
	}

	public boolean isChecked() {
		return fIsChecked;
	}

	public void setChecked(boolean flag) {
		fIsChecked = flag;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {

		return "[" + getClass().getSimpleName() + ": " + fMemoryBlock + ", " + super.toString() + "]";
	}

	// ------------------------------------------------------------------------
}
