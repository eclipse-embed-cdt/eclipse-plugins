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

/**
 * Static class used to support the PDSC parsing.
 */
public class PdscUtils {

	/**
	 * Validate the schema version.
	 * 
	 * @param schemaVersion
	 *            a string with the xml attribute of the <package> element.
	 * @return true if the value is valid.
	 */
	public static boolean isSchemaValid(String schemaVersion) {

		if ("1.0".equals(schemaVersion) || "1.1".equals(schemaVersion)
				|| "1.2".equals(schemaVersion)) {
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
