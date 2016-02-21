/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.core.tree;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;

public class Leaf implements Comparable<Leaf>, IAdaptable {

	protected String fType;
	protected Node fParent;
	protected Map<String, String> fProperties;

	public Leaf(String type) {
		fType = type;
		fParent = null;
		fProperties = null;
	}

	// Does not copy properties!
	public Leaf(Leaf node) {

		fType = node.fType;
		fProperties = null;
		fParent = null;

		String name = node.getPropertyOrNull(Property.NAME);
		if (name != null) {
			setName(name.trim());
		}

		String description = node.getPropertyOrNull(Property.DESCRIPTION);
		if (description != null) {
			setDescription(description.trim());
		}
	}

	public String getType() {
		return fType;
	}

	public boolean isType(String type) {
		return fType.equals(type);
	}

	public void setType(String type) {
		this.fType = type;
	}

	public String getName() {

		String name = getProperty(Property.NAME);
		return name;
	}

	public void setName(String name) {
		putProperty(Property.NAME, name);
	}

	public String getDescription() {

		String description = getProperty(Property.DESCRIPTION);
		return description;
	}

	public void setDescription(String description) {
		putProperty(Property.DESCRIPTION, description);
	}

	public boolean hasChildren() {
		return false;
	}

	// Return null if no more siblings
	public Leaf getNextSibling() {

		if (getParent() == null) {
			return null; // Root node has no siblings
		}
		List<Leaf> list = getParent().getChildren();

		// Find the current node in the list of children
		int ix = list.indexOf(this);

		assert ix >= 0;

		ix++;
		if (ix >= list.size()) {
			return null; // No more siblings
		}

		// Return next sibling
		return list.get(ix);
	}

	public Node getParent() {
		return fParent;
	}

	public void moveTo(Node parent) {

		assert parent != null;

		if (fParent != null) {
			fParent.removeChild(this);
			fParent = null;
		}
		parent.addChild(this);
	}

	public boolean hasProperties() {
		return (fProperties != null && !fProperties.isEmpty());
	}

	public boolean hasRelevantProperties() {

		if (!hasProperties()) {
			return false;
		}
		for (String key : fProperties.keySet()) {
			if (Property.NAME.equals(key)) {
				continue; // skip name
			}
			if (Property.DESCRIPTION.equals(key)) {
				continue; // skip description
			}
			return true;
		}

		return false;
	}

	public Map<String, String> getProperties() {
		return fProperties;
	}

	public Object putProperty(String name, String value) {

		if (fProperties == null) {
			// Linked (slightly more inefficient) to preserve order.
			// TODO: document why the order is required.
			fProperties = new LinkedHashMap<String, String>();
		}

		return fProperties.put(name, value.trim());
	}

	public Object putNonEmptyProperty(String name, String value) {

		if (value != null && value.trim().length() > 0) {
			return putProperty(name, value);
		}

		return null;
	}

	public boolean hasProperty(String name) {

		if (fProperties == null) {
			return false;
		}

		if (!fProperties.containsKey(name)) {
			return false;
		}

		return true;
	}

	// May return null!
	public String getPropertyOrNull(String name) {

		if (fProperties == null) {
			return null;
		}

		if (!fProperties.containsKey(name)) {
			return null;
		}

		return fProperties.get(name);
	}

	/**
	 * Get property.
	 * 
	 * @param name
	 *            a string with the property name.
	 * @return a string with the value of the property, or an empty string if
	 *         not found.
	 */
	public String getProperty(String name) {
		return getProperty(name, "");
	}

	public String getProperty(String name, String defaultValue) {
		String property = getPropertyOrNull(name);
		if (property != null) {
			return property;
		}
		return defaultValue;
	}

	public String getPropertyWithParent(String name, String defaultValue) {
		String property = getPropertyOrNull(name);
		if (property != null) {
			return property;
		}
		Node parent = getParent();
		if (parent != null) {
			return parent.getPropertyWithParent(name, defaultValue);
		}
		return defaultValue;
	}

	public Map<String, String> copyPropertiesRef(Leaf node) {
		fProperties = node.fProperties;
		return fProperties;
	}

	public void copyProperties(Leaf node) {

		if (node.hasProperties()) {
			for (String key : node.fProperties.keySet()) {
				if (Property.NAME.equals(key)) {
					if (getPropertyOrNull(Property.NAME) != null) {
						continue; // leave name unchanged
					}
				} else if (Property.DESCRIPTION.equals(key)) {
					if (getPropertyOrNull(Property.DESCRIPTION) != null) {
						continue; // leave description unchanged
					}
				}
				putProperty(key, node.getPropertyOrNull(key));
			}
		}
	}

	public boolean isBooleanProperty(String name) {

		// Return true if the given property is true.
		return (String.valueOf(true).equals(getProperty(name)));
	}

	public void setBooleanProperty(String name, boolean value) {

		// Set the property to true/false.
		putProperty(name, String.valueOf(value));
	}

	// Required by the sorter, don't mess with it. (???)
	public String toString() {
		String str = "[" + getType();
		if (hasProperties()) {
			str += ": " + getProperties().toString();
		}
		if (hasChildren()) {
			str += ", " + ((Node) this).getChildren().size() + " kids";
		}
		str += "]";
		return str;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(Leaf comp) {
		return getName().compareTo(comp.getName());
	}

	// ------------------------------------------------------------------------

	public static Leaf addNewChild(Node parent, String type) {

		assert (parent != null);

		Leaf node = new Leaf(type);
		parent.addChild(node);
		return node;
	}

	public static Leaf addNewChild(Node parent, Leaf from) {

		assert (parent != null);

		Leaf node = new Leaf(from);
		parent.addChild(node);
		return node;
	}

	public static Leaf addUniqueChild(Node parent, String type, String name) {

		assert (parent != null);

		Leaf node = parent.findChild(type, name);
		if (node == null) {

			node = new Leaf(type);
			parent.addChild(node);

			node.setName(name);
		}

		return node;
	}

	// ------------------------------------------------------------------------
}
