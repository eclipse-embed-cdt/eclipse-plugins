/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.preferences;

import ilg.gnumcueclipse.debug.gdbjtag.PeripheralsColorFactory;
import ilg.gnumcueclipse.debug.gdbjtag.PersistentPreferences;
import ilg.gnumcueclipse.debug.gdbjtag.render.peripheral.PeripheralColumnLabelProvider;

public class ColorChangedFactory extends PeripheralsColorFactory {

	public ColorChangedFactory() {
		super(PeripheralColumnLabelProvider.COLOR_CHANGED, PersistentPreferences.PERIPHERALS_COLOR_CHANGED);
	}
}
