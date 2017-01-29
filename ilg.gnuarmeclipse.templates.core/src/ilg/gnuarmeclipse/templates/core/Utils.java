/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.templates.core;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;

public class Utils {

	public static boolean isBuildType(IConfiguration config, String buildTypeSuffix) {

		if (buildTypeSuffix == null)
			return true;

		// If suffix not given, assume it is ok
		buildTypeSuffix = buildTypeSuffix.trim();
		if (buildTypeSuffix.length() == 0)
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
		if (condition.length() == 0) {
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
