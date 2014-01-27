package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

public interface ConfigurationAttributes {

	public static final String PREFIX = Activator.PLUGIN_ID;

	// TabDebugger

	public static final String JTAG_DEVICE = "J-Link";
	// public static final String JTAG_DEVICE = "Generic TCP/IP";

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
	public static final String GDB_SERVER_SPEED = PREFIX + ".gdbServerSpeed"; //$NON-NLS-1$
	public static final String INTERFACE_SPEED_AUTO = "auto";
	public static final String INTERFACE_SPEED_ADAPTIVE = "adaptive";
	public static final String INTERFACE_SPEED_DEFAULT = INTERFACE_SPEED_AUTO;
	public static final String GDB_SERVER_SPEED_DEFAULT = INTERFACE_SPEED_AUTO;
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

	public static final String DO_START_GDB_SERVER = PREFIX
			+ ".doStartGdbServer"; //$NON-NLS-1$
	public static final boolean DO_START_GDB_SERVER_DEFAULT = true;

	public static final String GDB_SERVER_EXECUTABLE = PREFIX
			+ ".gdbServerExecutable"; //$NON-NLS-1$
	public static final String JLINK_PATH = "${jlink_path}";
	public static final String GDB_SERVER_EXECUTABLE_DEFAULT = JLINK_PATH
			+ "/JLinkGDBServer";
	public static final String GDB_SERVER_EXECUTABLE_DEFAULT_WINDOWS = JLINK_PATH
			+ "/JLinkGDBServerCL";
	public static final String GDB_SERVER_EXECUTABLE_DEFAULT_MAC = JLINK_PATH
			+ "/JLinkGDBServer";
	public static final String GDB_SERVER_EXECUTABLE_DEFAULT_LINUX = GDB_SERVER_EXECUTABLE_DEFAULT;

	public static final String GDB_SERVER_CONNECTION = PREFIX
			+ ".gdbServerConnection"; //$NON-NLS-1$
	public static final String GDB_SERVER_CONNECTION_USB = "usb"; //$NON-NLS-1$
	public static final String GDB_SERVER_CONNECTION_IP = "ip"; //$NON-NLS-1$
	public static final String GDB_SERVER_CONNECTION_DEFAULT = GDB_SERVER_CONNECTION_USB;

	public static final String GDB_SERVER_CONNECTION_ADDRESS = PREFIX
			+ ".gdbServerConnectionAddress"; //$NON-NLS-1$
	public static final String GDB_SERVER_CONNECTION_ADDRESS_DEFAULT = "";

	public static final String GDB_SERVER_GDB_PORT_NUMBER = PREFIX
			+ ".gdbServerGdbPortNumber"; //$NON-NLS-1$
	public static final int GDB_SERVER_GDB_PORT_NUMBER_DEFAULT = 2331;

	public static final String GDB_SERVER_SWO_PORT_NUMBER = PREFIX
			+ ".gdbServerSwoPortNumber"; //$NON-NLS-1$
	public static final int GDB_SERVER_SWO_PORT_NUMBER_DEFAULT = 2332;

	public static final String GDB_SERVER_TELNET_PORT_NUMBER = PREFIX
			+ ".gdbServerTelnetPortNumber"; //$NON-NLS-1$
	public static final int GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT = 2333;

	public static final String DO_GDB_SERVER_VERIFY_DOWNLOAD = PREFIX
			+ ".doGdbServerVerifyDownload"; //$NON-NLS-1$
	public static final boolean DO_GDB_SERVER_VERIFY_DOWNLOAD_DEFAULT = true;

	public static final String DO_GDB_SERVER_INIT_REGS = PREFIX
			+ ".doGdbServerInitRegs"; //$NON-NLS-1$
	public static final boolean DO_GDB_SERVER_INIT_REGS_DEFAULT = true;

	public static final String DO_GDB_SERVER_LOCAL_ONLY = PREFIX
			+ ".doGdbServerLocalOnly"; //$NON-NLS-1$
	public static final boolean DO_GDB_SERVER_LOCAL_ONLY_DEFAULT = false;

	public static final String DO_GDB_SERVER_SILENT = PREFIX
			+ ".doGdbServerSilent"; //$NON-NLS-1$
	public static final boolean DO_GDB_SERVER_SILENT_DEFAULT = false;

	public static final String GDB_SERVER_LOG = PREFIX + ".gdbServerLog"; //$NON-NLS-1$
	public static final String GDB_SERVER_LOG_DEFAULT = ""; //$NON-NLS-1$

	public static final String GDB_SERVER_OTHER = PREFIX + ".gdbServerOther"; //$NON-NLS-1$
	public static final String GDB_SERVER_OTHER_DEFAULT = ""; //$NON-NLS-1$

	public static final String DO_GDB_SERVER_ALLOCATE_CONSOLE = PREFIX
			+ ".doGdbServerAllocateConsole"; //$NON-NLS-1$
	public static final boolean DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT = true;

	public static final String DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE = PREFIX
			+ ".doGdbServerAllocateSemihostingConsole"; //$NON-NLS-1$
	public static final boolean DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT = true;

	public static final String GDB_CLIENT_EXECUTABLE_DEFAULT = "${cross_prefix}gdb${cross_suffix}";

	public static final String GDB_CLIENT_OTHER_COMMANDS = PREFIX
			+ ".gdbClientOtherCommands"; //$NON-NLS-1$
	public static final String GDB_CLIENT_OTHER_COMMANDS_DEFAULT = "set mem inaccessible-by-default off\n";

	public static final boolean USE_REMOTE_TARGET_DEFAULT = true;
	public static final String REMOTE_IP_ADDRESS_LOCALHOST = "localhost"; //$NON-NLS-1$
	public static final String REMOTE_IP_ADDRESS_DEFAULT = REMOTE_IP_ADDRESS_LOCALHOST; //$NON-NLS-1$
	public static final int REMOTE_PORT_NUMBER_DEFAULT = GDB_SERVER_GDB_PORT_NUMBER_DEFAULT;

	public static final boolean UPDATE_THREAD_LIST_DEFAULT = false;

	// TabStartup
	public static final String DO_FIRST_RESET = PREFIX + ".doFirstReset"; //$NON-NLS-1$
	public static final boolean DO_FIRST_RESET_DEFAULT = true;
	public static final String DO_FIRST_RESET_COMMAND = "monitor reset ";

	public static final String HALT_COMMAND = "monitor halt";

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
	public static final String ENABLE_SWO_COMMAND = "monitor SWO DisableTarget 0xFFFFFFFF\n"
			+ "monitor SWO EnableTarget ";
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
	public static final String OTHER_INIT_COMMANDS_DEFAULT = "monitor flash breakpoints = 1";

	//public static final String ENABLE_FLASH_DOWNLOAD_COMMAND = "monitor flash download = 1";
	//public static final String DISABLE_FLASH_DOWNLOAD_COMMAND = "monitor flash download = 0";

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
