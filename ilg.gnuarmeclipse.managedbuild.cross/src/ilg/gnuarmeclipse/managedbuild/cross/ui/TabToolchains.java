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
 *    Liviu Ionescu - ARM version
 *******************************************************************************/

package ilg.gnuarmeclipse.managedbuild.cross.ui;

import ilg.gnuarmeclipse.managedbuild.cross.Activator;
import ilg.gnuarmeclipse.managedbuild.cross.IDs;
import ilg.gnuarmeclipse.managedbuild.cross.Option;
import ilg.gnuarmeclipse.managedbuild.cross.ToolchainDefinition;
import ilg.gnuarmeclipse.managedbuild.cross.Utils;

import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyValue;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
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
import org.eclipse.swt.widgets.Text;

/**
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@SuppressWarnings("restriction")
public class TabToolchains extends AbstractCBuildPropertyTab {

	// ------------------------------------------------------------------------

	// private Composite fComposite;
	private IConfiguration fConfig;
	private IProject fProject;
	private IConfiguration fLastUpdatedConfig = null;

	// ---

	private Combo fToolchainCombo;
	private int fSelectedToolchainIndex;
	private String fSelectedToolchainName;

	private Combo fArchitectureCombo;

	private Text fPrefixText;
	private Text fSuffixText;
	private Text fCommandCText;
	private Text fCommandCppText;
	private Text fCommandArText;
	private Text fCommandObjcopyText;
	private Text fCommandObjdumpText;
	private Text fCommandSizeText;
	private Text fCommandMakeText;
	private Text fCommandRmText;

	private Button fUseGlobalCheckButton;
	private Text fGlobalPathText;
	private Button fGlobalPathButton;
	private Text fProjectPathText;
	private Button fProjectPathButton;

	// private Button fUseGlobalToolsCheckButton;

	private Button fFlashButton;
	private Button fListingButton;
	private Button fSizeButton;

	// private boolean fIsExecutable;
	// private boolean fIsStaticLibrary;

	private static int WIDTH_HINT = 120;

	// ------------------------------------------------------------------------

	@Override
	public void createControls(Composite parent) {

		if (Utils.isLinux()) {
			WIDTH_HINT = 150;
		}

		// fComposite = parent;
		// Disabled, otherwise toolchain changes fail
		System.out.println("Toolchains.createControls()");
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

		fConfig = getCfg();
		fProject = (IProject) fConfig.getManagedProject().getOwner();

		System.out.println("createControls() fConfig=" + fConfig);

		// usercomp is defined in parent class

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		// layout.marginHeight = 0;
		// layout.marginWidth = 0;
		usercomp.setLayout(layout);

		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		usercomp.setLayoutData(layoutData);

		// fWasUpdateRefused = false;

		// ----- Toolchain ----------------------------------------------------
		Label toolchainLbl = new Label(usercomp, SWT.NONE);
		toolchainLbl.setLayoutData(new GridData(GridData.BEGINNING));
		toolchainLbl.setText(Messages.ToolChainSettingsTab_name);

		fToolchainCombo = new Combo(usercomp, SWT.DROP_DOWN);
		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		fToolchainCombo.setLayoutData(layoutData);

		fToolchainCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				updateInterfaceAfterToolchainChange();
			}
		});

		// ----- Architecture -------------------------------------------------
		Label architectureLbl = new Label(usercomp, SWT.NONE);
		architectureLbl.setLayoutData(new GridData(GridData.BEGINNING));
		architectureLbl.setText(Messages.ToolChainSettingsTab_architecture);

		fArchitectureCombo = new Combo(usercomp, SWT.DROP_DOWN);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = WIDTH_HINT;
		fArchitectureCombo.setLayoutData(layoutData);

		// ----- Prefix -------------------------------------------------------
		Label prefixLabel = new Label(usercomp, SWT.NONE);
		prefixLabel.setText(Messages.ToolChainSettingsTab_prefix);

		fPrefixText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = WIDTH_HINT;
		fPrefixText.setLayoutData(layoutData);

		// ----- Suffix -------------------------------------------------------
		Label suffixLabel = new Label(usercomp, SWT.NONE);
		suffixLabel.setText(Messages.ToolChainSettingsTab_suffix);

		fSuffixText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = WIDTH_HINT;
		fSuffixText.setLayoutData(layoutData);

		// ----- Command c ----------------------------------------------------
		Label commandCLabel = new Label(usercomp, SWT.NONE);
		commandCLabel.setText(Messages.ToolChainSettingsTab_cCmd);

		fCommandCText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = WIDTH_HINT;
		fCommandCText.setLayoutData(layoutData);

		// ----- Command cpp --------------------------------------------------
		Label commandCppLabel = new Label(usercomp, SWT.NONE);
		commandCppLabel.setText(Messages.ToolChainSettingsTab_cppCmd);

		fCommandCppText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = WIDTH_HINT;
		fCommandCppText.setLayoutData(layoutData);

		// ----- Command ar ---------------------------------------------------
		Label commandArLabel = new Label(usercomp, SWT.NONE);
		commandArLabel.setText(Messages.ToolChainSettingsTab_arCmd);

		fCommandArText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = WIDTH_HINT;
		fCommandArText.setLayoutData(layoutData);

		// ----- Command objcopy ----------------------------------------------
		Label commandObjcopyLabel = new Label(usercomp, SWT.NONE);
		commandObjcopyLabel.setText(Messages.ToolChainSettingsTab_objcopyCmd);

		fCommandObjcopyText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = WIDTH_HINT;
		fCommandObjcopyText.setLayoutData(layoutData);

		// ----- Command objdump ----------------------------------------------
		Label commandObjdumpLabel = new Label(usercomp, SWT.NONE);
		commandObjdumpLabel.setText(Messages.ToolChainSettingsTab_objdumpCmd);

		fCommandObjdumpText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = WIDTH_HINT;
		fCommandObjdumpText.setLayoutData(layoutData);

		// ----- Command size -------------------------------------------------
		Label commandSizeLabel = new Label(usercomp, SWT.NONE);
		commandSizeLabel.setText(Messages.ToolChainSettingsTab_sizeCmd);

		fCommandSizeText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = WIDTH_HINT;
		fCommandSizeText.setLayoutData(layoutData);

		// ----- Command make -------------------------------------------------
		Label commandMakeLabel = new Label(usercomp, SWT.NONE);
		commandMakeLabel.setText(Messages.ToolChainSettingsTab_makeCmd);

		fCommandMakeText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = WIDTH_HINT;
		fCommandMakeText.setLayoutData(layoutData);

		// ----- Command rm ---------------------------------------------------
		Label commandRmLabel = new Label(usercomp, SWT.NONE);
		commandRmLabel.setText(Messages.ToolChainSettingsTab_rmCmd);

		fCommandRmText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
		layoutData = new GridData();
		layoutData.horizontalSpan = 2;
		layoutData.widthHint = WIDTH_HINT;
		fCommandRmText.setLayoutData(layoutData);

		fCommandRmText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				// System.out.println("commandRm modified");
			}
		});

		{
			// ----- Use Global Path ------------------------------------------

			fUseGlobalCheckButton = new Button(usercomp, SWT.CHECK);
			fUseGlobalCheckButton
					.setText(Messages.ToolChainSettingsTab_useGlobal);
			fUseGlobalCheckButton
					.setToolTipText(Messages.ToolChainSettingsTab_useGlobal_toolTip);

			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
			fUseGlobalCheckButton.setLayoutData(layoutData);

			fUseGlobalCheckButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					useGlobalChanged();
				}
			});
		}

		{
			// ----- Global Path ----------------------------------------------
			Label pathLabel = new Label(usercomp, SWT.NONE);
			pathLabel.setText(Messages.ToolChainSettingsTab_globalPath);
			pathLabel
					.setToolTipText(Messages.ToolChainSettingsTab_globalPath_toolTip);

			fGlobalPathText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			fGlobalPathText.setLayoutData(layoutData);

			fGlobalPathButton = new Button(usercomp, SWT.NONE);
			fGlobalPathButton.setText(Messages.ToolChainSettingsTab_browse);
			fGlobalPathButton.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dirDialog = new DirectoryDialog(usercomp
							.getShell(), SWT.APPLICATION_MODAL);
					String browsedDirectory = dirDialog.open();
					if (browsedDirectory != null) {
						fGlobalPathText.setText(browsedDirectory);
					}
				}
			});
			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
			fGlobalPathButton.setLayoutData(layoutData);
		}
		{
			// ----- Project Path ---------------------------------------------
			Label pathLabel = new Label(usercomp, SWT.NONE);
			pathLabel.setText(Messages.ToolChainSettingsTab_projectPath);
			pathLabel
					.setToolTipText(Messages.ToolChainSettingsTab_projectPath_toolTip);

			fProjectPathText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
			fProjectPathText.setLayoutData(layoutData);

			fProjectPathButton = new Button(usercomp, SWT.NONE);
			fProjectPathButton.setText(Messages.ToolChainSettingsTab_browse);
			fProjectPathButton.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					DirectoryDialog dirDialog = new DirectoryDialog(usercomp
							.getShell(), SWT.APPLICATION_MODAL);
					String browsedDirectory = dirDialog.open();
					if (browsedDirectory != null) {
						fProjectPathText.setText(browsedDirectory);
					}
				}
			});
			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1);
			fProjectPathButton.setLayoutData(layoutData);
		}

		// ----- Flash --------------------------------------------------------
		fFlashButton = new Button(usercomp, SWT.CHECK);
		fFlashButton.setText(Messages.ToolChainSettingsTab_flash);
		layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
		fFlashButton.setLayoutData(layoutData);

		// ----- Listing ------------------------------------------------------
		fListingButton = new Button(usercomp, SWT.CHECK);
		fListingButton.setText(Messages.ToolChainSettingsTab_listing);
		layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
		fListingButton.setLayoutData(layoutData);

		// ----- Size ---------------------------------------------------------
		fSizeButton = new Button(usercomp, SWT.CHECK);
		fSizeButton.setText(Messages.ToolChainSettingsTab_size);
		layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
		fSizeButton.setLayoutData(layoutData);

		// fIsCreated = true;

		updateControlsForConfig(fConfig);

		String toolchainPath = PersistentPreferences.getToolchainPath(
				fSelectedToolchainName, fProject);
		if (toolchainPath != null) {
			fGlobalPathText.setText(toolchainPath);
		}

		useGlobalChanged();

		// --------------------------------------------------------------------

	}

	private void useGlobalChanged() {

		boolean enabled = fUseGlobalCheckButton.getSelection();

		fGlobalPathText.setEnabled(enabled);
		fGlobalPathButton.setEnabled(enabled);

		fProjectPathText.setEnabled(!enabled);
		fProjectPathButton.setEnabled(!enabled);
	}

	private void updateInterfaceAfterToolchainChange() {

		System.out.println("Toolchains.updateInterfaceAfterToolchainChange()");
		int index;
		try {
			String sSelectedCombo = fToolchainCombo.getText();
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
		fArchitectureCombo.setText(ToolchainDefinition.getArchitecture(index));

		fPrefixText.setText(td.getPrefix());
		fSuffixText.setText(td.getSuffix());
		fCommandCText.setText(td.getCmdC());
		fCommandCppText.setText(td.getCmdCpp());
		fCommandArText.setText(td.getCmdAr());
		fCommandObjcopyText.setText(td.getCmdObjcopy());
		fCommandObjdumpText.setText(td.getCmdObjdump());
		fCommandSizeText.setText(td.getCmdSize());

		fCommandMakeText.setText(td.getCmdMake());
		String oldCommandRm = fCommandRmText.getText();
		String newCommandRm = td.getCmdRm();
		if (oldCommandRm == null || !oldCommandRm.equals(newCommandRm)) {
			// if same value skip it, to avoid remove the makefile
			fCommandRmText.setText(newCommandRm);
		}

		String path = PersistentPreferences.getToolchainPath(td.getName(),
				fProject);
		fGlobalPathText.setText(path);

		// leave the bottom three buttons as the user set them
		// leave the project toolchain path as the user set it
	}

	// This event comes when the tab is selected after the windows is
	// displayed, to account for content change
	// It also comes when the configuration is changed in the selection.
	@Override
	public void updateData(ICResourceDescription cfgd) {
		if (cfgd == null)
			return;

		// fConfig = getCfg();
		System.out.println("Toolchains.updateData() " + getCfg().getName());

		boolean isExecutable;
		boolean isStaticLibrary;
		IBuildPropertyValue propertyValue = fConfig.getBuildArtefactType();
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
			isExecutable = true;
			isStaticLibrary = false;
		}

		IConfiguration config = getCfg(cfgd.getConfiguration());
		if (config instanceof MultiConfiguration) {
			MultiConfiguration multi = (MultiConfiguration) config;

			// Take the first config in the multi-config
			config = (IConfiguration) multi.getItems()[0];
		}

		updateControlsForConfig(config);

		fCommandArText.setEnabled(isStaticLibrary);

		fCommandObjcopyText.setEnabled(isExecutable);
		fCommandObjdumpText.setEnabled(isExecutable);
		fCommandSizeText.setEnabled(isExecutable);

		fFlashButton.setEnabled(isExecutable);
		fListingButton.setEnabled(isExecutable);
		fSizeButton.setEnabled(isExecutable);

		useGlobalChanged();
	}

	@Override
	protected void performApply(ICResourceDescription src,
			ICResourceDescription dst) {

		System.out.println("Toolchains.performApply() " + src.getName());
		IConfiguration config = getCfg(src.getConfiguration());

		updateOptions(config);
		// does not work like this
		// SpecsProvider.clear();

		// System.out.println("performApply()");
	}

	@Override
	protected void performOK() {

		IConfiguration config = getCfg();
		System.out.println("Toolchains.performOK() " + config);

		if (fLastUpdatedConfig.equals(config)) {
			updateOptions(config);
		} else {
			System.out.println("skipped " + fConfig);
		}
	}

	private void updateControlsForConfig(IConfiguration config) {

		System.out.println("Toolchains.updateControlsForConfig() "
				+ config.getName());

		// int fSelectedToolchainIndex;
		// String fSelectedToolchainName;

		// create the selection array
		String[] toolchains = new String[ToolchainDefinition.getSize()];
		for (int i = 0; i < ToolchainDefinition.getSize(); ++i) {
			toolchains[i] = ToolchainDefinition.getToolchain(i).getFullName();
		}
		fToolchainCombo.setItems(toolchains);

		fSelectedToolchainName = Option.getOptionStringValue(config,
				Option.OPTION_TOOLCHAIN_NAME);

		// System.out
		// .println("Previous toolchain name " + fSelectedToolchainName);
		if (fSelectedToolchainName != null
				&& fSelectedToolchainName.length() > 0) {
			fSelectedToolchainIndex = ToolchainDefinition
					.findToolchainByName(fSelectedToolchainName);
		} else {
			System.out.println("No toolchain selected");
			// This is not a project created with the wizard
			// (most likely it is the result of a toolchain change)
			fSelectedToolchainIndex = ToolchainDefinition.getDefault();
			fSelectedToolchainName = ToolchainDefinition.getToolchain(
					fSelectedToolchainIndex).getName();

			// Initialise .cproject options that were not done at project
			// creation by the toolchain wizard
			try {
				setOptionsForToolchain(config, fSelectedToolchainIndex);

			} catch (BuildException e1) {
				System.out.println("cannot setOptionsForToolchain");
				// e1.printStackTrace();
			}
		}

		String toolchainSel = toolchains[fSelectedToolchainIndex];
		fToolchainCombo.setText(toolchainSel);

		ToolchainDefinition toolchainDefinition = ToolchainDefinition
				.getToolchain(fSelectedToolchainIndex);

		fArchitectureCombo.setItems(ToolchainDefinition.getArchitectures());

		String sSelectedArchitecture = Option.getOptionStringValue(config,
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
		fArchitectureCombo.setText(ToolchainDefinition.getArchitecture(index));

		String prefix = Option.getOptionStringValue(config,
				Option.OPTION_COMMAND_PREFIX);
		if (prefix != null) {
			fPrefixText.setText(prefix);
		} else {
			fPrefixText.setText(toolchainDefinition.getPrefix());
		}

		String suffix = Option.getOptionStringValue(config,
				Option.OPTION_COMMAND_SUFFIX);
		if (suffix != null) {
			fSuffixText.setText(suffix);
		} else {
			fSuffixText.setText(toolchainDefinition.getSuffix());
		}

		String commandC = Option.getOptionStringValue(config,
				Option.OPTION_COMMAND_C);
		if (commandC != null) {
			fCommandCText.setText(commandC);
		} else {
			fCommandCText.setText(toolchainDefinition.getCmdC());
		}

		String commandCpp = Option.getOptionStringValue(config,
				Option.OPTION_COMMAND_CPP);
		if (commandCpp != null) {
			fCommandCppText.setText(commandCpp);
		} else {
			fCommandCppText.setText(toolchainDefinition.getCmdCpp());
		}

		String commandAr = Option.getOptionStringValue(config,
				Option.OPTION_COMMAND_AR);
		if (commandAr != null) {
			fCommandArText.setText(commandAr);
		} else {
			fCommandArText.setText(toolchainDefinition.getCmdAr());
		}

		String commandObjcopy = Option.getOptionStringValue(config,
				Option.OPTION_COMMAND_OBJCOPY);
		if (commandObjcopy != null) {
			fCommandObjcopyText.setText(commandObjcopy);
		} else {
			fCommandObjcopyText.setText(toolchainDefinition.getCmdObjcopy());
		}

		String commandObjdump = Option.getOptionStringValue(config,
				Option.OPTION_COMMAND_OBJDUMP);
		if (commandObjdump != null) {
			fCommandObjdumpText.setText(commandObjdump);
		} else {
			fCommandObjdumpText.setText(toolchainDefinition.getCmdObjdump());
		}

		String commandSize = Option.getOptionStringValue(config,
				Option.OPTION_COMMAND_SIZE);
		if (commandSize != null) {
			fCommandSizeText.setText(commandSize);
		} else {
			fCommandSizeText.setText(toolchainDefinition.getCmdSize());
		}

		String commandMake = Option.getOptionStringValue(config,
				Option.OPTION_COMMAND_MAKE);
		if (commandMake != null) {
			fCommandMakeText.setText(commandMake);
		} else {
			fCommandMakeText.setText(toolchainDefinition.getCmdMake());
		}

		String commandRm = Option.getOptionStringValue(config,
				Option.OPTION_COMMAND_RM);
		if (commandRm != null) {
			fCommandRmText.setText(commandRm);
		} else {
			fCommandRmText.setText(toolchainDefinition.getCmdRm());
		}

		// Initialise field from per project storage
		boolean useGlobalPath = !ProjectStorage
				.isToolchainPathPerProject(config);

		fUseGlobalCheckButton.setSelection(useGlobalPath);

		String path = PersistentPreferences.getToolchainPath(
				fSelectedToolchainName, fProject);
		fGlobalPathText.setText(path);

		String toolchainPath = ProjectStorage.getToolchainPath(config);

		if (toolchainPath != null) {
			fProjectPathText.setText(toolchainPath);
		} else {
			fProjectPathText.setText("");
		}

		Boolean isCreateFlash = Option.getOptionBooleanValue(config,
				Option.OPTION_ADDTOOLS_CREATEFLASH);
		if (isCreateFlash != null) {
			fFlashButton.setSelection(isCreateFlash);
		} else {
			fFlashButton
					.setSelection(Option.OPTION_ADDTOOLS_CREATEFLASH_DEFAULT);
		}

		Boolean isCreateListing = Option.getOptionBooleanValue(config,
				Option.OPTION_ADDTOOLS_CREATELISTING);
		if (isCreateListing != null) {
			fListingButton.setSelection(isCreateListing);
		} else {
			fListingButton
					.setSelection(Option.OPTION_ADDTOOLS_CREATELISTING_DEFAULT);
		}

		Boolean isPrintSize = Option.getOptionBooleanValue(config,
				Option.OPTION_ADDTOOLS_PRINTSIZE);
		if (isPrintSize != null) {
			fSizeButton.setSelection(isPrintSize);
		} else {
			fSizeButton.setSelection(Option.OPTION_ADDTOOLS_PRINTSIZE_DEFAULT);
		}

		fConfig = config;
		fProject = (IProject) fConfig.getManagedProject().getOwner();
		System.out.println("updateControlsForConfig() fConfig=" + fConfig);

		fLastUpdatedConfig = config;
	}

	private void updateOptions(IConfiguration config) {

		System.out.println("Toolchains.updateOptions() " + config.getName());

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

			String sSelectedArchitecture = fArchitectureCombo.getText();
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

			String sSelectedCombo = fToolchainCombo.getText();
			int index = ToolchainDefinition
					.findToolchainByFullName(sSelectedCombo);
			ToolchainDefinition td = ToolchainDefinition.getToolchain(index);
			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME); //$NON-NLS-1$
			config.setOption(toolchain, option, td.getName());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_PREFIX); //$NON-NLS-1$
			config.setOption(toolchain, option, fPrefixText.getText().trim());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_SUFFIX); //$NON-NLS-1$
			config.setOption(toolchain, option, fSuffixText.getText().trim());

			option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_C); //$NON-NLS-1$
			config.setOption(toolchain, option, fCommandCText.getText().trim());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_CPP); //$NON-NLS-1$
			config.setOption(toolchain, option, fCommandCppText.getText()
					.trim());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_AR); //$NON-NLS-1$
			config.setOption(toolchain, option, fCommandArText.getText().trim());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJCOPY); //$NON-NLS-1$
			config.setOption(toolchain, option, fCommandObjcopyText.getText()
					.trim());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJDUMP); //$NON-NLS-1$
			config.setOption(toolchain, option, fCommandObjdumpText.getText()
					.trim());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_SIZE); //$NON-NLS-1$
			config.setOption(toolchain, option, fCommandSizeText.getText()
					.trim());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_MAKE); //$NON-NLS-1$
			config.setOption(toolchain, option, fCommandMakeText.getText()
					.trim());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_COMMAND_RM); //$NON-NLS-1$
			String oldValue = option.getStringValue();
			String newValue = fCommandRmText.getText().trim();

			if (newValue != null && !newValue.equals(oldValue)) {
				config.setOption(toolchain, option, newValue);

				// propagate is expensive, run it only if needed
				propagateCommandRmUpdate(config);
			}

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATEFLASH); //$NON-NLS-1$
			config.setOption(toolchain, option, fFlashButton.getSelection());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATELISTING); //$NON-NLS-1$
			config.setOption(toolchain, option, fListingButton.getSelection());

			option = toolchain
					.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_PRINTSIZE); //$NON-NLS-1$
			config.setOption(toolchain, option, fSizeButton.getSelection());

			ProjectStorage.putToolchainPathPerProject(config,
					!fUseGlobalCheckButton.getSelection());

			ProjectStorage.putToolchainPath(config, fProjectPathText.getText()
					.trim());

			String sGlobalToolchainPath = PersistentPreferences
					.getToolchainPath(td.getName(), fProject);
			String sNewToolchainPath = fGlobalPathText.getText().trim();

			if (sGlobalToolchainPath.length() == 0
					|| !sGlobalToolchainPath.equals(sNewToolchainPath)) {
				PersistentPreferences.putToolchainPath(td.getName(),
						sNewToolchainPath);
				PersistentPreferences.flush();
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

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_AR); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdAr());

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJCOPY); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdObjcopy());

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJDUMP); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdObjdump());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_SIZE); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdSize());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_MAKE); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdMake());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_RM); //$NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdRm());

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATEFLASH); //$NON-NLS-1$
		config.setOption(toolchain, option,
				Option.OPTION_ADDTOOLS_CREATEFLASH_DEFAULT);

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATELISTING); //$NON-NLS-1$
		config.setOption(toolchain, option,
				Option.OPTION_ADDTOOLS_CREATELISTING_DEFAULT);

		option = toolchain
				.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_PRINTSIZE); //$NON-NLS-1$
		config.setOption(toolchain, option,
				Option.OPTION_ADDTOOLS_PRINTSIZE_DEFAULT);

		// do not set the project toolchain path
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
					System.out.println("propagateCommandRmUpdate "
							+ e.getMessage());
				}

			}
		}
	}

	@Override
	protected void performDefaults() {

		System.out.println("Toolchains.performDefaults()");
		updateInterfaceAfterToolchainChange();

		fFlashButton.setSelection(Option.OPTION_ADDTOOLS_CREATEFLASH_DEFAULT);
		fListingButton
				.setSelection(Option.OPTION_ADDTOOLS_CREATELISTING_DEFAULT);
		fSizeButton.setSelection(Option.OPTION_ADDTOOLS_PRINTSIZE_DEFAULT);
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
		fConfig = getCfg();
		fProject = (IProject) fConfig.getManagedProject().getOwner();
		System.out.println("isThisPlugin() fConfig=" + fConfig);

		IToolChain toolchain = fConfig.getToolChain();
		String sToolchainId = toolchain.getBaseId();
		if (sToolchainId.startsWith(IDs.TOOLCHAIN_ID + "."))
			return true;

		return false;
	}

	// ------------------------------------------------------------------------
}
