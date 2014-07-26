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
import ilg.gnuarmeclipse.packs.core.data.IDataManager;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.xcdl.GenericParser;
import ilg.gnuarmeclipse.packs.xcdl.GenericSerialiser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.Path;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

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
	private Node fInstalledDevicesForBuild;
	private MessageConsoleStream fOut;
	private List<IDataManagerListener> fListeners;

	public DataManager() {

		fOut = ConsoleStream.getConsoleOut();

		fStorage = PacksStorage.getInstance();
		fInstalledDevicesForBuild = null;

		fListeners = new ArrayList<IDataManagerListener>();
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

		// System.out.println("PacksStorage notifyRefresh()");
		DataManagerEvent event = new DataManagerEvent(this,
				DataManagerEvent.Type.UPDATE_PACKS);

		notifyListener(event);

		// Force to re-cache
		clearCachedInstalledDevicesForBuild();
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

		BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

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

	private Node updateInstalledDevicesForBuild() {

		File devicesFile = fStorage.getFile(
				new Path(PacksStorage.CACHE_FOLDER),
				PacksStorage.DEVICES_FILE_NAME);

		Node rootNode = null;

		if (devicesFile != null && devicesFile.exists()) {

			rootNode = parseCachedInstalledDevicesForBuild(devicesFile);

		}

		if (rootNode == null) {

			// Parse all installed packages
			rootNode = parseInstalledDevicesForBuild();

		}

		if (rootNode != null) {
			// Save cached file for future use
			GenericSerialiser serialiser = new GenericSerialiser();
			try {
				serialiser.serialise(rootNode, devicesFile);
			} catch (IOException e) {

				e.printStackTrace();

				fOut.println(e.getMessage());
				Utils.reportError(e.getMessage());
			}
		}

		if (rootNode == null) {

			rootNode = new Node(Type.ROOT);
			Node emptyNode = Node.addNewChild(rootNode, Type.NONE);
			emptyNode.setName("No devices available, install packs first.");
		}

		return rootNode;
	}

	private Node parseCachedInstalledDevicesForBuild(File file) {

		long beginTime = System.currentTimeMillis();

		fOut.println();
		fOut.println(Utils.getCurrentDateTime());

		fOut.println("Parsing \"" + file + "\"");

		Node node = null;
		try {
			// Parse the cached file
			Document document = Xml.parseFile(file);
			GenericParser parser = new GenericParser();
			node = parser.parse(document);
		} catch (DocumentParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}
		fOut.print("Completed in ");
		fOut.print(duration + "ms, ");

		return node;
	}

	private Node parseInstalledDevicesForBuild() {

		List<PackNode> versionsList = fStorage
				.getInstalledPacksLatestVersionsList();

		PdscParserForBuild pdsc = new PdscParserForBuild();

		// PdscGenericParser p2 = new PdscGenericParser();

		Node boardsNode = new Node(Type.FOLDER);
		boardsNode.setName("Boards");
		Node devicesNode = new Node(Type.FOLDER);
		devicesNode.setName("Devices");

		long beginTime = System.currentTimeMillis();

		fOut.println();
		fOut.println(Utils.getCurrentDateTime());

		fOut.println("Extracting devices&boards... ");

		int count = 0;
		for (PackNode node : versionsList) {

			String pdscName = node.getProperty(Property.PDSC_NAME);
			String version = node.getName();

			File file = fStorage.getCachedPdsc(pdscName, version);

			fOut.println("Parsing \"" + file + "\"");
			try {
				pdsc.parseXml(file);

				pdsc.parseDevices(devicesNode);

				// p2.setDocument(pdsc.getDocument());
				// Node n2 = p2.parse();

				// GenericSerialiser s2 = new GenericSerialiser();
				// try {
				// s2.serialise(n2, new PrintWriter(System.out));
				// } catch (IOException e1) {
				// // TODO Auto-generated catch block
				// e1.printStackTrace();
				// }

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
		fOut.print("Completed in ");
		fOut.print(duration + "ms, ");
		if (count == 0) {
			fOut.println("No packages.");
		} else if (count == 1) {
			fOut.println("1 package.");
		} else {
			fOut.println(count + " packages.");
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
