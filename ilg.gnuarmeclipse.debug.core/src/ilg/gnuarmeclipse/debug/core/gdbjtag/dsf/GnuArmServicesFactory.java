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

package ilg.gnuarmeclipse.debug.core.gdbjtag.dsf;

import ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel.IPeripheralsService;
import ilg.gnuarmeclipse.debug.core.gdbjtag.datamodel.PeripheralsService;

import org.eclipse.cdt.dsf.gdb.service.GdbDebugServicesFactory;
import org.eclipse.cdt.dsf.service.DsfSession;

/**
 * Services factory intended to create the peripherals service.
 * <p>
 * To be used as parent class by actual implementations (J-Link and OpenOCD
 * factories).
 */
public class GnuArmServicesFactory extends GdbDebugServicesFactory {

	public GnuArmServicesFactory(String version) {
		super(version);
	}

	@SuppressWarnings("unchecked")
	public <V> V createService(Class<V> clazz, DsfSession session,
			Object... optionalArguments) {

		if (IPeripheralsService.class.isAssignableFrom(clazz))
			return (V) createPeripheralsService(session);

		return super.createService(clazz, session, optionalArguments);
	}

	private PeripheralsService createPeripheralsService(DsfSession session) {
		return new PeripheralsService(session);
	}
}
