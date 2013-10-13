/*******************************************************************************
 * Copyright (c) 2011, 2013 Marc-Andre Laperle and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Marc-Andre Laperle - initial API and implementation
 *     Liviu Ionescu - ARM version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross;

import org.eclipse.cdt.core.templateengine.SharedDefaults;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPage;
import org.eclipse.cdt.managedbuilder.ui.wizards.MBSCustomPageManager;
import org.eclipse.jface.resource.ImageDescriptor;
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
 * Cross GCC command. The values are passed to {@link SetCrossCommandOperation}
 * using the {@link MBSCustomPageManager}
 */
public class SetCrossCommandWizardPage extends MBSCustomPage {

	private Composite m_composite;
	private boolean m_finish = false;
	private Text m_pathTxt;
	private Combo m_toolchainCombo;
	private int m_selectedToolchainIndex;
	private String m_selectedToolchainName;

	// must match the plugin.xml <wizardPage ID="">
	public static final String PAGE_ID = Activator.getIdPrefix()
			+ ".setCrossCommandWizardPage"; //$NON-NLS-1$

	public static final String CROSS_PROJECT_NAME = "projectName"; //$NON-NLS-1$

	public static final String CROSS_TOOLCHAIN_NAME = "toolchain.name"; //$NON-NLS-1$
	public static final String CROSS_TOOLCHAIN_PATH = "toolchain.path"; //$NON-NLS-1$

	// Note: The shared defaults keys don't have "cross" in them because we want
	// to keep
	// compatibility with defaults that were saved when it used to be a template
	static final String SHARED_CROSS_TOOLCHAIN_NAME = Activator.getIdPrefix()
			+ "." + CROSS_TOOLCHAIN_NAME;
	static final String SHARED_CROSS_TOOLCHAIN_PATH = Activator.getIdPrefix()
			+ "." + CROSS_TOOLCHAIN_PATH;

	public SetCrossCommandWizardPage() {
		pageID = PAGE_ID;

		// initialise properties in local storage
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_TOOLCHAIN_PATH, ""); //$NON-NLS-1$
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_TOOLCHAIN_NAME, ""); //$NON-NLS-1$
	}

	@Override
	protected boolean isCustomPageComplete() {
		// Make sure that if the users goes back to the first page and changes
		// the project name,
		// the property will be updated
		updateProjectNameProperty();
		// System.out.println("isCustomPageComplete() "+m_finish);
		return m_finish;
	}

	public String getName() {
		return Messages.SetCrossCommandWizardPage_name;
	}

	public void createControl(Composite parent) {
		m_composite = new Composite(parent, SWT.NULL);

		m_composite.setLayout(new GridLayout(3, false));
		GridData layoutData = new GridData();
		m_composite.setLayoutData(layoutData);

		// Toolchain
		Label toolchainLbl = new Label(m_composite, SWT.NONE);
		toolchainLbl.setText(Messages.SetCrossCommandWizardPage_toolchain);

		m_toolchainCombo = new Combo(m_composite, SWT.DROP_DOWN);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_toolchainCombo.setLayoutData(layoutData);

		// create the selection array
		String[] toolchains = new String[ToolchainDefinition.getSize()];
		for (int i = 0; i < ToolchainDefinition.getSize(); ++i) {
			toolchains[i] = ToolchainDefinition.getToolchain(i).getName()
					+ " (" + getSelectedCompilerCommand(i) + ")";
		}
		m_toolchainCombo.setItems(toolchains);

		// decide which one is selected
		try {
			m_selectedToolchainName = (String) SharedDefaults.getInstance()
					.getSharedDefaultsMap().get(SHARED_CROSS_TOOLCHAIN_NAME);
			// System.out.println("Previous toolchain name "
			// + m_selectedToolchainName);
			if (m_selectedToolchainName != null
					&& m_selectedToolchainName.length() > 0) {
				m_selectedToolchainIndex = ToolchainDefinition
						.findToolchainByName(m_selectedToolchainName);
			} else {
				m_selectedToolchainIndex = ToolchainDefinition.getDefault();
				m_selectedToolchainName = ToolchainDefinition.getToolchain(
						m_selectedToolchainIndex).getName();
			}
		} catch (Exception e) {
			m_selectedToolchainIndex = 0;
		}
		updateToolchainNameProperty();

		String toolchainSel = toolchains[m_selectedToolchainIndex];
		m_toolchainCombo.setText(toolchainSel);

		m_toolchainCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// System.out.println("Combo " + toolchainCombo.getText());
				m_selectedToolchainIndex = m_toolchainCombo.getSelectionIndex();
				m_selectedToolchainName = ToolchainDefinition.getToolchain(
						m_selectedToolchainIndex).getName();
				updateToolchainNameProperty();

				String pathKey = SHARED_CROSS_TOOLCHAIN_PATH + "."
						+ m_selectedToolchainName.hashCode();
				String crossCommandPath = SharedDefaults.getInstance()
						.getSharedDefaultsMap().get(pathKey);
				if (crossCommandPath == null) {
					crossCommandPath = "";
				}
				m_pathTxt.setText(crossCommandPath);

			}
		});

		// Path
		Label label = new Label(m_composite, SWT.NONE);
		label.setText(Messages.SetCrossCommandWizardPage_path);

		m_pathTxt = new Text(m_composite, SWT.SINGLE | SWT.BORDER);
		String pathKey = SHARED_CROSS_TOOLCHAIN_PATH + "."
				+ m_selectedToolchainName.hashCode();
		String crossCommandPath = SharedDefaults.getInstance()
				.getSharedDefaultsMap().get(pathKey);
		if (crossCommandPath != null) {
			m_pathTxt.setText(crossCommandPath);
			updatePathProperty();
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		m_pathTxt.setLayoutData(layoutData);
		m_pathTxt.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {

				updatePathProperty();
			}
		});

		Button button = new Button(m_composite, SWT.NONE);
		button.setText(Messages.SetCrossCommandWizardPage_browse);
		button.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(m_composite
						.getShell(), SWT.APPLICATION_MODAL);
				String browsedDirectory = dirDialog.open();
				m_pathTxt.setText(browsedDirectory);

			}
		});
		layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		button.setLayoutData(layoutData);
	}

	public Control getControl() {
		return m_composite;
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
			m_finish = true;
		}
		m_composite.setVisible(visible);
	}

	public void dispose() {
		// System.out.println("dispose() "+m_finish);
	}

	private String getSelectedCompilerCommand(int index) {
		ToolchainDefinition td = ToolchainDefinition.getToolchain(index);
		return td.getFullCmdC();
	}

	/**
	 * MBSCustomPageManager and properties are used to pass things to
	 * SetCrossCommandOperation
	 */
	private void updatePathProperty() {
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_TOOLCHAIN_PATH,
				m_pathTxt.getText());
	}

	private void updateToolchainNameProperty() {
		// save current toolchain name
		MBSCustomPageManager.addPageProperty(PAGE_ID, CROSS_TOOLCHAIN_NAME,
				m_selectedToolchainName);
	}

	private void updateProjectNameProperty() {
		IWizardPage[] pages = getWizard().getPages();
		for (IWizardPage wizardPage : pages) {
			if (wizardPage instanceof WizardNewProjectCreationPage) {
				MBSCustomPageManager.addPageProperty(PAGE_ID,
						CROSS_PROJECT_NAME,
						((WizardNewProjectCreationPage) wizardPage)
								.getProjectName());
				break;
			}
		}
	}
}