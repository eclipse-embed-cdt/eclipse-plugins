/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * Copyright (c) 2015-2016 Chris Reed.
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
 *     Chris Reed - pyOCD changes
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.pyocd.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.embedcdt.internal.debug.gdbjtag.pyocd.ui.Activator;

// toolbar:org.eclipse.debug.ui.main.toolbar?after=additions

public class ButtonResetHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.ButtonResetHandler.execute(" + event + ")");
		}
		return null;
	}

}
