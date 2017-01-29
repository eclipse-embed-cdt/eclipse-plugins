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

package ilg.gnuarmeclipse.debug.gdbjtag;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

/**
 * Manage a workspace preference file stored in:
 * 
 * <pre>
 * workspace/.metadata/.plugins/org.eclipse.core.runtime/.settings/<plug-in-id>.prefs
 * </pre>
 *
 * Some of the values may be retrieved from the EclipseDefaults.
 */

public class PersistentPreferences {

	// ------------------------------------------------------------------------

	public static final String PERIPHERALS_COLOR_READONLY = "peripherals.color.readonly";
	public static final String PERIPHERALS_COLOR_WRITEONLY = "peripherals.color.writeonly";
	public static final String PERIPHERALS_COLOR_CHANGED = "peripherals.color.changed";
	public static final String PERIPHERALS_COLOR_CHANGED_MEDIUM = "peripherals.color.changed.medium";
	public static final String PERIPHERALS_COLOR_CHANGED_LIGHT = "peripherals.color.changed.light";

	public static final String PERIPHERALS_CHANGED_USE_FADING_BACKGROUND = "peripherals.changed.useFadingBackground";
	public static final boolean PERIPHERALS_CHANGED_USE_FADING_BACKGROUND_DEFAULT = true;

	// ----- Getters ----------------------------------------------------------

	public static String getString(String key, String defaultValue) {

		String value;
		value = Platform.getPreferencesService().getString(Activator.PLUGIN_ID, key, null, null);
		// System.out.println("Value of " + id + " is " + value);

		if (value != null) {
			return value;
		}

		return defaultValue;
	}

	public static boolean getBoolean(String key, boolean defaultValue) {

		boolean value;
		value = Platform.getPreferencesService().getBoolean(Activator.PLUGIN_ID, key, defaultValue, null);
		// System.out.println("Value of " + id + " is " + value);
		return value;
	}

	// ----- Setters ----------------------------------------------------------

	public static void putWorkspaceString(String key, String value) {

		value = value.trim();

		// Access the instanceScope
		Preferences preferences = InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID);
		preferences.put(key, value);
	}

	public static void flush() {

		try {
			InstanceScope.INSTANCE.getNode(Activator.PLUGIN_ID).flush();
		} catch (BackingStoreException e) {
			Activator.log(e);
		}
	}

	// ------------------------------------------------------------------------

	public static boolean getPeripheralsChangedUseFadingBackground() {
		return getBoolean(PERIPHERALS_CHANGED_USE_FADING_BACKGROUND, PERIPHERALS_CHANGED_USE_FADING_BACKGROUND_DEFAULT);
	}

	// ------------------------------------------------------------------------
}
