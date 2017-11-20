/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.properties;

public class PersistentProperties {

	public static String SVD_ABSOLUTE_PATH = "svdAbsolutePath";

	public static String getSvdAbsolutePathKey(String id) {
		String[] parts = id.split("\\.");
		return SVD_ABSOLUTE_PATH + "." + parts[parts.length - 1];
	}
}
