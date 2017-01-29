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

package ilg.gnuarmeclipse.packs.ui.views;

import ilg.gnuarmeclipse.packs.core.Preferences;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class CopyExampleDialog extends Dialog {

	private TreeSelection fSelection;

	private Text fFolderText;
	private String fOutputFolder;
	private String fInputFolder;
	// private String m_exampleName;

	private Button fBrowseButton;

	public CopyExampleDialog(Shell parentShell, TreeSelection selection) {

		super(parentShell);
		fSelection = selection;

		// Node exampleNode = (Node) selection.getFirstElement();
		// m_exampleName = exampleNode.getProperty(Property.EXAMPLE_NAME,
		// exampleNode.getName());

		String folderPath = Preferences.getPreferenceStore().getString(Preferences.PACKS_FOLDER_PATH);
		// m_inputFolder = folderPath + "/../Examples/" + m_exampleName;
		fInputFolder = new Path(folderPath).append("../Examples/").toOSString();

		fOutputFolder = fInputFolder;
	}

	public String[] getData() {
		return new String[] { fOutputFolder };
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void configureShell(Shell shell) {

		super.configureShell(shell);

		String s = "Copy ";
		if (fSelection.size() == 1) {
			s += "example";
		} else {
			s += fSelection.size() + " examples";
		}

		s += " to folder";
		shell.setText(s);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite comp = new Composite(parent, SWT.NULL);
		comp.setFont(parent.getFont());

		GridLayout layout;
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginTop = 10;
		layout.marginBottom = 10;
		layout.marginWidth = 11;
		comp.setLayout(layout);

		GridData layoutData = new GridData();
		layoutData.widthHint = 600;
		layoutData.verticalAlignment = SWT.FILL;
		layoutData.horizontalAlignment = SWT.FILL;
		layoutData.grabExcessHorizontalSpace = true;
		comp.setLayoutData(layoutData);

		{
			Label folderLabel = new Label(comp, SWT.LEFT);
			// typeLabel.setFont(comp.getFont());
			folderLabel.setText("Folder:");
			layoutData = new GridData();
			folderLabel.setLayoutData(layoutData);

			fFolderText = new Text(comp, SWT.SINGLE | SWT.BORDER);
			fFolderText.setText(fInputFolder);
			layoutData = new GridData();
			layoutData.horizontalAlignment = SWT.FILL;
			layoutData.grabExcessHorizontalSpace = true;
			fFolderText.setLayoutData(layoutData);

			fBrowseButton = new Button(comp, SWT.PUSH);
			fBrowseButton.setText("Browse...");
			layoutData = new GridData();
			layoutData.verticalAlignment = SWT.CENTER;
			layoutData.grabExcessHorizontalSpace = false;
			layoutData.horizontalAlignment = SWT.FILL;
			// layoutData.minimumWidth = 80;
			fBrowseButton.setLayoutData(layoutData);
		}

		// -----

		fFolderText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				fOutputFolder = fFolderText.getText().trim();
			}
		});

		fBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.SAVE);
				dialog.setText("Destination folder");
				String str = dialog.open();
				if (str != null) {
					// System.out.println(str);
					fFolderText.setText(str.trim());
				}
			}
		});

		return comp;
	}

}
