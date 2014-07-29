/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.cmsis;

import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.Utils;

import org.eclipse.ui.console.MessageConsoleStream;

public class PdscTreeParser {

	protected MessageConsoleStream fOut;

	public PdscTreeParser() {

		fOut = ConsoleStream.getConsoleOut();
	}

	protected boolean checkValid(Node node) {

		if (node == null || !node.hasChildren()) {
			return false;
		}

		Leaf firstChild = node.getFirstChild();
		if (!firstChild.isType("package")) {
			String msg = "Missing <package>; instead, <" + firstChild.getType()
					+ "> encountered";

			fOut.println("Error+" + msg);
			Utils.reportError(msg);
			return false;
		}

		String schemaVersion = firstChild.getProperty("schemaVersion");

		if (!isSchemaValid(schemaVersion)) {
			return false;
		}
		return true;
	}

	protected boolean isSchemaValid(String schemaVersion) {

		if ("1.0".equals(schemaVersion)) {
			;
		} else if ("1.1".equals(schemaVersion)) {
			;
		} else if ("1.2".equals(schemaVersion)) {
			;
		} else if ("1.3".equals(schemaVersion)) {
			;
		} else {
			System.out.println("Unrecognised schema version " + schemaVersion);
			return false;
		}
		return true;
	}

	protected Node addUniqueVendor(Node parent, String vendorName,
			String vendorId) {

		if (parent.hasChildren()) {
			for (Leaf child : parent.getChildren()) {
				if (vendorId.equals(child.getProperty(Property.VENDOR_ID))) {
					return (Node) child;
				}
			}
		}

		Node vendor = Node.addNewChild(parent, Type.VENDOR);
		vendor.setName(vendorName);
		vendor.putProperty(Property.VENDOR_ID, vendorId);

		return vendor;
	}

	protected String updatePosixSeparators(String spath) {
		return spath.replace('\\', '/');
	}

}
