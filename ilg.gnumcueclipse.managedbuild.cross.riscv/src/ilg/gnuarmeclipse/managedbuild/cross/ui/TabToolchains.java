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

import org.eclipse.cdt.core.settings.model.ICResourceDescription;
import org.eclipse.cdt.managedbuilder.buildproperties.IBuildPropertyValue;
import org.eclipse.cdt.managedbuilder.core.BuildException;
import org.eclipse.cdt.managedbuilder.core.IConfiguration;
import org.eclipse.cdt.managedbuilder.core.IManagedProject;
import org.eclipse.cdt.managedbuilder.core.IOption;
import org.eclipse.cdt.managedbuilder.core.IProjectType;
import org.eclipse.cdt.managedbuilder.core.IToolChain;
import org.eclipse.cdt.managedbuilder.internal.core.MultiConfiguration;
import org.eclipse.cdt.managedbuilder.makegen.IManagedBuilderMakefileGenerator;
import org.eclipse.cdt.managedbuilder.makegen.gnu.GnuMakefileGenerator;
import org.eclipse.cdt.managedbuilder.ui.properties.AbstractCBuildPropertyTab;
import org.eclipse.cdt.managedbuilder.ui.properties.Page_BuildSettings;
import org.eclipse.cdt.ui.newui.ICPropertyProvider2;
import org.eclipse.cdt.ui.newui.ICPropertyTab;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.managedbuild.cross.Activator;
import ilg.gnuarmeclipse.managedbuild.cross.IDs;
import ilg.gnuarmeclipse.managedbuild.cross.Option;
import ilg.gnuarmeclipse.managedbuild.cross.ToolchainDefinition;
import ilg.gnuarmeclipse.managedbuild.cross.Utils;
import ilg.gnuarmeclipse.managedbuild.cross.preferences.GlobalToolsPathsPreferencePage;
import ilg.gnuarmeclipse.managedbuild.cross.preferences.WorkspaceToolsPathsPreferencePage;
import ilg.gnuarmeclipse.managedbuild.cross.properties.ProjectToolsPathPropertyPage;

