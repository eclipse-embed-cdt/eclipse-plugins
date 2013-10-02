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

import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionApplicability;
import org.eclipse.cdt.managedbuilder.core.IToolChain;

public class ARMOptionApplicability implements IOptionApplicability {

	@Override
	public boolean isOptionUsedInCommandLine(IBuildObject configuration,
			IHoldsOptions holder, IOption option) {

		return isOption(configuration, holder, option);
	}

	@Override
	public boolean isOptionVisible(IBuildObject configuration,
			IHoldsOptions holder, IOption option) {

		return isOption(configuration, holder, option);
	}

	@Override
	public boolean isOptionEnabled(IBuildObject configuration,
			IHoldsOptions holder, IOption option) {

		return isOption(configuration, holder, option);
	}

	private boolean isOption(IBuildObject configuration, IHoldsOptions holder,
			IOption option) {

		IToolChain toolchain = (IToolChain) holder;
		String sFamilyId = Activator.getOptionPrefix() + ".family";
		IOption checkedOption = toolchain.getOptionBySuperClassId(sFamilyId); //$NON-NLS-1$
		if (checkedOption != null) {
			String sValue;
			try {
				sValue = checkedOption.getStringValue();
				if (sValue.endsWith(".arm"))
					return true;
			} catch (BuildException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return false;
	}
}
