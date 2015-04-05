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

package ilg.gnuarmeclipse.debug.gdbjtag.qemu;

public interface ConfigurationAttributes {

	public static final String PREFIX = Activator.PLUGIN_ID;

	// TabDebugger

	// Must be in sync with plugin.xml definition
	public static final String JTAG_DEVICE = "GNU ARM QEMU";

	public static final String DO_START_GDB_SERVER = PREFIX
			+ ".doStartGdbServer"; //$NON-NLS-1$
	public static final boolean DO_START_GDB_SERVER_DEFAULT = true;

	public static final String GDB_SERVER_EXECUTABLE = PREFIX
			+ ".gdbServerExecutable"; //$NON-NLS-1$
	public static final String GDB_SERVER_EXECUTABLE_DEFAULT = "${qemu_path}/${qemu_executable}";

	public static final String GDB_SERVER_EXECUTABLE_DEFAULT_NAME = "qemu-system-gnuarmeclipse";

	public static final String GDB_SERVER_MACHINE_NAME = PREFIX
			+ ".gdbServerMachineName"; //$NON-NLS-1$
	public static final String GDB_SERVER_MACHINE_NAME_DEFAULT = "?";

	public static final String GDB_SERVER_CONNECTION_ADDRESS = PREFIX
			+ ".gdbServerConnectionAddress"; //$NON-NLS-1$
	public static final String GDB_SERVER_CONNECTION_ADDRESS_DEFAULT = "";

	public static final String GDB_SERVER_GDB_PORT_NUMBER = PREFIX
			+ ".gdbServerGdbPortNumber"; //$NON-NLS-1$
	public static final int GDB_SERVER_GDB_PORT_NUMBER_DEFAULT = 1234;

	public static final String GDB_SERVER_OTHER = PREFIX + ".gdbServerOther"; //$NON-NLS-1$
	public static final String GDB_SERVER_OTHER_DEFAULT = ""; //$NON-NLS-1$

	public static final String DO_GDB_SERVER_ALLOCATE_CONSOLE = PREFIX
			+ ".doGdbServerAllocateConsole"; //$NON-NLS-1$
	public static final boolean DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT = true;

	public static final String IS_GDB_SERVER_VERBOSE = PREFIX
			+ ".isGdbServerVerbose"; //$NON-NLS-1$
	public static final boolean IS_GDB_SERVER_VERBOSE_DEFAULT = false;

	public static final String GDB_CLIENT_EXECUTABLE_DEFAULT = "${cross_prefix}gdb${cross_suffix}";

	public static final String GDB_CLIENT_OTHER_OPTIONS = PREFIX
			+ ".gdbClientOtherOptions"; //$NON-NLS-1$
	public static final String GDB_CLIENT_OTHER_OPTIONS_DEFAULT = "";

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
	public static final boolean DO_FIRST_RESET_DEFAULT = false;
	public static final String DO_FIRST_RESET_COMMAND = "monitor system_reset ";

	public static final String HALT_COMMAND = ""; // "monitor stop";

	public static final String ENABLE_SEMIHOSTING = PREFIX
			+ ".enableSemihosting"; //$NON-NLS-1$
	public static final boolean ENABLE_SEMIHOSTING_DEFAULT = true;

	public static final String SEMIHOSTING_CMDLINE = PREFIX
			+ ".semihosting.cmdline"; //$NON-NLS-1$

	// public static final String ENABLE_SEMIHOSTING_OPTION =
	// "-semihosting-config enable=on,target=native";

	public static final String OTHER_INIT_COMMANDS = PREFIX
			+ ".otherInitCommands"; //$NON-NLS-1$
	public static final String OTHER_INIT_COMMANDS_DEFAULT = "";

	public static final String DO_DEBUG_IN_RAM = PREFIX + ".doDebugInRam"; //$NON-NLS-1$
	public static final boolean DO_DEBUG_IN_RAM_DEFAULT = false;

	public static final String DO_SECOND_RESET = PREFIX + ".doSecondReset"; //$NON-NLS-1$
	public static final boolean DO_SECOND_RESET_DEFAULT = true;
	public static final String DO_SECOND_RESET_COMMAND = DO_FIRST_RESET_COMMAND;

	public static final boolean DO_STOP_AT_DEFAULT = true;
	public static final String STOP_AT_NAME_DEFAULT = "main";

	public static final String OTHER_RUN_COMMANDS = PREFIX
			+ ".otherRunCommands"; //$NON-NLS-1$
	public static final String OTHER_RUN_COMMANDS_DEFAULT = "";

	public static final String DO_CONTINUE = PREFIX + ".doContinue"; //$NON-NLS-1$
	public static final boolean DO_CONTINUE_DEFAULT = true;
	public static final String DO_CONTINUE_COMMAND = "continue";
}
