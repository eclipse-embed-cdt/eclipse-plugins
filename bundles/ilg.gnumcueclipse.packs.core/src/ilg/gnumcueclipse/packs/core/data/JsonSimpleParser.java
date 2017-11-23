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

import ilg.gnumcueclipse.packs.core.tree.Leaf;
import ilg.gnumcueclipse.packs.core.tree.Node;
import ilg.gnumcueclipse.packs.core.tree.Property;
import ilg.gnumcueclipse.packs.core.tree.Type;

/**
 * Very simple parser, to convert the tree created by JSON.Simple into the same
 * tree used by the DOM parser. (Objects in the JSON tree do not have parents).
 */
public class JsonSimpleParser {

	public JsonSimpleParser() {

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

	public Node parse(JSONObject json) {

		Node tree = new Node(Type.ROOT);
		tree.setPackType(Leaf.PACK_TYPE_XPACK);

		for (Object key : json.keySet()) {
			parseRecursive(key.toString(), json.get(key), tree);
		}
		return tree;
	}

	@SuppressWarnings("unchecked")
	private void parseRecursive(String name, Object value, Node parent) {
		if (value instanceof JSONObject) {
			boolean canBeCollection = true;
			for (Object childKey : ((JSONObject) value).keySet()) {
				Object childValue = ((JSONObject) value).get(childKey);
				if (!(childValue instanceof JSONObject)) {
					canBeCollection = false;
					break;
				}
			}
			if (canBeCollection) {
				String type = isCollection(name);
				if (type != null) {
					for (Object key : ((JSONObject) value).keySet()) {
						JSONObject child = (JSONObject) ((JSONObject) value).get(key);
						if (child.containsKey("name")) {
							System.out.println("name already present in " + child);
							child.put(Property.KEY_, key);
						} else {
							child.put("name", key);
						}
						parseRecursive(type, child, parent);
					}
				}
			} else {
				Node node = Node.addNewChild(parent, name);
				node.setPackType(Leaf.PACK_TYPE_XPACK);

				for (Object key : ((JSONObject) value).keySet()) {
					parseRecursive(key.toString(), ((JSONObject) value).get(key), node);
				}
			}
		} else if (value instanceof JSONArray) {
			for (Object arrValue : (JSONArray) value) {
				Node node = Node.addNewChild(parent, name);
				node.setPackType(Leaf.PACK_TYPE_XPACK);

				parseRecursive(name, (JSONObject) arrValue, node);
			}
		} else {
			// Scalar, add as property.
			// Assume the value can be converted to string. (true/false?)
			if ("description".equals(name)) {
				parent.setDescription(value.toString());
			} else if (value == null) {
				parent.putNonEmptyProperty(name, "null");
			} else {
				parent.putNonEmptyProperty(name, value.toString());
			}
		}
	}

}
