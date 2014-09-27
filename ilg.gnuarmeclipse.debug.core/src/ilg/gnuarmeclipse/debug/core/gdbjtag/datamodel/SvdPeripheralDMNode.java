/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel;

import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * Wrapper over the tree node parsed from the SVD file. All details of a
 * peripheral are available from this point. Some of them are cached, for faster
 * access.
 */
public class SvdPeripheralDMNode extends SvdDMNode {

	// ------------------------------------------------------------------------

	private String fHexAddress;
	private BigInteger fBigAbsoluteAddress;

	private Boolean fIsSystem;

	/**
	 * The address where the Cortex-M system registers start.
	 */
	private static final BigInteger fSystemLimit = new BigInteger("E0000000",
			16);

	// ------------------------------------------------------------------------

	public SvdPeripheralDMNode(Leaf node) {

		super(node);

		// System.out.println("SvdPeripheralDMNode(" + node + ")");

		fBigAbsoluteAddress = null;
		fHexAddress = null;
		fIsSystem = null;
	}

	/**
	 * Clear all internal references.
	 */
	@Override
	public void dispose() {

		// System.out.println("SvdPeripheralDMNode.dispose(" + this + ")");

		super.dispose();

		fBigAbsoluteAddress = null;
		fHexAddress = null;
		fIsSystem = null;
	}

	// ------------------------------------------------------------------------

	/**
	 * Enumerate all peripherals of the same device and find the derived from
	 * node. The name is taken from the derivedFrom attribute.
	 * 
	 * @return a peripheral node, or null if not found.
	 */
	@Override
	protected Leaf findDerivedFromNode() {

		String derivedFromName = fNode.getPropertyOrNull("derivedFrom");
		if (derivedFromName == null) {
			return null;
		}

		Node parent = fNode.getParent();
		for (Leaf child : parent.getChildren()) {
			if (child.isType("peripheral")
					&& derivedFromName.equals(child.getProperty("name"))) {

				// Found the peripheral with the given name.
				return child;
			}
		}
		return null;
	}

	// ------------------------------------------------------------------------

	public String getId() {
		return getHexAddress();
	}

	/**
	 * Get peripheral block length.
	 * 
	 * @return a big integer value with the length in bytes.
	 */
	@Override
	public BigInteger getBigSizeBytes() {

		String size;

		size = getSizeElement(fNode);
		if ((size == null) && (getDerivedFromNode() != null)) {
			size = getSizeElement(getDerivedFromNode());
		}

		if (size != null) {
			return SvdUtils.parseNonNegativeBigInteger(size);
		}

		return null;
	}

	private String getSizeElement(Leaf node) {

		if (node.hasChildren()) {
			for (Leaf child : ((Node) node).getChildren()) {
				if (child.isType("addressBlock")) {
					String usage = child.getProperty("usage", "");
					if ("registers".equals(usage)) {
						return child.getProperty("size", null);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Get the numeric value of the address. Decimal, hex and octal values are
	 * accepted.
	 * <p>
	 * To support 64-bit addresses, use BigInteger objects.
	 * 
	 * @return a big integer.
	 */
	public BigInteger getBigAbsoluteAddress() {

		if (fBigAbsoluteAddress == null) {
			fBigAbsoluteAddress = SvdUtils
					.parseNonNegativeBigInteger(getBaseAddress());
		}
		return fBigAbsoluteAddress;
	}

	/**
	 * Get the address formatted as a fixed size hex string.
	 * <p>
	 * Also used as peripheral ID.
	 * 
	 * @return a string with the "%08X" formatted value.
	 */
	public String getHexAddress() {

		if (fHexAddress == null) {
			fHexAddress = String.format("0x%08X", getBigAbsoluteAddress()
					.longValue());
		}

		return fHexAddress;
	}

	/**
	 * Get the original address value from the SVD file. Mandatory, not derived.
	 * 
	 * @return a string with the address, usually a hex number.
	 */
	public String getBaseAddress() {
		return fNode.getProperty("baseAddress", "0");
	}

	/**
	 * Check if the peripheral is a Cortex-M system peripheral, i.e. has an
	 * address higher then 0xE0000000.
	 * 
	 * @return true if the peripheral is a system peripheral.
	 */
	public boolean isSystem() {

		if (fIsSystem == null) {

			BigInteger addr = getBigAbsoluteAddress();
			if (addr.compareTo(fSystemLimit) >= 0) {
				fIsSystem = new Boolean(true);
			} else {
				fIsSystem = new Boolean(false);
			}
		}
		return fIsSystem.booleanValue();
	}

	// ------------------------------------------------------------------------

	@Override
	protected SvdDMNode[] prepareChildren(Leaf node) {

		if (node == null || !node.hasChildren()) {
			return null;
		}

		// System.out.println("prepareChildren(" + node.getProperty("name") +
		// ")");

		Leaf group = ((Node) node).findChild("registers");
		if (!group.hasChildren()) {
			return null;
		}

		List<SvdDMNode> list = new LinkedList<SvdDMNode>();
		for (Leaf child : ((Node) group).getChildren()) {

			// Keep only <register> and <cluster> nodes
			if (child.isType("register")) {
				// TODO: process dimElementGroup, for one node generate multiple
				// objects
				list.add(new SvdRegisterDMNode(child));
			} else if (child.isType("cluster")) {
				// TODO: process dimElementGroup, for one node generate multiple
				// objects
				list.add(new SvdClusterDMNode(child));
			}
		}

		SvdDMNode[] array = list.toArray(new SvdDMNode[list.size()]);

		// Preserve apparition order.
		return array;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {

		return "[" + getName() + ", " + getBaseAddress() + ", "
				+ getBigSizeBytes() + ", \"" + getDescription() + "\"]";
	}

	// ------------------------------------------------------------------------
}
