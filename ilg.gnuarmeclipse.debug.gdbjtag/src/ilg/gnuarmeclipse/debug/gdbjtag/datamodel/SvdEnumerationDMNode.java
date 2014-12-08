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

		// Trigger processing for <enumeratedValue> and setting for a
		// default value.
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

		if (node == null || !node.hasChildren()) {
			return null;
		}

		List<SvdObjectDMNode> list = new LinkedList<SvdObjectDMNode>();
		for (Leaf child : ((Node) node).getChildren()) {

			// Consider only <enumeratedValue> nodes
			if (child.isType("enumeratedValue")) {

				SvdEnumeratedValueDMNode enumeratedValue = new SvdEnumeratedValueDMNode(
						child);

				String value = enumeratedValue.getValue();
				if (!value.isEmpty()) {
					list.add(enumeratedValue);
				} else if (enumeratedValue.isDefault()) {
					if (fDefaultEnumerationNode == null) {
						fDefaultEnumerationNode = enumeratedValue;
					} else {
						// TODO: add issues
						System.out
								.println("duplicate enumeratedValue isDefault "
										+ enumeratedValue.getName());
					}
				}
			}
		}

		SvdObjectDMNode[] array = list
				.toArray(new SvdObjectDMNode[list.size()]);

		// Preserve apparition order.
		return array;
	}

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

		String str = "[" + getName() + ", \"" + getDescription() + "\"]";

		return str; // String.format(str);
	}

	// ------------------------------------------------------------------------
}
