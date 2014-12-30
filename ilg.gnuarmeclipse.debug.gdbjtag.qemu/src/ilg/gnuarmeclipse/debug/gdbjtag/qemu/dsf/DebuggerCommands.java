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

package ilg.gnuarmeclipse.debug.gdbjtag.qemu.dsf;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.core.StringUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.dsf.GnuArmDebuggerCommandsService;
import ilg.gnuarmeclipse.debug.gdbjtag.qemu.ConfigurationAttributes;

import java.util.List;
import java.util.Map;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.IPath;
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
	public IStatus addGdbInitCommandsCommands(Map<String, Object> attributes,
			List<String> commandsList) {

		String otherInits = CDebugUtils.getAttribute(attributes,
				ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
				ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS_DEFAULT)
				.trim();

		otherInits = DebugUtils.resolveAll(otherInits, attributes);
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

		status = addLoadSymbolsCommands(fAttributes,
				fGDBBackend.getProgramPath(), commandsList);

		if (!status.isOK()) {
			return status;
		}

		if (CDebugUtils.getAttribute(fAttributes,
				IGDBJtagConstants.ATTR_LOAD_IMAGE,
				IGDBJtagConstants.DEFAULT_LOAD_IMAGE)
				&& !CDebugUtils.getAttribute(fAttributes,
						ConfigurationAttributes.DO_DEBUG_IN_RAM,
						ConfigurationAttributes.DO_DEBUG_IN_RAM_DEFAULT)) {

			status = addLoadImageCommands(fAttributes,
					fGDBBackend.getProgramPath(), commandsList);

			if (!status.isOK()) {
				return status;
			}
		}

		return Status.OK_STATUS;
	}

	@Override
	public IStatus addGnuArmStartCommands(List<String> commandsList) {

		IStatus status = addStartRestartCommands(fAttributes, true,
				fGDBBackend.getProgramPath(), commandsList);

		if (!status.isOK()) {
			return status;
		}

		return Status.OK_STATUS;
	}

	// ------------------------------------------------------------------------

	@Override
	public IStatus addFirstResetCommands(List<String> commandsList) {

		if (CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.DO_FIRST_RESET,
				ConfigurationAttributes.DO_FIRST_RESET_DEFAULT)) {

			String commandStr = ConfigurationAttributes.DO_FIRST_RESET_COMMAND;
			commandsList.add(commandStr);

			// Although the manual claims that reset always does a
			// halt, better issue it explicitly
			commandStr = ConfigurationAttributes.HALT_COMMAND;
			commandsList.add(commandStr);
		}

		String otherInits = CDebugUtils.getAttribute(fAttributes,
				ConfigurationAttributes.OTHER_INIT_COMMANDS,
				ConfigurationAttributes.OTHER_INIT_COMMANDS_DEFAULT).trim();

		otherInits = DebugUtils.resolveAll(otherInits, fAttributes);
		if (EclipseUtils.isWindows()) {
			otherInits = StringUtils.duplicateBackslashes(otherInits);
		}
		DebugUtils.addMultiLine(otherInits, commandsList);

		return Status.OK_STATUS;
	}

	@Override
	public IStatus addStartRestartCommands(Map<String, Object> attributes,
			boolean doReset, IPath programPath, List<String> commandsList) {

		if (doReset) {
			if (CDebugUtils.getAttribute(attributes,
					ConfigurationAttributes.DO_SECOND_RESET,
					ConfigurationAttributes.DO_SECOND_RESET_DEFAULT)) {
				String commandStr = ConfigurationAttributes.DO_SECOND_RESET_COMMAND;
				commandsList.add(commandStr);

				// Although the manual claims that reset always does a
				// halt, better issue it explicitly
				commandStr = ConfigurationAttributes.HALT_COMMAND;
				commandsList.add(commandStr);
			}
		}

		if (CDebugUtils.getAttribute(attributes,
				IGDBJtagConstants.ATTR_LOAD_IMAGE,
				IGDBJtagConstants.DEFAULT_LOAD_IMAGE)
				&& CDebugUtils.getAttribute(attributes,
						ConfigurationAttributes.DO_DEBUG_IN_RAM,
						ConfigurationAttributes.DO_DEBUG_IN_RAM_DEFAULT)) {

			IStatus status = addLoadImageCommands(attributes, programPath,
					commandsList);

			if (!status.isOK()) {
				return status;
			}
		}

		String userCmd = CDebugUtils.getAttribute(attributes,
				ConfigurationAttributes.OTHER_RUN_COMMANDS,
				ConfigurationAttributes.OTHER_RUN_COMMANDS_DEFAULT).trim();

		userCmd = DebugUtils.resolveAll(userCmd, attributes);

		if (fDoDoubleBackslash && EclipseUtils.isWindows()) {
			userCmd = StringUtils.duplicateBackslashes(userCmd);
		}

		DebugUtils.addMultiLine(userCmd, commandsList);

		addSetPcCommands(commandsList);

		addStopAtCommands(commandsList);

		// commandsList.add("monitor reg");

		if (CDebugUtils.getAttribute(attributes,
				ConfigurationAttributes.DO_CONTINUE,
				ConfigurationAttributes.DO_CONTINUE_DEFAULT)) {
			commandsList.add(ConfigurationAttributes.DO_CONTINUE_COMMAND);
		}

		return Status.OK_STATUS;
	}

	// ------------------------------------------------------------------------
}
