/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
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
 *     		(based on Ningareddy Modase contribution)
 *******************************************************************************/

package org.eclipse.embedcdt.core;

import org.eclipse.embedcdt.internal.core.Activator;

public class AltWindowsRegistry {

	public static String query(String location, String key) {

		String query = "reg query " + '"' + location + "\" /v " + key;

		String value = null;
		try {
			Process process = Runtime.getRuntime().exec(query);
			AltWindowsRegistryStreamReader reader = new AltWindowsRegistryStreamReader(process.getInputStream(), key);
			reader.start();
			process.waitFor();
			reader.join();

			String[] str = reader.getResult().trim().split("REG_[^\\s]+");
			if (str.length > 1) {
				value = str[str.length - 1].trim();
			}
		} catch (Exception e) {
			;
		}
		if (Activator.getInstance().isDebugging()) {
			System.out.println("AltWindowsRegistry.query(\"" + location + "\", \"" + key + "\") = \"" + value + "\"");
		}
		return value;
	}
}
