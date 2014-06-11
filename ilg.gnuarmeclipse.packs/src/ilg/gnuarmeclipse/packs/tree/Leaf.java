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

package ilg.gnuarmeclipse.packs.tree;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;

public class Leaf implements Comparable<Leaf>, IAdaptable {

	protected String m_type;
	protected String m_name;
	protected String m_description;
	protected Node m_parent;
	protected Map<String, String> m_properties;

	public Leaf(String type) {
		m_type = type;
		m_name = "";
		m_description = "";
		m_parent = null;
		m_properties = null;
	}

	// Does not copy properties!
	public Leaf(Leaf node) {
		m_type = node.m_type;
		m_name = node.m_name;
		m_description = node.m_description;
		m_parent = null;
		m_properties = null;
	}

	public String getType() {
		return m_type;
	}

	public boolean isType(String type) {
		return m_type.equals(type);
	}

	public void setType(String type) {
		this.m_type = type;
	}

	public String getName() {
		return m_name;
	}

	public void setName(String name) {
		this.m_name = name;
	}

	public String getDescription() {
		return m_description;
	}

	public void setDescription(String description) {
		this.m_description = description;
	}

	public boolean hasChildren() {
		return false;
	}

	public List<Leaf> getChildren() {
		return null;
	}

	public Leaf[] getChildrenArray() {
		return new Leaf[0];
	}

	public Node getParent() {
		return m_parent;
	}

	public boolean hasProperties() {
		return (m_properties != null && !m_properties.isEmpty());
	}

	public Map<String, String> getProperties() {
		return m_properties;
	}

	public Object putProperty(String name, String value) {

		if (m_properties == null) {
			// Linked to preserve order
			m_properties = new LinkedHashMap<String, String>();
		}

		return m_properties.put(name, value);
	}

	public Object putNonEmptyProperty(String name, String value) {

		if (value != null && value.length() > 0) {
			return putProperty(name, value);
		}

		return null;
	}

	public String getProperty(String name) {

		if (m_properties == null) {
			return null;
		}

		if (!m_properties.containsKey(name)) {
			return null;
		}

		return m_properties.get(name);
	}

	public String getProperty(String name, String defaultValue) {
		String property = getProperty(name);
		if (property == null) {
			return defaultValue;
		} else {
			return property;
		}
	}

	public Map<String, String> copyPropertiesRef(Leaf node) {
		m_properties = node.m_properties;
		return m_properties;
	}

	public boolean isBooleanProperty(String name) {

		// Return true if the given propery is true.
		return (String.valueOf(true).equals(getProperty(name, "")));
	}

	public void setBooleanProperty(String name, boolean value) {

		// Set the property to true/false.
		putProperty(name, String.valueOf(value));
	}

	public boolean isInstalled() {
		return false;
	}

	// Required by the sorter
	public String toString() {
		return getName();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(Leaf o) {
		return m_name.compareTo(o.m_name);
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

	// ------------------------------------------------------------------------
}
