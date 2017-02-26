/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 * Copyright (c) 2015-2016 Chris Reed.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Liviu Ionescu - initial version
 *     Chris Reed - pyOCD changes
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.pyocd;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.core.preferences.Discoverer;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.INodeChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.NodeChangeEvent;
import org.osgi.service.prefs.Preferences;

/**
 * Initialisations are executed in two different moments: as the first step
 * during bundle inits and after all defaults are loaded from all possible
 * sources
 *
 */
public class DefaultPreferenceInitializer extends AbstractPreferenceInitializer {

	// ------------------------------------------------------------------------

	// HKCU & HKLM LOCAL_MACHINE
	private static final String REG_SUBKEY = "\\GNU ARM Eclipse\\PyOCD";
	// Standard Microsoft recommendation.
	private static final String REG_NAME = "InstallLocation";
	// Custom name, used before reading the standard.
	private static final String REG_NAME_DEPRECATED = "InstallFolder";

	// ------------------------------------------------------------------------

	/**
	 * Early inits. Preferences set here might be overridden by plug-in
	 * preferences.ini, product .ini or command line option.
	 */
	@Override
	public void initializeDefaultPreferences() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferenceInitializer.initializeDefaultPreferences()");
		}

		DefaultPreferences.putBoolean(PersistentPreferences.GDB_SERVER_DO_START,
				DefaultPreferences.DO_START_GDB_SERVER_DEFAULT);

		DefaultPreferences.putString(PersistentPreferences.GDB_CLIENT_COMMANDS,
				DefaultPreferences.GDB_CLIENT_OTHER_COMMANDS_DEFAULT);

		DefaultPreferences.putBoolean(PersistentPreferences.TAB_MAIN_CHECK_PROGRAM,
				DefaultPreferences.TAB_MAIN_CHECK_PROGRAM_DEFAULT);

		// When the 'ilg.gnuarmeclipse.managedbuild.cross' node is completely
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
				System.out.println("LateInitializer.added() " + event + " " + event.getChild().name());
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
				System.out.println("LateInitializer.removed() " + event);
			}
		}

		/**
		 * The second step of defaults initialisation.
		 */
		public void finalizeInitializationsDefaultPreferences() {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("LateInitializer.finalizeInitializationsDefaultPreferences()");
			}

			// pyOCD executable name
			String name = DefaultPreferences.getExecutableName();
			if (name.isEmpty()) {
				// If not defined elsewhere, get platform specific name.
				name = DefaultPreferences.getExecutableNameOs();
				if (!name.isEmpty()) {
					DefaultPreferences.putExecutableName(name);
				}
			}

			String executableName = EclipseUtils.getVariableValue(VariableInitializer.VARIABLE_PYOCD_EXECUTABLE);
			if (executableName == null || executableName.isEmpty()) {
				executableName = DefaultPreferences.getExecutableName();
			}
			if (EclipseUtils.isWindows() && !executableName.endsWith(".exe")) {
				executableName += ".exe";
			}

			// Check if the search path is defined in the default
			// preferences.
			String searchPath = DefaultPreferences.getSearchPath();
			if (searchPath.isEmpty()) {

				// If not defined, get the OS Specific default
				// from preferences.ini.
				searchPath = DefaultPreferences.getSearchPathOs();

				if (!searchPath.isEmpty()) {
					// Store the search path in the preferences
					DefaultPreferences.putSearchPath(searchPath);
				}
			}

			// pyOCD install folder
			// Check if the toolchain path is explictly defined in the
			// default preferences.
			String folder = DefaultPreferences.getInstallFolder();
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

					// Search the non standard key too.
					if (folder == null) {
						folder = Discoverer.getRegistryInstallFolder(executableName, "bin", REG_SUBKEY,
								REG_NAME_DEPRECATED);
					}
				}

				if (folder == null || folder.isEmpty()) {
					folder = Discoverer.searchInstallFolder(executableName, searchPath, "bin");
				}
			}

			if (folder != null && !folder.isEmpty()) {
				// If the install folder was finally discovered, store
				// it in the preferences.
				DefaultPreferences.putInstallFolder(folder);
			}
		}
	}

	// ------------------------------------------------------------------------
}
