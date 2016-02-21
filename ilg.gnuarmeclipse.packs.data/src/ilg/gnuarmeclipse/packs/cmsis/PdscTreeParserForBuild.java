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

import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;

public class PdscTreeParserForBuild extends PdscTreeParser {

	private int fCount;
	private String fVendorName;
	private String fPackName;
	private String fVersion;

	// ------------------------------------------------------------------------

	// Parse for devices, add them to parent node
	public int parseDevices(Node node, Node parent) {

		if (!checkValid(node)) {
			return 0;
		}

		fCount = 0;

		fVendorName = "";
		fPackName = "";
		fVersion = "";

		Leaf packageNode = node.getFirstChild();

		if (!packageNode.isType("package")) {
			return 0;
		}

		fVendorName = packageNode.getProperty("vendor");
		fPackName = packageNode.getProperty("name");

		if (packageNode.hasChildren()) {
			for (Leaf child : ((Node) packageNode).getChildren()) {

				if (child.isType("devices") && child.hasChildren()) {
					for (Leaf grandchild : ((Node) child).getChildren()) {
						if (grandchild.isType("family")) {
							processFamilyNode(grandchild, parent);
						}
					}
				} else if (child.isType("releases") && child.hasChildren()) {

					Leaf versionNode = ((Node) child).getFirstChild();
					fVersion = versionNode.getProperty("version");
				}
			}
		}

		return fCount;
	}

	// Parse for boards, add them to parent node
	public int parseBoards(Node node, Node parent) {

		if (!checkValid(node)) {
			return 0;
		}

		fCount = 0;

		fVendorName = "";
		fPackName = "";
		fVersion = "";

		Leaf packageNode = node.getFirstChild();

		if (!packageNode.isType("package")) {
			return 0;
		}

		fVendorName = packageNode.getProperty("vendor");
		fPackName = packageNode.getProperty("name");

		if (packageNode.hasChildren()) {
			for (Leaf child : ((Node) packageNode).getChildren()) {

				if (child.isType("boards") && child.hasChildren()) {
					for (Leaf grandchild : ((Node) child).getChildren()) {
						if (grandchild.isType("board")) {
							processBoardNode(grandchild, parent);
						}
					}
				} else if (child.isType("releases") && child.hasChildren()) {

					Leaf versionNode = ((Node) child).getFirstChild();
					fVersion = versionNode.getProperty("version");
				}
			}
		}

		return fCount;
	}

	// ------------------------------------------------------------------------

	private void processFamilyNode(Leaf node, Node parent) {

		// Required
		String familyName = node.getProperty("Dfamily");
		String familyVendor = node.getProperty("Dvendor");

		String va[] = familyVendor.split("[:]");

		Node vendorNode = addUniqueVendor(parent, va[0], va[1]);

		Node familyNode = Node.addUniqueChild(vendorNode, Type.FAMILY, familyName);

		// The last encountered value one will be preserved
		// TODO: update vendor name from vendor id based on a conversion table
		familyNode.putProperty(Property.VENDOR_NAME, va[0]);
		familyNode.putProperty(Property.VENDOR_ID, va[1]);

		// Store all details to identify the pack where the device family
		// was defined.
		familyNode.putProperty(Property.PACK_VENDOR, fVendorName);
		familyNode.putProperty(Property.PACK_NAME, fPackName);
		familyNode.putProperty(Property.PACK_VERSION, fVersion);

		if (node.hasChildren()) {
			for (Leaf child : ((Node) node).getChildren()) {
				if (child.isType("subFamily")) {
					processSubFamilyNode(child, familyNode);
				} else if (child.isType("device")) {
					processDeviceNode(child, familyNode);
				} else {
					processDevicePropertiesGroup(child, familyNode);
				}
			}
		}
	}

	private void processSubFamilyNode(Leaf node, Node parent) {

		String subFamilyName = node.getProperty("DsubFamily");

		Node subFamilyNode = Node.addUniqueChild(parent, Type.SUBFAMILY, subFamilyName);

		if (node.hasChildren()) {
			for (Leaf child : ((Node) node).getChildren()) {
				if (child.isType("device")) {
					processDeviceNode(child, subFamilyNode);
				} else {
					processDevicePropertiesGroup(child, subFamilyNode);
				}
			}
		}
	}

	private void processDeviceNode(Leaf node, Node parent) {

		// Required
		String deviceName = node.getProperty("Dname");

		Node deviceNode = Node.addUniqueChild(parent, Type.DEVICE, deviceName);
		int saveCount = fCount;

		if (node.hasChildren()) {
			for (Leaf child : ((Node) node).getChildren()) {
				processDevicePropertiesGroup(child, deviceNode);
			}
		}

		if (fCount == saveCount) {
			fCount++; // If there were no variants, count this device
		}
	}

	private void processDevicePropertiesGroup(Leaf node, Node parent) {

		if (node.isType("processor")) {
			processProcessorNode(node, parent);
		} else if (node.isType("memory")) {
			processMemoryNode(node, parent);
		} else if (node.isType("compile")) {
			processCompileNode(node, parent);
		} else if (node.isType("book")) {
			processBookNode(node, parent);
		} else if (node.isType("debug")) {
			processDebugNode(node, parent);
		}
	}

