package ilg.gnuarmeclipse.packs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;

public class TreeNode implements Comparable<TreeNode>, IAdaptable {

	public static final String NONE_TYPE = "none";
	public static final String ROOT_TYPE = "root";
	public static final String OUTLINE_TYPE = "outline";

	public static final String FOLDER_TYPE = "folder";
	public static final String BOARDS_TYPE = "boards";
	public static final String DEVICES_TYPE = "devices";
	public static final String EXAMPLES_TYPE = "examples";
	// public static final String COMPONENTS_TYPE = "components";

	// public static final String CORE_TYPE = "core";
	public static final String BOOK_TYPE = "book";
	public static final String FEATURE_TYPE = "feature";
	// public static final String CLOCK_TYPE = "clock";
	public static final String HEADER_TYPE = "header";
	public static final String DEFINE_TYPE = "define";
	public static final String DEBUG_TYPE = "debug";
	public static final String MEMORY_TYPE = "memory";
	public static final String FILE_TYPE = "file";
	public static final String COMPONENT_TYPE = "component";
	public static final String BUNDLE_TYPE = "bundle";
	public static final String DEBUGINTERFACE_TYPE = "debuginterface";
	public static final String CATEGORY_TYPE = "category";
	public static final String KEYWORD_TYPE = "keyword";
	public static final String TAXONOMY_TYPE = "taxonomy";
	public static final String CONDITION_TYPE = "condition";
	public static final String REQUIRE_TYPE = "require";
	public static final String ACCEPT_TYPE = "accept";
	public static final String DENY_TYPE = "deny";
	public static final String API_TYPE = "api";
	public static final String PROCESSOR_TYPE = "processor";
	public static final String VARIANT_TYPE = "variant";

	public static final String VENDOR_TYPE = "vendor";
	public static final String FAMILY_TYPE = "family";
	public static final String SUBFAMILY_TYPE = "subfamily";

	public static final String PACKAGE_TYPE = "package";
	public static final String VERSION_TYPE = "version";

	public static final String DEVICE_TYPE = "device";
	public static final String BOARD_TYPE = "board";
	public static final String EXAMPLE_TYPE = "example";

	// Properties
	public static final String URL_PROPERTY = "url";
	public static final String VENDOR_PROPERTY = "vendor";
	public static final String VENDORID_PROPERTY = "vendorid";
	public static final String VERSION_PROPERTY = "version";
	public static final String DATE_PROPERTY = "date";
	public static final String FPU_PROPERTY = "fpu";
	public static final String MPU_PROPERTY = "mpu";
	public static final String ENDIAN_PROPERTY = "endian";
	public static final String FILE_PROPERTY = "file";
	public static final String N_PROPERTY = "n";
	public static final String M_PROPERTY = "m";
	public static final String REVISION_PROPERTY = "revision";
	public static final String CATEGORY_PROPERTY = "category";
	public static final String ATTR_PROPERTY = "attr";
	public static final String CONDITION_PROPERTY = "condition";
	public static final String APIVERSION_PROPERTY = "apiversion";
	public static final String MAXINSTANCES_PROPERTY = "maxinstances";
	public static final String SRC_PROPERTY = "src";
	public static final String DEPRECATED_PROPERTY = "deprecated";
	public static final String RTE_PROPERTY = "rte";
	public static final String FOLDER_PROPERTY = "folder";
	public static final String ARCHIVE_PROPERTY = "folder";
	public static final String CONNECTOR_PROPERTY = "connector";
	public static final String SELECT_PROPERTY = "select";
	public static final String ID_PROPERTY = "id";
	public static final String START_PROPERTY = "start";
	public static final String SIZE_PROPERTY = "size";
	public static final String EXCLUSIVE_PROPERTY = "exclusive";

	public class Condition {

		public static final String BOARD_TYPE = "board";
		public static final String DEVICEFAMILY_TYPE = "devicefamily";
		public static final String DEPRECATED_TYPE = "deprecated";

		private String m_type;
		private String m_vendor;
		// private String m_attribute;
		private String m_value;

		public Condition(String type) {
			m_type = type.trim();
			m_vendor = null;
			// m_attribute = null;
			m_value = "";
		}

		public String getType() {
			return m_type;
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

		// public boolean hasAttribute() {
		// return m_attribute != null;
		// }
		//
		// public String getAttribute() {
		// return m_attribute;
		// }
		//
		// public void setAttribute(String attribute) {
		// if (attribute != null) {
		// m_attribute = attribute.trim();
		// } else {
		// m_attribute = null;
		// }
		// }

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

		public String toString() {
			return m_type + "." + m_vendor + "." + m_value;
		}
	}

	private String m_type;
	private String m_name;
	private String m_description;
	private boolean m_isInstalled;
	private TreeNode m_parent;
	private Map<String, String> m_properties;
	private List<Condition> m_conditions;
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
			m_properties = new HashMap<String, String>();
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

	public List<Condition> getConditions() {
		return m_conditions;
	}

	public List<Condition> getConditionsByType(String type) {
		List<Condition> list = new LinkedList<Condition>();
		if (m_conditions != null) {
			for (Condition condition : m_conditions) {
				if (condition.getType().equals(type)) {
					list.add(condition);
				}
			}
		}

		return list;
	}

	public void addCondition(Condition condition) {
		if (m_conditions == null) {
			m_conditions = new ArrayList<Condition>();
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

}
