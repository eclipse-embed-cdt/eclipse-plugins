/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ningareddy Modase - initial implementation.
 *     Liviu Ionescu - ARM version.
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.restart.action;

public class RestartCommandActionDelegate extends DebugCommandActionDelegate {

	// ------------------------------------------------------------------------

	public RestartCommandActionDelegate() {
		super(new RestartCommandAction());
	}

	// ------------------------------------------------------------------------
}