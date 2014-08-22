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

import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.viewmodel.AbstractVMAdapter;
import org.eclipse.cdt.dsf.ui.viewmodel.IVMModelProxy;
import org.eclipse.cdt.dsf.ui.viewmodel.IVMNode;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.AbstractDMVMProvider;
import org.eclipse.cdt.dsf.ui.viewmodel.datamodel.RootDMVMNode;
import org.eclipse.cdt.dsf.ui.viewmodel.properties.IPropertiesUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IColumnPresentation;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IColumnPresentationFactory;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;

/**
 * Provide the column presentation and the model proxy strategy for the
 * peripherals view.
 */
@SuppressWarnings("restriction")
public class PeripheralsVMProvider extends AbstractDMVMProvider implements
		IColumnPresentationFactory {

	// ------------------------------------------------------------------------

	public PeripheralsVMProvider(AbstractVMAdapter adapter,
			IPresentationContext presentationContext, DsfSession session) {

		super(adapter, presentationContext, session);

		RootDMVMNode rootDMVMNode = new RootDMVMNode(this);
		PeripheralVMNode peripheralVMNode = new PeripheralVMNode(this,
				getSession());
		addChildNodes(rootDMVMNode, new IVMNode[] { peripheralVMNode });
		setRootNode(rootDMVMNode);
	}

	@Override
	public IColumnPresentation createColumnPresentation(
			IPresentationContext context, Object element) {

		return new PeripheralsColumnPresentation();
	}

	@Override
	protected IVMModelProxy createModelProxyStrategy(Object object) {

		return new PeripheralsVMModelProxyStrategy(this, object);
	}

	@Override
	public String getColumnPresentationId(IPresentationContext context,
			Object element) {

		return PeripheralsColumnPresentation.ID;
	}

	@Override
	public void refresh() {

		super.refresh();

		// TODO: process local refresh
	}

	@Override
	public void update(IPropertiesUpdate[] propertiesUpdates) {

		super.update(propertiesUpdates);

		// TODO: process local update
	}

	// ------------------------------------------------------------------------

}
