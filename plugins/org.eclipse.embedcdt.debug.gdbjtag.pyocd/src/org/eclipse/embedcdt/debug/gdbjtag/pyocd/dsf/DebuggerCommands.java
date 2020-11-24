/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * Copyright (c) 2015-2016 Chris Reed.
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
 *     Chris Reed - pyOCD changes
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.pyocd.dsf;

import java.util.List;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.core.StringUtils;
import org.eclipse.embedcdt.debug.gdbjtag.core.DebugUtils;
import org.eclipse.embedcdt.debug.gdbjtag.core.dsf.GnuMcuDebuggerCommandsService;
import org.eclipse.embedcdt.debug.gdbjtag.pyocd.Activator;
import org.eclipse.embedcdt.debug.gdbjtag.pyocd.ConfigurationAttributes;
import org.eclipse.embedcdt.debug.gdbjtag.pyocd.preferences.DefaultPreferences;
import org.osgi.framework.BundleContext;

public class DebuggerCommands extends GnuMcuDebuggerCommandsService {

	// ------------------------------------------------------------------------

	public DebuggerCommands(DsfSession session, ILaunchConfiguration lc, String mode) {
		super(session, lc, mode, true); // do double backslash
	}

	// ------------------------------------------------------------------------

	@Override
	protected BundleContext getBundleContext() {
		return Activator.getInstance().getBundle().getBundleContext();
	}

	// ------------------------------------------------------------------------

	@Override
	public IStatus addGdbInitCommandsCommands(List<String> commandsList) {

		String otherInits = DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
				DefaultPreferences.GDB_CLIENT_OTHER_COMMANDS_DEFAULT).trim();

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

		if (DebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_LOAD_IMAGE,
				IGDBJtagConstants.DEFAULT_LOAD_IMAGE)
				&& !DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_DEBUG_IN_RAM,
						DefaultPreferences.DO_DEBUG_IN_RAM_DEFAULT)) {

			status = addLoadImageCommands(commandsList);

			if (!status.isOK()) {
				return status;
			}
		}

		return Status.OK_STATUS;
	}

	@Override
	public IStatus addGnuMcuStartCommands(List<String> commandsList) {

		IStatus status = addStartRestartCommands(true, commandsList);

		if (!status.isOK()) {
			return status;
		}

		return Status.OK_STATUS;
	}

	// ------------------------------------------------------------------------

	@Override
	public IStatus addFirstResetCommands(List<String> commandsList) {

		if (DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_FIRST_RESET,
				DefaultPreferences.DO_FIRST_RESET_DEFAULT)) {

			String commandStr = DefaultPreferences.DO_FIRST_RESET_COMMAND;
			String resetType = DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.FIRST_RESET_TYPE,
					DefaultPreferences.FIRST_RESET_TYPE_DEFAULT);
			commandsList.add(commandStr + resetType);

			// Although the manual claims that reset always does a
			// halt, better issue it explicitly
			commandStr = DefaultPreferences.HALT_COMMAND;
			commandsList.add(commandStr);
		}

		String otherInits = DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.OTHER_INIT_COMMANDS,
				DefaultPreferences.OTHER_INIT_COMMANDS_DEFAULT).trim();

		otherInits = DebugUtils.resolveAll(otherInits, fAttributes);
		if (fDoDoubleBackslash && EclipseUtils.isWindows()) {
			otherInits = StringUtils.duplicateBackslashes(otherInits);
		}
		DebugUtils.addMultiLine(otherInits, commandsList);

		if (DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.ENABLE_SEMIHOSTING,
				DefaultPreferences.ENABLE_SEMIHOSTING_DEFAULT)) {
			String commandStr = DefaultPreferences.ENABLE_SEMIHOSTING_COMMAND;
			commandsList.add(commandStr);
		}

		return Status.OK_STATUS;
	}

	@Override
	public IStatus addStartRestartCommands(boolean doReset, List<String> commandsList) {

		if (doReset) {
			if (DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_SECOND_RESET,
					DefaultPreferences.DO_SECOND_RESET_DEFAULT)) {
				String commandStr = DefaultPreferences.DO_SECOND_RESET_COMMAND;
				String resetType = DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.SECOND_RESET_TYPE,
						DefaultPreferences.SECOND_RESET_TYPE_DEFAULT);
				commandsList.add(commandStr + resetType);

				// Although the manual claims that reset always does a
				// halt, better issue it explicitly
				commandStr = DefaultPreferences.HALT_COMMAND;
				commandsList.add(commandStr);
			}
		}

		if (DebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_LOAD_IMAGE,
				IGDBJtagConstants.DEFAULT_LOAD_IMAGE)
				&& DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_DEBUG_IN_RAM,
						DefaultPreferences.DO_DEBUG_IN_RAM_DEFAULT)) {

			IStatus status = addLoadImageCommands(commandsList);

			if (!status.isOK()) {
				return status;
			}
		}

		String userCmd = DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.OTHER_RUN_COMMANDS,
				DefaultPreferences.OTHER_RUN_COMMANDS_DEFAULT).trim();

		userCmd = DebugUtils.resolveAll(userCmd, fAttributes);

		if (fDoDoubleBackslash && EclipseUtils.isWindows()) {
			userCmd = StringUtils.duplicateBackslashes(userCmd);
		}

		DebugUtils.addMultiLine(userCmd, commandsList);

		addSetPcCommands(commandsList);

		addStopAtCommands(commandsList);

		// commandsList.add("monitor reg");

		if (DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_CONTINUE,
				DefaultPreferences.DO_CONTINUE_DEFAULT)) {
			commandsList.add(DefaultPreferences.DO_CONTINUE_COMMAND);
		}

		return Status.OK_STATUS;
	}

	// ------------------------------------------------------------------------
}
