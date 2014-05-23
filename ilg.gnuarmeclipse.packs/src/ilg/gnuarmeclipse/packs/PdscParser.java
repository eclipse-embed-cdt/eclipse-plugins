package ilg.gnuarmeclipse.packs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PdscParser {

	public PdscParser() {

	}

	private String extendDescription(String description, String comment,
			String value) {

		if (value.length() > 0) {
			if (description.length() > 0)
				description += "\n";
			description += comment + ": " + value;
		}
		return description;
	}

	private String extendName(String name, String value) {

		if (value.length() > 0) {
			if (name.length() > 0)
				name += " / ";
			name += value;
		}
		return name;
	}

	public TreeNode parsePdscOutline(IPath path)
			throws ParserConfigurationException, SAXException, IOException {

		File file = path.toFile();
		if (file == null) {
			throw new IOException("Could not open " + path.toFile());
		}

		TreeNode tree = new TreeNode(TreeNode.OUTLINE_TYPE);

		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder parser = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document document = parser.parse(inputSource);

		Element packageElement = document.getDocumentElement();
		String firstElementName = packageElement.getNodeName();
		if (!"package".equals(firstElementName)) {
			System.out.println("Missing <packages>, <" + firstElementName
					+ "> encountered");
			return tree;
		}

		String schemaVersion = packageElement.getAttribute("schemaVersion")
				.trim();

		if ("1.0".equals(schemaVersion)) {
			;
		} else if ("1.2".equals(schemaVersion)) {
			;
		} else {
			System.out.println("Unrecognised schema version " + schemaVersion);
			return tree;
		}

		TreeNode packNode = new TreeNode(TreeNode.PACKAGE_TYPE);
		tree.addChild(packNode);

		String packDescription = "";

		List<Element> childElements = Utils
				.getChildElementsList(packageElement);
		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("vendor".equals(elementName)) {

				String vendorName = Utils.getElementContent(childElement);
				packNode.putProperty(TreeNode.VENDOR_PROPERTY, vendorName);

			} else if ("name".equals(elementName)) {

				String packName = Utils.getElementContent(childElement);
				packNode.setName(packName);

				packDescription = "Package: " + packNode;

			} else if ("description".equals(elementName)) {

				packDescription += "\n" + Utils.getElementContent(childElement);

			} else if ("url".equals(elementName)) {

				String url = Utils.getElementContent(childElement);
				packNode.putNonEmptyProperty(TreeNode.URL_PROPERTY, url);

				packDescription = extendDescription(packDescription, "url", url);

			} else if ("releases".equals(elementName)) {

				List<Element> releaseElements = Utils.getChildElementsList(
						childElement, "release");
				if (!releaseElements.isEmpty()) {
					Element releaseElement = releaseElements.get(0);

					String releaseVersion = releaseElement.getAttribute(
							"version").trim();
					// Optional
					String releaseDate = releaseElement.getAttribute("date")
							.trim();

					String description;
					description = "Version: " + releaseVersion;
					if (releaseDate.length() > 0) {
						description += ", from " + releaseDate;
					}
					description += "\n"
							+ Utils.getElementContent(releaseElement);

					TreeNode versionNode = new TreeNode(TreeNode.VERSION_TYPE);
					versionNode.setName(releaseVersion);
					versionNode.setDescription(description);
					versionNode.putNonEmptyProperty(TreeNode.DATE_PROPERTY,
							releaseDate);

					tree.addChild(versionNode);
				}

			} else if ("keywords".equals(elementName)) {

				List<Element> childElements2 = Utils
						.getChildElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("keyword".equals(elementName2)) {

						processKeyword(childElement2, tree);

					} else {
						System.out.println("Not processed <" + elementName2
								+ ">");
					}
				}

			} else if ("devices".equals(elementName)) {

				// TreeNode devicesNode = new TreeNode(TreeNode.DEVICES_TYPE);
				// tree.addChild(devicesNode);

				List<Element> childElements2 = Utils
						.getChildElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("family".equals(elementName2)) {

						processFamily(childElement2, tree);

					} else {
						System.out.println("Not processed <" + elementName2
								+ ">");
					}
				}

			} else if ("boards".equals(elementName)) {

				// TreeNode boardsNode = new TreeNode(TreeNode.BOARDS_TYPE);
				// tree.addChild(boardsNode);

				List<Element> childElements2 = Utils
						.getChildElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("board".equals(elementName2)) {

						processBoard(childElement2, tree);

					} else {
						System.out.println("Not processed <" + elementName2
								+ ">");
					}
				}

			} else if ("conditions".equals(elementName)) {

				List<Element> childElements2 = Utils
						.getChildElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("condition".equals(elementName2)) {

						processCondition(childElement2, tree);

					} else {
						System.out.println("Not processed <" + elementName2
								+ ">");
					}
				}

			} else if ("examples".equals(elementName)) {

				// TreeNode examplesNode = new TreeNode(TreeNode.EXAMPLES_TYPE);
				// tree.addChild(examplesNode);

				List<Element> childElements2 = Utils
						.getChildElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("example".equals(elementName2)) {

						processExample(childElement2, tree);

					} else {
						System.out.println("Not processed <" + elementName2
								+ ">");
					}
				}

			} else if ("components".equals(elementName)) {

				// TreeNode componentsNode = new
				// TreeNode(TreeNode.COMPONENTS_TYPE);
				// tree.addChild(componentsNode);

				List<Element> childElements2 = Utils
						.getChildElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("component".equals(elementName2)) {

						processComponent(childElement2, tree, "");

					} else if ("bundle".equals(elementName2)) {

						processBundle(childElement2, tree);

					} else {
						System.out.println("Not processed <" + elementName2
								+ ">");
					}
				}

			} else if ("apis".equals(elementName)) {

				// TODO

			} else if ("taxonomy".equals(elementName)) {

				List<Element> childElements2 = Utils
						.getChildElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("description".equals(elementName2)) {

						processTaxonomyDescription(childElement2, tree);

					} else {
						System.out.println("Not processed <" + elementName2
								+ ">");
					}
				}
			} else if ("generators".equals(elementName)) {

				// Ignore

			} else {
				System.out.println("Not processed <" + elementName + ">");
			}
		}

		packNode.setDescription(packDescription);

		return tree;
	}

	private void processFamily(Element el, TreeNode parent) {

		String familyName = el.getAttribute("Dfamily").trim();
		String familyVendor = el.getAttribute("Dvendor").trim();

		String va[] = familyVendor.split("[:]");

		TreeNode familyNode = new TreeNode(TreeNode.FAMILY_TYPE);
		familyNode.setName(familyName);
		familyNode.putNonEmptyProperty(TreeNode.VENDOR_PROPERTY, va[0]);
		familyNode.putNonEmptyProperty(TreeNode.VENDORID_PROPERTY, va[1]);

		String familyDescription = "Family: " + familyName;

		parent.addChild(familyNode);

		List<Element> childElements = Utils.getChildElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("processor".equals(elementName)) {

				String coreName = childElement.getAttribute("Dcore").trim();
				String DcoreVersion = childElement.getAttribute("DcoreVersion")
						.trim();
				String Dfpu = childElement.getAttribute("Dfpu").trim();
				String Dmpu = childElement.getAttribute("Dmpu").trim();
				String Dendian = childElement.getAttribute("Dendian").trim();

				TreeNode coreNode = new TreeNode(TreeNode.CORE_TYPE);
				coreNode.setName(coreName);
				coreNode.putNonEmptyProperty(TreeNode.VERSION_PROPERTY,
						DcoreVersion);
				coreNode.putNonEmptyProperty(TreeNode.FPU_PROPERTY, Dfpu);
				coreNode.putNonEmptyProperty(TreeNode.MPU_PROPERTY, Dmpu);
				coreNode.putNonEmptyProperty(TreeNode.ENDIAN_PROPERTY, Dendian);

				String description = "Core: " + coreName;
				description += "\nDcoreVersion: " + DcoreVersion;
				description += "\nDfpu: " + Dfpu;
				description += "\nDmpu: " + Dmpu;
				description += "\nDendian: " + Dendian;
				coreNode.setDescription(description);

				familyNode.addChild(coreNode);

			} else if ("book".equals(elementName)) {

				processBook(childElement, familyNode);

			} else if ("description".equals(elementName)) {

				familyDescription += "\n"
						+ Utils.getElementContent(childElement);

			} else if ("feature".equals(elementName)) {

				processFeature(childElement, familyNode);

			} else if ("subFamily".equals(elementName)) {

				processSubFamily(childElement, familyNode);

			} else if ("device".equals(elementName)) {

				processDevice(childElement, familyNode);

			} else {
				System.out.println("Not processed <" + elementName + ">");
			}
		}

		familyNode.setDescription(familyDescription);
	}

	private void processFeature(Element el, TreeNode parent) {

		String featureType = el.getAttribute("type").trim();
		String featureN = el.getAttribute("n").trim();
		String featureM = el.getAttribute("m").trim();
		String name = el.getAttribute("name").trim();
		String Pname = el.getAttribute("Pname").trim();

		TreeNode featureNode = new TreeNode(TreeNode.FEATURE_TYPE);
		featureNode.setName(featureType);
		featureNode.putNonEmptyProperty(TreeNode.N_PROPERTY, featureN);
		featureNode.putNonEmptyProperty(TreeNode.M_PROPERTY, featureM);

		String description = "Feature";
		if (name.length() > 0) {
			description += ": " + name;
		}
		description = extendDescription(description, "type", featureType);
		description = extendDescription(description, "n", featureN);
		description = extendDescription(description, "m", featureM);
		description = extendDescription(description, "Pname", Pname);

		featureNode.setDescription(description);

		parent.addChild(featureNode);
	}

	private void processBook(Element el, TreeNode parent) {

		String bookName = el.getAttribute("name").trim();
		String posixName = bookName.replace('\\', '/');

		String bookTitle = el.getAttribute("title").trim();
		String category = el.getAttribute("category").trim();

		TreeNode bookNode = new TreeNode(TreeNode.BOOK_TYPE);
		bookNode.setName(bookTitle);

		String description = "Book: " + bookTitle;
		description += "\n";

		if (bookName.startsWith("http://") || bookName.startsWith("https://")
				|| bookName.startsWith("ftp://")) {
			bookNode.putNonEmptyProperty(TreeNode.URL_PROPERTY, bookName);
			description += "url: " + bookName;
		} else {
			bookNode.putNonEmptyProperty(TreeNode.FILE_PROPERTY, bookName);
			description += "path: " + posixName;
		}

		if (category.length() > 0) {
			bookNode.putNonEmptyProperty(TreeNode.CATEGORY_PROPERTY, category);

			if (description.length() > 0)
				description += "\n";
			description += "category: " + category;
		}

		bookNode.setDescription(description);

		parent.addChild(bookNode);
	}

	private void processSubFamily(Element el, TreeNode parent) {

		String subFamilyName = el.getAttribute("DsubFamily").trim();

		TreeNode subFamilyNode = new TreeNode(TreeNode.SUBFAMILY_TYPE);
		subFamilyNode.setName(subFamilyName);
		subFamilyNode.setDescription("Subfamily: " + subFamilyName);

		parent.addChild(subFamilyNode);

		List<Element> childElements = Utils.getChildElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("feature".equals(elementName)) {

				processFeature(childElement, subFamilyNode);

			} else if ("device".equals(elementName)) {

				processDevice(childElement, subFamilyNode);

			} else {
				System.out.println("Not processed <" + elementName + ">");
			}
		}
	}

	private void processDevice(Element el, TreeNode parent) {

		String deviceName = el.getAttribute("Dname").trim();

		TreeNode deviceNode = new TreeNode(TreeNode.DEVICE_TYPE);
		deviceNode.setName(deviceName);
		String deviceDescription = "Device: " + deviceName;

		parent.addChild(deviceNode);

		int ramKB = 0;
		int romKB = 0;

		List<Element> childElements = Utils.getChildElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("processor".equals(elementName)) {

				String clock = childElement.getAttribute("Dclock").trim();

				if (clock.length() > 0) {
					TreeNode clockNode = new TreeNode(TreeNode.CLOCK_TYPE);
					clockNode.setName(clock);
					String description = "Processor clock";
					try {
						int mhz = Integer.parseInt(clock) / 1000000;
						description += ": " + mhz + "MHz";

						deviceDescription += ", " + mhz + "MHz";
					} catch (NumberFormatException e) {
						;
					}
					clockNode.setDescription(description);

					deviceNode.addChild(clockNode);
				} else {
					System.out.println("Missing Dclock");
				}

			} else if ("compile".equals(elementName)) {

				String header = childElement.getAttribute("header").trim();
				String define = childElement.getAttribute("define").trim();

				TreeNode headerNode = new TreeNode(TreeNode.HEADER_TYPE);
				String posixHeader = header.replace('\\', '/');
				String va[] = posixHeader.split("/");

				headerNode.setName(va[va.length - 1]);
				headerNode.setDescription("Header file: " + posixHeader);
				headerNode.putProperty(TreeNode.FILE_PROPERTY, posixHeader);
				deviceNode.addChild(headerNode);

				TreeNode defineNode = new TreeNode(TreeNode.DEFINE_TYPE);
				defineNode.setName(define);
				defineNode.setDescription("Macro definition");
				deviceNode.addChild(defineNode);

			} else if ("debug".equals(elementName)) {

				String svd = childElement.getAttribute("svd").trim();
				String posixSvd = svd.replace('\\', '/');
				String va[] = posixSvd.split("/");

				TreeNode debugNode = new TreeNode(TreeNode.DEBUG_TYPE);
				debugNode.setName(va[va.length - 1]);
				debugNode.setDescription("Debug file: " + posixSvd);
				debugNode.putProperty(TreeNode.FILE_PROPERTY, posixSvd);

				deviceNode.addChild(debugNode);

			} else if ("memory".equals(elementName)) {

				String id = childElement.getAttribute("id").trim();
				String start = childElement.getAttribute("start").trim();
				String size = childElement.getAttribute("size").trim();
				// Optional
				String startup = childElement.getAttribute("startup").trim();
				String init = childElement.getAttribute("init").trim();
				String defa = childElement.getAttribute("default").trim();

				TreeNode memoryNode = new TreeNode(TreeNode.MEMORY_TYPE);
				memoryNode.setName(id);

				int sizeKB = Utils.convertHexInt(size) / 1024;

				String description;
				if (id.contains("ROM")) {
					description = "ROM area";
					romKB += sizeKB;
				} else if (id.contains("RAM")) {
					description = "RAM area";
					ramKB += sizeKB;
				} else {
					description = "Memory";
				}

				description = extendDescription(description, "id", id);
				description = extendDescription(description, "start", start);
				description = extendDescription(description, "size", size);
				description = extendDescription(description, "startup", startup);
				description = extendDescription(description, "init", init);
				description = extendDescription(description, "default", defa);
				memoryNode.setDescription(description);

				memoryNode.setDescription(description);

				memoryNode.putProperty(TreeNode.ID_PROPERTY, id);
				memoryNode.putProperty(TreeNode.START_PROPERTY, start);
				memoryNode.putProperty(TreeNode.SIZE_PROPERTY, size);

				deviceNode.addChild(memoryNode);

			} else if ("algorithm".equals(elementName)) {

				// Ignore

			} else if ("book".equals(elementName)) {

				processBook(childElement, deviceNode);

			} else if ("feature".equals(elementName)) {

				processFeature(childElement, deviceNode);

			} else {
				System.out.println("Not processed <" + elementName + ">");
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
		deviceNode.setDescription(deviceDescription);
	}

	private void processBoard(Element el, TreeNode parent) {

		String boardName = el.getAttribute("name").trim();
		String boardVendor = el.getAttribute("vendor").trim();
		String boardRevision = el.getAttribute("revision").trim();

		TreeNode boardNode = new TreeNode(TreeNode.BOARD_TYPE);
		boardNode.setName(boardName);
		boardNode.putProperty(TreeNode.VENDOR_PROPERTY, boardVendor);
		boardNode
				.putNonEmptyProperty(TreeNode.REVISION_PROPERTY, boardRevision);

		parent.addChild(boardNode);

		List<Element> childElements = Utils.getChildElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {

				String description = "Board: "
						+ Utils.getElementContent(childElement);
				if (boardRevision.length() > 0) {
					description += "\nrevision: " + boardRevision;
				}
				description += "\nvendor: " + boardVendor;
				boardNode.setDescription(description);

			} else if ("image".equals(elementName)) {

				// Ignored

			} else if ("book".equals(elementName)) {

				processBook(childElement, boardNode);

			} else if ("mountedDevice".equals(elementName)) {

				String Dvendor = childElement.getAttribute("Dvendor").trim();
				String Dfamily = childElement.getAttribute("Dfamily").trim();
				String DsubFamily = childElement.getAttribute("DsubFamily")
						.trim();
				String Dname = childElement.getAttribute("Dname").trim();

				TreeNode deviceNode;
				deviceNode = new TreeNode(TreeNode.DEVICE_TYPE);
				String name = "";
				if (Dname.length() > 0) {
					name = Dname;
				} else if (DsubFamily.length() > 0) {
					name = DsubFamily;
				} else if (Dfamily.length() > 0) {
					name = Dfamily;
				}
				deviceNode.setName(name);

				String va[] = Dvendor.split(":");
				deviceNode.putProperty(TreeNode.VENDOR_PROPERTY, va[0]);
				deviceNode.putProperty(TreeNode.VENDORID_PROPERTY, va[1]);

			} else if ("compatibleDevice".equals(elementName)) {

				// TODO

			} else if ("feature".equals(elementName)) {

				processFeature(childElement, boardNode);

			} else if ("debugInterface".equals(elementName)) {

				String adapter = childElement.getAttribute("adapter").trim();
				String connector = childElement.getAttribute("connector")
						.trim();

				TreeNode debugNode = new TreeNode(TreeNode.DEBUGINTERFACE_TYPE);
				debugNode.setName(adapter);
				debugNode.putProperty(TreeNode.CONNECTOR_PROPERTY, connector);

				String description = "Debug interface: " + adapter;
				if (connector.length() > 0) {
					description += "\nconnector: " + connector;
				}
				debugNode.setDescription(description);

				boardNode.addChild(debugNode);

			} else {
				System.out.println("Not processed <" + elementName + ">");
			}
		}
	}

	private void processExample(Element el, TreeNode parent) {

		String exampleName = el.getAttribute("name").trim();
		String exampleFolder = el.getAttribute("folder").trim();
		String exampleVendor = el.getAttribute("vendor").trim();
		String exampleVersion = el.getAttribute("version").trim();
		String exampleArchive = el.getAttribute("archive").trim();

		TreeNode exampleNode = new TreeNode(TreeNode.EXAMPLE_TYPE);
		exampleNode.setName(exampleName);
		exampleNode.putProperty(TreeNode.VENDOR_PROPERTY, exampleVendor);

		String posixFolder = exampleFolder.replace('\\', '/');
		exampleNode.putProperty(TreeNode.FOLDER_PROPERTY, posixFolder);

		exampleNode.putNonEmptyProperty(TreeNode.VERSION_PROPERTY,
				exampleVersion);

		exampleNode.putNonEmptyProperty(TreeNode.ARCHIVE_PROPERTY,
				exampleArchive);

		String descriptionTail = "";
		descriptionTail = extendDescription(descriptionTail, "folder",
				posixFolder);
		descriptionTail = extendDescription(descriptionTail, "version",
				exampleVersion);
		descriptionTail = extendDescription(descriptionTail, "archive",
				exampleArchive);

		parent.addChild(exampleNode);

		List<Element> childElements = Utils.getChildElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {

				String description = "Example: "
						+ Utils.getElementContent(childElement);
				if (descriptionTail.length() > 0) {
					description += "\n" + descriptionTail;
					descriptionTail = "";
				}
				exampleNode.setDescription(description);

			} else if ("board".equals(elementName)) {

				String boardName = childElement.getAttribute("name").trim();
				String boardVendor = childElement.getAttribute("vendor").trim();

				TreeNode boardNode = new TreeNode(TreeNode.BOARD_TYPE);
				boardNode.setName(boardName);
				boardNode.putProperty(TreeNode.VENDOR_PROPERTY, boardVendor);

				String description = "Board: " + boardName;
				description += "\nvendor: " + boardVendor;
				boardNode.setDescription(description);

				exampleNode.addChild(boardNode);

			} else if ("project".equals(elementName)) {

				// Ignored

			} else if ("attributes".equals(elementName)) {

				List<Element> childElements2 = Utils
						.getChildElementsList(childElement);
				for (Element childElement2 : childElements2) {

					String elementName2 = childElement2.getNodeName();
					if ("component".equals(elementName2)) {

						String Cvendor = childElement2.getAttribute("Cvendor")
								.trim();
						String Cclass = childElement2.getAttribute("Cclass")
								.trim();
						String Cgroup = childElement2.getAttribute("Cgroup")
								.trim();
						String Csub = childElement2.getAttribute("Csub").trim();
						String Cvariant = childElement2
								.getAttribute("Cvariant").trim();
						String Cversion = childElement2.getAttribute("Csub")
								.trim();

						String name = "";
						name = extendName(name, Cclass);
						name = extendName(name, Cgroup);
						name = extendName(name, Csub);
						name = extendName(name, Cvariant);

						TreeNode componentNode = new TreeNode(
								TreeNode.COMPONENT_TYPE);
						componentNode.setName(name);

						String description = "Referred component";
						description = extendDescription(description, "Cvendor",
								Cvendor);
						description = extendDescription(description, "Cclass",
								Cclass);
						description = extendDescription(description, "Cgroup",
								Cgroup);
						description = extendDescription(description, "Csub",
								Csub);
						description = extendDescription(description,
								"Cvariant", Cvariant);
						description = extendDescription(description,
								"Cversion", Cversion);

						if (description.length() > 0) {
							componentNode.setDescription(description);
						}
						exampleNode.addChild(componentNode);

					} else if ("category".equals(elementName2)) {

						TreeNode categoryNode = new TreeNode(
								TreeNode.CATEGORY_TYPE);
						String category = Utils
								.getElementContent(childElement2);
						categoryNode.setName(category);
						categoryNode.setDescription("Category: " + category);

						exampleNode.addChild(categoryNode);

					} else if ("keyword".equals(elementName2)) {

						processKeyword(childElement2, exampleNode);

					} else {
						System.out.println("Not processed <" + elementName2
								+ ">");
					}
				}
			} else {
				System.out.println("Not processed <" + elementName + ">");
			}
		}
		if (descriptionTail.length() > 0) {
			exampleNode.setDescription(descriptionTail);
		}
	}

	private void processComponent(Element el, TreeNode parent,
			String parentClass) {

		String Cvendor = el.getAttribute("Cvendor").trim();
		String Cclass = el.getAttribute("Cclass").trim();
		String Cgroup = el.getAttribute("Cgroup").trim();
		String Csub = el.getAttribute("Csub").trim();
		String Cvariant = el.getAttribute("Cvariant").trim();
		String Cversion = el.getAttribute("Cversion").trim();

		String apiVersion = el.getAttribute("Capiversion").trim();
		String condition = el.getAttribute("condition").trim();
		String maxInstances = el.getAttribute("maxInstances").trim();

		String descriptionTail = "";
		descriptionTail = extendDescription(descriptionTail, "Cvendor", Cvendor);
		descriptionTail = extendDescription(descriptionTail, "Cclass", Cclass);
		descriptionTail = extendDescription(descriptionTail, "Cgroup", Cgroup);
		descriptionTail = extendDescription(descriptionTail, "Csub", Csub);
		descriptionTail = extendDescription(descriptionTail, "Cvariant",
				Cvariant);
		descriptionTail = extendDescription(descriptionTail, "Cversion",
				Cversion);
		descriptionTail = extendDescription(descriptionTail, "condition",
				condition);
		descriptionTail = extendDescription(descriptionTail, "apiVersion",
				apiVersion);
		descriptionTail = extendDescription(descriptionTail, "maxInstances",
				maxInstances);

		String name = "";
		name = extendName(name, Cvendor);
		name = extendName(name, Cclass.length() > 0 ? Cclass : parentClass);
		name = extendName(name, Cgroup);
		name = extendName(name, Csub);
		name = extendName(name, Cvariant);

		TreeNode componentNode = new TreeNode(TreeNode.COMPONENT_TYPE);
		componentNode.setName(name);

		componentNode.putNonEmptyProperty(TreeNode.CONDITION_PROPERTY,
				condition);
		componentNode.putNonEmptyProperty(TreeNode.APIVERSION_PROPERTY,
				apiVersion);
		componentNode.putNonEmptyProperty(TreeNode.MAXINSTANCES_PROPERTY,
				maxInstances);

		parent.addChild(componentNode);

		List<Element> childElements = Utils.getChildElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {

				String description = "Component: "
						+ Utils.getElementContent(childElement);
				if (descriptionTail.length() > 0) {
					description += "\n" + descriptionTail;
					descriptionTail = "";
				}
				componentNode.setDescription(description);

			} else if ("RTE_Components_h".equals(elementName)) {

				String rte = Utils.getElementContent(childElement);
				componentNode.putNonEmptyProperty(TreeNode.RTE_PROPERTY, rte);

			} else if ("deprecated".equals(elementName)) {

				String deprecated = Utils.getElementContent(childElement);
				componentNode.putNonEmptyProperty(TreeNode.DEPRECATED_PROPERTY,
						deprecated);

			} else if ("files".equals(elementName)) {

				List<Element> childElements2 = Utils
						.getChildElementsList(childElement);
				for (Element childElement2 : childElements2) {
					String elementName2 = childElement2.getNodeName();
					if ("file".equals(elementName2)) {

						processFile(childElement2, componentNode);

					} else {
						System.out.println("Not processed <" + elementName2
								+ ">");
					}
				}
			} else {
				System.out.println("Not processed <" + elementName + ">");
			}
		}

		if (descriptionTail.length() > 0) {
			componentNode.setDescription(descriptionTail);
		}
	}

	private void processFile(Element el, TreeNode parent) {

		String name = el.getAttribute("name").trim();
		String category = el.getAttribute("category").trim();
		// Optional
		String attr = el.getAttribute("attr").trim();
		String version = el.getAttribute("version").trim();
		String src = el.getAttribute("src").trim();
		String select = el.getAttribute("select").trim();
		String condition = el.getAttribute("condition").trim();

		String posixPath = name.replace('\\', '/');
		String pathArray[] = posixPath.split("/");
		String baseName = pathArray[pathArray.length - 1];

		TreeNode fileNode = new TreeNode(TreeNode.FILE_TYPE);
		fileNode.putProperty(TreeNode.FILE_PROPERTY, posixPath);
		fileNode.putProperty(TreeNode.CATEGORY_PROPERTY, category);
		fileNode.putNonEmptyProperty(TreeNode.ATTR_PROPERTY, attr);
		fileNode.putNonEmptyProperty(TreeNode.VERSION_PROPERTY, version);
		fileNode.putNonEmptyProperty(TreeNode.SRC_PROPERTY, src);
		fileNode.putNonEmptyProperty(TreeNode.SELECT_PROPERTY, select);
		fileNode.putNonEmptyProperty(TreeNode.CONDITION_PROPERTY, condition);

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
		description = extendDescription(description, "src", src);
		description = extendDescription(description, "select", select);
		description = extendDescription(description, "condition", condition);

		fileNode.setDescription(description);

		parent.addChild(fileNode);
	}

	private void processBundle(Element el, TreeNode parent) {

		String Cvendor = el.getAttribute("Cvendor").trim();
		String Cclass = el.getAttribute("Cclass").trim();
		String Cbundle = el.getAttribute("Cbundle").trim();
		String Cversion = el.getAttribute("Cversion").trim();

		String descriptionTail = "";
		descriptionTail = extendDescription(descriptionTail, "Cvendor", Cvendor);
		descriptionTail = extendDescription(descriptionTail, "Cbundle", Cbundle);
		descriptionTail = extendDescription(descriptionTail, "Cclass", Cclass);
		descriptionTail = extendDescription(descriptionTail, "Cversion",
				Cversion);

		TreeNode bundleNode = new TreeNode(TreeNode.BUNDLE_TYPE);

		String name = "";
		name = extendName(name, Cbundle);
		name = extendName(name, Cclass);
		bundleNode.setName(name);

		parent.addChild(bundleNode);

		List<Element> childElements = Utils.getChildElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {

				String description = "Bundle: "
						+ Utils.getElementContent(childElement);
				if (descriptionTail.length() > 0) {
					description += "\n" + descriptionTail;
					descriptionTail = "";
				}
				bundleNode.setDescription(description);

			} else if ("doc".equals(elementName)) {

				String doc = Utils.getElementContent(childElement);
				bundleNode.putNonEmptyProperty(TreeNode.URL_PROPERTY, doc);

			} else if ("component".equals(elementName)) {

				processComponent(childElement, bundleNode, Cclass);

			} else {
				System.out.println("Not processed <" + elementName + ">");
			}
		}

		if (descriptionTail.length() > 0) {
			bundleNode.setDescription(descriptionTail);
		}
	}

	private void processTaxonomyDescription(Element el, TreeNode parent) {

		String Cclass = el.getAttribute("Cclass").trim();
		// Optional
		String Cgroup = el.getAttribute("Cgroup").trim();
		String doc = el.getAttribute("doc").trim();
		String generator = el.getAttribute("generator").trim();

		TreeNode taxonomyNode = new TreeNode(TreeNode.TAXONOMY_TYPE);

		String name = Cclass;
		name = extendName(name, Cgroup);

		taxonomyNode.setName(name);

		String posixDoc = doc.replace('\\', '/');
		String description = "Taxonomy: " + name;
		description += Utils.getElementContent(el);
		description = extendDescription(description, "doc", posixDoc);
		description = extendDescription(description, "generator", generator);

		taxonomyNode.setDescription(description);

		parent.addChild(taxonomyNode);
	}

	private void processKeyword(Element el, TreeNode parent) {

		TreeNode keywordNode = new TreeNode(TreeNode.KEYWORD_TYPE);
		String keyword = Utils.getElementContent(el);
		keywordNode.setName(keyword);
		keywordNode.setDescription("Keyword: " + keyword);

		parent.addChild(keywordNode);
	}

	private void processCondition(Element el, TreeNode parent) {

		String id = el.getAttribute("id").trim();

		TreeNode conditionNode = new TreeNode(TreeNode.CONDITION_TYPE);
		conditionNode.setName(id);

		String conditionDescription = "Condition: " + id;

		parent.addChild(conditionNode);

		List<Element> childElements = Utils.getChildElementsList(el);

		for (Element childElement : childElements) {

			String elementName = childElement.getNodeName();
			if ("description".equals(elementName)) {

				String description = Utils.getElementContent(childElement);
				if (description.length() > 0) {
					conditionDescription += "\n" + description;
				}

			} else if ("accept".equals(elementName)
					|| "require".equals(elementName)
					|| "deny".equals(elementName)) {

				String conditionName = "";
				String description = "";

				TreeNode node = null;
				if ("accept".equals(elementName)) {
					node = new TreeNode(TreeNode.ACCEPT_TYPE);
					description = "Accept";
				} else if ("require".equals(elementName)) {
					node = new TreeNode(TreeNode.REQUIRE_TYPE);
					description = "Require";
				} else if ("deny".equals(elementName)) {
					node = new TreeNode(TreeNode.DENY_TYPE);
					description = "Deny";
				}

				String sa[] = { "Dfamily", "DsubFamily", "Dvariant", "Dvendor",
						"Dname", "Dcore", "Dfpu", "Dmpu", "Dendian", "Cvendor",
						"Cbundle", "Cclass", "Cgroup", "Csub", "Cvariant",
						"Cversion", "Capiversion", "Tcompiler", "condition" };
				List<String> attrNames = Utils.getAttributesNames(childElement,
						sa);
				for (String attrName : attrNames) {

					String value = childElement.getAttribute(attrName).trim();

					conditionName = extendName(conditionName, value);

					description = extendDescription(description, attrName,
							value);
				}

				node.setName(conditionName);
				node.setDescription(description);

				conditionNode.addChild(node);

			} else {
				System.out.println("Not processed <" + elementName + ">");
			}
		}

		conditionNode.setDescription(conditionDescription);
	}

}
