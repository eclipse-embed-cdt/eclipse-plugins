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

package ilg.gnuarmeclipse.packs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;

public class TreeNode implements Comparable<TreeNode>, IAdaptable {

	// Node types (sorted)
	public static final String ACCEPT_TYPE = "accept";
	public static final String API_TYPE = "api";
	public static final String BOARD_TYPE = "board";
	public static final String BOARDS_SUBTREE_TYPE = "boards";
	public static final String BOOK_TYPE = "book";
	public static final String BUNDLE_TYPE = "bundle";
	public static final String CATEGORY_TYPE = "category";
	public static final String COMPONENT_TYPE = "component";
	public static final String CONDITION_TYPE = "condition";
	public static final String DEBUGINTERFACE_TYPE = "debuginterface";
	public static final String DEBUG_TYPE = "debug";
	public static final String DEFINE_TYPE = "define";
	public static final String DENY_TYPE = "deny";
	public static final String DEVICE_TYPE = "device";
	public static final String DEVICES_SUBTREE_TYPE = "devices";
	public static final String ENVIRONMENT_TYPE = "environment";
	public static final String EXAMPLE_TYPE = "example";
	public static final String EXTERNAL_TYPE = "external";
	public static final String FAMILY_TYPE = "family";
	public static final String FEATURE_TYPE = "feature";
	public static final String FILE_TYPE = "file";
	public static final String HEADER_TYPE = "header";
	public static final String KEYWORD_TYPE = "keyword";
	public static final String KEYWORDS_SELECT_TYPE = "keywords";
	public static final String MEMORY_TYPE = "memory";
	public static final String NONE_TYPE = "none";
	public static final String OUTLINE_TYPE = "outline";
	public static final String PACKAGE_TYPE = "package";
	public static final String PACKAGES_SUBTREE_TYPE = "packages";
	public static final String PROCESSOR_TYPE = "processor";
	public static final String REPOSITORY_TYPE = "repository";
	public static final String REQUIRE_TYPE = "require";
	public static final String ROOT_TYPE = "root";
	public static final String SUBFAMILY_TYPE = "subfamily";
	public static final String TAXONOMY_TYPE = "taxonomy";
	public static final String VARIANT_TYPE = "variant";
	public static final String VENDOR_TYPE = "vendor";
	public static final String VERSION_TYPE = "version";

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

	public class PROPERTY {
		public static final String ARCHIVE_NAME = "archive.name";
		public static final String ARCHIVE_SIZE = "archive.size";
		public static final String ARCHIVE_URL = "archive.url";
		public static final String DATE = "date";
		public static final String DEST_FOLDER = "dest.folder";
		public static final String GENERATOR = "generator";
		public static final String REPO_URL = "repo.url";
		// public static final String SITE_URL = "site.url";
		public static final String TYPE = "type";
		// public static final String UTC_DATE = "utc.date";
		public static final String VENDOR_NAME = "vendor.name";
		public static final String VENDOR_ID = "vendor.id";
	};

	public static final String CONDITION_ATTRIBUTES[] = { "Dfamily",
			"DsubFamily", "Dvariant", "Dvendor", "Dname", "Dcore", "Dfpu",
			"Dmpu", "Dendian", "Cvendor", "Cbundle", "Cclass", "Cgroup",
			"Csub", "Cvariant", "Cversion", "Capiversion", "Tcompiler",
			"condition" };

	public class Selector {

		public static final String DEVICEFAMILY_TYPE = "devicefamily";
		public static final String BOARD_TYPE = "board";
		public static final String KEYWORD_TYPE = "keyword";

		public static final String DEPRECATED_TYPE = "deprecated";

		private String m_type;
		private String m_value;

		// If more are needed, better use a map
		private String m_vendor;
		private String m_vendorId;

		public Selector(String type) {
			m_type = type.trim();
			m_value = "";

			m_vendor = null;
			m_vendorId = null;
		}

		public String getType() {
			return m_type;
		}

		public String getValue() {
			return m_value;
		}

		public void setValue(String value) {
			if (value != null) {
				m_value = value.trim();
			} else {
				m_value = "";
			}
		}

		public boolean hasVendor() {
			return m_vendor != null;
		}

		public String getVendor() {
			return m_vendor;
		}

		public void setVendor(String vendor) {
			if (vendor != null) {
				m_vendor = vendor.trim();
			} else {
				m_vendor = null;
			}
		}

		public boolean hasVendorId() {
			return m_vendorId != null;
		}

		public String getVendorId() {
			return m_vendorId;
		}

		public void setVendorId(String vendorId) {
			if (vendorId != null) {
				m_vendorId = vendorId.trim();
			} else {
				m_vendorId = null;
			}
		}

		public String toString() {
			return m_type + ":" + m_value;
		}

		public boolean equals(Object obj) {

			if (!(obj instanceof Selector))
				return false;

			Selector s = (Selector) obj;

			if (!m_type.equals(s.m_type))
				return false;

			if (!m_value.equals(s.m_value))
				return false;

			if (m_vendor != null && !m_vendor.equals(m_vendor))
				return false;

			if (m_vendorId != null && !m_vendorId.equals(m_vendorId))
				return false;

			return true;
		}

	}

	private String m_type;
	private String m_name;
	private String m_description;
	private boolean m_isInstalled;
	private TreeNode m_parent;
	private Map<String, String> m_properties;
	private List<Selector> m_conditions;
	private TreeNode m_outline;

	private List<TreeNode> m_children;

	public TreeNode(String type) {
		m_type = type;
		m_name = "";
		m_description = "";
		m_isInstalled = false;
		m_children = null;
		m_parent = null;
		m_properties = null;
		m_conditions = null;
		m_outline = null;
	}

	public String getType() {
		return m_type;
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

	public boolean isInstalled() {
		return m_isInstalled;
	}

	public void setIsInstalled(boolean flag) {
		m_isInstalled = flag;
	}

	public boolean hasChildren() {
		return (m_children != null && !m_children.isEmpty());
	}

	public List<TreeNode> getChildren() {
		return m_children;
	}

	public TreeNode[] getChildrenArray() {
		if (m_children != null) {
			return m_children.toArray(new TreeNode[m_children.size()]);
		} else {
			return new TreeNode[0];
		}
	}

	public void addChild(TreeNode child) {
		if (m_children == null) {
			m_children = new ArrayList<TreeNode>();
		}
		m_children.add(child);
		child.m_parent = this;
	}

	public TreeNode addUniqueChild(String type, String name) {

		if (type == null) {
			return null;
		}

		if (m_children == null) {
			// On first child create list
			m_children = new ArrayList<TreeNode>();
		}

		for (TreeNode node : m_children) {
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
		TreeNode child = new TreeNode(type);
		if (name != null) {
			child.setName(name);
		}

		m_children.add(child);
		child.m_parent = this;

		return child;
	}

	public TreeNode getChild(String type, String name) {
		if (type == null) {
			return null;
		}
		if (m_children == null) {
			return null;
		}

		for (TreeNode node : m_children) {
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

	public TreeNode getParent() {
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

	public TreeNode getOutline() {
		return m_outline;
	}

	public void setOutline(TreeNode node) {
		m_outline = node;
	}

	// ------
	@Override
	public int compareTo(TreeNode o) {
		return m_name.compareTo(o.m_name);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	// Required by the sorter
	public String toString() {
		return getName();
	}

	public void copyChildren(TreeNode node) {
		m_children = node.m_children;
	}
}
