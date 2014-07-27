package ilg.gnuarmeclipse.packs.data.jobs;

import ilg.gnuarmeclipse.packs.core.ConsoleStream;
import ilg.gnuarmeclipse.packs.data.Utils;

import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;

public class BusyIndicatorWithDuration {

	private static MessageConsoleStream sfOut = ConsoleStream.getConsoleOut();

	public static void showWhile(String message, Runnable runnable) {

		long beginTime = System.currentTimeMillis();

		sfOut.println();
		sfOut.println(Utils.getCurrentDateTime());
		sfOut.println(message);

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
