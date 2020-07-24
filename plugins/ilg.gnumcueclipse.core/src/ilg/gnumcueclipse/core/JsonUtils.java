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

package ilg.gnumcueclipse.core;

import org.json.simple.JSONObject;

public class JsonUtils {

	/**
	 * Get a JSON property, or the default.
	 * 
	 * @param json
	 * @param dottedPath
	 * @param defaultValue
	 * @return an object or the default value.
	 */
	public static Object get(JSONObject json, String dottedPath, Object defaultValue) {

		String[] parts = dottedPath.split("[.]");
		Object obj = json;
		for (int i = 0; i < parts.length; ++i) {
			if (obj instanceof JSONObject) {
				obj = ((JSONObject) obj).get(parts[i]);
			}
			if (obj == null) {
				return defaultValue;
			}
		}

		return obj;
	}

	public static Object get(JSONObject json, String dottedPath) {
		return get(json, dottedPath, null);
	}
}
