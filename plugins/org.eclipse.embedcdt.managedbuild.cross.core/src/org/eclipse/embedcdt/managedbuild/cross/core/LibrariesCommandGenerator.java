/*******************************************************************************
 * Copyright (c) 2023 John Dallaway and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    John Dallaway - initial implementation (#608)
 *    Liviu Ionescu - use single `-Wl,` string
 *******************************************************************************/
package org.eclipse.embedcdt.managedbuild.cross.core;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.cdt.core.cdtvariables.CdtVariableException;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionCommandGenerator;
import org.eclipse.cdt.utils.cdtvariables.CdtVariableResolver;
import org.eclipse.cdt.utils.cdtvariables.IVariableSubstitutor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * An option command generator to group libraries on the GNU linker command line
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @since 8.6
 */
public class LibrariesCommandGenerator implements IOptionCommandGenerator {

	private static final String GROUP_LIBRARIES_COMMAND_FORMAT = "-Wl,--start-group,%s,--end-group"; //$NON-NLS-1$

	private final String fGroupLibrariesOptionId;

	/**
	 * @param groupLibrariesOptionId the ID of the IOption controlling library grouping
	 */
	protected LibrariesCommandGenerator(String groupLibrariesOptionId) {
		fGroupLibrariesOptionId = groupLibrariesOptionId;
	}

	@Override
	public String generateCommand(IOption option, IVariableSubstitutor macroSubstitutor) {
		IOption groupOption = option.getOptionHolder().getOptionBySuperClassId(fGroupLibrariesOptionId);
		try {
			if ((groupOption != null) && groupOption.getBooleanValue()) { // if library grouping enabled
				String command = option.getCommand();
				String libraries = Arrays.stream(option.getLibraries()).map(lib -> command + lib)
						.collect(Collectors.joining(",")); //$NON-NLS-1$
				if (!libraries.isEmpty()) {
					libraries = CdtVariableResolver.resolveToString(libraries, macroSubstitutor);
					return String.format(GROUP_LIBRARIES_COMMAND_FORMAT, libraries);
				}
			}
		} catch (BuildException | CdtVariableException e) {
			Platform.getLog(getClass()).log(Status.error("Error generating libraries command", e)); //$NON-NLS-1$
		}
		return null; // fallback to default command generator
	}

}
