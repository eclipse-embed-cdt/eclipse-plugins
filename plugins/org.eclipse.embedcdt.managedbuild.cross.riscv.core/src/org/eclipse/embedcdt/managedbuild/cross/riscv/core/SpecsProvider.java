/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
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
 *******************************************************************************/

package org.eclipse.embedcdt.managedbuild.cross.riscv.core;

import org.eclipse.cdt.core.language.settings.providers.ILanguageSettingsProvider;
import org.eclipse.cdt.core.language.settings.providers.LanguageSettingsManager;

public class SpecsProvider {

	// ------------------------------------------------------------------------

	private static String PROVIDER_ID = "org.eclipse.embedcdt.managedbuild.cross.riscv.core.GCCBuiltinSpecsDetector";

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
