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

package ilg.gnumcueclipse.packs.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.console.MessageConsoleStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.core.Xml;
import ilg.gnumcueclipse.core.XpackUtils;
import ilg.gnumcueclipse.packs.cmsis.PdscGenericParser;
import ilg.gnumcueclipse.packs.cmsis.PdscTreeParserForBuild;
import ilg.gnumcueclipse.packs.core.ConsoleStream;
import ilg.gnumcueclipse.packs.core.PackType;
import ilg.gnumcueclipse.packs.core.data.DurationMonitor;
import ilg.gnumcueclipse.packs.core.data.IPacksDataManager;
import ilg.gnumcueclipse.packs.core.data.JsonGenericParser;
import ilg.gnumcueclipse.packs.core.data.JsonJsGenericParser;
import ilg.gnumcueclipse.packs.core.data.PacksStorage;
import ilg.gnumcueclipse.packs.core.data.XcdlGenericParser;
import ilg.gnumcueclipse.packs.core.jstree.JsNode;
import ilg.gnumcueclipse.packs.core.tree.AbstractTreePreOrderIterator;
import ilg.gnumcueclipse.packs.core.tree.ITreeIterator;
import ilg.gnumcueclipse.packs.core.tree.Leaf;
import ilg.gnumcueclipse.packs.core.tree.Node;
import ilg.gnumcueclipse.packs.core.tree.PackNode;
import ilg.gnumcueclipse.packs.core.tree.Property;
import ilg.gnumcueclipse.packs.core.tree.Selector;
import ilg.gnumcueclipse.packs.core.tree.Type;
import ilg.gnumcueclipse.packs.core.xml.GenericParser;
import ilg.gnumcueclipse.packs.core.xml.GenericSerialiser;

/**
 * This singleton class manages all data structures related to packs.
 * 
 * It uses two kind of data:
 * <ul>
 * <li>a summary of ALL existing pack versions
 * <li>full information only on INSTALLED pack versions.
 * </ul>
 * 
 * The main function to access the summary data is:
 * <ul>
 * <li>getRepositoriesTree()
 * </ul>
 * which returns a tree of all repositories/packages/versions/...
 * <p>
 * This tree is prepared when the user updates the packages.
 * <p>
 * The existing package versions can be also accesses with specific functions:
 * <ul>
 * <li>getPacksVersionsList() - ? private
 * <li>findPackVersion()
 * <li>findPackLatest()
 * <li>getInstalledPacksLatestVersionsList()
 * </ul>
 * 
 * To access detailed data from installed packages, specific functions are
 * available:
 * <ul>
 * <li>getParsedPdscTree()
 * <li>getInstalledObjectsForBuild()
 * <li>findInstalledDevice()
 * <li>findInstalledBoard()
 * <li>getDestinationFolder()
 * </ul>
 */

// TODO: add synchronisation

public class DataManager implements IPacksDataManager {

	// ------------------------------------------------------------------------

	private static final DataManager fgInstance;

	static {
		fgInstance = new DataManager();
	}

	public static DataManager getInstance() {
		return fgInstance;
	}

	// ------------------------------------------------------------------------

	// ----- Summary data -----
	private Node fRepositoriesTree;
	private List<PackNode> fPacksVersionsList;

	// A map of maps to find versions faster
	private Map<String, Map<String, PackNode>> fPacksVersionsMap;
	// A map of package nodes
	private Map<String, PackNode> fPacksMap;

	// ----- Installed packs data -----
	private Map<String, Node> fParsedPdsc;
	private List<PackNode> fInstalledPacksLatestVersionsList;
	private Node fInstalledObjectsForBuild;

	private IConfiguration fInstalledConfig;
	private Node fConfigObjectsForBuild;

	// ----- Other data -----
	private MessageConsoleStream fOut;
	private List<IDataManagerListener> fListeners;

	private Map<String, Leaf> fInstalledDevicesMap;
	private Map<String, Leaf> fInstalledBoardsMap;

	public DataManager() {

		fOut = ConsoleStream.getConsoleOut();

		fInstalledObjectsForBuild = null;

		fConfigObjectsForBuild = null;
		fInstalledConfig = null;

		fListeners = new ArrayList<IDataManagerListener>();
		fParsedPdsc = null;

		fRepositoriesTree = null;
		fInstalledPacksLatestVersionsList = null;
		fPacksVersionsList = null;
		fPacksVersionsMap = new TreeMap<String, Map<String, PackNode>>();
		fPacksMap = new TreeMap<String, PackNode>();

		fInstalledDevicesMap = new TreeMap<String, Leaf>();
		fInstalledBoardsMap = new TreeMap<String, Leaf>();
		// fDevicesMap = new TreeMap<String, PackNode>();

	}

	// ----- Listeners & notifiers --------------------------------------------

	public void addListener(IDataManagerListener listener) {
		if (!fListeners.contains(listener)) {
			fListeners.add(listener);
		}
	}

	public void removeListener(IDataManagerListener listener) {
		fListeners.remove(listener);
	}

	// Note: since the DataManager is the only source of events, the
	// source is 'this'.

	// Used by 'Update', when everything might change and need to be refreshed
	public void notifyNewInput() {

		clearSummaryData();

		// Force to re-cache
		clearInstalledPacksLatestVersionsList();
		clearCachedInstalledObjectsForBuild();

		// System.out.println("PacksStorage notifyRefresh()");
		DataManagerEvent event = new DataManagerEvent(this, DataManagerEvent.Type.NEW_INPUT);

		notifyListener(event);
	}

	// Used when a package is installed/removed
	public void notifyInstallRemove() {

		clearSummaryData();

		// Force to re-cache
		clearInstalledPacksLatestVersionsList();
		clearCachedInstalledObjectsForBuild();

		// System.out.println("PacksStorage notifyRefresh()");
		DataManagerEvent event = new DataManagerEvent(this, DataManagerEvent.Type.UPDATE_PACKS);

		notifyListener(event);
	}

	// 'Install/Remove Pack' notifies Type.UPDATE_VERSIONS
	public void notifyUpdateView(String type, List<Leaf> list) {

		// System.out.println("PacksStorage notifyUpdateView()");
		DataManagerEvent event = new DataManagerEvent(this, type, list);

		notifyListener(event);

		// TODO: check this one!
		// fDevicesMap.clear();
	}

	public void notifyListener(DataManagerEvent event) {

		for (IDataManagerListener listener : fListeners) {
			// System.out.println(listener);
			listener.packsChanged(event);
		}
	}

	// ----- Summary data access ----------------------------------------------

	public void clearSummaryData() {

		fRepositoriesTree = null;
		fPacksVersionsList = null;

		fInstalledPacksLatestVersionsList = null;
	}

	public Node getRepositoriesTree() {

		return getRepositoriesTree(new DurationMonitor());
	}

