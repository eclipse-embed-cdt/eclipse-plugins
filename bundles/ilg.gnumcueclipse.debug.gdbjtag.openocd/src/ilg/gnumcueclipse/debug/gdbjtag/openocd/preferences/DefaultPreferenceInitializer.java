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

package ilg.gnumcueclipse.debug.gdbjtag.openocd.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.INodeChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.NodeChangeEvent;
import org.osgi.service.prefs.Preferences;

import ilg.gnumcueclipse.debug.gdbjtag.openocd.Activator;

/**
 * Initialisations are executed in two different moments: as the first step
 * during bundle inits and after all defaults are loaded from all possible
 * sources
 * 
 */
public class DefaultPreferenceInitializer extends AbstractPreferenceInitializer {

	// ------------------------------------------------------------------------

	DefaultPreferences fDefaultPreferences;
	PersistentPreferences fPersistentPreferences;

	// ------------------------------------------------------------------------

	/**
	 * Early inits. Preferences set here might be overridden by plug-in
	 * preferences.ini, product .ini or command line option.
	 */
	@Override
	public void initializeDefaultPreferences() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("openocd.DefaultPreferenceInitializer.initializeDefaultPreferences()");
		}

		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
		fPersistentPreferences = Activator.getInstance().getPersistentPreferences();

		fDefaultPreferences.putBoolean(PersistentPreferences.GDB_SERVER_DO_START,
				DefaultPreferences.DO_START_GDB_SERVER_DEFAULT);

		fDefaultPreferences.putString(PersistentPreferences.GDB_CLIENT_COMMANDS,
				DefaultPreferences.GDB_CLIENT_OTHER_COMMANDS_DEFAULT);

		fDefaultPreferences.putBoolean(PersistentPreferences.TAB_MAIN_CHECK_PROGRAM,
				DefaultPreferences.TAB_MAIN_CHECK_PROGRAM_DEFAULT);

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
				System.out.println("openocd.LateInitializer.added() " + event + " " + event.getChild().name());
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
				System.out.println("openocd.LateInitializer.removed() " + event);
			}
		}

		/**
		 * The second step of defaults initialisation.
		 */
		public void finalizeInitializationsDefaultPreferences() {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("openocd.LateInitializer.finalizeInitializationsDefaultPreferences()");
			}

			// ----------------------------------------------------------------

			// Try the default executable name.
			String executableName = fDefaultPreferences.getExecutableName();
			if (executableName.isEmpty()) {
				// Try the platform specific name.
				executableName = fDefaultPreferences.getExecutableNameOs();
			}

			if (executableName.isEmpty()) {
				// Try the persistent preferences.
				executableName = fPersistentPreferences.getExecutableName();
			}

			if (!executableName.isEmpty()) {
				// Save the result back as default.
				fDefaultPreferences.putExecutableName(executableName);
			}

			// ----------------------------------------------------------------

			// Try the defaults.
			String path = fDefaultPreferences.getInstallFolder();
			if (!fDefaultPreferences.checkFolderExecutable(path, executableName)) {
				// Try the persistent preferences.
				path = fPersistentPreferences.getInstallFolder();
			}

			if (!fDefaultPreferences.checkFolderExecutable(path, executableName)) {
				// If not defined elsewhere, discover.
				path = fDefaultPreferences.discoverInstallPath(null, executableName);
			}

			if (path != null && !path.isEmpty()) {
				// If the path was finally discovered, store
				// it in the default preferences.
				fDefaultPreferences.putInstallFolder(path);
			}

			if (Activator.getInstance().isDebugging()) {
				System.out.println("openocd.LateInitializer.finalizeInitializationsDefaultPreferences() done");
			}
		}
	}

	// ------------------------------------------------------------------------
}
