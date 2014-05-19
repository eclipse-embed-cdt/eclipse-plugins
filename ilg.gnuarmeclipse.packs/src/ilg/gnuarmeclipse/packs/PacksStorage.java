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

import ilg.gnuarmeclipse.packs.ui.preferences.FolderConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class PacksStorage {

	public static final String SITES_FILE_NAME = "sites.xml";
	public static final String CACHE_FILE_NAME = ".cache.xml";

	public static final String CMSIS_PACK_TYPE = "CMSIS Pack";

	public static final String DOWNLOAD_FOLDER = ".Download";

	public static final String PACKAGES_SUBTREE = "packages";
	public static final String DEVICES_SUBTREE = "devices";
	public static final String BOARDS_SUBTREE = "boards";

	public static TreeNode ms_tree = null;

	public static List<String[]> getDefaultSites() {

		List<String[]> sitesList;
		sitesList = new ArrayList<String[]>();

		sitesList.add(new String[] { CMSIS_PACK_TYPE,
				"http://www.keil.com/pack/index.idx" });
		sitesList.add(new String[] { CMSIS_PACK_TYPE,
				"http://gnuarmeclipse.sourceforge.net/packages/index.xml" });

		return sitesList;
	}

	// Return a list of urls where packages are stored
	public static List<String[]> getSites() {

		List<String[]> sites;
		try {
			sites = parseSites();
			return sites;
		} catch (UsingDefaultFileException e) {
			Activator.log(e.getMessage());
		} catch (Exception e) {
			Activator.log(e);
		}
		return getDefaultSites();
	}

	// Return the absolute full path of the folder used to store packages
	public static String getFolderPath() throws IOException {

		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String sitesFolderPath = store.getString(FolderConstants.P_FOLDER_PATH)
				.trim();

		// Remove the terminating separator
		if (sitesFolderPath.endsWith(String.valueOf(IPath.SEPARATOR))) {
			sitesFolderPath = sitesFolderPath.substring(0,
					sitesFolderPath.length() - 1);
		}

		if ((sitesFolderPath == null) || (sitesFolderPath.length() == 0)) {
			throw new IOException("Missing folder path");
		}
		return sitesFolderPath;
	}

	// Cannot return null
	private static File getFileObject(String name) throws IOException {

		String folderPath = getFolderPath();

		IPath path = (new Path(folderPath)).append(name);
		File file = path.toFile();
		if (file == null) {
			throw new IOException(name + " File object null");
		}
		return file;
	}

	private static List<String[]> parseSites() throws IOException,
			ParserConfigurationException, SAXException {

		File file = getFileObject(SITES_FILE_NAME);
		if (!file.exists()) {
			throw new UsingDefaultFileException("File " + SITES_FILE_NAME
					+ " does not exist, using defaults");
		}

		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder parser = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document document = parser.parse(inputSource);

		Element el = document.getDocumentElement();
		if (!"sites".equals(el.getNodeName())) {
			throw new IOException("Missing <sites>");
		}

		List<String[]> sitesList;
		sitesList = new ArrayList<String[]>();

		NodeList siteList = el.getElementsByTagName("site");
		for (int i = 0; i < siteList.getLength(); ++i) {
			Node siteNode = siteList.item(i);

			String type = "(unknown)";
			String url = "(unknown)";

			NodeList siteNodeChildren = siteNode.getChildNodes();
			for (int j = 0; j < siteNodeChildren.getLength(); ++j) {
				Node child = siteNodeChildren.item(j);
				short nodeType = child.getNodeType();
				if (nodeType != Node.ELEMENT_NODE) {
					continue;
				}
				String childName = child.getNodeName();
				if ("type".equals(childName)) {
					type = child.getTextContent().trim();
				} else if ("url".equals(childName)) {
					url = child.getTextContent().trim();
				}
			}

			sitesList.add(new String[] { type, url });
		}
		// System.out.println(SITES_FILE_NAME+" parsed");
		return sitesList;
	}

	public static void putSites(List<String[]> sitesList) throws IOException {

		File file = getFileObject(SITES_FILE_NAME);

		// The xml structure is simple, write it as strings
		if (!file.exists())
			file.createNewFile();
		if (file.exists()) {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(file)));
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //$NON-NLS-1$
			writer.println("<sites>"); // $NON-NLS-1$

			for (String[] site : sitesList) {
				writer.println("\t<site>"); // $NON-NLS-1$
				// Warning: the strings should not contain xml special chars
				writer.println("\t\t<type>" + site[0] + "</type>"); // $NON-NLS-1$
				writer.println("\t\t<url>" + site[1] + "</url>"); // $NON-NLS-1$
				writer.println("\t</site>"); // $NON-NLS-1$
			}
			writer.println("</sites>");
			writer.close();

			// System.out.println(SITES_FILE_NAME+" saved");
		}
	}

	public static void putCache(TreeNode tree) throws IOException {

		File file = getFileObject(CACHE_FILE_NAME);

		// The xml structure is simple, write it as strings
		if (!file.exists())
			file.createNewFile();
		if (file.exists()) {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(file)));
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<root version=\"1.0\">");

			putCacheNodeRecursive(tree, 0, writer);

			writer.println("</root>");
			writer.close();

			// System.out.println(SITES_FILE_NAME+" saved");
		}
	}

	private static void putCacheNodeRecursive(TreeNode node, int depth,
			PrintWriter writer) {

		putIndentation(depth * 2, writer);
		writer.println("<node type=\"" + node.getType() + "\" name=\""
				+ node.getName() + "\">");

		String description = node.getDescription();
		if (description != null && description.length() > 0) {
			putIndentation(depth * 2 + 1, writer);
			writer.println("<description>"
					+ Utils.xmlEscape(node.getDescription()) + "</description>");
		}

		if (node.hasProperties()) {
			putIndentation(depth * 2 + 1, writer);
			writer.println("<properties>");

			Map<String, String> properties = node.getProperties();
			for (Object key : properties.keySet()) {
				putIndentation(depth * 2 + 2, writer);
				writer.println("<property name=\"" + key.toString() + "\">"
						+ Utils.xmlEscape(properties.get(key).toString())
						+ "</property>");
			}

			putIndentation(depth * 2 + 1, writer);
			writer.println("</properties>");
		}

		if (node.hasConditions()) {
			putIndentation(depth * 2 + 1, writer);
			writer.println("<conditions>");

			List<TreeNode.Condition> conditions = node.getConditions();
			for (TreeNode.Condition condition : conditions) {
				putIndentation(depth * 2 + 2, writer);
				writer.print("<condition type=\"" + condition.getType() + "\"");
				if (condition.hasVendor()) {
					writer.print(" vendor=\"" + condition.getVendor() + "\"");
				}
				// if (condition.hasAttribute()) {
				// writer.print(" attribute=\"" + condition.getAttribute()
				// + "\"");
				// }
				writer.println(">" + Utils.xmlEscape(condition.getValue())
						+ "</condition>");
			}

			putIndentation(depth * 2 + 1, writer);
			writer.println("</conditions>");
		}

		List<TreeNode> children = node.getChildren();
		if (children != null && !children.isEmpty()) {
			putIndentation(depth * 2 + 1, writer);
			writer.println("<nodes>");

			for (TreeNode child : children) {
				putCacheNodeRecursive(child, depth + 1, writer);
			}

			putIndentation(depth * 2 + 1, writer);
			writer.println("</nodes>");
		}
		putIndentation(depth * 2, writer);
		writer.println("</node>");
	}

	private static void putIndentation(int depth, PrintWriter writer) {
		depth++;
		for (int i = 0; i < depth; ++i) {
			writer.print("  ");
		}
	}

	public static TreeNode getCachedSubTree(String type) throws IOException,
			ParserConfigurationException, SAXException {

		TreeNode rootNode = getCachedTree();
		for (TreeNode childNode : rootNode.getChildren()) {
			if (childNode.getType().equals(type)) {
				return childNode;
			}
		}

		throw new IOException("No such node type " + type);
	}

	public static TreeNode getCachedTree() throws IOException,
			ParserConfigurationException, SAXException {
		return getCachedTree(false);
	}

	public static TreeNode getCachedTree(boolean doReload) throws IOException,
			ParserConfigurationException, SAXException {

		if (doReload) {
			ms_tree = null;
		}

		if (ms_tree != null) {
			return ms_tree;
		}

		System.out.println("Parse cache file");

		File file = getFileObject(CACHE_FILE_NAME);
		if (!file.exists())
			throw new UsingDefaultFileException("File " + CACHE_FILE_NAME
					+ " does not exist, using defaults");

		InputSource inputSource = new InputSource(new FileInputStream(file));

		DocumentBuilder parser = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder();
		Document document = parser.parse(inputSource);

		Element rootElement = document.getDocumentElement();
		if (!"root".equals(rootElement.getNodeName())) {
			throw new IOException("Missing <root>");
		}

		Element nodeRootElement = Utils.getChildElement(rootElement, "node");
		if (nodeRootElement == null) {
			throw new IOException("Missing <node>");
		}
		ms_tree = getCacheRecursive(nodeRootElement);

		List<TreeNode> deviceNodes = new LinkedList<TreeNode>();
		List<TreeNode> boardNodes = new LinkedList<TreeNode>();

		final List<TreeNode>[] lists = (List<TreeNode>[]) (new List<?>[] {
				deviceNodes, boardNodes });

		// Update installed nodes
		IPath path = new Path(getFolderPath());
		updateInstalledNodesRecursive(getCachedSubTree(PACKAGES_SUBTREE), path,
				true);

		return ms_tree;
	}

	private static TreeNode getCacheRecursive(Element nodeElement) {

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
			List<Element> propertyElements = Utils.getChildElementList(
					propertiesElement, "property");

			for (Element propertyElement : propertyElements) {
				String propertyName = propertyElement.getAttribute("name")
						.trim();
				String propertyValue = propertyElement.getTextContent().trim();

				treeNode.putProperty(propertyName, propertyValue);
			}
		}

		// Conditions
		Element conditionsElement = Utils.getChildElement(nodeElement,
				"conditions");
		if (conditionsElement != null) {
			List<Element> conditionElements = Utils.getChildElementList(
					conditionsElement, "condition");

			for (Element conditionElement : conditionElements) {
				String conditionType = conditionElement.getAttribute("type")
						.trim();
				String conditionVendor = conditionElement
						.getAttribute("vendor").trim();
				String conditionValue = conditionElement.getTextContent()
						.trim();

				TreeNode.Condition condition = treeNode.new Condition(
						conditionType);
				condition.setVendor(conditionVendor);
				condition.setValue(conditionValue);

				treeNode.addCondition(condition);
			}
		}

		Element nodesElement = Utils.getChildElement(nodeElement, "nodes");
		if (nodesElement != null) {
			List<Element> nodeElements = Utils.getChildElementList(
					nodesElement, "node");
			for (Element childElement : nodeElements) {

				TreeNode childTreeNode = getCacheRecursive((Element) childElement);
				treeNode.addChild(childTreeNode);
			}
		}

		return treeNode;
	}

	public static void updateInstalledNodesRecursive(TreeNode node, IPath path,
			boolean isInstalled) {

		if (node.hasChildren()) {
			for (TreeNode childNode : node.getChildren()) {
				// Extend path with name and recurse down
				IPath childPath = path.append(childNode.getName());
				updateInstalledNodesRecursive(childNode, childPath, isInstalled);
			}
		}

		String nodeType = node.getType();
		if (TreeNode.VERSION_TYPE.equals(nodeType)) {
			File folder = path.toFile();
			if (folder.exists() && folder.isDirectory()) {
				// Update PacksView and related from Devices & Boards
				updateInstalledVersionNode(node, isInstalled, null);
			}
		}
	}

	public static void updateInstalledVersionNode(TreeNode versionNode,
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

		List<TreeNode.Condition> conditionsList = versionNode.getParent()
				.getConditionsByType(TreeNode.Condition.DEVICEFAMILY_TYPE);
		if (conditionsList.size() > 0) {
			for (TreeNode.Condition condition : conditionsList) {
				updateDeviceInstalled(condition.getVendor(),
						condition.getValue(), isInstalled, deviceNodes);
			}
		}

		conditionsList = versionNode.getParent().getConditionsByType(
				TreeNode.Condition.BOARD_TYPE);
		if (conditionsList.size() > 0) {
			for (TreeNode.Condition condition : conditionsList) {
				updateBoardInstalled(condition.getVendor(),
						condition.getValue(), isInstalled, boardNodes);
			}
		}
	}

	private static void updateDeviceInstalled(String vendorId,
			String familyName, boolean isInstalled, List<TreeNode> deviceNodes) {

		try {
			TreeNode devicesTree = getCachedSubTree(DEVICES_SUBTREE);
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

	private static void updateBoardInstalled(String vendor, String boardName,
			boolean isInstalled, List<TreeNode> boardNodes) {

		try {
			TreeNode boardsTree = getCachedSubTree(BOARDS_SUBTREE);
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

	public static List<TreeNode> getInstalledVersions() {
		
		List<TreeNode> list = new LinkedList<TreeNode>();
		try {
			TreeNode node;
			node = getCachedSubTree(PACKAGES_SUBTREE);
			getInstalledVersionsRecursive(node, list);
		} catch (Exception e) {
		}
		return list;
	}

	public static void getInstalledVersionsRecursive(TreeNode node,
			List<TreeNode> list) {

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
