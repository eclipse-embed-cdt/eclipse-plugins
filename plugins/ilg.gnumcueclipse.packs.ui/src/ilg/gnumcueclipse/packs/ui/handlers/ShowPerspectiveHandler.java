/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnumcueclipse.packs.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

import ilg.gnumcueclipse.packs.ui.perspectives.PacksPerspective;

public class ShowPerspectiveHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IPerspectiveDescriptor persDescription1 = PlatformUI.getWorkbench().getPerspectiveRegistry()
				.findPerspectiveWithId(PacksPerspective.ID);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(persDescription1);

		return null;
	}

}
