package ilg.gnuarmeclipse.debug.gdbjtag.openocd;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

public class OpenOcdVariableInitializer implements IValueVariableInitializer {

	static final String OPENOCD_VARIABLE_NAME = "openocd_executable";

	@Override
	public void initialize(IValueVariable variable) {

		if (OPENOCD_VARIABLE_NAME.equals(variable.getName())) {

			String value;

			value = ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_NAME;

			variable.setValue(value);
		}
	}

}
