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
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.templates.core;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.embedcdt.internal.templates.core.Activator;

public class Utils {

	public static boolean isBuildType(IConfiguration config, String buildTypeSuffix) {

		if (buildTypeSuffix == null)
			return true;

		// If suffix not given, assume it is ok
		buildTypeSuffix = buildTypeSuffix.trim();
		if (buildTypeSuffix.isEmpty())
			return true;

		String configBuildTypeValue = config.getBuildProperties().getProperty("org.eclipse.cdt.build.core.buildType")
				.getValue().toString();
		if (configBuildTypeValue != null && configBuildTypeValue.endsWith(buildTypeSuffix)) {
			return true;
		}

		return false;
	}

	public static boolean isConditionSatisfied(String condition) {

		if (condition == null)
			return true;

		condition = condition.trim();
		if (condition.isEmpty()) {
			// No condition string present, take it as ALWAYS
			return true;
		}

		String sa[] = condition.split(" ");
		if (sa.length != 3) {
			Activator.log("Unrecognised condition " + condition);
			return false;
		}

		if ("==".equals(sa[1])) {
			return sa[0].equals(sa[2]);
		} else if ("!=".equals(sa[1])) {
			return !sa[0].equals(sa[2]);
		}

		return false;
	}
}
