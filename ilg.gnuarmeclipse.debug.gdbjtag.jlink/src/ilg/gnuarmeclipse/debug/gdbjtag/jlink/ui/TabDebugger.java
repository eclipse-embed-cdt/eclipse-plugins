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
 ******************************************************************************/

package ilg.gnuarmeclipse.debug.gdbjtag.jlink.ui;

import ilg.gnuarmeclipse.debug.gdbjtag.jlink.Activator;
import ilg.gnuarmeclipse.debug.gdbjtag.jlink.ConfigurationAttributes;
import ilg.gnuarmeclipse.debug.gdbjtag.jlink.PreferenceConstants;
import ilg.gnuarmeclipse.debug.gdbjtag.jlink.SharedStorage;

import java.io.File;

import org.eclipse.cdt.debug.gdbjtag.core.IGDBJtagConstants;
import org.eclipse.cdt.debug.gdbjtag.ui.GDBJtagImages;
import org.eclipse.cdt.debug.mi.core.IMILaunchConfigurationConstants;
import org.eclipse.cdt.debug.mi.core.MIPlugin;
import org.eclipse.cdt.debug.mi.core.command.factories.CommandFactoryDescriptor;
import org.eclipse.cdt.debug.mi.core.command.factories.CommandFactoryManager;
import org.eclipse.cdt.dsf.gdb.IGDBLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.internal.GdbPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.StringVariableSelectionDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

/**
 * @since 7.0
 */
public class TabDebugger extends AbstractLaunchConfigurationTab {

	private static final String TAB_NAME = "Debugger";
	private static final String TAB_ID = Activator.PLUGIN_ID
			+ ".ui.debuggertab";

	private Button doStartGdbServer;
	private Text gdbClientExecutable;
	private boolean didGdbClientExecutableChanged;
	private Text gdbClientOtherCommands;
	// private Button useRemote;
	// private Combo jtagDevice;
	// private Composite remoteConnectionParameters;
	// private StackLayout remoteConnectParmsLayout;
	// private Composite remoteTcpipBox;
	private Text targetIpAddress;
	private Text targetPortNumber;
	// private Composite remoteConnectionBox;
	// private Text connection;
	// private String savedJtagDevice;
	private Text flashDeviceName;
	private boolean didFlashDeviceNameChanged;
	private Button endiannessLittle;
	private Button endiannessBig;
	private Button interfaceJtag;
	private Button interfaceSwd;

	private Button gdbServerSpeedAuto;
	private Button gdbServerSpeedAdaptive;
	private Button gdbServerSpeedFixed;
	private Text gdbServerSpeedFixedValue;

	private Button doConnectToRunning;

	private Text gdbServerGdbPort;
	private Text gdbServerSwoPort;
	private Text gdbServerTelnetPort;
	// private Label gdbServerCommandLabel;
	private Text gdbServerExecutable;
	private boolean didGdbServerExecutableChanged;
	private Button gdbServerBrowse;
	private Button gdbServerVariables;

	private Button doGdbServerVerifyDownload;
	private Button doGdbServerInitRegs;
	private Button doGdbServerLocalOnly;
	private Button doGdbServerSilent;

	private Text gdbServerLog;
	private Button gdbServerLogBrowse;
	private Text gdbServerOther;

	private Button doGdbServerAllocateConsole;
	private Button doGdbServerAllocateSemihostingConsole;

	protected Button fUpdateThreadlistOnSuspend;

	private TabStartup tabStartup;

	TabDebugger(TabStartup tabStartup) {
		super();
		this.tabStartup = tabStartup;
	}

	@Override
	public String getName() {
		return TAB_NAME;
	}

	@Override
	public Image getImage() {
		return GDBJtagImages.getDebuggerTabImage();
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

		{
			Composite dual = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			dual.setLayout(layout);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			dual.setLayoutData(gd);

			createInterfaceControl(dual);
			createDeviceControls(dual);
		}

		createGdbServerGroup(comp);

		createGdbClientControls(comp);
		createRemoteControl(comp);

		fUpdateThreadlistOnSuspend = new Button(comp, SWT.CHECK);
		fUpdateThreadlistOnSuspend.setText(Messages
				.getString("DebuggerTab.update_thread_list_on_suspend_Text"));
		fUpdateThreadlistOnSuspend
				.setToolTipText(Messages
						.getString("DebuggerTab.update_thread_list_on_suspend_ToolTipText"));

		fUpdateThreadlistOnSuspend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
	}

	private void createInterfaceControl(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("DebuggerTab.interfaceGroup_Text"));
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		Composite comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = 0;
		comp.setLayout(layout);

