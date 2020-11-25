/*******************************************************************************
 * Copyright (c) 2020 ArSysOp and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Alexander Fedorov (ArSysOp) - initial API and implementation.
 *******************************************************************************/
package org.eclipse.embedcdt.internal.packs.ui;

import org.eclipse.embedcdt.packs.core.PacksConsoleStream;
import org.eclipse.embedcdt.packs.core.PacksConsoles;
import org.eclipse.embedcdt.packs.ui.ConsoleStream;
import org.osgi.service.component.annotations.Component;

@Component
public final class MessageConsoles implements PacksConsoles {
	
	private PacksConsoleStream stream;

	@Override
	public PacksConsoleStream output() {
		if (stream == null) {
			stream = new UiConsoleStream(ConsoleStream.getConsoleOut());
		}
		return stream;
	}

}
