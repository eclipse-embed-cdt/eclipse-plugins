package ilg.gnuarmeclipse.packs.ui.views;

import ilg.gnuarmeclipse.packs.TreeNode;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class PacksFilter extends ViewerFilter {

	private IStructuredSelection m_selection;

	// private String m_type;

	public void setSelection(String type, IStructuredSelection selection) {
		// m_type = type;
		m_selection = selection;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		// 'element' is the node to be tested by the filter

		if (m_selection == null || m_selection.isEmpty())
			return true; // Nothing selected, all nodes visible

		// System.out.println(parentElement + " " + element);
		TreeNode node = (TreeNode) element;
		// String nodeType = node.getType();

		if (!"package".equals(node.getType())) {
			// Non leaf nodes
			StructuredViewer sviewer = (StructuredViewer) viewer;
			ITreeContentProvider provider = (ITreeContentProvider) sviewer
					.getContentProvider();
			for (Object child : provider.getChildren(element)) {
				if (select(viewer, element, child))
					return true;
			}
			return false;
		}

		if (!node.hasConditions()) {
			//System.out.println("accepted, no conditions");
			return true; // Node always visible
		}

		List<TreeNode.Condition> conditions = node.getConditions();
		for (TreeNode.Condition condition : conditions) {

			for (Object obj : m_selection.toList()) {
				if (obj instanceof TreeNode) {
					TreeNode selectionNode = (TreeNode) obj;

					if (isNodeVisible(condition, selectionNode)) {
						//System.out.println("accepted, match " + condition);
						return true;
					}
				}
			}
		}

		// System.out.println("rejected");
		return false;
	}

	private boolean isNodeVisible(TreeNode.Condition condition,
			TreeNode selectionNode) {

		if (TreeNode.Condition.BOARD_TYPE.equals(condition.getType())) {

			// Check board conditions (generic vendor string and
			// board name)
			if (TreeNode.VENDOR_TYPE.equals(selectionNode.getType())) {
				// compare the selected node name with the condition
				// vendor
				if (condition.getVendor().equals(selectionNode.getName())) {
					return true;
				}
			} else if (TreeNode.Condition.BOARD_TYPE.equals(selectionNode
					.getType())) {
				// compare both the parent name with the vendor and
				// the board
				TreeNode selectionVendorNode = selectionNode.getParent();
				while (selectionVendorNode != null
						&& !TreeNode.VENDOR_TYPE.equals(selectionVendorNode
								.getType())) {
					selectionVendorNode = selectionVendorNode.getParent();
				}

				if (selectionVendorNode == null) {
					return false;
				}
				if (condition.getVendor().equals(selectionVendorNode.getName())
						&& condition.getValue().equals(selectionNode.getName())) {
					return true;
				}
			}

		} else if (TreeNode.Condition.DEVICEFAMILY_TYPE.equals(condition
				.getType())) {

			String vendorId;
			TreeNode selectionVendorNode = selectionNode;
			while (selectionVendorNode != null
					&& !TreeNode.VENDOR_TYPE.equals(selectionVendorNode
							.getType())) {
				selectionVendorNode = selectionVendorNode.getParent();
			}

			if (selectionVendorNode == null) {
				return false;
			}

			vendorId = selectionVendorNode.getProperty("vendorid");
			if (vendorId == null) {
				return false;
			}

			// Check board conditions (generic vendor string and
			// board name)
			if (TreeNode.VENDOR_TYPE.equals(selectionNode.getType())) {

				// compare the condition vendor with the selection vendorid
				if (condition.getVendor().equals(vendorId)) {
					return true;
				}
			} else {
				TreeNode selectionFamilyNode = selectionNode;
				while (!TreeNode.FAMILY_TYPE.equals(selectionFamilyNode
						.getType())) {
					selectionFamilyNode = selectionFamilyNode.getParent();
				}
				// compare the condition vendor with the selection vendorid and
				// the condition name with selection family name
				if (condition.getVendor().equals(vendorId)
						&& condition.getValue().equals(
								selectionFamilyNode.getName())) {
					return true;
				}
			}
		}
		return false;
	}
}