	private void processProcessorNode(Leaf node, Node parent) {

		// Required
		String Dcore = node.getProperty("Dcore");
		String DcoreVersion = node.getProperty("DcoreVersion");
		String Dfpu = node.getProperty("Dfpu");
		String Dmpu = node.getProperty("Dmpu");
		String Dendian = node.getProperty("Dendian");
		String Dclock = node.getProperty("Dclock");

		parent.putNonEmptyProperty(Property.CORE, Dcore);
		parent.putNonEmptyProperty(Property.CORE_VERSION, DcoreVersion);
		parent.putNonEmptyProperty(Property.FPU, Dfpu);
		parent.putNonEmptyProperty(Property.MPU, Dmpu);
		parent.putNonEmptyProperty(Property.ENDIAN, Dendian);
		parent.putNonEmptyProperty(Property.CLOCK, Dclock);
	}

	private void processMemoryNode(Leaf node, Node parent) {

		// Deprecated in 1.4
		String id = node.getProperty("id");

		// Added to 1.4
		String nm = node.getProperty("name", id);
		String access = node.getProperty("access");
		String alias = node.getProperty("alias");

		// Either name or id required

		String start = node.getProperty("start");
		String size = node.getProperty("size");

		// -
		String Pname = node.getProperty("Pname");

		// Optional
		String startup = node.getProperty("startup");

		Leaf memoryNode = Leaf.addUniqueChild(parent, Type.MEMORY, nm);

		// memoryNode.putProperty(Property.ID, id);
		memoryNode.putProperty(Property.START, start);
		memoryNode.putProperty(Property.SIZE, size);

		memoryNode.putNonEmptyProperty(Property.PNAME, Pname);
		memoryNode.putNonEmptyProperty(Property.STARTUP, startup);

		memoryNode.putProperty(Property.ACCESS, access);
		memoryNode.putProperty(Property.ALIAS, alias);
	}

	private void processCompileNode(Leaf node, Node parent) {

		// Required
		String define = node.getProperty("define");

		parent.putNonEmptyProperty(Property.DEFINE, define);
	}

	private void processDebugNode(Leaf node, Node parent) {

		// Required
		String svd = node.getProperty("svd");

		String posixSvd = PdscUtils.updatePosixSeparators(svd);

		parent.putProperty(Property.SVD_FILE, posixSvd);
	}

	private void processBookNode(Leaf node, Node parent) {

		// Required
		String bookName = node.getProperty("name");
		String title = node.getProperty("title");

		// Optional
		String category = node.getProperty("category");

		Leaf bookNode = Leaf.addNewChild(parent, Type.BOOK);
		bookNode.setName(title);

		String posixName = updatePosixSeparators(bookName);
		if (bookName.startsWith("http://") || bookName.startsWith("https://") || bookName.startsWith("ftp://")) {
			bookNode.putNonEmptyProperty(Property.URL, bookName);
		} else {
			bookNode.putNonEmptyProperty(Property.FILE, posixName);
		}

		bookNode.putNonEmptyProperty(Property.CATEGORY, category);
	}

	private void processBoardNode(Leaf node, Node parent) {

		// Required
		String boardVendor = node.getProperty("vendor");
		String boardName = node.getProperty("name");

		// Optional
		String boardRevision = node.getProperty("revision");

		Node vendorNode = Node.addUniqueChild(parent, Type.VENDOR, boardVendor);

		Node boardNode = Node.addNewChild(vendorNode, Type.BOARD);

		boardNode.setName(boardName);

		boardNode.putNonEmptyProperty(Property.BOARD_REVISION, boardRevision);
		boardNode.putNonEmptyProperty(Property.VENDOR_NAME, boardVendor);

		// Store all details to identify the pack where the board
		// was defined.
		boardNode.putProperty(Property.PACK_VENDOR, fVendorName);
		boardNode.putProperty(Property.PACK_NAME, fPackName);
		boardNode.putProperty(Property.PACK_VERSION, fVersion);

		// Count the encountered boards
		fCount++;

		if (node.hasChildren()) {
			for (Leaf child : ((Node) node).getChildren()) {
				if (child.isType("mountedDevice")) {
					// Required
					String Dvendor = child.getProperty("Dvendor");

					// Optional
					String Dname = child.getProperty("Dname");

					if (Dname.length() > 0) {
						Node deviceNode = Node.addNewChild(boardNode, Type.DEVICE);
						deviceNode.setName(Dname);

						String va[] = Dvendor.split(":");
						deviceNode.putProperty(Property.VENDOR_NAME, va[0]);
						deviceNode.putProperty(Property.VENDOR_ID, va[1]);
					}
				} else if (child.isType("feature")) {
					processFeatureNode(child, boardNode);
				} else if (child.isType("book")) {
					processBookNode(child, boardNode);
				}
			}
		}

		// Clock can be set by processFeatureNode()
		String clock = boardNode.getProperty(Property.CLOCK);
		if (clock.length() > 0) {
			// Also add a property with the clcock value
			boardNode.putProperty(Property.CLOCK, clock);
		}
	}

	private void processFeatureNode(Leaf node, Node parent) {

		// Required
		String featureType = node.getProperty("type");

		// Optional
		String featureN = node.getProperty("n");

		// String featureM = el.getAttribute("m").trim();
		// String name = el.getAttribute("name").trim();
		// String Pname = el.getAttribute("Pname").trim();

		if ("XTAL".equals(featureType)) {
			parent.putNonEmptyProperty(Property.CLOCK, featureN);
		}
	}

	// ------------------------------------------------------------------------
}
