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

package ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel;

import java.math.BigInteger;

import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.core.Xml;
import ilg.gnuarmeclipse.packs.core.Activator;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;

public class SvdDMNode implements Comparable<SvdDMNode> {

	// ------------------------------------------------------------------------

	/**
	 * Reference to the original node in the generic tree parsed from SVD.
	 */
	protected Leaf fNode;

	/**
	 * Reference to the node referenced, or null.
	 */
	private Leaf fDerivedFromNode;

	/**
	 * The children nodes, with types depending on individual node.
	 */
	private SvdDMNode[] fChildren;

	/**
	 * The description string. If multiple lines, they were already joined.
	 */
	private String fDescription;

	/**
	 * The content of the SVD <tt>&lt;access&gt;</tt> element. Defines the
	 * default access rights for all registers. Access rights can be redefined
	 * on any lower level of the description using the access element there.
	 * <p>
	 * <ul>
	 * <li><b>read-only</b>: read access is permitted. Write operations have an
	 * undefined result.
	 * <li><b>write-only</b>: write access is permitted. Read operations have an
	 * undefined result.
	 * <li><b>read-write</b>: both read and write accesses are permitted. Writes
	 * affect the state of the register and reads return a value related to the
	 * register.
	 * <li><b>writeOnce</b>: only the first write after reset has an effect on
	 * the register. Read operations deliver undefined results.
	 * <li><b>read-writeOnce</b>: Read operations deliver a result related to
	 * the register content. Only the first write access to this register after
	 * a reset will have an effect on the register content.
	 * </ul>
	 */
	private String fAccess;

	/**
	 * The content of the SVD <tt>&lt;readAction&gt;</tt> element.
	 * <p>
	 * If set, it specifies the side effect following a read operation. If not
	 * set, the register is not modified.
	 * <p>
	 * The defined side effects are:
	 * <ul>
	 * <li><b>clear</b>: The register is cleared (set to zero) following a read
	 * operation.
	 * <li><b>set</b>: The register is set (set to ones) following a read
	 * operation.
	 * <li><b>modify</b>: The register is modified in some way after a read
	 * operation.
	 * <li><b>modifyExternal</b>: One or more dependent resources other than the
	 * current register are immediately affected by a read operation (it is
	 * recommended that the register description specifies these dependencies).
	 * </ul>
	 * Debuggers are not expected to read this register location unless
	 * explicitly instructed by the user.
	 */
	private String fReadAction;

	// ------------------------------------------------------------------------

	public SvdDMNode(Leaf node) {

		fNode = node;
		fDerivedFromNode = null;

		fChildren = null;
		fDescription = null;
		fAccess = null;
		fReadAction = null;
	}

	/**
	 * Clear all internal references.
	 */
	public void dispose() {

		fNode = null;
		fDerivedFromNode = null;

		fChildren = null;
		fDescription = null;
		fAccess = null;
		fReadAction = null;
	}

	// ------------------------------------------------------------------------

	/**
	 * Get the original SVD node, in the generic tree.
	 * 
	 * @return a generic tree node.
	 */
	public Leaf getNode() {
		return fNode;
	}

	/**
	 * Get name. Mandatory, cannot be derived.
	 * 
	 * @return a short (usually upper case) string.
	 */
	public String getName() {
		return fNode.getProperty("name");
	}

	/**
	 * Check if the node is derived from another node.
	 * 
	 * @return true if derived.
	 */
	public boolean isDerived() {
		return fNode.hasProperty("derivedFrom");
	}

	/**
	 * Get the node that this node is derived from.
	 * 
	 * @return the derived from node, or null if not derived or not found.
	 */
	public Leaf getDerivedFromNode() {

		if (fDerivedFromNode == null) {

			fDerivedFromNode = findDerivedFromNode();
		}

		// If not derived, return null
		return fDerivedFromNode;
	}

	protected Leaf findDerivedFromNode() {
		return null;
	}

	/**
	 * Get the cluster, register, field children nodes.
	 * 
	 * @return an array of nodes.
	 */
	public SvdDMNode[] getChildren() {

		if (fChildren == null) {

			// If any, try to use the derivedFrom node children.
			fChildren = prepareChildren(getDerivedFromNode());

			if (fChildren == null) {
				// Try to get the children from the current node.
				fChildren = prepareChildren(fNode);
			}

			if (fChildren == null) {
				// If none worked, return an empty array.
				fChildren = new SvdDMNode[0];
			}
		}

		return fChildren;
	}

	/**
	 * To be redefined by nodes that have children.
	 * 
	 * @return an array of nodes or null if no children.
	 */
	protected SvdDMNode[] prepareChildren(Leaf node) {
		return null;
	}

	public BigInteger getBigAbsoluteAddress() {
		return null;
	}

	/**
	 * Get the default display name.
	 * 
	 * @return a string.
	 */
	public String getDisplayName() {
		return getName();
	}

