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

package org.eclipse.embedcdt.debug.gdbjtag.ui.viewmodel.peripherals;

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
import org.eclipse.embedcdt.debug.gdbjtag.ui.render.peripherals.PeripheralsColumnPresentation;
import org.eclipse.embedcdt.internal.debug.gdbjtag.ui.Activator;

/**
 * Provide the column presentation and the model proxy strategy for the
 * peripherals view.
 */
@SuppressWarnings("restriction")
public class PeripheralsVMProvider extends AbstractDMVMProvider implements IColumnPresentationFactory {

	// ------------------------------------------------------------------------

	public PeripheralsVMProvider(AbstractVMAdapter adapter, IPresentationContext presentationContext,
			DsfSession session) {

		super(adapter, presentationContext, session);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PeripheralsVMProvider() " + presentationContext + " " + session);
		}

		RootDMVMNode rootDMVMNode = new RootDMVMNode(this);
		PeripheralsVMNode peripheralVMNode = new PeripheralsVMNode(this, getSession());
		addChildNodes(rootDMVMNode, new IVMNode[] { peripheralVMNode });
		setRootNode(rootDMVMNode);
	}

	// Contributed by IColumnPresentationFactory.

	@Override
	public String getColumnPresentationId(IPresentationContext context, Object element) {

		return PeripheralsColumnPresentation.ID;
	}

	// Contributed by IColumnPresentationFactory.

	// Factory for the column presentation.
	@Override
	public IColumnPresentation createColumnPresentation(IPresentationContext context, Object element) {

		return new PeripheralsColumnPresentation();
	}

	// Factory for the model proxy strategy.
	@Override
	protected IVMModelProxy createModelProxyStrategy(Object object) {

		return new PeripheralsVMModelProxyStrategy(this, object);
	}

	@Override
	public void refresh() {

		// System.out.println("PeripheralsVMProvider.refresh() ");

		super.refresh();

		// TODO: perhaps clear caches, if any
	}

	@Override
	public void update(IPropertiesUpdate[] propertiesUpdates) {

		// System.out.println("PeripheralsVMProvider.update() ");

		super.update(propertiesUpdates);
	}

	// ------------------------------------------------------------------------
}
