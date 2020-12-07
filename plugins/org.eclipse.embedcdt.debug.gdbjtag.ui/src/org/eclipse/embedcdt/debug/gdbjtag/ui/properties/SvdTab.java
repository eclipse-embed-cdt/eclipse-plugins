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

package org.eclipse.embedcdt.debug.gdbjtag.ui.properties;

import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.ui.newui.AbstractCPropertyTab;
import org.eclipse.cdt.utils.ui.controls.ControlFactory;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.embedcdt.debug.gdbjtag.core.properties.PersistentProperties;
import org.eclipse.embedcdt.internal.debug.gdbjtag.ui.Activator;
import org.eclipse.embedcdt.internal.debug.gdbjtag.ui.Messages;
import org.eclipse.embedcdt.ui.DirectoryNotStrictFieldEditor;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

// DEPRECATED, functionality moved to debugger launch configuration.
public class SvdTab extends AbstractCPropertyTab {

	protected Text fFileText;
	protected Button fFileButton;
	protected DirectoryNotStrictFieldEditor fEditor;

	protected Preferences fPreferences;
	protected Preferences fCompatPreferences;

	@Override
	public void createControls(Composite parent) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("SvdTab.createControls()");
		}

		IProject project = page.getProject();
		fPreferences = new ProjectScope(project).getNode(Activator.PLUGIN_ID);
		fCompatPreferences = new ProjectScope(project).getNode(Activator.COMPATIBILITY_PLUGIN_ID);

		super.createControls(parent);

		usercomp.setLayout(new GridLayout(3, false));
		usercomp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		{
			Label introLabel = new Label(usercomp, SWT.WRAP | SWT.LEFT);
			introLabel.setText(Messages.SvdPathProperties_intro_label);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 3;
			introLabel.setLayoutData(gd);
		}

		{
			Label fileLabel = ControlFactory.createLabel(usercomp, Messages.SvdPathProperties_file_label);
			((GridData) fileLabel.getLayoutData()).grabExcessHorizontalSpace = false;

			fFileText = ControlFactory.createTextField(usercomp, SWT.SINGLE | SWT.BORDER);
			((GridData) fFileText.getLayoutData()).widthHint = new PixelConverter(parent)
					.convertWidthInCharsToPixels(40);
			fFileText.setToolTipText(Messages.SvdPathProperties_file_tooltip);

			fFileButton = ControlFactory.createPushButton(usercomp, Messages.SvdPathProperties_file_button);
			((GridData) fFileButton.getLayoutData()).horizontalAlignment = GridData.END;
		}

		Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		fFileButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog dialog = new FileDialog(activeShell, SWT.NONE);
				dialog.setText(Messages.SvdPathProperties_file_dialog);
				String fileName = fFileText.getText();
				dialog.setFilterPath(fileName);
				String chosenFile = dialog.open();
				if (chosenFile != null) {
					fFileText.setText(chosenFile);
				}
			}

		});

	}

	private void storeValue(ICResourceDescription desc) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("SvdTab.storeValue('" + desc.getId() + "') = '" + fFileText.getText().trim() + "'");
		}
		String key = PersistentProperties.getSvdAbsolutePathKey(desc.getConfiguration().getId());
		fPreferences.put(key, fFileText.getText().trim());
		try {
			fPreferences.flush();
		} catch (BackingStoreException ex) {
			System.out.println(ex);
		}
	}

	@Override
	protected void performApply(ICResourceDescription src, ICResourceDescription dst) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("SvdTab.performApply(" + src.getConfiguration().getName() + ","
					+ dst.getConfiguration().getName() + ")");
		}
		storeValue(src);
	}

	@Override
	protected void performOK() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("SvdTab.performOK()");
		}
		ICResourceDescription desc = getResDesc();
		storeValue(desc);
	}

	@Override
	protected void performDefaults() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("SvdTab.performDefaults()");
		}
		fFileText.setText("");
	}

	@Override
	protected void updateData(ICResourceDescription cfg) {

		String key = PersistentProperties.getSvdAbsolutePathKey(cfg.getConfiguration().getId());
		String value = fPreferences.get(key, fCompatPreferences.get(key, ""));

		if (Activator.getInstance().isDebugging()) {
			System.out.println("SvdTab.updateData(" + cfg.getConfiguration().getName() + ") '" + value + "'");
		}
		fFileText.setText(value);
	}

	@Override
	protected void updateButtons() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("SvdTab.updateButtons()");
		}

	}

	public boolean canSupportMultiCfg() {
		return false;
	}

}
