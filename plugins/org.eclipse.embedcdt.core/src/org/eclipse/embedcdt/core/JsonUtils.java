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

package org.eclipse.embedcdt.core;

import org.json.simple.JSONObject;

public class JsonUtils {

	/**
	 * Get a JSON property, or the default.
	 *
	 * @param json
	 * @param dottedPath
	 * @param defaultValue
	 * @return an object or the default value.
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 * @apiNote This method takes a non-API type, {@link JSONObject}, the concrete
	 *          version of this type may change in a future release, however the
	 *          method itself is API. Consumers of this API should ensure their
	 *          {@link JSONObject} is loaded from the same bundle as this bundle.
	 *          See https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/453
	 *          for the current status of this issue.
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

	/**
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 * @apiNote This method takes a non-API type, {@link JSONObject}, the concrete
	 *          version of this type may change in a future release, however the
	 *          method itself is API. Consumers of this API should ensure their
	 *          {@link JSONObject} is loaded from the same bundle as this bundle.
	 *          See https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/453
	 *          for the current status of this issue.
	 */
	public static Object get(JSONObject json, String dottedPath) {
		return get(json, dottedPath, null);
	}
}
