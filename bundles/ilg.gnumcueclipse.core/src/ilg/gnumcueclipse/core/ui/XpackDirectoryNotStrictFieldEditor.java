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

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import ilg.gnumcueclipse.core.XpackUtils;

public class XpackDirectoryNotStrictFieldEditor extends DirectoryNotStrictFieldEditor {

	protected String[] fXpackNames;
	protected Button fXpackButton;
	String[] fVersions;

	public XpackDirectoryNotStrictFieldEditor(String[] xpackNames, String buildToolsPathKey, String toolsPaths_label,
			Composite fieldEditorParent, boolean isStrict) {
		super(buildToolsPathKey, toolsPaths_label, fieldEditorParent, isStrict);

		assert xpackNames != null;
		fXpackNames = xpackNames;

		fVersions = XpackUtils.getPackVersions(fXpackNames);

		if (fVersions.length > 0) {
			for (String xpackName : xpackNames) {
				IPath packPath = XpackUtils.getPackPath(xpackName);
				if (packPath.toFile().isDirectory()) {
					fXpackButton.setEnabled(true);
					break;
				}
			}
		}
	}

	@Override
	public int getNumberOfControls() {
		return 4;
	}

	@Override
	public void adjustForNumColumns(int numColumns) {
		super.adjustForNumColumns(numColumns - 1);
	}

	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns - 1);

		fXpackButton = new Button(parent, SWT.PUSH);
		fXpackButton.setText("xPack...");
		fXpackButton.setFont(parent.getFont());
		fXpackButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				buttonPressed(event);
			}
		});

		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		int widthHint = convertHorizontalDLUsToPixels(fXpackButton, IDialogConstants.BUTTON_WIDTH);
		gd.widthHint = Math.max(widthHint, fXpackButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		fXpackButton.setLayoutData(gd);

		fXpackButton.setEnabled(false);
	}

	private void buttonPressed(SelectionEvent e) {
		XpackBrowseDialog dlg = new XpackBrowseDialog(getShell(), fVersions);
		if (dlg.open() == Dialog.OK) {
			int index = dlg.getData();
			String version = fVersions[index];
			IPath path = XpackUtils.getPackPath(fXpackNames[index]);
			// TODO: remove hard reference to .content/bin
			path = path.append(version).append(".content").append("bin");
			setStringValue(path.toString());
		}
	}

}
