/*******************************************************************************
 * Copyright (c) 2014 Liviu ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PacksStorage {

	public static final String SITES_FILE_NAME = "sites.xml";
	public static final String CACHE_FILE_NAME = ".cache.xml";
	public static final String CACHE_XML_VERSION = "1.1";

	public static final String DOWNLOAD_FOLDER = ".download";
	public static final String CACHE_FOLDER = ".cache";

	// ------------------------------------------------------------------------

	private static PacksStorage ms_packsStorage;

	public static PacksStorage getInstance() {
		if (ms_packsStorage == null) {
			ms_packsStorage = new PacksStorage();
		}

		return ms_packsStorage;
	}

	// ------------------------------------------------------------------------

	private Repos m_repos;

	public PacksStorage() {
		m_repos = Repos.getInstance();
	}

	public static TreeNode ms_tree = null;

	// Parse the Keil index. It is available from
	// curl "http://www.keil.com/pack/index.idx"

	// The syntax is erroneous, the root element is missing, so we need
	// to dynamically add it.

	// <?xml version="1.0" encoding="UTF-8" ?>
	// <pdsc url="http://www.keil.com/pack/" name="ARM.CMSIS.pdsc"
	// version="3.20.4"/>
	// <pdsc url="http://media.infineon.com/mdk/"
	// name="Infineon.XMC1000_DFP.pdsc" version="1.5.0"/>

	// Append string arrays to the given list
	// new String[] { url, name, version }

	public void putCache(TreeNode tree) throws IOException {

		File file = m_repos.getFileObject(CACHE_FILE_NAME);

		// The xml structure is simple, write it as strings
		if (!file.exists())
			file.createNewFile();
		if (file.exists()) {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(file)));
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<root version=\"" + CACHE_XML_VERSION + "\">");

			putCacheNodeRecursive(tree, 0, writer);

			writer.println("</root>");
			writer.close();

			// System.out.println(SITES_FILE_NAME+" saved");
		}

		ms_tree = tree;
	}

	public void putContent(TreeNode tree, String fileName) throws IOException {

		File file = m_repos.getFileObject(fileName);

		file.getParentFile().mkdir();

		// The xml structure is simple, write it as strings
		if (!file.exists())
			file.createNewFile();
		if (file.exists()) {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(file)));
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println();
			writer.println("<root version=\"" + CACHE_XML_VERSION + "\">");

			putContentNodeRecursive(tree, 0, writer);

			writer.println("</root>");
			writer.close();

			// System.out.println(SITES_FILE_NAME+" saved");
		}
	}

	private void putContentNodeRecursive(TreeNode node, int depth,
			PrintWriter writer) {

		putIndentation(depth, writer);

		String nodeType = node.getType();

		String nodeElementName = "";
		String nodesElementName = "";
		boolean doOutputNodes = true;
		boolean doOutputName = true;
		boolean doOutputProperties = true;
		boolean hasNoChildrenElements = false;
		if (TreeNode.REPOSITORY_TYPE.equals(nodeType)) {
			nodeElementName = "repository";
			nodesElementName = "packages";
		} else if (TreeNode.PACKAGE_TYPE.equals(nodeType)) {
			nodeElementName = "packages";
			nodesElementName = "versions";
		} else if (TreeNode.VERSION_TYPE.equals(nodeType)) {
			nodeElementName = "version";
			doOutputNodes = false;
		} else if (TreeNode.OUTLINE_TYPE.equals(nodeType)) {
			nodeElementName = "outline";
			doOutputNodes = false;
			doOutputName = false;
		} else if (TreeNode.EXTERNAL_TYPE.equals(nodeType)) {
			nodeElementName = "external";
			doOutputNodes = false;
			doOutputName = false;
		} else if (TreeNode.FAMILY_TYPE.equals(nodeType)) {
			nodeElementName = "devicefamily";
			doOutputNodes = false;
			doOutputProperties = false;
		} else if (TreeNode.BOARD_TYPE.equals(nodeType)) {
			nodeElementName = "board";
			doOutputNodes = false;
			doOutputProperties = false;
		} else if (TreeNode.KEYWORD_TYPE.equals(nodeType)) {
			nodeElementName = "keyword";
			doOutputNodes = false;
			doOutputProperties = false;
			hasNoChildrenElements = true;
		} else if (TreeNode.COMPONENT_TYPE.equals(nodeType)) {
			nodeElementName = "component";
			doOutputNodes = false;
			doOutputProperties = false;
		} else if (TreeNode.BUNDLE_TYPE.equals(nodeType)) {
			nodeElementName = "bundle";
			doOutputNodes = false;
			doOutputProperties = false;
		} else if (TreeNode.EXAMPLE_TYPE.equals(nodeType)) {
			nodeElementName = "example";
			doOutputNodes = false;
			doOutputProperties = false;
		}

		if (nodeElementName.length() > 0) {
			writer.print("<" + nodeElementName);
		} else {
			writer.print("<node type=\"" + nodeType);
		}
		if (doOutputName) {
			writer.print(" name=\"" + node.getName() + "\"");
		}
		if (hasNoChildrenElements) {
			writer.println(" />");
		} else {
			writer.println(">");

			String description = node.getDescription();
			if (description != null && description.length() > 0) {
				putIndentation(depth + 1, writer);
				writer.println("<description>"
						+ Utils.xmlEscape(node.getDescription())
						+ "</description>");
			}

			if (node.hasProperties()) {

				if (doOutputProperties) {
					putIndentation(depth + 1, writer);
					writer.println("<properties>");
				}

				int newDepth;
				if (doOutputProperties) {
					newDepth = depth + 2;
				} else {
					newDepth = depth + 1;
				}

				Map<String, String> properties = node.getProperties();
				for (Object key : properties.keySet()) {
					putIndentation(newDepth, writer);
					writer.println("<property name=\"" + key.toString()
							+ "\" value=\"" + properties.get(key).toString()
							+ "\" />");
				}

				if (doOutputProperties) {
					putIndentation(depth + 1, writer);
					writer.println("</properties>");
				}
			}

			List<TreeNode> children = node.getChildren();
			if (children != null && !children.isEmpty()) {
				if (doOutputNodes) {
					putIndentation(depth + 1, writer);
					if (nodesElementName.length() > 0) {
						writer.println("<" + nodesElementName + ">");
					} else {
						writer.println("<nodes>");
					}
				}

				int newDepth;
				if (doOutputNodes) {
					newDepth = depth + 2;
				} else {
					newDepth = depth + 1;
				}

				for (TreeNode child : children) {
					putContentNodeRecursive(child, newDepth, writer);
				}

				if (doOutputNodes) {
					putIndentation(depth + 1, writer);
					if (nodesElementName.length() > 0) {
						writer.println("</" + nodesElementName + ">");
					} else {
						writer.println("</nodes>");
					}
				}
			}

			putIndentation(depth, writer);
			if (nodeElementName.length() > 0) {
				writer.println("</" + nodeElementName + ">");
			} else {
				writer.println("</node>");
			}
		}
	}

	private void putCacheNodeRecursive(TreeNode node, int depth,
			PrintWriter writer) {

		putIndentation(depth, writer);
		writer.println("<node type=\"" + node.getType() + "\" name=\""
				+ node.getName() + "\">");

		String description = node.getDescription();
		if (description != null && description.length() > 0) {
			putIndentation(depth + 1, writer);
			writer.println("<description>"
					+ Utils.xmlEscape(node.getDescription()) + "</description>");
		}

		if (node.hasProperties()) {
			putIndentation(depth + 1, writer);
			writer.println("<properties>");

			Map<String, String> properties = node.getProperties();
			for (Object key : properties.keySet()) {
				putIndentation(depth + 2, writer);
				writer.println("<property name=\"" + key.toString()
						+ "\" value=\"" + properties.get(key).toString()
						+ "\" />");
			}

			putIndentation(depth + 1, writer);
			writer.println("</properties>");
		}

		if (node.hasConditions()) {
			putIndentation(depth + 1, writer);
			writer.println("<conditions>");

			List<TreeNode.Selector> conditions = node.getConditions();
			for (TreeNode.Selector condition : conditions) {
				putIndentation(depth + 2, writer);
				writer.print("<condition type=\"" + condition.getType() + "\"");
				writer.print(" value=\"" + condition.getValue() + "\"");
				if (condition.hasVendor()) {
					writer.print(" vendor=\"" + condition.getVendor() + "\"");
				}
				if (condition.hasVendorId()) {
					writer.print(" vendorid=\"" + condition.getVendorId()
							+ "\"");
				}
				writer.println(" />");
			}

			putIndentation(depth + 1, writer);
			writer.println("</conditions>");
		}
		if (node.hasOutline()) {
			putIndentation(depth + 1, writer);
			writer.println("<outline>");

			TreeNode outlineNode = node.getOutline();
			for (TreeNode outlineChild : outlineNode.getChildrenArray()) {
				putCacheNodeRecursive(outlineChild, depth + 2, writer);
			}

			putIndentation(depth + 1, writer);
			writer.println("</outline>");
		}

		List<TreeNode> children = node.getChildren();
		if (children != null && !children.isEmpty()) {
			putIndentation(depth + 1, writer);
			writer.println("<nodes>");

			for (TreeNode child : children) {
				putCacheNodeRecursive(child, depth + 2, writer);
			}

			putIndentation(depth + 1, writer);
			writer.println("</nodes>");
		}
		putIndentation(depth, writer);
		writer.println("</node>");
	}

	private void putIndentation(int depth, PrintWriter writer) {
		depth++;
		for (int i = 0; i < depth; ++i) {
			writer.print("  ");
		}
	}

	public TreeNode getCachedSubTree(String type) throws IOException,
			ParserConfigurationException, SAXException {

		TreeNode rootNode = getCachedTree();
		for (TreeNode childNode : rootNode.getChildren()) {
			if (childNode.getType().equals(type)) {
				return childNode;
			}
		}

		throw new IOException("No such node type " + type + ".");
	}

	public TreeNode getCachedTree() throws IOException,
			ParserConfigurationException, SAXException {
		return getCachedTree(false);
	}

	public TreeNode getCachedTree(boolean doReload) throws IOException,
			ParserConfigurationException, SAXException {

		if (doReload) {
			ms_tree = null;
		}

		if (ms_tree != null) {
			return ms_tree;
		}

		System.out.println("Parse cache file");

		File file = m_repos.getFileObject(CACHE_FILE_NAME);
		if (!file.exists())
			throw new UsingDefaultFileException("File " + CACHE_FILE_NAME
					+ " does not exist, using defaults.");

		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder parser = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document document = parser.parse(inputSource);

		Element rootElement = document.getDocumentElement();
		if (!"root".equals(rootElement.getNodeName())) {
			throw new IOException("Missing <root>.");
		}

		String version = rootElement.getAttribute("version").trim();
		if (!CACHE_XML_VERSION.equals(version)) {
			throw new IOException("Expected <root> version not found.");
		}

		Element nodeRootElement = Utils.getChildElement(rootElement, "node");
		if (nodeRootElement == null) {
			throw new IOException("Missing <node>.");
		}
		ms_tree = getCacheRecursive(nodeRootElement);

		updateInstalled();

		return ms_tree;
	}

	public void updateInstalled() throws IOException,
			ParserConfigurationException, SAXException {
		// List<TreeNode> deviceNodes = new LinkedList<TreeNode>();
		// List<TreeNode> boardNodes = new LinkedList<TreeNode>();

		// final List<TreeNode>[] lists = (List<TreeNode>[]) (new List<?>[] {
		// deviceNodes, boardNodes });

		// Update installed nodes
		IPath path = new Path(m_repos.getFolderPathString());
		updateInstalledNodesRecursive(
				getCachedSubTree(TreeNode.PACKAGES_SUBTREE_TYPE), path, true);
	}

	private TreeNode getCacheRecursive(Element nodeElement) {

		String type = nodeElement.getAttribute("type");
		TreeNode treeNode = new TreeNode(type);

		String name = nodeElement.getAttribute("name");
		treeNode.setName(name);

		Element descriptionElement = Utils.getChildElement(nodeElement,
				"description");
		if (descriptionElement != null) {
			String description = descriptionElement.getTextContent();
			treeNode.setDescription(description);
		}

		// Properties
		Element propertiesElement = Utils.getChildElement(nodeElement,
				"properties");
		if (propertiesElement != null) {
			List<Element> propertyElements = Utils.getChildElementsList(
					propertiesElement, "property");

			for (Element propertyElement : propertyElements) {
				String propertyName = propertyElement.getAttribute("name")
						.trim();
				String propertyValue = propertyElement.getAttribute("value")
						.trim();

				treeNode.putProperty(propertyName, propertyValue);
			}
		}

		// Conditions
		Element conditionsElement = Utils.getChildElement(nodeElement,
				"conditions");
		if (conditionsElement != null) {
			List<Element> conditionElements = Utils.getChildElementsList(
					conditionsElement, "condition");

			for (Element conditionElement : conditionElements) {
				String conditionType = conditionElement.getAttribute("type")
						.trim();
				String conditionVendor = conditionElement
						.getAttribute("vendor").trim();
				String conditionVendorId = conditionElement.getAttribute(
						"vendorid").trim();
				String conditionValue = conditionElement.getAttribute("value")
						.trim();

				TreeNode.Selector condition = treeNode.new Selector(
						conditionType);
				if (conditionVendor.length() > 0) {
					condition.setVendor(conditionVendor);
				}
				if (conditionVendorId.length() > 0) {
					condition.setVendorId(conditionVendorId);
				}
				condition.setValue(conditionValue);

				treeNode.addCondition(condition);
			}
		}

		Element outlineElement = Utils.getChildElement(nodeElement, "outline");
		if (outlineElement != null) {
			List<Element> nodeElements = Utils.getChildElementsList(
					outlineElement, "node");

			TreeNode outlineNode = new TreeNode(TreeNode.OUTLINE_TYPE);
			treeNode.setOutline(outlineNode);

			for (Element childElement : nodeElements) {

				TreeNode childTreeNode = getCacheRecursive((Element) childElement);
				outlineNode.addChild(childTreeNode);
			}
		}

		Element nodesElement = Utils.getChildElement(nodeElement, "nodes");
		if (nodesElement != null) {
			List<Element> nodeElements = Utils.getChildElementsList(
					nodesElement, "node");
			for (Element childElement : nodeElements) {

				TreeNode childTreeNode = getCacheRecursive((Element) childElement);
				treeNode.addChild(childTreeNode);
			}
		}

		return treeNode;
	}

	public void updateInstalledNodesRecursive(TreeNode node, IPath path,
			boolean isInstalled) {

		if (node.hasChildren()) {
			for (TreeNode childNode : node.getChildren()) {
				// Extend path with node name and recurse down
				// TODO: use NAME_PROPERTY
				IPath childPath = path.append(childNode.getName());
				updateInstalledNodesRecursive(childNode, childPath, isInstalled);
			}
		}

		String nodeType = node.getType();
		if (TreeNode.VERSION_TYPE.equals(nodeType)) {
			File folder = path.toFile();
			if (folder.exists() && folder.isDirectory()) {
				// If the name exists and it is indeed a directory,
				// then the package is probably installed.

				// Update PacksView and related from Devices & Boards
				updateInstalledVersionNode(node, isInstalled, null);
			}
		}
	}

	public void updateInstalledVersionNode(TreeNode versionNode,
			boolean isInstalled, List<TreeNode>[] lists) {

		List<TreeNode> deviceNodes;
		List<TreeNode> boardNodes;

		if (lists != null) {
			deviceNodes = lists[0];
			boardNodes = lists[1];
		} else {
			deviceNodes = new LinkedList<TreeNode>();
			boardNodes = new LinkedList<TreeNode>();
		}

		String type = versionNode.getType();
		if (!TreeNode.VERSION_TYPE.equals(type)) {
			return;
		}

		versionNode.setIsInstalled(isInstalled);
		TreeNode packNode = versionNode.getParent();

		boolean doMarkPackage = true;
		if (!isInstalled) {
			for (TreeNode child : packNode.getChildrenArray()) {
				if (child.isInstalled()) {
					doMarkPackage = false;
				}
			}
		}
		if (doMarkPackage) {
			packNode.setIsInstalled(isInstalled);
		}

		List<TreeNode.Selector> conditionsList = versionNode.getParent()
				.getConditionsByType(TreeNode.Selector.DEVICEFAMILY_TYPE);
		if (conditionsList.size() > 0) {
			for (TreeNode.Selector condition : conditionsList) {
				updateDeviceInstalled(condition.getVendorId(),
						condition.getValue(), isInstalled, deviceNodes);
			}
		}

		conditionsList = versionNode.getParent().getConditionsByType(
				TreeNode.Selector.BOARD_TYPE);
		if (conditionsList.size() > 0) {
			for (TreeNode.Selector condition : conditionsList) {
				updateBoardInstalled(condition.getVendor(),
						condition.getValue(), isInstalled, boardNodes);
			}
		}

		versionNode.removeChildren();
		versionNode.setOutline(null);
	}

	private void updateDeviceInstalled(String vendorId, String familyName,
			boolean isInstalled, List<TreeNode> deviceNodes) {

		try {
			TreeNode devicesTree = getCachedSubTree(TreeNode.DEVICES_SUBTREE_TYPE);

			// Assume the two level hierarchy, vendor/devicefamily
			for (TreeNode vendorNode : devicesTree.getChildrenArray()) {

				// Select vendors that match the given vendor id
				if (vendorId.equals(vendorNode
						.getProperty(TreeNode.VENDORID_PROPERTY))) {

					for (TreeNode familyNode : vendorNode.getChildrenArray()) {
						// Select family
						if (familyName.equals(familyNode.getName())) {

							familyNode.setIsInstalled(isInstalled);
							deviceNodes.add(familyNode);
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	private void updateBoardInstalled(String vendor, String boardName,
			boolean isInstalled, List<TreeNode> boardNodes) {

		try {
			TreeNode boardsTree = getCachedSubTree(TreeNode.BOARDS_SUBTREE_TYPE);

			// Assume the two level hierarchy, vendor/board
			for (TreeNode vendorNode : boardsTree.getChildrenArray()) {

				// Select vendors that match the given vendor name
				if (vendor.equals(vendorNode.getName())) {

					for (TreeNode boardNode : vendorNode.getChildrenArray()) {
						// Select board
						if (boardName.equals(boardNode.getName())) {

							boardNode.setIsInstalled(isInstalled);
							boardNodes.add(boardNode);
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public List<TreeNode> getInstalledVersions() {

		List<TreeNode> list = new LinkedList<TreeNode>();
		try {
			TreeNode node;
			node = getCachedSubTree(TreeNode.PACKAGES_SUBTREE_TYPE);
			getInstalledVersionsRecursive(node, list);
		} catch (Exception e) {
		}
		return list;
	}

	public void getInstalledVersionsRecursive(TreeNode node, List<TreeNode> list) {

		if (node.hasChildren()) {
			for (TreeNode child : node.getChildren()) {
				getInstalledVersionsRecursive(child, list);
			}
		}

		if (TreeNode.VERSION_TYPE.equals(node.getType())) {
			if (node.isInstalled()) {
				list.add(node);
			}
		}
	}

}
