/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Liviu Ionescu - initial version
 *******************************************************************************/

package ilg.gnumcueclipse.templates.freescale.pe.ui;

import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPage;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPageManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class WizardPage extends MBSCustomPage {

	private Composite composite;

	public WizardPage() {
		pageID = "ilg.gnumcueclipse.templates.freescale.pe.ui.WizardPage";
	}

	@Override
	public boolean canFlipToNextPage() {

		return (MBSCustomPageManager.getNextPage(pageID) != null);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createControl(Composite parent) {

		composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Text pageText = new Text(composite, SWT.CENTER);
		// pageText.setBounds(composite.getBounds());
		pageText.setText("Next un New -> Processor Expert -> Enable ProcessExpert...");
		pageText.setVisible(true);
	}

	@Override
	public void dispose() {
		composite.dispose();
	}

	@Override
	public Control getControl() {
		return composite;
	}

	@Override
	public String getDescription() {
		return new String("Enable Processor Expert for C project.");
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public Image getImage() {
		return wizard.getDefaultPageImage();
	}

	@Override
	public String getMessage() {
		return null;
	}

	@Override
	public String getTitle() {
		return new String("Proccessor Expert");
	}

	@Override
	public void performHelp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDescription(String description) {
	}

	@Override
	public void setImageDescriptor(ImageDescriptor image) {
	}

	@Override
	public void setTitle(String title) {
	}

	@Override
	public void setVisible(boolean visible) {
		composite.setVisible(visible);
	}

	@Override
	protected boolean isCustomPageComplete() {
		return true;
	}

}
