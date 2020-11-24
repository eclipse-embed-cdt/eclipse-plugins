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

import org.eclipse.debug.core.model.IMemoryBlockExtension;
import org.eclipse.embedcdt.debug.gdbjtag.viewmodel.peripheral.PeripheralGroupVMNode;
import org.eclipse.embedcdt.debug.gdbjtag.viewmodel.peripheral.PeripheralTreeVMNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * The content provider for the rendering tree.
 * <p>
 * There are only three levels group/register/field, handled with two objects,
 * the PeripheralRegisterGroup and the PeripheralRegister.
 */
public class PeripheralContentProvider implements ITreeContentProvider {

	// ------------------------------------------------------------------------

	private PeripheralGroupVMNode fPeripheralNode;

	// ------------------------------------------------------------------------

	public PeripheralContentProvider(PeripheralGroupVMNode node) {

		fPeripheralNode = node;
	}

	@Override
	public void dispose() {
		fPeripheralNode = null;
	}

	// ------------------------------------------------------------------------
	// Contributed by ITreeContentProvider

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		;
	}

	@Override
	public Object[] getElements(Object inputElement) {

		if (((inputElement instanceof IMemoryBlockExtension)) && (fPeripheralNode != null)) {
			// There is a single element below root, the peripheral.
			return new Object[] { fPeripheralNode };
		}
		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		if ((parentElement instanceof PeripheralTreeVMNode)) {
			return ((PeripheralTreeVMNode) parentElement).getChildren();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {

		if ((element instanceof PeripheralTreeVMNode)) {
			return ((PeripheralTreeVMNode) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {

		if ((element instanceof PeripheralTreeVMNode)) {
			return ((PeripheralTreeVMNode) element).hasChildren();
		}
		return false;
	}

	// ------------------------------------------------------------------------
}
