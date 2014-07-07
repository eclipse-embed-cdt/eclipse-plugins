package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

public class GdbServerVariableInitializer implements IValueVariableInitializer {

	static final String GDB_SERVER_VARIABLE_NAME = "jlink_gdbserver";

	@Override
	public void initialize(IValueVariable variable) {

		if (GDB_SERVER_VARIABLE_NAME.equals(variable.getName())) {

			String value;

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
		}
	}

}
