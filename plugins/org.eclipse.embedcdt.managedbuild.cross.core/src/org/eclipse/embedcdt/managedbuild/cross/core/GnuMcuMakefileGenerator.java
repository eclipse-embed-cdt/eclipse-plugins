/*******************************************************************************
 * Copyright (c) 2019 Liviu Ionescu.
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

package org.eclipse.embedcdt.managedbuild.cross.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.cdt.managedbuilder.makegen.gnu.GnuMakefileGenerator;

@SuppressWarnings("deprecation")
public class GnuMcuMakefileGenerator extends GnuMakefileGenerator {

	private Pattern doubleQuotedOption = Pattern.compile("--?[a-zA-Z]+.*?\\\".*?\\\".*"); //$NON-NLS-1$
	private Pattern singleQuotedOption = Pattern.compile("--?[a-zA-Z]+.*?'.*?'.*"); //$NON-NLS-1$
	private static Pattern singleGroupOption = Pattern.compile("-Wl,--start-group (.*?) -Wl,--end-group"); //$NON-NLS-1$

	@Override
	public String ensurePathIsGNUMakeTargetRuleCompatibleSyntax(String path) {
		boolean isQuotedOption = false;
		if (path.startsWith("-")) { //$NON-NLS-1$
			isQuotedOption = checkIfQuotedOption(path);
		}
		if (checkIfGroupOption(path))
			return escapeGroupWhitespaces(ensureUnquoted(path));
		if (!isQuotedOption)
			return escapeWhitespaces(ensureUnquoted(path));

		return path;
	}

	static public String escapeGroupWhitespaces(String path) {
		Matcher m = singleGroupOption.matcher(path);
		while (m.find()) {
			String grouplib = m.group(1).toString().trim();
			if (grouplib.length() > 0) {
				StringBuffer escapedPath = new StringBuffer();
				String[] segments = grouplib.split("-l"); //$NON-NLS-1$
				if (segments.length > 1) {
					for (int index = 0; index < segments.length; ++index) {
						String segment = segments[index].trim();
						if (segment.length() > 0) {
							escapedPath.append("-l");
							escapedPath.append(escapeWhitespaces(segment));
							if (index + 1 < segments.length) {
								escapedPath.append(" "); //$NON-NLS-1$
							}
						}
					}
				} else {
					escapedPath.append(grouplib);
				}
				path = path.replace(grouplib, escapedPath.toString().trim());
			}
		}
		return path;
	}

	private boolean checkIfGroupOption(String path) {
		Matcher m1 = singleGroupOption.matcher(path);
		if (m1.matches())
			return true;
		return false;
	}

	private boolean checkIfQuotedOption(String path) {
		Matcher m1 = doubleQuotedOption.matcher(path);
		if (m1.matches())
			return true;
		Matcher m2 = singleQuotedOption.matcher(path);
		if (m2.matches())
			return true;
		return false;
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
		return prefix + (doubleQuoted || singleQuoted ? path.substring(1, path.length() - 1) : path);
	}

}
