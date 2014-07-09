package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

public class GdbServerVariableInitializer implements IValueVariableInitializer {

	static final String JLINK_GDBSERVER = "jlink_gdbserver";
	static final String JLINK_PATH = "jlink_path";

	static final String UNDEFINED_PATH = "undefined_path";

	@Override
	public void initialize(IValueVariable variable) {

		String value;

		if (JLINK_GDBSERVER.equals(variable.getName())) {

			if (Utils.isWindows()) {
				value = ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_NAME_WINDOWS;
			} else if (Utils.isLinux()) {
				value = ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_NAME_LINUX;
			} else if (Utils.isMacOSX()) {
				value = ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_NAME_MAC;
			} else {
				value = ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_NAME;
			}

			variable.setValue(value);

		} else if (JLINK_PATH.equals(variable.getName())) {

			if (Utils.isWindows()) {
				value = "C:\\Program Files\\SEGGER\\JLinkARM_Vxxx";
			} else if (Utils.isLinux()) {
				value = "/usr/bin";
			} else if (Utils.isMacOSX()) {
				value = "/Applications/SEGGER/JLink";
			} else {
				value = UNDEFINED_PATH;
			}

			variable.setValue(value);
		}
	}

}
