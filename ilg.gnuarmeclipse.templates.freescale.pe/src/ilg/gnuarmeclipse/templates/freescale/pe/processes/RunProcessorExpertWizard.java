/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnuarmeclipse.templates.freescale.pe.processes;

import ilg.gnuarmeclipse.templates.freescale.pe.Activator;

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
	public void process(TemplateCore template, ProcessArgument[] args, String processId, IProgressMonitor monitor)
			throws ProcessFailureException {

		String projectName = args[0].getSimpleValue();
		if (Activator.getInstance().isDebugging()) {
			System.out.println("ProcessorExpertWizard.process(projectName='" + projectName + "')");
		}

		String id = "com.processorexpert.ui.pewizard.newprjwizard";

		IWizardDescriptor descriptor = PlatformUI.getWorkbench().getNewWizardRegistry().findWizard(id);

		try {
			// Then if we have a wizard, open it.
			if (descriptor != null) {
				IWizard wizard = descriptor.createWizard();
				Display display = Display.getCurrent();

				// We need a method to pass the project name to the wizard.
				// This can be done either with a separate constructor or a
				// new method, for example:
				// wizard.setProjectName(projectName);

				WizardDialog wd = new WizardDialog(display.getActiveShell(), wizard);
				wd.setTitle(wizard.getWindowTitle());

				wd.open();
			}
		} catch (CoreException e) {
			Activator.log(e);
			;
		}
	}

}
