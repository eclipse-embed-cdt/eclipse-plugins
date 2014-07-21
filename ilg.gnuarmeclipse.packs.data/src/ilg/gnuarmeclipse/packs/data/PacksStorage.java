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

package ilg.gnuarmeclipse.packs.data;

import ilg.gnuarmeclipse.packs.cmsis.PdscParserForBuild;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Selector;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.xcdl.ContentParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PacksStorage {

	// public static final String SITES_FILE_NAME = "sites.xml";
	// public static final String CACHE_FILE_NAME = ".cache.xml";
	// public static final String CACHE_XML_VERSION = "1.1";

	// public static final String DOWNLOAD_FOLDER = ".download";
	public static final String CACHE_FOLDER = ".cache";

	public static final String CONTENT_FILE_NAME_SUFFIX = "content.xml";
	public static final String CONTENT_XML_VERSION = "1.1";

	public static final String DEVICES_FILE_NAME = ".devicesForBuild.xml";

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

	private List<PackNode> m_packsVersionsList;
	private Node m_packsVersionsTree;
	private Map<String, Map<String, PackNode>> m_packsVersionsMap;
	private Map<String, PackNode> m_packsMap;

	private Node fInstalledDevicesForBuild;

	public PacksStorage() {

		m_repos = Repos.getInstance();

		m_out = ConsoleStream.getConsoleOut();

		m_packsVersionsTree = new Node(Type.ROOT);
		m_packsVersionsList = new LinkedList<PackNode>();
		m_packsVersionsMap = new TreeMap<String, Map<String, PackNode>>();
		m_packsMap = new TreeMap<String, PackNode>();

		m_listeners = new ArrayList<IPacksStorageListener>();

		fInstalledDevicesForBuild = null;
	}

	// For convenient recursive search, a full tree with all
	// repos/packs/versions is available.
	public Node getPacksTree() {
		return m_packsVersionsTree;
	}

	// The list of all version nodes, linearised, to be used when
	// enumerating versions is ok.
	public List<PackNode> getPacksVersionsList() {
		return m_packsVersionsList;
	}

	// A map of maps, with versions identified by Vendor::Pack and Version
	public Map<String, Map<String, PackNode>> getPacksVersionsMap() {
		return m_packsVersionsMap;
	}

	// A map of packages, identified by Vendor::Pack
	public Map<String, PackNode> getPacksMap() {
		return m_packsMap;
	}

	// Construct an internal representation of the map key, inspired
	// form CMSIS Pack examples.
	public String makeMapKey(String vendorName, String packName) {

		String key = vendorName + "::" + packName;
		return key;
	}

	// Find a given package version.
	public PackNode getPackVersion(String vendorName, String packName,
			String versionName) {

		String key = makeMapKey(vendorName, packName);
		Map<String, PackNode> versionsMap = m_packsVersionsMap.get(key);

		if (versionsMap == null) {
			return null;
		}

		// May return null
		return (PackNode) versionsMap.get(versionName);
	}

	// Find the latest version of a pack
	public PackNode getPackLatest(String vendorName, String packName) {

		String key = makeMapKey(vendorName, packName);
		Map<String, PackNode> versionsMap = m_packsVersionsMap.get(key);

		if (versionsMap == null) {
			return null;
		}

		PackNode node = null;
		for (String versionName : versionsMap.keySet()) {
			node = versionsMap.get(versionName);
		}

		// If the map is sorted (as it should be), this is the package
		// with the highest version (or null).
		return node;
	}

	// Filter the versions to the installed latest ones
	public List<PackNode> getInstalledPacksLatestVersionsList() {

		// Filter installed packages
		List<PackNode> installedPackages = new LinkedList<PackNode>();
		for (PackNode versionNode : m_packsVersionsList) {
			if (versionNode.isBooleanProperty(Property.INSTALLED)) {
				installedPackages.add((PackNode) versionNode.getParent());
			}
		}

		// Filter only the latest versions (first installed in children list)
		List<PackNode> installedLatestVersions = new LinkedList<PackNode>();
		for (PackNode packNode : installedPackages) {
			List<Leaf> children = packNode.getChildren();
			for (Leaf node : children) {
				if (node.isBooleanProperty(Property.INSTALLED)) {
					installedLatestVersions.add((PackNode) node);
					break;
				}
			}
		}

		return installedLatestVersions;
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

	// Used by 'Refresh', 'LoadReposSummaries'
	public void notifyNewInput() {

		// System.out.println("PacksStorage notifyRefresh()");
		PacksStorageEvent event = new PacksStorageEvent(this,
				PacksStorageEvent.Type.NEW_INPUT);

		notifyListener(event);
	}

	public void notifyRefreshAll() {

		// System.out.println("PacksStorage notifyRefresh()");
		PacksStorageEvent event = new PacksStorageEvent(this,
				PacksStorageEvent.Type.REFRESH_ALL);

		notifyListener(event);
	}

	// 'Install/Remove Pack' notifies Type.UPDATE_VERSIONS
	public void notifyUpdateView(String type, List<Leaf> list) {

		// System.out.println("PacksStorage notifyUpdateView()");
		PacksStorageEvent event = new PacksStorageEvent(this, type, list);

		notifyListener(event);

		// Force to re-cache
		fInstalledDevicesForBuild = null;
	}

	public void notifyListener(PacksStorageEvent event) {

		for (IPacksStorageListener listener : m_listeners) {
			// System.out.println(listener);
			listener.packsChanged(event);
		}
	}

	public int computeParseReposWorkUnits() {

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

		preparePublicData();

		addSelectors();

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

	private void preparePublicData() {

		List<Map<String, Object>> reposList;
		reposList = m_repos.getList();

		// Create a full hierarchy with all repos
		Node packsTree = new Node(Type.ROOT);
		packsTree.setName("PacksTree");

		for (Map<String, Object> map : reposList) {

			Leaf node = (Node) map.get("content");
			if (node != null) {
				packsTree.addChild(node);
			}
		}
		m_packsVersionsTree = packsTree;

		// Collect all version nodes in a list
		List<PackNode> packsVersionsList = new LinkedList<PackNode>();
		for (Map<String, Object> map : reposList) {

			Leaf node = (Node) map.get("content");
			if (node != null) {
				getVersionsRecursive(node, packsVersionsList);
			}
		}
		m_packsVersionsList = packsVersionsList;

		// Group versions by [vendor::package] in a Map
		Map<String, Map<String, PackNode>> packsVersionsMap = new TreeMap<String, Map<String, PackNode>>();
		for (PackNode versionNode : packsVersionsList) {
			String vendorName = versionNode.getProperty(Property.VENDOR_NAME);
			String packName = versionNode.getProperty(Property.PACK_NAME);
			String key = makeMapKey(vendorName, packName);

			Map<String, PackNode> versionMap;
			versionMap = packsVersionsMap.get(key);
			if (versionMap == null) {
				versionMap = new TreeMap<String, PackNode>();
				packsVersionsMap.put(key, versionMap);
			}

			versionMap.put(versionNode.getName(), versionNode);
		}
		m_packsVersionsMap = packsVersionsMap;

		// Group packages by [vendor::package] in a Map
		Map<String, PackNode> packsMap = new TreeMap<String, PackNode>();
		for (PackNode versionNode : packsVersionsList) {
			String vendorName = versionNode.getProperty(Property.VENDOR_NAME);
			String packName = versionNode.getProperty(Property.PACK_NAME);
			String key = makeMapKey(vendorName, packName);

			if (!packsMap.containsKey(key)) {
				PackNode parent = (PackNode) versionNode.getParent();
				packsMap.put(key, parent);
			}
		}
		m_packsMap = packsMap;

		if (packsVersionsMap.size() == 0) {
			m_out.println("Processed no packages.");
		} else {
			m_out.println("Processed " + packsVersionsMap.size()
					+ " package(s), with " + packsVersionsList.size()
					+ " version(s).");
		}
	}

	private void getVersionsRecursive(Leaf node, List<PackNode> list) {

		if (Type.VERSION.equals(node.getType())) {
			list.add((PackNode) node);
			// Stop recursion here
		} else if (node.hasChildren()) {
			for (Leaf child : ((Node) node).getChildren()) {
				getVersionsRecursive(child, list);
			}
		}

	}

	private void addSelectors() {

		// Add unique selectors to each package, by inspecting the outline and
		// external definitions in the latest version.

		for (PackNode packNode : m_packsMap.values()) {

			Node versionNode = (Node) packNode.getChildren().get(0);
			if (versionNode.hasChildren()) {
				for (Leaf child : versionNode.getChildren()) {

					if (child.isType(Type.OUTLINE)
							|| child.isType(Type.EXTERNAL)) {
						if (child.hasChildren()) {
							for (Leaf node : ((Node) child).getChildren()) {

								Selector selector = null;

								String type = node.getType();
								if (Type.FAMILY.equals(type)) {

									selector = new Selector(
											Selector.Type.DEVICEFAMILY);
									selector.setValue(node.getName());
									selector.setVendorId(node
											.getProperty(Property.VENDOR_ID));

								} else if (Type.BOARD.equals(type)) {

									selector = new Selector(Selector.Type.BOARD);
									selector.setValue(node.getName());
									selector.setVendor(node
											.getProperty(Property.VENDOR_NAME));

								} else if (Type.KEYWORD.equals(type)) {

									selector = new Selector(
											Selector.Type.KEYWORD);
									selector.setValue(node.getName());
								}

								if (selector != null) {
									packNode.addSelector(selector);
								}
							}
						}
					}
				}
			}
		}
	}

	public void updateInstalledVersions() {

		if (m_packsVersionsList == null) {
			return;
		}

		if (m_packsVersionsList.size() == 0) {
			return;
		}

		m_out.println("Identifying installed packages...");

		int count = 0;

		// Check if the packages are installed
		for (Leaf versionNode : m_packsVersionsList) {

			String unpackFolder = versionNode.getProperty(Property.DEST_FOLDER);
			String pdscName = versionNode.getProperty(Property.PDSC_NAME);

			String pdscRelativePath = unpackFolder + '/' + pdscName;

			try {
				// Test if the pdsc file exists in the package folder
				File file = m_repos.getFileObject(pdscRelativePath);
				if (file.exists()) {

					// Add an explicit property for more visibility
					versionNode.setBooleanProperty(Property.INSTALLED, true);
					if (versionNode.getParent().isType(Type.PACKAGE)) {
						versionNode.getParent().setBooleanProperty(
								Property.INSTALLED, true);
					}

					count++;
				}
			} catch (Exception e) {
				;
			}

		}
		if (count == 0) {
			m_out.println("Found no installed packages.");
		} else {
			m_out.println("Found " + count + " installed packages.");
		}
	}

	public void computeUpdatedNodes(Node versionNode, boolean isInstall,
			List<Leaf> devicesList, List<Leaf> boardsList) {

		if (!versionNode.isType(Type.VERSION)) {
			return;
		}

		// These will directly update model data
		versionNode.setBooleanProperty(Property.INSTALLED, isInstall);

		Node packNode = versionNode.getParent();

		boolean doMarkPackage = true;
		if (!isInstall) {
			for (Leaf child : packNode.getChildren()) {
				if (child.isBooleanProperty(Property.INSTALLED)) {
					doMarkPackage = false;
				}
			}
		}

		if (doMarkPackage) {
			packNode.setBooleanProperty(Property.INSTALLED, isInstall);
		}

		String vendorName = versionNode.getProperty(Property.VENDOR_NAME);
		String packName = versionNode.getProperty(Property.PACK_NAME);
		String versionName = versionNode.getName();

		Node origNode = (Node) getPackVersion(vendorName, packName, versionName);
		Node outlinesNode = (Node) origNode.getChild(Type.OUTLINE);

		if (outlinesNode != null && outlinesNode.hasChildren()) {

			for (Leaf child : outlinesNode.getChildren()) {

				// Use model node to pass enabled info to view
				child.setBooleanProperty(Property.ENABLED, isInstall);

				if (child.isType(Type.FAMILY)) {
					devicesList.add(child);
				} else if (child.isType(Type.BOARD)) {
					boardsList.add(child);
				}
			}

			if (!isInstall) {
				// Must remove those
			}
		}

	}

	public String makeCachePdscName(String name, String version) {

		String s;

		s = name;
		int ix = s.lastIndexOf('.');
		if (ix > 0) {
			// Insert .version before extension
			s = s.substring(0, ix) + "." + version + s.substring(ix);
		}
		return s;
	}

	public File getFile(IPath pathPart, String name) {

		IPath path;
		try {
			path = m_repos.getFolderPath().append(pathPart).append(name);
		} catch (IOException e) {
			return null;
		}
		File file = path.toFile();
		new File(file.getParent()).mkdirs();

		return file; // may be null
	}

	// Return the cached file for the name (used to read .pdsc, for example)
	public File getCachedFile(String name) {

		return getFile(new Path(CACHE_FOLDER), name);
	}

	// Called from TabDevice in managedbuild.cross
	public Node getInstalledDevicesForBuild() {

		if (fInstalledDevicesForBuild != null) {

			// Return the in-memory cached tree
			return fInstalledDevicesForBuild;
		}

		Activator.getDefault().waitLoadReposJob();

		System.out.println("LoadRepos must be ready");

		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

			public void run() {
				fInstalledDevicesForBuild = updateInstalledDevicesForBuild();
			}
		});

		return fInstalledDevicesForBuild;
	}

	private Node updateInstalledDevicesForBuild() {

		File devicesFile = getFile(new Path(PacksStorage.CACHE_FOLDER),
				DEVICES_FILE_NAME);

		Node rootNode;
		rootNode = new Node(Type.ROOT);
		Node emptyNode = Node.addNewChild(rootNode, Type.NONE);
		emptyNode.setName("No devices available, install packs first.");

		if (devicesFile != null && devicesFile.exists()) {
			// Parse the cached file
		} else {
			// Parse all installed packages
			Node node = parseInstalledDevicesForBuild();
			if (node != null) {
				rootNode = node;
			}

			// Save cached file for future use
		}

		// rootNode = new Node(Type.ROOT);
		// rootNode.setName("Devices");
		//
		// Node family = Node.addUniqueChild(rootNode, Type.FAMILY, "myFamily");
		// Node subFamily = Node.addUniqueChild(family, Type.SUBFAMILY,
		// "mySubFamily");
		// Node device = Node.addUniqueChild(subFamily, Type.DEVICE,
		// "myDevice");
		return rootNode;
	}

	private Node parseInstalledDevicesForBuild() {

		List<PackNode> versionsList = getInstalledPacksLatestVersionsList();

		PdscParserForBuild pdsc = new PdscParserForBuild();

		Node boardsNode = new Node(Type.NONE);
		boardsNode.setName("Boards");
		Node devicesNode = new Node(Type.NONE);
		devicesNode.setName("Devices");

		long beginTime = System.currentTimeMillis();

		m_out.println();
		m_out.println(Utils.getCurrentDateTime());

		m_out.println("Extracting devices&boards... ");

		int count = 0;
		for (PackNode node : versionsList) {

			String pdscName = node.getProperty(Property.PDSC_NAME);
			String version = node.getName();

			File file = getCachedFile(makeCachePdscName(pdscName, version));

			m_out.println("Parsing \"" + file + "\"");
			try {
				pdsc.parseXml(file);

				pdsc.parseDevices(devicesNode);

				// Using boards requires existing devices to be present,
				// to get the memory map
				pdsc.parseBoards(boardsNode);

				updateBoardsDevices(boardsNode, devicesNode);

				count++;
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}
		m_out.print("Completed in ");
		m_out.print(duration + "ms, ");
		if (count == 0) {
			m_out.println("No packages.");
		} else if (count == 1) {
			m_out.println("1 package.");
		} else {
			m_out.println(count + " packages.");
		}

		if (boardsNode.hasChildren() && devicesNode.hasChildren()) {
			Node rootNode = new Node(Type.ROOT);
			rootNode.addChild(boardsNode);
			rootNode.addChild(devicesNode);

			return rootNode;
		} else if (boardsNode.hasChildren()) {
			return boardsNode;
		} else if (devicesNode.hasChildren()) {
			return devicesNode;
		}

		return null;
	}

	private void updateBoardsDevices(Node boardsNode, Node devicesNode) {

		// TODO: check if boards mounted devices are present in devices.
		// If so, copy memory map, otherwise remove
	}
}
