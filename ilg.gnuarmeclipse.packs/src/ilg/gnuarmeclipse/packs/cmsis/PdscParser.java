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

import ilg.gnuarmeclipse.packs.Activator;
import ilg.gnuarmeclipse.packs.Utils;
import ilg.gnuarmeclipse.packs.tree.Leaf;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.PackNode;
import ilg.gnuarmeclipse.packs.tree.Property;
import ilg.gnuarmeclipse.packs.tree.Type;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PdscParser {

	protected MessageConsoleStream m_out;
	private boolean m_isBrief;

	protected IPath m_path;
	// private URL m_url;
	protected Document m_document;

	// private Repos m_repos;

	public PdscParser() {

		m_out = Activator.getConsoleOut();
		// m_repos = Repos.getInstance();

		m_isBrief = false;
	}

	public void setIsBrief(boolean brief) {
		m_isBrief = brief;
	}

	public boolean isBrief() {
		return m_isBrief;
	}

	protected String extendDescription(String description, String value) {
		return extendDescription(description, null, value);
	}

	protected String extendDescription(String description, String comment,
			String value) {

		if (value.length() > 0) {
			if (description.length() > 0)
				description += "\n";
			if (comment != null && comment.length() > 0) {
				description += comment + ": ";
			}
			description += value;
		}
		return description;
	}

	protected String extendName(String name, String value) {

		if (value.length() > 0) {
			if (name.length() > 0)
				name += " / ";
			name += value;
		}
		return name;
	}

	protected String updatePosixSeparators(String spath) {
		return spath.replace('\\', '/');
	}

	public Document parseXml(IPath path) throws ParserConfigurationException,
			SAXException, IOException {

		File file = path.toFile();
		if (file == null) {
			throw new FileNotFoundException(path.toFile().toString());
		}

		m_path = path;
		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder xml = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		m_document = xml.parse(inputSource);

		return m_document;
	}

	public Document parseXml(File file) throws ParserConfigurationException,
			SAXException, IOException {

		m_path = new Path(file.getPath());
		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder xml = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		m_document = xml.parse(inputSource);

		return m_document;
	}

	public boolean isSchemaValid(String schemaVersion) {
		if ("1.0".equals(schemaVersion)) {
			;
		} else if ("1.1".equals(schemaVersion)) {
			;
		} else if ("1.2".equals(schemaVersion)) {
			;
		} else {
			System.out.println("Unrecognised schema version " + schemaVersion);
			return false;
		}
		return true;
	}

	// ------------------------------------------------------------------------

	public Document parseXml(URL url) throws IOException,
			ParserConfigurationException, SAXException {

		long beginTime = System.currentTimeMillis();

		m_out.println("Fetching & parsing \"" + url + " ...");

		// m_url = url;

		InputStream is = Utils.checkForUtf8BOM(url.openStream());

		InputSource inputSource = new InputSource(new InputStreamReader(is));

		DocumentBuilder xml = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		m_document = xml.parse(inputSource);

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}

		m_out.println("Completed in " + duration + "ms.");

		return m_document;
	}

	// Called from ParsePdscJob, to add example nodes below version
	public void parseExamples(Node parent) {

		Element packageElement = m_document.getDocumentElement();

		Element examplesElement = Utils.getChildElement(packageElement,
				"examples");
		if (examplesElement != null) {

			// Enumerate <example> in *.pdsc.
			List<Element> exampleElements = Utils.getChildElementsList(
					examplesElement, "example");
			for (Element exampleElement : exampleElements) {
				String exampleName = exampleElement.getAttribute("name").trim();

				Element boardElement = Utils.getChildElement(exampleElement,
						"board");

				// Example names are not unique, add the first board to
				// help identify them.
				String boardName;
				boardName = boardElement.getAttribute("name");
				if (boardName.length() > 0) {
					exampleName = exampleName + " (" + boardName + ")";
				}

				// Examples must be PackNode, to accomodate outlines.
				PackNode exampleNode;
				exampleNode = (PackNode) parent.getChild(Type.EXAMPLE,
						exampleName);
				if (exampleNode == null) {
					exampleNode = new PackNode(Type.EXAMPLE);
					parent.addChild(exampleNode);
					exampleNode.setName(exampleName);
				}

				Element descriptionElement = Utils.getChildElement(
						exampleElement, "description");
				String description;
				description = Utils.getElementContent(descriptionElement);
				exampleNode.setDescription(description);

				Node outlineNode = new Node(Type.OUTLINE);
				exampleNode.setOutline(outlineNode);

				// Parse a flat outline
				processExample(exampleElement, outlineNode, true);
			}
		}
	}

	public void parsePdscContent(String pdscNname, String version, Node parent) {

		long beginTime = System.currentTimeMillis();

		m_out.println("Processing \"" + m_path + "\" for content.xml ...");

		Element packageElement = m_document.getDocumentElement();
		String firstElementName = packageElement.getNodeName();
		if (!"package".equals(firstElementName)) {
			System.out.println("Missing <package>, <" + firstElementName
					+ "> encountered");
			return;
		}

		String schemaVersion = packageElement.getAttribute("schemaVersion")
				.trim();

		m_out.print("Schema version \"" + schemaVersion + "\"");
		if ("1.0".equals(schemaVersion)) {
			;
		} else if ("1.1".equals(schemaVersion)) {
			;
		} else if ("1.2".equals(schemaVersion)) {
			;
		} else {
			m_out.println(" not recognised.");
			return;
		}
		m_out.println(".");

		String urlRef = "";
		// Kludge: use URL to detect empty package
		// TODO: use a better condition
		Element urlElement = Utils.getChildElement(packageElement, "url");
		urlRef = Utils.getElementContent(urlElement);
		if (urlRef.length() == 0) {

			// Deprecate
			return;
		}

		Node packNode;
		Element nameElement = Utils.getChildElement(packageElement, "name");
		if (nameElement == null) {
			m_out.println("Missing <name>.");
			return;
		}
		String packName = Utils.getElementContent(nameElement);

		packNode = Node.addUniqueChild(parent, Type.PACKAGE, packName);

		Element packDescriptionElement = Utils.getChildElement(packageElement,
				"description");
		if (packDescriptionElement == null) {
			m_out.println("Missing <description>.");
			return;
		}
		String packDescription = Utils
				.getElementMultiLineContent(packDescriptionElement);

		// TODO: do it only when the version is right
		packNode.setDescription(packDescription);

		Element vendorElement = Utils.getChildElement(packageElement, "vendor");
		if (vendorElement == null) {
			m_out.println("Missing <vendor>.");
			return;
		}
		String packVendorName = Utils.getElementContent(vendorElement);

		Element releasesElement = Utils.getChildElement(packageElement,
				"releases");
		if (releasesElement == null) {
			m_out.println("Missing <releases>.");
			return;
		}

		String shortUrl = urlRef;
		if (shortUrl.endsWith("/")) {
			shortUrl = shortUrl.substring(0, shortUrl.length() - 1);
		}

		Node versionNode = null;

		boolean isFirst = true;
		List<Element> releaseElements = Utils.getChildElementsList(
				releasesElement, "release");
		for (Element releaseElement : releaseElements) {

			String releaseName = releaseElement.getAttribute("version").trim();

			String releaseDate = releaseElement.getAttribute("date").trim();
			String description = Utils
					.getElementMultiLineContent(releaseElement);

			Node verNode = Node.addUniqueChild(packNode, Type.VERSION,
					releaseName);

			verNode.putProperty(Property.TYPE, "cmsis.pack");

			verNode.putProperty(Property.VENDOR_NAME, packVendorName);
			verNode.putProperty(Property.PACK_NAME, packName);
			verNode.putProperty(Property.VERSION_NAME, releaseName);

			String archiveName = packVendorName + "." + packName + "."
					+ releaseName + ".pack";
			String archiveUrl = shortUrl + "/" + archiveName;
			verNode.putProperty(Property.ARCHIVE_URL, archiveUrl);
			verNode.putProperty(Property.ARCHIVE_NAME, archiveName);

			// Default as for unavailable packages
			String size = "0";
			try {
				int sz = Utils.getRemoteFileSize(new URL(archiveUrl));
				if (sz > 0) {
					size = String.valueOf(sz);
				}
			} catch (IOException e) {
				;
			}
			verNode.putProperty(Property.ARCHIVE_SIZE, size);

			String unpackFolder = packVendorName + "/" + packName + "/"
					+ releaseName;
			verNode.putProperty(Property.DEST_FOLDER, unpackFolder);

			String pdscName = packVendorName + "." + packName + ".pdsc";
			verNode.putProperty(Property.PDSC_NAME, pdscName);

			if (releaseDate.length() > 0) {

				// TODO: normalise date
				verNode.putProperty(Property.DATE, releaseDate);
			}

			verNode.setDescription(description);

			if (isFirst) {
				if (!version.equals(releaseName)) {
					m_out.println("Index version=\"" + version
							+ "\" not the most recent (" + releaseName + ").");

				}

				// Remember top most version
				versionNode = verNode;
			}

			isFirst = false;
		}

		Node outlineNode = new Node(Type.OUTLINE);
		versionNode.addChild(outlineNode);

		Node externNode = new Node(Type.EXTERNAL);

		// Keywords
		Element keywordsElement = Utils.getChildElement(packageElement,
				"keywords");
		if (keywordsElement != null) {

			List<Element> childElements = Utils
					.getChildElementsList(keywordsElement);
			for (Element childElement : childElements) {

				String elementName2 = childElement.getNodeName();
				if ("keyword".equals(elementName2)) {

					// Add a unique node to selection
					String keyword = Utils.getElementContent(childElement);
					Node.addUniqueChild(outlineNode, Type.KEYWORD, keyword);
				}
			}
		}

		// Devices
		Element devicesElement = Utils.getChildElement(packageElement,
				"devices");
		if (devicesElement != null) {

			List<Element> familyElements = Utils.getChildElementsList(
					devicesElement, Type.FAMILY);
			for (Element familyElement : familyElements) {

				String family = familyElement.getAttribute("Dfamily").trim();
				String vendor = familyElement.getAttribute("Dvendor").trim();

				Element deviceDescriptionElement = Utils.getChildElement(
						familyElement, "description");
				String description = "";

				description = extendDescription(
						description,
						Utils.getElementMultiLineContent(deviceDescriptionElement));

				String va[] = vendor.split("[:]");
				if (va.length < 2) {
					m_out.println("Dvendor=\"" + vendor
							+ "\" not enumeration, ignored.");
					continue;
				}

				Node deviceFamilyNode = Node.addUniqueChild(outlineNode,
						Type.FAMILY, family);
				deviceFamilyNode.setDescription(description);

				deviceFamilyNode.putNonEmptyProperty(Property.VENDOR_NAME,
						va[0]);
				deviceFamilyNode.putNonEmptyProperty(Property.VENDOR_ID, va[1]);
			}
		}

		// Boards
		Element boardsElement = Utils.getChildElement(packageElement, "boards");
		if (boardsElement != null) {

			List<Element> boardElements = Utils.getChildElementsList(
					boardsElement, "board");
			for (Element boardElement : boardElements) {
				String vendor = boardElement.getAttribute("vendor").trim();
				String boardName = boardElement.getAttribute("name").trim();
				// String revision =
				// boardElement.getAttribute("revision").trim();

				Element descriptionElement = Utils.getChildElement(
						boardElement, "description");
				String description = "";
				description = Utils
						.getElementMultiLineContent(descriptionElement);

				Node boardNode = Node.addUniqueChild(outlineNode, Type.BOARD,
						boardName);
				boardNode.putProperty(Property.VENDOR_NAME, vendor);

				boardNode.setDescription(description);

				// For boards with compatible family devices, add them to the
				// package selected conditions
				List<Element> compatibleDevicesElements = Utils
						.getChildElementsList(boardElement, "compatibleDevice");

				for (Element compatibleDevicesElement : compatibleDevicesElements) {

					String family = compatibleDevicesElement.getAttribute(
							"Dfamily").trim();
					if (family.length() > 0) {

						String vendor2 = compatibleDevicesElement.getAttribute(
								"Dvendor").trim();
						String va[] = vendor2.split("[:]");
						if (va.length < 2) {
							m_out.println("Dvendor=\"" + vendor2
									+ "\" not enumeration, ignored.");
							continue;
						}

						// Contribute external device family
						Node deviceFamilyNode = Node.addUniqueChild(externNode,
								Type.FAMILY, family);

						deviceFamilyNode.putProperty(Property.VENDOR_NAME,
								va[0]);
						deviceFamilyNode.putProperty(Property.VENDOR_ID, va[1]);
					}
				}
			}
		}

		// Components
		Element componentsElement = Utils.getChildElement(packageElement,
				"components");
		if (componentsElement != null) {

			List<Element> componentElements = Utils.getChildElementsList(
					componentsElement, "component");
			for (Element componentElement : componentElements) {

				// Required
				String Cclass = componentElement.getAttribute("Cclass").trim();
				String Cgroup = componentElement.getAttribute("Cgroup").trim();
				// String Cversion = componentElement.getAttribute("Cversion")
				// .trim();

				// Optional
				String Csub = componentElement.getAttribute("Csub").trim();
				String Cvariant = componentElement.getAttribute("Cvariant")
						.trim();
				String Cvendor = componentElement.getAttribute("Cvendor")
						.trim();

				Node componentNode = new Node(Type.COMPONENT);
				outlineNode.addChild(componentNode);

				String name = "";
				name = extendName(name, Cvendor);
				name = extendName(name, Cclass);
				name = extendName(name, Cgroup);
				name = extendName(name, Csub);
				name = extendName(name, Cvariant);

				componentNode.setName(name);

				Element componentsDescriptionElement = Utils.getChildElement(
						componentElement, "description");
				if (componentsDescriptionElement != null) {

					String componentDescription = Utils
							.getElementMultiLineContent(componentsDescriptionElement);

					componentNode.setDescription(componentDescription);
				}
			}

			List<Element> bundleElements = Utils.getChildElementsList(
					componentsElement, "bundle");
			for (Element el : bundleElements) {

				// Required
				String Cbundle = el.getAttribute("Cbundle").trim();
				String Cclass = el.getAttribute("Cclass").trim();
				// String Cversion = el.getAttribute("Cversion").trim();

				// Optional
				// String Cvendor = el.getAttribute("Cvendor").trim();

				Node bundleNode = new Node(Type.BUNDLE);
				outlineNode.addChild(bundleNode);

				String name = "";
				name = extendName(name, Cclass);
				name = extendName(name, Cbundle);
				bundleNode.setName(name);

				Element bundleDescriptionElement = Utils.getChildElement(el,
						"description");
				if (bundleDescriptionElement != null) {

					String bundleDescription = Utils
							.getElementMultiLineContent(bundleDescriptionElement);

					bundleNode.setDescription(bundleDescription);
				}
			}
		}

		// Examples
		Element examplesElement = Utils.getChildElement(packageElement,
				"examples");
		if (examplesElement != null) {

			List<Element> exampleElements = Utils.getChildElementsList(
					examplesElement, "example");
			for (Element exampleElement : exampleElements) {

				String firstBoardName = "";
				// String firstBoardVendorName = "";

				Element boardElement = Utils.getChildElement(exampleElement,
						"board");
				if (boardElement != null) {

					String boardVendor = boardElement.getAttribute("vendor")
							.trim();
					String boardName = boardElement.getAttribute("name").trim();

					// Contribute external board
					Node boardNode = Node.addUniqueChild(externNode,
							Type.BOARD, boardName);
					boardNode.putProperty(Property.VENDOR_NAME, boardVendor);

					if (firstBoardName.length() == 0) {
						firstBoardName = boardName;
						// firstBoardVendorName = boardVendor;
					}
				}

				// Required
				String exampleName = exampleElement.getAttribute("name").trim();
				// String exampleFolder = exampleElement.getAttribute("folder")
				// .trim();
				// String exampleDoc =
				// exampleElement.getAttribute("doc").trim();
				//
				// // Optional
				// String exampleVendor = exampleElement.getAttribute("vendor")
				// .trim();
				// String exampleVersion =
				// exampleElement.getAttribute("version")
				// .trim();
				// String exampleArchive =
				// exampleElement.getAttribute("archive")
				// .trim();

				Node exampleNode = new Node(Type.EXAMPLE);
				outlineNode.addChild(exampleNode);

				if (firstBoardName.length() > 0) {
					// Remember the original example name
					exampleNode.putProperty(Property.EXAMPLE_NAME, exampleName);

					// Suffix example name with first board name (if not
					// already)
					String suffix = " (" + firstBoardName + ")";
					if (!exampleName.endsWith(suffix)) {
						exampleName += suffix;
					}
				}
				exampleNode.setName(exampleName);

				Element exampleDescriptionElement = Utils.getChildElement(
						exampleElement, "description");
				if (exampleDescriptionElement != null) {

					String exampleDescription = Utils
							.getElementMultiLineContent(exampleDescriptionElement);

					exampleNode.setDescription(exampleDescription);
				}

				// contribute possible keywords
				Element attributesElement = Utils.getChildElement(
						exampleElement, "attributes");
				if (attributesElement != null) {

					List<Element> keywordElements = Utils.getChildElementsList(
							attributesElement, "keyword");

					for (Element keywordElement : keywordElements) {

						// Add a unique node to selection
						String keyword = Utils
								.getElementContent(keywordElement);
						Node.addUniqueChild(outlineNode, Type.KEYWORD, keyword);
					}
				}
			}
		}

		// TODO: check externals are really externals
		if (externNode.hasChildren()) {
			versionNode.addChild(externNode);
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}

		m_out.println("Completed in " + duration + "ms.");
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

	protected void processExample(Element el, Node parent, boolean isFlat) {

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

			exampleNode
					.putNonEmptyProperty(Node.VENDOR_PROPERTY, exampleVendor);
			exampleNode.putNonEmptyProperty(Node.VERSION_PROPERTY,
					exampleVersion);
			exampleNode.putNonEmptyProperty(Node.ARCHIVE_PROPERTY,
					exampleArchive);
		}

		String exampleDescription = "Example: " + exampleName;

		String descriptionTail = "";
		descriptionTail = extendDescription(descriptionTail, "folder",
				posixFolder);
		descriptionTail = extendDescription(descriptionTail, "doc", posixDoc);
		descriptionTail = extendDescription(descriptionTail, "version",
				exampleVersion);
		descriptionTail = extendDescription(descriptionTail, "archive",
				exampleArchive);

		Node linkNode;
		if (isFlat) {
			// Linearise, add all children to the outline node
			linkNode = parent;
		} else {
			linkNode = exampleNode;
		}

		List<Element> childElements = Utils.getChildElementsList(el);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();

			if (isBrief()) {

				if ("description".equals(elementName)) {

					exampleDescription = extendDescription(exampleDescription,
							Utils.getElementContent(childElement));

				} else if ("board".equals(elementName)) {

					processBoardRef(childElement, linkNode);

				}

			} else {

				if ("description".equals(elementName)) {

					exampleDescription = extendDescription(exampleDescription,
							Utils.getElementContent(childElement));

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

					List<Element> childElements2 = Utils
							.getChildElementsList(childElement);
					for (Element childElement2 : childElements2) {

						String elementName2 = childElement2.getNodeName();
						if ("environment".equals(elementName2)) {

							// Required
							String name = childElement2.getAttribute("name")
									.trim();
							String load = childElement2.getAttribute("load")
									.trim();

							Node environmentNode = new Node(Type.ENVIRONMENT);
							linkNode.addChild(environmentNode);

							environmentNode.putProperty(Node.NAME_PROPERTY,
									name);
							environmentNode.putProperty(Node.LOAD_PROPERTY,
									load);

							environmentNode.setName(load + " (" + name + ")");

							String description = "Environment";
							description = extendDescription(description,
									"name", name);
							description = extendDescription(description,
									"load", load);
							environmentNode.setDescription(description);

						} else {
							System.out.println("Not processed <" + elementName2
									+ ">");
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

					List<Element> childElements2 = Utils
							.getChildElementsList(childElement);
					for (Element childElement2 : childElements2) {

						String elementName2 = childElement2.getNodeName();
						if ("category".equals(elementName2)) {

							Node categoryNode = new Node(Type.CATEGORY);
							linkNode.addChild(categoryNode);

							String category = Utils
									.getElementContent(childElement2);

							categoryNode.putProperty(Node.NAME_PROPERTY,
									category);

							categoryNode.setName(category);
							categoryNode
									.setDescription("Category: " + category);

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
							String Cclass = childElement2
									.getAttribute("Cclass").trim();

							// Optional
							String Cgroup = childElement2
									.getAttribute("Cgroup").trim();
							String Csub = childElement2.getAttribute("Csub")
									.trim();
							String Cversion = childElement2.getAttribute(
									"Cversion").trim();
							String Cvendor = childElement2.getAttribute(
									"Cvendor").trim();

							Node componentNode = new Node(Type.COMPONENT);
							linkNode.addChild(componentNode);

							componentNode.putProperty(Node.CLASS_PROPERTY,
									Cclass);
							componentNode.putNonEmptyProperty(
									Node.CLASS_PROPERTY, Cclass);
							componentNode.putNonEmptyProperty(
									Node.GROUP_PROPERTY, Cgroup);
							componentNode.putNonEmptyProperty(
									Node.SUBGROUP_PROPERTY, Csub);
							componentNode.putNonEmptyProperty(
									Node.VERSION_PROPERTY, Cversion);
							componentNode.putNonEmptyProperty(
									Node.VENDOR_PROPERTY, Cvendor);

							String name = "";
							name = extendName(name, Cclass);
							name = extendName(name, Cgroup);
							name = extendName(name, Csub);

							componentNode.setName(name);

							String description = "Referred component: " + name;
							description = extendDescription(description,
									"Cvendor", Cvendor);
							description = extendDescription(description,
									"Cclass", Cclass);
							description = extendDescription(description,
									"Cgroup", Cgroup);
							description = extendDescription(description,
									"Csub", Csub);
							description = extendDescription(description,
									"Cversion", Cversion);

							componentNode.setDescription(description);

						} else if ("keyword".equals(elementName2)) {

							processKeywordElement(childElement2, linkNode);

						} else {
							System.out.println("Not processed <" + elementName2
									+ ">");
						}
					}
				} else {
					System.out.println("Not processed <" + elementName + ">");
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

		exampleDescription = extendDescription(exampleDescription,
				descriptionTail);
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
		String keyword = Utils.getElementContent(el);

		keywordNode.putProperty(Node.NAME_PROPERTY, keyword);

		keywordNode.setName(keyword);
		keywordNode.setDescription("Keyword: " + keyword);

		parent.addChild(keywordNode);
	}

	protected String processDeviceSummary(Node deviceNode) {

		int ramKB = 0;
		int romKB = 0;

		String summary = "";

		String core = deviceNode.getProperty(Node.CORE_PROPERTY, "");
		if (core.length() > 0) {
			if (summary.length() > 0) {
				summary += ", ";
			}
			summary += core;

			String version = deviceNode.getProperty(Node.VERSION_PROPERTY, "");
			if (version.length() > 0) {
				if (summary.length() > 0) {
					summary += ", ";
				}
				summary += version;
			}
		}

		String fpu = deviceNode.getProperty(Node.FPU_PROPERTY, "");
		if (fpu.length() > 0) {
			if (summary.length() > 0) {
				summary += ", ";
			}
			summary += "FPU";
		}

		String mpu = deviceNode.getProperty(Node.MPU_PROPERTY, "");
		if (mpu.length() > 0) {
			if (summary.length() > 0) {
				summary += ", ";
			}
			summary += "MPU";
		}

		String clock = deviceNode.getProperty(Node.CLOCK_PROPERTY, "");
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

		if (deviceNode.hasChildren()) {
			for (Leaf childNode : deviceNode.getChildren()) {

				if (Type.MEMORY.equals(childNode.getType())) {
					String size = childNode.getProperty(Node.SIZE_PROPERTY, "");
					int sizeKB = Utils.convertHexInt(size) / 1024;

					String id = childNode.getProperty(Node.ID_PROPERTY, "");
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

		return summary;
	}
}
