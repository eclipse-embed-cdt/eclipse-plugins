/*******************************************************************************
 * Copyright (c) 2014, 2020 Liviu Ionescu and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *     Alexander Fedorov (ArSysOp) - UI part extraction.
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.core.datamodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.embedcdt.core.CProjectPacksStorage;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.core.JsonUtils;
import org.eclipse.embedcdt.core.Xml;
import org.eclipse.embedcdt.debug.gdbjtag.core.ConfigurationAttributes;
import org.eclipse.embedcdt.debug.gdbjtag.core.DebugUtils;
import org.eclipse.embedcdt.debug.gdbjtag.core.data.CProjectExtraDataManagerProxy;
import org.eclipse.embedcdt.debug.gdbjtag.core.data.SVDPathManagerProxy;
import org.eclipse.embedcdt.internal.debug.gdbjtag.core.Activator;
import org.eclipse.embedcdt.packs.core.IConsoleStream;
import org.eclipse.embedcdt.packs.core.PackType;
import org.eclipse.embedcdt.packs.core.data.IPacksDataManager;
import org.eclipse.embedcdt.packs.core.data.JsonGenericParser;
import org.eclipse.embedcdt.packs.core.data.PacksDataManagerFactoryProxy;
import org.eclipse.embedcdt.packs.core.data.PacksStorage;
import org.eclipse.embedcdt.packs.core.data.SvdGenericParser;
import org.eclipse.embedcdt.packs.core.data.SvdJsGenericParser;
import org.eclipse.embedcdt.packs.core.data.XmlJsGenericParser;
import org.eclipse.embedcdt.packs.core.data.XsvdGenericParser;
import org.eclipse.embedcdt.packs.core.data.xcdl.XcdlUtils;
import org.eclipse.embedcdt.packs.core.jstree.JsObject;
import org.eclipse.embedcdt.packs.core.tree.AbstractTreePreOrderIterator;
import org.eclipse.embedcdt.packs.core.tree.ITreeIterator;
import org.eclipse.embedcdt.packs.core.tree.Leaf;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
				} else if ("t".equals(lastChar)) {
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
	 *
	 * @param cConfigDescription
	 * @return Absolute path.
	 * @throws CoreException
	 */
	public static IPath getSvdPath(ILaunchConfiguration launchConfiguration) throws CoreException {

		ICConfigurationDescription cConfigDescription = EclipseUtils.getBuildConfigDescription(launchConfiguration);
		IProject project = cConfigDescription.getProjectDescription().getProject();

		String value = launchConfiguration.getAttribute(ConfigurationAttributes.SVD_PATH, "");

		if (!value.isEmpty()) {
			value = resolveAll(value, launchConfiguration);
			File f = new File(value);
			if (f.exists() && !f.isDirectory()) {
				// Accept path only if the file exists.
				return new Path(value);
			} else {
				Activator.log("File '" + value + "' does not exist, searching packages.");
			}
		}

		// If not explicitly set by the user, search for the SVD/XSVD.
		IConfiguration config = EclipseUtils.getConfigurationFromDescription(cConfigDescription);

		// This requires the device name and supplier id, which can be found in
		// a custom storageModule in the build configuration, or in the config.xcdl
		// object of the package.json.

		String packType = PackType.XPACK_XCDL;
		String deviceId = null;
		String deviceVendorId = null;

		// TODO: add a new version of the extension point, with a third parameter,
		// the package type (CMSIS Pack vs xPack XCDL)

		CProjectExtraDataManagerProxy dataManager = CProjectExtraDataManagerProxy.getInstance();
		Map<String, String> propertiesMap = dataManager.getExtraProperties(config);
		if (propertiesMap != null && !propertiesMap.isEmpty()) {
			packType = propertiesMap.get(CProjectPacksStorage.PACK_TYPE);
			// At this point the variant is not relevant, the device is enough.
			if (packType == null || PackType.CMSIS.equals(packType)) {
				deviceVendorId = propertiesMap.get(CProjectPacksStorage.CMSIS_DEVICE_VENDOR_ID);
				deviceId = propertiesMap.get(CProjectPacksStorage.CMSIS_DEVICE_NAME);
			} else if (PackType.XPACK_XCDL.equals(packType)) {
				deviceVendorId = propertiesMap.get(CProjectPacksStorage.DEVICE_VENDOR_ID);
				deviceId = propertiesMap.get(CProjectPacksStorage.DEVICE_KEY);
			}
		}

		IPath svdPath = null;

		if (deviceVendorId == null || deviceId == null) {

			JSONObject packageJson;
			try {
				packageJson = XcdlUtils.getPackageJson(project);
				deviceId = (String) JsonUtils.get(packageJson, "config.xcdl.device.id");
				deviceVendorId = (String) JsonUtils.get(packageJson, "config.xcdl.device.supplier.id");

				svdPath = SvdUtils.getSvdProjectPath(project, packType, deviceVendorId, deviceId);
				File svdFile = svdPath.toFile();
				if (svdFile.isFile()) {
					return svdPath;
				}
			} catch (IOException e) {

			} catch (ParseException e) {
				Activator.log(e);
			}
		}

		if (deviceVendorId == null || deviceId == null) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "Assign a device to the project."));
		}

		svdPath = SvdUtils.getSvdPath(packType, deviceVendorId, deviceId, config);

		if (svdPath == null) {
			throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID, "No peripherals available."));
		}

		File svdFile = svdPath.toFile();
		if (svdFile.isFile()) {
			return svdPath;
		}

		return null;
	}

	protected static IPath getSvdProjectPath(IProject project, String packType, String supplierId, String deviceName) {

		IPath path = null;
		if (PackType.XPACK_XCDL.equals(packType)) {
			IPath projectPath = project.getLocation();

			// Search only the xpacks folder, if it exists.
			File xpacksFolder = projectPath.append("xpacks").toFile();
			if (!xpacksFolder.isDirectory()) {
				return null;
			}
			File[] folders = xpacksFolder.listFiles();
			for (int i = 0; i < folders.length; ++i) {
				if (folders[i].isDirectory()) {
					// System.out.println(folders[i]);
					File[] xcdlFiles = folders[i].listFiles(new FilenameFilter() {

						@Override
						public boolean accept(File dir, String name) {
							return name.endsWith("xcdl.json");
						}
					});
					for (int j = 0; j < xcdlFiles.length; ++j) {
						System.out.println(xcdlFiles[j]);
						String xsvd = getSvdFromXcdl(xcdlFiles[j], supplierId, deviceName);
						if (xsvd != null) {
							path = new Path(folders[i].toString());
							return path.append(xsvd);
						}
					}
				}
			}
		}

		return path;
	}

	public static String getSvdFromXcdl(File xcdlFile, String supplierId, String deviceName) {

		JSONParser parser = new JSONParser();
		try {
			FileReader reader = new FileReader(xcdlFile);
			JSONObject xcdlJson = (JSONObject) parser.parse(reader);
			JSONObject families = (JSONObject) JsonUtils.get(xcdlJson, "mcus.families");
			if (families == null) {
				return null;
			}
			// System.out.println(families);
			for (Object familyName : families.keySet()) {
				if (familyName instanceof String) {
					JSONObject family = (JSONObject) families.get(familyName);
					// System.out.println(family);
					String supplier = (String) JsonUtils.get(family, "supplier.id");
					if (!supplierId.equals(supplier)) {
						return null;
					}
					JSONObject devices = (JSONObject) family.get("devices");
					if (devices == null) {
						return null;
					}
					JSONObject device = (JSONObject) devices.get(deviceName);
					if (device == null) {
						return null;
					}
					String xsvd = (String) JsonUtils.get(device, "debug.xsvd");
					// System.out.println(xsvd);
					return xsvd;
				}
			}
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			Activator.log(e.getMessage());
		} catch (ParseException e) {
			Activator.log(e.getMessage());
		}

		return null;
	}

	/**
	 * Identify the SVD file associated with the given device.
	 *
	 * @param deviceSupplierId
	 *            a string with the numeric vendor id.
	 * @param deviceId
	 *            a string with the CMSIS device name.
	 * @return a path.
	 * @throws CoreException
	 *             differentiate when the Packs plug-in is not installed or when the
	 *             device is not found in the installed packages.
	 */
	public static IPath getSvdPath(String packType, String deviceSupplierId, String deviceId, IConfiguration config)
			throws CoreException {

		IPath path = null;

		// TODO: use packType

		// Try to get the SVD from a custom provider, like in DAVE.
		SVDPathManagerProxy pathManager = SVDPathManagerProxy.getInstance();
		path = pathManager.getSVDAbsolutePath(deviceSupplierId, deviceId);

		if (path == null) {

			// Try to get the SVD from the CMSIS Packs.
			IPacksDataManager dataManager = PacksDataManagerFactoryProxy.getInstance().createDataManager();

			if (dataManager == null) {
				throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID,
						"Peripherals descriptions are available only via the CMSIS Packs plug-in."));
			}

			path = dataManager.getSVDAbsolutePath(packType, deviceSupplierId, deviceId, config);

			if (path == null) {
				throw new CoreException(new Status(Status.ERROR, Activator.PLUGIN_ID,
						"There are no peripherals descriptions available, install the required xPacks or CMSIS Packs."));
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

		IConsoleStream out = Activator.getInstance().getConsoleOutput();
		try {

			File file = path.toFile();
			if (file == null) {
				out.println("Parsing SVD file \"" + path.toOSString() + "\"...");
				throw new IOException(path + " File object null.");
			}

			FileReader reader = null;
			reader = new FileReader(file);
			char[] chars = new char[10];
			reader.read(chars);
			reader.close();
			String str = "";
			if (chars[0] == '\ufeff') {
				str = (new String(Arrays.copyOfRange(chars, 1, chars.length - 1))).trim();
			} else {
				str = (new String(chars)).trim();
			}

			IPath actualPath = path;
			if (str.startsWith("PK")) {
				out.println("Decompressing zipped SVD file \"" + path.toOSString() + "\"...");

				// This is the signature of ZIP files.
				ZipInputStream zipInput;
				zipInput = new ZipInputStream(new FileInputStream(file));
				// Get the zipped file list entry
				ZipEntry zipEntry = zipInput.getNextEntry();
				while (zipEntry != null) {
					if (!zipEntry.isDirectory()) {
						String fileName = zipEntry.getName();

						File outFile = PacksStorage.getCachedFileObject(fileName);
						if (!outFile.getParentFile().exists()) {
							outFile.getParentFile().mkdirs();
						}
						out.println("Writing \"" + outFile + "\"...");

						OutputStream output = new FileOutputStream(outFile);

						byte[] buf = new byte[1024];
						int bytesRead;
						while ((bytesRead = zipInput.read(buf)) > 0) {
							output.write(buf, 0, bytesRead);
						}
						output.close();
						actualPath = new Path(outFile.getAbsolutePath());
						break;
					}
					zipEntry = zipInput.getNextEntry();
				}
				zipInput.closeEntry();
				zipInput.close();

				file = actualPath.toFile();
				reader = new FileReader(file);
				chars = new char[10];
				reader.read(chars);
				reader.close();

				str = " ";
				if (chars[0] == '\ufeff') {
					str = (new String(Arrays.copyOfRange(chars, 1, chars.length - 1))).trim();
				} else {
					str = (new String(chars)).trim();
				}
			}

			out.println("Parsing SVD file \"" + actualPath.toOSString() + "\"...");
			// If the file starts with the Unicode BOM (0xEF,0xBB,0xBF),
			// the first char must be skipped.

			if (str.startsWith("<?xml ")) {
				Document document = Xml.parseFile(file);

				XmlJsGenericParser jsParser = new SvdJsGenericParser();
				JsObject jsObject = jsParser.parse(document);
				System.out.println(jsObject);
				// FileOutputStream fo = new FileOutputStream(new File("/tmp/svd.json"));
				// jsObject.serialize(fo);
				// fo.close();

				SvdGenericParser parser = new SvdGenericParser();
				return parser.parse(document);
			} else if (str.startsWith("{")) {
				JSONParser parser = new JSONParser();
				reader = new FileReader(file);
				JSONObject json = (JSONObject) parser.parse(reader);
				JsonGenericParser jsonParser = new XsvdGenericParser();
				return jsonParser.parse(json);
			} else {
				String serr = "File format does not look like XML, JSON or ZIP";
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

		List<Leaf> list = new LinkedList<>();
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

	public static String resolveAll(String str, ILaunchConfiguration configuration) throws CoreException {
		String value = str;
		value = value.trim();
		if (value.isEmpty())
			return null;

		if (value.indexOf("${") >= 0) {
			// If more macros to process.
			value = DebugUtils.resolveAll(value, configuration.getAttributes());

			ICConfigurationDescription buildConfig = EclipseUtils.getBuildConfigDescription(configuration);
			if (buildConfig != null) {
				value = DebugUtils.resolveAll(value, buildConfig);
			}
		}
		if (Activator.getInstance().isDebugging()) {
			System.out.println("gdbjtag.resolveAll(\"" + str + "\") = \"" + value + "\"");
		}
		return value;
	}

	// ------------------------------------------------------------------------
}
