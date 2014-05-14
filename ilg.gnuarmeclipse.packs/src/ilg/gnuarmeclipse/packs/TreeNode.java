package ilg.gnuarmeclipse.packs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;

public class TreeNode implements Comparable<TreeNode>, IAdaptable {

	public static final String VENDOR_TYPE = "vendor";
	public static final String FAMILY_TYPE = "family";
	public static final String SUBFAMILY_TYPE = "subfamily";

	public static final String URL_PROPERTY = "url";
	public static final String VENDOR_PROPERTY = "vendor";
	
	public class Condition {

		public static final String BOARD_TYPE = "board";
		public static final String DEVICEFAMILY_TYPE = "devicefamily";
		public static final String DEPRECATED_TYPE = "deprecated";

		private String m_type;
		private String m_vendor;
		//private String m_attribute;
		private String m_value;

		public Condition(String type) {
			m_type = type.trim();
			m_vendor = null;
			//m_attribute = null;
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
			if (vendor != null){
				m_vendor = vendor.trim();
			} else {
				m_vendor = null;
			}
		}

//		public boolean hasAttribute() {
//			return m_attribute != null;
//		}
//
//		public String getAttribute() {
//			return m_attribute;
//		}
//
//		public void setAttribute(String attribute) {
//			if (attribute != null) {
//				m_attribute = attribute.trim();
//			} else {
//				m_attribute = null;
//			}
//		}

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

	public boolean isInstalled(){
		return m_isInstalled;
	}
	
	public void setIsInstalled(boolean flag){
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

	public String getProperty(String name) {
		if (m_properties == null) {
			return null;
		}

		if (!m_properties.containsKey(name)) {
			return null;
		}

		return m_properties.get(name);
	}

	public boolean hasConditions() {
		return (m_conditions != null && !m_conditions.isEmpty());
	}

	public List<Condition> getConditions() {
		return m_conditions;
	}

	public void addCondition(Condition condition) {
		if (m_conditions == null) {
			m_conditions = new ArrayList<Condition>();
		}
		m_conditions.add(condition);
	}

	// ------
	@Override
	public int compareTo(TreeNode o) {
		return m_name.compareTo(o.m_name);
	}

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
