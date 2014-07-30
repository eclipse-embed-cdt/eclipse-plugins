package ilg.gnuarmeclipse.packs.data.jobs;

import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.data.Utils;

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * Support for showing a Busy Cursor during a long running process.
 * <p>
 */
public class BusyIndicatorWithDuration {

	private static MessageConsoleStream sfOut = ConsoleStream.getConsoleOut();

	/**
	 * Display a time stamp, run the code and display the duration, in ms.
	 * 
	 * @param message
	 *            a string, or null.
	 * @param runnable
	 *            a class that implements run().
	 */
	public static void showWhile(Runnable runnable) {

		long beginTime = System.currentTimeMillis();

		sfOut.println();
		sfOut.println(Utils.getCurrentDateTime());

		BusyIndicator.showWhile(Display.getDefault(), runnable);

		long endTime = System.currentTimeMillis();
		long duration = endTime - beginTime;
		if (duration == 0) {
			duration = 1;
		}
		sfOut.print("Completed in ");
		sfOut.println(duration + "ms.");

	}
}
