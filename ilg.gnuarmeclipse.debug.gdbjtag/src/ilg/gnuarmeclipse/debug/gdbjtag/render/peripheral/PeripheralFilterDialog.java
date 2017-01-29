/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial version 
 *     		(many thanks to Code Red for providing the inspiration)
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.render.peripheral;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PeripheralFilterDialog extends TitleAreaDialog {

	// ------------------------------------------------------------------------

	private String fFilterText;
	private Text fText;

	// ------------------------------------------------------------------------

	public PeripheralFilterDialog(Shell parentShell, String initValue) {
		super(parentShell);

		if (initValue != null) {
			fFilterText = initValue;
		} else {
			fFilterText = "";
		}
	}

	// ------------------------------------------------------------------------

	@Override
	public void create() {
		super.create();
		setTitle("Filter");
		setMessage("Hide register/fields not containing the filter string", IMessageProvider.NONE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		// Create the parent dialog.
		Composite area = (Composite) super.createDialogArea(parent);

		// Add some local content (in this case just a single Text).
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		container.setLayout(layout);

		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		layoutData.grabExcessVerticalSpace = true;
		container.setLayoutData(layoutData);

		Label lbtFirstName = new Label(container, SWT.NONE);
		lbtFirstName.setText("Filter:");

		GridData dataFirstName = new GridData();
		dataFirstName.grabExcessHorizontalSpace = true;
		dataFirstName.horizontalAlignment = GridData.FILL;

		fText = new Text(container, SWT.BORDER);
		fText.setLayoutData(dataFirstName);
		if (fFilterText != null) {
			fText.setText(fFilterText);
		}

		return area;
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	// ------------------------------------------------------------------------

	/**
	 * Save content of the Text fields because they get disposed as soon as the
	 * Dialog closes.
	 */
	private void saveInput() {
		fFilterText = fText.getText();
	}

	public String getValue() {
		return fFilterText.trim();
	}

	// ------------------------------------------------------------------------
}
