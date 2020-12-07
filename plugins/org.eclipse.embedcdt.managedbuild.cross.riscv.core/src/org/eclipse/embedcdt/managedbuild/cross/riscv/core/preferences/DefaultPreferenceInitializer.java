/*******************************************************************************
 * Copyright (c) 2015 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.managedbuild.cross.riscv.core.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.INodeChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.NodeChangeEvent;
import org.eclipse.embedcdt.internal.managedbuild.cross.riscv.core.Activator;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.managedbuild.cross.riscv.core.ToolchainDefinition;
import org.osgi.service.prefs.Preferences;

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
			System.out.println("riscv.DefaultPreferenceInitializer.initializeDefaultPreferences()");
		}

		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
		fPersistentPreferences = Activator.getInstance().getPersistentPreferences();

		// Default toolchain name
		String toolchainName = ToolchainDefinition.DEFAULT_TOOLCHAIN_NAME;
		fDefaultPreferences.putToolchainName(toolchainName);

		// When the 'org.eclipse.embedcdt.managedbuild.cross.riscv' node is
		// completely
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
				System.out.println("riscv.LateInitializer.added() " + event + " " + event.getChild().name());
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
				System.out.println("riscv.LateInitializer.removed() " + event);
			}
		}

		/**
		 * The second step of defaults initialisation.
		 */
		public void finalizeInitializationsDefaultPreferences() {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("riscv.LateInitializer.finalizeInitializationsDefaultPreferences()");
			}

			// Toolchains paths
			for (ToolchainDefinition toolchain : ToolchainDefinition.getList()) {

				String toolchainName = toolchain.getName();

				// If the search path is known, discover toolchain.
				int ix;
				try {
					ix = ToolchainDefinition.findToolchainByName(toolchainName);
				} catch (IndexOutOfBoundsException e) {
					ix = ToolchainDefinition.getDefault();
				}

				String executableName = ToolchainDefinition.getToolchain(ix).getFullCmdC();

				// Try the defaults.
				String path = fDefaultPreferences.getToolchainPath(toolchainName);

				if (!fDefaultPreferences.checkFolderExecutable(path, executableName)) {
					// Try the persistent preferences.
					path = fPersistentPreferences.getToolchainPath(toolchainName, null);
				}

				if (!fDefaultPreferences.checkFolderExecutable(path, executableName)) {
					// If not defined elsewhere, discover.
					path = fDefaultPreferences.discoverToolchainPath(toolchainName, executableName);
				}

				if (path != null && !path.isEmpty()) {
					// If the toolchain path was finally discovered, store
					// it in the default preferences.
					fDefaultPreferences.putToolchainPath(toolchainName, path);
				}

				if (Activator.getInstance().isDebugging()) {
					System.out.println("riscv.LateInitializer.finalizeInitializationsDefaultPreferences() done");
				}
			}
		}
	}

	// ------------------------------------------------------------------------
}
