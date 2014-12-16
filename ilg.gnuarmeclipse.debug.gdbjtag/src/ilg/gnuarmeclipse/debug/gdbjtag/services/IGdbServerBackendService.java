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

package ilg.gnuarmeclipse.debug.gdbjtag.services;

import org.eclipse.cdt.dsf.mi.service.IMIBackend.State;
import org.eclipse.cdt.dsf.service.IDsfService;
import org.eclipse.core.runtime.IStatus;

public interface IGdbServerBackendService extends IDsfService {

	// ------------------------------------------------------------------------

	public Process getServerProcess();

	public State getServerState();

	public int getServerExitCode();

	public IStatus getServerExitStatus();

	public void destroy();

	// ------------------------------------------------------------------------
}