	private Node getRepositoriesTree(final DurationMonitor dm) {

		if (fRepositoriesTree != null) {
			return fRepositoriesTree;
		}

		assert dm != null;
		dm.displayTimeAndRun(new Runnable() {

			public void run() {
				loadCachedReposContent(dm);
			}
		});
		return fRepositoriesTree;
	}

	/**
	 * Get a list of all existing package versions.
	 * 
	 * @return a list of version nodes.
	 */
	private List<PackNode> getPacksVersionsList(DurationMonitor dm) {

		if (fPacksVersionsList != null) {
			return fPacksVersionsList;
		}

		// Populate the list.
		getRepositoriesTree(dm);

		return fPacksVersionsList;
	}

	/**
	 * Find a given package version.
	 * 
	 * @param vendorName
	 *            a string with the package vendor name (for example ARM).
	 * @param packName
	 *            a string with the package name (for example CMSIS).
	 * @param version
	 *            a string with the package version (for example 4.1.1).
	 * @return a version node or null if not found.
	 */
	public PackNode findCmsisPackVersion(String vendorName, String packName, String version) {

		return findCmsisPackVersion(vendorName, packName, version, new DurationMonitor());
	}

	private PackNode findCmsisPackVersion(String vendorName, String packName, String version, DurationMonitor dm) {

		// Be sure the map is populated.
		getRepositoriesTree(dm);

		String key = makeMapKey(PackType.CMSIS, vendorName, packName);
		Map<String, PackNode> versionsMap = fPacksVersionsMap.get(key);

		if (versionsMap == null) {
			return null;
		}

		// May return null
		return (PackNode) versionsMap.get(version);
	}

	/**
	 * Find the latest version of a package.
	 * 
	 * @param vendorName
	 *            a string with the package vendor name (for example ARM).
	 * @param packName
	 *            a string with the package name (for example CMSIS).
	 * @return a version node or null if not found.
	 */
	public PackNode findPackLatest(String vendorName, String packName) {

		return findPackLatest(vendorName, packName, new DurationMonitor());
	}

	private PackNode findPackLatest(String vendorName, String packName, DurationMonitor dm) {

		// Be sure the map is populated.
		getRepositoriesTree(dm);

		String key = makeMapKey(PackType.CMSIS, vendorName, packName);
		Map<String, PackNode> versionsMap = fPacksVersionsMap.get(key);

		if (versionsMap == null) {
			return null;
		}

		PackNode node = null;
		for (String versionName : versionsMap.keySet()) {
			// Iterate to the last version in the set
			node = versionsMap.get(versionName);
		}

		// If the map is sorted (as it should be), this is the package
		// with the highest version (or null).
		return node;
	}

	/**
	 * Construct an internal representation of the map key, inspired from CMSIS Pack
	 * examples.
	 * 
	 * @param packType
	 *            a string with the pack type (from PackTypes)
	 * @param vendorName
	 *            a string with the package vendor name (for example ARM)
	 * @param packName
	 *            a string with the package name (for example CMSIS)
	 * @return a string combining the vendor and the pack names.
	 */
	public String makeMapKey(String packType, String vendorName, String packName) {

		String key = packType + "::" + vendorName + "::" + packName;
		return key;
	}

	/**
	 * Load cached content with updated values, for example after a full packs
	 * update.
	 * 
	 * Set the following field members:
	 * <ul>
	 * <li>fRepositoriesTree
	 * <li>fPacksVersionsList
	 * </ul>
	 * and update the content of the following maps:
	 * <ul>
	 * <li>fPacksVersionsMap
	 * <li>fPacksMap.
	 * </ul>
	 */
	public void loadCachedReposContent() {

		loadCachedReposContent(null);
	}

	private void loadCachedReposContent(DurationMonitor dm) {

		Node node = new Node(Type.ROOT);
		fPacksVersionsList = Repos.getInstance().loadCachedReposContent(node);
		fRepositoriesTree = node;

		preparePacksMaps();

		addSelectors();

		updateInstalledVersions();
	}

