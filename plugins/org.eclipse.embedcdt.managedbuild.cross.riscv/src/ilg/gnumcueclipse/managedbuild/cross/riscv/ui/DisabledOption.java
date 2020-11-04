/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
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

package ilg.gnumcueclipse.managedbuild.cross.riscv.ui;

import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IOptionApplicability;

public class DisabledOption implements IOptionApplicability {

	@Override
	public boolean isOptionUsedInCommandLine(IBuildObject configuration, IHoldsOptions holder, IOption option) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOptionVisible(IBuildObject configuration, IHoldsOptions holder, IOption option) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isOptionEnabled(IBuildObject configuration, IHoldsOptions holder, IOption option) {
		// TODO Auto-generated method stub
		return false;
	}

}
