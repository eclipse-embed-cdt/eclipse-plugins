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

import ilg.gnuarmeclipse.core.Activator;
import ilg.gnuarmeclipse.core.Xml;
import ilg.gnuarmeclipse.packs.cmsis.PdscGenericParser;
import ilg.gnuarmeclipse.packs.cmsis.PdscTreeParserForBuild;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.data.DurationMonitor;
import ilg.gnuarmeclipse.packs.core.data.IPacksDataManager;
import ilg.gnuarmeclipse.packs.core.data.PacksStorage;
import ilg.gnuarmeclipse.packs.core.tree.AbstractTreePreOrderIterator;
import ilg.gnuarmeclipse.packs.core.tree.ITreeIterator;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Selector;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.xcdl.GenericParser;
import ilg.gnuarmeclipse.packs.xcdl.GenericSerialiser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

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

	// ----- Other data -----
	private MessageConsoleStream fOut;
	private List<IDataManagerListener> fListeners;

	private Map<String, Leaf> fInstalledDevicesMap;
	private Map<String, Leaf> fInstalledBoardsMap;

	public DataManager() {

		fOut = ConsoleStream.getConsoleOut();

		fInstalledObjectsForBuild = null;

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
	public PackNode findPackVersion(String vendorName, String packName, String version) {

		return findPackVersion(vendorName, packName, version, new DurationMonitor());
	}

	private PackNode findPackVersion(String vendorName, String packName, String version, DurationMonitor dm) {

		// Be sure the map is populated.
		getRepositoriesTree(dm);

		String key = makeMapKey(vendorName, packName);
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

		String key = makeMapKey(vendorName, packName);
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
	 * Construct an internal representation of the map key, inspired from CMSIS
	 * Pack examples.
	 * 
	 * @param vendorName
	 *            a string with the package vendor name (for example ARM)
	 * @param packName
	 *            a string with the package name (for example CMSIS)
	 * @return a string combining the vendor and the pack names.
	 */
	public String makeMapKey(String vendorName, String packName) {

		String key = vendorName + "::" + packName;
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

		// Group versions by [vendor::package] in a Map
		Map<String, Map<String, PackNode>> packsVersionsMap = new TreeMap<String, Map<String, PackNode>>();
		for (PackNode versionNode : fPacksVersionsList) {
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
		for (PackNode versionNode : fPacksVersionsList) {
			String vendorName = versionNode.getProperty(Property.VENDOR_NAME);
			String packName = versionNode.getProperty(Property.PACK_NAME);
			String key = makeMapKey(vendorName, packName);

			if (!packsMap.containsKey(key)) {
				PackNode parent = (PackNode) versionNode.getParent();
				packsMap.put(key, parent);
			}
		}
		// Used for adding selectors.
		fPacksMap = packsMap;
	}

	/**
	 * Add unique selectors to each package, by inspecting the outline and
	 * external definitions in the latest version.
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

			fOut.println("Parsing PDSC file \"" + file.getPath() + "\"...");
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
	 * Get the devices from all installed packs, to be used by the device
	 * selection properties page in project settings page.
	 * 
	 * @return a tree of nodes, with Devices/Boards, Vendors, Family, Subfamily,
	 *         Device.
	 */
	// @Override
	public Node getInstalledObjectsForBuild() {

		return getInstalledObjectsForBuild(new DurationMonitor());
	}

	private Node getInstalledObjectsForBuild(final DurationMonitor dm) {

		if (fInstalledObjectsForBuild != null) {

			// Return the in-memory cached tree
			return fInstalledObjectsForBuild;
		}

		assert dm != null;
		dm.displayTimeAndRun(new Runnable() {

			public void run() {
				fInstalledObjectsForBuild = loadInstalledObjectsForBuild(dm);
			}
		});
		return fInstalledObjectsForBuild;
	}

	// Update the tree, from cache or from original pdsc files
	/**
	 * Load from the cached file or recreate the tree with the installed objects
	 * (devices/boards/books).
	 * 
	 * @return a tree with device/boards.
	 */
	private Node loadInstalledObjectsForBuild(DurationMonitor dm) {

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

		if (rootNode == null) {

			rootNode = new Node(Type.ROOT);
			Node emptyNode = Node.addNewChild(rootNode, Type.NONE);
			emptyNode.setName("No devices available, install packs first.");
		}

		// Always return a tree, even a very simple one
		return rootNode;
	}

	/**
	 * Load the cached file with the installed objects.
	 * 
	 * @param file
	 *            the cached xml file
	 * @return a tree with the parsed content of the file, or null if error.
	 */
	private Node loadCachedInstalledObjectsForBuild(File file) {

		fOut.println("Parsing cached file \"" + file.getPath() + "\".");

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

	public Leaf findInstalledDevice(String deviceVendorId, String deviceName) {

		return findInstalledDevice(deviceVendorId, deviceName, new DurationMonitor());
	}

	private Leaf findInstalledDevice(String deviceVendorId, String deviceName, DurationMonitor dm) {

		String key = makeMapKey(deviceVendorId, deviceName);
		if (fInstalledDevicesMap.containsKey(key)) {
			return fInstalledDevicesMap.get(key);
		}

		Node tree = getInstalledObjectsForBuild(dm);

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

			String installedDeviceName = installedDevice.getName();
			if (!deviceName.equals(installedDeviceName)) {
				continue; // Different device name, try next
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
			if (deviceVendorId.equals(installedDeviceVendorId)) {

				// Both device name & vendor id match
				fInstalledDevicesMap.put(key, installedDevice);
				return installedDevice;
			}
		}

		// Store the null value, as a negative ack for next searches.
		fInstalledDevicesMap.put(key, null);
		return null; // Not found
	}

	public Leaf findInstalledBoard(String boardVendorName, String boardName) {

		return findInstalledBoard(boardVendorName, boardName, new DurationMonitor());
	}

	private Leaf findInstalledBoard(String boardVendorName, String boardName, DurationMonitor dm) {

		String key = makeMapKey(boardVendorName, boardName);
		if (fInstalledBoardsMap.containsKey(key)) {
			return fInstalledBoardsMap.get(key);
		}

		Node tree = getInstalledObjectsForBuild(dm);

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

			String installedBoardName = installedBoard.getName();

			if (!boardName.equals(installedBoardName)) {
				continue; // Different board name, try next
			}
			String installedBoardVendorName = installedBoard.getProperty(Property.VENDOR_NAME);
			if (boardVendorName.equals(installedBoardVendorName)) {
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
	 *            a string with the node type where the search stops; the node
	 *            of this type is not inspected; if null, the search will stop
	 *            at the tree root
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

	public String getDestinationFolder(Leaf node) {

		return getDestinationFolder(node, new DurationMonitor());
	}

	public String getDestinationFolder(Leaf node, DurationMonitor dm) {

		String vendorName = collectProperty(node, Property.PACK_VENDOR, Type.DEVICES_SUBTREE);
		String packName = collectProperty(node, Property.PACK_NAME, Type.DEVICES_SUBTREE);
		String version = collectProperty(node, Property.PACK_VERSION, Type.DEVICES_SUBTREE);

		Leaf summaryVersionNode = findPackVersion(vendorName, packName, version, dm);

		String destFolder = "";
		if (summaryVersionNode != null) {
			destFolder = summaryVersionNode.getProperty(Property.DEST_FOLDER);
		}
		return destFolder;
	}

	// Called from debugger.
	@Override
	public IPath getSVDAbsolutePath(String deviceVendorId, String deviceName) {

		Leaf installedDeviceNode = findInstalledDevice(deviceVendorId, deviceName);

		if (installedDeviceNode == null) {
			return null;
		}

		String svdFile = installedDeviceNode.getPropertyWithParent(Property.SVD_FILE, null);
		if (svdFile == null) {
			return null;
		}

		String destFolder = getDestinationFolder(installedDeviceNode);
		IPath path;
		try {
			path = PacksStorage.getFolderPath().append(destFolder).append(svdFile);
		} catch (IOException e) {
			return null;
		}

		return path;
	}

	// ------------------------------------------------------------------------

	// Will be called from template code
	/**
	 * Get the list of files referring to CMSIS Core (ARM headers and vendor
	 * headers and source files), for the given device and compiler.
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
	 * Get the register details (address and bit fields) for display/modify in
	 * the debug perspective.
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
