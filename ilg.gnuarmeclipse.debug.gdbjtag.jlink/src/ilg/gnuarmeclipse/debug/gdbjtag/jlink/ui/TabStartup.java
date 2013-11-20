/*******************************************************************************
 * Copyright (c) 2007 - 2010 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Andy Jin - Hardware debugging UI improvements, bug 229946
 *     Andy Jin - Added DSF debugging, bug 248593
 *     Liviu Ionescu - ARM version
 *******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui;

import ilg.gnuarmeclipse.debug.gdbjtag.jlink.ConfigurationAttributes;

import java.io.File;

import org.eclipse.cdt.debug.core.CDebugUtils;
import org.eclipse.cdt.debug.gdbjtag.core.Activator;
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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;

public class TabStartup extends AbstractLaunchConfigurationTab {

	private static final String TAB_NAME = "Startup";
	private static final String TAB_ID = Activator.PLUGIN_ID + ".ui.startuptab";

	Text initCommands;
	//Text delay;
	//Button doReset;  
	//Button doHalt;

	Button doFirstReset;
	Text firstResetType;
	
	Button enableSemihosting;
	Button enableSwo;

	Button loadImage;
	Text imageFileName;
	Button imageFileBrowseWs;
	Button imageFileBrowse;
	Text imageOffset;

	Button loadSymbols;
	Text symbolsFileName;

	Button symbolsFileBrowseWs;
	Button symbolsFileBrowse;
	Text symbolsOffset;

	Button setPcRegister;
	Text pcRegister;

	Button setStopAt;
	Text stopAt;

	Button setResume;
	boolean resume = false;

	Text runCommands;

	// New GUI added to address bug 310304
	private Button useProjectBinaryForImage;
	private Button useFileForImage;
	private Button useProjectBinaryForSymbols;
	private Button useFileForSymbols;
	private Label imageOffsetLabel;
	private Label symbolsOffsetLabel;
	private Label projBinaryLabel1;
	private Label projBinaryLabel2;

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
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL
				| SWT.H_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		setControl(sc);

		Composite comp = new Composite(sc, SWT.NONE);
		sc.setContent(comp);
		GridLayout layout = new GridLayout();
		comp.setLayout(layout);

		createInitGroup(comp);
		createLoadGroup(comp);
		createRunOptionGroup(comp);
		createRunGroup(comp);

		sc.setMinSize(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
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
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		dialog.setTitle(title);
		dialog.setMessage(Messages
				.getString("GDBJtagStartupTab.FileBrowseWs_Message"));
		dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));
		if (dialog.open() == IDialogConstants.OK_ID) {
			IResource resource = (IResource) dialog.getFirstResult();
			String arg = resource.getFullPath().toOSString();
			String fileLoc = VariablesPlugin.getDefault()
					.getStringVariableManager()
					.generateVariableExpression("workspace_loc", arg); //$NON-NLS-1$
			text.setText(fileLoc);
		}
	}

	public void createInitGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		group.setText(Messages.getString("StartupTab.initGroup_Text"));

		Composite comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		comp.setLayout(layout);

//		doReset = new Button(comp, SWT.CHECK);
//		doReset.setText(Messages.getString("GDBJtagStartupTab.doReset_Text"));
//		doReset.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				doResetChanged();
//				updateLaunchConfigurationDialog();
//			}
//		});
//		delay = new Text(comp, SWT.BORDER);
//		gd = new GridData();
//		gd.horizontalSpan = 1;
//		gd.widthHint = 60;
//		delay.setLayoutData(gd);
//		delay.addVerifyListener(new VerifyListener() {
//			@Override
//			public void verifyText(VerifyEvent e) {
//				e.doit = (Character.isDigit(e.character) || Character
//						.isISOControl(e.character));
//			}
//		});
//		delay.addModifyListener(new ModifyListener() {
//			@Override
//			public void modifyText(ModifyEvent e) {
//				scheduleUpdateJob();
//			}
//		});
//
//		comp = new Composite(group, SWT.NONE);
//		layout = new GridLayout();
//		layout.numColumns = 1;
//		layout.marginHeight = 0;
//		comp.setLayout(layout);
//
//		doHalt = new Button(comp, SWT.CHECK);
//		doHalt.setText(Messages.getString("GDBJtagStartupTab.doHalt_Text"));
//		gd = new GridData();
//		gd.horizontalSpan = 1;
//		doHalt.setLayoutData(gd);
//		doHalt.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				updateLaunchConfigurationDialog();
//			}
//		});

		
		comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		comp.setLayout(layout);

		doFirstReset = new Button(comp, SWT.CHECK);
		doFirstReset.setText(Messages.getString("StartupTab.doFirstReset_Text"));
		doFirstReset.setToolTipText(Messages.getString("StartupTab.doFirstReset_ToolTipText"));
		doFirstReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//doResetChanged();
				doFirstResetChanged();
				updateLaunchConfigurationDialog();
			}
		});
		firstResetType = new Text(comp, SWT.BORDER);
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.widthHint = 30;
		firstResetType.setLayoutData(gd);
		firstResetType.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = (Character.isDigit(e.character) || Character
						.isISOControl(e.character));
			}
		});
		firstResetType.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				// ! scheduleUpdateJob();
			}
		});
		
		comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		comp.setLayout(layout);

		enableSemihosting = new Button(comp, SWT.CHECK);
		enableSemihosting.setText(Messages.getString("StartupTab.enableSemihosting_Text"));
		enableSemihosting.setToolTipText(Messages.getString("StartupTab.enableSemihosting_ToolTipText"));
		enableSemihosting.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		// use the same layout as before
		enableSwo = new Button(comp, SWT.CHECK);
		enableSwo.setText(Messages.getString("StartupTab.enableSwo_Text"));
		enableSwo.setToolTipText(Messages.getString("StartupTab.enableSwo_ToolTipText"));
		enableSwo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		initCommands = new Text(group, SWT.MULTI | SWT.WRAP | SWT.BORDER
				| SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 60;
		initCommands.setLayoutData(gd);
		initCommands.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent evt) {
				//scheduleUpdateJob();
				updateLaunchConfigurationDialog();
			}
		});

	}

	private void createLoadGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		layout.numColumns = 4;
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		group.setLayoutData(gd);
		group.setText(Messages.getString("GDBJtagStartupTab.loadGroup_Text"));

		loadImage = new Button(group, SWT.CHECK);
		loadImage.setText(Messages
				.getString("GDBJtagStartupTab.loadImage_Text"));
		gd = new GridData();
		gd.horizontalSpan = 4;
		loadImage.setLayoutData(gd);
		loadImage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadImageChanged();
				updateLaunchConfigurationDialog();
			}
		});

		Composite comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 4;
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp.setLayout(layout);

		SelectionListener radioButtonListener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
				updateUseFileEnablement();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};

		useProjectBinaryForImage = new Button(comp, SWT.RADIO);
		useProjectBinaryForImage.setText(Messages
				.getString("GDBJtagStartupTab.useProjectBinary_Label"));
		useProjectBinaryForImage.setToolTipText(Messages
				.getString("GDBJtagStartupTab.useProjectBinary_ToolTip"));
		gd = new GridData();
		gd.horizontalSpan = 1;
		useProjectBinaryForImage.setLayoutData(gd);
		useProjectBinaryForImage.addSelectionListener(radioButtonListener);

		projBinaryLabel1 = new Label(comp, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		projBinaryLabel1.setLayoutData(gd);

		useFileForImage = new Button(comp, SWT.RADIO);
		useFileForImage.setText(Messages
				.getString("GDBJtagStartupTab.useFile_Label"));
		gd = new GridData();
		gd.horizontalSpan = 1;
		useFileForImage.setLayoutData(gd);
		useFileForImage.addSelectionListener(radioButtonListener);

		imageFileName = new Text(comp, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		imageFileName.setLayoutData(gd);
		imageFileName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		imageFileBrowseWs = createPushButton(comp,
				Messages.getString("GDBJtagStartupTab.FileBrowseWs_Label"),
				null);
		imageFileBrowseWs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseWsButtonSelected(
						Messages.getString("GDBJtagStartupTab.imageFileBrowseWs_Title"),
						imageFileName);
			}
		});

		imageFileBrowse = createPushButton(comp,
				Messages.getString("GDBJtagStartupTab.FileBrowse_Label"), null);
		imageFileBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(Messages
						.getString("GDBJtagStartupTab.imageFileBrowse_Title"),
						imageFileName);
			}
		});

		imageOffsetLabel = new Label(comp, SWT.NONE);
		imageOffsetLabel.setText(Messages
				.getString("GDBJtagStartupTab.imageOffsetLabel_Text"));
		imageOffset = new Text(comp, SWT.BORDER);
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.widthHint = 100;
		imageOffset.setLayoutData(gd);
		imageOffset.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = (Character.isDigit(e.character)
						|| Character.isISOControl(e.character) || "abcdef"
						.contains(String.valueOf(e.character).toLowerCase()));
			}
		});
		imageOffset.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		loadSymbols = new Button(group, SWT.CHECK);
		loadSymbols.setText(Messages
				.getString("GDBJtagStartupTab.loadSymbols_Text"));
		gd = new GridData();
		gd.horizontalSpan = 4;
		loadSymbols.setLayoutData(gd);
		loadSymbols.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				loadSymbolsChanged();
				updateLaunchConfigurationDialog();
			}
		});

		comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 4;
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comp.setLayout(layout);

		useProjectBinaryForSymbols = new Button(comp, SWT.RADIO);
		useProjectBinaryForSymbols.setText(Messages
				.getString("GDBJtagStartupTab.useProjectBinary_Label"));
		useProjectBinaryForSymbols.setToolTipText(Messages
				.getString("GDBJtagStartupTab.useProjectBinary_ToolTip"));
		gd = new GridData();
		gd.horizontalSpan = 1;
		useProjectBinaryForSymbols.setLayoutData(gd);
		useProjectBinaryForSymbols.addSelectionListener(radioButtonListener);

		projBinaryLabel2 = new Label(comp, SWT.NONE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		projBinaryLabel2.setLayoutData(gd);

		useFileForSymbols = new Button(comp, SWT.RADIO);
		useFileForSymbols.setText(Messages
				.getString("GDBJtagStartupTab.useFile_Label"));
		gd = new GridData();
		gd.horizontalSpan = 1;
		useFileForSymbols.setLayoutData(gd);
		useFileForSymbols.addSelectionListener(radioButtonListener);

		symbolsFileName = new Text(comp, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		symbolsFileName.setLayoutData(gd);
		symbolsFileName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		symbolsFileBrowseWs = createPushButton(comp,
				Messages.getString("GDBJtagStartupTab.FileBrowseWs_Label"),
				null);
		symbolsFileBrowseWs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseWsButtonSelected(
						Messages.getString("GDBJtagStartupTab.symbolsFileBrowseWs_Title"),
						symbolsFileName);
			}
		});

		symbolsFileBrowse = createPushButton(comp,
				Messages.getString("GDBJtagStartupTab.FileBrowse_Label"), null);
		symbolsFileBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(
						Messages.getString("GDBJtagStartupTab.symbolsFileBrowse_Title"),
						symbolsFileName);
			}
		});

		symbolsOffsetLabel = new Label(comp, SWT.NONE);
		symbolsOffsetLabel.setText(Messages
				.getString("GDBJtagStartupTab.symbolsOffsetLabel_Text"));
		symbolsOffset = new Text(comp, SWT.BORDER);
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.widthHint = 100;
		symbolsOffset.setLayoutData(gd);
		symbolsOffset.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = (Character.isDigit(e.character)
						|| Character.isISOControl(e.character) || "abcdef"
						.contains(String.valueOf(e.character).toLowerCase()));
			}
		});
		symbolsOffset.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

	}

	private void updateUseFileEnablement() {
		boolean enabled = loadImage.getSelection()
				&& useFileForImage.getSelection();
		imageFileName.setEnabled(enabled);
		imageFileBrowseWs.setEnabled(enabled);
		imageFileBrowse.setEnabled(enabled);

		enabled = loadSymbols.getSelection()
				&& useFileForSymbols.getSelection();
		symbolsFileName.setEnabled(enabled);
		symbolsFileBrowseWs.setEnabled(enabled);
		symbolsFileBrowse.setEnabled(enabled);
	}

	public void createRunOptionGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		layout.numColumns = 2;
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		group.setLayoutData(gd);
		group.setText(Messages
				.getString("GDBJtagStartupTab.runOptionGroup_Text"));

		setPcRegister = new Button(group, SWT.CHECK);
		setPcRegister.setText(Messages
				.getString("GDBJtagStartupTab.setPcRegister_Text"));
		gd = new GridData();
		gd.horizontalSpan = 1;
		setPcRegister.setLayoutData(gd);
		setPcRegister.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				pcRegisterChanged();
				updateLaunchConfigurationDialog();
			}
		});

		pcRegister = new Text(group, SWT.BORDER);
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.widthHint = 100;
		pcRegister.setLayoutData(gd);
		pcRegister.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = (Character.isDigit(e.character)
						|| Character.isISOControl(e.character) || "abcdef"
						.contains(String.valueOf(e.character).toLowerCase()));
			}
		});
		pcRegister.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		setStopAt = new Button(group, SWT.CHECK);
		setStopAt.setText(Messages
				.getString("GDBJtagStartupTab.setStopAt_Text"));
		gd = new GridData();
		gd.horizontalSpan = 1;
		setStopAt.setLayoutData(gd);
		setStopAt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				stopAtChanged();
				updateLaunchConfigurationDialog();
			}
		});

		stopAt = new Text(group, SWT.BORDER);
		gd = new GridData();
		gd.horizontalSpan = 1;
		gd.widthHint = 100;
		stopAt.setLayoutData(gd);
		stopAt.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		setResume = new Button(group, SWT.CHECK);
		setResume.setText(Messages
				.getString("GDBJtagStartupTab.setResume_Text"));
		gd = new GridData();
		gd.horizontalSpan = 1;
		setResume.setLayoutData(gd);
		setResume.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				resumeChanged();
				updateLaunchConfigurationDialog();
			}
		});
	}

//	private void doResetChanged() {
//		delay.setEnabled(doReset.getSelection());
//	}

	private void doFirstResetChanged() {
		firstResetType.setEnabled(doFirstReset.getSelection());
	}

	private void loadImageChanged() {
		boolean enabled = loadImage.getSelection();
		useProjectBinaryForImage.setEnabled(enabled);
		useFileForImage.setEnabled(enabled);
		imageOffset.setEnabled(enabled);
		imageOffsetLabel.setEnabled(enabled);
		updateUseFileEnablement();
	}

	private void loadSymbolsChanged() {
		boolean enabled = loadSymbols.getSelection();
		useProjectBinaryForSymbols.setEnabled(enabled);
		useFileForSymbols.setEnabled(enabled);
		symbolsOffset.setEnabled(enabled);
		symbolsOffsetLabel.setEnabled(enabled);
		updateUseFileEnablement();
	}

	private void pcRegisterChanged() {
		pcRegister.setEnabled(setPcRegister.getSelection());
	}

	private void stopAtChanged() {
		stopAt.setEnabled(setStopAt.getSelection());
	}

	private void resumeChanged() {
		resume = setResume.getSelection();
	}

	public void createRunGroup(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		group.setText(Messages.getString("GDBJtagStartupTab.runGroup_Text"));

		runCommands = new Text(group, SWT.MULTI | SWT.WRAP | SWT.BORDER
				| SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 60;
		runCommands.setLayoutData(gd);
		runCommands.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent evt) {
				scheduleUpdateJob();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.AbstractLaunchConfigurationTab#isValid(org.eclipse
	 * .debug.core.ILaunchConfiguration)
	 */
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		if (!super.isValid(launchConfig))
			return false;
		setErrorMessage(null);
		setMessage(null);

		if (loadImage.getSelection()) {
			if (!useProjectBinaryForImage.getSelection()) {
				if (imageFileName.getText().trim().length() == 0) {
					setErrorMessage(Messages
							.getString("GDBJtagStartupTab.imageFileName_not_specified"));
					return false;
				}

				try {
					String path = VariablesPlugin
							.getDefault()
							.getStringVariableManager()
							.performStringSubstitution(
									imageFileName.getText().trim());
					IPath filePath = new Path(path);
					if (!filePath.toFile().exists()) {
						setErrorMessage(Messages
								.getString("GDBJtagStartupTab.imageFileName_does_not_exist"));
						return false;
					}
				} catch (CoreException e) { // string substitution throws this
											// if expression doesn't resolve
					setErrorMessage(Messages
							.getString("GDBJtagStartupTab.imageFileName_does_not_exist"));
					return false;
				}
			}
		} else {
			setErrorMessage(null);
		}
		if (loadSymbols.getSelection()) {
			if (!useProjectBinaryForSymbols.getSelection()) {
				if (symbolsFileName.getText().trim().length() == 0) {
					setErrorMessage(Messages
							.getString("GDBJtagStartupTab.symbolsFileName_not_specified"));
					return false;
				}

				try {
					String path = VariablesPlugin
							.getDefault()
							.getStringVariableManager()
							.performStringSubstitution(
									symbolsFileName.getText().trim());
					IPath filePath = new Path(path);
					if (!filePath.toFile().exists()) {
						setErrorMessage(Messages
								.getString("GDBJtagStartupTab.symbolsFileName_does_not_exist"));
						return false;
					}
				} catch (CoreException e) { // string substitution throws this
											// if expression doesn't resolve
					setErrorMessage(Messages
							.getString("GDBJtagStartupTab.symbolsFileName_does_not_exist"));
					return false;
				}
			}
		} else {
			setErrorMessage(null);
		}

		if (setPcRegister.getSelection()) {
			if (pcRegister.getText().trim().length() == 0) {
				setErrorMessage(Messages
						.getString("GDBJtagStartupTab.pcRegister_not_specified"));
				return false;
			}
		} else {
			setErrorMessage(null);
		}
		if (setStopAt.getSelection()) {
			if (stopAt.getText().trim().length() == 0) {
				setErrorMessage(Messages
						.getString("GDBJtagStartupTab.stopAt_not_specified"));
			}
		} else {
			setErrorMessage(null);
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.ui.AbstractLaunchConfigurationTab#getId()
	 */
	@Override
	public String getId() {
		return TAB_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#initializeFrom(org.eclipse
	 * .debug.core.ILaunchConfiguration)
	 */
	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			// Initialization Commands
//			doReset.setSelection(configuration.getAttribute(
//					IGDBJtagConstants.ATTR_DO_RESET,
//					IGDBJtagConstants.DEFAULT_DO_RESET));
//			delay.setText(String.valueOf(configuration.getAttribute(
//					IGDBJtagConstants.ATTR_DELAY,
//					IGDBJtagConstants.DEFAULT_DELAY)));
//			doHalt.setSelection(configuration.getAttribute(
//					IGDBJtagConstants.ATTR_DO_HALT,
//					IGDBJtagConstants.DEFAULT_DO_HALT));
//			initCommands.setText(configuration.getAttribute(
//					IGDBJtagConstants.ATTR_INIT_COMMANDS,
//					IGDBJtagConstants.DEFAULT_INIT_COMMANDS));
			
			doFirstReset.setSelection(configuration.getAttribute(
					ConfigurationAttributes.DO_FIRST_RESET,
					ConfigurationAttributes.DO_FIRST_RESET_DEFAULT));
			firstResetType.setText(configuration.getAttribute(
					ConfigurationAttributes.FIRST_RESET_TYPE,
					ConfigurationAttributes.FIRST_RESET_TYPE_DEFAULT));
			enableSemihosting.setSelection(configuration.getAttribute(
					ConfigurationAttributes.ENABLE_SEMIHOSTING,
					ConfigurationAttributes.ENABLE_SEMIHOSTING_DEFAULT));
			enableSwo.setSelection(configuration.getAttribute(
					ConfigurationAttributes.ENABLE_SWO,
					ConfigurationAttributes.ENABLE_SWO_DEFAULT));
			initCommands.setText(configuration.getAttribute(
					ConfigurationAttributes.OTHER_INIT_COMMANDS,
					ConfigurationAttributes.OTHER_INIT_COMMANDS_DEFAULT));

			// Load Image...
			loadImage.setSelection(configuration.getAttribute(
					IGDBJtagConstants.ATTR_LOAD_IMAGE,
					IGDBJtagConstants.DEFAULT_LOAD_IMAGE));
			useProjectBinaryForImage.setSelection(configuration.getAttribute(
					IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE,
					IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_IMAGE));
			useFileForImage.setSelection(configuration.getAttribute(
					IGDBJtagConstants.ATTR_USE_FILE_FOR_IMAGE,
					IGDBJtagConstants.DEFAULT_USE_FILE_FOR_IMAGE));
			imageFileName.setText(configuration.getAttribute(
					IGDBJtagConstants.ATTR_IMAGE_FILE_NAME,
					IGDBJtagConstants.DEFAULT_IMAGE_FILE_NAME));
			imageOffset.setText(configuration.getAttribute(
					IGDBJtagConstants.ATTR_IMAGE_OFFSET,
					IGDBJtagConstants.DEFAULT_IMAGE_OFFSET));

			// .. and Symbols
			loadSymbols.setSelection(configuration.getAttribute(
					IGDBJtagConstants.ATTR_LOAD_SYMBOLS,
					IGDBJtagConstants.DEFAULT_LOAD_SYMBOLS));
			useProjectBinaryForSymbols.setSelection(configuration.getAttribute(
					IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS,
					IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_SYMBOLS));
			useFileForSymbols.setSelection(configuration.getAttribute(
					IGDBJtagConstants.ATTR_USE_FILE_FOR_SYMBOLS,
					IGDBJtagConstants.DEFAULT_USE_FILE_FOR_SYMBOLS));
			symbolsFileName.setText(configuration.getAttribute(
					IGDBJtagConstants.ATTR_SYMBOLS_FILE_NAME,
					IGDBJtagConstants.DEFAULT_SYMBOLS_FILE_NAME));
			symbolsOffset.setText(configuration.getAttribute(
					IGDBJtagConstants.ATTR_SYMBOLS_OFFSET,
					IGDBJtagConstants.DEFAULT_SYMBOLS_OFFSET));

			// Runtime Options
			setPcRegister.setSelection(configuration.getAttribute(
					IGDBJtagConstants.ATTR_SET_PC_REGISTER,
					IGDBJtagConstants.DEFAULT_SET_PC_REGISTER));
			pcRegister.setText(configuration.getAttribute(
					IGDBJtagConstants.ATTR_PC_REGISTER,
					IGDBJtagConstants.DEFAULT_PC_REGISTER));
			setStopAt.setSelection(configuration.getAttribute(
					IGDBJtagConstants.ATTR_SET_STOP_AT,
					IGDBJtagConstants.DEFAULT_SET_STOP_AT));
			stopAt.setText(configuration.getAttribute(
					IGDBJtagConstants.ATTR_STOP_AT,
					IGDBJtagConstants.DEFAULT_STOP_AT));
			setResume.setSelection(configuration.getAttribute(
					IGDBJtagConstants.ATTR_SET_RESUME,
					IGDBJtagConstants.DEFAULT_SET_RESUME));

			// Run Commands
			runCommands.setText(configuration.getAttribute(
					IGDBJtagConstants.ATTR_RUN_COMMANDS,
					IGDBJtagConstants.DEFAULT_RUN_COMMANDS));

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
				projBinaryLabel1.setText(programName);
				projBinaryLabel2.setText(programName);
			}

