package ilg.gnuarmeclipse.debug.gdbjtag.openocd;

import ilg.gnuarmeclipse.core.EclipseUtils;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

public class OpenOcdVariableInitializer implements IValueVariableInitializer {

	static final String OPENOCD_VARIABLE_NAME = "openocd_executable";
	static final String OPENOCD_PATH = "openocd_path";

	static final String UNDEFINED_PATH = "undefined_path";

	@Override
	public void initialize(IValueVariable variable) {

		String value;

		if (OPENOCD_VARIABLE_NAME.equals(variable.getName())) {

			value = ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_NAME;
			variable.setValue(value);

		} else if (OPENOCD_PATH.equals(variable.getName())) {

			if (EclipseUtils.isWindows()) {
				value = UNDEFINED_PATH;
			} else if (EclipseUtils.isLinux()) {
				value = "/usr/bin";
			} else if (EclipseUtils.isMacOSX()) {
				value = "/opt/local/bin";
			} else {
				value = UNDEFINED_PATH;
			}

			variable.setValue(value);
		}
	}

}
