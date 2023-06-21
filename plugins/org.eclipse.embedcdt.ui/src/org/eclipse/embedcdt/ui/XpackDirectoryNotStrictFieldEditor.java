/*******************************************************************************
 * Copyright (c) 2018, 2023 Liviu Ionescu and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Liviu Ionescu - initial implementation.
 *******************************************************************************/

package org.eclipse.embedcdt.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.embedcdt.core.XpackUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class XpackDirectoryNotStrictFieldEditor extends DirectoryNotStrictFieldEditor {

	protected String[] fXpackNames;
	protected Button fXpackButton;
	protected String[] fVersions;

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
				packPath = XpackUtils.getSysPackPath(xpackName);
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

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		super.doFillIntoGrid(parent, numColumns - 1);
		fXpackButton = getXPackButton(parent);

		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		int widthHint = convertHorizontalDLUsToPixels(fXpackButton, IDialogConstants.BUTTON_WIDTH);
		gd.widthHint = Math.max(widthHint, fXpackButton.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		fXpackButton.setLayoutData(gd);
	}

	private Button getXPackButton(Composite parent) {
		if (fXpackButton == null) {
			fXpackButton = new Button(parent, SWT.PUSH);
			fXpackButton.setText("xPack...");
			fXpackButton.setFont(parent.getFont());
			fXpackButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					buttonPressed(event);
				}
			});

			fXpackButton.setEnabled(false);
		} else {
			checkParent(fXpackButton, parent);
		}

		return fXpackButton;
	}

	private void buttonPressed(SelectionEvent e) {
		XpackBrowseDialog dlg = new XpackBrowseDialog(getShell(), fVersions);
		if (dlg.open() == Dialog.OK) {
			int index = dlg.getData();
			String version = fVersions[index];

			for (String name : fXpackNames) {
				IPath path = XpackUtils.getPackPath(name).append(version);
				if (!path.toFile().isDirectory()) {
					path = XpackUtils.getSysPackPath(name).append(version);
					if (!path.toFile().isDirectory()) {
						continue;
					}
				}
				// TODO: remove hard reference to .content/bin
				path = path.append(".content").append("bin");
				setStringValue(path.toString());
				break;
			}
		}
	}

}
