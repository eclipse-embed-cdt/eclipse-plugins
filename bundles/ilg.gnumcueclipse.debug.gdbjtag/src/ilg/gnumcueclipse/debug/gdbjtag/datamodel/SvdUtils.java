/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.datamodel;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.console.MessageConsoleStream;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ilg.gnumcueclipse.core.Xml;
import ilg.gnumcueclipse.debug.gdbjtag.Activator;
import ilg.gnumcueclipse.debug.gdbjtag.data.SVDPathManagerProxy;
import ilg.gnumcueclipse.packs.core.ConsoleStream;
import ilg.gnumcueclipse.packs.core.data.IPacksDataManager;
import ilg.gnumcueclipse.packs.core.data.JsonSimpleParser;
import ilg.gnumcueclipse.packs.core.data.PacksDataManagerFactoryProxy;
import ilg.gnumcueclipse.packs.core.data.SvdGenericParser;
import ilg.gnumcueclipse.packs.core.data.XsvdGenericParser;
import ilg.gnumcueclipse.packs.core.tree.AbstractTreePreOrderIterator;
import ilg.gnumcueclipse.packs.core.tree.ITreeIterator;
import ilg.gnumcueclipse.packs.core.tree.Leaf;

/**
 * Utilities for processing CMSIS SVD files.
 * <p>
 * According to the SVD.xsd, a scaledNonNegativeInteger is defined as
 * "[+]?(0x|0X|#)?[0-9a-fA-F]+[kmgtKMGT]?"
 */
public class SvdUtils {

	// ------------------------------------------------------------------------

	private static long computeScale(String str) {

		long scale = 1;
		if (!str.isEmpty()) {
			String lastChar = str.substring(str.length() - 1);
			if (lastChar.matches("[kmgtKMGT]")) {
				lastChar = lastChar.toLowerCase();
				if ("k".equals(lastChar)) {
					scale = 1024;
				} else if ("m".equals(lastChar)) {
					scale = 1024 * 1024;
				} else if ("g".equals(lastChar)) {
					scale = 1024 * 1024 * 1024;
				} else if ("7".equals(lastChar)) {
					scale = 1024 * 1024 * 1024 * 1024;
				}
			}
		}
		return scale;
	}

	private static String adjustForScale(String str, long scale) {
		if (scale != 1) {
			return str.substring(0, str.length() - 2);
		}
		return str;
	}

	private static int computeRadix(String str) {

		int radix = 10;
		if ((str.startsWith("0x")) || (str.startsWith("0X"))) {
			radix = 16;
		} else if ((str.startsWith("0b")) || (str.startsWith("0B"))) {
			radix = 2;
		} else if (str.startsWith("#")) {
			radix = 2;
		}

		return radix;
	}

	private static String adjustForRadix(String str) {

		if ((str.startsWith("0x")) || (str.startsWith("0X"))) {
			return str.substring(2); // Skip 0x, 0X
		} else if ((str.startsWith("0b")) || (str.startsWith("0B"))) {
			return str.substring(2); // Skip 0b, 0B
		} else if (str.startsWith("#")) {
			return str.substring(1); // Skip #
		}

		// Decimal numbers have no radix prefix to skip.
		return str;
	}

	private static String adjustForSign(String str) {

		if (str.startsWith("+")) {
			return str.substring(1);
		}

		return str;
	}

	public static long parseScaledNonNegativeLong(String str) throws NumberFormatException {

		str = adjustForSign(str);
		int radix = computeRadix(str);
		str = adjustForRadix(str);
		long scale = computeScale(str);
		str = adjustForScale(str, scale);

		long value = Long.parseLong(str, radix);
		if (scale != 1) {
			value *= scale;
		}
		return value;
	}

