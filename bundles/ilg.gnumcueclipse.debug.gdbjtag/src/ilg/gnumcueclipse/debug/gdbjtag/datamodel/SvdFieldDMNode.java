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

package ilg.gnumcueclipse.debug.gdbjtag.datamodel;

import java.math.BigInteger;

import ilg.gnumcueclipse.debug.gdbjtag.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralValue;
import ilg.gnumcueclipse.packs.core.tree.AbstractTreePreOrderIterator;
import ilg.gnumcueclipse.packs.core.tree.ITreeIterator;
import ilg.gnumcueclipse.packs.core.tree.Leaf;
import ilg.gnumcueclipse.packs.core.tree.Node;

/**
 * Wrapper over the tree node parsed from the SVD file. All details of a field
 * are available from this point. Some of them are cached, for faster access.
 */
public class SvdFieldDMNode extends SvdDMNode implements Comparable<SvdDMNode> {

	// ------------------------------------------------------------------------

	private Integer fOffset;
	private Integer fWidth;

	// Not really big, but must match the parent prototype.
	private BigInteger fBigRepeatIncrement;

	private SvdEnumerationDMNode fReadEnumeration;
	private SvdEnumerationDMNode fWriteEnumeration;

	private String fResetValue;
	private String fResetMask;

	// ------------------------------------------------------------------------

	public SvdFieldDMNode(Leaf node) {

		super(node);

		fOffset = null;
		fWidth = null;

		fReadEnumeration = null;
		fWriteEnumeration = null;

		fResetValue = null;
		fResetMask = null;

		prepareEnumerations();
	}

	@Override
	public void dispose() {

		fOffset = null;
		fWidth = null;

		if (fReadEnumeration != null) {
			fReadEnumeration.dispose();
			fReadEnumeration = null;
		}

		if (fWriteEnumeration != null) {
			fWriteEnumeration.dispose();
			fWriteEnumeration = null;
		}

		fResetValue = null;
		fResetMask = null;

		super.dispose();
	}

	// ------------------------------------------------------------------------

	private void prepareEnumerations() {

		if (((Node) getNode()).hasChildren()) {
			for (Leaf child : ((Node) getNode()).getChildren()) {

				String element = "";
				if (child.getPackType() == Leaf.PACK_TYPE_CMSIS) {
					element = "enumeratedValues";
				} else if (child.getPackType() == Leaf.PACK_TYPE_XPACK) {
					element = "enumeration";
				}

				// Consider only <enumeratedValues> nodes
				if (child.isType(element)) {
					SvdEnumerationDMNode enumeration = new SvdEnumerationDMNode(child);

					if (enumeration.isUsageRead()) {
						fReadEnumeration = enumeration;
					}
					if (enumeration.isUsageWrite()) {
						fWriteEnumeration = enumeration;
					}
				}
			}
		}

		if ((fReadEnumeration != null) && (fWriteEnumeration != null)) {
			return; // Mission accomplished
		}

		if ((getDerivedFromNode() != null) && getDerivedFromNode().hasChildren()) {
			for (Leaf child : ((Node) getDerivedFromNode()).getChildren()) {

				String element = "";
				if (child.getPackType() == Leaf.PACK_TYPE_CMSIS) {
					element = "enumeratedValues";
				} else if (child.getPackType() == Leaf.PACK_TYPE_XPACK) {
					element = "enumeration";
				}

				if (child.isType(element)) {
					SvdEnumerationDMNode enumeration = new SvdEnumerationDMNode(child);

					if ((fReadEnumeration == null) && enumeration.isUsageRead()) {
						fReadEnumeration = enumeration;
					}
					if ((fWriteEnumeration == null) && enumeration.isUsageWrite()) {
						fWriteEnumeration = enumeration;
					}
				}
			}
		}
	}

	// ------------------------------------------------------------------------

