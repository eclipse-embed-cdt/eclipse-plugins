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
import ilg.gnuarmeclipse.packs.tree.PackNode;
import ilg.gnuarmeclipse.packs.tree.Property;
import ilg.gnuarmeclipse.packs.tree.Selector;
import ilg.gnuarmeclipse.packs.tree.Type;
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class PacksStorage {

	public static final String SITES_FILE_NAME = "sites.xml";
	public static final String CACHE_FILE_NAME = ".cache.xml";
	public static final String CACHE_XML_VERSION = "1.1";

	// public static final String DOWNLOAD_FOLDER = ".download";
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

	private List<PackNode> m_packsVersionsList;
	private Node m_packsVersionsTree;
	private Map<String, Map<String, PackNode>> m_packsVersionsMap;
	private Map<String, PackNode> m_packsMap;

	public PacksStorage() {
		m_repos = Repos.getInstance();

		m_out = Activator.getConsoleOut();

		m_packsVersionsTree = new Node(Type.ROOT);
		m_packsVersionsList = new LinkedList<PackNode>();
		m_packsVersionsMap = new TreeMap<String, Map<String, PackNode>>();
		m_packsMap = new TreeMap<String, PackNode>();

		m_listeners = new ArrayList<IPacksStorageListener>();
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

	// ------------------------------------------------------------------------

	public void addListener(IPacksStorageListener listener) {
		if (!m_listeners.contains(listener)) {
			m_listeners.add(listener);
		}
	}

	public void removeListener(IPacksStorageListener listener) {
		m_listeners.remove(listener);
	}

	public void notifyRefresh() {

		// System.out.println("PacksStorage notifyRefresh()");
		PacksStorageEvent event = new PacksStorageEvent(this,
				PacksStorageEvent.Type.REFRESH);

		notifyListener(event);
	}

	public void notifyUpdateView(String type, List<Leaf> list) {

		// System.out.println("PacksStorage notifyUpdateView()");
		PacksStorageEvent event = new PacksStorageEvent(this, type, list);

		notifyListener(event);
	}

	public void notifyListener(PacksStorageEvent event) {

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
				notifyRefresh();
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

		m_out.println("Processed " + packsVersionsMap.size()
				+ " package(s), with " + packsVersionsList.size()
				+ " version(s).");
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

		m_out.println("Updating installed packages...");

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
		m_out.println("Updated " + count + " installed packages.");
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

}
