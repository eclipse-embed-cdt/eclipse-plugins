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

import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.core.IOption;

public class ClearProviderManagedOptionValueHandler implements IManagedOptionValueHandler {

	// ------------------------------------------------------------------------

	@Override
	public boolean handleValue(IBuildObject configuration, IHoldsOptions holder, IOption option, String extraArgument,
			int event) {

		// ManagedOptionValueHandlerDebug.dump(configuration, holder, option,
		// extraArgument, event);

		if (event == EVENT_APPLY) {

			// SpecsProvider.clear();

			// the event was handled
			return true;
		}

		return false;
	}

	@Override
	public boolean isDefaultValue(IBuildObject configuration, IHoldsOptions holder, IOption option,
			String extraArgument) {

		// no explicit default values
		return false;
	}

	@Override
	public boolean isEnumValueAppropriate(IBuildObject configuration, IHoldsOptions holder, IOption option,
			String extraArgument, String enumValue) {

		// no enum values
		return false;
	}

	// ------------------------------------------------------------------------
}
