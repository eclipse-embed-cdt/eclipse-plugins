/*******************************************************************************
 * Copyright (c) 2011, 2013 Marc-Andre Laperle and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Marc-Andre Laperle - initial API and implementation
 *     Liviu Ionescu - Arm version
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui;

import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPage;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPageManager;
import org.eclipse.core.runtime.IPath;
import org.eclipse.embedcdt.core.XpackUtils;
import org.eclipse.embedcdt.managedbuild.cross.arm.core.ToolchainDefinition;
import org.eclipse.embedcdt.managedbuild.cross.arm.core.preferences.DefaultPreferences;
import org.eclipse.embedcdt.managedbuild.cross.arm.ui.SetCrossCommandWizardOperation;
import org.eclipse.embedcdt.managedbuild.cross.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.ui.EclipseUiUtils;
import org.eclipse.embedcdt.ui.XpackBrowseDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * A wizard page that allows the user to specify the prefix and the path of a
 * Cross GCC command. The values are passed to
 * {@link SetCrossCommandWizardOperation} using the {@link MBSCustomPageManager}
 */
public class SetCrossCommandWizardPage extends MBSCustomPage {

	// ------------------------------------------------------------------------

	private PersistentPreferences fPersistentPreferences;
	private DefaultPreferences fDefaultPreferences;

	private Composite fComposite;
	private boolean fFinish = false;
	private Text fPathTxt;
	private Combo fToolchainCombo;
	private int fSelectedToolchainIndex;
	private String fSelectedToolchainId;
	private String fSelectedToolchainName;
	private Button fXpackButton;
	private String[] fXpackNames;
	private String[] fXpackVersions;

	// ------------------------------------------------------------------------

	// must match the plugin.xml <wizardPage ID="">
	public static final String PAGE_ID = "org.eclipse.embedcdt.internal.managedbuild.cross.arm.ui.setCrossCommandWizardPage"; //$NON-NLS-1$

	public static final String CROSS_WIZARD = "wizard"; //$NON-NLS-1$
	public static final String CROSS_PROJECT_NAME = "projectName"; //$NON-NLS-1$

	public static final String CROSS_TOOLCHAIN_ID = "toolchain.id"; //$NON-NLS-1$
	public static final String CROSS_TOOLCHAIN_NAME = "toolchain.name"; //$NON-NLS-1$
	public static final String CROSS_TOOLCHAIN_PATH = "toolchain.path"; //$NON-NLS-1$

	// ------------------------------------------------------------------------

