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

package org.eclipse.embedcdt.managedbuild.cross.riscv.core;

import org.eclipse.cdt.managedbuilder.core.IManagedOutputNameProvider;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class FlashImageManagedOutputNameProvider implements IManagedOutputNameProvider {

	// ------------------------------------------------------------------------

	@Override
	public IPath[] getOutputNames(ITool tool, IPath[] primaryInputNames) {

		String value = null;

		IOption option = tool.getOptionBySuperClassId(Option.OPTION_CREATEFLASH_CHOICE);
		if (option != null)
			value = (String) option.getValue();
		// System.out.println(value);

		String ext = "unknown";
		if (value != null) {
			if (value.endsWith("." + Option.CHOICE_IHEX))
				ext = "hex";
			else if (value.endsWith("." + Option.CHOICE_SREC))
				ext = "srec";
			else if (value.endsWith("." + Option.CHOICE_SYMBOLSREC))
				ext = "symbolsrec";
			else if (value.endsWith("." + Option.CHOICE_BINARY))
				ext = "bin";
		}

		IPath[] iPath = new IPath[1];
		iPath[0] = new Path("${BuildArtifactFileBaseName}." + ext);
		return iPath;
	}

	// ------------------------------------------------------------------------
}
