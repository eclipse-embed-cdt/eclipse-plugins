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

import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.core.Xml;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.Activator;

import java.util.List;

import org.w3c.dom.Element;

import com.github.zafarkhaja.semver.Version;

// Deprecated, parsing done via the intermediate tree with PdscTreeParser.

public class PdscParserForBuild extends PdscParser {

	// ------------------------------------------------------------------------

	private Version deviceSchemaSemVer;
	private Version boardSchemaSemVer;

	// ------------------------------------------------------------------------

	public PdscParserForBuild() {
		super();
	}

	// ------------------------------------------------------------------------

	// Deprecated
	public void _parseDevices(Node tree) {

		Element packageElement = fDocument.getDocumentElement();
		String firstElementName = packageElement.getNodeName();
		if (!"package".equals(firstElementName)) {
			Activator.log("Missing <package>, <" + firstElementName + "> encountered");
			return;
		}

		String schemaVersion = packageElement.getAttribute("schemaVersion").trim();
		deviceSchemaSemVer = Version.valueOf(schemaVersion);

		if (!PdscUtils.isSchemaValid(deviceSchemaSemVer)) {
			Activator.log("Unrecognised schema version " + schemaVersion);
			return;
		}

		List<Element> childElements = Xml.getChildrenElementsList(packageElement);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("devices".equals(elementName)) {
				List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("family".equals(elementName2)) {

						processFamilyElement(childElement2, tree);

					}
				}
			}
		}
	}

	private Node addUniqueVendor(Node parent, String vendorName, String vendorId) {

		if (parent.hasChildren()) {
			for (Leaf child : parent.getChildren()) {
				if (vendorId.equals(child.getProperty(Node.VENDORID_PROPERTY))) {
					return (Node) child;
				}
			}
		}

		Node vendor = Node.addNewChild(parent, Type.VENDOR);
		vendor.setName(vendorName);
		vendor.putProperty(Node.VENDORID_PROPERTY, vendorId);

		return vendor;
	}

	private void processFamilyElement(Element el, Node parent) {

		// Required
		String familyName = el.getAttribute("Dfamily").trim();
		String familyVendor = el.getAttribute("Dvendor").trim();

		String va[] = familyVendor.split("[:]");

		Node vendorNode = addUniqueVendor(parent, va[0], va[1]);

		Node familyNode = Node.addUniqueChild(vendorNode, Type.FAMILY, familyName);

		// The last encountered value one will be preserved
		// TODO: update vendor name from vendor id based on a conversion table
		familyNode.putProperty(Node.VENDOR_PROPERTY, va[0]);
		familyNode.putProperty(Node.VENDORID_PROPERTY, va[1]);

		List<Element> childElements = Xml.getChildrenElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("subFamily".equals(elementName)) {

				processSubFamilyElement(childElement, familyNode);

			} else if ("device".equals(elementName)) {

				processDeviceElement(childElement, familyNode);

			} else {

				processDevicePropertiesGroup(childElement, familyNode);
			}
		}

		familyNode.setDescription(processDeviceSummary(familyNode));
	}

	//
	private void processProcessorElement(Element el, Node parent) {

		String Dcore = el.getAttribute("Dcore").trim();
		String DcoreVersion = el.getAttribute("DcoreVersion").trim();
		String Dfpu = el.getAttribute("Dfpu").trim();
		String Dmpu = el.getAttribute("Dmpu").trim();
		String Dendian = el.getAttribute("Dendian").trim();
		String Dclock = el.getAttribute("Dclock").trim();

		parent.putNonEmptyProperty(Node.CORE_PROPERTY, Dcore);
		parent.putNonEmptyProperty(Node.VERSION_PROPERTY, DcoreVersion);
		parent.putNonEmptyProperty(Node.FPU_PROPERTY, Dfpu);
		parent.putNonEmptyProperty(Node.MPU_PROPERTY, Dmpu);
		parent.putNonEmptyProperty(Node.ENDIAN_PROPERTY, Dendian);
		parent.putNonEmptyProperty(Node.CLOCK_PROPERTY, Dclock);
	}

	private void processSubFamilyElement(Element el, Node parent) {

		String subFamilyName = el.getAttribute("DsubFamily").trim();

		Node subFamilyNode = Node.addUniqueChild(parent, Type.SUBFAMILY, subFamilyName);

		List<Element> childElements = Xml.getChildrenElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("device".equals(elementName)) {

				processDeviceElement(childElement, subFamilyNode);

			} else {

				processDevicePropertiesGroup(childElement, subFamilyNode);

			}
		}

		subFamilyNode.setDescription(processDeviceSummary(subFamilyNode));
	}

	private void processDeviceElement(Element el, Node parent) {

		// Required
		String deviceName = el.getAttribute("Dname").trim();

		Node deviceNode = Node.addUniqueChild(parent, Type.DEVICE, deviceName);

		List<Element> childElements = Xml.getChildrenElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("variant".equals(elementName)) {

				processVariantElement(childElement, deviceNode);

			} else {

				processDevicePropertiesGroup(childElement, deviceNode);
			}
		}
		deviceNode.setDescription(processDeviceSummary(deviceNode));
	}

	private void processVariantElement(Element el, Node parent) {

		// Required
		String variantName = el.getAttribute("Dvariant").trim();

		Node variantNode = Node.addUniqueChild(parent, Type.VARIANT, variantName);

		List<Element> childElements2 = Xml.getChildrenElementsList(el);
		for (Element childElement2 : childElements2) {

			processDevicePropertiesGroup(childElement2, variantNode);
		}

		variantNode.setDescription(processDeviceSummary(variantNode));
	}

	private void processDevicePropertiesGroup(Element el, Node parent) {

		String elementName = el.getNodeName();
		if ("processor".equals(elementName)) {

			processProcessorElement(el, parent);

		} else if ("memory".equals(elementName)) {

			processMemoryElement(el, parent);

		}
	}

	private void processMemoryElement(Element el, Node parent) {

		// Required
		String id = el.getAttribute("id").trim();
		String start = el.getAttribute("start").trim();
		String size = el.getAttribute("size").trim();

		// -
		String Pname = el.getAttribute("Pname").trim();

		// Optional
		String startup = el.getAttribute("startup").trim();
		String init = el.getAttribute("init").trim();
		String defa = el.getAttribute("default").trim();

		Leaf memoryNode = Leaf.addUniqueChild(parent, Type.MEMORY, id);

		// memoryNode.putProperty(Node.ID_PROPERTY, id);
		memoryNode.putProperty(Node.START_PROPERTY, start);
		memoryNode.putProperty(Node.SIZE_PROPERTY, size);

		memoryNode.putNonEmptyProperty(Node.PNAME_PROPERTY, Pname);
		memoryNode.putNonEmptyProperty(Node.STARTUP_PROPERTY, startup);
		memoryNode.putNonEmptyProperty(Node.INIT_PROPERTY, init);
		memoryNode.putNonEmptyProperty(Node.DEFAULT_PROPERTY, defa);
	}

	// ------------------------------------------------------------------------

	// Deprecated
	public void _parseBoards(Node tree) {

		Element packageElement = fDocument.getDocumentElement();
		String firstElementName = packageElement.getNodeName();
		if (!"package".equals(firstElementName)) {
			Activator.log("Missing <packages>, <" + firstElementName + "> encountered");
			return;
		}

		String schemaVersion = packageElement.getAttribute("schemaVersion").trim();
		boardSchemaSemVer = Version.valueOf(schemaVersion);

		if (!PdscUtils.isSchemaValid(boardSchemaSemVer)) {
			Activator.log("Unrecognised schema version " + schemaVersion);
			return;
		}

		List<Element> childElements = Xml.getChildrenElementsList(packageElement);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("boards".equals(elementName)) {
				List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("board".equals(elementName2)) {

						processBoardElement(childElement2, tree);

					}
				}
			}
		}
	}

	private void processBoardElement(Element el, Node parent) {

		// Required
		String boardVendor = el.getAttribute("vendor").trim();
		String boardName = el.getAttribute("name").trim();

		// Optional
		String boardRevision = el.getAttribute("revision").trim();

		Node vendorNode = Node.addUniqueChild(parent, Type.VENDOR, boardVendor);

		String name = boardName;
		if (boardRevision.length() > 0) {
			name += " (" + boardRevision + ")";
		}

		Node boardNode = Node.addUniqueChild(vendorNode, Type.BOARD, name);

		List<Element> childElements = Xml.getChildrenElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();

			if ("mountedDevice".equals(elementName)) {

				// Required
				String Dvendor = childElement.getAttribute("Dvendor").trim();

				// Optional
				String Dname = childElement.getAttribute("Dname").trim();

				if (Dname.length() > 0) {
					Node deviceNode = Node.addNewChild(boardNode, Type.DEVICE);
					deviceNode.setName(Dname);

					String va[] = Dvendor.split(":");
					deviceNode.putProperty(Node.VENDOR_PROPERTY, va[0]);
					deviceNode.putProperty(Node.VENDORID_PROPERTY, va[1]);
				}

			} else if ("feature".equals(elementName)) {

				// <xs:element name="feature" type="BoardFeatureType"
				// maxOccurs="unbounded"></xs:element>

				processFeatureElement(childElement, boardNode);

			}
		}

		String prefix = "Board";
		String summary = "";
		String clock = boardNode.getProperty(Node.CLOCK_PROPERTY);
		if (clock.length() > 0) {
			try {
				int clockMHz = Integer.parseInt(clock) / 1000000;
				if (summary.length() > 0) {
					summary += ", ";
				}
				summary += String.valueOf(clockMHz) + " MHz XTAL";
			} catch (NumberFormatException e) {
				// Ignore not number
			}
		}
		if (summary.length() > 0) {
			summary = prefix + " (" + summary + ")";
		} else {
			summary = prefix;
		}
		boardNode.setDescription(summary);
	}

	private void processFeatureElement(Element el, Node parent) {

		// Required
		String featureType = el.getAttribute("type").trim();

		// Optional
		String featureN = el.getAttribute("n").trim();

		// String featureM = el.getAttribute("m").trim();
		// String name = el.getAttribute("name").trim();
		// String Pname = el.getAttribute("Pname").trim();

		if ("XTAL".equals(featureType)) {
			parent.putNonEmptyProperty(Node.CLOCK_PROPERTY, featureN);
		}
	}

	// ------------------------------------------------------------------------

	// Compose the details string for device nodes
	protected String processDeviceSummary(Node deviceNode) {

		String prefix = "";
		if (deviceNode.isType(Type.FAMILY)) {
			prefix = "Family";
		} else if (deviceNode.isType(Type.SUBFAMILY)) {
			prefix = "Subfamily";
		} else if (deviceNode.isType(Type.DEVICE)) {
			prefix = "Device";
		} else if (deviceNode.isType(Type.VARIANT)) {
			prefix = "Variant";
		}

		String summary = "";
		String core = deviceNode.getProperty(Node.CORE_PROPERTY);
		if (core.length() > 0) {
			if (summary.length() > 0) {
				summary += ", ";
			}
			summary += core;

			String version = deviceNode.getProperty(Node.VERSION_PROPERTY);
			if (version.length() > 0) {
				if (summary.length() > 0) {
					summary += ", ";
				}
				summary += version;
			}
		}

		String fpu = deviceNode.getProperty(Node.FPU_PROPERTY);
		if (fpu.length() > 0 && "1".equals(fpu)) {
			if (summary.length() > 0) {
				summary += ", ";
			}
			summary += "FPU";
		}

		String mpu = deviceNode.getProperty(Node.MPU_PROPERTY);
		if (mpu.length() > 0 && "1".equals(mpu)) {
			if (summary.length() > 0) {
				summary += ", ";
			}
			summary += "MPU";
		}

		String clock = deviceNode.getProperty(Node.CLOCK_PROPERTY);
		if (clock.length() > 0) {
			try {
				int clockMHz = Integer.parseInt(clock) / 1000000;
				if (summary.length() > 0) {
					summary += ", ";
				}
				summary += String.valueOf(clockMHz) + " MHz";
			} catch (NumberFormatException e) {
				// Ignore not number
			}
		}

		int ramKB = 0;
		int romKB = 0;

		// TODO: iterate on parents too
		if (deviceNode.hasChildren()) {
			for (Leaf childNode : deviceNode.getChildren()) {

				if (Type.MEMORY.equals(childNode.getType())) {
					String size = childNode.getProperty(Node.SIZE_PROPERTY);
					long sizeKB = StringUtils.convertHexLong(size) / 1024;

					String id = childNode.getName();
					if (id.contains("ROM")) {
						romKB += sizeKB;
					} else if (id.contains("RAM")) {
						ramKB += sizeKB;
					} else {
					}
				}
			}
		}

		if (ramKB > 0) {
			if (summary.length() > 0) {
				summary += ", ";
			}

			summary += String.valueOf(ramKB) + " kB RAM";
		}

		if (romKB > 0) {
			if (summary.length() > 0) {
				summary += ", ";
			}

			summary += String.valueOf(romKB) + " kB ROM";
		}

		if (summary.length() > 0) {
			summary = prefix + " (" + summary + ")";
		} else {
			summary = prefix;
		}

		return summary;
	}

	// ------------------------------------------------------------------------

}
