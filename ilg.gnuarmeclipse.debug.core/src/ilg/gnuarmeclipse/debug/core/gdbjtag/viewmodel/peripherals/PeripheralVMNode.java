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

package ilg.gnuarmeclipse.debug.core.gdbjtag.viewmodel.peripherals;

import ilg.gnuarmeclipse.debug.core.gdbjtag.dsf.IPeripheral;

import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.datamodel.IDMContext;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.viewmodel.VMDelta;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMNode;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IElementPropertiesProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IPropertiesUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementEditor;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementLabelProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.ILabelUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
public class PeripheralVMNode extends AbstractDMVMNode implements
		IElementLabelProvider, IElementPropertiesProvider, IElementEditor {

	// ------------------------------------------------------------------------

	/**
	 * Peripheral view model context.
	 */
	public class PeripheralVMContext extends AbstractDMVMNode.DMVMContext {
		protected PeripheralVMContext(IDMContext context) {
			super(context);
		}
	}

	// ------------------------------------------------------------------------

	public PeripheralVMNode(AbstractDMVMProvider provider, DsfSession session,
			Class<? extends IDMContext> dmcClassType) {
		super(provider, session, dmcClassType);
		// TODO Auto-generated constructor stub
	}

	public PeripheralVMNode(AbstractDMVMProvider provider, DsfSession session) {
		super(provider, session, IPeripheral.IPeripheralDMContext.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getDeltaFlags(Object event) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void buildDelta(Object event, VMDelta parent, int nodeOffset,
			RequestMonitor requestMonitor) {
		// TODO Auto-generated method stub
	}

	@Override
	public CellEditor getCellEditor(IPresentationContext context,
			String columnId, Object element, Composite parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICellModifier getCellModifier(IPresentationContext context,
			Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(IPropertiesUpdate[] updates) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update(ILabelUpdate[] updates) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void updateElementsInSessionThread(IChildrenUpdate update) {
		// TODO Auto-generated method stub

	}

	// ------------------------------------------------------------------------
}
