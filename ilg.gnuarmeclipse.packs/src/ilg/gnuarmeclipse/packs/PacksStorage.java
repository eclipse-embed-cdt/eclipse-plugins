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

package ilg.gnuarmeclipse.packs;

import ilg.gnuarmeclipse.packs.xcdl.ContentParser;

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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.console.MessageConsoleStream;
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

	public static final String CONTENT_FILE_NAME = "content.xml";
	public static final String CONTENT_XML_VERSION = "1.1";

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
	private MessageConsoleStream m_out;

	public PacksStorage() {
		m_repos = Repos.getInstance();

		m_out = Activator.getConsoleOut();
	}

	public static TreeNode ms_tree = null;

	public static TreeNode ms_tree2 = null;

	// Executed as a separate job from plug-in activator
	public IStatus loadRepositories(IProgressMonitor monitor) {

		long beginTime = System.currentTimeMillis();

		m_out.println("Loading content of packs repositories...");

		List<Map<String, Object>> list;
		list = m_repos.getList();

		int workUnits = list.size();
		monitor.beginTask("Load packs repositories", workUnits);

		TreeNode root = new TreeNode(TreeNode.ROOT_TYPE);

		for (Map<String, Object> map : list) {
			String type = (String) map.get("type");
			String name = (String) map.get("name");
			String url = (String) map.get("url");

			monitor.subTask(name);
			if (Repos.CMSIS_PACK_TYPE.equals(type)
					|| Repos.XCDL_CMSIS_PACK_TYPE.equals(type)) {

				String fileName = m_repos.getRepoContentXmlFromUrl(url);

				parseContent(fileName, root);
			}
			monitor.worked(1);
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}
		m_out.print("Load completed in ");
		if (duration < 1000) {
			m_out.println(duration + "ms.");
		} else {
			m_out.println((duration + 500) / 1000 + "s.");
		}

		return Status.OK_STATUS;
	}

	private void parseContent(String fileName, TreeNode parent) {

		long beginTime = System.currentTimeMillis();

		File file;
		try {
			file = m_repos.getFileObject(fileName);
		} catch (IOException e) {
			m_out.println(e.getMessage());
			return;
		}

		m_out.println("Parsing \"" + file.getPath() + "\"...");

		if (!file.exists()) {
			m_out.println("File does not exist, ignored.");
			return;
		}

		Document document;
		try {
			document = Utils.parseXml(file);
		} catch (Exception e) {
			m_out.println("XML parse error: " + e.getMessage());
			return;
		}

		ContentParser parser = new ContentParser(document);
		parser.parseDocument(parent);

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}

		m_out.println("File parsed in " + duration + "ms.");
	}

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
