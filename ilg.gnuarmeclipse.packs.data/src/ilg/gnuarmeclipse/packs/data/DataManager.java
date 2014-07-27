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
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Property;
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

import org.eclipse.core.runtime.Path;
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

	private PacksStorage fStorage;
	private Repos fRepos;
	private Node fInstalledDevicesForBuild;
	private MessageConsoleStream fOut;
	private List<IDataManagerListener> fListeners;
	private Map<String, Node> fParsedPdsc;
	private List<PackNode> fInstalledPacksLatestVersionsList;
	private List<PackNode> fPacksVersionsList;

	public DataManager() {

		fOut = ConsoleStream.getConsoleOut();

		fRepos = Repos.getInstance();
		fStorage = PacksStorage.getInstance();
		fInstalledDevicesForBuild = null;

		fListeners = new ArrayList<IDataManagerListener>();
		fParsedPdsc = new HashMap<String, Node>();

		fInstalledPacksLatestVersionsList = null;
		fPacksVersionsList = null;
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
	public Node getInstalledDevicesForBuild() {

		if (fInstalledDevicesForBuild != null) {

			// Return the in-memory cached tree
			return fInstalledDevicesForBuild;
		}

		Activator.getInstance().waitLoadReposJob();

		BusyIndicatorWithDuration.showWhile("Extracting devices & boards... ",
				new Runnable() {

					public void run() {
						fInstalledDevicesForBuild = updateInstalledDevicesForBuild();
					}
				});

		return fInstalledDevicesForBuild;
	}

	// Will be called from managed build DocsView
	@Override
	public Node getDocsTree(String deviceVendorId, String deviceName,
			String boardVendorName, String boardName) {

		Node devicesRoot = new Node(Type.ROOT);
		devicesRoot.setName("Docs");

		Node deviceDocsNode = Node.addNewChild(devicesRoot, Type.FOLDER);
		deviceDocsNode.setName("Device docs");

		if (true) {
			Node book = Node.addNewChild(deviceDocsNode, Type.BOOK);
			book.setName("Cortex-M4");
			book.putProperty(Node.FILE_PROPERTY, "xx.pdf");
			book.putProperty(Node.CATEGORY_PROPERTY, "xx.pdf");
		} else {
			Node n = fStorage.getLatestInstalledPackForDevice(deviceVendorId,
					deviceName);
		}
		Node boardDocsNode = Node.addNewChild(devicesRoot, Type.FOLDER);
		boardDocsNode.setName("Board docs");

		if (true) {
			Node book = Node.addNewChild(boardDocsNode, Type.BOOK);
			book.setName("Getting Started");
			book.putProperty(Node.FILE_PROPERTY, "yy.pdf");
			book.putProperty(Node.CATEGORY_PROPERTY, "manual");
		} else {
			Node n = fStorage.getLatestInstalledPackForBoard(boardVendorName,
					boardName);

		}

		return devicesRoot;
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

	// ------------------------------------------------------------------------

	private void clearCachedInstalledDevicesForBuild() {

		System.out.println("clearCachedInstalledDevicesForBuild()");

		fInstalledDevicesForBuild = null;

		File devicesFile = fStorage.getFile(
				new Path(PacksStorage.CACHE_FOLDER),
				PacksStorage.DEVICES_FILE_NAME);

		if (devicesFile != null) {
			devicesFile.delete();
		}
	}

	// Update the tree, from cache or from original pdsc files
	private Node updateInstalledDevicesForBuild() {

		Node rootNode = null;
		File devicesFile = null;
		try {
			devicesFile = PacksStorage
					.getCachedFileObject(PacksStorage.DEVICES_FILE_NAME);

			if (devicesFile.exists()) {

				// If the cached file exists, try to use it
				rootNode = parseCachedInstalledDevicesForBuild(devicesFile);
				// However, it may fail
			}

		} catch (IOException e1) {
		}

		if (rootNode == null) {

			// Parse all installed packages
			rootNode = parseInstalledDevicesForBuild();

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

		Node boardsNode = new Node(Type.FOLDER);
		boardsNode.setName("Boards");
		Node devicesNode = new Node(Type.FOLDER);
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

	private List<PackNode> getPacksVersionsList() {

		if (fPacksVersionsList != null) {
			return fPacksVersionsList;
		}

		fPacksVersionsList = fRepos.loadCachedReposContent();
		updateInstalledVersions(fPacksVersionsList);

		return fPacksVersionsList;
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

}
