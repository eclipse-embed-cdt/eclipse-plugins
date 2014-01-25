package ilg.gnuarmeclipse.templates.freescale.pe.ui;

import org.eclipse.jface.wizard.Wizard;

public class PEW extends Wizard {

	@Override
	public boolean performFinish() {

		System.out.println("PEW.performFinish()");
		return true;
	}

}
