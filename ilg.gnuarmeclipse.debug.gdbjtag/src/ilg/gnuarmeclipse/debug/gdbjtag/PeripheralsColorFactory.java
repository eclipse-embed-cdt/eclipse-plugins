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

package ilg.gnuarmeclipse.debug.gdbjtag;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.themes.IColorFactory;

public class PeripheralsColorFactory implements IColorFactory {

	// ------------------------------------------------------------------------

	private String fThemeColorName;
	private String fPreferenceName;

	// ------------------------------------------------------------------------

	protected PeripheralsColorFactory(String themeColorName, String preferenceName) {

		fThemeColorName = themeColorName;
		fPreferenceName = preferenceName;
	}

	// ------------------------------------------------------------------------

	@Override
	public RGB createColor() {

		String value;
		value = InstanceScope.INSTANCE.getNode("org.eclipse.ui.workbench").get(fThemeColorName, null);
		if (value == null) {
			value = DefaultScope.INSTANCE.getNode(Activator.PLUGIN_ID).get(fPreferenceName, "0,0,0");
		}
		String a[] = value.split(",");
		RGB rgb;
		try {
			rgb = new RGB(Integer.parseInt(a[0]), Integer.parseInt(a[1]), Integer.parseInt(a[2]));
		} catch (Exception e) {
			rgb = new RGB(0, 0, 0);
		}
		return rgb;
	}
	// ------------------------------------------------------------------------
}
