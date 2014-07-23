package ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

// toolbar:org.eclipse.debug.ui.main.toolbar?after=additions

public class ButtonResetHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("ButtonResetHandler.execute(" + event + ")");
		return null;
	}

}
