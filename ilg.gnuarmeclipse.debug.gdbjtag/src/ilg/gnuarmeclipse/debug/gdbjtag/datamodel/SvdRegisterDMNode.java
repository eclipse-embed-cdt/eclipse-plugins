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

import ilg.gnuarmeclipse.packs.core.Activator;
import ilg.gnuarmeclipse.packs.core.tree.AbstractTreePreOrderIterator;
import ilg.gnuarmeclipse.packs.core.tree.ITreeIterator;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;

public class SvdRegisterDMNode extends SvdDMNode {

	// ------------------------------------------------------------------------

	private String fDisplayName;
	private BigInteger fAddressOffset;
	private Integer fSize;
	private BigInteger fBigSizeBytes;

	private String fReadAction;
	private String fResetValue;
	private String fResetMask;

	// ------------------------------------------------------------------------

	public SvdRegisterDMNode(Leaf node) {

		super(node);

		fNode = node;

		fDisplayName = null;
		fAddressOffset = null;
		fSize = null;
		fBigSizeBytes = null;
		fReadAction = null;

		fResetValue = null;
		fResetMask = null;
	}

	public SvdRegisterDMNode(Leaf node, String displayName,
			BigInteger addressOffset) {

		this(node);

		// Start with given name and address offset
		fDisplayName = displayName;
		fAddressOffset = addressOffset;
	}

	/**
	 * Clear all internal references.
	 */
	@Override
	public void dispose() {

		fDisplayName = null;
		fAddressOffset = null;
		fSize = null;
		fBigSizeBytes = null;
		fReadAction = null;

		fResetValue = null;
		fResetMask = null;
		
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

		Leaf group = ((Node) node).findChild("fields");
		if (!group.hasChildren()) {
			return null;
		}

		List<SvdObjectDMNode> list = new LinkedList<SvdObjectDMNode>();
		for (Leaf child : ((Node) group).getChildren()) {

			// Keep only <field> nodes
			if (child.isType("field")) {
				list.add(new SvdFieldDMNode(child));
			}
		}

		SvdObjectDMNode[] array = list
				.toArray(new SvdObjectDMNode[list.size()]);

		// Preserve apparition order.
		return array;
	}

	// ------------------------------------------------------------------------

	/**
	 * Enumerate all registers and find the derived from node. The name is taken
	 * from the derivedFrom attribute.
	 * 
	 * @return a register node, or null if not found.
	 */
	@Override
	protected Leaf findDerivedFromNode() {

		String derivedFromName = fNode.getPropertyOrNull("derivedFrom");
		final SvdDerivedFromPath path = SvdDerivedFromPath
				.createRegisterPath(derivedFromName);

		if (path == null) {
			return null;
		}

		Node root = fNode.getParent();
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
				}
				return false;
			}

			@Override
			public boolean isLeaf(Leaf node) {

				if (node.isType("register")) {
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
			if (node.isType("register")) {
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

	/**
	 * Get the address offset inside the peripheral block or the cluster.
	 * <p>
	 * Remark: <tt>addressOffset</tt> is mandatory and cannot be derived.
	 * 
	 * @return a big integer with the address offset.
	 */
	@Override
	public BigInteger getBigAddressOffset() {

		if (fAddressOffset == null) {

			try {
				String offset = fNode.getProperty("addressOffset");
				if (!offset.isEmpty()) {

					fAddressOffset = SvdUtils
							.parseScaledNonNegativeBigInteger(offset);
				} else {
					System.out.println("Missing addressOffset, node " + fNode);
					fAddressOffset = BigInteger.ZERO;
				}
			} catch (NumberFormatException e) {
				System.out.println("Bad offset, node " + fNode);
				fAddressOffset = BigInteger.ZERO;
			}
		}

		return fAddressOffset;
	}

	/**
	 * Get the display name; if missing, use the name.
	 * <p>
	 * It may contain special characters and white spaces. The place holder %s
	 * can be used and is replaced by the dimIndex substring.
	 * 
	 * @return a short string.
	 */
	@Override
	public String getDisplayName() {

		if (fDisplayName == null) {

			// TODO: check derivedFrom
			fDisplayName = fNode.getProperty("displayName", getName());
		}
		return fDisplayName;
	}

	/**
	 * Get the register size, in bits.
	 * 
	 * @return an integer (usually 32).
	 */
	@Override
	public int getWidthBits() {

		if (fSize == null) {

			// TODO: check derivedFrom
			String size = SvdUtils.getProperty(fNode, "size", "32");

			try {
				fSize = new Integer(SvdUtils.parseScaledNonNegativeBigInteger(
						size).intValue());
			} catch (NumberFormatException e) {
				System.out.println("Bad size, node " + fNode);
				fSize = new Integer(32);
			}

		}

		return fSize.intValue();
	}

	@Override
	public BigInteger getBigSizeBytes() {
		if (fBigSizeBytes == null) {
			String sizeBytes = String.valueOf((getWidthBits() + 7) / 8);
			fBigSizeBytes = new BigInteger(sizeBytes);
		}
		return fBigSizeBytes;
	}

	// ------------------------------------------------------------------------

	public String getReadAction() {

		if (fReadAction == null) {

			// TODO: check derivedFrom
			fReadAction = SvdUtils.getProperty(fNode, "readAction", "");
		}

		return fReadAction;
	}

	public String getResetValue() {

		if (fResetValue == null) {

			// TODO: check derivedFrom
			fResetValue = SvdUtils.getProperty(fNode, "resetValue", "");
		}

		return fResetValue;
	}

	public String getResetMask() {

		if (fResetMask == null) {

			// TODO: check derivedFrom
			fResetMask = SvdUtils.getProperty(fNode, "resetMask", "");
		}

		return fResetMask;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {

		String str = "[" + getDisplayName() + ", 0x%08X, \"" + getDescription()
				+ "\"]";

		return String.format(str, getBigAddressOffset().longValue());
	}

	// ------------------------------------------------------------------------
}
