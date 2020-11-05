/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
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

package org.eclipse.embedcdt.debug.gdbjtag.jlink.dsf;

import java.util.List;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.core.StringUtils;
import org.eclipse.embedcdt.debug.gdbjtag.DebugUtils;
import org.eclipse.embedcdt.debug.gdbjtag.dsf.GnuMcuDebuggerCommandsService;
import org.eclipse.embedcdt.debug.gdbjtag.jlink.Activator;
import org.eclipse.embedcdt.debug.gdbjtag.jlink.ConfigurationAttributes;
import org.eclipse.embedcdt.debug.gdbjtag.jlink.preferences.DefaultPreferences;
import org.osgi.framework.BundleContext;

public class DebuggerCommands extends GnuMcuDebuggerCommandsService {

	// ------------------------------------------------------------------------

	private DefaultPreferences fDefaultPreferences;

	// ------------------------------------------------------------------------

	public DebuggerCommands(DsfSession session, ILaunchConfiguration lc, String mode) {
		super(session, lc, mode, true); // do double backslash

		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
	}

	// ------------------------------------------------------------------------

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getInstance().getBundle().getBundleContext();
	}

	// ------------------------------------------------------------------------

	@Override
	public IStatus addGdbInitCommandsCommands(List<String> commandsList) {

		String otherInits = CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
				fDefaultPreferences.getGdbClientCommands()).trim();

		otherInits = DebugUtils.resolveAll(otherInits, fAttributes);
		DebugUtils.addMultiLine(otherInits, commandsList);

		return Status.OK_STATUS;
	}

	// ------------------------------------------------------------------------

	@Override
	public IStatus addGnuMcuResetCommands(List<String> commandsList) {

		IStatus status = addFirstResetCommands(commandsList);
		if (!status.isOK()) {
			return status;
		}

		status = addLoadSymbolsCommands(commandsList);

		if (!status.isOK()) {
			return status;
		}

		boolean doConnectToRunning = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.DO_CONNECT_TO_RUNNING, DefaultPreferences.DO_CONNECT_TO_RUNNING_DEFAULT);

		if (!doConnectToRunning) {
			if (CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_LOAD_IMAGE,
					IGDBJtagConstants.DEFAULT_LOAD_IMAGE)
					&& !CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_DEBUG_IN_RAM,
							fDefaultPreferences.getJLinkDebugInRam())) {

				status = addLoadImageCommands(commandsList);

				if (!status.isOK()) {
					return status;
				}
			}
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus addGnuMcuStartCommands(List<String> commandsList) {

		boolean doReset = !CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
				DefaultPreferences.DO_CONNECT_TO_RUNNING_DEFAULT);

		IStatus status = addStartRestartCommands(doReset, commandsList);

		if (!status.isOK()) {
			return status;
		}

		return Status.OK_STATUS;
	}

	@Override
	public IStatus addGnuMcuRestartCommands(List<String> commandsList) {

		return addStartRestartCommands(true, commandsList);
	}

	// ------------------------------------------------------------------------

	@Override
	public IStatus addFirstResetCommands(List<String> commandsList) {

		String attr;

		try {
			attr = String.valueOf(CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.FIRST_RESET_SPEED,
					fDefaultPreferences.getJLinkInitialResetSpeed()));
		} catch (Exception e) {
			attr = CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.FIRST_RESET_SPEED,
					String.valueOf(fDefaultPreferences.getJLinkInitialResetSpeed()));
		}
		if (!attr.isEmpty()) {
			commandsList.add(DefaultPreferences.INTERFACE_SPEED_FIXED_COMMAND + attr);
		}

		String commandStr;
		boolean noReset = CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
				DefaultPreferences.DO_CONNECT_TO_RUNNING_DEFAULT);
		if (!noReset) {
			if (CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_FIRST_RESET,
					fDefaultPreferences.getJLinkDoInitialReset())) {

				// Since reset does not clear breakpoints, we do it explicitly
				commandStr = DefaultPreferences.CLRBP_COMMAND;
				commandsList.add(commandStr);

				commandStr = DefaultPreferences.DO_FIRST_RESET_COMMAND;
				String resetType = CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.FIRST_RESET_TYPE,
						fDefaultPreferences.getJLinkInitialResetType());
				commandsList.add(commandStr + resetType);

				// Although the manual claims that reset always does a
				// halt, better issue it explicitly
				commandStr = DefaultPreferences.HALT_COMMAND;
				commandsList.add(commandStr);

				// Also add a command to see the registers in the
				// location where execution halted
				commandStr = DefaultPreferences.REGS_COMMAND;
				commandsList.add(commandStr);

				// Flush registers, GDB should read them again
				commandStr = DefaultPreferences.FLUSH_REGISTERS_COMMAND;
				commandsList.add(commandStr);
			}
		}

		attr = CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.INTERFACE_SPEED,
				DefaultPreferences.INTERFACE_SPEED_AUTO);
		if (DefaultPreferences.INTERFACE_SPEED_AUTO.equals(attr)) {
			commandsList.add(DefaultPreferences.INTERFACE_SPEED_AUTO_COMMAND);
		} else if (DefaultPreferences.INTERFACE_SPEED_ADAPTIVE.equals(attr)) {
			commandsList.add(DefaultPreferences.INTERFACE_SPEED_ADAPTIVE_COMMAND);
		} else {
			commandsList.add(DefaultPreferences.INTERFACE_SPEED_FIXED_COMMAND + attr);
		}

		commandStr = DefaultPreferences.ENABLE_FLASH_BREAKPOINTS_COMMAND;
		if (CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.ENABLE_FLASH_BREAKPOINTS,
				fDefaultPreferences.getJLinkEnableFlashBreakpoints()))
			commandStr += "1";
		else
			commandStr += "0";
		commandsList.add(commandStr);

		if (CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.ENABLE_SEMIHOSTING,
				fDefaultPreferences.getJLinkEnableSemihosting())) {
			commandStr = DefaultPreferences.ENABLE_SEMIHOSTING_COMMAND;
			commandsList.add(commandStr);

			int ioclientMask = 0;
			if (CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_TELNET,
					fDefaultPreferences.getJLinkSemihostingTelnet())) {
				ioclientMask |= DefaultPreferences.ENABLE_SEMIHOSTING_IOCLIENT_TELNET_MASK;
			}
			if (CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT,
					fDefaultPreferences.getJLinkSemihostingClient())) {
				ioclientMask |= DefaultPreferences.ENABLE_SEMIHOSTING_IOCLIENT_GDBCLIENT_MASK;
			}

			commandStr = DefaultPreferences.ENABLE_SEMIHOSTING_IOCLIENT_COMMAND + String.valueOf(ioclientMask);
			commandsList.add(commandStr);
		}

		attr = CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.GDB_SERVER_DEBUG_INTERFACE,
				DefaultPreferences.INTERFACE_SWD);
		if (DefaultPreferences.INTERFACE_SWD.equals(attr)) {

			if (CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.ENABLE_SWO,
					fDefaultPreferences.getJLinkEnableSwo())) {

				commandsList.add(DefaultPreferences.DISABLE_SWO_COMMAND);

				commandStr = DefaultPreferences.ENABLE_SWO_COMMAND;
				commandStr += CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.SWO_ENABLETARGET_CPUFREQ,
						fDefaultPreferences.getJLinkSwoEnableTargetCpuFreq());
				commandStr += " ";
				commandStr += CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.SWO_ENABLETARGET_SWOFREQ,
						fDefaultPreferences.getJLinkSwoEnableTargetSwoFreq());
				commandStr += " ";
				commandStr += CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.SWO_ENABLETARGET_PORTMASK,
						fDefaultPreferences.getJLinkSwoEnableTargetPortMask());
				commandStr += " 0";

				commandsList.add(commandStr);
			}
		}

		String otherInits = CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.OTHER_INIT_COMMANDS,
				fDefaultPreferences.getJLinkInitOther()).trim();

		otherInits = DebugUtils.resolveAll(otherInits, fAttributes);
		if (fDoDoubleBackslash && EclipseUtils.isWindows()) {
			otherInits = StringUtils.duplicateBackslashes(otherInits);
		}
		DebugUtils.addMultiLine(otherInits, commandsList);

		return Status.OK_STATUS;
	}

	@Override
	public IStatus addStartRestartCommands(boolean doReset, List<String> commandsList) {

		String commandStr;

		if (doReset) {
			if (CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_SECOND_RESET,
					fDefaultPreferences.getJLinkDoPreRunReset())) {

				// Since reset does not clear breakpoints, we do it
				// explicitly
				commandStr = DefaultPreferences.CLRBP_COMMAND;
				commandsList.add(commandStr);

				commandStr = DefaultPreferences.DO_SECOND_RESET_COMMAND;
				String resetType = CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.SECOND_RESET_TYPE,
						fDefaultPreferences.getJLinkPreRunResetType()).trim();
				commandsList.add(commandStr + resetType);

				// Although the manual claims that reset always does a
				// halt, better issue it explicitly
				commandStr = DefaultPreferences.HALT_COMMAND;
				commandsList.add(commandStr);
			}
		}

		if (CDebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_LOAD_IMAGE,
				IGDBJtagConstants.DEFAULT_LOAD_IMAGE)
				&& CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_DEBUG_IN_RAM,
						fDefaultPreferences.getJLinkDebugInRam())) {

			IStatus status = addLoadImageCommands(commandsList);

			if (!status.isOK()) {
				return status;
			}
		}

		String userCmd = CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.OTHER_RUN_COMMANDS,
				fDefaultPreferences.getJLinkPreRunOther()).trim();

		userCmd = DebugUtils.resolveAll(userCmd, fAttributes);

		if (fDoDoubleBackslash && EclipseUtils.isWindows()) {
			userCmd = StringUtils.duplicateBackslashes(userCmd);
		}

		DebugUtils.addMultiLine(userCmd, commandsList);

		addSetPcCommands(commandsList);

		addStopAtCommands(commandsList);

		// Also add a command to see the registers in the
		// location where execution halted
		commandStr = DefaultPreferences.REGS_COMMAND;
		commandsList.add(commandStr);

		// Flush registers, GDB should read them again
		commandStr = DefaultPreferences.FLUSH_REGISTERS_COMMAND;
		commandsList.add(commandStr);

		if (CDebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_CONTINUE,
				DefaultPreferences.DO_CONTINUE_DEFAULT)) {
			commandsList.add(DefaultPreferences.DO_CONTINUE_COMMAND);
		}

		return Status.OK_STATUS;
	}

	// ------------------------------------------------------------------------
}
