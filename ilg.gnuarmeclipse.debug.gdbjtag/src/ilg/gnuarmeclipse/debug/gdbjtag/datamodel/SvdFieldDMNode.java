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

import ilg.gnuarmeclipse.packs.core.tree.Leaf;

/**
 * Wrapper over the tree node parsed from the SVD file. All details of a field
 * are available from this point. Some of them are cached, for faster access.
 */
public class SvdFieldDMNode extends SvdDMNode implements Comparable<SvdDMNode> {

	// ------------------------------------------------------------------------

	private Integer fOffset;
	private Integer fWidth;

	// ------------------------------------------------------------------------

	public SvdFieldDMNode(Leaf node) {

		super(node);

		fOffset = null;
		fWidth = null;
	}

	@Override
	public void dispose() {

		super.dispose();

		fOffset = null;
		fWidth = null;
	}

	// ------------------------------------------------------------------------

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
					fOffset = SvdUtils.parseScaledNonNegativeInteger(offset);
				} else {
					String lsb = fNode.getProperty("lsb");
					if (!lsb.isEmpty()) {
						fOffset = SvdUtils.parseScaledNonNegativeInteger(lsb);
					} else {
						String bitRange = fNode.getProperty("bitRange");
						if (!bitRange.isEmpty()) {
							bitRange = bitRange.replace('[', ' ');
							bitRange = bitRange.replace(']', ' ');
							bitRange = bitRange.trim();
							// Convert the second value
							fOffset = SvdUtils
									.parseScaledNonNegativeInteger(bitRange
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
					fWidth = SvdUtils.parseScaledNonNegativeInteger(width);
				} else {
					String msb = fNode.getProperty("msb");
					if (!msb.isEmpty()) {
						fWidth = new Integer(
								SvdUtils.parseScaledNonNegativeInt(msb)
										- getOffset() + 1);
					} else {
						String bitRange = fNode.getProperty("bitRange");
						if (!bitRange.isEmpty()) {
							bitRange = bitRange.replace('[', ' ');
							bitRange = bitRange.replace(']', ' ');
							bitRange = bitRange.trim();
							// Convert the first value and subtract the offset
							fWidth = new Integer(
									SvdUtils.parseScaledNonNegativeInt(bitRange
											.split(":")[0]) - getOffset() + 1);
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
