package ilg.gnuarmeclipse.managedbuild.cross.ui;

import ilg.gnuarmeclipse.managedbuild.cross.Activator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

public class DefaultPreferences {

	public static final String TOOLCHAIN = "toolchains.properties";
	public static final String DEFAULT_NAME = "default.name";
	public static final String DEFAULT_PATH = "default.path";

	private static Properties ms_toolchainProperties;

	public static String getToolchainName() {

		try {
			Properties prop = getToolchainProperties();
			return prop.getProperty(DEFAULT_NAME, "").trim();
		} catch (IOException e) {
			return "";
		}
	}

	public static String getToolchainPath(String toolchainName) {

		int hash = Math.abs(toolchainName.trim().hashCode());
		String property = DEFAULT_PATH + "." + String.valueOf(hash);
		try {
			Properties prop = getToolchainProperties();
			return prop.getProperty(property, "").trim();
		} catch (IOException e) {
			return "";
		}
	}

	private static Properties getToolchainProperties() throws IOException {

		if (ms_toolchainProperties == null) {

			URL url = Platform.getInstallLocation().getURL();

			IPath path = new Path(url.getPath());
			File file = path.append("configuration")
					.append(Activator.PLUGIN_ID).append(TOOLCHAIN).toFile();
			InputStream is = new FileInputStream(file);

			Properties prop = new Properties();
			prop.load(is);

			ms_toolchainProperties = prop;
		}

		return ms_toolchainProperties;
	}
}
