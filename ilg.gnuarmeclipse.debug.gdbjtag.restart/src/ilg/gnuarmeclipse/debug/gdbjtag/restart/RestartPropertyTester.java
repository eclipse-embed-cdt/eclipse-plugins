/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ningareddy Modase - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.restart;

import org.eclipse.core.expressions.PropertyTester;

public class RestartPropertyTester extends PropertyTester {

	public static final String PROPERTY_NAMESPACE = "ilg.gnuarmeclipse.debug.gdbjtag.restart";
	public static final String PROPERTY_CAN_RESTART = "canRestart";

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {

		if (PROPERTY_CAN_RESTART.equals(property)) {
			return true;
		}

		return false;
	}
}
