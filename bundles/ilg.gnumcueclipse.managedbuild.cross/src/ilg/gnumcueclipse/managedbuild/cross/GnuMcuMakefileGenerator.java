/*******************************************************************************
 * Copyright (c) 2019 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.managedbuild.cross;

import org.eclipse.cdt.managedbuilder.makegen.gnu.GnuMakefileGenerator;

public class GnuMcuMakefileGenerator extends GnuMakefileGenerator {

	public String ensurePathIsGNUMakeTargetRuleCompatibleSyntax(String path) {
		return escapeWhitespaces(ensureUnquoted(path));
	}

	/**
	 * Strips outermost quotes of Strings of the form "a" and 'a' or returns the original
	 * string if the input is not of this form.
	 *
	 * @throws NullPointerException if path is null
	 * @return a String without the outermost quotes (if the input has them)
	 */
	public static String ensureUnquoted(String path) {
		String prefix = "";
		// Hack to accommodate LIBS which start with "-l".
		if (path.startsWith("-l\"") || path.startsWith("-l'")) {
			prefix = "-l";
			path = path.substring(2);
		}
		boolean doubleQuoted = path.startsWith("\"") && path.endsWith("\""); //$NON-NLS-1$ //$NON-NLS-2$
		boolean singleQuoted = path.startsWith("'") && path.endsWith("'"); //$NON-NLS-1$ //$NON-NLS-2$
		return prefix + (doubleQuoted || singleQuoted ? path.substring(1,path.length()-1) : path);
	}

}