/**
 * @noextend This class is not intended to be subclassed by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
@SuppressWarnings("restriction")
public class TabToolchains extends AbstractCBuildPropertyTab {

	// ------------------------------------------------------------------------

	// private Composite fComposite;
	private IConfiguration fConfig;
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
	private Text fPathLabel;

	private Button fFlashButton;
	private Button fListingButton;
	private Button fSizeButton;

	// private boolean fIsExecutable;
	// private boolean fIsStaticLibrary;

	private static int WIDTH_HINT = 120;

	// ------------------------------------------------------------------------

	protected IProject getProject() {
		assert (fConfig != null);
		return (IProject) fConfig.getManagedProject().getOwner();
	}

	protected String getSelectedToolchainName() {

		assert (fToolchainCombo != null);

		int index;
		try {
			String sSelectedCombo = fToolchainCombo.getText();
			index = ToolchainDefinition.findToolchainByFullName(sSelectedCombo);
		} catch (NullPointerException e) {
			index = 0;
		}
		ToolchainDefinition td = ToolchainDefinition.getToolchain(index);

		return td.getName();
	}

	@Override
	public void createControls(final Composite parent) {

		if (EclipseUtils.isLinux()) {
			WIDTH_HINT = 150;
		}

		// fComposite = parent;
		// Disabled, otherwise toolchain changes fail
		if (Activator.getInstance().isDebugging()) {
			System.out.println("TabToolchains.createControls()");
		}

		if (!page.isForProject()) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("not this project");
			}
			return;
		}

		super.createControls(parent);

		fConfig = getCfg();

		if (Activator.getInstance().isDebugging()) {
			System.out.println("createControls() fConfig=" + fConfig);
		}

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

		if (isManaged()) {
			// ----- Command ar
			// ---------------------------------------------------
			Label commandArLabel = new Label(usercomp, SWT.NONE);
			commandArLabel.setText(Messages.ToolChainSettingsTab_arCmd);

			fCommandArText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			layoutData = new GridData();
			layoutData.horizontalSpan = 2;
			layoutData.widthHint = WIDTH_HINT;
			fCommandArText.setLayoutData(layoutData);

			// ----- Command objcopy ------------------------------------------
			Label commandObjcopyLabel = new Label(usercomp, SWT.NONE);
			commandObjcopyLabel.setText(Messages.ToolChainSettingsTab_objcopyCmd);

			fCommandObjcopyText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			layoutData = new GridData();
			layoutData.horizontalSpan = 2;
			layoutData.widthHint = WIDTH_HINT;
			fCommandObjcopyText.setLayoutData(layoutData);

			// ----- Command objdump ------------------------------------------
			Label commandObjdumpLabel = new Label(usercomp, SWT.NONE);
			commandObjdumpLabel.setText(Messages.ToolChainSettingsTab_objdumpCmd);

			fCommandObjdumpText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			layoutData = new GridData();
			layoutData.horizontalSpan = 2;
			layoutData.widthHint = WIDTH_HINT;
			fCommandObjdumpText.setLayoutData(layoutData);

			// ----- Command size ---------------------------------------------
			Label commandSizeLabel = new Label(usercomp, SWT.NONE);
			commandSizeLabel.setText(Messages.ToolChainSettingsTab_sizeCmd);

			fCommandSizeText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			layoutData = new GridData();
			layoutData.horizontalSpan = 2;
			layoutData.widthHint = WIDTH_HINT;
			fCommandSizeText.setLayoutData(layoutData);

			// ----- Command make ---------------------------------------------
			Label commandMakeLabel = new Label(usercomp, SWT.NONE);
			commandMakeLabel.setText(Messages.ToolChainSettingsTab_makeCmd);

			fCommandMakeText = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			layoutData = new GridData();
			layoutData.horizontalSpan = 2;
			layoutData.widthHint = WIDTH_HINT;
			fCommandMakeText.setLayoutData(layoutData);

			// ----- Command rm -----------------------------------------------
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
		} else {
			Label label = new Label(usercomp, SWT.NONE);
			label.setText("");

			Label link = new Label(usercomp, SWT.NONE);
			link.setText(Messages.ToolChainSettingsTab_warning_link);
			layoutData = new GridData();
			layoutData.horizontalSpan = 2;
			link.setLayoutData(layoutData);
		}

		{
			Label empty = new Label(usercomp, SWT.NONE);
			empty.setText("");
			layoutData = new GridData();
			layoutData.horizontalSpan = 3;
			empty.setLayoutData(layoutData);
		}

		{
			Label label = new Label(usercomp, SWT.NONE);
			label.setText(Messages.ToolChainSettingsTab_path_label);

			fPathLabel = new Text(usercomp, SWT.SINGLE | SWT.BORDER);
			layoutData = new GridData(SWT.FILL, 0, true, false);
			layoutData.horizontalSpan = 2;
			fPathLabel.setLayoutData(layoutData);

			fPathLabel.setEnabled(true);
			fPathLabel.setEditable(false);
		}

		{
			Label label = new Label(usercomp, SWT.NONE);
			label.setText("");

			Link link = new Link(usercomp, SWT.NONE);
			link.setText(Messages.ToolChainSettingsTab_path_link);
			layoutData = new GridData();
			layoutData.horizontalSpan = 2;
			link.setLayoutData(layoutData);

			link.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {

					String text = e.text;
					if (Activator.getInstance().isDebugging()) {
						System.out.println(text);
					}

					int ret = -1;
					if ("global".equals(text)) {
						ret = PreferencesUtil.createPreferenceDialogOn(parent.getShell(),
								GlobalToolsPathsPreferencePage.ID, null, null).open();
					} else if ("workspace".equals(text)) {
						ret = PreferencesUtil.createPreferenceDialogOn(parent.getShell(),
								WorkspaceToolsPathsPreferencePage.ID, null, null).open();
					} else if ("project".equals(text)) {
						ret = PreferencesUtil.createPropertyDialogOn(parent.getShell(), getProject(),
								ProjectToolsPathPropertyPage.ID, null, null, 0).open();
					}

					if (ret == Window.OK) {
						updateToolchainPath(getSelectedToolchainName());
					}
				}
			});
		}

		{
			Label empty = new Label(usercomp, SWT.NONE);
			empty.setText("");
			layoutData = new GridData();
			layoutData.horizontalSpan = 3;
			empty.setLayoutData(layoutData);
		}

		if (isManaged()) {
			// ----- Flash ----------------------------------------------------
			fFlashButton = new Button(usercomp, SWT.CHECK);
			fFlashButton.setText(Messages.ToolChainSettingsTab_flash);
			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
			fFlashButton.setLayoutData(layoutData);

			// ----- Listing --------------------------------------------------
			fListingButton = new Button(usercomp, SWT.CHECK);
			fListingButton.setText(Messages.ToolChainSettingsTab_listing);
			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
			fListingButton.setLayoutData(layoutData);

			// ----- Size -----------------------------------------------------
			fSizeButton = new Button(usercomp, SWT.CHECK);
			fSizeButton.setText(Messages.ToolChainSettingsTab_size);
			layoutData = new GridData(SWT.LEFT, SWT.TOP, false, false, 3, 1);
			fSizeButton.setLayoutData(layoutData);

			// fIsCreated = true;
		}

		updateControlsForConfig(fConfig);

		// --------------------------------------------------------------------

	}

	private void updateInterfaceAfterToolchainChange() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("TabToolchains.updateInterfaceAfterToolchainChange()");
		}
		int index;
		try {
			String sSelectedCombo = fToolchainCombo.getText();
			index = ToolchainDefinition.findToolchainByFullName(sSelectedCombo);
		} catch (NullPointerException e) {
			index = 0;
		}
		ToolchainDefinition td = ToolchainDefinition.getToolchain(index);

		String sArchitecture = td.getArchitecture();
		if ("arm".equals(sArchitecture)) {
			index = 0;
		} else if ("aarch64".equals(sArchitecture)) {
			index = 1;
		} else {
			index = 0; // default is ARM
		}
		fArchitectureCombo.setText(ToolchainDefinition.getArchitecture(index));

		fPrefixText.setText(td.getPrefix());
		fSuffixText.setText(td.getSuffix());
		fCommandCText.setText(td.getCmdC());
		fCommandCppText.setText(td.getCmdCpp());

		if (isManaged()) {
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
		}

		updateToolchainPath(td.getName());
	}

	protected void updateToolchainPath(String toolchainName) {

		assert (fConfig != null);
		IProject project = (IProject) fConfig.getManagedProject().getOwner();
		String toolchainPath = PersistentPreferences.getToolchainPath(toolchainName, project);
		fPathLabel.setText(toolchainPath);
	}

	// This event comes when the tab is selected after the windows is
	// displayed, to account for content change
	// It also comes when the configuration is changed in the selection.
	@Override
	public void updateData(ICResourceDescription cfgd) {
		if (cfgd == null)
			return;

		// fConfig = getCfg();
		if (Activator.getInstance().isDebugging()) {
			System.out.println("TabToolchains.updateData() " + getCfg().getName());
		}

		boolean isExecutable;
		boolean isStaticLibrary;
		IBuildPropertyValue propertyValue = fConfig.getBuildArtefactType();
		if (propertyValue != null) {
			String artefactId = propertyValue.getId();
			if (Utils.BUILD_ARTEFACT_TYPE_EXE.equals(artefactId) || artefactId.endsWith(".exe"))
				isExecutable = true;
			else
				isExecutable = false;

			if (Utils.BUILD_ARTEFACT_TYPE_STATICLIB.equals(artefactId) || artefactId.endsWith("Lib"))
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

		if (isManaged()) {
			fCommandArText.setEnabled(isStaticLibrary);

			fCommandObjcopyText.setEnabled(isExecutable);
			fCommandObjdumpText.setEnabled(isExecutable);
			fCommandSizeText.setEnabled(isExecutable);

			fFlashButton.setEnabled(isExecutable);
			fListingButton.setEnabled(isExecutable);
			fSizeButton.setEnabled(isExecutable);
		}
	}

	@Override
	protected void performApply(ICResourceDescription src, ICResourceDescription dst) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("TabToolchains.performApply() " + src.getName());
		}

		// need to apply changes in both configurations
		// dst is the new description, will be used when saving changes on disk
		// (set project description)
		// src is the old description used in current page
		IConfiguration config1 = getCfg(src.getConfiguration());
		IConfiguration config2 = getCfg(dst.getConfiguration());
		updateOptions(config1);
		updateOptions(config2);

		// does not work like this
		// SpecsProvider.clear();

		// System.out.println("performApply()");
	}

	@Override
	protected void performOK() {

		IConfiguration config = getCfg();
		if (Activator.getInstance().isDebugging()) {
			System.out.println("Toolchains.performOK() " + config);
		}

		if (fLastUpdatedConfig != null && fLastUpdatedConfig.equals(config)) {
			updateOptions(config);
		} else {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("skipped " + fConfig);
			}
		}
	}

	private void updateControlsForConfig(IConfiguration config) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Toolchains.updateControlsForConfig() " + config.getName());
		}

		// int fSelectedToolchainIndex;
		// String fSelectedToolchainName;

		if (!isThisPlugin()) {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("not this plugin");
			}
			return;
		}

		// create the selection array
		String[] toolchains = new String[ToolchainDefinition.getSize()];
		for (int i = 0; i < ToolchainDefinition.getSize(); ++i) {
			toolchains[i] = ToolchainDefinition.getToolchain(i).getFullName();
		}
		fToolchainCombo.setItems(toolchains);

		fSelectedToolchainName = Option.getOptionStringValue(config, Option.OPTION_TOOLCHAIN_NAME);

		// System.out
		// .println("Previous toolchain name " + fSelectedToolchainName);
		if (fSelectedToolchainName != null && fSelectedToolchainName.length() > 0) {
			try {
				fSelectedToolchainIndex = ToolchainDefinition.findToolchainByName(fSelectedToolchainName);
			} catch (IndexOutOfBoundsException e) {
				fSelectedToolchainIndex = ToolchainDefinition.getDefault();
			}
		} else {
			if (Activator.getInstance().isDebugging()) {
				System.out.println("No toolchain selected");
			}
			// This is not a project created with the wizard
			// (most likely it is the result of a toolchain change)
			fSelectedToolchainName = PersistentPreferences.getToolchainName();
			fSelectedToolchainIndex = ToolchainDefinition.findToolchainByName(fSelectedToolchainName);

			// Initialise .cproject options that were not done at project
			// creation by the toolchain wizard
			try {
				setOptionsForToolchain(config, fSelectedToolchainIndex);
			} catch (BuildException e1) {
				Activator.log(e1);
			}
		}

		String toolchainSel = toolchains[fSelectedToolchainIndex];
		fToolchainCombo.setText(toolchainSel);

		ToolchainDefinition toolchainDefinition = ToolchainDefinition.getToolchain(fSelectedToolchainIndex);

		fArchitectureCombo.setItems(ToolchainDefinition.getArchitectures());

		String sSelectedArchitecture = Option.getOptionStringValue(config, Option.OPTION_ARCHITECTURE);
		int index;
		try {
			if (sSelectedArchitecture.endsWith("." + Option.ARCHITECTURE_ARM))
				index = 0;
			else if (sSelectedArchitecture.endsWith("." + Option.ARCHITECTURE_AARCH64))
				index = 1;
			else
				index = 0; // default is ARM
		} catch (NullPointerException e) {
			index = 0; // default is ARM
		}
		fArchitectureCombo.setText(ToolchainDefinition.getArchitecture(index));

		String prefix = Option.getOptionStringValue(config, Option.OPTION_COMMAND_PREFIX);
		if (prefix != null) {
			fPrefixText.setText(prefix);
		} else {
			fPrefixText.setText(toolchainDefinition.getPrefix());
		}

		String suffix = Option.getOptionStringValue(config, Option.OPTION_COMMAND_SUFFIX);
		if (suffix != null) {
			fSuffixText.setText(suffix);
		} else {
			fSuffixText.setText(toolchainDefinition.getSuffix());
		}

		String commandC = Option.getOptionStringValue(config, Option.OPTION_COMMAND_C);
		if (commandC != null) {
			fCommandCText.setText(commandC);
		} else {
			fCommandCText.setText(toolchainDefinition.getCmdC());
		}

		String commandCpp = Option.getOptionStringValue(config, Option.OPTION_COMMAND_CPP);
		if (commandCpp != null) {
			fCommandCppText.setText(commandCpp);
		} else {
			fCommandCppText.setText(toolchainDefinition.getCmdCpp());
		}

		if (isManaged()) {
			String commandAr = Option.getOptionStringValue(config, Option.OPTION_COMMAND_AR);
			if (commandAr != null) {
				fCommandArText.setText(commandAr);
			} else {
				fCommandArText.setText(toolchainDefinition.getCmdAr());
			}

			String commandObjcopy = Option.getOptionStringValue(config, Option.OPTION_COMMAND_OBJCOPY);
			if (commandObjcopy != null) {
				fCommandObjcopyText.setText(commandObjcopy);
			} else {
				fCommandObjcopyText.setText(toolchainDefinition.getCmdObjcopy());
			}

			String commandObjdump = Option.getOptionStringValue(config, Option.OPTION_COMMAND_OBJDUMP);
			if (commandObjdump != null) {
				fCommandObjdumpText.setText(commandObjdump);
			} else {
				fCommandObjdumpText.setText(toolchainDefinition.getCmdObjdump());
			}

			String commandSize = Option.getOptionStringValue(config, Option.OPTION_COMMAND_SIZE);
			if (commandSize != null) {
				fCommandSizeText.setText(commandSize);
			} else {
				fCommandSizeText.setText(toolchainDefinition.getCmdSize());
			}

			String commandMake = Option.getOptionStringValue(config, Option.OPTION_COMMAND_MAKE);
			if (commandMake != null) {
				fCommandMakeText.setText(commandMake);
			} else {
				fCommandMakeText.setText(toolchainDefinition.getCmdMake());
			}

			String commandRm = Option.getOptionStringValue(config, Option.OPTION_COMMAND_RM);
			if (commandRm != null) {
				fCommandRmText.setText(commandRm);
			} else {
				fCommandRmText.setText(toolchainDefinition.getCmdRm());
			}

			Boolean isCreateFlash = Option.getOptionBooleanValue(config, Option.OPTION_ADDTOOLS_CREATEFLASH);
			if (isCreateFlash != null) {
				fFlashButton.setSelection(isCreateFlash);
			} else {
				fFlashButton.setSelection(Option.OPTION_ADDTOOLS_CREATEFLASH_DEFAULT);
			}

			Boolean isCreateListing = Option.getOptionBooleanValue(config, Option.OPTION_ADDTOOLS_CREATELISTING);
			if (isCreateListing != null) {
				fListingButton.setSelection(isCreateListing);
			} else {
				fListingButton.setSelection(Option.OPTION_ADDTOOLS_CREATELISTING_DEFAULT);
			}

			Boolean isPrintSize = Option.getOptionBooleanValue(config, Option.OPTION_ADDTOOLS_PRINTSIZE);
			if (isPrintSize != null) {
				fSizeButton.setSelection(isPrintSize);
			} else {
				fSizeButton.setSelection(Option.OPTION_ADDTOOLS_PRINTSIZE_DEFAULT);
			}
		}

		fConfig = config;
		if (Activator.getInstance().isDebugging()) {
			System.out.println("updateControlsForConfig() fConfig=" + fConfig);
		}

		fLastUpdatedConfig = config;

		updateToolchainPath(toolchainDefinition.getName());
	}

	private void updateOptions(IConfiguration config) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Toolchains.updateOptions() " + config.getName());
		}

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
			if (ToolchainDefinition.getArchitecture(0).equals(sSelectedArchitecture)) {
				val = Option.OPTION_ARCHITECTURE_ARM;
			} else if (ToolchainDefinition.getArchitecture(1).equals(sSelectedArchitecture)) {
				val = Option.OPTION_ARCHITECTURE_AARCH64;
			} else {
				val = Option.OPTION_ARCHITECTURE_ARM; // default is ARM
			}
			option = toolchain.getOptionBySuperClassId(Option.OPTION_ARCHITECTURE); // $NON-NLS-1$
			config.setOption(toolchain, option, val);

			String sSelectedCombo = fToolchainCombo.getText();
			int index = ToolchainDefinition.findToolchainByFullName(sSelectedCombo);
			ToolchainDefinition td = ToolchainDefinition.getToolchain(index);
			option = toolchain.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME); // $NON-NLS-1$
			config.setOption(toolchain, option, td.getName());

			option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_PREFIX); // $NON-NLS-1$
			config.setOption(toolchain, option, fPrefixText.getText().trim());

			option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_SUFFIX); // $NON-NLS-1$
			config.setOption(toolchain, option, fSuffixText.getText().trim());

			option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_C); // $NON-NLS-1$
			config.setOption(toolchain, option, fCommandCText.getText().trim());

			option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_CPP); // $NON-NLS-1$
			config.setOption(toolchain, option, fCommandCppText.getText().trim());

			if (isManaged()) {
				option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_AR); // $NON-NLS-1$
				config.setOption(toolchain, option, fCommandArText.getText().trim());

				option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJCOPY); // $NON-NLS-1$
				config.setOption(toolchain, option, fCommandObjcopyText.getText().trim());

				option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJDUMP); // $NON-NLS-1$
				config.setOption(toolchain, option, fCommandObjdumpText.getText().trim());

				option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_SIZE); // $NON-NLS-1$
				config.setOption(toolchain, option, fCommandSizeText.getText().trim());

				option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_MAKE); // $NON-NLS-1$
				config.setOption(toolchain, option, fCommandMakeText.getText().trim());

				option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_RM); // $NON-NLS-1$
				String oldValue = option.getStringValue();
				String newValue = fCommandRmText.getText().trim();

				if (newValue != null && !newValue.equals(oldValue)) {
					config.setOption(toolchain, option, newValue);

					// propagate is expensive, run it only if needed
					propagateCommandRmUpdate(config);
				}

				option = toolchain.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATEFLASH); // $NON-NLS-1$
				config.setOption(toolchain, option, fFlashButton.getSelection());

				option = toolchain.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATELISTING); // $NON-NLS-1$
				config.setOption(toolchain, option, fListingButton.getSelection());

				option = toolchain.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_PRINTSIZE); // $NON-NLS-1$
				config.setOption(toolchain, option, fSizeButton.getSelection());
			}

		} catch (NullPointerException e) {
			Activator.log(e);
		} catch (BuildException e) {
			Activator.log(e);
		}

	}

	// Used in SetCrossCommandOperation to set toolchain specific options
	// after wizard selection. The compiler command name must be set as
	// early as possible.
	public static void setOptionsForToolchain(IConfiguration config, int toolchainIndex) throws BuildException {

		IToolChain toolchain = config.getToolChain();

		IOption option;
		String val;

		ToolchainDefinition td = ToolchainDefinition.getToolchain(toolchainIndex);

		// Do NOT use ManagedBuildManager.setOption() to avoid sending
		// events to the option. Also do not use option.setValue()
		// since this does not propagate notifications and the
		// values are not saved to .cproject.
		option = toolchain.getOptionBySuperClassId(Option.OPTION_TOOLCHAIN_NAME); // $NON-NLS-1$
		config.setOption(toolchain, option, td.getName());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_ARCHITECTURE); // $NON-NLS-1$
		// compose the architecture ID
		String sArchitecture = td.getArchitecture();
		val = Option.OPTION_ARCHITECTURE + "." + sArchitecture;
		Utils.setOptionForced(config, toolchain, option, val);

		if ("arm".equals(sArchitecture)) {
			option = toolchain.getOptionBySuperClassId(Option.OPTION_ARM_TARGET_FAMILY);
			Utils.forceOptionRewrite(config, toolchain, option);

			option = toolchain.getOptionBySuperClassId(Option.OPTION_ARM_TARGET_INSTRUCTIONSET);
			Utils.forceOptionRewrite(config, toolchain, option);
		} else if ("aarch64".equals(sArchitecture)) {
			option = toolchain.getOptionBySuperClassId(Option.OPTION_AARCH64_TARGET_FAMILY);
			Utils.setOptionForced(config, toolchain, option, Option.OPTION_AARCH64_MCPU_GENERIC);

			option = toolchain.getOptionBySuperClassId(Option.OPTION_AARCH64_FEATURE_SIMD);
			Utils.setOptionForced(config, toolchain, option, Option.OPTION_AARCH64_FEATURE_SIMD_ENABLED);

			option = toolchain.getOptionBySuperClassId(Option.OPTION_AARCH64_CMODEL);
			Utils.setOptionForced(config, toolchain, option, Option.OPTION_AARCH64_CMODEL_SMALL);
		}

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_PREFIX); // $NON-NLS-1$
		config.setOption(toolchain, option, td.getPrefix());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_SUFFIX); // $NON-NLS-1$
		config.setOption(toolchain, option, td.getSuffix());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_C); // $NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdC());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_CPP); // $NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdCpp());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_AR); // $NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdAr());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJCOPY); // $NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdObjcopy());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_OBJDUMP); // $NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdObjdump());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_SIZE); // $NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdSize());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_MAKE); // $NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdMake());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_COMMAND_RM); // $NON-NLS-1$
		config.setOption(toolchain, option, td.getCmdRm());

		option = toolchain.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATEFLASH); // $NON-NLS-1$
		config.setOption(toolchain, option, Option.OPTION_ADDTOOLS_CREATEFLASH_DEFAULT);

		option = toolchain.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_CREATELISTING); // $NON-NLS-1$
		config.setOption(toolchain, option, Option.OPTION_ADDTOOLS_CREATELISTING_DEFAULT);

		option = toolchain.getOptionBySuperClassId(Option.OPTION_ADDTOOLS_PRINTSIZE); // $NON-NLS-1$
		config.setOption(toolchain, option, Option.OPTION_ADDTOOLS_PRINTSIZE_DEFAULT);

		// do not set the project toolchain path
	}

	private void propagateCommandRmUpdate(IConfiguration config) {
		// System.out.println("propagateCommandRmUpdate()");
		if (true) {
			IProject project = (IProject) config.getOwner();

			IPath makefilePath = project.getFullPath().append(config.getName())
					.append(IManagedBuilderMakefileGenerator.MAKEFILE_NAME);
			IResource makefileResource = project.findMember(makefilePath.removeFirstSegments(1));
			if (makefileResource != null && makefileResource.exists()) {
				try {
					makefileResource.delete(true, new NullProgressMonitor());

					GnuMakefileGenerator makefileGenerator = new GnuMakefileGenerator();
					makefileGenerator.initialize(0, config, config.getBuilder(), new NullProgressMonitor());
					makefileGenerator.regenerateMakefiles();
				} catch (CoreException e) {
					Activator.log(e.getStatus());
				}
			}
		}
	}

	@Override
	protected void performDefaults() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("Toolchains.performDefaults()");
		}
		updateInterfaceAfterToolchainChange();

		if (isManaged()) {
			fFlashButton.setSelection(Option.OPTION_ADDTOOLS_CREATEFLASH_DEFAULT);
			fListingButton.setSelection(Option.OPTION_ADDTOOLS_CREATELISTING_DEFAULT);
			fSizeButton.setSelection(Option.OPTION_ADDTOOLS_PRINTSIZE_DEFAULT);
			// System.out.println("performDefaults()");
		}
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
		// Do nothing. No buttons to update.
	}

	private boolean isThisPlugin() {

		fConfig = getCfg();
		if (Activator.getInstance().isDebugging()) {
			System.out.println("isThisPlugin() fConfig=" + fConfig);
		}

		IToolChain toolchain = fConfig.getToolChain();
		String sToolchainId = toolchain.getBaseId();
		if (!sToolchainId.startsWith(IDs.TOOLCHAIN_ID + ".")) {
			return false;
		}

		return true;
	}

	public static final String TYPE_PREFIX = "ilg.gnuarmeclipse.managedbuild.cross.target.";

	private boolean isManaged() {

		fConfig = getCfg();

		IManagedProject managedProject = fConfig.getManagedProject();
		IProjectType projectType = managedProject.getProjectType();

		if (projectType == null || !projectType.getId().startsWith(TYPE_PREFIX)) {
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.cdt.ui.newui.AbstractCPropertyTab#handleTabEvent(int,
	 * java.lang.Object)
	 */
	@Override
	public void handleTabEvent(int kind, Object data) {
		super.handleTabEvent(kind, data);

		switch (kind) {
		case ICPropertyTab.UPDATE: {
			/*
			 * If the page needs updating fire the handleMessage() method. This
			 * redraws the tabs and if the page visibility has been modified the
			 * tab will be updated (removed or added). This is necessary to
			 * solved the following problem: When in project properties > C/C++
			 * Build > Settings, if you change to another configuration that
			 * uses a different toolchain (eg Linux GCC) using the Configuration
			 * combo (at the top of the dialog) the tabs in Settings do not get
			 * updated. Note, data is not used so set to null.
			 */
			Page_BuildSettings pageConcrete = (Page_BuildSettings) ((ICPropertyProvider2) page);
			pageConcrete.handleMessage(ICPropertyTab.MANAGEDBUILDSTATE, null);
		}
			break;
		default:
			break;
		}
	}

	// ------------------------------------------------------------------------
}
