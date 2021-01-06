/*******************************************************************************
 * Copyright (c) 2007 - 2010 QNX Software Systems and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Andy Jin - Hardware debugging UI improvements, bug 229946
 *     Andy Jin - Added DSF debugging, bug 248593
 *     Liviu Ionescu - Arm version
 *     Liviu Ionescu - UI part extraction.
 *******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.qemu.ui;

import java.io.File;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.ui.GDBJtagImages;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.embedcdt.debug.gdbjtag.core.DebugUtils;
import org.eclipse.embedcdt.debug.gdbjtag.qemu.core.ConfigurationAttributes;
import org.eclipse.embedcdt.debug.gdbjtag.qemu.core.preferences.DefaultPreferences;
import org.eclipse.embedcdt.debug.gdbjtag.qemu.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui.Activator;
import org.eclipse.embedcdt.internal.debug.gdbjtag.qemu.ui.Messages;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

public class TabStartup extends AbstractLaunchConfigurationTab {

	// ------------------------------------------------------------------------

	private static final String TAB_NAME = "Startup";
	private static final String TAB_ID = Activator.PLUGIN_ID + ".ui.startuptab";

	// ------------------------------------------------------------------------

	private Text fInitCommands;

	private Button fDoFirstReset;

	private Button fDoSecondReset;
	private Label fSecondResetWarning;

	private Button fLoadExecutable;
	private Text fImageFileName;
	private Button fImageFileBrowseWs;
	private Button fImageFileBrowse;
	private Text fImageOffset;

	private Button fLoadSymbols;
	private Text fSymbolsFileName;

	private Button fSymbolsFileBrowseWs;
	private Button fSymbolsFileBrowse;
	private Text fSymbolsOffset;

	private Button fSetPcRegister;
	private Text fPcRegister;

	private Button fSetStopAt;
	private Text fStopAt;

	private Text fRunCommands;
	private Button fDoContinue;
	private Button fDoDebugInRam;

	// New GUI added to address bug 310304
	private Button fUseProjectBinaryForImage;
	private Button fUseFileForImage;
	private Button fUseProjectBinaryForSymbols;
	private Button fUseFileForSymbols;
	private Label fImageOffsetLabel;
	private Label fSymbolsOffsetLabel;
	private Label fProjBinaryLabel1;
	private Label fProjBinaryLabel2;

	private String fSavedProgName;

	private DefaultPreferences fDefaultPreferences;
	private PersistentPreferences fPersistentPreferences;

	// ------------------------------------------------------------------------

	public TabStartup() {
		super();

		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
		fPersistentPreferences = Activator.getInstance().getPersistentPreferences();
	}

	@Override
	public String getName() {
		return TAB_NAME;
	}

	@Override
	public Image getImage() {
		return GDBJtagImages.getStartupTabImage();
	}

	@Override
	public void createControl(Composite parent) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.TabStartup.createControl() ");
		}

		Composite comp = new Composite(parent, SWT.NONE);
		{
			setControl(comp);
			GridLayout layout = new GridLayout();
			comp.setLayout(layout);
		}

		createInitGroup(comp);
		createLoadGroup(comp);
		createRunOptionGroup(comp);
		createRunGroup(comp);

		Link restoreDefaults;
		{
			restoreDefaults = new Link(comp, SWT.NONE);
			restoreDefaults.setText(Messages.getString("DebuggerTab.restoreDefaults_Link"));
			restoreDefaults.setToolTipText(Messages.getString("DebuggerTab.restoreDefaults_ToolTipText"));

			GridData gd = new GridData();
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = SWT.RIGHT;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			restoreDefaults.setLayoutData(gd);
		}

		// --------------------------------------------------------------------

		restoreDefaults.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				initializeFromDefaults();
				scheduleUpdateJob();
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

	private void browseWsButtonSelected(String title, Text text) {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setTitle(title);
		dialog.setMessage(Messages.getString("StartupTab.FileBrowseWs_Message"));
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		if (dialog.open() == IDialogConstants.OK_ID) {
			IResource resource = (IResource) dialog.getFirstResult();
			String arg = resource.getFullPath().toOSString();
			String fileLoc = VariablesPlugin.getDefault().getStringVariableManager()
					.generateVariableExpression("workspace_loc", arg); //$NON-NLS-1$
			text.setText(fileLoc);
		}
	}

	public void createInitGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		{
			group.setText(Messages.getString("StartupTab.initGroup_Text"));
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
		}

		Composite comp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			layout.marginHeight = 0;
			comp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			comp.setLayoutData(gd);
		}

		{
			Composite local = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 3;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);
			// local.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			fDoFirstReset = new Button(local, SWT.CHECK);
			fDoFirstReset.setText(Messages.getString("StartupTab.doFirstReset_Text"));
			fDoFirstReset.setToolTipText(Messages.getString("StartupTab.doFirstReset_ToolTipText"));
		}

		{
			fInitCommands = new Text(comp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.heightHint = 60;
			fInitCommands.setLayoutData(gd);
		}

		// Actions
		fDoFirstReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// doResetChanged();
				doFirstResetChanged();
				scheduleUpdateJob(); // updateLaunchConfigurationDialog();
			}
		});

		ModifyListener scheduleUpdateJobModifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		};

		// firstResetType.addModifyListener(scheduleUpdateJobModifyListener);

		fInitCommands.addModifyListener(scheduleUpdateJobModifyListener);
	}

	private void doFirstResetChanged() {

		// boolean enabled = doFirstReset.getSelection();

		// firstResetType.setEnabled(enabled);
	}

	private void createLoadGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
			group.setText(Messages.getString("StartupTab.loadGroup_Text"));
		}

		Composite comp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 1;
			layout.marginHeight = 0;
			comp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			comp.setLayoutData(gd);
		}

		{
			fLoadSymbols = new Button(comp, SWT.CHECK);
			fLoadSymbols.setText(Messages.getString("StartupTab.loadSymbols_Text"));
		}

		{
			Composite local = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 4;
			layout.marginHeight = 0;
			local.setLayout(layout);
			local.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			{
				fUseProjectBinaryForSymbols = new Button(local, SWT.RADIO);
				fUseProjectBinaryForSymbols.setText(Messages.getString("StartupTab.useProjectBinary_Label"));
				fUseProjectBinaryForSymbols.setToolTipText(Messages.getString("StartupTab.useProjectBinary_ToolTip"));

				fProjBinaryLabel2 = new Label(local, SWT.NONE);
				GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				gd.horizontalSpan = ((GridLayout) local.getLayout()).numColumns - 1;
				fProjBinaryLabel2.setLayoutData(gd);
			}

			{
				fUseFileForSymbols = new Button(local, SWT.RADIO);
				fUseFileForSymbols.setText(Messages.getString("StartupTab.useFile_Label"));

				fSymbolsFileName = new Text(local, SWT.BORDER);
				GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				fSymbolsFileName.setLayoutData(gd);

				fSymbolsFileBrowseWs = createPushButton(local, Messages.getString("StartupTab.FileBrowseWs_Label"),
						null);

				fSymbolsFileBrowse = createPushButton(local, Messages.getString("StartupTab.FileBrowse_Label"), null);
			}

			{
				fSymbolsOffsetLabel = new Label(local, SWT.NONE);
				fSymbolsOffsetLabel.setText(Messages.getString("StartupTab.symbolsOffsetLabel_Text"));

				fSymbolsOffset = new Text(local, SWT.BORDER);
				GridData gd = new GridData();
				gd.horizontalSpan = ((GridLayout) local.getLayout()).numColumns - 1;
				gd.widthHint = 100;
				fSymbolsOffset.setLayoutData(gd);
			}
		}

		{
			fLoadExecutable = new Button(comp, SWT.CHECK);
			fLoadExecutable.setText(Messages.getString("StartupTab.loadImage_Text"));
		}

		{
			Composite local = new Composite(comp, SWT.NONE);
			GridLayout layout = new GridLayout();
			layout.numColumns = 4;
			layout.marginHeight = 0;
			local.setLayout(layout);
			local.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			{
				fUseProjectBinaryForImage = new Button(local, SWT.RADIO);
				fUseProjectBinaryForImage.setText(Messages.getString("StartupTab.useProjectBinary_Label"));
				fUseProjectBinaryForImage.setToolTipText(Messages.getString("StartupTab.useProjectBinary_ToolTipText"));

				fProjBinaryLabel1 = new Label(local, SWT.NONE);
				GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				gd.horizontalSpan = ((GridLayout) local.getLayout()).numColumns - 1;
				fProjBinaryLabel1.setLayoutData(gd);
			}

			{
				fUseFileForImage = new Button(local, SWT.RADIO);
				fUseFileForImage.setText(Messages.getString("StartupTab.useFile_Label"));

				fImageFileName = new Text(local, SWT.BORDER);
				GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				fImageFileName.setLayoutData(gd);

				fImageFileBrowseWs = createPushButton(local, Messages.getString("StartupTab.FileBrowseWs_Label"), null);

				fImageFileBrowse = createPushButton(local, Messages.getString("StartupTab.FileBrowse_Label"), null);
			}

			{
				fImageOffsetLabel = new Label(local, SWT.NONE);
				fImageOffsetLabel.setText(Messages.getString("StartupTab.imageOffsetLabel_Text"));

				fImageOffset = new Text(local, SWT.BORDER);
				GridData gd = new GridData();
				gd.horizontalSpan = ((GridLayout) local.getLayout()).numColumns - 1;
				gd.widthHint = ((GridData) fSymbolsOffset.getLayoutData()).widthHint;

				fImageOffset.setLayoutData(gd);
			}
		}

		// ----- Actions ------------------------------------------------------

		fLoadExecutable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadExecutableChanged();
				scheduleUpdateJob(); // updateLaunchConfigurationDialog();
			}
		});

		SelectionListener radioButtonListener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// updateLaunchConfigurationDialog();
				updateUseFileEnablement();
				scheduleUpdateJob(); //
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};

		fUseProjectBinaryForImage.addSelectionListener(radioButtonListener);

		fUseFileForImage.addSelectionListener(radioButtonListener);
		fUseProjectBinaryForSymbols.addSelectionListener(radioButtonListener);
		fUseFileForSymbols.addSelectionListener(radioButtonListener);

		fImageFileName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		fImageFileBrowseWs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseWsButtonSelected(Messages.getString("StartupTab.imageFileBrowseWs_Title"), fImageFileName);
			}
		});

		fImageFileBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(Messages.getString("StartupTab.imageFileBrowse_Title"), fImageFileName);
			}
		});

		fImageOffset.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = (Character.isDigit(e.character) || Character.isISOControl(e.character)
						|| "abcdef".contains(String.valueOf(e.character).toLowerCase()));
			}
		});
		fImageOffset.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		fLoadSymbols.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadSymbolsChanged();
				scheduleUpdateJob(); // updateLaunchConfigurationDialog();
			}
		});

		fSymbolsFileName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		fSymbolsFileBrowseWs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseWsButtonSelected(Messages.getString("StartupTab.symbolsFileBrowseWs_Title"), fSymbolsFileName);
			}
		});

		fSymbolsFileBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(Messages.getString("StartupTab.symbolsFileBrowse_Title"), fSymbolsFileName);
			}
		});

		fSymbolsOffset.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = (Character.isDigit(e.character) || Character.isISOControl(e.character)
						|| "abcdef".contains(String.valueOf(e.character).toLowerCase()));
			}
		});
		fSymbolsOffset.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

	}

	private void updateUseFileEnablement() {

		boolean enabled = fLoadExecutable.getSelection() && fUseFileForImage.getSelection();
		fImageFileName.setEnabled(enabled);
		fImageFileBrowseWs.setEnabled(enabled);
		fImageFileBrowse.setEnabled(enabled);

		enabled = fLoadSymbols.getSelection() && fUseFileForSymbols.getSelection();
		fSymbolsFileName.setEnabled(enabled);
		fSymbolsFileBrowseWs.setEnabled(enabled);
		fSymbolsFileBrowse.setEnabled(enabled);
	}

	public void createRunOptionGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		{
			group.setText(Messages.getString("StartupTab.runOptionGroup_Text"));
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
		}

		Composite comp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			comp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			comp.setLayoutData(gd);
		}

		fDoDebugInRam = new Button(comp, SWT.CHECK);
		fDoDebugInRam.setText(Messages.getString("StartupTab.doDebugInRam_Text"));
		fDoDebugInRam.setToolTipText(Messages.getString("StartupTab.doDebugInRam_ToolTipText"));

		// ----- Actions ------------------------------------------------------

		fDoDebugInRam.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scheduleUpdateJob();
			}
		});
	}

	private void doSecondResetChanged() {
		boolean enabled = fDoSecondReset.getSelection();
		// secondResetType.setEnabled(enabled);
		fSecondResetWarning.setEnabled(enabled);
	}

	private void loadExecutableChanged() {
		boolean enabled = fLoadExecutable.getSelection();
		fUseProjectBinaryForImage.setEnabled(enabled);
		fUseFileForImage.setEnabled(enabled);
		fImageOffset.setEnabled(enabled);
		fImageOffsetLabel.setEnabled(enabled);
		updateUseFileEnablement();
	}

	private void loadSymbolsChanged() {
		boolean enabled = fLoadSymbols.getSelection();
		fUseProjectBinaryForSymbols.setEnabled(enabled);
		fUseFileForSymbols.setEnabled(enabled);
		fSymbolsOffset.setEnabled(enabled);
		fSymbolsOffsetLabel.setEnabled(enabled);
		updateUseFileEnablement();
	}

	private void pcRegisterChanged() {
		fPcRegister.setEnabled(fSetPcRegister.getSelection());
	}

	private void stopAtChanged() {
		fStopAt.setEnabled(fSetStopAt.getSelection());
	}

	public void createRunGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		{
			group.setText(Messages.getString("StartupTab.runGroup_Text"));
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
		}

		Composite comp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 4;
			layout.marginHeight = 0;
			comp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			comp.setLayoutData(gd);
		}

		{
			fDoSecondReset = new Button(comp, SWT.CHECK);
			fDoSecondReset.setText(Messages.getString("StartupTab.doSecondReset_Text"));
			fDoSecondReset.setToolTipText(Messages.getString("StartupTab.doSecondReset_ToolTipText"));

			fSecondResetWarning = new Label(comp, SWT.NONE);
			fSecondResetWarning.setText(Messages.getString("StartupTab.secondResetWarning_Text"));

			GridData gd = new GridData();
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fSecondResetWarning.setLayoutData(gd);
		}

		{
			fRunCommands = new Text(comp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			fRunCommands.setToolTipText(Messages.getString("StartupTab.runCommands_ToolTipText"));
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.heightHint = 60;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			fRunCommands.setLayoutData(gd);
		}

		{
			fSetPcRegister = new Button(comp, SWT.CHECK);
			fSetPcRegister.setText(Messages.getString("StartupTab.setPcRegister_Text"));
			fSetPcRegister.setToolTipText(Messages.getString("StartupTab.setPcRegister_ToolTipText"));

			fPcRegister = new Text(comp, SWT.BORDER);
			fPcRegister.setToolTipText(Messages.getString("StartupTab.pcRegister_ToolTipText"));
			GridData gd = new GridData();
			gd.widthHint = 100;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fPcRegister.setLayoutData(gd);
		}

		{
			fSetStopAt = new Button(comp, SWT.CHECK);
			fSetStopAt.setText(Messages.getString("StartupTab.setStopAt_Text"));
			fSetStopAt.setToolTipText(Messages.getString("StartupTab.setStopAt_ToolTipText"));

			fStopAt = new Text(comp, SWT.BORDER);
			GridData gd = new GridData();
			gd.widthHint = 100;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fStopAt.setLayoutData(gd);
		}

		{
			fDoContinue = new Button(comp, SWT.CHECK);
			fDoContinue.setText(Messages.getString("StartupTab.doContinue_Text"));
			fDoContinue.setToolTipText(Messages.getString("StartupTab.doContinue_ToolTipText"));

			GridData gd = new GridData();
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			fDoContinue.setLayoutData(gd);
		}

		// ---- Actions -------------------------------------------------------

		fDoSecondReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// doResetChanged();
				doSecondResetChanged();
				scheduleUpdateJob(); // updateLaunchConfigurationDialog();
			}
		});

		fSetPcRegister.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				pcRegisterChanged();
				scheduleUpdateJob(); // updateLaunchConfigurationDialog();
			}
		});

		fPcRegister.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = (Character.isDigit(e.character) || Character.isISOControl(e.character)
						|| "abcdef".contains(String.valueOf(e.character).toLowerCase()));
			}
		});

		fPcRegister.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		fSetStopAt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				stopAtChanged();
				scheduleUpdateJob(); // updateLaunchConfigurationDialog();
			}
		});

		fStopAt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		ModifyListener scheduleUpdateJobModifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		};

		SelectionAdapter scheduleUpdateJobSelectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scheduleUpdateJob();
			}
		};

		fRunCommands.addModifyListener(scheduleUpdateJobModifyListener);

		fDoContinue.addSelectionListener(scheduleUpdateJobSelectionAdapter);
	}

	public void doConnectToRunningChanged(boolean flag) {

		// System.out.println(flag);
		fDoFirstReset.setEnabled(!flag);
		// firstResetType.setEnabled(!flag);

		fDoSecondReset.setEnabled(!flag);
		// secondResetType.setEnabled(!flag);
		fSecondResetWarning.setEnabled(!flag);

		fLoadExecutable.setEnabled(!flag);
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		if (!super.isValid(launchConfig))
			return false;
		setErrorMessage(null);
		setMessage(null);

		if (fLoadExecutable.getSelection()) {
			if (!fUseProjectBinaryForImage.getSelection()) {
				if (fImageFileName.getText().trim().isEmpty()) {
					setErrorMessage(Messages.getString("StartupTab.imageFileName_not_specified"));
					return false;
				}

				try {
					String path = fImageFileName.getText().trim();
					path = DebugUtils.resolveAll(path, launchConfig.getAttributes());
					IPath filePath = new Path(path);
					if (!filePath.toFile().exists()) {
						setErrorMessage(Messages.getString("StartupTab.imageFileName_does_not_exist"));
						return false;
					}
				} catch (CoreException e) { // string substitution throws this
											// if expression doesn't resolve
					setErrorMessage(Messages.getString("StartupTab.imageFileName_does_not_exist"));
					return false;
				}
			}
		} else {
			setErrorMessage(null);
		}
		if (fLoadSymbols.getSelection()) {
			if (!fUseProjectBinaryForSymbols.getSelection()) {
				if (fSymbolsFileName.getText().trim().isEmpty()) {
					setErrorMessage(Messages.getString("StartupTab.symbolsFileName_not_specified"));
					return false;
				}

				try {
					String path = fSymbolsFileName.getText().trim();
					path = DebugUtils.resolveAll(path, launchConfig.getAttributes());
					IPath filePath = new Path(path);
					if (!filePath.toFile().exists()) {
						setErrorMessage(Messages.getString("StartupTab.symbolsFileName_does_not_exist"));
						return false;
					}
				} catch (CoreException e) { // string substitution throws this
											// if expression doesn't resolve
					setErrorMessage(Messages.getString("StartupTab.symbolsFileName_does_not_exist"));
					return false;
				}
			}
		} else {
			setErrorMessage(null);
		}

		if (fSetPcRegister.getSelection()) {
			if (fPcRegister.getText().trim().isEmpty()) {
				setErrorMessage(Messages.getString("StartupTab.pcRegister_not_specified"));
				return false;
			}
		} else {
			setErrorMessage(null);
		}
		if (fSetStopAt.getSelection()) {
			if (fStopAt.getText().trim().isEmpty()) {
				setErrorMessage(Messages.getString("StartupTab.stopAt_not_specified"));
			}
		} else {
			setErrorMessage(null);
		}
		return true;
	}

	@Override
	public String getId() {
		return TAB_ID;
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.TabStartup.initializeFrom() " + configuration.getName() + ", dirty=" + isDirty());
		}

		try {
			String stringDefault;
			boolean booleanDefault;

			// Initialisation Commands
			{
				// Do initial reset
				booleanDefault = fPersistentPreferences.getQemuDoInitialReset();
				fDoFirstReset.setSelection(
						configuration.getAttribute(ConfigurationAttributes.DO_FIRST_RESET, booleanDefault));

				// Other commands
				stringDefault = fPersistentPreferences.getQemuInitOther();
				fInitCommands.setText(
						configuration.getAttribute(ConfigurationAttributes.OTHER_INIT_COMMANDS, stringDefault));
			}

			// Load Symbols & Image
			{
				fLoadSymbols.setSelection(configuration.getAttribute(IGDBJtagConstants.ATTR_LOAD_SYMBOLS,
						IGDBJtagConstants.DEFAULT_LOAD_SYMBOLS));
				fUseProjectBinaryForSymbols
						.setSelection(configuration.getAttribute(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS,
								IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_SYMBOLS));
				fUseFileForSymbols.setSelection(configuration.getAttribute(IGDBJtagConstants.ATTR_USE_FILE_FOR_SYMBOLS,
						IGDBJtagConstants.DEFAULT_USE_FILE_FOR_SYMBOLS));
				fSymbolsFileName.setText(configuration.getAttribute(IGDBJtagConstants.ATTR_SYMBOLS_FILE_NAME,
						IGDBJtagConstants.DEFAULT_SYMBOLS_FILE_NAME));
				fSymbolsOffset.setText(configuration.getAttribute(IGDBJtagConstants.ATTR_SYMBOLS_OFFSET,
						IGDBJtagConstants.DEFAULT_SYMBOLS_OFFSET));

				fLoadExecutable.setSelection(configuration.getAttribute(IGDBJtagConstants.ATTR_LOAD_IMAGE,
						IGDBJtagConstants.DEFAULT_LOAD_IMAGE));
				fUseProjectBinaryForImage
						.setSelection(configuration.getAttribute(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE,
								IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_IMAGE));
				fUseFileForImage.setSelection(configuration.getAttribute(IGDBJtagConstants.ATTR_USE_FILE_FOR_IMAGE,
						IGDBJtagConstants.DEFAULT_USE_FILE_FOR_IMAGE));
				fImageFileName.setText(configuration.getAttribute(IGDBJtagConstants.ATTR_IMAGE_FILE_NAME,
						IGDBJtagConstants.DEFAULT_IMAGE_FILE_NAME));
				fImageOffset.setText(configuration.getAttribute(IGDBJtagConstants.ATTR_IMAGE_OFFSET,
						IGDBJtagConstants.DEFAULT_IMAGE_OFFSET));

				String programName = CDebugUtils.getProgramName(configuration);
				if (programName != null) {
					int lastSlash = programName.indexOf('\\');
					if (lastSlash >= 0) {
						programName = programName.substring(lastSlash + 1);
					}
					lastSlash = programName.indexOf('/');
					if (lastSlash >= 0) {
						programName = programName.substring(lastSlash + 1);
					}
					fProjBinaryLabel1.setText(programName);
					fProjBinaryLabel2.setText(programName);
				}
				fSavedProgName = programName;
			}

			// Runtime Options
			{
				booleanDefault = fPersistentPreferences.getQemuDebugInRam();
				fDoDebugInRam.setSelection(
						configuration.getAttribute(ConfigurationAttributes.DO_DEBUG_IN_RAM, booleanDefault));
			}

			// Run Commands
			{
				// Do pre-run reset
				booleanDefault = fPersistentPreferences.getQemuDoPreRunReset();
				fDoSecondReset.setSelection(
						configuration.getAttribute(ConfigurationAttributes.DO_SECOND_RESET, booleanDefault));

				// Other commands
				stringDefault = fPersistentPreferences.getQemuPreRunOther();
				fRunCommands
						.setText(configuration.getAttribute(ConfigurationAttributes.OTHER_RUN_COMMANDS, stringDefault));

				fSetPcRegister.setSelection(configuration.getAttribute(IGDBJtagConstants.ATTR_SET_PC_REGISTER,
						IGDBJtagConstants.DEFAULT_SET_PC_REGISTER));
				fPcRegister.setText(configuration.getAttribute(IGDBJtagConstants.ATTR_PC_REGISTER,
						IGDBJtagConstants.DEFAULT_PC_REGISTER));

				fSetStopAt.setSelection(configuration.getAttribute(IGDBJtagConstants.ATTR_SET_STOP_AT,
						DefaultPreferences.DO_STOP_AT_DEFAULT));
				fStopAt.setText(configuration.getAttribute(IGDBJtagConstants.ATTR_STOP_AT,
						DefaultPreferences.STOP_AT_NAME_DEFAULT));

				// Do continue
				fDoContinue.setSelection(configuration.getAttribute(ConfigurationAttributes.DO_CONTINUE,
						DefaultPreferences.DO_CONTINUE_DEFAULT));
			}

			doFirstResetChanged();

			doSecondResetChanged();
			loadExecutableChanged();
			loadSymbolsChanged();
			pcRegisterChanged();
			stopAtChanged();

			updateUseFileEnablement();

		} catch (CoreException e) {
			Activator.log(e.getStatus());
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"qemu.TabStartup.initializeFrom() completed " + configuration.getName() + ", dirty=" + isDirty());
		}
	}

	public void initializeFromDefaults() {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.TabStartup.initializeFromDefaults()");
		}

		String stringDefault;
		boolean booleanDefault;

		// Initialisation Commands
		{
			// Do initial reset
			booleanDefault = fDefaultPreferences.getQemuDoInitialReset();
			fDoFirstReset.setSelection(booleanDefault);

			// Other commands
			stringDefault = fDefaultPreferences.getQemuInitOther();
			fInitCommands.setText(stringDefault);
		}

		// Load Symbols & Image
		{
			fLoadSymbols.setSelection(IGDBJtagConstants.DEFAULT_LOAD_SYMBOLS);
			fUseProjectBinaryForSymbols.setSelection(IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_SYMBOLS);
			fUseFileForSymbols.setSelection(IGDBJtagConstants.DEFAULT_USE_FILE_FOR_SYMBOLS);
			fSymbolsFileName.setText(IGDBJtagConstants.DEFAULT_SYMBOLS_FILE_NAME);
			fSymbolsOffset.setText(IGDBJtagConstants.DEFAULT_SYMBOLS_OFFSET);

			fLoadExecutable.setSelection(IGDBJtagConstants.DEFAULT_LOAD_IMAGE);
			fUseProjectBinaryForImage.setSelection(IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_IMAGE);
			fUseFileForImage.setSelection(IGDBJtagConstants.DEFAULT_USE_FILE_FOR_IMAGE);
			fImageFileName.setText(IGDBJtagConstants.DEFAULT_IMAGE_FILE_NAME);
			fImageOffset.setText(IGDBJtagConstants.DEFAULT_IMAGE_OFFSET);

			String programName = fSavedProgName;
			if (programName != null) {
				fProjBinaryLabel1.setText(programName);
				fProjBinaryLabel2.setText(programName);
			}
		}

		// Runtime Options
		{
			booleanDefault = fDefaultPreferences.getQemuDebugInRam();
			fDoDebugInRam.setSelection(booleanDefault);
		}

		// Run Commands
		{
			// Do pre-run reset
			booleanDefault = fDefaultPreferences.getQemuDoPreRunReset();
			fDoSecondReset.setSelection(booleanDefault);

			// Other commands
			stringDefault = fDefaultPreferences.getQemuPreRunOther();
			fRunCommands.setText(stringDefault);

			fSetPcRegister.setSelection(IGDBJtagConstants.DEFAULT_SET_PC_REGISTER);
			fPcRegister.setText(IGDBJtagConstants.DEFAULT_PC_REGISTER);

			fSetStopAt.setSelection(DefaultPreferences.DO_STOP_AT_DEFAULT);
			fStopAt.setText(DefaultPreferences.STOP_AT_NAME_DEFAULT);

			// Do continue
			fDoContinue.setSelection(DefaultPreferences.DO_CONTINUE_DEFAULT);
		}

		doFirstResetChanged();

		doSecondResetChanged();
		loadExecutableChanged();
		loadSymbolsChanged();
		pcRegisterChanged();
		stopAtChanged();

		updateUseFileEnablement();
	}

	@Override
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.TabStartup.activated() " + workingCopy.getName());
		}
	}

	@Override
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.TabStartup.deactivated() " + workingCopy.getName());
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.TabStartup.performApply() " + configuration.getName() + ", dirty=" + isDirty());
		}

		boolean booleanValue;
		String stringValue;

		// Initialisation Commands
		{
			// Do first reset
			booleanValue = fDoFirstReset.getSelection();
			configuration.setAttribute(ConfigurationAttributes.DO_FIRST_RESET, booleanValue);
			fPersistentPreferences.putQemuDoInitialReset(booleanValue);

			// Other commands
			stringValue = fInitCommands.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.OTHER_INIT_COMMANDS, stringValue);
			fPersistentPreferences.putQemuInitOther(stringValue);
		}

		// Load Symbols & Image...
		{
			configuration.setAttribute(IGDBJtagConstants.ATTR_LOAD_SYMBOLS, fLoadSymbols.getSelection());
			configuration.setAttribute(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS,
					fUseProjectBinaryForSymbols.getSelection());
			configuration.setAttribute(IGDBJtagConstants.ATTR_USE_FILE_FOR_SYMBOLS, fUseFileForSymbols.getSelection());
			configuration.setAttribute(IGDBJtagConstants.ATTR_SYMBOLS_FILE_NAME, fSymbolsFileName.getText().trim());

			configuration.setAttribute(IGDBJtagConstants.ATTR_SYMBOLS_OFFSET, fSymbolsOffset.getText());

			configuration.setAttribute(IGDBJtagConstants.ATTR_LOAD_IMAGE, fLoadExecutable.getSelection());
			configuration.setAttribute(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE,
					fUseProjectBinaryForImage.getSelection());
			configuration.setAttribute(IGDBJtagConstants.ATTR_USE_FILE_FOR_IMAGE, fUseFileForImage.getSelection());
			configuration.setAttribute(IGDBJtagConstants.ATTR_IMAGE_FILE_NAME, fImageFileName.getText().trim());

			configuration.setAttribute(IGDBJtagConstants.ATTR_IMAGE_OFFSET, fImageOffset.getText());
		}

		// Runtime Options
		{
			booleanValue = fDoDebugInRam.getSelection();
			configuration.setAttribute(ConfigurationAttributes.DO_DEBUG_IN_RAM, booleanValue);
			fPersistentPreferences.putQemuDebugInRam(booleanValue);
		}

		// Run Commands
		{
			// Pre-run reset
			booleanValue = fDoSecondReset.getSelection();
			configuration.setAttribute(ConfigurationAttributes.DO_SECOND_RESET, booleanValue);
			fPersistentPreferences.putQemuDoPreRunReset(booleanValue);

			// Other commands
			stringValue = fRunCommands.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.OTHER_RUN_COMMANDS, stringValue);
			fPersistentPreferences.putQemuPreRunOther(stringValue);

			configuration.setAttribute(IGDBJtagConstants.ATTR_SET_PC_REGISTER, fSetPcRegister.getSelection());
			configuration.setAttribute(IGDBJtagConstants.ATTR_PC_REGISTER, fPcRegister.getText());
			configuration.setAttribute(IGDBJtagConstants.ATTR_SET_STOP_AT, fSetStopAt.getSelection());
			configuration.setAttribute(IGDBJtagConstants.ATTR_STOP_AT, fStopAt.getText());

			// Continue
			configuration.setAttribute(ConfigurationAttributes.DO_CONTINUE, fDoContinue.getSelection());
		}

		fPersistentPreferences.flush();

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"qemu.TabStartup.performApply() completed " + configuration.getName() + ", dirty=" + isDirty());
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("qemu.TabStartup.setDefaults() " + configuration.getName());
		}

		boolean defaultBoolean;
		String defaultString;

		// Initialisation Commands
		defaultBoolean = fPersistentPreferences.getQemuDoInitialReset();
		configuration.setAttribute(ConfigurationAttributes.DO_FIRST_RESET, defaultBoolean);

		defaultBoolean = fPersistentPreferences.getQemuEnableSemihosting();
		configuration.setAttribute(ConfigurationAttributes.ENABLE_SEMIHOSTING, defaultBoolean);

		defaultString = fPersistentPreferences.getQemuInitOther();
		configuration.setAttribute(ConfigurationAttributes.OTHER_INIT_COMMANDS, defaultString);

		// Load Image...
		configuration.setAttribute(IGDBJtagConstants.ATTR_LOAD_IMAGE, IGDBJtagConstants.DEFAULT_LOAD_IMAGE);
		configuration.setAttribute(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE,
				IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_IMAGE);
		configuration.setAttribute(IGDBJtagConstants.ATTR_USE_FILE_FOR_IMAGE,
				IGDBJtagConstants.DEFAULT_USE_FILE_FOR_IMAGE);
		configuration.setAttribute(IGDBJtagConstants.ATTR_IMAGE_FILE_NAME, IGDBJtagConstants.DEFAULT_IMAGE_FILE_NAME);
		configuration.setAttribute(IGDBJtagConstants.ATTR_IMAGE_OFFSET, IGDBJtagConstants.DEFAULT_IMAGE_OFFSET);

		// .. and Symbols
		configuration.setAttribute(IGDBJtagConstants.ATTR_LOAD_SYMBOLS, IGDBJtagConstants.DEFAULT_LOAD_SYMBOLS);
		configuration.setAttribute(IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS,
				IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_SYMBOLS);
		configuration.setAttribute(IGDBJtagConstants.ATTR_USE_FILE_FOR_SYMBOLS,
				IGDBJtagConstants.DEFAULT_USE_FILE_FOR_SYMBOLS);
		configuration.setAttribute(IGDBJtagConstants.ATTR_SYMBOLS_FILE_NAME,
				IGDBJtagConstants.DEFAULT_SYMBOLS_FILE_NAME);
		configuration.setAttribute(IGDBJtagConstants.ATTR_SYMBOLS_OFFSET, IGDBJtagConstants.DEFAULT_SYMBOLS_OFFSET);

		// Runtime Options
		defaultBoolean = fPersistentPreferences.getQemuDebugInRam();
		configuration.setAttribute(ConfigurationAttributes.DO_DEBUG_IN_RAM, defaultBoolean);

		// Run Commands
		defaultBoolean = fPersistentPreferences.getQemuDoPreRunReset();
		configuration.setAttribute(ConfigurationAttributes.DO_SECOND_RESET, defaultBoolean);

		defaultString = fPersistentPreferences.getQemuPreRunOther();
		configuration.setAttribute(ConfigurationAttributes.OTHER_RUN_COMMANDS, defaultString);

		configuration.setAttribute(IGDBJtagConstants.ATTR_SET_PC_REGISTER, IGDBJtagConstants.DEFAULT_SET_PC_REGISTER);
		configuration.setAttribute(IGDBJtagConstants.ATTR_PC_REGISTER, IGDBJtagConstants.DEFAULT_PC_REGISTER);
		configuration.setAttribute(IGDBJtagConstants.ATTR_SET_STOP_AT, DefaultPreferences.DO_STOP_AT_DEFAULT);
		configuration.setAttribute(IGDBJtagConstants.ATTR_STOP_AT, DefaultPreferences.STOP_AT_NAME_DEFAULT);
		configuration.setAttribute(IGDBJtagConstants.ATTR_SET_RESUME, IGDBJtagConstants.DEFAULT_SET_RESUME);

		configuration.setAttribute(ConfigurationAttributes.DO_CONTINUE, DefaultPreferences.DO_CONTINUE_DEFAULT);
	}

	// ------------------------------------------------------------------------
}
