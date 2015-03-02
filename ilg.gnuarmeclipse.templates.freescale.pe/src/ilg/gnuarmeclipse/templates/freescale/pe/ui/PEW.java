package ilg.gnuarmeclipse.templates.freescale.pe.ui;

import ilg.gnuarmeclipse.templates.freescale.pe.Activator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class PEW extends Wizard implements INewWizard {

	@Override
	public boolean performFinish() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PEW.performFinish()");
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("PEW.init()");
		}
	}

}