	/**
	 * Enumerate all fields and find the derived from node. The name is taken from
	 * the derivedFrom attribute.
	 * 
	 * @return a register node, or null if not found.
	 */
	@Override
	protected Leaf findDerivedFromNode() {

		String derivedFromName = getNode().getPropertyOrNull("derivedFrom");
		final SvdDerivedFromPath path = SvdDerivedFromPath.createFieldPath(derivedFromName);

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

				String clustersElement = "";
				if (node.getPackType() == Node.PACK_TYPE_CMSIS) {
					clustersElement = "cluster";
				} else if (node.getPackType() == Node.PACK_TYPE_XPACK) {
					clustersElement = "clusters";
				}

				if (node.isType("peripherals")) {
					return true;
				} else if (node.isType("peripheral")) {
					if (path.peripheralName == null) {
						return true;
					}
					if (path.peripheralName.equals(node.getName())) {
						return true;
					}
					return false;
				} else if (node.isType("registers")) {
					return true;
				} else if (node.isType(clustersElement)) {
					return true;
				} else if (node.isType("register")) {
					if (path.registerName == null) {
						return true;
					}
					if (path.registerName.equals(node.getName())) {
						return true;
					}
					return false;
				} else if (node.isType("fields")) {
					return true;
				} else if (node.isType("field")) {
					if (path.fieldName == null) {
						return true;
					}
					if (path.fieldName.equals(node.getName())) {
						return true;
					}
					return false;
				}
				return false;
			}

