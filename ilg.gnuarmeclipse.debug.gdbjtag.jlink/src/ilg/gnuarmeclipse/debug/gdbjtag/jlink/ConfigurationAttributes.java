/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.jlink;

public interface ConfigurationAttributes {

	// ------------------------------------------------------------------------

	public static final String PREFIX = Activator.PLUGIN_ID;

	// ------------------------------------------------------------------------

	// TabDebugger

	// Must be in sync with plugin.xml definition
	public static final String JTAG_DEVICE = "GNU ARM J-Link";

	public static final String INTERFACE_COMPAT = PREFIX + ".interface"; //$NON-NLS-1$
	public static final String GDB_SERVER_DEBUG_INTERFACE = PREFIX + ".gdbServerDebugInterface"; //$NON-NLS-1$

	public static final String DO_CONNECT_TO_RUNNING = PREFIX + ".doConnectToRunning"; //$NON-NLS-1$

	public static final String INTERFACE_SPEED = PREFIX + ".interfaceSpeed"; //$NON-NLS-1$
	public static final String GDB_SERVER_SPEED_COMPAT = PREFIX + ".gdbServerSpeed"; //$NON-NLS-1$
	public static final String GDB_SERVER_DEVICE_SPEED = PREFIX + ".gdbServerDeviceSpeed"; //$NON-NLS-1$

	public static final String FLASH_DEVICE_NAME_COMPAT = PREFIX + ".flashDeviceName"; //$NON-NLS-1$
	public static final String GDB_SERVER_DEVICE_NAME = PREFIX + ".gdbServerDeviceName"; //$NON-NLS-1$

	public static final String ENDIANNESS_COMPAT = PREFIX + ".endianness"; //$NON-NLS-1$
	public static final String GDB_SERVER_DEVICE_ENDIANNESS = PREFIX + ".gdbServerDeviceEndianness"; //$NON-NLS-1$

	public static final String DO_START_GDB_SERVER = PREFIX + ".doStartGdbServer"; //$NON-NLS-1$

	public static final String GDB_SERVER_EXECUTABLE = PREFIX + ".gdbServerExecutable"; //$NON-NLS-1$

	public static final String GDB_SERVER_CONNECTION = PREFIX + ".gdbServerConnection"; //$NON-NLS-1$

	public static final String GDB_SERVER_CONNECTION_ADDRESS = PREFIX + ".gdbServerConnectionAddress"; //$NON-NLS-1$

	public static final String GDB_SERVER_GDB_PORT_NUMBER = PREFIX + ".gdbServerGdbPortNumber"; //$NON-NLS-1$

	public static final String GDB_SERVER_SWO_PORT_NUMBER = PREFIX + ".gdbServerSwoPortNumber"; //$NON-NLS-1$

	public static final String GDB_SERVER_TELNET_PORT_NUMBER = PREFIX + ".gdbServerTelnetPortNumber"; //$NON-NLS-1$

	public static final String DO_GDB_SERVER_VERIFY_DOWNLOAD = PREFIX + ".doGdbServerVerifyDownload"; //$NON-NLS-1$

	public static final String DO_GDB_SERVER_INIT_REGS = PREFIX + ".doGdbServerInitRegs"; //$NON-NLS-1$

	public static final String DO_GDB_SERVER_LOCAL_ONLY = PREFIX + ".doGdbServerLocalOnly"; //$NON-NLS-1$

	public static final String DO_GDB_SERVER_SILENT = PREFIX + ".doGdbServerSilent"; //$NON-NLS-1$

	public static final String GDB_SERVER_LOG = PREFIX + ".gdbServerLog"; //$NON-NLS-1$

	public static final String GDB_SERVER_OTHER = PREFIX + ".gdbServerOther"; //$NON-NLS-1$

	public static final String DO_GDB_SERVER_ALLOCATE_CONSOLE = PREFIX + ".doGdbServerAllocateConsole"; //$NON-NLS-1$

	public static final String DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE = PREFIX
			+ ".doGdbServerAllocateSemihostingConsole"; //$NON-NLS-1$

	public static final String GDB_CLIENT_OTHER_OPTIONS = PREFIX + ".gdbClientOtherOptions"; //$NON-NLS-1$

	public static final String GDB_CLIENT_OTHER_COMMANDS = PREFIX + ".gdbClientOtherCommands"; //$NON-NLS-1$

	// ------------------------------------------------------------------------

	// TabStartup
	public static final String DO_FIRST_RESET = PREFIX + ".doFirstReset"; //$NON-NLS-1$

	public static final String FIRST_RESET_TYPE = PREFIX + ".firstResetType"; //$NON-NLS-1$

	public static final String FIRST_RESET_SPEED = PREFIX + ".firstResetSpeed"; //$NON-NLS-1$

	public static final String ENABLE_FLASH_BREAKPOINTS = PREFIX + ".enableFlashBreakpoints"; //$NON-NLS-1$

	public static final String ENABLE_SEMIHOSTING = PREFIX + ".enableSemihosting"; //$NON-NLS-1$

	public static final String ENABLE_SEMIHOSTING_IOCLIENT_TELNET = PREFIX + ".enableSemihostingIoclientTelnet"; //$NON-NLS-1$

	public static final String ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT = PREFIX + ".enableSemihostingIoclientGdbClient"; //$NON-NLS-1$

	public static final String ENABLE_SWO = PREFIX + ".enableSwo"; //$NON-NLS-1$

	public static final String SWO_ENABLETARGET_CPUFREQ = PREFIX + ".swoEnableTargetCpuFreq"; //$NON-NLS-1$

	public static final String SWO_ENABLETARGET_SWOFREQ = PREFIX + ".swoEnableTargetSwoFreq"; //$NON-NLS-1$

	public static final String SWO_ENABLETARGET_PORTMASK = PREFIX + ".swoEnableTargetPortMask"; //$NON-NLS-1$

	public static final String OTHER_INIT_COMMANDS = PREFIX + ".otherInitCommands"; //$NON-NLS-1$

	public static final String DO_DEBUG_IN_RAM = PREFIX + ".doDebugInRam"; //$NON-NLS-1$

	public static final String DO_SECOND_RESET = PREFIX + ".doSecondReset"; //$NON-NLS-1$

	public static final String SECOND_RESET_TYPE = PREFIX + ".secondResetType"; //$NON-NLS-1$

	public static final String OTHER_RUN_COMMANDS = PREFIX + ".otherRunCommands"; //$NON-NLS-1$

	public static final String DO_CONTINUE = PREFIX + ".doContinue"; //$NON-NLS-1$

	// ------------------------------------------------------------------------
}
