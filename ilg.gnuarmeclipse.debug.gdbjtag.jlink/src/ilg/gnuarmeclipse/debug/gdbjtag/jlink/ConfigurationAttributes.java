package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

import org.eclipse.cdt.debug.gdbjtag.core.Activator;

public interface ConfigurationAttributes {

	public static final String PREFIX = Activator.PLUGIN_ID;

	// TabDebugger

	public static final String JTAG_DEVICE = "J-Link";

	public static final String INTERFACE = PREFIX + ".interface"; //$NON-NLS-1$
	public static final String INTERFACE_SWD = "swd";
	public static final String INTERFACE_JTAG = "jtag";
	public static final String INTERFACE_DEFAULT = INTERFACE_SWD;
	public static final String INTERFACE_SWD_COMMAND = "monitor interface SWD";
	public static final String INTERFACE_JTAG_COMMAND = "monitor interface JTAG";

	public static final String DO_CONNECT_TO_RUNNING = PREFIX
			+ ".doConnectToRunning"; //$NON-NLS-1$
	public static final boolean DO_CONNECT_TO_RUNNING_DEFAULT = false;

	public static final String INTERFACE_SPEED = PREFIX + ".interfaceSpeed"; //$NON-NLS-1$
	public static final String GDBSERVER_SPEED = PREFIX + ".gdbServerSpeed"; //$NON-NLS-1$
	public static final String INTERFACE_SPEED_AUTO = "auto";
	public static final String INTERFACE_SPEED_ADAPTIVE = "adaptive";
	public static final String INTERFACE_SPEED_DEFAULT = INTERFACE_SPEED_AUTO;
	public static final String GDBSERVER_SPEED_DEFAULT = INTERFACE_SPEED_AUTO;
	public static final String INTERFACE_SPEED_AUTO_COMMAND = "monitor speed auto";
	public static final String INTERFACE_SPEED_ADAPTIVE_COMMAND = "monitor speed adaptive";
	public static final String INTERFACE_SPEED_FIXED_COMMAND = "monitor speed ";

	public static final String FLASH_DEVICE_NAME = PREFIX + ".flashDeviceName"; //$NON-NLS-1$
	public static final String FLASH_DEVICE_NAME_DEFAULT = "";
	public static final String FLASH_DEVICE_NAME_COMMAND = "monitor flash device = ";

	public static final String ENDIANNESS = PREFIX + ".endianness"; //$NON-NLS-1$
	public static final String ENDIANNESS_LITTLE = "little";
	public static final String ENDIANNESS_BIG = "big";
	public static final String ENDIANNESS_DEFAULT = ENDIANNESS_LITTLE;
	public static final String ENDIANNESS_LITTLE_COMMAND = "monitor endian little";
	public static final String ENDIANNESS_BIG_COMMAND = "monitor endian big";

	public static final String GDB_CLIENT_OTHER_COMMANDS = PREFIX
			+ ".gdbClientOtherCommands"; //$NON-NLS-1$
	public static final String GDB_CLIENT_OTHER_COMMANDS_DEFAULT = "set mem inaccessible-by-default off\n"
			+ "set remote memory-write-packet-size 4096\n"
			+ "set remote memory-write-packet-size fixed";

	// TabStartup
	public static final String DO_FIRST_RESET = PREFIX + ".doFirstReset"; //$NON-NLS-1$
	public static final boolean DO_FIRST_RESET_DEFAULT = true;
	public static final String DO_FIRST_RESET_COMMAND = "monitor reset ";

	public static final String FIRST_RESET_TYPE = PREFIX + ".firstResetType"; //$NON-NLS-1$
	public static final String FIRST_RESET_TYPE_DEFAULT = "";

	public static final String FIRST_RESET_SPEED = PREFIX + ".firstResetSpeed"; //$NON-NLS-1$
	public static final String FIRST_RESET_SPEED_DEFAULT = "30";

	public static final String ENABLE_SEMIHOSTING = PREFIX
			+ ".enableSemihosting"; //$NON-NLS-1$
	public static final boolean ENABLE_SEMIHOSTING_DEFAULT = true;
	public static final String ENABLE_SEMIHOSTING_COMMAND = "monitor semihosting enable";

	public static final String ENABLE_SEMIHOSTING_IOCLIENT_TELNET = PREFIX
			+ ".enableSemihostingIoclientTelnet"; //$NON-NLS-1$
	public static final boolean ENABLE_SEMIHOSTING_IOCLIENT_TELNET_DEFAULT = true;
	public static final int ENABLE_SEMIHOSTING_IOCLIENT_TELNET_MASK = 1;

	public static final String ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT = PREFIX
			+ ".enableSemihostingIoclientGdbClient"; //$NON-NLS-1$
	public static final boolean ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT_DEFAULT = false;
	public static final int ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT_MASK = 2;

	public static final String ENABLE_SEMIHOSTING_IOCLIENT_COMMAND = "monitor semihosting IOClient "; //$NON-NLS-1$

	public static final String ENABLE_SWO = PREFIX + ".enableSwo"; //$NON-NLS-1$
	public static final boolean ENABLE_SWO_DEFAULT = true;
	public static final String ENABLE_SWO_COMMAND = "monitor SWO EnableTarget ";
	// public static final String ENABLE_SWO_GETSPEEDINFO_COMMAND =
	// "monitor SWO GetSpeedInfo";

	public static final String SWO_ENABLETARGET_CPUFREQ = PREFIX
			+ ".swoEnableTargetCpuFreq"; //$NON-NLS-1$
	public static final int SWO_ENABLETARGET_CPUFREQ_DEFAULT = 0;

	public static final String SWO_ENABLETARGET_SWOFREQ = PREFIX
			+ ".swoEnableTargetSwoFreq"; //$NON-NLS-1$
	public static final int SWO_ENABLETARGET_SWOFREQ_DEFAULT = 0;

	public static final String SWO_ENABLETARGET_PORTMASK = PREFIX
			+ ".swoEnableTargetPortMask"; //$NON-NLS-1$
	public static final int SWO_ENABLETARGET_PORTMASK_DEFAULT = 1;

	public static final String OTHER_INIT_COMMANDS = PREFIX
			+ ".otherInitCommands"; //$NON-NLS-1$
	public static final String OTHER_INIT_COMMANDS_DEFAULT = "monitor flash download = 1";

	public static final String DO_SECOND_RESET = PREFIX + ".doSecondReset"; //$NON-NLS-1$
	public static final boolean DO_SECOND_RESET_DEFAULT = true;
	public static final String DO_SECOND_RESET_COMMAND = "monitor reset ";

	public static final String SECOND_RESET_TYPE = PREFIX + ".secondResetType"; //$NON-NLS-1$
	public static final String SECOND_RESET_TYPE_DEFAULT = "";

	public static final boolean DO_STOP_AT_DEFAULT = true;
	public static final String STOP_AT_NAME_DEFAULT = "main";

	public static final String OTHER_RUN_COMMANDS = PREFIX
			+ ".otherRunCommands"; //$NON-NLS-1$
	public static final String OTHER_RUN_COMMANDS_DEFAULT = "";

	public static final String DO_CONTINUE = PREFIX + ".doContinue"; //$NON-NLS-1$
	public static final boolean DO_CONTINUE_DEFAULT = true;
	public static final String DO_CONTINUE_COMMAND = "continue";

}
