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

	public void setSelection(IStructuredSelection selection) {
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

		// For folder nodes (vendor), if there is no child visible,
		// make the entire parent invisible.
		if (TreeNode.VENDOR_TYPE.equals(node.getType())) {
			StructuredViewer sviewer = (StructuredViewer) viewer;
			ITreeContentProvider provider = (ITreeContentProvider) sviewer
					.getContentProvider();
			for (Object child : provider.getChildren(element)) {
				if (select(viewer, element, child))
					return true;
			}
			return false;
		}

		// If the node has no restricting conditions, then it is always visible
		if (!node.hasConditions()) {
			return true;
		}

		// If the node has conditions, enumerate them and check one by one.
		// If at least one is true, the node is visible
		List<TreeNode.Selector> conditions = node.getConditions();
		for (TreeNode.Selector condition : conditions) {

			// Enumerate all selections
			for (Object obj : m_selection.toList()) {
				if (obj instanceof TreeNode) {
					TreeNode selectionNode = (TreeNode) obj;

					if (isNodeVisible(condition, selectionNode)) {
						// System.out.println("accepted, match " + condition);
						return true;
					}
				}
			}
		}

		// No condition fulfilled, reject
		return false;
	}

	private boolean isNodeVisible(TreeNode.Selector condition,
			TreeNode selectionNode) {

		String conditionType = condition.getType();
		// Condition is from the node
		if (TreeNode.Selector.BOARD_TYPE.equals(conditionType)) {

			// Check board conditions (generic vendor string and
			// board name)
			if (TreeNode.VENDOR_TYPE.equals(selectionNode.getType())) {
				// compare the selected node name with the condition
				// vendor
				if (condition.getVendor().equals(selectionNode.getName())) {
					return true;
				}
			} else if (TreeNode.Selector.BOARD_TYPE.equals(selectionNode
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

		} else if (TreeNode.Selector.DEVICEFAMILY_TYPE.equals(conditionType)) {

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
		} else if (TreeNode.Selector.KEYWORD_TYPE.equals(conditionType)) {
			if (TreeNode.KEYWORD_TYPE.equals(selectionNode.getType())) {
				if (condition.getValue().equals(selectionNode.getName())) {
					return true;
				}
			}
		}
		return false;
	}
}
