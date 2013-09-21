/*******************************************************************************
 * Copyright (c) 2011, 2012 Marc-Andre Laperle and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Marc-Andre Laperle - initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.build.crossgcc;

import org.eclipse.cdt.core.templateengine.SharedDefaults;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPage;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPageManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * A wizard page that allows the user to specify the prefix and the path of a Cross GCC command.
 * The values are passed to {@link SetCrossCommandOperation} using the {@link MBSCustomPageManager}
 */
public class SetCrossCommandWizardPage extends MBSCustomPage {

	private Composite composite;
	private boolean finish = false;
	private Text pathTxt;
	private Text prefixTxt;

	public static final String PAGE_ID = "org.eclipse.cdt.build.crossgcc.setcCrossCommandWizardPage"; //$NON-NLS-1$
	
	public static final String CROSS_PROJECT_NAME = "crossProjectName"; //$NON-NLS-1$
	public static final String CROSS_COMMAND_PREFIX = "crossCommandPrefix"; //$NON-NLS-1$
	public static final String CROSS_COMMAND_PATH = "crossCommandPath"; //$NON-NLS-1$
	
	// Note: The shared defaults keys don't have "cross" in them because we want to keep
	// compatibility with defaults that were saved when it used to be a template
	static final String SHARED_DEFAULTS_PREFIX_KEY = "prefix";
	static final String SHARED_DEFAULTS_PATH_KEY = "path";
	
	public SetCrossCommandWizardPage() {
		pageID = PAGE_ID;
		
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_COMMAND_PATH, ""); //$NON-NLS-1$
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_COMMAND_PREFIX, ""); //$NON-NLS-1$
	}

	@Override
	protected boolean isCustomPageComplete() {
		// Make sure that if the users goes back to the first page and changes the project name,
		// the property will be updated
		updateProjectNameProperty();
		return finish;
	}

	public String getName() {
		return Messages.SetCrossCommandWizardPage_name;
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NULL);
		
		composite.setLayout(new GridLayout(3, false));
		GridData layoutData = new GridData();
		composite.setLayoutData(layoutData);
		
		Label prefixLbl = new Label(composite, SWT.NONE);
		prefixLbl.setText(Messages.SetCrossCommandWizardPage_prefix);
		
		prefixTxt = new Text(composite, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		prefixTxt.setLayoutData(layoutData);
		String crossCommandPrefix = SharedDefaults.getInstance().getSharedDefaultsMap().get(SHARED_DEFAULTS_PREFIX_KEY);
		if (crossCommandPrefix != null) {
			prefixTxt.setText(crossCommandPrefix);
			updatePrefixProperty();
		}
		prefixTxt.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				updatePrefixProperty();
			}
		});
		
		Label label = new Label(composite, SWT.NONE);
		label.setText(Messages.SetCrossCommandWizardPage_path);
		
		pathTxt = new Text(composite, SWT.SINGLE | SWT.BORDER);
		String crossCommandPath = SharedDefaults.getInstance().getSharedDefaultsMap().get(SHARED_DEFAULTS_PATH_KEY);
		if (crossCommandPath != null) {
			pathTxt.setText(crossCommandPath);
			updatePathProperty();
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		pathTxt.setLayoutData(layoutData);
		pathTxt.addModifyListener(new ModifyListener() {
			
			public void modifyText(ModifyEvent e) {
				updatePathProperty();
			}
		});
		
		Button button = new Button(composite, SWT.NONE);
		button.setText(Messages.SetCrossCommandWizardPage_browse);
		button.addSelectionListener(new SelectionListener() {
			
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(composite.getShell(), SWT.APPLICATION_MODAL);
				String browsedDirectory = dirDialog.open();
				pathTxt.setText(browsedDirectory);
				
			}
		});
		layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		button.setLayoutData(layoutData);
	}
	
	public Control getControl() {
		return composite;
	}

	public String getDescription() {
		return Messages.SetCrossCommandWizardPage_description;
	}

	public String getErrorMessage() {
		return null;
	}

	public Image getImage() {
		return wizard.getDefaultPageImage();
	}

	public String getMessage() {
		return null;
	}

	public String getTitle() {
		return Messages.SetCrossCommandWizardPage_title;
	}

	public void performHelp() {
	}

	public void setDescription(String description) {
	}

	public void setImageDescriptor(ImageDescriptor image) {
	}

	public void setTitle(String title) {
	}

	public void setVisible(boolean visible) {
		if (visible) {
			finish = true;
		}
		composite.setVisible(visible);
	}
	
	public void dispose() {
	}

	
	/**
	 * MBSCustomPageManager and properties are used to pass things to SetCrossCommandOperation 
	 */
	private void updatePathProperty() {
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_COMMAND_PATH, pathTxt.getText());
	}

	private void updatePrefixProperty() {
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_COMMAND_PREFIX, prefixTxt.getText());
	}

	private void updateProjectNameProperty() {
		IWizardPage[] pages = getWizard().getPages();
		for (IWizardPage wizardPage : pages) {
			if (wizardPage instanceof WizardNewProjectCreationPage) {
				MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_PROJECT_NAME, ((WizardNewProjectCreationPage) wizardPage).getProjectName());
				break;
			}
		}
	}
}