			@Override
			public boolean isLeaf(Leaf node) {

				if (node.isType("field")) {
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
			if (node.isType("field")) {
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
	 * The field is an enumeration if a read <enumerationValues> is present.
	 * 
	 * @return true if the field is an enumeration.
	 */
	public boolean isEnumeration() {
		return (fReadEnumeration != null);
	}

	/**
	 * Iterate the read enumeration and try to match the given value.
	 * 
	 * @param value
	 *            a PeripheralValue object
	 * @return a SvdEnumeratedValueDMNode object, or null if not found.
	 */
	public SvdEnumeratedValueDMNode findEnumeratedValue(PeripheralValue value) {

		if (fReadEnumeration == null) {
			return null;
		}

		SvdObjectDMNode children[] = fReadEnumeration.getChildren();
		for (int i = 0; i < children.length; ++i) {

			// Return first match.
			if (((SvdEnumeratedValueDMNode) children[i]).isMatchForValue(value)) {
				if (Activator.getInstance().isDebugging()) {
					System.out.println("findEnumeratedValue("+value+") " + children[i]);
				}
				return (SvdEnumeratedValueDMNode) children[i];
			}
		}

		SvdEnumeratedValueDMNode defNode = fReadEnumeration.getDefaultEnumerationNode();
		if (Activator.getInstance().isDebugging()) {
			System.out.println("findEnumeratedValue("+value+") default " + defNode);
		}
		return defNode;
	}

	/**
	 * Iterate the write enumeration and try to match the given value.
	 * 
	 * @param value
	 *            a PeripheralValue object
	 * @return an Integer or null if not found.
	 */
	public Integer findEnumeratedComboIndex(PeripheralValue value) {

		if (fWriteEnumeration == null) {
			return null;
		}

		SvdObjectDMNode children[] = fWriteEnumeration.getChildren();
		for (int i = 0; i < children.length; ++i) {

			// Return first match.
			if (((SvdEnumeratedValueDMNode) children[i]).isMatchForValue(value)) {
				return new Integer(i);
			}
		}

		return null;
	}

	public SvdEnumerationDMNode getWriteEnumerationDMNode() {
		return fWriteEnumeration;
	}

	/**
	 * Get the field least significative bit position in the register.
	 * 
	 * @return an integer between 0 and register size (usually 31).
	 */
	public int getOffset() {

		if (fOffset == null) {
			try {
				String offset = getPropertyWithDerived("bitOffset");
				if (!offset.isEmpty()) {
					fOffset = (int) SvdUtils.parseScaledNonNegativeLong(offset);
				} else {
					String lsb = getPropertyWithDerived("lsb");
					if (!lsb.isEmpty()) {
						fOffset = (int) SvdUtils.parseScaledNonNegativeLong(lsb);
					} else {
						String bitRange = getPropertyWithDerived("bitRange");
						if (!bitRange.isEmpty()) {
							bitRange = bitRange.replace('[', ' ');
							bitRange = bitRange.replace(']', ' ');
							bitRange = bitRange.trim();
							// Convert the second value
							fOffset = (int) SvdUtils.parseScaledNonNegativeLong(bitRange.split(":")[1]);
						} else {
							Activator.log("Missing offset, node " + getNode());
							fOffset = new Integer(0);
						}
					}
				}
			} catch (NumberFormatException e) {
				Activator.log("Bad offset, node " + getNode());
				fOffset = new Integer(0);
			}
		}

		return fOffset.intValue();
	}

	/**
	 * Get the field width, in bits.
	 * 
	 * @return an integer, between 1 and register size (usually 32).
	 */
	public int getWidthBits() {

		if (fWidth == null) {
			try {
				String width = getPropertyWithDerived("bitWidth");
				if (!width.isEmpty()) {
					fWidth = (int) SvdUtils.parseScaledNonNegativeLong(width);
				} else {
					String msb = getPropertyWithDerived("msb");
					if (!msb.isEmpty()) {
						fWidth = new Integer((int) (SvdUtils.parseScaledNonNegativeLong(msb) - getOffset() + 1));
					} else {
						String bitRange = getPropertyWithDerived("bitRange");
						if (!bitRange.isEmpty()) {
							bitRange = bitRange.replace('[', ' ');
							bitRange = bitRange.replace(']', ' ');
							bitRange = bitRange.trim();
							// Convert the first value and subtract the offset
							fWidth = new Integer((int) (SvdUtils.parseScaledNonNegativeLong(bitRange.split(":")[0])
									- getOffset() + 1));
						} else {
							Activator.log("Missing width, node " + getNode());
							fWidth = new Integer(1);
						}
					}
				}
			} catch (NumberFormatException e) {
				Activator.log("Bad width, node " + getNode());
				fWidth = new Integer(1);
			}
		}
		return fWidth.intValue();
	}

	@Override
	public BigInteger getBigRepeatIncrement() {

		if (fBigRepeatIncrement == null) {
			fBigRepeatIncrement = BigInteger.valueOf(getWidthBits());
			BigInteger repeatIncrement = getBigArrayAddressIncrement();
			if (repeatIncrement != BigInteger.ZERO) {
				if (repeatIncrement.compareTo(fBigRepeatIncrement) > 0) {
					fBigRepeatIncrement = repeatIncrement;
				}
			}
		}
		return fBigRepeatIncrement;
	}

	public String getResetValue() {

		if (fResetValue == null) {
			fResetValue = getPropertyWithDerivedWithParent("resetValue");
		}

		return fResetValue;
	}

	public String getResetMask() {

		if (fResetMask == null) {
			fResetMask = getPropertyWithDerivedWithParent("resetMask");
		}

		return fResetMask;
	}

	// ------------------------------------------------------------------------

	/**
	 * Comparator using the field bit offset.
	 * 
	 * @param comp
	 *            another SvdField to compare with.
	 * @return -1, 0, 1 for lower, same, greater
	 */
	@Override
	public int compareTo(SvdDMNode comp) {

		if (comp instanceof SvdFieldDMNode) {
			// The field bit offsets are (should be!) unique, use them for
			// sorting. Reverse sign to sort descending.
			return -(getOffset() - ((SvdFieldDMNode) comp).getOffset());
		}
		return 0;
	}

	@Override
	public String toString() {

		return "[" + getClass().getSimpleName() + ": " + getName() + ", " + (getOffset() + getWidthBits() - 1) + "-"
				+ getOffset() + ", " + getAccess() + ", \"" + getDescription() + "\"]";
	}

	// ------------------------------------------------------------------------
}
