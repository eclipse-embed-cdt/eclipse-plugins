/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.datamodel;

import java.util.LinkedList;
import java.util.List;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.packs.core.tree.AbstractTreePreOrderIterator;
import ilg.gnuarmeclipse.packs.core.tree.ITreeIterator;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;

public class SvdEnumerationDMNode extends SvdObjectDMNode {

	// ------------------------------------------------------------------------

	private String fUsage;
	private SvdEnumeratedValueDMNode fDefaultEnumerationNode;

	// ------------------------------------------------------------------------

	public SvdEnumerationDMNode(Leaf node) {
		super(node);

		fUsage = null;
		fDefaultEnumerationNode = null;

		// If not derived from another enumeration, trigger processing for
		// <enumeratedValue> and setting for a default value.
		getChildren();
	}

	@Override
	public void dispose() {

		fUsage = null;
		if (fDefaultEnumerationNode != null) {
			fDefaultEnumerationNode.dispose();
			fDefaultEnumerationNode = null;
		}

		super.dispose();
	}

	// ------------------------------------------------------------------------

	@Override
	protected SvdObjectDMNode[] prepareChildren(Leaf node) {

		if (node == null) {
			return null;
		}

		Leaf startNode = getDerivedFromNode();
		if (startNode == null) {
			startNode = node;
		}

		if (!startNode.hasChildren()) {
			return null;
		}

		List<SvdObjectDMNode> list = new LinkedList<SvdObjectDMNode>();

		for (Leaf child : ((Node) startNode).getChildren()) {

			// Consider only <enumeratedValue> nodes
			if (child.isType("enumeratedValue")) {

				SvdEnumeratedValueDMNode enumeratedValue = new SvdEnumeratedValueDMNode(child);

				String value = enumeratedValue.getValue();
				if (!value.isEmpty()) {
					list.add(enumeratedValue);
				} else if (enumeratedValue.isDefault()) {
					if (fDefaultEnumerationNode == null) {
						fDefaultEnumerationNode = enumeratedValue;
					} else {
						// TODO: add issues
						Activator.log("duplicate isDefault enumeratedValue " + enumeratedValue.getName());
					}
				}
			}
		}

		SvdObjectDMNode[] array = list.toArray(new SvdObjectDMNode[list.size()]);

		// Preserve apparition order.
		return array;
	}

	// ------------------------------------------------------------------------

	/**
	 * Enumerate all enumerations and find the derived from node. The name is
	 * taken from the derivedFrom attribute.
	 * 
	 * @return a register node, or null if not found.
	 */
	@Override
	protected Leaf findDerivedFromNode() {

		String derivedFromName = getNode().getPropertyOrNull("derivedFrom");
		final SvdDerivedFromPath path = SvdDerivedFromPath.createEnumerationPath(derivedFromName);

		if (path == null) {
			return null;
		}

		Node root = getNode().getParent();
		while (!root.isType("device")) {
			root = root.getParent();
		}

		ITreeIterator peripheralNodes = new AbstractTreePreOrderIterator() {

			@Override
			public boolean isIterable(Leaf node) {

				if (node.isType("peripherals")) {
					return true;
				} else if (node.isType("peripheral")) {
					if (path.peripheralName == null) {
						return true;
					}
					if (path.peripheralName.equals(node.getProperty("name"))) {
						return true;
					}
					return false;
				} else if (node.isType("registers")) {
					return true;
				} else if (node.isType("cluster")) {
					return true;
				} else if (node.isType("register")) {
					if (path.registerName == null) {
						return true;
					}
					if (path.registerName.equals(node.getProperty("name"))) {
						return true;
					}
					return false;
				} else if (node.isType("fields")) {
					return true;
				} else if (node.isType("field")) {
					if (path.fieldName == null) {
						return true;
					}
					if (path.fieldName.equals(node.getProperty("name"))) {
						return true;
					}
					return false;
				} else if (node.isType("enumeratedValues")) {
					if (path.enumerationName == null) {
						return true;
					}
					if (path.enumerationName.equals(node.getProperty("name"))) {
						return true;
					}
					return false;
				}
				return false;
			}

			@Override
			public boolean isLeaf(Leaf node) {

				if (node.isType("enumeratedValues")) {
					return true;
				}
				return false;
			}
		};

		// Iterate only the current device children nodes
		peripheralNodes.setTreeNode(root);

		Leaf ret = null;
		for (Leaf node : peripheralNodes) {

			// System.out.println(node);
			if (node.isType("enumeratedValues")) {
				// There should be only one, filtered by the iterator.
				if (ret == null) {
					ret = node;
				} else {
					Activator.log("Non unique SVD path " + path);
				}
			}
		}

		return ret;
	}

	// ------------------------------------------------------------------------

	public SvdEnumeratedValueDMNode getDefaultEnumerationNode() {
		return fDefaultEnumerationNode;
	}

	public String getUsage() {

		if (fUsage == null) {
			fUsage = getNode().getProperty("usage");
		}

		if (fUsage.isEmpty()) {
			fUsage = "read-write";
		}

		return fUsage;
	}

	public boolean isUsageRead() {

		return ("read".equals(getUsage()) || "read-write".equals(getUsage()));
	}

	public boolean isUsageWrite() {

		return ("write".equals(getUsage()) || "read-write".equals(getUsage()));
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {

		if (getName().isEmpty()) {
			return "[" + getClass().getSimpleName() + ": \"" + getDescription() + "\"]";
		} else {
			return "[" + getClass().getSimpleName() + ": " + getName() + ", \"" + getDescription() + "\"]";
		}
	}

	// ------------------------------------------------------------------------
}
