/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
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

package org.eclipse.embedcdt.debug.gdbjtag.properties;

public class PersistentProperties {

	public static String SVD_ABSOLUTE_PATH = "svdAbsolutePath";

	public static String getSvdAbsolutePathKey(String id) {
		String[] parts = id.split("\\.");
		return SVD_ABSOLUTE_PATH + "." + parts[parts.length - 2] + "." + parts[parts.length - 1];
	}
}
