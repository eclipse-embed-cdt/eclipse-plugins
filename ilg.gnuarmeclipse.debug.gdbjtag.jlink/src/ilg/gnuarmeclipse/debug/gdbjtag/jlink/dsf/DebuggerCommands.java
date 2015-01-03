/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.jlink.dsf;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmDebuggerCommandsService;
import ilg.gnuarmeclipse.debug.gdbjtag.jlink.ConfigurationAttributes;

import java.util.List;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.osgi.framework.BundleContext;

public class DebuggerCommands extends GnuArmDebuggerCommandsService {

	// ------------------------------------------------------------------------

	public DebuggerCommands(DsfSession session, ILaunchConfiguration lc) {
		super(session, lc, true); // do double backslash
	}

	// ------------------------------------------------------------------------

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getInstance().getBundle().getBundleContext();
	}

	// ------------------------------------------------------------------------

	@Override
	public IStatus addGdbInitCommandsCommands(List<String> commandsList) {

		String otherInits = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
				ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS_DEFAULT)
				.trim();

		otherInits = DebugUtils.resolveAll(otherInits, fAttributes);
		DebugUtils.addMultiLine(otherInits, commandsList);

		return Status.OK_STATUS;
	}

	// ------------------------------------------------------------------------

	@Override
	public IStatus addGnuArmResetCommands(List<String> commandsList) {

		IStatus status = addFirstResetCommands(commandsList);
		if (!status.isOK()) {
			return status;
		}

		status = addLoadSymbolsCommands(commandsList);

		if (!status.isOK()) {
			return status;
		}

		boolean doConnectToRunning = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);

		if (!doConnectToRunning) {
			if (CDebugUtils.getAttribute(fAttributes,
					IGDBJtagConstants.ATTR_LOAD_IMAGE,
					IGDBJtagConstants.DEFAULT_LOAD_IMAGE)
					&& !CDebugUtils.getAttribute(fAttributes,
							ConfigurationAttributes.DO_DEBUG_IN_RAM,
							ConfigurationAttributes.DO_DEBUG_IN_RAM_DEFAULT)) {

				status = addLoadImageCommands(commandsList);

				if (!status.isOK()) {
					return status;
				}
			}
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus addGnuArmStartCommands(List<String> commandsList) {

		boolean doReset = !CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);

		IStatus status = addStartRestartCommands(doReset, commandsList);

		if (!status.isOK()) {
			return status;
		}

		return Status.OK_STATUS;
	}

	@Override
	public IStatus addGnuArmRestartCommands(List<String> commandsList) {

		return addStartRestartCommands(true, commandsList);
	}

	// ------------------------------------------------------------------------

	@Override
	public IStatus addFirstResetCommands(List<String> commandsList) {

		String attr;

		try {
			attr = String.valueOf(CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.FIRST_RESET_SPEED,
					ConfigurationAttributes.FIRST_RESET_SPEED_DEFAULT));
		} catch (Exception e) {
			attr = CDebugUtils
					.getAttribute(
							fAttributes,
							ConfigurationAttributes.FIRST_RESET_SPEED,
							String.valueOf(ConfigurationAttributes.FIRST_RESET_SPEED_DEFAULT));
		}
		if (!attr.isEmpty()) {
			commandsList
					.add(ConfigurationAttributes.INTERFACE_SPEED_FIXED_COMMAND
							+ attr);
		}

		String commandStr;
		boolean noReset = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);
		if (!noReset) {
			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.DO_FIRST_RESET,
					ConfigurationAttributes.DO_FIRST_RESET_DEFAULT)) {

				// Since reset does not clear breakpoints, we do it explicitly
				commandStr = ConfigurationAttributes.CLRBP_COMMAND;
				commandsList.add(commandStr);

				commandStr = ConfigurationAttributes.DO_FIRST_RESET_COMMAND;
				String resetType = CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.FIRST_RESET_TYPE,
						ConfigurationAttributes.FIRST_RESET_TYPE_DEFAULT);
				commandsList.add(commandStr + resetType);

				// Although the manual claims that reset always does a
				// halt, better issue it explicitly
				commandStr = ConfigurationAttributes.HALT_COMMAND;
				commandsList.add(commandStr);

				// Also add a command to see the registers in the
				// location where execution halted
				commandStr = ConfigurationAttributes.REGS_COMMAND;
				commandsList.add(commandStr);

				// Flush registers, GDB should read them again
				commandStr = ConfigurationAttributes.FLUSH_REGISTERS_COMMAND;
				commandsList.add(commandStr);
			}
		}

		attr = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.INTERFACE_SPEED,
				ConfigurationAttributes.INTERFACE_SPEED_AUTO);
		if (ConfigurationAttributes.INTERFACE_SPEED_AUTO.equals(attr)) {
			commandsList
					.add(ConfigurationAttributes.INTERFACE_SPEED_AUTO_COMMAND);
		} else if (ConfigurationAttributes.INTERFACE_SPEED_ADAPTIVE
				.equals(attr)) {
			commandsList
					.add(ConfigurationAttributes.INTERFACE_SPEED_ADAPTIVE_COMMAND);
		} else {
			commandsList
					.add(ConfigurationAttributes.INTERFACE_SPEED_FIXED_COMMAND
							+ attr);
		}

		commandStr = ConfigurationAttributes.ENABLE_FLASH_BREAKPOINTS_COMMAND;
		if (CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.ENABLE_FLASH_BREAKPOINTS,
				ConfigurationAttributes.ENABLE_FLASH_BREAKPOINTS_DEFAULT))
			commandStr += "1";
		else
			commandStr += "0";
		commandsList.add(commandStr);

		if (CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.ENABLE_SEMIHOSTING,
				ConfigurationAttributes.ENABLE_SEMIHOSTING_DEFAULT)) {
			commandStr = ConfigurationAttributes.ENABLE_SEMIHOSTING_COMMAND;
			commandsList.add(commandStr);

			int ioclientMask = 0;
			if (CDebugUtils
					.getAttribute(
							fAttributes,
							ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_TELNET,
							ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_TELNET_DEFAULT)) {
				ioclientMask |= ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_TELNET_MASK;
			}
			if (CDebugUtils
					.getAttribute(
							fAttributes,
							ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT,
							ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT_DEFAULT)) {
				ioclientMask |= ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT_MASK;
			}

			commandStr = ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_COMMAND
					+ String.valueOf(ioclientMask);
			commandsList.add(commandStr);
		}

		attr = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.GDB_SERVER_DEBUG_INTERFACE,
				ConfigurationAttributes.INTERFACE_SWD);
		if (ConfigurationAttributes.INTERFACE_SWD.equals(attr)) {

			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.ENABLE_SWO,
					ConfigurationAttributes.ENABLE_SWO_DEFAULT)) {

				commandsList.add(ConfigurationAttributes.DISABLE_SWO_COMMAND);

				commandStr = ConfigurationAttributes.ENABLE_SWO_COMMAND;
				commandStr += CDebugUtils
						.getAttribute(
								fAttributes,
								ConfigurationAttributes.SWO_ENABLETARGET_CPUFREQ,
								ConfigurationAttributes.SWO_ENABLETARGET_CPUFREQ_DEFAULT);
				commandStr += " ";
				commandStr += CDebugUtils
						.getAttribute(
								fAttributes,
								ConfigurationAttributes.SWO_ENABLETARGET_SWOFREQ,
								ConfigurationAttributes.SWO_ENABLETARGET_SWOFREQ_DEFAULT);
				commandStr += " ";
				commandStr += CDebugUtils
						.getAttribute(
								fAttributes,
								ConfigurationAttributes.SWO_ENABLETARGET_PORTMASK,
								ConfigurationAttributes.SWO_ENABLETARGET_PORTMASK_DEFAULT);
				commandStr += " 0";

				commandsList.add(commandStr);
			}
		}

		String otherInits = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.OTHER_INIT_COMMANDS,
				ConfigurationAttributes.OTHER_INIT_COMMANDS_DEFAULT).trim();

		otherInits = DebugUtils.resolveAll(otherInits, fAttributes);
		if (fDoDoubleBackslash && EclipseUtils.isWindows()) {
			otherInits = StringUtils.duplicateBackslashes(otherInits);
		}
		DebugUtils.addMultiLine(otherInits, commandsList);

		return Status.OK_STATUS;
	}

	@Override
	public IStatus addStartRestartCommands(boolean doReset,
			List<String> commandsList) {

		String commandStr;

		if (doReset) {
			if (CDebugUtils.getAttribute(fAttributes,
					ConfigurationAttributes.DO_SECOND_RESET,
					ConfigurationAttributes.DO_SECOND_RESET_DEFAULT)) {

				// Since reset does not clear breakpoints, we do it
				// explicitly
				commandStr = ConfigurationAttributes.CLRBP_COMMAND;
				commandsList.add(commandStr);

				commandStr = ConfigurationAttributes.DO_SECOND_RESET_COMMAND;
				String resetType = CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.SECOND_RESET_TYPE,
						ConfigurationAttributes.SECOND_RESET_TYPE_DEFAULT)
						.trim();
				commandsList.add(commandStr + resetType);

				// Although the manual claims that reset always does a
				// halt, better issue it explicitly
				commandStr = ConfigurationAttributes.HALT_COMMAND;
				commandsList.add(commandStr);
			}
		}

		if (CDebugUtils.getAttribute(fAttributes,
				IGDBJtagConstants.ATTR_LOAD_IMAGE,
				IGDBJtagConstants.DEFAULT_LOAD_IMAGE)
				&& CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.DO_DEBUG_IN_RAM,
						ConfigurationAttributes.DO_DEBUG_IN_RAM_DEFAULT)) {

			IStatus status = addLoadImageCommands(commandsList);

			if (!status.isOK()) {
				return status;
			}
		}

		String userCmd = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.OTHER_RUN_COMMANDS,
				ConfigurationAttributes.OTHER_RUN_COMMANDS_DEFAULT).trim();

		userCmd = DebugUtils.resolveAll(userCmd, fAttributes);

		if (fDoDoubleBackslash && EclipseUtils.isWindows()) {
			userCmd = StringUtils.duplicateBackslashes(userCmd);
		}

		DebugUtils.addMultiLine(userCmd, commandsList);

		addSetPcCommands(commandsList);

		addStopAtCommands(commandsList);

		// Also add a command to see the registers in the
		// location where execution halted
		commandStr = ConfigurationAttributes.REGS_COMMAND;
		commandsList.add(commandStr);

		// Flush registers, GDB should read them again
		commandStr = ConfigurationAttributes.FLUSH_REGISTERS_COMMAND;
		commandsList.add(commandStr);

		if (CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.DO_CONTINUE,
				ConfigurationAttributes.DO_CONTINUE_DEFAULT)) {
			commandsList.add(ConfigurationAttributes.DO_CONTINUE_COMMAND);
		}

		return Status.OK_STATUS;
	}

	// ------------------------------------------------------------------------
}
