package ilg.gnuarmeclipse.packs;


import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;

public class TreeNode implements Comparable<TreeNode>, IAdaptable {

	private String m_type;
	private String m_name;
	private String m_description;
	private TreeNode m_parent;

	private List<TreeNode> m_children;

	public TreeNode(String type) {
		m_type = type;
		m_name = "";
		m_description = "";
		m_children = null;
		m_parent = null;
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

	public boolean hasChildren(){
		return (m_children != null && !m_children.isEmpty());
	}

	public List<TreeNode> getChildren() {
		return m_children;
	}

	public TreeNode[] getChildrenArray() {
		if (m_children != null) {
			return m_children.toArray(new TreeNode[m_children.size()]);
		} else {
			return null;
		}
	}

	public void addChild(TreeNode child) {
		if (m_children == null) {
			m_children = new ArrayList<TreeNode>();
		}
		m_children.add(child);
		child.m_parent = this;
	}

	public TreeNode getChild(String name) {
		if (name == null) {
			return null;
		}
		if (m_children == null) {
			return null;
		}

		for (TreeNode node : m_children) {
			if (node.m_name.equals(name)) {
				return node;
			}
		}

		return null;
	}

	public TreeNode getParent() {
		return m_parent;
	}

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
