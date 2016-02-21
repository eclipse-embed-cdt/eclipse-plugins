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

import ilg.gnuarmeclipse.core.Xml;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.Activator;
import ilg.gnuarmeclipse.packs.data.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.w3c.dom.Element;

import com.github.zafarkhaja.semver.Version;

public class PdscParserForContent extends PdscParser {

	public void parse(String pdscNname, String version, Node parent) {

		long beginTime = System.currentTimeMillis();

		fOut.println("Processing \"" + fPath + "\" for content.xml ...");

		Element packageElement = fDocument.getDocumentElement();
		String firstElementName = packageElement.getNodeName();
		if (!"package".equals(firstElementName)) {
			Activator.log("Missing <package>, <" + firstElementName + "> encountered");
			return;
		}

		String schemaVersion = packageElement.getAttribute("schemaVersion").trim();
		Version semVer = Version.valueOf(schemaVersion);

		String msg = "Schema version \"" + schemaVersion + "\"";

		if (!PdscUtils.isSchemaValid(semVer)) {
			msg += " not recognised, package " + pdscNname + " ignored.";
			fOut.println(msg);
			Utils.reportWarning(msg);
			return;
		}
		fOut.println(msg + ".");

		String urlRef = "";
		// Kludge: use URL to detect empty packages
		// TODO: use a better condition
		Element urlElement = Xml.getFirstChildElement(packageElement, "url");
		urlRef = Xml.getElementContent(urlElement);
		if (urlRef.length() == 0) {

			// Deprecate
			return;
		}

		Node packNode;
		Element nameElement = Xml.getFirstChildElement(packageElement, "name");
		if (nameElement == null) {
			fOut.println("Missing <name>.");
			return;
		}
		String packName = Xml.getElementContent(nameElement);

		packNode = Node.addUniqueChild(parent, Type.PACKAGE, packName);

		Element packDescriptionElement = Xml.getFirstChildElement(packageElement, "description");
		if (packDescriptionElement == null) {
			fOut.println("Missing <description>.");
			return;
		}
		String packDescription = Xml.getElementMultiLineContent(packDescriptionElement);

		// TODO: do it only when the version is right
		packNode.setDescription(packDescription);

		Element vendorElement = Xml.getFirstChildElement(packageElement, "vendor");
		if (vendorElement == null) {
			fOut.println("Missing <vendor>.");
			return;
		}
		String packVendorName = Xml.getElementContent(vendorElement);

		Element releasesElement = Xml.getFirstChildElement(packageElement, "releases");
		if (releasesElement == null) {
			fOut.println("Missing <releases>.");
			return;
		}

		String shortUrl = urlRef;
		if (shortUrl.endsWith("/")) {
			shortUrl = shortUrl.substring(0, shortUrl.length() - 1);
		}

		Node versionNode = null;

		boolean isFirst = true;
		List<Element> releaseElements = Xml.getChildrenElementsList(releasesElement, "release");
		for (Element releaseElement : releaseElements) {

			String releaseName = releaseElement.getAttribute("version").trim();

			String releaseDate = releaseElement.getAttribute("date").trim();
			String description = Xml.getElementMultiLineContent(releaseElement);

			Node verNode = Node.addUniqueChild(packNode, Type.VERSION, releaseName);

			verNode.putProperty(Property.TYPE, "cmsis.pack");

			verNode.putProperty(Property.VENDOR_NAME, packVendorName);
			verNode.putProperty(Property.PACK_NAME, packName);
			verNode.putProperty(Property.VERSION_NAME, releaseName);

			String archiveName = packVendorName + "." + packName + "." + releaseName + ".pack";
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

			String unpackFolder = packVendorName + "/" + packName + "/" + releaseName;
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
					fOut.println("Index version=\"" + version + "\" not the most recent (" + releaseName + ").");

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
		Element keywordsElement = Xml.getFirstChildElement(packageElement, "keywords");
		if (keywordsElement != null) {

			List<Element> childElements = Xml.getChildrenElementsList(keywordsElement);
			for (Element childElement : childElements) {

				String elementName2 = childElement.getNodeName();
				if ("keyword".equals(elementName2)) {

					// Add a unique node to selection
					String keyword = Xml.getElementContent(childElement);
					Node.addUniqueChild(outlineNode, Type.KEYWORD, keyword);
				}
			}
		}

		// Devices
		Element devicesElement = Xml.getFirstChildElement(packageElement, "devices");
		if (devicesElement != null) {

			List<Element> familyElements = Xml.getChildrenElementsList(devicesElement, Type.FAMILY);
			for (Element familyElement : familyElements) {

				String family = familyElement.getAttribute("Dfamily").trim();
				String vendor = familyElement.getAttribute("Dvendor").trim();

				Element deviceDescriptionElement = Xml.getFirstChildElement(familyElement, "description");
				String description = "";

				description = extendDescription(description, Xml.getElementMultiLineContent(deviceDescriptionElement));

				String va[] = vendor.split("[:]");
				if (va.length < 2) {
					fOut.println("Dvendor=\"" + vendor + "\" not enumeration, ignored.");
					continue;
				}

				Node deviceFamilyNode = Node.addUniqueChild(outlineNode, Type.FAMILY, family);
				deviceFamilyNode.setDescription(description);

				deviceFamilyNode.putNonEmptyProperty(Property.VENDOR_NAME, va[0]);
				deviceFamilyNode.putNonEmptyProperty(Property.VENDOR_ID, va[1]);
			}
		}

		// Boards
		Element boardsElement = Xml.getFirstChildElement(packageElement, "boards");
		if (boardsElement != null) {

			List<Element> boardElements = Xml.getChildrenElementsList(boardsElement, "board");
			for (Element boardElement : boardElements) {
				String vendor = boardElement.getAttribute("vendor").trim();
				String boardName = boardElement.getAttribute("name").trim();
				// String revision =
				// boardElement.getAttribute("revision").trim();

				Element descriptionElement = Xml.getFirstChildElement(boardElement, "description");
				String description = "";
				description = Xml.getElementMultiLineContent(descriptionElement);

				Node boardNode = Node.addUniqueChild(outlineNode, Type.BOARD, boardName);
				boardNode.putProperty(Property.VENDOR_NAME, vendor);

				boardNode.setDescription(description);

				// For boards with compatible family devices, add them to the
				// package selected conditions
				List<Element> compatibleDevicesElements = Xml.getChildrenElementsList(boardElement, "compatibleDevice");

				for (Element compatibleDevicesElement : compatibleDevicesElements) {

					String family = compatibleDevicesElement.getAttribute("Dfamily").trim();
					if (family.length() > 0) {

						String vendor2 = compatibleDevicesElement.getAttribute("Dvendor").trim();
						String va[] = vendor2.split("[:]");
						if (va.length < 2) {
							fOut.println("Dvendor=\"" + vendor2 + "\" not enumeration, ignored.");
							continue;
						}

						// Contribute external device family
						Node deviceFamilyNode = Node.addUniqueChild(externNode, Type.FAMILY, family);

						deviceFamilyNode.putProperty(Property.VENDOR_NAME, va[0]);
						deviceFamilyNode.putProperty(Property.VENDOR_ID, va[1]);
					}
				}
			}
		}

		// Components
		Element componentsElement = Xml.getFirstChildElement(packageElement, "components");
		if (componentsElement != null) {

			List<Element> componentElements = Xml.getChildrenElementsList(componentsElement, "component");
			for (Element componentElement : componentElements) {

				// Required
				String Cclass = componentElement.getAttribute("Cclass").trim();
				String Cgroup = componentElement.getAttribute("Cgroup").trim();
				// String Cversion = componentElement.getAttribute("Cversion")
				// .trim();

				// Optional
				String Csub = componentElement.getAttribute("Csub").trim();
				String Cvariant = componentElement.getAttribute("Cvariant").trim();
				String Cvendor = componentElement.getAttribute("Cvendor").trim();

				Node componentNode = new Node(Type.COMPONENT);
				outlineNode.addChild(componentNode);

				String name = "";
				name = extendName(name, Cvendor);
				name = extendName(name, Cclass);
				name = extendName(name, Cgroup);
				name = extendName(name, Csub);
				name = extendName(name, Cvariant);

				componentNode.setName(name);

				Element componentsDescriptionElement = Xml.getFirstChildElement(componentElement, "description");
				if (componentsDescriptionElement != null) {

					String componentDescription = Xml.getElementMultiLineContent(componentsDescriptionElement);

					componentNode.setDescription(componentDescription);
				}
			}

			List<Element> bundleElements = Xml.getChildrenElementsList(componentsElement, "bundle");
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

				Element bundleDescriptionElement = Xml.getFirstChildElement(el, "description");
				if (bundleDescriptionElement != null) {

					String bundleDescription = Xml.getElementMultiLineContent(bundleDescriptionElement);

					bundleNode.setDescription(bundleDescription);
				}
			}
		}

		// Examples
		Element examplesElement = Xml.getFirstChildElement(packageElement, "examples");
		if (examplesElement != null) {

			List<Element> exampleElements = Xml.getChildrenElementsList(examplesElement, "example");
			for (Element exampleElement : exampleElements) {

				String firstBoardName = "";
				// String firstBoardVendorName = "";

				Element boardElement = Xml.getFirstChildElement(exampleElement, "board");
				if (boardElement != null) {

					String boardVendor = boardElement.getAttribute("vendor").trim();
					String boardName = boardElement.getAttribute("name").trim();

					// Contribute external board
					Node boardNode = Node.addUniqueChild(externNode, Type.BOARD, boardName);
					boardNode.putProperty(Property.VENDOR_NAME, boardVendor);

					if (firstBoardName.length() == 0) {
						firstBoardName = boardName;
						// firstBoardVendorName = boardVendor;
					}
				}

				// Required
				String exampleName = exampleElement.getAttribute("name").trim();

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

				Element exampleDescriptionElement = Xml.getFirstChildElement(exampleElement, "description");
				if (exampleDescriptionElement != null) {

					String exampleDescription = Xml.getElementMultiLineContent(exampleDescriptionElement);

					exampleNode.setDescription(exampleDescription);
				}

				// contribute possible keywords
				Element attributesElement = Xml.getFirstChildElement(exampleElement, "attributes");
				if (attributesElement != null) {

					List<Element> keywordElements = Xml.getChildrenElementsList(attributesElement, "keyword");

					for (Element keywordElement : keywordElements) {

						// Add a unique node to selection
						String keyword = Xml.getElementContent(keywordElement);
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

		fOut.println("Completed in " + duration + "ms.");
	}

}
