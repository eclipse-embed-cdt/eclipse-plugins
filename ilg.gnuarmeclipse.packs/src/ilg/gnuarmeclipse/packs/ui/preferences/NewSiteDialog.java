/*******************************************************************************
 * Copyright (c) 2014 Liviu ionescu. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.ui.preferences;

import ilg.gnuarmeclipse.packs.ui.Messages;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NewSiteDialog extends Dialog {

	private boolean m_isEdit;
	private String[] m_data;

	private String[] m_typeSelections = { "CMSIS Pack" };

	private Combo m_typeCombo;
	private Text m_urlText;

	private String m_returnType;
	private String m_returnUrl;

	protected NewSiteDialog(Shell parentShell, String[] data) {
		
		super(parentShell);
		
		m_isEdit = (data != null);
		m_data = data;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);

		String title;
		if (m_isEdit) {
			title = Messages.NewSiteDialog_label_title_edit;
		} else {
			title = Messages.NewSiteDialog_label_title_new;
		}

		if (title != null)
			shell.setText(title);
	}

	protected String[] getData() {
		return new String[] { m_returnType, m_returnUrl };
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
		layoutData.widthHint = 400;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		comp.setLayoutData(layoutData);

		{
			Label typeLabel = new Label(comp, SWT.LEFT);
			// typeLabel.setFont(comp.getFont());
			typeLabel.setText(Messages.NewSiteDialog_label_type);
			layoutData = new GridData();
			typeLabel.setLayoutData(layoutData);

			m_typeCombo = new Combo(comp, SWT.READ_ONLY);
			m_typeCombo.setItems(m_typeSelections);

			int ix = 0;
			if (m_isEdit) {
				for (int i = 0; i < m_typeSelections.length; ++i) {
					if (m_data[0].equals(m_typeSelections[i])) {
						ix = i;
						break;
					}
				}
			}
			m_typeCombo.select(ix);

			m_returnType = m_typeSelections[ix];

			layoutData = new GridData();
			m_typeCombo.setLayoutData(layoutData);
		}

		{
			Label urlLabel = new Label(comp, SWT.LEFT);
			// typeLabel.setFont(comp.getFont());
			urlLabel.setText(Messages.NewSiteDialog_label_url);
			layoutData = new GridData();
			urlLabel.setLayoutData(layoutData);

			m_urlText = new Text(comp, SWT.SINGLE | SWT.BORDER);
			if (m_isEdit) {
				m_urlText.setText(m_data[1]);
			} else {
				m_urlText.setText("");
			}

			m_returnUrl = m_urlText.getText();

			layoutData = new GridData();
			layoutData.horizontalAlignment = SWT.FILL;
			layoutData.grabExcessHorizontalSpace = true;
			m_urlText.setLayoutData(layoutData);
		}

		m_typeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				m_returnType = m_typeCombo.getText();
			}
		});

		m_urlText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				m_returnUrl = m_urlText.getText();
			}
		});

		return comp;
	}
}
