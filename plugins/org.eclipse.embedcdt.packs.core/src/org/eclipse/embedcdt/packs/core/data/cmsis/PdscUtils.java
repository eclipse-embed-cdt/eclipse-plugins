/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.core.data.cmsis;

import org.eclipse.embedcdt.core.zafarkhaja.semver.Version;
import org.json.simple.JSONObject;

/**
 * Static class used to support the PDSC parsing.
 */
public class PdscUtils {

	private static int highestMinorForOne = 6;

	/**
	 * Validate the schema version.
	 *
	 * @param semVer a semantic version object.
	 * @return true if the value is valid.
	 *
	 * @noreference This method is not intended to be referenced by clients.
	 * @apiNote This method takes a non-API type, {@link Version}, the concrete
	 *          version of this type may change in a future release, however the
	 *          method itself is API. Consumers of this API should ensure their
	 *          {@link Version} is loaded from the same bundle as this bundle.
	 *          See https://github.com/eclipse-embed-cdt/eclipse-plugins/issues/453
	 *          for the current status of this issue.
	 */
	public static boolean isSchemaValid(Version semVer) {

		int major = semVer.getMajorVersion();
		int minor = semVer.getMinorVersion();

		if ((major == 1) && (minor <= highestMinorForOne)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Convert a Windows path into a POSIX path.
	 *
	 * @param spath the string representing the Windows path.
	 * @return the corresponding POSIX path.
	 */
	public static String updatePosixSeparators(String spath) {
		return spath.replace('\\', '/');
	}

}
