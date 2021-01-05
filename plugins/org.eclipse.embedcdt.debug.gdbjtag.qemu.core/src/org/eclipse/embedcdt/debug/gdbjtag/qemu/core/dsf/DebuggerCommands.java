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

package org.eclipse.embedcdt.debug.gdbjtag.qemu.core.dsf;

import java.util.List;

import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.core.StringUtils;
import org.eclipse.embedcdt.debug.gdbjtag.core.DebugUtils;
import org.eclipse.embedcdt.debug.gdbjtag.core.dsf.GnuMcuDebuggerCommandsService;
import org.eclipse.embedcdt.debug.gdbjtag.qemu.core.ConfigurationAttributes;
import org.eclipse.embedcdt.debug.gdbjtag.qemu.core.preferences.DefaultPreferences;
import org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.core.Activator;
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

		String otherInits = DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
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

		if (DebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_LOAD_IMAGE,
				IGDBJtagConstants.DEFAULT_LOAD_IMAGE)
				&& !DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_DEBUG_IN_RAM,
						fDefaultPreferences.getQemuDebugInRam())) {

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
				fDefaultPreferences.getQemuDoInitialReset())) {

			String commandStr = DefaultPreferences.DO_INITIAL_RESET_COMMAND;
			commandsList.add(commandStr);

			// Although the manual claims that reset always does a
			// halt, better issue it explicitly
			commandStr = DefaultPreferences.HALT_COMMAND;
			commandsList.add(commandStr);
		}

		String otherInits = DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.OTHER_INIT_COMMANDS,
				fDefaultPreferences.getQemuInitOther()).trim();

		otherInits = DebugUtils.resolveAll(otherInits, fAttributes);
		if (fDoDoubleBackslash && EclipseUtils.isWindows()) {
			otherInits = StringUtils.duplicateBackslashes(otherInits);
		}
		DebugUtils.addMultiLine(otherInits, commandsList);

		return Status.OK_STATUS;
	}

	@Override
	public IStatus addStartRestartCommands(boolean doReset, List<String> commandsList) {

		if (doReset) {
			if (DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_SECOND_RESET,
					fDefaultPreferences.getQemuDoPreRunReset())) {
				String commandStr = DefaultPreferences.DO_PRERUN_RESET_COMMAND;
				commandsList.add(commandStr);

				// Although the manual claims that reset always does a
				// halt, better issue it explicitly
				commandStr = DefaultPreferences.HALT_COMMAND;
				commandsList.add(commandStr);
			}
		}

		if (DebugUtils.getAttribute(fAttributes, IGDBJtagConstants.ATTR_LOAD_IMAGE,
				IGDBJtagConstants.DEFAULT_LOAD_IMAGE)
				&& DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.DO_DEBUG_IN_RAM,
						fDefaultPreferences.getQemuDebugInRam())) {

			IStatus status = addLoadImageCommands(commandsList);

			if (!status.isOK()) {
				return status;
			}
		}

		String userCmd = DebugUtils.getAttribute(fAttributes, ConfigurationAttributes.OTHER_RUN_COMMANDS,
				fDefaultPreferences.getQemuPreRunOther()).trim();

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
