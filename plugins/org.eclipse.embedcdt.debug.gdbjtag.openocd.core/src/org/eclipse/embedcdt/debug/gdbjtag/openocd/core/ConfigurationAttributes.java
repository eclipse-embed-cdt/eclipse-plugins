/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.openocd.core;

public interface ConfigurationAttributes extends org.eclipse.embedcdt.debug.gdbjtag.core.ConfigurationAttributes {

	// ------------------------------------------------------------------------

	// public static final String PREFIX = Activator.PLUGIN_ID;
	public static final String PREFIX = "ilg.gnumcueclipse.debug.gdbjtag.openocd";

	// ------------------------------------------------------------------------

	// TabDebugger

	// Must be in sync with plugin.xml definition
	public static final String JTAG_DEVICE = "GNU MCU OpenOCD";

	public static final String DO_START_GDB_SERVER = PREFIX + ".doStartGdbServer"; //$NON-NLS-1$

	public static final String GDB_SERVER_EXECUTABLE = PREFIX + ".gdbServerExecutable"; //$NON-NLS-1$

	public static final String GDB_SERVER_CONNECTION_ADDRESS = PREFIX + ".gdbServerConnectionAddress"; //$NON-NLS-1$

	public static final String GDB_SERVER_GDB_PORT_NUMBER = PREFIX + ".gdbServerGdbPortNumber"; //$NON-NLS-1$
	public static final String GDB_SERVER_TELNET_PORT_NUMBER = PREFIX + ".gdbServerTelnetPortNumber"; //$NON-NLS-1$
	public static final String GDB_SERVER_TCL_PORT_NUMBER = PREFIX + ".gdbServerTclPortNumber"; //$NON-NLS-1$

	public static final String GDB_SERVER_LOG = PREFIX + ".gdbServerLog"; //$NON-NLS-1$

	public static final String GDB_SERVER_OTHER = PREFIX + ".gdbServerOther"; //$NON-NLS-1$

	public static final String DO_GDB_SERVER_ALLOCATE_CONSOLE = PREFIX + ".doGdbServerAllocateConsole"; //$NON-NLS-1$

	public static final String DO_GDB_SERVER_ALLOCATE_TELNET_CONSOLE = PREFIX + ".doGdbServerAllocateTelnetConsole"; //$NON-NLS-1$

	public static final String DO_START_GDB_CLIENT = PREFIX + ".doStartGdbCLient"; //$NON-NLS-1$

	public static final String GDB_CLIENT_OTHER_OPTIONS = PREFIX + ".gdbClientOtherOptions"; //$NON-NLS-1$

	public static final String GDB_CLIENT_OTHER_COMMANDS = PREFIX + ".gdbClientOtherCommands"; //$NON-NLS-1$

	public static final String GDB_CONNECT_TO_GDB_SERVER = PREFIX + ".useHostNameAndPortOfStartedGdbServer"; //$NON-NLS-1$

	// ------------------------------------------------------------------------

	// TabStartup
	public static final String DO_FIRST_RESET = PREFIX + ".doFirstReset"; //$NON-NLS-1$
	public static final String FIRST_RESET_TYPE = PREFIX + ".firstResetType"; //$NON-NLS-1$

	public static final String ENABLE_SEMIHOSTING = PREFIX + ".enableSemihosting"; //$NON-NLS-1$

	public static final String OTHER_INIT_COMMANDS = PREFIX + ".otherInitCommands"; //$NON-NLS-1$

	public static final String DO_DEBUG_IN_RAM = PREFIX + ".doDebugInRam"; //$NON-NLS-1$

	public static final String DO_SECOND_RESET = PREFIX + ".doSecondReset"; //$NON-NLS-1$

	public static final String SECOND_RESET_TYPE = PREFIX + ".secondResetType"; //$NON-NLS-1$

	public static final String OTHER_RUN_COMMANDS = PREFIX + ".otherRunCommands"; //$NON-NLS-1$

	public static final String DO_CONTINUE = PREFIX + ".doContinue"; //$NON-NLS-1$

	// ------------------------------------------------------------------------
}
