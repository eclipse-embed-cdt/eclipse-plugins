/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.core;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.core.settings.model.ICStorageElement;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;

@SuppressWarnings("restriction")
public class CProjectPacksStorage {

	// ------------------------------------------------------------------------

	public static final String STORAGE_NAME = "ilg.gnumcueclipse.managedbuild.packs";

	// If missing, assume CMSIS.
	public static final String PACK_TYPE = "pack.type";
	// Values are in ilg.gnumcueclipse.packs.core.PackTypes

	// ------------------------------------------------------------------------

	public static final String CMSIS_DEVICE_NAME = "cmsis.device.name";
	public static final String CMSIS_DEVICE_VENDOR_NAME = "cmsis.device.vendor.name";
	public static final String CMSIS_DEVICE_VENDOR_ID = "cmsis.device.vendor.id";
	public static final String CMSIS_FAMILY_NAME = "cmsis.family.name";
	public static final String CMSIS_SUBFAMILY_NAME = "cmsis.subfamily.name";
	public static final String CMSIS_CORE_NAME = "cmsis.core.name";
	public static final String CMSIS_COMPILER_DEFINE = "cmsis.compiler.define";

	public static final String CMSIS_DEVICE_PACK_VENDOR = "cmsis.device.pack.vendor";
	public static final String CMSIS_DEVICE_PACK_NAME = "cmsis.device.pack.name";
	public static final String CMSIS_DEVICE_PACK_VERSION = "cmsis.device.pack.version";

	public static final String CMSIS_BOARD_NAME = "cmsis.board.name";
	public static final String CMSIS_BOARD_REVISION = "cmsis.board.revision";
	public static final String CMSIS_BOARD_VENDOR_NAME = "cmsis.board.vendor.name";
	public static final String CMSIS_BOARD_CLOCK = "cmsis.board.clock";

	public static final String CMSIS_BOARD_PACK_VENDOR = "cmsis.board.pack.vendor";
	public static final String CMSIS_BOARD_PACK_NAME = "cmsis.board.pack.name";
	public static final String CMSIS_BOARD_PACK_VERSION = "cmsis.board.pack.version";

	// ------------------------------------------------------------------------

	public static final String DEVICE_KEY = "device.id";
	public static final String DEVICE_VENDOR_NAME = "device.vendor.name";
	public static final String DEVICE_VENDOR_ID = "device.vendor.id";
	public static final String DEVICE_COMPILER_DEFINES = "device.compiler.defines";
	public static final String DEVICE_COMPILER_HEADERS = "device.compiler.headers";
	public static final String FAMILY_KEY = "family.id";
	public static final String SUBFAMILY_KEY = "subfamily.key";
	public static final String CORE_NAME = "core.name";
	public static final String CORE_ARCH = "core.arch";

	public static final String DEVICE_PACK_NAME = "device.pack.name";
	public static final String DEVICE_PACK_VERSION = "device.pack.version";

	public static final String BOARD_KEY = "board.id";
	public static final String BOARD_REVISION = "board.revision";
	public static final String BOARD_VENDOR_NAME = "board.vendor.name";
	public static final String BOARD_VENDOR_ID = "board.vendor.id";
	public static final String BOARD_COMPILER_DEFINES = "board.compiler.defines";
	public static final String BOARD_COMPILER_HEADERS = "board.compiler.headers";
	public static final String BOARD_CLOCK = "board.clock";

	public static final String BOARD_PACK_NAME = "board.pack.name";
	public static final String BOARD_PACK_VERSION = "board.pack.version";

	// ------------------------------------------------------------------------

	private ICStorageElement fStorage;
	private Configuration fConfig;
	// private String fStorageId = null;
	private ICConfigurationDescription fConfigDesc;

	// ------------------------------------------------------------------------

	/**
	 * Create a new custom storage for packs related config options. The new storage
	 * is specific for each build configuration, so it is created inside the
	 * "cconfiguration" element.
	 * 
	 * @param config
	 *            a Configuration object (like Debug/Release).
	 * @throws CoreException
	 */
	public CProjectPacksStorage(IConfiguration config) throws CoreException {

		this(config, STORAGE_NAME);
	}

	public CProjectPacksStorage(IConfiguration config, String storageId) throws CoreException {

		// fStorageId = storageId;

		if (config instanceof Configuration) {
			fConfig = (Configuration) config;
			fConfigDesc = fConfig.getConfigurationDescription();

			// if (fConfigDesc instanceof CConfigurationDescriptionCache) {
			// CConfigurationSpecSettings ss = ((CConfigurationDescriptionCache)
			// fConfigDesc).getSpecSettings();
			// ICExternalSetting[] ics = ss.getExternalSettings();
			// System.out.println(ics.length);
			// // ss.setReadOnly(true, true);
			// }
			fStorage = fConfigDesc.getStorage(storageId, true);
			if (fStorage == null) {
				throw new CoreException(
						new Status(Status.ERROR, Activator.PLUGIN_ID, "Storage " + storageId + " not found."));
			}
			if (Activator.getInstance().isDebugging()) {
				System.out
						.println("CProjectPacksStorage() " + fStorage + " " + (fConfigDesc.isReadOnly() ? "ro" : "rw"));
			}
			// if (!fConfigDesc.isReadOnly()) {
			// fStorage = st; //.createCopy();
			// } else {
			// fStorage = st.createCopy();
			// }
			// System.out.println(fStorage.getName());

		} else {
			throw new CoreException(
					new Status(Status.ERROR, Activator.PLUGIN_ID, "CProjectPacksStorage() requires Configuration"));
		}
	}

