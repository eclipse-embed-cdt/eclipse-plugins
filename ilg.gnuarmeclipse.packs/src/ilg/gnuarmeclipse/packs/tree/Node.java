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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Node extends Leaf {

	// Node types (sorted)
	public class Type {

	}

	// Properties (sorted)
	public static final String AP_PROPERTY = "ap";
	public static final String APIVERSION_PROPERTY = "apiversion";
	public static final String ARCHIVE_PROPERTY = "archive";
	public static final String ARCHIVENAME_PROPERTY = "archivename";
	public static final String ARCHIVEURL_PROPERTY = "archiveurl";
	public static final String ATTR_PROPERTY = "attr";
	public static final String BUNDLE_PROPERTY = "bundle";
	public static final String CATEGORY_PROPERTY = "category";
	public static final String CLASS_PROPERTY = "class";
	public static final String CLOCK_PROPERTY = "clock";
	public static final String CONDITION_PROPERTY = "condition";
	public static final String CONNECTOR_PROPERTY = "connector";
	public static final String CORE_PROPERTY = "core";
	public static final String DATE_PROPERTY = "date";
	public static final String DEFAULT_PROPERTY = "default";
	public static final String DEFINE_PROPERTY = "define";
	public static final String DEPRECATED_PROPERTY = "deprecated";
	public static final String DEVICEINDEX_PROPERTY = "deviceindex";
	public static final String DOC_PROPERTY = "doc";
	public static final String DP_PROPERTY = "dp";
	public static final String ENDIAN_PROPERTY = "endian";
	public static final String EXCLUSIVE_PROPERTY = "exclusive";
	public static final String FAMILY_PROPERTY = "family";
	public static final String FILE_PROPERTY = "file";
	public static final String FOLDER_PROPERTY = "folder";
	public static final String FPU_PROPERTY = "fpu";
	public static final String GENERATOR_PROPERTY = "generator";
	public static final String GROUP_PROPERTY = "group";
	public static final String ID_PROPERTY = "id";
	public static final String INIT_PROPERTY = "init";
	public static final String LICENSE_PROPERTY = "license";
	public static final String LOAD_PROPERTY = "load";
	public static final String MAXINSTANCES_PROPERTY = "maxinstances";
	public static final String MPU_PROPERTY = "mpu";
	public static final String M_PROPERTY = "m";
	public static final String N_PROPERTY = "n";
	public static final String NAME_PROPERTY = "name";
	public static final String PDSCNAME_PROPERTY = "pdscname";
	public static final String PDSCURL_PROPERTY = "pdscurl";
	public static final String PNAME_PROPERTY = "pname";
	public static final String REVISION_PROPERTY = "revision";
	public static final String RTE_PROPERTY = "rte";
	public static final String SCHEMA_PROPERTY = "schema";
	public static final String SELECT_PROPERTY = "select";
	public static final String SIZE_PROPERTY = "size";
	public static final String SRC_PROPERTY = "src";
	public static final String START_PROPERTY = "start";
	public static final String STARTUP_PROPERTY = "startup";
	public static final String SUBGROUP_PROPERTY = "subgroup";
	public static final String SUBFAMILY_PROPERTY = "subfamily";
	public static final String TITLE_PROPERTY = "title";
	public static final String URL_PROPERTY = "url";
	public static final String VARIANT_PROPERTY = "variant";
	public static final String VENDORID_PROPERTY = "vendorid";
	public static final String VENDOR_PROPERTY = "vendor";
	public static final String VERSION_PROPERTY = "version";

	public static final String CONDITION_ATTRIBUTES[] = { "Dfamily",
			"DsubFamily", "Dvariant", "Dvendor", "Dname", "Dcore", "Dfpu",
			"Dmpu", "Dendian", "Cvendor", "Cbundle", "Cclass", "Cgroup",
			"Csub", "Cvariant", "Cversion", "Capiversion", "Tcompiler",
			"condition" };

	protected List<Leaf> m_children;

	protected boolean m_isInstalled;
	protected List<Selector> m_conditions;
	protected Node m_outline;

	public Node(String type) {

		super(type);
		
		m_isInstalled = false;
		m_children = null;
		m_conditions = null;
		m_outline = null;
	}

	public boolean isInstalled() {
		return m_isInstalled;
	}

	public void setIsInstalled(boolean flag) {
		m_isInstalled = flag;
	}

	public boolean hasChildren() {
		return (m_children != null && !m_children.isEmpty());
	}

	public List<Leaf> getChildren() {
		return m_children;
	}

	public Leaf[] getChildrenArray() {
		if (m_children != null) {
			return m_children.toArray(new Leaf[m_children.size()]);
		} else {
			return new Leaf[0];
		}
	}

	public Node addNewChild(String type) {

		Node node = new Node(type);
		addChild(node);

		return node;
	}

	public void addChild(Leaf node) {
		if (m_children == null) {
			m_children = new ArrayList<Leaf>();
		}
		m_children.add(node);
		node.m_parent = this;
	}

	public Leaf addUniqueChild(String type, String name) {

		if (type == null) {
			return null;
		}

		if (m_children == null) {
			// On first child create list
			m_children = new ArrayList<Leaf>();
		}

		for (Leaf node : m_children) {
			if (node.m_type.equals(type)) {
				if (name == null) {
					// Node of given type, any name, found
					return node;
				} else {
					if (node.m_name.equals(name)) {
						return node;
					}
				}
			}
		}

		// Node not found, create a new one
		Node child = new Node(type);
		if (name != null) {
			child.setName(name);
		}

		m_children.add(child);
		child.m_parent = this;

		return child;
	}

	public Leaf getChild(String type, String name) {
		if (type == null) {
			return null;
		}
		if (m_children == null) {
			return null;
		}

		for (Leaf node : m_children) {
			if (node.m_type.equals(type)) {
				if (name == null) {
					// Node of given type, any name, found
					return node;
				} else {
					if (node.m_name.equals(name)) {
						return node;
					}
				}
			}
		}

		return null;
	}

	public void removeChildren() {
		m_children = null;
	}

	public boolean hasConditions() {
		return (m_conditions != null && !m_conditions.isEmpty());
	}

	public List<Selector> getConditions() {
		return m_conditions;
	}

	public List<Selector> getConditionsByType(String type) {
		List<Selector> list = new LinkedList<Selector>();
		if (m_conditions != null) {
			for (Selector condition : m_conditions) {
				if (condition.getType().equals(type)) {
					list.add(condition);
				}
			}
		}

		return list;
	}

	public void addCondition(Selector condition) {
		if (m_conditions == null) {
			m_conditions = new ArrayList<Selector>();
		} else {

			// Check if not already in
			for (Selector c : m_conditions) {
				if (c.equals(condition)) {
					return;
				}
			}
		}
		m_conditions.add(condition);
	}

	public boolean hasOutline() {
		return (m_outline != null);
	}

	public Node getOutline() {
		return m_outline;
	}

	public void setOutline(Node node) {
		m_outline = node;
	}

	// ------

	public void copyChildren(Node node) {
		m_children = node.m_children;
	}
}
