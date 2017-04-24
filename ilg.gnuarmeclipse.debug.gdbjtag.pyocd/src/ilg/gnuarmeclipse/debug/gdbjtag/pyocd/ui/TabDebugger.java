/*******************************************************************************
 * Copyright (c) 2007, 2012 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     QNX Software Systems - Initial API and implementation
 *     Andy Jin - Hardware debugging UI improvements, bug 229946
 *     Anna Dushistova (MontaVista) - bug 241279
 *              - Hardware Debugging: Host name or ip address not saving in
 *                the debug configuration
 *     Andy Jin (QNX) - Added DSF debugging, bug 248593
 *     Bruce Griffith, Sage Electronic Engineering, LLC - bug 305943
 *              - API generalization to become transport-independent (e.g. to
 *                allow connections via serial ports and pipes).
 *     Liviu Ionescu - ARM version
 *     Chris Reed - pyOCD changes
 *     John Cortell - cleanup and fixes
 *     Jonah Graham - fix for Neon
 ******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.pyocd.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.settings.model.ICConfigurationDescription;
import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.ui.GDBJtagImages;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

import ilg.gnuarmeclipse.core.EclipseUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.DebugUtils;
import ilg.gnuarmeclipse.debug.gdbjtag.pyocd.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.pyocd.ConfigurationAttributes;
import ilg.gnuarmeclipse.debug.gdbjtag.pyocd.DefaultPreferences;
import ilg.gnuarmeclipse.debug.gdbjtag.pyocd.PersistentPreferences;
import ilg.gnuarmeclipse.debug.gdbjtag.pyocd.PyOCD;

/**
 * @since 7.0
 */
public class TabDebugger extends AbstractLaunchConfigurationTab {

	// ------------------------------------------------------------------------

	private static final String TAB_NAME = "Debugger";
	private static final String TAB_ID = Activator.PLUGIN_ID + ".ui.debuggertab";

	// ------------------------------------------------------------------------

	/** The launch configuration this GUI is showing/modifying */
	private ILaunchConfiguration fConfiguration;

	private List<PyOCD.Board> fBoards;
	private String fSelectedBoardId;

	private Button fDoStartGdbServer;
	private Text fGdbClientExecutable;
	private Text fGdbClientOtherOptions;
	private Text fGdbClientOtherCommands;

	private Text fTargetIpAddress;
	private Text fTargetPortNumber;

	private Combo fGdbServerBoardId;
	private Button fGdbServerRefreshBoards;

	private Text fGdbServerGdbPort;
	private Text fGdbServerTelnetPort;

	private Button fGdbServerOverrideTarget;
	private Combo fGdbServerTargetName;

	private Combo fGdbServerBusSpeed;

	private Button fGdbServerHaltAtHardFault;
	private Button fGdbServerStepIntoInterrupts;

	private Combo fGdbServerFlashMode;
	private Button fGdbServerFlashFastVerify;

	private Button fGdbServerEnableSemihosting;
	private Button fGdbServerUseGdbSyscallsForSemihosting;

	private Text fGdbServerExecutable;
	private Button fGdbServerBrowseButton;
	private Button fGdbServerVariablesButton;

	private Text fGdbServerOtherOptions;

	private Button fDoGdbServerAllocateConsole;
	private Button fDoGdbServerAllocateSemihostingConsole;

	protected Button fUpdateThreadlistOnSuspend;

	/** Active errors , if any. When any, only first is shown */
	private Set<String> fErrors = new LinkedHashSet<String>();

	/**
	 * Where widgets in a row are rendered in columns, the amount of padding (in
	 * pixels) between columns
	 */
	private static final int COLUMN_PAD = 30;

	private static class Msgs {
		public static final String INVALID_PYOCD_EXECUTABLE = "pyOCD gdbserver not found where specified";
	}
	// ------------------------------------------------------------------------

	protected TabDebugger(TabStartup tabStartup) {
		super();
	}

	// ------------------------------------------------------------------------

	@Override
	public String getName() {
		return TAB_NAME;
	}

	@Override
	public Image getImage() {
		return GDBJtagImages.getDebuggerTabImage();
	}

