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
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.core.data;

import org.eclipse.embedcdt.packs.core.jstree.JsArray;
import org.eclipse.embedcdt.packs.core.jstree.JsNode;
import org.eclipse.embedcdt.packs.core.jstree.JsObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Very simple parser, to convert the tree created by JSON.Simple into an equivalent
 * tree but using the enhanced objects that keep track of parents and keys.
 */
public class JsonJsGenericParser {

	public JsonJsGenericParser() {

	}

	/**
	 * Check if the name refers to a collection.
	 *
	 * @param name
	 * @return The type for the collection members.
	 */
	public String isCollection(String name) {
		return null;
	}

	/**
	 * Parse the JSON tree produced by JSON.simple and return a JS object.
	 *
	 * @param json
	 * @return
	 * 		a JS object.
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 * @apiNote This method takes a non-API type, {@link JSONObject}, the concrete
	 *          version of this type may change in a future release, however the
	 *          method itself is API. Consumers of this API should ensure their
	 *          {@link JSONObject} is loaded from the same bundle as this bundle.
	 *          See https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/453
	 *          for the current status of this issue.
	 */
	public JsObject parse(JSONObject json) {

		return (JsObject) parseRecursive(json);
	}

	/**
	 * Recursively parse the json and build the new tree.
	 *
	 * @param json
	 * 		a JSON.simple object.
	 * @return
	 * 		an object to be stored as a property value (JsNode or String).
	 */
	private Object parseRecursive(Object json) {
		if (json instanceof JSONObject) {
			JsObject node = new JsObject();
			// Objects enumerate their properties.
			for (Object key : ((JSONObject) json).keySet()) {
				Object value = parseRecursive(((JSONObject) json).get(key));
				if (value instanceof String) {
					node.putProperty(key.toString(), (String) value);
				} else if (value instanceof JsNode) {
					// Objects and arrays know their key name.
					node.putProperty(key.toString(), (JsNode) value);
				}
			}
			return node;
		} else if (json instanceof JSONArray) {
			JsArray node = new JsArray();
			// Arrays enumerate their elements.
			for (Object arrValue : (JSONArray) json) {
				Object value = parseRecursive(arrValue);
				if (value instanceof JsNode) {
					node.add((JsNode) value);
				} else if (value instanceof String) {
					node.add((String) value);
				}
			}
			return node;
		} else {
			// All scalars are returned as strings.
			if (json == null) {
				return "null";
			} else {
				return json.toString();
			}
		}
	}
}
