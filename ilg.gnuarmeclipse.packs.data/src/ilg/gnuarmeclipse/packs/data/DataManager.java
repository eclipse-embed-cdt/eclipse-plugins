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

import ilg.gnuarmeclipse.packs.cmsis.PdscGenericParser;
import ilg.gnuarmeclipse.packs.cmsis.PdscTreeParserForBuild;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.data.IDataManager;
import ilg.gnuarmeclipse.packs.core.tree.AbstractTreePreOrderIterator;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Selector;
import ilg.gnuarmeclipse.packs.core.tree.TreePreOrderIterator;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.data.jobs.BusyIndicatorWithDuration;
import ilg.gnuarmeclipse.packs.xcdl.GenericParser;
import ilg.gnuarmeclipse.packs.xcdl.GenericSerialiser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;

public class DataManager implements IDataManager {

	private static DataManager sfInstance;

	public static synchronized DataManager getInstance() {

		if (sfInstance == null) {
			sfInstance = new DataManager();
		}

		return sfInstance;
	}

	// ------------------------------------------------------------------------

	private Node fInstalledObjectsForBuild;
	private MessageConsoleStream fOut;
	private List<IDataManagerListener> fListeners;
	private Map<String, Node> fParsedPdsc;
	private List<PackNode> fInstalledPacksLatestVersionsList;
	private Node fRepositoriesTree;
	private List<PackNode> fPacksVersionsList;

	// A map of maps to find versions faster
	private Map<String, Map<String, PackNode>> fPacksVersionsMap;
	// A map of package nodes
	private Map<String, PackNode> fPacksMap;

	// private Map<String, PackNode> fDevicesMap;

	public DataManager() {

		fOut = ConsoleStream.getConsoleOut();

		fInstalledObjectsForBuild = null;

		fListeners = new ArrayList<IDataManagerListener>();
		fParsedPdsc = new HashMap<String, Node>();

		fRepositoriesTree = null;
		fInstalledPacksLatestVersionsList = null;
		fPacksVersionsList = null;
		fPacksVersionsMap = new TreeMap<String, Map<String, PackNode>>();
		fPacksMap = new TreeMap<String, PackNode>();

		// fDevicesMap = new TreeMap<String, PackNode>();

	}

	// ------------------------------------------------------------------------

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

	// Used by 'Refresh', 'LoadReposSummaries'
	public void notifyNewInput() {

		// System.out.println("PacksStorage notifyRefresh()");
		DataManagerEvent event = new DataManagerEvent(this,
				DataManagerEvent.Type.NEW_INPUT);

		notifyListener(event);
	}

