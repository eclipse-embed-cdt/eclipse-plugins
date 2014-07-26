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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.Path;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;
import org.xml.sax.SAXException;

import ilg.gnuarmeclipse.packs.cmsis.PdscGenericParser;
import ilg.gnuarmeclipse.packs.cmsis.PdscParserForBuild;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.data.IDataManager;
import ilg.gnuarmeclipse.packs.core.tree.Node;
import ilg.gnuarmeclipse.packs.core.tree.PackNode;
import ilg.gnuarmeclipse.packs.core.tree.Property;
import ilg.gnuarmeclipse.packs.core.tree.Type;
import ilg.gnuarmeclipse.packs.xcdl.GenericSerialiser;

public class DataManager implements IDataManager, IPacksStorageListener {

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

	public DataManager() {

		fOut = ConsoleStream.getConsoleOut();

		fStorage = PacksStorage.getInstance();
		fInstalledDevicesForBuild = null;

		// Subscribe to Storage notifications
		fStorage.addListener(this);
	}

	@Override
	public void packsChanged(PacksStorageEvent event) {

		String eventType = event.getType();
		if (PacksStorageEvent.Type.UPDATE_VERSIONS.equals(eventType)
				|| PacksStorageEvent.Type.NEW_INPUT.equals(eventType)) {

			// Force to re-cache
			fInstalledDevicesForBuild = null;
		}
	}

	// Called from TabDevice in managedbuild.cross
	@Override
	public Node getInstalledDevicesForBuild() {

		if (fInstalledDevicesForBuild != null) {

			// Return the in-memory cached tree
			return fInstalledDevicesForBuild;
		}

		Activator.getInstance().waitLoadReposJob();

		System.out.println("LoadRepos must be ready");

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

	private Node updateInstalledDevicesForBuild() {

		File devicesFile = fStorage.getFile(
				new Path(PacksStorage.CACHE_FOLDER),
				PacksStorage.DEVICES_FILE_NAME);

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

		return rootNode;
	}

	private Node parseInstalledDevicesForBuild() {

		List<PackNode> versionsList = fStorage
				.getInstalledPacksLatestVersionsList();

		PdscParserForBuild pdsc = new PdscParserForBuild();

		PdscGenericParser p2 = new PdscGenericParser();

		Node boardsNode = new Node(Type.NONE);
		boardsNode.setName("Boards");
		Node devicesNode = new Node(Type.NONE);
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

				p2.setDocument(pdsc.getDocument());
				Node n2 = p2.parse();

				GenericSerialiser s2 = new GenericSerialiser();
				try {
					s2.serialise(n2, new PrintWriter(System.out));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

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
