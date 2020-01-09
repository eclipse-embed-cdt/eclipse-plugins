/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
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

package ilg.gnumcueclipse.debug.gdbjtag.pyocd;

import java.util.Collection;

import org.eclipse.cdt.debug.gdbjtag.core.jtagdevice.DefaultGDBJtagDeviceImpl;

public class JTagDevice extends DefaultGDBJtagDeviceImpl {

	@Override
	public void doDelay(int delay, Collection<String> commands) {
	}

	@Override
	public void doHalt(Collection<String> commands) {
	}

	@Override
	public void doReset(Collection<String> commands) {
	}

	@Override
	public void doStopAt(String stopAt, Collection<String> commands) {
		super.doStopAt(stopAt, commands);
	}

}