	/**
	 * Get field description. In case the description spans multiple lines,
	 * these lines are joined together in a single line, with each of the
	 * individual lines trimmed.
	 * <p>
	 * If not present, the derived from node description is returned.
	 * 
	 * @return a string with the field description, possibly empty.
	 */
	public String getDescription() {

		if (fDescription == null) {

			fDescription = fNode.getDescription();
			if ((fDescription.isEmpty()) && (getDerivedFromNode() != null)) {
				fDescription = getDerivedFromNode().getDescription();
			}
			if (fDescription != null) {
				// Process multiple lines descriptions
				fDescription = Xml.joinMultiLine(fDescription);
			} else {
				fDescription = "";
			}

			fDescription = StringUtils.capitalizeFirst(fDescription);
		}
		return fDescription;
	}

	/**
	 * Get the register access info.
	 * <p>
	 * The legal values are:
	 * <ul>
	 * <li>read-only
	 * <li>write-only
	 * <li>read-write
	 * <li>writeOnce
	 * <li>read-writeOnce
	 * </ul>
	 * 
	 * @return a string, possibly empty.
	 */
	public String getAccess() {

		if (fAccess == null) {
			fAccess = SvdUtils.getProperty(fNode, getDerivedFromNode(),
					"access", "");
		}
		return fAccess;
	}

	public boolean isReadOnly() {

		String access = getAccess();
		return "read-only".equals(access);
	}

	public boolean isWriteOnly() {

		String access = getAccess();
		if ("write-only".equals(access) || "writeOnce".equals(access)) {
			return true;
		}
		return false;
	}

	public boolean hasReadAction() {
		return !getReadAction().isEmpty();
	}

	public boolean isReadAllowed() {
		return (!isWriteOnly()) && !hasReadAction();
	}

	public int getWidthBits() {
		return -1;
	}

	public BigInteger getBigSizeBytes() {
		return null;
	}

	public BigInteger getBigAddressOffset() {
		return BigInteger.ZERO;
	}

	public String getReadAction() {

		if (fReadAction == null) {
			fReadAction = SvdUtils.getProperty(fNode, getDerivedFromNode(),
					"readAction", "");
		}

		return fReadAction;
	}

	public boolean isArray() {
		return !(fNode.getProperty("dim").isEmpty());
	}

	public int getArrayDim() {

		String str = fNode.getProperty("dim");
		int dim;
		try {
			dim = SvdUtils.parseScaledNonNegativeInt(str);
		} catch (NumberFormatException e) {
			Activator.log("Node " + fNode.getName() + ", non integer <dim> "
					+ str);
			dim = 0;
		}

		return dim;
	}

	public BigInteger getBigIntegerArrayAddressIncrement() {

		String increment = fNode.getProperty("dimIncrement");

		try {
			return SvdUtils.parseScaledNonNegativeBigInteger(increment);
		} catch (NumberFormatException e) {
			Activator.log("Node " + fNode.getName()
					+ ", non number <dimIncrement> " + increment);
			return BigInteger.ZERO;
		}
	}

	public String[] getArrayIndices() {

		int dim = getArrayDim();
		if (dim == 0) {
			return new String[] { "0" };
		}

		// Create the array of string indices
		String[] arr = new String[dim];

		// Init with increasing numbers
		for (int i = 0; i < dim; ++i) {
			arr[i] = String.valueOf(i);
		}

		String index = fNode.getProperty("dimIndex");
		if (!index.isEmpty()) {
			// "[0-9]+\-[0-9]+|[A-Z]-[A-Z]|[_0-9a-zA-Z]+(,\s*[_0-9a-zA-Z]+)+"
			if (index.contains("-")) {
				// First two cases, range of numbers or of letters.
				String[] range = index.split("-");
				try {
					int from = Integer.parseInt(range[0]);
					int to = Integer.parseInt(range[1]);

					int i = 0;
					// Expand inclusive range of numbers.
					for (int j = from; (j <= to) && (i < arr.length); j++, i++) {
						arr[i] = String.valueOf(j);
					}
				} catch (NumberFormatException e) {
					// Range of characters.
					char from = range[0].charAt(0);
					char to = range[1].charAt(0);

					int i = 0;
					// Expand inclusive range of upper case letters.
					for (char j = from; (j <= to) && (i < arr.length); j++, i++) {
						arr[i] = String.valueOf(j);
					}
				}
			} else if (index.contains(",")) {
				// Third case, comma separated list of strings.
				String[] indices = index.split(",");
				// Trim and copy.
				for (int i = 0; (i < indices.length) && (i < arr.length); ++i) {
					arr[i] = indices[i].trim();
				}
			}

		}

		return arr;
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + " " + getDisplayName() + ", "
				+ getAccess() + "]";

	}

	@Override
	public int compareTo(SvdDMNode comp) {
		return getDisplayName().compareTo(comp.getDisplayName());
	}

	// ------------------------------------------------------------------------
}