	public static BigInteger parseScaledNonNegativeBigInteger(String str) throws NumberFormatException {

		str = adjustForSign(str);
		int radix = computeRadix(str);
		str = adjustForRadix(str);
		long scale = computeScale(str);
		str = adjustForScale(str, scale);

		BigInteger value = new BigInteger(str, radix);
		if (scale != 1) {
			value = value.multiply(new BigInteger(String.valueOf(scale)));
		}
		return value;
	}

	// ------------------------------------------------------------------------

	/**
	 * Identify the SVD file associated with the given device.
	 * 
	 * @param deviceVendorId
	 *            a string with the numeric vendor id.
	 * @param deviceName
	 *            a string with the CMSIS device name.
	 * @return a path.
	 * @throws CoreException
	 *             differentiate when the Packs plug-in is not installed or when the
	 *             device is not found in the installed packages.
	 */
	public static IPath getSvdPath(String deviceVendorId, String deviceName) throws CoreException {

		IPath path = null;

		// Try to get the SVD from a custom provider, like in DAVE.
		SVDPathManagerProxy pathManager = SVDPathManagerProxy.getInstance();
		path = pathManager.getSVDAbsolutePath(deviceVendorId, deviceName);

		if (path == null) {

			// Try to get the SVD from the CMSIS Packs.
			IPacksDataManager dataManager = PacksDataManagerFactoryProxy.getInstance().createDataManager();

			if (dataManager == null) {
				throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID,
						"Peripherals descriptions are available only via the Packs plug-in."));
			}

			path = dataManager.getSVDAbsolutePath(deviceVendorId, deviceName);

			if (path == null) {
				throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID,
						"There are no peripherals descriptions available, install the required packs."));
			}
		}
		assert path != null;
		return path;
	}

	/**
	 * Parse the SVD file with the generic parser.
	 * 
	 * @param path
	 *            an absolute path to the SVD file.
	 * @return a tree with the parsed SVD.
	 * @throws CoreException
	 *             differentiate when the Packs plug-in is not installed or when the
	 *             device is not found in the installed packages.
	 */
	public static Leaf getTree(IPath path) throws CoreException {

		assert path != null;

		MessageConsoleStream out = ConsoleStream.getConsoleOut();
		try {

			out.println("Parsing SVD file \"" + path.toOSString() + "\"...");

			File file = path.toFile();
			if (file == null) {
				throw new IOException(path + " File object null.");
			}

			FileReader reader = null;
			reader = new FileReader(file);
			char[] chars = new char[10];
			reader.read(chars);
			reader.close();
			String str = (new String(chars)).trim();

			if (str.startsWith("<?xml ")) {
				Document document = Xml.parseFile(file);
				SvdGenericParser parser = new SvdGenericParser();
				return parser.parse(document);
			} else if (str.startsWith("{")) {
				JSONParser parser = new JSONParser();
				reader = new FileReader(file);
				JSONObject json = (JSONObject) parser.parse(reader);
				JsonSimpleParser jsonParser = new XsvdGenericParser();
				return jsonParser.parse(json);
			} else {
				String serr = "File format does not look like XML or JSON.";
				Activator.log(serr);
				throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, serr));
			}

		} catch (ParserConfigurationException e) {
			Activator.log(e);
			throw new CoreException(
					new Status(Status.ERROR, Activator.PLUGIN_ID, "Failed to get the peripherals descriptions.", e));
		} catch (SAXParseException e) {
			Activator.log(e);
			String msg = "Failed to parse the peripherals descriptions, line: " + e.getLineNumber() + ", column: "
					+ e.getColumnNumber() + ".";
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, msg, e));
		} catch (SAXException e) {
			Activator.log(e);
			throw new CoreException(
					new Status(Status.ERROR, Activator.PLUGIN_ID, "Failed to parse the peripherals descriptions.", e));
		} catch (IOException e) {
			Activator.log(e);
			throw new CoreException(
					new Status(Status.ERROR, Activator.PLUGIN_ID, "Failed to get the peripherals descriptions.", e));
		} catch (ParseException e) {
			Activator.log(e);
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Failed to parse the JSON.", e));
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
