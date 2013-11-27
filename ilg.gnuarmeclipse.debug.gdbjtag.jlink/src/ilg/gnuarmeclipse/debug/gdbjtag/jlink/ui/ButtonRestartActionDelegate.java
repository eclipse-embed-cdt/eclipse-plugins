package ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public class ButtonRestartActionDelegate implements
		IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		System.out.println("Restart.run("+action+")");

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		System.out.println("Restart.selectionChanged("+action+","+selection+")");
	}

	@Override
	public void dispose() {
		System.out.println("Restart.dispose()");

	}

	@Override
	public void init(IWorkbenchWindow window) {
		System.out.println("Restart.init()");

	}

}
