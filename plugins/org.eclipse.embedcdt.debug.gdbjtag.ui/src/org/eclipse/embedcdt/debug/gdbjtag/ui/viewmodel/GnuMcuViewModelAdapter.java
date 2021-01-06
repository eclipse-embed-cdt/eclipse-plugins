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
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.ui.viewmodel;

import org.eclipse.cdt.dsf.debug.ui.viewmodel.SteppingController;
import org.eclipse.cdt.dsf.gdb.internal.ui.viewmodel.GdbViewModelAdapter;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.cdt.dsf.ui.viewmodel.IVMProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.embedcdt.debug.gdbjtag.ui.viewmodel.peripherals.PeripheralsVMProvider;
import org.eclipse.embedcdt.internal.debug.gdbjtag.ui.render.peripherals.PeripheralsView;

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
