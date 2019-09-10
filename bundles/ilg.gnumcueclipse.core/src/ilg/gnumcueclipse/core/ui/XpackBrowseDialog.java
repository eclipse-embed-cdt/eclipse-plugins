/*******************************************************************************
 * Copyright (c) 2018 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnumcueclipse.core.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ilg.gnumcueclipse.core.Activator;

public class XpackBrowseDialog extends Dialog {

	private String[] fVersions;
	private int fReturnIndex;
	private Combo fTypeCombo;

	protected XpackBrowseDialog(Shell parentShell, String[] versions) {
		super(parentShell);

		fVersions = versions;
		fReturnIndex = 0;
	}

	@Override
	protected boolean isResizable() {
		return false;
	}

	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);

		shell.setText("Select the xPack version");
	}

	protected int getData() {
		return fReturnIndex;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite comp = new Composite(parent, SWT.NULL);
		comp.setFont(parent.getFont());

		GridLayout layout;
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginTop = 10;
		layout.marginBottom = 10;
		layout.marginWidth = 10;
		comp.setLayout(layout);

		GridData layoutData = new GridData();
		layoutData.widthHint = 300;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		comp.setLayoutData(layoutData);

		{
			Label typeLabel = new Label(comp, SWT.LEFT);
			// typeLabel.setFont(comp.getFont());
			typeLabel.setText(Messages.XpackBrowseDialog_Label_text);
			layoutData = new GridData();
			typeLabel.setLayoutData(layoutData);

			fTypeCombo = new Combo(comp, SWT.READ_ONLY);
			fTypeCombo.setItems(fVersions);

			fTypeCombo.select(0);

			layoutData = new GridData();
			layoutData.widthHint = 200;
			layoutData.horizontalAlignment = SWT.FILL;
			layoutData.grabExcessHorizontalSpace = true;
			fTypeCombo.setLayoutData(layoutData);
		}

		fTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (Activator.getInstance().isDebugging()) {
					System.out.println(fTypeCombo.getText());
				}
				fReturnIndex = fTypeCombo.getSelectionIndex();
			}
		});

		return comp;
	}
}
