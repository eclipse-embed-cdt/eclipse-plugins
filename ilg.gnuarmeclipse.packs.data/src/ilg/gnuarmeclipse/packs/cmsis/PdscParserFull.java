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
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.Activator;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.github.zafarkhaja.semver.Version;

public class PdscParserFull extends PdscParser {

	private boolean fIsBrief;

	public PdscParserFull() {

		super();

		fIsBrief = false;
	}

	public void setIsBrief(boolean brief) {
		fIsBrief = brief;
	}

	public boolean isBrief() {
		return fIsBrief;
	}

	public Node parsePdscFull() throws ParserConfigurationException, SAXException, IOException {

		Node tree = new Node(Type.OUTLINE);
		tree.setName("Full Outline");
		tree.putProperty(Node.FOLDER_PROPERTY, fPath.removeLastSegments(1).toString());

		Element packageElement = fDocument.getDocumentElement();
		String firstElementName = packageElement.getNodeName();
		if (!"package".equals(firstElementName)) {
			Activator.log("Missing <packages>, <" + firstElementName + "> encountered");
			return tree;
		}

		String schemaVersion = packageElement.getAttribute("schemaVersion").trim();
		Version semVer = Version.valueOf(schemaVersion);

		if (!PdscUtils.isSchemaValid(semVer)) {
			Activator.log("Unrecognised schema version " + schemaVersion);
			return tree;
		}

		Node packNode = new Node(Type.PACKAGE);
		tree.addChild(packNode);

		packNode.putProperty(Node.SCHEMA_PROPERTY, schemaVersion);

		String packDescription = "";

		List<Element> childElements = Xml.getChildrenElementsList(packageElement);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("vendor".equals(elementName)) {

				String vendorName = Xml.getElementContent(childElement);
				packNode.putProperty(Node.VENDOR_PROPERTY, vendorName);

			} else if ("name".equals(elementName)) {

				String packName = Xml.getElementContent(childElement);
				packNode.setName(packName);
				packNode.putProperty(Node.NAME_PROPERTY, packName);

				packDescription = "Package: " + packName;

			} else if ("description".equals(elementName)) {

				// Warning: must be located after <name>
				packDescription = extendDescription(packDescription, Xml.getElementContent(childElement));

			} else if ("url".equals(elementName)) {

				String url = Xml.getElementContent(childElement);
				packNode.putNonEmptyProperty(Node.URL_PROPERTY, url);

				packDescription = extendDescription(packDescription, "url", url);

			} else if ("license".equals(elementName)) {

				String license = Xml.getElementContent(childElement);
				license = updatePosixSeparators(license);
				packNode.putNonEmptyProperty(Node.LICENSE_PROPERTY, license);

				packDescription = extendDescription(packDescription, "license", license);

			} else if ("releases".equals(elementName)) {

				List<Element> releaseElements = Xml.getChildrenElementsList(childElement, "release");
				for (Element releaseElement : releaseElements) {

					String releaseVersion = releaseElement.getAttribute("version").trim();
					// Optional
					String releaseDate = releaseElement.getAttribute("date").trim();

					String description;
					description = "Version: " + releaseVersion;
					if (releaseDate.length() > 0) {
						description += ", from " + releaseDate;
					}
					description = extendDescription(description, Xml.getElementContent(releaseElement));

					Node versionNode = new Node(Type.VERSION);
					versionNode.setName(releaseVersion);
					versionNode.putProperty(Node.NAME_PROPERTY, releaseVersion);
					versionNode.putNonEmptyProperty(Node.DATE_PROPERTY, releaseDate);

					versionNode.setDescription(description);

					tree.addChild(versionNode);
				}

			} else if ("keywords".equals(elementName)) {

				List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("keyword".equals(elementName2)) {

						processKeywordElement(childElement2, tree);

					} else {
						Activator.log("Not processed <" + elementName2 + ">");
					}
				}

			} else if ("devices".equals(elementName)) {

				// TreeNode devicesNode = new TreeNode(Type.DEVICES);
				// tree.addChild(devicesNode);

				List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("family".equals(elementName2)) {

						processFamilyElement(childElement2, tree);

					} else {
						Activator.log("Not processed <" + elementName2 + ">");
					}
				}

			} else if ("boards".equals(elementName)) {

				List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("board".equals(elementName2)) {

						processBoardElement(childElement2, tree);

					} else {
						Activator.log("Not processed <" + elementName2 + ">");
					}
				}

			} else if ("conditions".equals(elementName)) {

				List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("condition".equals(elementName2)) {

						processConditionElement(childElement2, tree);

					} else {
						Activator.log("Not processed <" + elementName2 + ">");
					}
				}

			} else if ("examples".equals(elementName)) {

				List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("example".equals(elementName2)) {

						processExampleElement(childElement2, tree, false);

					} else {
						Activator.log("Not processed <" + elementName2 + ">");
					}
				}

			} else if ("components".equals(elementName)) {

				List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("component".equals(elementName2)) {

						processComponentElement(childElement2, tree, "");

					} else if ("bundle".equals(elementName2)) {

						processBundleElement(childElement2, tree);

					} else {
						Activator.log("Not processed <" + elementName2 + ">");
					}
				}

			} else if ("apis".equals(elementName)) {

				// <xs:complexType name="ApisType">
				// <xs:sequence>
				// <xs:element name="api" type="ApiType" minOccurs="1"
				// maxOccurs="unbounded"/>
				// </xs:sequence>
				// </xs:complexType>

				List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("api".equals(elementName2)) {

						processApiElement(childElement2, tree);

					} else {
						Activator.log("Not processed <" + elementName2 + ">");
					}
				}

			} else if ("taxonomy".equals(elementName)) {

				List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("description".equals(elementName2)) {

						processTaxonomyDescription(childElement2, tree);

					} else {
						Activator.log("Not processed <" + elementName2 + ">");
					}
				}
				// } else if ("generators".equals(elementName)) {

				// Ignore

			} else {
				Activator.log("Not processed <" + elementName + ">");
			}
		}

		packDescription = extendDescription(packDescription, "schema", schemaVersion);
		packNode.setDescription(packDescription);

		return tree;
	}

	// Called from ParsePdscJob, to add example nodes below version
	public void parseExamples(Node parent) {

		Element packageElement = fDocument.getDocumentElement();

		Element examplesElement = Xml.getFirstChildElement(packageElement, "examples");
		if (examplesElement != null) {

			// Enumerate <example> in *.pdsc.
			List<Element> exampleElements = Xml.getChildrenElementsList(examplesElement, "example");
			for (Element exampleElement : exampleElements) {
				String exampleName = exampleElement.getAttribute("name").trim();

				Element boardElement = Xml.getFirstChildElement(exampleElement, "board");

				// Example names are not unique, add the first board to
				// help identify them.
				String boardName;
				boardName = boardElement.getAttribute("name");
				if (boardName.length() > 0) {
					exampleName = exampleName + " (" + boardName + ")";
				}

				// Examples must be PackNode, to accomodate outlines.
				PackNode exampleNode;
				exampleNode = (PackNode) parent.findChild(Type.EXAMPLE, exampleName);
				if (exampleNode == null) {
					exampleNode = new PackNode(Type.EXAMPLE);
					parent.addChild(exampleNode);
					exampleNode.setName(exampleName);
				}

				Element descriptionElement = Xml.getFirstChildElement(exampleElement, "description");
				String description;
				description = Xml.getElementContent(descriptionElement);
				exampleNode.setDescription(description);

				Node outlineNode = new Node(Type.OUTLINE);
				exampleNode.setOutline(outlineNode);

				// Parse a flat outline
				processExampleElement(exampleElement, outlineNode, true);
			}
		}
	}

	// ------------------------------------------------------------------------

	// <!-- Family Level begin -->
	// <xs:element name="family" maxOccurs="unbounded">
	// <xs:complexType>
	// <xs:sequence>
	// <xs:group ref="DevicePropertiesGroup"/>
	// <xs:element name="device" type="DeviceType" minOccurs="0"
	// maxOccurs="unbounded"/>
	// <!-- Sub Family Level begin-->
	// <xs:element name="subFamily" minOccurs="0" maxOccurs="unbounded">
	// <xs:complexType>
	// <xs:sequence>
	// <xs:group ref="DevicePropertiesGroup"/>
	// <!-- Device Level begin-->
	// <xs:element name="device" type="DeviceType" maxOccurs="unbounded"/>
	// <!-- Device Level end -->
	// </xs:sequence>
	// <xs:attribute name="DsubFamily" type="xs:string" use="required"/>
	// </xs:complexType>
	// </xs:element>
	// <!-- Sub Family Level end -->
	// </xs:sequence>
	// <xs:attribute name="Dfamily" type="xs:string" use="required"/>
	// <xs:attribute name="Dvendor" type="DeviceVendorEnum" use="required"/>
	// </xs:complexType>
	// </xs:element>
	// <!-- Family Level end -->

	private void processFamilyElement(Element el, Node parent) {

		// Required
		String familyName = el.getAttribute("Dfamily").trim();
		String familyVendor = el.getAttribute("Dvendor").trim();

		String va[] = familyVendor.split("[:]");

		Node familyNode = new Node(Type.FAMILY);
		parent.addChild(familyNode);

		familyNode.setName(familyName + " (family)");

		familyNode.putProperty(Node.NAME_PROPERTY, familyName);
		familyNode.putProperty(Node.VENDOR_PROPERTY, va[0]);
		familyNode.putProperty(Node.VENDORID_PROPERTY, va[1]);

		String familyDescription = "Device family: " + familyName;

		List<Element> childElements = Xml.getChildrenElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();

			if (isBrief()) {
				if ("description".equals(elementName)) {

					familyDescription = extendDescription(familyDescription, Xml.getElementContent(childElement));

				}
			} else {
				if ("description".equals(elementName)) {

					familyDescription = extendDescription(familyDescription, Xml.getElementContent(childElement));

				} else if ("subFamily".equals(elementName)) {

					processSubFamilyElement(childElement, familyNode);

				} else if ("device".equals(elementName)) {

					processDeviceElement(childElement, familyNode);

				} else {

					processDevicePropertiesGroup(childElement, familyNode);

				}
			}
		}

		familyNode.setDescription(familyDescription);
	}

	// <!-- Processor Type -->
	// <xs:complexType name="ProcessorType">
	// <!-- Pname defines an identifier for a specific processor in a
	// multi-processor devices -->
	// <xs:attribute name="Pname" type="xs:string"/>
	// <!-- Dcore specifies the processor from a list of supported processors
	// -->
	// <xs:attribute name="Dcore" type="DcoreEnum"/>
	// <!-- Dfpu specifies the hardware floating point unit -->
	// <xs:attribute name="Dfpu" type="DfpuEnum"/>
	// <!-- Dmpu specifies the memory protection unit -->
	// <xs:attribute name="Dmpu" type="DmpuEnum"/>
	// <!-- Dendian specifies the endianess supported by the processor -->
	// <xs:attribute name="Dendian" type="DendianEnum"/>
	// <!-- Dclock specifies the maximum core clock frequency -->
	// <xs:attribute name="Dclock" type="xs:unsignedInt"/>
	// <!-- DcoreVersion specifies the revision of the processor -->
	// <xs:attribute name="DcoreVersion" type="xs:string"/>
	// </xs:complexType>

	private void processProcessorElement(Element el, Node parent) {

		String Dcore = el.getAttribute("Dcore").trim();
		String DcoreVersion = el.getAttribute("DcoreVersion").trim();
		String Dfpu = el.getAttribute("Dfpu").trim();
		String Dmpu = el.getAttribute("Dmpu").trim();
		String Dendian = el.getAttribute("Dendian").trim();
		String Dclock = el.getAttribute("Dclock").trim();

		Node processorNode = new Node(Type.PROCESSOR);
		parent.addChild(processorNode);

		String name;
		if (Dcore.length() > 0) {
			name = Dcore;
		} else if (Dclock.length() > 0) {
			name = Dclock;
		} else {
			name = "Processor";
		}
		processorNode.setName(name);

		processorNode.putNonEmptyProperty(Node.CORE_PROPERTY, Dcore);
		processorNode.putNonEmptyProperty(Node.VERSION_PROPERTY, DcoreVersion);
		processorNode.putNonEmptyProperty(Node.FPU_PROPERTY, Dfpu);
		processorNode.putNonEmptyProperty(Node.MPU_PROPERTY, Dmpu);
		processorNode.putNonEmptyProperty(Node.ENDIAN_PROPERTY, Dendian);
		processorNode.putNonEmptyProperty(Node.CLOCK_PROPERTY, Dclock);

		String description = "Processor";
		description = extendDescription(description, "Dcore", Dcore);
		description = extendDescription(description, "DcoreVersion", DcoreVersion);
		description = extendDescription(description, "Dfpu", Dfpu);
		description = extendDescription(description, "Dmpu", Dmpu);
		description = extendDescription(description, "Dendian", Dendian);
		description = extendDescription(description, "Dclock", Dclock);

		processorNode.setDescription(description);
	}

	// <!-- Board Feature Type -->
	// <xs:complexType name="BoardFeatureType">
	// <xs:attribute name="type" type="BoardFeatureTypeEnum" use="required"/>
	// <xs:attribute name="n" type="xs:decimal" use="optional"/>
	// <xs:attribute name="m" type="xs:decimal" use="optional"/>
	// <xs:attribute name="name" type="xs:string" use="optional"/>
	// </xs:complexType>

	// <!-- Device Feature Type -->
	// <xs:complexType name="DeviceFeatureType">
	// <xs:attribute name="Pname" type="xs:string" use="optional"/>
	// <xs:attribute name="type" type="DeviceFeatureTypeEnum" use="required"/>
	// <xs:attribute name="n" type="xs:decimal" use="optional"/>
	// <xs:attribute name="m" type="xs:decimal" use="optional"/>
	// <xs:attribute name="name" type="xs:string" use="optional"/>
	// <!-- deprecated, only for backwards compatibility -->
	// <xs:attribute name="count" type="xs:int" use="optional"/>
	// </xs:complexType>

	private void processFeatureElement(Element el, Node parent) {

		// Required
		String featureType = el.getAttribute("type").trim();

		// Optional
		String featureN = el.getAttribute("n").trim();
		String featureM = el.getAttribute("m").trim();
		String name = el.getAttribute("name").trim();
		String Pname = el.getAttribute("Pname").trim();

		Node featureNode = new Node(Type.FEATURE);
		parent.addChild(featureNode);

		featureNode.setName(featureType);
		featureNode.putNonEmptyProperty(Node.NAME_PROPERTY, name);
		featureNode.putNonEmptyProperty(Node.N_PROPERTY, featureN);
		featureNode.putNonEmptyProperty(Node.M_PROPERTY, featureM);
		featureNode.putNonEmptyProperty(Node.PNAME_PROPERTY, Pname);

		String description = "Feature";
		if (name.length() > 0) {
			description += ": " + name;
		}
		description = extendDescription(description, "type", featureType);
		description = extendDescription(description, "n", featureN);
		description = extendDescription(description, "m", featureM);
		description = extendDescription(description, "Pname", Pname);

		featureNode.setDescription(description);
	}

	// <xs:complexType name="BoardsBookType">
	// <xs:attribute name="category" type="BoardBookCategoryEnum"/>
	// <xs:attribute name="name" type="xs:string"/>
	// <xs:attribute name="title" type="xs:string"/>
	// </xs:complexType>

	private void processBookElement(Element el, Node parent) {

		String bookName = el.getAttribute("name").trim();

		String bookTitle = el.getAttribute("title").trim();
		String category = el.getAttribute("category").trim();

		Node bookNode = new Node(Type.BOOK);
		parent.addChild(bookNode);

		bookNode.setName(bookTitle);

		String description = "Book: " + bookTitle;

		String posixName = updatePosixSeparators(bookName);

		// Instead of NAME_PROPERTY we store URL or FILE
		if (bookName.startsWith("http://") || bookName.startsWith("https://") || bookName.startsWith("ftp://")) {
			bookNode.putNonEmptyProperty(Node.URL_PROPERTY, bookName);
			description = extendDescription(description, "url", bookName);
		} else {
			bookNode.putNonEmptyProperty(Node.FILE_PROPERTY, posixName);
			description = extendDescription(description, "file", posixName);
		}

		bookNode.putNonEmptyProperty(Node.TITLE_PROPERTY, bookTitle);
		bookNode.putNonEmptyProperty(Node.CATEGORY_PROPERTY, category);

		description = extendDescription(description, "category", category);

		bookNode.setDescription(description);

	}

	// <xs:element name="subFamily" minOccurs="0" maxOccurs="unbounded">
	// <xs:complexType>
	// <xs:sequence>
	// <xs:group ref="DevicePropertiesGroup"/>
	// <!-- Device Level begin-->
	// <xs:element name="device" type="DeviceType" maxOccurs="unbounded"/>
	// <!-- Device Level end -->
	// </xs:sequence>
	// <xs:attribute name="DsubFamily" type="xs:string" use="required"/>
	// </xs:complexType>
	// </xs:element>

	private void processSubFamilyElement(Element el, Node parent) {

		String subFamilyName = el.getAttribute("DsubFamily").trim();

		Node subFamilyNode = new Node(Type.SUBFAMILY);
		parent.addChild(subFamilyNode);

		subFamilyNode.setName(subFamilyName + " (subfamily)");
		subFamilyNode.putProperty(Node.NAME_PROPERTY, subFamilyName);

		String description = "Device subfamily: " + subFamilyName;

		List<Element> childElements = Xml.getChildrenElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {

				description = extendDescription(description, Xml.getElementContent(childElement));

			} else if ("device".equals(elementName)) {

				processDeviceElement(childElement, subFamilyNode);

			} else {

				processDevicePropertiesGroup(childElement, subFamilyNode);

			}
		}

		subFamilyNode.setDescription(description);
	}

	// <xs:complexType name="DeviceType">
	// <xs:sequence>
	// <xs:group ref="DevicePropertiesGroup"/>
	// <!-- Variant Level begin-->
	// <xs:element name="variant" minOccurs="0" maxOccurs="unbounded">
	// <xs:complexType>
	// <xs:sequence>
	// <xs:group ref="DevicePropertiesGroup"/>
	// </xs:sequence>
	// <xs:attribute name="Dvariant" type="xs:string" use="required"/>
	// </xs:complexType>
	// </xs:element>
	// <!-- Variant Level end -->
	// </xs:sequence>
	// <xs:attribute name="Dname" type="xs:string" use="required"/>
	// <!-- <xs:attributeGroup ref="DefaultDeviceAttributesGroup"/> -->
	// </xs:complexType>

	private void processDeviceElement(Element el, Node parent) {

		// Required
		String deviceName = el.getAttribute("Dname").trim();

		Node deviceNode = new Node(Type.DEVICE);
		parent.addChild(deviceNode);

		deviceNode.setName(deviceName + " (device)");
		deviceNode.putProperty(Node.NAME_PROPERTY, deviceName);

		String descriptionContent = "";

		List<Element> childElements = Xml.getChildrenElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {

				descriptionContent = Xml.getElementContent(childElement);

			} else if ("variant".equals(elementName)) {

				// Required
				String variantName = childElement.getAttribute("Dvariant").trim();

				Node variantNode = new Node(Type.VARIANT);
				deviceNode.addChild(variantNode);

				variantNode.setName(variantName + " (variant)");
				variantNode.putProperty(Node.NAME_PROPERTY, variantName);

				String variantDescriptionContent = "Device variant: " + variantName;

				List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("description".equals(elementName2)) {

						variantDescriptionContent = extendDescription(variantDescriptionContent,
								Xml.getElementContent(childElement));
					} else {
						processDevicePropertiesGroup(childElement2, variantNode);
					}
				}

				variantNode.setDescription(variantDescriptionContent);

			} else {

				processDevicePropertiesGroup(childElement, deviceNode);
			}
		}

		String deviceDescription = "Device: " + deviceName;

		int ramKB = 0;
		int romKB = 0;

		if (deviceNode.hasChildren()) {
			for (Leaf childNode : deviceNode.getChildren()) {

				if (Type.MEMORY.equals(childNode.getType())) {
					String size = childNode.getProperty(Node.SIZE_PROPERTY);
					long sizeKB = StringUtils.convertHexLong(size) / 1024;

					String id = childNode.getProperty(Node.ID_PROPERTY);
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
			if (deviceDescription.length() > 0) {
				deviceDescription += ", ";
			}

			deviceDescription += String.valueOf(ramKB) + " kB RAM";
		}

		if (romKB > 0) {
			if (deviceDescription.length() > 0) {
				deviceDescription += ", ";
			}

			deviceDescription += String.valueOf(romKB) + " kB ROM";
		}

		deviceDescription = extendDescription(deviceDescription, descriptionContent);

		deviceNode.setDescription(deviceDescription);
	}

	// <!-- Device Properties Group -->
	// <xs:group name="DevicePropertiesGroup">
	// <!-- multi-core devices have unique Pname attribute. One entry per
	// processor and level -->
	// <xs:sequence>
	// <xs:element name="processor" type="ProcessorType" minOccurs="0"
	// maxOccurs="unbounded"/>
	// <xs:element name="jtagconfig" type="JtagConfigType" minOccurs="0"
	// maxOccurs="1"/>
	// <xs:element name="swdconfig" type="SwdConfigType" minOccurs="0"
	// maxOccurs="1"/>
	// <xs:group ref="DefaultDevicePropertiesGroup" minOccurs="0"
	// maxOccurs="unbounded"/>
	// </xs:sequence>
	// </xs:group>

	// <!-- Default Device Properties Group -->
	// <xs:group name="DefaultDevicePropertiesGroup">
	// <!-- multi-core devices have unique Pname attribute. One entry per
	// processor and level -->
	// <xs:choice>
	// <xs:element name="compile" type="CompileType" />
	// <xs:element name="memory" type="MemoryType" />
	// <xs:element name="algorithm" type="AlgorithmType" />
	// <xs:element name="book" type="BookType" />
	// <xs:element name="description" type="DescriptionType" />
	// <xs:element name="feature" type="DeviceFeatureType" />
	// <xs:element name="debugport" type="DebugPortType" />
	// <xs:element name="debug" type="DebugType" />
	// <xs:element name="trace" type="TraceType" />
	// <xs:element name="environment" type="EnvironmentType" />
	// </xs:choice>
	// </xs:group>

	private void processDevicePropertiesGroup(Element el, Node parent) {

		String elementName = el.getNodeName();
		if ("processor".equals(elementName)) {

			processProcessorElement(el, parent);

			// } else if ("jtagconfig".equals(elementName)) {
			// } else if ("swdconfig".equals(elementName)) {
		} else if ("compile".equals(elementName)) {

			// <!-- Compile Type: -->
			// <xs:complexType name="CompileType">
			// <!-- Pname identifies the processor this setting belongs to -->
			// <xs:attribute name="Pname" type="xs:string" use="optional"/>
			// <!-- CMSIS-CORE device header file (sets compiler include path)
			// -->
			// <xs:attribute name="header" type="xs:string"/>
			// <!-- Device specific preprocessor define (sets preprocessor
			// define -->
			// <xs:attribute name="define" type="xs:string"/>
			// </xs:complexType>

			String header = el.getAttribute("header").trim();
			String define = el.getAttribute("define").trim();

			String Pname = el.getAttribute("Pname").trim();
			if (Pname.length() > 0) {
				Activator.log("Pname not processed " + Pname + "");
			}

			Node headerNode = new Node(Type.HEADER);
			parent.addChild(headerNode);

			String posixHeader = updatePosixSeparators(header);
			String va[] = posixHeader.split("/");

			headerNode.setName(va[va.length - 1]);
			headerNode.setDescription("Header file: " + posixHeader);
			headerNode.putProperty(Node.FILE_PROPERTY, posixHeader);
			headerNode.putNonEmptyProperty(Node.DEFINE_PROPERTY, define);

			if (define.length() > 0) {
				Node defineNode = new Node(Type.DEFINE);
				parent.addChild(defineNode);

				defineNode.setName(define);
				defineNode.setDescription("Macro definition");
			}

		} else if ("memory".equals(elementName)) {

			// <!-- Memory Type-->
			// <xs:complexType name="MemoryType">
			// <!-- Pname identifies the processor this setting belongs to -->
			// <xs:attribute name="Pname" type="xs:string"/>
			// <!-- id specifies the enumerated ID of memory -->
			// <xs:attribute name="id" type="MemoryIDTypeEnum" use="required"/>
			// <!-- start specifies the base address of the memory -->
			// <xs:attribute name="start" type="NonNegativeInteger"
			// use="required"/>
			// <!-- size specifies the size of the memory -->
			// <xs:attribute name="size" type="NonNegativeInteger"
			// use="required"/>
			// <!-- specifies whether the memory shall be initialized -->
			// <xs:attribute name="init" type="xs:boolean" use="optional"
			// default="0"/>
			// <!-- specifies whether the memory is used as default by linker
			// -->
			// <xs:attribute name="default" type="xs:boolean" use="optional"
			// default="0"/>
			// <!-- specifies whether the memory shall be used for the startup
			// by linker -->
			// <xs:attribute name="startup" type="xs:boolean" use="optional"
			// default="0"/>
			// </xs:complexType>

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

			Node memoryNode = new Node(Type.MEMORY);
			parent.addChild(memoryNode);

			memoryNode.setName(id);

			memoryNode.putProperty(Node.ID_PROPERTY, id);
			memoryNode.putProperty(Node.START_PROPERTY, start);
			memoryNode.putProperty(Node.SIZE_PROPERTY, size);

			memoryNode.putNonEmptyProperty(Node.PNAME_PROPERTY, Pname);
			memoryNode.putNonEmptyProperty(Node.STARTUP_PROPERTY, startup);
			memoryNode.putNonEmptyProperty(Node.INIT_PROPERTY, init);
			memoryNode.putNonEmptyProperty(Node.DEFAULT_PROPERTY, defa);

			String description;
			if (id.contains("ROM")) {
				description = "ROM area";
			} else if (id.contains("RAM")) {
				description = "RAM area";
			} else {
				description = "Memory";
			}

			description = extendDescription(description, "id", id);
			description = extendDescription(description, "start", start);
			description = extendDescription(description, "size", size);
			description = extendDescription(description, "startup", startup);
			description = extendDescription(description, "init", init);
			description = extendDescription(description, "default", defa);
			description = extendDescription(description, "Pname", Pname);
			memoryNode.setDescription(description);

		} else if ("algorithm".equals(elementName)) {

			// Ignore

		} else if ("book".equals(elementName)) {

			processBookElement(el, parent);

		} else if ("feature".equals(elementName)) {

			processFeatureElement(el, parent);

			// } else if ("debugport".equals(elementName)) {

		} else if ("debug".equals(elementName)) {

			// <!-- DebugType -->
			// <xs:complexType name="DebugType">
			// <xs:sequence>
			// <xs:element name="datapatch" type="DataPatchType" minOccurs="0"
			// maxOccurs="unbounded"/>
			// <xs:element name="sequence" type="SequenceType" minOccurs="0"
			// maxOccurs="unbounded"/>
			// </xs:sequence>
			// <xs:attribute name="Pname" type="xs:string" use="optional"/>
			// <xs:attribute name="dp" type="xs:string" use="optional"/>
			// <xs:attribute name="ap" type="xs:unsignedInt" use="optional"/>
			// <!-- access port index -->
			// <xs:attribute name="svd" type="xs:string" use="optional"/>
			// </xs:complexType>

			String Pname = el.getAttribute("Pname").trim();
			String dp = el.getAttribute("dp").trim();
			String ap = el.getAttribute("ap").trim();
			String svd = el.getAttribute("svd").trim();

			String posixSvd = updatePosixSeparators(svd);
			String va[] = posixSvd.split("/");

			Node debugNode = new Node(Type.DEBUG);
			parent.addChild(debugNode);

			debugNode.setName(va[va.length - 1]);

			debugNode.putProperty(Node.FILE_PROPERTY, posixSvd);
			debugNode.putNonEmptyProperty(Node.PNAME_PROPERTY, Pname);
			debugNode.putNonEmptyProperty(Node.DP_PROPERTY, dp);
			debugNode.putNonEmptyProperty(Node.AP_PROPERTY, ap);

			String description = "Debug";
			description = extendDescription(description, "Pname", Pname);
			description = extendDescription(description, "dp", dp);
			description = extendDescription(description, "ap", ap);
			description = extendDescription(description, "svd", posixSvd);

			debugNode.setDescription(description);

			// } else if ("trace".equals(elementName)) {

			// } else if ("environment".equals(elementName)) {

		} else {
			Activator.log("Not processed <" + elementName + ">");
		}
	}

	// <xs:complexType name="BoardType">
	// <xs:sequence>
	// <xs:group ref="BoardElementsGroup" minOccurs="0" maxOccurs="unbounded"/>
	// </xs:sequence>
	// <xs:attribute name="vendor" type="xs:string" use="required"/>
	// <xs:attribute name="name" type="xs:string" use="required"/>
	// <xs:attribute name="revision" type="xs:string" use="optional"/>
	// <xs:attribute name="salesContact" type="xs:string" use="optional"/>
	// <xs:attribute name="orderForm" type="xs:anyURI" use="optional"/>
	// </xs:complexType>
	//
	// <xs:group name="BoardElementsGroup">
	// <xs:choice>
	// <xs:element name="description" type="xs:string"/>
	// <xs:element name="feature" type="BoardFeatureType"
	// maxOccurs="unbounded"></xs:element>
	// <xs:element name="mountedDevice" type="BoardsDeviceType"
	// maxOccurs="unbounded"/>
	// <xs:element name="compatibleDevice" type="CompatibleDeviceType"
	// maxOccurs="unbounded"/>
	// <xs:element name="image">
	// <xs:complexType>
	// <xs:attribute name="small" type="xs:string" use="optional"/>
	// <xs:attribute name="large" type="xs:string" use="optional"/>
	// </xs:complexType>
	// </xs:element>
	// <xs:element name="debugInterface" type="DebugInterfaceType"
	// maxOccurs="unbounded"/>
	// <xs:element name="book" type="BoardsBookType" maxOccurs="unbounded"/>
	// </xs:choice>
	// </xs:group>

	private void processBoardElement(Element el, Node parent) {

		// Required
		String boardVendor = el.getAttribute("vendor").trim();
		String boardName = el.getAttribute("name").trim();

		// Optional
		String boardRevision = el.getAttribute("revision").trim();
		String salesContact = el.getAttribute("salesContact").trim();
		String orderForm = el.getAttribute("orderForm").trim();

		Node boardNode = new Node(Type.BOARD);
		parent.addChild(boardNode);

		boardNode.setName(boardName);
		boardNode.putProperty(Node.VENDOR_PROPERTY, boardVendor);
		boardNode.putProperty(Node.NAME_PROPERTY, boardName);
		boardNode.putNonEmptyProperty(Node.REVISION_PROPERTY, boardRevision);

		String boardDescription = "Board: " + boardName;

		String descriptionTail = "";
		descriptionTail = extendDescription(descriptionTail, "revision", boardRevision);
		descriptionTail = extendDescription(descriptionTail, "vendor", boardVendor);
		descriptionTail = extendDescription(descriptionTail, "salesContact", salesContact);
		descriptionTail = extendDescription(descriptionTail, "orderForm", orderForm);

		List<Element> childElements = Xml.getChildrenElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();

			if (isBrief()) {
				if ("description".equals(elementName)) {

					boardDescription = extendDescription(boardDescription, Xml.getElementContent(childElement));

				}
			} else {
				if ("description".equals(elementName)) {

					boardDescription = extendDescription(boardDescription, Xml.getElementContent(childElement));

				} else if ("image".equals(elementName)) {

					// Ignored

				} else if ("book".equals(elementName)) {

					// <xs:element name="book" type="BoardsBookType"
					// maxOccurs="unbounded"/>

					processBookElement(childElement, boardNode);

				} else if ("mountedDevice".equals(elementName)) {

					// <xs:complexType name="BoardsDeviceType">
					// <xs:attribute name="deviceIndex" type="xs:string"
					// use="optional" />
					// <xs:attribute name="Dvendor" type="DeviceVendorEnum"
					// use="required"/>
					// <xs:attribute name="Dfamily" type="xs:string"
					// use="optional"/>
					// <xs:attribute name="DsubFamily" type="xs:string"
					// use="optional"/>
					// <xs:attribute name="Dname" type="xs:string"
					// use="optional"/>
					// </xs:complexType>

					// Required
					String Dvendor = childElement.getAttribute("Dvendor").trim();

					// Optional
					String deviceIndex = childElement.getAttribute("deviceIndex").trim();
					String Dfamily = childElement.getAttribute("Dfamily").trim();
					String DsubFamily = childElement.getAttribute("DsubFamily").trim();
					String Dname = childElement.getAttribute("Dname").trim();

					Node deviceNode = new Node(Type.DEVICE);
					boardNode.addChild(deviceNode);

					String va[] = Dvendor.split(":");
					deviceNode.putProperty(Node.VENDOR_PROPERTY, va[0]);
					deviceNode.putProperty(Node.VENDORID_PROPERTY, va[1]);

					deviceNode.putNonEmptyProperty(Node.DEVICEINDEX_PROPERTY, deviceIndex);
					deviceNode.putNonEmptyProperty(Node.FAMILY_PROPERTY, Dfamily);
					deviceNode.putNonEmptyProperty(Node.SUBFAMILY_PROPERTY, DsubFamily);
					deviceNode.putNonEmptyProperty(Node.NAME_PROPERTY, Dname);

					String name = "";
					name = extendName(name, Dfamily);
					name = extendName(name, DsubFamily);
					name = extendName(name, Dname);

					deviceNode.setName(name);

					String deviceDescription = "Mounted device: " + name;
					deviceDescription = extendDescription(deviceDescription, "deviceIndex", deviceIndex);
					deviceDescription = extendDescription(deviceDescription, "Dvendor", Dvendor);
					deviceDescription = extendDescription(deviceDescription, "Dfamily", Dfamily);
					deviceDescription = extendDescription(deviceDescription, "DsubFamily", DsubFamily);
					deviceDescription = extendDescription(deviceDescription, "Dname", Dname);

					deviceNode.setDescription(deviceDescription);

				} else if ("compatibleDevice".equals(elementName)) {

					// <xs:complexType name="CompatibleDeviceType">
					// <xs:attribute name="deviceIndex" type="xs:string"
					// use="optional" />
					// <xs:attribute name="Dvendor" type="DeviceVendorEnum"
					// use="optional"/>
					// <xs:attribute name="Dfamily" type="xs:string"
					// use="optional"/>
					// <xs:attribute name="DsubFamily" type="xs:string"
					// use="optional"/>
					// <xs:attribute name="Dname" type="xs:string"
					// use="optional"/>
					// </xs:complexType>

					// Optional
					String deviceIndex = childElement.getAttribute("deviceIndex").trim();
					String Dvendor = childElement.getAttribute("Dvendor").trim();
					String Dfamily = childElement.getAttribute("Dfamily").trim();
					String DsubFamily = childElement.getAttribute("DsubFamily").trim();
					String Dname = childElement.getAttribute("Dname").trim();

					Node deviceNode = new Node(Type.COMPATIBLEDEVICE);
					boardNode.addChild(deviceNode);

					String va[] = Dvendor.split(":");
					deviceNode.putProperty(Node.VENDOR_PROPERTY, va[0]);
					deviceNode.putProperty(Node.VENDORID_PROPERTY, va[1]);

					deviceNode.putNonEmptyProperty(Node.DEVICEINDEX_PROPERTY, deviceIndex);
					deviceNode.putNonEmptyProperty(Node.FAMILY_PROPERTY, Dfamily);
					deviceNode.putNonEmptyProperty(Node.SUBFAMILY_PROPERTY, DsubFamily);
					deviceNode.putNonEmptyProperty(Node.NAME_PROPERTY, Dname);

					String name = "";
					name = extendName(name, Dfamily);
					name = extendName(name, DsubFamily);
					name = extendName(name, Dname);

					deviceNode.setName(name);

					String deviceDescription = "Compatible device: " + name;
					deviceDescription = extendDescription(deviceDescription, "deviceIndex", deviceIndex);
					deviceDescription = extendDescription(deviceDescription, "Dvendor", Dvendor);
					deviceDescription = extendDescription(deviceDescription, "Dfamily", Dfamily);
					deviceDescription = extendDescription(deviceDescription, "DsubFamily", DsubFamily);
					deviceDescription = extendDescription(deviceDescription, "Dname", Dname);

					deviceNode.setDescription(deviceDescription);

				} else if ("feature".equals(elementName)) {

					// <xs:element name="feature" type="BoardFeatureType"
					// maxOccurs="unbounded"></xs:element>

					processFeatureElement(childElement, boardNode);

				} else if ("debugInterface".equals(elementName)) {

					// <xs:complexType name="DebugInterfaceType">
					// <xs:attribute name="adapter" type="xs:string"/>
					// <xs:attribute name="connector" type="xs:string"/>
					// </xs:complexType>

					String adapter = childElement.getAttribute("adapter").trim();
					String connector = childElement.getAttribute("connector").trim();

					Node debugNode = new Node(Type.DEBUGINTERFACE);
					boardNode.addChild(debugNode);

					debugNode.putProperty(Node.NAME_PROPERTY, adapter);
					debugNode.putProperty(Node.CONNECTOR_PROPERTY, connector);

					debugNode.setName(adapter);

					String description = "Debug interface: " + adapter;
					description = extendDescription(description, "connector", connector);
					debugNode.setDescription(description);

				} else {
					Activator.log("Not processed <" + elementName + ">");
				}
			}
		}

		boardDescription = extendDescription(boardDescription, descriptionTail);
		boardNode.setDescription(boardDescription);
	}

	// <xs:element name="component" minOccurs="1" maxOccurs="unbounded">
	// <xs:complexType>
	// <xs:sequence>
	// <!-- a component can be deprecated if it is no longer maintained-->
	// <xs:element name="deprecated" type="xs:boolean" minOccurs="0"
	// default="false"/>
	// <!-- short component description displayed -->
	// <xs:element name="description" type="xs:string"/>
	// <!-- content to be added to generated RTE_Component.h file -->
	// <xs:element name="RTE_Components_h" type="xs:string" minOccurs="0"/>
	// <!-- list of files / content -->
	// <xs:element name="files">
	// <xs:complexType>
	// <xs:sequence>
	// <xs:element name="file" type="FileType" maxOccurs="unbounded"/>
	// </xs:sequence>
	// </xs:complexType>
	// </xs:element>
	// </xs:sequence>
	// <!-- component identity attributes -->
	// <xs:attribute name="Cvendor" type="xs:string" use="optional"/>
	// <xs:attribute name="Cclass" type="CclassType" use="required"/>
	// <xs:attribute name="Cgroup" type="CgroupType" use="required"/>
	// <xs:attribute name="Csub" type="CsubType" use="optional"/>
	// <xs:attribute name="Cvariant" type="CvariantType" use="optional"/>
	// <xs:attribute name="Cversion" type="VersionType" use="required"/>
	// <!-- api version for this component -->
	// <xs:attribute name="Capiversion" type="VersionType" use="optional"/>
	// <!-- component attribute for referencing a condition specified in
	// conditions section above -->
	// <xs:attribute name="condition" type="xs:string" use="optional"/>
	// <!-- maximum allowed number of instances of a component in a project,
	// default - 1-->
	// <xs:attribute name="maxInstances" type="MaxInstancesType"
	// use="optional"/>
	// </xs:complexType>
	// </xs:element>

	// Bundle element:
	// <xs:element name="component" minOccurs="1" maxOccurs="unbounded">
	// <xs:complexType>
	// <xs:sequence>
	// <!-- a component can be deprecated if it is no longer maintained-->
	// <xs:element name="deprecated" type="xs:boolean" minOccurs="0"
	// default="false"/>
	// <!-- short component description displayed -->
	// <xs:element name="description" type="xs:string"/>
	// <!-- content to be added to generated RTE_Component.h file -->
	// <xs:element name="RTE_Components_h" type="xs:string" minOccurs="0"/>
	// <!-- list of files / content -->
	// <xs:element name="files">
	// <xs:complexType>
	// <xs:sequence>
	// <xs:element name="file" type="FileType" maxOccurs="unbounded"/>
	// </xs:sequence>
	// </xs:complexType>
	// </xs:element>
	// </xs:sequence>
	// <!-- component identity attributes Cclass Cvendor and Cversion are
	// specified by bundle -->
	// <xs:attribute name="Cgroup" type="CgroupType" use="required"/>
	// <xs:attribute name="Csub" type="CsubType" use="optional"/>
	// <xs:attribute name="Cvariant" type="CvariantType" use="optional"/>
	// <!-- api version for this component -->
	// <xs:attribute name="Capiversion" type="VersionType" use="optional"/>
	// <!-- component attribute for referencing a condition specified in
	// conditions section above -->
	// <xs:attribute name="condition" type="xs:string" use="optional"/>
	// <!-- maximum allowed number of instances of a component in a project,
	// default - 1-->
	// <xs:attribute name="maxInstances" type="MaxInstancesType"
	// use="optional"/>
	// </xs:complexType>
	// </xs:element>

	private void processComponentElement(Element el, Node parent, String parentClass) {

		// Required
		String Cclass = el.getAttribute("Cclass").trim(); // may be inherited
		String Cgroup = el.getAttribute("Cgroup").trim();
		String Cversion = el.getAttribute("Cversion").trim();

		// Optional
		String Csub = el.getAttribute("Csub").trim();
		String Cvariant = el.getAttribute("Cvariant").trim();
		String Cvendor = el.getAttribute("Cvendor").trim();

		String apiVersion = el.getAttribute("Capiversion").trim();
		String condition = el.getAttribute("condition").trim();
		String maxInstances = el.getAttribute("maxInstances").trim();

		Node componentNode = new Node(Type.COMPONENT);
		parent.addChild(componentNode);

		if (!isBrief()) {
			componentNode.putProperty(Node.CLASS_PROPERTY, Cclass);
			componentNode.putProperty(Node.GROUP_PROPERTY, Cgroup);
			componentNode.putProperty(Node.VERSION_PROPERTY, Cversion);

			componentNode.putNonEmptyProperty(Node.SUBGROUP_PROPERTY, Csub);
			componentNode.putNonEmptyProperty(Node.VARIANT_PROPERTY, Cvariant);
			componentNode.putNonEmptyProperty(Node.VENDOR_PROPERTY, Cvendor);

			componentNode.putNonEmptyProperty(Node.CONDITION_PROPERTY, condition);
			componentNode.putNonEmptyProperty(Node.APIVERSION_PROPERTY, apiVersion);
			componentNode.putNonEmptyProperty(Node.MAXINSTANCES_PROPERTY, maxInstances);
		}

		String name = "";
		name = extendName(name, Cvendor);
		name = extendName(name, Cclass.length() > 0 ? Cclass : parentClass);
		name = extendName(name, Cgroup);
		name = extendName(name, Csub);
		name = extendName(name, Cvariant);

		componentNode.setName(name);

		String componentDescription = "Component: " + name;

		String descriptionTail = "";
		descriptionTail = extendDescription(descriptionTail, "Cvendor", Cvendor);
		descriptionTail = extendDescription(descriptionTail, "Cclass", Cclass);
		descriptionTail = extendDescription(descriptionTail, "Cgroup", Cgroup);
		descriptionTail = extendDescription(descriptionTail, "Csub", Csub);
		descriptionTail = extendDescription(descriptionTail, "Cvariant", Cvariant);
		descriptionTail = extendDescription(descriptionTail, "Cversion", Cversion);
		descriptionTail = extendDescription(descriptionTail, "condition", condition);
		descriptionTail = extendDescription(descriptionTail, "apiVersion", apiVersion);
		descriptionTail = extendDescription(descriptionTail, "maxInstances", maxInstances);

		List<Element> childElements = Xml.getChildrenElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();

			if (isBrief()) {
				if ("description".equals(elementName)) {

					componentDescription = extendDescription(componentDescription, Xml.getElementContent(childElement));

				}
			} else {
				if ("description".equals(elementName)) {

					componentDescription = extendDescription(componentDescription, Xml.getElementContent(childElement));

				} else if ("RTE_Components_h".equals(elementName)) {

					// <!-- content to be added to generated RTE_Component.h
					// file
					// -->
					// <xs:element name="RTE_Components_h" type="xs:string"
					// minOccurs="0"/>

					String rte = Xml.getElementContent(childElement);
					componentNode.putNonEmptyProperty(Node.RTE_PROPERTY, rte);

				} else if ("deprecated".equals(elementName)) {

					// <!-- a component can be deprecated if it is no longer
					// maintained-->
					// <xs:element name="deprecated" type="xs:boolean"
					// minOccurs="0"
					// default="false"/>

					String deprecated = Xml.getElementContent(childElement);
					componentNode.putNonEmptyProperty(Node.DEPRECATED_PROPERTY, deprecated);

				} else if ("files".equals(elementName)) {

					// <xs:element name="files">
					// <xs:complexType>
					// <xs:sequence>
					// <xs:element name="file" type="FileType"
					// maxOccurs="unbounded"/>
					// </xs:sequence>
					// </xs:complexType>
					// </xs:element>

					List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
					for (Element childElement2 : childElements2) {
						String elementName2 = childElement2.getNodeName();
						if ("file".equals(elementName2)) {

							processFileElement(childElement2, componentNode);

						} else {
							Activator.log("Not processed <" + elementName2 + ">");
						}
					}
				} else {
					Activator.log("Not processed <" + elementName + ">");
				}
			}
		}

		componentDescription = extendDescription(componentDescription, descriptionTail);
		componentNode.setDescription(componentDescription);
	}

	// <!-- Component file type definition -->
	// <xs:complexType name="FileType">
	// <xs:attribute name="condition" type="xs:string" use="optional"/>
	// <!-- file item category: source, header, include path, etc. -->
	// <xs:attribute name="category" type="FileCategoryType" use ="required"/>
	// <!-- file item action attribute : config (copy to project, template,
	// interface) -->
	// <xs:attribute name="attr" type="FileAttributeType" use ="optional"/>
	// <!-- description for "template" or "interface" files. Multiple items are
	// combined when they have the same select attribute -->
	// <xs:attribute name="select" type="xs:string" use ="optional"/>
	// <!-- path + filename + extension -->
	// <xs:attribute name="name" type ="xs:string" use="required" />
	// <!-- copy file to project folder: deprecated, use attr="config" instead
	// -->
	// <xs:attribute name="copy" type ="xs:string" use="optional"/>
	// <!-- simple file version: to be used by RTE copy file action to see
	// whether the file needs updating in project -->
	// <xs:attribute name="version" type ="VersionType" use="optional"/>
	// <!-- path(s) to find source files for a library, paths are delimited with
	// semicolon (;) -->
	// <xs:attribute name="src" type="xs:string" use ="optional"/>
	// </xs:complexType>

	private void processFileElement(Element el, Node parent) {

		// Required
		String name = el.getAttribute("name").trim();
		String category = el.getAttribute("category").trim();

		// Optional
		String attr = el.getAttribute("attr").trim();
		String version = el.getAttribute("version").trim();
		String src = el.getAttribute("src").trim();
		String select = el.getAttribute("select").trim();
		String condition = el.getAttribute("condition").trim();

		Node fileNode = new Node(Type.FILE);
		parent.addChild(fileNode);

		String posixPath = updatePosixSeparators(name);
		String pathArray[] = posixPath.split("/");
		String baseName = pathArray[pathArray.length - 1];

		String posixSrc = updatePosixSeparators(src);

		fileNode.putProperty(Node.FILE_PROPERTY, posixPath);
		fileNode.putProperty(Node.CATEGORY_PROPERTY, category);
		fileNode.putNonEmptyProperty(Node.ATTR_PROPERTY, attr);
		fileNode.putNonEmptyProperty(Node.VERSION_PROPERTY, version);
		fileNode.putNonEmptyProperty(Node.SRC_PROPERTY, posixSrc);
		fileNode.putNonEmptyProperty(Node.SELECT_PROPERTY, select);
		fileNode.putNonEmptyProperty(Node.CONDITION_PROPERTY, condition);

		String description;
		if ("include".equals(category)) {
			fileNode.setName(posixPath);
			description = "Include: ";
		} else {
			fileNode.setName(baseName);
			description = "File: ";
		}

		description += posixPath;
		description = extendDescription(description, "category", category);
		description = extendDescription(description, "attr", attr);
		description = extendDescription(description, "version", version);
		description = extendDescription(description, "src", posixSrc);
		description = extendDescription(description, "select", select);
		description = extendDescription(description, "condition", condition);

		fileNode.setDescription(description);
	}

	// <xs:element name="bundle" minOccurs="1" maxOccurs="unbounded">
	// <xs:complexType>
	// <xs:sequence>
	// <xs:element name="description" type="xs:string"/>
	// <xs:element name="doc" type="xs:string"/>
	// <xs:element name="component" minOccurs="1" maxOccurs="unbounded">
	// <xs:complexType>
	// <xs:sequence>
	// <!-- a component can be deprecated if it is no longer maintained-->
	// <xs:element name="deprecated" type="xs:boolean" minOccurs="0"
	// default="false"/>
	// <!-- short component description displayed -->
	// <xs:element name="description" type="xs:string"/>
	// <!-- content to be added to generated RTE_Component.h file -->
	// <xs:element name="RTE_Components_h" type="xs:string" minOccurs="0"/>
	// <!-- list of files / content -->
	// <xs:element name="files">
	// <xs:complexType>
	// <xs:sequence>
	// <xs:element name="file" type="FileType" maxOccurs="unbounded"/>
	// </xs:sequence>
	// </xs:complexType>
	// </xs:element>
	// </xs:sequence>
	// <!-- component identity attributes Cclass Cvendor and Cversion are
	// specified by bundle -->
	// <xs:attribute name="Cgroup" type="CgroupType" use="required"/>
	// <xs:attribute name="Csub" type="CsubType" use="optional"/>
	// <xs:attribute name="Cvariant" type="CvariantType" use="optional"/>
	// <!-- api version for this component -->
	// <xs:attribute name="Capiversion" type="VersionType" use="optional"/>
	// <!-- component attribute for referencing a condition specified in
	// conditions section above -->
	// <xs:attribute name="condition" type="xs:string" use="optional"/>
	// <!-- maximum allowed number of instances of a component in a project,
	// default - 1-->
	// <xs:attribute name="maxInstances" type="MaxInstancesType"
	// use="optional"/>
	// </xs:complexType>
	// </xs:element>
	// </xs:sequence>
	// <!-- bundle attributes -->
	// <xs:attribute name="Cbundle" type="xs:string" use="required"/>
	// <xs:attribute name="Cvendor" type="xs:string" use="optional"/>
	// <xs:attribute name="Cclass" type="CclassType" use="required"/>
	// <xs:attribute name="Cversion" type="VersionType" use="required"/>
	// </xs:complexType>
	// </xs:element>

	private void processBundleElement(Element el, Node parent) {

		// Required
		String Cbundle = el.getAttribute("Cbundle").trim();
		String Cclass = el.getAttribute("Cclass").trim();
		String Cversion = el.getAttribute("Cversion").trim();

		// Optional
		String Cvendor = el.getAttribute("Cvendor").trim();

		Node bundleNode = new Node(Type.BUNDLE);
		parent.addChild(bundleNode);

		if (!isBrief()) {
			bundleNode.putProperty(Node.BUNDLE_PROPERTY, Cbundle);
			bundleNode.putProperty(Node.CLASS_PROPERTY, Cclass);
			bundleNode.putProperty(Node.VERSION_PROPERTY, Cversion);

			bundleNode.putNonEmptyProperty(Node.VENDOR_PROPERTY, Cvendor);
		}

		String name = "";
		name = extendName(name, Cclass);
		name = extendName(name, Cbundle);
		bundleNode.setName(name);

		String bundleDescription = "Bundle: " + name;

		String descriptionTail = "";
		descriptionTail = extendDescription(descriptionTail, "Cvendor", Cvendor);
		descriptionTail = extendDescription(descriptionTail, "Cbundle", Cbundle);
		descriptionTail = extendDescription(descriptionTail, "Cclass", Cclass);
		descriptionTail = extendDescription(descriptionTail, "Cversion", Cversion);

		List<Element> childElements = Xml.getChildrenElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();

			if (isBrief()) {
				if ("description".equals(elementName)) {

					bundleDescription = extendDescription(bundleDescription, Xml.getElementContent(childElement));

				}
			} else {
				if ("description".equals(elementName)) {

					bundleDescription = extendDescription(bundleDescription, Xml.getElementContent(childElement));

				} else if ("doc".equals(elementName)) {

					String doc = Xml.getElementContent(childElement);
					String posixDoc = updatePosixSeparators(doc);
					descriptionTail = extendDescription(descriptionTail, "doc", posixDoc);
					bundleNode.putNonEmptyProperty(Node.URL_PROPERTY, doc);

				} else if ("component".equals(elementName)) {

					processComponentElement(childElement, bundleNode, Cclass);

				} else {
					Activator.log("Not processed <" + elementName + ">");
				}
			}
		}

		bundleDescription = extendDescription(bundleDescription, descriptionTail);
		bundleNode.setDescription(bundleDescription);
	}

	// <!-- taxonomy description type definition -->
	// <xs:complexType name="TaxonomyDescriptionType">
	// <xs:simpleContent>
	// <xs:extension base='xs:string'>
	// <xs:attribute name="Cclass" type="CclassType" use="required"/>
	// <xs:attribute name="Cgroup" type="CgroupType" use="optional"/>
	// <xs:attribute name="doc" type="xs:string" use="optional"/>
	// <xs:attribute name="generator" type="xs:string" use="optional"/>
	// </xs:extension>
	// </xs:simpleContent>
	// </xs:complexType>

	private void processTaxonomyDescription(Element el, Node parent) {

		// Required
		String Cclass = el.getAttribute("Cclass").trim();
		// Optional

		String Cgroup = el.getAttribute("Cgroup").trim();
		String doc = el.getAttribute("doc").trim();
		String generator = el.getAttribute("generator").trim();

		Node taxonomyNode = new Node(Type.TAXONOMY);
		parent.addChild(taxonomyNode);

		taxonomyNode.putProperty(Node.CLASS_PROPERTY, Cclass);

		taxonomyNode.putNonEmptyProperty(Node.GROUP_PROPERTY, Cgroup);
		taxonomyNode.putNonEmptyProperty(Node.DOC_PROPERTY, doc);
		taxonomyNode.putNonEmptyProperty(Node.GENERATOR_PROPERTY, generator);

		String name = Cclass;
		name = extendName(name, Cgroup);

		taxonomyNode.setName(name);

		String posixDoc = updatePosixSeparators(doc);
		String description = "Taxonomy: " + name;
		description = extendDescription(description, Xml.getElementContent(el));
		description = extendDescription(description, "Cclass", Cclass);
		description = extendDescription(description, "Cgroup", Cgroup);
		description = extendDescription(description, "doc", posixDoc);
		description = extendDescription(description, "generator", generator);

		taxonomyNode.setDescription(description);

	}

	// <xs:complexType name="ConditionType">
	// <xs:sequence>
	// <xs:element name="description" type="xs:string" minOccurs="0"/>
	// <xs:choice minOccurs="1" maxOccurs="unbounded">
	// <xs:element name="accept" type="FilterType"/>
	// <xs:element name="require" type="FilterType"/>
	// <xs:element name="deny" type="FilterType"/>
	// </xs:choice>
	// </xs:sequence>
	// <xs:attribute name="id" type="xs:string" use="required"/>
	// </xs:complexType>

	private void processConditionElement(Element el, Node parent) {

		// Required
		String id = el.getAttribute("id").trim();

		Node conditionNode = new Node(Type.CONDITION);
		parent.addChild(conditionNode);

		conditionNode.putProperty(Node.ID_PROPERTY, id);

		conditionNode.setName(id);

		String conditionDescription = "Condition: " + id;

		List<Element> childElements = Xml.getChildrenElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {

				conditionDescription = extendDescription(conditionDescription, Xml.getElementContent(childElement));

			} else if ("accept".equals(elementName) || "require".equals(elementName) || "deny".equals(elementName)) {

				// <xs:complexType name="FilterType">
				// <xs:attribute name="Dfamily" type="xs:string"/>
				// <xs:attribute name="DsubFamily" type="xs:string"/>
				// <xs:attribute name="Dvariant" type="xs:string"/>
				// <xs:attribute name="Dvendor" type="DeviceVendorEnum"/>
				// <xs:attribute name="Dname" type="xs:string"/>
				// <xs:attribute name="Dcore" type="DcoreEnum"/>
				// <xs:attribute name="Dfpu" type="DfpuEnum"/>
				// <xs:attribute name="Dmpu" type="DmpuEnum"/>
				// <xs:attribute name="Dendian" type="DendianEnum"/>
				// <xs:attribute name="Cvendor" type="xs:string"/>
				// <xs:attribute name="Cbundle" type="xs:string"/>
				// <xs:attribute name="Cclass" type="CclassType"/>
				// <xs:attribute name="Cgroup" type="CgroupType"/>
				// <xs:attribute name="Csub" type="CsubType"/>
				// <xs:attribute name="Cvariant" type="CvariantType"/>
				// <xs:attribute name="Cversion" type="VersionType"/>
				// <xs:attribute name="Capiversion" type="VersionType"/>
				// <xs:attribute name="Tcompiler" type="CompilerEnumType"/>
				// <xs:attribute name="condition" type="xs:string"/>
				// </xs:complexType>

				String conditionName = "";
				String description = "";

				Node node = null;
				if ("accept".equals(elementName)) {
					node = new Node(Type.ACCEPT);
					description = "Accept";
				} else if ("require".equals(elementName)) {
					node = new Node(Type.REQUIRE);
					description = "Require";
				} else if ("deny".equals(elementName)) {
					node = new Node(Type.DENY);
					description = "Deny";
				}
				conditionNode.addChild(node);

				List<String> attrNames = Xml.getAttributesNames(childElement, Node.CONDITION_ATTRIBUTES);
				for (String attrName : attrNames) {

					String value = childElement.getAttribute(attrName).trim();

					// Store exactly the same name
					node.putNonEmptyProperty(attrName, value);

					if ("Dvendor".equals(attrName)) {
						conditionName = extendName(conditionName, value.split(":")[0]);
					} else {
						conditionName = extendName(conditionName, value);
					}

					description = extendDescription(description, attrName, value);
				}

				node.setName(conditionName);
				node.setDescription(description);

			} else {
				Activator.log("Not processed <" + elementName + ">");
			}
		}

		conditionNode.setDescription(conditionDescription);
	}

	// <!-- API type definition -->
	// <xs:complexType name="ApiType">
	// <xs:sequence>
	// <xs:element name="description" type="xs:string" minOccurs="0"/>
	// <!-- list of files / content -->
	// <xs:element name="files">
	// <xs:complexType>
	// <xs:sequence>
	// <xs:element name="file" type="FileType" maxOccurs="unbounded"/>
	// </xs:sequence>
	// </xs:complexType>
	// </xs:element>
	// </xs:sequence>
	// <xs:attribute name="Cclass" type="CclassType" use="required"/>
	// <xs:attribute name="Cgroup" type="CgroupType" use="required"/>
	// <xs:attribute name="exclusive" type="xs:boolean" use="optional"
	// default="1"/>
	// <xs:attribute name="Capiversion" type="VersionType" use="optional"/>
	// </xs:complexType>

	private void processApiElement(Element el, Node parent) {

		// Required
		String Cclass = el.getAttribute("Cclass").trim();
		String Cgroup = el.getAttribute("Cgroup").trim();

		// Optional
		String exclusive = el.getAttribute("exclusive").trim();
		String apiVersion = el.getAttribute("Capiversion").trim();

		Node apiNode = new Node(Type.API);
		parent.addChild(apiNode);

		apiNode.putProperty(Node.CLASS_PROPERTY, Cclass);
		apiNode.putProperty(Node.GROUP_PROPERTY, Cgroup);

		apiNode.putNonEmptyProperty(Node.EXCLUSIVE_PROPERTY, exclusive);
		apiNode.putNonEmptyProperty(Node.APIVERSION_PROPERTY, apiVersion);

		String name = "";
		name = extendName(name, Cclass);
		name = extendName(name, Cgroup);
		apiNode.setName(name);

		String apiDescription = "Api: " + name;

		String descriptionTail = "";
		descriptionTail = extendDescription(descriptionTail, "Cclass", Cclass);
		descriptionTail = extendDescription(descriptionTail, "Cgroup", Cgroup);
		descriptionTail = extendDescription(descriptionTail, "exclusive", exclusive);
		descriptionTail = extendDescription(descriptionTail, "CapiVersion", apiVersion);

		List<Element> childElements = Xml.getChildrenElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {

				apiDescription = extendDescription(apiDescription, Xml.getElementContent(childElement));

			} else if ("files".equals(elementName)) {

				List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
				for (Element childElement2 : childElements2) {
					String elementName2 = childElement2.getNodeName();
					if ("file".equals(elementName2)) {

						processFileElement(childElement2, apiNode);

					} else {
						Activator.log("Not processed <" + elementName2 + ">");
					}
				}
			} else {
				Activator.log("Not processed <" + elementName + ">");
			}
		}

		apiDescription = extendDescription(apiDescription, descriptionTail);
		apiNode.setDescription(apiDescription);
	}

	// <xs:complexType name="ExampleType">
	// <xs:sequence>
	// <!-- brief example description -->
	// <xs:element name="description" type="xs:string"/>
	// <!-- references the board -->
	// <xs:element name="board" type="BoardReferenceType"
	// maxOccurs="unbounded"/>
	// <!-- lists environments with their load files -->
	// <xs:element name="project" type="ExampleProjectType"/>
	// <!-- categories, keywords and used components -->
	// <xs:element name="attributes" type="ExampleAttributesType" />
	// </xs:sequence>
	// <!-- display name of the example -->
	// <xs:attribute name="name" type="xs:string" use="required"/>
	// <!-- relative folder where the example is stored in the package -->
	// <xs:attribute name="folder" type="xs:string" use="required"/>
	// <!-- archive file name with extension located in folder -->
	// <xs:attribute name="archive" type="xs:string" use="optional"/>
	// <!-- file name with extension relative to folder -->
	// <xs:attribute name="doc" type="xs:string" use="required"/>
	// <!-- version of the example -->
	// <xs:attribute name="version" type="xs:string" use="optional"/>
	// </xs:complexType>

	protected void processExampleElement(Element el, Node parent, boolean isFlat) {

		// Required
		String exampleName = el.getAttribute("name").trim();
		String exampleFolder = el.getAttribute("folder").trim();
		String exampleDoc = el.getAttribute("doc").trim();

		// Optional
		String exampleVendor = el.getAttribute("vendor").trim();
		String exampleVersion = el.getAttribute("version").trim();
		String exampleArchive = el.getAttribute("archive").trim();

		Node exampleNode = new Node(Type.EXAMPLE);
		parent.addChild(exampleNode);

		exampleNode.putProperty(Node.NAME_PROPERTY, exampleName);

		String posixDoc = updatePosixSeparators(exampleDoc);
		String posixFolder = updatePosixSeparators(exampleFolder);
		if (!isBrief()) {
			exampleNode.putProperty(Node.FOLDER_PROPERTY, posixFolder);
			exampleNode.putProperty(Node.DOC_PROPERTY, exampleDoc);

			exampleNode.putNonEmptyProperty(Node.VENDOR_PROPERTY, exampleVendor);
			exampleNode.putNonEmptyProperty(Node.VERSION_PROPERTY, exampleVersion);
			exampleNode.putNonEmptyProperty(Node.ARCHIVE_PROPERTY, exampleArchive);
		}

		String exampleDescription = "Example: " + exampleName;

		String descriptionTail = "";
		descriptionTail = extendDescription(descriptionTail, "folder", posixFolder);
		descriptionTail = extendDescription(descriptionTail, "doc", posixDoc);
		descriptionTail = extendDescription(descriptionTail, "version", exampleVersion);
		descriptionTail = extendDescription(descriptionTail, "archive", exampleArchive);

		Node linkNode;
		if (isFlat) {
			// Linearise, add all children to the outline node
			linkNode = parent;
		} else {
			linkNode = exampleNode;
		}

		List<Element> childElements = Xml.getChildrenElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();

			if (isBrief()) {

				if ("description".equals(elementName)) {

					exampleDescription = extendDescription(exampleDescription, Xml.getElementContent(childElement));

				} else if ("board".equals(elementName)) {

					processBoardRef(childElement, linkNode);

				}

			} else {

				if ("description".equals(elementName)) {

					exampleDescription = extendDescription(exampleDescription, Xml.getElementContent(childElement));

				} else if ("board".equals(elementName)) {

					processBoardRef(childElement, linkNode);

				} else if ("project".equals(elementName)) {

					// <xs:complexType name="ExampleProjectType">
					// <xs:sequence>
					// <xs:element name="environment" maxOccurs="unbounded">
					// <xs:complexType>
					// <xs:attribute name="name" type="xs:string"
					// use="required"/>
					// <xs:attribute name="load" type="xs:string"
					// use="required"/>
					// </xs:complexType>
					// </xs:element>
					// </xs:sequence>
					// </xs:complexType>

					List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
					for (Element childElement2 : childElements2) {

						String elementName2 = childElement2.getNodeName();
						if ("environment".equals(elementName2)) {

							// Required
							String name = childElement2.getAttribute("name").trim();
							String load = childElement2.getAttribute("load").trim();

							Node environmentNode = new Node(Type.ENVIRONMENT);
							linkNode.addChild(environmentNode);

							environmentNode.putProperty(Node.NAME_PROPERTY, name);
							environmentNode.putProperty(Node.LOAD_PROPERTY, load);

							environmentNode.setName(load + " (" + name + ")");

							String description = "Environment";
							description = extendDescription(description, "name", name);
							description = extendDescription(description, "load", load);
							environmentNode.setDescription(description);

						} else {
							Activator.log("Not processed <" + elementName2 + ">");
						}
					}

				} else if ("attributes".equals(elementName)) {

					// <xs:complexType name="ExampleAttributesType">
					// <xs:choice maxOccurs="unbounded">
					// <xs:element name="category" type="xs:string"
					// minOccurs="0"
					// maxOccurs="unbounded"/>
					// <xs:element name="component" type="ComponentCategoryType"
					// minOccurs="0" maxOccurs="unbounded"/>
					// <xs:element name="keyword" type="xs:string" minOccurs="0"
					// maxOccurs="unbounded"/>
					// </xs:choice>
					// </xs:complexType>

					List<Element> childElements2 = Xml.getChildrenElementsList(childElement);
					for (Element childElement2 : childElements2) {

						String elementName2 = childElement2.getNodeName();
						if ("category".equals(elementName2)) {

							Node categoryNode = new Node(Type.CATEGORY);
							linkNode.addChild(categoryNode);

							String category = Xml.getElementContent(childElement2);

							categoryNode.putProperty(Node.NAME_PROPERTY, category);

							categoryNode.setName(category);
							categoryNode.setDescription("Category: " + category);

						} else if ("component".equals(elementName2)) {

							// <xs:complexType name="ComponentCategoryType">
							// <xs:attribute name="Cclass" type="CclassType"
							// use="required"/>
							// <xs:attribute name="Cgroup" type="CgroupType"
							// use="optional"/>
							// <xs:attribute name="Csub" type="CsubType"
							// use="optional"/>
							// <xs:attribute name="Cversion" type="VersionType"
							// use="optional"/>
							// <xs:attribute name="Cvendor" type="xs:string"
							// use="optional"/>
							// </xs:complexType>

							// Required
							String Cclass = childElement2.getAttribute("Cclass").trim();

							// Optional
							String Cgroup = childElement2.getAttribute("Cgroup").trim();
							String Csub = childElement2.getAttribute("Csub").trim();
							String Cversion = childElement2.getAttribute("Cversion").trim();
							String Cvendor = childElement2.getAttribute("Cvendor").trim();

							Node componentNode = new Node(Type.COMPONENT);
							linkNode.addChild(componentNode);

							componentNode.putProperty(Node.CLASS_PROPERTY, Cclass);
							componentNode.putNonEmptyProperty(Node.CLASS_PROPERTY, Cclass);
							componentNode.putNonEmptyProperty(Node.GROUP_PROPERTY, Cgroup);
							componentNode.putNonEmptyProperty(Node.SUBGROUP_PROPERTY, Csub);
							componentNode.putNonEmptyProperty(Node.VERSION_PROPERTY, Cversion);
							componentNode.putNonEmptyProperty(Node.VENDOR_PROPERTY, Cvendor);

							String name = "";
							name = extendName(name, Cclass);
							name = extendName(name, Cgroup);
							name = extendName(name, Csub);

							componentNode.setName(name);

							String description = "Referred component: " + name;
							description = extendDescription(description, "Cvendor", Cvendor);
							description = extendDescription(description, "Cclass", Cclass);
							description = extendDescription(description, "Cgroup", Cgroup);
							description = extendDescription(description, "Csub", Csub);
							description = extendDescription(description, "Cversion", Cversion);

							componentNode.setDescription(description);

						} else if ("keyword".equals(elementName2)) {

							processKeywordElement(childElement2, linkNode);

						} else {
							Activator.log("Not processed <" + elementName2 + ">");
						}
					}
				} else {
					Activator.log("Not processed <" + elementName + ">");
				}
			}
		}

		String boardName = "";
		if (linkNode.hasChildren()) {
			for (Leaf childNode : linkNode.getChildren()) {

				if (Type.BOARD.equals(childNode.getType())) {
					boardName = childNode.getName();
					break;
				}
			}
		}

		// Example names are not unique, add board to help identify them.
		if (boardName.length() > 0) {
			exampleName = exampleName + " (" + boardName + ")";
		}
		exampleNode.setName(exampleName);

		if (isBrief()) {
			exampleNode.removeChildren();
		}

		exampleDescription = extendDescription(exampleDescription, descriptionTail);
		exampleNode.setDescription(exampleDescription);
	}

	protected void processBoardRef(Element el, Node parent) {

		// <xs:complexType name="BoardReferenceType">
		// <xs:attribute name="name" type="xs:string" use="required"/>
		// <!-- refers to Board Description by name -->
		// <xs:attribute name="vendor" type="xs:string" use="required"/>
		// <xs:attribute name="Dvendor" type="DeviceVendorEnum"
		// use="optional"/> <!-- deprecated in 1.1 -->
		// <xs:attribute name="Dfamily" type="xs:string"
		// use="optional"/> <!-- deprecated in 1.1 -->
		// <xs:attribute name="DsubFamily" type="xs:string"
		// use="optional"/> <!-- deprecated in 1.1 -->
		// <xs:attribute name="Dname" type="xs:string" use="optional"/>
		// <!-- deprecated in 1.1 -->
		// </xs:complexType>

		// Required
		String boardName = el.getAttribute("name").trim();
		String boardVendor = el.getAttribute("vendor").trim();

		Node boardNode = new Node(Type.BOARD);
		parent.addChild(boardNode);

		boardNode.putProperty(Node.NAME_PROPERTY, boardName);
		boardNode.putProperty(Node.VENDOR_PROPERTY, boardVendor);

		boardNode.setName(boardName);

		String description = "Board: " + boardName;
		description = extendDescription(description, "vendor", boardVendor);
		boardNode.setDescription(description);
	}

	// <xs:element name="keyword" type="xs:string" maxOccurs="unbounded"/>

	protected void processKeywordElement(Element el, Node parent) {

		Node keywordNode = new Node(Type.KEYWORD);
		String keyword = Xml.getElementContent(el);

		keywordNode.putProperty(Node.NAME_PROPERTY, keyword);

		keywordNode.setName(keyword);
		keywordNode.setDescription("Keyword: " + keyword);

		parent.addChild(keywordNode);
	}

	// ------------------------------------------------------------------------
}
