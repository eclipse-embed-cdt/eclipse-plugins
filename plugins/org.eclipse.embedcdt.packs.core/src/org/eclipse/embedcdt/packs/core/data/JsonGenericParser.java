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

import org.eclipse.embedcdt.packs.core.tree.Leaf;
import org.eclipse.embedcdt.packs.core.tree.Node;
import org.eclipse.embedcdt.packs.core.tree.Property;
import org.eclipse.embedcdt.packs.core.tree.Type;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Very simple parser, to convert the tree created by JSON.Simple into the same
 * tree used by the DOM parser. (Objects in the JSON tree do not have parents).
 *
 * To make things simpler, there are no Leaf objects, all nodes are Node
 * objects. All nodes are tagged with PACK_TYPE_XPACK.
 */
public class JsonGenericParser {

	public JsonGenericParser() {

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
	 * @noreference This method is not intended to be referenced by clients.
	 * @apiNote This method takes a non-API type, {@link JSONObject}, the concrete
	 *          version of this type may change in a future release, however the
	 *          method itself is API. Consumers of this API should ensure their
	 *          {@link JSONObject} is loaded from the same bundle as this bundle.
	 *          See https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/453
	 *          for the current status of this issue.
	 */
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
			// An object can be considered a collection if all values are objects.
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
					Node node;
					if (type.equals(name)) {
						// If the type of the collection nodes is the same as the collection, link
						// the children directly to the parent, without the grouping node.
						node = parent;
					} else {
						// Otherwise keep an intermediate node to group all similar children.
						node = Node.addNewChild(parent, name);
						node.setPackType(Leaf.PACK_TYPE_XPACK);
					}
					for (Object key : ((JSONObject) value).keySet()) {
						JSONObject child = (JSONObject) ((JSONObject) value).get(key);
						child.put(Property.KEY_, key);
						parseRecursive(type, child, node);
					}
				} else {
					Node node = Node.addNewChild(parent, name);
					node.setPackType(Leaf.PACK_TYPE_XPACK);

					for (Object key : ((JSONObject) value).keySet()) {
						parseRecursive(key.toString(), ((JSONObject) value).get(key), node);
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
			Node node = Node.addNewChild(parent, name);
			node.setPackType(Leaf.PACK_TYPE_XPACK);
			for (Object arrValue : (JSONArray) value) {
				if (arrValue instanceof JSONObject) {
					parseRecursive(name, arrValue, node);
				} else if (arrValue instanceof String) {
					Node subNode = Node.addNewChild(node, Type.ARRAY_ELEMENT_);
					subNode.setPackType(Leaf.PACK_TYPE_XPACK);
					subNode.putProperty(Property.VALUE_, (String) arrValue);
				}
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
