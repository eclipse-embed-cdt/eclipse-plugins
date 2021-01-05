/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
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

package org.eclipse.embedcdt.managedbuild.cross.core;

import org.eclipse.cdt.managedbuilder.core.IManagedCommandLineInfo;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedCommandLineGenerator;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedCommandLineInfo;

@SuppressWarnings("restriction")
public class CompilerCommandLineGenerator extends ManagedCommandLineGenerator {

	// ------------------------------------------------------------------------

	@Override
	public IManagedCommandLineInfo generateCommandLineInfo(ITool tool, String commandName, String[] flags,
			String outputFlag, String outputPrefix, String outputName, String[] inputResources,
			String commandLinePattern) {
		IManagedCommandLineInfo lineInfo;
		lineInfo = super.generateCommandLineInfo(tool, commandName, flags, outputFlag, outputPrefix, outputName,
				inputResources, commandLinePattern);

		String newCommandLine = lineInfo.getCommandLine();
		newCommandLine = updateMT(newCommandLine);
		String newFlags = lineInfo.getFlags();
		newFlags = updateMT(newFlags);

		return new ManagedCommandLineInfo(newCommandLine, lineInfo.getCommandLinePattern(), lineInfo.getCommandName(),
				newFlags, lineInfo.getOutputFlag(), lineInfo.getOutputPrefix(), lineInfo.getOutput(),
				lineInfo.getInputs());
	}

	private String updateMT(String s) {
		return s.replace("-MT\"$(@:%.o=%.d)\"", "-MT\"$@\"");
	}

	// ------------------------------------------------------------------------
}
