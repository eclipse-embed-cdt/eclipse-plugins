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

package ilg.gnuarmeclipse.debug.gdbjtag.dsf;

import ilg.gnuarmeclipse.debug.gdbjtag.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IGdbServerBackendService;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IGnuArmDebuggerCommandsService;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IPeripheralMemoryService;
import ilg.gnuarmeclipse.debug.gdbjtag.services.IPeripheralsService;
import ilg.gnuarmeclipse.debug.gdbjtag.services.PeripheralMemoryService;
import ilg.gnuarmeclipse.debug.gdbjtag.services.PeripheralsService;

import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControl;
import org.eclipse.cdt.dsf.gdb.service.GdbDebugServicesFactory;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl_7_0;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl_7_2;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl_7_4;
// import org.eclipse.cdt.dsf.gdb.service.command.GDBControl_7_7;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Services factory intended to create the peripherals service.
 * <p>
 * To be used as parent class by actual implementations (J-Link and OpenOCD
 * factories).
 */
public abstract class GnuArmServicesFactory extends GdbDebugServicesFactory {

	// ------------------------------------------------------------------------

	private final String fVersion;
	private String fMode;

	// ------------------------------------------------------------------------

	public GnuArmServicesFactory(String version, String mode) {
		super(version);

		fVersion = version;
		fMode = mode;
	}

	// ------------------------------------------------------------------------

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
		} else if (IGnuArmDebuggerCommandsService.class.isAssignableFrom(clazz)) {
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

	protected abstract GnuArmDebuggerCommandsService createDebuggerCommandsService(DsfSession session,
			ILaunchConfiguration lc, String mode);

	protected abstract GnuArmGdbServerBackend createGdbServerBackendService(DsfSession session,
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
					"GnuArmServicesFactory.createCommandControl(" + session + "," + config.getName() + ") " + this);
		}

		if (DebugUtils.compareVersions(GDB_7_4_VERSION, fVersion) <= 0) {
			return new GnuArmControl_7_4(session, config, new GnuArmCommandFactory(), fMode);
		}

		return super.createCommandControl(session, config);
	}

	@Override
	protected IProcesses createProcessesService(DsfSession session) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("GnuArmServicesFactory.createProcessesService(" + session + ") " + this);
		}

		if (DebugUtils.compareVersions(GDB_7_2_1_VERSION, fVersion) <= 0) {
			return new GnuArmProcesses_7_2_1(session);
		}

		return super.createProcessesService(session);
	}

	// Not yet functional
	protected ICommandControl _createCommandControl(DsfSession session, ILaunchConfiguration config) {

		// TODO: keep it in sync with future versions

		// TODO: copy control locally, to make it work on 4.3 SR2
		// if (GDB_7_7_VERSION.compareTo(fVersion) <= 0) {
		// return new GDBControl_7_7(session, config,
		// new GnuArmCommandFactory());
		// }
		if (DebugUtils.compareVersions(GDB_7_4_VERSION, fVersion) <= 0) {
			return new GDBControl_7_4(session, config, new GnuArmCommandFactory());
		}
		if (DebugUtils.compareVersions(GDB_7_2_VERSION, fVersion) <= 0) {
			return new GDBControl_7_2(session, config, new GnuArmCommandFactory());
		}
		if (DebugUtils.compareVersions(GDB_7_0_VERSION, fVersion) <= 0) {
			return new GDBControl_7_0(session, config, new GnuArmCommandFactory());
		}

		return super.createCommandControl(session, config);
	}

	// ------------------------------------------------------------------------
}
