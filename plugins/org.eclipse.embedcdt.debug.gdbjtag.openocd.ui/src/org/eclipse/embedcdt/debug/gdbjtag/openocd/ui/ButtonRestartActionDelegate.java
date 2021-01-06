/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
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
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.openocd.ui;

import org.eclipse.embedcdt.internal.debug.gdbjtag.openocd.ui.Activator;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ButtonRestartActionDelegate implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.ButtonRestartActionDelegate.run(" + action + ")");
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (Activator.getInstance().isDebugging()) {
			System.out
					.println("openocd.ButtonRestartActionDelegate.selectionChanged(" + action + "," + selection + ")");
		}
	}

	@Override
	public void dispose() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.ButtonRestartActionDelegate.dispose()");
		}
	}

	@Override
	public void init(IWorkbenchWindow window) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.ButtonRestartActionDelegate.init()");
		}
	}

}
