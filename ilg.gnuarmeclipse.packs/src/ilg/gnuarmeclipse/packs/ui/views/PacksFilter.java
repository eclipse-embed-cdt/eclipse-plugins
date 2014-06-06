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

package ilg.gnuarmeclipse.packs.ui.views;

import ilg.gnuarmeclipse.packs.tree.Leaf;
import ilg.gnuarmeclipse.packs.tree.Property;
import ilg.gnuarmeclipse.packs.tree.Selector;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.Type;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class PacksFilter extends ViewerFilter {

	private IStructuredSelection m_selection;
	private String m_conditionType;

	public void setSelection(String conditionType,
			IStructuredSelection selection) {
		m_selection = selection;
		m_conditionType = conditionType;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		// 'element' is the node to be tested by the filter

		if (m_selection == null || m_selection.isEmpty())
			return true; // Nothing selected, all nodes visible

		// System.out.println(parentElement + " " + element);
		Leaf node = (Leaf) element;
		// String nodeType = node.getType();

		// For folder nodes (vendor), if there is no child visible,
		// make the entire parent invisible.
		if (Type.VENDOR.equals(node.getType())) {
			StructuredViewer sviewer = (StructuredViewer) viewer;
			ITreeContentProvider provider = (ITreeContentProvider) sviewer
					.getContentProvider();
			for (Object child : provider.getChildren(element)) {
				if (select(viewer, element, child))
					return true;
			}
			return false;
		}

		if (!Type.PACKAGE.equals(node.getType())) {
			return true;
		}

		if (!(node instanceof Node)) {
			return false;
		}

		// If the node has no restricting conditions at all,
		// then it is not visible
		if (!((Node) node).hasConditions()) {
			return false;
		}

		List<Selector> conditions = ((Node) node).getConditions();
		List<Selector> filteredConditions = new LinkedList<Selector>();
		for (Selector condition : conditions) {
			if (m_conditionType.equals(condition.getType())) {
				filteredConditions.add(condition);
			}
		}

		// If the node has no restricting conditions of the given type,
		// then it is always visible
		if (filteredConditions.size() == 0) {
			return false; // true;
		}

		// If the node has conditions, enumerate them and check one by one.
		// If at least one is true, the node is visible
		for (Selector condition : filteredConditions) {

			// Enumerate all selections
			for (Object obj : m_selection.toList()) {
				if (obj instanceof Leaf) {
					Leaf selectionNode = (Leaf) obj;

					if (isNodeVisible(condition, selectionNode)) {
						// System.out.println("accepted, match " +
						// condition);
						return true;
					}
				}
			}
		}

		// No condition fulfilled, reject
		return false;
	}

	private boolean isNodeVisible(Selector condition, Leaf selectionNode) {

		// Condition is from the evaluated node
		String conditionType = condition.getType();

		String selectionNodeType = selectionNode.getType();
		if (Selector.BOARD_TYPE.equals(conditionType)) {

			// Check board conditions (generic vendor string and
			// board name)
			if (Type.VENDOR.equals(selectionNodeType)) {

				// compare the selected node name with the condition
				// vendor
				if (condition.getVendor().equals(selectionNode.getName())) {
					return true;
				}
			} else if (Type.BOARD.equals(selectionNodeType)) {

				if (condition.getVendor().equals(
						selectionNode.getProperty(Property.VENDOR_NAME, ""))
						&& condition.getValue().equals(selectionNode.getName())) {
					return true;
				}
			}

		} else if (Selector.DEVICEFAMILY_TYPE.equals(conditionType)) {

			// Check device conditions (numeric vendor id and
			// family name)
			if (Type.VENDOR.equals(selectionNodeType)) {

				// compare the condition vendor id with the selection vendor id
				if (condition.getVendorId().equals(
						selectionNode.getProperty(Property.VENDOR_ID, ""))) {
					return true;
				}
			} else if (Type.FAMILY.equals(selectionNodeType)) {

				// compare the condition vendor id with the selection vendor id
				// and the condition name with selection family name
				if (condition.getVendorId().equals(
						selectionNode.getProperty(Property.VENDOR_ID, ""))
						&& condition.getValue().equals(selectionNode.getName())) {
					return true;
				}
			}
		} else if (Selector.KEYWORD_TYPE.equals(conditionType)) {

			// Check keyword name
			if (Type.KEYWORD.equals(selectionNode.getType())) {
				if (condition.getValue().equals(selectionNode.getName())) {
					return true;
				}
			}
		}
		return false;
	}
}
