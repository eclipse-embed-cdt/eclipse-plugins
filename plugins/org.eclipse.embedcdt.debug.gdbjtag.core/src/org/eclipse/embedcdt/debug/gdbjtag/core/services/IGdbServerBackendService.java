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

package org.eclipse.embedcdt.debug.gdbjtag.core.services;

import org.eclipse.cdt.dsf.concurrent.Immutable;
import org.eclipse.cdt.dsf.mi.service.IMIBackend.State;
import org.eclipse.cdt.dsf.service.IDsfService;
import org.eclipse.core.runtime.IStatus;

public interface IGdbServerBackendService extends IDsfService {

	// ========================================================================

	/**
	 * Event indicating that the server back end process has started or
	 * terminated.
	 */
	@Immutable
	public static class ServerBackendStateChangedEvent {

		// --------------------------------------------------------------------

		final private String fSessionId;
		final private String fBackendId;
		final private State fState;

		// --------------------------------------------------------------------

		public ServerBackendStateChangedEvent(String sessionId, String backendId, State state) {
			fSessionId = sessionId;
			fBackendId = backendId;
			fState = state;
		}

		// --------------------------------------------------------------------

		public String getSessionId() {
			return fSessionId;
		}

		public String getBackendId() {
			return fBackendId;
		}

		public State getState() {
			return fState;
		}

		// --------------------------------------------------------------------
	}

	// ------------------------------------------------------------------------

	/**
	 * Returns the identifier of this backend service. It can be used to
	 * distinguish between multiple instances of this service in a single
	 * session.
	 */
	public String getId();

	public Process getServerProcess();

	public State getServerState();

	public int getServerExitCode();

	public IStatus getServerExitStatus();

	public void destroy();

	public String getServerCommandName();

	// ------------------------------------------------------------------------
}
