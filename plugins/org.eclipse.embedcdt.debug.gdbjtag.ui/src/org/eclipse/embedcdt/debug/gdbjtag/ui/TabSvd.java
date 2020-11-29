/*******************************************************************************
 * Copyright (c) 2017 Liviu Ionescu.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Liviu Ionescu - initial version
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.ui;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.embedcdt.debug.gdbjtag.core.ConfigurationAttributes;
import org.eclipse.embedcdt.internal.debug.gdbjtag.ui.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class TabSvd extends AbstractLaunchConfigurationTab {

	private static final String TAB_NAME = "SVD Path";
	private static final String TAB_ID = Activator.PLUGIN_ID + ".svdtab";

	private Text fSvdPath;
	private Button fBrowseButton;
	private Button fVariablesButton;

	@Override
	public void createControl(Composite parent) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("TabSvd.createControl() ");
		}

		Composite comp = new Composite(parent, SWT.NONE);
		{
			setControl(comp);
			GridLayout layout = new GridLayout();
			comp.setLayout(layout);
		}

		Group group = new Group(comp, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
			group.setText(Messages.getString("TabSvd_group_text"));
		}

		Composite groupComp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 5;
			layout.marginHeight = 0;
			groupComp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			groupComp.setLayoutData(gd);
		}

		{
			Label label = new Label(groupComp, SWT.NONE);
			label.setText(Messages.getString("TabSvd_label_text"));
			label.setToolTipText(Messages.getString("TabSvd_label_tooltip"));

			Composite local = new Composite(groupComp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 3;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) groupComp.getLayout()).numColumns - 1;
			local.setLayoutData(gd);
			{
				fSvdPath = new Text(local, SWT.SINGLE | SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				fSvdPath.setLayoutData(gd);
				fSvdPath.setToolTipText(Messages.getString("TabSvd_label_tooltip"));

				fBrowseButton = new Button(local, SWT.NONE);
				fBrowseButton.setText(Messages.getString("TabSvd_button_Browse_text"));

				fVariablesButton = new Button(local, SWT.NONE);
				fVariablesButton.setText(Messages.getString("TabSvd_button_Variables_text"));
			}
		}

		fSvdPath.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});

		fBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(Messages.getString("DebuggerTab.gdbServerExecutableBrowse_Title"), fSvdPath);
			}
		});

		fVariablesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variablesButtonSelected(fSvdPath);
			}
		});
	}

	private void browseButtonSelected(String title, Text text) {

		FileDialog dialog = new FileDialog(getShell(), SWT.NONE);
		dialog.setText(title);
		String str = text.getText().trim();
		int lastSeparatorIndex = str.lastIndexOf(File.separator);
		if (lastSeparatorIndex != -1)
			dialog.setFilterPath(str.substring(0, lastSeparatorIndex));
		str = dialog.open();
		if (str != null)
			text.setText(str);
	}

	private void variablesButtonSelected(Text text) {

		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());
		if (dialog.open() == StringVariableSelectionDialog.OK) {
			text.insert(dialog.getVariableExpression());
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("TabSvd.setDefaults() " + configuration.getName());
		}
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("TabSvd.initializeFrom() " + configuration.getName());
		}

		try {
			fSvdPath.setText(configuration.getAttribute(ConfigurationAttributes.SVD_PATH, ""));
		} catch (CoreException e) {
			Activator.log(e.getStatus());
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("TabSvd.performApply() " + configuration.getName() + ", dirty=" + isDirty());
		}

		String svdPath = fSvdPath.getText().trim();
		configuration.setAttribute(ConfigurationAttributes.SVD_PATH, svdPath);
	}

	@Override
	public String getName() {
		return TAB_NAME;
	}

	@Override
	public Image getImage() {
		return Activator.getInstance().getImage("peripheral");
	}

	@Override
	public String getId() {
		return TAB_ID;
	}

}
