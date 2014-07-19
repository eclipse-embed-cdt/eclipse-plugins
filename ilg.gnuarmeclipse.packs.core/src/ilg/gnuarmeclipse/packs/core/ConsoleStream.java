package ilg.gnuarmeclipse.packs.core;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleStream {

	public static String CONSOLE_NAME = "GNU ARM Eclipse Packs console";

	private static MessageConsoleStream sfConsoleOutput;

	public static MessageConsoleStream getConsoleOut() {

		if (sfConsoleOutput == null) {
			sfConsoleOutput = findConsole(CONSOLE_NAME).newMessageStream();
		}

		return sfConsoleOutput;
	}

	public static MessageConsole findConsole(String name) {

		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];

		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });

		return myConsole;
	}

}
