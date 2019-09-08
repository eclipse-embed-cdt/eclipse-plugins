/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.preferences;

public class PersistentPreferences extends ilg.gnumcueclipse.core.preferences.PersistentPreferences {

	// ------------------------------------------------------------------------
	// EXECUTABLE_NAME, INSTALL_FOLDER, FOLDER_STRICT are used as dynamic
	// variables.
	public static final String EXECUTABLE_NAME = "executable.name";
	public static final String EXECUTABLE_NAME_DEFAULT = "";

	public static final String INSTALL_FOLDER = "install.folder";
	public static final String INSTALL_FOLDER_DEFAULT = "";

	public static final String FOLDER_STRICT = "folder.strict";
	public static final boolean FOLDER_STRICT_DEFAULT = true;

	public static final String SEARCH_PATH = "search.path";
	public static final String SEARCH_PATH_DEFAULT = "";

	public static final String XPACK_NAMES = "xpack.names";

	public static final String EXECUTABLE_NAME_OS = EXECUTABLE_NAME + ".%s";
	public static final String SEARCH_PATH_OS = SEARCH_PATH + ".%s";

	public static final String TAB_MAIN_CHECK_PROGRAM = "tab.main.checkProgram";
	public static final boolean TAB_MAIN_CHECK_PROGRAM_DEFAULT = false;

	// ------------------------------------------------------------------------

	public static final String PERIPHERALS_COLOR_READONLY = "peripherals.color.readonly";
	public static final String PERIPHERALS_COLOR_WRITEONLY = "peripherals.color.writeonly";
	public static final String PERIPHERALS_COLOR_CHANGED = "peripherals.color.changed";
	public static final String PERIPHERALS_COLOR_CHANGED_MEDIUM = "peripherals.color.changed.medium";
	public static final String PERIPHERALS_COLOR_CHANGED_LIGHT = "peripherals.color.changed.light";

	public static final String PERIPHERALS_CHANGED_USE_FADING_BACKGROUND = "peripherals.changed.useFadingBackground";
	public static final boolean PERIPHERALS_CHANGED_USE_FADING_BACKGROUND_DEFAULT = true;

	// ------------------------------------------------------------------------

	public PersistentPreferences(String pluginId) {
		super(pluginId);
	}

	// ----- Install folder -------------------------------------------
	public String getInstallFolder() {

		return getString(INSTALL_FOLDER, INSTALL_FOLDER_DEFAULT);
	}

	// ----- Executable name ------------------------------------------
	public String getExecutableName() {

		return getString(EXECUTABLE_NAME, EXECUTABLE_NAME_DEFAULT);
	}

	// ----- Is strict ------------------------------------------------
	public boolean getFolderStrict() {

		return getBoolean(FOLDER_STRICT, FOLDER_STRICT_DEFAULT);
	}

	// ------------------------------------------------------------------------

	public boolean getPeripheralsChangedUseFadingBackground() {
		return getBoolean(PERIPHERALS_CHANGED_USE_FADING_BACKGROUND, PERIPHERALS_CHANGED_USE_FADING_BACKGROUND_DEFAULT);
	}

	// ------------------------------------------------------------------------
}
