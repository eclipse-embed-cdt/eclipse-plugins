package ilg.gnuarmeclipse.templates.freescale.pe.processes;

import org.eclipse.cdt.core.templateengine.TemplateCore;
import org.eclipse.cdt.core.templateengine.process.ProcessArgument;
import org.eclipse.cdt.core.templateengine.process.ProcessFailureException;
import org.eclipse.cdt.core.templateengine.process.ProcessRunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class ProcessorExpertWizard extends ProcessRunner {

	class PEThread implements Runnable {

		// Does not work :-(
		// The Refresh workspace deadlocks
		@Override
		public void run() {
			
			try {
				Thread.sleep(3*1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			//String id = "ilg.peww";
			String id = "com.processorexpert.ui.pewizard.newprjwizard";

			IWizardDescriptor descriptor = PlatformUI.getWorkbench()
					.getNewWizardRegistry().findWizard(id);

			try {
				// Then if we have a wizard, open it.
				if (descriptor != null) {
					IWizard wizard = descriptor.createWizard();
					Display display = Display.getCurrent();

					WizardDialog wd = new WizardDialog(
							display.getActiveShell(), wizard);
					wd.setTitle(wizard.getWindowTitle());
					
					//wd.run(true, true, null);

					wd.open();
				}
			} catch (CoreException e) {
				e.printStackTrace();
			//} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			//} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}

		}

	}

	@Override
	public void process(TemplateCore template, ProcessArgument[] args,
			String processId, IProgressMonitor monitor)
			throws ProcessFailureException {

		System.out.println("ProcessorExpertWizard.process()");

	}

}
