/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.templates.freescale.pe.ui;

import ilg.gnuarmeclipse.templates.freescale.pe.Activator;

public class WizardPageOperation implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (Activator.getInstance().isDebugging()) {
			System.out.println("WizardPageOperation.run()");
		}
	}

}
