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
import java.util.LinkedList;
import java.util.List;

import ilg.gnumcueclipse.debug.gdbjtag.Activator;
import ilg.gnumcueclipse.packs.core.tree.Leaf;

public class SvdDMNode extends SvdObjectDMNode implements Comparable<SvdDMNode> {

	// ------------------------------------------------------------------------

	/**
	 * The content of the SVD <tt>&lt;access&gt;</tt> element. Defines the default
	 * access rights for all registers. Access rights can be redefined on any lower
	 * level of the description using the access element there.
	 * <p>
	 * <ul>
	 * <li><b>read-only</b>: read access is permitted. Write operations have an
	 * undefined result.
	 * <li><b>write-only</b>: write access is permitted. Read operations have an
	 * undefined result.
	 * <li><b>read-write</b>: both read and write accesses are permitted. Writes
	 * affect the state of the register and reads return a value related to the
	 * register.
	 * <li><b>writeOnce</b>: only the first write after reset has an effect on the
	 * register. Read operations deliver undefined results.
	 * <li><b>read-writeOnce</b>: Read operations deliver a result related to the
	 * register content. Only the first write access to this register after a reset
	 * will have an effect on the register content.
	 * </ul>
	 */
	private String fAccess;

	/**
	 * The content of the SVD <tt>&lt;readAction&gt;</tt> element.
	 * <p>
	 * If set, it specifies the side effect following a read operation. If not set,
	 * the register is not modified.
	 * <p>
	 * The defined side effects are:
	 * <ul>
	 * <li><b>clear</b>: The register is cleared (set to zero) following a read
	 * operation.
	 * <li><b>set</b>: The register is set (set to ones) following a read operation.
	 * <li><b>modify</b>: The register is modified in some way after a read
	 * operation.
	 * <li><b>modifyExternal</b>: One or more dependent resources other than the
	 * current register are immediately affected by a read operation (it is
	 * recommended that the register description specifies these dependencies).
	 * </ul>
	 * Debuggers are not expected to read this register location unless explicitly
	 * instructed by the user.
	 */
	private String fReadAction;

	private BigInteger fBigArrayAddressIncrement;
	// ------------------------------------------------------------------------

	public SvdDMNode(Leaf node) {

		super(node);

		fAccess = null;
		fReadAction = null;
		fBigArrayAddressIncrement = null;
	}

	/**
	 * Clear all internal references.
	 */
	public void dispose() {

		fAccess = null;
		fReadAction = null;

		super.dispose();
	}

	// ------------------------------------------------------------------------

	public BigInteger getBigAbsoluteAddress() {
		return null;
	}

	public BigInteger getBigMaxAbsoluteAddress() {
		return null;
	}

