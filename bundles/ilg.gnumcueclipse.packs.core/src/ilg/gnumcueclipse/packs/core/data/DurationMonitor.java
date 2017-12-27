/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.packs.core.data;

import org.eclipse.ui.console.MessageConsoleStream;

import ilg.gnumcueclipse.packs.core.ConsoleStream;
import ilg.gnumcueclipse.packs.core.Utils;

public class DurationMonitor {

	private int fDepth;
	private MessageConsoleStream fOut;
	long fBeginTime;

	public DurationMonitor() {

		fDepth = 0;
		fOut = ConsoleStream.getConsoleOut();
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
