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

import org.eclipse.embedcdt.internal.templates.freescale.pe.ui.Activator;

public class WizardPageOperation implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (Activator.getInstance().isDebugging()) {
			System.out.println("WizardPageOperation.run()");
		}
	}

}
