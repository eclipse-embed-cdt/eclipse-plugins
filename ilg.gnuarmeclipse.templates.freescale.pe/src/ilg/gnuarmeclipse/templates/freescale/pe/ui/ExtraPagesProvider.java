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

package ilg.gnuarmeclipse.templates.freescale.pe.ui;

import java.util.Collections;
import java.util.Map;

import org.eclipse.cdt.ui.templateengine.AbstractWizardDataPage;
import org.eclipse.cdt.ui.templateengine.IPagesAfterTemplateSelectionProvider;
import org.eclipse.cdt.ui.templateengine.IWizardDataPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

public class ExtraPagesProvider implements IPagesAfterTemplateSelectionProvider {

	IWizardDataPage[] pages;

	@Override
	public IWizardDataPage[] createAdditionalPages(IWorkbenchWizard wizard, IWorkbench workbench,
			IStructuredSelection selection) {

		pages = new IWizardDataPage[1];
		pages[0] = new ProcessorExpertPage();

		return pages;
	}

	@Override
	public IWizardDataPage[] getCreatedPages(IWorkbenchWizard wizard) {

		return pages;
	}

	/**
	 * An example implementation of {@link IWizardDataPage} for test purposes.
	 */
	static class ProcessorExpertPage extends AbstractWizardDataPage implements IWizardDataPage {

		public ProcessorExpertPage() {
			super("Processor Expert", "Processor Expert Wizard", null);
			setMessage("Populate project with Processor Expert specifics");
		}

		@Override
		public Map<String, String> getPageData() {
			return Collections.singletonMap("myKey", "myData");
		}

		@Override
		public void createControl(Composite parent) {
			Label l = new Label(parent, SWT.NONE);
			l.setText("Currently this can only be done with New -> Processor Expert -> Enable ProcessExpert...");
			setControl(l);
		}
	}

}
