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

package ilg.gnumcueclipse.debug.gdbjtag.preferences;

import ilg.gnumcueclipse.debug.gdbjtag.PeripheralsColorFactory;
import ilg.gnumcueclipse.debug.gdbjtag.preferences.PersistentPreferences;
import ilg.gnumcueclipse.debug.gdbjtag.render.peripheral.PeripheralColumnLabelProvider;

public class ColorChangedFactory extends PeripheralsColorFactory {

	public ColorChangedFactory() {
		super(PeripheralColumnLabelProvider.COLOR_CHANGED, PersistentPreferences.PERIPHERALS_COLOR_CHANGED);
	}
}