	// Used when a package is installed/removed
	public void notifyInstallRemove() {

		// Force to re-cache
		clearCachedPacksVersions();
		clearCachedInstalledDevicesForBuild();

		// System.out.println("PacksStorage notifyRefresh()");
		DataManagerEvent event = new DataManagerEvent(this,
				DataManagerEvent.Type.UPDATE_PACKS);

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

	// ------------------------------------------------------------------------

	// Called from TabDevice in managedbuild.cross
	@Override
	public Node getInstalledObjectsForBuild() {

		if (fInstalledObjectsForBuild != null) {

			// Return the in-memory cached tree
			return fInstalledObjectsForBuild;
		}

		BusyIndicatorWithDuration.showWhile("Extracting devices & boards... ",
				new Runnable() {

					public void run() {
						fInstalledObjectsForBuild = loadInstalledObjectsForBuild();
					}
				});

		return fInstalledObjectsForBuild;
	}

	// Will be called from template code
	@Override
	public Node getCmsisCoreFiles(String deviceName, String compiler) {
		// TODO Auto-generated method stub
		return null;
	}

	// Will be called from debugger
	@Override
	public Node getRegisterDetailsForDebug(String deviceName) {
		// TODO Auto-generated method stub
		return null;
	}

	// ------------------------------------------------------------------------

	public Node getParsedPdscTree(String name, String version) {

		String fileName = PacksStorage.makeCachedPdscName(name, version);
		Node node = fParsedPdsc.get(fileName);
		if (node != null) {
			return node;
		}

		// Not found in cache, load from file.
		long beginTime = System.currentTimeMillis();

		try {

			File file = PacksStorage.getCachedFileObject(fileName);
			fOut.println("Parsing cached PDSC file \"" + file + "\"...");
			Document document = Xml.parseFile(file);

			PdscGenericParser parser = new PdscGenericParser();
			node = parser.parse(document);

			if (node != null) {
				// Remember for future use
				fParsedPdsc.put(fileName, node);
			}

			long endTime = System.currentTimeMillis();
			long duration = endTime - beginTime;
			if (duration == 0) {
				duration = 1;
			}

			fOut.println("File parsed in " + duration + "ms.");

		} catch (Exception e) {
			String msg = e.getMessage() + ", file: " + fileName;
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(e);
		}

		return node;
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

	// ------------------------------------------------------------------------

	private void clearCachedInstalledDevicesForBuild() {

		System.out.println("clearCachedInstalledDevicesForBuild()");

		fInstalledObjectsForBuild = null;

		File devicesFile = null;
		try {
			devicesFile = PacksStorage
					.getCachedFileObject(PacksStorage.INSTALLED_DEVICES_FILE_NAME);
		} catch (IOException e) {
			;
		}

		if (devicesFile != null) {
			devicesFile.delete();
		}
	}

	// Update the tree, from cache or from original pdsc files
	private Node loadInstalledObjectsForBuild() {

		Node rootNode = null;
		File devicesFile = null;
		try {
			devicesFile = PacksStorage
					.getCachedFileObject(PacksStorage.INSTALLED_DEVICES_FILE_NAME);

			if (devicesFile.exists()) {

				// If the cached file exists, try to use it
				rootNode = parseCachedInstalledDevicesForBuild(devicesFile);
				// However, it may fail
			}

		} catch (IOException e1) {
		}

		if (rootNode == null) {

			// Extract devices from all installed packages
			rootNode = parseInstalledDevicesForBuild();

			//
			addPdscNames(rootNode);

			if (rootNode != null) {

				fOut.println("Writing cache file \"" + devicesFile + "\".");
				// Save cached file for future use
				GenericSerialiser serialiser = new GenericSerialiser();
				try {
					serialiser.serialise(rootNode, devicesFile);
				} catch (IOException e) {

					String msg = e.getMessage() + ", file: "
							+ devicesFile.getName();
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

	private void addPdscNames(Node tree) {

		class FamiliesIterator extends TreePreOrderIterator {

			@Override
			public boolean isIterable(Leaf node) {
				if (node.isType(Type.FAMILY)) {
					return true;
				}
				return false;
			}
		}

		FamiliesIterator familyNodes = new FamiliesIterator();
		familyNodes.setTreeNode(tree);

		Node repositoriesTree = getRepositoriesTree();

		AbstractTreePreOrderIterator repoFamilyNodes = new FamiliesIterator();
		repoFamilyNodes.setTreeNode(repositoriesTree);

		for (Leaf familyNode : familyNodes) {

			String familyName = familyNode.getName();
			String vendorName = familyNode.getProperty(Property.VENDOR_NAME);
			String vendorId = familyNode.getProperty(Property.VENDOR_ID);

			repoFamilyNodes.setTreeNode(repositoriesTree);
			for (Leaf repoFamilyNode : repoFamilyNodes) {

				String familyName2 = repoFamilyNode.getName();
				String vendorName2 = repoFamilyNode
						.getProperty(Property.VENDOR_NAME);
				String vendorId2 = repoFamilyNode
						.getProperty(Property.VENDOR_ID);

				if (familyName.equals(familyName2)
						&& vendorName.equals(vendorName2)
						&& vendorId.equals(vendorId2)) {

					Node versionNode = repoFamilyNode.getParent();
					while (versionNode != null
							&& !versionNode.isType(Type.VERSION)) {
						versionNode = versionNode.getParent();
					}

					if (versionNode != null) {
						String pdscName = versionNode
								.getProperty(Property.PDSC_NAME);
						String versionName = versionNode
								.getProperty(Property.VERSION_NAME);

						// Add properties to identify the PDSC
						familyNode.putNonEmptyProperty(Property.PDSC_NAME,
								pdscName);
						familyNode.putNonEmptyProperty(Property.VERSION_NAME,
								versionName);

						// Keep the most recent version installed.
						// Hopefully the same family will not be defined in
						// multiple packages...
						// TODO: process deprecation.
						break;
					}
				}
			}
		}
	}

	private Node parseCachedInstalledDevicesForBuild(File file) {

		fOut.println("Parsing cached file \"" + file + "\".");

		Node node = null;
		try {
			// Parse the cached file
			Document document = Xml.parseFile(file);
			GenericParser parser = new GenericParser();
			node = parser.parse(document);
		} catch (Exception e) {
			String msg = e.getMessage() + ", file: " + file.getName();
			fOut.println("Error: " + msg);
			Utils.reportError(msg);
			Activator.log(e);
		}

		return node;
	}

	// Return null if something went wrong
	private Node parseInstalledDevicesForBuild() {

		List<PackNode> versionsList = getInstalledPacksLatestVersionsList();

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

			Node tree = getParsedPdscTree(pdscName, version);

			int countDevices = pdsc.parseDevices(tree, devicesNode);
			totalCountDevices += countDevices;

			// Using boards requires existing devices to be present,
			// to get the memory map
			int countBoards = pdsc.parseBoards(tree, boardsNode);
			totalCountBoards += countBoards;

			// Necessary?
			updateBoardsDevices(boardsNode, devicesNode);

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

	private void updateBoardsDevices(Node boardsNode, Node devicesNode) {

		// TODO: check if boards mounted devices are present in devices.
		// If so, copy memory map, otherwise remove
	}

	private void clearCachedPacksVersions() {

		fInstalledPacksLatestVersionsList = null;
	}

	// Filter the versions to the installed latest ones
	private List<PackNode> getInstalledPacksLatestVersionsList() {

		if (fInstalledPacksLatestVersionsList != null) {
			return fInstalledPacksLatestVersionsList;
		}

		// Filter installed packages
		List<PackNode> installedPackages = new LinkedList<PackNode>();
		List<PackNode> packsVersionsList = getPacksVersionsList();
		if (packsVersionsList != null) {
			for (PackNode versionNode : getPacksVersionsList()) {
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

	// ------------------------------------------------------------------------

	public void loadCachedReposContent() {

		Node node = new Node(Type.ROOT);
		fPacksVersionsList = Repos.getInstance().loadCachedReposContent(node);
		fRepositoriesTree = node;

		preparePublicData();

		updateInstalledVersions(fPacksVersionsList);

		addSelectors();
	}

	private void preparePublicData() {

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
		fPacksMap = packsMap;

	}

	private void addSelectors() {

		// Add unique selectors to each package, by inspecting the outline and
		// external definitions in the latest version.

		for (PackNode packNode : fPacksMap.values()) {

			Node versionNode = (Node) packNode.getFirstChild();
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

	// ------------------------------------------------------------------------

	private List<PackNode> getPacksVersionsList() {

		if (fPacksVersionsList != null) {
			return fPacksVersionsList;
		}

		loadCachedReposContent();

		return fPacksVersionsList;
	}

	public Node getRepositoriesTree() {

		if (fRepositoriesTree != null) {
			return fRepositoriesTree;
		}

		BusyIndicatorWithDuration.showWhile(null, new Runnable() {

			public void run() {
				loadCachedReposContent();
			}
		});

		return fRepositoriesTree;
	}

	// Add (Property.INSTALLED, true) to installed version nodes and
	// to parent pack nodes.
	private void updateInstalledVersions(List<PackNode> list) {

		if (list == null || list.size() == 0) {
			return;
		}

		fOut.println("Identifying installed packages...");

		int count = 0;

		// Check if the packages are installed
		for (Leaf versionNode : list) {

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

	// Construct an internal representation of the map key, inspired
	// form CMSIS Pack examples.
	public String makeMapKey(String vendorName, String packName) {

		String key = vendorName + "::" + packName;
		return key;
	}

	// ------------------------------------------------------------------------
}
