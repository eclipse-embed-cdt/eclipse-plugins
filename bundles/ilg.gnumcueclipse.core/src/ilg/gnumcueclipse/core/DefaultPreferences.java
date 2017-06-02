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

package ilg.gnumcueclipse.core;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class DefaultPreferences {

	// ------------------------------------------------------------------------

	protected String fPluginId;

	/**
	 * The DefaultScope preference store.
	 */
	protected IEclipsePreferences fPreferences;

	// ------------------------------------------------------------------------

	public DefaultPreferences(String pluginId) {
		fPluginId = pluginId;
		fPreferences = DefaultScope.INSTANCE.getNode(fPluginId);
	}

	// ------------------------------------------------------------------------

	/**
	 * Get a string preference value, or the default.
	 * 
	 * @param key
	 *            a string with the key to search.
	 * @param defaultValue
	 *            a string with the default, possibly null.
	 * @return a trimmed string, or a null default.
	 */
	public String getString(String key, String defaultValue) {

		String value;
		value = fPreferences.get(key, defaultValue);

		if (value != null) {
			value = value.trim();
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getString(\"" + key + "\", \"" + defaultValue + "\") "
					+ fPreferences.name() + " = \"" + value + "\"");
		}

		return value;
	}

	public boolean getBoolean(String key, boolean defaultValue) {

		boolean value = fPreferences.getBoolean(key, defaultValue);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getString(\"" + key + "\", \"" + defaultValue + "\") "
					+ fPreferences.name() + " = " + value);
		}
		return value;
	}

	public int getInt(String key, int defaultValue) {

		int value = fPreferences.getInt(key, defaultValue);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.getBoolean(\"" + key + "\", " + defaultValue + ") = " + value);
		}
		return value;
	}

	public void putString(String key, String value) {
		if (Activator.getInstance().isDebugging()) {
			System.out
					.println("DefaultPreferences.putString(\"" + key + "\", \"" + value + "\") " + fPreferences.name());
		}
		fPreferences.put(key, value);
	}

	public void putInt(String key, int value) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putInt(\"" + key + "\", " + value + ")");
		}

		fPreferences.putInt(key, value);
	}

	public void putBoolean(String key, boolean value) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("DefaultPreferences.putBoolean(\"" + key + "\", " + value + ")");
		}

		fPreferences.putBoolean(key, value);
	}

	// ------------------------------------------------------------------------
}