	public BigInteger getBigRepeatIncrement() {
		return null;
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
			fAccess = getPropertyWithDerivedWithParent("access");
		}
		return fAccess;
	}

	public boolean isReadOnly() {

		String access = getAccess();
		if (getNode().getPackType() == Leaf.PACK_TYPE_CMSIS) {
			return ("read-only".equals(access));
		} else if (getNode().getPackType() == Leaf.PACK_TYPE_XPACK) {
			return ("r".equals(access));
		}
		return false;
	}

	public boolean isWriteOnly() {

		String access = getAccess();
		if (getNode().getPackType() == Leaf.PACK_TYPE_CMSIS) {
			return ("write-only".equals(access) || "writeOnce".equals(access));
		} else if (getNode().getPackType() == Leaf.PACK_TYPE_XPACK) {
			return ("w".equals(access));
		}
		return false;
	}

	public boolean hasReadAction() {
		return !getReadAction().isEmpty();
	}

	public boolean isReadAllowed() {
		return !hasReadAction();
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
			fReadAction = getPropertyWithDerivedWithParent("readAction");
		}

		return fReadAction;
	}

	public boolean isArray() {
		String element = null;
		if (getNode().getPackType() == Leaf.PACK_TYPE_CMSIS) {
			element = "dim";
		} else if (getNode().getPackType() == Leaf.PACK_TYPE_XPACK) {
			element = "arraySize";
		}
		return !(getNode().getProperty(element).isEmpty());
	}

	public boolean isRepetition() {
		if (getNode().getPackType() == Leaf.PACK_TYPE_XPACK) {
			return !(getNode().getProperty("repeatGenerator").isEmpty());
		}
		// In CMSIS there are no explicit repetitions.
		return false;
	}

	/**
	 * 
	 * @return Number or 0, if not an array.
	 */
	public int getArraySize() {

		String element = null;
		if (getNode().getPackType() == Leaf.PACK_TYPE_CMSIS) {
			element = "dim";
		} else if (getNode().getPackType() == Leaf.PACK_TYPE_XPACK) {
			element = "arraySize";
		} else {
			return 0;
		}

		String str = getNode().getProperty(element);

		int dim;
		try {
			dim = (int) SvdUtils.parseScaledNonNegativeLong(str);
		} catch (NumberFormatException e) {
			Activator.log("Node " + getNode().getName() + ", non integer <" + element + "> " + str);
			dim = 0;
		}

		return dim;
	}

	/**
	 * 
	 * @return BigInteger or ZERO.
	 */
	public BigInteger getBigArrayAddressIncrement() {

		if (fBigArrayAddressIncrement == null) {
			String element = null;
			if (getNode().getPackType() == Leaf.PACK_TYPE_CMSIS) {
				element = "dimIncrement";
			} else if (getNode().getPackType() == Leaf.PACK_TYPE_XPACK) {
				element = "repeatIncrement";
			}
			String increment = getNode().getProperty(element);
			if (increment.isEmpty()) {
				fBigArrayAddressIncrement = BigInteger.ZERO;
			} else {
				try {
					fBigArrayAddressIncrement = SvdUtils.parseScaledNonNegativeBigInteger(increment);
				} catch (NumberFormatException e) {
					Activator.log("Node " + getNode().getName() + ", non number <" + element + "> " + increment);
					fBigArrayAddressIncrement = BigInteger.ZERO;
				}
			}
		}
		return fBigArrayAddressIncrement;
	}

	public String[] getRepetitionSubstitutions() {

		List<String> lst = new LinkedList<String>();

		String element = null;
		if (getNode().getPackType() == Leaf.PACK_TYPE_CMSIS) {
			element = "dimIndex";
		} else if (getNode().getPackType() == Leaf.PACK_TYPE_XPACK) {
			element = "repeatGenerator";
		}

		String index = getNode().getProperty(element);
		if (!index.isEmpty()) {
			// "[0-9]+\-[0-9]+|[A-Z]-[A-Z]|[_0-9a-zA-Z]+(,\s*[_0-9a-zA-Z]+)+"
			if (index.contains("-")) {
				// Range of numbers.
				String[] range = index.split("-");
				try {
					int from = Integer.parseInt(range[0]);
					int to = Integer.parseInt(range[1]);

					// Expand inclusive range of numbers.
					for (int i = from; i <= to; i++) {
						lst.add(String.valueOf(i));
					}
				} catch (NumberFormatException e) {
					Activator.log("Node " + getNode().getName() + ", non number range " + range + ", ignored.");
				}
			} else if (index.contains(",")) {
				// Comma separated list of strings.
				String[] indices = index.split(",");
				// Trim and copy.
				for (int i = 0; i < indices.length; ++i) {
					lst.add(String.valueOf(i));
				}
			}
		}

		return lst.toArray(new String[0]);
	}

	// ------------------------------------------------------------------------

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + ": " + getDisplayName() + ", " + getAccess() + "]";

	}

	@Override
	public int compareTo(SvdDMNode comp) {
		return getDisplayName().compareTo(comp.getDisplayName());
	}

	// ------------------------------------------------------------------------
}