	public SetCrossCommandWizardPage() {
		pageID = PAGE_ID;

		// initialise properties in local storage
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_TOOLCHAIN_PATH, ""); //$NON-NLS-1$
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_TOOLCHAIN_ID, ""); //$NON-NLS-1$
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_TOOLCHAIN_NAME, ""); //$NON-NLS-1$

		fPersistentPreferences = Activator.getInstance().getPersistentPreferences();
		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
	}

	// ------------------------------------------------------------------------

	@Override
	protected boolean isCustomPageComplete() {
		// Make sure that if the users goes back to the first page and changes
		// the project name,
		// the property will be updated
		updateProjectNameProperty();
		// System.out.println("isCustomPageComplete() "+fFinish);
		return fFinish;
	}

	@Override
	public String getName() {
		return Messages.SetCrossCommandWizardPage_name;
	}

	@Override
	public void setWizard(IWizard newWizard) {
		super.setWizard(newWizard);
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_WIZARD, newWizard);
	}

	@Override
	public void createControl(Composite parent) {
		fComposite = new Composite(parent, SWT.NULL);

		fComposite.setLayout(new GridLayout(4, false));
		GridData layoutData = new GridData();
		fComposite.setLayoutData(layoutData);

		// Toolchain
		Label toolchainLbl = new Label(fComposite, SWT.NONE);
		toolchainLbl.setText(Messages.SetCrossCommandWizardPage_toolchain);

		fToolchainCombo = new Combo(fComposite, SWT.DROP_DOWN);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		fToolchainCombo.setLayoutData(layoutData);

		// create the selection array
		String[] toolchains = new String[ToolchainDefinition.getSize()];
		for (int i = 0; i < ToolchainDefinition.getSize(); ++i) {
			toolchains[i] = ToolchainDefinition.getToolchain(i).getFullName();
		}
		fToolchainCombo.setItems(toolchains);

		// decide which one is selected
		fSelectedToolchainIndex = -1;
		try {
			fSelectedToolchainId = fPersistentPreferences.getToolchainId();
			if (!fSelectedToolchainId.isEmpty()) {
				try {
					fSelectedToolchainIndex = ToolchainDefinition.findToolchainById(fSelectedToolchainId);
				} catch (IndexOutOfBoundsException e) {
				}
			}

			if (fSelectedToolchainIndex == -1) {
				fSelectedToolchainName = fPersistentPreferences.getToolchainName();
				if (!fSelectedToolchainName.isEmpty()) {
					try {
						fSelectedToolchainIndex = ToolchainDefinition.findToolchainByName(fSelectedToolchainName);
					} catch (IndexOutOfBoundsException e) {
					}
				}
			}
		} catch (Exception e) {
		}
		if (fSelectedToolchainIndex == -1) {
			fSelectedToolchainIndex = ToolchainDefinition.getDefault();
		}
		fSelectedToolchainId = ToolchainDefinition.getToolchain(fSelectedToolchainIndex).getId();
		fSelectedToolchainName = ToolchainDefinition.getToolchain(fSelectedToolchainIndex).getName();

		fXpackNames = fDefaultPreferences.getToolchainXpackNames(fSelectedToolchainId, fSelectedToolchainName);
		fXpackVersions = XpackUtils.getPackVersions(fXpackNames);

		updateToolchainNameProperty();

		String toolchainSel = toolchains[fSelectedToolchainIndex];
		fToolchainCombo.setText(toolchainSel);

		fToolchainCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// System.out.println("Combo " + toolchainCombo.getText());
				fSelectedToolchainIndex = fToolchainCombo.getSelectionIndex();
				fSelectedToolchainId = ToolchainDefinition.getToolchain(fSelectedToolchainIndex).getId();
				fSelectedToolchainName = ToolchainDefinition.getToolchain(fSelectedToolchainIndex).getName();
				updateToolchainNameProperty();

				String crossCommandPath = fPersistentPreferences.getToolchainPath(fSelectedToolchainId,
						fSelectedToolchainName, null);
				fPathTxt.setText(crossCommandPath);

				fXpackNames = fDefaultPreferences.getToolchainXpackNames(fSelectedToolchainId, fSelectedToolchainName);
				fXpackVersions = XpackUtils.getPackVersions(fXpackNames);

				if (fXpackVersions.length > 0) {
					fXpackButton.setEnabled(true);
				} else {
					fXpackButton.setEnabled(false);
				}
			}
		});

		// Path
		Label label = new Label(fComposite, SWT.NONE);
		label.setText(Messages.SetCrossCommandWizardPage_path);

		fPathTxt = new Text(fComposite, SWT.SINGLE | SWT.BORDER);
		String crossCommandPath = fPersistentPreferences.getToolchainPath(fSelectedToolchainId, fSelectedToolchainName,
				null);
		fPathTxt.setText(crossCommandPath);
		updatePathProperty();

		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		fPathTxt.setLayoutData(layoutData);
		fPathTxt.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				updatePathProperty();
			}
		});

		Button browseButton = new Button(fComposite, SWT.NONE);
		browseButton.setText(Messages.SetCrossCommandWizardPage_browse);
		browseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(fComposite.getShell(), SWT.APPLICATION_MODAL);
				String browsedDirectory = dirDialog.open();
				fPathTxt.setText(browsedDirectory);
			}
		});
		layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		browseButton.setLayoutData(layoutData);

		fXpackButton = new Button(fComposite, SWT.NONE);
		fXpackButton.setText("xPack...");
		layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		fXpackButton.setLayoutData(layoutData);
		fXpackButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				buttonPressed(event);
			}
		});

		if (fXpackVersions.length > 0) {
			fXpackButton.setEnabled(true);
		} else {
			fXpackButton.setEnabled(false);
		}

		Label message = new Label(fComposite, SWT.NONE);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		message.setLayoutData(layoutData);
		message.setText(Messages.SetCrossCommandWizardPage_text);
	}

	private void buttonPressed(SelectionEvent e) {
		XpackBrowseDialog dlg = new XpackBrowseDialog(EclipseUiUtils.getShell(), fXpackVersions);
		if (dlg.open() == Dialog.OK) {
			int index = dlg.getData();
			String version = fXpackVersions[index];

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
				fPathTxt.setText(path.toString());
				break;
			}
		}
	}

	@Override
	public Control getControl() {
		return fComposite;
	}

	@Override
	public String getDescription() {
		return Messages.SetCrossCommandWizardPage_description;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public Image getImage() {
		return wizard.getDefaultPageImage();
	}

	@Override
	public String getMessage() {
		return null;
	}

	@Override
	public String getTitle() {
		return Messages.SetCrossCommandWizardPage_title;
	}

	@Override
	public void performHelp() {
	}

	@Override
	public void setDescription(String description) {
	}

	@Override
	public void setImageDescriptor(ImageDescriptor image) {
	}

	@Override
	public void setTitle(String title) {
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			fFinish = true;
		}
		fComposite.setVisible(visible);
	}

	@Override
	public void dispose() {
		// System.out.println("dispose() "+fFinish);
	}

	/**
	 * MBSCustomPageManager and properties are used to pass things to
	 * SetCrossCommandOperation
	 */
	private void updatePathProperty() {
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_TOOLCHAIN_PATH, fPathTxt.getText());
	}

	private void updateToolchainNameProperty() {
		// save current toolchain name
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_TOOLCHAIN_ID, fSelectedToolchainId);
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_TOOLCHAIN_NAME, fSelectedToolchainName);
	}

	private void updateProjectNameProperty() {
		String name = getProjectName(getWizard());
		if (name != null) {
			MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_PROJECT_NAME, name);
		}
	}

	public static String getProjectName(IWizard wizard) {
		IWizardPage[] pages = wizard.getPages();
		for (IWizardPage wizardPage : pages) {
			if (wizardPage instanceof WizardNewProjectCreationPage) {
				return ((WizardNewProjectCreationPage) wizardPage).getProjectName();
			}
		}
		return null;
	}

	// ------------------------------------------------------------------------
}
