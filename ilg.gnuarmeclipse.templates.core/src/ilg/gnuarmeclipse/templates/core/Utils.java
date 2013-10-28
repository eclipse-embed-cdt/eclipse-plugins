package ilg.gnuarmeclipse.templates.core;

import org.eclipse.cdt.managedbuilder.core.IConfiguration;

public class Utils {

	public static boolean isBuildType(IConfiguration config, String buildTypeSuffix) {

		if (buildTypeSuffix != null) {
			buildTypeSuffix = buildTypeSuffix.trim();
			String configBuildTypeValue = config.getBuildProperties()
					.getProperty("org.eclipse.cdt.build.core.buildType")
					.getValue().toString();
			if (buildTypeSuffix.length() > 0 && configBuildTypeValue != null
					&& configBuildTypeValue.endsWith(buildTypeSuffix)) {
				return true;
			}
		}

		return false;
	}
}
