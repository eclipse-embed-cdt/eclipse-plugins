/*******************************************************************************
 * Copyright (c) 2014 Liviu ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.ui.preferences;

import ilg.gnuarmeclipse.packs.SitesStorage;

import java.util.List;

import org.eclipse.cdt.ui.newui.AbstractCPropertyTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog.
 */

public class SitesPage extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Composite m_composite;

	private Table m_table;
	private TableColumn m_columnType;
	private TableColumn m_columnUrl;

	private Composite m_buttonsComposite;

	private String[] m_buttonsNames = { AbstractCPropertyTab.ADD_STR,
			AbstractCPropertyTab.EDIT_STR, AbstractCPropertyTab.DEL_STR };

	private Button[] m_buttons; // right side buttons

	private List<String[]> m_contentList;

	public SitesPage() {

		super();
		setDescription("Add links to the sites where packages are published.");

		m_contentList = null;
		m_buttons = null;
	}

	@Override
	protected Control createContents(Composite parent) {

		m_composite = new Composite(parent, SWT.NULL);
		m_composite.setFont(parent.getFont());

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		m_composite.setLayout(layout);

		Composite groupComposite = new Composite(m_composite, SWT.NULL);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginHeight = 5;
		groupComposite.setLayout(layout);

		GridData layoutData;

		layoutData = new GridData();
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		groupComposite.setLayoutData(layoutData);

		// Column 1: table
		{
			initTable(groupComposite);

			m_contentList = SitesStorage.getSites();

			updateTableContent();
		}

		// Column 2: buttons
		{
			initButtons(groupComposite, m_buttonsNames, 80);
		}

		return m_composite;
	}

	protected void initTable(Composite comp) {

		m_table = new Table(comp, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		m_table.setHeaderVisible(true);
		m_table.setLinesVisible(true);

		GridData layoutData = new GridData();
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		m_table.setLayoutData(layoutData);

		m_columnType = new TableColumn(m_table, SWT.NULL);
		m_columnType.setText("Type");
		m_columnType.setWidth(80);
		m_columnType.setResizable(true);

		m_columnUrl = new TableColumn(m_table, SWT.NULL);
		m_columnUrl.setText("URL");
		m_columnUrl.setWidth(240);
		m_columnUrl.setResizable(true);
	}

	protected void updateTableContent() {

		m_table.removeAll();

		if (m_contentList != null) {
			TableItem item;
			for (String[] a : m_contentList) {
				item = new TableItem(m_table, SWT.NULL);
				item.setText(a);
			}
		}

		// columnType.pack();
		// columnUrl.pack();
	}

	protected void initButtons(Composite comp, String[] names, int width) {

		m_buttonsComposite = new Composite(comp, SWT.NULL);

		if (names == null || names.length == 0)
			return;

		GridData layoutData = new GridData();
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.horizontalAlignment = SWT.RIGHT;
		m_buttonsComposite.setLayoutData(layoutData);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 0;

		m_buttonsComposite.setLayout(layout);

		m_buttons = new Button[names.length];
		for (int i = 0; i < names.length; i++) {

			m_buttons[i] = new Button(m_buttonsComposite, SWT.PUSH);

			layoutData = new GridData();
			layoutData.verticalAlignment = SWT.CENTER;
			layoutData.grabExcessHorizontalSpace = false;
			layoutData.horizontalAlignment = SWT.FILL;
			layoutData.minimumWidth = width;

			if (names[i] != null)
				m_buttons[i].setText(names[i]);
			else { // no button, but placeholder !
				m_buttons[i].setVisible(false);
				m_buttons[i].setEnabled(false);
				layoutData.heightHint = 10;
			}

			m_buttons[i].setLayoutData(layoutData);

			m_buttons[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					buttonPressed(event);
				}
			});
		}
	}

	private void buttonPressed(SelectionEvent e) {

		for (int i = 0; i < m_buttons.length; i++) {
			if (m_buttons[i].equals(e.widget)) {
				buttonPressed(i);
				return;
			}
		}
	}

	public void buttonPressed(int index) {

		switch (index) {
		case 0:
			handleAddButton();
			break;
		case 1:
			handleEditButton();
			break;
		case 2:
			handleDelButton();
			break;
		}
		updateTableContent();
	}

	private void handleAddButton() {

		NewSiteDialog dlg = new NewSiteDialog(m_composite.getShell(), null);
		if (dlg.open() == Dialog.OK) {
			m_contentList.add(dlg.getData());
			// System.out.println("added");
		}
	}

	private void handleEditButton() {

		int index = m_table.getSelectionIndex();
		if (index == -1) {
			return; // nothing selected
		}

		NewSiteDialog dlg = new NewSiteDialog(m_composite.getShell(),
				m_contentList.get(index));
		if (dlg.open() == Dialog.OK) {
			m_contentList.set(index, dlg.getData());
			// System.out.println("edited");
		}
	}

	private void handleDelButton() {

		int index = m_table.getSelectionIndex();
		if (index == -1) {
			return; // nothing selected
		}

		m_contentList.remove(index);
	}

	@Override
	public void init(IWorkbench workbench) {
		// System.out.println("SitesPage.init()");
	}

	protected void performDefaults() {

		// System.out.println("SitesPage.performDefaults()");
		super.performDefaults();

		m_contentList = SitesStorage.getDefaultSites();
		updateTableContent();
	}

	@Override
	public boolean performOk() {

		// System.out.println("SitesPage.performOk()");

		SitesStorage.putSites(m_contentList);
		return super.performOk();
	}

}