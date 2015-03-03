package ilg.gnuarmeclipse.templates.core;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;

public class Utils {

	public static boolean isBuildType(IConfiguration config,
			String buildTypeSuffix) {

		if (buildTypeSuffix == null)
			return true;

		// If suffix not given, assume it is ok
		buildTypeSuffix = buildTypeSuffix.trim();
		if (buildTypeSuffix.length() == 0)
			return true;

		String configBuildTypeValue = config.getBuildProperties()
				.getProperty("org.eclipse.cdt.build.core.buildType").getValue()
				.toString();
		if (configBuildTypeValue != null
				&& configBuildTypeValue.endsWith(buildTypeSuffix)) {
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
