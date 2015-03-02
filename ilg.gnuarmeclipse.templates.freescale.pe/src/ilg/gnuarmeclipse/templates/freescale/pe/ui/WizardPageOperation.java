package ilg.gnuarmeclipse.templates.freescale.pe.ui;

import ilg.gnuarmeclipse.templates.freescale.pe.Activator;

public class WizardPageOperation implements Runnable {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (Activator.getInstance().isDebugging()) {
			System.out.println("WizardPageOperation.run()");
		}
	}

}
