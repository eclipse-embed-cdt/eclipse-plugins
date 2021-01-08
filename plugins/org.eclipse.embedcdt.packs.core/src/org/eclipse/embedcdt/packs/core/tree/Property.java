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
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.core.tree;

/**
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class Property {

	// Special properties for name & description.
	public static final String NAME_ = "$NAME";
	public static final String DESCRIPTION_ = "$DESC";

	// Special property for JSON keys used to identify collection members.
	public static final String KEY_ = "$KEY";

	// Special property for JSON array elements.
	public static final String VALUE_ = "$VALUE";

	// Node properties (sorted)
	public static final String ACCESS = "access";
	public static final String ADDRESS = "address";
	public static final String ALIAS = "alias";
	public static final String ARCH = "arch";
	public static final String ARCHIVE_NAME = "archive.name";
	public static final String ARCHIVE_SIZE = "archive.size";
	public static final String ARCHIVE_URL = "archive.url";
	// public static final String BOARD_NAME = "board.name";
	public static final String BOARD_REVISION = "board.revision";
	public static final String CATEGORY = "category";
	/**
	 * @since 3.1
	 */
	public static final String CHIP_PACKAGE = "chip.package";
	/**
	 * @since 3.1
	 */
	public static final String CHIP_PINS = "chip.pins";
	public static final String CLOCK = "clock";
	public static final String COMPILER_HEADERS = "compilerHeaders";
	public static final String COMPILER_DEFINES = "compilerDefines";
	public static final String CORE = "core";
	public static final String CORE_VERSION = "core.version";
	public static final String DATE = "date";
	// public static final String DEFAULT = "default";
	public static final String DEFINE = "define";
	public static final String DEST_FOLDER = "dest.folder";
	public static final String DISPLAY_NAME = "displayName";
	public static final String ENABLED = "enabled";
	public static final String ENDIAN = "endian";
	public static final String EXAMPLE_NAME = "example.name";
	public static final String FILE = "file";
	public static final String FILE_ABSOLUTE = "file.absolute";
	public static final String FPU = "fpu";
	public static final String GENERATOR = "generator";
	public static final String HFOSC = "hfosc";
	public static final String HFXTAL = "hfxtal";
	public static final String ID = "id";
	// public static final String INIT = "init";
	public static final String INSTALLED = "installed";
	public static final String LFOSC = "lfosc";
	public static final String LFXTAL = "lfxtal";
	public static final String MPU = "mpu";
	public static final String NAME = "name";
	public static final String ON_CHIP = "onChip";
	public static final String PACK_TYPE = "pack.type";
	public static final String PACK_NAME = "pack.name";
	public static final String PACK_VENDOR = "pack.vendor";
	public static final String PACK_VERSION = "pack.version";
	public static final String PDSC_NAME = "pdsc.name";
	public static final String PNAME = "pname";
	public static final String REPO_URL = "repo.url";
	public static final String REVISION = "revision";
	// public static final String SITE_URL = "site.url";
	public static final String SCHEMA_VERSION = "schema.version";
	public static final String SIZE = "size";
	public static final String START = "start";
	public static final String STARTUP = "startup";
	public static final String SVD_FILE = "svd.file";
	public static final String TYPE = "type";
	public static final String URL = "url";
	// public static final String UTC_DATE = "utc.date";
	public static final String VENDOR_NAME = "vendor.name";
	public static final String VENDOR_ID = "vendor.id";
	public static final String VERSION_NAME = "version.name";
	public static final String XML_CONTENT = "xml.content";
	public static final String XSVD = "xsvd";
	public static final String XSVD_FILE = "xsvd.file";
	// public static final String XML_PREFIX = "xml.";
}