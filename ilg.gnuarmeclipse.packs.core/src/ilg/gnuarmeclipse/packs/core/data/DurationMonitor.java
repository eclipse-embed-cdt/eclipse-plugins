package ilg.gnuarmeclipse.packs.core.data;

import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.core.Utils;

import org.eclipse.ui.console.MessageConsoleStream;

public class DurationMonitor {

	private int fDepth;
	private MessageConsoleStream fOut;

	public DurationMonitor() {

		fDepth = 0;
		fOut = ConsoleStream.getConsoleOut();
	}

	public void displayTimeAndRun(Runnable runnable) {

		fDepth++;
		long beginTime = System.currentTimeMillis();

		if (fDepth == 1) {
			fOut.println();
			fOut.println(Utils.getCurrentDateTime());
		}

		runnable.run();

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}
		fOut.print("Completed in ");
		fOut.println(duration + "ms.");

		fDepth--;

	}

}