	/**
	 * Recreate the package maps with content from the list.
	 * <p>
	 * Iterate fPacksVersionsList and recreate the following maps
	 * <ul>
	 * <li>fPacksVersionsMap
	 * <li>fPacksMap
	 * <ul>
	 */
	private void preparePacksMaps() {

		// Group versions by [packType::vendor::package] in a Map
		Map<String, Map<String, PackNode>> packsVersionsMap = new TreeMap<String, Map<String, PackNode>>();
		for (PackNode versionNode : fPacksVersionsList) {
			String vendorName = versionNode.getProperty(Property.VENDOR_NAME);
			String packName = versionNode.getProperty(Property.PACK_NAME);
			String key = makeMapKey(PackType.CMSIS, vendorName, packName);

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
		for (PackNode versionNode : fPacksVersionsList) {
			String vendorName = versionNode.getProperty(Property.VENDOR_NAME);
			String packName = versionNode.getProperty(Property.PACK_NAME);
			String key = makeMapKey(PackType.CMSIS, vendorName, packName);

			if (!packsMap.containsKey(key)) {
				PackNode parent = (PackNode) versionNode.getParent();
				packsMap.put(key, parent);
			}
		}
		// Used for adding selectors.
		fPacksMap = packsMap;
	}

	/**
	 * Add unique selectors to each package, by inspecting the outline and external
	 * definitions in the latest version.
	 * <p>
	 * Used to support filtering in the PacksView tree.
	 */
	private void addSelectors() {

		for (PackNode packNode : fPacksMap.values()) {

			Node versionNode = (Node) packNode.getFirstChild();
			if (versionNode.hasChildren()) {
				for (Leaf child : versionNode.getChildren()) {

					if (child.isType(Type.OUTLINE) || child.isType(Type.EXTERNAL)) {
						if (child.hasChildren()) {
							for (Leaf node : ((Node) child).getChildren()) {

								Selector selector = null;

								String type = node.getType();
								if (Type.FAMILY.equals(type)) {

									selector = new Selector(Selector.Type.DEVICEFAMILY);
									selector.setValue(node.getName());
									selector.setVendorId(node.getProperty(Property.VENDOR_ID));

								} else if (Type.BOARD.equals(type)) {

									selector = new Selector(Selector.Type.BOARD);
									selector.setValue(node.getName());
									selector.setVendor(node.getProperty(Property.VENDOR_NAME));

								} else if (Type.KEYWORD.equals(type)) {

									selector = new Selector(Selector.Type.KEYWORD);
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

	/**
	 * Add "Property.INSTALLED=true" to all installed version nodes and to their
	 * parent pack nodes.
	 */
	private void updateInstalledVersions() {

		fOut.println("Identifying installed packages...");

		int count = 0;

		// Check if the packages are installed
		for (Leaf versionNode : fPacksVersionsList) {

			String unpackFolder = versionNode.getProperty(Property.DEST_FOLDER);
			String pdscName = versionNode.getProperty(Property.PDSC_NAME);

			String pdscRelativePath = unpackFolder + '/' + pdscName;

			try {
				// Test if the pdsc file exists in the package folder
				File file = PacksStorage.getFileObject(pdscRelativePath);
				if (file.exists()) {

					// Add an explicit property for more visibility
					versionNode.setBooleanProperty(Property.INSTALLED, true);
					if (versionNode.getParent().isType(Type.PACKAGE)) {
						versionNode.getParent().setBooleanProperty(Property.INSTALLED, true);
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

	/**
	 * Force to recreate the list of installed versions.
	 * <p>
	 * Used after installing/removing packages.
	 */
	private void clearInstalledPacksLatestVersionsList() {
		fInstalledPacksLatestVersionsList = null;
	}

	/**
	 * Filter all available versions and return only the latest installed ones.
	 * 
	 * @return a list of version nodes.
	 */
	public List<PackNode> getInstalledPacksLatestVersionsList() {

		return getInstalledPacksLatestVersionsList(new DurationMonitor());
	}

	private List<PackNode> getInstalledPacksLatestVersionsList(DurationMonitor dm) {

		if (fInstalledPacksLatestVersionsList != null) {
			return fInstalledPacksLatestVersionsList;
		}

		// Filter installed packages
		Set<PackNode> installedPackages = new HashSet<PackNode>();
		List<PackNode> packsVersionsList = getPacksVersionsList(dm);
		if (packsVersionsList != null) {
			for (PackNode versionNode : packsVersionsList) {
				if (versionNode.isBooleanProperty(Property.INSTALLED)) {
					installedPackages.add((PackNode) versionNode.getParent());
				}
			}

			// Filter only the latest versions (first installed in children
			// list)
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

			fInstalledPacksLatestVersionsList = installedLatestVersions;
		}

		return fInstalledPacksLatestVersionsList;
	}

	// ----- Detailed data ----------------------------------------------------

	/**
	 * Get the tree version equivalent with the full PDSC file.
	 * <p>
	 * If the file is not already in, load it and contribute to the map.
	 * 
	 * @return a tree starting with node "package", or null if error.
	 */
	public Node getParsedPdscTree(String pdscName, String version) {

		return getParsedPdscTree(pdscName, version, new DurationMonitor());
	}

	private Node getParsedPdscTree(String pdscName, String version, final DurationMonitor dm) {

		final String fileName = PacksStorage.makeCachedPdscName(pdscName, version);
		if (fParsedPdsc != null) {
			Node node = fParsedPdsc.get(fileName);
			if (node != null) {
				return node;
			}
		} else {
			fParsedPdsc = new HashMap<String, Node>();
		}

		File file = null;
		try {
			String parts[] = pdscName.split("\\.", 3);
			if (!"pdsc".equals(parts[2])) {
				String msg = pdscName + " not a valid PDSC file name (vendor.name.pdsc).";
				fOut.println("Error: " + msg);
				Utils.reportError(msg);
				Activator.log(msg);

				return null;
			}
			file = PacksStorage.getPackageFileObject(parts[0], parts[1], version, pdscName);
			if (!file.isFile()) {
				// Second chance: check if it was cached.
				file = PacksStorage.getCachedFileObject(fileName);
			}
		} catch (IOException e) {
			String msg = e.getMessage() + ", file: " + file.getName();
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(e);

			return null;
		}
		final File finalFile = file;
		assert dm != null;
		dm.displayTimeAndRun(new Runnable() {

			public void run() {
				Node node = parsePdscFile(finalFile, dm);
				if (node != null) {
					fParsedPdsc.put(fileName, node);
				}
			}
		});

		return fParsedPdsc.get(fileName); // may be null
	}

	/**
	 * Load the PDSC file and parse it with the generic parser.
	 * <p>
	 * The tree reflects 100% the content in the original file, just that it is
	 * easier to store and further parse.
	 * 
	 * @return a tree starting with node "package".
	 */
	private Node parsePdscFile(File file, DurationMonitor dm) {

		try {

			fOut.println("Parsing PDSC file \"" + file.getCanonicalPath() + "\"...");
			Document document = Xml.parseFile(file);

			PdscGenericParser parser = new PdscGenericParser();
			Node node = parser.parse(document);
			return node;

		} catch (SAXParseException e) {
			String msg = e.getMessage() + ", file: " + file.getName() + ", line: " + e.getLineNumber() + ", column: "
					+ e.getColumnNumber();
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(e);
		} catch (Exception e) {
			String msg = e.getMessage() + ", file: " + file.getName();
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(e);
		}

		return null;
	}

	private Node parseXcdlFile(File file, DurationMonitor dm) {

		FileReader reader;
		try {
			fOut.println("Parsing XCDL file \"" + file.getCanonicalPath() + "\"...");

			JSONParser parser = new JSONParser();
			reader = new FileReader(file);
			JSONObject json = (JSONObject) parser.parse(reader);

			JsonJsGenericParser jsonJsParser = new JsonJsGenericParser();
			JsNode jsNode = jsonJsParser.parse(json);
			System.out.println(jsNode);

			JsonGenericParser jsonParser = new XcdlGenericParser();
			Node node = jsonParser.parse(json);

			return node;

		} catch (FileNotFoundException e) {
			String msg = e.getMessage() + ", file: " + file.getName();
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(e);
		} catch (IOException e) {
			String msg = e.getMessage() + ", file: " + file.getName();
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(e);
		} catch (ParseException e) {
			String msg = e.getMessage() + ", file: " + file.getName() + ", position: " + e.getPosition();
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(e);
		}

		return null;
	}

	// ------------------------------------------------------------------------

	/**
	 * Force to recreate the cached installed objects.
	 * <p>
	 * To achieve this, also removes the cached file.
	 */
	private void clearCachedInstalledObjectsForBuild() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("clearCachedInstalledObjectsForBuild()");
		}

		fInstalledObjectsForBuild = null;
		fConfigObjectsForBuild = null;

		try {
			File devicesFile = PacksStorage.getCachedFileObject(PacksStorage.INSTALLED_DEVICES_FILE_NAME);

			if (devicesFile.isFile()) {
				devicesFile.delete();
			}
		} catch (IOException e) {
			;
		}

		fInstalledDevicesMap.clear();
	}

	/**
	 * Get the devices from all installed packs, to be used by the device selection
	 * properties page in project settings page.
	 * 
	 * @return a tree of nodes, with Devices/Boards, Vendors, Family, Subfamily,
	 *         Device, or null.
	 */
	// @Override
	public Node getInstalledObjectsForBuild(IConfiguration config) {

		return getInstalledObjectsForBuild(config, new DurationMonitor());
	}

	private Node getInstalledObjectsForBuild(IConfiguration config, final DurationMonitor dm) {

		if (config != null && config != fInstalledConfig) {
			fInstalledConfig = config;

			// Clear cache.
			fConfigObjectsForBuild = null;
			fInstalledDevicesMap.clear();
			fInstalledBoardsMap.clear();
		}

		if (fConfigObjectsForBuild == null) {

			if (fInstalledObjectsForBuild == null) {

				assert dm != null;
				dm.displayTimeAndRun(new Runnable() {

					public void run() {
						fInstalledObjectsForBuild = loadInstalledObjectsForBuild(config, dm);
					}
				});
			}

			Node rootNode = fInstalledObjectsForBuild;

			// Parse project xcdl & merge
			Node projectRootNode = parseProjectPackagesForBuild(config, dm);
			if (projectRootNode != null) {
				rootNode = mergeTrees(rootNode, projectRootNode);
			}

			fConfigObjectsForBuild = rootNode;
		}
		return fConfigObjectsForBuild;
	}

	// Update the tree, from cache or from original pdsc/xcdl files
	/**
	 * Load from the cached file or recreate the tree with the installed objects
	 * (devices/boards/books).
	 * 
	 * @return a tree with device/boards, or null.
	 */
	private Node loadInstalledObjectsForBuild(IConfiguration config, DurationMonitor dm) {

		fOut.println("Extracting devices & boards... ");

		Node rootNode = null;
		File devicesFile = null;
		try {
			devicesFile = PacksStorage.getCachedFileObject(PacksStorage.INSTALLED_DEVICES_FILE_NAME);

			if (devicesFile.isFile()) {

				// If the cached file exists, try to use it
				rootNode = loadCachedInstalledObjectsForBuild(devicesFile);
				// However, it may fail
			}

		} catch (IOException e1) {
			;
		}

		if (rootNode != null) {
			ITreeIterator packNodes = new AbstractTreePreOrderIterator() {

				@Override
				public boolean isIterable(Leaf node) {

					if (node.isType("family")) {
						return true;
					} else if (node.isType("board")) {
						return true;
					}
					return false;
				}

				@Override
				public boolean isLeaf(Leaf node) {

					if (node.isType("family")) {
						return true;
					} else if (node.isType("board")) {
						return true;
					}
					return false;
				}
			};

			// Iterate only the current device children nodes
			packNodes.setTreeNode(rootNode);

			for (Leaf node : packNodes) {

				// System.out.println(node);
				String packName = node.getProperty("pack.name");
				if (packName.isEmpty()) {
					rootNode = null;
					Activator.log("Buggy cache detected, ignored.");
					break;
				}
			}
		}

		if (rootNode == null) {

			// Extract devices/boards/books from all installed packages
			rootNode = parseInstalledPackagesForBuild(dm);

			// TODO: check if still needed
			// addPdscNames(rootNode);

			if (rootNode != null) {

				fOut.println("Writing cache file \"" + devicesFile + "\".");
				// Save cached file for future use
				GenericSerialiser serialiser = new GenericSerialiser();
				try {
					serialiser.serialise(rootNode, devicesFile);
				} catch (IOException e) {

					String msg = e.getMessage() + ", file: " + devicesFile.getName();
					fOut.println("Error: " + msg);
					Utils.reportError(msg);
					Activator.log(e);
				}
			}
		}

		// TODO: parse xPacks from repo & merge

		// Always return a tree, even a very simple one
		return rootNode;
	}

	private Node mergeTrees(Node first, Node second) {
		Node rootNode = first;
		if (first == null) {
			rootNode = new Node(Type.ROOT);
		}

		// Merge devices first, since we need to search for the displayName of
		// the board.installedDevice.name
		ITreeIterator secondDeviceFamilies = new AbstractTreePreOrderIterator() {

			@Override
			public boolean isIterable(Leaf node) {
				if (node.isType(Type.FAMILY)) {
					return true;
				}
				return false;
			}

			@Override
			public boolean isLeaf(Leaf node) {
				if (node.isType(Type.FAMILY) || node.isType(Type.BOARDS_SUBTREE)) {
					return true;
				}
				return false;
			}

		};
		// tree contains the installed objects
		secondDeviceFamilies.setTreeNode(second);

		// Iterate device families and find the requested one, if any.
		for (Leaf secondDeviceFamily : secondDeviceFamilies) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println(secondDeviceFamily);
			}
			addInstalledDeviceFamily((Node) secondDeviceFamily, rootNode);
		}

		ITreeIterator secondBoards = new AbstractTreePreOrderIterator() {

			// Only nodes that pass this test are returned.
			@Override
			public boolean isIterable(Leaf node) {
				if (node.isType(Type.BOARD)) {
					return true;
				}
				return false;
			}

			// Optimize and stop recursion early, when these nodes are encountered.
			@Override
			public boolean isLeaf(Leaf node) {
				if (node.isType(Type.BOARD) || node.isType(Type.FAMILIES_SUBTREE)) {
					return true;
				}
				return false;
			}

		};
		// tree contains the installed objects
		secondBoards.setTreeNode(second);

		// Iterate boards and find the requested one, if any.
		for (Leaf secondBoard : secondBoards) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println(secondBoard);
			}
			addInstalledBoard((Node) secondBoard, rootNode);
		}

		return rootNode;
	}

	private String[] getArrayValues(Node node) {
		assert node != null;

		List<String> list = new LinkedList<String>();

		if (node.hasChildren()) {
			for (Leaf child : node.getChildren()) {
				// System.out.println(child);
				if (child.isType(Type.ARRAY_ELEMENT_)) {
					String value = child.getProperty(Property.VALUE_, "");
					if (!value.isEmpty()) {
						list.add(value);
					}
				}
			}
		}
		return list.toArray(new String[list.size()]);
	}

	private void addInstalledDeviceFamily(Node deviceFamily, Node parent) {

		// Find root, to get package name & version.
		Node root = deviceFamily;
		while (!root.isType(Type.ROOT)) {
			root = root.getParent();
		}

		Node rootDevices = (Node) parent.findChild(Type.DEVICES_SUBTREE);
		if (rootDevices == null) {
			rootDevices = Node.addNewChild(parent, Type.DEVICES_SUBTREE);
			rootDevices.setName("Devices");
		}
		Node supplier = (Node) deviceFamily.findChild(Type.SUPPLIER);
		if (supplier == null) {
			String name = deviceFamily.getProperty(Property.DISPLAY_NAME);
			String msg = "Device family " + name + " has no 'supplier; ignored.'";
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(msg);
			return;
		}
		String supplierName = supplier.getProperty(Property.DISPLAY_NAME);
		String supplierId = supplier.getProperty(Property.ID);

		Node vendor = (Node) rootDevices.findChild(Type.VENDOR, supplierName);
		if (vendor == null) {
			vendor = Node.addNewChild((Node) rootDevices, Type.VENDOR);
			vendor.setName(supplierName);
			vendor.putProperty(Property.VENDOR_ID, supplierId);
		}
		// The vendor node is for grouping only; the board vendor id is defined as
		// property.

		String familyName = deviceFamily.getProperty(Property.DISPLAY_NAME);
		if (familyName.isEmpty()) {
			familyName = deviceFamily.getProperty(Property.KEY_);
		}

		Node family = (Node) vendor.findChild(Type.FAMILY, familyName);
		if (family == null) {
			family = Node.addNewChild(vendor, Type.FAMILY);
			family.setName(familyName);
			family.putProperty(Property.PACK_TYPE, PackType.XPACK_XCDL);
			family.putProperty(Property.KEY_, deviceFamily.getProperty(Property.KEY_));

			family.putProperty(Property.VENDOR_NAME, supplierName);
			family.putProperty(Property.VENDOR_ID, supplierId);

			family.putProperty(Property.PACK_NAME, root.getProperty(Property.PACK_NAME));
			family.putProperty(Property.PACK_VERSION, root.getProperty(Property.PACK_VERSION));
		}
		// TODO: process subFamily.

		Node devices = (Node) deviceFamily.findChild(Type.DEVICES_SUBTREE);
		// Family exist, check each device.
		if (devices == null || !devices.hasChildren()) {
			return;
		}

		for (Leaf device : devices.getChildren()) {
			if (!device.isType(Type.DEVICE)) {
				continue;
			}
			String deviceName = device.getProperty(Property.DISPLAY_NAME);
			if (deviceName.isEmpty()) {
				deviceName = device.getProperty(Property.KEY_);
			}
			Node existingDevice = (Node) family.findChild(Type.DEVICE, deviceName);
			if (existingDevice != null) {
				if (Activator.getInstance().isDebugging()) {
					System.out.println("Device " + deviceName + " exists; replaced.");
				}
				family.removeChild(existingDevice);
			}
			Node newDevice = Node.addNewChild(family, Type.DEVICE);
			newDevice.setName(deviceName);
			newDevice.putProperty(Property.PACK_TYPE, PackType.XPACK_XCDL);
			newDevice.putProperty(Property.VENDOR_NAME, supplierName);
			newDevice.putProperty(Property.VENDOR_ID, supplierId);
			// Pass the key, to allow the board logic to find the name.
			newDevice.putProperty(Property.KEY_, device.getProperty(Property.KEY_));

			Node features = (Node) ((Node) device).findChild(Type.FEATURES);
			if (features != null) {
				newDevice.putNonEmptyProperty(Property.ARCH, features.getPropertyOrNull(Property.ARCH));
				newDevice.putNonEmptyProperty(Property.HFOSC, features.getPropertyOrNull(Property.HFOSC));
				newDevice.putNonEmptyProperty(Property.LFOSC, features.getPropertyOrNull(Property.LFOSC));
			}

			Node compile = (Node) ((Node) device).findChild(Type.COMPILER);
			if (compile != null) {
				Node headers = (Node) compile.findChild(Type.HEADERS);
				if (headers != null) {
					String arr[] = getArrayValues(headers);
					if (arr.length > 0) {
						newDevice.putNonEmptyProperty(Property.COMPILER_HEADERS, String.join(",", arr));
					}
				}
				Node defines = (Node) compile.findChild(Type.DEFINES);
				if (defines != null) {
					String arr[] = getArrayValues(defines);
					if (arr.length > 0) {
						newDevice.putNonEmptyProperty(Property.COMPILER_DEFINES, String.join(",", arr));
					}
				}
			}

			Node memoryRegions = (Node) ((Node) device).findChild(Type.MEMORY_REGIONS);
			if (memoryRegions != null && memoryRegions.hasChildren()) {
				for (Leaf memoryRegion : memoryRegions.getChildren()) {
					String onChip = memoryRegion.getProperty(Property.ON_CHIP);
					if ("true".equals(onChip)) {
						Node newMemory = Node.addNewChild(newDevice, Type.MEMORY);
						newMemory.setName(memoryRegion.getProperty(Property.KEY_));
						newMemory.putProperty(Property.START, memoryRegion.getProperty(Property.ADDRESS));
						newMemory.putProperty(Property.SIZE, memoryRegion.getProperty(Property.SIZE));
						newMemory.putProperty(Property.ACCESS, memoryRegion.getProperty(Property.ACCESS));
					}
				}
			}
			Node debug = (Node) ((Node) device).findChild(Type.DEBUG);
			newDevice.putNonEmptyProperty(Property.XSVD_FILE, debug.getPropertyOrNull(Property.XSVD));
		}
	}

	private void addInstalledBoard(Node board, Node parent) {

		// Find root, to get package name & version.
		Node root = board;
		while (!root.isType(Type.ROOT)) {
			root = root.getParent();
		}

		Node rootBoards = (Node) parent.findChild(Type.BOARDS_SUBTREE);
		if (rootBoards == null) {
			rootBoards = Node.addNewChild(parent, Type.BOARDS_SUBTREE);
			rootBoards.setName("Boards");
		}
		String boardName = board.getProperty(Property.DISPLAY_NAME);
		if (boardName.isEmpty()) {
			boardName = board.getProperty(Property.KEY_);
		}
		Node supplier = (Node) board.findChild(Type.SUPPLIER);
		if (supplier == null) {
			String msg = "Board " + boardName + " has no 'supplier'; ignored.";
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(msg);
			return;
		}
		String supplierName = supplier.getProperty(Property.DISPLAY_NAME);
		String supplierId = supplier.getProperty(Property.ID);

		Node vendor = (Node) rootBoards.findChild(Type.VENDOR, supplierName);
		if (vendor == null) {
			vendor = Node.addNewChild(rootBoards, Type.VENDOR);
			vendor.setName(supplierName);
			// For boards, do not store the vendor id, CMSIS does not support it.
		}

		// If already in, remove it and later add the new one.
		Node existingBoard = (Node) vendor.findChild(Type.BOARD, boardName);
		if (existingBoard != null) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("Board " + boardName + " exists; replaced.");
			}
			vendor.removeChild(existingBoard);
		}

		Node newBoard = Node.addNewChild(vendor, Type.BOARD);
		newBoard.setName(boardName);
		newBoard.putProperty(Property.PACK_TYPE, PackType.XPACK_XCDL);
		newBoard.putProperty(Property.KEY_, board.getProperty(Property.KEY_));

		newBoard.putNonEmptyProperty(Property.BOARD_REVISION, board.getPropertyOrNull(Property.REVISION));

		newBoard.putProperty(Property.VENDOR_NAME, supplierName);
		newBoard.putProperty(Property.VENDOR_ID, supplierId);

		newBoard.putProperty(Property.PACK_NAME, root.getProperty(Property.PACK_NAME));
		newBoard.putProperty(Property.PACK_VERSION, root.getProperty(Property.PACK_VERSION));

		Node features = (Node) board.findChild(Type.FEATURES);
		if (features != null) {
			newBoard.putNonEmptyProperty(Property.HFXTAL, features.getPropertyOrNull(Property.HFXTAL));
			newBoard.putNonEmptyProperty(Property.LFXTAL, features.getPropertyOrNull(Property.LFXTAL));
		}

		Node compile = (Node) board.findChild(Type.COMPILER);
		if (compile != null) {
			Node headers = (Node) compile.findChild(Type.HEADERS);
			if (headers != null) {
				String arr[] = getArrayValues(headers);
				if (arr.length > 0) {
					newBoard.putNonEmptyProperty(Property.COMPILER_HEADERS, String.join(",", arr));
				}
			}
			Node defines = (Node) compile.findChild(Type.DEFINES);
			if (defines != null) {
				String arr[] = getArrayValues(defines);
				if (arr.length > 0) {
					newBoard.putNonEmptyProperty(Property.COMPILER_DEFINES, String.join(",", arr));
				}
			}
		}

		// TODO get clock
		// Add package name and version

		Node installedDevice = (Node) board.findChild(Type.INSTALLED_DEVICE);
		if (installedDevice == null) {
			String msg = "Board " + boardName + " has no 'installedDevice'; ignored.";
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(msg);
			return;
		}
		Leaf installedDeviceSupplier = installedDevice.findChild(Type.SUPPLIER);
		if (installedDeviceSupplier == null) {
			String msg = "Board " + boardName + " has no 'installedDevice.supplier'; ignored.";
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(msg);
			return;
		}
		String installedDeviceSupplierName = installedDeviceSupplier.getProperty(Property.DISPLAY_NAME);
		if (installedDeviceSupplierName.isEmpty()) {
			String msg = "Board " + boardName + " has no 'installedDevice.supplier.name'; ignored.";
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(msg);
			return;
		}
		String installedDeviceSupplierId = installedDeviceSupplier.getProperty(Property.ID);
		if (installedDeviceSupplierId.isEmpty()) {
			String msg = "Board " + boardName + " has no 'installedDevice.supplier.id'; ignored.";
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(msg);
			return;
		}
		String installedDeviceName = installedDevice.getProperty(Property.NAME);
		if (installedDeviceName.isEmpty()) {
			String msg = "Board " + boardName + " has no 'installedDevice.name'; ignored.";
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(msg);
			return;
		}

		Node nodeDevice = Node.addNewChild(newBoard, Type.DEVICE);

		String installedDeviceKey = installedDeviceName;

		// TODO: compute device displayName from name
		Node rootDevices = (Node) parent.findChild(Type.DEVICES_SUBTREE);
		if (rootDevices != null) {
			ITreeIterator existingDevices = new AbstractTreePreOrderIterator() {

				@Override
				public boolean isIterable(Leaf node) {
					if (node.isType(Type.DEVICE)) {
						return true;
					}
					return false;
				}

				@Override
				public boolean isLeaf(Leaf node) {
					if (node.isType(Type.DEVICE)) {
						return true;
					}
					return false;
				}

			};
			// tree contains the installed objects
			existingDevices.setTreeNode(rootDevices);

			// Iterate devices and find the requested one, if any.
			for (Leaf existingDevice : existingDevices) {
				if (existingDevice.isType(Type.DEVICE)) {
					if (installedDeviceName.equals(existingDevice.getProperty(Property.KEY_))) {
						String displayName = existingDevice.getName();
						if (!displayName.isEmpty()) {
							installedDeviceName = displayName;
						}
						break;
					}
				}
			}
		}
		nodeDevice.setName(installedDeviceName);
		nodeDevice.putProperty(Property.PACK_TYPE, PackType.XPACK_XCDL);
		nodeDevice.putProperty(Property.VENDOR_NAME, installedDeviceSupplierName);
		nodeDevice.putProperty(Property.VENDOR_ID, installedDeviceSupplierId);
		nodeDevice.putProperty(Property.KEY_, installedDeviceKey);
	}

	/**
	 * Load the cached file with the installed objects.
	 * 
	 * @param file
	 *            the cached xml file
	 * @return a tree with the parsed content of the file, or null if error.
	 */
	private Node loadCachedInstalledObjectsForBuild(File file) {

		String str;
		try {
			str = file.getCanonicalPath();
		} catch (IOException e) {
			str = file.getPath();
		}
		fOut.println("Parsing cached file \"" + str + "\".");

		Node node = null;
		try {
			// Parse the cached file
			Document document = Xml.parseFile(file);
			GenericParser parser = new GenericParser();
			node = parser.parse(document);
		} catch (SAXParseException e) {
			String msg = e.getMessage() + ", file: " + file.getName() + ", line: " + e.getLineNumber() + ", column: "
					+ e.getColumnNumber();
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(e);
		} catch (Exception e) {
			String msg = e.getMessage() + ", file: " + file.getName();
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(e);
		}

		return node;
	}

	/**
	 * Process all installed packs and collect the objects required for build
	 * (devices/boards/books).
	 * <p>
	 * It uses the cached PDSC trees; if not there, parse the PDSC files.
	 * 
	 * @return a tree or null if error.
	 */
	private Node parseInstalledPackagesForBuild(DurationMonitor dm) {

		List<PackNode> versionsList = getInstalledPacksLatestVersionsList(dm);

		PdscTreeParserForBuild pdsc = new PdscTreeParserForBuild();

		Node boardsNode = new Node(Type.BOARDS_SUBTREE);
		boardsNode.setName("Boards");
		Node devicesNode = new Node(Type.DEVICES_SUBTREE);
		devicesNode.setName("Devices");

		int countPackages = 0;
		int totalCountDevices = 0;
		int totalCountBoards = 0;

		for (PackNode node : versionsList) {

			String pdscName = node.getProperty(Property.PDSC_NAME);
			String version = node.getName();

			// Get the parsed PDSC, either from memory or from disc.
			Node tree = getParsedPdscTree(pdscName, version, dm);

			// Extract the devices.
			int countDevices = pdsc.parseDevices(tree, devicesNode);
			totalCountDevices += countDevices;

			// Extract the boards.
			// Later use of boards requires existing the devices to be present,
			// to get the memory map.
			int countBoards = pdsc.parseBoards(tree, boardsNode);
			totalCountBoards += countBoards;

			countPackages++;
		}

		if (countPackages == 0) {
			fOut.println("No installed packages.");
		} else {
			if (countPackages == 1) {
				fOut.print("Processed 1 package, found ");
			} else {
				fOut.print("Processed " + countPackages + " packages, found ");
			}
			if (totalCountDevices == 0) {
				fOut.print("no devices, ");
			} else if (totalCountDevices == 1) {
				fOut.print("1 device, ");
			} else {
				fOut.print(totalCountDevices + " devices, ");
			}
			if (totalCountBoards == 0) {
				fOut.print("no boards.");
			} else if (totalCountBoards == 1) {
				fOut.print("1 board.");
			} else {
				fOut.print(totalCountBoards + " boards.");
			}
			fOut.println();
		}

		if (boardsNode.hasChildren() || devicesNode.hasChildren()) {

			Node rootNode = new Node(Type.ROOT);
			rootNode.addChild(boardsNode);
			rootNode.addChild(devicesNode);
			return rootNode;
		}

		return null;
	}

	private Node parseProjectPackagesForBuild(IConfiguration config, DurationMonitor dm) {

		assert (dm != null);
		if (config == null) {
			return null;
		}

		IProject project = EclipseUtils.getProjectFromConfiguration(config);
		assert (project != null);

		if (!XpackUtils.hasPackageJson(project)) {
			return null;
		}

		IPath projectPath = project.getLocation();

		// Search only the xpacks folder, if it exists.
		File xpacksFolder = projectPath.append("xpacks").toFile();
		if (!xpacksFolder.isDirectory()) {
			return null;
		}

		Node tree = new Node(Type.ROOT);
		tree.setPackType(Leaf.PACK_TYPE_XPACK);

		File[] folders = xpacksFolder.listFiles();
		for (int i = 0; i < folders.length; ++i) {
			if (folders[i].isDirectory()) {
				// System.out.println(folders[i]);

				String packageName = "";
				String packageVersion = "";

				Path folderPath = new Path(folders[i].getPath());
				File packageFile = folderPath.append("package.json").toFile();
				if (packageFile.exists() && !packageFile.isDirectory()) {
					JSONParser parser = new JSONParser();
					try {
						FileReader reader;
						reader = new FileReader(packageFile);
						JSONObject packageJson = (JSONObject) parser.parse(reader);
						packageName = (String) packageJson.get("name");
						packageVersion = (String) packageJson.get("version");

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
					}
				}

				File[] xcdlFiles = folders[i].listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith("xcdl.json");
					}
				});
				for (int j = 0; j < xcdlFiles.length; ++j) {
					if (Activator.getInstance().isDebugging()) {
						System.out.println(xcdlFiles[j]);
					}
					final File xcdlFile = xcdlFiles[j];
					final String name = packageName;
					final String version = packageVersion;

					dm.displayTimeAndRun(new Runnable() {

						public void run() {
							Node xcdlRoot = parseXcdlFile(xcdlFile, dm);
							// Warning, multiple ROOT nodes, one for each file.
							tree.addChild(xcdlRoot);

							// Add pack identity.
							xcdlRoot.putProperty(Property.PACK_NAME, name);
							xcdlRoot.putProperty(Property.PACK_VERSION, version);
						}
					});

				}
			}
		}
		if (tree.hasChildren()) {
			return tree;
		}

		return null;
	}

	public Leaf findInstalledDevice(String packType, String deviceSupplierId, String deviceId, IConfiguration config) {

		return findInstalledDevice(packType, deviceSupplierId, deviceId, config, new DurationMonitor());
	}

	private Leaf findInstalledDevice(String packType, String deviceSupplierId, String deviceId, IConfiguration config,
			DurationMonitor dm) {

		String key = makeMapKey(packType, deviceSupplierId, deviceId);
		if (fInstalledDevicesMap.containsKey(key)) {
			return fInstalledDevicesMap.get(key);
		}

		Node tree = getInstalledObjectsForBuild(config, dm);

		ITreeIterator installedDevices = new AbstractTreePreOrderIterator() {

			@Override
			public boolean isIterable(Leaf node) {
				if (node.isType(Type.DEVICE)) {
					return true;
				}
				return false;
			}

			@Override
			public boolean isLeaf(Leaf node) {
				if (node.isType(Type.DEVICE) || node.isType(Type.BOARDS_SUBTREE)) {
					return true;
				}
				return false;
			}

		};
		// tree contains the installed objects
		installedDevices.setTreeNode(tree);

		// Iterate devices and find the requested one, if any.
		for (Leaf installedDevice : installedDevices) {

			if (PackType.XPACK_XCDL.equals(packType)) {
				String installedDeviceKey = installedDevice.getProperty(Property.KEY_);
				if (!deviceId.equals(installedDeviceKey)) {
					continue; // Different device key, try next
				}
			} else {
				String installedDeviceName = installedDevice.getName();
				if (!deviceId.equals(installedDeviceName)) {
					continue; // Different device name, try next
				}
			}

			String installedDeviceVendorId = "";
			Leaf node = installedDevice;
			while (node != null && !node.isType(Type.VENDOR)) {

				if (node.hasProperty(Property.VENDOR_ID)) {
					installedDeviceVendorId = node.getProperty(Property.VENDOR_ID);
					break;
				}
				node = node.getParent();
			}
			if (deviceSupplierId.equals(installedDeviceVendorId)) {

				// Both device name & vendor id match
				fInstalledDevicesMap.put(key, installedDevice);
				return installedDevice;
			}
		}

		// Store the null value, as a negative ack for next searches.
		fInstalledDevicesMap.put(key, null);
		return null; // Not found
	}

	public Leaf findInstalledBoard(String packType, String boardSupplierId, String boardSupplierName, String boardId,
			IConfiguration config) {
		return findInstalledBoard(packType, boardSupplierId, boardSupplierName, boardId, config, new DurationMonitor());
	}

	private Leaf findInstalledBoard(String packType, String boardSupplierId, String boardSupplierName, String boardId,
			IConfiguration config, DurationMonitor dm) {

		assert (packType != null);
		assert (boardSupplierId != null || boardSupplierName != null);
		assert (boardId != null);

		// TODO: boardSupplierId vs boardSupplierName
		String boardSupplierKey = null;
		if (PackType.CMSIS.equals(packType)) {
			boardSupplierKey = boardSupplierName;
		} else if (PackType.XPACK_XCDL.equals(packType)) {
			boardSupplierKey = boardSupplierId;
		}
		String key = makeMapKey(packType, boardSupplierKey, boardId);
		if (fInstalledBoardsMap.containsKey(key)) {
			return fInstalledBoardsMap.get(key);
		}

		Node tree = getInstalledObjectsForBuild(config, dm);

		ITreeIterator installedBoards = new AbstractTreePreOrderIterator() {

			@Override
			public boolean isIterable(Leaf node) {
				if (node.isType(Type.BOARD)) {
					return true;
				}
				return false;
			}

			@Override
			public boolean isLeaf(Leaf node) {
				if (node.isType(Type.BOARD) || node.isType(Type.DEVICES_SUBTREE)) {
					return true;
				}
				return false;
			}

		};
		// 'tree' contains the installed objects
		installedBoards.setTreeNode(tree);

		// Iterate boards and find the requested one, if any.
		for (Leaf installedBoard : installedBoards) {

			String nodePackType = installedBoard.getProperty(Property.PACK_TYPE, "");
			String installedBoardVendorKey = "";
			if (PackType.XPACK_XCDL.equals(packType) && PackType.XPACK_XCDL.equals(nodePackType)) {
				String installedBoardKey = installedBoard.getProperty(Property.KEY_);
				if (!boardId.equals(installedBoardKey)) {
					continue; // Different board key, try next
				}
				installedBoardVendorKey = installedBoard.getProperty(Property.VENDOR_ID);
			} else {
				String installedBoardName = installedBoard.getName();
				if (!boardId.equals(installedBoardName)) {
					continue; // Different board name, try next
				}
				installedBoardVendorKey = installedBoard.getProperty(Property.VENDOR_NAME);
			}
			if (boardSupplierKey.equals(installedBoardVendorKey)) {
				// Both board name & vendor name match
				fInstalledBoardsMap.put(key, installedBoard);
				return installedBoard;
			}
		}

		// Store the null value, as a negative ack for next searches.
		fInstalledBoardsMap.put(key, null);
		return null; // Not found
	}

	/**
	 * Try to get a property from the current node or from parent nodes, up to a
	 * node of given type (exclusively).
	 * 
	 * @param node
	 *            a leaf node where to start the search
	 * @param name
	 *            a string with the property name
	 * @param type
	 *            a string with the node type where the search stops; the node of
	 *            this type is not inspected; if null, the search will stop at the
	 *            tree root
	 * @return the property value or an empty string if not found
	 */
	public static String collectProperty(Leaf node, String name, String type) {

		while (node != null && !node.isType(type)) {

			if (node.hasProperty(name)) {
				return node.getProperty(name);
			}

			node = node.getParent();
		}

		return "";
	}

	public String getCmsisDestinationFolder(Leaf node) {

		return getCmsisDestinationFolder(node, new DurationMonitor());
	}

	public String getCmsisDestinationFolder(Leaf node, DurationMonitor dm) {

		String vendorName = collectProperty(node, Property.PACK_VENDOR, Type.DEVICES_SUBTREE);
		String packName = collectProperty(node, Property.PACK_NAME, Type.DEVICES_SUBTREE);
		String version = collectProperty(node, Property.PACK_VERSION, Type.DEVICES_SUBTREE);

		Leaf summaryVersionNode = findCmsisPackVersion(vendorName, packName, version, dm);

		String destFolder = "";
		if (summaryVersionNode != null) {
			destFolder = summaryVersionNode.getProperty(Property.DEST_FOLDER);
		}
		return destFolder;
	}

	public String getXpackDestinationFolder(Leaf node, IConfiguration config) {

		return getXpackDestinationFolder(node, config, new DurationMonitor());
	}

	public String getXpackDestinationFolder(Leaf node, IConfiguration config, DurationMonitor dm) {

		String packageName = collectProperty(node, Property.PACK_NAME, Type.DEVICES_SUBTREE);
		String version = collectProperty(node, Property.PACK_VERSION, Type.DEVICES_SUBTREE);
		// System.out.println(packageName + " " + version);

		IProject project = EclipseUtils.getProjectFromConfiguration(config);
		assert project != null;
		if (XpackUtils.hasPackageJson(project)) {

			// If the project is an xPack, try to identify the device package locally,
			// in the xpacks folder.
			String linearFolderName = packageName;
			String arr[] = packageName.split("[/]");
			if (arr.length > 1) {
				linearFolderName = arr[0].substring(1) + "-" + arr[1];
			}
			// System.out.println(linearFolderName);

			IPath projectPath = project.getLocation();
			IPath packageFolderPath = projectPath.append("xpacks").append(linearFolderName);
			if (checkPackage(packageFolderPath, packageName, version)) {
				return packageFolderPath.toString();
			}
		}

		// Check the user xPacks repo.
		IPath packPath = XpackUtils.getPackPath(packageName);

		// Give priority to linked packages.
		if (checkPackage(packPath.append(".link"), packageName, version)) {
			return packPath.append(".link").toString();
		}

		if (checkPackage(packPath.append(version), packageName, version)) {
			return packPath.append(version).toString();
		}

		return null;
	}

	public boolean checkPackage(IPath folderPath, String packageName, String packageVersion) {
		File packageFile = folderPath.append("package.json").toFile();
		if (packageFile.isFile()) {
			JSONParser parser = new JSONParser();

			try {
				FileReader reader;
				reader = new FileReader(packageFile);
				JSONObject packageJson = (JSONObject) parser.parse(reader);
				String actualPackageName = (String) packageJson.get("name");
				String actualPackageVersion = (String) packageJson.get("version");

				if (packageName.equals(actualPackageName) && packageVersion.equals(actualPackageVersion)) {
					return true;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	// Called from debugger.
	@Override
	public IPath getSVDAbsolutePath(String packType, String deviceVendorId, String deviceId, IConfiguration config) {

		Leaf installedDeviceNode = findInstalledDevice(packType, deviceVendorId, deviceId, config);

		if (installedDeviceNode == null) {
			return null;
		}

		String svdFile = installedDeviceNode.getPropertyWithParent(Property.XSVD_FILE, null);
		if (svdFile == null) {
			svdFile = installedDeviceNode.getPropertyWithParent(Property.SVD_FILE, null);
			if (svdFile == null) {
				return null;
			}
		}

		String destFolder;
		IPath path;
		if (PackType.XPACK_XCDL.equals(packType)) {
			destFolder = getXpackDestinationFolder(installedDeviceNode, config);
			path = new Path(destFolder).append(svdFile);
		} else {
			destFolder = getCmsisDestinationFolder(installedDeviceNode);
			try {
				path = PacksStorage.getFolderPath().append(destFolder).append(svdFile);
			} catch (IOException e) {
				return null;
			}
		}

		return path;
	}

	// ------------------------------------------------------------------------

	// Will be called from template code
	/**
	 * Get the list of files referring to CMSIS Core (ARM headers and vendor headers
	 * and source files), for the given device and compiler.
	 * 
	 * @param deviceName
	 * @param compiler
	 * @return a tree of nodes, CMSIS/Vendor, files.
	 */
	// @Override
	public Node getCmsisCoreFiles(String deviceName, String compiler) {
		// TODO Auto-generated method stub
		return null;
	}

	// Will be called from debugger
	/**
	 * Get the register details (address and bit fields) for display/modify in the
	 * debug perspective.
	 * 
	 * @param deviceName
	 * @return (to be defined)
	 */
	// @Override
	public Node getRegisterDetailsForDebug(String deviceName) {
		// TODO Auto-generated method stub
		return null;
	}

	// ------------------------------------------------------------------------
}
