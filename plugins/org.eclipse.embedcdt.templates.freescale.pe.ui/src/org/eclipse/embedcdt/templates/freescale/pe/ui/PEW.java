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
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.templates.freescale.pe.ui;

import org.eclipse.embedcdt.templates.freescale.pe.Activator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class PEW extends Wizard implements INewWizard {

	@Override
	public boolean performFinish() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PEW.performFinish()");
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PEW.init()");
		}
	}

}
