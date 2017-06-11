/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.debug.gdbjtag.jlink.preferences;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.INodeChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.NodeChangeEvent;
import org.osgi.service.prefs.Preferences;

import ilg.gnumcueclipse.core.EclipseUtils;
import ilg.gnumcueclipse.core.preferences.Discoverer;
import ilg.gnumcueclipse.debug.gdbjtag.jlink.Activator;

/**
 * Initialisations are executed in two different moments: as the first step
 * during bundle inits and after all defaults are loaded from all possible
 * sources
 * 
 */
public class DefaultPreferenceInitializer extends AbstractPreferenceInitializer {

	// ------------------------------------------------------------------------

	// Current SEGGER versions use HKEY_CURRENT_USER
	private static final String REG_SUBKEY = "\\SEGGER\\J-Link";
	private static final String REG_NAME = "InstallPath";

	// ------------------------------------------------------------------------

	/**
	 * Early inits. Preferences set here might be overridden by plug-in
	 * preferences.ini, product .ini or command line option.
	 */
	@Override
	public void initializeDefaultPreferences() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("jlink.DefaultPreferenceInitializer.initializeDefaultPreferences()");
		}

		DefaultPreferences fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
		fDefaultPreferences.putString(PersistentPreferences.GDB_SERVER_INTERFACE,
				DefaultPreferences.SERVER_INTERFACE_DEFAULT);

		fDefaultPreferences.putBoolean(PersistentPreferences.GDB_JLINK_ENABLE_SEMIHOSTING,
				DefaultPreferences.ENABLE_SEMIHOSTING_DEFAULT);

		fDefaultPreferences.putBoolean(PersistentPreferences.GDB_JLINK_ENABLE_SWO,
				DefaultPreferences.ENABLE_SWO_DEFAULT);

		fDefaultPreferences.putBoolean(PersistentPreferences.TAB_MAIN_CHECK_PROGRAM,
				DefaultPreferences.TAB_MAIN_CHECK_PROGRAM_DEFAULT);

		fDefaultPreferences.putBoolean(PersistentPreferences.GDB_SERVER_DO_START,
				DefaultPreferences.SERVER_DO_START_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_SERVER_ENDIANNESS,
				DefaultPreferences.SERVER_ENDIANNESS_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_SERVER_CONNECTION,
				DefaultPreferences.SERVER_CONNECTION_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_SERVER_CONNECTION_ADDRESS,
				DefaultPreferences.SERVER_CONNECTION_ADDRESS_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_SERVER_INITIAL_SPEED,
				DefaultPreferences.SERVER_INITIAL_SPEED_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_SERVER_OTHER_OPTIONS,
				DefaultPreferences.SERVER_OTHER_OPTIONS_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_CLIENT_COMMANDS,
				DefaultPreferences.CLIENT_COMMANDS_DEFAULT);

		fDefaultPreferences.putBoolean(PersistentPreferences.GDB_JLINK_DO_INITIAL_RESET,
				DefaultPreferences.DO_INITIAL_RESET_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_JLINK_INITIAL_RESET_TYPE,
				DefaultPreferences.INITIAL_RESET_TYPE_DEFAULT);

		fDefaultPreferences.putInt(PersistentPreferences.GDB_JLINK_INITIAL_RESET_SPEED,
				DefaultPreferences.INITIAL_RESET_SPEED_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_JLINK_SPEED, DefaultPreferences.JLINK_SPEED_DEFAULT);

		fDefaultPreferences.putBoolean(PersistentPreferences.GDB_JLINK_ENABLE_FLASH_BREAKPOINTS,
				DefaultPreferences.ENABLE_FLASH_BREAKPOINTS_DEFAULT);

		fDefaultPreferences.putBoolean(PersistentPreferences.GDB_JLINK_SEMIHOSTING_TELNET,
				DefaultPreferences.SEMIHOSTING_TELNET_DEFAULT);

		fDefaultPreferences.putBoolean(PersistentPreferences.GDB_JLINK_SEMIHOSTING_CLIENT,
				DefaultPreferences.SEMIHOSTING_CLIENT_DEFAULT);

		fDefaultPreferences.putInt(PersistentPreferences.GDB_JLINK_SWO_ENABLE_TARGET_CPU_FREQ,
				DefaultPreferences.SWO_ENABLE_TARGET_CPU_FREQ_DEFAULT);

		fDefaultPreferences.putInt(PersistentPreferences.GDB_JLINK_SWO_ENABLE_TARGET_SWO_FREQ,
				DefaultPreferences.SWO_ENABLE_TARGET_SWO_FREQ_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_JLINK_SWO_ENABLE_TARGET_PORT_MASK,
				DefaultPreferences.SWO_ENABLE_TARGET_PORT_MASK_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_JLINK_INIT_OTHER,
				DefaultPreferences.INIT_OTHER_DEFAULT);

		fDefaultPreferences.putBoolean(PersistentPreferences.GDB_JLINK_DO_DEBUG_IN_RAM,
				DefaultPreferences.DO_DEBUG_IN_RAM_DEFAULT);

		fDefaultPreferences.putBoolean(PersistentPreferences.GDB_JLINK_DO_PRERUN_RESET,
				DefaultPreferences.DO_PRERUN_RESET_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_JLINK_PRERUN_RESET_TYPE,
				DefaultPreferences.PRERUN_RESET_TYPE_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_JLINK_PRERUN_OTHER,
				DefaultPreferences.PRERUN_OTHER_DEFAULT);

		// When the 'ilg.gnumcueclipse.managedbuild.cross' node is completely
		// added to /default, a NodeChangeEvent is raised.
		// This is the moment when all final default values are in, possibly
		// set by product or command line.

		Preferences prefs = Platform.getPreferencesService().getRootNode().node(DefaultScope.SCOPE);
		if (prefs instanceof IEclipsePreferences) {
			((IEclipsePreferences) prefs).addNodeChangeListener(new LateInitializer());
		}
	}

	/**
	 * INodeChangeListener for late initialisations.
	 */
	private class LateInitializer implements INodeChangeListener {

		@Override
		public void added(NodeChangeEvent event) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("jlink.LateInitializer.added() " + event + " " + event.getChild().name());
			}

			if (Activator.PLUGIN_ID.equals(event.getChild().name())) {

				finalizeInitializationsDefaultPreferences();

				// We're done, de-register listener.
				((IEclipsePreferences) (event.getSource())).removeNodeChangeListener(this);
			}
		}

		@Override
		public void removed(NodeChangeEvent event) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("jlink.LateInitializer.removed() " + event);
			}
		}

		/**
		 * The second step of defaults initialisation.
		 */
		public void finalizeInitializationsDefaultPreferences() {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("jlink.LateInitializer.finalizeInitializationsDefaultPreferences()");
			}

			DefaultPreferences fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
			// J-Link GDB Server executable name
			String name = fDefaultPreferences.getExecutableName();
			if (name.isEmpty()) {
				// If not defined elsewhere, get platform specific name.
				name = fDefaultPreferences.getExecutableNameOs();
				if (!name.isEmpty()) {
					fDefaultPreferences.putExecutableName(name);
				}
			}

			PersistentPreferences fPersistentPreferences = Activator.getInstance().getPersistentPreferences();

			String executableName = fPersistentPreferences.getExecutableName();
			if (executableName == null || executableName.isEmpty()) {
				executableName = fDefaultPreferences.getExecutableName();
			}
			if (EclipseUtils.isWindows() && !executableName.endsWith(".exe")) {
				executableName += ".exe";
			}

			// Check if the search path is defined in the default
			// preferences.
			String searchPath = fDefaultPreferences.getSearchPath();
			if (searchPath.isEmpty()) {

				// If not defined, get the OS Specific default
				// from preferences.ini.
				searchPath = fDefaultPreferences.getSearchPathOs();
				if (!searchPath.isEmpty()) {
					// Store the search path in the preferences
					fDefaultPreferences.putSearchPath(searchPath);
				}
			}

			// J-Link GDB Server install folder
			// Check if the toolchain path is explictly defined in the
			// default preferences.
			String folder = fDefaultPreferences.getInstallFolder();
			if (!folder.isEmpty()) {
				IPath path = (new Path(folder)).append(executableName);
				if (!path.toFile().isFile()) {
					// If the file does not exist, refuse the given folder
					// and prefer to search.
					folder = "";
				}
			}

			if (folder.isEmpty()) {

				if (EclipseUtils.isWindows()) {
					// If the search path is known, discover toolchain.
					folder = Discoverer.getRegistryInstallFolder(executableName, "bin", REG_SUBKEY, REG_NAME);
				}

				if (folder == null || folder.isEmpty()) {
					folder = Discoverer.searchInstallFolder(executableName, searchPath, null);
				}
			}

			if (folder != null && !folder.isEmpty()) {
				// If the install folder was finally discovered, store
				// it in the preferences.
				fDefaultPreferences.putInstallFolder(folder);
			}
		}
	}

	// ------------------------------------------------------------------------
}
