/*******************************************************************************
 * Copyright (c) 2014, 2020 Liviu Ionescu and others.
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
 *     Alexander Fedorov (ArSysOp) - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.packs.core.data;

import org.eclipse.embedcdt.packs.core.PacksConsoleStream;
import org.eclipse.embedcdt.packs.core.Utils;

public class DurationMonitor {

	private int fDepth;
	private PacksConsoleStream fOut;
	long fBeginTime;

	public DurationMonitor() {
		fDepth = 0;
		fOut = org.eclipse.embedcdt.packs.core.Activator.getInstance().getConsoleOutput();
	}

	public void displayTimeAndRun(Runnable runnable) {

		start();
		runnable.run();
		stop();
	}

	public void start() {
		fDepth++;
		fBeginTime = System.currentTimeMillis();

		if (fDepth == 1) {
			fOut.println();
			fOut.println(Utils.getCurrentDateTime());
		}
	}

	public void stop() {
		long endTime = System.currentTimeMillis();
		long duration = endTime - fBeginTime;
		if (duration == 0) {
			duration = 1;
		}
		fOut.print("Completed in ");
		fOut.println(duration + "ms.");

		fDepth--;
	}
}
