/*******************************************************************************
 * Copyright (c) 2007, 2012 QNX Software Systems and others.
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
 *     Anna Dushistova (MontaVista) - bug 241279
 *              - Hardware Debugging: Host name or ip address not saving in
 *                the debug configuration
 *     Andy Jin (QNX) - Added DSF debugging, bug 248593
 *     Bruce Griffith, Sage Electronic Engineering, LLC - bug 305943
 *              - API generalization to become transport-independent (e.g. to
 *                allow connections via serial ports and pipes).
 *     Liviu Ionescu - Arm version
 *     Chris Reed - pyOCD changes
 *     John Cortell - cleanup and fixes
 *     Jonah Graham - fix for Neon
 *     Liviu Ionescu - UI part extraction.
 ******************************************************************************/

package org.eclipse.embedcdt.debug.gdbjtag.pyocd.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.ui.GDBJtagImages;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.embedcdt.core.EclipseUtils;
import org.eclipse.embedcdt.debug.gdbjtag.core.ImmediateDataRequestMonitor;
import org.eclipse.embedcdt.debug.gdbjtag.pyocd.core.Configuration;
import org.eclipse.embedcdt.debug.gdbjtag.pyocd.core.ConfigurationAttributes;
import org.eclipse.embedcdt.debug.gdbjtag.pyocd.core.PyOCD;
import org.eclipse.embedcdt.debug.gdbjtag.pyocd.core.PyOCD.Target;
import org.eclipse.embedcdt.debug.gdbjtag.pyocd.core.preferences.DefaultPreferences;
import org.eclipse.embedcdt.debug.gdbjtag.pyocd.core.preferences.PersistentPreferences;
import org.eclipse.embedcdt.internal.debug.gdbjtag.pyocd.ui.Activator;
import org.eclipse.embedcdt.internal.debug.gdbjtag.pyocd.ui.Messages;
import org.eclipse.embedcdt.internal.debug.gdbjtag.pyocd.ui.preferences.GlobalMcuPage;
import org.eclipse.embedcdt.internal.debug.gdbjtag.pyocd.ui.preferences.WorkspaceMcuPage;
import org.eclipse.embedcdt.internal.debug.gdbjtag.pyocd.ui.properties.ProjectMcuPage;
import org.eclipse.embedcdt.ui.SystemUIJob;
import org.eclipse.jface.window.Window;
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
import org.eclipse.ui.dialogs.PreferencesUtil;

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

	private List<PyOCD.Probe> fProbes;
	private String fSelectedProbeId;
	private boolean fProbeIdListHasUnavailableItem; //!< Whether the probes list includes an item for the probe in the config that is not currently connected.
	private String fProbeIdListUnavailableId; //!< Probe ID for the unavailable item.
	private Map<String, PyOCD.Target> fTargetsByPartNumber; //!< Maps part number (user friendly name) to target object.
	private Map<String, PyOCD.Target> fTargetsByName; //!< Maps target name to target object.

	private Text fGdbClientExecutable;
	private Text fGdbClientPathLabel;
	private Text fGdbClientOtherOptions;
	private Text fGdbClientOtherCommands;

	private Button fDoStartGdbServer;
	private Text fGdbServerPathLabel;
	private Link fLink;

	private Text fTargetIpAddress;
	private Text fTargetPortNumber;

	private Combo fGdbServerProbeId;
	private Button fGdbServerRefreshProbes;

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

	private DefaultPreferences fDefaultPreferences;
	private PersistentPreferences fPersistentPreferences;
	
	private AtomicBoolean fOutstandingProbesLoad = new AtomicBoolean(false);
	private AtomicBoolean fOutstandingTargetsLoad = new AtomicBoolean(false);
	private String fPyocdPathForOutstandingProbesLoad;
	private String fPyocdPathForOutstandingTargetsLoad;
	private RequestMonitor fOutstandingProbesLoadMonitor;
	private RequestMonitor fOutstandingTargetsLoadMonitor;

	/**
	 * Where widgets in a row are rendered in columns, the amount of padding (in
	 * pixels) between columns
	 */
	private static final int COLUMN_PAD = 30;

	private static class Msgs {
		public static final String LOADING_DATA = "DebuggerTab.loading_data_from_pyocd";
		public static final String INVALID_PYOCD_EXECUTABLE = "DebuggerTab.invalid_pyocd_executable";
		public static final String OLD_PYOCD_EXECUTABLE = "DebuggerTab.old_pyocd_executable";
		public static final String PROBES_FAILURE_PARSING_PYOCD_OUTPUT = "DebuggerTab.probes_failure_parsing_output";
		public static final String PROBES_FAILURE_INVOKING_PYOCD = "DebuggerTab.probes_failure_invoking_pyocd";
		public static final String PROBES_PYOCD_TIMEOUT = "DebuggerTab.probes_pyocd_timeout";
		public static final String TARGETS_FAILURE_PARSING_PYOCD_OUTPUT = "DebuggerTab.targets_failure_parsing_output";
		public static final String TARGETS_FAILURE_INVOKING_PYOCD = "DebuggerTab.targets_failure_invoking_pyocd";
		public static final String TARGETS_PYOCD_TIMEOUT = "DebuggerTab.targets_pyocd_timeout";
		public static final String INVALID_GDBSERVER_PORT = "DebuggerTab.invalid_gdbserver_port";
		public static final String INVALID_TELNET_PORT = "DebuggerTab.invalid_telnet_port";
		public static final String INVALID_GDBCLIENT_EXECUTABLE = "DebuggerTab.invalid_gdbclient_executable";
	}
	// ------------------------------------------------------------------------

	protected TabDebugger(TabStartup tabStartup) {
		super();

		fDefaultPreferences = Activator.getInstance().getDefaultPreferences();
		fPersistentPreferences = Activator.getInstance().getPersistentPreferences();
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

		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.createControl() ");
		}

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
		{
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
			group.setText(Messages.getString("DebuggerTab.gdbServerGroup_Text"));
		}

		Composite comp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 5;
			layout.marginHeight = 0;
			comp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			comp.setLayoutData(gd);
		}

		{
			fDoStartGdbServer = new Button(comp, SWT.CHECK);
			fDoStartGdbServer.setText(Messages.getString("DebuggerTab.doStartGdbServer_Text"));
			fDoStartGdbServer.setToolTipText(Messages.getString("DebuggerTab.doStartGdbServer_ToolTipText"));
			GridData gd = new GridData();
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			fDoStartGdbServer.setLayoutData(gd);
		}

		{
			Composite subcomp = new Composite(comp, SWT.NONE);
			{
				GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
				gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
				subcomp.setLayoutData(gd);
				GridLayout layout = new GridLayout(2, false);
				layout.marginWidth = layout.marginHeight = 0;
				subcomp.setLayout(layout);
			}

			{
				Label label = new Label(subcomp, SWT.NONE);
				label.setText(Messages.getString("DebuggerTab.gdbServerExecutable_Label"));
				label.setToolTipText(Messages.getString("DebuggerTab.gdbServerExecutable_ToolTipText"));

				Composite local = createHorizontalLayout(subcomp, 3, 1);
				{
					fGdbServerExecutable = new Text(local, SWT.SINGLE | SWT.BORDER);
					GridData gd = new GridData(GridData.FILL_HORIZONTAL);
					fGdbServerExecutable.setLayoutData(gd);

					fGdbServerBrowseButton = new Button(local, SWT.NONE);
					fGdbServerBrowseButton.setText(Messages.getString("DebuggerTab.gdbServerExecutableBrowse"));

					fGdbServerVariablesButton = new Button(local, SWT.NONE);
					fGdbServerVariablesButton.setText(Messages.getString("DebuggerTab.gdbServerExecutableVariable"));
				}
			}

			{
				Label label = new Label(subcomp, SWT.NONE);
				label.setText(Messages.getString("DebuggerTab.gdbServerActualPath_Label"));

				fGdbServerPathLabel = new Text(subcomp, SWT.SINGLE | SWT.BORDER);
				GridData gd = new GridData(SWT.FILL, 0, true, false);
				gd.horizontalSpan = 1;
				fGdbServerPathLabel.setLayoutData(gd);

				fGdbServerPathLabel.setEnabled(true);
				fGdbServerPathLabel.setEditable(false);
			}

			{
				Label label = new Label(subcomp, SWT.NONE);
				label.setText("");

				fLink = new Link(subcomp, SWT.NONE);
				fLink.setText(Messages.getString("DebuggerTab.gdbServerActualPath_link"));
				GridData gd = new GridData();
				gd.horizontalSpan = 1;
				fLink.setLayoutData(gd);
			}

			{
				Label label = new Label(subcomp, SWT.NONE);
				label.setText(Messages.getString("DebuggerTab.gdbServerGdbPort_Label"));
				label.setToolTipText(Messages.getString("DebuggerTab.gdbServerGdbPort_ToolTipText"));

				Composite subcomp2 = new Composite(subcomp, SWT.NONE);
				GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
				subcomp2.setLayoutData(gd);
				GridLayout layout = new GridLayout(2, false);
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

			{
				Label label = new Label(subcomp, SWT.NONE);
				label.setText(Messages.getString("DebuggerTab.gdbServerTelnetPort_Label"));
				label.setToolTipText(Messages.getString("DebuggerTab.gdbServerTelnetPort_ToolTipText"));

				Composite subcomp2 = new Composite(subcomp, SWT.NONE);
				GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
				subcomp2.setLayoutData(gd);
				GridLayout layout = new GridLayout(2, false);
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
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerProbeId_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerProbeId_ToolTipText"));

			Composite local = createHorizontalLayout(comp, 2, 1);
			{
				fGdbServerProbeId = new Combo(local, SWT.DROP_DOWN | SWT.READ_ONLY);
				GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				fGdbServerProbeId.setLayoutData(gd);
				fGdbServerProbeId.setItems(new String[] {});
				fGdbServerProbeId.select(0);

				fGdbServerRefreshProbes = new Button(local, SWT.NONE);
				fGdbServerRefreshProbes.setText(Messages.getString("DebuggerTab.gdbServerRefreshProbes_Label"));
			}
		}

		{
			fGdbServerOverrideTarget = new Button(comp, SWT.CHECK);
			fGdbServerOverrideTarget.setText(Messages.getString("DebuggerTab.gdbServerOverrideTarget_Label"));
			fGdbServerOverrideTarget
					.setToolTipText(Messages.getString("DebuggerTab.gdbServerOverrideTarget_ToolTipText"));
			GridData gd = new GridData();
			fGdbServerOverrideTarget.setLayoutData(gd);

			fGdbServerTargetName = new Combo(comp, SWT.DROP_DOWN);
			gd = new GridData();
			gd.widthHint = 360;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fGdbServerTargetName.setLayoutData(gd);
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerBusSpeed_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerBusSpeed_ToolTipText"));

			fGdbServerBusSpeed = new Combo(comp, SWT.DROP_DOWN);
			GridData gd = new GridData();
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
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerFlashMode_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerFlashMode_ToolTipText"));
			GridData gd = new GridData();
			label.setLayoutData(gd);

			fGdbServerFlashMode = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
			gd = new GridData();
			gd.widthHint = 120;
			fGdbServerFlashMode.setLayoutData(gd);
			// Note: the index of these items must match the PreferenceConstants.FlashMode constants.
			fGdbServerFlashMode.setItems(new String[] {
					Messages.getString("DebuggerTab.gdbServerFlashMode.AutoErase"),
					Messages.getString("DebuggerTab.gdbServerFlashMode.ChipErase"),
					Messages.getString("DebuggerTab.gdbServerFlashMode.SectorErase"),
					});
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
			GridData gd = new GridData();
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			subcomp.setLayoutData(gd);
			GridLayout layout = new GridLayout(2, false);
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
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerOther_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages.getString("DebuggerTab.gdbServerOther_ToolTipText"));
			GridData gd = new GridData();
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
					updateProbes();
					updateTargets();
				}

				updateGdbServerActualPath();
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

		fLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String text = e.text;
				if (Activator.getInstance().isDebugging()) {
					System.out.println(text);
				}

				int ret = -1;
				if ("global".equals(text)) {
					ret = PreferencesUtil.createPreferenceDialogOn(parent.getShell(), GlobalMcuPage.ID, null, null)
							.open();
				} else if ("workspace".equals(text)) {
					ret = PreferencesUtil.createPreferenceDialogOn(parent.getShell(), WorkspaceMcuPage.ID, null, null)
							.open();
				} else if ("project".equals(text)) {
					assert (fConfiguration != null);
					IProject project = EclipseUtils.getProjectByLaunchConfiguration(fConfiguration);
					ret = PreferencesUtil
							.createPropertyDialogOn(parent.getShell(), project, ProjectMcuPage.ID, null, null, 0)
							.open();
				}

				if (ret == Window.OK) {
					updateGdbServerActualPath();
				}
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

		fGdbServerProbeId.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				probeWasSelected(((Combo) e.widget).getSelectionIndex());
				scheduleUpdateJob();
			}
		});

		fGdbServerRefreshProbes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateProbes();
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
		{
			group.setText(Messages.getString("DebuggerTab.gdbSetupGroup_Text"));
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
		}

		Composite comp = new Composite(group, SWT.NONE);
		{
			GridLayout layout = new GridLayout();
			layout.numColumns = 5;
			layout.marginHeight = 0;
			comp.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			comp.setLayoutData(gd);
		}

		Button browseButton;
		Button variableButton;
		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbCommand_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbCommand_ToolTipText"));

			Composite local = createHorizontalLayout(comp, 3, 1);
			{
				fGdbClientExecutable = new Text(local, SWT.SINGLE | SWT.BORDER);
				GridData gd = new GridData(GridData.FILL_HORIZONTAL);
				fGdbClientExecutable.setLayoutData(gd);

				browseButton = new Button(local, SWT.NONE);
				browseButton.setText(Messages.getString("DebuggerTab.gdbCommandBrowse"));

				variableButton = new Button(local, SWT.NONE);
				variableButton.setText(Messages.getString("DebuggerTab.gdbCommandVariable"));
			}
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbCommandActualPath_Label"));

			fGdbClientPathLabel = new Text(comp, SWT.SINGLE | SWT.BORDER);
			GridData gd = new GridData(SWT.FILL, 0, true, false);
			gd.horizontalSpan = 4;
			fGdbClientPathLabel.setLayoutData(gd);

			fGdbClientPathLabel.setEnabled(true);
			fGdbClientPathLabel.setEditable(false);
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbOtherOptions_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbOtherOptions_ToolTipText"));
			GridData gd = new GridData();
			label.setLayoutData(gd);

			fGdbClientOtherOptions = new Text(comp, SWT.SINGLE | SWT.BORDER);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fGdbClientOtherOptions.setLayoutData(gd);
		}

		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbOtherCommands_Label"));
			label.setToolTipText(Messages.getString("DebuggerTab.gdbOtherCommands_ToolTipText"));
			GridData gd = new GridData();
			gd.verticalAlignment = SWT.TOP;
			label.setLayoutData(gd);

			fGdbClientOtherCommands = new Text(comp, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 60;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			fGdbClientOtherCommands.setLayoutData(gd);
		}

		// ----- Actions ------------------------------------------------------

		fGdbClientExecutable.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
				updateGdbClientActualPath();
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
		{
			group.setText(Messages.getString("DebuggerTab.remoteGroup_Text"));
			GridLayout layout = new GridLayout();
			group.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			group.setLayoutData(gd);
		}

		Composite comp = createHorizontalLayout(group, 2, -1);
		{
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			comp.setLayoutData(gd);
		}

		// Create entry fields for TCP/IP connections
		{
			Label label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.ipAddressLabel")); //$NON-NLS-1$

			fTargetIpAddress = new Text(comp, SWT.BORDER);
			GridData gd = new GridData();
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

	private void updateGdbServerActualPath() {

		if (fConfiguration == null) {
			return;
		}
		String fullCommand = Configuration.getGdbServerCommand(fConfiguration, fGdbServerExecutable.getText());
		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.updateActualpath() \"" + fullCommand + "\"");
		}
		if (fullCommand == null) {
			fGdbServerPathLabel.setText("");
		} else {
			fGdbServerPathLabel.setText(fullCommand);
		}
	}

	private void updateGdbClientActualPath() {

		if (fConfiguration == null) {
			return;
		}
		String fullCommand = Configuration.getGdbClientCommand(fConfiguration, fGdbClientExecutable.getText());
		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.updateGdbClientActualPath() \"" + fullCommand + "\"");
		}
		if (fullCommand == null) {
			fGdbClientPathLabel.setText("");
		} else {
			fGdbClientPathLabel.setText(fullCommand);
		}
	}

	private void doStartGdbServerChanged() {

		boolean enabled = fDoStartGdbServer.getSelection();

		fGdbServerExecutable.setEnabled(enabled);
		fGdbServerBrowseButton.setEnabled(enabled);
		fGdbServerVariablesButton.setEnabled(enabled);
		fGdbServerOtherOptions.setEnabled(enabled);

		fGdbServerGdbPort.setEnabled(enabled);
		fGdbServerTelnetPort.setEnabled(enabled);
		fGdbServerProbeId.setEnabled(enabled);
		fGdbServerBusSpeed.setEnabled(enabled);
		fGdbServerOverrideTarget.setEnabled(enabled);
		fGdbServerTargetName.setEnabled(enabled && fGdbServerOverrideTarget.getSelection());
		fGdbServerHaltAtHardFault.setEnabled(enabled);
		fGdbServerStepIntoInterrupts.setEnabled(enabled);
		fGdbServerEnableSemihosting.setEnabled(enabled);
		fGdbServerUseGdbSyscallsForSemihosting.setEnabled(enabled);
		fGdbServerFlashMode.setEnabled(enabled);
		fGdbServerFlashFastVerify.setEnabled(enabled);

		fDoGdbServerAllocateConsole.setEnabled(enabled);
		fDoGdbServerAllocateSemihostingConsole.setEnabled(enabled);

		// Disable remote target params when the server is started
		fTargetIpAddress.setEnabled(!enabled);
		fTargetPortNumber.setEnabled(!enabled);

		fGdbServerPathLabel.setEnabled(enabled);
		fLink.setEnabled(enabled);
	}

	private void overrideTargetChanged() {
		boolean enabled = fGdbServerOverrideTarget.getSelection();

		fGdbServerTargetName.setEnabled(enabled);
	}

	/**
	 * Resolve the string in the gdbserver field and validate it. Return the result
	 * if valid, otherwise return null.
	 * 
	 * @return an absolute path, relative path or just the name of the executable
	 *         (if it's in PATH)
	 */
	private String getPyOCDExecutablePath() {
		// Clear errors first.
		deregisterError(Msgs.INVALID_PYOCD_EXECUTABLE);
		deregisterError(Msgs.OLD_PYOCD_EXECUTABLE);
		
		String path = Configuration.getGdbServerCommand(fConfiguration, fGdbServerExecutable.getText());
		if (path == null || path.isEmpty()) {
			registerError(Msgs.INVALID_PYOCD_EXECUTABLE);
			return null;
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.printf("pyOCD resolved path = %s\n", path);
		}

		// Validate path.

		// First check if the file is a directory.
		File file = new File(path);
		if (!file.exists()) {
			registerError(Msgs.INVALID_PYOCD_EXECUTABLE);
			return null;
		}
		else if (file.isDirectory()) {
			// TODO: Use java.nio.Files when we move to Java 7 to also check
			// that file is executable
			if (Activator.getInstance().isDebugging()) {
				System.out.printf("pyOCD path is invalid\n");
			}
			registerError(Msgs.INVALID_PYOCD_EXECUTABLE);
			return null;
		}

		return path;
	}

	private int indexForProbeId(String probeId) {
		// Search for a matching probe.
		if (fProbes != null) {
			int index = 0;
			for (PyOCD.Probe b : fProbes) {
				if (b.fUniqueId.equals(probeId)) {
					return index;
				}
				index += 1;
			}
		}
		return -1;
	}

	private void probeWasSelected(int index) {
		if (fProbeIdListHasUnavailableItem) {
			if (index == 0) {
				fSelectedProbeId = fProbeIdListUnavailableId;
				return;
			}
			else {
				index -= 1;
			}
		}
		PyOCD.Probe selectedProbe = fProbes.get(index);
		fSelectedProbeId = selectedProbe.fUniqueId;
	}

	private void selectActiveProbe() {
		// Get current probe ID.
		int index = indexForProbeId(fSelectedProbeId);
		if (index != -1) {
			if (fProbeIdListHasUnavailableItem) {
				index += 1;
			}
			
			fGdbServerProbeId.select(index);
		} else {
			assert(fProbeIdListHasUnavailableItem);
			fGdbServerProbeId.select(0);
		}
	}
	
	private void selectActiveTarget() {
		if (fConfiguration != null) {
			try {
				// Convert target name to part number.
				String configTargetName = fConfiguration.getAttribute(ConfigurationAttributes.GDB_SERVER_TARGET_NAME,
						DefaultPreferences.GDB_SERVER_TARGET_NAME_DEFAULT);
				Target target = fTargetsByName.get(configTargetName);
				if (target != null) {
					fGdbServerTargetName.setText(target.fPartNumber);
				}
				else {
					// Support arbitrary target names entered by the user.
					fGdbServerTargetName.setText(configTargetName);
				}
			} catch (CoreException e) {
				Activator.log(e.getStatus());
			}
		}
	}
	
	private void clearPyocdErrors(boolean isProbe) {
		if (isProbe) {
			deregisterError(Msgs.PROBES_FAILURE_PARSING_PYOCD_OUTPUT);
			deregisterError(Msgs.PROBES_FAILURE_INVOKING_PYOCD);
			deregisterError(Msgs.PROBES_PYOCD_TIMEOUT);
		}
		else {
			deregisterError(Msgs.TARGETS_FAILURE_PARSING_PYOCD_OUTPUT);
			deregisterError(Msgs.TARGETS_FAILURE_INVOKING_PYOCD);
			deregisterError(Msgs.TARGETS_PYOCD_TIMEOUT);
		}
	}
	
	private void setPyocdError(IStatus status, boolean isProbe) {
		if (isProbe) {
			switch (status.getCode()) {
				case PyOCD.Errors.ERROR_INVALID_JSON_FORMAT:
				case PyOCD.Errors.ERROR_PARSING_OUTPUT:
					registerError(Msgs.PROBES_FAILURE_PARSING_PYOCD_OUTPUT);
					break;
				case PyOCD.Errors.ERROR_TIMEOUT:
					registerError(Msgs.PROBES_PYOCD_TIMEOUT);
					break;
				case PyOCD.Errors.ERROR_RUNNING_PYOCD:
				default:
					registerError(Msgs.PROBES_FAILURE_INVOKING_PYOCD);
					break;
			}
		}
		else {
			switch (status.getCode()) {
			case PyOCD.Errors.ERROR_INVALID_JSON_FORMAT:
			case PyOCD.Errors.ERROR_PARSING_OUTPUT:
				registerError(Msgs.TARGETS_FAILURE_PARSING_PYOCD_OUTPUT);
				break;
			case PyOCD.Errors.ERROR_TIMEOUT:
				registerError(Msgs.TARGETS_PYOCD_TIMEOUT);
				break;
			case PyOCD.Errors.ERROR_RUNNING_PYOCD:
			default:
				registerError(Msgs.TARGETS_FAILURE_INVOKING_PYOCD);
				break;
		}
		}
	}
	
	private void updatePyocdLoadingMessage() {
		if (fOutstandingProbesLoad.getAcquire() || fOutstandingTargetsLoad.getAcquire()) {
			setMessage(Messages.getString(Msgs.LOADING_DATA));
		}
		else {
			setMessage(null);
		}
		scheduleUpdateJob();
		
	}

	private void updateProbes() {
		String path = getPyOCDExecutablePath();
		if (path != null) {
			if (fOutstandingProbesLoad.getAcquire()) {
				// If the pyocd path is the same as for the currently running call to pyocd, then
				// ignore the update request.
				if (path.equals(fPyocdPathForOutstandingProbesLoad)) {
					if (Activator.getInstance().isDebugging()) {
						System.out.printf("skipping probes load due to outstanding load\n");
					}
					return;
				}
				
				// Handle the case where the pyocd executable path has changed before an async call
				// to get probes from pyocd finished.
				fOutstandingProbesLoadMonitor.cancel();
				
				// FIXME potential race from here to when the fPyocdPathForOutstandingProbesLoad and
				// fOutstandingProbesLoadMonitor are updated.
			}

			fOutstandingProbesLoad.setRelease(true);
			
			// Save this pyocd path.
			fPyocdPathForOutstandingProbesLoad = path;
			
			updatePyocdLoadingMessage();
			
			var monitor = new ImmediateDataRequestMonitor<List<PyOCD.Probe>>() {
						@Override
						protected void handleCompleted() {
							fOutstandingProbesLoad.setRelease(false);
							fOutstandingProbesLoadMonitor = null;

							if (getControl().isDisposed()) {
								if (Activator.getInstance().isDebugging()) {
									System.out.printf("(probes) bailing on updating debugger tab because it has been disposed\n");
								}
								return;
							}
						
							ArrayList<String> itemList = new ArrayList<String>();
							
							IStatus status = getStatus();
							final boolean isOK = status.isOK();
							if (!isOK) {
								if (Activator.getInstance().isDebugging()) {
									System.out.printf("error getting probes from pyocd: %s\n", status.toString());
								}
								setPyocdError(getStatus(), false);
								fProbes = new ArrayList<PyOCD.Probe>();
							}
							else {
								clearPyocdErrors(true);
								setMessage(null);
								
								List<PyOCD.Probe> probes = getData();
								assert(probes != null);
								
								if (Activator.getInstance().isDebugging()) {
									System.out.printf("probes = %s\n", probes);
								}
	
								Collections.sort(probes, PyOCD.Probe.DESCRIPTION_COMPARATOR);
	
								fProbes = probes;
								
								// Figure out if the selected probe is connected. This also handles the case where
								// no selected probe is stored in the launch config (fSelectedProbeId is empty).
								int currentProbeIndex = indexForProbeId(fSelectedProbeId);
								if (currentProbeIndex == -1) {
									fProbeIdListHasUnavailableItem = true;
									fProbeIdListUnavailableId = fSelectedProbeId;
									
									// The message shown in the menu depends on whether the selected probe ID is
									// empty (no selection) or not (valid selection but probe isn't available).
									if (fSelectedProbeId.isEmpty()) {
										itemList.add(Messages.getString("DebuggerTab.gdbServerSelectProbe"));
									}
									else {
										itemList.add(String.format(
												Messages.getString("DebuggerTab.gdbServerUnconnectedProbe"),
												fSelectedProbeId));
									}
								}
								else {
									fProbeIdListHasUnavailableItem = false;
								}
	
								for (PyOCD.Probe probe : probes) {
									itemList.add(probe.getDescription());
								}
							}

							final String[] items = itemList.toArray(new String[itemList.size()]);

							SystemUIJob updateJob = new SystemUIJob("update probes") {
								@Override
								public IStatus runInUIThread(IProgressMonitor monitor) {
									// Check again to make sure we haven't been disposed.
									if (getControl().isDisposed()) {
										if (Activator.getInstance().isDebugging()) {
											System.out.printf("(probes, from UI job) bailing on updating debugger tab because it has been disposed\n");
										}
										return Status.OK_STATUS;
									}
									
									fGdbServerProbeId.setItems(items);

									selectActiveProbe();
									
									updatePyocdLoadingMessage();
									
									return Status.OK_STATUS;
								}
							};
							updateJob.schedule();
						}
					};
			fOutstandingProbesLoadMonitor = monitor;
			PyOCD.getInstance().getProbes(path, monitor);
		}
		else {
			fGdbServerProbeId.setItems(new String[] {});
			fProbes = new ArrayList<PyOCD.Probe>();
		}
	}

	private void updateTargets() {
		String path = getPyOCDExecutablePath();
		if (path != null) {
			if (fOutstandingTargetsLoad.getAcquire()) {
				// If the pyocd path is the same as for the currently running call to pyocd, then
				// ignore the update request.
				if (path.equals(fPyocdPathForOutstandingTargetsLoad)) {
					if (Activator.getInstance().isDebugging()) {
						System.out.printf("skipping targets load due to outstanding load\n");
					}
					return;
				}
				
				// Handle the case where the pyocd executable path has changed before an async call
				// to get targets from pyocd finished.
				fOutstandingTargetsLoadMonitor.cancel();
				
				// FIXME potential race from here to when the fPyocdPathForOutstandingTargetsLoad and
				// fOutstandingTargetsLoadMonitor are updated.
			}
			
			fOutstandingTargetsLoad.setRelease(true);
			
			// Save this pyocd path.
			fPyocdPathForOutstandingTargetsLoad = path;
			
			updatePyocdLoadingMessage();
			
			var monitor = new ImmediateDataRequestMonitor<List<PyOCD.Target>>() {
						@Override
						protected void handleCompleted() {
							fOutstandingTargetsLoad.setRelease(false);
							fOutstandingTargetsLoadMonitor = null;

							if (getControl().isDisposed()) {
								if (Activator.getInstance().isDebugging()) {
									System.out.printf("(targets) bailing on updating debugger tab because it has been disposed\n");
								}
								return;
							}
							
							ArrayList<String> itemList = new ArrayList<String>();
							
							// Create maps to go between target part number and name.
							fTargetsByPartNumber = new HashMap<>();
							fTargetsByName = new HashMap<>();
							
							IStatus status = getStatus();
							final boolean isOK = status.isOK();
							if (!isOK) {
								if (Activator.getInstance().isDebugging()) {
									System.out.printf("error getting targets from pyocd: %s\n", status.toString());
								}
								setPyocdError(getStatus(), false);
							}
							else {
								clearPyocdErrors(false);
								setMessage(null);
							
								List<PyOCD.Target> targets = getData();
								assert(targets != null);
								
								if (Activator.getInstance().isDebugging()) {
									System.out.printf("target = %s\n", targets);
								}
	
								Collections.sort(targets, PyOCD.Target.PART_NUMBER_COMPARATOR);
								
								for (PyOCD.Target target : targets) {
									itemList.add(String.format("%s", target.getFullPartName()));
									fTargetsByPartNumber.put(target.fPartNumber, target);
									fTargetsByName.put(target.fName, target);
								}
							}
							final String[] itemsToUpdate = itemList.toArray(new String[itemList.size()]);

							SystemUIJob updateJob = new SystemUIJob("update targets") {
								@Override
								public IStatus runInUIThread(IProgressMonitor monitor) {
									// Check again to make sure we haven't been disposed.
									if (getControl().isDisposed()) {
										if (Activator.getInstance().isDebugging()) {
											System.out.printf("(targets, from UI job) bailing on updating debugger tab because it has been disposed\n");
										}
										return Status.OK_STATUS;
									}
									
									fGdbServerTargetName.setItems(itemsToUpdate);

									// Select current target from config.
									selectActiveTarget();
									
									scheduleUpdateJob();
									
									updatePyocdLoadingMessage();									
									
									return Status.OK_STATUS;
								}
							};
							updateJob.schedule();
						}
					};
			fOutstandingTargetsLoadMonitor = monitor;
			PyOCD.getInstance().getTargets(path, monitor);
		} else {
			// Clear combobox and show error
			fGdbServerTargetName.setItems(new String[] {});
			fTargetsByPartNumber = new HashMap<>();
			fTargetsByName = new HashMap<>();
		}
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.initializeFrom() " + configuration.getName());
		}

		fConfiguration = configuration;

		try {
			Boolean booleanDefault;
			String stringDefault;

			// PyOCD GDB server
			{
				// Start server locally
				booleanDefault = fPersistentPreferences.getGdbServerDoStart();
				fDoStartGdbServer.setSelection(
						configuration.getAttribute(ConfigurationAttributes.DO_START_GDB_SERVER, booleanDefault));

				// Executable
				stringDefault = fPersistentPreferences.getGdbServerExecutable();
				fGdbServerExecutable.setText(
						configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE, stringDefault));

				// Ports
				fGdbServerGdbPort.setText(
						Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
								DefaultPreferences.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT)));

				fGdbServerTelnetPort.setText(Integer
						.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
								DefaultPreferences.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT)));

				// Probe ID
				fSelectedProbeId = configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_ID,
						DefaultPreferences.GDB_SERVER_BOARD_ID_DEFAULT);
				// active probe will be selected after updateProbes() runs.

				// Target override
				fGdbServerOverrideTarget
						.setSelection(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_OVERRIDE_TARGET,
								DefaultPreferences.GDB_SERVER_OVERRIDE_TARGET_DEFAULT));

				fGdbServerTargetName.setText(""); // will be updated with updateTargets() call below. 

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
				booleanDefault = fPersistentPreferences.getPyOCDEnableSemihosting();
				fGdbServerEnableSemihosting
						.setSelection(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_ENABLE_SEMIHOSTING,
								booleanDefault));

				fGdbServerUseGdbSyscallsForSemihosting
						.setSelection(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_USE_GDB_SYSCALLS,
								DefaultPreferences.GDB_SERVER_USE_GDB_SYSCALLS_DEFAULT));

				// Bus speed
				fGdbServerBusSpeed.setText(
						Integer.toString(configuration.getAttribute(ConfigurationAttributes.GDB_SERVER_BUS_SPEED,
								DefaultPreferences.GDB_SERVER_BUS_SPEED_DEFAULT)));

				// Other options
				stringDefault = fPersistentPreferences.getGdbServerOtherOptions();
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
				stringDefault = fPersistentPreferences.getGdbClientExecutable();
				String gdbCommandAttr = configuration.getAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
						stringDefault);
				fGdbClientExecutable.setText(gdbCommandAttr);

				// Other options
				stringDefault = fPersistentPreferences.getGdbClientOtherOptions();
				fGdbClientOtherOptions.setText(
						configuration.getAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS, stringDefault));

				stringDefault = fPersistentPreferences.getGdbClientCommands();
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

			// Force thread update
			{
				boolean updateThreadsOnSuspend = configuration.getAttribute(
						IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
						DefaultPreferences.UPDATE_THREAD_LIST_DEFAULT);
				fUpdateThreadlistOnSuspend.setSelection(updateThreadsOnSuspend);
			}
			
			doStartGdbServerChanged();
			overrideTargetChanged();
			updateProbes();
			updateTargets();

		} catch (CoreException e) {
			Activator.log(e.getStatus());
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.initializeFrom() completed " + configuration.getName());
		}
	}

	public void initializeFromDefaults() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.initializeFromDefaults()");
		}

		String stringDefault;

		// PyOCD GDB server
		{
			// Start server locally
			fDoStartGdbServer.setSelection(DefaultPreferences.DO_START_GDB_SERVER_DEFAULT);

			// Executable
			stringDefault = fDefaultPreferences.getGdbServerExecutable();
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
			stringDefault = fDefaultPreferences.getPyocdConfig();
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
			stringDefault = fDefaultPreferences.getGdbClientExecutable();
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
		updateProbes();
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
		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.activated() " + workingCopy.getName());
		}
		// Do nothing. Override is necessary to avoid heavy cost of
		// reinitialization (see super implementation)
	}

	@Override
	public void deactivated(ILaunchConfigurationWorkingCopy workingCopy) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.deactivated() " + workingCopy.getName());
		}
		// Do nothing. Override is necessary to avoid heavy unnecessary Apply
		// (see super implementation)
	}
	
	@Override
	public void dispose() {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.dispose()");
		}
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.isValid() " + launchConfig.getName());
		}

		if (!(fOutstandingProbesLoad.getAcquire() || fOutstandingTargetsLoad.getAcquire())) {
			setMessage(null);
		}

		boolean result = true;

		if (fDoStartGdbServer != null && fDoStartGdbServer.getSelection()) {
			// Quick check if the field is filled in. getPyOCDExecutablePath() will handle showing an error.
			if (fGdbServerExecutable != null && fGdbServerExecutable.getText().trim().isEmpty()) {
				result = false;
			}

			if (fGdbServerGdbPort != null && fGdbServerGdbPort.getText().trim().isEmpty()) {
				registerError(Msgs.INVALID_GDBSERVER_PORT);
				result = false;
			}
			else {
				deregisterError(Msgs.INVALID_GDBSERVER_PORT);
			}

			if (fGdbServerTelnetPort != null && fGdbServerTelnetPort.getText().trim().isEmpty()) {
				registerError(Msgs.INVALID_TELNET_PORT);
				result = false;
			}
			else {
				deregisterError(Msgs.INVALID_TELNET_PORT);
			}
		}

		if (fGdbClientExecutable != null && fGdbClientExecutable.getText().trim().isEmpty()) {
			registerError(Msgs.INVALID_GDBCLIENT_EXECUTABLE);
			result = false;
		}
		else {
			deregisterError(Msgs.INVALID_GDBCLIENT_EXECUTABLE);
		}

		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.isValid() " + launchConfig.getName() + " = " + result);
		}

		return result;
	}

	@Override
	public boolean canSave() {
		if (fDoStartGdbServer != null && fDoStartGdbServer.getSelection()) {
			if (fGdbServerExecutable != null && fGdbServerExecutable.getText().trim().isEmpty())
				return false;

			if (fGdbServerGdbPort != null && fGdbServerGdbPort.getText().trim().isEmpty())
				return false;

			if (fGdbServerTelnetPort != null && fGdbServerTelnetPort.getText().trim().isEmpty())
				return false;
		}

		return true;
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {

		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.performApply() " + configuration.getName() + ", dirty=" + isDirty());
		}

		{
			// legacy definition; although the jtag device class is not used,
			// it must be there, to avoid NPEs
			configuration.setAttribute(ConfigurationAttributes.ATTR_JTAG_DEVICE, ConfigurationAttributes.JTAG_DEVICE);
		}

		boolean booleanValue;
		String stringValue;

		// PyOCD server
		{
			// Start server
			booleanValue = fDoStartGdbServer.getSelection();
			configuration.setAttribute(ConfigurationAttributes.DO_START_GDB_SERVER, booleanValue);
			fPersistentPreferences.putGdbServerDoStart(booleanValue);

			// Executable
			stringValue = fGdbServerExecutable.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE, stringValue);
			fPersistentPreferences.putGdbServerExecutable(stringValue);

			// Ports
			int port;
			if (!fGdbServerGdbPort.getText().trim().isEmpty()) {
				port = Integer.parseInt(fGdbServerGdbPort.getText().trim());
				configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER, port);
			} else {
				Activator.log("empty fGdbServerGdbPort");
			}

			if (!fGdbServerTelnetPort.getText().trim().isEmpty()) {
				port = Integer.parseInt(fGdbServerTelnetPort.getText().trim());
				configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER, port);
			} else {
				Activator.log("empty fGdbServerTelnetPort");
			}

			// Probe ID
			if (fSelectedProbeId != null) {
				configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_BOARD_ID, fSelectedProbeId);
			}

			// Target override
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_OVERRIDE_TARGET,
					fGdbServerOverrideTarget.getSelection());

			// Target type. Only save into the config if a valid targets list has been loaded.
			if (fTargetsByPartNumber != null) {
				String targetPartNumber = fGdbServerTargetName.getText().trim();
				String targetName = "";
				if (!targetPartNumber.isEmpty()) {
					Target target = fTargetsByPartNumber.get(targetPartNumber);
					if (target != null) {
						targetName = target.fName;
					}
					else {
						// If the user enters a target name that we can't find, just use as-is.
						targetName = targetPartNumber;
					}
				}
				configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_TARGET_NAME, targetName);
			}

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
			booleanValue = fGdbServerEnableSemihosting.getSelection();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_ENABLE_SEMIHOSTING,
					booleanValue);
			fPersistentPreferences.putPyOCDEnableSemihosting(booleanValue);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_USE_GDB_SYSCALLS,
					fGdbServerUseGdbSyscallsForSemihosting.getSelection());

			// Bus speed
			int freq;
			freq = Integer.parseInt(fGdbServerBusSpeed.getText().trim());
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_BUS_SPEED, freq);

			// Other options
			stringValue = fGdbServerOtherOptions.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, stringValue);
			fPersistentPreferences.putGdbServerOtherOptions(stringValue);

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
			fPersistentPreferences.putGdbClientExecutable(stringValue);

			stringValue = fGdbClientOtherOptions.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS, stringValue);
			fPersistentPreferences.putGdbClientOtherOptions(stringValue);

			stringValue = fGdbClientOtherCommands.getText().trim();
			configuration.setAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS, stringValue);
			fPersistentPreferences.putGdbClientCommands(stringValue);
		}

		{
			if (fDoStartGdbServer.getSelection()) {
				configuration.setAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS, "localhost");

				String str = fGdbServerGdbPort.getText().trim();
				if (!str.isEmpty()) {
					try {
						int port;
						port = Integer.parseInt(str);
						configuration.setAttribute(IGDBJtagConstants.ATTR_PORT_NUMBER, port);
					} catch (NumberFormatException e) {
						Activator.log(e);
					}
				}
			} else {
				String ip = fTargetIpAddress.getText().trim();
				configuration.setAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS, ip);

				String str = fTargetPortNumber.getText().trim();
				if (!str.isEmpty()) {
					try {
						int port = Integer.valueOf(str).intValue();
						configuration.setAttribute(IGDBJtagConstants.ATTR_PORT_NUMBER, port);
					} catch (NumberFormatException e) {
						Activator.log(e);
					}
				}
			}
		}

		// Force thread update
		configuration.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
				fUpdateThreadlistOnSuspend.getSelection());

		fPersistentPreferences.flush();

		if (Activator.getInstance().isDebugging()) {
			System.out.println(
					"pyocd.TabDebugger.performApply() completed " + configuration.getName() + ", dirty=" + isDirty());
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

		configuration.setAttribute(ConfigurationAttributes.ATTR_JTAG_DEVICE, ConfigurationAttributes.JTAG_DEVICE);

		if (Activator.getInstance().isDebugging()) {
			System.out.println("pyocd.TabDebugger.setDefaults() " + configuration.getName());
		}

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
			defaultBoolean = fPersistentPreferences.getGdbServerDoStart();
			configuration.setAttribute(ConfigurationAttributes.DO_START_GDB_SERVER, defaultBoolean);

			defaultString = fPersistentPreferences.getGdbServerExecutable();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_EXECUTABLE, defaultString);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_CONNECTION_ADDRESS,
					DefaultPreferences.GDB_SERVER_CONNECTION_ADDRESS_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
					DefaultPreferences.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
					DefaultPreferences.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT);

			// Probe ID
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
			defaultBoolean = fPersistentPreferences.getPyOCDEnableSemihosting();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_ENABLE_SEMIHOSTING,
					defaultBoolean);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_USE_GDB_SYSCALLS,
					DefaultPreferences.GDB_SERVER_USE_GDB_SYSCALLS_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_LOG,
					DefaultPreferences.GDB_SERVER_LOG_DEFAULT);

			defaultString = fPersistentPreferences.getGdbServerOtherOptions();
			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_OTHER, defaultString);

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
					DefaultPreferences.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
					DefaultPreferences.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT);
		}

		// GDB client setup
		{
			defaultString = fPersistentPreferences.getGdbClientExecutable();
			configuration.setAttribute(IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME, defaultString);

			configuration.setAttribute(IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
					DefaultPreferences.USE_REMOTE_TARGET_DEFAULT);

			defaultString = fPersistentPreferences.getGdbClientOtherOptions();
			configuration.setAttribute(ConfigurationAttributes.GDB_CLIENT_OTHER_OPTIONS, defaultString);

			defaultString = fPersistentPreferences.getGdbClientCommands();
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
	 * Any number of unique errors can be registered. Only one is shown to the user.
	 * First come first serve.
	 */
	private void registerError(String msg) {
		if (fErrors.isEmpty()) {
			setErrorMessage(Messages.getString(msg));
		}
		fErrors.add(msg);
	}

	/**
	 * Remove a previously registered error.
	 * 
	 * If the removed error was being displayed, the next in line (if any) is shown.
	 */
	private void deregisterError(String msg) {
		if (fErrors.remove(msg)) {
			if (fErrors.isEmpty()) {
				setErrorMessage(null);
			} else {
				setErrorMessage(Messages.getString(fErrors.iterator().next()));
			}
		}
	}

	// ------------------------------------------------------------------------
}
