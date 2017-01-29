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

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;

public interface IGnuArmDebuggerCommandsService {

	public void setAttributes(Map<String, Object> attributes);

	// ------------------------------------------------------------------------

	/**
	 * This step is part of the GROUP_TOP_LEVEL. Cannot use the IGDBJtagDevice
	 * object, it is not available at this moment.
	 * 
	 * @param attributes
	 * @param commandsList
	 * @return Status.OK_STATUS or an IStatus object if error.
	 */
	public IStatus addGdbInitCommandsCommands(List<String> commandsList);

	// ------------------------------------------------------------------------

	public IStatus addGnuArmSelectRemoteCommands(List<String> commandsList);

	public IStatus addGnuArmResetCommands(List<String> commandsList);

	public IStatus addGnuArmStartCommands(List<String> commandsList);

	public IStatus addGnuArmRestartCommands(List<String> commandsList);

	// ------------------------------------------------------------------------

	public IStatus addFirstResetCommands(List<String> commandsList);

	public IStatus addLoadSymbolsCommands(List<String> commandsList);

	public IStatus addLoadImageCommands(List<String> commandsList);

	/**
	 * Used by both FinalLaunchSequence & RestartProcessSequence.
	 * 
	 * @param attributes
	 * @param doReset
	 * @param programPath
	 * @param commandsList
	 * @return
	 */
	public IStatus addStartRestartCommands(boolean doReset, List<String> commandsList);

	public IStatus addSetPcCommands(List<String> commandsList);

	public IStatus addStopAtCommands(List<String> commandsList);

	// ------------------------------------------------------------------------
}
