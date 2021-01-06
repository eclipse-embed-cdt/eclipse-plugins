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
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.ManagedOptionValueHandler;
import org.eclipse.cdt.managedbuilder.internal.core.FolderInfo;
import org.eclipse.cdt.managedbuilder.internal.core.MultiFolderInfo;
import org.eclipse.cdt.managedbuilder.internal.core.ResourceConfiguration;
import org.eclipse.embedcdt.internal.managedbuild.cross.core.Activator;

@SuppressWarnings("restriction")
public class AdditionalToolsManagedOptionValueHandler extends ManagedOptionValueHandler {

	// ------------------------------------------------------------------------

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.cdt.managedbuilder.core.IManagedOptionValueHandler#
	 * handleValue (IConfiguration,IToolChain,IOption,String,int)
	 */
	@Override
	@SuppressWarnings("unused")
	public boolean handleValue(IBuildObject configuration, IHoldsOptions holder, IOption option, String extraArgument,
			int event) {

		// ManagedOptionValueHandlerDebug.dump(configuration, holder, option,
		// extraArgument, event);

		if (event == EVENT_APPLY) {
			if (configuration instanceof FolderInfo) {
				FolderInfo oFolderInfo;
				oFolderInfo = (FolderInfo) configuration;
				// oFolderInfo.setDirty(true); //does not update interface :-(
				// TODO: find the proper sequence to update the interface
				// System.out.println("should update FolderInfo");
				// PlatformUI.getWorkbench().getModalDialogShellProvider().getShell().redraw();

				return false; // should we return true?

			} else if (configuration instanceof ResourceConfiguration) {
				// TODO: find the proper sequence to update the interface
				// System.out
				// .println("should update ResourceConfiguration");

				return false; // should we return true?

			} else if (configuration instanceof MultiFolderInfo) {
				// TODO: find the proper sequence to update the interface
				// System.out
				// .println("should update MultiFolderInfo");

				return false; // should we return true?

			} else {
				Activator.log("unexpected instanceof configuration " + configuration.getClass().getCanonicalName());
			}

		}
		// The event was not handled, thus return false
		return false;
	}

	// ------------------------------------------------------------------------
}
