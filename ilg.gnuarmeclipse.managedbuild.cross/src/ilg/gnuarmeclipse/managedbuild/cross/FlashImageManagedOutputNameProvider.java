/*******************************************************************************
 * Copyright (c) 2013 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross;

import org.eclipse.cdt.managedbuilder.core.IManagedOutputNameProvider;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

public class FlashImageManagedOutputNameProvider implements
		IManagedOutputNameProvider {

	public IPath[] getOutputNames(ITool tool, IPath[] primaryInputNames) {

		String value = null;

		IOption option = tool.getOptionBySuperClassId(Option.OPTION_PREFIX
				+ ".base.createflash.choice");
		if (option != null)
			value = (String) option.getValue();
		// System.out.println(value);

		String ext = "unknown";
		if (value != null) {
			if (value.endsWith(".choice.ihex"))
				ext = "hex";
			else if (value.endsWith(".choice.srec"))
				ext = "srec";
			else if (value.endsWith(".choice.symbolsrec"))
				ext = "symbolsrec";
			else if (value.endsWith(".choice.binary"))
				ext = "bin";
		}

		IPath[] iPath = new IPath[1];
		iPath[0] = new Path("${BuildArtifactFileBaseName}." + ext);
		return iPath;
	}

}
