/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel;

import ilg.gnuarmeclipse.core.Activator;
import ilg.gnuarmeclipse.core.Xml;
import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.data.IPacksDataManager;
import ilg.gnuarmeclipse.packs.core.data.PacksDataManagerFactoryProxy;
import ilg.gnuarmeclipse.packs.core.data.PacksStorage;
import ilg.gnuarmeclipse.packs.core.data.SvdGenericParser;
import ilg.gnuarmeclipse.packs.core.tree.AbstractTreePreOrderIterator;
import ilg.gnuarmeclipse.packs.core.tree.ITreeIterator;
import ilg.gnuarmeclipse.packs.core.tree.Leaf;
import ilg.gnuarmeclipse.packs.core.tree.Property;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.console.MessageConsoleStream;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Utilities for processing CMSIS SVD files.
 * <p>
 * According to the SVD.xsd, a scaledNonNegativeInteger is defined as
 * "[+]?(0x|0X|#)?[0-9a-fA-F]+[kmgtKMGT]?"
 */
public class SvdUtils {

	// ------------------------------------------------------------------------

	public static int parseNonNegativeInt(String str)
			throws NumberFormatException {

		int radix = 10;
		if ((str.startsWith("0x")) || (str.startsWith("0X"))) {
			radix = 16;
			str = str.substring(2);
		} else if (str.startsWith("0")) {
			radix = 8;
		}
		return Integer.parseInt(str, radix);
	}

	public static long parseNonNegativeLong(String str)
			throws NumberFormatException {

		int radix = 10;
		if ((str.startsWith("0x")) || (str.startsWith("0X"))) {
			radix = 16;
			str = str.substring(2);
		} else if (str.startsWith("0")) {
			radix = 8;
		}
		return Long.parseLong(str, radix);
	}

	public static Integer parseNonNegativeInteger(String str)
			throws NumberFormatException {

		int radix = 10;
		if ((str.startsWith("0x")) || (str.startsWith("0X"))) {
			radix = 16;
			str = str.substring(2);
		} else if (str.startsWith("0")) {
			radix = 8;
		}
		return new Integer(Integer.parseInt(str, radix));
	}

	public static BigInteger parseNonNegativeBigInteger(String str)
			throws NumberFormatException {

		int radix = 10;
		if ((str.startsWith("0x")) || (str.startsWith("0X"))) {
			radix = 16;
			str = str.substring(2);
		} else if (str.startsWith("0")) {
			radix = 8;
		}
		return new BigInteger(str, radix);
	}

	/**
	 * Return the node property value. If missing, iterate to parent.
	 * 
	 * @param node
	 *            the tree node.
	 * @param name
	 *            a string with the property name.
	 * @param defaultValue
	 *            a string with the default value, if not found.
	 * @return a string with the property value or the default value.
	 */
	public static String getProperty(Leaf node, String name, String defaultValue) {

		return getProperty(node, null, name, defaultValue);
	}

	public static String getProperty(Leaf node, Leaf derivedFromNode,
			String name, String defaultValue) {

		String property = node.getProperty(name);
		if (!property.isEmpty()) {
			return property;
		}

		if (derivedFromNode != null) {
			property = derivedFromNode.getProperty(name);
			if (!property.isEmpty()) {
				return property;
			}
		}

		property = "";

		Leaf n = node.getParent();
		while (n != null) {

			property = n.getProperty(name);
			if (!property.isEmpty()) {
				return property;
			}

			n = n.getParent();
		}

		// If nothing happened, return the default value.
		return defaultValue;
	}

	// ------------------------------------------------------------------------

	/**
	 * Identify the SVD file associated with the given device and parse it with
	 * the generic parser.
	 * 
	 * @param deviceVendorId
	 *            a string with the numeric vendor id.
	 * @param deviceName
	 *            a string with the CMSIS device name.
	 * @return a tree with the parsed SVD.
	 * @throws CoreException
	 *             differentiate when the Packs plug-in is not installed or when
	 *             the device is not found in the installed packages.
	 */
	public static Leaf getTree(String deviceVendorId, String deviceName)
			throws CoreException {

		MessageConsoleStream out = ConsoleStream.getConsoleOut();

		IPacksDataManager dataManager = PacksDataManagerFactoryProxy
				.getInstance().createDataManager();

		if (dataManager == null) {
			throw new CoreException(
					new Status(Status.ERROR, Activator.PLUGIN_ID,
							"Peripheral descriptions are available only via the Packs plug-in."));
		}

		Leaf installedDeviceNode = dataManager.findInstalledDevice(
				deviceVendorId, deviceName);

		if (installedDeviceNode == null) {
			throw new CoreException(
					new Status(Status.ERROR, Activator.PLUGIN_ID,
							"There are no peripheral descriptions available, install the required packs."));
		}

		String svdFile = installedDeviceNode.getProperty(Property.SVD_FILE);

		String destFolder = dataManager
				.getDestinationFolder(installedDeviceNode);
		IPath path = new Path(destFolder).append(svdFile);

		try {

			out.println("Parsing SVD file \"" + path.toString() + "\"...");
			File file = PacksStorage.getFileObject(path.toString());
			Document document = Xml.parseFile(file);

			SvdGenericParser parser = new SvdGenericParser();
			return parser.parse(document);

		} catch (ParserConfigurationException e) {
			Activator.log(e);
			throw new CoreException(new Status(Status.ERROR,
					Activator.PLUGIN_ID,
					"Failed to get the peripheral descriptions.", e));
		} catch (SAXException e) {
			Activator.log(e);
			throw new CoreException(new Status(Status.ERROR,
					Activator.PLUGIN_ID,
					"Failed to get the peripheral descriptions.", e));
		} catch (IOException e) {
			Activator.log(e);
			throw new CoreException(new Status(Status.ERROR,
					Activator.PLUGIN_ID,
					"Failed to get the peripheral descriptions.", e));
		}
	}

	public static List<Leaf> getPeripherals(Leaf tree) {

		class SvdPeriphIterator extends AbstractTreePreOrderIterator {

			@Override
			public boolean isIterable(Leaf node) {
				if (node.isType("peripheral")) {
					return true;
				}
				return false;
			}

			@Override
			public boolean isLeaf(Leaf node) {
				if (node.isType("peripheral")) {
					return true;
				}
				return false;
			}
		}

		List<Leaf> list = new LinkedList<Leaf>();
		if (tree != null) {

			ITreeIterator peripheralNodes = new SvdPeriphIterator();

			peripheralNodes.setTreeNode(tree);
			for (Leaf peripheral : peripheralNodes) {
				list.add(peripheral);
			}

			if (!list.isEmpty()) {
				return list;
			}

		}

		return list;
	}

	// ------------------------------------------------------------------------
}
