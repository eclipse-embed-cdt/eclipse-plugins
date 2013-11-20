package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.cdt.debug.gdbjtag.core.Activator;

public interface ConfigurationAttributes {

	public static final String PREFIX = Activator.PLUGIN_ID;
	
	public static final String DO_FIRST_RESET = PREFIX + ".doFirstReset"; //$NON-NLS-1$
	public static final boolean DO_FIRST_RESET_DEFAULT = false;
	public static final String DO_FIRST_RESET_COMMAND = "monitor reset ";
	
	public static final String FIRST_RESET_TYPE = PREFIX + ".firstResetType"; //$NON-NLS-1$
	public static final String FIRST_RESET_TYPE_DEFAULT = "";

	public static final String ENABLE_SEMIHOSTING = PREFIX + ".enableSemihosting"; //$NON-NLS-1$
	public static final boolean ENABLE_SEMIHOSTING_DEFAULT = true;
	public static final String ENABLE_SEMIHOSTING_COMMAND = "monitor semihosting enable";

	public static final String ENABLE_SWO = PREFIX + ".enableSwo"; //$NON-NLS-1$
	public static final boolean ENABLE_SWO_DEFAULT = true;
	public static final String ENABLE_SWO_COMMAND = "monitor SWO EnableTarget 0 0 1 0";

	public static final String OTHER_INIT_COMMANDS = PREFIX + ".otherInitCommands"; //$NON-NLS-1$
	public static final String OTHER_INIT_COMMANDS_DEFAULT = "";

}
