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

import org.eclipse.cdt.managedbuilder.core.IBuildObject;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IHoldsOptions;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IResourceConfiguration;
import org.eclipse.cdt.managedbuilder.core.ITool;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedOptionValueHandler;

public class ManagedOptionValueHandlerDebug {

	// ------------------------------------------------------------------------

	public static void dump(IBuildObject configuration, IHoldsOptions holder, IOption option, String extraArgument,
			int event) {

		String configLabel = "config"; //$NON-NLS-1$
		String holderLabel = "holder"; //$NON-NLS-1$
		String eventLabel = "event"; //$NON-NLS-1$

		if (configuration instanceof IConfiguration) {
			configLabel = "IConfiguration"; //$NON-NLS-1$
		} else if (configuration instanceof IResourceConfiguration) {
			configLabel = "IResourceConfiguration"; //$NON-NLS-1$
		}

		if (holder instanceof IToolChain) {
			holderLabel = "IToolChain"; //$NON-NLS-1$
		} else if (holder instanceof ITool) {
			holderLabel = "ITool"; //$NON-NLS-1$
		}

		switch (event) {
		case ManagedOptionValueHandler.EVENT_OPEN:
			eventLabel = "EVENT_OPEN"; //$NON-NLS-1$
			break;
		case ManagedOptionValueHandler.EVENT_APPLY:
			eventLabel = "EVENT_APPLY"; //$NON-NLS-1$
			break;
		case ManagedOptionValueHandler.EVENT_SETDEFAULT:
			eventLabel = "EVENT_SETDEFAULT"; //$NON-NLS-1$
			break;
		case ManagedOptionValueHandler.EVENT_CLOSE:
			eventLabel = "EVENT_CLOSE"; //$NON-NLS-1$
			break;
		}

		// Print the event
		if (Activator.getInstance().isDebugging()) {
			System.out.println(eventLabel + "(" + //$NON-NLS-1$
					configLabel + " = " + //$NON-NLS-1$
					configuration.getId() + ", " + //$NON-NLS-1$
					holderLabel + " = " + //$NON-NLS-1$
					holder.getId() + ", " + //$NON-NLS-1$
					"IOption = " + //$NON-NLS-1$
					option.getId() + ", " + //$NON-NLS-1$
					"String = " + //$NON-NLS-1$
					extraArgument + ")"); //$NON-NLS-1$
		}
	}

	// ------------------------------------------------------------------------
}
