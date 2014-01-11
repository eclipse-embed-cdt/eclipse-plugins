/*******************************************************************************
 * Copyright (c) 2007, 2010 Intel Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Intel Corporation - Initial API and implementation
 *    James Blackburn (Broadcom Corp.)
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross;

import org.eclipse.cdt.core.settings.model.ICMultiItemsHolder;
import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyValue;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.MultiConfiguration;
import org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator;
import org.eclipse.cdt.managedbuilder.makegen.gnu.GnuMakefileGenerator;
import org.eclipse.cdt.managedbuilder.ui.properties.AbstractCBuildPropertyTab;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ToolchainSettingsTab extends AbstractCBuildPropertyTab {

	// public static final String PROPERTY =
	// ManagedBuildManager.BUILD_ARTEFACT_TYPE_PROPERTY_ID;

	private IConfiguration m_config;

	// ---

	private Combo m_toolchainCombo;
	private int m_selectedToolchainIndex;
	private String m_selectedToolchainName;

	private Combo m_architectureCombo;

	private Text m_pathText;
	private Text m_prefixText;
	private Text m_suffixText;
	private Text m_commandCText;
	private Text m_commandCppText;
	private Text m_commandArText;
	private Text m_commandObjcopyText;
	private Text m_commandObjdumpText;
	private Text m_commandSizeText;
	private Text m_commandMakeText;
	private Text m_commandRmText;

	private Button m_flashButton;
	private Button m_listingButton;
	private Button m_sizeButton;

	private boolean m_isExecutable;
	private boolean m_isStaticLibrary;

	private boolean m_isCreated = false;

	private Composite m_composite;

	@Override
	public void createControls(Composite parent) {

		m_composite = parent;
		// Disabled, otherwise toolchain changes fail
		System.out.println("createControls()");
		if (!isThisPlugin()) {
			System.out.println("not this plugin");
			return;
		}
		//
		if (!page.isForProject()) {
			System.out.println("not this project");
			return;
		}
		//
		super.createControls(parent);

		m_config = getCfg();
		IBuildPropertyValue propertyValue = m_config.getBuildArtefactType();
		if (propertyValue != null) {
			String artefactId = propertyValue.getId();
			if (Utils.BUILD_ARTEFACT_TYPE_EXE.equals(artefactId)
					|| artefactId.endsWith(".exe"))
				m_isExecutable = true;
			else
				m_isExecutable = false;

			if (Utils.BUILD_ARTEFACT_TYPE_STATICLIB.equals(artefactId)
					|| artefactId.endsWith("Lib"))
				m_isStaticLibrary = true;
			else
				m_isStaticLibrary = false;
		} else {
			m_isExecutable = true;
			m_isStaticLibrary = false;
		}
		usercomp.setLayout(new GridLayout(3, false));
		GridData layoutData = new GridData();
		usercomp.setLayoutData(layoutData);

		// ----- Toolchain ------------------------------------------------
		Label toolchainLbl = new Label(usercomp, SWT.NONE);
		toolchainLbl.setLayoutData(new GridData(GridData.BEGINNING));
		toolchainLbl.setText(Messages.ToolChainSettingsTab_name);

		m_toolchainCombo = new Combo(usercomp, SWT.DROP_DOWN);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_toolchainCombo.setLayoutData(layoutData);

		// create the selection array
		String[] toolchains = new String[ToolchainDefinition.getSize()];
		for (int i = 0; i < ToolchainDefinition.getSize(); ++i) {
			toolchains[i] = ToolchainDefinition.getToolchain(i).getFullName();
		}
		m_toolchainCombo.setItems(toolchains);

		m_selectedToolchainName = Option.getOptionStringValue(m_config,
				Option.OPTION_TOOLCHAIN_NAME);

		// System.out
		// .println("Previous toolchain name " + m_selectedToolchainName);
		if (m_selectedToolchainName != null
				&& m_selectedToolchainName.length() > 0) {
			m_selectedToolchainIndex = ToolchainDefinition
					.findToolchainByName(m_selectedToolchainName);
		} else {
			System.out.println("No toolchain selected");
			// This is not a project created with the wizard
			// (most likely it is the result of a toolchain change)
			m_selectedToolchainIndex = ToolchainDefinition.getDefault();
			m_selectedToolchainName = ToolchainDefinition.getToolchain(
					m_selectedToolchainIndex).getName();

			// Initialise .cproject options that were not done at project
			// creation by the toolchain wizard
			try {
				setOptionsForToolchain(m_config, m_selectedToolchainIndex);

			} catch (BuildException e1) {
				System.out.println("cannot setOptionsForToolchain");
				// e1.printStackTrace();
			}

			// Set toolchain path
			String path = SharedStorage
					.getToolchainPath(m_selectedToolchainName);
			IToolChain toolchain = m_config.getToolChain();
			IOption option = toolchain
					.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_PATH); //$NON-NLS-1$
			ManagedBuildManager.setOption(m_config, toolchain, option, path);

		}

		String toolchainSel = toolchains[m_selectedToolchainIndex];
		m_toolchainCombo.setText(toolchainSel);

		m_toolchainCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				updateInterfaceAfterToolchainChange();
			}
		});

		// ----- Architecture ---------------------------------------------
		Label architectureLbl = new Label(usercomp, SWT.NONE);
		architectureLbl.setLayoutData(new GridData(GridData.BEGINNING));
		architectureLbl.setText(Messages.ToolChainSettingsTab_architecture);

		m_architectureCombo = new Combo(usercomp, SWT.DROP_DOWN);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_architectureCombo.setLayoutData(layoutData);

		m_architectureCombo.setItems(ToolchainDefinition.getArchitectures());

		String sSelectedArchitecture = Option.getOptionStringValue(m_config,
				Option.OPTION_ARCHITECTURE);
		int index;
		try {
			if (sSelectedArchitecture.endsWith("." + Option.ARCHITECTURE_ARM))
				index = 0;
			else if (sSelectedArchitecture.endsWith("."
					+ Option.ARCHITECTURE_AARCH64))
				index = 1;
			else
				index = 0; // default is ARM
		} catch (NullPointerException e) {
			index = 0; // default is ARM
		}
		m_architectureCombo.setText(ToolchainDefinition.getArchitecture(index));

		// m_toolchainCombo.addSelectionListener(new SelectionAdapter() {
		// public void widgetSelected(SelectionEvent event) {
		// }
		// });

		// ----- Prefix ---------------------------------------------------
		Label prefixLabel = new Label(usercomp, SWT.NONE);
		prefixLabel.setText(Messages.ToolChainSettingsTab_prefix);

		m_prefixText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String prefix = Option.getOptionStringValue(m_config,
				Option.OPTION_COMMAND_PREFIX);
		if (prefix != null) {
			m_prefixText.setText(prefix);
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_prefixText.setLayoutData(layoutData);

		// ----- Suffix ---------------------------------------------------
		Label suffixLabel = new Label(usercomp, SWT.NONE);
		suffixLabel.setText(Messages.ToolChainSettingsTab_suffix);

		m_suffixText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String suffix = Option.getOptionStringValue(m_config,
				Option.OPTION_COMMAND_SUFFIX);
		if (suffix != null) {
			m_suffixText.setText(suffix);
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_suffixText.setLayoutData(layoutData);

		// ----- Command c ------------------------------------------------
		Label commandCLabel = new Label(usercomp, SWT.NONE);
		commandCLabel.setText(Messages.ToolChainSettingsTab_cCmd);

		m_commandCText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String commandC = Option.getOptionStringValue(m_config,
				Option.OPTION_COMMAND_C);
		if (commandC != null) {
			m_commandCText.setText(commandC);
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_commandCText.setLayoutData(layoutData);

		// ----- Command cpp ----------------------------------------------
		Label commandCppLabel = new Label(usercomp, SWT.NONE);
		commandCppLabel.setText(Messages.ToolChainSettingsTab_cppCmd);

		m_commandCppText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String commandCpp = Option.getOptionStringValue(m_config,
				Option.OPTION_COMMAND_CPP);
		if (commandCpp != null) {
			m_commandCppText.setText(commandCpp);
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_commandCppText.setLayoutData(layoutData);

		if (m_isStaticLibrary) {
			// ----- Command ar -------------------------------------------
			Label commandArLabel = new Label(usercomp, SWT.NONE);
			commandArLabel.setText(Messages.ToolChainSettingsTab_arCmd);

			m_commandArText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			String commandAr = Option.getOptionStringValue(m_config,
					Option.OPTION_COMMAND_AR);
			if (commandAr != null) {
				m_commandArText.setText(commandAr);
			}
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			m_commandArText.setLayoutData(layoutData);
		}

		if (m_isExecutable) {
			// ----- Command objcopy --------------------------------------
			Label commandObjcopyLabel = new Label(usercomp, SWT.NONE);
			commandObjcopyLabel
					.setText(Messages.ToolChainSettingsTab_objcopyCmd);

			m_commandObjcopyText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			String commandObjcopy = Option.getOptionStringValue(m_config,
					Option.OPTION_COMMAND_OBJCOPY);
			if (commandObjcopy != null) {
				m_commandObjcopyText.setText(commandObjcopy);
			}
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			m_commandObjcopyText.setLayoutData(layoutData);

			// ----- Command objdump --------------------------------------
			Label commandObjdumpLabel = new Label(usercomp, SWT.NONE);
			commandObjdumpLabel
					.setText(Messages.ToolChainSettingsTab_objdumpCmd);

			m_commandObjdumpText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			String commandObjdump = Option.getOptionStringValue(m_config,
					Option.OPTION_COMMAND_OBJDUMP);
			if (commandObjdump != null) {
				m_commandObjdumpText.setText(commandObjdump);
			}
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			m_commandObjdumpText.setLayoutData(layoutData);

			// ----- Command size -----------------------------------------
			Label commandSizeLabel = new Label(usercomp, SWT.NONE);
			commandSizeLabel.setText(Messages.ToolChainSettingsTab_sizeCmd);

			m_commandSizeText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			String commandSize = Option.getOptionStringValue(m_config,
					Option.OPTION_COMMAND_SIZE);
			if (commandSize != null) {
				m_commandSizeText.setText(commandSize);
			}
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
			m_commandSizeText.setLayoutData(layoutData);
		}

		// ----- Command make ---------------------------------------------
		Label commandMakeLabel = new Label(usercomp, SWT.NONE);
		commandMakeLabel.setText(Messages.ToolChainSettingsTab_makeCmd);

		m_commandMakeText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String commandMake = Option.getOptionStringValue(m_config,
				Option.OPTION_COMMAND_MAKE);
		if (commandMake != null) {
			m_commandMakeText.setText(commandMake);
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_commandMakeText.setLayoutData(layoutData);

		// ----- Command rm -----------------------------------------------
		Label commandRmLabel = new Label(usercomp, SWT.NONE);
		commandRmLabel.setText(Messages.ToolChainSettingsTab_rmCmd);

		m_commandRmText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String commandRm = Option.getOptionStringValue(m_config,
				Option.OPTION_COMMAND_RM);
		if (commandRm != null) {
			m_commandRmText.setText(commandRm);
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		m_commandRmText.setLayoutData(layoutData);

		m_commandRmText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// System.out.println("commandRm modified");
			}
		});

		// ----- Path -----------------------------------------------------
		Label pathLabel = new Label(usercomp, SWT.NONE);
		pathLabel.setText(Messages.ToolChainSettingsTab_path);

		m_pathText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		String toolchainPath = Option.getOptionStringValue(m_config,
				Option.OPTION_TOOLCHAIN_PATH);
		if (toolchainPath != null) {
			m_pathText.setText(toolchainPath);
		}
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		m_pathText.setLayoutData(layoutData);

		Button pathButton = new Button(usercomp, SWT.NONE);
		pathButton.setText(Messages.ToolChainSettingsTab_browse);
		pathButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dirDialog = new DirectoryDialog(usercomp
						.getShell(), SWT.APPLICATION_MODAL);
				String browsedDirectory = dirDialog.open();
				if (browsedDirectory != null) {
					m_pathText.setText(browsedDirectory);
				}
			}
		});
		layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
		pathButton.setLayoutData(layoutData);

		if (m_isExecutable) {
			// ----- Flash ------------------------------------------------
			m_flashButton = new Button(usercomp, SWT.CHECK);
			m_flashButton.setText(Messages.ToolChainSettingsTab_flash);

			Boolean isCreateFlash = Option.getOptionBooleanValue(m_config,
					Option.OPTION_ADDTOOLS_CREATEFLASH);
			if (isCreateFlash != null) {
				m_flashButton.setSelection(isCreateFlash);
			}
			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
			m_flashButton.setLayoutData(layoutData);

			// ----- Listing ----------------------------------------------
			m_listingButton = new Button(usercomp, SWT.CHECK);
			m_listingButton.setText(Messages.ToolChainSettingsTab_listing);

			Boolean isCreateListing = Option.getOptionBooleanValue(m_config,
					Option.OPTION_ADDTOOLS_CREATELISTING);
			if (isCreateListing != null) {
				m_listingButton.setSelection(isCreateListing);
			}
			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
			m_listingButton.setLayoutData(layoutData);

			// ----- Size -------------------------------------------------
			m_sizeButton = new Button(usercomp, SWT.CHECK);
			m_sizeButton.setText(Messages.ToolChainSettingsTab_size);

			Boolean isPrintSize = Option.getOptionBooleanValue(m_config,
					Option.OPTION_ADDTOOLS_PRINTSIZE);
			if (isPrintSize != null) {
				m_sizeButton.setSelection(isPrintSize);
			}
			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
			m_sizeButton.setLayoutData(layoutData);
		}

		m_isCreated = true;

		// --------------------------------------------------------------------

	}

	private void updateInterfaceAfterToolchainChange() {

		System.out.println("updateInterfaceAfterToolchainChange()");
		int index;
		try {
			String sSelectedCombo = m_toolchainCombo.getText();
			index = ToolchainDefinition.findToolchainByFullName(sSelectedCombo);
		} catch (NullPointerException e) {
			index = 0;
		}
		ToolchainDefinition td = ToolchainDefinition.getToolchain(index);

		String sArchitecture = td.getArchitecture();
		if ("arm".equals(sArchitecture))
			index = 0;
		else if ("aarch64".equals(sArchitecture))
			index = 1;
		else
			index = 0; // default is ARM
		m_architectureCombo.setText(ToolchainDefinition.getArchitecture(index));

		m_prefixText.setText(td.getPrefix());
		m_suffixText.setText(td.getSuffix());
		m_commandCText.setText(td.getCmdC());
		m_commandCppText.setText(td.getCmdCpp());
		if (m_isStaticLibrary) {
			m_commandArText.setText(td.getCmdAr());
		}
		if (m_isExecutable) {
			m_commandObjcopyText.setText(td.getCmdObjcopy());
			m_commandObjdumpText.setText(td.getCmdObjdump());
			m_commandSizeText.setText(td.getCmdSize());
		}
		m_commandMakeText.setText(td.getCmdMake());
		String oldCommandRm = m_commandRmText.getText();
		String newCommandRm = td.getCmdRm();
		if (oldCommandRm == null || !oldCommandRm.equals(newCommandRm)) {
			// if same value skip it, to avoid remove the makefile
			m_commandRmText.setText(newCommandRm);
		}

		String path = SharedStorage.getToolchainPath(td.getName());
		m_pathText.setText(path);

		// leave the bottom three buttons as the user set them
	}

	// This event comes when the tab is selected after the windows is
	// displayed, to account for content change
	@Override
	public void updateData(ICResourceDescription cfgd) {
		if (cfgd == null)
			return;

		// currently there is no content to change since createControls()
	}

	@Override
	protected void performApply(ICResourceDescription src,
			ICResourceDescription dst) {

		System.out.println("performApply()");
		IConfiguration config = getCfg(src.getConfiguration());

		updateOptions(config);
		// does not work like this
		// SpecsProvider.clear();

		// System.out.println("performApply()");

	}

	@Override
	protected void performOK() {

		System.out.println("performOK()");

		updateOptions(m_config);
	}

	private void updateOptions(IConfiguration config) {

		if (config instanceof MultiConfiguration) {
			MultiConfiguration multi = (MultiConfiguration) config;
			for (Object obj : multi.getItems()) {
				IConfiguration cfg = (IConfiguration) obj;
				updateOptions(cfg);
			}
			return;
		}
		IToolChain toolchain = config.getToolChain();

		IOption option;
		String val;

		try {
			// Do NOT use ManagedBuildManager.setOption() to avoid sending
			// events to the option. Also do not use option.setValue()
			// since this does not propagate notifications and the
			// values are not saved to .cproject.

			String sSelectedArchitecture = m_architectureCombo.getText();
			if (ToolchainDefinition.getArchitecture(0).equals(
					sSelectedArchitecture)) {
				val = Option.OPTION_ARCHITECTURE_ARM;
			} else if (ToolchainDefinition.getArchitecture(1).equals(
					sSelectedArchitecture)) {
				val = Option.OPTION_ARCHITECTURE_AARCH64;
			} else {
				val = Option.OPTION_ARCHITECTURE_ARM; // default is ARM
			}
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ARCHITECTURE); //$NON-NLS-1$
			config.setOption(toolchain, option, val);

			String sSelectedCombo = m_toolchainCombo.getText();
			int index = ToolchainDefinition
					.findToolchainByFullName(sSelectedCombo);
			ToolchainDefinition td = ToolchainDefinition.getToolchain(index);
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME); //$NON-NLS-1$
			config.setOption(toolchain, option, td.getName());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_PREFIX); //$NON-NLS-1$
			config.setOption(toolchain, option, m_prefixText.getText());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_SUFFIX); //$NON-NLS-1$
			config.setOption(toolchain, option, m_suffixText.getText());

			option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_C); //$NON-NLS-1$
			config.setOption(toolchain, option, m_commandCText.getText());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_CPP); //$NON-NLS-1$
			config.setOption(toolchain, option, m_commandCppText.getText());

			if (m_isStaticLibrary) {
				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_COMMAND_AR); //$NON-NLS-1$
				config.setOption(toolchain, option, m_commandArText.getText());
			}

			if (m_isExecutable) {
				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJCOPY); //$NON-NLS-1$
				config.setOption(toolchain, option,
						m_commandObjcopyText.getText());

				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJDUMP); //$NON-NLS-1$
				config.setOption(toolchain, option,
						m_commandObjdumpText.getText());

				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_COMMAND_SIZE); //$NON-NLS-1$
				config.setOption(toolchain, option, m_commandSizeText.getText());
			}

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_MAKE); //$NON-NLS-1$
			config.setOption(toolchain, option, m_commandMakeText.getText());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_RM); //$NON-NLS-1$
			String oldValue = option.getStringValue();
			String newValue = m_commandRmText.getText();

			if (newValue != null && !newValue.equals(oldValue)) {
				config.setOption(toolchain, option, newValue);

				// propagate is expensive, run it only if needed
				propagateCommandRmUpdate(config);
			}

			if (m_isExecutable) {
				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATEFLASH); //$NON-NLS-1$
				config.setOption(toolchain, option,
						m_flashButton.getSelection());

				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATELISTING); //$NON-NLS-1$
				config.setOption(toolchain, option,
						m_listingButton.getSelection());

				option = toolchain
						.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_PRINTSIZE); //$NON-NLS-1$
				config.setOption(toolchain, option, m_sizeButton.getSelection());
			}

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_PATH); //$NON-NLS-1$
			ManagedBuildManager.setOption(config, toolchain, option,
					m_pathText.getText());

			String sToolchainWizardPath = SharedStorage.getToolchainPath(td
					.getName());
			String sNewToolchainPath = m_pathText.getText().trim();

			if (sToolchainWizardPath.length() == 0) {
				SharedStorage.putToolchainPath(td.getName(), sNewToolchainPath);
				SharedStorage.update();
			} else if (!sToolchainWizardPath.equals(sNewToolchainPath)) {

				// Create message box
				MessageBox box = new MessageBox(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(), SWT.YES
						| SWT.NO | SWT.APPLICATION_MODAL | SWT.ICON_QUESTION);
				// Set title
				box.setText("Path");
				// Set message
				box.setMessage("Do you want to remember '" + sNewToolchainPath
						+ "' for toolchain '" + td.getName() + "'?");
				// Open message box
				if (box.open() == SWT.YES) {
					SharedStorage.putToolchainPath(td.getName(),
							sNewToolchainPath);
					SharedStorage.update();
				}
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
			Activator.log(e);
		} catch (BuildException e) {
			Activator.log(e);
		}

	}

	// Used in SetCrossCommandOperation to set toolchain specific options
	// after wizard selection. The compiler command name must be set as
	// early as possible.
	public static void setOptionsForToolchain(IConfiguration config,
			int toolchainIndex) throws BuildException {

		boolean isExecutable;
		boolean isStaticLibrary;

		IBuildPropertyValue propertyValue = config.getBuildArtefactType();
		if (propertyValue != null) {
			String artefactId = propertyValue.getId();
			if (Utils.BUILD_ARTEFACT_TYPE_EXE.equals(artefactId)
					|| artefactId.endsWith(".exe"))
				isExecutable = true;
			else
				isExecutable = false;

			if (Utils.BUILD_ARTEFACT_TYPE_STATICLIB.equals(artefactId)
					|| artefactId.endsWith("Lib"))
				isStaticLibrary = true;
			else
				isStaticLibrary = false;
		} else {

			try {
				System.out.println(config.getProjectType().getId());
			} catch (Exception e) {
				;
			}
			isExecutable = true;
			isStaticLibrary = false;
		}
		IToolChain toolchain = config.getToolChain();

		IOption option;
		String val;

		ToolchainDefinition td = ToolchainDefinition
				.getToolchain(toolchainIndex);

		// Do NOT use ManagedBuildManager.setOption() to avoid sending
		// events to the option. Also do not use option.setValue()
		// since this does not propagate notifications and the
		// values are not saved to .cproject.
		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getName());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_ARCHITECTURE); //$NON-NLS-1$
		// compose the architecture ID
		String sArchitecture = td.getArchitecture();
		val = Option.OPTION_ARCHITECTURE + "." + sArchitecture;
		Utils.setOptionForced(config, toolchain, option, val);

		if ("arm".equals(sArchitecture)) {
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ARM_TARGET_FAMILY);
			Utils.forceOptionRewrite(config, toolchain, option);

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ARM_TARGET_INSTRUCTIONSET);
			Utils.forceOptionRewrite(config, toolchain, option);
		} else if ("aarch64".equals(sArchitecture)) {
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_AARCH64_TARGET_FAMILY);
			Utils.setOptionForced(config, toolchain, option,
					Option.OPTION_AARCH64_MCPU_GENERIC);

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_AARCH64_FEATURE_SIMD);
			Utils.setOptionForced(config, toolchain, option,
					Option.OPTION_AARCH64_FEATURE_SIMD_ENABLED);

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_AARCH64_CMODEL);
			Utils.setOptionForced(config, toolchain, option,
					Option.OPTION_AARCH64_CMODEL_SMALL);
		}

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_COMMAND_PREFIX); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getPrefix());

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_COMMAND_SUFFIX); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getSuffix());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_C); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdC());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_CPP); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdCpp());

		if (isStaticLibrary) {
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_AR); //$NON-NLS-1$
			config.setOption(toolchain, option, td.getCmdAr());
		}

		if (isExecutable) {
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJCOPY); //$NON-NLS-1$
			config.setOption(toolchain, option, td.getCmdObjcopy());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJDUMP); //$NON-NLS-1$
			config.setOption(toolchain, option, td.getCmdObjdump());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_SIZE); //$NON-NLS-1$
			config.setOption(toolchain, option, td.getCmdSize());
		}

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_MAKE); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdMake());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_RM); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdRm());

		if (isExecutable) {
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATEFLASH); //$NON-NLS-1$
			config.setOption(toolchain, option, Option.OPTION_ADDTOOLS_CREATEFLASH_DEFAULT);

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATELISTING); //$NON-NLS-1$
			config.setOption(toolchain, option, Option.OPTION_ADDTOOLS_CREATELISTING_DEFAULT);

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_PRINTSIZE); //$NON-NLS-1$
			config.setOption(toolchain, option, Option.OPTION_ADDTOOLS_PRINTSIZE_DEFAULT);
		}
	}

	private void propagateCommandRmUpdate(IConfiguration config) {
		// System.out.println("propagateCommandRmUpdate()");
		if (true) {
			IProject project = (IProject) config.getOwner();

			IPath makefilePath = project.getFullPath().append(config.getName())
					.append(IManagedBuilderMakefileGenerator.MAKEFILE_NAME);
			IResource makefileResource = project.findMember(makefilePath
					.removeFirstSegments(1));
			if (makefileResource != null && makefileResource.exists()) {
				try {
					makefileResource.delete(true, new NullProgressMonitor());

					GnuMakefileGenerator makefileGenerator = new GnuMakefileGenerator();
					makefileGenerator.initialize(0, config,
							config.getBuilder(), new NullProgressMonitor());
					makefileGenerator.regenerateMakefiles();
				} catch (CoreException e) {
					// This had better be allowed during a build
				}

			}
		}
	}

	@Override
	protected void performDefaults() {

		System.out.println("performDefaults()");
		updateInterfaceAfterToolchainChange();

		if (m_isExecutable) {
			m_flashButton.setSelection(Option.OPTION_ADDTOOLS_CREATEFLASH_DEFAULT);
			m_listingButton.setSelection(Option.OPTION_ADDTOOLS_CREATELISTING_DEFAULT);
			m_sizeButton.setSelection(Option.OPTION_ADDTOOLS_PRINTSIZE_DEFAULT);
		}
		// System.out.println("performDefaults()");
	}

	@Override
	public boolean canBeVisible() {

		if (!isThisPlugin())
			return false;

		if (page.isForProject()) {
			return true;
			// if (page.isMultiCfg()) {
			// ICMultiItemsHolder mih = (ICMultiItemsHolder) getCfg();
			// IConfiguration[] cfs = (IConfiguration[]) mih.getItems();
			// for (int i = 0; i < cfs.length; i++) {
			// if (cfs[i].getBuilder().isManagedBuildOn())
			// return true;
			// }
			// return false;
			// } else {
			//
			// return getCfg().getBuilder().isManagedBuildOn();
			// }
		} else
			return false;
	}

	// Must be true, otherwise the page is not shown
	public boolean canSupportMultiCfg() {
		return true;
	}

	@Override
	protected void updateButtons() {
	} // Do nothing. No buttons to update.

	private boolean isThisPlugin() {
		m_config = getCfg();
		IToolChain toolchain = m_config.getToolChain();
		String sToolchainId = toolchain.getBaseId();
		if (sToolchainId.startsWith(Activator.TOOLCHAIN_ID + "."))
			return true;

		return false;
	}
}
