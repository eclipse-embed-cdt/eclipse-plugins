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

package ilg.gnumcueclipse.debug.gdbjtag.dsf;

import org.eclipse.cdt.dsf.debug.ui.viewmodel.SteppingController;
import org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.GdbViewModelAdapter;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.viewmodel.IVMProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;

import ilg.gnumcueclipse.debug.gdbjtag.render.peripherals.PeripheralsView;
import ilg.gnumcueclipse.debug.gdbjtag.viewmodel.peripherals.PeripheralsVMProvider;

@SuppressWarnings("restriction")
public class GnuMcuViewModelAdapter extends GdbViewModelAdapter {

	// ------------------------------------------------------------------------

	GnuMcuViewModelAdapter(DsfSession session, SteppingController controller) {

		// Parent will register IColumnPresentationFactory as modelAdapter
		super(session, controller);
	}

	@Override
	protected IVMProvider createViewModelProvider(IPresentationContext context) {

		// For the Peripherals view, return the view model provider.
		if (PeripheralsView.PRESENTATION_CONTEXT_ID.equals(context.getId())) {
			return new PeripheralsVMProvider(this, context, getSession());
		}

		// For all others, refer them to the parent class.
		return super.createViewModelProvider(context);
	}

	// ------------------------------------------------------------------------
}
