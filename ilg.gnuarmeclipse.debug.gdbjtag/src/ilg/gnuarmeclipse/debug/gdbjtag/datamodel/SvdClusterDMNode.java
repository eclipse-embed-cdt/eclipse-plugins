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

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;

/**
 * As per SVD 1.1, <i>"A cluster describes a sequence of registers within a
 * peripheral. A cluster has an base offset relative to the base address of the
 * peripheral. All registers within a cluster specify their address offset
 * relative to the cluster base address. Register and cluster sections can occur
 * in an arbitrary order."</i>
 */
public class SvdClusterDMNode extends SvdDMNode {

	// ------------------------------------------------------------------------

	public SvdClusterDMNode(Leaf node) {

		super(node);
	}

	@Override
	public void dispose() {

		super.dispose();
	}

	// ------------------------------------------------------------------------

	@Override
	protected SvdObjectDMNode[] prepareChildren(Leaf node) {

		if (node == null || !node.hasChildren()) {
			return null;
		}

		// System.out.println("prepareChildren(" + node.getProperty("name") +
		// ")");

		List<SvdObjectDMNode> list = new LinkedList<SvdObjectDMNode>();
		for (Leaf child : ((Node) node).getChildren()) {

			// Keep only <register> and <cluster> nodes
			if (child.isType("register")) {
				list.add(new SvdRegisterDMNode(child));
			} else if (child.isType("cluster")) {
				list.add(new SvdClusterDMNode(child));
			}
		}

		SvdObjectDMNode[] array = list.toArray(new SvdObjectDMNode[list.size()]);

		// Preserve apparition order.
		return array;
	}
	
	public BigInteger getBigAddressOffset() {
		String str = getNode().getProperty("addressOffset");
		if (!str.isEmpty()) {
			return SvdUtils.parseScaledNonNegativeBigInteger(str);
		} else {
			return BigInteger.ZERO;
		}
	}


	// ------------------------------------------------------------------------
}
