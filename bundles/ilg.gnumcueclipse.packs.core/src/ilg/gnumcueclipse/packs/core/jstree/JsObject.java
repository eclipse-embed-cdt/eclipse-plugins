/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.packs.core.jstree;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A class to manage a JavaScript style of object, with named properties.
 * Properties can be only strings or JsNodes.
 * 
 * DO NOT add or remove elements via direct access via getProperties(), to avoid
 * dangling references to parent objects.
 * 
 * @author ilg
 */

public class JsObject extends JsNode {

	private Map<String, Object> fProperties;

	// Redundant array, to keep track of children (properties with object values)
	// and to help find the siblings.
	private ArrayList<JsNode> fChildren;

	public JsObject() {
		// Linked list, to preserve the order.
		fProperties = new LinkedHashMap<String, Object>();

		fChildren = new ArrayList<JsNode>();
	}

	/**
	 * Get all properties, strings and objects.
	 * 
	 * @return
	 */
	public Map<String, Object> getProperties() {
		return fProperties;
	}

	@Override
	public boolean hasChildren() {
		return !fChildren.isEmpty();
	}

	/**
	 * Get properties with object values.
	 * 
	 * @return a collection of JsNode nodes.
	 */
	@Override
	public Collection<JsNode> getChildren() {
		return fChildren;
	}

	@Override
	public JsNode getFirstChild() {
		assert !fChildren.isEmpty();
		return fChildren.get(0);
	}

	@Override
	protected JsNode getNextSibling(JsNode node) {
		int index = fChildren.indexOf(node);
		assert index >= 0;
		index++;

		if (index >= fChildren.size()) {
			// No more siblings.
			return null;
		}
		return fChildren.get(index);
	}

	/**
	 * Add a string property.
	 * 
	 * @param name
	 * @param value
	 *            a string; cannot be null; will be trimmed before storing.
	 * @return the previous value of the property, or null if not present, or had
	 *         non-string type.
	 */
	public String putProperty(String name, String value) {

		assert value != null;

		value = value.trim();
		Object prevValue = fProperties.put(name, (Object) value);
		// System.out.println(this);
		if (prevValue instanceof String) {
			return (String) prevValue;
		}
		return null;
	}

	/**
	 * Add a JsNode property.
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public JsNode putProperty(String name, JsNode value) {

		// Prevent linking a node to multiple parents.
		assert value.getParent() == null;
		// Link all object properties to the parent node.
		value.setParent(this);

		value.setPropertyName(name);
		fChildren.add(value);

		Object prevValue = fProperties.put(name, (Object) value);
		if (prevValue instanceof JsNode) {
			return (JsNode) prevValue;
		}
		return null;
	}

	public Object putProperty(String name, Object value) {
		if (value instanceof String) {
			return putProperty(name, (String) value);
		} else if (value instanceof JsNode) {
			return putProperty(name, (JsNode) value);
		} else {
			assert false : "Unsupported property value type " + value.getClass().getName();
		}
		return null;
	}

	/**
	 * Add the string property only if the value has content.
	 * 
	 * @param name
	 * @param value
	 * @return the previous value of the property, or null if not present, or had
	 *         non-string type.
	 */
	public String putNonEmptyProperty(String name, String value) {

		if (value != null && value.trim().length() > 0) {
			return putProperty(name, value);
		}

		return null;
	}

	/**
	 * Check if a property exists.
	 * 
	 * @param name
	 * @return true if the property exists.
	 */
	public boolean hasProperty(String name) {

		if (!fProperties.containsKey(name)) {
			return false;
		}

		return true;
	}

	/**
	 * Get a generic property.
	 * 
	 * @param name
	 *            a string with the property name.
	 * @return an object (string or JsNode) or null if the property is not found.
	 */
	public Object getProperty(String name) {
		return fProperties.get(name);
	}

	/**
	 * Retrieve a string property, or the default.
	 * 
	 * @param name
	 * @param defaultValue
	 * @return the non-empty property value or the default value.
	 */
	public String getProperty(String name, String defaultValue) {
		Object property = fProperties.get(name);
		if ((property != null) && (property instanceof String)) {
			return (String) property;
		}
		return defaultValue;
	}

	/**
	 * Remove a property, if present.
	 * 
	 * @param name
	 * @return the value of the property, or null if the property was not defined.
	 */
	public Object removeProperty(String name) {

		Object value = fProperties.remove(name);
		if ((value != null) && (value instanceof JsNode)) {
			// Unlink from parent.
			JsNode node = (JsNode) value;
			node.setParent(null);

			fChildren.remove(node);
		}
		return value;
	}

	/**
	 * Convert the node to a string; only string properties are shown.
	 * 
	 * @return a string similar to a JSON.
	 */
	public String toString() {
		String str = "{ ";
		boolean first = true;
		String pName = getPropertyName();
		if (pName != null) {
			str += "\"$this\":\"" + pName + "\"";
			first = false;
		}
		for (String key : fProperties.keySet()) {
			if (first) {
				first = false;
			} else {
				str += ", ";
			}
			str += "\"" + key + "\":";
			Object value = fProperties.get(key);
			if (value instanceof String) {
				str += "\"" + value + "\"";
			} else {
				str += "<" + value.getClass().getSimpleName() + ">";
			}
		}
		str += " }";
		return str;
	}

	@Override
	public void serialize(OutputStream o) throws IOException {
		String str = "{";
		boolean first = true;
		for (String key : fProperties.keySet()) {
			if (first) {
				first = false;
			} else {
				str += ",";
			}
			str += "\"" + key + "\":";
			Object value = fProperties.get(key);
			if (value instanceof String) {
				str += "\"" + value + "\"";
				o.write(str.getBytes());
				str = "";
			} else if (value instanceof JsNode) {
				o.write(str.getBytes());
				str = "";
				((JsNode) value).serialize(o);
			}
		}
		str += "}";
		o.write(str.getBytes());
	}
}
