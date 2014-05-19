package ilg.gnuarmeclipse.packs.handlers;

import ilg.gnuarmeclipse.packs.ui.perspectives.PacksPerspective;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

public class ShowPerspectiveHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		IPerspectiveDescriptor persDescription1 = PlatformUI.getWorkbench()
				.getPerspectiveRegistry()
				.findPerspectiveWithId(PacksPerspective.ID);

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.setPerspective(persDescription1);

		return null;
	}

}
