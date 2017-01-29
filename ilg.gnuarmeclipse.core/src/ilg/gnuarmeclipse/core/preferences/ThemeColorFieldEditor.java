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

package ilg.gnuarmeclipse.core.preferences;

import ilg.gnuarmeclipse.core.Activator;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;
import org.osgi.service.prefs.BackingStoreException;

public class ThemeColorFieldEditor extends ColorFieldEditor {

	// ------------------------------------------------------------------------

	private String fThemeColorName;

	private ColorRegistry fColorRegistry;

	// ------------------------------------------------------------------------

	/**
	 * Creates a custom colour field editor, using data from the theme colour
	 * registry.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on.
	 * @param themeColorName
	 *            the name of the theme colour.
	 * @param labelText
	 *            the label text of the field editor.
	 * @param parent
	 *            the parent of the field editor's control.
	 */
	public ThemeColorFieldEditor(String name, String themeColorName, String labelText, Composite parent) {
		super(name, labelText, parent);

		fThemeColorName = themeColorName;

		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		ITheme theme = themeManager.getCurrentTheme();
		fColorRegistry = theme.getColorRegistry();

	}

	// ------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	@Override
	protected void doLoad() {

		if (getColorSelector() == null) {
			return;
		}

		RGB rgb = fColorRegistry.getRGB(fThemeColorName);
		getColorSelector().setColorValue(rgb);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	protected void doLoadDefault() {

		if (getColorSelector() == null) {
			return;
		}
		RGB rgb = PreferenceConverter.getDefaultColor(getPreferenceStore(), getPreferenceName());
		getColorSelector().setColorValue(rgb);

		setPresentsDefaultValue(false);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	protected void doStore() {

		RGB rgb = getColorSelector().getColorValue();

		fColorRegistry.put(fThemeColorName, rgb);

		String value = String.format("%d,%d,%d", rgb.red, rgb.green, rgb.blue);

		IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode("org.eclipse.ui.workbench");

		if (!value.equals(preferences.get(fThemeColorName, ""))) {

			if (Activator.getInstance().isDebugging()) {
				System.out.println("Color " + fThemeColorName + "=" + value);
			}

			preferences.put(fThemeColorName, value);
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				Activator.log(e);
			}
		}
	}

	// ------------------------------------------------------------------------
}