		{
			Composite radio = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 3;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			radio.setLayout(layout);
			{
				Label label = new Label(radio, SWT.NONE);
				label.setText(Messages.getString("DebuggerTab.interface_Label")); //$NON-NLS-1$
				label.setToolTipText(Messages
						.getString("DebuggerTab.interface_ToolTipText"));

				interfaceSwd = new Button(radio, SWT.RADIO);
				interfaceSwd.setText(Messages
						.getString("DebuggerTab.interfaceSWD_Text"));
				gd = new GridData();
				gd.widthHint = 55;
				interfaceSwd.setLayoutData(gd);

				interfaceJtag = new Button(radio, SWT.RADIO);
				interfaceJtag.setText(Messages
						.getString("DebuggerTab.interfaceJtag_Text"));
			}
		}

		{
			doConnectToRunning = new Button(comp, SWT.CHECK);
			doConnectToRunning.setText(Messages
					.getString("DebuggerTab.noReset_Text"));
			doConnectToRunning.setToolTipText(Messages
					.getString("DebuggerTab.noReset_ToolTipText"));
		}

		// ----- Actions ------------------------------------------------------
		interfaceSwd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		interfaceJtag.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		doConnectToRunning.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();

				tabStartup.doConnectToRunningChanged(doConnectToRunning
						.getSelection());
			}
		});

	}

	private void createDeviceControls(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("DebuggerTab.deviceGroup_Text"));
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		Composite comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 0;
		comp.setLayout(layout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(gd);

		Label label;
		Link link;
		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.deviceName_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages
					.getString("DebuggerTab.deviceName_ToolTipText"));

			flashDeviceName = new Text(comp, SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 180;
			flashDeviceName.setLayoutData(gd);

			didFlashDeviceNameChanged = false;

			link = new Link(comp, SWT.NONE);
			link.setText(Messages.getString("DebuggerTab.deviceName_Link"));
			gd = new GridData(SWT.RIGHT);
			link.setLayoutData(gd);
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.endianness_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages
					.getString("DebuggerTab.endianness_ToolTipText")); //$NON-NLS-1$

			Composite endiannessGroup = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = ((GridLayout) comp.getLayout()).numColumns - 1;
			layout.marginHeight = 0;
			endiannessGroup.setLayout(layout);
			endiannessGroup
					.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			{
				endiannessLittle = new Button(endiannessGroup, SWT.RADIO);
				endiannessLittle.setText(Messages
						.getString("DebuggerTab.endiannesslittle_Text"));

				endiannessBig = new Button(endiannessGroup, SWT.RADIO);
				endiannessBig.setText(Messages
						.getString("DebuggerTab.endiannessBig_Text"));
			}
		}

		// ----- Actions ------------------------------------------------------
		flashDeviceName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();

				didFlashDeviceNameChanged = true;
			}
		});

		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent event) {
				// this will open the hyperlink in the default web browser
				Program.launch(event.text);
			}
		});

		endiannessLittle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		endiannessBig.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

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

		Label label;
		{
			doStartGdbServer = new Button(comp, SWT.CHECK);
			doStartGdbServer.setText(Messages
					.getString("DebuggerTab.doStartGdbServer_Text"));
			doStartGdbServer.setToolTipText(Messages
					.getString("DebuggerTab.doStartGdbServer_ToolTipText"));
			gd = new GridData();
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			doStartGdbServer.setLayoutData(gd);
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbServerCommand_Label"));
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerCommand_ToolTipText"));

			Composite local = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 3;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			local.setLayoutData(gd);
			{
				gdbServerExecutable = new Text(local, SWT.SINGLE | SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				gdbServerExecutable.setLayoutData(gd);
				didGdbServerExecutableChanged = false;

				gdbServerBrowse = new Button(local, SWT.NONE);
				gdbServerBrowse.setText(Messages
						.getString("DebuggerTab.gdbServerCommandBrowse"));

				gdbServerVariables = new Button(local, SWT.NONE);
				gdbServerVariables.setText(Messages
						.getString("DebuggerTab.gdbServerCommandVariable"));
			}
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbServerSpeed_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerSpeed_ToolTipText"));

			Composite radio = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 5;
			layout.marginHeight = 0;
			radio.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			radio.setLayoutData(gd);
			{
				gdbServerSpeedAuto = new Button(radio, SWT.RADIO);
				gdbServerSpeedAuto.setText(Messages
						.getString("DebuggerTab.gdbServerSpeedAuto_Text"));
				gd = new GridData();
				gd.widthHint = ((GridData) interfaceSwd.getLayoutData()).widthHint;
				gdbServerSpeedAuto.setLayoutData(gd);

				gdbServerSpeedAdaptive = new Button(radio, SWT.RADIO);
				gdbServerSpeedAdaptive.setText(Messages
						.getString("DebuggerTab.gdbServerSpeedAdaptive_Text"));

				gdbServerSpeedFixed = new Button(radio, SWT.RADIO);
				gdbServerSpeedFixed.setText(Messages
						.getString("DebuggerTab.gdbServerSpeedFixed_Text"));

				gdbServerSpeedFixedValue = new Text(radio, SWT.BORDER);
				gd = new GridData();
				gd.widthHint = 60;
				gdbServerSpeedFixedValue.setLayoutData(gd);

				label = new Label(radio, SWT.NONE);
				label.setText(Messages
						.getString("DebuggerTab.gdbServerSpeedFixedUnit_Text")); //$NON-NLS-1$
			}
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbServerGdbPort_Label"));
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerGdbPort_ToolTipText"));

			gdbServerGdbPort = new Text(comp, SWT.SINGLE | SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 60;
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			gdbServerGdbPort.setLayoutData(gd);
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbServerSwoPort_Label"));
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerSwoPort_ToolTipText"));

			gdbServerSwoPort = new Text(comp, SWT.SINGLE | SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 60;
			gdbServerSwoPort.setLayoutData(gd);

			Composite empty = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 1;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			empty.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			empty.setLayoutData(gd);

			doGdbServerVerifyDownload = new Button(comp, SWT.CHECK);
			doGdbServerVerifyDownload.setText(Messages
					.getString("DebuggerTab.gdbServerVerifyDownload_Label"));
			doGdbServerVerifyDownload
					.setToolTipText(Messages
							.getString("DebuggerTab.gdbServerVerifyDownload_ToolTipText"));
			gd = new GridData();
			gd.horizontalIndent = 60;
			doGdbServerVerifyDownload.setLayoutData(gd);

			doGdbServerInitRegs = new Button(comp, SWT.CHECK);
			doGdbServerInitRegs.setText(Messages
					.getString("DebuggerTab.gdbServerInitRegs_Label"));
			doGdbServerInitRegs.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerInitRegs_ToolTipText"));
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbServerTelnetPort_Label"));
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerTelnetPort_ToolTipText"));

			gdbServerTelnetPort = new Text(comp, SWT.SINGLE | SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 60;
			gdbServerTelnetPort.setLayoutData(gd);

			Composite empty = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 1;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			empty.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			empty.setLayoutData(gd);

			doGdbServerLocalOnly = new Button(comp, SWT.CHECK);
			doGdbServerLocalOnly.setText(Messages
					.getString("DebuggerTab.gdbServerLocalOnly_Label"));
			doGdbServerLocalOnly.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerLocalOnly_ToolTipText"));
			gd = new GridData();
			gd.horizontalIndent = 60;
			doGdbServerLocalOnly.setLayoutData(gd);

			doGdbServerSilent = new Button(comp, SWT.CHECK);
			doGdbServerSilent.setText(Messages
					.getString("DebuggerTab.gdbServerSilent_Label"));
			doGdbServerSilent.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerSilent_ToolTipText"));
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.gdbServerLog_Label")); //$NON-NLS-1$

			Composite local = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			local.setLayoutData(gd);
			{
				gdbServerLog = new Text(local, SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				gdbServerLog.setLayoutData(gd);

				gdbServerLogBrowse = new Button(local, SWT.NONE);
				gdbServerLogBrowse.setText(Messages
						.getString("DebuggerTab.gdbServerLog_Button"));
			}
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbServerOther_Label")); //$NON-NLS-1$

			gdbServerOther = new Text(comp, SWT.BORDER);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			gdbServerOther.setLayoutData(gd);
		}

		{
			Composite local = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 2;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			layout.makeColumnsEqualWidth = true;
			local.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns;
			local.setLayoutData(gd);

			doGdbServerAllocateConsole = new Button(local, SWT.CHECK);
			doGdbServerAllocateConsole.setText(Messages
					.getString("DebuggerTab.gdbServerAllocateConsole_Label"));
			doGdbServerAllocateConsole
					.setToolTipText(Messages
							.getString("DebuggerTab.gdbServerAllocateConsole_ToolTipText"));
			gd = new GridData(GridData.FILL_HORIZONTAL);
			doGdbServerAllocateConsole.setLayoutData(gd);

			doGdbServerAllocateSemihostingConsole = new Button(local, SWT.CHECK);
			doGdbServerAllocateSemihostingConsole
					.setText(Messages
							.getString("DebuggerTab.gdbServerAllocateSemihostingConsole_Label"));
			doGdbServerAllocateSemihostingConsole
					.setToolTipText(Messages
							.getString("DebuggerTab.gdbServerAllocateSemihostingConsole_ToolTipText"));
			gd = new GridData(GridData.FILL_HORIZONTAL);
			doGdbServerAllocateSemihostingConsole.setLayoutData(gd);

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

		doStartGdbServer.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doStartGdbServerChanged();
				if (doStartGdbServer.getSelection()) {
					targetIpAddress
							.setText(ConfigurationAttributes.REMOTE_IP_ADDRESS_LOCALHOST);
				}
				scheduleUpdateJob();
			}
		});

		gdbServerExecutable.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				didGdbServerExecutableChanged = true;

				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});

		gdbServerSpeedAuto.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gdbServerSpeedFixedValue.setEnabled(false);
				scheduleUpdateJob();
			}
		});

		gdbServerSpeedAdaptive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gdbServerSpeedFixedValue.setEnabled(false);
				scheduleUpdateJob();
			}
		});

		gdbServerSpeedFixed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				gdbServerSpeedFixedValue.setEnabled(true);
				scheduleUpdateJob();
			}
		});

		gdbServerGdbPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				// make the target port the same
				targetPortNumber.setText(gdbServerGdbPort.getText());
				scheduleUpdateJob();
			}
		});

		gdbServerSwoPort.addModifyListener(scheduleUpdateJobModifyListener);
		gdbServerTelnetPort.addModifyListener(scheduleUpdateJobModifyListener);

		doGdbServerVerifyDownload
				.addSelectionListener(scheduleUpdateJobSelectionAdapter);
		doGdbServerInitRegs
				.addSelectionListener(scheduleUpdateJobSelectionAdapter);
		doGdbServerLocalOnly
				.addSelectionListener(scheduleUpdateJobSelectionAdapter);
		doGdbServerSilent
				.addSelectionListener(scheduleUpdateJobSelectionAdapter);

		gdbServerLog.addModifyListener(scheduleUpdateJobModifyListener);
		gdbServerOther.addModifyListener(scheduleUpdateJobModifyListener);

		doGdbServerAllocateConsole
				.addSelectionListener(scheduleUpdateJobSelectionAdapter);
		doGdbServerAllocateSemihostingConsole
				.addSelectionListener(scheduleUpdateJobSelectionAdapter);
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
		StringVariableSelectionDialog dialog = new StringVariableSelectionDialog(
				getShell());
		if (dialog.open() == StringVariableSelectionDialog.OK) {
			text.insert(dialog.getVariableExpression());
		}
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
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbCommand_ToolTipText"));

			Composite local = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 3;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			local.setLayoutData(gd);
			{
				gdbClientExecutable = new Text(local, SWT.SINGLE | SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				gdbClientExecutable.setLayoutData(gd);

				didGdbClientExecutableChanged = false;

				browseButton = new Button(local, SWT.NONE);
				browseButton.setText(Messages
						.getString("DebuggerTab.gdbCommandBrowse"));

				variableButton = new Button(local, SWT.NONE);
				variableButton.setText(Messages
						.getString("DebuggerTab.gdbCommandVariable"));
			}
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbOtherCommands_Label"));
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbOtherCommands_ToolTipText"));
			gd = new GridData();
			gd.verticalAlignment = SWT.TOP;
			label.setLayoutData(gd);

			gdbClientOtherCommands = new Text(comp, SWT.MULTI | SWT.WRAP
					| SWT.BORDER | SWT.V_SCROLL);
			gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 60;
			gdbClientOtherCommands.setLayoutData(gd);
		}

		// ----- Actions ------------------------------------------------------
		gdbClientExecutable.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {

				didGdbClientExecutableChanged = true;

				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});

		gdbClientOtherCommands.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseButtonSelected(Messages
						.getString("DebuggerTab.gdbCommandBrowse_Title"),
						gdbClientExecutable);
			}
		});

		variableButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				variablesButtonSelected(gdbClientExecutable);
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

		Composite comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		comp.setLayout(layout);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		comp.setLayoutData(gd);

		// Create entry fields for TCP/IP connections
		Label label;
		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.ipAddressLabel")); //$NON-NLS-1$

			targetIpAddress = new Text(comp, SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 125;
			targetIpAddress.setLayoutData(gd);

			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.portNumberLabel")); //$NON-NLS-1$

			targetPortNumber = new Text(comp, SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 125;
			targetPortNumber.setLayoutData(gd);
		}

		// ---- Actions -------------------------------------------------------
		// Add watchers for user data entry
		targetIpAddress.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});
		targetPortNumber.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = Character.isDigit(e.character)
						|| Character.isISOControl(e.character);
			}
		});
		targetPortNumber.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});

	}

	private void useRemoteChanged() {
		boolean enabled = true; // useRemote.getSelection();
		// jtagDevice.setEnabled(enabled);
		targetIpAddress.setEnabled(enabled);
		targetPortNumber.setEnabled(enabled);
		// connection.setEnabled(enabled);
	}

	private void doStartGdbServerChanged() {

		boolean enabled = doStartGdbServer.getSelection();

		// gdbServerSpeedAuto.setSelection(enabled);
		// gdbServerSpeedAdaptive.setSelection(enabled);
		// gdbServerSpeedFixed.setSelection(enabled);
		// gdbServerSpeedFixedValue.setEnabled(enabled);

		gdbServerExecutable.setEnabled(enabled);
		gdbServerBrowse.setEnabled(enabled);
		gdbServerVariables.setEnabled(enabled);
		gdbServerOther.setEnabled(enabled);

		gdbServerLog.setEnabled(enabled);
		gdbServerLogBrowse.setEnabled(enabled);

		gdbServerGdbPort.setEnabled(enabled);
		gdbServerSwoPort.setEnabled(enabled);
		gdbServerTelnetPort.setEnabled(enabled);

		doGdbServerVerifyDownload.setEnabled(enabled);
		doGdbServerInitRegs.setEnabled(enabled);
		doGdbServerLocalOnly.setEnabled(enabled);
		doGdbServerSilent.setEnabled(enabled);

		doGdbServerAllocateConsole.setEnabled(enabled);
		doGdbServerAllocateSemihostingConsole.setEnabled(enabled);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {

			// J-Link interface
			{
				String physicalInterface = configuration.getAttribute(
						ConfigurationAttributes.INTERFACE,
						ConfigurationAttributes.INTERFACE_DEFAULT);
				if (ConfigurationAttributes.INTERFACE_SWD
						.equals(physicalInterface))
					interfaceSwd.setSelection(true);
				else if (ConfigurationAttributes.INTERFACE_JTAG
						.equals(physicalInterface))
					interfaceJtag.setSelection(true);
				else {
					String message = "unknown interface " + physicalInterface
							+ ", using swd";
					Activator.log(Status.ERROR, message);
					interfaceSwd.setSelection(true);
				}

				doConnectToRunning.setSelection(configuration.getAttribute(
						ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
						ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT));
			}

			// J-Link device
			{
				String sharedName = SharedStorage
						.getFlashDeviceName(ConfigurationAttributes.FLASH_DEVICE_NAME_DEFAULT);
				flashDeviceName.setText(configuration.getAttribute(
						ConfigurationAttributes.FLASH_DEVICE_NAME, sharedName));
				didFlashDeviceNameChanged = false;

				String endianness = configuration.getAttribute(
						ConfigurationAttributes.ENDIANNESS,
						ConfigurationAttributes.ENDIANNESS_DEFAULT);
				if (ConfigurationAttributes.ENDIANNESS_LITTLE
						.equals(endianness))
					endiannessLittle.setSelection(true);
				else if (ConfigurationAttributes.ENDIANNESS_BIG
						.equals(endianness))
					endiannessBig.setSelection(true);
				else {
					String message = "unknown endianness " + endianness
							+ ", using little";
					Activator.log(Status.ERROR, message);
					endiannessLittle.setSelection(true);
				}
			}

			// J-Link GDB server
			{
				doStartGdbServer.setSelection(configuration.getAttribute(
						ConfigurationAttributes.DO_START_GDB_SERVER,
						ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT));

				gdbServerExecutable.setText(configuration.getAttribute(
						ConfigurationAttributes.GDB_SERVER_EXECUTABLE,
						getGdbServerExecutableDefault()));
				didGdbServerExecutableChanged = false;

				String physicalInterfaceSpeed = configuration.getAttribute(
						ConfigurationAttributes.GDB_SERVER_SPEED,
						ConfigurationAttributes.GDB_SERVER_SPEED_DEFAULT);

				if (ConfigurationAttributes.INTERFACE_SPEED_AUTO
						.equals(physicalInterfaceSpeed)) {
					gdbServerSpeedAuto.setSelection(true);
					gdbServerSpeedAdaptive.setSelection(false);
					gdbServerSpeedFixed.setSelection(false);

					gdbServerSpeedFixedValue.setEnabled(false);

				} else if (ConfigurationAttributes.INTERFACE_SPEED_ADAPTIVE
						.equals(physicalInterfaceSpeed)) {
					gdbServerSpeedAuto.setSelection(false);
					gdbServerSpeedAdaptive.setSelection(true);
					gdbServerSpeedFixed.setSelection(false);

					gdbServerSpeedFixedValue.setEnabled(false);
				} else {
					try {
						Integer.parseInt(physicalInterfaceSpeed);
						gdbServerSpeedAuto.setSelection(false);
						gdbServerSpeedAdaptive.setSelection(false);
						gdbServerSpeedFixed.setSelection(true);

						gdbServerSpeedFixedValue.setEnabled(true);
						gdbServerSpeedFixedValue
								.setText(physicalInterfaceSpeed);
					} catch (NumberFormatException e) {
						String message = "unknown interface speed "
								+ physicalInterfaceSpeed + ", using auto";
						Activator.log(Status.ERROR, message);
						gdbServerSpeedAuto.setSelection(true);
						gdbServerSpeedFixedValue.setEnabled(false);
					}
				}

				gdbServerGdbPort
						.setText(Integer.toString(configuration
								.getAttribute(
										ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
										ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT)));

				gdbServerSwoPort
						.setText(Integer.toString(configuration
								.getAttribute(
										ConfigurationAttributes.GDB_SERVER_SWO_PORT_NUMBER,
										ConfigurationAttributes.GDB_SERVER_SWO_PORT_NUMBER_DEFAULT)));

				gdbServerTelnetPort
						.setText(Integer.toString(configuration
								.getAttribute(
										ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
										ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT)));

				doGdbServerVerifyDownload
						.setSelection(configuration
								.getAttribute(
										ConfigurationAttributes.DO_GDB_SERVER_VERIFY_DOWNLOAD,
										ConfigurationAttributes.DO_GDB_SERVER_VERIFY_DOWNLOAD_DEFAULT));

				doGdbServerInitRegs
						.setSelection(configuration
								.getAttribute(
										ConfigurationAttributes.DO_GDB_SERVER_INIT_REGS,
										ConfigurationAttributes.DO_GDB_SERVER_INIT_REGS_DEFAULT));

				doGdbServerLocalOnly
						.setSelection(configuration
								.getAttribute(
										ConfigurationAttributes.DO_GDB_SERVER_LOCAL_ONLY,
										ConfigurationAttributes.DO_GDB_SERVER_LOCAL_ONLY_DEFAULT));

				doGdbServerSilent.setSelection(configuration.getAttribute(
						ConfigurationAttributes.DO_GDB_SERVER_SILENT,
						ConfigurationAttributes.DO_GDB_SERVER_SILENT_DEFAULT));

				gdbServerLog.setText(configuration.getAttribute(
						ConfigurationAttributes.GDB_SERVER_LOG,
						ConfigurationAttributes.GDB_SERVER_LOG_DEFAULT));

				gdbServerOther.setText(configuration.getAttribute(
						ConfigurationAttributes.GDB_SERVER_OTHER,
						ConfigurationAttributes.GDB_SERVER_OTHER_DEFAULT));

				doGdbServerAllocateConsole
						.setSelection(configuration
								.getAttribute(
										ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
										ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT));

				doGdbServerAllocateSemihostingConsole
						.setSelection(configuration
								.getAttribute(
										ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
										ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT));

				doStartGdbServerChanged();
			}

			// J-Link client
			{
				String gdbCommandAttr = configuration.getAttribute(
						IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
						SharedStorage.getGdbClientExecutable(null));
				gdbClientExecutable.setText(gdbCommandAttr);
				didGdbClientExecutableChanged = false;

				gdbClientOtherCommands
						.setText(configuration
								.getAttribute(
										ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
										ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS_DEFAULT));
			}

			// Remote target
			{
				targetIpAddress.setText(configuration.getAttribute(
						IGDBJtagConstants.ATTR_IP_ADDRESS,
						ConfigurationAttributes.REMOTE_IP_ADDRESS_DEFAULT)); //$NON-NLS-1$

				int storedPort = 0;
				storedPort = configuration.getAttribute(
						IGDBJtagConstants.ATTR_PORT_NUMBER, 0);

				if ((storedPort < 0) || (65535 < storedPort)) {
					storedPort = ConfigurationAttributes.REMOTE_PORT_NUMBER_DEFAULT;
				}

				String portString = Integer.toString(storedPort); //$NON-NLS-1$
				targetPortNumber.setText(portString);

				useRemoteChanged();
			}

			// Force thread update
			boolean updateThreadsOnSuspend = configuration
					.getAttribute(
							IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
							ConfigurationAttributes.UPDATE_THREAD_LIST_DEFAULT);
			fUpdateThreadlistOnSuspend.setSelection(updateThreadsOnSuspend);

		} catch (CoreException e) {
			Activator.getDefault().getLog().log(e.getStatus());
		}

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
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {

		boolean doSharedUpdate = false;
		// J-Link interface
		{
			if (interfaceSwd.getSelection()) {
				configuration.setAttribute(ConfigurationAttributes.INTERFACE,
						ConfigurationAttributes.INTERFACE_SWD);
			} else if (interfaceJtag.getSelection()) {
				configuration.setAttribute(ConfigurationAttributes.INTERFACE,
						ConfigurationAttributes.INTERFACE_JTAG);
			} else {
				String message = "interface not selected, setting swd";
				Activator.log(Status.ERROR, message);
				interfaceSwd.setSelection(true);

				configuration.setAttribute(ConfigurationAttributes.INTERFACE,
						ConfigurationAttributes.INTERFACE_SWD);
			}

			configuration.setAttribute(
					ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
					doConnectToRunning.getSelection());
		}

		// J-Link device
		{
			String name = flashDeviceName.getText().trim();
			configuration.setAttribute(
					ConfigurationAttributes.FLASH_DEVICE_NAME, name);
			if (didFlashDeviceNameChanged) {
				SharedStorage.putFlashDeviceName(name);
				doSharedUpdate = true;
				didFlashDeviceNameChanged = false;
			}

			if (endiannessLittle.getSelection()) {
				configuration.setAttribute(ConfigurationAttributes.ENDIANNESS,
						ConfigurationAttributes.ENDIANNESS_LITTLE);
			} else if (endiannessBig.getSelection()) {
				configuration.setAttribute(ConfigurationAttributes.ENDIANNESS,
						ConfigurationAttributes.ENDIANNESS_BIG);
			} else {
				String message = "endianness not selected, setting little";
				Activator.log(Status.ERROR, message);

				configuration.setAttribute(ConfigurationAttributes.ENDIANNESS,
						ConfigurationAttributes.ENDIANNESS_LITTLE);
			}
		}

		// J-Link GDB server
		{
			configuration.setAttribute(
					ConfigurationAttributes.DO_START_GDB_SERVER,
					doStartGdbServer.getSelection());

			String serverExecutable = gdbServerExecutable.getText().trim();
			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_EXECUTABLE,
					serverExecutable);
			if (didGdbServerExecutableChanged) {
				SharedStorage.putGdbServerExecutable(serverExecutable);
				doSharedUpdate = true;
				didGdbServerExecutableChanged = false;
			}

			if (gdbServerSpeedAuto.getSelection()) {
				configuration.setAttribute(
						ConfigurationAttributes.GDB_SERVER_SPEED,
						ConfigurationAttributes.INTERFACE_SPEED_AUTO);
			} else if (gdbServerSpeedAdaptive.getSelection()) {
				configuration.setAttribute(
						ConfigurationAttributes.GDB_SERVER_SPEED,
						ConfigurationAttributes.INTERFACE_SPEED_ADAPTIVE);
			} else if (gdbServerSpeedFixed.getSelection()) {
				configuration.setAttribute(
						ConfigurationAttributes.GDB_SERVER_SPEED,
						gdbServerSpeedFixedValue.getText().trim());
			}

			int port;
			port = Integer.parseInt(gdbServerGdbPort.getText().trim());
			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER, port);

			port = Integer.parseInt(gdbServerSwoPort.getText().trim());
			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_SWO_PORT_NUMBER, port);

			port = Integer.parseInt(gdbServerTelnetPort.getText().trim());
			configuration
					.setAttribute(
							ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
							port);

			configuration.setAttribute(
					ConfigurationAttributes.DO_GDB_SERVER_VERIFY_DOWNLOAD,
					doGdbServerVerifyDownload.getSelection());

			configuration.setAttribute(
					ConfigurationAttributes.DO_GDB_SERVER_INIT_REGS,
					doGdbServerInitRegs.getSelection());

			configuration.setAttribute(
					ConfigurationAttributes.DO_GDB_SERVER_LOCAL_ONLY,
					doGdbServerLocalOnly.getSelection());

			configuration.setAttribute(
					ConfigurationAttributes.DO_GDB_SERVER_SILENT,
					doGdbServerSilent.getSelection());

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_LOG,
					gdbServerLog.getText().trim());

			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_OTHER, gdbServerOther
							.getText().trim());

			configuration.setAttribute(
					ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
					doGdbServerAllocateConsole.getSelection());

			configuration
					.setAttribute(
							ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
							doGdbServerAllocateSemihostingConsole
									.getSelection());
		}

		// J-Link GDB client
		{
			// always use remote
			configuration.setAttribute(
					IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
					ConfigurationAttributes.USE_REMOTE_TARGET_DEFAULT);

			String clientExecutable = gdbClientExecutable.getText().trim();
			// configuration.setAttribute(
			// IMILaunchConfigurationConstants.ATTR_DEBUG_NAME,
			// clientExecutable);
			configuration.setAttribute(
					IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
					clientExecutable); // DSF

			if (didGdbClientExecutableChanged) {
				SharedStorage.putGdbClientExecutable(clientExecutable);
				didGdbClientExecutableChanged = true;
			}

			configuration.setAttribute(
					ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
					gdbClientOtherCommands.getText().trim());
		}

		// Remote target
		{
			String ip = targetIpAddress.getText().trim();
			configuration.setAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS, ip);

			try {
				int port = Integer.valueOf(targetPortNumber.getText().trim())
						.intValue();
				configuration.setAttribute(IGDBJtagConstants.ATTR_PORT_NUMBER,
						port);
			} catch (NumberFormatException e) {
				Activator.log(e);
			}
		}

		// Force thread update
		configuration
				.setAttribute(
						IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
						fUpdateThreadlistOnSuspend.getSelection());

		if (doSharedUpdate) {
			SharedStorage.update();
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

		configuration.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE,
				ConfigurationAttributes.JTAG_DEVICE);

		// These are inherited from the generic implementation.
		// Some might need some trimming.
		{
			CommandFactoryManager cfManager = MIPlugin.getDefault()
					.getCommandFactoryManager();
			CommandFactoryDescriptor defDesc = cfManager
					.getDefaultDescriptor(IGDBJtagConstants.DEBUGGER_ID);
			configuration
					.setAttribute(
							IMILaunchConfigurationConstants.ATTR_DEBUGGER_COMMAND_FACTORY,
							defDesc.getName());
			configuration.setAttribute(
					IMILaunchConfigurationConstants.ATTR_DEBUGGER_PROTOCOL,
					defDesc.getMIVersions()[0]);
			configuration
					.setAttribute(
							IMILaunchConfigurationConstants.ATTR_DEBUGGER_VERBOSE_MODE,
							IMILaunchConfigurationConstants.DEBUGGER_VERBOSE_MODE_DEFAULT);
			configuration
					.setAttribute(
							IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
							IGDBLaunchConfigurationConstants.DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND_DEFAULT);
		}

		// J-Link interface
		{
			configuration.setAttribute(ConfigurationAttributes.INTERFACE,
					ConfigurationAttributes.INTERFACE_DEFAULT);

			configuration.setAttribute(
					ConfigurationAttributes.DO_CONNECT_TO_RUNNING,
					ConfigurationAttributes.DO_CONNECT_TO_RUNNING_DEFAULT);
		}

		// J-Link device
		{
			String sharedName = SharedStorage
					.getFlashDeviceName(ConfigurationAttributes.FLASH_DEVICE_NAME_DEFAULT);
			configuration.setAttribute(
					ConfigurationAttributes.FLASH_DEVICE_NAME, sharedName);

			configuration.setAttribute(ConfigurationAttributes.ENDIANNESS,
					ConfigurationAttributes.ENDIANNESS_DEFAULT);
		}

		// J-Link GDB server setup
		{
			configuration.setAttribute(
					ConfigurationAttributes.DO_START_GDB_SERVER,
					ConfigurationAttributes.DO_START_GDB_SERVER_DEFAULT);

			String sharedName = SharedStorage
					.getGdbServerExecutable(getGdbServerExecutableDefault());
			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_EXECUTABLE, sharedName);

			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_SPEED,
					ConfigurationAttributes.GDB_SERVER_SPEED_DEFAULT);

			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER,
					ConfigurationAttributes.GDB_SERVER_GDB_PORT_NUMBER_DEFAULT);

			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_SWO_PORT_NUMBER,
					ConfigurationAttributes.GDB_SERVER_SWO_PORT_NUMBER_DEFAULT);

			configuration
					.setAttribute(
							ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER,
							ConfigurationAttributes.GDB_SERVER_TELNET_PORT_NUMBER_DEFAULT);

			configuration
					.setAttribute(
							ConfigurationAttributes.DO_GDB_SERVER_VERIFY_DOWNLOAD,
							ConfigurationAttributes.DO_GDB_SERVER_VERIFY_DOWNLOAD_DEFAULT);

			configuration.setAttribute(
					ConfigurationAttributes.DO_GDB_SERVER_INIT_REGS,
					ConfigurationAttributes.DO_GDB_SERVER_INIT_REGS_DEFAULT);

			configuration.setAttribute(
					ConfigurationAttributes.DO_GDB_SERVER_LOCAL_ONLY,
					ConfigurationAttributes.DO_GDB_SERVER_LOCAL_ONLY_DEFAULT);

			configuration.setAttribute(
					ConfigurationAttributes.DO_GDB_SERVER_SILENT,
					ConfigurationAttributes.DO_GDB_SERVER_SILENT_DEFAULT);

			configuration.setAttribute(ConfigurationAttributes.GDB_SERVER_LOG,
					ConfigurationAttributes.GDB_SERVER_LOG_DEFAULT);

			configuration.setAttribute(
					ConfigurationAttributes.GDB_SERVER_OTHER,
					ConfigurationAttributes.GDB_SERVER_OTHER_DEFAULT);

			configuration
					.setAttribute(
							ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE,
							ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_CONSOLE_DEFAULT);

			configuration
					.setAttribute(
							ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE,
							ConfigurationAttributes.DO_GDB_SERVER_ALLOCATE_SEMIHOSTING_CONSOLE_DEFAULT);
		}

		// J-Link GDB client setup
		{
			configuration.setAttribute(
					IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
					SharedStorage.getGdbClientExecutable(null));

			configuration.setAttribute(
					IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
					ConfigurationAttributes.USE_REMOTE_TARGET_DEFAULT);

			configuration.setAttribute(
					ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
					ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS_DEFAULT);
		}

		// Force thread update
		configuration
				.setAttribute(
						IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
						ConfigurationAttributes.UPDATE_THREAD_LIST_DEFAULT);

	}

	private String getGdbServerExecutableDefault() {
		return ConfigurationAttributes.GDB_SERVER_EXECUTABLE_DEFAULT_MAC;
	}

}
