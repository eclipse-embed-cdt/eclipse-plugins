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

public class RunProcessorExpertWizard extends ProcessRunner {

	@Override
	public void process(TemplateCore template, ProcessArgument[] args,
			String processId, IProgressMonitor monitor)
			throws ProcessFailureException {

		System.out.println("ProcessorExpertWizard.process()");

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
				
				wd.open();
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
