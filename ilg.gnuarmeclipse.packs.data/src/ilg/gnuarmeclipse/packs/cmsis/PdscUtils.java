/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.cmsis;

import com.github.zafarkhaja.semver.Version;

/**
 * Static class used to support the PDSC parsing.
 */
public class PdscUtils {

	/**
	 * Validate the schema version.
	 * 
	 * @param semVer
	 *            a semantic version object.
	 * @return true if the value is valid.
	 */
	public static boolean isSchemaValid(Version semVer) {

		int major = semVer.getMajorVersion();
		int minor = semVer.getMinorVersion();

		if ((major == 1) && (minor <= 4)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Convert a Windows path into a POSIX path.
	 * 
	 * @param spath
	 *            the string representing the Windows path.
	 * @return the corresponding POSIX path.
	 */
	public static String updatePosixSeparators(String spath) {
		return spath.replace('\\', '/');
	}

}
