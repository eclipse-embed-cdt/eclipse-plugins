/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
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

package org.eclipse.embedcdt.debug.gdbjtag.preferences;

import org.eclipse.embedcdt.debug.gdbjtag.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.debug.gdbjtag.render.peripheral.PeripheralColumnLabelProvider;
import org.eclipse.embedcdt.debug.gdbjtag.ui.PeripheralsColorFactory;

public class ColorChangedFactory extends PeripheralsColorFactory {

	public ColorChangedFactory() {
		super(PeripheralColumnLabelProvider.COLOR_CHANGED, PersistentPreferences.PERIPHERALS_COLOR_CHANGED);
	}
}
