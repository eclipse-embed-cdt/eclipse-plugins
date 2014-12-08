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

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;

import ilg.gnuarmeclipse.debug.gdbjtag.viewmodel.peripheral.PeripheralValue;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;

/**
 * Wrapper over the tree node parsed from the SVD file. All details of a field
 * are available from this point. Some of them are cached, for faster access.
 */
public class SvdFieldDMNode extends SvdDMNode implements Comparable<SvdDMNode> {

	// ------------------------------------------------------------------------

	private Integer fOffset;
	private Integer fWidth;

	private SvdEnumerationDMNode fReadEnumeration;
	private SvdEnumerationDMNode fWriteEnumeration;

	// ------------------------------------------------------------------------

	public SvdFieldDMNode(Leaf node) {

		super(node);

		fOffset = null;
		fWidth = null;

		fReadEnumeration = null;
		fWriteEnumeration = null;

		prepareEnumerations(node);
	}

	@Override
	public void dispose() {

		super.dispose();

		fOffset = null;
		fWidth = null;

		if (fReadEnumeration != null) {
			fReadEnumeration.dispose();
		}

		if (fWriteEnumeration != null) {
			fWriteEnumeration.dispose();
		}
	}

	// ------------------------------------------------------------------------

	private void prepareEnumerations(Leaf node) {

		if (node == null || !node.hasChildren()) {
			return;
		}

		for (Leaf child : ((Node) node).getChildren()) {

			// Consider only <enumeratedValues> nodes
			if (child.isType("enumeratedValues")) {
				SvdEnumerationDMNode enumeration = new SvdEnumerationDMNode(
						child);

				if (enumeration.isUsageRead()) {
					fReadEnumeration = enumeration;
				}
				if (enumeration.isUsageWrite()) {
					fWriteEnumeration = enumeration;
				}
			}
		}
	}

	/**
	 * The field is an enumeration if a read <enumerationValues> is present.
	 * 
	 * @return true if the field is an enumeration.
	 */
	public boolean isEnumeration() {
		return (fReadEnumeration != null);
	}

	public SvdEnumeratedValueDMNode findEnumeratedValue(PeripheralValue value) {

		if (fReadEnumeration == null) {
			return null;
		}

		SvdObjectDMNode children[] = fReadEnumeration.getChildren();
		for (int i = 0; i < children.length; ++i) {

			// TODO: find the good one
			if (((SvdEnumeratedValueDMNode) children[i]).isMatchForValue(value)) {
				return (SvdEnumeratedValueDMNode) children[i];
			}
		}

		return fReadEnumeration.getDefaultEnumerationNode();
	}

	/**
	 * Get the field least significative bit position in the register.
	 * 
	 * @return an integer between 0 and register size (usually 31).
	 */
	public int getOffset() {

		if (fOffset == null) {
			try {
				String offset = fNode.getProperty("bitOffset");
				if (!offset.isEmpty()) {
					fOffset = (int) SvdUtils.parseScaledNonNegativeLong(offset);
				} else {
					String lsb = fNode.getProperty("lsb");
					if (!lsb.isEmpty()) {
						fOffset = (int) SvdUtils
								.parseScaledNonNegativeLong(lsb);
					} else {
						String bitRange = fNode.getProperty("bitRange");
						if (!bitRange.isEmpty()) {
							bitRange = bitRange.replace('[', ' ');
							bitRange = bitRange.replace(']', ' ');
							bitRange = bitRange.trim();
							// Convert the second value
							fOffset = (int) SvdUtils
									.parseScaledNonNegativeLong(bitRange
											.split(":")[1]);
						} else {
							System.out.println("Missing offset, node " + fNode);
							fOffset = new Integer(0);
						}
					}
				}
			} catch (NumberFormatException e) {
				System.out.println("Bad offset, node " + fNode);
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
				String width = fNode.getProperty("bitWidth");
				if (!width.isEmpty()) {
					fWidth = (int) SvdUtils.parseScaledNonNegativeLong(width);
				} else {
					String msb = fNode.getProperty("msb");
					if (!msb.isEmpty()) {
						fWidth = new Integer(
								(int) (SvdUtils.parseScaledNonNegativeLong(msb)
										- getOffset() + 1));
					} else {
						String bitRange = fNode.getProperty("bitRange");
						if (!bitRange.isEmpty()) {
							bitRange = bitRange.replace('[', ' ');
							bitRange = bitRange.replace(']', ' ');
							bitRange = bitRange.trim();
							// Convert the first value and subtract the offset
							fWidth = new Integer(
									(int) (SvdUtils
											.parseScaledNonNegativeLong(bitRange
													.split(":")[0])
											- getOffset() + 1));
						} else {
							System.out.println("Missing width, node " + fNode);
							fWidth = new Integer(1);
						}
					}
				}
			} catch (NumberFormatException e) {
				System.out.println("Bad width, node " + fNode);
				fWidth = new Integer(1);
			}
		}
		return fWidth.intValue();
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

		return "[" + getName() + ", " + (getOffset() + getWidthBits() - 1)
				+ "-" + getOffset() + ", " + getAccess() + ", \""
				+ getDescription() + "\"]";
	}

	// ------------------------------------------------------------------------
}
