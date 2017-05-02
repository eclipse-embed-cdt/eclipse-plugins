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

package ilg.gnuarmeclipse.managedbuild.cross;

import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvider;
import org.eclipse.cdt.core.language.settings.providers.LanguageSettingsManager;

public class SpecsProvider {

	// ------------------------------------------------------------------------

	private static String PROVIDER_ID = IDs.getIdPrefix() + ".GCCBuiltinSpecsDetector";

	// ------------------------------------------------------------------------

	// private static String PROVIDER_ID =
	// "org.eclipse.cdt.managedbuilder.core.GCCBuiltinSpecsDetector";

	public static ILanguageSettingsProvider getProvider() {
		ILanguageSettingsProvider p = LanguageSettingsManager.getWorkspaceProvider(PROVIDER_ID);
		return LanguageSettingsManager.getRawProvider(p);// ;
	}

	// private static void clear() {
	// ILanguageSettingsProvider provider = getProvider();
	// ((AbstractBuiltinSpecsDetector) provider).clear();
	// // System.out.println("clear " + provider.getName());
	// }

	// ------------------------------------------------------------------------
}
