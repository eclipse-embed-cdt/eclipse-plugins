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

package org.eclipse.embedcdt.debug.gdbjtag.viewmodel.peripherals;

import org.eclipse.cdt.dsf.ui.viewmodel.AbstractVMProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.DefaultVMModelProxyStrategy;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ICheckboxModelProxy;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.embedcdt.debug.gdbjtag.datamodel.PeripheralDMContext;
import org.eclipse.embedcdt.debug.gdbjtag.ui.Activator;
import org.eclipse.embedcdt.debug.gdbjtag.ui.MemoryBlockMonitor;
import org.eclipse.jface.viewers.TreePath;

@SuppressWarnings("restriction")
public class PeripheralsVMModelProxyStrategy extends DefaultVMModelProxyStrategy implements ICheckboxModelProxy {

	@SuppressWarnings("unused")
	private Object fRootElement;

	// ------------------------------------------------------------------------

	public PeripheralsVMModelProxyStrategy(AbstractVMProvider provider, Object rootElement) {
		super(provider, rootElement);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsVMModelProxyStrategy() " + this + " " + provider + " " + rootElement);
		}

		fRootElement = rootElement;
	}

	// Contributed by ICheckboxModelProxy

	/**
	 * This is called when the radio button is checked in the interface.
	 */
	@Override
	public boolean setChecked(IPresentationContext context, Object viewerInput, TreePath path, boolean checked) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsVMModelProxyStrategy.setChecked(" + checked + ")");
		}

		Object segment = path.getLastSegment();
		if ((segment instanceof PeripheralsVMNode.PeripheralsVMContext)) {

			PeripheralsVMNode.PeripheralsVMContext peripheralVMContext = (PeripheralsVMNode.PeripheralsVMContext) segment;
			PeripheralDMContext peripheralDMContext = (PeripheralDMContext) peripheralVMContext.getDMContext();
			peripheralDMContext.setChecked(checked);
			MemoryBlockMonitor.getInstance().displayPeripheralMonitor(context.getWindow(), peripheralDMContext, peripheralDMContext.isChecked());
			return checked;
		}
		return false;
	}

	// ------------------------------------------------------------------------
}
