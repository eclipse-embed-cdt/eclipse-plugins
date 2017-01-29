/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.ui.handlers;

import ilg.gnuarmeclipse.packs.ui.perspectives.PacksPerspective;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

public class ShowPerspectiveHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IPerspectiveDescriptor persDescription1 = PlatformUI.getWorkbench().getPerspectiveRegistry()
				.findPerspectiveWithId(PacksPerspective.ID);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().setPerspective(persDescription1);

		return null;
	}

}
