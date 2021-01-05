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

package org.eclipse.embedcdt.debug.gdbjtag.core.dsf;

import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControl;
import org.eclipse.cdt.dsf.gdb.service.GdbDebugServicesFactory;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl_7_0;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl_7_2;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl_7_4;
// import org.eclipse.cdt.dsf.gdb.service.command.GDBControl_7_7;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.embedcdt.debug.gdbjtag.core.DebugUtils;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.IGdbServerBackendService;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.IGnuMcuDebuggerCommandsService;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.IPeripheralMemoryService;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.IPeripheralsService;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.PeripheralMemoryService;
import org.eclipse.embedcdt.debug.gdbjtag.core.services.PeripheralsService;
import org.eclipse.embedcdt.internal.debug.gdbjtag.core.Activator;

/**
 * Services factory intended to create the peripherals service.
 * <p>
 * To be used as parent class by actual implementations (J-Link and OpenOCD
 * factories).
 */
public abstract class GnuMcuServicesFactory extends GdbDebugServicesFactory {

	// ------------------------------------------------------------------------

	private final String fVersion;
	private String fMode;

	// ------------------------------------------------------------------------

	public GnuMcuServicesFactory(String version, String mode) {
		super(version, null);

		fVersion = version;
		fMode = mode;
	}

	// ------------------------------------------------------------------------

	@Override
	@SuppressWarnings("unchecked")
	public <V> V createService(Class<V> clazz, DsfSession session, Object... optionalArguments) {

		if (IPeripheralsService.class.isAssignableFrom(clazz)) {
			return (V) createPeripheralsService(session);
		} else if (IPeripheralMemoryService.class.isAssignableFrom(clazz)) {
			for (Object arg : optionalArguments) {
				if (arg instanceof ILaunchConfiguration) {
					return (V) createPeripheralMemoryService(session, (ILaunchConfiguration) arg);
				}
			}
		} else if (IGnuMcuDebuggerCommandsService.class.isAssignableFrom(clazz)) {
			for (Object arg : optionalArguments) {
				if (arg instanceof ILaunchConfiguration) {
					return (V) createDebuggerCommandsService(session, (ILaunchConfiguration) arg, fMode);
				}
			}
		} else if (IGdbServerBackendService.class.isAssignableFrom(clazz)) {
			for (Object arg : optionalArguments) {
				if (arg instanceof ILaunchConfiguration) {
					return (V) createGdbServerBackendService(session, (ILaunchConfiguration) arg);
				}
			}
		}
		return super.createService(clazz, session, optionalArguments);
	}

	// ------------------------------------------------------------------------

	protected abstract GnuMcuDebuggerCommandsService createDebuggerCommandsService(DsfSession session,
			ILaunchConfiguration lc, String mode);

	protected abstract GnuMcuGdbServerBackend createGdbServerBackendService(DsfSession session,
			ILaunchConfiguration lc);

	// ------------------------------------------------------------------------

	private PeripheralsService createPeripheralsService(DsfSession session) {
		return new PeripheralsService(session);
	}

	private PeripheralMemoryService createPeripheralMemoryService(DsfSession session,
			ILaunchConfiguration launchConfiguration) {
		return new PeripheralMemoryService(session, launchConfiguration);
	}

	@Override
	protected ICommandControl createCommandControl(DsfSession session, ILaunchConfiguration config) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"GnuMcuServicesFactory.createCommandControl(" + session + "," + config.getName() + ") " + this);
		}

		if (DebugUtils.compareVersions(GDB_7_4_VERSION, fVersion) <= 0) {
			return new GnuMcuControl_7_4(session, config, new GnuMcuCommandFactory(), fMode);
		}

		return super.createCommandControl(session, config);
	}

	@Override
	protected IProcesses createProcessesService(DsfSession session) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuMcuServicesFactory.createProcessesService(" + session + ") " + this);
		}

		if (DebugUtils.compareVersions(GDB_7_2_1_VERSION, fVersion) <= 0) {
			return new GnuMcuProcesses_7_2_1(session);
		}

		return super.createProcessesService(session);
	}

	// Not yet functional
	protected ICommandControl _createCommandControl(DsfSession session, ILaunchConfiguration config) {

		// TODO: keep it in sync with future versions

		// TODO: copy control locally, to make it work on 4.3 SR2
		// if (GDB_7_7_VERSION.compareTo(fVersion) <= 0) {
		// return new GDBControl_7_7(session, config,
		// new GnuMcuCommandFactory());
		// }
		if (DebugUtils.compareVersions(GDB_7_4_VERSION, fVersion) <= 0) {
			return new GDBControl_7_4(session, config, new GnuMcuCommandFactory());
		}
		if (DebugUtils.compareVersions(GDB_7_2_VERSION, fVersion) <= 0) {
			return new GDBControl_7_2(session, config, new GnuMcuCommandFactory());
		}
		if (DebugUtils.compareVersions(GDB_7_0_VERSION, fVersion) <= 0) {
			return new GDBControl_7_0(session, config, new GnuMcuCommandFactory());
		}

		return super.createCommandControl(session, config);
	}

	// ------------------------------------------------------------------------
}