	public void update() {
		;
	}

	/**
	 * Clear all definitions inside this storage.
	 */
	public void clear() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("CProjectPacksStorage.clear()");
		}
		fStorage.clear();
	}

	// ------------------------------------------------------------------------

	/**
	 * Retrieve a map with all properties.
	 * 
	 * @return a map of strings.
	 */
	public Map<String, String> getOptions() {

		assert (fStorage != null);

		Map<String, String> map = new HashMap<String, String>();

		for (ICStorageElement child : fStorage.getChildrenByName("option")) {

			if (child.hasAttribute("id")) {
				map.put(child.getAttribute("id"), child.getAttribute("value"));
			}
		}

		return map;
	}

	/**
	 * Retrieve the value of an option.
	 * 
	 * @param id
	 *            a string uniquely identifying the option
	 * @return its value or null, if not found
	 */
	public String getOption(String id) {

		assert (id != null);
		assert (fStorage != null);

		for (ICStorageElement child : fStorage.getChildrenByName("option")) {

			if (child.hasAttribute("id") && id.equals(child.getAttribute("id"))) {
				String value = child.getAttribute("value");
				if (Activator.getInstance().isDebugging()) {
					System.out.println("CProjectPacksStorage.getOption(" + id + ") = " + value);
				}
				return value;
			}
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("CProjectPacksStorage.getOption(" + id + ") = null");
		}
		return null;
	}

	public String getOption(String id, String defaultValue) {

		String result = getOption(id);
		if (result != null && result.length() > 0) {
			return result;
		}

		return defaultValue;
	}

	/**
	 * Store the value of an option.
	 * 
	 * @param id
	 *            a string uniquely identifying the option
	 * @param value
	 *            a string to be assigned as option value; if null, the empty string
	 *            is assigned
	 */
	public void setOption(String id, String value) {

		assert (id != null);
		assert (fStorage != null);

		ICStorageElement option = null;
		for (ICStorageElement child : fStorage.getChildrenByName("option")) {

			if (id.equals(child.getAttribute("id"))) {
				if (option == null) {
					// Remember first occurrence
					option = child;
				} else {
					// remove possible duplicates
					fStorage.removeChild(child);
				}
			}
		}

		if (option == null) {
			option = fStorage.createChild("option");
			option.setAttribute("id", id);
		}
		if (value == null) {
			value = "";
		} else {
			value = value.trim();
		}
		if (Activator.getInstance().isDebugging()) {
			System.out.println("CProjectPacksStorage.setOption(" + id + "," + value + ") ");
		}
		option.setAttribute("value", value);
	}

	/**
	 * Store the value of an option. If the value is null or empty, the operation is
	 * not performed..
	 * 
	 * @param id
	 *            a string uniquely identifying the option
	 * @param value
	 *            a string to be assigned as option value
	 */
	public void setNonEmptyOption(String id, String value) {

		if (value != null) {
			value = value.trim();
			if (value.length() > 0) {
				setOption(id, value);
			}
		}
	}

	/**
	 * Store the description of a memory section.
	 * 
	 * @param section
	 *            a string with the section name, using the CMSIS convention (like
	 *            IRAM1, IROM1)
	 * @param start
	 *            a string with the hex value of the start address
	 * @param size
	 *            a string with the hex value of the size, in bytes
	 * @param startup
	 *            a string with 1 if the section will be used for startup (to host
	 *            the vectors table)
	 */
	public void setMemory(String section, String start, String size, String startup) {

		ICStorageElement memory = null;
		for (ICStorageElement child : fStorage.getChildrenByName("memory")) {

			if (section.equals(child.getAttribute("section"))) {
				if (memory == null) {
					// Remember first occurrence
					memory = child;
				} else {
					// remove possible duplicates
					fStorage.removeChild(child);
				}
			}
		}

		if (memory == null) {
			memory = fStorage.createChild("memory");
			memory.setAttribute("section", section);
		}
		memory.setAttribute("start", start);
		memory.setAttribute("size", size);
		memory.setAttribute("startup", startup);
	}

	public Map<String, String[]> getMemoryMap() {

		Map<String, String[]> map = new TreeMap<String, String[]>();

		ICStorageElement[] memory = fStorage.getChildrenByName("memory");
		if (memory != null) {
			for (ICStorageElement child : memory) {

				String section = child.getAttribute("section");
				String arr[] = new String[] { section, child.getAttribute("start"), child.getAttribute("size"),
						child.getAttribute("startup") };
				map.put(section, arr);
			}
		}

		return map;
	}

	// ------------------------------------------------------------------------
}