//			doResetChanged();
			doFirstResetChanged();
			loadImageChanged();
			loadSymbolsChanged();
			pcRegisterChanged();
			stopAtChanged();
			resumeChanged();
			updateUseFileEnablement();

		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#performApply(org.eclipse
	 * .debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {

		// Initialisation Commands
		configuration.setAttribute(ConfigurationAttributes.DO_FIRST_RESET,
				doFirstReset.getSelection());
		configuration.setAttribute(ConfigurationAttributes.FIRST_RESET_TYPE,
				firstResetType.getText());
		configuration.setAttribute(ConfigurationAttributes.ENABLE_SEMIHOSTING,
				enableSemihosting.getSelection());
		configuration.setAttribute(ConfigurationAttributes.OTHER_INIT_COMMANDS,
				initCommands.getText());

		// Load Image...
		configuration.setAttribute(IGDBJtagConstants.ATTR_LOAD_IMAGE,
				loadImage.getSelection());
		configuration.setAttribute(
				IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE,
				useProjectBinaryForImage.getSelection());
		configuration.setAttribute(IGDBJtagConstants.ATTR_USE_FILE_FOR_IMAGE,
				useFileForImage.getSelection());
		configuration.setAttribute(IGDBJtagConstants.ATTR_IMAGE_FILE_NAME,
				imageFileName.getText().trim());
		configuration.setAttribute(IGDBJtagConstants.ATTR_IMAGE_OFFSET,
				imageOffset.getText());

		// .. and Symbols
		configuration.setAttribute(IGDBJtagConstants.ATTR_LOAD_SYMBOLS,
				loadSymbols.getSelection());
		configuration.setAttribute(
				IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS,
				useProjectBinaryForSymbols.getSelection());
		configuration.setAttribute(IGDBJtagConstants.ATTR_USE_FILE_FOR_SYMBOLS,
				useFileForSymbols.getSelection());
		configuration.setAttribute(IGDBJtagConstants.ATTR_SYMBOLS_FILE_NAME,
				symbolsFileName.getText().trim());
		configuration.setAttribute(IGDBJtagConstants.ATTR_SYMBOLS_OFFSET,
				symbolsOffset.getText());

		// Runtime Options
		configuration.setAttribute(IGDBJtagConstants.ATTR_SET_PC_REGISTER,
				setPcRegister.getSelection());
		configuration.setAttribute(IGDBJtagConstants.ATTR_PC_REGISTER,
				pcRegister.getText());
		configuration.setAttribute(IGDBJtagConstants.ATTR_SET_STOP_AT,
				setStopAt.getSelection());
		configuration.setAttribute(IGDBJtagConstants.ATTR_STOP_AT,
				stopAt.getText());
		configuration.setAttribute(IGDBJtagConstants.ATTR_SET_RESUME,
				setResume.getSelection());

		// Run Commands
		configuration.setAttribute(IGDBJtagConstants.ATTR_RUN_COMMANDS,
				runCommands.getText());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.debug.ui.ILaunchConfigurationTab#setDefaults(org.eclipse.
	 * debug.core.ILaunchConfigurationWorkingCopy)
	 */
	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
		// Initialisation Commands
		configuration.setAttribute(ConfigurationAttributes.DO_FIRST_RESET,
				ConfigurationAttributes.DO_FIRST_RESET_DEFAULT);
		configuration.setAttribute(ConfigurationAttributes.FIRST_RESET_TYPE,
				ConfigurationAttributes.FIRST_RESET_TYPE_DEFAULT);
		configuration.setAttribute(ConfigurationAttributes.ENABLE_SEMIHOSTING,
				ConfigurationAttributes.ENABLE_SEMIHOSTING_DEFAULT);
		configuration.setAttribute(ConfigurationAttributes.ENABLE_SWO,
				ConfigurationAttributes.ENABLE_SWO_DEFAULT);		
		configuration.setAttribute(ConfigurationAttributes.OTHER_INIT_COMMANDS,
				ConfigurationAttributes.OTHER_INIT_COMMANDS_DEFAULT);

		// Load Image...
		configuration.setAttribute(IGDBJtagConstants.ATTR_LOAD_IMAGE,
				IGDBJtagConstants.DEFAULT_LOAD_IMAGE);
		configuration.setAttribute(
				IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_IMAGE,
				IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_IMAGE);
		configuration.setAttribute(IGDBJtagConstants.ATTR_USE_FILE_FOR_IMAGE,
				IGDBJtagConstants.DEFAULT_USE_FILE_FOR_IMAGE);
		configuration.setAttribute(IGDBJtagConstants.ATTR_IMAGE_FILE_NAME,
				IGDBJtagConstants.DEFAULT_IMAGE_FILE_NAME);
		configuration.setAttribute(IGDBJtagConstants.ATTR_IMAGE_OFFSET,
				IGDBJtagConstants.DEFAULT_IMAGE_OFFSET);

		// .. and Symbols
		configuration.setAttribute(IGDBJtagConstants.ATTR_LOAD_SYMBOLS,
				IGDBJtagConstants.DEFAULT_LOAD_SYMBOLS);
		configuration.setAttribute(
				IGDBJtagConstants.ATTR_USE_PROJ_BINARY_FOR_SYMBOLS,
				IGDBJtagConstants.DEFAULT_USE_PROJ_BINARY_FOR_SYMBOLS);
		configuration.setAttribute(IGDBJtagConstants.ATTR_USE_FILE_FOR_SYMBOLS,
				IGDBJtagConstants.DEFAULT_USE_FILE_FOR_SYMBOLS);
		configuration.setAttribute(IGDBJtagConstants.ATTR_SYMBOLS_FILE_NAME,
				IGDBJtagConstants.DEFAULT_SYMBOLS_FILE_NAME);
		configuration.setAttribute(IGDBJtagConstants.ATTR_SYMBOLS_OFFSET,
				IGDBJtagConstants.DEFAULT_SYMBOLS_OFFSET);

		// Runtime Options
		configuration.setAttribute(IGDBJtagConstants.ATTR_SET_PC_REGISTER,
				IGDBJtagConstants.DEFAULT_SET_PC_REGISTER);
		configuration.setAttribute(IGDBJtagConstants.ATTR_PC_REGISTER,
				IGDBJtagConstants.DEFAULT_PC_REGISTER);
		configuration.setAttribute(IGDBJtagConstants.ATTR_SET_STOP_AT,
				IGDBJtagConstants.DEFAULT_SET_STOP_AT);
		configuration.setAttribute(IGDBJtagConstants.ATTR_STOP_AT,
				IGDBJtagConstants.DEFAULT_STOP_AT);
		configuration.setAttribute(IGDBJtagConstants.ATTR_SET_RESUME,
				IGDBJtagConstants.DEFAULT_SET_RESUME);

		// Run Commands
		configuration.setAttribute(IGDBJtagConstants.ATTR_RUN_COMMANDS,
				IGDBJtagConstants.DEFAULT_RUN_COMMANDS);
	}
}
