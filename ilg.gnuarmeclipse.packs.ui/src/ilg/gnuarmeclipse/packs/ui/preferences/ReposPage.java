/*******************************************************************************
 * Copyright (c) 2014 Liviu Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package ilg.gnuarmeclipse.packs.ui.preferences;

import ilg.gnuarmeclipse.packs.data.Repos;
import ilg.gnuarmeclipse.packs.ui.Activator;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

public class ReposPage extends PreferencePage implements IWorkbenchPreferencePage {

	private Composite fComposite;

	private Table fTable;
	private TableColumn fColumnType;
	private TableColumn fColumnName;
	private TableColumn fColumnUrl;

	private Composite fButtonsComposite;

	private String[] fButtonsNames = { AbstractCPropertyTab.ADD_STR, AbstractCPropertyTab.EDIT_STR,
			AbstractCPropertyTab.DEL_STR };

	private Button[] fButtons; // right side buttons

	private List<Map<String, Object>> fContentList;

	private Repos fRepos;

	public ReposPage() {

		super();
		setDescription("Add links to the sites where packages are published.");

		fContentList = null;
		fButtons = null;

		fRepos = Repos.getInstance();
	}

	@Override
	protected Control createContents(Composite parent) {

		fComposite = new Composite(parent, SWT.NULL);
		fComposite.setFont(parent.getFont());

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		fComposite.setLayout(layout);

		Composite groupComposite = new Composite(fComposite, SWT.NULL);
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

			fContentList = fRepos.getList();

			updateTableContent();
		}

		// Column 2: buttons
		{
			initButtons(groupComposite, fButtonsNames, 80);
		}

		return fComposite;
	}

	protected void initTable(Composite comp) {

		fTable = new Table(comp, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		fTable.setHeaderVisible(true);
		fTable.setLinesVisible(true);

		GridData layoutData = new GridData();
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.grabExcessVerticalSpace = true;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		fTable.setLayoutData(layoutData);

		fColumnType = new TableColumn(fTable, SWT.NULL);
		fColumnType.setText("Type");
		fColumnType.setWidth(100);
		fColumnType.setResizable(true);

		fColumnName = new TableColumn(fTable, SWT.NULL);
		fColumnName.setText("Name");
		fColumnName.setWidth(100);
		fColumnName.setResizable(true);

		fColumnUrl = new TableColumn(fTable, SWT.NULL);
		fColumnUrl.setText("URL");
		fColumnUrl.setWidth(350);
		fColumnUrl.setResizable(true);
	}

	protected void updateTableContent() {

		fTable.removeAll();

		if (fContentList != null) {
			TableItem item;
			for (Map<String, Object> map : fContentList) {
				item = new TableItem(fTable, SWT.NULL);
				item.setText(fRepos.convertToArray(map));
			}
		}

		// columnType.pack();
		// columnUrl.pack();
	}

	protected void initButtons(Composite comp, String[] names, int width) {

		fButtonsComposite = new Composite(comp, SWT.NULL);

		if (names == null || names.length == 0)
			return;

		GridData layoutData = new GridData();
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.horizontalAlignment = SWT.RIGHT;
		fButtonsComposite.setLayoutData(layoutData);

		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 0;

		fButtonsComposite.setLayout(layout);

		fButtons = new Button[names.length];
		for (int i = 0; i < names.length; i++) {

			fButtons[i] = new Button(fButtonsComposite, SWT.PUSH);

			layoutData = new GridData();
			layoutData.verticalAlignment = SWT.CENTER;
			layoutData.grabExcessHorizontalSpace = false;
			layoutData.horizontalAlignment = SWT.FILL;
			layoutData.minimumWidth = width;

			if (names[i] != null)
				fButtons[i].setText(names[i]);
			else { // no button, but placeholder !
				fButtons[i].setVisible(false);
				fButtons[i].setEnabled(false);
				layoutData.heightHint = 10;
			}

			fButtons[i].setLayoutData(layoutData);

			fButtons[i].addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					buttonPressed(event);
				}
			});
		}
	}

	private void buttonPressed(SelectionEvent e) {

		for (int i = 0; i < fButtons.length; i++) {
			if (fButtons[i].equals(e.widget)) {
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

		NewRepoDialog dlg = new NewRepoDialog(fComposite.getShell(), null);
		if (dlg.open() == Dialog.OK) {
			fContentList.add(fRepos.convertToMap(dlg.getData()));
			// System.out.println("added");
		}
	}

	private void handleEditButton() {

		int index = fTable.getSelectionIndex();
		if (index == -1) {
			return; // nothing selected
		}

		NewRepoDialog dlg = new NewRepoDialog(fComposite.getShell(), fRepos.convertToArray(fContentList.get(index)));
		if (dlg.open() == Dialog.OK) {
			fContentList.set(index, fRepos.convertToMap(dlg.getData()));
			// System.out.println("edited");
		}
	}

	private void handleDelButton() {

		int index = fTable.getSelectionIndex();
		if (index == -1) {
			return; // nothing selected
		}

		fContentList.remove(index);
	}

	@Override
	public void init(IWorkbench workbench) {
		// System.out.println("SitesPage.init()");
	}

	protected void performDefaults() {

		// System.out.println("SitesPage.performDefaults()");
		super.performDefaults();

		fContentList = fRepos.getDefaultList();
		updateTableContent();
	}

	@Override
	public boolean performOk() {

		// System.out.println("SitesPage.performOk()");

		try {
			fRepos.putList(fContentList);
		} catch (IOException e) {
			Activator.log(e);
		}
		return super.performOk();
	}

}