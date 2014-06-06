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

import ilg.gnuarmeclipse.packs.tree.Leaf;
import ilg.gnuarmeclipse.packs.tree.Node;
import ilg.gnuarmeclipse.packs.tree.Property;
import ilg.gnuarmeclipse.packs.tree.Selector;
import ilg.gnuarmeclipse.packs.tree.Type;
import ilg.gnuarmeclipse.packs.xcdl.ContentParser;

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
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
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

	public static synchronized PacksStorage getInstance() {

		if (ms_packsStorage == null) {
			ms_packsStorage = new PacksStorage();
		}

		return ms_packsStorage;
	}

	// ------------------------------------------------------------------------

	private Repos m_repos;
	private MessageConsoleStream m_out;
	private List<IPacksStorageListener> m_listeners;

	private Node m_packsTree;
	private Map<String, Map<String, Leaf>> m_packsMap;

	public PacksStorage() {
		m_repos = Repos.getInstance();

		m_out = Activator.getConsoleOut();

		m_packsTree = new Node(Type.ROOT);

		m_packsMap = new TreeMap<String, Map<String, Leaf>>();
		m_listeners = new ArrayList<IPacksStorageListener>();
	}

	// For convenient recursive search, a simple tree with all
	// versions below a root node.
	public Node getPacksTree() {
		return m_packsTree;
	}

	public Map<String, Map<String, Leaf>> getPacksMap() {
		return m_packsMap;
	}

	public String makeMapKey(String vendorName, String packName) {

		String key = vendorName + "::" + packName;
		return key;
	}

	public Leaf getPackVersion(String vendorName, String packName,
			String versionName) {

		String key = makeMapKey(vendorName, packName);
		Map<String, Leaf> versionsMap = m_packsMap.get(key);

		if (versionsMap == null) {
			return null;
		}

		// May return null
		return versionsMap.get(versionName);
	}

	public Leaf getPackLatest(String vendorName, String packName) {

		String key = makeMapKey(vendorName, packName);
		Map<String, Leaf> versionsMap = m_packsMap.get(key);

		if (versionsMap == null) {
			return null;
		}

		Leaf node = null;
		for (String versionName : versionsMap.keySet()) {
			node = versionsMap.get(versionName);
		}

		// If the map is sorted (as it should be), this is the package
		// with the highest version (or null).
		return node;
	}

	// ------------------------------------------------------------------------

	public void addListener(IPacksStorageListener listener) {
		if (!m_listeners.contains(listener)) {
			m_listeners.add(listener);
		}
	}

	public void removeListener(IPacksStorageListener listener) {
		m_listeners.remove(listener);
	}

	public void fireChanged() {

		System.out.println("PacksStorage fireChanged()");
		// System.out.println(this);
		PacksStorageEvent event = new PacksStorageEvent(this,
				PacksStorageEvent.Type.REFRESH);

		for (IPacksStorageListener listener : m_listeners) {
			// System.out.println(listener);
			listener.packsChanged(event);
		}
	}

	// Executed as a separate job from plug-in activator
	public IStatus loadRepositories(IProgressMonitor monitor) {

		long beginTime = System.currentTimeMillis();

		m_out.println();
		m_out.println(Utils.getCurrentDateTime());

		List<Map<String, Object>> reposList;
		reposList = m_repos.getList();

		int workUnits = reposList.size();
		workUnits++; // For post processing
		monitor.beginTask("Load packs repositories", workUnits);

		parseRepos(monitor);

		// Notify listeners (currently the views) that the packs changed
		// (for just in case this takes very long, normally the views are
		// not created at this moment)

		// System.out.println(this);
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				// System.out.println("run()");
				fireChanged();
			}
		});

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

		System.out.println("loadRepositories() completed");

		return Status.OK_STATUS;
	}

	public int getParseReposWorkUnits() {

		List<Map<String, Object>> reposList;
		reposList = m_repos.getList();

		return reposList.size() + 1;
	}

	public void parseRepos(IProgressMonitor monitor) {

		m_out.println("Loading repos summaries...");

		List<Map<String, Object>> reposList;
		reposList = m_repos.getList();

		for (Map<String, Object> map : reposList) {
			String type = (String) map.get("type");
			String name = (String) map.get("name");
			String url = (String) map.get("url");

			monitor.subTask(name);
			if (Repos.CMSIS_PACK_TYPE.equals(type)
					|| Repos.XCDL_CMSIS_PACK_TYPE.equals(type)) {

				String fileName = m_repos.getRepoContentXmlFromUrl(url);

				try {
					Node node = parseContentFile(fileName);
					map.put("content", node);
				} catch (IOException e) {
					m_out.println(e.getMessage());
				} catch (ParserConfigurationException e) {
					m_out.println(e.toString());
				} catch (SAXException e) {
					m_out.println(e.toString());
				}
			}
			monitor.worked(1);
		}

		monitor.subTask("Post processing");

		postProcessRepos();

		updateInstalledVersions();

		monitor.worked(1);
	}

	private Node parseContentFile(String fileName) throws IOException,
			ParserConfigurationException, SAXException {

		long beginTime = System.currentTimeMillis();

		File file;
		file = m_repos.getFileObject(fileName);

		m_out.println("Parsing \"" + file.getPath() + "\"...");

		if (!file.exists()) {
			throw new IOException("File does not exist, ignored.");
		}

		Document document;
		document = Utils.parseXml(file);

		ContentParser parser = new ContentParser(document);
		Node node = parser.parseDocument();

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}

		m_out.println("File parsed in " + duration + "ms.");

		return node;
	}

	private void postProcessRepos() {

		List<Map<String, Object>> reposList;
		reposList = m_repos.getList();

		// Collect all version nodes in a list
		List<Leaf> packsVersionsList = new LinkedList<Leaf>();
		for (Map<String, Object> map : reposList) {

			Leaf node = (Node) map.get("content");
			if (node != null) {
				getVersionsRecursive(node, packsVersionsList);
			}
		}

		// Group versions by [vendor::package]
		Map<String, Map<String, Leaf>> packsMap = new TreeMap<String, Map<String, Leaf>>();
		for (Leaf versionNode : packsVersionsList) {
			String vendorName = versionNode.getProperty(Property.VENDOR_NAME);
			String packName = versionNode.getProperty(Property.PACK_NAME);
			String key = vendorName + "::" + packName;

			Map<String, Leaf> versionMap;
			versionMap = packsMap.get(key);
			if (versionMap == null) {
				versionMap = new TreeMap<String, Leaf>();
				packsMap.put(key, versionMap);
			}

			versionMap.put(versionNode.getName(), versionNode);
		}

		// Create a one level hierarchy with all packages, to ease recursion
		Node packsTree = new Node(Type.ROOT);
		for (Leaf versionNode : packsVersionsList) {
			packsTree.addChild(versionNode);
		}

		// Make them publicly available
		m_packsTree = packsTree;
		m_packsMap = packsMap;

		m_out.println("Processed " + packsMap.size() + " package(s), with "
				+ packsVersionsList.size() + " version(s).");
	}

	private void getVersionsRecursive(Leaf node, List<Leaf> list) {

		if (Type.VERSION.equals(node.getType())) {
			list.add(node);
			// Stop recursion here
		} else if (node.hasChildren()) {
			for (Leaf child : node.getChildren()) {
				getVersionsRecursive(child, list);
			}
		}

	}

	public void updateInstalledVersions() {

		List<Leaf> versionNodes = m_packsTree.getChildren();
		if (versionNodes == null) {
			return;
		}

		m_out.println("Updating installed packages...");

		int count = 0;

		// Check if the packages are installed
		for (Leaf versionNode : versionNodes) {

			String unpackFolder = versionNode.getProperty(Property.DEST_FOLDER);
			String pdscName = versionNode.getProperty(Property.PDSC_NAME);

			String pdscRelativePath = unpackFolder + '/' + pdscName;

			try {
				// Test if the pdsc file exists in the package folder
				File file = m_repos.getFileObject(pdscRelativePath);
				if (file.exists()) {

					// Add an explicit property for more visibility
					versionNode.setBooleanProperty(Property.INSTALLED, true);

					count++;
				}
			} catch (Exception e) {
				;
			}

		}
		m_out.println("Updated " + count + " installed packages.");
	}

	// -----------------------------------------------------------------------
	// Deprecated

	public static Node ms_tree = null;

	public void putCache_x(Node tree) throws IOException {

		File file = m_repos.getFileObject(CACHE_FILE_NAME);

		// The xml structure is simple, write it as strings
		if (!file.exists())
			file.createNewFile();
		if (file.exists()) {
			PrintWriter writer = new PrintWriter(new BufferedWriter(
					new FileWriter(file)));
			writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			writer.println("<root version=\"" + CACHE_XML_VERSION + "\">");

			putCacheNodeRecursive_x(tree, 0, writer);

			writer.println("</root>");
			writer.close();

			// System.out.println(SITES_FILE_NAME+" saved");
		}

		ms_tree = tree;
	}

	private void putCacheNodeRecursive_x(Leaf node, int depth,
			PrintWriter writer) {

		putIndentation_x(depth, writer);
		writer.println("<node type=\"" + node.getType() + "\" name=\""
				+ node.getName() + "\">");

		String description = node.getDescription();
		if (description != null && description.length() > 0) {
			putIndentation_x(depth + 1, writer);
			writer.println("<description>"
					+ Utils.xmlEscape(node.getDescription()) + "</description>");
		}

		if (node.hasProperties()) {
			putIndentation_x(depth + 1, writer);
			writer.println("<properties>");

			Map<String, String> properties = node.getProperties();
			for (Object key : properties.keySet()) {
				putIndentation_x(depth + 2, writer);
				writer.println("<property name=\"" + key.toString()
						+ "\" value=\"" + properties.get(key).toString()
						+ "\" />");
			}

			putIndentation_x(depth + 1, writer);
			writer.println("</properties>");
		}

		// TODO: make it PackNode
		if (node instanceof Node) {
			if (((Node) node).hasConditions()) {
				putIndentation_x(depth + 1, writer);
				writer.println("<conditions>");

				List<Selector> conditions = ((Node) node).getConditions();
				for (Selector condition : conditions) {
					putIndentation_x(depth + 2, writer);
					writer.print("<condition type=\"" + condition.getType()
							+ "\"");
					writer.print(" value=\"" + condition.getValue() + "\"");
					if (condition.hasVendor()) {
						writer.print(" vendor=\"" + condition.getVendor()
								+ "\"");
					}
					if (condition.hasVendorId()) {
						writer.print(" vendorid=\"" + condition.getVendorId()
								+ "\"");
					}
					writer.println(" />");
				}

				putIndentation_x(depth + 1, writer);
				writer.println("</conditions>");
			}
			if (((Node) node).hasOutline()) {
				putIndentation_x(depth + 1, writer);
				writer.println("<outline>");

				Node outlineNode = ((Node) node).getOutline();
				for (Leaf outlineChild : outlineNode.getChildrenArray()) {
					putCacheNodeRecursive_x(outlineChild, depth + 2, writer);
				}

				putIndentation_x(depth + 1, writer);
				writer.println("</outline>");
			}
		}

		List<Leaf> children = node.getChildren();
		if (children != null && !children.isEmpty()) {
			putIndentation_x(depth + 1, writer);
			writer.println("<nodes>");

			for (Leaf child : children) {
				putCacheNodeRecursive_x(child, depth + 2, writer);
			}

			putIndentation_x(depth + 1, writer);
			writer.println("</nodes>");
		}
		putIndentation_x(depth, writer);
		writer.println("</node>");
	}

	private void putIndentation_x(int depth, PrintWriter writer) {
		depth++;
		for (int i = 0; i < depth; ++i) {
			writer.print("  ");
		}
	}

	public Leaf getCachedSubTree_x(String type) throws IOException,
			ParserConfigurationException, SAXException {

		Node rootNode = getCachedTree_x();
		for (Leaf childNode : rootNode.getChildren()) {
			if (childNode.getType().equals(type)) {
				return childNode;
			}
		}

		throw new IOException("No such node type " + type + ".");
	}

	public Node getCachedTree_x() throws IOException,
			ParserConfigurationException, SAXException {
		return getCachedTree_x(false);
	}

	public Node getCachedTree_x(boolean doReload) throws IOException,
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
		ms_tree = getCacheRecursive_x(nodeRootElement);

		updateInstalled_x();

		return ms_tree;
	}

	public void updateInstalled_x() throws IOException,
			ParserConfigurationException, SAXException {
		// List<TreeNode> deviceNodes = new LinkedList<TreeNode>();
		// List<TreeNode> boardNodes = new LinkedList<TreeNode>();

		// final List<TreeNode>[] lists = (List<TreeNode>[]) (new List<?>[] {
		// deviceNodes, boardNodes });

		// Update installed nodes
		IPath path = new Path(m_repos.getFolderPathString());
		updateInstalledNodesRecursive_x(
				getCachedSubTree_x(Type.PACKAGES_SUBTREE), path, true);
	}

	private Node getCacheRecursive_x(Element nodeElement) {

		String type = nodeElement.getAttribute("type");
		Node treeNode = new Node(type);

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

				Selector condition = new Selector(conditionType);
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

			Node outlineNode = new Node(Type.OUTLINE);
			treeNode.setOutline(outlineNode);

			for (Element childElement : nodeElements) {

				Node childTreeNode = getCacheRecursive_x((Element) childElement);
				outlineNode.addChild(childTreeNode);
			}
		}

		Element nodesElement = Utils.getChildElement(nodeElement, "nodes");
		if (nodesElement != null) {
			List<Element> nodeElements = Utils.getChildElementsList(
					nodesElement, "node");
			for (Element childElement : nodeElements) {

				Node childTreeNode = getCacheRecursive_x((Element) childElement);
				treeNode.addChild(childTreeNode);
			}
		}

		return treeNode;
	}

	public void updateInstalledNodesRecursive_x(Leaf node, IPath path,
			boolean isInstalled) {

		if (node.hasChildren()) {
			for (Leaf childNode : node.getChildren()) {
				// Extend path with node name and recurse down
				// TODO: use NAME_PROPERTY
				IPath childPath = path.append(childNode.getName());
				updateInstalledNodesRecursive_x(childNode, childPath,
						isInstalled);
			}
		}

		String nodeType = node.getType();
		if (Type.VERSION.equals(nodeType)) {
			File folder = path.toFile();
			if (folder.exists() && folder.isDirectory()) {
				// If the name exists and it is indeed a directory,
				// then the package is probably installed.

				// Update PacksView and related from Devices & Boards
				updateInstalledVersionNode_x((Node) node, isInstalled, null);
			}
		}
	}

	public void updateInstalledVersionNode_x(Node versionNode,
			boolean isInstalled, List<Node>[] lists) {

		List<Node> deviceNodes;
		List<Node> boardNodes;

		if (lists != null) {
			deviceNodes = lists[0];
			boardNodes = lists[1];
		} else {
			deviceNodes = new LinkedList<Node>();
			boardNodes = new LinkedList<Node>();
		}

		String type = versionNode.getType();
		if (!Type.VERSION.equals(type)) {
			return;
		}

		versionNode.setIsInstalled(isInstalled);
		Node packNode = versionNode.getParent();

		boolean doMarkPackage = true;
		if (!isInstalled) {
			for (Leaf child : packNode.getChildrenArray()) {
				if (child.isInstalled()) {
					doMarkPackage = false;
				}
			}
		}
		if (doMarkPackage) {
			packNode.setIsInstalled(isInstalled);
		}

		List<Selector> conditionsList = versionNode.getParent()
				.getConditionsByType(Selector.DEVICEFAMILY_TYPE);
		if (conditionsList.size() > 0) {
			for (Selector condition : conditionsList) {
				updateDeviceInstalled_x(condition.getVendorId(),
						condition.getValue(), isInstalled, deviceNodes);
			}
		}

		conditionsList = versionNode.getParent().getConditionsByType(
				Selector.BOARD_TYPE);
		if (conditionsList.size() > 0) {
			for (Selector condition : conditionsList) {
				updateBoardInstalled_x(condition.getVendor(),
						condition.getValue(), isInstalled, boardNodes);
			}
		}

		versionNode.removeChildren();
		versionNode.setOutline(null);
	}

	private void updateDeviceInstalled_x(String vendorId, String familyName,
			boolean isInstalled, List<Node> deviceNodes) {

		try {
			Node devicesTree = (Node) getCachedSubTree_x(Type.DEVICES_SUBTREE);

			// Assume the two level hierarchy, vendor/devicefamily
			for (Leaf vendorNode : devicesTree.getChildrenArray()) {

				// Select vendors that match the given vendor id
				if (vendorId.equals(vendorNode
						.getProperty(Node.VENDORID_PROPERTY))) {

					for (Leaf familyNode : vendorNode.getChildrenArray()) {
						// Select family
						if (familyName.equals(familyNode.getName())) {

							((Node) familyNode).setIsInstalled(isInstalled);
							deviceNodes.add((Node) familyNode);
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	private void updateBoardInstalled_x(String vendor, String boardName,
			boolean isInstalled, List<Node> boardNodes) {

		try {
			Node boardsTree = (Node) getCachedSubTree_x(Type.BOARDS_SUBTREE);

			// Assume the two level hierarchy, vendor/board
			for (Leaf vendorNode : boardsTree.getChildrenArray()) {

				// Select vendors that match the given vendor name
				if (vendor.equals(vendorNode.getName())) {

					for (Leaf boardNode : vendorNode.getChildrenArray()) {
						// Select board
						if (boardName.equals(boardNode.getName())) {

							((Node) boardNode).setIsInstalled(isInstalled);
							boardNodes.add((Node) boardNode);
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	public List<Node> getInstalledVersions_x() {

		List<Node> list = new LinkedList<Node>();
		try {
			Node node;
			node = (Node) getCachedSubTree_x(Type.PACKAGES_SUBTREE);
			getInstalledVersionsRecursive_x(node, list);
		} catch (Exception e) {
		}
		return list;
	}

	public void getInstalledVersionsRecursive_x(Leaf node, List<Node> list) {

		if (node.hasChildren()) {
			for (Leaf child : node.getChildren()) {
				getInstalledVersionsRecursive_x(child, list);
			}
		}

		if (Type.VERSION.equals(node.getType())) {
			if (((Node) node).isInstalled()) {
				list.add(((Node) node));
			}
		}
	}

}
