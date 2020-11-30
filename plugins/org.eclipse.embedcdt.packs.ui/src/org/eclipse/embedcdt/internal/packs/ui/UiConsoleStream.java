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
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.internal.packs.ui;

import org.eclipse.embedcdt.packs.core.IConsoleStream;
import org.eclipse.ui.console.MessageConsoleStream;

final class UiConsoleStream implements IConsoleStream {
	
	private final MessageConsoleStream console;

	UiConsoleStream(MessageConsoleStream console) {
		this.console = console;
	}

	@Override
	public void println(String message) {
		console.println(message);
	}

	@Override
	public void println() {
		console.println();
	}

	@Override
	public void print(String message) {
		console.print(message);
	}
}