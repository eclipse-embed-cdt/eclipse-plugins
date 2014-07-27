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

import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.Preferences;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Selector;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.xcdl.ContentParser;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.preference.IPreferenceStore;
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

	private static IPath sfFolderPath = null;

	// ------------------------------------------------------------------------

	// Return a file object in Packages
	public static File getFileObject(String name) throws IOException {

		IPath path = getFolderPath().append(name);
		File file = path.toFile();
		if (file == null) {
			throw new IOException(name + " File object null.");
		}
		return file; // Cannot return null
	}

	// Return a file object in Packages/.cache
	public static File getCachedFileObject(String name) throws IOException {

		IPath path = getFolderPath().append(CACHE_FOLDER).append(name);
		File file = path.toFile();
		if (file == null) {
			throw new IOException(name + " File object null.");
		}
		return file; // Cannot return null
	}

	// Return the absolute 'Packages' path.
	public static IPath getFolderPath() throws IOException {

		if (sfFolderPath == null) {

			sfFolderPath = new Path(getFolderPathString());
		}

		return sfFolderPath;
	}

	// Return a string with the absolute full path of the folder used
	// to store packages
	public static String getFolderPathString() throws IOException {

		IPreferenceStore store = Preferences.getPreferenceStore();
		String folderPath = store.getString(Preferences.PACKS_FOLDER_PATH)
				.trim();

		if (folderPath == null) {
			throw new IOException("Missing folder path.");
		}

		// Remove the terminating separator
		if (folderPath.endsWith(String.valueOf(IPath.SEPARATOR))) {
			folderPath = folderPath.substring(0, folderPath.length() - 1);
		}

		if (folderPath.length() == 0) {
			throw new IOException("Missing folder path.");
		}
		return folderPath;
	}

	public static String makeCachedPdscName(String name, String version) {

		String s;

		s = name;
		int ix = s.lastIndexOf('.');
		if (ix > 0) {
			// Insert .version before extension
			s = s.substring(0, ix) + "." + version + s.substring(ix);
		}
		return s;
	}

	// ========================================================================
	// To be reorganised (moved to DataManager or removed)

	private static PacksStorage sfInstance;

	public static synchronized PacksStorage getInstance() {

		if (sfInstance == null) {
			sfInstance = new PacksStorage();
		}

		return sfInstance;
	}

	// ------------------------------------------------------------------------

	private Repos fRepos;
	private MessageConsoleStream fOut;
	// private List<IDataManagerListener> fListeners;

	private List<PackNode> fPacksVersionsList;
	private Node fPacksVersionsTree;
	private Map<String, Map<String, PackNode>> fPacksVersionsMap;
	private Map<String, PackNode> fPacksMap;

	private Map<String, PackNode> fDevicesMap;

	public PacksStorage() {

		fRepos = Repos.getInstance();

		fOut = ConsoleStream.getConsoleOut();

		fPacksVersionsTree = new Node(Type.ROOT);
		fPacksVersionsList = new LinkedList<PackNode>();
		fPacksVersionsMap = new TreeMap<String, Map<String, PackNode>>();
		fPacksMap = new TreeMap<String, PackNode>();

		// fListeners = new ArrayList<IDataManagerListener>();

		fDevicesMap = new TreeMap<String, PackNode>();

	}

	// For convenient recursive search, a full tree with all
	// repos/packs/versions is available.
	public Node getPacksTree() {
		return fPacksVersionsTree;
	}

	// The list of all version nodes, linearised, to be used when
	// enumerating versions is ok.
	public List<PackNode> getPacksVersionsList() {
		return fPacksVersionsList;
	}

	// A map of maps, with versions identified by Vendor::Pack and Version
	public Map<String, Map<String, PackNode>> getPacksVersionsMap() {
		return fPacksVersionsMap;
	}

	// A map of packages, identified by Vendor::Pack
	public Map<String, PackNode> getPacksMap() {
		return fPacksMap;
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
		Map<String, PackNode> versionsMap = fPacksVersionsMap.get(key);

		if (versionsMap == null) {
			return null;
		}

		// May return null
		return (PackNode) versionsMap.get(versionName);
	}

	// Find the latest version of a pack
	public PackNode getPackLatest(String vendorName, String packName) {

		String key = makeMapKey(vendorName, packName);
		Map<String, PackNode> versionsMap = fPacksVersionsMap.get(key);

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
		for (PackNode versionNode : fPacksVersionsList) {
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

	public int computeParseReposWorkUnits() {

		List<Map<String, Object>> reposList;
		reposList = fRepos.getList();

		return reposList.size() + 1;
	}

	public void parseRepos(IProgressMonitor monitor) {

		fOut.println("Loading repos summaries...");

		List<Map<String, Object>> reposList;
		reposList = fRepos.getList();

		for (Map<String, Object> map : reposList) {
			String type = (String) map.get("type");
			String name = (String) map.get("name");
			String url = (String) map.get("url");

			monitor.subTask(name);
			if (Repos.CMSIS_PACK_TYPE.equals(type)
					|| Repos.XCDL_CMSIS_PACK_TYPE.equals(type)) {

				String fileName = fRepos.getRepoContentXmlFromUrl(url);

				try {
					Node node = parseContentFile(fileName);
					map.put("content", node);
				} catch (IOException e) {
					fOut.println(e.getMessage());
				} catch (ParserConfigurationException e) {
					fOut.println(e.toString());
				} catch (SAXException e) {
					fOut.println(e.toString());
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
		file = getFileObject(fileName);

		fOut.println("Parsing \"" + file.getPath() + "\"...");

		if (!file.exists()) {
			throw new IOException("File does not exist, ignored.");
		}

		Document document = Xml.parseFile(file);
		ContentParser parser = new ContentParser();
		Node node = parser.parse(document);

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}

		fOut.println("File parsed in " + duration + "ms.");

		return node;
	}

	private void preparePublicData() {

		List<Map<String, Object>> reposList;
		reposList = fRepos.getList();

		// Create a full hierarchy with all repos
		Node packsTree = new Node(Type.ROOT);
		packsTree.setName("PacksTree");

		for (Map<String, Object> map : reposList) {

			Leaf node = (Node) map.get("content");
			if (node != null) {
				packsTree.addChild(node);
			}
		}
		fPacksVersionsTree = packsTree;

		// Collect all version nodes in a list
		List<PackNode> packsVersionsList = new LinkedList<PackNode>();
		for (Map<String, Object> map : reposList) {

			Leaf node = (Node) map.get("content");
			if (node != null) {
				getVersionsRecursive(node, packsVersionsList);
			}
		}
		fPacksVersionsList = packsVersionsList;

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
		fPacksVersionsMap = packsVersionsMap;

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
		fPacksMap = packsMap;

		if (packsVersionsMap.size() == 0) {
			fOut.println("Processed no packages.");
		} else {
			fOut.println("Processed " + packsVersionsMap.size()
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

		for (PackNode packNode : fPacksMap.values()) {

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

	// Add (Property.INSTALLED, true) to version node ant to parent pack node
	public void updateInstalledVersions() {

		if (fPacksVersionsList == null) {
			return;
		}

		if (fPacksVersionsList.size() == 0) {
			return;
		}

		fOut.println("Identifying installed packages...");

		int count = 0;

		// Check if the packages are installed
		for (Leaf versionNode : fPacksVersionsList) {

			String unpackFolder = versionNode.getProperty(Property.DEST_FOLDER);
			String pdscName = versionNode.getProperty(Property.PDSC_NAME);

			String pdscRelativePath = unpackFolder + '/' + pdscName;

			try {
				// Test if the pdsc file exists in the package folder
				File file = getFileObject(pdscRelativePath);
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
			fOut.println("Found no installed packages.");
		} else {
			fOut.println("Found " + count + " installed packages.");
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

	public File getFile(IPath pathPart, String name) {

		IPath path;
		try {
			path = getFolderPath().append(pathPart).append(name);
		} catch (IOException e) {
			return null;
		}
		File file = path.toFile();
		new File(file.getParent()).mkdirs();

		return file; // may be null
	}

	// Return the cached file for the name (relative to CACHE folder)
	public File getCachedFile(String fileName) {

		return getFile(new Path(CACHE_FOLDER), fileName);
	}

	public File getCachedPdsc(String pdscName, String version) {

		return getFile(new Path(CACHE_FOLDER),
				makeCachedPdscName(pdscName, version));
	}

	public PackNode getLatestInstalledPackForDevice(String deviceVendorId,
			String deviceName) {

		String key = makeMapKey(deviceVendorId, deviceName);
		if (fDevicesMap.containsKey(key)) {
			return fDevicesMap.get(key);
		}

		Node tree = getPacksTree();
		PackNode pack = getLatestInstalledPackForDeviceRecursive(tree,
				deviceVendorId, deviceName);

		if (pack != null) {
			fDevicesMap.put(key, pack);
		}

		return pack;
	}

	private PackNode getLatestInstalledPackForDeviceRecursive(Node parent,
			String deviceVendorId, String deviceName) {

		if (parent.isType(Type.PACKAGE)) {

			if (!parent.isBooleanProperty(Property.INSTALLED)) {
				return null; // skip packages not installed
			}
			if (!parent.hasChildren()) {
				return null;
			}
			for (Leaf node : parent.getChildren()) {
				if (node.isBooleanProperty(Property.INSTALLED)) {
					return getLatestInstalledPackForDeviceRecursive(
							(Node) node, deviceVendorId, deviceName);
				}
			}
			return null; // No installed versions

		} else if (parent.isType(Type.DEVICE)) {

		}

		return null;
	}

	public Node getLatestInstalledPackForBoard(String boardVendorName,
			String boardName) {

		return null;
	}
}
