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
	private boolean gdbClientExecutableChanged;
	private Text gdbClientOtherCommands;
	// private Button useRemote;
	// private Combo jtagDevice;
	// private Composite remoteConnectionParameters;
	// private StackLayout remoteConnectParmsLayout;
	// private Composite remoteTcpipBox;
	private Text ipAddress;
	private Text portNumber;
	// private Composite remoteConnectionBox;
	// private Text connection;
	// private String savedJtagDevice;
	private Text flashDeviceName;
	private boolean flashDeviceNameChanged;
	private Button endiannessLittle;
	private Button endiannessBig;
	private Button interfaceJtag;
	private Button interfaceSwd;

	private Button interfaceSpeedAuto;
	private Button interfaceSpeedAdaptive;
	private Button interfaceSpeedFixed;
	private Text interfaceSpeedFixedValue;

	private Button noReset;

	private Text gdbServerSwoPort;
	private Text gdbServerSemiPort;
	// private Label gdbServerCommandLabel;
	private Text gdbServerCommand;
	private Button gdbServerBrowse;
	private Button gdbServerVariables;

	private Button gdbServerVerifyDownload;
	private Button gdbServerInitRegs;
	private Button gdbServerLocalOnly;
	private Button gdbServerSilent;

	private Text gdbServerLog;
	private Button gdbServerLogBrowse;
	private Text gdbServerOther;

	private Button gdbServerAllocateConsole;
	private Button gdbServerAllocateSemihostingConsole;

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

		createInterfaceControl(comp);
		createDeviceControls(comp);

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

				gdbClientExecutableChanged = false;

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

				gdbClientExecutableChanged = true;

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

			ipAddress = new Text(comp, SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 125;
			ipAddress.setLayoutData(gd);

			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.portNumberLabel")); //$NON-NLS-1$

			portNumber = new Text(comp, SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 125;
			portNumber.setLayoutData(gd);
		}

		// ---- Actions -------------------------------------------------------
		// Add watchers for user data entry
		ipAddress.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
			}
		});
		portNumber.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = Character.isDigit(e.character)
						|| Character.isISOControl(e.character);
			}
		});
		portNumber.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				scheduleUpdateJob(); // provides much better performance for
										// Text listeners
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

			flashDeviceNameChanged = false;

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

				flashDeviceNameChanged = true;
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

	private void createInterfaceControl(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.getString("DebuggerTab.interfaceGroup_Text"));
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);

		Composite comp = new Composite(group, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 0;
		comp.setLayout(layout);

		Label label;
		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages.getString("DebuggerTab.interface_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages
					.getString("DebuggerTab.interface_ToolTipText"));

			Composite radio = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = ((GridLayout) comp.getLayout()).numColumns - 1;
			layout.marginHeight = 0;
			radio.setLayout(layout);
			{
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

			noReset = new Button(comp, SWT.CHECK);
			noReset.setText(Messages.getString("DebuggerTab.noReset_Text"));
			noReset.setToolTipText(Messages
					.getString("DebuggerTab.noReset_ToolTipText"));
		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.interfaceSpeed_Label")); //$NON-NLS-1$
			label.setToolTipText(Messages
					.getString("DebuggerTab.interfaceSpeed_ToolTipText"));

			Composite radio = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 3;
			layout.marginHeight = 0;
			radio.setLayout(layout);
			{
				interfaceSpeedAuto = new Button(radio, SWT.RADIO);
				interfaceSpeedAuto.setText(Messages
						.getString("DebuggerTab.interfaceSpeedAuto_Text"));
				gd = new GridData();
				gd.widthHint = ((GridData) interfaceSwd.getLayoutData()).widthHint;
				interfaceSpeedAuto.setLayoutData(gd);

				interfaceSpeedAdaptive = new Button(radio, SWT.RADIO);
				interfaceSpeedAdaptive.setText(Messages
						.getString("DebuggerTab.interfaceSpeedAdaptive_Text"));

				interfaceSpeedFixed = new Button(radio, SWT.RADIO);
				interfaceSpeedFixed.setText(Messages
						.getString("DebuggerTab.interfaceSpeedFixed_Text"));

			}
			interfaceSpeedFixedValue = new Text(comp, SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 60;
			interfaceSpeedFixedValue.setLayoutData(gd);
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

		interfaceSpeedAuto.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				interfaceSpeedFixedValue.setEnabled(false);
				updateLaunchConfigurationDialog();
			}
		});

		interfaceSpeedAdaptive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				interfaceSpeedFixedValue.setEnabled(false);
				updateLaunchConfigurationDialog();
			}
		});

		interfaceSpeedFixed.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				interfaceSpeedFixedValue.setEnabled(true);
				updateLaunchConfigurationDialog();
			}
		});

		noReset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();

				tabStartup.noResetChanged(noReset.getSelection());
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
			layout.numColumns = ((GridLayout) comp.getLayout()).numColumns - 1;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			local.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ((GridLayout) comp.getLayout()).numColumns - 1;
			local.setLayoutData(gd);
			{
				gdbServerCommand = new Text(local, SWT.SINGLE | SWT.BORDER);
				gd = new GridData(GridData.FILL_HORIZONTAL);
				gdbServerCommand.setLayoutData(gd);

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

			gdbServerVerifyDownload = new Button(comp, SWT.CHECK);
			gdbServerVerifyDownload.setText(Messages
					.getString("DebuggerTab.gdbServerVerifyDownload_Label"));
			gdbServerVerifyDownload
					.setToolTipText(Messages
							.getString("DebuggerTab.gdbServerVerifyDownload_ToolTipText"));
			gd = new GridData();
			gd.horizontalIndent = 60;
			gdbServerVerifyDownload.setLayoutData(gd);

			gdbServerInitRegs = new Button(comp, SWT.CHECK);
			gdbServerInitRegs.setText(Messages
					.getString("DebuggerTab.gdbServerInitRegs_Label"));
			gdbServerInitRegs.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerInitRegs_ToolTipText"));

		}

		{
			label = new Label(comp, SWT.NONE);
			label.setText(Messages
					.getString("DebuggerTab.gdbServerSemiPort_Label"));
			label.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerSemiPort_ToolTipText"));

			gdbServerSemiPort = new Text(comp, SWT.SINGLE | SWT.BORDER);
			gd = new GridData();
			gd.widthHint = 60;
			gdbServerSemiPort.setLayoutData(gd);

			Composite empty = new Composite(comp, SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 1;
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			empty.setLayout(layout);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			empty.setLayoutData(gd);

			gdbServerLocalOnly = new Button(comp, SWT.CHECK);
			gdbServerLocalOnly.setText(Messages
					.getString("DebuggerTab.gdbServerLocalOnly_Label"));
			gdbServerLocalOnly.setToolTipText(Messages
					.getString("DebuggerTab.gdbServerLocalOnly_ToolTipText"));
			gd = new GridData();
			gd.horizontalIndent = 60;
			gdbServerLocalOnly.setLayoutData(gd);

			gdbServerSilent = new Button(comp, SWT.CHECK);
			gdbServerSilent.setText(Messages
					.getString("DebuggerTab.gdbServerSilent_Label"));
			gdbServerSilent.setToolTipText(Messages
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

			gdbServerAllocateConsole = new Button(local, SWT.CHECK);
			gdbServerAllocateConsole.setText(Messages
					.getString("DebuggerTab.gdbServerAllocateConsole_Label"));
			gdbServerAllocateConsole
					.setToolTipText(Messages
							.getString("DebuggerTab.gdbServerAllocateConsole_ToolTipText"));
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gdbServerAllocateConsole.setLayoutData(gd);

			gdbServerAllocateSemihostingConsole = new Button(local, SWT.CHECK);
			gdbServerAllocateSemihostingConsole
					.setText(Messages
							.getString("DebuggerTab.gdbServerAllocateSemihostingConsole_Label"));
			gdbServerAllocateSemihostingConsole
					.setToolTipText(Messages
							.getString("DebuggerTab.gdbServerAllocateSemihostingConsole_ToolTipText"));
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gdbServerAllocateSemihostingConsole.setLayoutData(gd);

		}
		// ----- Actions ------------------------------------------------------
		// TODO
	}

	private void useRemoteChanged() {
		boolean enabled = true; // useRemote.getSelection();
		// jtagDevice.setEnabled(enabled);
		ipAddress.setEnabled(enabled);
		portNumber.setEnabled(enabled);
		// connection.setEnabled(enabled);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {

			String gdbCommandAttr = configuration.getAttribute(
					IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
					SharedStorage.getGdbClientExecutable(null));
			gdbClientExecutable.setText(gdbCommandAttr);
			gdbClientExecutableChanged = false;

			String storedAddress = ""; //$NON-NLS-1$
			int storedPort = 0;

			storedAddress = configuration.getAttribute(
					IGDBJtagConstants.ATTR_IP_ADDRESS, ""); //$NON-NLS-1$
			storedPort = configuration.getAttribute(
					IGDBJtagConstants.ATTR_PORT_NUMBER, 0);

			if (storedAddress != null && storedAddress.length() > 0) {
				// Treat as legacy network probe
				ipAddress.setText(storedAddress);
				String portString = (0 < storedPort) && (storedPort <= 65535) ? Integer
						.valueOf(storedPort).toString() : ""; //$NON-NLS-1$
				portNumber.setText(portString);
			} else {
				ipAddress.setText("localhost");
				portNumber.setText("2331");
			}

			boolean updateThreadsOnSuspend = configuration
					.getAttribute(
							IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
							IGDBLaunchConfigurationConstants.DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND_DEFAULT);
			fUpdateThreadlistOnSuspend.setSelection(updateThreadsOnSuspend);

			// get J-Link specific settings
			String physicalInterface = configuration.getAttribute(
					ConfigurationAttributes.INTERFACE,
					ConfigurationAttributes.INTERFACE_DEFAULT);
			if (ConfigurationAttributes.INTERFACE_SWD.equals(physicalInterface))
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

			noReset.setSelection(configuration.getAttribute(
					ConfigurationAttributes.NO_RESET,
					ConfigurationAttributes.NO_RESET_DEFAULT));

			String physicalInterfaceSpeed = configuration.getAttribute(
					ConfigurationAttributes.INTERFACE_SPEED,
					ConfigurationAttributes.INTERFACE_SPEED_DEFAULT);

			if (ConfigurationAttributes.INTERFACE_SPEED_AUTO
					.equals(physicalInterfaceSpeed)) {
				interfaceSpeedAuto.setSelection(true);
				interfaceSpeedFixedValue.setEnabled(false);

			} else if (ConfigurationAttributes.INTERFACE_SPEED_ADAPTIVE
					.equals(physicalInterfaceSpeed)) {
				interfaceSpeedAdaptive.setSelection(true);
				interfaceSpeedFixedValue.setEnabled(false);
			} else {
				try {
					Integer.parseInt(physicalInterfaceSpeed);
					interfaceSpeedFixed.setSelection(true);
					interfaceSpeedFixedValue.setEnabled(true);
					interfaceSpeedFixedValue.setText(physicalInterfaceSpeed);
				} catch (NumberFormatException e) {
					String message = "unknown interface speed "
							+ physicalInterfaceSpeed + ", using auto";
					Activator.log(Status.ERROR, message);
					interfaceSpeedAuto.setSelection(true);
					interfaceSpeedFixedValue.setEnabled(false);
				}
			}

			flashDeviceName
					.setText(configuration
							.getAttribute(
									ConfigurationAttributes.FLASH_DEVICE_NAME,
									SharedStorage
											.getFlashDeviceName(ConfigurationAttributes.FLASH_DEVICE_NAME_DEFAULT)));
			flashDeviceNameChanged = false;

			String endianness = configuration.getAttribute(
					ConfigurationAttributes.ENDIANNESS,
					ConfigurationAttributes.ENDIANNESS_DEFAULT);
			if (ConfigurationAttributes.ENDIANNESS_LITTLE.equals(endianness))
				endiannessLittle.setSelection(true);
			else if (ConfigurationAttributes.ENDIANNESS_BIG.equals(endianness))
				endiannessBig.setSelection(true);
			else {
				String message = "unknown endianness " + endianness
						+ ", using little";
				Activator.log(Status.ERROR, message);
				endiannessLittle.setSelection(true);
			}

			gdbClientOtherCommands.setText(configuration.getAttribute(
					ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
					ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS_DEFAULT));

			{
				// No server configuration yet
				doStartGdbServer.setEnabled(false);
				gdbServerCommand.setEnabled(false);
				gdbServerBrowse.setEnabled(false);
				gdbServerVariables.setEnabled(false);
				gdbServerOther.setEnabled(false);

				gdbServerLog.setEnabled(false);
				gdbServerLogBrowse.setEnabled(false);

				gdbServerSwoPort.setEnabled(false);
				gdbServerSemiPort.setEnabled(false);

				gdbServerVerifyDownload.setEnabled(false);
				gdbServerInitRegs.setEnabled(false);
				gdbServerLocalOnly.setEnabled(false);
				gdbServerSilent.setEnabled(false);

				gdbServerAllocateConsole.setEnabled(false);
				gdbServerAllocateSemihostingConsole.setEnabled(false);
			}

			useRemoteChanged();
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

		// To satisfy the references, always point to the Generic
		configuration.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE,
				ConfigurationAttributes.JTAG_DEVICE);

		configuration.setAttribute(
				IMILaunchConfigurationConstants.ATTR_DEBUG_NAME,
				gdbClientExecutable.getText().trim());
		configuration.setAttribute(
				IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
				gdbClientExecutable.getText().trim()); // DSF

		if (gdbClientExecutableChanged) {
			SharedStorage.putGdbClientExecutable(gdbClientExecutable.getText().trim());
			gdbClientExecutableChanged = true;
		}
		configuration.setAttribute(IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
				true);

		try {
			String ip = ipAddress.getText().trim();
			configuration.setAttribute(IGDBJtagConstants.ATTR_IP_ADDRESS, ip);
			int port = Integer.valueOf(portNumber.getText().trim()).intValue();
			configuration
					.setAttribute(IGDBJtagConstants.ATTR_PORT_NUMBER, port);
		} catch (NumberFormatException e) {
			Activator.log(e);
		}

		configuration
				.setAttribute(
						IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
						fUpdateThreadlistOnSuspend.getSelection());

		// store J-Link specific attributes
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

		if (interfaceSpeedAuto.getSelection()) {
			configuration.setAttribute(ConfigurationAttributes.INTERFACE_SPEED,
					ConfigurationAttributes.INTERFACE_SPEED_AUTO);
		} else if (interfaceSpeedAdaptive.getSelection()) {
			configuration.setAttribute(ConfigurationAttributes.INTERFACE_SPEED,
					ConfigurationAttributes.INTERFACE_SPEED_ADAPTIVE);
		} else if (interfaceSpeedFixed.getSelection()) {
			configuration.setAttribute(ConfigurationAttributes.INTERFACE_SPEED,
					interfaceSpeedFixedValue.getText().trim());
		}

		configuration.setAttribute(ConfigurationAttributes.NO_RESET,
				noReset.getSelection());

		configuration.setAttribute(ConfigurationAttributes.FLASH_DEVICE_NAME,
				flashDeviceName.getText().trim());
		if (flashDeviceNameChanged) {
			SharedStorage.putFlashDeviceName(flashDeviceName.getText().trim());
			SharedStorage.update();
			flashDeviceNameChanged = false;
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

		configuration.setAttribute(
				ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
				gdbClientOtherCommands.getText().trim());

	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

		configuration.setAttribute(IGDBJtagConstants.ATTR_JTAG_DEVICE,
				ConfigurationAttributes.JTAG_DEVICE);

		configuration.setAttribute(
				IGDBLaunchConfigurationConstants.ATTR_DEBUG_NAME,
				SharedStorage.getGdbClientExecutable(null));

		CommandFactoryManager cfManager = MIPlugin.getDefault()
				.getCommandFactoryManager();
		CommandFactoryDescriptor defDesc = cfManager
				.getDefaultDescriptor(IGDBJtagConstants.DEBUGGER_ID);
		configuration.setAttribute(
				IMILaunchConfigurationConstants.ATTR_DEBUGGER_COMMAND_FACTORY,
				defDesc.getName());
		configuration.setAttribute(
				IMILaunchConfigurationConstants.ATTR_DEBUGGER_PROTOCOL,
				defDesc.getMIVersions()[0]);
		configuration.setAttribute(
				IMILaunchConfigurationConstants.ATTR_DEBUGGER_VERBOSE_MODE,
				IMILaunchConfigurationConstants.DEBUGGER_VERBOSE_MODE_DEFAULT);
		configuration.setAttribute(IGDBJtagConstants.ATTR_USE_REMOTE_TARGET,
				IGDBJtagConstants.DEFAULT_USE_REMOTE_TARGET);
		configuration
				.setAttribute(
						IGDBLaunchConfigurationConstants.ATTR_DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND,
						IGDBLaunchConfigurationConstants.DEBUGGER_UPDATE_THREADLIST_ON_SUSPEND_DEFAULT);

		// J-Link specific defaults
		configuration.setAttribute(ConfigurationAttributes.INTERFACE,
				ConfigurationAttributes.INTERFACE_DEFAULT);

		configuration.setAttribute(ConfigurationAttributes.INTERFACE_SPEED,
				ConfigurationAttributes.INTERFACE_SPEED_DEFAULT);

		configuration.setAttribute(ConfigurationAttributes.NO_RESET,
				ConfigurationAttributes.NO_RESET_DEFAULT);

		configuration
				.setAttribute(
						ConfigurationAttributes.FLASH_DEVICE_NAME,
						SharedStorage
								.getFlashDeviceName(ConfigurationAttributes.FLASH_DEVICE_NAME_DEFAULT));

		configuration.setAttribute(ConfigurationAttributes.ENDIANNESS,
				ConfigurationAttributes.ENDIANNESS_DEFAULT);

		configuration.setAttribute(
				ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS,
				ConfigurationAttributes.GDB_CLIENT_OTHER_COMMANDS_DEFAULT);

		// TODO: add GDB server defaults
	}

}