	private Composite createHorizontalLayout(Composite comp, int columns, int spanSub) {
		Composite local = new Composite(comp, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = columns;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		local.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		if (spanSub > 0) {
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - spanSub;
		}
		local.setLayoutData(gd);
		return local;

	}

	@Override
	public void createControl(Composite parent) {

		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout layout = new GridLayout();
		comp.setLayout(layout);

		createGdbServerGroup(comp);

		createGdbClientControls(comp);

		createRemoteControl(comp);

		fUpdateThreadlistOnSuspend = new Button(comp, SWT.CHECK);
		fUpdateThreadlistOnSuspend.setText(Messages.getString("DebuggerTab.update_thread_list_on_suspend_Text"));
		fUpdateThreadlistOnSuspend
				.setToolTipText(Messages.getString("DebuggerTab.update_thread_list_on_suspend_ToolTipText"));

		Link restoreDefaults;
		GridData gd;
		{
			restoreDefaults = new Link(comp, SWT.NONE);
			restoreDefaults.setText(Messages.getString("DebuggerTab.restoreDefaults_Link"));
			restoreDefaults.setToolTipText(Messages.getString("DebuggerTab.restoreDefaults_ToolTipText"));

			gd = new GridData();
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = SWT.RIGHT;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			restoreDefaults.setLayoutData(gd);
		}

		// --------------------------------------------------------------------

		fUpdateThreadlistOnSuspend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

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

	private void variablesButtonSelected(Text text) {
		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(getShell());
		if (dialog.open() == StringVariableSelectionDialog.OK) {
			text.insert(dialog.getVariableExpression());
		}
	}

	private void createGdbServerGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		group.setText(Messages.getString("DebuggerTab.gdbServerGroup_Text"));

		Composite comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 5;
		layout.marginHeight = 0;
		comp.setLayout(layout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(gd);

		Composite local;
		Label label;
		{
			fDoStartGdbServer = new Button(comp, SWT.CHECK);
			fDoStartGdbServer.setText(Messages.getString("DebuggerTab.doStartGdbServer_Text"));
			fDoStartGdbServer.setToolTipText(Messages.getString("DebuggerTab.doStartGdbServer_ToolTipText"));
			gd = new GridData();
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			fDoStartGdbServer.setLayoutData(gd);
		}

		{
			Composite subcomp = new Composite(comp, SWT.NONE);
			gd = new GridData(SWT.FILL, SWT.TOP, true, false);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			subcomp.setLayoutData(gd);
			layout = new GridLayout(2, false);
			layout.marginWidth = layout.marginHeight = 0;
			subcomp.setLayout(layout);

			label = new Label(subcomp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerExecutable_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerExecutable_ToolTipText"));

			{
				local = createHorizontalLayout(subcomp, 3, 1);
				{
					fGdbServerExecutable = new Text(local, SWT.SINGLE | SWT.BORDER);
					gd = new GridData(GridData.FILL_HORIZONTAL);
					fGdbServerExecutable.setLayoutData(gd);

					fGdbServerBrowseButton = new Button(local, SWT.NONE);
					fGdbServerBrowseButton.setText(Messages.getString("DebuggerTab.gdbServerExecutableBrowse"));

					fGdbServerVariablesButton = new Button(local, SWT.NONE);
					fGdbServerVariablesButton.setText(Messages.getString("DebuggerTab.gdbServerExecutableVariable"));
				}
			}

			label = new Label(subcomp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerGdbPort_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerGdbPort_ToolTipText"));

			{
				Composite subcomp2 = new Composite(subcomp, SWT.NONE);
				gd = new GridData(SWT.FILL, SWT.TOP, true, false);
				subcomp2.setLayoutData(gd);
				layout = new GridLayout(2, false);
				layout.horizontalSpacing = COLUMN_PAD;
				layout.marginWidth = layout.marginHeight = 0;
				subcomp2.setLayout(layout);

				fGdbServerGdbPort = new Text(subcomp2, SWT.SINGLE | SWT.BORDER);
				gd = new GridData();
				gd.widthHint = 60;
				fGdbServerGdbPort.setLayoutData(gd);

				fDoGdbServerAllocateConsole = new Button(subcomp2, SWT.CHECK);
				fDoGdbServerAllocateConsole.setLayoutData(new GridData());
				fDoGdbServerAllocateConsole.setText(Messages.getString("DebuggerTab.gdbServerAllocateConsole_Label"));
				fDoGdbServerAllocateConsole
						.setToolTipText(Messages.getString("DebuggerTab.gdbServerAllocateConsole_ToolTipText"));
			}

			label = new Label(subcomp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerTelnetPort_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerTelnetPort_ToolTipText"));

			{
				Composite subcomp2 = new Composite(subcomp, SWT.NONE);
				gd = new GridData(SWT.FILL, SWT.TOP, true, false);
				subcomp2.setLayoutData(gd);
				layout = new GridLayout(2, false);
				layout.horizontalSpacing = COLUMN_PAD;
				layout.marginWidth = layout.marginHeight = 0;
				subcomp2.setLayout(layout);

				fGdbServerTelnetPort = new Text(subcomp2, SWT.SINGLE | SWT.BORDER);
				gd = new GridData();
				gd.widthHint = 60;
				fGdbServerTelnetPort.setLayoutData(gd);

				fDoGdbServerAllocateSemihostingConsole = new Button(subcomp2, SWT.CHECK);
				fDoGdbServerAllocateSemihostingConsole.setLayoutData(new GridData());
				fDoGdbServerAllocateSemihostingConsole
						.setText(Messages.getString("DebuggerTab.gdbServerAllocateTelnetConsole_Label"));
				fDoGdbServerAllocateSemihostingConsole
						.setToolTipText(Messages.getString("DebuggerTab.gdbServerAllocateTelnetConsole_ToolTipText"));
				fDoGdbServerAllocateSemihostingConsole.setLayoutData(new GridData());
			}
		}

		createSeparator(comp, ((GridLayout) comp.getLayout()).numColumns);

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerBoardId_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerBoardId_ToolTipText"));

			local = createHorizontalLayout(comp, 2, 1);
			{
				fGdbServerBoardId = new Combo(local, SWT.DROP_DOWN | SWT.READ_ONLY);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				fGdbServerBoardId.setLayoutData(gd);
				fGdbServerBoardId.setItems(new String[] {});
				fGdbServerBoardId.select(0);

				fGdbServerRefreshBoards = new Button(local, SWT.NONE);
				fGdbServerRefreshBoards.setText(Messages.getString("DebuggerTab.gdbServerRefreshBoards_Label"));
			}
		}

		{
			fGdbServerOverrideTarget = new Button(comp, SWT.CHECK);
			fGdbServerOverrideTarget.setText(Messages.getString("DebuggerTab.gdbServerOverrideTarget_Label"));
			fGdbServerOverrideTarget
					.setToolTipText(Messages.getString("DebuggerTab.gdbServerOverrideTarget_ToolTipText"));
			gd = new GridData();
			fGdbServerOverrideTarget.setLayoutData(gd);

			fGdbServerTargetName = new Combo(comp, SWT.DROP_DOWN);
			gd = new GridData();
			gd.widthHint = 120;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fGdbServerTargetName.setLayoutData(gd);
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerBusSpeed_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerBusSpeed_ToolTipText"));

			fGdbServerBusSpeed = new Combo(comp, SWT.DROP_DOWN);
			gd = new GridData();
			gd.widthHint = 120;
			fGdbServerBusSpeed.setLayoutData(gd);
			fGdbServerBusSpeed.setItems(new String[] { "1000000", "2000000", "8000000", "12000000" });
			fGdbServerBusSpeed.select(0);

			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerBusSpeedUnits_Label"));
			gd = new GridData();
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 2;
			label.setLayoutData(gd);
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerFlashMode_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerFlashMode_ToolTipText"));
			gd = new GridData();
			label.setLayoutData(gd);

			fGdbServerFlashMode = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
			gd = new GridData();
			gd.widthHint = 120;
			fGdbServerFlashMode.setLayoutData(gd);
			fGdbServerFlashMode.setItems(new String[] { Messages.getString("DebuggerTab.gdbServerFlashMode.AutoErase"),
					Messages.getString("DebuggerTab.gdbServerFlashMode.ChipErase"),
					Messages.getString("DebuggerTab.gdbServerFlashMode.SectorErase"), });
			fGdbServerFlashMode.select(0);

			fGdbServerFlashFastVerify = new Button(comp, SWT.CHECK);
			fGdbServerFlashFastVerify.setText(Messages.getString("DebuggerTab.gdbServerFlashFastVerify_Label"));
			fGdbServerFlashFastVerify
					.setToolTipText(Messages.getString("DebuggerTab.gdbServerFlashFastVerify_ToolTipText"));
			gd = new GridData();

			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 2;
			gd.horizontalIndent = COLUMN_PAD;
			fGdbServerFlashFastVerify.setLayoutData(gd);
		}

		// Composite for next four checkboxes. Will render using two columns
		{
			Composite subcomp = new Composite(comp, SWT.NONE);
			gd = new GridData();
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			subcomp.setLayoutData(gd);
			layout = new GridLayout(2, false);
			layout.horizontalSpacing = COLUMN_PAD;
			layout.marginWidth = layout.marginHeight = 0;
			subcomp.setLayout(layout);
			{
				fGdbServerHaltAtHardFault = new Button(subcomp, SWT.CHECK);
				fGdbServerHaltAtHardFault.setLayoutData(new GridData());
				fGdbServerHaltAtHardFault.setText(Messages.getString("DebuggerTab.gdbServerHaltAtHardFault_Label"));
				fGdbServerHaltAtHardFault
						.setToolTipText(Messages.getString("DebuggerTab.gdbServerHaltAtHardFault_ToolTipText"));
			}

			{
				fGdbServerStepIntoInterrupts = new Button(subcomp, SWT.CHECK);
				fGdbServerStepIntoInterrupts.setLayoutData(new GridData());
				fGdbServerStepIntoInterrupts
						.setText(Messages.getString("DebuggerTab.gdbServerStepIntoInterrupts_Label"));
				fGdbServerStepIntoInterrupts
						.setToolTipText(Messages.getString("DebuggerTab.gdbServerStepIntoInterrupts_ToolTipText"));
			}

			{
				fGdbServerEnableSemihosting = new Button(subcomp, SWT.CHECK);
				fGdbServerEnableSemihosting.setLayoutData(new GridData());
				fGdbServerEnableSemihosting.setText(Messages.getString("DebuggerTab.gdbServerEnableSemihosting_Label"));
				fGdbServerEnableSemihosting
						.setToolTipText(Messages.getString("DebuggerTab.gdbServerEnableSemihosting_ToolTipText"));
			}

			{
				fGdbServerUseGdbSyscallsForSemihosting = new Button(subcomp, SWT.CHECK);
				fGdbServerUseGdbSyscallsForSemihosting.setLayoutData(new GridData());
				fGdbServerUseGdbSyscallsForSemihosting
						.setText(Messages.getString("DebuggerTab.gdbServerUseGdbSyscallsForSemihosting_Label"));
				fGdbServerUseGdbSyscallsForSemihosting.setToolTipText(
						Messages.getString("DebuggerTab.gdbServerUseGdbSyscallsForSemihosting_ToolTipText"));
			}
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerOther_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerOther_ToolTipText"));
			gd = new GridData();
			gd.verticalAlignment = SWT.TOP;
			label.setLayoutData(gd);

			fGdbServerOtherOptions = new Text(comp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 60;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fGdbServerOtherOptions.setLayoutData(gd);
		}

		// ----- Actions ------------------------------------------------------

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

		fDoStartGdbServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doStartGdbServerChanged();
				if (fDoStartGdbServer.getSelection()) {
					fTargetIpAddress.setText(DefaultPreferences.REMOTE_IP_ADDRESS_LOCALHOST);
				}
				scheduleUpdateJob();
			}
		});

		fGdbServerExecutable.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (fConfiguration != null) {
					updateBoards();
					updateTargets();
					scheduleUpdateJob(); // provides much better performance for
											// Text listeners
				}
			}
		});

		fGdbServerBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(Messages.getString("DebuggerTab.gdbServerExecutableBrowse_Title"),
						fGdbServerExecutable);
			}
		});

		fGdbServerVariablesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variablesButtonSelected(fGdbServerExecutable);
			}
		});

		fGdbServerGdbPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				// make the target port the same
				fTargetPortNumber.setText(fGdbServerGdbPort.getText());
				scheduleUpdateJob();
			}
		});

		fGdbServerBoardId.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boardSelected(((Combo) e.widget).getSelectionIndex());
			}
		});

		fGdbServerRefreshBoards.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateBoards();
			}
		});

		fGdbServerOverrideTarget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				overrideTargetChanged();
				scheduleUpdateJob();
			}
		});

		fGdbServerTelnetPort.addModifyListener(scheduleUpdateJobModifyListener);

		fGdbServerBusSpeed.addModifyListener(scheduleUpdateJobModifyListener);

		fGdbServerTargetName.addModifyListener(scheduleUpdateJobModifyListener);

		fGdbServerHaltAtHardFault.addSelectionListener(scheduleUpdateJobSelectionAdapter);

		fGdbServerStepIntoInterrupts.addSelectionListener(scheduleUpdateJobSelectionAdapter);

		fGdbServerFlashMode.addModifyListener(scheduleUpdateJobModifyListener);

		fGdbServerFlashFastVerify.addSelectionListener(scheduleUpdateJobSelectionAdapter);

		fGdbServerEnableSemihosting.addSelectionListener(scheduleUpdateJobSelectionAdapter);

		fGdbServerUseGdbSyscallsForSemihosting.addSelectionListener(scheduleUpdateJobSelectionAdapter);

		fGdbServerOtherOptions.addModifyListener(scheduleUpdateJobModifyListener);

		fDoGdbServerAllocateConsole.addSelectionListener(scheduleUpdateJobSelectionAdapter);
		fDoGdbServerAllocateSemihostingConsole.addSelectionListener(scheduleUpdateJobSelectionAdapter);
	}

	private void createGdbClientControls(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("DebuggerTab.gdbSetupGroup_Text"));
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		Composite comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		comp.setLayout(layout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(gd);

		Label label;
		Button browseButton;
		Button variableButton;
		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbCommand_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbCommand_ToolTipText"));

			Composite local = createHorizontalLayout(comp, 3, -1);
			{
				fGdbClientExecutable = new Text(local, SWT.SINGLE | SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				fGdbClientExecutable.setLayoutData(gd);

				browseButton = new Button(local, SWT.NONE);
				browseButton.setText(Messages.getString("DebuggerTab.gdbCommandBrowse"));

				variableButton = new Button(local, SWT.NONE);
				variableButton.setText(Messages.getString("DebuggerTab.gdbCommandVariable"));
			}
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbOtherOptions_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbOtherOptions_ToolTipText"));
			gd = new GridData();
			label.setLayoutData(gd);

			fGdbClientOtherOptions = new Text(comp, SWT.SINGLE | SWT.BORDER);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			fGdbClientOtherOptions.setLayoutData(gd);
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbOtherCommands_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbOtherCommands_ToolTipText"));
			gd = new GridData();
			gd.verticalAlignment = SWT.TOP;
			label.setLayoutData(gd);

			fGdbClientOtherCommands = new Text(comp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 60;
			fGdbClientOtherCommands.setLayoutData(gd);
		}

		// ----- Actions ------------------------------------------------------

		fGdbClientExecutable.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});

		fGdbClientOtherOptions.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		fGdbClientOtherCommands.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob();
			}
		});

		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(Messages.getString("DebuggerTab.gdbCommandBrowse_Title"), fGdbClientExecutable);
			}
		});

		variableButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variablesButtonSelected(fGdbClientExecutable);
			}
		});
	}

	private void createRemoteControl(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("DebuggerTab.remoteGroup_Text"));
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		Composite comp = createHorizontalLayout(group, 2, -1);
		comp.setLayoutData(gd);

		// Create entry fields for TCP/IP connections
		Label label;
		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.ipAddressLabel")); //$NON-NLS-1$

			fTargetIpAddress = new Text(comp, SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 125;
			fTargetIpAddress.setLayoutData(gd);

			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.portNumberLabel")); //$NON-NLS-1$

			fTargetPortNumber = new Text(comp, SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 125;
			fTargetPortNumber.setLayoutData(gd);
		}

		// ---- Actions -------------------------------------------------------

		// Add watchers for user data entry
		fTargetIpAddress.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});
		fTargetPortNumber.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = Character.isDigit(e.character) || Character.isISOControl(e.character);
			}
		});
		fTargetPortNumber.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});
	}

	private void doStartGdbServerChanged() {

		boolean enabled = fDoStartGdbServer.getSelection();

		fGdbServerExecutable.setEnabled(enabled);
		fGdbServerBrowseButton.setEnabled(enabled);
		fGdbServerVariablesButton.setEnabled(enabled);
		fGdbServerOtherOptions.setEnabled(enabled);

		fGdbServerGdbPort.setEnabled(enabled);
		fGdbServerTelnetPort.setEnabled(enabled);
		fGdbServerBusSpeed.setEnabled(enabled);
		fGdbServerOverrideTarget.setEnabled(enabled);
		fGdbServerTargetName.setEnabled(enabled && fGdbServerOverrideTarget.getSelection());
		fGdbServerHaltAtHardFault.setEnabled(enabled);
		fGdbServerStepIntoInterrupts.setEnabled(enabled);
		fGdbServerFlashMode.setEnabled(enabled);
		fGdbServerFlashFastVerify.setEnabled(enabled);

		fDoGdbServerAllocateConsole.setEnabled(enabled);
		fDoGdbServerAllocateSemihostingConsole.setEnabled(enabled);

		// Disable remote target params when the server is started
		fTargetIpAddress.setEnabled(!enabled);
		fTargetPortNumber.setEnabled(!enabled);
	}

	private void overrideTargetChanged() {
		boolean enabled = fGdbServerOverrideTarget.getSelection();

		fGdbServerTargetName.setEnabled(enabled);
	}

	/**
	 * Resolve the string in the gdbserver field and validate it. Return the
	 * result if valid, otherwise return null.
	 * 
	 * @return an absolute path, relative path or just the name of the
	 *         executable (if it's in PATH)
	 */
	private String getPyOCDExecutablePath() {
		String path = null;

		try {
			path = fGdbServerExecutable.getText().trim();
			if (path.length() == 0) {
				return null;
			}
			if (Activator.getInstance().isDebugging()) {
				System.out.printf("pyOCD path = %s\n", path);
			}
			path = DebugUtils.resolveAll(path, fConfiguration.getAttributes());

			ICConfigurationDescription buildConfig = EclipseUtils.getBuildConfigDescription(fConfiguration);
			if (buildConfig != null) {
				path = DebugUtils.resolveAll(path, buildConfig);
			}

			if (Activator.getInstance().isDebugging()) {
				System.out.printf("pyOCD resolved path = %s\n", path);
			}

			// Validate path.

			// First check using the most efficient means: see if the file
			// exists. If it does, that's good enough.
			File file = new File(path);
			if (!file.exists()) {
				// Support pyOCD being in PATH and specified sans path (issue#
				// 102)
				try {
					Process process = Runtime.getRuntime().exec(path + " --version");
					// If no exception, then it's an executable in PATH
					try {
						process.waitFor();
					} catch (InterruptedException e) {
						// No harm, no foul
					}
				} catch (IOException e) {
					if (Activator.getInstance().isDebugging()) {
						System.out.printf("pyOCD path is invalid\n");
					}
					return null;
				}
			} else if (file.isDirectory()) {
				// TODO: Use java.nio.Files when we move to Java 7 to also check
				// that file is executable
				if (Activator.getInstance().isDebugging()) {
					System.out.printf("pyOCD path is invalid\n");
				}
				return null;
			}

		} catch (CoreException e) {
			Activator.log(e);
			return null;
		}

		return path;
	}

	private int indexForBoardId(String boardId) {
		// Search for a matching board.
		if (fBoards != null) {
			int index = 0;
			for (PyOCD.Board b : fBoards) {
				if (b.fUniqueId.equals(boardId)) {
					return index;
				}
				index += 1;
			}
		}
		return -1;
	}

	private void boardSelected(int index) {
		PyOCD.Board selectedBoard = fBoards.get(index);
		fSelectedBoardId = selectedBoard.fUniqueId;
	}

	private void selectActiveBoard() {
		// Get current board ID.
		int index = indexForBoardId(fSelectedBoardId);
		if (index != -1) {
			fGdbServerBoardId.select(index);
		} else {

		}

	}

	private void updateBoards() {
		String path = getPyOCDExecutablePath();
		if (path != null) {
			deregisterError(Msgs.INVALID_PYOCD_EXECUTABLE);
			List<PyOCD.Board> boards = PyOCD.getBoards(path);
			if (boards == null) {
				boards = new ArrayList<PyOCD.Board>();
			}
			if (Activator.getInstance().isDebugging()) {
				System.out.printf("board = %s\n", boards);
			}

			Collections.sort(boards, PyOCD.Board.COMPARATOR);

			fBoards = boards;

			final ArrayList<String> itemList = new ArrayList<String>();

			for (PyOCD.Board board : boards) {
				String desc = board.fProductName;
				if (!board.fProductName.startsWith(board.fVendorName)) {
					desc = board.fVendorName + " " + board.fProductName;
				}
				itemList.add(String.format("%s - %s (%s)", board.fName, desc, board.fUniqueId));
			}

			String[] items = itemList.toArray(new String[itemList.size()]);

			fGdbServerBoardId.setItems(items);

			selectActiveBoard();
		} else {
			fGdbServerBoardId.setItems(new String[] {});
			registerError(Msgs.INVALID_PYOCD_EXECUTABLE);
		}
	}

	private void updateTargets() {
		String path = getPyOCDExecutablePath();
		if (path != null) {
			deregisterError(Msgs.INVALID_PYOCD_EXECUTABLE);

			List<PyOCD.Target> targets = PyOCD.getTargets(path);
			if (targets == null) {
				targets = new ArrayList<PyOCD.Target>();
			}
			if (Activator.getInstance().isDebugging()) {
				System.out.printf("target = %s\n", targets);
			}

			Collections.sort(targets, PyOCD.Target.COMPARATOR);

			final ArrayList<String> itemList = new ArrayList<String>();
			for (PyOCD.Target target : targets) {
				itemList.add(String.format("%s", target.fPartNumber));
			}
			String[] items = itemList.toArray(new String[itemList.size()]);

			fGdbServerTargetName.setItems(items);
			
			// Select current target from config.
			if (fConfiguration != null) {
				try {
					fGdbServerTargetName.setText(fConfiguration.getAttribute(ConfigurationAttributes.GDB_SERVER_TARGET_NAME,
					DefaultPreferences.GDB_SERVER_TARGET_NAME_DEFAULT));
				} catch (CoreException e) {
					Activator.log(e.getStatus());
				}
			}
		} else {
			// Clear combobox and show error
			fGdbServerTargetName.setItems(new String[] {});
			registerError(Msgs.INVALID_PYOCD_EXECUTABLE);
		}
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			Boolean booleanDefault;
			String stringDefault;

			// PyOCD GDB server
			{
				// Start server locally
				booleanDefault = PersistentPreferences.getGdbServerDoStart();
				fDoStartGdbServer.setSelection(
						configuration.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER, booleanDefault));

				// Executable
				stringDefault = PersistentPreferences.getGdbServerExecutable();
				fGdbServerExecutable.setText(
						configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE, stringDefault));

				// Ports
				fGdbServerGdbPort.setText(
						Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
								DefaultPreferences.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT)));

				fGdbServerTelnetPort.setText(Integer
						.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
								DefaultPreferences.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT)));

				// Board ID
				fSelectedBoardId = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_ID,
						DefaultPreferences.GDB_SERVER_BOARD_ID_DEFAULT);
				selectActiveBoard();

				// Target override
				fGdbServerOverrideTarget
						.setSelection(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_OVERRIDE_TARGET,
								DefaultPreferences.GDB_SERVER_OVERRIDE_TARGET_DEFAULT));

				fGdbServerTargetName.setText(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_TARGET_NAME,
						DefaultPreferences.GDB_SERVER_TARGET_NAME_DEFAULT));

				// Misc options
				fGdbServerHaltAtHardFault
						.setSelection(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_HALT_AT_HARD_FAULT,
								DefaultPreferences.GDB_SERVER_HALT_AT_HARD_FAULT_DEFAULT));

				fGdbServerStepIntoInterrupts.setSelection(
						configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_STEP_INTO_INTERRUPTS,
								DefaultPreferences.GDB_SERVER_STEP_INTO_INTERRUPTS_DEFAULT));

				// Flash
				fGdbServerFlashMode.select(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_FLASH_MODE,
						DefaultPreferences.GDB_SERVER_FLASH_MODE_DEFAULT));

				fGdbServerFlashFastVerify
						.setSelection(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_FLASH_FAST_VERIFY,
								DefaultPreferences.GDB_SERVER_FLASH_FAST_VERIFY_DEFAULT));

				// Semihosting
				fGdbServerEnableSemihosting
						.setSelection(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_ENABLE_SEMIHOSTING,
								DefaultPreferences.GDB_SERVER_ENABLE_SEMIHOSTING_DEFAULT));

				fGdbServerUseGdbSyscallsForSemihosting
						.setSelection(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_USE_GDB_SYSCALLS,
								DefaultPreferences.GDB_SERVER_USE_GDB_SYSCALLS_DEFAULT));

				// Bus speed
				fGdbServerBusSpeed.setText(
						Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_BUS_SPEED,
								DefaultPreferences.GDB_SERVER_BUS_SPEED_DEFAULT)));

				// Other options
				stringDefault = PersistentPreferences.getGdbServerOtherOptions();
				fGdbServerOtherOptions
						.setText(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, stringDefault));

				// Allocate server console
				if (EclipseUtils.isWindows()) {
					fDoGdbServerAllocateConsole.setSelection(true);
				} else {
					fDoGdbServerAllocateConsole.setSelection(
							configuration.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
									DefaultPreferences.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT));
				}

				// Allocate telnet console
				fDoGdbServerAllocateSemihostingConsole.setSelection(
						configuration.getAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
								DefaultPreferences.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT));

			}

			// GDB Client Setup
			{
				// Executable
				stringDefault = PersistentPreferences.getGdbClientExecutable();
				String gdbCommandAttr = configuration.getAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
						stringDefault);
				fGdbClientExecutable.setText(gdbCommandAttr);

				// Other options
				stringDefault = PersistentPreferences.getGdbClientOtherOptions();
				fGdbClientOtherOptions.setText(
						configuration.getAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS, stringDefault));

				stringDefault = PersistentPreferences.getGdbClientCommands();
				fGdbClientOtherCommands.setText(
						configuration.getAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS, stringDefault));
			}

			// Remote target
			{
				fTargetIpAddress.setText(configuration.getAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS,
						DefaultPreferences.REMOTE_IP_ADDRESS_DEFAULT)); // $NON-NLS-1$

				int storedPort = 0;
				storedPort = configuration.getAttribute(IGDBJtagConstants.ATTR_PORT_NUMBER, 0); // Default
																								// 0

				// 0 means undefined, use default
				if ((storedPort <= 0) || (65535 < storedPort)) {
					storedPort = DefaultPreferences.REMOTE_PORT_NUMBER_DEFAULT;
				}

				String portString = Integer.toString(storedPort); // $NON-NLS-1$
				fTargetPortNumber.setText(portString);
			}

			fConfiguration = configuration;

			doStartGdbServerChanged();
			overrideTargetChanged();
			updateBoards();
			updateTargets();

			// Force thread update
			boolean updateThreadsOnSuspend = configuration.getAttribute(
					IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
					DefaultPreferences.UPDATE_THREAD_LIST_DEFAULT);
			fUpdateThreadlistOnSuspend.setSelection(updateThreadsOnSuspend);

		} catch (CoreException e) {
			Activator.log(e.getStatus());
		}
	}

	public void initializeFromDefaults() {

		String stringDefault;

		// PyOCD GDB server
		{
			// Start server locally
			fDoStartGdbServer.setSelection(DefaultPreferences.DO_START_GDB_SERVER_DEFAULT);

			// Executable
			stringDefault = DefaultPreferences.getGdbServerExecutable();
			fGdbServerExecutable.setText(stringDefault);

			// Ports
			fGdbServerGdbPort.setText(Integer.toString(DefaultPreferences.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT));

			fGdbServerTelnetPort.setText(Integer.toString(DefaultPreferences.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT));

			// Board ID
			// fGdbServerBoardId.setText(DefaultPreferences.GDB_SERVER_BOARD_ID_DEFAULT);

			// Target override
			fGdbServerOverrideTarget.setSelection(DefaultPreferences.GDB_SERVER_OVERRIDE_TARGET_DEFAULT);

			fGdbServerTargetName.setText(DefaultPreferences.GDB_SERVER_TARGET_NAME_DEFAULT);

			// Misc options
			fGdbServerHaltAtHardFault.setSelection(DefaultPreferences.GDB_SERVER_HALT_AT_HARD_FAULT_DEFAULT);

			fGdbServerStepIntoInterrupts.setSelection(DefaultPreferences.GDB_SERVER_STEP_INTO_INTERRUPTS_DEFAULT);

			// Flash
			fGdbServerFlashMode.select(DefaultPreferences.GDB_SERVER_FLASH_MODE_DEFAULT);

			fGdbServerFlashFastVerify.setSelection(DefaultPreferences.GDB_SERVER_FLASH_FAST_VERIFY_DEFAULT);

			// Semihosting
			fGdbServerEnableSemihosting.setSelection(DefaultPreferences.GDB_SERVER_ENABLE_SEMIHOSTING_DEFAULT);

			fGdbServerUseGdbSyscallsForSemihosting.setSelection(DefaultPreferences.GDB_SERVER_USE_GDB_SYSCALLS_DEFAULT);

			// Bus speed
			fGdbServerBusSpeed.setText(Integer.toString(DefaultPreferences.GDB_SERVER_BUS_SPEED_DEFAULT));

			// Other options
			stringDefault = DefaultPreferences.getPyocdConfig();
			fGdbServerOtherOptions.setText(stringDefault);

			// Allocate server console
			if (EclipseUtils.isWindows()) {
				fDoGdbServerAllocateConsole.setSelection(true);
			} else {
				fDoGdbServerAllocateConsole.setSelection(DefaultPreferences.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);
			}

			// Allocate telnet console
			fDoGdbServerAllocateSemihostingConsole
					.setSelection(DefaultPreferences.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT);

		}

		// GDB Client Setup
		{
			// Executable
			stringDefault = DefaultPreferences.getGdbClientExecutable();
			fGdbClientExecutable.setText(stringDefault);

			// Other options
			fGdbClientOtherOptions.setText(DefaultPreferences.GDB_CLIENT_OTHER_OPTIONS_DEFAULT);

			fGdbClientOtherCommands.setText(DefaultPreferences.GDB_CLIENT_OTHER_COMMANDS_DEFAULT);
		}

		// Remote target
		{
			fTargetIpAddress.setText(DefaultPreferences.REMOTE_IP_ADDRESS_DEFAULT); // $NON-NLS-1$

			String portString = Integer.toString(DefaultPreferences.REMOTE_PORT_NUMBER_DEFAULT); // $NON-NLS-1$
			fTargetPortNumber.setText(portString);
		}

		doStartGdbServerChanged();
		overrideTargetChanged();
		updateBoards();
		updateTargets();

		// Force thread update
		fUpdateThreadlistOnSuspend.setSelection(DefaultPreferences.UPDATE_THREAD_LIST_DEFAULT);
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

	@Override
	public void activated(ILaunchConfigurationWorkingCopy workingCopy) {
		// Do nothing. Override is necessary to avoid heavy cost of
		// reinitialization (see super implementation)
	}

	@Override
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		// Do nothing. Override is necessary to avoid heavy unnecessary Apply
		// (see super implementation)
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {

		{
			// legacy definition; although the jtag device class is not used,
			// it must be there, to avoid NPEs
			configuration.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE, ConfigurationAttributes.JTAG_DEVICE);
		}

		boolean booleanValue;
		String stringValue;

		// PyOCD server
		{
			// Start server
			booleanValue = fDoStartGdbServer.getSelection();
			configuration.setAttribute(ConfigurationAttributes.DO_START_GDB_SERVER, booleanValue);
			PersistentPreferences.putGdbServerDoStart(booleanValue);

			// Executable
			stringValue = fGdbServerExecutable.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE, stringValue);
			PersistentPreferences.putGdbServerExecutable(stringValue);

			// Ports
			int port;
			port = Integer.parseInt(fGdbServerGdbPort.getText().trim());
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER, port);

			port = Integer.parseInt(fGdbServerTelnetPort.getText().trim());
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER, port);

			// Board ID
			if (fSelectedBoardId != null) {
				configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_ID, fSelectedBoardId);
			}

			// Target override
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_OVERRIDE_TARGET,
					fGdbServerOverrideTarget.getSelection());

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_TARGET_NAME, fGdbServerTargetName.getText());

			// Misc options
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_HALT_AT_HARD_FAULT,
					fGdbServerHaltAtHardFault.getSelection());

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_STEP_INTO_INTERRUPTS,
					fGdbServerStepIntoInterrupts.getSelection());

			// Flash
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_FLASH_MODE,
					fGdbServerFlashMode.getSelectionIndex());

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_FLASH_FAST_VERIFY,
					fGdbServerFlashFastVerify.getSelection());

			// Semihosting
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_ENABLE_SEMIHOSTING,
					fGdbServerEnableSemihosting.getSelection());

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_USE_GDB_SYSCALLS,
					fGdbServerUseGdbSyscallsForSemihosting.getSelection());

			// Bus speed
			int freq;
			freq = Integer.parseInt(fGdbServerBusSpeed.getText().trim());
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_BUS_SPEED, freq);

			// Other options
			stringValue = fGdbServerOtherOptions.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, stringValue);
			PersistentPreferences.putGdbServerOtherOptions(stringValue);

			// Allocate server console
			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
					fDoGdbServerAllocateConsole.getSelection());

			// Allocate semihosting console
			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
					fDoGdbServerAllocateSemihostingConsole.getSelection());
		}

		// GDB client
		{
			// always use remote
			configuration.setAttribute(IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
					DefaultPreferences.USE_REMOTE_TARGET_DEFAULT);

			stringValue = fGdbClientExecutable.getText().trim();
			configuration.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, stringValue); // DSF
			PersistentPreferences.putGdbClientExecutable(stringValue);

			stringValue = fGdbClientOtherOptions.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS, stringValue);
			PersistentPreferences.putGdbClientOtherOptions(stringValue);

			stringValue = fGdbClientOtherCommands.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS, stringValue);
			PersistentPreferences.putGdbClientCommands(stringValue);
		}

		{
			if (fDoStartGdbServer.getSelection()) {
				configuration.setAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS, "localhost");

				try {
					int port;
					port = Integer.parseInt(fGdbServerGdbPort.getText().trim());
					configuration.setAttribute(IGDBJtagConstants.ATTR_PORT_NUMBER, port);
				} catch (NumberFormatException e) {
					Activator.log(e);
				}
			} else {
				String ip = fTargetIpAddress.getText().trim();
				configuration.setAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS, ip);

				try {
					int port = Integer.valueOf(fTargetPortNumber.getText().trim()).intValue();
					configuration.setAttribute(IGDBJtagConstants.ATTR_PORT_NUMBER, port);
				} catch (NumberFormatException e) {
					Activator.log(e);
				}
			}
		}

		// Force thread update
		configuration.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
				fUpdateThreadlistOnSuspend.getSelection());

		PersistentPreferences.flush();
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

		configuration.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE, ConfigurationAttributes.JTAG_DEVICE);

		String defaultString;
		boolean defaultBoolean;

		// These are inherited from the generic implementation.
		// Some might need some trimming.
		{
			configuration.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
					IGDBLaunchConfigurationConstants.DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND_DEFAULT);
		}

		// PyOCD GDB server setup
		{
			defaultBoolean = PersistentPreferences.getGdbServerDoStart();
			configuration.setAttribute(ConfigurationAttributes.DO_START_GDB_SERVER, defaultBoolean);

			defaultString = PersistentPreferences.getGdbServerExecutable();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE, defaultString);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_CONNECTION_ADDRESS,
					DefaultPreferences.GDB_SERVER_CONNECTION_ADDRESS_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
					DefaultPreferences.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
					DefaultPreferences.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT);

			// Board ID
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_ID,
					DefaultPreferences.GDB_SERVER_BOARD_ID_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_NAME,
					DefaultPreferences.GDB_SERVER_BOARD_NAME_DEFAULT);

			// Bus speed
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_BUS_SPEED,
					DefaultPreferences.GDB_SERVER_BUS_SPEED_DEFAULT);

			// Target override
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_OVERRIDE_TARGET,
					DefaultPreferences.GDB_SERVER_OVERRIDE_TARGET_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_TARGET_NAME,
					DefaultPreferences.GDB_SERVER_TARGET_NAME_DEFAULT);

			// Misc options
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_HALT_AT_HARD_FAULT,
					DefaultPreferences.GDB_SERVER_HALT_AT_HARD_FAULT_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_STEP_INTO_INTERRUPTS,
					DefaultPreferences.GDB_SERVER_STEP_INTO_INTERRUPTS_DEFAULT);

			// Flash
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_FLASH_MODE,
					DefaultPreferences.GDB_SERVER_FLASH_MODE_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_FLASH_FAST_VERIFY,
					DefaultPreferences.GDB_SERVER_FLASH_FAST_VERIFY_DEFAULT);

			// Semihosting
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_ENABLE_SEMIHOSTING,
					DefaultPreferences.GDB_SERVER_ENABLE_SEMIHOSTING_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_USE_GDB_SYSCALLS,
					DefaultPreferences.GDB_SERVER_USE_GDB_SYSCALLS_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_LOG,
					DefaultPreferences.GDB_SERVER_LOG_DEFAULT);

			defaultString = PersistentPreferences.getGdbServerOtherOptions();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, defaultString);

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
					DefaultPreferences.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
					DefaultPreferences.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT);
		}

		// GDB client setup
		{
			defaultString = PersistentPreferences.getGdbClientExecutable();
			configuration.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, defaultString);

			configuration.setAttribute(IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
					DefaultPreferences.USE_REMOTE_TARGET_DEFAULT);

			defaultString = PersistentPreferences.getGdbClientOtherOptions();
			configuration.setAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS, defaultString);

			defaultString = PersistentPreferences.getGdbClientCommands();
			configuration.setAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS, defaultString);
		}

		// Force thread update
		configuration.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
				DefaultPreferences.UPDATE_THREAD_LIST_DEFAULT);
	}

	/**
	 * Register an error
	 * 
	 * <p>
	 * Any number of unique errors can be registered. Only one is shown to the
	 * user. First come first serve.
	 */
	private void registerError(String msg) {
		if (fErrors.isEmpty()) {
			setErrorMessage(msg);
		}
		fErrors.add(msg);
	}

	/**
	 * Remove a previously registered error.
	 * 
	 * If the removed error was being displayed, the next in line (if any) is
	 * shown.
	 */
	private void deregisterError(String msg) {
		if (fErrors.remove(msg)) {
			if (fErrors.isEmpty()) {
				setErrorMessage(null);
			} else {
				setErrorMessage(fErrors.iterator().next());
			}
		}
	}

	// ------------------------------------------------------------------------
}
