/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *     		(based on Ningareddy Modase contribution)
 *******************************************************************************/

package ilg.gnuarmeclipse.core;

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
		return value;
	}
}
