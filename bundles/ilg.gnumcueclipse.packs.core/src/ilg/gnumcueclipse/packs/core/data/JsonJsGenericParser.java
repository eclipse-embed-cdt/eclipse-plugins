/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnumcueclipse.packs.core.data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ilg.gnumcueclipse.packs.core.jstree.JsArray;
import ilg.gnumcueclipse.packs.core.jstree.JsNode;
import ilg.gnumcueclipse.packs.core.jstree.JsObject;

/**
 * Very simple parser, to convert the tree created by JSON.Simple into an equivalent
 * tree but using the enhanced objects that keep track of parents and keys.
 */
public class JsonJsGenericParser {

	public JsonJsGenericParser() {
		;
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
	 */
	public JsObject parse(JSONObject json) {

		return (JsObject)parseRecursive(json);
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
			for (Object key : ((JSONObject)json).keySet()) {
				Object value = parseRecursive(((JSONObject)json).get(key));
				if (value instanceof String) {
					node.putProperty(key.toString(), (String)value);
				} else if (value instanceof JsNode) {
					// Objects and arrays know their key name.
					node.putProperty(key.toString(), (JsNode)value);
				}
			}
			return node;
		} else if (json instanceof JSONArray) {
			JsArray node = new JsArray();
			// Arrays enumerate their elements.
			for (Object arrValue : (JSONArray) json) {
				Object value = parseRecursive(arrValue);
				if (value instanceof JsNode) {
					node.add((JsNode)value);
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
